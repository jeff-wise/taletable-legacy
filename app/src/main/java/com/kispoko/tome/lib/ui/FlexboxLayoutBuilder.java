
package com.kispoko.tome.lib.ui;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.google.android.flexbox.FlexboxLayout;
import com.kispoko.tome.model.sheet.Spacing;

import java.util.ArrayList;
import java.util.List;



/**
 * Flexbox Layout Builder
 */
public class FlexboxLayoutBuilder
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

    public Integer                  gravity;
    public Integer                  layoutGravity;

    public Integer                  backgroundColor;
    public Integer                  backgroundResource;

    public Margins                  margin;
    public Spacing                  marginSpacing;
    public Padding                  padding;
    public Spacing                  paddingSpacing;

    public Integer                  wrap;

    public View.OnClickListener     onClick;
    public View.OnLongClickListener onLongClick;

    public List<Integer> rules;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private List<ViewBuilder>      children;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public FlexboxLayoutBuilder()
    {
        this.id                 = null;

        this.height             = null;
        this.heightDp           = null;

        this.width              = null;
        this.weight             = null;

        this.layoutType         = LayoutType.LINEAR;

        this.gravity            = null;
        this.layoutGravity      = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;

        this.margin             = new Margins();
        this.padding            = new Padding();
        this.paddingSpacing     = null;

        this.onClick            = null;
        this.onLongClick        = null;

        this.children           = new ArrayList<>();

        this.rules              = new ArrayList<>();
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Attributes
    // ------------------------------------------------------------------------------------------

    public FlexboxLayoutBuilder child(ViewBuilder childViewBuilder)
    {
        this.children.add(childViewBuilder);
        return this;
    }


    public FlexboxLayoutBuilder addRule(int verb)
    {
        this.rules.add(verb);
        return this;
    }


    // > View Builder
    // ------------------------------------------------------------------------------------------

    public View view(Context context)
    {
        return this.flexboxLayout(context);
    }


    // > Layout
    // ------------------------------------------------------------------------------------------

    public FlexboxLayout flexboxLayout(Context context)
    {
        FlexboxLayout flexboxLayout = new FlexboxLayout(context);

        // [1] Layout
        // --------------------------------------------------------------------------------------

        // > Id
        // --------------------------------------------------------------------------------------

        if (this.id != null)
            flexboxLayout.setId(this.id);

        // > Padding
        // --------------------------------------------------------------------------------------

        flexboxLayout.setPadding(this.padding.left(context),
                                 this.padding.top(context),
                                 this.padding.right(context),
                                 this.padding.bottom(context));

        // > Padding Spacing
        // --------------------------------------------------------------------------------------

        if (this.paddingSpacing != null)
        {
            flexboxLayout.setPadding(this.paddingSpacing.leftPx(),
                                     this.paddingSpacing.topPx(),
                                     this.paddingSpacing.rightPx(),
                                     this.paddingSpacing.bottomPx());
        }

        // > Flexbox Wrap
        // --------------------------------------------------------------------------------------

        if (this.wrap != null)
            flexboxLayout.setFlexWrap(this.wrap);

        // > On Click Listener
        // --------------------------------------------------------------------------------------

        if (this.onClick != null)
            flexboxLayout.setOnClickListener(this.onClick);

        // > On Long Click Listener
        // --------------------------------------------------------------------------------------

        if (this.onLongClick != null)
            flexboxLayout.setOnLongClickListener(this.onLongClick);

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
            flexboxLayout.setBackgroundColor(ContextCompat.getColor(context, this.backgroundColor));

        // > Background Resource
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null && this.backgroundColor != null) {
            Drawable bgDrawable = ContextCompat.getDrawable(context, this.backgroundResource);
            int      color      = ContextCompat.getColor(context, this.backgroundColor);
            bgDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            flexboxLayout.setBackground(bgDrawable);
        }
        else if (this.backgroundResource != null) {
            flexboxLayout.setBackgroundResource(this.backgroundResource);
        }


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

        if (this.marginSpacing != null)
            layoutParamsBuilder.setMargins(this.marginSpacing);
        else
            layoutParamsBuilder.setMargins(this.margin);


        // > Rules (Relative Layout Only)
        // --------------------------------------------------------------------------------------

        layoutParamsBuilder.setRules(this.rules);


        flexboxLayout.setLayoutParams(layoutParamsBuilder.layoutParams());


        // [3] Children
        // --------------------------------------------------------------------------------------

        for (ViewBuilder childViewBuilder : this.children)
        {
            flexboxLayout.addView(childViewBuilder.view(context));
        }


        return flexboxLayout;
    }


}
