
package com.kispoko.tome.sheet.widget.text;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.Spacing;
import com.kispoko.tome.sheet.widget.util.Height;
import com.kispoko.tome.sheet.widget.util.Position;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Text Widget Format
 */
public class TextWidgetFormat extends Model
                              implements ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>    insideLabel;
    private PrimitiveFunctor<Position>  insideLabelPosition;
    private ModelFunctor<TextStyle>     insideLabelStyle;
    private ModelFunctor<Spacing>       insideLabelMargins;

    private PrimitiveFunctor<String>    outsideLabel;
    private PrimitiveFunctor<Position>  outsideLabelPosition;
    private ModelFunctor<TextStyle>     outsideLabelStyle;
    private ModelFunctor<Spacing>       outsideLabelMargins;

    private ModelFunctor<TextStyle>     valueStyle;
    private PrimitiveFunctor<Height>    valueHeight;
    private PrimitiveFunctor<Integer>   valuePaddingVertical;

    private ModelFunctor<TextStyle>     descriptionStyle;

    private PrimitiveFunctor<Boolean>   isQuote;
    private PrimitiveFunctor<String>    quoteSource;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public TextWidgetFormat()
    {
        this.id                     = null;

        this.insideLabel            = new PrimitiveFunctor<>(null, String.class);
        this.insideLabelPosition    = new PrimitiveFunctor<>(null, Position.class);
        this.insideLabelStyle       = ModelFunctor.empty(TextStyle.class);
        this.insideLabelMargins     = ModelFunctor.empty(Spacing.class);

        this.outsideLabel           = new PrimitiveFunctor<>(null, String.class);
        this.outsideLabelPosition   = new PrimitiveFunctor<>(null, Position.class);
        this.outsideLabelStyle      = ModelFunctor.empty(TextStyle.class);
        this.outsideLabelMargins    = ModelFunctor.empty(Spacing.class);

        this.valueStyle             = ModelFunctor.empty(TextStyle.class);
        this.valueHeight            = new PrimitiveFunctor<>(null, Height.class);
        this.valuePaddingVertical   = new PrimitiveFunctor<>(null, Integer.class);

        this.descriptionStyle       = ModelFunctor.empty(TextStyle.class);

        this.isQuote                = new PrimitiveFunctor<>(null, Boolean.class);
        this.quoteSource            = new PrimitiveFunctor<>(null, String.class);

    }


    public TextWidgetFormat(UUID id,
                            String insideLabel,
                            Position insideLabelPosition,
                            TextStyle insideLabelStyle,
                            Spacing insideLabelMargins,
                            String outsideLabel,
                            Position outsideLabelPosition,
                            TextStyle outsideLabelStyle,
                            Spacing outsideLabelMargins,
                            TextStyle valueStyle,
                            Height valueHeight,
                            Integer valuePaddingVertical,
                            TextStyle descriptionStyle,
                            Boolean isQuote,
                            String quoteSource)
    {
        this.id                     = id;

        this.insideLabel            = new PrimitiveFunctor<>(insideLabel, String.class);
        this.insideLabelPosition    = new PrimitiveFunctor<>(insideLabelPosition, Position.class);
        this.insideLabelStyle       = ModelFunctor.full(insideLabelStyle, TextStyle.class);
        this.insideLabelMargins     = ModelFunctor.full(insideLabelMargins, Spacing.class);

        this.outsideLabel           = new PrimitiveFunctor<>(outsideLabel, String.class);
        this.outsideLabelPosition   = new PrimitiveFunctor<>(outsideLabelPosition, Position.class);
        this.outsideLabelStyle      = ModelFunctor.full(outsideLabelStyle, TextStyle.class);
        this.outsideLabelMargins    = ModelFunctor.full(outsideLabelMargins, Spacing.class);

        this.valueStyle             = ModelFunctor.full(valueStyle, TextStyle.class);
        this.valueHeight            = new PrimitiveFunctor<>(valueHeight, Height.class);
        this.valuePaddingVertical   = new PrimitiveFunctor<>(valuePaddingVertical, Integer.class);

        this.descriptionStyle       = ModelFunctor.full(descriptionStyle, TextStyle.class);

        this.isQuote                = new PrimitiveFunctor<>(isQuote, Boolean.class);
        this.quoteSource            = new PrimitiveFunctor<>(quoteSource, String.class);

        // > Ensure null values are given defaults
        this.setInsideLabel(insideLabel);
        this.setInsideLabelPosition(insideLabelPosition);
        this.setInsideLabelStyle(insideLabelStyle);
        this.setInsideLabelMargins(insideLabelMargins);

        this.setOutsideLabel(outsideLabel);
        this.setOutsideLabelPosition(outsideLabelPosition);
        this.setOutsideLabelStyle(outsideLabelStyle);
        this.setOutsideLabelMargins(outsideLabelMargins);

        this.setValueStyle(valueStyle);
        this.setValueHeight(valueHeight);
        this.setValuePaddingVertical(valuePaddingVertical);

        this.setDescriptionStyle(descriptionStyle);

        this.setIsQuote(isQuote);
        this.setQuoteSource(quoteSource);
    }


    public static TextWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return TextWidgetFormat.asDefault();

        UUID      id                   = UUID.randomUUID();

        String    insideLabel          = yaml.atMaybeKey("inside_label").getString();
        Position  insideLabelPosition  = Position.fromYaml(
                                                    yaml.atMaybeKey("inside_label_position"));
        TextStyle insideLabelStyle     = TextStyle.fromYaml(yaml.atMaybeKey("inside_label_style"));
        Spacing   insideLabelMargins   = Spacing.fromYaml(yaml.atMaybeKey("inside_label_margins"));

        String    outsideLabel         = yaml.atMaybeKey("outside_label").getString();
        Position  outsideLabelPosition = Position.fromYaml(
                                                    yaml.atMaybeKey("outside_label_position"));
        TextStyle outsideLabelStyle    = TextStyle.fromYaml(yaml.atMaybeKey("outside_label_style"));
        Spacing   outsideLabelMargins  = Spacing.fromYaml(yaml.atMaybeKey("outside_label_margins"));

        TextStyle valueStyle           = TextStyle.fromYaml(yaml.atMaybeKey("value_style"));
        Height    valueHeight          = Height.fromYaml(yaml.atMaybeKey("value_height"));
        Integer   valuePaddingVertical = yaml.atMaybeKey("value_padding_vertical").getInteger();

        TextStyle descriptionStyle     = TextStyle.fromYaml(yaml.atMaybeKey("description_style"));

        Boolean   isQuote              = yaml.atMaybeKey("is_quote").getBoolean();
        String    quoteSource          = yaml.atMaybeKey("quote_source").getString();

        return new TextWidgetFormat(id, insideLabel, insideLabelPosition, insideLabelStyle,
                                    insideLabelMargins, outsideLabel, outsideLabelPosition,
                                    outsideLabelStyle, outsideLabelMargins, valueStyle, valueHeight,
                                    valuePaddingVertical, descriptionStyle, isQuote, quoteSource);
    }


    private static TextWidgetFormat asDefault()
    {
        TextWidgetFormat textWidgetFormat = new TextWidgetFormat();

        textWidgetFormat.setId(UUID.randomUUID());

        textWidgetFormat.setInsideLabel(null);
        textWidgetFormat.setInsideLabelPosition(null);
        textWidgetFormat.setInsideLabelStyle(null);
        textWidgetFormat.setInsideLabelMargins(null);

        textWidgetFormat.setOutsideLabel(null);
        textWidgetFormat.setOutsideLabelPosition(null);
        textWidgetFormat.setOutsideLabelStyle(null);
        textWidgetFormat.setOutsideLabelMargins(null);

        textWidgetFormat.setValueStyle(null);
        textWidgetFormat.setValueHeight(null);
        textWidgetFormat.setValuePaddingVertical(null);

        textWidgetFormat.setDescriptionStyle(null);

        textWidgetFormat.setIsQuote(null);
        textWidgetFormat.setQuoteSource(null);

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
        return YamlBuilder.map()
                .putString("inside_label", this.insideLabel())
                .putYaml("inside_label_position", this.insideLabelPosition())
                .putYaml("inside_label_style", this.insideLabelStyle())
                .putYaml("inside_label_margins", this.insideLabelMargins())
                .putString("outside_label", this.outsideLabel())
                .putYaml("outside_label_position", this.outsideLabelPosition())
                .putYaml("outside_label_style", this.outsideLabelStyle())
                .putYaml("outside_label_margins", this.outsideLabelMargins())
                .putYaml("value_style", this.valueStyle())
                .putYaml("value_height", this.valueHeight())
                .putInteger("value_padding_vertical", this.valuePaddingVertical())
                .putYaml("description_style", this.descriptionStyle())
                .putBoolean("is_quote", this.isQuote())
                .putString("quote_source", this.quoteSource());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Inside Label
    // --------------------------------------------------------------------------------------

    /**
     * The inline label. May be null.
     * @return The label.
     */
    public String insideLabel()
    {
        return this.insideLabel.getValue();
    }


    /**
     * Set the label.
     * @param insideLabel The label.
     */
    public void setInsideLabel(String insideLabel)
    {
        this.insideLabel.setValue(insideLabel);
    }


    // ** Inside Label Position
    // --------------------------------------------------------------------------------------

    /**
     * The position of the widget's inside label.
     * @return The inline label text.
     */
    public Position insideLabelPosition()
    {
        return this.insideLabelPosition.getValue();
    }


    /**
     * Set the label position.
     * @param insideLabelPosition The label position.
     */
    public void setInsideLabelPosition(Position insideLabelPosition)
    {
        if (insideLabelPosition != null)
            this.insideLabelPosition.setValue(insideLabelPosition);
        else
            this.insideLabelPosition.setValue(Position.LEFT);
    }


    // ** Inside Label Style
    // --------------------------------------------------------------------------------------

    /**
     * The inside label style.
     * @return The inside label style.
     */
    public TextStyle insideLabelStyle()
    {
        return this.insideLabelStyle.getValue();
    }


    public void setInsideLabelStyle(TextStyle style)
    {
        if (style != null) {
            this.insideLabelStyle.setValue(style);
        }
        else {
            TextStyle defaultLabelStyle = new TextStyle(UUID.randomUUID(),
                                                        TextColor.THEME_VERY_DARK,
                                                        TextSize.MEDIUM,
                                                        Alignment.CENTER);
            this.insideLabelStyle.setValue(defaultLabelStyle);
        }
    }


    // ** Inside Label Margins
    // --------------------------------------------------------------------------------------

    /**
     * The inside label margins.
     * @return The inside label margins spacing.
     */
    public Spacing insideLabelMargins()
    {
        return this.insideLabelMargins.getValue();
    }


    /**
     * Set the inside label margins. If null, sets defaults (all 0s).
     * @param spacing The spacing.
     */
    public void setInsideLabelMargins(Spacing spacing)
    {
        if (spacing != null)
            this.insideLabelMargins.setValue(spacing);
        else
            this.insideLabelMargins.setValue(Spacing.asDefault());
    }


    // ** Outside Label
    // --------------------------------------------------------------------------------------

    /**
     * The outside label. May be null.
     * @return The label.
     */
    public String outsideLabel()
    {
        return this.outsideLabel.getValue();
    }


    /**
     * Set the outside label.
     * @param label The label.
     */
    public void setOutsideLabel(String label)
    {
        this.outsideLabel.setValue(label);
    }


    // ** Outside Label Position
    // --------------------------------------------------------------------------------------

    /**
     * The position of the widget's outside label.
     * @return The outside label text.
     */
    public Position outsideLabelPosition()
    {
        return this.outsideLabelPosition.getValue();
    }


    /**
     * Set the outside label position.
     * @param position The label position.
     */
    public void setOutsideLabelPosition(Position position)
    {
        if (position != null)
            this.outsideLabelPosition.setValue(position);
        else
            this.outsideLabelPosition.setValue(Position.LEFT);
    }


    // ** Outside Label Style
    // --------------------------------------------------------------------------------------

    /**
     * The outside label style.
     * @return The outside label style.
     */
    public TextStyle outsideLabelStyle()
    {
        return this.outsideLabelStyle.getValue();
    }


    /**
     * Set the outside label style. If null, a default style is set.
     * @param style The label style.
     */
    public void setOutsideLabelStyle(TextStyle style)
    {
        if (style != null) {
            this.outsideLabelStyle.setValue(style);
        }
        else {
            TextStyle defaultLabelStyle = new TextStyle(UUID.randomUUID(),
                                                        TextColor.THEME_VERY_DARK,
                                                        TextSize.MEDIUM,
                                                        Alignment.CENTER);
            this.outsideLabelStyle.setValue(defaultLabelStyle);
        }
    }


    // ** Outside Label Margins
    // --------------------------------------------------------------------------------------

    /**
     * The outside label margins.
     * @return The outside label margins spacing.
     */
    public Spacing outsideLabelMargins()
    {
        return this.outsideLabelMargins.getValue();
    }


    /**
     * Set the outside label margins. If null, sets defaults (all 0s).
     * @param spacing The spacing.
     */
    public void setOutsideLabelMargins(Spacing spacing)
    {
        if (spacing != null)
            this.outsideLabelMargins.setValue(spacing);
        else
            this.outsideLabelMargins.setValue(Spacing.asDefault());
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


    // ** Value Height
    // --------------------------------------------------------------------------------------

    /**
     * The value height.
     * @return The value height.
     */
    public Height valueHeight()
    {
        return this.valueHeight.getValue();
    }


    /**
     * Set the value height. If null, sets the height to SMALL.
     * @param height The height.
     */
    public void setValueHeight(Height height)
    {
        if (height != null)
            this.valueHeight.setValue(height);
        else
            this.valueHeight.setValue(Height.SMALL);
    }


    // ** Value Padding Vertical
    // --------------------------------------------------------------------------------------

    /**
     * The vertical value padding.
     * @return The vertical padding.
     */
    public Integer valuePaddingVertical()
    {
        return this.valuePaddingVertical.getValue();
    }


    /**
     * Set the value's vertical padding. If null, defaults to 0.
     * @param padding The padding.
     */
    public void setValuePaddingVertical(Integer padding)
    {
        if (padding != null)
            this.valuePaddingVertical.setValue(padding);
        else
            this.valuePaddingVertical.setValue(0);
    }


    // ** Description Style
    // --------------------------------------------------------------------------------------

    /**
     * The description style.
     * @return The description style.
     */
    public TextStyle descriptionStyle()
    {
        return this.descriptionStyle.getValue();
    }


    public void setDescriptionStyle(TextStyle style)
    {
        if (style != null) {
            this.descriptionStyle.setValue(style);
        }
        else {
            TextStyle defaultDescriptionStyle = new TextStyle(UUID.randomUUID(),
                                                              TextColor.THEME_DARK,
                                                              TextSize.MEDIUM_SMALL);
            this.descriptionStyle.setValue(defaultDescriptionStyle);
        }
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


}
