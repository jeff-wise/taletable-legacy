
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kispoko.tome.util.Util;



/**
 * Edit View Builder
 */
public class EditTextBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public Integer  id;

    public Integer  height;
    public Integer  width;
    public Integer  minHeight;

    public Integer  gravity;

    public String   text;

    public Padding  padding;

    public Integer  size;
    public Integer  color;
    public Typeface font;

    public Integer  backgroundColor;
    public Integer  backgroundResource;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public EditTextBuilder()
    {
        this.id                 = null;

        this.height             = null;
        this.width              = null;
        this.minHeight          = null;

        this.gravity            = null;

        this.text               = null;

        this.padding            = new Padding();

        this.size               = null;
        this.color              = null;
        this.font               = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;
    }


    // API
    // ------------------------------------------------------------------------------------------

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

        LinearLayout.LayoutParams editTextLayoutParams = Util.linearLayoutParamsMatch();
        editText.setLayoutParams(editTextLayoutParams);

        // > Height
        // --------------------------------------------------------------------------------------

        if (this.height != null)
        {
            if (isLayoutConstant(this.height)) {
                editTextLayoutParams.height = this.height;
            }
            else {
                editTextLayoutParams.height = (int) context.getResources()
                                                           .getDimension(this.height);
            }
        }

        // > Width
        // --------------------------------------------------------------------------------------

        if (this.width != null)
        {
            if (isLayoutConstant(this.width)) {
                editTextLayoutParams.width = this.width;
            }
            else {
                editTextLayoutParams.width = (int) context.getResources()
                                                          .getDimension(this.width);
            }
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
