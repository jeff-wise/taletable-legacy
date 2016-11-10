
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.activity.sheet.ActionDialogFragment;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.util.Util;



/**
 * Widget UI
 */
public class WidgetUI
{

    /**
     * The layout for a component.
     *
     * @param context Context object.
     * @return A LinearLayout that represents the outer-most container of a component view.
     */
    protected LinearLayout linearLayout(Context context, final Rules rules) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

        int layoutMarginsHorz = (int) Util.getDim(context, R.dimen.comp_layout_margins_horz);

        linearLayoutParams.setMargins(layoutMarginsHorz, 0, layoutMarginsHorz, 0);
        layout.setLayoutParams(linearLayoutParams);

        layout.setBackgroundResource(R.drawable.bg_component);

        final WidgetData thisWidgetData = this;

        final SheetActivity thisActivity = (SheetActivity) context;
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionDialogFragment actionDialogFragment =
                        ActionDialogFragment.newInstance(thisWidgetData, rules);
                actionDialogFragment.show(thisActivity.getSupportFragmentManager(),
                        actionDialogFragment.getTag());
            }
        });


        // Add label
        TextView labelView = new TextView(context);
        labelView.setGravity(Gravity.CENTER_HORIZONTAL);
        labelView.setText(this.getLabel().toUpperCase());
        float labelTextSize = (int) Util.getDim(context, R.dimen.comp_label_text_size);
        labelView.setTextSize(labelTextSize);

        int labelPaddingBottom = (int) Util.getDim(context, R.dimen.comp_label_padding_bottom);
        labelView.setPadding(0, 0, 0, labelPaddingBottom);

        labelView.setTextColor(ContextCompat.getColor(context, R.color.text_light));

        labelView.setTypeface(Util.sansSerifFontRegular(context));


        if (this.getShowLabel())
            layout.addView(labelView);

        return layout;
    }
}
