
package com.kispoko.tome.component;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

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
    private Integer value;
    private String label;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberInteger(String name)
    {
        super(name);
        this.label = null;
    }


    public NumberInteger(String name, String label)
    {
        super(name, label);
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


    public void setValue(Integer value)
    {
        this.value = value;
    }


    public View getView(Context context)
    {
        EditText editText = new EditText(context);

        float textSize = (int) context.getResources()
                                      .getDimension(R.dimen.comp_text_text_size_medium);
        editText.setTextSize(textSize);

        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        Typeface font = Typeface.createFromAsset(context.getAssets(),
                                                 "fonts/DavidLibre-Regular.ttf");
        editText.setTypeface(font);
        editText.setTextColor(ContextCompat.getColor(context, R.color.text));

        if (this.value != null)
            editText.setText(Integer.toString(this.value));
        else
            editText.setText("");

        return editText;
    }

}
