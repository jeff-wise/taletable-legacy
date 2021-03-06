
package com.taletable.android.lib.ui;


import android.content.Context;
import androidx.appcompat.widget.SwitchCompat;
import android.view.View;

import java.util.ArrayList;
import java.util.List;



/**
 * Switch Builder
 */
public class SwitchBuilder implements ViewBuilder
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    public Integer          width;
    public Integer          height;

    public LayoutType       layoutType;

    public Padding          padding;
    public Margins          margin;

    public Boolean          checked;
    public String           text;
    public String           onText;
    public String           offText;

    public Float            scaleX;
    public Float            scaleY;

    public List<Integer>    rules;

    private Integer         styleId;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public SwitchBuilder()
    {
        initialize(null);
    }


    public SwitchBuilder(Integer styleId)
    {
        initialize(null);
    }


    private void initialize(Integer styleId)
    {
        this.width              = null;
        this.height             = null;

        this.layoutType         = LayoutType.LINEAR;

        this.padding            = new Padding();
        this.margin             = new Margins();

        this.checked            = null;
        this.text               = null;
        this.onText             = null;
        this.offText            = null;

        this.styleId            = styleId;

        this.scaleX             = null;
        this.scaleY             = null;

        this.rules              = new ArrayList<>();
    }



    // API
    // -----------------------------------------------------------------------------------------

    // > Attributes
    // ------------------------------------------------------------------------------------------

    public SwitchBuilder addRule(int verb) {
        this.rules.add(verb);
        return this;
    }


    // > View Builder
    // ------------------------------------------------------------------------------------------

    public View view(Context context) {
        return this.switchView(context);
    }


    // > Text View
    // ------------------------------------------------------------------------------------------

    public SwitchCompat switchView(Context context)
    {
        SwitchCompat switchView;
        if (this.styleId != null)
            switchView = new SwitchCompat(context, null, this.styleId);
        else
            switchView = new SwitchCompat(context);

        // [1] Switch View
        // -------------------------------------------------------------------------------------

        // > Checked
        // -------------------------------------------------------------------------------------

        if (this.checked != null)
            switchView.setChecked(this.checked);

        // > Text
        // -------------------------------------------------------------------------------------

        if (this.text != null)
            switchView.setText(this.text);

        // > On Text
        // -------------------------------------------------------------------------------------

        if (this.onText != null)
            switchView.setTextOn(this.onText);

        // > Off Text
        // -------------------------------------------------------------------------------------

        if (this.offText != null)
            switchView.setTextOff(this.offText);

        // > Scale X
        // -------------------------------------------------------------------------------------

        if (this.scaleX != null)
            switchView.setScaleX(this.scaleX);

        // > Scale Y
        // -------------------------------------------------------------------------------------

        if (this.scaleY != null)
            switchView.setScaleY(this.scaleY);


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


        switchView.setLayoutParams(layoutParamsBuilder.layoutParams());


        return switchView;
    }


}
