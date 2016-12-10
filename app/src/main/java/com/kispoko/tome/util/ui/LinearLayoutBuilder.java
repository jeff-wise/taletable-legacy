
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.kispoko.tome.R.id.textView;


/**
 * Linear Layout Builder
 *
 * Convenience class for creating Linear Layouts
 */
public class LinearLayoutBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Layout State
    // ------------------------------------------------------------------------------------------

    public LinearLayoutOrientation orientation;

    public Integer                 id;

    public Integer                 height;
    public Integer                 width;

    public Integer                 gravity;
    public Integer                 layoutGravity;

    public Integer                 backgroundColor;
    public Integer                 backgroundResource;

    public Margins                 margin;
    public Padding                 padding;

    public View.OnClickListener    onClick;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private List<View>             children;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public LinearLayoutBuilder()
    {
        this.id                 = null;

        this.orientation        = null;

        this.height             = null;
        this.width              = null;

        this.gravity            = null;
        this.layoutGravity      = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;

        this.margin             = new Margins();
        this.padding            = new Padding();

        this.onClick            = null;

        this.children           = new ArrayList<>();
    }


    // API
    // ------------------------------------------------------------------------------------------

    public LinearLayoutBuilder child(View childView)
    {
        this.children.add(childView);
        return this;
    }


    public LinearLayout linearLayout(Context context)
    {
        LinearLayout linearLayout = new LinearLayout(context);

        // [1] Layout
        // --------------------------------------------------------------------------------------

        // > Id
        // --------------------------------------------------------------------------------------

        if (this.id != null)
            linearLayout.setId(this.id);

        // > Orientation
        // --------------------------------------------------------------------------------------

        if (this.orientation != null)
        {
            switch (this.orientation)
            {
            case HORIZONTAL:
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                break;
            case VERTICAL:
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                break;
            }
        }

        // > Padding
        // --------------------------------------------------------------------------------------

        linearLayout.setPadding(this.padding.left(context),
                                this.padding.top(context),
                                this.padding.right(context),
                                this.padding.bottom(context));

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.gravity != null)
            linearLayout.setGravity(this.gravity);


        // > On Click Listener
        // --------------------------------------------------------------------------------------

        if (this.onClick != null)
            linearLayout.setOnClickListener(this.onClick);

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
            linearLayout.setBackgroundColor(ContextCompat.getColor(context, this.backgroundColor));

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null)
            linearLayout.setBackgroundResource(this.backgroundResource);


        // [2] Layout Parameters
        // --------------------------------------------------------------------------------------

        LinearLayout.LayoutParams linearLayoutParams = Util.linearLayoutParamsMatch();
        linearLayout.setLayoutParams(linearLayoutParams);

        // > Height
        // --------------------------------------------------------------------------------------

        if (this.height != null)
            linearLayoutParams.height = this.height;
        else
            linearLayoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;

        // > Width
        // --------------------------------------------------------------------------------------

        if (this.width != null)
            linearLayoutParams.width = this.width;
        else
            linearLayoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.layoutGravity != null)
            linearLayoutParams.gravity = this.layoutGravity;

        // > Margins
        // --------------------------------------------------------------------------------------

        linearLayoutParams.setMargins(this.margin.left(context),
                                      this.margin.top(context),
                                      this.margin.right(context),
                                      this.margin.bottom(context));

        // [3] Children
        // --------------------------------------------------------------------------------------

        for (View childView : this.children)
        {
            linearLayout.addView(childView);
        }

        return linearLayout;
    }


}
