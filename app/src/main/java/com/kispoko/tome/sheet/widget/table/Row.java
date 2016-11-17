
package com.kispoko.tome.sheet.widget.table;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * Table Widget Row
 */
public class Row implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                  id;

    private CollectionValue<Cell> cells;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Row(UUID id, List<Cell> cells)
    {
        this.id = id;

        List<Class<Cell>> cellClassList = Arrays.asList(Cell.class);
        this.cells = new CollectionValue<>(cells, this, cellClassList);
    }


    public static Row fromYaml(Yaml yaml, int rowIndex)
                  throws YamlException
    {
        UUID id = UUID.randomUUID();

        List<Cell> cells = yaml.atKey("cells").forEach(new Yaml.ForEach<Cell>() {
            @Override
            public Cell forEach(Yaml yaml, int columnIndex) throws YamlException {
                return Cell.fromYaml(yaml, rowIndex, columnIndex);
            }
        });

        return new Row(id, cells);
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

    public List<Cell> getCells()
    {
        return this.cells.getValue();
    }


}
