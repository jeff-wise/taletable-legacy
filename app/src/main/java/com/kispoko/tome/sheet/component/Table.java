
package com.kispoko.tome.sheet.component;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;



/**
 * Table Component
 */
public class Table extends Component implements Serializable
{
    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String name;

    private ArrayList<String> columnNames;
    private ArrayList<Row> rows;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Table(String name, String typeName, ArrayList<String> columnNames, ArrayList<Row> rows)
    {
        super(name, typeName);
        this.columnNames = columnNames;
        this.rows = rows;
    }


    @SuppressWarnings("unchecked")
    public static Table fromYaml(Map<String, Object> tableYaml)
    {
        String name = (String) tableYaml.get("name");

        ArrayList<String> columnNames = (ArrayList<String>) tableYaml.get("columns");

        Map<String,Object> dataYaml = (Map<String,Object>) tableYaml.get("data");

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

            rows.add(new Row(cells));
        }

        return new Table(name, null, columnNames, rows);
    }


    // > API
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
        private ArrayList<Cell> cells;

        public Row(ArrayList<Cell> cells)
        {
            this.cells = cells;
        }

        public ArrayList<Cell> getCells()
        {
            return this.cells;
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
