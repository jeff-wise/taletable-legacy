
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.Height
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.theme.ColorId
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.util.*



/**
 * Quote View Type
 */
enum class QuoteViewType
{
    SOURCE,
    ICON_OVER_SOURCE,
    NO_ICON;
}


/**
 * Quote
 */
data class Quote(val value : String)
{

    companion object : Factory<Quote>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Quote> = when (doc)
        {
            is DocText -> effValue(Quote(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Quote Source
 */
data class QuoteSource(val value : String)
{

    companion object : Factory<QuoteSource>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<QuoteSource> = when (doc)
        {
            is DocText -> effValue(QuoteSource(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Quote Widget Format
 */
data class QuoteWidgetFormat(override val id : UUID,
                             val widgetFormat : Func<WidgetFormat>,
                             val quoteStyle : Func<TextStyle>,
                             val sourceStyle : Func<TextStyle>,
                             val iconColor : Func<ColorId>) : Model
{
    companion object : Factory<QuoteWidgetFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<QuoteWidgetFormat> = when (doc)
        {
            is DocDict -> effApply(::QuoteWidgetFormat,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Widget Format
                                   split(doc.maybeAt("widget_format"),
                                         nullEff<WidgetFormat>(),
                                         { effApply(::Comp, WidgetFormat.fromDocument(it)) }),
                                   // Quote Style
                                   split(doc.maybeAt("quote_style"),
                                         nullEff<TextStyle>(),
                                         { effApply(::Comp, TextStyle.fromDocument(it)) }),
                                   // Source Style
                                   split(doc.maybeAt("source_style"),
                                         nullEff<TextStyle>(),
                                         { effApply(::Comp, TextStyle.fromDocument(it)) }),
                                   // Icon Color
                                   split(doc.maybeAt("icon_color"),
                                         nullEff<ColorId>(),
                                         { effApply(::Prim, ColorId.fromDocument(it)) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

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


