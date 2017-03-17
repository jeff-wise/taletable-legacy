
package com.kispoko.tome.sheet.widget.table;


import com.kispoko.tome.sheet.widget.util.Height;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Table Row Format
 */
public class TableRowFormat implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<Height>    cellHeight;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TableRowFormat()
    {
        this.id         = null;

        this.cellHeight = new PrimitiveFunctor<>(null, Height.class);
    }


    public TableRowFormat(UUID id, Height cellHeight)
    {
        this.id         = id;

        this.cellHeight = new PrimitiveFunctor<>(cellHeight, Height.class);

        this.setCellHeight(cellHeight);
    }


    /**
     * Create a Table Row Format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Table Row Format.
     * @throws YamlParseException
     */
    public static TableRowFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return TableRowFormat.asDefault();

        UUID   id         = UUID.randomUUID();

        Height cellHeight = Height.fromYaml(yaml.atMaybeKey("cell_height"));

        return new TableRowFormat(id, cellHeight);
    }


    /**
     * Create a Table Row Format with default values.
     * @return The default Table Row Format.
     */
    private static TableRowFormat asDefault()
    {
        TableRowFormat format = new TableRowFormat();

        format.setId(UUID.randomUUID());

        format.setCellHeight(null);

        return format;
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     *
     * @return The model UUID.
     */
    public UUID getId() {
        return this.id;
    }


    /**
     * Set the model identifier.
     *
     * @param id The new model UUID.
     */
    public void setId(UUID id) {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Table Widget is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Table Row's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("cell_height", this.cellHeight());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // > Cell Height
    // ------------------------------------------------------------------------------------------

    /**
     * The cell height.
     * @return The cell height.
     */
    public Height cellHeight()
    {
        return this.cellHeight.getValue();
    }


    /**
     * Set the cell height.
     * @param cellHeight The cell height.
     */
    public void setCellHeight(Height cellHeight)
    {
        this.cellHeight.setValue(cellHeight);
    }

}
