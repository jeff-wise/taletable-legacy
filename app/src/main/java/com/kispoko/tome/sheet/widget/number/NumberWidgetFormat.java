
package com.kispoko.tome.sheet.widget.number;


import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.Spacing;
import com.kispoko.tome.sheet.widget.util.Height;
import com.kispoko.tome.sheet.widget.util.Position;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.functor.ModelFunctor;
import com.kispoko.tome.util.functor.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Number Widget Format
 */
public class NumberWidgetFormat implements Model, ToYaml, Serializable
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

    private ModelFunctor<TextStyle>     descriptionStyle;

    private ModelFunctor<TextStyle>     valueStyle;
    private PrimitiveFunctor<Height>    valueHeight;
    private PrimitiveFunctor<Integer>   valuePaddingHorizontal;
    private PrimitiveFunctor<Integer>   valuePaddingVertical;

    private ModelFunctor<TextStyle>     valuePrefixStyle;
    private ModelFunctor<TextStyle>     valuePostfixStyle;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public NumberWidgetFormat()
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

        this.descriptionStyle       = ModelFunctor.empty(TextStyle.class);

        this.valueStyle             = ModelFunctor.empty(TextStyle.class);
        this.valueHeight            = new PrimitiveFunctor<>(null, Height.class);
        this.valuePaddingHorizontal = new PrimitiveFunctor<>(null, Integer.class);
        this.valuePaddingVertical   = new PrimitiveFunctor<>(null, Integer.class);

        this.valuePrefixStyle       = ModelFunctor.empty(TextStyle.class);
        this.valuePostfixStyle      = ModelFunctor.empty(TextStyle.class);
    }


    public NumberWidgetFormat(UUID id,
                              String insideLabel,
                              Position insideLabelPosition,
                              TextStyle insideLabelStyle,
                              Spacing insideLabelMargins,
                              String outsideLabel,
                              Position outsideLabelPosition,
                              TextStyle outsideLabelStyle,
                              Spacing outsideLabelMargins,
                              TextStyle descriptionStyle,
                              TextStyle valueStyle,
                              Height valueHeight,
                              Integer valuePaddingHorizontal,
                              Integer valuePaddingVertical,
                              TextStyle valuePrefixStyle,
                              TextStyle valuePostfixStyle)
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

        this.descriptionStyle       = ModelFunctor.full(descriptionStyle, TextStyle.class);

        this.valueStyle             = ModelFunctor.full(valueStyle, TextStyle.class);
        this.valueHeight            = new PrimitiveFunctor<>(valueHeight, Height.class);
        this.valuePaddingHorizontal = new PrimitiveFunctor<>(valuePaddingHorizontal, Integer.class);
        this.valuePaddingVertical   = new PrimitiveFunctor<>(valuePaddingVertical, Integer.class);

        this.valuePrefixStyle       = ModelFunctor.full(valuePrefixStyle, TextStyle.class);
        this.valuePostfixStyle      = ModelFunctor.full(valuePostfixStyle, TextStyle.class);

        // > Set defaults for null values
        this.setInsideLabelPosition(insideLabelPosition);
        this.setInsideLabelStyle(insideLabelStyle);
        this.setInsideLabelMargins(insideLabelMargins);

        this.setOutsideLabelPosition(outsideLabelPosition);
        this.setOutsideLabelStyle(outsideLabelStyle);
        this.setOutsideLabelMargins(outsideLabelMargins);

        this.setDescriptionStyle(descriptionStyle);

        this.setValueStyle(valueStyle);
        this.setValueHeight(valueHeight);
        this.setValuePaddingVertical(valuePaddingVertical);

        this.setValuePrefixStyle(valuePrefixStyle);
        this.setValuePostfixStyle(valuePostfixStyle);
    }


    /**
     * Create a Number Widget Format from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Number Widget Format.
     * @throws YamlParseException
     */
    public static NumberWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return NumberWidgetFormat.asDefault();

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

        TextStyle descriptionStyle     = TextStyle.fromYaml(yaml.atMaybeKey("description_style"));

        TextStyle valueStyle           = TextStyle.fromYaml(yaml.atMaybeKey("value_style"));
        Height    valueHeight          = Height.fromYaml(yaml.atMaybeKey("value_height"));
        Integer   valuePaddingHorz     = yaml.atMaybeKey("value_padding_horizontal").getInteger();
        Integer   valuePaddingVert     = yaml.atMaybeKey("value_padding_vertical").getInteger();

        TextStyle valuePrefixStyle     = TextStyle.fromYaml(yaml.atMaybeKey("value_prefix_style"));
        TextStyle valuePostfixStyle    = TextStyle.fromYaml(yaml.atMaybeKey("value_postfix_style"));

        return new NumberWidgetFormat(id, insideLabel, insideLabelPosition, insideLabelStyle,
                                      insideLabelMargins, outsideLabel, outsideLabelPosition,
                                      outsideLabelStyle, outsideLabelMargins, descriptionStyle,
                                      valueStyle, valueHeight, valuePaddingHorz, valuePaddingVert,
                                      valuePrefixStyle, valuePostfixStyle);
    }


    /**
     * A Number Widget Format with default values.
     * @return The default Number Widget Format.
     */
    private static NumberWidgetFormat asDefault()
    {
        NumberWidgetFormat numberWidgetFormat = new NumberWidgetFormat();

        numberWidgetFormat.setId(UUID.randomUUID());

        numberWidgetFormat.setInsideLabel(null);
        numberWidgetFormat.setInsideLabelPosition(null);
        numberWidgetFormat.setInsideLabelStyle(null);
        numberWidgetFormat.setInsideLabelMargins(null);

        numberWidgetFormat.setOutsideLabel(null);
        numberWidgetFormat.setOutsideLabelPosition(null);
        numberWidgetFormat.setOutsideLabelStyle(null);
        numberWidgetFormat.setOutsideLabelMargins(null);

        numberWidgetFormat.setDescriptionStyle(null);

        numberWidgetFormat.setValueStyle(null);
        numberWidgetFormat.setValueHeight(null);
        numberWidgetFormat.setValuePaddingHorizontal(null);
        numberWidgetFormat.setValuePaddingVertical(null);

        numberWidgetFormat.setValuePrefixStyle(null);
        numberWidgetFormat.setValuePostfixStyle(null);

        return numberWidgetFormat;
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

            .putYaml("description_style", this.descriptionStyle())

            .putYaml("value_style", this.valueStyle())
            .putInteger("value_padding_horizontal", this.valuePaddingHorizontal())
            .putInteger("value_padding_vertical", this.valuePaddingVertical())

            .putYaml("value_prefix_style", this.valuePrefixStyle())
            .putYaml("value_postfix_style", this.valuePostfixStyle());
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
                                                        TextSize.MEDIUM_SMALL,
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


    public void setValueStyle(TextStyle style)
    {
        if (style != null) {
            this.valueStyle.setValue(style);
        }
        else {
            TextStyle defaultValueStyle =
                    new TextStyle(UUID.randomUUID(),
                                  TextColor.THEME_LIGHT,
                                  TextSize.MEDIUM_SMALL,
                                  Alignment.CENTER);
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


    // ** Value Padding Horizontal
    // --------------------------------------------------------------------------------------

    /**
     * The horizontal padding around the value.
     * @return The padding.
     */
    public Integer valuePaddingHorizontal()
    {
        return this.valuePaddingHorizontal.getValue();
    }


    /**
     * Set the horizontal value padding.
     * @param padding The padding.
     */
    public void setValuePaddingHorizontal(Integer padding)
    {
        this.valuePaddingHorizontal.setValue(padding);
    }


    // ** Value Padding Vertical
    // -----------------------------------------------------------------------------------------

    /**
     * The vertical padding around the value.
     * @return The padding.
     */
    public Integer valuePaddingVertical()
    {
        return this.valuePaddingVertical.getValue();
    }


    /**
     * Set the vertical value padding.
     * @param padding The padding.
     */
    public void setValuePaddingVertical(Integer padding)
    {
        if (padding != null)
            this.valuePaddingVertical.setValue(padding);
        else
            this.valuePaddingVertical.setValue(0);
    }


    // ** Value Prefix Style
    // --------------------------------------------------------------------------------------

    /**
     * The prefix style.
     * @return The prefix style.
     */
    public TextStyle valuePrefixStyle()
    {
        return this.valuePrefixStyle.getValue();
    }


    public void setValuePrefixStyle(TextStyle style)
    {
        if (style != null) {
            this.valuePrefixStyle.setValue(style);
        }
        else {
            TextStyle defaultValuePrefixStyle =
                    new TextStyle(UUID.randomUUID(),
                                        TextColor.THEME_LIGHT,
                                        TextSize.MEDIUM);
            this.valuePrefixStyle.setValue(defaultValuePrefixStyle);
        }
    }


    // ** Value Postfix Style
    // --------------------------------------------------------------------------------------

    /**
     * The postfix style.
     * @return The postfix style.
     */
    public TextStyle valuePostfixStyle()
    {
        return this.valuePostfixStyle.getValue();
    }


    public void setValuePostfixStyle(TextStyle style)
    {
        if (style != null) {
            this.valuePostfixStyle.setValue(style);
        }
        else {
            TextStyle defaultValuePostfixStyle = new TextStyle(UUID.randomUUID(),
                                                               TextColor.THEME_LIGHT,
                                                               TextSize.MEDIUM);
            this.valuePostfixStyle.setValue(defaultValuePostfixStyle);
        }
    }


}
