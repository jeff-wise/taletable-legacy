
package com.kispoko.tome.activity.engine.value;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.value.ValueUnion;
import com.kispoko.tome.lib.ui.EditTextBuilder;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;



/**
 * New Value SheetDialog Fragment
 */
public class NewValueDialogFragment extends DialogFragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private NewValueDialogListener newValueDialogListener;

    private EditText               newValueInput;


    // DIALOG FRAGMENT
    // ------------------------------------------------------------------------------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LinearLayout dialogLayout = dialogLayout(getContext());

        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(dialogLayout);

        int width = (int) getContext().getResources().getDimension(R.dimen.action_dialog_width);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(width, height);

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

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            this.newValueDialogListener = (NewValueDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NewValueDialogListener");
        }
    }


    // NEW VALUE DIAGLOG LISTENER
    // ------------------------------------------------------------------------------------------

    public interface NewValueDialogListener {
        public void onNewValue(ValueUnion newValue);
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    private LinearLayout dialogLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;

        return layout.linearLayout(context);
    }


    private LinearLayout view(Context context)
    {
        LinearLayout layout = viewLayout(context);

        // > Title
        layout.addView(titleView(context));

        // > Input
        this.newValueInput = valueInputView(context);
        layout.addView(this.newValueInput);

        // > Buttons
        layout.addView(buttonsView(context));

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.backgroundColor      = R.color.dark_blue_5;

        layout.padding.left         = R.dimen.value_set_new_value_dialog_padding_horz;
        layout.padding.right        = R.dimen.value_set_new_value_dialog_padding_horz;
        layout.padding.top          = R.dimen.value_set_new_value_dialog_padding_vert;
        layout.padding.bottom       = R.dimen.value_set_new_value_dialog_padding_vert;

        return layout.linearLayout(context);
    }


    private TextView titleView(Context context)
    {
        TextViewBuilder title = new TextViewBuilder();

        title.width                 = LinearLayout.LayoutParams.MATCH_PARENT;
        title.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        title.textId                = R.string.value_set_new_value_dialog_title;
        title.font                  = Font.sansSerifFontBold(context);
        title.size                  = R.dimen.value_set_new_value_dialog_title_text_size;
        title.color                 = R.color.gold_light;

        title.margin.bottom         = R.dimen.value_set_new_value_dialog_title_margin_bottom;

        return title.textView(context);
    }


    private EditText valueInputView(Context context)
    {
        EditTextBuilder input = new EditTextBuilder();

        input.width                 = LinearLayout.LayoutParams.MATCH_PARENT;
        input.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        input.font                  = Font.sansSerifFontRegular(context);
        input.color                 = R.color.dark_blue_hlx_7;
        input.size                  = R.dimen.value_set_new_value_dialog_input_text_size;
        input.backgroundResource    = R.drawable.bg_edit_text;

        input.margin.bottom         = R.dimen.value_set_data_input_margin_bottom;

        return (EditText) input.editText(context);
    }


    private LinearLayout buttonsView(Context context)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout       = new LinearLayoutBuilder();
        TextViewBuilder     cancelButton = new TextViewBuilder();
        TextViewBuilder     addButton    = new TextViewBuilder();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation              = LinearLayout.HORIZONTAL;
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity                  = Gravity.END;

        layout.padding.right       = R.dimen.value_set_new_value_dialog_button_layout_padding_right;

        layout.child(cancelButton)
              .child(addButton);

        // [3 A] Cancel Button
        // --------------------------------------------------------------------------------------

        cancelButton.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        cancelButton.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        cancelButton.textId             = R.string.dialog_cancel;
        cancelButton.font               = Font.sansSerifFontBold(context);
        cancelButton.size               = R.dimen.value_set_new_value_dialog_button_text_size;
        cancelButton.color              = R.color.dark_blue_hl_4;

        cancelButton.margin.right   = R.dimen.value_set_new_value_dialog_button_cancel_margin_right;

        cancelButton.onClick            = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        };

        // [3 B] Add Button
        // --------------------------------------------------------------------------------------

        addButton.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        addButton.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        addButton.textId                = R.string.value_set_new_value_dialog_add;
        addButton.font                  = Font.sansSerifFontBold(context);
        addButton.size                  = R.dimen.value_set_new_value_dialog_button_text_size;
        addButton.color                 = R.color.dark_blue_hlx_9;

        addButton.onClick               = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Do value sets contain values of only one type?
                //newValueDialogListener.onNewValue(newValueInput.getText().toString());
            }
        };

        return layout.linearLayout(context);
    }

}
