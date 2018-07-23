
package com.taletable.android.lib.ui;


import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;



public class CheckboxBuilder implements ViewBuilder
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    public Integer          width;
    public Integer          height;

    public LayoutType       layoutType;

    public Padding          padding;
    public Margins          margin;

    public String           text;

    public Boolean          checked;

    public List<Integer> rules;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public CheckboxBuilder()
    {
        this.width              = null;
        this.height             = null;

        this.layoutType         = LayoutType.LINEAR;

        this.padding            = new Padding();
        this.margin             = new Margins();

        this.text               = null;

        this.checked            = false;

        this.rules              = new ArrayList<>();
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Attributes
    // ------------------------------------------------------------------------------------------

    public CheckboxBuilder addRule(int verb) {
        this.rules.add(verb);
        return this;
    }


    // > View Builder
    // ------------------------------------------------------------------------------------------

    public View view(Context context) {
        return this.checkboxView(context);
    }


    // > Text View
    // ------------------------------------------------------------------------------------------

    public CheckBox checkboxView(Context context)
    {
        CheckBox checkbox = new CheckBox(context);

        // [1] Switch View
        // -------------------------------------------------------------------------------------

        // > Checked
        // -------------------------------------------------------------------------------------

        if (this.checked != null)
            checkbox.setChecked(this.checked);

        // > Text
        // -------------------------------------------------------------------------------------

        if (this.text != null)
            checkbox.setText(this.text);


        // [2] Layout
        // --------------------------------------------------------------------------------------

        LayoutParamsBuilder layoutParamsBuilder;
        layoutParamsBuilder = new LayoutParamsBuilder(this.layoutType, context);

        // > Width
        // --------------------------------------------------------------------------------------

        if (this.width != null)
            layoutParamsBuilder.setWidth(this.width);

        // > Height
        // --------------------------------------------------------------------------------------

        if (this.height != null)
            layoutParamsBuilder.setHeight(this.height);

        // > Rules (Relative Layout Only)
        // --------------------------------------------------------------------------------------

        layoutParamsBuilder.setRules(this.rules);


        checkbox.setLayoutParams(layoutParamsBuilder.layoutParams());


        return checkbox;
    }


}
