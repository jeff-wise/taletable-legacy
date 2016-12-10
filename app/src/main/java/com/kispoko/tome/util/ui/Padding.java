
package com.kispoko.tome.util.ui;


import android.content.Context;



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


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Padding()
    {
        this.left   = null;
        this.right  = null;
        this.top    = null;
        this.bottom = null;
    }


    // API
    // ------------------------------------------------------------------------------------------

    public int left(Context context)
    {
        if (this.left != null)
            return (int) context.getResources().getDimension(this.left);
        else
            return 0;
    }


    public int top(Context context)
    {
        if (this.top != null)
            return (int) context.getResources().getDimension(this.top);
        else
            return 0;
    }


    public int right(Context context)
    {
        if (this.right != null)
            return (int) context.getResources().getDimension(this.right);
        else
            return 0;
    }


    public int bottom(Context context)
    {
        if (this.bottom != null)
            return (int) context.getResources().getDimension(this.bottom);
        else
            return 0;
    }

}
