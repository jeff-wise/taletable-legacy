
package com.kispoko.tome.sheet.widget.table;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Table Widget Format
 */
public class TableWidgetFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<Boolean>   showDividers;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public TableWidgetFormat()
    {
        this.id             = null;

        this.showDividers   = new PrimitiveFunctor<>(null, Boolean.class);
    }


    public TableWidgetFormat(UUID id, Boolean showDividers)
    {
        this.id             = id;

        this.showDividers   = new PrimitiveFunctor<>(showDividers, Boolean.class);
    }


    /**
     * Create a Table Widget Format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Table Widget Format.
     * @throws YamlParseException
     */
    public static TableWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return TableWidgetFormat.asDefault();

        UUID    id           = UUID.randomUUID();

        Boolean showDividers = yaml.atMaybeKey("show_dividers").getBoolean();

        return new TableWidgetFormat(id, showDividers);
    }


    /**
     * Create a Table Widget Format with default values.
     * @return The default Table Widget Format.
     */
    private static TableWidgetFormat asDefault()
    {
        TableWidgetFormat format = new TableWidgetFormat();

        format.setId(UUID.randomUUID());
        format.setShowDividers(null);

        return format;
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
                .putBoolean("show_dividers", this.showDividers());
    }


    // > State
    // --------------------------------------------------------------------------------------

    // ** Show Dividers
    // --------------------------------------------------------------------------------------

    /**
     * True if the table displays dividers between each row.
     * @return Show dividers?
     */
    public Boolean showDividers()
    {
        return this.showDividers.getValue();
    }


    /**
     * Set the show dividers value. If null, it defaults to true.
     * @param showDividers Show dividers?
     */
    public void setShowDividers(Boolean showDividers)
    {
        if (showDividers != null)
            this.showDividers.setValue(showDividers);
        else
            this.showDividers.setValue(true);
    }

}
