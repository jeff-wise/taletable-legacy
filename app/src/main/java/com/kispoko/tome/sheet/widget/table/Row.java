
package com.kispoko.tome.sheet.widget.table;


import com.kispoko.tome.sheet.widget.table.cell.CellUnion;
import com.kispoko.tome.sheet.widget.table.column.ColumnUnion;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.ArrayList;
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

    private CollectionValue<CellUnion> cells;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Row()
    {
        this.id    = null;

        List<Class<? extends CellUnion>> cellClassList = new ArrayList<>();
        cellClassList.add(CellUnion.class);
        this.cells = new CollectionValue<>(null, cellClassList);
    }


    public Row(UUID id, List<CellUnion> cells)
    {
        this.id = id;

        List<Class<? extends CellUnion>> cellClassList = new ArrayList<>();
        cellClassList.add(CellUnion.class);
        this.cells = new CollectionValue<>(cells, cellClassList);
    }


    public static Row fromYaml(Yaml yaml, final List<ColumnUnion> columns)
                  throws YamlException
    {
        UUID id = UUID.randomUUID();

        List<CellUnion> cells = yaml.atKey("cells").forEach(new Yaml.ForEach<CellUnion>() {
            @Override
            public CellUnion forEach(Yaml yaml, int columnIndex) throws YamlException {
                return CellUnion.fromYaml(yaml, columns.get(columnIndex));
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

    public void onValueUpdate(String valueName) { }


    // > State
    // ------------------------------------------------------------------------------------------

    public List<CellUnion> getCells()
    {
        return this.cells.getValue();
    }


    /**
     * Get the cell at the given index in the row.
     * @param index The index of the cell.
     * @return The CellUnion in the row.
     */
    public CellUnion cellAtIndex(Integer index)
    {
        return this.getCells().get(index);
    }


}
