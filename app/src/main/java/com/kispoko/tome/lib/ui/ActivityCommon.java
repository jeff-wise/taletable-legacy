
package com.kispoko.tome.lib.ui;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kispoko.tome.R;



/**
 * Common methods for activity UI.
 */
public class ActivityCommon
{

    public static void initializeToolbar(AppCompatActivity activity)
    {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();

        try {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        catch (NullPointerException exception) {

        }
    }
}
