
package com.kispoko.tome.component;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;

import java.io.Serializable;
import java.util.Map;



/**
 * NumberInteger Component
 */
public class NumberInteger extends Component implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String name;
    private String label;

    private TextSize textSize;
    private String prefix;
    private Integer value;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberInteger(String name, String typeName)
    {
        super(name, typeName);
        this.label = null;
        this.textSize = TextSize.MEDIUM;
    }


    public NumberInteger(String name, String typeName, String label)
    {
        super(name, typeName, label);
        this.value = null;
        this.textSize = TextSize.MEDIUM;
    }


    @SuppressWarnings("unchecked")
    public static NumberInteger fromYaml(Map<String, Object> integerYaml)
    {
        // Parse all integer fields
        // >> Name
        String name = (String) integerYaml.get("name");

        // >> Label
        String label = null;
        if (integerYaml.containsKey("label"))
            label = (String) integerYaml.get("label");

        // >> Prefix
        String prefix = null;
        if (integerYaml.containsKey("prefix"))
            prefix = (String) integerYaml.get("prefix");

        // >> Text Size
        TextSize textSize = null;
        if (integerYaml.containsKey("size"))
            textSize = Component.TextSize.fromString((String) integerYaml.get("size"));

        Map<String, Object> dataYaml = (Map<String, Object>) integerYaml.get("data");

        // >> Value
        Integer value = null;
        String typeName = null;
        if (dataYaml != null)
        {
            // >> Value
            if (dataYaml.containsKey("value"))
                value = (Integer) dataYaml.get("value");

            // >> Type
            if (dataYaml.containsKey("type"))
                typeName = (String) dataYaml.get("type");
        }


        // Construct new integer
        NumberInteger newInteger;
        if (label != null)
            newInteger = new NumberInteger(name, typeName, label);
        else
            newInteger = new NumberInteger(name, typeName);

        // >> Set any given properties
        if (textSize != null)
            newInteger.setTextSize(textSize);

        if (prefix != null)
            newInteger.setPrefix(prefix);

        if (value != null)
            newInteger.setValue(value);

        return newInteger;
    }


    // > API
    // ------------------------------------------------------------------------------------------


    public void setValue(Integer value)
    {
        this.value = value;
    }


    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }


    public void setTextSize(TextSize textSize)
    {
        this.textSize = textSize;
    }


    public View getDisplayView(Context context)
    {
        // Create layout
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams);

        // Add prefix if exists
        if (this.prefix != null) {
            layout.addView(Util.prefixView(context, this.prefix, this.textSize));
        }

        // Add text view
        TextView textView = new TextView(context);

        textView.setTextSize(Util.getTextSizeSP(context, this.textSize));

        Typeface font = Typeface.createFromAsset(context.getAssets(),
                                                 "fonts/DavidLibre-Regular.ttf");
        textView.setTypeface(font);
        textView.setTextColor(ContextCompat.getColor(context, R.color.text_medium));

        if (this.value != null)
            textView.setText(Integer.toString(this.value));
        else
            textView.setText("");

        layout.addView(textView);

        return layout;
    }


    public View getEditorView(Context context)
    {
        return new LinearLayout(context);
    }

}
