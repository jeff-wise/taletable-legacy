
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;



/**
 * Edit View Builder
 */
public class EditTextBuilder implements ViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public Integer      id;

    public LayoutType   layoutType;

    public Integer      height;
    public Integer      width;
    public Float        weight;
    public Integer      minHeight;

    public Integer      gravity;
    public Integer      layoutGravity;

    public String       text;

    public Padding      padding;
    public Margins      margin;

    public Integer      size;
    public Integer      color;
    public Typeface     font;

    public Integer      backgroundColor;
    public Integer      backgroundResource;

    public boolean      withLabel;
    public String       hint;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public EditTextBuilder()
    {
        this.id                 = null;

        this.layoutType         = LayoutType.LINEAR;

        this.height             = null;
        this.width              = null;
        this.weight             = null;
        this.minHeight          = null;

        this.gravity            = null;
        this.layoutGravity      = null;

        this.text               = null;

        this.padding            = new Padding();
        this.margin             = new Margins();

        this.size               = null;
        this.color              = null;
        this.font               = null;

        this.backgroundColor    = null;
        this.backgroundResource = null;

        this.withLabel          = false;
        this.hint               = null;
    }


    // API
    // ------------------------------------------------------------------------------------------


    public View view(Context context)
    {
        return this.editText(context);
    }


    public View editText(Context context)
    {
        EditText editText = new EditText(context);

        // [1] Text View
        // --------------------------------------------------------------------------------------

        // > Id
        // --------------------------------------------------------------------------------------

        if (this.id != null)
            editText.setId(this.id);

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.gravity != null)
            editText.setGravity(this.gravity);

        // > Min Height
        // --------------------------------------------------------------------------------------

        if (this.minHeight != null)
            editText.setMinHeight((int) context.getResources().getDimension(this.minHeight));


        // > Padding
        // --------------------------------------------------------------------------------------

        editText.setPadding(this.padding.left(context),
                            this.padding.top(context),
                            this.padding.right(context),
                            this.padding.bottom(context));

        // > Size
        // --------------------------------------------------------------------------------------

        if (this.size != null)
            editText.setTextSize(context.getResources().getDimension(this.size));

        // > Color
        // --------------------------------------------------------------------------------------

        if (this.color != null)
            editText.setTextColor(ContextCompat.getColor(context, this.color));

        // > Background Resource
        // --------------------------------------------------------------------------------------

        if (this.backgroundResource != null)
            editText.setBackgroundResource(this.backgroundResource);

        // > Font
        // --------------------------------------------------------------------------------------

        if (this.font != null)
            editText.setTypeface(this.font);

        // > Background Color
        // --------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
            editText.setBackgroundColor(ContextCompat.getColor(context, this.backgroundColor));

        // > Text
        // --------------------------------------------------------------------------------------

        if (this.text != null)
            editText.setText(this.text);

        // > Hint
        // --------------------------------------------------------------------------------------

        if (this.hint != null)
            editText.setHint(this.hint);


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


        // [3] Configure Layout
        // --------------------------------------------------------------------------------------

        TextInputLayout textInputLayout = new TextInputLayout(context);

        ViewGroup.LayoutParams layoutParams = layoutParamsBuilder.layoutParams();

        if (withLabel)
            textInputLayout.setLayoutParams(layoutParams);
        else
            editText.setLayoutParams(layoutParams);

        if (withLabel) {
            textInputLayout.addView(editText);
        }


        if (withLabel)
            return textInputLayout;
        else
            return editText;
    }

}
