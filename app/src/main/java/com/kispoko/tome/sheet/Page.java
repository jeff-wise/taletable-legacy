
package com.kispoko.tome.sheet;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.db.SheetContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.R.string.no;
import static android.icu.text.MessagePattern.ArgType.SELECT;
import static android.text.style.TtsSpan.TYPE_TEXT;
import static org.yaml.snakeyaml.nodes.Tag.SET;


/**
 * Sheet Page
 *
 * A page corresponds to a real-life piece of paper, where each page has fields that are related
 * to a specific theme. The fields are cotained in a list of groups, which group related
 * character content.
 */
public class Page implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String label;
    private ArrayList<Group> groups;

    private Long id;

    public static Map<Integer,AsyncConstructor> asyncConstructorMap = new HashMap<>();


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Page(String label, ArrayList<Group> groups)
    {
        this.label = label;
        this.groups = groups;

        this.id = null;
    }


    @SuppressWarnings("unchecked")
    public static Page fromYaml(Map<String, Object> pageYaml) {
        // Parse label
        String name = (String) pageYaml.get("label");

        // Parse groups
        ArrayList<Group> groups = new ArrayList<>();
        ArrayList<Object> groupsYaml = (ArrayList<Object>) pageYaml.get("groups");

        if (groupsYaml != null)
        {
            for (Object groupYaml : groupsYaml) {
                Group group = Group.fromYaml((Map<String, Object>) groupYaml);
                groups.add(group);
            }
        }

        return new Page(name, groups);
    }


    public static Integer fromAysnc(Integer numberOfGroups, Integer roleplayConstructorId)
    {
        Random randGen = new Random();
        Integer constructorId = randGen.nextInt();

        Page.asyncConstructorMap.put(constructorId,
                                     new AsyncConstructor(numberOfGroups, roleplayConstructorId));

        return constructorId;
    }


    // > API
    // ------------------------------------------------------------------------------------------

    /**
     * Returns the label of this page.
     * @return The page label.
     */
    public String getLabel()
    {
        return this.label;
    }


    public void setId(Long id)
    {
        this.id = id;
    }


    public ArrayList<Group> getGroups()
    {
        return this.groups;
    }


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
                            final Integer roleplayConstructorId,
                            final Integer pageId)
    {
        new AsyncTask<Void,Void,Void>()
        {

            protected Void doInBackground(Void... args)
            {
                // Query Page Data
                String pageQuery =
                    "SELECT label " +
                    "FROM Page " +
                    "WHERE Page.page_id =  " + Integer.toString(pageId);

                Cursor pageCursor = database.rawQuery(pageQuery, null);

                String label;
                try {
                    pageCursor.moveToFirst();
                    label = pageCursor.getString(0);
                }
                // TODO log
                finally {
                    pageCursor.close();
                }

                // Query Page Groups
                String groupsOfPageQuery =
                    "SELECT group_id " +
                    "FROM Group " +
                    "WHERE Group.page_id = " + Integer.toString(pageId);

                Cursor groupsCursor = database.rawQuery(groupsOfPageQuery, null);

                ArrayList<Integer> groupIds = new ArrayList<Integer>();
                try {
                    while (groupsCursor.moveToNext()) {
                        groupIds.add(groupsCursor.getInt(0));
                    }
                }
                // TODO log
                finally {
                    groupsCursor.close();
                }

                // Create Asynchronous Constructor
                Integer pageConstructorId = Page.fromAysnc(groupIds.size(), roleplayConstructorId);

                // >> Add Label
                Page.asyncConstructorMap.get(pageConstructorId).setLabel(label);

                // >> Asynchronously add groups
                for (Integer groupId : groupIds) {
                    Group.load(database, pageConstructorId, groupId);
                }

                return null;
            }

        }.execute();
    }


    /**
     * Save to the database.
     * @param database The SQLite database object.
     * @param recursive If true, save all child objects as well.
     */
    public void save(final SQLiteDatabase database, final Long sheetId,
                     final String sectionId, final boolean recursive)
    {
        final Page thisPage = this;

        new AsyncTask<Void,Void,Long>()
        {
            protected Long doInBackground(Void... args)
            {
                ContentValues row = new ContentValues();
                row.put("page_id", thisPage.id);
                row.put("sheet_id", sheetId);
                row.put("section_id", sectionId);
                row.put("label", thisPage.label);

                Long pageId = database.insertWithOnConflict(SheetContract.Page.TABLE_NAME,
                                                             null,
                                                             row,
                                                             SQLiteDatabase.CONFLICT_REPLACE);

                // Set ID in case of first insert and ID was Null
                thisPage.setId(pageId);

                return pageId;
            }

            protected void onPostExecute(Long pageId)
            {
                if (!recursive) return;

                // Save the child data to the database
                for (Group group : groups)
                {
                    group.save(database, pageId, true);
                }
            }

        }.execute();

    }




    // > NESTED CLASSES
    // ------------------------------------------------------------------------------------------

    public static class AsyncConstructor
    {
        private Integer roleplayConsId;

        private Integer numberOfGroups;

        private String label;
        private ArrayList<Group> groups;

        public AsyncConstructor(int numberOfGroups, Integer roleplayConsId)
        {
            this.roleplayConsId = roleplayConsId;
            this.numberOfGroups = numberOfGroups;

            label = null;
            groups = new ArrayList<>();
        }

        synchronized public void setLabel(String label)
        {
            this.label = label;

            if (this.isReady())  ready();
        }

        synchronized public void addGroup(Group group)
        {
            this.groups.add(group);

            if (this.isReady())  ready();
        }

        private boolean isReady()
        {
            return this.label != null && this.groups.size() == numberOfGroups;
        }

        private void ready()
        {
            Page page = new Page(this.label, this.groups);
            Roleplay.asyncConstructorMap.get(roleplayConsId).addPage(page);
        }

    }

}



