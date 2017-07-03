
package com.kispoko.tome.lib.ui;


import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.LinearLayout;

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


    public static LinearLayout navigationButtonView(int labelId, int iconId, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.padding.topDp        = 15f;
        layout.padding.bottomDp     = 15f;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = iconId;

        icon.color                  = R.color.dark_theme_primary_15;

        icon.margin.rightDp         = 15f;

        // [3 B] Button
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.textId                = labelId;

//        label.font                  = Font.serifFontRegular(context);
        label.color                 = R.color.dark_theme_primary_10;
        label.sizeSp                = 17f;


        return layout.linearLayout(context);
    }


}
