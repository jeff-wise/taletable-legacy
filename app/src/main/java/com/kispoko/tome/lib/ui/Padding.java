
package com.kispoko.tome.lib.ui;


import android.content.Context;

import com.kispoko.tome.util.Util;


/**
 * Padding
 */
public class Padding
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public Integer left;
    public Integer right;
    public Integer top;
    public Integer bottom;

    public Integer leftDp;
    public Integer rightDp;
    public Integer topDp;
    public Integer bottomDp;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Padding()
    {
        this.left       = null;
        this.right      = null;
        this.top        = null;
        this.bottom     = null;

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
        else
            return 0;
    }


    public int top(Context context)
    {
        if (this.top != null)
            return (int) context.getResources().getDimension(this.top);
        else if (this.topDp != null)
            return Util.dpToPixel(this.topDp);
        else
            return 0;
    }


    public int right(Context context)
    {
        if (this.right != null)
            return (int) context.getResources().getDimension(this.right);
        else if (this.rightDp != null)
            return Util.dpToPixel(this.rightDp);
        else
            return 0;
    }


    public int bottom(Context context)
    {
        if (this.bottom != null)
            return (int) context.getResources().getDimension(this.bottom);
        else if (this.bottomDp != null)
            return Util.dpToPixel(this.bottomDp);
        else
            return 0;
    }

}
