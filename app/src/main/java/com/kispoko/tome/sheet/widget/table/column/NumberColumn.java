
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

    public NumberColumn(UUID id, String name, Integer defaultValue, CellAlignment defaultAlignment)
    {
        this.id               = id;

        this.name             = new PrimitiveValue<>(name, this, String.class);
        this.defaultValue     = new PrimitiveValue<>(defaultValue, this, Integer.class);
        this.defaultAlignment = new PrimitiveValue<>(defaultAlignment, this, CellAlignment.class);
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


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onModelUpdate(String valueName) { }


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
