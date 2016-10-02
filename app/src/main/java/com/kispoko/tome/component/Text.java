
package com.kispoko.tome.component;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.kispoko.tome.R;

import java.io.Serializable;
import java.util.Map;


/**
 * Text
 */
public class Text extends Component implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String name;
    private String value;
    private String label;

    private TextSize textSize;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Text(String name, TextSize textSize)
    {
        super(name);
        this.textSize = textSize;
        this.value = "";
    }


    public Text(String name, TextSize textSize, String label)
    {
        super(name, label);
        this.textSize = textSize;
        this.value = "";
    }


    // TODO allow integer values as strings here too
    @SuppressWarnings("unchecked")
    public static Text fromYaml(Map<String, Object> textYaml)
    {
        String name = (String) textYaml.get("name");

        String label = null;
        if (textYaml.containsKey("label"))
            label = (String) textYaml.get("label");

        TextSize textSize = Component.TextSize.fromString((String) textYaml.get("size"));

        Map<String, Object> dataYaml = (Map<String, Object>) textYaml.get("data");

        String value = null;
        if (dataYaml != null && dataYaml.containsKey("value"))
            value = (String) dataYaml.get("value");

        Text newText;
        if (label == null)
            newText = new Text(name, textSize);
        else
            newText = new Text(name, textSize, label);

        if (value != null)
            newText.setValue(value);

        return newText;
    }


    // > API
    // ------------------------------------------------------------------------------------------


    public void setValue(String value)
    {
        this.value = value;
    }


    public void setTextSize(TextSize textSize)
    {
        this.textSize = textSize;
    }


    public View getView(Context context)
    {
        EditText editText = new EditText(context);

        float textSize = 0;
        switch (this.textSize) {
            case SMALL:
                textSize = context.getResources()
                                  .getDimension(R.dimen.comp_text_text_size_small);
                break;
            case MEDIUM:
                textSize = context.getResources()
                                  .getDimension(R.dimen.comp_text_text_size_medium);
                break;
            case LARGE:
                textSize = context.getResources()
                                  .getDimension(R.dimen.comp_text_text_size_large);
                break;
        }
        editText.setTextSize(textSize);

        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        Typeface font = Typeface.createFromAsset(context.getAssets(),
                                                 "fonts/DavidLibre-Regular.ttf");
        editText.setTypeface(font);
        editText.setTextColor(ContextCompat.getColor(context, R.color.text_medium));

        editText.setText(this.value);

        return editText;
    }

}
