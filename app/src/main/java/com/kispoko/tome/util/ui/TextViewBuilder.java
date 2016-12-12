
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.util.Util;



/**
 * Text View Builder
 */
public class TextViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public Integer              id;

    public Integer              height;
    public Integer              width;

    public Integer              gravity;
    public Integer              layoutGravity;

    public String               text;

    public Padding              padding;

    public Integer              size;
    public Integer              color;
    public Typeface             font;

    public Integer              backgroundColor;
    public Integer              backgroundResource;

    public View.OnClickListener onClick;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextViewBuilder()
    {
        this.id                 = null;

        this.height             = null;
        this.width              = null;

        this.gravity            = null;
        this.layoutGravity      = null;

        this.text               = null;

        this.padding            = new Padding();

        this.size               = null;
        this.color              = null;
        this.font               = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;

        this.onClick            = null;
    }


    // API
    // ------------------------------------------------------------------------------------------

    public TextView textView(Context context)
    {
        TextView textView = new TextView(context);

        // [1] Text View
        // --------------------------------------------------------------------------------------

        // > Id
        // --------------------------------------------------------------------------------------

        if (this.id != null)
            textView.setId(this.id);

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.gravity != null)
            textView.setGravity(this.gravity);

        // > Padding
        // --------------------------------------------------------------------------------------

        textView.setPadding(this.padding.left(context),
                            this.padding.top(context),
                            this.padding.right(context),
                            this.padding.bottom(context));

        // > On Click Listener
        // --------------------------------------------------------------------------------------

        if (this.onClick != null)
            textView.setOnClickListener(this.onClick);

        // > Size
        // --------------------------------------------------------------------------------------

        if (this.size != null)
            textView.setTextSize(context.getResources().getDimension(this.size));

        // > Color
        // --------------------------------------------------------------------------------------

        if (this.color != null)
            textView.setTextColor(ContextCompat.getColor(context, this.color));

        // > Font
        // --------------------------------------------------------------------------------------

        if (this.font != null)
            textView.setTypeface(this.font);

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
            textView.setBackgroundColor(ContextCompat.getColor(context, this.backgroundColor));

        // > Background Resource
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null)
            textView.setBackgroundResource(this.backgroundResource);

        // > Text
        // --------------------------------------------------------------------------------------

        if (this.text != null)
            textView.setText(this.text);

        // [2] Layout
        // --------------------------------------------------------------------------------------

        LinearLayout.LayoutParams textViewLayoutParams = Util.linearLayoutParamsMatch();
        textView.setLayoutParams(textViewLayoutParams);

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.layoutGravity != null)
            textViewLayoutParams.gravity = this.layoutGravity;

        // > Height
        // --------------------------------------------------------------------------------------

        if (this.height != null)
        {
            if (isLayoutConstant(this.height)) {
                textViewLayoutParams.height = this.height;
            }
            else {
                textViewLayoutParams.height = (int) context.getResources()
                                                           .getDimension(this.height);
            }
        }

        // > Width
        // --------------------------------------------------------------------------------------

        if (this.width != null)
        {
            if (isLayoutConstant(this.width)) {
                textViewLayoutParams.width = this.width;
            }
            else {
                textViewLayoutParams.width = (int) context.getResources()
                                                          .getDimension(this.width);
            }
        }

        return textView;
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
