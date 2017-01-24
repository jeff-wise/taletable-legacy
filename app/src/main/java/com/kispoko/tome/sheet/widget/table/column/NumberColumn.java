
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.sheet.widget.table.cell.CellAlignment;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Number ColumnUnion
 *
 * Contains metadata about the cells in a table's number column.
 */
public class NumberColumn implements Model, Column, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>        name;
    private PrimitiveFunctor<Integer>       defaultValue;
    private PrimitiveFunctor<CellAlignment> alignment;
    private PrimitiveFunctor<Integer>       width;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberColumn()
    {
        this.id           = null;

        this.name         = new PrimitiveFunctor<>(null, String.class);
        this.defaultValue = new PrimitiveFunctor<>(null, Integer.class);
        this.alignment    = new PrimitiveFunctor<>(null, CellAlignment.class);
        this.width        = new PrimitiveFunctor<>(null, Integer.class);
    }


    public NumberColumn(UUID id,
                        String name,
                        Integer defaultValue,
                        CellAlignment alignment,
                        Integer width)
    {
        this.id           = id;

        this.name         = new PrimitiveFunctor<>(name, String.class);
        this.defaultValue = new PrimitiveFunctor<>(defaultValue, Integer.class);
        this.alignment    = new PrimitiveFunctor<>(alignment, CellAlignment.class);
        this.width        = new PrimitiveFunctor<>(width, Integer.class);
    }


    /**
     * Create a number column from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The parsed Number ColumnUnion.
     * @throws YamlParseException
     */
    public static NumberColumn fromYaml(YamlParser yaml)
                  throws YamlParseException
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


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Number Column's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putInteger("default_value", this.defaultValue())
                .putYaml("default_alignment", this.alignment())
                .putInteger("width", this.width());
    }


    // > Column
    // ------------------------------------------------------------------------------------------

    /**
     * Get the column name.
     * @return The column name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    /**
     * Get the alignment of this cell.
     * @return The cell Alignment.
     */
    public CellAlignment alignment()
    {
        return this.alignment.getValue();
    }


    /**
     * Get the column width. All cells in the column should have the same width.
     * @return The column width.
     */
    public Integer width()
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
    public Integer defaultValue()
    {
        return this.defaultValue.getValue();
    }



}
