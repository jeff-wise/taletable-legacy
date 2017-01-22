
package com.kispoko.tome.util;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;



/**
 * Common user interface functions
 */
public class UI
{

    public static View divider(Context context, Integer colorId, Integer width)
    {
        View dividerView = new View(context);

        if (width == 1) {
            int one_dp = (int) Util.getDim(context, R.dimen.one_dp);
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, one_dp);
            dividerView.setLayoutParams(layoutParams);
        }
        else if (width == 2) {
            int two_dp = (int) Util.getDim(context, R.dimen.two_dp);
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, two_dp);
            dividerView.setLayoutParams(layoutParams);
        }

        dividerView.setBackgroundColor(ContextCompat.getColor(context, colorId));

        return dividerView;
    }


    public static void initializeToolbar(AppCompatActivity activity)
    {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        TextView titleView = (TextView) activity.findViewById(R.id.page_title);
        titleView.setTypeface(Util.sansSerifFontBold(activity));
    }

}
