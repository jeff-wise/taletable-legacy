
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
import com.kispoko.tome.lib.ui.EditDialog;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.NumberPickerBuilder;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.List;



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

        // > Value Chooser
        layout.addView(valueChooserView(context));

        // > Footer
        List<String> secondaryButtonNames = new ArrayList<>();
        secondaryButtonNames.add(context.getString(R.string.advanced_editor));
        LinearLayout footerView = EditDialog.footerView(secondaryButtonNames,
                                                        context.getString(R.string.save),
                                                        R.drawable.ic_dialog_footer_button_save,
                                                        EditDialog.Shade.LIGHT,
                                                        context);
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
        layout.addView(EditDialog.headerTitleView(this.variableName,
                                                  EditDialog.Shade.LIGHT, context));

        return layout;
    }


    private LinearLayout valueChooserView(Context context)
    {
        LinearLayout layout = this.valueChooserViewLayout(context);

        layout.addView(this.valuePickerView(context));

        return layout;
    }


    private LinearLayout valueChooserViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.top          = R.dimen.dialog_increment_value_padding_top;
        layout.padding.bottom       = R.dimen.dialog_increment_value_padding_bottom;

        layout.backgroundColor      = R.color.dark_blue_5;

        layout.margin.bottom        = R.dimen.one_dp;

        return layout.linearLayout(context);
    }


    private NumberPicker valuePickerView(Context context)
    {
        NumberPickerBuilder chooser = new NumberPickerBuilder();

        chooser.width               = LinearLayout.LayoutParams.MATCH_PARENT;
        chooser.height              = LinearLayout.LayoutParams.WRAP_CONTENT;

        chooser.orientation         = NumberPicker.HORIZONTAL;

        chooser.minValue            = 0;
        chooser.maxValue            = 100;

        chooser.textSize            = R.dimen.dialog_increment_value_text_size;
        chooser.textColor           = R.color.dark_blue_hl_1;
//        chooser.textFont            = Font.serifFontRegular(context);

        chooser.dividerColor        = R.color.dark_blue_2;

        chooser.wheelItemCount      = 5;

        return chooser.numberPicker(context);
    }

}
