
package com.kispoko.tome.component;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;

import java.io.Serializable;
import java.util.Map;

import static android.R.attr.label;


/**
 * Component
 */
public class Component implements Serializable
{

    public static enum Type
    {
        TEXT,
        IMAGE
    }

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    ComponentI component;
    Type _type;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Component(ComponentI component)
    {
        this.component = component;

        if (component instanceof Text) {
           this._type = Type.TEXT;
        } else if (component instanceof Image) {
           this._type = Type.IMAGE;
        }
    }


    public static Component fromYaml(Map<String, Object> componentYaml)
    {
        String componentType = (String) componentYaml.get("type");
        ComponentI componentI = null;

        switch (componentType)
        {
            case "text":
               componentI = Text.fromYaml(componentYaml);
               break;
            case "image":
                componentI = Image.fromYaml(componentYaml);
                break;
        }

        return new Component(componentI);
    }

    // > API
    // ------------------------------------------------------------------------------------------


    public Type getType()
    {
        return this._type;
    }

//    public View getView(Context context)
//    {
//        FrameLayout cardView = this.cardView(context);
//        View componentView = this.component.getView(context);
//        cardView.addView(componentView);
//        return cardView;
//    }



    public static TextView labelView(Context context)
    {
        TextView textView = new TextView(context);
        float labelTextSize = (int) context.getResources()
                                         .getDimension(R.dimen.label_text_size);
        textView.setTextSize(labelTextSize);
        textView.setId(R.id.component_label);
        textView.setTextColor(ContextCompat.getColor(context, R.color.theme_gray_500));
        textView.setTypeface(null, Typeface.BOLD);
        int padding = (int) context.getResources().getDimension(R.dimen.label_padding);
        textView.setPadding(padding, padding, padding, padding);

        return textView;
    }


    public static LinearLayout linearLayout(Context context)
    {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(linearLayoutParams);
        return layout;
    }


    public void configureView(View view)
    {
        this.component.configureView(view);
    }


    // > INTERNAL
    // ------------------------------------------------------------------------------------------


    private FrameLayout cardView(Context context)
    {
        CardView cardView = new CardView(context);
        cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.theme_light));

//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(0, 0, 0, 50);

        //cardView.setLayoutParams(layoutParams);
//        cardView.setMaxCardElevation(20.0f);
//        cardView.setCardElevation(15.0f);
//        cardView.setRadius(10.0f);
//
//        cardView.setUseCompatPadding(true);
//        cardView.setPreventCornerOverlap(true);

        return cardView;
    }


}
