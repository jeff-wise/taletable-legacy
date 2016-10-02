
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
 * Document Component
 */
public class Document extends Component implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String name;
    private String value;
    private String label;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Document(String name)
    {
        super(name);
        this.value = "";
    }


    public Document(String name, String label)
    {
        super(name, label);
        this.value = "";
    }


    @SuppressWarnings("unchecked")
    public static Document fromYaml(Map<String, Object> documentYaml)
    {
        // Map<String, Object> dataYaml = (Map<String, Object>) documentYaml.get("data");

        String name = (String) documentYaml.get("name");

        String label = null;
        if (documentYaml.containsKey("label"))
            label = (String) documentYaml.get("label");

        String value = null;
        if (documentYaml.containsKey("value"))
            value = (String) documentYaml.get("value");

        Document newDocument;
        if (label != null)
            newDocument = new Document(name, label);
        else
            newDocument =  new Document(name);

        if (value != null)
            newDocument.setValue(value);

        return newDocument;
    }


    // > API
    // ------------------------------------------------------------------------------------------


    public void setValue(String value)
    {
        this.value = value;
    }


    public View getView(Context context)
    {
        EditText editText = new EditText(context);

        float textSize = (int) context.getResources()
                                      .getDimension(R.dimen.document_component_text_size);
        editText.setTextSize(textSize);

        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        Typeface font = Typeface.createFromAsset(context.getAssets(),
                                                 "fonts/DavidLibre-Regular.ttf");
        editText.setTypeface(font);
        editText.setTextColor(ContextCompat.getColor(context, R.color.text));

        editText.setText(this.value);

        return editText;
    }

}
