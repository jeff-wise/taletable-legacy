
package com.kispoko.tome.sheet.component;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.Component;

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

    public Document(String name, String typeName)
    {
        super(name, typeName);
        this.value = "";
    }


    public Document(String name, String typeName, String label)
    {
        super(name, typeName, label);
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
            newDocument = new Document(name, null, label);
        else
            newDocument =  new Document(name, null);

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


    public View getDisplayView(Context context)
    {
        TextView textView = new TextView(context);

        float textSize = (int) context.getResources()
                                      .getDimension(R.dimen.document_component_text_size);
        textView.setTextSize(textSize);

        Typeface font = Typeface.createFromAsset(context.getAssets(),
                                                 "fonts/DavidLibre-Regular.ttf");
        textView.setTypeface(font);
        textView.setTextColor(ContextCompat.getColor(context, R.color.text));

        textView.setText(this.value);

        return textView;
    }


    public View getEditorView(Context context)
    {
        return new LinearLayout(context);
    }

}
