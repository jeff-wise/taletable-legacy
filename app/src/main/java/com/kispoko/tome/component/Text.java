
package com.kispoko.tome.component;


import android.content.Context;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.kispoko.tome.R;

import java.io.Serializable;
import java.util.Map;


/**
 * Text
 */
public class Text implements Serializable, ComponentI
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String name;
    private String value;
    private String label;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Text(String name)
    {
        this.name = name;
        this.value = "";
        this.label = null;
    }


    public Text(String name, String value)
    {
        this.name = name;
        this.value = value;
        this.label = null;
    }


    public Text(String name, String value, String label)
    {
        this.name = name;
        this.value = value;
        this.label = label;
    }


    // TODO allow integer values as strings here too
    @SuppressWarnings("unchecked")
    public static Text fromYaml(Map<String, Object> textYaml)
    {
        String name = (String) textYaml.get("name");

        String label = null;
        if (textYaml.containsKey("label"))
            label = (String) textYaml.get("label");

        Map<String, Object> dataYaml = (Map<String, Object>) textYaml.get("data");

        String value = null;
        if (dataYaml != null && dataYaml.containsKey("value"))
            value = (String) dataYaml.get("value");

        Text newText;
        if (label != null)
            newText = new Text(name, label);
        else
            newText = new Text(name);

        if (value != null)
            newText.setValue(value);

        return newText;
    }


    // > API
    // ------------------------------------------------------------------------------------------


    public String getName()
    {
        return this.name;
    }


    public void setValue(String value)
    {
        this.value = value;
    }


    public View getView(Context context)
    {
        EditText editText = new EditText(context);

        editText.setId(R.id.text_component_edit_text);

        float textSize = (int) context.getResources()
                                      .getDimension(R.dimen.text_component_text_size);
        editText.setTextSize(textSize);

        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        Typeface font = Typeface.createFromAsset(context.getAssets(),
                                                 "fonts/DavidLibre-Regular.ttf");
        editText.setTypeface(font);

        editText.setText(this.value);

        return editText;
    }

}
