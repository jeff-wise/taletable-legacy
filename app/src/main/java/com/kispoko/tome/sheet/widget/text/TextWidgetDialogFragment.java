
package com.kispoko.tome.sheet.widget.text;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.util.ui.EditDialog;
import com.kispoko.tome.util.ui.EditTextBuilder;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;

import static com.kispoko.tome.util.ui.EditDialog.footerView;


/**
 * Text Widget SheetDialog Fragment
 *
 * This dialog presents some quick actions for a text widget
 */
public class TextWidgetDialogFragment extends DialogFragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private TextWidget textWidget;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextWidgetDialogFragment() { }


    public static TextWidgetDialogFragment newInstance(TextWidget textWidget)
    {
        TextWidgetDialogFragment textWidgetDialogFragment = new TextWidgetDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable("text_widget", textWidget);
        textWidgetDialogFragment.setArguments(args);

        return textWidgetDialogFragment;
    }


    // DIALOG FRAGMENT
    // ------------------------------------------------------------------------------------------

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LinearLayout dialogLayout = this.dialogLayout(getContext());

        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(dialogLayout);

        int width = (int) getContext().getResources().getDimension(R.dimen.action_dialog_width);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(width, height);

        // > Read State
        this.textWidget = (TextWidget) getArguments().getSerializable("text_widget");

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

    // > SheetDialog Layout
    // ------------------------------------------------------------------------------------------

    private LinearLayout dialogLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.backgroundColor      = R.color.dark_blue_5;

        return layout.linearLayout(context);
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    private View view(Context context)
    {
        LinearLayout layout = viewLayout(context);

        // > Header
        // -------------------------------------------------------------------------------------

        String headerString         = context.getString(R.string.edit) + " ";

        String label = this.textWidget.valueVariable().label();
        if (label != null)
            headerString += label;

        layout.addView(EditDialog.headerView(headerString, context));

        // > Input
        // -------------------------------------------------------------------------------------

        layout.addView(inputView(this.textWidget.value(), context));

        // > Expand Button
        // -------------------------------------------------------------------------------------

        layout.addView(expandButtonView(context));

        // > Footer
        // -------------------------------------------------------------------------------------

        layout.addView(EditDialog.footerView(context));

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundResource   = R.drawable.bg_dialog;

        layout.padding.bottom       = R.dimen.dialog_edit_padding_vert;
        layout.padding.top          = R.dimen.dialog_edit_padding_vert;

        return layout.linearLayout(context);
    }


    private View inputView(String value, Context context)
    {
        EditTextBuilder input  = new EditTextBuilder();

        input.width                 = LinearLayout.LayoutParams.MATCH_PARENT;
        input.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        input.font                  = Font.serifFontRegular(context);
        input.color                 = R.color.dark_blue_hl_1;
        input.size                  = R.dimen.field_text_input_text_size;
        input.text                  = value;

        input.backgroundResource    = R.drawable.bg_edit_text;

        input.margin.left           = R.dimen.dialog_edit_padding_horz;
        input.margin.right          = R.dimen.dialog_edit_padding_horz;

        return input.editText(context);
    }


    private LinearLayout expandButtonView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.layoutGravity        = Gravity.CENTER_HORIZONTAL;
        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.margin.top           = R.dimen.dialog_edit_text_expand_button_margin_top;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image              = R.drawable.ic_full_screen_text_edit;

        icon.margin.right       = R.dimen.five_dp;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.textId            = R.string.edit_full_screen;
        label.font              = Font.serifFontRegular(context);
        label.color             = R.color.dark_blue_1;
        label.size              = R.dimen.dialog_edit_text_expand_button_text_size;


        return layout.linearLayout(context);
    }

}
