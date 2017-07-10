
package com.kispoko.tome.lib.ui;


import android.graphics.Paint;
import android.text.style.LineHeightSpan;



public class StoryLineHeightSpan implements LineHeightSpan
{

    private final int height;

    public StoryLineHeightSpan(int height) {
        this.height = height;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v,
                             Paint.FontMetricsInt fm) {
        fm.bottom += height;
        fm.descent += height;
    }

}
