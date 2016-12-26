
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.sheet.widget.table.cell.CellAlignment;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Text ColumnUnion
 *
 * Contains metadata about the cells in a table's text column.
 */
public class TextColumn implements Model, Column, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                          id;

    private PrimitiveFunctor<String> name;
    private PrimitiveFunctor<String> defaultValue;
    private PrimitiveFunctor<CellAlignment> alignment;
    private PrimitiveFunctor<Integer> width;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextColumn()
    {
        this.id            = null;

        this.name          = new PrimitiveFunctor<>(null, String.class);
        this.defaultValue  = new PrimitiveFunctor<>(null, String.class);
        this.alignment     = new PrimitiveFunctor<>(null, CellAlignment.class);
        this.width         = new PrimitiveFunctor<>(null, Integer.class);
    }


    public TextColumn(UUID id,
                      String name,
                      String defaultValue,
                      CellAlignment alignment,
                      Integer width)
    {
        this.id            = id;

        this.name          = new PrimitiveFunctor<>(name, String.class);
        this.defaultValue  = new PrimitiveFunctor<>(defaultValue, String.class);
        this.alignment     = new PrimitiveFunctor<>(alignment, CellAlignment.class);
        this.width         = new PrimitiveFunctor<>(width, Integer.class);
    }


    /**
     * Create a text column from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The parsed Text ColumnUnion.
     * @throws YamlException
     */
    public static TextColumn fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID          id           = UUID.randomUUID();

        String        name         = yaml.atKey("name").getString();
        String        defaultValue = yaml.atKey("default_value").getString();
        CellAlignment alignment    = CellAlignment.fromYaml(yaml.atKey("default_alignment"));
        Integer       width        = yaml.atKey("width").getInteger();

        return new TextColumn(id, name, defaultValue, alignment, width);
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
     * This method is called when the Text Column is completely loaded for the first time.
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
     * Get the alignment of this column. All cells in the column should have the same alignment.
     * @return The column alignment.
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
    public String getDefaultValue()
    {
        return this.defaultValue.getValue();
    }

}
