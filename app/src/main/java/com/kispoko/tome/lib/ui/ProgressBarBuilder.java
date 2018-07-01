
package com.kispoko.tome.lib.ui;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PaintDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;

import com.kispoko.tome.R;
import com.kispoko.tome.model.sheet.style.Corners;
import com.kispoko.tome.model.sheet.style.Spacing;
import com.kispoko.tome.util.Util;

import java.util.ArrayList;
import java.util.List;



public class ProgressBarBuilder implements ViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Layout State
    // ------------------------------------------------------------------------------------------

    public Integer                  id;

    public Integer                  height;
    public Integer                  heightDp;

    public Integer                  width;
    public Integer                  widthDp;

    public Integer                  visibility;

    public LayoutType               layoutType;

    public Integer                  layoutGravity;

    public Integer                  backgroundColor;
    public Integer                  backgroundResource;
    public Bitmap                   backgroundBitmap;
    public Float                    elevation;

    public Integer                  progressDrawableId;

    public Margins                  margin;
    public Spacing                  marginSpacing;
    public Padding                  padding;
    public Spacing                  paddingSpacing;

    public Float                    topLeftCornerRadiusDp;
    public Float                    topRightCornerRadiusDp;
    public Float                    bottomRightCornerRadiusDp;
    public Float                    bottomLeftCornerRadiusDp;

    public Corners                  corners;

    public List<Integer>            rules;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ProgressBarBuilder()
    {
        this.id                         = null;

        this.height                     = null;
        this.heightDp                   = null;

        this.width                      = null;
        this.widthDp                    = null;

        this.visibility                 = null;

        this.layoutType                 = LayoutType.LINEAR;

        this.layoutGravity              = null;

        this.backgroundColor            = null;
        this.backgroundResource         = null;
        this.elevation                  = null;

        this.progressDrawableId         = null;

        this.margin                     = new Margins();
        this.marginSpacing              = null;
        this.padding                    = new Padding();
        this.paddingSpacing             = null;

        this.topLeftCornerRadiusDp      = null;
        this.topRightCornerRadiusDp     = null;
        this.bottomRightCornerRadiusDp  = null;
        this.bottomLeftCornerRadiusDp   = null;

        this.corners                    = null;

        this.rules                      = new ArrayList<>();
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Attributes
    // ------------------------------------------------------------------------------------------

    public ProgressBarBuilder addRule(int verb)
    {
        this.rules.add(verb);
        return this;
    }


    // > View Builder
    // ------------------------------------------------------------------------------------------

    public View view(Context context)
    {
        return this.progressBar(context);
    }


    // > Layout
    // ------------------------------------------------------------------------------------------

    public ProgressBar progressBar(Context context)
    {
        ProgressBar progressBar = new ProgressBar(context, null, android.R.style.Widget_DeviceDefault_ProgressBar_Horizontal);

        PaintDrawable bgDrawable = new PaintDrawable();


        // [1] Layout
        // --------------------------------------------------------------------------------------

        // > Id
        // --------------------------------------------------------------------------------------

        if (this.id != null)
            progressBar.setId(this.id);

        // > Padding
        // --------------------------------------------------------------------------------------

        progressBar.setPadding(this.padding.left(context),
                               this.padding.top(context),
                               this.padding.right(context),
                               this.padding.bottom(context));

        // > Padding Spacing
        // --------------------------------------------------------------------------------------

        if (this.paddingSpacing != null)
        {
            progressBar.setPadding(this.paddingSpacing.leftPx(),
                                   this.paddingSpacing.topPx(),
                                   this.paddingSpacing.rightPx(),
                                   this.paddingSpacing.bottomPx());
        }

        // > Visible
        // --------------------------------------------------------------------------------------

        if (this.visibility != null)
            progressBar.setVisibility(this.visibility);

        // > Background Bitmap
        // --------------------------------------------------------------------------------------

        if (this.backgroundBitmap != null)
        {
            BitmapDrawable background = new BitmapDrawable(context.getResources(), this.backgroundBitmap);
            progressBar.setBackground(background);
        }

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
        {
            progressBar.setBackgroundColor(this.backgroundColor);
            bgDrawable.setColorFilter(this.backgroundColor, PorterDuff.Mode.SRC_IN);
//            progressDrawable.setColorFilter(
//                    new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.SRC_IN));

//            progressBar.
//            progressBar.getProgressDrawable().setColorFilter(this.backgroundColor, PorterDuff.Mode.SRC_IN);

        }


//        if (this.progressDrawableId != null)
//        {
//            progressBar.setProgressDrawable(ContextCompat.getDrawable(context, this.progressDrawableId));
//        }

        if (this.progressDrawableId != null)
        {
            progressBar.setProgressDrawable(ContextCompat.getDrawable(context, this.progressDrawableId));
        }

        // > Background Resource
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null && this.backgroundColor != null) {
            //bgDrawable = (PaintDrawable) ContextCompat.getDrawable(context, this.backgroundResource);
//            //int      color      = ContextCompat.getColor(context, this.backgroundColor);
//            bgDrawable.setColorFilter(
//                    new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.SRC_IN));
            //linearLayout.setBackground(bgDrawable);
        }
        else if (this.backgroundResource != null) {
            progressBar.setBackgroundResource(this.backgroundResource);
        }

        // > Corners
        // --------------------------------------------------------------------------------------

        if (this.corners != null)
        {
            float topLeft     = Util.dpToPixel(this.corners.topLeftCornerRadiusDp());
            float topRight    = Util.dpToPixel(this.corners.topRightCornerRadiusDp());
            float bottomRight = Util.dpToPixel(this.corners.bottomRightCornerRadiusDp());
            float bottomLeft  = Util.dpToPixel(this.corners.bottomLeftCornerRadiusDp());

            float[] radii = {topLeft, topLeft, topRight, topRight,
                             bottomRight, bottomRight, bottomLeft, bottomLeft};

            bgDrawable.setCornerRadii(radii);
        }


        if (this.topLeftCornerRadiusDp != null ||
            this.topRightCornerRadiusDp != null ||
            this.bottomRightCornerRadiusDp != null ||
            this.bottomLeftCornerRadiusDp != null)
        {
            float topLeft     = this.topLeftCornerRadiusDp != null ?
                                        Util.dpToPixel(this.topLeftCornerRadiusDp) : 0f;
            float topRight    = this.topRightCornerRadiusDp != null ?
                                        Util.dpToPixel(this.topRightCornerRadiusDp) : 0f;
            float bottomRight = this.bottomRightCornerRadiusDp != null ?
                                        Util.dpToPixel(this.bottomRightCornerRadiusDp) : 0f;
            float bottomLeft  = this.bottomLeftCornerRadiusDp != null ?
                                        Util.dpToPixel(this.bottomLeftCornerRadiusDp) : 0f;

            float[] radii = {topLeft, topLeft, topRight, topRight,
                             bottomRight, bottomRight, bottomLeft, bottomLeft};

            bgDrawable.setCornerRadii(radii);
        }

        // > Elevation
        // --------------------------------------------------------------------------------------

        if (this.elevation != null && android.os.Build.VERSION.SDK_INT >= 21)
            progressBar.setElevation(this.elevation);

        // [2] Layout Parameters
        // --------------------------------------------------------------------------------------

        LayoutParamsBuilder layoutParamsBuilder;
        layoutParamsBuilder = new LayoutParamsBuilder(this.layoutType, context);

        // > Width
        // --------------------------------------------------------------------------------------

        if (this.width != null)
            layoutParamsBuilder.setWidth(this.width);
        else if (this.widthDp != null)
            layoutParamsBuilder.setWidthDp(this.widthDp);

        // > Height
        // --------------------------------------------------------------------------------------

        if (this.height != null)
            layoutParamsBuilder.setHeight(this.height);
        else if (this.heightDp != null) {
            layoutParamsBuilder.setHeightDp(this.heightDp);
//            progressDrawable.setIntrinsicHeight(Util.dpToPixel(this.heightDp));
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


        // > Rules (Relative Layout Only)
        // --------------------------------------------------------------------------------------

        layoutParamsBuilder.setRules(this.rules);

        progressBar.setLayoutParams(layoutParamsBuilder.layoutParams());

        progressBar.setBackground(bgDrawable);

        return progressBar;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

}
