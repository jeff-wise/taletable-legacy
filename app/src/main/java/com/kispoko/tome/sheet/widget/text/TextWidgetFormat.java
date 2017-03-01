
package com.kispoko.tome.sheet.widget.text;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.widget.util.Position;
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

    private UUID                        id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>    insideLabel;
    private PrimitiveFunctor<Position>  insideLabelPosition;
    private ModelFunctor<TextStyle>     insideLabelStyle;

    private PrimitiveFunctor<String>    outsideLabel;
    private PrimitiveFunctor<Position>  outsideLabelPosition;
    private ModelFunctor<TextStyle>     outsideLabelStyle;

    private ModelFunctor<TextStyle>     valueStyle;
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

        this.outsideLabel           = new PrimitiveFunctor<>(null, String.class);
        this.outsideLabelPosition   = new PrimitiveFunctor<>(null, Position.class);
        this.outsideLabelStyle      = ModelFunctor.empty(TextStyle.class);

        this.valueStyle             = ModelFunctor.empty(TextStyle.class);
        this.descriptionStyle       = ModelFunctor.empty(TextStyle.class);

        this.isQuote                = new PrimitiveFunctor<>(null, Boolean.class);
        this.quoteSource            = new PrimitiveFunctor<>(null, String.class);

    }


    public TextWidgetFormat(UUID id,
                            String insideLabel,
                            Position insideLabelPosition,
                            TextStyle insideLabelStyle,
                            String outsideLabel,
                            Position outsideLabelPosition,
                            TextStyle outsideLabelStyle,
                            TextStyle valueStyle,
                            TextStyle descriptionStyle,
                            Boolean isQuote,
                            String quoteSource)
    {
        this.id                     = id;

        this.insideLabel            = new PrimitiveFunctor<>(insideLabel, String.class);
        this.insideLabelPosition    = new PrimitiveFunctor<>(insideLabelPosition, Position.class);
        this.insideLabelStyle       = ModelFunctor.full(insideLabelStyle, TextStyle.class);

        this.outsideLabel           = new PrimitiveFunctor<>(outsideLabel, String.class);
        this.outsideLabelPosition   = new PrimitiveFunctor<>(outsideLabelPosition, Position.class);
        this.outsideLabelStyle      = ModelFunctor.full(outsideLabelStyle, TextStyle.class);

        this.valueStyle             = ModelFunctor.full(valueStyle, TextStyle.class);
        this.descriptionStyle       = ModelFunctor.full(descriptionStyle, TextStyle.class);

        this.isQuote                = new PrimitiveFunctor<>(isQuote, Boolean.class);
        this.quoteSource            = new PrimitiveFunctor<>(quoteSource, String.class);

        // > Ensure null values are given defaults
        this.setInsideLabel(insideLabel);
        this.setInsideLabelPosition(insideLabelPosition);
        this.setInsideLabelStyle(insideLabelStyle);

        this.setOutsideLabel(outsideLabel);
        this.setOutsideLabelPosition(outsideLabelPosition);
        this.setOutsideLabelStyle(outsideLabelStyle);

        this.setValueStyle(valueStyle);
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

        String    outsideLabel         = yaml.atMaybeKey("outside_label").getString();
        Position  outsideLabelPosition = Position.fromYaml(
                                                    yaml.atMaybeKey("outside_label_position"));
        TextStyle outsideLabelStyle    = TextStyle.fromYaml(yaml.atMaybeKey("outside_label_style"));

        TextStyle valueStyle           = TextStyle.fromYaml(yaml.atMaybeKey("value_style"));
        TextStyle descriptionStyle     = TextStyle.fromYaml(yaml.atMaybeKey("description_style"));

        Boolean   isQuote              = yaml.atMaybeKey("is_quote").getBoolean();
        String    quoteSource          = yaml.atMaybeKey("quote_source").getString();

        return new TextWidgetFormat(id, insideLabel, insideLabelPosition, insideLabelStyle,
                                    outsideLabel, outsideLabelPosition, outsideLabelStyle,
                                    valueStyle, descriptionStyle, isQuote, quoteSource);
    }


    private static TextWidgetFormat asDefault()
    {
        TextWidgetFormat textWidgetFormat = new TextWidgetFormat();

        textWidgetFormat.setId(UUID.randomUUID());

        textWidgetFormat.setInsideLabel(null);
        textWidgetFormat.setInsideLabelPosition(null);
        textWidgetFormat.setInsideLabelStyle(null);

        textWidgetFormat.setOutsideLabel(null);
        textWidgetFormat.setOutsideLabelPosition(null);
        textWidgetFormat.setOutsideLabelStyle(null);

        textWidgetFormat.setValueStyle(null);
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
                .putString("outside_label", this.outsideLabel())
                .putYaml("outside_label_position", this.outsideLabelPosition())
                .putYaml("outside_label_style", this.outsideLabelStyle())
                .putYaml("value_style", this.valueStyle())
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
