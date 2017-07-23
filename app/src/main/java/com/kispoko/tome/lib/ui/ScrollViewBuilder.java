
package com.kispoko.tome.lib.ui;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ScrollView;



/**
 * Scroll View Builder
 *
 */
public class ScrollViewBuilder implements ViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Layout State
    // ------------------------------------------------------------------------------------------

    public Integer                  id;

    public Integer                  height;
    public Integer                  heightDp;
    public Integer                  width;
    public Float                    weight;

    public LayoutType               layoutType;

    public Integer                  layoutGravity;

    public Integer                  backgroundColor;
    public Integer                  backgroundResource;

    public Margins                  margin;
    public Padding                  padding;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ScrollViewBuilder()
    {
        this.id                 = null;

        this.height             = null;
        this.width              = null;
        this.weight             = null;

        this.layoutType         = LayoutType.LINEAR;

        this.layoutGravity      = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;

        this.margin             = new Margins();
        this.padding            = new Padding();
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > View Builder
    // ------------------------------------------------------------------------------------------

    public View view(Context context)
    {
        return this.scrollView(context);
    }


    // > Layout
    // ------------------------------------------------------------------------------------------

    public ScrollView scrollView(Context context)
    {
        ScrollView scrollView = new ScrollView(context);

        // [1] Layout
        // --------------------------------------------------------------------------------------

        // > Id
        // --------------------------------------------------------------------------------------

        if (this.id != null)
            scrollView.setId(this.id);

        // > Padding
        // --------------------------------------------------------------------------------------

        scrollView.setPadding(this.padding.left(context),
                              this.padding.top(context),
                              this.padding.right(context),
                              this.padding.bottom(context));

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
            scrollView.setBackgroundColor(ContextCompat.getColor(context, this.backgroundColor));

        // > Background Resource
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null)
            scrollView.setBackgroundResource(this.backgroundResource);


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
        else if (this.heightDp != null)
            layoutParamsBuilder.setHeightDp(this.heightDp);

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


        scrollView.setLayoutParams(layoutParamsBuilder.layoutParams());

        return scrollView;
    }

}
