
package com.kispoko.tome.sheet.widget.table;


import com.kispoko.tome.engine.variable.Namespace;
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.sheet.widget.table.cell.CellType;
import com.kispoko.tome.sheet.widget.table.cell.CellUnion;
import com.kispoko.tome.sheet.widget.table.cell.TextCell;
import com.kispoko.tome.sheet.widget.table.column.ColumnUnion;
import com.kispoko.tome.sheet.widget.util.WidgetContainer;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;



/**
 * Table Widget Row
 */
public class TableRow extends Model
                      implements WidgetContainer, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private CollectionFunctor<CellUnion>    cells;
    private ModelFunctor<TableRowFormat>    format;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Namespace                       namespace;
    private List<Variable>                  namespacedVariables;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TableRow()
    {
        this.id                     = null;

        this.cells                  = CollectionFunctor.empty(CellUnion.class);
        this.format                 = ModelFunctor.empty(TableRowFormat.class);

        this.namespace              = null;
        this.namespacedVariables    = new ArrayList<>();
    }


    public TableRow(UUID id, List<CellUnion> cells, TableRowFormat format)
    {
        this.id         = id;

        this.cells      = CollectionFunctor.full(cells, CellUnion.class);
        this.format     = ModelFunctor.full(format, TableRowFormat.class);

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

        TableRowFormat format = TableRowFormat.fromYaml(yaml.atMaybeKey("format"));

        return new TableRow(id, cells, format);
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
    @Override
    public void setNamespace(Namespace namespace)
    {
        this.namespace = namespace;

        // > Update all namespaced variables
        for (Variable variable : this.namespacedVariables)
        {
            //String newName = this.namespace + "." + variable.name();
            variable.setNamespace(this.namespace);
        }
    }


    // > Initialize
    // ------------------------------------------------------------------------------------------

    public void initialize(List<ColumnUnion> columns,
                           TableWidgetFormat tableFormat,
                           UUID tableWidgetId)
    {
        // [1] Apply default row/cell height
        // --------------------------------------------------------------------------------------
        if (tableFormat.cellHeight() != null && this.format().cellHeight() == null)
            this.format().setCellHeight(tableFormat.cellHeight());

        // [1] Initialize the cells
        // --------------------------------------------------------------------------------------
        for (int i = 0; i < this.width(); i++)
        {
            CellUnion   cell = this.cellAtIndex(i);
            ColumnUnion column = columns.get(i);

            switch (cell.type())
            {
                case TEXT:
                    cell.textCell().initialize(column.textColumn(), this, tableWidgetId);
                    break;
                case NUMBER:
                    cell.numberCell().initialize(column.numberColumn(), tableWidgetId);
                    break;
                case BOOLEAN:
                    cell.booleanCell().initialize(column.booleanColumn(), tableWidgetId);
                    break;
            }
        }

        // [2] Configure namespaces
        // --------------------------------------------------------------------------------------

        this.namespace              = null;

        // > Index each namespaced variable
        // --------------------------------------------------------------------------------------

        this.namespacedVariables = new ArrayList<>();
        for (CellUnion cellUnion : this.cells()) {
            List<Variable> variables = cellUnion.cell().namespacedVariables();
            this.namespacedVariables.addAll(variables);
        }

        // > Set the namespace if one is found
        // --------------------------------------------------------------------------------------

        for (CellUnion cellUnion : this.cells())
        {
            if (cellUnion.type() == CellType.TEXT)
            {
                TextCell textCell = cellUnion.textCell();
                if (textCell.valueVariable().definesNamespace())
                {
                    try {
                        this.setNamespace(textCell.valueVariable().namespace());
                    }
                    catch (NullVariableException exception) {

                    }
                }
            }
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
        return Collections.unmodifiableList(this.cells.getValue());
    }


    /**
     * The table row formatting options.
     * @return The format.
     */
    public TableRowFormat format()
    {
        return this.format.getValue();
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

    }

}
