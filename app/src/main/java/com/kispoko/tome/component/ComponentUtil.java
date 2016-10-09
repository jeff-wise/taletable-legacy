
package com.kispoko.tome.component;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.kispoko.tome.R;

import static android.R.attr.labelTextSize;
import static com.kispoko.tome.component.Component.TextSize.LARGE;
import static com.kispoko.tome.component.Component.TextSize.MEDIUM;
import static com.kispoko.tome.component.Component.TextSize.SMALL;

/**
 * Component utility functions
 */
public class ComponentUtil
{

    public static TextView prefixView(Context context, String prefix, Component.TextSize textSize)
    {
        TextView textView = new TextView(context);

        textView.setTextSize(getTextSizeSP(context, textSize) * 0.7f);

        textView.setTextColor(ContextCompat.getColor(context, R.color.text_medium));

        textView.setText(prefix);

        int padding = (int) context.getResources().getDimension(R.dimen.prefix_padding_left);
        textView.setPadding(padding, 0, 0, padding);

        return textView;
    }


    /**
     * Return text size in scaled pixels.
     */
    public static float getTextSizeSP(Context context, Component.TextSize textSize)
    {
        float textSizeSP = 0;
        switch (textSize) {
            case SMALL:
                textSizeSP = context.getResources()
                                    .getDimension(R.dimen.text_size_small);
                break;
            case MEDIUM:
                textSizeSP = context.getResources()
                                    .getDimension(R.dimen.text_size_medium);
                break;
            case LARGE:
                textSizeSP = context.getResources()
                                    .getDimension(R.dimen.text_size_large);
                break;
        }
        return textSizeSP;
    }
}
