
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.theme.ColorTheme
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
        val defaultQuoteStyle     = TextStyle.default
        val defaultSoureStyle     = TextStyle.default
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


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "quote_widget_format"

    override val modelObject = this

}


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
//    // > Views
//    // -----------------------------------------------------------------------------------------
//
//    private View widgetView(boolean rowHasLabel, Context context)
//    {
//        LinearLayout layout = this.layout(rowHasLabel, context);
//
//        layout.addView(mainView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout mainView(Context context)
//    {
//        LinearLayout layout = mainViewLayout(context);
//
//        // > Quote View
//        layout.addView(quoteView(context));
//
//        // > Source View
//        if (this.source() != null)
//        {
//            switch (this.viewType())
//            {
//                case SOURCE:
//                    layout.addView(sourceHorizontalView(context));
//                    break;
//                case ICON_OVER_SOURCE:
//                    layout.addView(sourceVerticalView(context));
//                    break;
//                case NO_ICON:
//                    layout.addView(sourceNoIconView(context));
//                    break;
//            }
//        }
//
//        return layout;
//    }
//
//
//    private LinearLayout mainViewLayout(final Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation      = LinearLayout.VERTICAL;
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.backgroundColor  = this.data().format().background().colorId();
//
//        layout.gravity          = this.data().format().alignment().gravityConstant();
//
//        layout.onClick          = new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                onQuoteWidgetShortClick(context);
//            }
//        };
//
//        return layout.linearLayout(context);
//    }
//
//
//    private TextView quoteView(Context context)
//    {
//        TextViewBuilder quote = new TextViewBuilder();
//
//        quote.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
//        quote.height        = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        quote.text          = this.quote();
//
//        quote.gravity       = this.format().quoteStyle().alignment().gravityConstant();
//
//        this.format().quoteStyle().styleTextViewBuilder(quote, context);
//
//        return quote.textView(context);
//    }
//
//
//    private LinearLayout sourceHorizontalView(Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        LinearLayoutBuilder layout  = new LinearLayoutBuilder();
//
//        ImageViewBuilder    icon    = new ImageViewBuilder();
//        TextViewBuilder     source  = new TextViewBuilder();
//
//        // [2] Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.orientation     = LinearLayout.HORIZONTAL;
//        layout.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
//        layout.height          = LinearLayout.LayoutParams.WRAP_CONTENT;
//        layout.layoutGravity   = Gravity.CENTER_HORIZONTAL;
//        layout.gravity         = Gravity.CENTER_VERTICAL;
//
//        layout.margin.top      = R.dimen.widget_text_quote_margin_top;
//
//        layout.child(icon)
//              .child(source);
//
//        // [3 A] Icon
//        // -------------------------------------------------------------------------------------
//
//        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
//        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        icon.image                  = R.drawable.ic_quote;
//
//        icon.color                  = this.format().iconColor().resourceId();
//
//        // [3 B] Source
//        // -------------------------------------------------------------------------------------
//
//        source.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
//        source.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        source.text                 = this.source();
//
//        this.format().sourceStyle().styleTextViewBuilder(source, context);
//
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout sourceVerticalView(Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        LinearLayoutBuilder layout  = new LinearLayoutBuilder();
//
//        ImageViewBuilder    icon    = new ImageViewBuilder();
//        TextViewBuilder     source  = new TextViewBuilder();
//
//        // [2] Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.orientation     = LinearLayout.VERTICAL;
//
//        layout.width           = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height          = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity         = Gravity.CENTER;
//
//        layout.margin.top      = R.dimen.widget_text_quote_margin_top;
//
//        layout.child(icon)
//              .child(source);
//
//        // [3 A] Icon
//        // -------------------------------------------------------------------------------------
//
//        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
//        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        icon.image              = R.drawable.ic_quote_medium;
//
//        icon.color                  = this.format().iconColor().resourceId();
//
//        // [3 B] Source
//        // -------------------------------------------------------------------------------------
//
//        source.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
//        source.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        source.text             = this.source();
//
//        source.gravity          = Gravity.CENTER;
//
//        this.format().sourceStyle().styleTextViewBuilder(source, context);
//
//
//        return layout.linearLayout(context);
//    }
//
//
//    private TextView sourceNoIconView(Context context)
//    {
//        TextViewBuilder source = new TextViewBuilder();
//
//        source.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
//        source.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        source.text             = this.source();
//
//        source.gravity          = Gravity.CENTER;
//
//        source.margin.top       = R.dimen.seven_dp;
//
//        this.format().sourceStyle().styleTextViewBuilder(source, context);
//
//        return source.textView(context);
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


