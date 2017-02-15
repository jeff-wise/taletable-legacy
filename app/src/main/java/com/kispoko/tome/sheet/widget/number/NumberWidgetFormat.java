
package com.kispoko.tome.sheet.widget.number;


import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.InlineLabelPosition;
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
 * Number Widget Format
 */
public class NumberWidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                                    id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<TextSize>              size;
    private PrimitiveFunctor<String>                inlineLabel;
    private PrimitiveFunctor<InlineLabelPosition>   inlineLabelPosition;
    private ModelFunctor<TextStyle>                 descriptionStyle;
    private ModelFunctor<TextStyle>                 valueStyle;
    private ModelFunctor<TextStyle>                 valuePrefixStyle;
    private ModelFunctor<TextStyle>                 valuePostfixStyle;
    private ModelFunctor<TextStyle>                 labelStyle;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public NumberWidgetFormat()
    {
        this.id                     = null;

        this.size                   = new PrimitiveFunctor<>(null, TextSize.class);
        this.inlineLabel            = new PrimitiveFunctor<>(null, String.class);
        this.inlineLabelPosition    = new PrimitiveFunctor<>(null, InlineLabelPosition.class);
        this.descriptionStyle       = ModelFunctor.empty(TextStyle.class);
        this.valueStyle             = ModelFunctor.empty(TextStyle.class);
        this.valuePrefixStyle       = ModelFunctor.empty(TextStyle.class);
        this.valuePostfixStyle      = ModelFunctor.empty(TextStyle.class);
        this.labelStyle             = ModelFunctor.empty(TextStyle.class);
    }


    public NumberWidgetFormat(UUID id,
                              TextSize size,
                              String inlineLabel,
                              InlineLabelPosition inlineLabelPosition,
                              TextStyle descriptionStyle,
                              TextStyle valueStyle,
                              TextStyle valuePrefixStyle,
                              TextStyle valuePostfixStyle,
                              TextStyle labelStyle)
    {
        this.id                     = id;

        this.size                   = new PrimitiveFunctor<>(size, TextSize.class);
        this.inlineLabel            = new PrimitiveFunctor<>(inlineLabel, String.class);
        this.inlineLabelPosition    = new PrimitiveFunctor<>(inlineLabelPosition,
                                                             InlineLabelPosition.class);
        this.descriptionStyle       = ModelFunctor.full(descriptionStyle, TextStyle.class);
        this.valueStyle             = ModelFunctor.full(valueStyle, TextStyle.class);
        this.valuePrefixStyle       = ModelFunctor.full(valuePrefixStyle, TextStyle.class);
        this.valuePostfixStyle      = ModelFunctor.full(valuePostfixStyle, TextStyle.class);
        this.labelStyle             = ModelFunctor.full(labelStyle, TextStyle.class);

        this.setSize(size);
        this.setInlineLabelPosition(inlineLabelPosition);
        this.setDescriptionStyle(descriptionStyle);
        this.setValueStyle(valueStyle);
        this.setValuePrefixStyle(valuePrefixStyle);
        this.setValuePostfixStyle(valuePostfixStyle);
        this.setLabelStyle(labelStyle);
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

        UUID                id                = UUID.randomUUID();

        TextSize size                         = TextSize.fromYaml(yaml.atMaybeKey("size"));
        String              label             = yaml.atMaybeKey("label").getString();
        InlineLabelPosition labelPosition     = InlineLabelPosition.fromYaml(
                                                        yaml.atMaybeKey("label_position"));
        TextStyle           descriptionStyle  = TextStyle.fromYaml(
                                                        yaml.atMaybeKey("description_style"),
                                                        false);
        TextStyle           valueStyle        = TextStyle.fromYaml(
                                                    yaml.atMaybeKey("value_style"), false);
        TextStyle           valuePrefixStyle  = TextStyle.fromYaml(
                                                    yaml.atMaybeKey("value_prefix_style"), false);
        TextStyle           valuePostfixStyle = TextStyle.fromYaml(
                                                    yaml.atMaybeKey("value_postfix_style"), false);
        TextStyle           labelStyle        = TextStyle.fromYaml(
                                                    yaml.atMaybeKey("label_style"), false);

        return new NumberWidgetFormat(id, size, label, labelPosition, descriptionStyle, valueStyle,
                                      valuePrefixStyle, valuePostfixStyle, labelStyle);
    }


    private static NumberWidgetFormat asDefault()
    {
        NumberWidgetFormat numberWidgetFormat = new NumberWidgetFormat();

        numberWidgetFormat.setId(UUID.randomUUID());

        numberWidgetFormat.setSize(null);
        numberWidgetFormat.setInlineLabel(null);
        numberWidgetFormat.setInlineLabelPosition(null);
        numberWidgetFormat.setDescriptionStyle(null);
        numberWidgetFormat.setValueStyle(null);
        numberWidgetFormat.setValuePrefixStyle(null);
        numberWidgetFormat.setValuePostfixStyle(null);
        numberWidgetFormat.setLabelStyle(null);

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
        YamlBuilder yaml = YamlBuilder.map();

        yaml.putYaml("size", this.size());
        yaml.putString("label", this.label());
        yaml.putYaml("label_position", this.labelPosition());
        yaml.putYaml("description_style", this.descriptionStyle());
        yaml.putYaml("value_style", this.valueStyle());
        yaml.putYaml("value_prefix_style", this.valuePrefixStyle());
        yaml.putYaml("value_postfix_style", this.valuePostfixStyle());
        yaml.putYaml("label_style", this.labelStyle());

        return yaml;
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Size
    // --------------------------------------------------------------------------------------

    /**
     * The Number Widget's text size.
     * @return The Widget Content Size.
     */
    public TextSize size()
    {
        return this.size.getValue();
    }


    /**
     * Set the number widget's text size.
     * @param size The text size.
     */
    public void setSize(TextSize size)
    {
        if (size != null)
            this.size.setValue(size);
        else
            this.size.setValue(TextSize.MEDIUM);
    }


    // ** Label
    // --------------------------------------------------------------------------------------

    /**
     * The inline label. May be null.
     * @return The label.
     */
    public String label()
    {
        return this.inlineLabel.getValue();
    }


    /**
     * Set the label.
     * @param inlineLabel The label.
     */
    public void setInlineLabel(String inlineLabel)
    {
        this.inlineLabel.setValue(inlineLabel);
    }


    // ** Label Position
    // --------------------------------------------------------------------------------------

    /**
     * The position of the widget's label.
     * @return The inline label text.
     */
    public InlineLabelPosition labelPosition()
    {
        return this.inlineLabelPosition.getValue();
    }


    /**
     * Set the label position.
     * @param inlineLabelPosition The label position.
     */
    public void setInlineLabelPosition(InlineLabelPosition inlineLabelPosition)
    {
        if (inlineLabelPosition != null)
            this.inlineLabelPosition.setValue(inlineLabelPosition);
        else
            this.inlineLabelPosition.setValue(InlineLabelPosition.LEFT);
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
                                                              TextColor.DARK,
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
                                        TextColor.LIGHT,
                                        TextSize.MEDIUM_SMALL);
            this.valueStyle.setValue(defaultValueStyle);
        }
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
                                        TextColor.LIGHT,
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
                                                               TextColor.LIGHT,
                                                               TextSize.MEDIUM);
            this.valuePostfixStyle.setValue(defaultValuePostfixStyle);
        }
    }


    // ** Label Style
    // --------------------------------------------------------------------------------------

    /**
     * The postfix style.
     * @return The postfix style.
     */
    public TextStyle labelStyle()
    {
        return this.labelStyle.getValue();
    }


    public void setLabelStyle(TextStyle style)
    {
        if (style != null) {
            this.labelStyle.setValue(style);
        }
        else {
            TextStyle defaultLabelStyle = new TextStyle(UUID.randomUUID(),
                                                        TextColor.VERY_DARK,
                                                        TextSize.MEDIUM);
            this.labelStyle.setValue(defaultLabelStyle);
        }
    }


}
