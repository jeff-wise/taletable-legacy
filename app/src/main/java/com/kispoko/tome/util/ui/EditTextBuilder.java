
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import static com.kispoko.tome.R.id.textView;


/**
 * Edit View Builder
 */
public class EditTextBuilder implements ViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public Integer      id;

    public LayoutType   layoutType;

    public Integer      height;
    public Integer      width;
    public Float        weight;
    public Integer      minHeight;

    public Integer      gravity;
    public Integer      layoutGravity;

    public String       text;

    public Padding      padding;
    public Margins      margin;

    public Integer      size;
    public Integer      color;
    public Typeface     font;

    public Integer      backgroundColor;
    public Integer      backgroundResource;


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

        this.gravity            = null;
        this.layoutGravity      = null;

        this.text               = null;

        this.padding            = new Padding();
        this.margin             = new Margins();

        this.size               = null;
        this.color              = null;
        this.font               = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;
    }


    // API
    // ------------------------------------------------------------------------------------------


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

        // > Color
        // --------------------------------------------------------------------------------------

        if (this.color != null)
            editText.setTextColor(ContextCompat.getColor(context, this.color));

        // > Background Resource
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null)
            editText.setBackgroundResource(this.backgroundResource);

        // > Font
        // --------------------------------------------------------------------------------------

        if (this.font != null)
            editText.setTypeface(this.font);

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
            editText.setBackgroundColor(ContextCompat.getColor(context, this.backgroundColor));

        // > Text
        // --------------------------------------------------------------------------------------

        if (this.text != null)
            editText.setText(this.text);

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


        switch (this.layoutType)
        {
            case LINEAR:
                editText.setLayoutParams(layoutParamsBuilder.linearLayoutParams());
                break;
            case RELATIVE:
                editText.setLayoutParams(layoutParamsBuilder.relativeLayoutParams());
                break;
            case TABLE:
                editText.setLayoutParams(layoutParamsBuilder.tableLayoutParams());
                break;
            case TABLE_ROW:
                editText.setLayoutParams(layoutParamsBuilder.tableRowLayoutParams());
                break;
        }


        return editText;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private boolean isLayoutConstant(Integer constant)
    {
        if (constant == LinearLayout.LayoutParams.MATCH_PARENT ||
            constant == LinearLayout.LayoutParams.WRAP_CONTENT) {
            return true;
        }
        else {
            return false;
        }
    }


}
