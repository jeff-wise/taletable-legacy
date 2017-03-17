
package com.kispoko.tome.util;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.Font;


/**
 * Common user interface functions
 */
public class UI
{

    public static void initializeToolbar(AppCompatActivity activity)
    {
        UI.initializeToolbar(activity, true, false);
    }


    public static void initializeToolbar(AppCompatActivity activity, boolean isbold)
    {
        UI.initializeToolbar(activity, true, isbold);
    }


    public static void initializeToolbar(AppCompatActivity activity,
                                         boolean isBold,
                                         boolean showIcon)
    {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();

        if (showIcon) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        actionBar.setDisplayShowTitleEnabled(false);

        TextView titleView = (TextView) activity.findViewById(R.id.page_title);

        if (isBold)
            titleView.setTypeface(Font.serifFontBold(activity));
        else
            titleView.setTypeface(Font.serifFontRegular(activity));
    }


}
