
package com.kispoko.tome.sheet;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.component.Group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;



/**
 * Format of the profile section.
 */
public class Profile implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ArrayList<Group> groups;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Profile(Collection<Group> groups)
    {
        this.groups = new ArrayList<>(groups);
    }


    @SuppressWarnings("unchecked")
    public static Profile fromYaml(Map<String, Object> profileYaml)
    {
        ArrayList<Group> groups = new ArrayList<>();

        ArrayList<Object> groupsYaml = (ArrayList<Object>) profileYaml.get("groups");
        for (Object groupYaml : groupsYaml) {
            Group group = Group.fromYaml((Map<String, Object>) groupYaml);
            groups.add(group);
        }

        return new Profile(groups);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    public ArrayList<Group> getGroups()
    {
        return this.groups;
    }


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

