
package com.kispoko.tome.sheet.widget.text;


import com.kispoko.tome.sheet.widget.util.InlineLabelPosition;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;


import java.io.Serializable;
import java.util.UUID;



/**
 * Text Widget Format
 */
public class TextWidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                                    id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<TextSize>              size;
    private PrimitiveFunctor<TextColor>             color;
    private PrimitiveFunctor<Boolean>               isQuote;
    private PrimitiveFunctor<String>                quoteSource;
    private PrimitiveFunctor<String>                label;
    private PrimitiveFunctor<InlineLabelPosition>   labelPosition;
    private ModelFunctor<TextStyle>                 valueStyle;
    private ModelFunctor<TextStyle>                 labelStyle;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public TextWidgetFormat()
    {
        this.id             = null;

        this.size           = new PrimitiveFunctor<>(null, TextSize.class);
        this.color          = new PrimitiveFunctor<>(null, TextColor.class);
        this.isQuote        = new PrimitiveFunctor<>(null, Boolean.class);
        this.quoteSource    = new PrimitiveFunctor<>(null, String.class);
        this.label          = new PrimitiveFunctor<>(null, String.class);
        this.labelPosition  = new PrimitiveFunctor<>(null, InlineLabelPosition.class);
        this.valueStyle     = ModelFunctor.empty(TextStyle.class);
        this.labelStyle     = ModelFunctor.empty(TextStyle.class);
    }


    public TextWidgetFormat(UUID id,
                            TextSize size,
                            TextColor color,
                            Boolean isQuote,
                            String quoteSource,
                            String label,
                            InlineLabelPosition labelPosition,
                            TextStyle valueStyle,
                            TextStyle labelStyle)
    {
        this.id             = id;

        this.size           = new PrimitiveFunctor<>(size, TextSize.class);
        this.color          = new PrimitiveFunctor<>(color, TextColor.class);
        this.isQuote        = new PrimitiveFunctor<>(isQuote, Boolean.class);
        this.quoteSource    = new PrimitiveFunctor<>(quoteSource, String.class);
        this.label          = new PrimitiveFunctor<>(label, String.class);
        this.labelPosition  = new PrimitiveFunctor<>(labelPosition, InlineLabelPosition.class);
        this.valueStyle     = ModelFunctor.full(valueStyle, TextStyle.class);
        this.labelStyle     = ModelFunctor.full(labelStyle, TextStyle.class);

        this.setIsQuote(isQuote);
        this.setSize(size);
        this.setQuoteSource(quoteSource);
        this.setColor(color);
        this.setLabelPosition(labelPosition);
        this.setValueStyle(valueStyle);
        this.setLabelStyle(labelStyle);
    }


    public static TextWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return TextWidgetFormat.asDefault();

        UUID                id              = UUID.randomUUID();

        TextSize            size            = TextSize.fromYaml(yaml.atMaybeKey("size"));
        TextColor           tint            = TextColor.fromYaml(yaml.atMaybeKey("tint"));
        Boolean             isQuote         = yaml.atMaybeKey("is_quote").getBoolean();
        String              quoteSource     = yaml.atMaybeKey("quote_source").getString();
        String              label           = yaml.atMaybeKey("label").getString();
        InlineLabelPosition labelPosition   = InlineLabelPosition.fromYaml(
                                                        yaml.atMaybeKey("label_position"));
        TextStyle           valueStyle      = TextStyle.fromYaml(
                                                        yaml.atMaybeKey("value_style"),
                                                        false);
        TextStyle           labelStyle      = TextStyle.fromYaml(
                                                            yaml.atMaybeKey("label_style"),
                                                            false);

        return new TextWidgetFormat(id, size, tint, isQuote, quoteSource, label,
                                    labelPosition, valueStyle, labelStyle);
    }


    private static TextWidgetFormat asDefault()
    {
        TextWidgetFormat textWidgetFormat = new TextWidgetFormat();

        textWidgetFormat.setId(UUID.randomUUID());
        textWidgetFormat.setSize(null);
        textWidgetFormat.setColor(null);
        textWidgetFormat.setIsQuote(null);
        textWidgetFormat.setQuoteSource(null);
        textWidgetFormat.setLabel(null);
        textWidgetFormat.setLabelPosition(null);
        textWidgetFormat.setValueStyle(null);
        textWidgetFormat.setLabelStyle(null);

        return textWidgetFormat;
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Model
    // --------------------------------------------------------------------------------------

    // ** Id
    // --------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // --------------------------------------------------------------------------------------

    /**
     * Called when the Text Widget Format is completely loaded.
     */
    public void onLoad() { }


    // > To Yaml
    // --------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        YamlBuilder yaml = YamlBuilder.map();

        yaml.putYaml("size", this.size());
        yaml.putYaml("tint", this.tint());
        yaml.putBoolean("is_quote", this.isQuote());
        yaml.putString("quote_source", this.quoteSource());
        yaml.putYaml("value_style", this.valueStyle());
        yaml.putYaml("label_style", this.labelStyle());

        return yaml;
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Size
    // --------------------------------------------------------------------------------------

    /**
     * The Text Widget's text size.
     * @return The Widget Content Size.
     */
    public TextSize size()
    {
        return this.size.getValue();
    }


    /**
     * Set the text widget's text size.
     * @param size The text size.
     */
    public void setSize(TextSize size)
    {
        if (size != null)
            this.size.setValue(size);
        else
            this.size.setValue(TextSize.MEDIUM);
    }


    // ** Tint
    // --------------------------------------------------------------------------------------

    /**
     * The text widget tint.
     * @return The tint.
     */
    public TextColor tint()
    {
        return this.color.getValue();
    }


    public void setColor(TextColor color)
    {
        if (color != null)
            this.color.setValue(color);
        else
            this.color.setValue(TextColor.THEME_MEDIUM);
    }


    // ** Is Quote
    // --------------------------------------------------------------------------------------

    /**
     * True if this text widget represents a quote.
     * @return Is Quote Boolean.
     */
    public Boolean isQuote()
    {
        return this.isQuote.getValue();
    }


    /**
     * Set the is quote value. True if the text widget is a quote.
     * @param isQuote Is Quote?
     */
    public void setIsQuote(Boolean isQuote)
    {
        if (isQuote != null)
            this.isQuote.setValue(isQuote);
        else
            this.isQuote.setValue(false);
    }


    // ** Quote Source
    // --------------------------------------------------------------------------------------

    /**
     * The quote source (who said it).
     * @return The quote source.
     */
    public String quoteSource()
    {
        return this.quoteSource.getValue();
    }


    /**
     * Set the quote source (who said the quote)
     * @param quoteSource The quote source.
     */
    public void setQuoteSource(String quoteSource)
    {
        if (quoteSource != null)
            this.quoteSource.setValue(quoteSource);
        else
            this.quoteSource.setValue("");
    }


    // ** Inline Label
    // --------------------------------------------------------------------------------------

    /**
     * The text widget's inline label (may be null).
     * @return The inline label.
     */
    public String label()
    {
        return this.label.getValue();
    }


    /**
     * Set the inline label.
     * @param label The label.
     */
    public void setLabel(String label)
    {
        this.label.setValue(label);
    }


    // ** Label Position
    // --------------------------------------------------------------------------------------

    /**
     * The position of the widget's label.
     * @return The inline label text.
     */
    public InlineLabelPosition labelPosition()
    {
        return this.labelPosition.getValue();
    }


    /**
     * Set the label position.
     * @param labelPosition The label position.
     */
    public void setLabelPosition(InlineLabelPosition labelPosition)
    {
        if (labelPosition != null)
            this.labelPosition.setValue(labelPosition);
        else
            this.labelPosition.setValue(InlineLabelPosition.LEFT);
    }


    // ** Value Style
    // --------------------------------------------------------------------------------------

    /**
     * The value style.
     * @return The value style.
     */
    public TextStyle valueStyle()
    {
        return this.valueStyle.getValue();
    }


    /**
     * Set the value style.
     * @param valueStyle The valueStyle.
     */
    public void setValueStyle(TextStyle valueStyle)
    {
        if (valueStyle != null) {
            this.valueStyle.setValue(valueStyle);
        }
        else {
            TextStyle defaultValueStyle = new TextStyle(UUID.randomUUID(),
                                                        TextColor.THEME_MEDIUM,
                                                        TextSize.MEDIUM_SMALL);
            this.valueStyle.setValue(defaultValueStyle);
        }
    }


    // ** Label Style
    // --------------------------------------------------------------------------------------

    /**
     * The label style.
     * @return The label style.
     */
    public TextStyle labelStyle()
    {
        return this.labelStyle.getValue();
    }


    /**
     * Set the inline label style.
     * @param labelStyle The inline label style.
     */
    public void setLabelStyle(TextStyle labelStyle)
    {
        if (labelStyle != null) {
            this.labelStyle.setValue(labelStyle);
        }
        else {
            TextStyle defaultLabelStyle = new TextStyle(UUID.randomUUID(),
                                                        TextColor.THEME_MEDIUM,
                                                        TextSize.MEDIUM_SMALL);
            this.labelStyle.setValue(defaultLabelStyle);
        }
    }


}
