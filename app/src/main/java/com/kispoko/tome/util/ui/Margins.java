
package com.kispoko.tome.util.ui;


import android.content.Context;

import com.kispoko.tome.util.Util;


/**
 * Margins
 */
public class Margins
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public Integer left;
    public Integer right;
    public Integer top;
    public Integer bottom;

    public Integer leftPx;
    public Integer rightPx;
    public Integer topPx;
    public Integer bottomPx;

    public Integer leftDp;
    public Integer rightDp;
    public Integer topDp;
    public Integer bottomDp;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Margins()
    {
        this.left       = null;
        this.right      = null;
        this.top        = null;
        this.bottom     = null;

        this.leftPx     = null;
        this.rightPx    = null;
        this.topPx      = null;
        this.bottomPx   = null;

        this.leftDp     = null;
        this.rightDp    = null;
        this.topDp      = null;
        this.bottomDp   = null;
    }


    // API
    // ------------------------------------------------------------------------------------------

    public int left(Context context)
    {
        if (this.left != null)
            return (int) context.getResources().getDimension(this.left);
        else if (this.leftDp != null)
            return Util.dpToPixel(this.leftDp);
        else if (this.leftPx != null)
            return this.leftPx;
        else
            return 0;
    }


    public int top(Context context)
    {
        if (this.top != null)
            return (int) context.getResources().getDimension(this.top);
        else if (this.topDp != null)
            return Util.dpToPixel(this.topDp);
        else if (this.topPx != null)
            return this.topPx;
        else
            return 0;
    }


    public int right(Context context)
    {
        if (this.right != null)
            return (int) context.getResources().getDimension(this.right);
        else if (this.rightDp != null)
            return Util.dpToPixel(this.rightDp);
        else if (this.rightPx != null)
            return this.rightPx;
        else
            return 0;
    }


    public int bottom(Context context)
    {
        if (this.bottom != null)
            return (int) context.getResources().getDimension(this.bottom);
        else if (this.bottomDp != null)
            return Util.dpToPixel(this.bottomDp);
        else if (this.bottomPx != null)
            return this.bottomPx;
        else
            return 0;
    }

}
