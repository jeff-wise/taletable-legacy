
package com.kispoko.tome.lib.ui;


import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.MovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;


import com.kispoko.tome.model.sheet.style.Corners;
import com.kispoko.tome.model.sheet.style.Spacing;
import com.kispoko.tome.util.Util;

import java.util.ArrayList;
import java.util.List;



/**
 * Text View Builder
 */
public class TextViewBuilder implements ViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public Integer                  id;

    public LayoutType               layoutType;

    public Integer                  height;
    public Integer                  heightDp;

    public Integer                  width;
    public Float                    weight;

    public Integer                  minEms;
    public Integer                  maxEms;
    public Integer                  ems;

    public Float                    lineSpacingAdd;
    public Float                    lineSpacingMult;

    public Integer                  gravity;
    public Integer                  layoutGravity;
    public Integer                  visibility;
    public Integer                  elevation;

    public String                   text;
    public Spanned                  textHtml;
    public Integer                  textId;
    public SpannableStringBuilder   textSpan;

    public Padding                  padding;
    public Spacing                  paddingSpacing;

    public Margins                  margin;
    public Spacing                  marginSpacing;

    public Corners                  corners;

    public Integer                  size;
    public Float                    sizeSp;

    public Integer                  color;
    public Typeface                 font;
    public Boolean                  underlined;

    public Integer                  backgroundColor;
    public Integer                  backgroundResource;

    public Integer                  shadowColor;
    public Float                    shadowRadius;
    public Float                    shadowDx;
    public Float                    shadowDy;

    public Integer                  drawableTop;
    public Integer                  drawablePadding;

    public View.OnClickListener     onClick;
    public View.OnLongClickListener onLongClick;

    public Boolean                  hapticFeedback;
    public MovementMethod           movementMethod;

    public List<Integer>            rules;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextViewBuilder()
    {
        this.id                 = null;

        this.layoutType         = LayoutType.LINEAR;

        this.height             = null;
        this.heightDp           = null;

        this.width              = null;
        this.weight             = null;

        this.minEms             = null;
        this.maxEms             = null;
        this.ems                = null;

        this.lineSpacingAdd     = null;
        this.lineSpacingMult    = null;

        this.gravity            = null;
        this.layoutGravity      = null;
        this.visibility         = null;
        this.elevation          = null;

        this.text               = null;
        this.textHtml           = null;
        this.textId             = null;
        this.textSpan           = null;

        this.padding            = new Padding();
        this.paddingSpacing     = null;

        this.margin             = new Margins();
        this.marginSpacing      = null;

        this.corners            = null;

        this.size               = null;
        this.sizeSp             = null;

        this.color              = null;
        this.font               = null;
        this.underlined         = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;

        this.shadowColor        = null;
        this.shadowRadius       = 1f;
        this.shadowDx           = 1f;
        this.shadowDy           = 1f;

        this.drawableTop        = null;
        this.drawablePadding    = null;

        this.onClick            = null;
        this.onLongClick        = null;

        this.hapticFeedback     = null;
        this.movementMethod     = null;

        this.rules              = new ArrayList<>();
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Attributes
    // ------------------------------------------------------------------------------------------

    public TextViewBuilder addRule(int verb)
    {
        this.rules.add(verb);
        return this;
    }


    // > View Builder
    // ------------------------------------------------------------------------------------------

    public View view(Context context)
    {
        return this.textView(context);
    }


    // > Text View
    // ------------------------------------------------------------------------------------------

    public TextView textView(Context context)
    {
        TextView textView = new TextView(context);

        PaintDrawable bgDrawable = new PaintDrawable();
        boolean useDrawableBackground = false;

        // [1] Text View
        // --------------------------------------------------------------------------------------

        // > Id
        // --------------------------------------------------------------------------------------

        if (this.id != null)
            textView.setId(this.id);

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.gravity != null)
            textView.setGravity(this.gravity);

        // > Min Ems
        // --------------------------------------------------------------------------------------

        if (this.minEms != null)
            textView.setMinEms(this.minEms);

        // > Max Ems
        // --------------------------------------------------------------------------------------

        if (this.maxEms != null)
            textView.setMaxEms(this.maxEms);

        // > Max Ems
        // --------------------------------------------------------------------------------------

        if (this.ems != null)
            textView.setEms(this.ems);

        // > Line Spacing
        // --------------------------------------------------------------------------------------

        if (this.lineSpacingAdd != null && this.lineSpacingMult != null)
            textView.setLineSpacing(this.lineSpacingAdd, this.lineSpacingMult);

        // > Visibility
        // --------------------------------------------------------------------------------------

        if (this.visibility != null)
            textView.setVisibility(this.visibility);

        // > Padding
        // --------------------------------------------------------------------------------------

        if (this.paddingSpacing != null) {
            textView.setPadding(this.paddingSpacing.leftPx(),
                                this.paddingSpacing.topPx(),
                                this.paddingSpacing.rightPx(),
                                this.paddingSpacing.bottomPx());
        }
        else
        {
            textView.setPadding(this.padding.left(context),
                                this.padding.top(context),
                                this.padding.right(context),
                                this.padding.bottom(context));
        }

        // > On Click Listener
        // --------------------------------------------------------------------------------------

        if (this.onClick != null)
            textView.setOnClickListener(this.onClick);

        // > On Long Click Listener
        // --------------------------------------------------------------------------------------

        if (this.onLongClick != null)
            textView.setOnLongClickListener(this.onLongClick);

        // > Haptic Feedback
        // --------------------------------------------------------------------------------------

        if (this.hapticFeedback != null)
            textView.setHapticFeedbackEnabled(this.hapticFeedback);

        // > Movement Method
        // --------------------------------------------------------------------------------------

        if (this.movementMethod != null)
            textView.setMovementMethod(this.movementMethod);

        // > Elevation
        // --------------------------------------------------------------------------------------

        if (this.elevation != null && android.os.Build.VERSION.SDK_INT >= 21)
            textView.setElevation(this.elevation);

        // > Shadow
        // --------------------------------------------------------------------------------------

        if (this.shadowColor != null) {
            int color = ContextCompat.getColor(context, this.shadowColor);
            textView.setShadowLayer(this.shadowRadius, this.shadowDx,
                                    this.shadowDy, color);
        }

        // > Size
        // --------------------------------------------------------------------------------------

        if (this.size != null)
            textView.setTextSize(context.getResources().getDimension(this.size));

        // > Size SP
        // --------------------------------------------------------------------------------------

        if (this.sizeSp != null)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.sizeSp);

        // > Color
        // --------------------------------------------------------------------------------------

        if (this.color != null)
            textView.setTextColor(this.color);

        // > Font
        // --------------------------------------------------------------------------------------

        if (this.font != null)
            textView.setTypeface(this.font);

        // > Underlined
        // --------------------------------------------------------------------------------------

        if (this.underlined != null && this.underlined)
            textView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
        {
            textView.setBackgroundColor(this.backgroundColor);
            bgDrawable.setColorFilter(
                    new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.SRC_IN));
        }

        // > Background Resource
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null) {
            textView.setBackgroundResource(this.backgroundResource);
        }

        // > Drawable Top
        // --------------------------------------------------------------------------------------

        if (this.drawableTop != null)
            textView.setCompoundDrawablesWithIntrinsicBounds(0, this.drawableTop, 0, 0);

        // > Drawable Padding
        // --------------------------------------------------------------------------------------

        if (this.drawablePadding != null)
            textView.setCompoundDrawablePadding(this.drawablePadding);

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


        // > Text
        // --------------------------------------------------------------------------------------

        if (this.text != null)
            textView.setText(this.text);

        // > Text Html
        // --------------------------------------------------------------------------------------

        if (this.textHtml != null)
            textView.setText(this.textHtml);

        // > Text Id
        // --------------------------------------------------------------------------------------

        if (this.textId != null)
            textView.setText(this.textId);

        // > Text Span
        // --------------------------------------------------------------------------------------

        if (this.textSpan != null)
            textView.setText(this.textSpan);

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

        if (useDrawableBackground)
            textView.setBackground(bgDrawable);


        textView.setLayoutParams(layoutParamsBuilder.layoutParams());


        return textView;
    }


}
