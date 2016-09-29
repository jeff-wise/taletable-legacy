
package com.kispoko.tome.component;


import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.LinkAddress;
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

    private String name;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Image(String name)
    {
        this.name = name;
    }


    public static Image fromYaml(Map<String, Object> imageYaml)
    {
        //Map<String, Object> dataYaml = (Map<String, Object>) imageYaml.get("data");

        String name = (String) imageYaml.get("name");

        return new Image(name);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    public String getName()
    {
        return this.name;
    }


    public View getView(Context context)
    {
        LinearLayout buttonLayout = Component.linearLayout(context);
        buttonLayout.setGravity(Gravity.CENTER);

        Button choosePictureButton = new Button(context);

        choosePictureButton.setText("Choose a Picture!");

        choosePictureButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_camera, 0, 0, 0);

        LinearLayout.LayoutParams buttonLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                              LinearLayout.LayoutParams.WRAP_CONTENT);
        int buttonMargin = (int) context.getResources().getDimension(R.dimen.choose_image_button_margins);

        buttonLayoutParams.setMargins(buttonMargin, buttonMargin, buttonMargin, buttonMargin);

        choosePictureButton.setLayoutParams(buttonLayoutParams);

        buttonLayout.addView(choosePictureButton);

        return buttonLayout;
    }

}
