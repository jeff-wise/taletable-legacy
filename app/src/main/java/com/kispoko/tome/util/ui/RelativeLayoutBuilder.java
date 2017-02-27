
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;



/**
 * Relative Layout Builder
 */
public class RelativeLayoutBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Layout State
    // ------------------------------------------------------------------------------------------

    public Integer                 orientation;

    public Integer                 id;

    public Integer                 height;
    public Integer                 width;
    public Float                   weight;

    public LayoutType              layoutType;

    public Integer                 gravity;
    public Integer                 layoutGravity;

    public Integer                 backgroundColor;
    public Integer                 backgroundResource;

    public Margins                 margin;
    public Padding                 padding;

    public View.OnClickListener    onClick;

    public List<Integer>           rules;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private List<ViewBuilder>      children;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public RelativeLayoutBuilder()
    {
        this.id                 = null;

        this.orientation        = null;

        this.height             = null;
        this.width              = null;
        this.weight             = null;

        this.layoutType         = LayoutType.LINEAR;

        this.gravity            = null;
        this.layoutGravity      = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;

        this.margin             = new Margins();
        this.padding            = new Padding();

        this.onClick            = null;

        this.children           = new ArrayList<>();

        this.rules              = new ArrayList<>();
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Methods
    // ------------------------------------------------------------------------------------------

    public RelativeLayoutBuilder child(ViewBuilder childViewBuilder)
    {
        this.children.add(childViewBuilder);
        return this;
    }


    public RelativeLayoutBuilder addRule(int verb)
    {
        this.rules.add(verb);
        return this;
    }


    // > Layout
    // ------------------------------------------------------------------------------------------

    public RelativeLayout relativeLayout(Context context)
    {
        RelativeLayout relativeLayout = new RelativeLayout(context);

        // [1] Layout
        // --------------------------------------------------------------------------------------

        // > Id
        // --------------------------------------------------------------------------------------

        if (this.id != null)
            relativeLayout.setId(this.id);

        // > Padding
        // --------------------------------------------------------------------------------------

        relativeLayout.setPadding(this.padding.left(context),
                                  this.padding.top(context),
                                  this.padding.right(context),
                                  this.padding.bottom(context));

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.gravity != null)
            relativeLayout.setGravity(this.gravity);


        // > On Click Listener
        // --------------------------------------------------------------------------------------

        if (this.onClick != null)
            relativeLayout.setOnClickListener(this.onClick);

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
            relativeLayout.setBackgroundColor(ContextCompat.getColor(context, this.backgroundColor));

        // > Background Resource
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null)
            relativeLayout.setBackgroundResource(this.backgroundResource);


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


        // > Rules (Relative Layout Only)
        // --------------------------------------------------------------------------------------

        layoutParamsBuilder.setRules(this.rules);



        // [3] Children
        // --------------------------------------------------------------------------------------

        for (ViewBuilder childViewBuilder : this.children)
        {
            relativeLayout.addView(childViewBuilder.view(context));
        }


        relativeLayout.setLayoutParams(layoutParamsBuilder.layoutParams());


        return relativeLayout;
    }


}
