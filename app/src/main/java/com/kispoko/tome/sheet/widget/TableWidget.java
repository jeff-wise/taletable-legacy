
package com.kispoko.tome.sheet.widget;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.Global;
import com.kispoko.tome.R;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.rules.programming.Variable;
import com.kispoko.tome.sheet.widget.table.Cell;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.database.SQL;
import com.kispoko.tome.util.Tracker;
import com.kispoko.tome.util.TrackerId;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * TableWidget WidgetData
 */
public class TableWidget extends WidgetData implements Serializable
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

    public static final int MAX_COLUMNS = 6;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TableWidget(UUID id, UUID groupId)
    {
        super(id, null, groupId, null, null, null, null);

        this.width = null;
        this.height = null;
        this.columnNames = null;
        this.rowTemplate = null;
        this.rows = null;
    }

    public TableWidget(UUID id, String name, UUID groupId, Variable value, Type.Id typeId,
                       Format format, List<String> actions, Integer width, Integer height,
                       String[] columnNames, Row rowTemplate, Row[] rows)
    {
        super(id, null, groupId, value, typeId, format, actions);

        this.width = width;
        this.height = height;
        this.columnNames = columnNames;
        this.rowTemplate = rowTemplate;
        this.rows = rows;
    }


    @SuppressWarnings("unchecked")
    public static TableWidget fromYaml(UUID groupId, Map<String, Object> tableYaml)
    {
        // VALUES TO PARSE
        // --------------------------------------------------------------------------------------
        UUID id = UUID.randomUUID();
        String name = null;
        Variable value = null;
        Type.Id typeId = null;
        Format format;
        List<String> actions = null;
        Integer width = null;
        Integer height = null;
        Row rowTemplate;
        String[] columnNames = null;
        Row[] rows;

        // PARSE VALUES
        // --------------------------------------------------------------------------------------

        // > Top Level
        // --------------------------------------------------------------------------------------
        Map<String,Object> formatYaml = (Map<String,Object>) tableYaml.get("format");
        Map<String,Object> dataYaml   = (Map<String,Object>) tableYaml.get("data");

        // >> Type Id
        typeId = Type.Id.fromYaml(dataYaml);

        // >> Format
        format = WidgetData.parseFormatYaml(tableYaml);

        // >> Name
        if (tableYaml.containsKey("name"))
            name = (String) tableYaml.get("name");

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
                                   Cell.fromYaml(cellYaml, id, true, null, templateColumnIndex, null));
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
                Cell template = rowTemplate.getCell(columnIndex);
                row.insertCell(columnIndex,
                               Cell.fromYaml(cellYaml, id, false, rowIndex, columnIndex, template));
                columnIndex += 1;
            }

            rows[rowIndex] = row;

            rowIndex += 1;
        }


        return new TableWidget(id, name, groupId, value, typeId, format, actions, width, height,
                         columnNames, rowTemplate, rows);
    }



    // > API
    // ------------------------------------------------------------------------------------------

    // >> State
    // ------------------------------------------------------------------------------------------

    public String componentName()
    {
        return "tableWidget";
    }


    public void runAction(String actionName, Context context, Rules rules)
    {

    }


    // >>> TableWidget Width
    // ------------------------------------------------------------------------------------------

    public Integer getTableWidth() {
        return this.width;
    }


    public void setTableWidth(Integer width) {
        this.width = width;
    }


    // >>> TableWidget Height
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

    private TrackerId addAsyncTracker(TableWidget tableWidget, TrackerId groupTrackerId)
    {
        UUID trackerCode = UUID.randomUUID();
        TableWidget.asyncTrackerMap.put(trackerCode, new AsyncTracker(tableWidget, groupTrackerId));
        return new TrackerId(trackerCode, TrackerId.Target.TABLE);
    }


    public static AsyncTracker getAsyncTracker(UUID trackerId)
    {
        return TableWidget.asyncTrackerMap.get(trackerId);
    }


    // >> Database
    // ------------------------------------------------------------------------------------------

    /**
     * Load a TextWidget from the database.
     * @param callerTrackerId The caller tracker id.
     */
    public void load(final UUID callerTrackerId)
    {
        final TableWidget thisTableWidget = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // ModelQuery TableWidget
                // -----------------------------------------------------------------------------

                String tableQuery =
                    "SELECT comp.name, comp.label, comp.show_label, comp.type_kind, comp.type_id, " +
                            "comp.actions, tbl.width, tbl.height, " +
                           "tbl.column1_name, tbl.column2_name, tbl.column3_name, " +
                           "tbl.column4_name, tbl.column5_name, tbl.column6_name " +
                    "FROM WidgetData comp " +
                    "INNER JOIN component_table tbl on tbl.component_id = comp.component_id " +
                    "WHERE comp.component_id =  " + SQL.quoted(thisTableWidget.getId().toString());

                Cursor tableCursor = database.rawQuery(tableQuery, null);

                String name = null;
                String label = null;
                Boolean showLabel = null;
                String typeKind = null;
                String typeId = null;
                List<String> actions = null;
                Integer tableWidth = null;
                Integer tableHeight = null;
                String[] columnNames = null;

                try {
                    tableCursor.moveToFirst();

                    name        = tableCursor.getString(0);
                    label       = tableCursor.getString(1);
                    showLabel   = SQL.intAsBool(tableCursor.getInt(2));
                    typeKind    = tableCursor.getString(3);
                    typeId      = tableCursor.getString(4);
                    actions     = new ArrayList<>(Arrays.asList(
                                                  TextUtils.split(tableCursor.getString(5), ",")));
                    tableWidth  = tableCursor.getInt(6);
                    tableHeight = tableCursor.getInt(7);

                    columnNames = new String[tableWidth];
                    for (int i = 0; i < columnNames.length; i++) {
                        String columnName = tableCursor.getString(i + 8);
                        columnNames[i] = columnName;

                    }
                } catch (Exception e) {
                    Log.d("***TABLE", Log.getStackTraceString(e));
                }
                finally {
                    tableCursor.close();
                }

                // ModelQuery Row Template
                // -----------------------------------------------------------------------------

                String rowTemplateQuery =
                    "SELECT cell.column_index, cell.component_id, comp.data_type " +
                    "FROM component_table_cell cell " +
                    "INNER JOIN component comp on comp.component_id = cell.component_id " +
                    "WHERE cell.table_id = " + SQL.quoted(thisTableWidget.getId().toString()) + " and " +
                          "cell.is_template = 1 " +
                    "ORDER BY cell.column_index";

                Cursor rowTemplateCursor = database.rawQuery(rowTemplateQuery, null);

                Row rowTemplate = new Row(tableWidth);
                try
                {
                    while (rowTemplateCursor.moveToNext())
                    {
                        int templateColumnIndex = rowTemplateCursor.getInt(0);
                        UUID templateComponentId = UUID.fromString(rowTemplateCursor.getString(1));
                        String templateComponentKind = rowTemplateCursor.getString(2);

                        Cell cell = new Cell(WidgetData.empty(templateComponentId,
                                                             null, templateComponentKind),
                                             thisTableWidget.getId(),
                                             true,
                                             null, templateColumnIndex, null);
                        rowTemplate.insertCell(templateColumnIndex, cell);
                    }
                } catch (Exception e) {
                    Log.d("***TABLE", Log.getStackTraceString(e));
                }
                finally {
                    tableCursor.close();
                }


                // ModelQuery TableWidget Cells
                // -----------------------------------------------------------------------------

                String tableCellsQuery =
                    "SELECT cell.row_index, cell.column_index, cell.component_id, comp.data_type " +
                    "FROM component_table_cell cell " +
                    "INNER JOIN component comp on comp.component_id = cell.component_id " +
                    "WHERE cell.table_id =  " + SQL.quoted(thisTableWidget.getId().toString()) + " and " +
                          "cell.is_template = 0 " +
                    "ORDER BY cell.row_index, cell.column_index";

                Cursor tableCellsCursor = database.rawQuery(tableCellsQuery, null);

                // Initialize rows
                Row[] rows = new Row[tableHeight];
                for (int i = 0; i < rows.length; i++) {
                    rows[i] = new Row(tableWidth);
                }
                try
                {
                    while (tableCellsCursor.moveToNext())
                    {
                        int tableRowIndex = tableCellsCursor.getInt(0);
                        int tableColIndex = tableCellsCursor.getInt(1);

                        UUID cellComponentId = UUID.fromString(tableCellsCursor.getString(2));
                        String componentKind = tableCellsCursor.getString(3);

                        Cell template = rowTemplate.getCell(tableColIndex);
                        Cell cell = new Cell(WidgetData.empty(cellComponentId, null, componentKind),
                                             thisTableWidget.getId(),
                                             false,
                                             tableRowIndex, tableColIndex, template);
                        rows[tableRowIndex].insertCell(tableColIndex, cell);
                    }
                } catch (Exception e) {
                    Log.d("***TABLE", Log.getStackTraceString(e));
                }
                finally {
                    tableCursor.close();
                }


                // Set loaded values
                thisTableWidget.setName(name);
                thisTableWidget.setTypeId(new Type.Id(typeKind, typeId));
                thisTableWidget.setLabel(label);
                thisTableWidget.setShowLabel(showLabel);
                thisTableWidget.setRow(1);
                thisTableWidget.setColumn(1);
                thisTableWidget.setWidth(1);
                thisTableWidget.setActions(actions);
                thisTableWidget.setTableWidth(tableWidth);
                thisTableWidget.setTableHeight(tableHeight);
                thisTableWidget.setColumnNames(columnNames);
                thisTableWidget.setRowTemplate(rowTemplate);
                thisTableWidget.setRows(rows);


                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                List<String> trackingKeys = new ArrayList<>();

                Tracker.OnReady onReady = new Tracker.OnReady() {
                    @Override
                    protected void go() {
                        Global.getTracker(callerTrackerId).setKey(thisTableWidget.getId().toString());
                    }
                };

                UUID textTrackerId = Global.addTracker(new Tracker(trackingKeys, onReady));

                thisInteger.getValue().load(thisInteger.getId(), "value", textTrackerId);
                thisInteger.getPrefix().load(thisInteger.getId(), "prefix", textTrackerId);
                t


                TrackerId tableTrackerId = thisTableWidget.addAsyncTracker(thisTableWidget, groupTrackerId);

                thisTableWidget.rowTemplate.load(tableTrackerId);

                // Load components in cells
                for (int rowIndex = 0; rowIndex < thisTableWidget.height; rowIndex++) {
                    thisTableWidget.getRow(rowIndex).load(tableTrackerId);
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
        final TableWidget thisTableWidget = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // Update WidgetData TableWidget
                // -----------------------------------------------------------------------------
                ContentValues componentRow = new ContentValues();

                componentRow.put("component_id", thisTableWidget.getId().toString());
                SQL.putOptString(componentRow, "group_id", thisTableWidget.getGroupId());
                componentRow.put("data_type", thisTableWidget.componentName());
                componentRow.put("label", thisTableWidget.getLabel());

                componentRow.put("show_label", SQL.boolAsInt(thisTableWidget.getShowLabel()));
                componentRow.put("row", thisTableWidget.getRow());
                componentRow.put("column", thisTableWidget.getColumn());
                componentRow.put("width", thisTableWidget.getWidth());
                componentRow.put("actions", TextUtils.join(",", thisTableWidget.getActions()));
                componentRow.putNull("text_value");

                if (thisTableWidget.getTypeId() != null) {
                    componentRow.put("type_kind", thisTableWidget.getTypeId().getKind());
                    componentRow.put("type_id", thisTableWidget.getTypeId().getId());
                } else {
                    componentRow.putNull("type_kind");
                    componentRow.putNull("type_id");
                }

                database.insertWithOnConflict(SheetContract.Component.TABLE_NAME,
                                              null,
                                              componentRow,
                                              SQLiteDatabase.CONFLICT_REPLACE);

                // Update TableWidget WidgetData TableWidget
                // -----------------------------------------------------------------------------

                ContentValues tableComponentRow = new ContentValues();
                tableComponentRow.put("component_id", thisTableWidget.getId().toString());
                tableComponentRow.put("width", thisTableWidget.getTableWidth());
                tableComponentRow.put("height", thisTableWidget.getTableHeight());

                String[] columnNames = thisTableWidget.columnNames;
                for (int col = 0; col < columnNames.length && col < MAX_COLUMNS; col++)
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
                TrackerId tableTrackerId = thisTableWidget.addAsyncTracker(thisTableWidget, groupTrackerId);

                // Save row template cells asynchronously
                thisTableWidget.rowTemplate.save(tableTrackerId);

                // Save each cell asynchronously
                for (int rowIndex = 0; rowIndex < thisTableWidget.height; rowIndex++) {
                    thisTableWidget.getRow(rowIndex).save(tableTrackerId);
                }
            }

        }.execute();
    }


    // >> Views
    // ------------------------------------------------------------------------------------------

    /**
     * Create the tableWidget view.
     * @param context
     * @return
     */
    public View getDisplayView(Context context, Rules rules)
    {
        LinearLayout layout = this.linearLayout(context, rules);

        layout.setPadding(0, 0, 0, 0);

        TableLayout tableLayout = new TableLayout(context);

        TableLayout.LayoutParams tableLayoutParams =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                             TableLayout.LayoutParams.MATCH_PARENT);
        tableLayout.setLayoutParams(tableLayoutParams);

        int tableLayoutPadding = (int) Util.getDim(context, R.dimen.one_dp);
        tableLayout.setPadding(tableLayoutPadding, tableLayoutPadding,
                               tableLayoutPadding, tableLayoutPadding);

        tableLayout.setDividerDrawable(ContextCompat.getDrawable(context, R.drawable.table_row_divider));
        tableLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

//        tableLayout.setStretchAllColumns(true);
        tableLayout.setShrinkAllColumns(true);

        tableLayout.addView(this.headerRow(context));

        for (Row row : this.rows)
        {
            TableRow tableRow = this.tableRow(context);

            TableRow.LayoutParams tableRowLayoutParams =
                    new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                                              TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(tableRowLayoutParams);

            int tableRowPaddingHorz = (int) Util.getDim(context, R.dimen.comp_table_row_padding_horz);
            int tableRowPaddingVert = (int) Util.getDim(context,
                                                        R.dimen.comp_table_row_padding_vert);
            tableRow.setPadding(tableRowPaddingHorz, tableRowPaddingVert,
                                tableRowPaddingHorz, tableRowPaddingVert);

            for (Cell cell : row.getCells())
            {
                View cellView = cell.getView(context);
                tableRow.addView(cellView);
            }

            tableLayout.addView(tableRow);
        }

        layout.addView(tableLayout);

        return layout;
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


    private TableRow headerRow(Context context)
    {
        TableRow headerRow = new TableRow(context);

        int paddingVert = (int) Util.getDim(context, R.dimen.comp_table_header_padding_vert);
        int paddingHorz = (int) Util.getDim(context, R.dimen.comp_table_row_padding_horz);
        headerRow.setPadding(paddingHorz, paddingVert, paddingHorz, paddingVert);


        for (int i = 0; i < columnNames.length; i++)
        {
            TextView headerText = new TextView(context);

            TableRow.LayoutParams layoutParams =
                    new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                              TableRow.LayoutParams.WRAP_CONTENT);

            Cell template = this.rowTemplate.getCell(i);
            if (template.getWidgetData().getAlignment() != null) {
                switch (template.getWidgetData().getAlignment()) {
                    case LEFT:
                        headerText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                        break;
                    case CENTER:
                        headerText.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
                        break;
                    case RIGHT:
                        headerText.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                        break;
                }
            }

            if (template.getWidgetData().getWidth() != null) {
                layoutParams.width = 0;
                layoutParams.weight = 1;
            }

            headerText.setLayoutParams(layoutParams);

            float headerTextSize = (int) context.getResources()
                                                .getDimension(R.dimen.comp_table_header_text_size);
            headerText.setTextSize(headerTextSize);

            headerText.setTextColor(ContextCompat.getColor(context, R.color.text_light));

            headerText.setTypeface(Util.sansSerifFontRegular(context));

            headerText.setText(columnNames[i].toUpperCase());

            headerRow.addView(headerText);
        }

        return headerRow;
    }


    private UUID createTracker(final UUID callerTrackerId)
    {
        final TableWidget thisTableWidget = this;

        List<String> trackingKeys = new ArrayList<>();
        for (int i = 0; i < this.getTableHeight(); i++) {
            for (int j = 0; j < this.getTableWidth(); j++) {
                trackingKeys.add(Integer.toString(i) + "-" + Integer.toString(j));
            }
        }

        for (int i = 0; i < tableWidth; i++) {
            this.templateCellTracker[i] = false;
        }

        Tracker.OnReady onReady = new Tracker.OnReady() {
            @Override
            protected void go() {
                Global.getTracker(callerTrackerId).setKey(thisTableWidget.getId().toString());
            }
        };

        UUID tableTrackerId = Global.addTracker(new Tracker(trackingKeys, onReady));

        return tableTrackerId;
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


        public void load(TrackerId tableTrackerId)
        {
            for (int i = 0; i < cells.length; i++) {
                cells[i].load(tableTrackerId);
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

        private TableWidget tableWidget;
        private TrackerId groupTrackerId;

        private boolean[][] cellTracker;
        private boolean[] templateCellTracker;

        private Integer tableHeight;
        private Integer tableWidth;


        // > CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public AsyncTracker(TableWidget tableWidget, TrackerId groupTrackerId)
        {
            this.tableWidget = tableWidget;
            this.groupTrackerId = groupTrackerId;

            // Save, for frequent access
            tableHeight = this.tableWidget.getTableHeight();
            tableWidth  = this.tableWidget.getTableWidth();

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
            Group.getAsyncTracker(trackerCode).markComponentId(this.tableWidget.getId());
        }
    }


}
