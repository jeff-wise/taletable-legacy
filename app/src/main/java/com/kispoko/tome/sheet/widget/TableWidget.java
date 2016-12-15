
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.programming.variable.TextVariable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.action.Action;
import com.kispoko.tome.sheet.widget.table.TableRow;
import com.kispoko.tome.sheet.widget.table.cell.CellUnion;
import com.kispoko.tome.sheet.widget.table.cell.TextCell;
import com.kispoko.tome.sheet.widget.table.column.Column;
import com.kispoko.tome.sheet.widget.table.column.ColumnUnion;
import com.kispoko.tome.sheet.widget.table.column.TextColumn;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TableLayoutBuilder;
import com.kispoko.tome.util.ui.TableRowBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Widget: Table
 */
public class TableWidget extends Widget implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                         id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelValue<WidgetData>       widgetData;
    private CollectionValue<ColumnUnion> columns;
    private CollectionValue<TableRow>    rows;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private TableRow headerRow;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TableWidget()
    {
        this.id         = null;

        this.widgetData = ModelValue.empty(WidgetData.class);

        List<Class<? extends ColumnUnion>> columnClassList = new ArrayList<>();
        columnClassList.add(ColumnUnion.class);
        this.columns    = CollectionValue.empty(columnClassList);

        List<Class<? extends TableRow>> rowClassList = new ArrayList<>();
        rowClassList.add(TableRow.class);
        this.rows       = CollectionValue.empty(rowClassList);
    }


    public TableWidget(UUID id,
                       WidgetData widgetData,
                       List<ColumnUnion> columns,
                       List<TableRow> rows)
    {
        this.id = id;

        this.widgetData = ModelValue.full(widgetData, WidgetData.class);

        List<Class<? extends ColumnUnion>> columnClassList = new ArrayList<>();
        columnClassList.add(ColumnUnion.class);
        this.columns    = CollectionValue.full(columns, columnClassList);

        List<Class<? extends TableRow>> rowClassList = new ArrayList<>();
        rowClassList.add(TableRow.class);
        this.rows        = CollectionValue.full(rows, rowClassList);

        initialize();

        // TODO validate that column types and cell types match
    }


    /**
     * Create a Table Widget from its yaml representation.
     * @param yaml The Yaml parser object.
     * @return A new TableWidget.
     * @throws YamlException
     */
    public static TableWidget fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID         id             = UUID.randomUUID();

        // ** Widget Data
        WidgetData   widgetData     = WidgetData.fromYaml(yaml.atKey("data"));

        // ** Columns
        final List<ColumnUnion> columns
                = yaml.atKey("columns").forEach(new Yaml.ForEach<ColumnUnion>() {
             @Override
             public ColumnUnion forEach(Yaml yaml, int index) throws YamlException {
                 return ColumnUnion.fromYaml(yaml);
             }
        });

        // ** Rows
        List<TableRow> rows = yaml.atKey("rows").forEach(new Yaml.ForEach<TableRow>() {
            @Override
            public TableRow forEach(Yaml yaml, int index) throws YamlException {
                return TableRow.fromYaml(yaml, columns);
            }
        });

        return new TableWidget(id, widgetData, columns, rows);
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
        initialize();
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    /**
     * The widget type as a string.
     * @return The widget's type as a string.
     */
    public String name() {
        return "text";
    }


    /**
     * Get the widget's common data values.
     * @return The widget's WidgetData.
     */
    public WidgetData data()
    {
        return this.widgetData.getValue();
    }


    public void runAction(Action action) { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Width
    // ------------------------------------------------------------------------------------------

    /**
     * Get the table width (number of columns).
     * @return The table width.
     */
    public int width()
    {
        return this.getColumns().size();
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
        return this.getColumns().get(index);

    }


    /**
     * Get the table columns.
     * @return List of the table's columns.
     */
    public List<ColumnUnion> getColumns() {
        return this.columns.getValue();
    }


    // ** Rows
    // ------------------------------------------------------------------------------------------

    /**
     * Get a specific row in the table (header does not count).
     * @param index The row index (starting at 0).
     * @return The table row at the specified index.
     */
    public TableRow getRow(Integer index) {
        return this.rows.getValue().get(index);
    }


    /**
     * Get all of the table rows.
     * @return The table row list (excluding header).
     */
    public List<TableRow> getRows() {
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

        for (ColumnUnion columnUnion : this.getColumns()) {
            columnNames.add(columnUnion.getColumn().getName());
        }

        return columnNames;
    }



    // > Views
    // ------------------------------------------------------------------------------------------

    /**
     * Create the tableWidget view.
     * @return
     */
    public View tileView()
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        Context context = SheetManager.currentSheetContext();

        LinearLayout tileLayout  = this.tileLayout(context);
        TableLayout  tableLayout = this.tileTableLayout(context);

        // [2] Structure
        // --------------------------------------------------------------------------------------

        tileLayout.addView(this.tableTitleView(this.data().getFormat().getLabel(), context));
        tileLayout.addView(tableLayout);

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

        return tileLayout;
    }


    public View editorView(Context context)
    {
        return new LinearLayout(context);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initialize()
    {
        // The header row is derived from the column information, so create it each time the
        // table widget is instantiated
        // --------------------------------------------------------------------------------------

        List<CellUnion> headerCells = new ArrayList<>();

        for (ColumnUnion columnUnion : this.getColumns())
        {
            TextVariable headerCellValue = TextVariable.asText(UUID.randomUUID(),
                                                               null,
                                                               columnUnion.getColumn().getName(),
                                                               null);
            TextCell headerCell = new TextCell(UUID.randomUUID(),
                                               headerCellValue,
                                               columnUnion.getColumn().getAlignment(),
                                               null);
            CellUnion headerCellUnion = CellUnion.asText(null, headerCell);
            headerCells.add(headerCellUnion);
        }

        this.headerRow = new TableRow(null, headerCells);
    }


    private TextView tableTitleView(String title, Context context)
    {
        TextViewBuilder tableTitle = new TextViewBuilder();

        tableTitle.layoutType       = LayoutType.LINEAR;
        tableTitle.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        tableTitle.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        tableTitle.text             = title.toUpperCase();
        tableTitle.padding.top      = R.dimen.widget_table_title_padding_top;
        tableTitle.padding.left     = R.dimen.widget_table_title_paddin_left;
        tableTitle.size             = R.dimen.widget_table_title_text_size;
        tableTitle.color            = R.color.gold_8;
        tableTitle.font             = Font.sansSerifFontBold(context);

        return tableTitle.textView(context);
    }


    private android.widget.TableRow tableRow(TableRow row, Context context)
    {
        TableRowBuilder tableRow = new TableRowBuilder();

        tableRow.width          = android.widget.TableRow.LayoutParams.MATCH_PARENT;
        tableRow.height         = android.widget.TableRow.LayoutParams.WRAP_CONTENT;
        tableRow.padding.left   = R.dimen.widget_table_row_padding_horz;
        tableRow.padding.right  = R.dimen.widget_table_row_padding_horz;
        tableRow.padding.top    = R.dimen.widget_table_row_padding_vert;
        tableRow.padding.bottom = R.dimen.widget_table_row_padding_vert;

        android.widget.TableRow tableRowView = tableRow.tableRow(context);

        for (int i = 0; i < row.width(); i++)
        {
            CellUnion cell = row.cellAtIndex(i);
            View cellView = cell.view(this.columnAtIndex(i));
            tableRowView.addView(cellView);
        }

        return tableRowView;
    }


    private LinearLayout tileLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation         = LinearLayout.VERTICAL;
        layout.width               = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height              = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.margin.left         = R.dimen.widget_layout_margins_horz;
        layout.margin.right        = R.dimen.widget_layout_margins_horz;
        layout.backgroundResource  = R.drawable.bg_widget;

        return layout.linearLayout(context);
    }


    private TableLayout tileTableLayout(Context context)
    {
        TableLayoutBuilder layout = new TableLayoutBuilder();

        layout.layoutType          = LayoutType.LINEAR;
        layout.width               = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height              = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.shrinkAllColumns    = true;

        TableLayout tableLayout = layout.tableLayout(context);

        tableLayout.setDividerDrawable(
                ContextCompat.getDrawable(context, R.drawable.table_row_divider));
        tableLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

        return tableLayout;
    }


    private android.widget.TableRow headerTableRow(Context context)
    {

        TableRowBuilder headerRow = new TableRowBuilder();

        headerRow.width          = android.widget.TableRow.LayoutParams.MATCH_PARENT;
        headerRow.height         = android.widget.TableRow.LayoutParams.WRAP_CONTENT;
        headerRow.padding.top    = R.dimen.widget_table_header_padding_vert;
        headerRow.padding.bottom = R.dimen.widget_table_header_padding_vert;
        headerRow.padding.left   = R.dimen.widget_table_row_padding_horz;
        headerRow.padding.right  = R.dimen.widget_table_row_padding_horz;

        android.widget.TableRow headerRowView = headerRow.tableRow(context);


        for (int i = 0; i < this.width(); i++)
        {
            CellUnion headerCell = this.headerRow.cellAtIndex(i);

            Column column = this.columnAtIndex(i).getColumn();
            TextColumn textColumn = new TextColumn(null, null, null,
                                                   column.getAlignment(),
                                                   column.getWidth());
            ColumnUnion columnUnion = ColumnUnion.asText(null, textColumn);

            TextView headerCellView = (TextView) headerCell.view(columnUnion);

            float headerTextSize = (int) context.getResources()
                                                .getDimension(R.dimen.widget_table_header_text_size);
            headerCellView.setTextSize(headerTextSize);
            headerCellView.setTextColor(ContextCompat.getColor(context, R.color.light_grey_5));
            headerCellView.setTypeface(Util.sansSerifFontBold(context));

            headerRowView.addView(headerCellView);
        }

        return headerRowView;
    }



}
