
package com.taletable.android.util;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.taletable.android.R;
import com.taletable.android.lib.ui.Font;
import com.taletable.android.model.sheet.style.TextFont;
import com.taletable.android.model.sheet.style.TextFontStyle;


/**
 * Common user interface functions
 */
public class UI
{

    public static void initializeToolbar(final AppCompatActivity activity, String title)
    {
        // [1] Configure Action Bar
        // -------------------------------------------------------------------------------------

        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();

        try {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        catch (Exception exception) {
        }

        // [2] Configure Title
        // -------------------------------------------------------------------------------------

        TextView titleView = (TextView) activity.findViewById(R.id.toolbar_title);

        if (titleView != null)
        {
            titleView.setTypeface(Font.INSTANCE.typeface(TextFont.RobotoCondensed.INSTANCE,
                                                         TextFontStyle.Regular.INSTANCE,
                                                         activity));
            titleView.setText(title);
        }

        // [3] Configure Back Button
        // -------------------------------------------------------------------------------------

        ImageView backButtonView = (ImageView) activity.findViewById(R.id.toolbar_back_button);

        if (backButtonView != null)
        {
            backButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.finish();
                }
            });
        }

    }


}
