
package com.kispoko.tome.util;


import android.widget.LinearLayout;

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

}
