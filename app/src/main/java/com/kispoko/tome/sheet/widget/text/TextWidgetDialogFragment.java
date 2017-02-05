
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.sheet.widget.WidgetType;
import com.kispoko.tome.util.ui.EditTextBuilder;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.Form;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.RelativeLayoutBuilder;
import com.kispoko.tome.util.ui.SheetDialog;
import com.kispoko.tome.util.ui.TextViewBuilder;

import static android.R.attr.button;
import static com.kispoko.tome.R.string.widget;


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

        String widgetName = this.textWidget.data().format().name();
        String widgetType = context.getString(
                                    WidgetType.TEXT.stringLabelResourceId()).toUpperCase();
        layout.addView(SheetDialog.headerView(widgetName, widgetType, getContext()));

        // > Value
        // -------------------------------------------------------------------------------------

        layout.addView(valueView(context));

        // > Actions Row
        // -------------------------------------------------------------------------------------

        layout.addView(SheetDialog.actionsView(context));

        // > Footer View
        // -------------------------------------------------------------------------------------

        layout.addView(footerView(context));

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundResource   = R.drawable.bg_dialog;

        layout.padding.bottom       = R.dimen.dialog_padding_bottom;

        return layout.linearLayout(context);
    }


    private LinearLayout valueView(Context context)
    {
        LinearLayout layout = valueViewLayout(context);

        // > Edit View
        layout.addView(valueEditView(context));

        return layout;
    }


    private LinearLayout valueViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private LinearLayout valueEditView(Context context)
    {
        LinearLayout layout = valueEditViewLayout(context);

        // > Input
        layout.addView(this.valueInputView(this.textWidget.value(), context));

        // > Expand Button
        layout.addView(expandButtonView(context));

        return layout;
    }


    private LinearLayout valueEditViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundColor      = R.color.dark_blue_9;

        layout.margin.left          = R.dimen.dialog_padding_horz;
        layout.margin.right         = R.dimen.dialog_padding_horz;

        layout.margin.top           = R.dimen.sheet_dialog_body_margin_top;

        layout.backgroundResource   = R.drawable.bg_edit_text;

        return layout.linearLayout(context);
    }


    private LinearLayout valueInputView(String value, Context context)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        EditTextBuilder     input  = new EditTextBuilder();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.layoutGravity        = Gravity.CENTER_VERTICAL;
        layout.gravity              = Gravity.CENTER_VERTICAL;
        layout.weight               = 1.0f;

        layout.child(icon)
              .child(input);

        // [3 A] Icon
        // --------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = R.drawable.ic_form_text_field;

        icon.margin.right           = R.dimen.field_icon_margin_right;

        // [3 B] Input Text
        // --------------------------------------------------------------------------------------

        input.width                 = LinearLayout.LayoutParams.MATCH_PARENT;
        input.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        input.font                  = Font.sansSerifFontRegular(context);
        input.color                 = R.color.dark_blue_hl_1;
        input.size                  = R.dimen.field_text_input_text_size;
        input.text                  = value;

        input.backgroundResource    = R.drawable.bg_edit_text_no_style;

        return layout.linearLayout(context);
    }


    private ImageView expandButtonView(Context context)
    {
        ImageViewBuilder button = new ImageViewBuilder();

        button.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.layoutGravity    = Gravity.CENTER;
        button.weight           = 0f;

        button.image            = R.drawable.ic_full_screen_text_edit;

        return button.imageView(context);
    }


    private LinearLayout footerView(Context context)
    {
        LinearLayout layout = footerViewLayout(context);

        // > Cancel Button
        layout.addView(cancelButtonView(context));

        // > Save Button
        layout.addView(saveButtonView(context));

        return layout;
    }


    private LinearLayout footerViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.RIGHT | Gravity.CENTER_VERTICAL;

        layout.margin.left          = R.dimen.dialog_padding_horz;
        layout.margin.right         = R.dimen.dialog_padding_horz;

        layout.margin.top           = R.dimen.sheet_dialog_footer_margin_top;

        return layout.linearLayout(context);
    }


    private TextView cancelButtonView(Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        button.text             = getString(R.string.dialog_cancel).toUpperCase();
        button.font             = Font.sansSerifFontBold(context);
        button.color            = R.color.dark_blue_hl_6;
        button.size             = R.dimen.sheet_dialog_footer_button_text_size;

        button.margin.right     = R.dimen.sheet_dialog_footer_button_cancel_margin_right;

        return button.textView(context);
    }


    private LinearLayout saveButtonView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.margin.right         = R.dimen.sheet_dialog_footer_button_ok_margin_right;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = R.drawable.ic_dialog_save;

        icon.margin.right           = R.dimen.sheet_dialog_footer_button_icon_margin_right;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text                  = getString(R.string.save).toUpperCase();
        label.color                 = R.color.green_light;
        label.font                  = Font.sansSerifFontBold(context);
        label.size                  = R.dimen.sheet_dialog_footer_button_text_size;


        return layout.linearLayout(context);
    }

}
