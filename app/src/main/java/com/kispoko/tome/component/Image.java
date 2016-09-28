
package com.kispoko.tome.component;


import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;

import java.io.Serializable;
import java.util.Map;



/**
 * Image
 */
public class Image implements Serializable, ComponentI
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    String label;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Image(String label)
    {
        this.label = label;
    }


    public static Image fromYaml(Map<String, Object> imageYaml)
    {
        Map<String, Object> dataYaml = (Map<String, Object>) imageYaml.get("data");

        String label = (String) dataYaml.get("label");

        return new Image(label);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    public static View getView(Context context)
    {
        LinearLayout mainLayout = Component.linearLayout(context);
        int cardPadding = (int) context.getResources().getDimension(R.dimen.component_card_padding);
        mainLayout.setPadding(cardPadding, cardPadding, cardPadding, cardPadding);

        TextView labelView = Component.labelView(context);

        LinearLayout buttonLayout = Component.linearLayout(context);
        buttonLayout.setGravity(Gravity.CENTER);
        Button choosePictureButton = new Button(context);
        choosePictureButton.setText("Choose a Picture!");
        choosePictureButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_camera, 0, 0, 0);
        choosePictureButton.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                              LinearLayout.LayoutParams.WRAP_CONTENT));
        buttonLayout.addView(choosePictureButton);


        mainLayout.addView(labelView);
        mainLayout.addView(buttonLayout);

        return mainLayout;
    }


    public void configureView(View view)
    {
        TextView labelView = (TextView) view.findViewById(R.id.component_label);
        labelView.setText(this.label.toUpperCase());
    }

}
