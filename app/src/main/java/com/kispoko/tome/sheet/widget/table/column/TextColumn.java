
package com.kispoko.tome.sheet.widget.table.column;


import com.kispoko.tome.sheet.widget.table.cell.CellAlignment;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.UUID;



/**
 * Text ColumnUnion
 *
 * Contains metadata about the cells in a table's text column.
 */
public class TextColumn implements Model, Column
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                          id;

    private PrimitiveValue<String>        name;
    private PrimitiveValue<String>        defaultValue;
    private PrimitiveValue<CellAlignment> defaultAlignment;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextColumn(UUID id, String name, String defaultValue, CellAlignment defaultAlignment)
    {
        this.id               = id;

        this.name             = new PrimitiveValue<>(name, this, String.class);
        this.defaultValue     = new PrimitiveValue<>(defaultValue, this, String.class);
        this.defaultAlignment = new PrimitiveValue<>(defaultAlignment, this, CellAlignment.class);
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
        UUID          id               = UUID.randomUUID();

        String        name             = yaml.atKey("name").getString();
        String        defaultValue     = yaml.atKey("default_value").getString();
        CellAlignment defaultAlignment = CellAlignment.fromYaml(yaml.atKey("default_alignment"));

        return new TextColumn(id, name, defaultValue, defaultAlignment);
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
