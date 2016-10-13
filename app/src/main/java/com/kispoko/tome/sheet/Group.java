
package com.kispoko.tome.sheet;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.sheet.group.Layout;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;



/**
 * Group
 */
public class Group implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Long id;

    private String label;
    private ArrayList<Component> components;
    private Layout layout;

    private Map<Integer, Component> componentById;

    public static Map<Integer,AsyncConstructor> asyncConstructorMap = new HashMap<>();


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Group(String label, ArrayList<Component> components, Layout layout)
    {
        this.label = label;
        this.components = components;
        this.layout = layout;

        this.id = null;

        // Index components for label lookup
        componentById = new HashMap<>();
        for (Component component : this.components)
        {
            //Log.d("coffee", "indexed component " + component.getLabel());
            componentById.put(component.getId(), component);
        }
    }


    @SuppressWarnings("unchecked")
    public static Group fromYaml(Map<String,Object> groupYaml)
    {
        ArrayList<Component> components = new ArrayList<>();
        Layout layout;

        String name = (String) groupYaml.get("label");

        ArrayList<Object> componentsYaml = (ArrayList<Object>) groupYaml.get("components");
        for (Object componentYaml : componentsYaml) {
            Component component = Component.fromYaml((Map<String, Object>) componentYaml);

            //Log.d("coffee", "component label " + component.getLabel());

            components.add(component);
        }

        if (groupYaml.containsKey("layout")) {
            Map<String,Object> layoutYaml = (Map<String,Object>) groupYaml.get("layout");
            layout = Layout.fromYaml(layoutYaml);
        } else {
            layout = Layout.asDefault(components);
        }

        return new Group(name, components, layout);
    }


    public static Integer fromAysnc(Integer numberOfComponents, Integer pageConstructorId)
    {
        Random randGen = new Random();
        Integer constructorId = randGen.nextInt();

        Group.asyncConstructorMap.put(constructorId,
                                     new AsyncConstructor(numberOfComponents, pageConstructorId));

        return constructorId;
    }


    // > API
    // ------------------------------------------------------------------------------------------

    // >> Getters / Setters
    // ------------------------------------------------------------------------------------------

    public Component getComponent(String name)
    {
        return this.componentById.get(name);
    }


    public ArrayList<Component> getComponents()
    {
        return this.components;
    }


    public void setId(Long id)
    {
        this.id = id;
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
                            final Integer pageConstructorId,
                            final Integer groupId)
    {
        new AsyncTask<Void,Void,Void>()
        {

            protected Void doInBackground(Void... args)
            {
                // Query Group Data
                String groupQuery =
                    "SELECT label " +
                    "FROM Group " +
                    "WHERE Group.group_id =  " + Integer.toString(groupId);

                Cursor groupCursor = database.rawQuery(groupQuery, null);

                String label;
                try {
                    groupCursor.moveToFirst();
                    label = groupCursor.getString(0);
                }
                // TODO log
                finally {
                    groupCursor.close();
                }

                // Query Components
                String componentsOfGroupQuery =
                    "SELECT c.component_id, c.data_type " +
                    "FROM Component c " +
                    "WHERE Component.group_id = " + Integer.toString(groupId);

                Cursor cursor = database.rawQuery(componentsOfGroupQuery, null);

                ArrayList<Integer> componentIds  = new ArrayList<Integer>();
                ArrayList<String> componentTypes = new ArrayList<String>();
                try {
                    while (cursor.moveToNext()) {
                        componentIds.add(cursor.getInt(0));
                        componentTypes.add(cursor.getString(1));
                    }
                }
                // TODO log
                finally {
                    cursor.close();
                }


                // Create Asynchronous Constructor
                Integer groupConstructorId = Group.fromAysnc(componentIds.size(), pageConstructorId);

                // >> Set Label
                Page.asyncConstructorMap.get(pageConstructorId).setLabel(label);

                // >> Asynchronous load components
                for (int i = 0; i < componentIds.size(); i++) {
                    Component.load(database, groupConstructorId,
                                   componentIds.get(i), componentTypes.get(i));
                }

                // >> Asynchronous load group layout
                Layout.load(database, groupConstructorId, groupId);

                return null;
            }

        }.execute();
    }


    /**
     * Save to the database.
     * @param database The SQLite database object.
     * @param pageId The id of the group's parent page.
     * @param recursive If true, save all child objects as well.
     */
    public void save(final SQLiteDatabase database, final Long pageId, final boolean recursive)
    {
        final Group thisGroup = this;

        new AsyncTask<Void,Void,Long>()
        {
            protected Long doInBackground(Void... args)
            {
                ContentValues row = new ContentValues();
                row.put("group_id", thisGroup.id);
                row.put("page_id", pageId);
                row.put("label", thisGroup.label);

                Long groupId = database.insertWithOnConflict(SheetContract.Group.TABLE_NAME,
                                                             null,
                                                             row,
                                                             SQLiteDatabase.CONFLICT_REPLACE);

                // Set ID in case of first insert and ID was Null
                thisGroup.setId(groupId);

                return groupId;
            }

            protected void onPostExecute(Long groupId)
            {
                if (!recursive) return;

                // Save the entire sheet to the database
                thisGroup.layout.save(database, groupId, true);

                for (Component component : thisGroup.components)
                {
                    component.save(database, groupId);
                }
            }

        }.execute();
    }


    // >> View Methods
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

        for (Layout.Row row : this.layout.getRows())
        {
            LinearLayout rowLayout = new LinearLayout(context);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setLayoutParams(Util.linearLayoutParamsMatch());
            int rowTopPadding = (int) context.getResources()
                                              .getDimension(R.dimen.row_padding_top);
            rowLayout.setPadding(0, rowTopPadding, 0, 0);

            for (Layout.Frame frame : row.getFrames())
            {
                LinearLayout frameLayout = new LinearLayout(context);
                frameLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams frameLayoutParams = Util.linearLayoutParamsWrap();
                frameLayoutParams.width = 0;
                frameLayoutParams.weight = (float) frame.getWidth();
                frameLayout.setLayoutParams(frameLayoutParams);

                Component component = this.componentById.get(frame.getComponentId());

                // Add Component Label
                if (component.hasLabel()) {
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

    public TextView labelView(Context context)
    {
        TextView textView = new TextView(context);
        textView.setId(R.id.component_label);

        float labelTextSize = (int) context.getResources()
                                         .getDimension(R.dimen.label_text_size);
        textView.setTextSize(labelTextSize);

        textView.setTextColor(ContextCompat.getColor(context, R.color.bluegrey_600));

        textView.setTypeface(null, Typeface.BOLD);

//        int padding = (int) context.getResources().getDimension(R.dimen.label_padding);
//        textView.setPadding(padding, 0, 0, 0);

        textView.setText(this.label.toUpperCase());

        return textView;
    }


    // > NESTED CLASSES
    // ------------------------------------------------------------------------------------------

    public static class AsyncConstructor
    {
        private Integer pageConstructorId;

        private Integer numberOfComponents;

        private String label;
        private ArrayList<Component> components;
        private Layout layout;

        public AsyncConstructor(Integer numberOfComponents, Integer pageConstructorId)
        {
            this.pageConstructorId = pageConstructorId;
            this.numberOfComponents = numberOfComponents;

            this.label = null;
            this.components = new ArrayList<>();
            this.layout = null;
        }

        synchronized public void setLabel(String label)
        {
            this.label = label;

            if (this.isReady())  ready();
        }

        synchronized public void addComponent(Component component)
        {
            this.components.add(component);

            if (this.isReady())  ready();
        }

        synchronized public void setLayout(Layout layout)
        {
            this.layout = layout;

            if (this.isReady())  ready();
        }

        private boolean isReady()
        {
            return this.label != null &&
                   this.components.size() == numberOfComponents &&
                   this.layout != null;
        }

        private void ready()
        {
            Group group = new Group(this.label, this.components, this.layout);
            Page.asyncConstructorMap.get(pageConstructorId).addGroup(group);
        }

    }


}
