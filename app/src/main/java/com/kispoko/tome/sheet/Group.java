
package com.kispoko.tome.sheet;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.Global;
import com.kispoko.tome.R;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.sheet.widget.WidgetData;
import com.kispoko.tome.util.database.SQL;
import com.kispoko.tome.util.TrackerId;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



/**
 * Group
 */
public class Group implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;
    private UUID pageId;

    private String label;
    private ArrayList<WidgetData> widgetDatas;
    private Integer numberOfRows;
    private Integer index;

    // >> STATIC
    private static Map<UUID,AsyncTracker> asyncTrackerMap = new HashMap<>();


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Group(UUID id, UUID pageId)
    {
        this.id = id;
        this.pageId = pageId;
    }


    public Group(UUID id, UUID pageId, String label, Integer index, Integer numberOfRows,
                 ArrayList<WidgetData> widgetDatas)
    {
        if (id != null)
            this.id = id;
        else
            this.id = UUID.randomUUID();

        this.pageId = pageId;

        this.label = label;
        this.index = index;
        this.numberOfRows = numberOfRows;
        this.widgetDatas = widgetDatas;
    }


    @SuppressWarnings("unchecked")
    public static Group fromYaml(UUID pageId, Map<String,Object> groupYaml)
    {
        // Values to parse
        UUID id = UUID.randomUUID();
        String label = null;
        Integer numberOfRows = null;
        ArrayList<WidgetData> widgetDatas = new ArrayList<>();

        // Parse values
        // >> Label
        if (groupYaml.containsKey("label"))
            label = (String) groupYaml.get("label");

        // >> NumberWidget of Rows
        if (groupYaml.containsKey("number_of_rows"))
            numberOfRows = (Integer) groupYaml.get("number_of_rows");

        // >> Components
        ArrayList<Object> componentsYaml = (ArrayList<Object>) groupYaml.get("widgetDatas");
        for (Object componentYaml : componentsYaml) {
            WidgetData widgetData = WidgetData.fromYaml(id, (Map<String, Object>) componentYaml);
            widgetDatas.add(widgetData);
        }

        return new Group(id, pageId, label, null, numberOfRows, widgetDatas);
    }


    // >> Async Tracking
    // ------------------------------------------------------------------------------------------

    /**
     * Create a tracker for asynchronously tracking the state of a sheet.
     * @return The new tracker's ID.
     */
    private TrackerId addAsyncTracker(TrackerId pageTrackerId)
    {
        UUID trackerCode = UUID.randomUUID();
        Group.asyncTrackerMap.put(trackerCode, new AsyncTracker(this, pageTrackerId));
        return new TrackerId(trackerCode, TrackerId.Target.GROUP);
    }


    public static AsyncTracker getAsyncTracker(UUID trackerId)
    {
        return Group.asyncTrackerMap.get(trackerId);
    }


    // >> State
    // ------------------------------------------------------------------------------------------

    // >>> Components
    // ------------------------------------------------------------------------------------------

    public ArrayList<WidgetData> getWidgetDatas() {
        return this.widgetDatas;
    }


    public void setWidgetDatas(ArrayList<WidgetData> widgetDatas) {
        this.widgetDatas = widgetDatas;
    }


    // >>> Label
    // ------------------------------------------------------------------------------------------

    public UUID getPageId() {
        return this.pageId;
    }


    public void setPageId(UUID pageId) {
        this.pageId = pageId;
    }


    // >>> Label
    // ------------------------------------------------------------------------------------------

    public String getLabel() {
        return this.label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    // >>> NumberWidget of Rows
    // ------------------------------------------------------------------------------------------

    public Integer getNumberOfRows() {
        return this.numberOfRows;
    }


    public void setNumberOfRows(Integer numberOfRows) {
        this.numberOfRows = numberOfRows;
    }


    public UUID getId()
    {
        return this.id;
    }


    // >> Index
    // ------------------------------------------------------------------------------------------

    public Integer getIndex()
    {
        return this.index;
    }

    public void setIndex(Integer index)
    {
        this.index = index;
    }


    // >> Database Methods
    // ------------------------------------------------------------------------------------------

    /**
     * Load a Group from the database.
     */
    public void load(final TrackerId pageTrackerId)
    {

        final Group thisGroup = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // Query Group Data
                String groupQuery =
                    "SELECT grp.label, grp.group_index, grp.number_of_rows " +
                    "FROM _group grp " +
                    "WHERE grp.group_id =  " + SQL.quoted(thisGroup.getId().toString());

                Cursor groupCursor = database.rawQuery(groupQuery, null);

                String label = null;
                Integer index = null;
                Integer numberOfRows = null;
                try {
                    groupCursor.moveToFirst();
                    label = groupCursor.getString(0);
                    index = groupCursor.getInt(1);
                    numberOfRows = groupCursor.getInt(2);
                } catch (Exception e) {
                    Log.d("***GROUP", Log.getStackTraceString(e));
                }
                finally {
                    groupCursor.close();
                }

                // Query Components
                String componentsOfGroupQuery =
                    "SELECT c.component_id, c.data_type " +
                    "FROM component c " +
                    "WHERE c.group_id = " + SQL.quoted(thisGroup.getId().toString());

                Cursor cursor = database.rawQuery(componentsOfGroupQuery, null);

                ArrayList<UUID> componentIds  = new ArrayList<>();
                ArrayList<String> componentTypes = new ArrayList<>();
                try {
                    while (cursor.moveToNext()) {
                        componentIds.add(UUID.fromString(cursor.getString(0)));
                        componentTypes.add(cursor.getString(1));
                    }
                } catch (Exception e) {
                    Log.d("***GROUP", Log.getStackTraceString(e));
                }
                finally {
                    cursor.close();
                }

                ArrayList<WidgetData> widgetDatas = new ArrayList<WidgetData>();
                for (int i = 0; i < componentIds.size(); i++) {
                    widgetDatas.add(
                            WidgetData.empty(componentIds.get(i), thisGroup.getId(),
                                            componentTypes.get(i))
                    );
                }

                thisGroup.setWidgetDatas(widgetDatas);
                thisGroup.setLabel(label);
                thisGroup.setNumberOfRows(numberOfRows);
                thisGroup.setIndex(index);

                return true;

            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                // Create Asynchronous Constructor
                TrackerId groupTrackerId = thisGroup.addAsyncTracker(pageTrackerId);

                // >> Asynchronous load widgetDatas
                for (WidgetData widgetData : thisGroup.getWidgetDatas()) {
                    widgetData.load(groupTrackerId);
                }
            }

        }.execute();
    }


    /**
     * Save to the database.
     */
    public void save(final TrackerId pageTrackerId, final boolean recursive)
    {
        final Group thisGroup = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                ContentValues row = new ContentValues();
                row.put("group_id", thisGroup.getId().toString());
                row.put("page_id", thisGroup.getPageId().toString());
                row.put("label", thisGroup.label);
                row.put("group_index", thisGroup.getIndex());
                row.put("number_of_rows", thisGroup.getNumberOfRows());

                database.insertWithOnConflict(SheetContract.Group.TABLE_NAME,
                                              null,
                                              row,
                                              SQLiteDatabase.CONFLICT_REPLACE);
                return null;
            }

            protected void onPostExecute(Boolean result)
            {
                if (!recursive) return;

                TrackerId groupTrackerId = thisGroup.addAsyncTracker(pageTrackerId);

                // Save the entire sheet to the database
                for (WidgetData widgetData : thisGroup.widgetDatas) {
                    widgetData.save(groupTrackerId);
                }
            }

        }.execute();
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    public View getView(Context context, Rules rules)
    {
        LinearLayout groupLayout = new LinearLayout(context);
        LinearLayout.LayoutParams mainLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.MATCH_PARENT);
        int groupHorzMargins = (int) Util.getDim(context, R.dimen.group_horz_margins);
        int groupMarginBottom = (int) Util.getDim(context, R.dimen.group_margin_bottom);
        groupLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.sheet_medium));
        mainLayoutParams.setMargins(groupHorzMargins, 0,
                                    groupHorzMargins, groupMarginBottom);
        groupLayout.setOrientation(LinearLayout.VERTICAL);
        groupLayout.setLayoutParams(mainLayoutParams);

        int groupPaddingTop = (int) Util.getDim(context, R.dimen.group_padding_top);
        int groupPaddingHorz = (int) Util.getDim(context, R.dimen.group_padding_horz);
        groupLayout.setPadding(groupPaddingHorz, groupPaddingTop, groupPaddingHorz, 0);


        ArrayList<ArrayList<WidgetData>> rows = new ArrayList<>();
        for (int i = 0; i  < this.numberOfRows; i++) {
            rows.add(new ArrayList<WidgetData>());
        }

        // Sort by row
        for (WidgetData widgetData : this.widgetDatas)
        {
            int rowIndex = widgetData.getRow() - 1;
            rows.get(rowIndex).add(widgetData);
        }

        // Sort by column
        for (int j = 0; j < this.numberOfRows; j++) {
            ArrayList<WidgetData> row = rows.get(j);
            Collections.sort(row, new Comparator<WidgetData>() {
                @Override
                public int compare(WidgetData c1, WidgetData c2) {
                    if (c1.getColumn() > c2.getColumn())
                        return 1;
                    if (c1.getColumn() < c2.getColumn())
                        return -1;
                    return 0;
                }
            });
        }

        groupLayout.addView(this.labelView(context));

        for (ArrayList<WidgetData> row : rows)
        {
            LinearLayout rowLayout = new LinearLayout(context);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setLayoutParams(Util.linearLayoutParamsMatch());
            int rowPaddingBottom = (int) context.getResources()
                                              .getDimension(R.dimen.row_padding_bottom);
            rowLayout.setPadding(0, 0, 0, rowPaddingBottom);

            for (WidgetData widgetData : row)
            {
                LinearLayout frameLayout = new LinearLayout(context);
                frameLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams frameLayoutParams = Util.linearLayoutParamsWrap();
                frameLayoutParams.width = 0;
                frameLayoutParams.weight = widgetData.getWidth();
                frameLayout.setLayoutParams(frameLayoutParams);

//                if (widgetData.hasLabel())
//                    frameLayout.addView(widgetData.labelView(context));
                // Add WidgetData Label
//                if (widgetData.hasLabel() && !widgetData.getLabel().equals(this.getLabel())) {
//                }

                // Add WidgetData View
                View componentView = widgetData.getDisplayView(context, rules);
                frameLayout.addView(componentView);

                rowLayout.addView(frameLayout);
            }

            groupLayout.addView(rowLayout);
        }

        //groupLayout.addView(UI.divider(context));

        return groupLayout;
    }



    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Views
    // ------------------------------------------------------------------------------------------

    private LinearLayout labelView(Context context)
    {
        LinearLayout layout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = Util.linearLayoutParamsMatchWrap();
        layout.setLayoutParams(layoutParams);

        int paddingLeft = (int) Util.getDim(context, R.dimen.group_label_padding_left);
        int paddingBottom = (int) Util.getDim(context, R.dimen.group_label_padding_bottom);
        layout.setPadding(paddingLeft, 0, 0, paddingBottom);


        TextView textView = new TextView(context);
        textView.setId(R.id.component_label);

        float labelTextSize = (int) Util.getDim(context, R.dimen.group_label_text_size);
        textView.setTextSize(labelTextSize);

        textView.setTextColor(ContextCompat.getColor(context, R.color.text_light));

        textView.setTypeface(Util.sansSerifFontRegular(context));

        //textView.setText(this.label.toUpperCase());
        textView.setText(this.label);

        layout.addView(textView);

        return layout;
    }


    /**
     * Track the state of a Group object. When the state reaches a desired configuration,
     * execute a callback.
     */
    public static class AsyncTracker
    {

        // > PROPERTIES
        // --------------------------------------------------------------------------------------

        private Map<UUID,Boolean> componentIdTrackerMap;

        private Group group;
        private TrackerId pageTrackerId;


        // > CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public AsyncTracker(Group group, TrackerId pageTrackerId)
        {
            this.group = group;
            this.pageTrackerId = pageTrackerId;

            componentIdTrackerMap = new HashMap<>();
            for (WidgetData widgetData : this.group.getWidgetDatas()) {
                componentIdTrackerMap.put(widgetData.getId(), false);
            }

            if (group.getWidgetDatas().size() == 0) ready();
        }


        // > API
        // --------------------------------------------------------------------------------------

        synchronized public void markComponentId(UUID componentId)
        {
            if (componentIdTrackerMap.containsKey(componentId))
                componentIdTrackerMap.put(componentId, true);
            if (isReady()) ready();
        }


        // > INTERNAL
        // --------------------------------------------------------------------------------------

        private boolean isReady()
        {
            for (boolean status : this.componentIdTrackerMap.values()) {
                if (!status) return false;
            }
            return true;
        }

        private void ready()
        {
            Page.getAsyncTracker(this.pageTrackerId.getCode())
                .markGroupId(this.group.getId());
        }
    }


}
