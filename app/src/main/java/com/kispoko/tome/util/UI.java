
package com.kispoko.tome.util;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.R;



/**
 * Common user interface functions
 */
public class UI
{

    public static View divider(Context context)
    {
        View dividerView = new View(context);

        int one_dp = (int) Util.getDim(context, R.dimen.one_dp);
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, one_dp);
        dividerView.setLayoutParams(layoutParams);

        dividerView.setBackgroundColor(ContextCompat.getColor(context, R.color.bluegrey_800));

        return dividerView;
    }

}
