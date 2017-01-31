
package com.kispoko.tome.sheet.widget.list;


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
 * List Widget Format
 */
public class ListWidgetFormat implements Model, ToYaml, Serializable
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


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ListWidgetFormat()
    {
        this.id             = null;

        this.size           = new PrimitiveFunctor<>(null, WidgetContentSize.class);
        this.tint           = new PrimitiveFunctor<>(null, WidgetTextTint.class);
    }


    public ListWidgetFormat(UUID id,
                            WidgetContentSize size,
                            WidgetTextTint tint)
    {
        this.id             = id;

        this.size           = new PrimitiveFunctor<>(size, WidgetContentSize.class);
        this.tint           = new PrimitiveFunctor<>(tint, WidgetTextTint.class);

        this.setSize(size);
        this.setTint(tint);
    }


    /**
     * Create a List Widget Format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The List Widget Format.
     * @throws YamlParseException
     */
    public static ListWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return ListWidgetFormat.asDefault();

        UUID              id          = UUID.randomUUID();

        WidgetContentSize size        = WidgetContentSize.fromYaml(yaml.atMaybeKey("size"));
        WidgetTextTint    tint        = WidgetTextTint.fromYaml(yaml.atMaybeKey("tint"));

        return new ListWidgetFormat(id, size, tint);
    }


    private static ListWidgetFormat asDefault()
    {
        ListWidgetFormat listWidgetFormat = new ListWidgetFormat();

        listWidgetFormat.setId(UUID.randomUUID());
        listWidgetFormat.setSize(null);
        listWidgetFormat.setTint(null);

        return listWidgetFormat;
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

        return yaml;
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Size
    // --------------------------------------------------------------------------------------

    /**
     * The List Widget's item text size.
     * @return The Widget Content Size.
     */
    public WidgetContentSize size()
    {
        return this.size.getValue();
    }


    /**
     * Set the list widget's item text size.
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
     * The list widget item text tint.
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


}
