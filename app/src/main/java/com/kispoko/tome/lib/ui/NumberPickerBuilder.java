
package com.kispoko.tome.lib.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.view.View;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.List;



/**
 * Number Picker Builder
 */
public class NumberPickerBuilder
{


    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public LayoutType               layoutType;

    public Integer                  height;
    public Integer                  width;

    public Integer                  gravity;
    public Integer                  layoutGravity;

    public Integer                  dividerColor;
    public Integer                  dividerThickness;
    public Integer                  dividerSpacing;

    public Integer                  textColor;
    public Integer                  textSize;
    public Typeface                 textFont;

    public Integer                  minValue;
    public Integer                  maxValue;

    public Integer                  orientation;

    public Integer                  wheelItemCount;

    public List<Integer>            rules;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberPickerBuilder()
    {
        this.layoutType         = LayoutType.LINEAR;

        this.height             = null;
        this.width              = null;

        this.gravity            = null;
        this.layoutGravity      = null;

        this.dividerColor       = null;
        this.dividerThickness   = null;
        this.dividerSpacing     = null;

        this.textColor          = null;
        this.textSize           = null;
        this.textFont           = null;

        this.minValue           = null;
        this.maxValue           = null;

        this.orientation        = null;

        this.wheelItemCount     = null;

        this.rules              = new ArrayList<>();
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Attributes
    // ------------------------------------------------------------------------------------------

    public NumberPickerBuilder addRule(int verb)
    {
        this.rules.add(verb);
        return this;
    }


    // > View Builder
    // ------------------------------------------------------------------------------------------

    public View view(Context context)
    {
        return this.numberPicker(context);
    }


    // > Number Picker
    // ------------------------------------------------------------------------------------------

    public NumberPicker numberPicker(Context context)
    {
        NumberPicker numberPicker = new NumberPicker(context);


        // [1] Number Picker
        // --------------------------------------------------------------------------------------

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.gravity != null)
            numberPicker.setGravity(this.gravity);

        // > Divider Color
        // --------------------------------------------------------------------------------------

        if (this.dividerColor != null)
            numberPicker.setDividerColorResource(this.dividerColor);

        // > Divider Thickness
        // --------------------------------------------------------------------------------------

        if (this.dividerThickness != null)
            numberPicker.setDividerThickness(this.dividerThickness);

        // > Divider Spacing
        // --------------------------------------------------------------------------------------

        if (this.dividerSpacing != null)
            numberPicker.setDividerDistance(this.dividerSpacing);

        // > Text Color
        // --------------------------------------------------------------------------------------

        if (this.textColor != null)
            numberPicker.setTextColorResource(this.textColor);

        // > Text Szie
        // --------------------------------------------------------------------------------------

        if (this.textSize != null)
            numberPicker.setTextSize(this.textSize);

        // > Text Font
        // --------------------------------------------------------------------------------------

        if (this.textFont != null)
            numberPicker.setTypeface(this.textFont);

        // > Min Value
        // --------------------------------------------------------------------------------------

        if (this.minValue != null)
            numberPicker.setMinValue(this.minValue);

        // > Max Value
        // --------------------------------------------------------------------------------------

        if (this.maxValue != null)
            numberPicker.setMaxValue(this.maxValue);

        // > Orientation
        // --------------------------------------------------------------------------------------

        if (this.orientation != null)
            numberPicker.setOrientation(this.orientation);

        // > Wheel Item Count
        // --------------------------------------------------------------------------------------

        if (this.wheelItemCount != null)
            numberPicker.setWheelItemCount(this.wheelItemCount);


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

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.layoutGravity != null)
            layoutParamsBuilder.setGravity(this.layoutGravity);

        // > Rules (Relative Layout Only)
        // --------------------------------------------------------------------------------------

        layoutParamsBuilder.setRules(this.rules);


        numberPicker.setLayoutParams(layoutParamsBuilder.layoutParams());


        return numberPicker;
    }


}
