
package com.kispoko.tome.sheet.widget.number;


import com.kispoko.tome.sheet.widget.util.WidgetContentSize;
import com.kispoko.tome.sheet.widget.util.InlineLabelPosition;
import com.kispoko.tome.sheet.widget.util.WidgetTextTint;
import com.kispoko.tome.util.model.Model;
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

    private PrimitiveFunctor<WidgetContentSize>     size;
    private PrimitiveFunctor<WidgetTextTint>        tint;
    private PrimitiveFunctor<String>                label;
    private PrimitiveFunctor<InlineLabelPosition>   labelPosition;
    private PrimitiveFunctor<NumberWidgetStyle>     style;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public NumberWidgetFormat()
    {
        this.id             = null;

        this.size           = new PrimitiveFunctor<>(null, WidgetContentSize.class);
        this.tint           = new PrimitiveFunctor<>(null, WidgetTextTint.class);
        this.label          = new PrimitiveFunctor<>(null, String.class);
        this.labelPosition  = new PrimitiveFunctor<>(null, InlineLabelPosition.class);
        this.style          = new PrimitiveFunctor<>(null, NumberWidgetStyle.class);
    }


    public NumberWidgetFormat(UUID id,
                              WidgetContentSize size,
                              WidgetTextTint tint,
                              String label,
                              InlineLabelPosition labelPosition,
                              NumberWidgetStyle style)
    {
        this.id             = id;

        this.size           = new PrimitiveFunctor<>(size, WidgetContentSize.class);
        this.tint           = new PrimitiveFunctor<>(tint, WidgetTextTint.class);
        this.label          = new PrimitiveFunctor<>(label, String.class);
        this.labelPosition  = new PrimitiveFunctor<>(labelPosition, InlineLabelPosition.class);
        this.style          = new PrimitiveFunctor<>(style, NumberWidgetStyle.class);

        this.setSize(size);
        this.setTint(tint);
        this.setLabelPosition(labelPosition);
        this.setStyle(style);
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

        UUID                id            = UUID.randomUUID();

        WidgetContentSize   size          = WidgetContentSize.fromYaml(yaml.atMaybeKey("size"));
        WidgetTextTint      tint          = WidgetTextTint.fromYaml(yaml.atMaybeKey("tint"));
        String              label         = yaml.atMaybeKey("label").getString();
        InlineLabelPosition labelPosition = InlineLabelPosition.fromYaml(
                                                            yaml.atMaybeKey("label_position"));
        NumberWidgetStyle   style         = NumberWidgetStyle.fromYaml(yaml.atMaybeKey("style"));

        return new NumberWidgetFormat(id, size, tint, label, labelPosition, style);
    }


    private static NumberWidgetFormat asDefault()
    {
        NumberWidgetFormat numberWidgetFormat = new NumberWidgetFormat();

        numberWidgetFormat.setId(UUID.randomUUID());
        numberWidgetFormat.setSize(null);
        numberWidgetFormat.setTint(null);
        numberWidgetFormat.setLabel(null);
        numberWidgetFormat.setLabelPosition(null);
        numberWidgetFormat.setStyle(null);

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
    public WidgetContentSize size()
    {
        return this.size.getValue();
    }


    /**
     * Set the number widget's text size.
     * @param size The text size.
     */
    public void setSize(WidgetContentSize size)
    {
        if (size != null)
            this.size.setValue(size);
        else
            this.size.setValue(WidgetContentSize.MEDIUM);
    }


    // ** Tint
    // --------------------------------------------------------------------------------------

    /**
     * The number widget's tint.
     * @return The tint.
     */
    public WidgetTextTint tint()
    {
        return this.tint.getValue();
    }


    public void setTint(WidgetTextTint tint)
    {
        if (tint != null)
            this.tint.setValue(tint);
        else
            this.tint.setValue(WidgetTextTint.MEDIUM);
    }


    // ** Label
    // --------------------------------------------------------------------------------------

    /**
     * The inline label. May be null.
     * @return The label.
     */
    public String label()
    {
        return this.label.getValue();
    }


    /**
     * Set the label.
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


    // ** Style
    // --------------------------------------------------------------------------------------

    /**
     * The number widget style.
     * @return The style.
     */
    public NumberWidgetStyle style()
    {
        return this.style.getValue();
    }


    /**
     * Set the style. If null, defaults to NONE.
     * @param style The style.
     */
    public void setStyle(NumberWidgetStyle style)
    {
        if (style != null)
            this.style.setValue(style);
        else
            this.style.setValue(NumberWidgetStyle.NONE);
    }


}
