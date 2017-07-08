
package com.kispoko.tome.model.sheet.widget


import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.SheetManager
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Quote View Type
 */
sealed class QuoteViewType : SQLSerializable, Serializable
{

    object Source : QuoteViewType()
    {
        override fun asSQLValue() : SQLValue = SQLText({"source"})
    }


    object IconOverSource : QuoteViewType()
    {
        override fun asSQLValue() : SQLValue = SQLText({"icon_over_source"})
    }


    object NoIcon : QuoteViewType()
    {
        override fun asSQLValue() : SQLValue = SQLText({"no_icon"})
    }


    companion object
    {
        fun fromDocument(doc : SpecDoc) : ValueParser<QuoteViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "source"           -> effValue<ValueError,QuoteViewType>(QuoteViewType.Source)
                "icon_over_source" -> effValue<ValueError,QuoteViewType>(
                                            QuoteViewType.IconOverSource)
                "no_icon"          -> effValue<ValueError,QuoteViewType>(QuoteViewType.NoIcon)
                else               -> effError<ValueError,QuoteViewType>(
                                            UnexpectedValue("QuoteViewType", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Quote
 */
data class Quote(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Quote>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Quote> = when (doc)
        {
            is DocText -> effValue(Quote(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}


/**
 * Quote Source
 */
data class QuoteSource(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<QuoteSource>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<QuoteSource> = when (doc)
        {
            is DocText -> effValue(QuoteSource(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}


/**
 * Quote Widget Format
 */
data class QuoteWidgetFormat(override val id : UUID,
                             val widgetFormat : Comp<WidgetFormat>,
                             val quoteStyle : Comp<TextStyle>,
                             val sourceStyle : Comp<TextStyle>,
                             val iconColorTheme : Prim<ColorTheme>) : Model
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetFormat.name      = "widget_format"
        this.quoteStyle.name        = "quote_style"
        this.sourceStyle.name       = "source_style"
        this.iconColorTheme.name    = "icon_color_theme"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                quoteStyle : TextStyle,
                sourceStyle : TextStyle,
                iconColorTheme : ColorTheme)
        : this(UUID.randomUUID(),
               Comp(widgetFormat),
               Comp(quoteStyle),
               Comp(sourceStyle),
               Prim(iconColorTheme))


    companion object : Factory<QuoteWidgetFormat>
    {

        val defaultWidgetFormat   = WidgetFormat.default()
        val defaultQuoteStyle     = TextStyle.default()
        val defaultSoureStyle     = TextStyle.default()
        val defaultIconColorTheme = ColorTheme.black


        override fun fromDocument(doc : SpecDoc) : ValueParser<QuoteWidgetFormat> = when (doc)
        {
            is DocDict -> effApply(::QuoteWidgetFormat,
                                   // Widget Format
                                   split(doc.maybeAt("widget_format"),
                                         effValue(defaultWidgetFormat),
                                         { WidgetFormat.fromDocument(it) }),
                                   // Quote Style
                                   split(doc.maybeAt("quote_style"),
                                         effValue(defaultQuoteStyle),
                                         { TextStyle.fromDocument(it) }),
                                   // Source Style
                                   split(doc.maybeAt("source_style"),
                                         effValue(defaultSoureStyle),
                                         { TextStyle.fromDocument(it) }),
                                   // Icon Color
                                   split(doc.maybeAt("icon_color_theme"),
                                         effValue(defaultIconColorTheme),
                                         { ColorTheme.fromDocument(it) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : QuoteWidgetFormat =
                QuoteWidgetFormat(defaultWidgetFormat,
                                  defaultQuoteStyle,
                                  defaultSoureStyle,
                                  defaultIconColorTheme)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value

    fun quoteStyle() : TextStyle = this.quoteStyle.value

    fun sourceStyle() : TextStyle = this.sourceStyle.value

    fun iconColorTheme() : ColorTheme = this.iconColorTheme.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "quote_widget_format"

    override val modelObject = this

}



object QuoteWidgetView
{


    fun widgetView(quoteWidget : QuoteWidget, sheetUIContext: SheetUIContext) : View
    {
        val layout = WidgetView.layout(quoteWidget.widgetFormat(), sheetUIContext)

        layout.addView(this.mainView(quoteWidget, sheetUIContext))

        return layout
    }



    private fun mainView(quoteWidget : QuoteWidget, sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = this.mainViewLayout(quoteWidget.format(), sheetUIContext)

        // > Quote View
        layout.addView(this.quoteView(quoteWidget, sheetUIContext))

        // > Source View
        val source = quoteWidget.sourceString()
        if (source != null)
        {
            when (quoteWidget.viewType())
            {
                is QuoteViewType.Source ->
                    layout.addView(this.sourceHorizontalView(source,
                                                             quoteWidget.format(),
                            sheetUIContext))
                is QuoteViewType.IconOverSource ->
                    layout.addView(this.sourceVerticalView(source,
                                                           quoteWidget.format(),
                            sheetUIContext))
                is QuoteViewType.NoIcon ->
                    layout.addView(this.sourceVerticalView(source,
                                                           quoteWidget.format(),
                            sheetUIContext))

            }
        }

        return layout
    }


    private fun mainViewLayout(format : QuoteWidgetFormat,
                               sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId,
                                                     format.widgetFormat().backgroundColorTheme())

        layout.gravity          = format.widgetFormat().alignment().gravityConstant()

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun quoteView(quoteWidget : QuoteWidget, sheetUIContext: SheetUIContext) : TextView
    {
        val quote = TextViewBuilder()

        quote.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        quote.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        quote.text          = quoteWidget.quoteString()

        quote.gravity       = quoteWidget.format().quoteStyle().alignment().gravityConstant()

        quoteWidget.format().quoteStyle().styleTextViewBuilder(quote, sheetUIContext)

        return quote.textView(sheetUIContext.context)
    }



    private fun sourceHorizontalView(sourceText : String,
                                     format : QuoteWidgetFormat,
                                     sheetUIContext: SheetUIContext) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout  = LinearLayoutBuilder()

        val icon    = ImageViewBuilder()
        val source  = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.orientation     = LinearLayout.HORIZONTAL
        layout.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height          = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.layoutGravity   = Gravity.CENTER_HORIZONTAL
        layout.gravity         = Gravity.CENTER_VERTICAL

        layout.margin.top      = R.dimen.widget_text_quote_margin_top

        layout.child(icon)
              .child(source)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT

        icon.image                  = R.drawable.ic_quote

        icon.color                  = SheetManager.color(sheetUIContext.sheetId,
                                                         format.iconColorTheme())

        // (3 B) Source
        // -------------------------------------------------------------------------------------

        source.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        source.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        source.text                 = sourceText

        format.sourceStyle().styleTextViewBuilder(source, sheetUIContext)


        return layout.linearLayout(sheetUIContext.context)
    }


    private fun sourceVerticalView(sourceText : String,
                                   format : QuoteWidgetFormat,
                                   sheetUIContext: SheetUIContext) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout  = LinearLayoutBuilder()

        val icon    = ImageViewBuilder()
        val source  = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.orientation     = LinearLayout.VERTICAL

        layout.width           = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity         = Gravity.CENTER

        layout.margin.top      = R.dimen.widget_text_quote_margin_top

        layout.child(icon)
              .child(source)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image              = R.drawable.ic_quote_medium;

        icon.color              = SheetManager.color(sheetUIContext.sheetId,
                                                     format.iconColorTheme())

        // (3 B) Source
        // -------------------------------------------------------------------------------------

        source.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        source.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        source.text             = sourceText

        source.gravity          = Gravity.CENTER

        format.sourceStyle().styleTextViewBuilder(source, sheetUIContext)


        return layout.linearLayout(sheetUIContext.context)
    }


    private fun sourceNoIconView(sourceText : String,
                                 format : QuoteWidgetFormat,
                                 sheetUIContext: SheetUIContext) : TextView
    {
        val source = TextViewBuilder()

        source.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        source.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        source.text             = sourceText

        source.gravity          = Gravity.CENTER

        source.margin.topDp     = 7f

        format.sourceStyle().styleTextViewBuilder(source, sheetUIContext)

        return source.textView(sheetUIContext.context)
    }


}



//
//    // > Views
//    // -----------------------------------------------------------------------------------------
//


//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Initialize
//    // -----------------------------------------------------------------------------------------
//
//    private void initializeQuoteWidget()
//    {
//        // [1] Apply default format values
//        // -------------------------------------------------------------------------------------
//
//        // ** Alignment
//        if (this.data().format().alignmentIsDefault())
//            this.data().format().setAlignment(Alignment.CENTER);
//
//        // ** Background
//        if (this.data().format().backgroundIsDefault())
//            this.data().format().setBackground(BackgroundColor.NONE);
//
//        // ** Corners
//        if (this.data().format().cornersIsDefault())
//            this.data().format().setCorners(Corners.SMALL);
//
//    }
//

//
//    // > Clicks
//    // -----------------------------------------------------------------------------------------
//
//    private void onQuoteWidgetShortClick(Context context)
//    {
//        SheetActivityOld sheetActivity = (SheetActivityOld) context;
//
//        if (this.quote().length() > 145 || this.source() != null)
//        {
//            Intent intent = new Intent(sheetActivity, QuoteEditorActivity.class);
//            intent.putExtra("quote_widget", this);
//            context.startActivity(intent);
//        }
//        else
//        {
//            QuoteWidgetDialogFragment dialog = QuoteWidgetDialogFragment.newInstance(this);
//            dialog.show(sheetActivity.getSupportFragmentManager(), "");
//        }
//    }
//
//
//    // UPDATE EVENT
//    // -----------------------------------------------------------------------------------------
//
//    public static class UpdateQuoteEvent
//    {
//
//        // PROPERTIES
//        // -------------------------------------------------------------------------------------
//
//        private UUID   widgetId;
//        private String newValue;
//
//
//        // CONSTRUCTORS
//        // -------------------------------------------------------------------------------------
//
//        public UpdateQuoteEvent(UUID widgetId, String newValue)
//        {
//            this.widgetId   = widgetId;
//            this.newValue   = newValue;
//        }
//
//        // API
//        // -------------------------------------------------------------------------------------
//
//        public UUID widgetId()
//        {
//            return this.widgetId;
//        }
//
//        public String newValue()
//        {
//            return this.newValue;
//        }
//
//    }


