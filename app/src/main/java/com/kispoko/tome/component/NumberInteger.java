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
 * NumberInteger Component
 */
public class NumberInteger implements Serializable, ComponentI
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String name;
    private Integer value;
    private String label;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberInteger(String name)
    {
        this.name = name;
        this.value = null;
        this.label = null;
    }


    public NumberInteger(String name, String label)
    {
        this.name = name;
        this.label = label;
        this.value = null;
    }


    @SuppressWarnings("unchecked")
    public static NumberInteger fromYaml(Map<String, Object> integerYaml)
    {
        String name = (String) integerYaml.get("name");

        String label = null;
        if (integerYaml.containsKey("label"))
            label = (String) integerYaml.get("label");

        Map<String, Object> dataYaml = (Map<String, Object>) integerYaml.get("data");

        Integer value = null;
        if (dataYaml != null && dataYaml.containsKey("value"))
            value = (Integer) dataYaml.get("value");

        NumberInteger newInteger;
        if (label != null)
            newInteger = new NumberInteger(name, label);
        else
            newInteger = new NumberInteger(name);

        if (value != null)
            newInteger.setValue(value);

        return newInteger;
    }


    // > API
    // ------------------------------------------------------------------------------------------

    public String getName()
    {
        return this.name;
    }


    public void setValue(Integer value)
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

        if (this.value != null)
            editText.setText(Integer.toString(this.value));
        else
            editText.setText("");

        return editText;
    }

}
