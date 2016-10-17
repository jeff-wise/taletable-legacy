
package com.kispoko.tome.sheet;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.tuple.Tuple5;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.kispoko.tome.R.id.textView;
import static com.kispoko.tome.util.Util.linearLayoutParamsMatchWrap;


/**
 * Group
 */
public class Group implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private String label;
    private ArrayList<Component> components;
    private Integer numberOfRows;
    private Integer index;

    // >> STATIC
    private static Map<UUID,AsyncConstructor> asyncConstructorMap = new HashMap<>();

    private static Map<UUID,SaveTracker> trackerMap = new HashMap<>();


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Group(UUID id, String label, Integer index, Integer numberOfRows,
                 ArrayList<Component> components)
    {
        if (id != null)
            this.id = id;
        else
            this.id = UUID.randomUUID();

        this.label = label;
        this.index = index;
        this.numberOfRows = numberOfRows;
        this.components = components;
    }


    @SuppressWarnings("unchecked")
    public static Group fromYaml(Map<String,Object> groupYaml)
    {
        // Values to parse
        String label = null;
        Integer numberOfRows = null;
        ArrayList<Component> components = new ArrayList<>();

        // Parse values
        // >> Label
        if (groupYaml.containsKey("label"))
            label = (String) groupYaml.get("label");

        // >> Number of Rows
        if (groupYaml.containsKey("number_of_rows"))
            numberOfRows = (Integer) groupYaml.get("number_of_rows");

        // >> Components
        ArrayList<Object> componentsYaml = (ArrayList<Object>) groupYaml.get("components");
        for (Object componentYaml : componentsYaml) {
            Component component = Component.fromYaml((Map<String, Object>) componentYaml);
            components.add(component);
        }

        return new Group(null, label, null, numberOfRows, components);
    }



    // > API
    // ------------------------------------------------------------------------------------------

    // >> Async Constructor
    // ------------------------------------------------------------------------------------------

    private static UUID addAsyncConstructor(UUID id, Integer numberOfComponents,
                                           UUID pageConstructorId)
    {
        UUID constructorId = UUID.randomUUID();

        Group.asyncConstructorMap.put(constructorId, new AsyncConstructor(id,
                                                                 numberOfComponents,
                                                                 pageConstructorId));

        return constructorId;
    }


    public static AsyncConstructor getAsyncConstructor(UUID constructorId)
    {
        return Group.asyncConstructorMap.get(constructorId);
    }


    // >> Tracking
    // ------------------------------------------------------------------------------------------

    /**
     * Create a tracker for asynchronously tracking the state of a sheet.
     * @return The new tracker's ID.
     */
    private static UUID addTracker(UUID groupId, UUID pageTrackerId, ArrayList<UUID> componentIds)
    {
        UUID trackerId = UUID.randomUUID();
        Group.trackerMap.put(trackerId, new SaveTracker(groupId, pageTrackerId, componentIds));
        return trackerId;
    }


    public static SaveTracker getTracker(UUID trackerId)
    {
        return Group.trackerMap.get(trackerId);
    }


    // >> Getters / Setters
    // ------------------------------------------------------------------------------------------

    public ArrayList<Component> getComponents()
    {
        return this.components;
    }


    public UUID getId()
    {
        return this.id;
    }


    public Integer getNumberOfRows()
    {
        return this.numberOfRows;
    }


    public String getLabel()
    {
        return this.label;
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
     * @param database The sqlite database object.
     * @param pageConstructorId The id of the async page constructor.
     * @param groupId The database id of the group to load.
     */
    public static void load(final SQLiteDatabase database,
                            final UUID pageConstructorId,
                            final UUID groupId)
    {
        new AsyncTask<Void,Void,Tuple5<ArrayList<UUID>,ArrayList<String>,String,Integer,Integer>>()
        {

            @Override
            protected Tuple5<ArrayList<UUID>,ArrayList<String>,String,Integer,Integer>
                      doInBackground(Void... args)
            {
                // Query Group Data
                String groupQuery =
                    "SELECT grp.label, grp.group_index, grp.number_of_rows " +
                    "FROM _group grp " +
                    "WHERE grp.group_id =  " + SQL.quoted(groupId.toString());

                Cursor groupCursor = database.rawQuery(groupQuery, null);

                String label;
                Integer index;
                Integer numberOfRows;
                try {
                    groupCursor.moveToFirst();
                    label = groupCursor.getString(0);
                    index = groupCursor.getInt(1);
                    numberOfRows = groupCursor.getInt(2);
                }
                // TODO log
                finally {
                    groupCursor.close();
                }

                // Query Components
                String componentsOfGroupQuery =
                    "SELECT c.component_id, c.data_type " +
                    "FROM component c " +
                    "WHERE c.group_id = " + SQL.quoted(groupId.toString());

                Cursor cursor = database.rawQuery(componentsOfGroupQuery, null);

                ArrayList<UUID> componentIds  = new ArrayList<>();
                ArrayList<String> componentTypes = new ArrayList<>();
                try {
                    while (cursor.moveToNext()) {
                        componentIds.add(UUID.fromString(cursor.getString(0)));
                        componentTypes.add(cursor.getString(1));
                    }
                }
                // TODO log
                finally {
                    cursor.close();
                }

                return new Tuple5<>(componentIds, componentTypes, label, index, numberOfRows);
            }

            @Override
            protected void onPostExecute(Tuple5<ArrayList<UUID>,ArrayList<String>,String,Integer,Integer> data)
            {
                ArrayList<UUID> componentIds   = data.getItem1();
                ArrayList<String>  componentTypes = data.getItem2();
                String             label          = data.getItem3();
                Integer            index          = data.getItem4();
                Integer            numberOfRows   = data.getItem5();

                // Create Asynchronous Constructor
                UUID groupConstructorId = Group.addAsyncConstructor(groupId,
                                                                    componentIds.size(),
                                                                    pageConstructorId);
                Group.getAsyncConstructor(groupConstructorId).setLabel(label);
                Group.getAsyncConstructor(groupConstructorId).setIndex(index);

                // >> Set Label
                Group.asyncConstructorMap.get(groupConstructorId).setNumberOfRows(numberOfRows);

                // >> Asynchronous load components
                for (int i = 0; i < componentIds.size(); i++) {
                    Component.load(database, groupConstructorId,
                                   componentIds.get(i), componentTypes.get(i));
                }

            }

        }.execute();
    }


    /**
     * Save to the database.
     * @param database The SQLite database object.
     * @param pageId The id of the group's parent page.
     * @param recursive If true, save all child objects as well.
     */
    public void save(final SQLiteDatabase database, final UUID pageTrackerId,
                     final UUID pageId, final boolean recursive)
    {
        final Group thisGroup = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                ContentValues row = new ContentValues();
                row.put("group_id", thisGroup.getId().toString());
                row.put("page_id", pageId.toString());
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

                ArrayList<UUID> componentIds = new ArrayList<UUID>();
                for (Component component : thisGroup.getComponents()) {
                    componentIds.add(component.getId());
                }
                UUID groupTrackerId = Group.addTracker(thisGroup.getId(),
                                                       pageTrackerId,
                                                       componentIds);

                // Save the entire sheet to the database
                for (Component component : thisGroup.components)
                {
                    component.save(database, groupTrackerId, thisGroup.getId());
                }
            }

        }.execute();
    }


    // >> Views
    // ------------------------------------------------------------------------------------------

    public View getView(Context context)
    {
        LinearLayout groupLayout = new LinearLayout(context);
        LinearLayout.LayoutParams mainLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.MATCH_PARENT);
        int groupHorzMargins = (int) context.getResources()
                                            .getDimension(R.dimen.group_horz_margins);
        int groupVertMargins = (int) context.getResources()
                                            .getDimension(R.dimen.group_vert_margins);
        mainLayoutParams.setMargins(groupHorzMargins, groupVertMargins,
                                    groupHorzMargins, groupVertMargins);
        groupLayout.setOrientation(LinearLayout.VERTICAL);
        groupLayout.setLayoutParams(mainLayoutParams);

        groupLayout.addView(this.labelView(context));

        ArrayList<ArrayList<Component>> rows = new ArrayList<>();
        for (int i = 0; i  < this.numberOfRows; i++) {
            rows.add(new ArrayList<Component>());
        }

        // Sort by row
        for (Component component : this.components)
        {
            int rowIndex = component.getRow() - 1;
            rows.get(rowIndex).add(component);
        }

        // Sort by column
        for (int j = 0; j < this.numberOfRows; j++) {
            ArrayList<Component> row = rows.get(j);
            Collections.sort(row, new Comparator<Component>() {
                @Override
                public int compare(Component c1,Component c2) {
                    if (c1.getColumn() > c2.getColumn())
                        return 1;
                    if (c1.getColumn() < c2.getColumn())
                        return -1;
                    return 0;
                }
            });
        }


        for (ArrayList<Component> row : rows)
        {
            LinearLayout rowLayout = new LinearLayout(context);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setLayoutParams(Util.linearLayoutParamsMatch());
            int rowTopPadding = (int) context.getResources()
                                              .getDimension(R.dimen.row_padding_top);
            rowLayout.setPadding(0, rowTopPadding, 0, 0);

            for (Component component : row)
            {
                LinearLayout frameLayout = new LinearLayout(context);
                frameLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams frameLayoutParams = Util.linearLayoutParamsWrap();
                frameLayoutParams.width = 0;
                frameLayoutParams.weight = component.getWidth();
                frameLayout.setLayoutParams(frameLayoutParams);

                // Add Component Label
                if (component.hasLabel() && !component.getLabel().equals(this.getLabel())) {
                    frameLayout.addView(component.labelView(context));
                }

                // Add Component View
                View componentView = component.getDisplayView(context);
                frameLayout.addView(componentView);

                rowLayout.addView(frameLayout);
            }

            groupLayout.addView(rowLayout);
        }

        return groupLayout;
    }



    // > INTERNAL
    // ------------------------------------------------------------------------------------------

    // >> Views
    // ------------------------------------------------------------------------------------------

    private LinearLayout labelView(Context context)
    {
        LinearLayout layout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = Util.linearLayoutParamsMatchWrap();
        layout.setLayoutParams(layoutParams);

        int paddingLeft = (int) Util.getDim(context, R.dimen.group_label_padding_left);
        layout.setPadding(paddingLeft, 0, 0, 0);


        TextView textView = new TextView(context);
        textView.setId(R.id.component_label);

        float labelTextSize = (int) context.getResources()
                                         .getDimension(R.dimen.label_text_size);
        textView.setTextSize(labelTextSize);

        textView.setTextColor(ContextCompat.getColor(context, R.color.bluegrey_700));

        //textView.setTypeface(null, Typeface.BOLD);
        textView.setTypeface(Util.sansSerifFontBold(context));

        textView.setText(this.label.toUpperCase());

        layout.addView(textView);

        return layout;
    }


    // > NESTED CLASSES
    // ------------------------------------------------------------------------------------------

    public static class AsyncConstructor
    {
        private UUID pageConstructorId;

        private Integer numberOfComponents;

        private UUID id;
        private String label;
        private Integer index;
        private Integer numberOfRows;
        private ArrayList<Component> components;

        public AsyncConstructor(UUID id, Integer numberOfComponents, UUID pageConstructorId)
        {
            this.id = id;

            this.label = null;
            this.index = null;
            this.numberOfComponents = null;

            this.pageConstructorId = pageConstructorId;
            this.numberOfComponents = numberOfComponents;

            this.components = new ArrayList<>();
        }

        synchronized public void setLabel(String label)
        {
            this.label = label;
        }

        synchronized public void setIndex(Integer index)
        {
            this.index = index;
        }

        synchronized public void addComponent(Component component)
        {
            this.components.add(component);

            if (this.isReady())  ready();
        }

        synchronized public void setNumberOfRows(Integer numberOfRows)
        {
            this.numberOfRows = numberOfRows;

            if (this.isReady())  ready();
        }

        private boolean isReady()
        {
            return this.label != null &&
                   this.index != null &&
                   this.numberOfRows != null &&
                   this.components.size() == numberOfComponents;
        }

        private void ready()
        {
            Group group = new Group(this.id, this.label, this.index, this.numberOfRows, this.components);
            Page.getAsyncConstructor(pageConstructorId).addGroup(group);
        }

    }


    /**
     * Track the state of a Group object. When the state reaches a desired configuration,
     * execute a callback.
     */
    public static class SaveTracker
    {

        // > PROPERTIES
        // --------------------------------------------------------------------------------------

        private Map<UUID,Boolean> componentIdTrackerMap;
        private Integer componentIdsRemaining;

        private UUID groupId;
        private UUID pageTrackerId;


        // > CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public SaveTracker(UUID groupId, UUID pageTrackerId, ArrayList<UUID> componentIds)
        {
            this.groupId = groupId;
            this.pageTrackerId = pageTrackerId;

            componentIdTrackerMap = new HashMap<>();
            for (UUID componentId : componentIds)
            {
                componentIdTrackerMap.put(componentId, false);
            }
            this.componentIdsRemaining = componentIds.size();

            if (componentIds.size() == 0)
                ready();
        }


        // > API
        // --------------------------------------------------------------------------------------

        synchronized public void setComponentId(UUID componentId)
        {
            if (componentIdTrackerMap.containsKey(componentId)) {
                boolean currentStatus = componentIdTrackerMap.get(componentId);
                if (!currentStatus) {
                    componentIdTrackerMap.put(componentId, true);
                    componentIdsRemaining -= 1;
                    if (isReady()) ready();
                }
            }
        }


        // > INTERNAL
        // --------------------------------------------------------------------------------------

        private boolean isReady()
        {
            return componentIdsRemaining == 0;
        }

        private void ready()
        {
            Page.getTracker(this.pageTrackerId).setGroupId(this.groupId);
        }
    }



}
