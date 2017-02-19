
package com.kispoko.tome.activity.sheet.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.util.ui.AppDialog;
import com.kispoko.tome.util.ui.EditDialog;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.kispoko.tome.util.ui.EditDialog.viewLayout;


/**
 * Increment Dialog Fragment
 *
 * Dialog for incrementing and decrementing a number variable.
 */
public class IncrementDialogFragment extends DialogFragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String  variableName;
    private Integer variableValue;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public IncrementDialogFragment() { }


    public static IncrementDialogFragment newInstance(String variableName, Integer variableValue)
    {
        IncrementDialogFragment incrementDialogFragment = new IncrementDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable("variable_name", variableName);
        args.putSerializable("variable_value", variableValue);
        incrementDialogFragment.setArguments(args);

        return incrementDialogFragment;
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
        this.variableName =  getArguments().getString("variable_name");
        this.variableValue = getArguments().getInt("variable_value");

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
        LinearLayout layout = this.viewLayout(context);

        // > Header
        layout.addView(headerView(context));

        // > Value Editor
        LinearLayout valueLayout = valueViewLayout(context);
        valueLayout.addView(valueView(context));
        layout.addView(valueLayout);

        // > Footer
        List<String> secondaryButtonNames = new ArrayList<>();
        secondaryButtonNames.add(context.getString(R.string.advanced_editor));
        LinearLayout footerView = EditDialog.footerView(secondaryButtonNames, false, context);
        layout.addView(footerView);

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundResource   = R.drawable.bg_dialog;

        return layout.linearLayout(context);
    }


    private LinearLayout headerView(Context context)
    {
        LinearLayout layout = EditDialog.headerViewLayout(context);

        // > Top Row
        layout.addView(EditDialog.headerTitleView(this.variableName, false, context));

        return layout;
    }


    private LinearLayout valueViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.top      = R.dimen.dialog_increment_value_padding_top;
        layout.padding.bottom   = R.dimen.dialog_increment_value_padding_bottom;

        return layout.linearLayout(context);
    }


    private LinearLayout valueView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout    = new LinearLayoutBuilder();
        TextViewBuilder     value     = new TextViewBuilder();
        TextViewBuilder     decButton = new TextViewBuilder();
        TextViewBuilder     incButton = new TextViewBuilder();

        // [2] Declarations
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.backgroundResource  = R.drawable.bg_dialog_inc_button;

        layout.margin.left          = R.dimen.dialog_increment_value_margin_horz;
        layout.margin.right          = R.dimen.dialog_increment_value_margin_horz;
        layout.margin.top          = R.dimen.one_dp;
        layout.margin.bottom          = R.dimen.one_dp;

        layout.child(decButton)
              .child(value)
              .child(incButton);

        // [3] Value
        // -------------------------------------------------------------------------------------

        value.width             = 0;
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.weight            = 1.0f;

        value.gravity           = Gravity.CENTER_HORIZONTAL;

        value.text              = this.variableValue.toString();
        value.color             = R.color.dark_blue_hl_2;
        value.font              = Font.serifFontRegular(context);
        value.size              = R.dimen.dialog_increment_value_text_size;

        // [4 A] Decrement Button
        // -------------------------------------------------------------------------------------

        decButton.width                 = 0;
        decButton.height                = LinearLayout.LayoutParams.WRAP_CONTENT;
        decButton.weight                = 1.0f;
        decButton.gravity               = Gravity.CENTER_HORIZONTAL;

        decButton.text                  = "- 1";
        decButton.color                 = R.color.dark_blue_hl_8;
        decButton.font                  = Font.serifFontBold(context);
        decButton.size                  = R.dimen.dialog_increment_button_text_size;


        // [4 B] Increment Button
        // -------------------------------------------------------------------------------------

        incButton.width                 = 0;
        incButton.height                = LinearLayout.LayoutParams.WRAP_CONTENT;
        incButton.weight                = 1.0f;
        incButton.gravity               = Gravity.CENTER_HORIZONTAL;

        incButton.text                  = "+ 1";
        incButton.color                 = R.color.dark_blue_hl_8;
        incButton.font                  = Font.serifFontBold(context);
        incButton.size                  = R.dimen.dialog_increment_button_text_size;


        return layout.linearLayout(context);
    }


}
