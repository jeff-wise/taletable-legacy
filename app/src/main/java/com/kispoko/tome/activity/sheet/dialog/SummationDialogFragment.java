
package com.kispoko.tome.activity.sheet.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.summation.Summation;
import com.kispoko.tome.util.ui.EditDialog;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


/**
 * Summation Dialog Fragment
 */
public class SummationDialogFragment extends DialogFragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Summation summation;
    private String    summationLabel;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public SummationDialogFragment() { }


    public static SummationDialogFragment newInstance(Summation summation, String summationLabel)
    {
        SummationDialogFragment summationDialogFragment = new SummationDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable("summation", summation);
        args.putString("summation_label", summationLabel);
        summationDialogFragment.setArguments(args);

        return summationDialogFragment;
    }


    // DIALOG FRAGMENT
    // ------------------------------------------------------------------------------------------

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LinearLayout dialogLayout = EditDialog.layout(getContext());

        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(dialogLayout);

        int width = (int) getContext().getResources().getDimension(R.dimen.action_dialog_width);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(width, height);

        // > Read State
        this.summation      = (Summation) getArguments().getSerializable("summation");
        this.summationLabel = getArguments().getString("summation_label");

        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater layoutInflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        return this.view(getContext());
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    // > Views
    // ------------------------------------------------------------------------------------------

    private View view(Context context)
    {
        LinearLayout layout = EditDialog.viewLayout(EditDialog.Shade.LIGHT, context);

        // > Header
        layout.addView(headerView(context));

        // > Summation Components
        layout.addView(componentsView(context));

        // > Bottom Divider
        layout.addView(SummationView.componentDividerView(context));

        // > Footer
        LinearLayout footerView = EditDialog.footerView(new ArrayList<String>(),
                                                        context.getString(R.string.edit),
                                                        R.drawable.ic_dialog_footer_button_edit,
                                                        false,
                                                        EditDialog.Shade.LIGHT,
                                                        context);
        layout.addView(footerView);

        return layout;
    }


    private LinearLayout headerView(Context context)
    {
        LinearLayout layout = EditDialog.headerViewLayout(context);

        // > Top Row
        layout.addView(EditDialog.headerTitleView(this.summationLabel,
                                                  EditDialog.Shade.LIGHT,
                                                  context));

        return layout;
    }


    private LinearLayout componentsView(Context context)
    {
        LinearLayout layout = componentsViewLayout(context);

        // > Components
        layout.addView(SummationView.componentsView(this.summation, context));

        return layout;
    }


    private LinearLayout componentsViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.top       = R.dimen.dialog_summ_components_margin_top;

        return layout.linearLayout(context);
    }



}
