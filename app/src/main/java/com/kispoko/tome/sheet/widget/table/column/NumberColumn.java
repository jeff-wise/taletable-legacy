
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.sheet.widget.table.cell.CellAlignment;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Number ColumnUnion
 *
 * Contains metadata about the cells in a table's number column.
 */
public class NumberColumn implements Model, Column, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private PrimitiveValue<String>        name;
    private PrimitiveValue<Integer>       defaultValue;
    private PrimitiveValue<CellAlignment> alignment;
    private PrimitiveValue<Integer>       width;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberColumn()
    {
        this.id           = null;

        this.name         = new PrimitiveValue<>(null, String.class);
        this.defaultValue = new PrimitiveValue<>(null, Integer.class);
        this.alignment    = new PrimitiveValue<>(null, CellAlignment.class);
        this.width        = new PrimitiveValue<>(null, Integer.class);
    }


    public NumberColumn(UUID id,
                        String name,
                        Integer defaultValue,
                        CellAlignment alignment,
                        Integer width)
    {
        this.id           = id;

        this.name         = new PrimitiveValue<>(name, String.class);
        this.defaultValue = new PrimitiveValue<>(defaultValue, Integer.class);
        this.alignment    = new PrimitiveValue<>(alignment, CellAlignment.class);
        this.width        = new PrimitiveValue<>(width, Integer.class);
    }


    /**
     * Create a number column from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The parsed Number ColumnUnion.
     * @throws YamlException
     */
    public static NumberColumn fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID          id           = UUID.randomUUID();

        String        name         = yaml.atKey("name").getString();
        Integer       defaultValue = yaml.atKey("default_value").getInteger();
        CellAlignment alignment    = CellAlignment.fromYaml(yaml.atKey("default_alignment"));
        Integer       width        = yaml.atKey("width").getInteger();

        return new NumberColumn(id, name, defaultValue, alignment, width);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     * @return The model UUID.
     */
    public UUID getId()
    {
        return this.id;
    }


    /**
     * Set the model identifier.
     * @param id The new model UUID.
     */
    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Number Column is completely loaded for the first time.
     */
    public void onLoad() { }


    // > Column
    // ------------------------------------------------------------------------------------------

    /**
     * Get the column name.
     * @return The column name.
     */
    public String getName()
    {
        return this.name.getValue();
    }


    /**
     * Get the alignment of this cell.
     * @return The cell Alignment.
     */
    public CellAlignment getAlignment()
    {
        return this.alignment.getValue();
    }


    /**
     * Get the column width. All cells in the column should have the same width.
     * @return The column width.
     */
    public Integer getWidth()
    {
        return this.width.getValue();
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the default column value. Cells with null values are given this value (if this value
     * is not null).
     * @return The default value.
     */
    public Integer getDefaultValue()
    {
        return this.defaultValue.getValue();
    }



}
