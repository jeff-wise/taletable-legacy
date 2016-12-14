
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TableLayout;



/**
 * Table Layout Builder
 */
public class TableLayoutBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Layout State
    // ------------------------------------------------------------------------------------------

    public Integer                 id;

    public Integer                 height;
    public Integer                 width;

    public LayoutType              layoutType;

    public Integer                 gravity;
    public Integer                 layoutGravity;

    public Integer                 backgroundColor;
    public Integer                 backgroundResource;

    public Margins                 margin;
    public Padding                 padding;

    public Boolean                 shrinkAllColumns;

    public View.OnClickListener    onClick;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TableLayoutBuilder()
    {
        this.id                 = null;

        this.height             = null;
        this.width              = null;

        this.layoutType         = LayoutType.TABLE;

        this.gravity            = null;
        this.layoutGravity      = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;

        this.margin             = new Margins();
        this.padding            = new Padding();

        this.shrinkAllColumns   = null;

        this.onClick            = null;
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > View Builder
    // ------------------------------------------------------------------------------------------

    public View view(Context context)
    {
        return this.tableLayout(context);
    }


    // > Layout
    // ------------------------------------------------------------------------------------------

    public TableLayout tableLayout(Context context)
    {
        TableLayout tableLayout = new TableLayout(context);

        // [1] Layout
        // --------------------------------------------------------------------------------------

        // > Id
        // --------------------------------------------------------------------------------------

        if (this.id != null)
            tableLayout.setId(this.id);

        // > Padding
        // --------------------------------------------------------------------------------------

        tableLayout.setPadding(this.padding.left(context),
                                this.padding.top(context),
                                this.padding.right(context),
                                this.padding.bottom(context));

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.gravity != null)
            tableLayout.setGravity(this.gravity);

        // > On Click Listener
        // --------------------------------------------------------------------------------------

        if (this.onClick != null)
            tableLayout.setOnClickListener(this.onClick);

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
            tableLayout.setBackgroundColor(ContextCompat.getColor(context, this.backgroundColor));

        // > Background Resource
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null)
            tableLayout.setBackgroundResource(this.backgroundResource);

        // > Shrink All Column
        // --------------------------------------------------------------------------------------

        if (this.shrinkAllColumns != null)
            tableLayout.setShrinkAllColumns(this.shrinkAllColumns);


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
                tableLayout.setLayoutParams(layoutParamsBuilder.linearLayoutParams());
                break;
            case RELATIVE:
                tableLayout.setLayoutParams(layoutParamsBuilder.relativeLayoutParams());
                break;
            case TABLE:
                tableLayout.setLayoutParams(layoutParamsBuilder.tableLayoutParams());
                break;
            case TABLE_ROW:
                tableLayout.setLayoutParams(layoutParamsBuilder.tableRowLayoutParams());
                break;
        }

        return tableLayout;
    }


}
