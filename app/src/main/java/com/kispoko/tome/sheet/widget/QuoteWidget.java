
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.quote.QuoteWidgetFormat;
import com.kispoko.tome.sheet.widget.quote.ViewType;
import com.kispoko.tome.sheet.Corners;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Quote Widget
 */
public class QuoteWidget extends Widget
                         implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    /**
     * The quote string.
     */
    private PrimitiveFunctor<String>        quote;

    /**
     * The quote source (who said it, when, etc...).
     */
    private PrimitiveFunctor<String>        source;

    /**
     * The type of quote view i.e. how the quote is displayed.
     */
    private PrimitiveFunctor<ViewType>      viewType;

    /**
     * The quote formatting options.
     */
    private ModelFunctor<QuoteWidgetFormat> format;

    private ModelFunctor<WidgetData>        widgetData;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public QuoteWidget()
    {
        this.id         = null;

        this.quote      = new PrimitiveFunctor<>(null, String.class);
        this.source     = new PrimitiveFunctor<>(null, String.class);

        this.viewType   = new PrimitiveFunctor<>(null, ViewType.class);
        this.format     = ModelFunctor.empty(QuoteWidgetFormat.class);

        this.widgetData = ModelFunctor.empty(WidgetData.class);
    }


    public QuoteWidget(UUID id,
                       String quote,
                       String source,
                       ViewType viewType,
                       QuoteWidgetFormat format,
                       WidgetData widgetData)
    {
        this.id         = id;

        this.quote      = new PrimitiveFunctor<>(quote, String.class);
        this.source     = new PrimitiveFunctor<>(source, String.class);

        this.viewType   = new PrimitiveFunctor<>(viewType, ViewType.class);
        this.format     = ModelFunctor.full(format, QuoteWidgetFormat.class);

        this.widgetData = ModelFunctor.full(widgetData, WidgetData.class);

        this.initializeQuoteWidget();
    }


    /**
     * Create a Quote Widget from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Quote Widget.
     * @throws YamlParseException
     */
    public static QuoteWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID              id         = UUID.randomUUID();

        String            quote      = yaml.atKey("quote").getTrimmedString();
        String            source     = yaml.atMaybeKey("source").getTrimmedString();

        ViewType          viewType   = ViewType.fromYaml(yaml.atMaybeKey("view_type"));
        QuoteWidgetFormat format     = QuoteWidgetFormat.fromYaml(yaml.atMaybeKey("format"));

        WidgetData        widgetData = WidgetData.fromYaml(yaml.atMaybeKey("data"), false);

        return new QuoteWidget(id, quote, source, viewType, format, widgetData);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Text Widget is completely loaded for the first time.
     */
    public void onLoad()
    {
        this.initializeQuoteWidget();
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Text Widget's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("quote", this.quote())
                .putString("source", this.source())
                .putYaml("view_type", this.viewType())
                .putYaml("format", this.format())
                .putYaml("data", this.data());
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    @Override
    public void initialize(GroupParent groupParent) { };

    @Override
    public WidgetData data()
    {
        return this.widgetData.getValue();
    }


    @Override
    public View view(boolean rowHasLabel, Context context)
    {
        return this.widgetView(rowHasLabel, context);
    }


    // > State
    // -----------------------------------------------------------------------------------------

    // ** Quote
    // -----------------------------------------------------------------------------------------

    /**
     * The quote.
     * @return The quote.
     */
    public String quote()
    {
        return this.quote.getValue();
    }


    // ** Source
    // -----------------------------------------------------------------------------------------

    /**
     * The quote source.
     * @return The source string.
     */
    public String source()
    {
        return this.source.getValue();
    }


    // ** View Type
    // -----------------------------------------------------------------------------------------

    /**
     * The view type.
     * @return The view type.
     */
    public ViewType viewType()
    {
        return this.viewType.getValue();
    }


    // ** Format
    // -----------------------------------------------------------------------------------------

    /**
     * The quote widget formatting options.
     * @return The format.
     */
    public QuoteWidgetFormat format()
    {
        return this.format.getValue();
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Initialize
    // -----------------------------------------------------------------------------------------

    private void initializeQuoteWidget()
    {
        // [1] Apply default format values
        // -------------------------------------------------------------------------------------

        // ** Width
        if (this.data().format().width() == null)
            this.data().format().setWidth(1);

        // ** Alignment
        if (this.data().format().alignment() == null)
            this.data().format().setAlignment(Alignment.CENTER);

        // ** Background
        if (this.data().format().background() == null)
            this.data().format().setBackground(BackgroundColor.NONE);

        // ** Corners
        if (this.data().format().corners() == null)
            this.data().format().setCorners(Corners.SMALL);

    }


    // > Views
    // -----------------------------------------------------------------------------------------

    private View widgetView(boolean rowHasLabel, Context context)
    {
        LinearLayout layout = this.layout(rowHasLabel, context);

        layout.addView(mainView(context));

        return layout;
    }


    private LinearLayout mainView(Context context)
    {
        LinearLayout layout = mainViewLayout(context);

        // > Quote View
        layout.addView(quoteView(context));

        // > Source View
        if (this.source() != null)
        {
            switch (this.viewType())
            {
                case SOURCE:
                    layout.addView(sourceHorizontalView(context));
                    break;
                case ICON_OVER_SOURCE:
                    layout.addView(sourceVerticalView(context));
                    break;
                case NO_ICON:
                    layout.addView(sourceNoIconView(context));
                    break;
            }
        }

        return layout;
    }


    private LinearLayout mainViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private TextView quoteView(Context context)
    {
        TextViewBuilder quote = new TextViewBuilder();

        quote.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
        quote.height        = LinearLayout.LayoutParams.WRAP_CONTENT;

        quote.text          = this.quote();

        quote.gravity       = this.format().quoteStyle().alignment().gravityConstant();

        this.format().quoteStyle().styleTextViewBuilder(quote, context);

        return quote.textView(context);
    }


    private LinearLayout sourceHorizontalView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout  = new LinearLayoutBuilder();

        ImageViewBuilder    icon    = new ImageViewBuilder();
        TextViewBuilder     source  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation     = LinearLayout.HORIZONTAL;
        layout.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height          = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.layoutGravity   = Gravity.CENTER_HORIZONTAL;
        layout.gravity         = Gravity.CENTER_VERTICAL;

        layout.margin.top      = R.dimen.widget_text_quote_margin_top;

        layout.child(icon)
              .child(source);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = R.drawable.ic_quote;

        icon.color                  = this.format().iconColor().resourceId();

        // [3 B] Source
        // -------------------------------------------------------------------------------------

        source.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        source.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        source.text                 = this.source();

        this.format().sourceStyle().styleTextViewBuilder(source, context);


        return layout.linearLayout(context);
    }


    private LinearLayout sourceVerticalView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout  = new LinearLayoutBuilder();

        ImageViewBuilder    icon    = new ImageViewBuilder();
        TextViewBuilder     source  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation     = LinearLayout.VERTICAL;

        layout.width           = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity         = Gravity.CENTER;

        layout.margin.top      = R.dimen.widget_text_quote_margin_top;

        layout.child(icon)
              .child(source);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image              = R.drawable.ic_quote_medium;

        icon.color                  = this.format().iconColor().resourceId();

        // [3 B] Source
        // -------------------------------------------------------------------------------------

        source.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        source.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        source.text             = this.source();

        source.gravity          = Gravity.CENTER;

        this.format().sourceStyle().styleTextViewBuilder(source, context);


        return layout.linearLayout(context);
    }


    private TextView sourceNoIconView(Context context)
    {
        TextViewBuilder source = new TextViewBuilder();

        source.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        source.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        source.text             = this.source();

        source.gravity          = Gravity.CENTER;

        source.margin.top       = R.dimen.seven_dp;

        this.format().sourceStyle().styleTextViewBuilder(source, context);

        return source.textView(context);
    }



}
