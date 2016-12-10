
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;



/**
 * Text View Builder
 */
public class TextViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public Integer  id;

    public Integer  gravity;

    public String   text;

    public Padding  padding;

    public Integer  size;
    public Integer  color;
    public Typeface font;

    public Integer  backgroundColor;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextViewBuilder()
    {
        this.id              = null;

        this.gravity         = null;

        this.text            = null;

        this.padding         = new Padding();

        this.size            = null;
        this.color           = null;
        this.font            = null;

        this.backgroundColor = null;
    }


    // API
    // ------------------------------------------------------------------------------------------

    public TextView textView(Context context)
    {
        TextView textView = new TextView(context);

        // > Id
        // ------------------------------------------------------------------------------------------

        if (this.id != null)
            textView.setId(this.id);

        // > Gravity
        // ------------------------------------------------------------------------------------------

        if (this.gravity != null)
            textView.setGravity(this.gravity);

        // > Padding
        // ------------------------------------------------------------------------------------------

        textView.setPadding(this.padding.left(context),
                            this.padding.top(context),
                            this.padding.right(context),
                            this.padding.bottom(context));

        // > Size
        // ------------------------------------------------------------------------------------------

        if (this.size != null)
            textView.setTextSize(context.getResources().getDimension(this.size));

        // > Color
        // ------------------------------------------------------------------------------------------

        if (this.color != null)
            textView.setTextColor(ContextCompat.getColor(context, this.color));

        // > Font
        // ------------------------------------------------------------------------------------------

        if (this.font != null)
            textView.setTypeface(this.font);

        // > Background Color
        // ------------------------------------------------------------------------------------------

        if (this.backgroundColor != null)
            textView.setBackgroundColor(ContextCompat.getColor(context, this.backgroundColor));

        // > Text
        // ------------------------------------------------------------------------------------------

        if (this.text != null)
            textView.setText(this.text);

        return textView;
    }


}
