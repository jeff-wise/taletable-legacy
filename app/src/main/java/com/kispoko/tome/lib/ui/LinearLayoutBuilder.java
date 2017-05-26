
package com.kispoko.tome.lib.ui;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;


import com.kispoko.tome.model.sheet.Spacing;

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
    public Float                    elevation;

    public Margins                  margin;
    public Spacing                  marginSpacing;
    public Padding                  padding;
    public Spacing                  paddingSpacing;

    public View.OnClickListener     onClick;
    public View.OnLongClickListener onLongClick;

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
        this.padding            = new Padding();
        this.paddingSpacing     = null;

        this.onClick            = null;
        this.onLongClick        = null;

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

        // > Haptic Feedback
        // --------------------------------------------------------------------------------------

        if (this.hapticFeedback != null)
            linearLayout.setHapticFeedbackEnabled(this.hapticFeedback);

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
            linearLayout.setBackgroundColor(ContextCompat.getColor(context, this.backgroundColor));

        // > Background Resource
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null && this.backgroundColor != null) {
            Drawable bgDrawable = ContextCompat.getDrawable(context, this.backgroundResource);
            int      color      = ContextCompat.getColor(context, this.backgroundColor);
            bgDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            linearLayout.setBackground(bgDrawable);
        }
        else if (this.backgroundResource != null) {
            linearLayout.setBackgroundResource(this.backgroundResource);
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


        linearLayout.setLayoutParams(layoutParamsBuilder.layoutParams());


        // [3] Children
        // --------------------------------------------------------------------------------------

        for (ViewBuilder childViewBuilder : this.children)
        {
            linearLayout.addView(childViewBuilder.view(context));
        }


        return linearLayout;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

}
