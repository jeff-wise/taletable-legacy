
package com.kispoko.tome.util;


import android.content.Context;
import android.widget.LinearLayout;

import com.kispoko.tome.R;

/**
 * Helpers for verbose UI code.
 */
public class UI
{

    public static LinearLayout.LayoutParams linearLayoutParamsWrap()
    {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                             LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public static LinearLayout.LayoutParams linearLayoutParamsMatch()
    {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                             LinearLayout.LayoutParams.MATCH_PARENT);
    }


    public static float getDim(Context context, int id)
    {
        return context.getResources().getDimension(id);
    }
}
