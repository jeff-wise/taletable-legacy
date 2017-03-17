
package com.kispoko.tome.lib.ui;


import android.content.Context;
import android.support.v7.widget.RecyclerView;



/**
 * Recycler View Builder
 */
public class RecyclerViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public LayoutType                   layoutType;

    public Integer                      width;
    public Integer                      height;

    public RecyclerView.LayoutManager   layoutManager;
    public RecyclerView.Adapter         adapter;

    public Padding                      padding;
    public Margins                      margin;

    public RecyclerView.ItemDecoration  divider;

    public Integer                      backgroundColor;
    public Integer                      backgroundResource;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public RecyclerViewBuilder()
    {
        this.layoutType         = LayoutType.LINEAR;

        this.width              = null;
        this.height             = null;

        this.layoutManager      = null;
        this.adapter            = null;

        this.padding            = new Padding();
        this.margin             = new Margins();

        this.divider            = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;
    }


    // API
    // ------------------------------------------------------------------------------------------

    public RecyclerView recyclerView(Context context)
    {
        RecyclerView recyclerView = new RecyclerView(context);

        // [1] Recycler View
        // --------------------------------------------------------------------------------------

        // > Layout Manager
        // --------------------------------------------------------------------------------------

        if (this.layoutManager != null)
            recyclerView.setLayoutManager(this.layoutManager);

        // > Adapter
        // --------------------------------------------------------------------------------------

        if (this.adapter != null)
            recyclerView.setAdapter(this.adapter);

        // > Divider
        // --------------------------------------------------------------------------------------

        if (this.divider != null)
            recyclerView.addItemDecoration(this.divider);

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
            recyclerView.setBackgroundColor(this.backgroundColor);

        // > Background Resource
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null)
            recyclerView.setBackgroundResource(this.backgroundResource);

        // > Padding
        // --------------------------------------------------------------------------------------

        recyclerView.setPadding(this.padding.left(context),
                                this.padding.top(context),
                                this.padding.right(context),
                                this.padding.bottom(context));



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

        // > Margins
        // --------------------------------------------------------------------------------------

        layoutParamsBuilder.setMargins(this.margin);


        switch (this.layoutType)
        {
            case LINEAR:
                recyclerView.setLayoutParams(layoutParamsBuilder.linearLayoutParams());
                break;
            case RELATIVE:
                recyclerView.setLayoutParams(layoutParamsBuilder.relativeLayoutParams());
                break;
            case TABLE:
                recyclerView.setLayoutParams(layoutParamsBuilder.tableLayoutParams());
                break;
            case TABLE_ROW:
                recyclerView.setLayoutParams(layoutParamsBuilder.tableRowLayoutParams());
                break;
        }


        return recyclerView;
    }

}
