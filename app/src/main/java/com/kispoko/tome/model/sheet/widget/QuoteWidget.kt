
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
import com.kispoko.tome.model.sheet.style.IconFormat
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.rts.sheet.SheetContext
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
sealed class QuoteViewType : ToDocument, SQLSerializable, Serializable
{

    object Source : QuoteViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"source"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("source")

    }


    object IconOverSource : QuoteViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"icon_over_source"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("icon_over_source")

    }


    object NoIcon : QuoteViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"no_icon"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("no_icon")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<QuoteViewType> = when (doc)
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
 * Quote Widget Format
 */
data class QuoteWidgetFormat(override val id : UUID,
                             val widgetFormat : Comp<WidgetFormat>,
                             val quoteStyle : Comp<TextStyle>,
                             val sourceStyle : Comp<TextStyle>,
                             val iconFormat : Comp<IconFormat>)
                             : ToDocument, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetFormat.name      = "widget_format"
        this.quoteStyle.name        = "quote_style"
        this.sourceStyle.name       = "source_style"
        this.iconFormat.name        = "icon_format"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                quoteStyle : TextStyle,
                sourceStyle : TextStyle,
                iconFormat : IconFormat)
        : this(UUID.randomUUID(),
               Comp(widgetFormat),
               Comp(quoteStyle),
               Comp(sourceStyle),
               Comp(iconFormat))


    companion object : Factory<QuoteWidgetFormat>
    {

        val defaultWidgetFormat   = WidgetFormat.default()
        val defaultQuoteStyle     = TextStyle.default()
        val defaultSoureStyle     = TextStyle.default()
        val defaultIconFormat     = IconFormat.default()


        override fun fromDocument(doc: SchemaDoc): ValueParser<QuoteWidgetFormat> = when (doc)
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
                                   // Icon Format
                                   split(doc.maybeAt("icon_format"),
                                         effValue(defaultIconFormat),
                                         { IconFormat.fromDocument(it) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = QuoteWidgetFormat(defaultWidgetFormat,
                                          defaultQuoteStyle,
                                          defaultSoureStyle,
                                          defaultIconFormat)

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "quote_style" to this.quoteStyle().toDocument(),
        "source_style" to this.sourceStyle().toDocument(),
        "icon_format" to this.iconFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value

    fun quoteStyle() : TextStyle = this.quoteStyle.value

    fun sourceStyle() : TextStyle = this.sourceStyle.value

    fun iconFormat() : IconFormat = this.iconFormat.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "quote_widget_format"

    override val modelObject = this

}



class QuoteWidgetViewBuilder(val quoteWidget : QuoteWidget,
                             val sheetUIContext : SheetUIContext)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val sheetContext = SheetContext(sheetUIContext)


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = WidgetView.layout(this.quoteWidget.widgetFormat(), sheetUIContext)

        layout.addView(this.mainView())

        return layout
    }



    private fun mainView() : LinearLayout
    {
        val layout = this.mainViewLayout()

        // > Quote View
        layout.addView(this.quoteView())

        // > Source View
        val maybeSource = quoteWidget.source(sheetContext)
        when (maybeSource)
        {
            is Just -> this.addSourceView(maybeSource.value, layout)
        }

        return layout
    }


    private fun addSourceView(source : String, layout : LinearLayout) =
        when (quoteWidget.viewType())
        {
            is QuoteViewType.Source ->
                layout.addView(this.sourceNormalView(source))
            is QuoteViewType.IconOverSource ->
                layout.addView(this.sourceVerticalView(source))
            is QuoteViewType.NoIcon ->
                layout.addView(this.sourceVerticalView(source))
        }



    private fun mainViewLayout() : LinearLayout
    {
        val widgetFormat = this.quoteWidget.widgetFormat()

        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId,
                                                     widgetFormat.backgroundColorTheme())

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun quoteView() : TextView
    {
        val quote           = TextViewBuilder()

        quote.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        quote.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        quote.text          = this.quoteWidget.quote(sheetContext)

        quote.gravity       = quoteWidget.format().quoteStyle().alignment().gravityConstant()

        quoteWidget.format().quoteStyle().styleTextViewBuilder(quote, sheetUIContext)

        return quote.textView(sheetUIContext.context)
    }



    private fun sourceNormalView(sourceText : String) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val format  = this.quoteWidget.format()

        val layout  = LinearLayoutBuilder()

        val icon    = ImageViewBuilder()
        val source  = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation     = LinearLayout.HORIZONTAL

        layout.layoutGravity   = Gravity.CENTER
        layout.gravity         = Gravity.CENTER

        layout.margin.top      = R.dimen.widget_text_quote_margin_top

        layout.child(icon)
              .child(source)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT

        icon.image                  = R.drawable.ic_quote

        icon.color                  = SheetManager.color(sheetUIContext.sheetId,
                                                         format.iconFormat().colorTheme())

        // (3 B) Source
        // -------------------------------------------------------------------------------------

        source.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        source.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        source.text                 = sourceText

        format.sourceStyle().styleTextViewBuilder(source, sheetUIContext)


        return layout.linearLayout(sheetUIContext.context)
    }


    private fun sourceVerticalView(sourceText : String) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val format  = this.quoteWidget.format()

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

        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        icon.image              = R.drawable.ic_quote_medium

        icon.color              = SheetManager.color(sheetUIContext.sheetId,
                                                     format.iconFormat().colorTheme())

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


