
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import static com.kispoko.tome.R.id.textView;


/**
 * Image View Builder
 */
public class ImageViewBuilder implements ViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public Integer              id;

    public LayoutType           layoutType;

    public Integer              height;
    public Integer              width;
    public Float                weight;

    public Integer              layoutGravity;

    public Padding              padding;
    public Margins              margin;

    public Integer              image;

    public ImageView.ScaleType  scaleType;
    public Boolean              adjustViewBounds;

    public Integer              backgroundColor;
    public Integer              backgroundResource;

    public Integer              color;

    private List<Integer>       rules;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ImageViewBuilder()
    {
        this.id                 = null;

        this.layoutType         = LayoutType.LINEAR;

        this.height             = null;
        this.width              = null;
        this.weight             = null;

        this.padding            = new Padding();
        this.margin             = new Margins();

        this.image              = null;

        this.scaleType          = null;
        this.adjustViewBounds   = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;

        this.color              = null;

        this.rules              = new ArrayList<>();
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

        // > Scale Type
        // --------------------------------------------------------------------------------------

        if (this.scaleType != null)
            imageView.setScaleType(this.scaleType);

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
            imageView.setBackgroundColor(ContextCompat.getColor(context, this.backgroundColor));

        // > Background Resource
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null)
            imageView.setBackgroundResource(this.backgroundResource);

        // > Color
        // --------------------------------------------------------------------------------------

        if (this.color != null)
            imageView.setColorFilter(ContextCompat.getColor(context, this.color));

        // > Adjust View Bounds
        // --------------------------------------------------------------------------------------

        if (this.adjustViewBounds != null)
            imageView.setAdjustViewBounds(this.adjustViewBounds);



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
            case TABLE:
                imageView.setLayoutParams(layoutParamsBuilder.tableLayoutParams());
                break;
            case TABLE_ROW:
                imageView.setLayoutParams(layoutParamsBuilder.tableRowLayoutParams());
                break;
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
