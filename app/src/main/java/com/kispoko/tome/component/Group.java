
package com.kispoko.tome.component;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



/**
 * Group
 */
public class Group implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String name;
    private ArrayList<Component> components;
    private Layout layout;

    private Map<String, Component> componentByName;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Group(String name, ArrayList<Component> components, Layout layout)
    {
        this.name = name;
        this.components = components;
        this.layout = layout;

        // Index components for name lookup
        componentByName = new HashMap<>();
        for (Component component : this.components)
        {
            componentByName.put(component.getName(), component);
        }
    }


    @SuppressWarnings("unchecked")
    public static Group fromYaml(Map<String,Object> groupYaml)
    {
        ArrayList<Component> components = new ArrayList<>();
        Layout layout;

        String name = (String) groupYaml.get("name");

        ArrayList<Object> componentsYaml = (ArrayList<Object>) groupYaml.get("components");
        for (Object componentYaml : componentsYaml) {
            Component component = Component.fromYaml((Map<String, Object>) componentYaml);
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



    // > API
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

            for (Layout.Frame frame : row.getFrames())
            {
                Component component = this.componentByName.get(frame.getComponentName());
                View frameView = component.getView(context);
                rowLayout.addView(frameView);
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

        textView.setTextColor(ContextCompat.getColor(context, R.color.theme_gray_500));

        textView.setTypeface(null, Typeface.BOLD);

        int padding = (int) context.getResources().getDimension(R.dimen.label_padding);
        textView.setPadding(padding, 0, padding, 0);

        textView.setText(this.name.toUpperCase());

        return textView;
    }

}
