
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.sheet.widget.table.Cell;
import com.kispoko.tome.sheet.widget.table.Row;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.sheet.widget.util.WidgetFormat;
import com.kispoko.tome.sheet.widget.util.WidgetUI;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



/**
 * Widget: Table
 */
public class TableWidget extends Widget implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private ModelValue<WidgetData>   widgetData;
    private PrimitiveValue<Integer>  width;
    private PrimitiveValue<Integer>  height;
    private PrimitiveValue<String[]> columnNames;
    private ModelValue<Row>          rowTemplate;
    private CollectionValue<Row>     rows;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TableWidget(UUID id,
                       WidgetData widgetData,
                       Integer width, Integer height,
                       String[] columnNames,
                       Row rowTemplate,
                       List<Row> rows)
    {
        this.id = id;

        this.widgetData = new ModelValue<>(widgetData, this, WidgetData.class);
        this.width       = new PrimitiveValue<>(width, this, Integer.class);
        this.height      = new PrimitiveValue<>(height, this, Integer.class);
        this.columnNames = new PrimitiveValue<>(columnNames, this, String[].class);
        this.rowTemplate = new ModelValue<>(rowTemplate, this, Row.class);

        List<Class<? extends Row>> rowClassList = new ArrayList<>();
        rowClassList.add(Row.class);
        this.rows        = new CollectionValue<>(rows, this, rowClassList);
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

        WidgetData   widgetData     = WidgetData.fromYaml(yaml.atKey("data"));

        Integer      width          = yaml.atKey("width").getInteger();
        Integer      height         = yaml.atKey("height").getInteger();

        List<String> columnNameList = yaml.atKey("column_names").getStringList();
        String[]     columnNames    = columnNameList.toArray(new String[0]);

        final Row    rowTemplate    = Row.fromYaml(yaml.atKey("row_template"), 0, null);

        List<Row>    rows           = yaml.atKey("rows").forEach(new Yaml.ForEach<Row>() {
            @Override
            public Row forEach(Yaml yaml, int index) throws YamlException {
                return Row.fromYaml(yaml, index, rowTemplate);
            }
        });

        return new TableWidget(id, widgetData, width, height, columnNames, rowTemplate, rows);
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

    public void onModelUpdate(String valueName) { }


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


    public void runAction(String actionName, Context context, Rules rules) { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Width
    // ------------------------------------------------------------------------------------------

    /**
     * Get the table width (number of columns).
     * @return The table width.
     */
    public Integer getWidth() {
        return this.width.getValue();
    }

    // ** Height
    // ------------------------------------------------------------------------------------------

    /**
     * Get the table height (number of rows, not counting header).
     * @return The table height.
     */
    public Integer getHeight() {
        return this.height.getValue();
    }


    // ** Column Names
    // ------------------------------------------------------------------------------------------

    /**
     * Get the names of the table columns.
     * @return Array of table column names.
     */
    public String[] getColumnNames() {
        return this.columnNames.getValue();
    }


    // ** Row Template
    // ------------------------------------------------------------------------------------------

    /**
     * Get the row template for the table. The row template is a row that is used as a blueprint
     * for all new table rows, for example to fill in default values.
     * @return The table's row template.
     */
    public Row getRowTemplate() {
        return this.rowTemplate.getValue();
    }


    // ** Rows
    // ------------------------------------------------------------------------------------------

    /**
     * Get a specific row in the table (header does not count).
     * @param index The row index (starting at 0).
     * @return The table row at the specified index.
     */
    public Row getRow(Integer index) {
        return this.rows.getValue().get(index);
    }


    /**
     * Get all of the table rows.
     * @return The table row list (excluding header).
     */
    public List<Row> getRows() {
        return this.rows.getValue();
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    /**
     * Create the tableWidget view.
     * @param context
     * @return
     */
    public View getDisplayView(Context context, Rules rules)
    {
        LinearLayout layout = WidgetUI.linearLayout(this, context, rules);

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

        for (Row row : this.rows.getValue())
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


    // INTERNAL
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


        for (int i = 0; i < getColumnNames().length; i++)
        {
            TextView headerText = new TextView(context);

            TableRow.LayoutParams layoutParams =
                    new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                              TableRow.LayoutParams.WRAP_CONTENT);

            Cell template = this.getRowTemplate().cellAtIndex(i);
            WidgetFormat.Alignment alignment = template.getWidget().data()
                    .getFormat().getAlignment();
            if (alignment != null) {
                switch (alignment) {
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

            Integer templateWidth = template.getWidget().data().getFormat().getWidth();
            if (templateWidth != null) {
                layoutParams.width = 0;
                layoutParams.weight = 1;
            }

            headerText.setLayoutParams(layoutParams);

            float headerTextSize = (int) context.getResources()
                                                .getDimension(R.dimen.comp_table_header_text_size);
            headerText.setTextSize(headerTextSize);

            headerText.setTextColor(ContextCompat.getColor(context, R.color.text_light));

            headerText.setTypeface(Util.sansSerifFontRegular(context));

            headerText.setText(getColumnNames()[i].toUpperCase());

            headerRow.addView(headerText);
        }

        return headerRow;
    }

}
