
package com.kispoko.tome.sheet.component;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.Util;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import static android.R.attr.name;
import static android.R.attr.textSize;
import static android.R.attr.value;


/**
 * Table Component
 */
public class Table extends Component implements Serializable
{
    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ArrayList<String> columnNames;
    private ArrayList<Row> rows;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Table(Id id, Type.Id typeId, ArrayList<String> columnNames, ArrayList<Row> rows)
    {
        super(id, typeId, null);

        this.columnNames = columnNames;
        this.rows = rows;
    }


    @SuppressWarnings("unchecked")
    public static Table fromYaml(Map<String, Object> tableYaml)
    {
        ArrayList<String> columnNames = (ArrayList<String>) tableYaml.get("columns");

        Map<String,Object> dataYaml = (Map<String,Object>) tableYaml.get("data");

        // >> Type
        Map<String, Object> typeYaml = (Map<String, Object>) dataYaml.get("type");

        Type.Id typeId = null;
        if (typeYaml != null) {
            String typeKind = (String) dataYaml.get("kind");
            String _typeId = (String) dataYaml.get("id");

            if (typeKind != null && _typeId != null) {
                typeId = new Type.Id(typeKind, _typeId);
            }
        }

        ArrayList<Map<String,Object>> rowsYaml =
                (ArrayList<Map<String,Object>>) dataYaml.get("rows");

        ArrayList<Row> rows = new ArrayList<>();
        for (Map<String,Object> rowYaml : rowsYaml)
        {
            ArrayList<Map<String,Object>> cellsYaml =
                    (ArrayList<Map<String,Object>>) rowYaml.get("cells");

            ArrayList<Cell> cells = new ArrayList<>();
            for (Map<String,Object> cellYaml : cellsYaml)
            {
                Map<String,Object> cellDataYaml = (Map<String,Object>) cellYaml.get("data");
                String value = (String) cellDataYaml.get("value");
                cells.add(new Cell(value));
            }

            rows.add(new Row(null, cells));
        }

        return new Table(null, typeId, columnNames, rows);
    }



    // > API
    // ------------------------------------------------------------------------------------------

    // >> Getters/Setters
    // ------------------------------------------------------------------------------------------

    public String componentName()
    {
        return "table";
    }


    // >> Database
    // ------------------------------------------------------------------------------------------

    /**
     * Load a Group from the database.
     * @param database The sqlite database object.
     * @param groupConstructorId The id of the async page constructor.
     * @param componentId The database id of the group to load.
     */
    public static void load(final SQLiteDatabase database,
                            final Integer groupConstructorId,
                            final Integer componentId)
    {
        new AsyncTask<Void,Void,Void>()
        {

            protected Void doInBackground(Void... args)
            {
                // Query Table
                String tableQuery =
                    "SELECT comp.component_id, comp.type_kind, comp.type_id, table.table_id, " +
                           "table.column1_name, table.column2_name, table.column3_name " +
                          " table.column4_name, table.column5_name, table.column6_name " +
                    "FROM Component comp " +
                    "INNER JOIN ComponentTable table on ComponentTable.component_id = Component.component_id " +
                    "WHERE Component.component_id =  " + Integer.toString(componentId);

                Cursor tableCursor = database.rawQuery(tableQuery, null);

                Long componentId;
                String typeKind;
                String typeId;
                Long tableId;
                ArrayList<String> columnNames = new ArrayList<String>();

                try {
                    tableCursor.moveToFirst();

                    componentId = tableCursor.getLong(0);
                    typeKind    = tableCursor.getString(1);
                    typeId      = tableCursor.getString(2);
                    tableId     = tableCursor.getLong(3);

                    String column1Name = tableCursor.getString(4);
                    String column2Name = tableCursor.getString(5);
                    String column3Name = tableCursor.getString(6);
                    String column4Name = tableCursor.getString(7);
                    String column5Name = tableCursor.getString(8);
                    String column6Name = tableCursor.getString(9);

                    if (column1Name != null)  columnNames.add(column1Name);
                    if (column2Name != null)  columnNames.add(column2Name);
                    if (column3Name != null)  columnNames.add(column3Name);
                    if (column4Name != null)  columnNames.add(column4Name);
                    if (column5Name != null)  columnNames.add(column5Name);
                    if (column6Name != null)  columnNames.add(column6Name);
                }
                finally {
                    tableCursor.close();
                }

                // Query Table Rows
                String tableRowQuery =
                    "SELECT row.table_row_id, row.column1, row.column2, row.column3, " +
                           "table.column4, table.column5, table.column6 " +
                    "FROM ComponentTableRow row " +
                    "WHERE ComponentTableRow.table_id =  " + Long.toString(componentId) + " " +
                    "ORDER BY row.index ASC ";

                Cursor tableRowCursor = database.rawQuery(tableRowQuery, null);

                ArrayList<Row> rows = new ArrayList<>();
                try
                {
                    while (tableRowCursor.moveToNext())
                    {

                        Long rowId     = tableCursor.getLong(0);
                        String column1 = tableCursor.getString(1);
                        String column2 = tableCursor.getString(2);
                        String column3 = tableCursor.getString(3);
                        String column4 = tableCursor.getString(4);
                        String column5 = tableCursor.getString(5);
                        String column6 = tableCursor.getString(6);

                        Row row = new Row(rowId, new ArrayList<Cell>());

                        if (column1 != null) row.addCell(new Cell(column1));

                        if (column2 != null) row.addCell(new Cell(column2));

                        if (column3 != null) row.addCell(new Cell(column3));

                        if (column4 != null) row.addCell(new Cell(column4));

                        if (column5 != null) row.addCell(new Cell(column5));

                        if (column6 != null) row.addCell(new Cell(column6));

                        rows.add(row);
                    }
                }
                // TODO log
                finally {
                    tableCursor.close();
                }

                Table table = new Table(new Id(componentId, tableId),
                                        new Type.Id(typeKind, typeId),
                                        columnNames,
                                        rows);

                Group.asyncConstructorMap.get(groupConstructorId).addComponent(table);

                return null;
            }

        }.execute();
    }


    /**
     * Save to the database.
     * @param database The SQLite database object.
     * @param groupId The ID of the parent group object.
     */
    public void save(final SQLiteDatabase database, final Long groupId)
    {
        final Table thisTable = this;

        new AsyncTask<Void,Void,Void>()
        {
            protected Void doInBackground(Void... args)
            {
                ContentValues componentRow = new ContentValues();

                if (thisTable.getId() != null)
                    componentRow.put("component_id", thisTable.getId().getId());
                else
                    componentRow.putNull("component_id");
                componentRow.put("group_id", groupId);
                componentRow.put("data_type", thisTable.componentName());
                componentRow.put("label", thisTable.getLabel());
                componentRow.put("type_kind", thisTable.getType().getId().getKind());
                componentRow.put("type_id", thisTable.getType().getId().getId());

                Long componentId = database.insertWithOnConflict(
                                                SheetContract.Component.TABLE_NAME,
                                                null,
                                                componentRow,
                                                SQLiteDatabase.CONFLICT_REPLACE);

                ContentValues tableComponentRow = new ContentValues();
                if (thisTable.getId() != null)
                    tableComponentRow.put("table_id", thisTable.getId().getSubId());
                else
                    tableComponentRow.putNull("table_id");
                tableComponentRow.put("component_id", componentId);

                ArrayList<String> columnNames = thisTable.columnNames;

                if (columnNames.size() > 0)
                    tableComponentRow.put("column1_name", columnNames.get(0));
                else
                    tableComponentRow.putNull("column1_name");

                if (columnNames.size() > 1)
                    tableComponentRow.put("column2_name", columnNames.get(1));
                else
                    tableComponentRow.putNull("column2_name");

                if (columnNames.size() > 2)
                    tableComponentRow.put("column3_name", columnNames.get(2));
                else
                    tableComponentRow.putNull("column3_name");

                if (columnNames.size() > 3)
                    tableComponentRow.put("column4_name", columnNames.get(3));
                else
                    tableComponentRow.putNull("column4_name");

                if (columnNames.size() > 4)
                    tableComponentRow.put("column5_name", columnNames.get(4));
                else
                    tableComponentRow.putNull("column5_name");

                if (columnNames.size() > 5)
                    tableComponentRow.put("column6_name", columnNames.get(5));
                else
                    tableComponentRow.putNull("column6_name");

                Long tableId = database.insertWithOnConflict(
                                                SheetContract.ComponentTable.TABLE_NAME,
                                                null,
                                                tableComponentRow,
                                                SQLiteDatabase.CONFLICT_REPLACE);

                // Set ID in case of first insert and ID was Null
                thisTable.setId(new Id(componentId, tableId));


                int index = 0;
                for (Row row : thisTable.rows)
                {
                    ContentValues tableRowRow = new ContentValues();

                    if (row.getId() != null)
                        tableRowRow.put("table_row_id", row.getId());
                    else
                        tableRowRow.putNull("table_row_id");
                    componentRow.put("table_id", tableId);
                    componentRow.put("index", index);

                    ArrayList<Cell> cells = row.getCells();

                    if (cells.size() > 0)
                        tableRowRow.put("column1", cells.get(0).getValue());
                    else
                        tableRowRow.putNull("column1");

                    if (cells.size() > 1)
                        tableRowRow.put("column2", cells.get(1).getValue());
                    else
                        tableRowRow.putNull("column2");

                    if (cells.size() > 2)
                        tableRowRow.put("column3", cells.get(2).getValue());
                    else
                        tableRowRow.putNull("column3");

                    if (cells.size() > 3)
                        tableRowRow.put("column4", cells.get(3).getValue());
                    else
                        tableRowRow.putNull("column4");

                    if (cells.size() > 4)
                        tableRowRow.put("column5", cells.get(4).getValue());
                    else
                        tableRowRow.putNull("column5");

                    if (cells.size() > 5)
                        tableRowRow.put("column6", cells.get(5).getValue());
                    else
                        tableRowRow.putNull("column6");

                    Long tableRowId = database.insertWithOnConflict(
                                                    SheetContract.ComponentTableRow.TABLE_NAME,
                                                    null,
                                                    tableRowRow,
                                                    SQLiteDatabase.CONFLICT_REPLACE);
                    row.setId(tableRowId);

                    index += 1;
                }

                return null;
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
    public View getDisplayView(Context context)
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
                TextView cellView = this.textCell(context, cell.getValue());
                tableRow.addView(cellView);
            }

            tableLayout.addView(tableRow);
        }

        return tableLayout;
    }


    public View getEditorView(Context context)
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

        //editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        //editText.setMaxWidth(300);

//        TableLayout.LayoutParams cellLayoutParams =
//                new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        //cellLayoutParams.weight = 1;
        //cellLayoutParams.gravity = Gravity.CENTER;
        //editText.setLayoutParams(cellLayoutParams);


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

//            int padding = (int) context.getResources().getDimension(R.dimen.label_padding);
//            headerText.setPadding(padding, 0, 0, 0);

            headerText.setText(columnName.toUpperCase());

            headerRow.addView(headerText);
        }

        return headerRow;
    }


    // > NESTED CLASSES
    // ------------------------------------------------------------------------------------------


    public static class Row implements Serializable
    {
        private Long id;
        private ArrayList<Cell> cells;

        public Row(Long id, ArrayList<Cell> cells)
        {
            this.id = id;
            this.cells = cells;
        }

        public ArrayList<Cell> getCells()
        {
            return this.cells;
        }

        public void addCell(Cell cell)
        {
            this.cells.add(cell);
        }

        public Long getId()
        {
            return this.id;
        }

        public void setId(Long id)
        {
            this.id = id;
        }

    }



    public static class Cell implements Serializable
    {
        private String value;

        public Cell(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return this.value;
        }
    }


}
