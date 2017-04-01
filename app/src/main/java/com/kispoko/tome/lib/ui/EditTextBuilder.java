
package com.kispoko.tome.lib.ui;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.kispoko.tome.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.kispoko.tome.R.id.textView;


/**
 * Edit View Builder
 */
public class EditTextBuilder implements ViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public Integer          id;

    public LayoutType       layoutType;

    public Integer          height;
    public Integer          width;
    public Float            weight;
    public Integer          minHeight;
    public Float            minHeightDp;

    public Integer          gravity;
    public Integer          layoutGravity;

    public String           text;

    public Padding          padding;
    public Margins          margin;

    public Integer          size;
    public Float            sizeSp;

    public Integer          color;
    public Typeface         font;

    public Integer          backgroundColor;
    public Integer          backgroundResource;
    public Integer          underlineColor;

    public String           hint;

    public List<Integer>    rules;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public EditTextBuilder()
    {
        this.id                 = null;

        this.layoutType         = LayoutType.LINEAR;

        this.height             = null;
        this.width              = null;
        this.weight             = null;
        this.minHeight          = null;
        this.minHeightDp        = null;

        this.gravity            = null;
        this.layoutGravity      = null;

        this.text               = null;

        this.padding            = new Padding();
        this.margin             = new Margins();

        this.size               = null;
        this.sizeSp             = null;

        this.color              = null;
        this.font               = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;
        this.underlineColor     = null;

        this.hint               = null;

        this.rules              = new ArrayList<>();
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Attributes
    // ------------------------------------------------------------------------------------------

    public EditTextBuilder addRule(int verb)
    {
        this.rules.add(verb);
        return this;
    }


    public View view(Context context)
    {
        return this.editText(context);
    }


    public EditText editText(Context context)
    {
        EditText editText = new EditText(context);

        // [1] Text View
        // --------------------------------------------------------------------------------------

        // > Id
        // --------------------------------------------------------------------------------------

        if (this.id != null)
            editText.setId(this.id);

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.gravity != null)
            editText.setGravity(this.gravity);

        // > Min Height
        // --------------------------------------------------------------------------------------

        if (this.minHeight != null)
            editText.setMinHeight((int) context.getResources().getDimension(this.minHeight));

        // > Min Height Dp
        // --------------------------------------------------------------------------------------

        if (this.minHeightDp != null)
            editText.setMinHeight(Util.dpToPixel(this.minHeightDp));

        // > Padding
        // --------------------------------------------------------------------------------------

        editText.setPadding(this.padding.left(context),
                            this.padding.top(context),
                            this.padding.right(context),
                            this.padding.bottom(context));

        // > Size
        // --------------------------------------------------------------------------------------

        if (this.size != null)
            editText.setTextSize(context.getResources().getDimension(this.size));

        // > Size SP
        // --------------------------------------------------------------------------------------

        if (this.sizeSp != null)
            editText.setTextSize(this.sizeSp);

        // > Color
        // --------------------------------------------------------------------------------------

        if (this.color != null)
            editText.setTextColor(ContextCompat.getColor(context, this.color));

        // > Underline Color
        // --------------------------------------------------------------------------------------

        if (this.underlineColor != null)
            editText.getBackground().setColorFilter(
                    ContextCompat.getColor(context, this.underlineColor), PorterDuff.Mode.SRC_ATOP);

        // > Font
        // --------------------------------------------------------------------------------------

        if (this.font != null)
            editText.setTypeface(this.font);

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
            editText.setBackgroundColor(ContextCompat.getColor(context, this.backgroundColor));

        // > Background Resource
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null && this.backgroundColor != null) {
            Drawable bgDrawable = ContextCompat.getDrawable(context, this.backgroundResource);
            int      color      = ContextCompat.getColor(context, this.backgroundColor);
            bgDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            editText.setBackground(bgDrawable);
        }
        else if (this.backgroundResource != null) {
            editText.setBackgroundResource(this.backgroundResource);
        }

        // > Text
        // --------------------------------------------------------------------------------------

        if (this.text != null)
            editText.setText(this.text);

        // > Hint
        // --------------------------------------------------------------------------------------

        if (this.hint != null)
            editText.setHint(this.hint);


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

        // > Weight
        // --------------------------------------------------------------------------------------

        if (this.weight != null)
            layoutParamsBuilder.setWeight(this.weight);

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.layoutGravity != null)
            layoutParamsBuilder.setGravity(this.layoutGravity);

        // > Margins
        // --------------------------------------------------------------------------------------

        layoutParamsBuilder.setMargins(this.margin);

        // > Rules (Relative Layout Only)
        // --------------------------------------------------------------------------------------

        layoutParamsBuilder.setRules(this.rules);


        ViewGroup.LayoutParams layoutParams = layoutParamsBuilder.layoutParams();

        editText.setLayoutParams(layoutParams);


        return editText;
    }

}
