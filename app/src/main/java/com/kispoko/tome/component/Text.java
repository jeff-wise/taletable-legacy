
package com.kispoko.tome.component;


import android.content.Context;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    String label;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Text(String label)
    {
        this.label = label;
    }


    @SuppressWarnings("unchecked")
    public static Text fromYaml(Map<String, Object> textYaml)
    {
        Map<String, Object> dataYaml = (Map<String, Object>) textYaml.get("data");

        String label = (String) dataYaml.get("label");

        return new Text(label);
    }

    // > API
    // ------------------------------------------------------------------------------------------

    public static View getView(Context context)
    {
        LinearLayout layout = Component.linearLayout(context);

        int cardPadding = (int) context.getResources().getDimension(R.dimen.component_card_padding);
        layout.setPadding(cardPadding, cardPadding, cardPadding, cardPadding);

        TextView labelView = Component.labelView(context);

        EditText editText = new EditText(context);
        float textSize = (int) context.getResources()
                                      .getDimension(R.dimen.text_component_text_size);
        editText.setTextSize(textSize);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);


        Typeface font = Typeface.createFromAsset(context.getAssets(),
                                                 "fonts/DavidLibre-Regular.ttf");
        editText.setTypeface(font);

        layout.addView(labelView);
        layout.addView(editText);

        return layout;
    }


    public void configureView(View view)
    {
        TextView labelView = (TextView) view.findViewById(R.id.component_label);
        labelView.setText(this.label.toUpperCase());
    }
}
