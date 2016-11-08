
package com.kispoko.tome.sheet.component.type.table;


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
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.sheet.component.Component;
import com.kispoko.tome.sheet.component.type.Bool;
import com.kispoko.tome.sheet.component.type.Number;
import com.kispoko.tome.sheet.component.type.Table;
import com.kispoko.tome.sheet.component.type.Text;
import com.kispoko.tome.util.TrackerId;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



/**
 * A Table Cell.
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
    private Component component;

    // STATIC
    private static Map<UUID,AsyncTracker> asyncTrackerMap = new HashMap<>();


    // > CONSTRUCTORS
    // --------------------------------------------------------------------------------------


    public Cell(UUID id, Component component, UUID tableId, boolean isTemplate,
                Integer rowIndex, Integer columnIndex, Cell template)
    {
        this.id = id;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.tableId = tableId;
        this.isTemplate = isTemplate;
        this.component = component;

        this.initializeFromTemplate(template);
    }


    @SuppressWarnings("unchecked")
    public static Cell fromYaml(Map<String,Object> cellYaml, UUID tableId, boolean isTemplate,
                                Integer rowIndex, Integer columnIndex, Cell template)
    {
        Map<String,Object> componentYaml = (Map<String,Object>) cellYaml.get("component");
        return new Cell(Component.fromYaml(null, componentYaml),
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

    public Component getComponent() {
        return this.component;
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
                row.put("component_id", thisCell.component.getId().toString());

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
                thisCell.component.save(cellTrackerId);
            }

        }.execute();
    }


    /**
     * Load
     * @param tableTrackerId Table tracker ID.
     */
    public void load(TrackerId tableTrackerId)
    {
        TrackerId cellTrackerId = this.addAsyncTracker(tableTrackerId);
        this.component.load(cellTrackerId);
    }


    // >> View
    // ------------------------------------------------------------------------------------------


    public View getView(Context context)
    {
        View view = new TextView(context);

        if (this.component instanceof Text || this.component instanceof Number) {
            view = this.textView(context);
        } else if (this.component instanceof Bool) {
            view = this.boolView(context);
        }

        TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) view.getLayoutParams();

        // Configure alignment
        if (this.component.getAlignment() != null) {
            switch (this.component.getAlignment()) {
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
        if (this.component.getWidth() != null) {
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

        if (this.component.getAlignment() != null) {
            switch (this.component.getAlignment()) {
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

        view.setText(this.component.getTextValue());

        view.setTextColor(ContextCompat.getColor(context, R.color.text_medium_light));
        view.setTypeface(Util.serifFontBold(context));

        float textSize = Util.getDim(context, R.dimen.comp_table_cell_text_size);
        view.setTextSize(textSize);

        return view;
    }


    private View boolView(final Context context)
    {
        final ImageView view = new ImageView(context);

        final Bool bool = (Bool) this.component;

        if (bool.getValue() != null) {
            if (bool.getValue()) {
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
                if (bool.getValue()) {
                    bool.setValue(false, null);
                    view.setImageDrawable(
                            ContextCompat.getDrawable(context, R.drawable.ic_boolean_false));
                } else {
                    bool.setValue(true, null);
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

        if (this.component.getLabel() == null)
            this.component.setLabel(template.component.getLabel());

        if (this.component.getShowLabel() == null)
            this.component.setShowLabel(template.component.getShowLabel());

        if (this.component.getRow() == null)
            this.component.setRow(template.component.getRow());

        if (this.component.getColumn() == null)
            this.component.setColumn(template.component.getColumn());

        if (this.component.getWidth() == null)
            this.component.setWidth(template.component.getWidth());

        if (this.component.getAlignment() == null) {
            this.component.setAlignment(template.component.getAlignment());
        }

        // Component specific initialization
        if (this.component instanceof Bool)
        {
            Bool boolComponent = (Bool) this.component;
            if (boolComponent.getValue() == null) {
                boolComponent.setValue(((Bool) template.getComponent()).getValue());
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
                Table.getAsyncTracker(this.tableTrackerId.getCode())
                     .markTemplateCell(this.cell.columnIndex);
            } else {
                Table.getAsyncTracker(this.tableTrackerId.getCode())
                        .markCell(this.cell.rowIndex, this.cell.columnIndex);
            }
        }

    }

}

