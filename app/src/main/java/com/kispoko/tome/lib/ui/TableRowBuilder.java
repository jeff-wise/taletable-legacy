
package com.kispoko.tome.lib.ui;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableRow;

import com.kispoko.tome.model.sheet.style.Spacing;
import com.kispoko.tome.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.kispoko.tome.R.id.textView;


/**
 * Table Row Builder
 */
public class TableRowBuilder implements ViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Layout State
    // ------------------------------------------------------------------------------------------

    public Integer                  id;

    public Integer                  height;
    public Integer                  heightDp;

    public Integer                  width;

    public Integer                  visibility;

    public LayoutType               layoutType;

    public Integer                  gravity;
    public Integer                  layoutGravity;

    public Integer                  backgroundColor;
    public Integer                  backgroundResource;

    public Margins                  margin;
    public Spacing                  marginSpacing;
    public Padding                  padding;
    public Spacing                  paddingSpacing;

    public View.OnClickListener     onClick;
    public View.OnLongClickListener onLongClick;
    public View.OnTouchListener     onTouch;

    public List<View>               rows;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TableRowBuilder()
    {
        this.id                 = null;

        this.height             = null;
        this.heightDp           = null;

        this.width              = null;

        this.visibility         = null;

        this.layoutType         = LayoutType.TABLE;

        this.gravity            = null;
        this.layoutGravity      = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;

        this.margin             = new Margins();
        this.marginSpacing      = null;
        this.padding            = new Padding();
        this.paddingSpacing     = null;

        this.onClick            = null;
        this.onLongClick        = null;
        this.onTouch            = null;

        this.rows               = new ArrayList<>();
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > View Builder
    // ------------------------------------------------------------------------------------------

    public View view(Context context)
    {
        return this.tableRow(context);
    }


    // > Layout
    // ------------------------------------------------------------------------------------------

    public TableRow tableRow(Context context)
    {
        TableRow tableRow = new TableRow(context);

        // [1] Layout
        // --------------------------------------------------------------------------------------

        // > Id
        // --------------------------------------------------------------------------------------

        if (this.id != null)
            tableRow.setId(this.id);

        // > Padding
        // --------------------------------------------------------------------------------------

        tableRow.setPadding(this.padding.left(context),
                            this.padding.top(context),
                            this.padding.right(context),
                            this.padding.bottom(context));

        // > Padding Spacing
        // --------------------------------------------------------------------------------------

        if (this.paddingSpacing != null)
        {
            tableRow.setPadding(this.paddingSpacing.leftPx(),
                                this.paddingSpacing.topPx(),
                                this.paddingSpacing.rightPx(),
                                this.paddingSpacing.bottomPx());
        }

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.gravity != null)
            tableRow.setGravity(this.gravity);

        // > Visibility
        // --------------------------------------------------------------------------------------

        if (this.visibility != null)
            tableRow.setVisibility(this.visibility);

        // > On Click Listener
        // --------------------------------------------------------------------------------------

        if (this.onClick != null)
            tableRow.setOnClickListener(this.onClick);

        // > On Long Click Listener
        // --------------------------------------------------------------------------------------

        if (this.onLongClick != null)
            tableRow.setOnLongClickListener(this.onLongClick);

        // > On Touch Listener
        // --------------------------------------------------------------------------------------

        if (this.onTouch != null)
            tableRow.setOnTouchListener(this.onTouch);

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
            tableRow.setBackgroundColor(this.backgroundColor);

        // > Background Resource
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null)
            tableRow.setBackgroundResource(this.backgroundResource);


        // [2] Layout Parameters
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

        if (this.heightDp != null) {
            layoutParamsBuilder.setHeightDp(this.heightDp);
        }

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.layoutGravity != null)
            layoutParamsBuilder.setGravity(this.layoutGravity);

        // > Margins
        // --------------------------------------------------------------------------------------

        if (this.marginSpacing != null)
            layoutParamsBuilder.setMargins(this.marginSpacing);
        else
            layoutParamsBuilder.setMargins(this.margin);


        tableRow.setLayoutParams(layoutParamsBuilder.layoutParams());


        // [3] Children
        // --------------------------------------------------------------------------------------
        for (View rowView : this.rows)
        {
            tableRow.addView(rowView);
        }


        return tableRow;
    }


}
