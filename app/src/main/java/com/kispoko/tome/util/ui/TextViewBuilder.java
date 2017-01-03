
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Text View Builder
 */
public class TextViewBuilder implements ViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public Integer              id;

    public LayoutType           layoutType;

    public Integer              height;
    public Integer              width;

    public Integer              gravity;
    public Integer              layoutGravity;
    public Integer              visibility;

    public String               text;
    public Integer              textId;

    public Padding              padding;
    public Margins              margin;

    public Integer              size;
    public Integer              color;
    public Typeface             font;

    public Integer              backgroundColor;
    public Integer              backgroundResource;

    public View.OnClickListener onClick;

    public List<Integer>        rules;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextViewBuilder()
    {
        this.id                 = null;

        this.layoutType         = LayoutType.LINEAR;

        this.height             = null;
        this.width              = null;

        this.gravity            = null;
        this.layoutGravity      = null;
        this.visibility         = null;

        this.text               = null;
        this.textId             = null;

        this.padding            = new Padding();
        this.margin             = new Margins();

        this.size               = null;
        this.color              = null;
        this.font               = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;

        this.onClick            = null;

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

        // > Visibility
        // --------------------------------------------------------------------------------------

        if (this.visibility != null)
            textView.setVisibility(this.visibility);

        // > Padding
        // --------------------------------------------------------------------------------------

        textView.setPadding(this.padding.left(context),
                            this.padding.top(context),
                            this.padding.right(context),
                            this.padding.bottom(context));

        // > On Click Listener
        // --------------------------------------------------------------------------------------

        if (this.onClick != null)
            textView.setOnClickListener(this.onClick);

        // > Size
        // --------------------------------------------------------------------------------------

        if (this.size != null)
            textView.setTextSize(context.getResources().getDimension(this.size));

        // > Color
        // --------------------------------------------------------------------------------------

        if (this.color != null)
            textView.setTextColor(ContextCompat.getColor(context, this.color));

        // > Font
        // --------------------------------------------------------------------------------------

        if (this.font != null)
            textView.setTypeface(this.font);

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
            textView.setBackgroundColor(ContextCompat.getColor(context, this.backgroundColor));

        // > Background Resource
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null)
            textView.setBackgroundResource(this.backgroundResource);

        // > Text
        // --------------------------------------------------------------------------------------

        if (this.text != null)
            textView.setText(this.text);

        // > Text Id
        // --------------------------------------------------------------------------------------

        if (this.textId != null)
            textView.setText(this.textId);

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


        switch (this.layoutType)
        {
            case LINEAR:
                textView.setLayoutParams(layoutParamsBuilder.linearLayoutParams());
                break;
            case RELATIVE:
                textView.setLayoutParams(layoutParamsBuilder.relativeLayoutParams());
                break;
            case TABLE:
                textView.setLayoutParams(layoutParamsBuilder.tableLayoutParams());
                break;
            case TABLE_ROW:
                textView.setLayoutParams(layoutParamsBuilder.tableRowLayoutParams());
                break;
        }

        return textView;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private boolean isLayoutConstant(Integer constant)
    {
        if (constant == LinearLayout.LayoutParams.MATCH_PARENT ||
            constant == LinearLayout.LayoutParams.WRAP_CONTENT) {
            return true;
        }
        else {
            return false;
        }
    }


}
