
package com.kispoko.tome.util;


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * This class configures the vertical space between items in a RecyclerView.
 */
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration
{

    private int verticalSpaceHeight;

    public VerticalSpaceItemDecoration(int verticalSpaceHeight)
    {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state)
    {
        outRect.bottom = this.verticalSpaceHeight;
    }
}
