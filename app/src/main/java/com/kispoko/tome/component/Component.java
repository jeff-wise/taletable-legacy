
package com.kispoko.tome.component;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;

import java.util.Map;



/**
 * Component
 *
 */
public abstract class Component
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String name;
    private String label;


    // > INTERFACE
    // ------------------------------------------------------------------------------------------

    abstract public View getView(Context context);


    // > SHARED METHODS
    // ------------------------------------------------------------------------------------------

    public Component(String name)
    {
        this.name = name;
        this.label = null;
    }


    public Component(String name, String label)
    {
        this.name = name;
        this.label = label;
    }


    public boolean hasLabel()
    {
        return this.label != null;
    }


    public String getName()
    {
        return this.name;
    }


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

        int padding = (int) context.getResources().getDimension(R.dimen.label_padding);
        textView.setPadding(padding, 0, 0, 0);

        textView.setText(this.label.toUpperCase());

        return textView;
    }


    // > STATIC METHODS
    // ------------------------------------------------------------------------------------------

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


}
