
package com.kispoko.tome.sheet.widget.bool;


import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.functor.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Boolean Widget Format
 */
public class BooleanWidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<TextSize> size;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public BooleanWidgetFormat()
    {
        this.id         = null;

        this.size       = new PrimitiveFunctor<>(null, TextSize.class);
    }


    public BooleanWidgetFormat(UUID id, TextSize size)
    {
        this.id         = id;

        this.size       = new PrimitiveFunctor<>(size, TextSize.class);

        this.setSize(size);
    }


    /**
     * Create a Boolean Widget Format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsedd Boolean Widget Format.
     * @throws YamlParseException
     */
    public static BooleanWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return BooleanWidgetFormat.asDefault();

        UUID                id      = UUID.randomUUID();

        TextSize size    = TextSize.fromYaml(yaml.atMaybeKey("size"));

        return new BooleanWidgetFormat(id, size);
    }


    private static BooleanWidgetFormat asDefault()
    {
        BooleanWidgetFormat booleanWidgetFormat = new BooleanWidgetFormat();

        booleanWidgetFormat.setId(UUID.randomUUID());
        booleanWidgetFormat.setSize(null);

        return booleanWidgetFormat;
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    // ** Id
    // -----------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // -----------------------------------------------------------------------------------------

    /**
     * Called when the Text Widget Format is completely loaded.
     */
    public void onLoad() { }


    // > To Yaml
    // -----------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        YamlBuilder yaml = YamlBuilder.map();

        yaml.putYaml("size", this.size());

        return yaml;
    }


    // > State
    // -----------------------------------------------------------------------------------------

    // ** Size
    // -----------------------------------------------------------------------------------------
    // ** Size
    // --------------------------------------------------------------------------------------

    /**
     * The Boolean Widget's text size.
     * @return The Widget Content Size.
     */
    public TextSize size()
    {
        return this.size.getValue();
    }


    /**
     * Set the boolean widget's text size.
     * @param size The text size.
     */
    public void setSize(TextSize size)
    {
        if (size != null)
            this.size.setValue(size);
        else
            this.size.setValue(TextSize.SMALL);
    }


}
