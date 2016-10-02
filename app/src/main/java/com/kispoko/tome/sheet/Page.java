
package com.kispoko.tome.sheet;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;



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

    private String name;
    private ArrayList<Group> groups;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Page(String name, ArrayList<Group> groups)
    {
        this.name = name;
        this.groups = groups;
    }


    @SuppressWarnings("unchecked")
    public static Page fromYaml(Map<String, Object> pageYaml) {
        // Parse name
        String name = (String) pageYaml.get("name");

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


    // > API
    // ------------------------------------------------------------------------------------------

    /**
     * Returns the name of this page.
     * @return The page name.
     */
    public String getName()
    {
        return this.name;
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

}
