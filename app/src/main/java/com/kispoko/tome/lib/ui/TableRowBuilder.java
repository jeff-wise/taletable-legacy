
package com.kispoko.tome.lib.ui;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.List;



/**
 * Table Row Builder
 */
public class TableRowBuilder implements ViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Layout State
    // ------------------------------------------------------------------------------------------

    public Integer                 id;

    public Integer                 height;
    public Integer                 width;

    public Integer                 visibility;

    public LayoutType              layoutType;

    public Integer                 gravity;
    public Integer                 layoutGravity;

    public Integer                 backgroundColor;
    public Integer                 backgroundResource;

    public Margins                 margin;
    public Padding                 padding;

    public View.OnClickListener    onClick;


    // > Internal
    // ------------------------------------------------------------------------------------------

    public List<ViewBuilder>       children;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TableRowBuilder()
    {
        this.id                 = null;

        this.height             = null;
        this.width              = null;

        this.visibility         = null;

        this.layoutType         = LayoutType.TABLE;

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

    // > Attributes
    // ------------------------------------------------------------------------------------------

    public TableRowBuilder child(ViewBuilder childViewBuilder)
    {
        this.children.add(childViewBuilder);
        return this;
    }


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

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
            tableRow.setBackgroundColor(ContextCompat.getColor(context, this.backgroundColor));

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
                tableRow.setLayoutParams(layoutParamsBuilder.linearLayoutParams());
                break;
            case RELATIVE:
                tableRow.setLayoutParams(layoutParamsBuilder.relativeLayoutParams());
                break;
            case TABLE:
                tableRow.setLayoutParams(layoutParamsBuilder.tableLayoutParams());
                break;
            case TABLE_ROW:
                tableRow.setLayoutParams(layoutParamsBuilder.tableRowLayoutParams());
                break;
        }


        // [3] Children
        // --------------------------------------------------------------------------------------
        for (ViewBuilder childViewBuilder : this.children)
        {
            tableRow.addView(childViewBuilder.view(context));
        }


        return tableRow;
    }


}
