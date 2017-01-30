
package com.kispoko.tome.sheet.widget.number;


import com.kispoko.tome.sheet.widget.util.WidgetContentSize;
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

    private UUID id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<WidgetContentSize> size;
    private PrimitiveFunctor<WidgetTextTint>    tint;
    private PrimitiveFunctor<String>            inlineLabel;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public NumberWidgetFormat()
    {
        this.id             = null;

        this.size           = new PrimitiveFunctor<>(null, WidgetContentSize.class);
        this.tint           = new PrimitiveFunctor<>(null, WidgetTextTint.class);
        this.inlineLabel    = new PrimitiveFunctor<>(null, String.class);
    }


    public NumberWidgetFormat(UUID id,
                              WidgetContentSize size,
                              WidgetTextTint tint,
                              String inlineLabel)
    {
        this.id             = id;

        this.size           = new PrimitiveFunctor<>(size, WidgetContentSize.class);
        this.tint           = new PrimitiveFunctor<>(tint, WidgetTextTint.class);
        this.inlineLabel    = new PrimitiveFunctor<>(inlineLabel, String.class);

        this.setSize(size);
        this.setTint(tint);
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

        UUID              id          = UUID.randomUUID();

        WidgetContentSize size        = WidgetContentSize.fromYaml(yaml.atMaybeKey("size"));
        WidgetTextTint    tint        = WidgetTextTint.fromYaml(yaml.atMaybeKey("tint"));
        String            inlineLabel = yaml.atMaybeKey("inline_label").getString();

        return new NumberWidgetFormat(id, size, tint, inlineLabel);
    }


    private static NumberWidgetFormat asDefault()
    {
        NumberWidgetFormat numberWidgetFormat = new NumberWidgetFormat();

        numberWidgetFormat.setId(UUID.randomUUID());
        numberWidgetFormat.setSize(null);
        numberWidgetFormat.setTint(null);
        numberWidgetFormat.setInlineLabel(null);

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


    // ** Inline Label
    // --------------------------------------------------------------------------------------

    /**
     * A label to be displayed before the number widget value.
     * @return The inline label text.
     */
    public String inlineLabel()
    {
        return this.inlineLabel.getValue();
    }


    /**
     * Set the inline label.
     * @param inlineLabel The label to be displayed before the value.
     */
    public void setInlineLabel(String inlineLabel)
    {
        this.inlineLabel.setValue(inlineLabel);
    }


}
