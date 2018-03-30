
package com.kispoko.tome.lib.ui;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PaintDrawable;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.model.sheet.style.Corners;
import com.kispoko.tome.model.sheet.style.Spacing;
import com.kispoko.tome.util.Util;

import java.util.ArrayList;
import java.util.List;



/**
 * Linear Layout Builder
 *
 * Convenience class for creating Linear Layouts
 */
public class LinearLayoutBuilder implements ViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Layout State
    // ------------------------------------------------------------------------------------------

    public Integer                  orientation;

    public Integer                  id;

    public Integer                  height;
    public Integer                  heightDp;

    public Integer                  width;
    public Integer                  widthDp;

    public Float                    weight;

    public Integer                  visibility;

    public LayoutType               layoutType;

    public Integer                  gravity;
    public Integer                  layoutGravity;

    public Integer                  backgroundColor;
    public Integer                  backgroundResource;
    public Bitmap                   backgroundBitmap;
    public Float                    elevation;

    public Margins                  margin;
    public Spacing                  marginSpacing;
    public Padding                  padding;
    public Spacing                  paddingSpacing;

    public Float                    topLeftCornerRadiusDp;
    public Float                    topRightCornerRadiusDp;
    public Float                    bottomRightCornerRadiusDp;
    public Float                    bottomLeftCornerRadiusDp;

    public Corners                  corners;

    public View.OnClickListener     onClick;
    public View.OnLongClickListener onLongClick;
    public View.OnTouchListener     onTouch;

    public Boolean                  hapticFeedback;

    public List<Integer>            rules;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private List<ViewBuilder>      children;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public LinearLayoutBuilder()
    {
        this.id                 = null;

        this.orientation        = null;

        this.height             = null;
        this.heightDp           = null;

        this.width              = null;
        this.widthDp            = null;

        this.weight             = null;

        this.visibility         = null;

        this.layoutType         = LayoutType.LINEAR;

        this.gravity            = null;
        this.layoutGravity      = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;
        this.elevation          = null;

        this.margin             = new Margins();
        this.marginSpacing      = null;
        this.padding            = new Padding();
        this.paddingSpacing     = null;

        this.topLeftCornerRadiusDp      = null;
        this.topRightCornerRadiusDp     = null;
        this.bottomRightCornerRadiusDp  = null;
        this.bottomLeftCornerRadiusDp   = null;

        this.corners                    = null;

        this.onClick            = null;
        this.onLongClick        = null;
        this.onTouch            = null;

        this.hapticFeedback     = null;

        this.children           = new ArrayList<>();

        this.rules              = new ArrayList<>();
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Attributes
    // ------------------------------------------------------------------------------------------

    public LinearLayoutBuilder child(ViewBuilder childViewBuilder)
    {
        this.children.add(childViewBuilder);
        return this;
    }


    public LinearLayoutBuilder addRule(int verb)
    {
        this.rules.add(verb);
        return this;
    }


    // > View Builder
    // ------------------------------------------------------------------------------------------

    public View view(Context context)
    {
        return this.linearLayout(context);
    }


    // > Layout
    // ------------------------------------------------------------------------------------------

    public LinearLayout linearLayout(Context context)
    {
        LinearLayout linearLayout = new LinearLayout(context);

        PaintDrawable bgDrawable = new PaintDrawable();
        boolean useDrawableBackground = false;

        // [1] Layout
        // --------------------------------------------------------------------------------------

        // > Id
        // --------------------------------------------------------------------------------------

        if (this.id != null)
            linearLayout.setId(this.id);

        // > Orientation
        // --------------------------------------------------------------------------------------

        if (this.orientation != null)
            linearLayout.setOrientation(this.orientation);

        // > Padding
        // --------------------------------------------------------------------------------------

        linearLayout.setPadding(this.padding.left(context),
                                this.padding.top(context),
                                this.padding.right(context),
                                this.padding.bottom(context));

        // > Padding Spacing
        // --------------------------------------------------------------------------------------

        if (this.paddingSpacing != null)
        {
            linearLayout.setPadding(this.paddingSpacing.leftPx(),
                                    this.paddingSpacing.topPx(),
                                    this.paddingSpacing.rightPx(),
                                    this.paddingSpacing.bottomPx());
        }

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.gravity != null)
            linearLayout.setGravity(this.gravity);

        // > Visible
        // --------------------------------------------------------------------------------------

        if (this.visibility != null)
            linearLayout.setVisibility(this.visibility);

        // > On Click Listener
        // --------------------------------------------------------------------------------------

        if (this.onClick != null)
            linearLayout.setOnClickListener(this.onClick);

        // > On Long Click Listener
        // --------------------------------------------------------------------------------------

        if (this.onLongClick != null)
            linearLayout.setOnLongClickListener(this.onLongClick);

        // > On Touch Listener
        // --------------------------------------------------------------------------------------

        if (this.onTouch != null)
            linearLayout.setOnTouchListener(this.onTouch);


        // > Haptic Feedback
        // --------------------------------------------------------------------------------------

        if (this.hapticFeedback != null)
            linearLayout.setHapticFeedbackEnabled(this.hapticFeedback);

        // > Background Bitmap
        // --------------------------------------------------------------------------------------

        if (this.backgroundBitmap != null)
        {
            BitmapDrawable background = new BitmapDrawable(context.getResources(), this.backgroundBitmap);
            linearLayout.setBackground(background);
        }

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
        {
            linearLayout.setBackgroundColor(this.backgroundColor);
            bgDrawable.setColorFilter(
                    new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.SRC_IN));
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
            linearLayout.setBackgroundResource(this.backgroundResource);
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
            useDrawableBackground = true;
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
            useDrawableBackground = true;
        }

        // > Elevation
        // --------------------------------------------------------------------------------------

        if (this.elevation != null && android.os.Build.VERSION.SDK_INT >= 21)
            linearLayout.setElevation(this.elevation);


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
            bgDrawable.setIntrinsicHeight(Util.dpToPixel(this.heightDp));
        }

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


        linearLayout.setLayoutParams(layoutParamsBuilder.layoutParams());


        // [3] Children
        // --------------------------------------------------------------------------------------

        for (ViewBuilder childViewBuilder : this.children)
        {
            linearLayout.addView(childViewBuilder.view(context));
        }


        if (useDrawableBackground)
            linearLayout.setBackground(bgDrawable);


        return linearLayout;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

}
