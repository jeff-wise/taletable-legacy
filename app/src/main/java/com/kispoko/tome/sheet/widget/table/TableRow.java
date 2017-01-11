
package com.kispoko.tome.sheet.widget.table;


import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.sheet.widget.table.cell.CellUnion;
import com.kispoko.tome.sheet.widget.table.column.ColumnUnion;
import com.kispoko.tome.sheet.widget.util.WidgetContainer;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Table Widget Row
 */
public class TableRow implements Model, WidgetContainer, ToYaml, Serializable {

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private CollectionFunctor<CellUnion> cells;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private String namespace;
    private List<Variable> namespacedVariables;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TableRow()
    {
        this.id = null;

        List<Class<? extends CellUnion>> cellClassList = new ArrayList<>();
        cellClassList.add(CellUnion.class);
        this.cells = CollectionFunctor.empty(cellClassList);

        this.namespace = null;
        this.namespacedVariables = new ArrayList<>();
    }


    public TableRow(UUID id, List<CellUnion> cells)
    {
        this.id = id;

        List<Class<? extends CellUnion>> cellClassList = new ArrayList<>();
        cellClassList.add(CellUnion.class);
        this.cells = CollectionFunctor.full(cells, cellClassList);

        this.initializeTableRow();
    }


    /**
     * Create a table row from its Yaml representation.
     *
     * @param yaml    The yaml parser.
     * @param columns The table columns.
     * @return The parsed Table Row.
     * @throws YamlParseException
     */
    public static TableRow fromYaml(YamlParser yaml, final List<ColumnUnion> columns)
            throws YamlParseException
    {
        UUID id = UUID.randomUUID();

        List<CellUnion> cells = yaml.atKey("cells").forEach(new YamlParser.ForEach<CellUnion>() {
            @Override
            public CellUnion forEach(YamlParser yaml, int columnIndex) throws YamlParseException {
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
    public void onLoad() {
        initializeTableRow();
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Table Row's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putList("cells", this.cells());
    }


    // > Widget Container
    // ------------------------------------------------------------------------------------------

    /**
     * Set the container namespace.
     * @param namespace The namespace.
     */
    public void setNamespace(String namespace)
    {
        this.namespace = namespace;

        // > Update all namespaced variables
        for (Variable variable : this.namespacedVariables)
        {
            String newName = this.namespace + "." + variable.name();
            variable.setName(newName);
        }
    }


    // > Initialize
    // ------------------------------------------------------------------------------------------

    public void initialize()
    {
        // Initialize each cell
        for (CellUnion cellUnion : this.cells()) {
            cellUnion.cell().initialize(this);
        }
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the cells in the row in order.
     * @return The list of cells.
     */
    public List<CellUnion> cells()
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
        return this.cells().get(index);
    }


    /**
     * Get the number of cells in the row.
     * @return The row width.
     */
    public int width()
    {
        return this.cells().size();
    }


    /**
     * Get the cell at the given index in the row.
     * @param index The index of the cell.
     * @return The CellUnion in the row.
     */
    public CellUnion cellAtIndex(Integer index)
    {
        return this.cells().get(index);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initializeTableRow()
    {
        // [1] Initialize namespace to null
        // --------------------------------------------------------------------------------------

        this.namespace              = null;

        // [2] Index each namespaced variable
        // --------------------------------------------------------------------------------------

        this.namespacedVariables = new ArrayList<>();
        for (CellUnion cellUnion : this.cells()) {
            List<Variable> variables = cellUnion.cell().namespacedVariables();
            this.namespacedVariables.addAll(variables);
        }
    }

}
