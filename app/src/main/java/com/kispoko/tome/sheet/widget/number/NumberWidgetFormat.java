
package com.kispoko.tome.sheet.widget.number;


import com.kispoko.tome.sheet.widget.NumberWidget;
import com.kispoko.tome.sheet.widget.text.TextWidgetFormat;
import com.kispoko.tome.sheet.widget.util.WidgetContentSize;
import com.kispoko.tome.sheet.widget.util.WidgetFormat;
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


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public NumberWidgetFormat()
    {
        this.id     = null;

        this.size   = new PrimitiveFunctor<>(null, WidgetContentSize.class);
    }


    public NumberWidgetFormat(UUID id, WidgetContentSize size)
    {
        this.id     = id;

        this.size   = new PrimitiveFunctor<>(size, WidgetContentSize.class);

        this.setSize(size);
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

        return new NumberWidgetFormat(id, size);
    }


    private static NumberWidgetFormat asDefault()
    {
        NumberWidgetFormat numberWidgetFormat = new NumberWidgetFormat();

        numberWidgetFormat.setId(UUID.randomUUID());
        numberWidgetFormat.setSize(null);

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

}
