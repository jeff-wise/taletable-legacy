
package com.kispoko.tome.sheet.widget.table;


import com.kispoko.tome.sheet.widget.table.cell.CellUnion;
import com.kispoko.tome.sheet.widget.table.column.ColumnUnion;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Table Widget Row
 */
public class TableRow implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                  id;

    private CollectionValue<CellUnion> cells;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TableRow()
    {
        this.id    = null;

        List<Class<? extends CellUnion>> cellClassList = new ArrayList<>();
        cellClassList.add(CellUnion.class);
        this.cells = CollectionValue.empty(cellClassList);
    }


    public TableRow(UUID id, List<CellUnion> cells)
    {
        this.id = id;

        List<Class<? extends CellUnion>> cellClassList = new ArrayList<>();
        cellClassList.add(CellUnion.class);
        this.cells = CollectionValue.full(cells, cellClassList);
    }


    public static TableRow fromYaml(Yaml yaml, final List<ColumnUnion> columns)
                  throws YamlException
    {
        UUID id = UUID.randomUUID();

        List<CellUnion> cells = yaml.atKey("cells").forEach(new Yaml.ForEach<CellUnion>() {
            @Override
            public CellUnion forEach(Yaml yaml, int columnIndex) throws YamlException {
                return CellUnion.fromYaml(yaml, columns.get(columnIndex));
            }
        });

        return new TableRow(id, cells);
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
     * This method is called when the Table Widget is completely loaded for the first time.
     */
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the cells in the row in order.
     * @return The list of cells.
     */
    public List<CellUnion> getCells()
    {
        return this.cells.getValue();
    }


    /**
     * Get the cell at the specified column index.
     * @param index The column index.
     * @return The Cell Union.
     */
    public CellUnion cellAtIndex(int index)
    {
        return this.getCells().get(index);
    }


    /**
     * Get the number of cells in the row.
     * @return The row width.
     */
    public int width()
    {
        return this.getCells().size();
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
