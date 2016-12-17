
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;



/**
 * Image View Builder
 */
public class ImageViewBuilder implements ViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public Integer          id;

    public LayoutType       layoutType;

    public Integer          height;
    public Integer          width;

    public Integer          layoutGravity;

    public Padding          padding;
    public Margins          margin;

    public Integer          image;

    private List<Integer>   rules;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ImageViewBuilder()
    {
        this.id         = null;

        this.layoutType = LayoutType.NONE;

        this.height     = null;
        this.width      = null;

        this.padding    = new Padding();
        this.margin     = new Margins();

        this.image      = null;

        this.rules      = new ArrayList<>();
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Methods
    // ------------------------------------------------------------------------------------------

    public ImageViewBuilder addRule(int verb)
    {
        this.rules.add(verb);
        return this;
    }


    // > View Builder
    // ------------------------------------------------------------------------------------------

    public View view(Context context)
    {
        return this.imageView(context);
    }


    // > Image View
    // ------------------------------------------------------------------------------------------

    public ImageView imageView(Context context)
    {
        ImageView imageView = new ImageView(context);

        // [1] Image View
        // --------------------------------------------------------------------------------------

        // > Id
        // --------------------------------------------------------------------------------------

        if (this.id != null)
            imageView.setId(this.id);

        // > Padding
        // --------------------------------------------------------------------------------------

        imageView.setPadding(this.padding.left(context),
                this.padding.top(context),
                this.padding.right(context),
                this.padding.bottom(context));

        // > Image
        // --------------------------------------------------------------------------------------

        if (this.image != null)
            imageView.setImageDrawable(ContextCompat.getDrawable(context, this.image));

        // [2] Layout
        // --------------------------------------------------------------------------------------

        LayoutParamsBuilder layoutParamsBuilder;

        if (this.layoutType != LayoutType.NONE)
            layoutParamsBuilder = new LayoutParamsBuilder(this.layoutType, context);
        else
            layoutParamsBuilder = new LayoutParamsBuilder(LayoutType.LINEAR, context);

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

        // > Rules
        // --------------------------------------------------------------------------------------

        layoutParamsBuilder.setRules(this.rules);

        switch (this.layoutType)
        {
            case LINEAR:
                imageView.setLayoutParams(layoutParamsBuilder.linearLayoutParams());
                break;
            case RELATIVE:
                imageView.setLayoutParams(layoutParamsBuilder.relativeLayoutParams());
                break;
            case NONE:
                imageView.setLayoutParams(layoutParamsBuilder.linearLayoutParams());
        }

        return imageView;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private boolean isLayoutConstant(Integer constant)
    {
        if (constant == LinearLayout.LayoutParams.MATCH_PARENT ||
                constant == LinearLayout.LayoutParams.WRAP_CONTENT) {
            return true;
        } else {
            return false;
        }
    }


}
