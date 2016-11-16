
package com.kispoko.tome.sheet.widget.table;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.Global;
import com.kispoko.tome.R;
import com.kispoko.tome.sheet.widget.WidgetData;
import com.kispoko.tome.sheet.widget.BooleanWidget;
import com.kispoko.tome.sheet.widget.NumberWidget;
import com.kispoko.tome.sheet.widget.TableWidget;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.util.TrackerId;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



/**
 * A TableWidget Cell.
 */
public class Cell implements Serializable
{

    // > PROPERTIES
    // --------------------------------------------------------------------------------------

    // Instance Properties
    private UUID id;
    private Integer rowIndex;
    private Integer columnIndex;
    private UUID tableId;
    private boolean isTemplate;
    private WidgetData widgetData;

    // STATIC
    private static Map<UUID,AsyncTracker> asyncTrackerMap = new HashMap<>();


    // > CONSTRUCTORS
    // --------------------------------------------------------------------------------------


    public Cell(UUID id, WidgetData widgetData, UUID tableId, boolean isTemplate,
                Integer rowIndex, Integer columnIndex, Cell template)
    {
        this.id = id;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.tableId = tableId;
        this.isTemplate = isTemplate;
        this.widgetData = widgetData;

        this.initializeFromTemplate(template);
    }


    @SuppressWarnings("unchecked")
    public static Cell fromYaml(Map<String,Object> cellYaml, UUID tableId, boolean isTemplate,
                                Integer rowIndex, Integer columnIndex, Cell template)
    {
        Map<String,Object> componentYaml = (Map<String,Object>) cellYaml.get("widgetData");
        return new Cell(WidgetData.fromYaml(null, componentYaml),
                        tableId, isTemplate, rowIndex, columnIndex, template);
    }


    // > API
    // --------------------------------------------------------------------------------------

    // >> Async Tracker
    // ------------------------------------------------------------------------------------------

    private TrackerId addAsyncTracker(TrackerId trackerId)
    {
        UUID trackerCode = UUID.randomUUID();
        Cell.asyncTrackerMap.put(trackerCode, new AsyncTracker(this, trackerId));
        return new TrackerId(trackerCode, TrackerId.Target.CELL);
    }


    public static AsyncTracker getAsyncTracker(UUID trackerCode)
    {
        return Cell.asyncTrackerMap.get(trackerCode);
    }


    // >> State
    // ------------------------------------------------------------------------------------------

    public WidgetData getWidgetData() {
        return this.widgetData;
    }



    // >> Database
    // ------------------------------------------------------------------------------------------

    /**
     * Save this cell to the database.
     */
    public void save(final TrackerId tableTrackerId)
    {
        final Cell thisCell = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // > Update ComponentTableCell
                ContentValues row = new ContentValues();

                row.put("table_id", thisCell.tableId.toString());
                row.put("row_index", thisCell.rowIndex);
                row.put("column_index", thisCell.columnIndex);
                row.put("component_id", thisCell.widgetData.getName().toString());

                int templateIdInt = thisCell.isTemplate ? 1 : 0;
                row.put("is_template", templateIdInt);

                database.insertWithOnConflict(SheetContract.ComponentTableCell.TABLE_NAME,
                                              null,
                                              row,
                                              SQLiteDatabase.CONFLICT_REPLACE);

//                database.insertOrThrow(SheetContract.ComponentTableCell.TABLE_NAME,
//                                          null,
//                                          row);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                TrackerId cellTrackerId = thisCell.addAsyncTracker(tableTrackerId);
                thisCell.widgetData.save(cellTrackerId);
            }

        }.execute();
    }


    /**
     * Load
     * @param tableTrackerId TableWidget tracker ID.
     */
    public void load(TrackerId tableTrackerId)
    {
        TrackerId cellTrackerId = this.addAsyncTracker(tableTrackerId);
        this.widgetData.load(cellTrackerId);
    }


    // >> View
    // ------------------------------------------------------------------------------------------


    public View getView(Context context)
    {
        View view = new TextView(context);

        if (this.widgetData instanceof TextWidget || this.widgetData instanceof NumberWidget) {
            view = this.textView(context);
        } else if (this.widgetData instanceof BooleanWidget) {
            view = this.boolView(context);
        }

        TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) view.getLayoutParams();

        // Configure alignment
        if (this.widgetData.getAlignment() != null) {
            switch (this.widgetData.getAlignment()) {
                case LEFT:
                    layoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                    break;
                case CENTER:
                    Log.d("***CELL", "setting center alignment");
                    layoutParams.gravity = Gravity.CENTER | Gravity.CENTER_VERTICAL;
                    break;
                case RIGHT:
                    layoutParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                    break;
            }
        }

        //view.setBackgroundColor(ContextCompat.getColor(context, R.color.amber_a200));

        // Configure column width
        if (this.widgetData.getWidth() != null) {
            layoutParams.width = 0;
            layoutParams.weight = 1;
        }

        return view;
    }


    private View textView(Context context)
    {
        TextView view = new TextView(context);

        TableRow.LayoutParams layoutParams =
                new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                          TableRow.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 0);
        view.setLayoutParams(layoutParams);

        if (this.widgetData.getAlignment() != null) {
            switch (this.widgetData.getAlignment()) {
                case LEFT:
                    view.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    break;
                case CENTER:
                    view.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
                    break;
                case RIGHT:
                    view.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                    break;
            }
        }

        view.setPadding(0, 0, 0, 0);

        view.setText(this.widgetData.getTextValue());

        view.setTextColor(ContextCompat.getColor(context, R.color.text_medium_light));
        view.setTypeface(Util.serifFontBold(context));

        float textSize = Util.getDim(context, R.dimen.comp_table_cell_text_size);
        view.setTextSize(textSize);

        return view;
    }


    private View boolView(final Context context)
    {
        final ImageView view = new ImageView(context);

        final BooleanWidget booleanWidget = (BooleanWidget) this.widgetData;

        if (booleanWidget.getValue() != null) {
            if (booleanWidget.getValue()) {
                view.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_boolean_true));
            } else {
                view.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_boolean_false));
            }
        }

        TableRow.LayoutParams layoutParams =
                new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                          TableRow.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (booleanWidget.getValue()) {
                    booleanWidget.setValue(false, null);
                    view.setImageDrawable(
                            ContextCompat.getDrawable(context, R.drawable.ic_boolean_false));
                } else {
                    booleanWidget.setValue(true, null);
                    view.setImageDrawable(
                            ContextCompat.getDrawable(context, R.drawable.ic_boolean_true));
                }
            }
        });

        return view;
    }



    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initializeFromTemplate(Cell template)
    {
        if (template == null) return;

        if (this.widgetData.getLabel() == null)
            this.widgetData.setLabel(template.widgetData.getLabel());

        if (this.widgetData.getShowLabel() == null)
            this.widgetData.setShowLabel(template.widgetData.getShowLabel());

        if (this.widgetData.getRow() == null)
            this.widgetData.setRow(template.widgetData.getRow());

        if (this.widgetData.getColumn() == null)
            this.widgetData.setColumn(template.widgetData.getColumn());

        if (this.widgetData.getWidth() == null)
            this.widgetData.setWidth(template.widgetData.getWidth());

        if (this.widgetData.getAlignment() == null) {
            this.widgetData.setAlignment(template.widgetData.getAlignment());
        }

        // WidgetData specific initialization
        if (this.widgetData instanceof BooleanWidget)
        {
            BooleanWidget booleanWidgetComponent = (BooleanWidget) this.widgetData;
            if (booleanWidgetComponent.getValue() == null) {
                booleanWidgetComponent.setValue(((BooleanWidget) template.getWidgetData()).getValue());
            }
        }

    }


    // NESTED CLASSES
    // ------------------------------------------------------------------------------------------

    /**
     * Track the state of a Group object. When the state reaches a desired configuration,
     * execute a callback.
     */
    public static class AsyncTracker
    {

        // > PROPERTIES
        // --------------------------------------------------------------------------------------

        TrackerId tableTrackerId;
        Cell cell;


        // > CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public AsyncTracker(Cell cell, TrackerId tableTrackerId)
        {
            this.cell = cell;
            this.tableTrackerId = tableTrackerId;
        }


        // > API
        // --------------------------------------------------------------------------------------

        synchronized public void markComponent()
        {
            if (this.cell.isTemplate) {
                TableWidget.getAsyncTracker(this.tableTrackerId.getCode())
                     .markTemplateCell(this.cell.columnIndex);
            } else {
                TableWidget.getAsyncTracker(this.tableTrackerId.getCode())
                        .markCell(this.cell.rowIndex, this.cell.columnIndex);
            }
        }

    }

}

