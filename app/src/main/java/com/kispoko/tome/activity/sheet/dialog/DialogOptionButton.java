
package com.kispoko.tome.activity.sheet.dialog;


import android.view.View;

import java.io.Serializable;



/**
 * Dialog Option Button
 */
public class DialogOptionButton implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private int                     labelId;
    private int                     iconId;
    private View.OnClickListener    onClickListener;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public DialogOptionButton(int labelId,
                              int iconId,
                              View.OnClickListener onClickListener)
    {
        this.labelId            = labelId;
        this.iconId             = iconId;
        this.onClickListener    = onClickListener;
    }


    // API
    // ------------------------------------------------------------------------------------------

    /**
     * The button label resource id.
     * @return The button label id.
     */
    public int labelId()
    {
        return this.labelId;
    }


    /**
     * The icon drawable resource id.
     * @return The resource id.
     */
    public int iconId()
    {
        return iconId;
    }


    /**
     * The On Click listerer to be called when the button is clicked.
     * @return The on-click listener.
     */
    public View.OnClickListener onClickListener()
    {
        return this.onClickListener;
    }


}
