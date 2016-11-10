
package com.kispoko.tome.sheet.widget.action;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.util.Util;


/**
 * WidgetData Action
 */
public class Action
{

    // > Labels
    // ------------------------------------------------------------------------------------------

    public static String label(String actionName)
    {
        switch (actionName)
        {
            case "edit":
                return "EDIT";
            case "generate":
                return "GENERATE";
        }
        return "";
    }

    // > Icons
    // ------------------------------------------------------------------------------------------

    public static ImageView iconView(Context context, String actionName)
    {
        ImageView iconView = new ImageView(context);

        LinearLayout.LayoutParams iconViewLayoutParams = Util.linearLayoutParamsWrapMatch();
        int iconMarginRight = (int) Util.getDim(context,
                                                R.dimen.action_sheet_action_icon_margin_right);
        int actionRowHeight = (int) Util.getDim(context,
                R.dimen.action_sheet_row_height);
        iconViewLayoutParams.rightMargin = iconMarginRight;
        iconViewLayoutParams.height = actionRowHeight;
        iconView.setLayoutParams(iconViewLayoutParams);
        iconView.setBackgroundResource(R.drawable.bg_action_button_icon);

        int iconPaddingHorz = (int) Util.getDim(context,
                                                R.dimen.action_sheet_action_icon_padding_horz);
        iconView.setPadding(iconPaddingHorz, 0,
                            iconPaddingHorz, 0);

        // Set drawable for action
        switch (actionName)
        {
            case "edit":
                iconView.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_action_edit));
                break;
            case "generate":
                iconView.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_action_generate));
                break;
        }

        return iconView;
    }
}
