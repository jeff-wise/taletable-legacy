
package com.taletable.android.lib.ui;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PaintDrawable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.taletable.android.model.sheet.style.Corners;
import com.taletable.android.model.sheet.style.Spacing;
import com.taletable.android.util.Util;

import java.io.InputStream;
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

    public Integer                  orientation;

    public Integer                  id;

    public Integer                  height;
    public Integer                  heightDp;

    public Integer                  widthDp;
    public Integer                  width;

    public Float                    weight;

    public LayoutType               layoutType;

    public Integer                  gravity;
    public Integer                  layoutGravity;

    public Integer                  backgroundColor;
    public Integer                  backgroundResource;
    public String                   backgroundBitmapPath;

    public Margins                  margin;
    public Spacing                  marginSpacing;

    public Padding                  padding;
    public Spacing                  paddingSpacing;

    public Corners corners;

    public View.OnClickListener     onClick;

    public List<Integer>            rules;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private List<ViewBuilder>       children;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public RelativeLayoutBuilder()
    {
        this.id                 = null;

        this.orientation        = null;

        this.height             = null;
        this.heightDp           = null;

        this.width              = null;
        this.widthDp            = null;

        this.weight             = null;

        this.layoutType         = LayoutType.LINEAR;

        this.gravity            = null;
        this.layoutGravity      = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;

        this.backgroundBitmapPath = null;

        this.margin             = new Margins();
        this.marginSpacing      = null;

        this.padding            = new Padding();
        this.paddingSpacing     = null;

        this.corners            = null;

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

        PaintDrawable bgDrawable = new PaintDrawable();
        boolean useDrawableBackground = false;

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

        // > Padding Spacing
        // --------------------------------------------------------------------------------------

        if (this.paddingSpacing != null)
        {
            relativeLayout.setPadding(this.paddingSpacing.leftPx(),
                                     this.paddingSpacing.topPx(),
                                     this.paddingSpacing.rightPx(),
                                     this.paddingSpacing.bottomPx());
        }

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.gravity != null)
            relativeLayout.setGravity(this.gravity);


        // > On Click Listener
        // --------------------------------------------------------------------------------------

        if (this.onClick != null)
            relativeLayout.setOnClickListener(this.onClick);

        // > Background Bitmap
        // --------------------------------------------------------------------------------------

        if (this.backgroundBitmapPath != null)
        {

            InputStream stream = null;

            try(InputStream is = context.getAssets().open(this.backgroundBitmapPath))
            {
                if (is != null)
                {
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);

                    BitmapDrawable background = new BitmapDrawable(context.getResources(),
                                                                   bitmap);
                    relativeLayout.setBackground(background);
                    Log.d("***REL LAY", "setting bg");
                }
            }
            catch (Exception ex) {
                //omitted.
            }

        }

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null) {
            relativeLayout.setBackgroundColor(this.backgroundColor);
            bgDrawable.setColorFilter(
                    new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.SRC_IN));
        }

        // > Background Resource
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null && this.backgroundColor != null) {
//            Drawable bgDrawable = ContextCompat.getDrawable(context, this.backgroundResource);
//            int      color      = ContextCompat.getColor(context, this.backgroundColor);
//            bgDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
//            relativeLayout.setBackground(bgDrawable);
        }
        else if (this.backgroundResource != null) {
            relativeLayout.setBackgroundResource(this.backgroundResource);
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



        // [3] Children
        // --------------------------------------------------------------------------------------

        for (ViewBuilder childViewBuilder : this.children)
        {
            relativeLayout.addView(childViewBuilder.view(context));
        }


        relativeLayout.setLayoutParams(layoutParamsBuilder.layoutParams());


        if (useDrawableBackground)
            relativeLayout.setBackground(bgDrawable);

        return relativeLayout;
    }


}
