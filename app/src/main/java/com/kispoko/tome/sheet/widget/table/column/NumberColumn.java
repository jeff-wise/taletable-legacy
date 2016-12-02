
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.sheet.widget.table.cell.CellAlignment;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.UUID;



/**
 * Number ColumnUnion
 *
 * Contains metadata about the cells in a table's number column.
 */
public class NumberColumn implements Model, Column
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private PrimitiveValue<String>        name;
    private PrimitiveValue<Integer>       defaultValue;
    private PrimitiveValue<CellAlignment> defaultAlignment;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberColumn()
    {
        this.id               = null;

        this.name             = new PrimitiveValue<>(null, String.class);
        this.defaultValue     = new PrimitiveValue<>(null, Integer.class);
        this.defaultAlignment = new PrimitiveValue<>(null, CellAlignment.class);
    }


    public NumberColumn(UUID id, String name, Integer defaultValue, CellAlignment defaultAlignment)
    {
        this.id               = id;

        this.name             = new PrimitiveValue<>(name, String.class);
        this.defaultValue     = new PrimitiveValue<>(defaultValue, Integer.class);
        this.defaultAlignment = new PrimitiveValue<>(defaultAlignment, CellAlignment.class);
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
        UUID          id               = UUID.randomUUID();

        String        name             = yaml.atKey("name").getString();
        Integer       defaultValue     = yaml.atKey("default_value").getInteger();
        CellAlignment defaultAlignment = CellAlignment.fromYaml(yaml.atKey("default_alignment"));

        return new NumberColumn(id, name, defaultValue, defaultAlignment);
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


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the column name.
     * @return The column name.
     */
    public String getName()
    {
        return this.name.getValue();
    }

}
