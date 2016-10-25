
package com.kispoko.tome.activity.sheet;


import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.sheet.component.Action;
import com.kispoko.tome.util.Util;



/**
 * Bottom Sheet Fragment for Action Dialog
 */
public class ActionDialogFragment extends BottomSheetDialogFragment
{

    private Component component;
    private Rules rules;


    public static final ActionDialogFragment newInstance(Component component, Rules rules)
    {
        ActionDialogFragment actionDialogFragment = new ActionDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("COMPONENT", component);
        bundle.putSerializable("RULES", rules);
        actionDialogFragment.setArguments(bundle);
        return actionDialogFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.component = (Component) getArguments().getSerializable("COMPONENT");
        this.rules = (Rules) getArguments().getSerializable("RULES");
    }


    @Override
    public void setupDialog(final Dialog dialog, int style)
    {
        super.setupDialog(dialog, style);

        // Layout
        LinearLayout layout = new LinearLayout(getContext());
        layout.setLayoutParams(Util.linearLayoutParamsMatchWrap());
        layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.sheet_medium));
        layout.setOrientation(LinearLayout.VERTICAL);

        int layoutPaddingVert = (int) Util.getDim(getContext(), R.dimen.action_sheet_padding_vert);
        layout.setPadding(0, layoutPaddingVert, 0, layoutPaddingVert);

        for (final String actionName : this.component.getActions())
        {
            // Row Layout
            LinearLayout actionRowLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams actionRowLayoutParams =
                    Util.linearLayoutParamsMatchWrap();
            int actionRowMarginsVert = (int) Util.getDim(getContext(),
                    R.dimen.action_sheet_row_margins_vert);
            actionRowLayoutParams.setMargins(0, actionRowMarginsVert,
                    0, actionRowMarginsVert);
            actionRowLayoutParams.gravity = Gravity.CENTER;
            actionRowLayout.setLayoutParams(actionRowLayoutParams);
            actionRowLayout.setOrientation(LinearLayout.HORIZONTAL);
            actionRowLayout.setGravity(Gravity.CENTER_HORIZONTAL);

            final Component thisComponent = this.component;
            actionRowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    thisComponent.runAction(actionName, getContext(), rules);
                    dialog.dismiss();
                }
            });

            // Icon
            actionRowLayout.addView(Action.iconView(getContext(), actionName));

            // Action Text
            TextView actionView = new TextView(getContext());
            LinearLayout.LayoutParams actionViewLayoutParams =
                    Util.linearLayoutParamsWrap();
            int actionViewWidth = (int) Util.getDim(getContext(),
                    R.dimen.action_sheet_action_text_width);
            int actionRowHeight = (int) Util.getDim(getContext(),
                    R.dimen.action_sheet_row_height);
            actionViewLayoutParams.height = actionRowHeight;
            actionViewLayoutParams.width = actionViewWidth;
            actionView.setGravity(Gravity.CENTER_VERTICAL);
            actionView.setLayoutParams(actionViewLayoutParams);
            actionView.setText(Action.label(actionName));
            actionView.setTextColor(ContextCompat.getColor(getContext(), R.color.blue_hard));
            actionView.setTypeface(Util.sansSerifFontBold(getContext()));
            actionView.setBackgroundResource(R.drawable.bg_action_button_text);


//                    int actionViewPaddingVert = (int) Util.getDim(thisActivity,
//                                                            R.dimen.action_sheet_action_text_padding_vert);
            int actionViewPaddingLeft = (int) Util.getDim(getContext(),
                    R.dimen.action_sheet_action_text_padding_left);
            actionView.setPadding(actionViewPaddingLeft, 0,
                    actionViewPaddingLeft, 0);

            float actionViewTextSize = Util.getDim(getContext(),
                    R.dimen.action_sheet_action_text_size);
            actionView.setTextSize(actionViewTextSize);

            //actionTextLayout.addView(actionView);

            actionRowLayout.addView(actionView);

            layout.addView(actionRowLayout);
        }

        dialog.setContentView(layout);
    }

}
