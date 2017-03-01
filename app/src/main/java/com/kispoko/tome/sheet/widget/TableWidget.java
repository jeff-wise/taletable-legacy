
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.table.TableRow;
import com.kispoko.tome.sheet.widget.table.TableWidgetFormat;
import com.kispoko.tome.sheet.widget.table.cell.CellUnion;
import com.kispoko.tome.sheet.widget.table.cell.TextCell;
import com.kispoko.tome.sheet.widget.table.cell.TextCellFormat;
import com.kispoko.tome.sheet.widget.table.column.Column;
import com.kispoko.tome.sheet.widget.table.column.ColumnUnion;
import com.kispoko.tome.sheet.widget.table.column.TextColumn;
import com.kispoko.tome.sheet.widget.table.column.TextColumnFormat;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TableLayoutBuilder;
import com.kispoko.tome.util.ui.TableRowBuilder;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Widget: Table
 */
public class TableWidget extends Widget
                         implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<TableWidgetFormat> format;
    private ModelFunctor<WidgetData>        widgetData;
    private CollectionFunctor<ColumnUnion>  columns;
    private CollectionFunctor<TableRow>     rows;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private TableRow                        headerRow;

    private GroupParent                     groupParent;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TableWidget()
    {
        this.id         = null;

        this.format     = ModelFunctor.empty(TableWidgetFormat.class);
        this.widgetData = ModelFunctor.empty(WidgetData.class);

        List<Class<? extends ColumnUnion>> columnClassList = new ArrayList<>();
        columnClassList.add(ColumnUnion.class);
        this.columns    = CollectionFunctor.empty(columnClassList);

        List<Class<? extends TableRow>> rowClassList = new ArrayList<>();
        rowClassList.add(TableRow.class);
        this.rows       = CollectionFunctor.empty(rowClassList);
    }


    public TableWidget(UUID id,
                       TableWidgetFormat format,
                       WidgetData widgetData,
                       List<ColumnUnion> columns,
                       List<TableRow> rows)
    {
        this.id         = id;

        this.format     = ModelFunctor.full(format, TableWidgetFormat.class);
        this.widgetData = ModelFunctor.full(widgetData, WidgetData.class);

        List<Class<? extends ColumnUnion>> columnClassList = new ArrayList<>();
        columnClassList.add(ColumnUnion.class);
        this.columns    = CollectionFunctor.full(columns, columnClassList);

        List<Class<? extends TableRow>> rowClassList = new ArrayList<>();
        rowClassList.add(TableRow.class);
        this.rows        = CollectionFunctor.full(rows, rowClassList);

        initializeTableWidget();

        // TODO validate that column types and cell types match
    }


    /**
     * Create a Table Widget from its yaml representation.
     * @param yaml The Yaml parser object.
     * @return A new TableWidget.
     * @throws YamlParseException
     */
    public static TableWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID              id      = UUID.randomUUID();

        // ** Format
        TableWidgetFormat format  = TableWidgetFormat.fromYaml(yaml.atMaybeKey("format"));

        // ** Widget Data
        WidgetData   widgetData   = WidgetData.fromYaml(yaml.atKey("data"), false);

        // ** Columns
        final List<ColumnUnion> columns
                = yaml.atKey("columns").forEach(new YamlParser.ForEach<ColumnUnion>() {
             @Override
             public ColumnUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                 return ColumnUnion.fromYaml(yaml);
             }
        });

        // ** Rows
        List<TableRow> rows = yaml.atKey("rows").forEach(new YamlParser.ForEach<TableRow>() {
            @Override
            public TableRow forEach(YamlParser yaml, int index) throws YamlParseException {
                return TableRow.fromYaml(yaml, columns);
            }
        });

        return new TableWidget(id, format, widgetData, columns, rows);
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
    public void onLoad()
    {
        initializeTableWidget();
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Table Widget yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("data", this.data())
                .putYaml("format", this.format())
                .putList("columns", this.columns())
                .putList("rows", this.rows());
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    @Override
    public void initialize(GroupParent groupParent)
    {
        this.groupParent = groupParent;

        for (TableRow tableRow : this.rows()) {
            tableRow.initialize(this.columns());
        }
    }


    /**
     * Get the widget's common data values.
     * @return The widget's WidgetData.
     */
    @Override
    public WidgetData data()
    {
        return this.widgetData.getValue();
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Format
    // ------------------------------------------------------------------------------------------

    /**
     * The table widget formatting options.
     * @return The format.
     */
    public TableWidgetFormat format()
    {
        return this.format.getValue();
    }


    // ** Width
    // ------------------------------------------------------------------------------------------

    /**
     * Get the table width (number of columns).
     * @return The table width.
     */
    public int width()
    {
        return this.columns().size();
    }

    // ** Height
    // ------------------------------------------------------------------------------------------

    /**
     * Get the table height (number of rows, not counting header).
     * @return The table height.
     */
    public int height()
    {
        return this.rows.getValue().size();
    }


    // ** Columns
    // ------------------------------------------------------------------------------------------

    /**
     * Get the column for the specified index.
     * @param index The column index.
     * @return The ColumnUnion.
     */
    public ColumnUnion columnAtIndex(int index)
    {
        return this.columns().get(index);

    }


    /**
     * Get the table columns.
     * @return List of the table's columns.
     */
    public List<ColumnUnion> columns()
    {
        return this.columns.getValue();
    }


    // ** Rows
    // ------------------------------------------------------------------------------------------

    /**
     * Get a specific row in the table (header does not count).
     * @param index The row index (starting at 0).
     * @return The table row at the specified index.
     */
    public TableRow getRow(Integer index)
    {
        return this.rows.getValue().get(index);
    }


    /**
     * Get all of the table rows.
     * @return The table row list (excluding header).
     */
    public List<TableRow> rows()
    {
        return this.rows.getValue();
    }


    // > Helpers
    // ------------------------------------------------------------------------------------------


    /**
     * Get a list of the table's column names.
     * @return A List of column names.
     */
    public List<String> columnNames()
    {
        List<String> columnNames = new ArrayList<>();

        for (ColumnUnion columnUnion : this.columns()) {
            columnNames.add(columnUnion.column().name());
        }

        return columnNames;
    }



    // > Views
    // ------------------------------------------------------------------------------------------

    /**
     * Create the tableWidget view.
     * @return
     */
    @Override
    public View view(boolean rowHasLabel, Context context)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayout widgetLayout  = this.widgetLayout(context);
        TableLayout  tableLayout = this.tileTableLayout(context);

        // [2] Structure
        // --------------------------------------------------------------------------------------

        widgetLayout.addView(tableLayout);

        // > Header
        // --------------------------------------------------------------------------------------

        tableLayout.addView(this.headerTableRow(context));

        // > Rows
        // --------------------------------------------------------------------------------------

        for (TableRow row : this.rows.getValue())
        {
            android.widget.TableRow tableRow = this.tableRow(row, context);
            tableLayout.addView(tableRow);
        }

        return widgetLayout;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initializeTableWidget()
    {
        // [1] Apply default formats
        // -------------------------------------------------------------------------------------

        if (this.data().format().width() == null)
            this.data().format().setWidth(1);

        if (this.data().format().background() == null)
            this.data().format().setBackground(BackgroundColor.NONE);


        // [2] The header row is derived from the column information, so create it each time the
        //     table widget is instantiated
        // --------------------------------------------------------------------------------------

        List<CellUnion> headerCells = new ArrayList<>();

        for (ColumnUnion columnUnion : this.columns())
        {
            TextVariable headerCellValue = TextVariable.asText(UUID.randomUUID(),
                                                               columnUnion.column().name());
            TextStyle headerCellStyle = new TextStyle(UUID.randomUUID(),
                                                      TextColor.THEME_DARK,
                                                      TextSize.SUPER_SMALL);

            TextCellFormat format = new TextCellFormat(UUID.randomUUID(),
                                                       columnUnion.column().alignment(),
                                                       BackgroundColor.NONE,
                                                       headerCellStyle);
            TextCell headerCell = new TextCell(UUID.randomUUID(),
                                               headerCellValue,
                                               format);
            CellUnion headerCellUnion = CellUnion.asText(null, headerCell);
            headerCells.add(headerCellUnion);
        }

        this.headerRow = new TableRow(null, headerCells);


    }


    private android.widget.TableRow tableRow(TableRow row, Context context)
    {
        TableRowBuilder tableRow = new TableRowBuilder();

        tableRow.layoutType         = LayoutType.TABLE;
        tableRow.width              = TableLayout.LayoutParams.MATCH_PARENT;
        tableRow.height             = TableLayout.LayoutParams.WRAP_CONTENT;

        tableRow.padding.left   = R.dimen.widget_table_row_padding_horz;
        tableRow.padding.right  = R.dimen.widget_table_row_padding_horz;

        android.widget.TableRow tableRowView = tableRow.tableRow(context);

        for (int i = 0; i < row.width(); i++)
        {
            CellUnion cell = row.cellAtIndex(i);
            View cellView = cell.view(this.columnAtIndex(i), context);
            tableRowView.addView(cellView);
        }

        return tableRowView;
    }


    private LinearLayout widgetLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation         = LinearLayout.VERTICAL;
        layout.width               = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height              = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private TableLayout tileTableLayout(Context context)
    {
        TableLayoutBuilder layout = new TableLayoutBuilder();

        layout.layoutType           = LayoutType.LINEAR;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.shrinkAllColumns     = true;

        if (this.format().showDividers())
        {
            switch (this.data().format().background())
            {
                case NONE:
                    switch (this.groupParent.background())
                    {
                        case LIGHT:
                            layout.divider = R.drawable.table_row_divider_light;
                            break;
                        case MEDIUM:
                            layout.divider = R.drawable.table_row_divider_medium;
                            break;
                        case DARK:
                            layout.divider = R.drawable.table_row_divider_dark;
                            break;
                    }
                    break;
                case LIGHT:
                    layout.divider = R.drawable.table_row_divider_light;
                    break;
                case DARK:
                    layout.divider = R.drawable.table_row_divider_medium;
                    break;
            }
        }

        return layout.tableLayout(context);
    }


    private android.widget.TableRow headerTableRow(Context context)
    {

        TableRowBuilder headerRow = new TableRowBuilder();

        headerRow.width          = android.widget.TableRow.LayoutParams.MATCH_PARENT;
        headerRow.height         = android.widget.TableRow.LayoutParams.WRAP_CONTENT;
        headerRow.padding.left   = R.dimen.widget_table_row_padding_horz;
        headerRow.padding.right  = R.dimen.widget_table_row_padding_horz;

        android.widget.TableRow headerRowView = headerRow.tableRow(context);


        for (int i = 0; i < this.width(); i++)
        {
            CellUnion headerCell = this.headerRow.cellAtIndex(i);

            Column column = this.columnAtIndex(i).column();
            TextColumnFormat format = new TextColumnFormat(UUID.randomUUID(),
                                                           null,
                                                           column.alignment(),
                                                           column.width(),
                                                           null);
            TextColumn textColumn = new TextColumn(null, null, null, null, format,
                                                   false, false);
            ColumnUnion columnUnion = ColumnUnion.asText(null, textColumn);

            LinearLayout headerCellView = (LinearLayout) headerCell.view(columnUnion, context);

            headerRowView.addView(headerCellView);
        }

        return headerRowView;
    }



}
