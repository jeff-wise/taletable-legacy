
package com.kispoko.tome.sheet;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.Unique;
import com.kispoko.tome.util.tuple.Tuple2;
import com.kispoko.tome.util.tuple.Tuple3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.R.attr.name;


/**
 * Sheet Page
 *
 * A page corresponds to a real-life piece of paper, where each page has fields that are related
 * to a specific theme. The fields are cotained in a list of groups, which group related
 * character content.
 */
public class Page implements Unique, Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private String label;
    private ArrayList<Group> groups;

    private Integer index;

    // > STATIC
    private static Map<UUID,AsyncConstructor> asyncConstructorMap = new HashMap<>();

    private static Map<UUID,SaveTracker> trackerMap = new HashMap<>();


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Page(UUID id, String label, Integer index, ArrayList<Group> groups)
    {
        // Set or create UUID
        if (id != null)
            this.id = id;
        else
            this.id = UUID.randomUUID();

        this.label = label;
        this.index = index;
        this.groups = groups;

        // Make sure groups are sorted
        Collections.sort(this.groups, new Comparator<Group>() {
            @Override
            public int compare(Group group1, Group group2) {
                if (group1.getIndex() > group2.getIndex())
                    return 1;
                if (group1.getIndex() < group2.getIndex())
                    return -1;
                return 0;
            }
        });
    }


    @SuppressWarnings("unchecked")
    public static Page fromYaml(Map<String, Object> pageYaml)
    {
        // Values to parse
        String label = null;
        Integer index = null;

        // Parse Values
        // >> Label
        label = (String) pageYaml.get("label");

        // >> Groups
        ArrayList<Group> groups = new ArrayList<>();
        ArrayList<Object> groupsYaml = (ArrayList<Object>) pageYaml.get("groups");

        if (groupsYaml != null)
        {
            int groupIndex = 0;
            for (Object groupYaml : groupsYaml)
            {
                Group group = Group.fromYaml((Map<String, Object>) groupYaml);
                group.setIndex(groupIndex);
                groups.add(group);
                groupIndex += 1;
            }
        }

        return new Page(null, label, index, groups);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    // >> Tracking
    // ------------------------------------------------------------------------------------------

    /**
     * Create a tracker for asynchronously tracking the state of a sheet.
     * @return The new tracker's ID.
     */
    private static UUID addTracker(UUID pageId, UUID roleplayTrackerId, ArrayList<UUID> groupIds)
    {
        UUID trackerId = UUID.randomUUID();
        Page.trackerMap.put(trackerId, new SaveTracker(pageId, roleplayTrackerId, groupIds));
        return trackerId;
    }


    public static SaveTracker getTracker(UUID trackerId)
    {
        return Page.trackerMap.get(trackerId);
    }


    // >> Async Constructor
    // ------------------------------------------------------------------------------------------

    private static UUID addAsyncConstructor(UUID id, Integer numberOfGroups,
                                           UUID roleplayConstructorId)
    {
        UUID constructorId = UUID.randomUUID();
        Page.asyncConstructorMap.put(constructorId, new AsyncConstructor(id,
                                                                 numberOfGroups,
                                                                 roleplayConstructorId));
        return constructorId;
    }


    public static AsyncConstructor getAsyncConstructor(UUID constructorId)
    {
        return Page.asyncConstructorMap.get(constructorId);
    }


    // >> Getters/Setters
    // ------------------------------------------------------------------------------------------

    /**
     * Returns the label of this page.
     * @return The page label.
     */
    public String getLabel()
    {
        return this.label;
    }


    public UUID getId()
    {
        return this.id;
    }


    public ArrayList<Group> getGroups()
    {
        return this.groups;
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


    // >> Views
    // ------------------------------------------------------------------------------------------

    /**
     * Returns an android view that represents this page.
     * @param context The parent activity context.
     * @return A View of this page.
     */
    public View getView(Context context)
    {
        LinearLayout profileLayout = new LinearLayout(context);

        LinearLayout.LayoutParams profileLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.WRAP_CONTENT);

        profileLayout.setOrientation(LinearLayout.VERTICAL);
        profileLayout.setLayoutParams(profileLayoutParams);

        for (Group group : this.groups)
        {
            profileLayout.addView(group.getView(context));
        }

        return profileLayout;
    }

    // >> I/O Methods
    // ------------------------------------------------------------------------------------------

    // >>> Database
    // ------------------------------------------------------------------------------------------

    public static void load(final SQLiteDatabase database,
                            final UUID roleplayConstructorId,
                            final UUID pageId)
    {
        new AsyncTask<Void,Void,Tuple3<ArrayList<UUID>,String,Integer>>()
        {

            @Override
            protected Tuple3<ArrayList<UUID>,String,Integer> doInBackground(Void... args)
            {
                // Query Page Data
                String pageQuery =
                    "SELECT page.label, page.page_index " +
                    "FROM Page page " +
                    "WHERE page.page_id =  " + SQL.quoted(pageId.toString());

                Cursor pageCursor = database.rawQuery(pageQuery, null);

                String label;
                Integer index;
                try {
                    pageCursor.moveToFirst();
                    label = pageCursor.getString(0);
                    index = pageCursor.getInt(1);
                }
                // TODO log
                finally {
                    pageCursor.close();
                }

                // Query Page Groups
                String groupsOfPageQuery =
                    "SELECT grp.group_id " +
                    "FROM _group grp " +
                    "WHERE grp.page_id = " + SQL.quoted(pageId.toString());

                Cursor groupsCursor = database.rawQuery(groupsOfPageQuery, null);

                ArrayList<UUID> groupIds = new ArrayList<>();
                try {
                    while (groupsCursor.moveToNext()) {
                        groupIds.add(UUID.fromString(groupsCursor.getString(0)));
                    }
                }
                // TODO log
                finally {
                    groupsCursor.close();
                }

                return new Tuple3<>(groupIds, label, index);
            }

            @Override
            protected void onPostExecute(Tuple3<ArrayList<UUID>,String,Integer> data)
            {
                ArrayList<UUID> groupIds = data.getItem1();
                String          label    = data.getItem2();
                Integer         index    = data.getItem3();

                // Create Asynchronous Constructor
                UUID pageConstructorId = Page.addAsyncConstructor(pageId,
                                                                  groupIds.size(),
                                                                  roleplayConstructorId);

                // >> Already have label and index
                Page.asyncConstructorMap.get(pageConstructorId).setLabel(label);
                Page.asyncConstructorMap.get(pageConstructorId).setIndex(index);

                // >> Asynchronously add groups
                for (UUID groupId : groupIds) {
                    Group.load(database, pageConstructorId, groupId);
                }

            }

        }.execute();
    }


    /**
     * Save to the database.
     * @param database The SQLite database object.
     * @param recursive If true, save all child objects as well.
     */
    public void save(final SQLiteDatabase database, final UUID roleplayTrackerId,
                     final UUID sheetId, final String sectionId, final boolean recursive)
    {
        final Page thisPage = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                ContentValues row = new ContentValues();
                row.put("page_id", thisPage.getId().toString());
                row.put("sheet_id", sheetId.toString());
                row.put("section_id", sectionId);
                row.put("page_index", thisPage.getIndex());
                row.put("label", thisPage.label);

                database.insertWithOnConflict(SheetContract.Page.TABLE_NAME,
                                              null,
                                              row,
                                              SQLiteDatabase.CONFLICT_REPLACE);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                if (!recursive) return;

                ArrayList<UUID> groupIds = new ArrayList<>();
                for (Group group : thisPage.getGroups()) {
                    groupIds.add(group.getId());
                }

                // Track which parts of the page were saved.
                UUID pageTrackerId = Page.addTracker(thisPage.getId(), roleplayTrackerId, groupIds);

                // Save the child data to the database
                for (Group group : groups)
                {
                    group.save(database, pageTrackerId, thisPage.getId(), true);
                }
            }

        }.execute();

    }


    // > PAGE CLASSES
    // ------------------------------------------------------------------------------------------

    /**
     * Create a page class asynchronously.
     * Define required state of a new page, and allow that state to be filled in by other
     * application components at any time. When the constructor reaches the desired state, then
     * it executes a callback.
     */
    public static class AsyncConstructor
    {

        // > PROPERTIES
        // --------------------------------------------------------------------------------------

        private UUID roleplayConstructorId;

        private Integer numberOfGroups;

        private UUID id;
        private String label;
        private Integer index;
        private ArrayList<Group> groups;


        // > CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public AsyncConstructor(UUID id, int numberOfGroups, UUID roleplayConstructorId)
        {
            this.id = id;
            this.roleplayConstructorId = roleplayConstructorId;
            this.numberOfGroups = numberOfGroups;

            label = null;
            index = null;
            groups = new ArrayList<>();
        }


        // > API
        // --------------------------------------------------------------------------------------

        synchronized public void setLabel(String label)
        {
            this.label = label;
            if (this.isReady())  ready();
        }

        synchronized public void setIndex(Integer index)
        {
            this.index = index;
            if (this.isReady())  ready();
        }

        synchronized public void addGroup(Group group)
        {
            this.groups.add(group);
            if (this.isReady())  ready();
        }


        // > INTERNAL
        // --------------------------------------------------------------------------------------

        private boolean isReady()
        {
            return this.id != null &&
                   this.label != null &&
                   this.index != null &&
                   this.groups.size() == numberOfGroups;
        }

        private void ready()
        {
            Page page = new Page(this.id, this.label, this.index, this.groups);
            Roleplay.getAsyncConstructor(this.roleplayConstructorId).addPage(page);
        }

    }



    /**
     * Track the state of a Page object. When the state reaches a desired configuration,
     * execute a callback.
     */
    public static class SaveTracker
    {

        // > PROPERTIES
        // --------------------------------------------------------------------------------------

        private Map<UUID,Boolean> groupIdTrackerMap;
        private Integer groupIdsRemaining;

        private UUID pageId;
        private UUID roleplayTrackerId;


        // > CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public SaveTracker(UUID pageId, UUID roleplayTrackerId, ArrayList<UUID> groupIds)
        {
            this.pageId = pageId;
            this.roleplayTrackerId = roleplayTrackerId;

            groupIdTrackerMap = new HashMap<>();
            for (UUID groupId : groupIds)
            {
                groupIdTrackerMap.put(groupId, false);
            }
            this.groupIdsRemaining = groupIds.size();

            if (groupIds.size() == 0)
                ready();
        }


        // > API
        // --------------------------------------------------------------------------------------

        synchronized public void setGroupId(UUID groupId)
        {
            if (groupIdTrackerMap.containsKey(groupId)) {
                boolean currentStatus = groupIdTrackerMap.get(groupId);
                if (!currentStatus) {
                    groupIdTrackerMap.put(groupId, true);
                    groupIdsRemaining -= 1;
                    if (isReady()) ready();
                }
            }
        }


        // > INTERNAL
        // --------------------------------------------------------------------------------------

        private boolean isReady()
        {
            return groupIdsRemaining == 0;
        }

        private void ready()
        {
            Roleplay.getTracker(this.roleplayTrackerId).setPageId(this.pageId);
        }
    }


}



