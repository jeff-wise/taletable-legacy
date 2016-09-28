package com.kispoko.tome.sheet;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.kispoko.tome.component.Component;
import com.kispoko.tome.component.Image;
import com.kispoko.tome.component.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static android.media.CamcorderProfile.get;


/**
 * Format of the profile section.
 */
public class Profile implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ArrayList<Component> componentList;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Profile(Collection<Component> components)
    {
        this.componentList = new ArrayList<>(components);
    }


    @SuppressWarnings("unchecked")
    public static Profile fromYaml(Map<String, Object> profileFormatYaml)
    {
        ArrayList<Component> componentList = new ArrayList<>();
        ArrayList<Object> componentsYaml = (ArrayList<Object>) profileFormatYaml.get("components");
        for (Object componentYaml : componentsYaml) {
            Component component = Component.fromYaml((Map<String, Object>) componentYaml);
            componentList.add(component);
        }

        return new Profile(componentList);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    public ArrayList<Component> getComponentList()
    {
        return this.componentList;
    }



}

