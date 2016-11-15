
package com.kispoko.tome.sheet;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.Global;
import com.kispoko.tome.R;
import com.kispoko.tome.rules.Rules;
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
    //private static Map<UUID,AsyncConstructor> asyncConstructorMap = new HashMap<>();

    private static Map<UUID,AsyncTracker> asyncTrackerMap = new HashMap<>();


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Page(UUID id)
    {
        this.id = id;
    }

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
        UUID id = UUID.randomUUID();
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
                Group group = Group.fromYaml(id, (Map<String, Object>) groupYaml);
                group.setIndex(groupIndex);
                groups.add(group);
                groupIndex += 1;
            }
        }

        return new Page(id, label, index, groups);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    // >> Tracking
    // ------------------------------------------------------------------------------------------

    /**
     * Create a tracker for asynchronously tracking the state of a sheet.
     * @return The new tracker's ID.
     */
//    private static UUID addTracker(UUID pageId, UUID roleplayTrackerId, ArrayList<UUID> groupIds)
//    {
//        UUID trackerId = UUID.randomUUID();
//        Page.trackerMap.put(trackerId, new AsyncTracker(pageId, roleplayTrackerId, groupIds));
//        return trackerId;
//    }
//
//
//    public static AsyncTracker getTracker(UUID trackerId)
//    {
//        return Page.trackerMap.get(trackerId);
//    }


    // >> Async Constructor
    // ------------------------------------------------------------------------------------------

    private TrackerId addAsyncTracker(TrackerId roleplayTrackerId)
    {
        UUID trackerCode = UUID.randomUUID();
        Page.asyncTrackerMap.put(trackerCode, new AsyncTracker(this, roleplayTrackerId));
        return new TrackerId(trackerCode, TrackerId.Target.PAGE);
    }


    public static AsyncTracker getAsyncTracker(UUID trackerCode)
    {
        return Page.asyncTrackerMap.get(trackerCode);
    }


    // >> State
    // ------------------------------------------------------------------------------------------

    // >>> Label
    // ------------------------------------------------------------------------------------------

    /**
     * Returns the label of this page.
     * @return The page label.
     */
    public String getLabel() {
        return this.label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public UUID getId()
    {
        return this.id;
    }


    // >>> Groups
    // ------------------------------------------------------------------------------------------

    public ArrayList<Group> getGroups() {
        return this.groups;
    }


    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }


    // >>> Index
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
    public View getView(Context context, Rules rules)
    {
        LinearLayout profileLayout = new LinearLayout(context);

        LinearLayout.LayoutParams profileLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.WRAP_CONTENT);

        int paddingTop = (int) Util.getDim(context, R.dimen.page_padding_top);
        profileLayout.setPadding(0, paddingTop, 0, 0);

        profileLayout.setOrientation(LinearLayout.VERTICAL);
        profileLayout.setLayoutParams(profileLayoutParams);

        for (Group group : this.groups)
        {
            profileLayout.addView(group.getView(context, rules));
        }

        return profileLayout;
    }

    // >> I/O Methods
    // ------------------------------------------------------------------------------------------

    // >>> Database
    // ------------------------------------------------------------------------------------------

    public void load(final TrackerId roleplayTrackerId)
    {
        final Page thisPage = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // ModelQuery Page Data
                String pageQuery =
                    "SELECT page.label, page.page_index " +
                    "FROM Page page " +
                    "WHERE page.page_id =  " + SQL.quoted(thisPage.getId().toString());

                Cursor pageCursor = database.rawQuery(pageQuery, null);

                String label;
                Integer index;
                try {
                    pageCursor.moveToFirst();
                    label = pageCursor.getString(0);
                    index = pageCursor.getInt(1);
                }
                finally {
                    pageCursor.close();
                }

                // ModelQuery Page Groups
                String groupsOfPageQuery =
                    "SELECT grp.group_id " +
                    "FROM _group grp " +
                    "WHERE grp.page_id = " + SQL.quoted(thisPage.getId().toString());

                Cursor groupsCursor = database.rawQuery(groupsOfPageQuery, null);

                ArrayList<Group> groups = new ArrayList<>();
                try {
                    while (groupsCursor.moveToNext()) {
                        UUID groupId = UUID.fromString(groupsCursor.getString(0));
                        groups.add(new Group(groupId, thisPage.getId()));
                    }
                }
                finally {
                    groupsCursor.close();
                }

                thisPage.setLabel(label);
                thisPage.setIndex(index);
                thisPage.setGroups(groups);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                // Create Asynchronous Constructor
                TrackerId pageTrackerId = thisPage.addAsyncTracker(roleplayTrackerId);

                // >> Asynchronously load group data
                for (Group group : thisPage.getGroups()) {
                    group.load(pageTrackerId);
                }

            }

        }.execute();
    }


    /**
     * Save to the database.
     */
    public void save(final TrackerId roleplayTrackerId, final UUID sheetId, final String sectionId,
                     final boolean recursive)
    {
        final Page thisPage = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

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

                // Track which parts of the page were saved.
                TrackerId pageTrackerId = thisPage.addAsyncTracker(roleplayTrackerId);

                // Save the child data to the database
                for (Group group : groups) {
                    group.save(pageTrackerId, true);
                }
            }

        }.execute();

    }


    // NESTED CLASSES
    // ------------------------------------------------------------------------------------------

    /**
     * Track the state of a Page object. When the state reaches a desired configuration,
     * execute a callback.
     */
    public static class AsyncTracker
    {

        // > PROPERTIES
        // --------------------------------------------------------------------------------------

        private Map<UUID,Boolean> groupIdTrackerMap;

        private Page page;
        private TrackerId roleplayTrackerId;


        // > CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public AsyncTracker(Page page, TrackerId roleplayTrackerId)
        {
            this.page = page;
            this.roleplayTrackerId = roleplayTrackerId;

            groupIdTrackerMap = new HashMap<>();
            for (Group group : page.getGroups()) {
                groupIdTrackerMap.put(group.getId(), false);
            }

            if (page.getGroups().size() == 0) ready();
        }


        // > API
        // --------------------------------------------------------------------------------------

        synchronized public void markGroupId(UUID groupId)
        {
            if (groupIdTrackerMap.containsKey(groupId))
                groupIdTrackerMap.put(groupId, true);
            if (isReady()) ready();
        }


        // > INTERNAL
        // --------------------------------------------------------------------------------------

        private boolean isReady()
        {
            for (boolean status : this.groupIdTrackerMap.values()) {
                if (!status) return false;
            }
            return true;
        }

        private void ready()
        {
            Log.d("***PAGE", "page is ready " + this.page.getLabel());
            Roleplay.getAsyncTracker(this.roleplayTrackerId.getCode())
                    .markPageId(this.page.getId());
        }
    }


}



