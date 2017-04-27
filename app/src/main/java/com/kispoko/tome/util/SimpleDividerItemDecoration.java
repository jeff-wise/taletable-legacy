
package com.kispoko.tome.util;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kispoko.tome.R;



/**
 * Implement divider lines for RecyclerView
 */
public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration
{

    private Drawable divider;


    public SimpleDividerItemDecoration(Context context)
    {
        this.divider = ContextCompat.getDrawable(context, R.drawable.line_divider);
    }


    public SimpleDividerItemDecoration(Context context, int colorId)
    {
        Drawable dividerDrawable = ContextCompat.getDrawable(context, R.drawable.line_divider);
        int color = ContextCompat.getColor(context, colorId);
        dividerDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));

        this.divider = dividerDrawable;
    }

    public SimpleDividerItemDecoration(Context context, int colorId, float width)
    {

    }


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state)
    {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }
}
