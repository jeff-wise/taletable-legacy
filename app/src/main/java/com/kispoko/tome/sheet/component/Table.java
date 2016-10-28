
package com.kispoko.tome.sheet.component;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.Global;
import com.kispoko.tome.R;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.sheet.component.table.Cell;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.TrackerId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Table Component
 */
public class Table extends Component implements Serializable
{
    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Integer width;
    private Integer height;
    private String[] columnNames;
    private Row rowTemplate;
    private Row[] rows;

    // STATIC
    private static Map<UUID,AsyncTracker> asyncTrackerMap = new HashMap<>();


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Table(UUID id, UUID groupId)
    {
        super(id, groupId, null, null, null);

        this.width = null;
        this.height = null;
        this.columnNames = null;
        this.rowTemplate = null;
        this.rows = null;
    }

    public Table(UUID id, UUID groupId, Type.Id typeId, Format format, List<String> actions,
                 Integer width, Integer height, String[] columnNames,
                 Row rowTemplate, Row[] rows)
    {
        super(id, groupId, typeId, format, actions);

        this.width = width;
        this.height = height;
        this.columnNames = columnNames;
        this.rowTemplate = rowTemplate;
        this.rows = rows;
    }


    @SuppressWarnings("unchecked")
    public static Table fromYaml(UUID groupId, Map<String, Object> tableYaml)
    {
        // Values to parse
        UUID id = UUID.randomUUID();
        Type.Id typeId = null;
        Format format;
        List<String> actions = null;
        Integer width = null;
        Integer height = null;
        Row rowTemplate;
        String[] columnNames = null;
        Row[] rows;

        // Parse Values
        Map<String,Object> formatYaml = (Map<String,Object>) tableYaml.get("format");
        Map<String,Object> dataYaml   = (Map<String,Object>) tableYaml.get("data");

        // >> Type Id
        if (dataYaml.containsKey("type"))
        {
            Map<String, Object> typeYaml = (Map<String,Object>) dataYaml.get("type");
            String _typeId = null;
            String typeKind = null;

            if (typeYaml.containsKey("id"))
                _typeId = (String) typeYaml.get("id");

            if (typeYaml.containsKey("kind"))
                typeKind = (String) typeYaml.get("kind");

            typeId = new Type.Id(typeKind, _typeId);
        }

        // >> Format
        format = Component.parseFormatYaml(tableYaml);

        // >> Actions
        if (tableYaml.containsKey("actions"))
            actions = (List<String>) tableYaml.get("actions");

        // >> Width
        if (tableYaml.containsKey("width"))
            width = (Integer) tableYaml.get("width");

        // >> Height
        if (tableYaml.containsKey("height"))
            height = (Integer) tableYaml.get("height");

        // >> Row Template
        Map<String,Object> rowTemplateYaml = (Map<String,Object>) tableYaml.get("row_template");
        List<Map<String,Object>> rowTemplateCellsYaml =
                                (List<Map<String,Object>>) rowTemplateYaml.get("cells");
        rowTemplate = new Row(width);
        int templateColumnIndex = 0;
        for (Map<String,Object> cellYaml : rowTemplateCellsYaml)
        {
            rowTemplate.insertCell(templateColumnIndex,
                                   Cell.fromYaml(cellYaml, id, true, null, templateColumnIndex));
            templateColumnIndex += 1;
        }

        // >> Column Names
        if (tableYaml.containsKey("columns"))
            columnNames = ((ArrayList<String>) tableYaml.get("columns")).toArray(new String[width]);

        // >> Rows
        ArrayList<Map<String,Object>> rowsYaml =
                (ArrayList<Map<String,Object>>) dataYaml.get("rows");

        rows = new Row[height];
        int rowIndex = 0;
        for (Map<String,Object> rowYaml : rowsYaml)
        {
            ArrayList<Map<String,Object>> cellsYaml =
                    (ArrayList<Map<String,Object>>) rowYaml.get("cells");

            Row row = new Row(width);
            int columnIndex = 0;
            for (Map<String,Object> cellYaml : cellsYaml)
            {
                row.insertCell(columnIndex, Cell.fromYaml(cellYaml, id, false, rowIndex, columnIndex));
                columnIndex += 1;
            }

            rows[rowIndex] = row;

            rowIndex += 1;
        }

        return new Table(id, groupId, typeId, format, actions, width, height,
                         columnNames, rowTemplate, rows);
    }



    // > API
    // ------------------------------------------------------------------------------------------

    // >> State
    // ------------------------------------------------------------------------------------------

    public String componentName()
    {
        return "table";
    }


    public void runAction(String actionName, Context context, Rules rules)
    {

    }


    // >>> Table Width
    // ------------------------------------------------------------------------------------------

    public Integer getTableWidth() {
        return this.width;
    }


    public void setTableWidth(Integer width) {
        this.width = width;
    }


    // >>> Table Height
    // ------------------------------------------------------------------------------------------

    public Integer getTableHeight() {
        return this.height;
    }


    public void setTableHeight(Integer height) {
        this.height = height;
    }


    // >>> Column Names
    // ------------------------------------------------------------------------------------------

    public String[] getColumnNames() {
        return this.columnNames;
    }


    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }


    // >>> Row Template
    // ------------------------------------------------------------------------------------------

    public Row getRowTemplate() {
        return this.rowTemplate;
    }


    public void setRowTemplate(Row rowTemplate) {
        this.rowTemplate = rowTemplate;
    }


    // >>> Rows
    // ------------------------------------------------------------------------------------------

    public Row getRow(Integer index) {
        if (index < this.rows.length)
            return this.rows[index];
        return null;
    }


    public Row[] getRows() {
        return this.rows;
    }


    public void setRows(Row[] rows) {
        this.rows = rows;
    }


    // >> Async Tracker
    // ------------------------------------------------------------------------------------------

    private TrackerId addAsyncTracker(Table table, TrackerId groupTrackerId)
    {
        UUID trackerCode = UUID.randomUUID();
        Table.asyncTrackerMap.put(trackerCode, new AsyncTracker(table, groupTrackerId));
        return new TrackerId(trackerCode, TrackerId.Target.TABLE);
    }


    public static AsyncTracker getAsyncTracker(UUID trackerId)
    {
        return Table.asyncTrackerMap.get(trackerId);
    }


    // >> Database
    // ------------------------------------------------------------------------------------------

    /**
     * Load a Group from the database.
     * @param groupTrackerId The tracker ID to locate the caller and deliver information asynchronously.
     */
    public void load(final TrackerId groupTrackerId)
    {
        final Table thisTable = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // Query Table
                // -----------------------------------------------------------------------------

                String tableQuery =
                    "SELECT comp.group_id, comp.row, comp.column, comp.width, comp.type_kind, " +
                           "comp.type_id, comp.actions, " +
                           "tbl.width, tbl.height, " +
                           "tbl.column1_name, tbl.column2_name, tbl.column3_name, " +
                           "tbl.column4_name, tbl.column5_name, tbl.column6_name " +
                    "FROM Component comp " +
                    "INNER JOIN component_table tbl on tbl.component_id = comp.component_id " +
                    "WHERE comp.component_id =  " + SQL.quoted(thisTable.getId().toString());

                Cursor tableCursor = database.rawQuery(tableQuery, null);

                UUID groupId = null;
                Integer rowIndex = null;
                Integer columnIndex = null;
                Integer width = null;
                String typeKind = null;
                String typeId = null;
                List<String> actions = null;
                Integer tableWidth = null;
                Integer tableHeight = null;
                String[] columnNames = null;

                try {
                    tableCursor.moveToFirst();

                    groupId     = UUID.fromString(tableCursor.getString(0));
                    rowIndex    = tableCursor.getInt(1);
                    columnIndex = tableCursor.getInt(2);
                    width       = tableCursor.getInt(3);
                    typeKind    = tableCursor.getString(4);
                    typeId      = tableCursor.getString(5);
                    actions     = new ArrayList<>(Arrays.asList(
                                        TextUtils.split(tableCursor.getString(6), ",")));
                    tableWidth  = tableCursor.getInt(7);
                    tableHeight = tableCursor.getInt(8);

                    columnNames = new String[width];
                    for (int i = 9; i < 9 + width; i++) {
                        String columnName = tableCursor.getString(i);
                        columnNames[i - 9] = columnName;

                    }
                } catch (Exception e) {
                    Log.d("***table", Log.getStackTraceString(e));
                }
                finally {
                    tableCursor.close();
                }

                // Query Row Template
                // -----------------------------------------------------------------------------

                String rowTemplateQuery =
                    "SELECT cell.column_index, cell.component_id, comp.data_type " +
                    "FROM component_table_cell cell " +
                    "INNER JOIN component comp on comp.component_id = cell.component_id " +
                    "WHERE cell.table_id = " + SQL.quoted(thisTable.getId().toString()) + " and " +
                          "cell.is_template = 1 " +
                    "ORDER BY cell.column_index";

                Cursor rowTemplateCursor = database.rawQuery(rowTemplateQuery, null);

                Row rowTemplate = new Row(width);
                try
                {
                    while (rowTemplateCursor.moveToNext())
                    {
                        int templateColumnIndex = rowTemplateCursor.getInt(0);
                        UUID templateComponentId = UUID.fromString(rowTemplateCursor.getString(1));
                        String templateComponentKind = rowTemplateCursor.getString(2);

                        Cell cell = new Cell(Component.empty(templateComponentId,
                                                             null, templateComponentKind),
                                             thisTable.getId(),
                                             true,
                                             null, templateColumnIndex);
                        rowTemplate.insertCell(templateColumnIndex, cell);
                    }
                }
                finally {
                    tableCursor.close();
                }


                // Query Table Cells
                // -----------------------------------------------------------------------------

                String tableCellsQuery =
                    "SELECT cell.row_index, cell.column_index, cell.component_id, comp.type_kind " +
                    "FROM component_table_cell cell " +
                    "INNER JOIN component comp on comp.component_id = cell.component_id " +
                    "WHERE cell.table_id =  " + SQL.quoted(thisTable.getId().toString()) + " and " +
                          "cell.is_template = 0 " +
                    "ORDER BY cell.row_index, cell.column_index";

                Cursor tableCellsCursor = database.rawQuery(tableCellsQuery, null);

                Row[] rows = new Row[tableHeight];
                try
                {
                    while (tableCellsCursor.moveToNext())
                    {
                        int tableRowIndex = tableCellsCursor.getInt(0);
                        int tableColIndex = tableCellsCursor.getInt(1);
                        UUID cellComponentId = UUID.fromString(tableCellsCursor.getString(2));
                        String componentKind = tableCellsCursor.getString(3);

                        Cell cell = new Cell(Component.empty(cellComponentId, null, componentKind),
                                             thisTable.getId(),
                                             false,
                                             tableRowIndex, tableColIndex);
                        rows[tableRowIndex].insertCell(tableColIndex, cell);
                    }
                }
                finally {
                    tableCursor.close();
                }


                // Set loaded values
                thisTable.setTypeId(new Type.Id(typeKind, typeId));
                thisTable.setLabel(null);
                thisTable.setRow(null);
                thisTable.setColumn(null);
                thisTable.setWidth(null);
                thisTable.setActions(actions);
                thisTable.setTableWidth(tableWidth);
                thisTable.setTableHeight(tableHeight);
                thisTable.setColumnNames(columnNames);
                thisTable.setRowTemplate(rowTemplate);
                thisTable.setRows(rows);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                TrackerId tableTrackerId = thisTable.addAsyncTracker(thisTable, groupTrackerId);

                // Load components in cells
                for (int rowIndex = 0; rowIndex < thisTable.height; rowIndex++) {
                    for (int colIndex = 0; colIndex < thisTable.width; colIndex++) {
                        thisTable.getRow(rowIndex).getCell(colIndex).load(tableTrackerId);
                    }
                }
            }

        }.execute();
    }


    /**
     * Save to the database.
     * @param groupTrackerId The async tracker ID of the caller.
     */
    public void save(final TrackerId groupTrackerId)
    {
        final Table thisTable = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // Update Component Table
                // -----------------------------------------------------------------------------
                ContentValues componentRow = new ContentValues();

                componentRow.put("component_id", thisTable.getId().toString());
                SQL.putOptString(componentRow, "group_id", thisTable.getGroupId());
                componentRow.put("data_type", thisTable.componentName());
                componentRow.put("label", thisTable.getLabel());
                componentRow.put("row", thisTable.getRow());
                componentRow.put("column", thisTable.getColumn());
                componentRow.put("width", thisTable.getWidth());
                componentRow.put("actions", TextUtils.join(",", thisTable.getActions()));
                componentRow.putNull("text_value");

                if (thisTable.getTypeId() != null) {
                    componentRow.put("type_kind", thisTable.getTypeId().getKind());
                    componentRow.put("type_id", thisTable.getTypeId().getId());
                } else {
                    componentRow.putNull("type_kind");
                    componentRow.putNull("type_id");
                }

                database.insertWithOnConflict(SheetContract.Component.TABLE_NAME,
                                              null,
                                              componentRow,
                                              SQLiteDatabase.CONFLICT_REPLACE);

                // Update Table Component Table
                // -----------------------------------------------------------------------------

                ContentValues tableComponentRow = new ContentValues();
                tableComponentRow.put("component_id", thisTable.getId().toString());

                String[] columnNames = thisTable.columnNames;
                for (int col = 0; col < thisTable.columnNames.length; col++)
                {
                    String dbColName = "column" + Integer.toString(col + 1) + "_name";
                    tableComponentRow.put(dbColName, columnNames[col]);
                }

                database.insertWithOnConflict(SheetContract.ComponentTable.TABLE_NAME,
                                              null,
                                              tableComponentRow,
                                              SQLiteDatabase.CONFLICT_REPLACE);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                TrackerId tableTrackerId = thisTable.addAsyncTracker(thisTable, groupTrackerId);

                // Save row template cells asynchronously
                //Log.d("***TABLE", "saving row template");
                thisTable.rowTemplate.save(tableTrackerId);

                Log.d("***TABLE", "table saved, saving cells");
                //thisTable.getRow(0).getCell(0).save(tableTrackerId);

                // Save each cell asynchronously
                for (int rowIndex = 0; rowIndex < thisTable.height; rowIndex++) {
                    for (int colIndex = 0; colIndex < thisTable.width; colIndex++) {
                        thisTable.getRow(rowIndex).getCell(colIndex).save(tableTrackerId);
                    }
                }
            }

        }.execute();
    }


    // >> Views
    // ------------------------------------------------------------------------------------------

    /**
     * Create the table view.
     * @param context
     * @return
     */
    public View getDisplayView(Context context, Rules rules)
    {
        TableLayout tableLayout = new TableLayout(context);

        TableLayout.LayoutParams tableLayoutParams =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                             TableLayout.LayoutParams.MATCH_PARENT);
        tableLayout.setLayoutParams(tableLayoutParams);

        tableLayout.setStretchAllColumns(true);
        tableLayout.setShrinkAllColumns(true);

        tableLayout.addView(this.headerRow(context));

        for (Row row : this.rows)
        {
            TableRow tableRow = this.tableRow(context);

            for (Cell cell : row.getCells())
            {
                TextView cellView = cell.getView(context);
                tableRow.addView(cellView);
            }

            tableLayout.addView(tableRow);
        }

        return tableLayout;
    }


    public View getEditorView(Context context, Rules rules)
    {
        return new LinearLayout(context);
    }



    // > INTERNAL
    // ------------------------------------------------------------------------------------------


    private TableRow tableRow(Context context)
    {
        TableRow tableRow = new TableRow(context);

        return tableRow;
    }


    private TextView textCell(Context context, String value)
    {
        TextView textView = new TextView(context);

        int cellPadding = (int) context.getResources()
                                       .getDimension(R.dimen.comp_table_cell_padding);
        textView.setPadding(0, cellPadding, 0, cellPadding);

        textView.setTextColor(ContextCompat.getColor(context, R.color.text_medium));

        float editTextSize = (int) context.getResources()
                                          .getDimension(R.dimen.comp_table_text_cell_text_size);
        textView.setTextSize(editTextSize);

        Typeface font = Typeface.createFromAsset(context.getAssets(),
                                                 "fonts/DavidLibre-Regular.ttf");
        textView.setTypeface(font);

        if (value != null)
            textView.setText(value);

        return textView;
    }


    private TableRow headerRow(Context context)
    {
        TableRow headerRow = new TableRow(context);


        for (String columnName : this.columnNames)
        {
            TextView headerText = new TextView(context);

            float headerTextSize = (int) context.getResources()
                                                .getDimension(R.dimen.comp_table_header_text_size);
            headerText.setTextSize(headerTextSize);

            headerText.setTextColor(ContextCompat.getColor(context, R.color.bluegrey_400));

            headerText.setTypeface(null, Typeface.BOLD);

            headerText.setText(columnName.toUpperCase());

            headerRow.addView(headerText);
        }

        return headerRow;
    }


    // > NESTED CLASSES
    // ------------------------------------------------------------------------------------------


    public static class Row implements Serializable
    {
        private Cell[] cells;

        public Row(Integer width)
        {
            this.cells = new Cell[width];
        }


        public void save(TrackerId tableTrackerId)
        {
            for (int i = 0; i < cells.length; i++) {
                cells[i].save(tableTrackerId);
            }
        }


        public Cell[] getCells() {
            return this.cells;
        }


        public Cell getCell(Integer index) {
            if (index < cells.length)
                return this.cells[index];
            return null;
        }


        public void insertCell(Integer columnIndex, Cell cell) {
            if (columnIndex < cells.length)
                this.cells[columnIndex] = cell;
        }
    }


    /**
     * Track the state of a Group object. When the state reaches a desired configuration,
     * execute a callback.
     */
    public static class AsyncTracker
    {

        // > PROPERTIES
        // --------------------------------------------------------------------------------------

        private Table table;
        private TrackerId groupTrackerId;

        private boolean[][] cellTracker;
        private boolean[] templateCellTracker;

        private Integer tableHeight;
        private Integer tableWidth;


        // > CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public AsyncTracker(Table table, TrackerId groupTrackerId)
        {
            this.table = table;
            this.groupTrackerId = groupTrackerId;

            // Save, for frequent access
            tableHeight = this.table.getTableHeight();
            tableWidth  = this.table.getTableWidth();

            cellTracker = new boolean[tableHeight][tableWidth];
            for (int i = 0; i < tableHeight; i++) {
                for (int j = 0; j < tableWidth; j++) {
                    cellTracker[i][j] = false;
                }
            }

            templateCellTracker = new boolean[tableWidth];
            for (int i = 0; i < tableWidth; i++) {
                this.templateCellTracker[i] = false;
            }
        }


        // > API
        // --------------------------------------------------------------------------------------

        synchronized public void markCell(int rowIndex, int columnIndex)
        {
            if (rowIndex >= tableHeight || columnIndex >= tableWidth)
                return;

            cellTracker[rowIndex][columnIndex] = true;

            if (isReady()) ready();
        }


        synchronized public void markTemplateCell(int columnIndex)
        {
            if (columnIndex >= tableWidth)  return;

            this.templateCellTracker[columnIndex] = true;

            if (isReady()) ready();
        }



        // > INTERNAL
        // --------------------------------------------------------------------------------------

        private boolean isReady()
        {
            for (int i = 0; i < tableHeight; i++) {
                for (int j = 0; j < tableWidth; j++) {
                    if (!this.cellTracker[i][j]) return false;
                }
            }

            for (int i = 0; i < tableWidth; i++) {
                if (!this.templateCellTracker[i]) return false;
            }

            return true;
        }

        private void ready()
        {
            if (groupTrackerId == null) return;

            UUID trackerCode = groupTrackerId.getCode();
            Group.getAsyncTracker(trackerCode).markComponentId(this.table.getId());
        }
    }


}
