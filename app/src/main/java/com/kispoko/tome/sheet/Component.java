
package com.kispoko.tome.sheet;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.component.Document;
import com.kispoko.tome.sheet.component.Image;
import com.kispoko.tome.sheet.component.NumberInteger;
import com.kispoko.tome.sheet.component.Table;
import com.kispoko.tome.sheet.component.Text;
import com.kispoko.tome.rules.RulesEngine;
import com.kispoko.tome.type.Type;

import java.io.Serializable;
import java.util.Map;

import static android.R.attr.id;


/**
 * Component
 *
 */
public abstract class Component implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Id id;
    private Type.Id typeId;
    private String label;


    // > INTERFACE
    // ------------------------------------------------------------------------------------------

    abstract public View getDisplayView(Context context);
    abstract public View getEditorView(Context context);

    abstract public String componentName();

    abstract public void save(SQLiteDatabase database, Long groupId);


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Component(Id id, Type.Id typeId, String label)
    {
        this.id = id;
        this.typeId = typeId;
        this.label = label;
    }


    // > API
    // ------------------------------------------------------------------------------------------

    // >> Id
    // ------------------------------------------------------------------------------------------

    public Id getId()
    {
        return this.id;
    }

    public void setId(Id id)
    {
        this.id = id;
    }


    // >> Label
    // ------------------------------------------------------------------------------------------

    public boolean hasLabel()
    {
        return this.label != null;
    }


    public String getLabel()
    {
        return this.label;
    }


    // >> Type
    // ------------------------------------------------------------------------------------------

    public boolean hasType()
    {
        return this.typeId != null;
    }


    public Type getType()
    {
        // TODO verify
        if (this.typeId != null)
            return RulesEngine.getType(this.typeId);
        else
            return null;
    }


    // >> Views
    // ------------------------------------------------------------------------------------------

    /**
     * Create the view for the component label.
     * @param context The context.
     * @return A TextView representing the component's label.
     */
    public TextView labelView(Context context)
    {
        TextView textView = new TextView(context);
        textView.setId(R.id.component_label);
        float labelTextSize = (int) context.getResources()
                                         .getDimension(R.dimen.label_text_size);
        textView.setTextSize(labelTextSize);

        textView.setTextColor(ContextCompat.getColor(context, R.color.bluegrey_400));

        textView.setTypeface(null, Typeface.BOLD);

        textView.setText(this.label.toUpperCase());

        return textView;
    }


    // > STATIC METHODS
    // ------------------------------------------------------------------------------------------

    /**
     * Create a component from Yaml.
     * @param componentYaml The yaml object.
     * @return The parsed component.
     */
    public static Component fromYaml(Map<String, Object> componentYaml)
    {
        String componentType = (String) componentYaml.get("type");

        switch (componentType)
        {
            case "text":
                return Text.fromYaml(componentYaml);
            case "image":
                return Image.fromYaml(componentYaml);
            case "integer":
                return NumberInteger.fromYaml(componentYaml);
            case "document":
                return Document.fromYaml(componentYaml);
            case "table":
                return Table.fromYaml(componentYaml);
        }

        return null;
    }


    /**
     * Load a Group from the database.
     * @param database The sqlite database object.
     * @param groupConstructorId The id of the async group constructor.
     * @param componentId The database id of the component to load.
     */
    public static void load(SQLiteDatabase database, Integer groupConstructorId,
                            Integer componentId, String componentType)
    {
        switch (componentType)
        {
            case "text":
                Text.load(database, groupConstructorId, componentId);
                break;
            case "image":
                Image.load(database, groupConstructorId, componentId);
                break;
            case "integer":
                NumberInteger.load(database, groupConstructorId, componentId);
                break;
            case "table":
                Table.load(database, groupConstructorId, componentId);
                break;
        }
    }



    public static LinearLayout linearLayout(Context context)
    {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(linearLayoutParams);

        return layout;
    }


    // > NESTED CLASSES
    // ------------------------------------------------------------------------------------------

    public enum TextSize
    {
        SMALL,
        MEDIUM,
        LARGE;

        public static TextSize fromString(String textSize)
        {
            return TextSize.valueOf(textSize.toUpperCase());
        }

    }


    public static class Id
    {
        private Long id;
        private Long subId;

        public Id(Long id, Long subId)
        {
            this.id = id;
            this.subId = subId;
        }

        public Long getId()
        {
            return this.id;
        }

        public Long getSubId()
        {
            return this.subId;
        }

    }


}
