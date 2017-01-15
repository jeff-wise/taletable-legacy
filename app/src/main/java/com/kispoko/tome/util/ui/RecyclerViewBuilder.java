
package com.kispoko.tome.util.ui;


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

    public RecyclerView.ItemDecoration  divider;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public RecyclerViewBuilder()
    {
        this.layoutType         = LayoutType.LINEAR;

        this.width              = null;
        this.height             = null;

        this.layoutManager      = null;
        this.adapter            = null;

        this.divider            = null;
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
