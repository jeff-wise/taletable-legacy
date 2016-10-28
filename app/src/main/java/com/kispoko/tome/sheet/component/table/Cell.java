
package com.kispoko.tome.sheet.component.table;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.kispoko.tome.Global;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.sheet.component.Table;
import com.kispoko.tome.util.TrackerId;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.kispoko.tome.util.TrackerId.Target.CELL;



/**
 * A Table Cell.
 */
public class Cell implements Serializable
{

    // > PROPERTIES
    // --------------------------------------------------------------------------------------

    // Instance Properties
    private Integer rowIndex;
    private Integer columnIndex;
    private UUID tableId;
    private boolean isTemplate;
    private Component component;

    // STATIC
    private static Map<UUID,AsyncTracker> asyncTrackerMap = new HashMap<>();


    // > CONSTRUCTORS
    // --------------------------------------------------------------------------------------


    public Cell(Component component, UUID tableId, boolean isTemplate,
                Integer rowIndex, Integer columnIndex)
    {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.tableId = tableId;
        this.isTemplate = isTemplate;
        this.component = component;
    }


    @SuppressWarnings("unchecked")
    public static Cell fromYaml(Map<String,Object> cellYaml, UUID tableId, boolean isTemplate,
                                Integer rowIndex, Integer columnIndex)
    {
        Map<String,Object> componentYaml = (Map<String,Object>) cellYaml.get("component");
        return new Cell(Component.fromYaml(null, componentYaml),
                        tableId, isTemplate, rowIndex, columnIndex);
    }


    // > API
    // --------------------------------------------------------------------------------------

    // >> Async Tracker
    // ------------------------------------------------------------------------------------------

    private TrackerId addAsyncTracker(TrackerId trackerId)
    {
        UUID trackerCode = UUID.randomUUID();
        Cell.asyncTrackerMap.put(trackerCode, new AsyncTracker(this, trackerId));
        return new TrackerId(trackerCode, CELL);
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


    public TextView getView(Context context)
    {
        TextView view = new TextView(context);

        view.setText(this.component.getTextValue());

        return view;
    }



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

