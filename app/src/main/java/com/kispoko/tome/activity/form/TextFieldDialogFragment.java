
package com.kispoko.tome.activity.form;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.model.form.Field;
import com.kispoko.tome.lib.ui.EditDialog;
import com.kispoko.tome.lib.ui.EditTextBuilder;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LayoutType;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.RelativeLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;



/**
 * Text Field Dialog Fragment
 */
public class TextFieldDialogFragment extends DialogFragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID        modelId;

    private Field       field;

    private EditText    valueView;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextFieldDialogFragment() { }


    public static TextFieldDialogFragment newInstance(UUID modelId, Field field)
    {
        TextFieldDialogFragment textFieldDialogFragment = new TextFieldDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable("model_id", modelId);
        args.putSerializable("field", field);
        textFieldDialogFragment.setArguments(args);

        return textFieldDialogFragment;
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
        this.modelId    = (UUID) getArguments().getSerializable("model_id");
        this.field      = (Field) getArguments().getSerializable("field");

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


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Update
    // ------------------------------------------------------------------------------------------

    private void sendTextFieldUpdate()
    {
        String valueString = this.valueView.getText().toString();
        Field.TextUpdateEvent event =
                new Field.TextUpdateEvent(this.modelId, this.field.name(), valueString);

        EventBus.getDefault().post(event);
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    private View view(Context context)
    {
        LinearLayout layout = viewLayout(context);

        // > Header
        layout.addView(headerView(context));

        layout.addView(dividerView(context));

        // > Edit Field
        this.valueView = editValueView(context);
        layout.addView(this.valueView);

        // > Footer View
        layout.addView(footerView(context));

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundColor      = R.color.dark_blue_9;
        layout.backgroundResource   = R.drawable.bg_dialog;

        layout.padding.leftDp       = 12f;
        layout.padding.rightDp      = 12f;

        return layout.linearLayout(context);
    }


    private RelativeLayout headerView(Context context)
    {
        RelativeLayout layout = headerViewLayout(context);

        // > Name
        layout.addView(this.fieldNameView(context));

        // > Open Activity Button
        layout.addView(this.fullscreenButtonView(context));

        return layout;
    }


    private RelativeLayout headerViewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.topDp        = 10f;
        layout.padding.bottomDp     = 10f;
        layout.padding.leftDp       = 2f;
        layout.padding.rightDp      = 2f;

        return layout.relativeLayout(context);
    }


    private TextView fieldNameView(Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.layoutType         = LayoutType.RELATIVE;
        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        if (this.field != null)
            name.text           = this.field.label();

        name.font               = Font.serifFontRegular(context);
        name.color              = R.color.dark_blue_hl_9;
        name.sizeSp             = 19f;

        name.addRule(RelativeLayout.ALIGN_PARENT_START);
        name.addRule(RelativeLayout.CENTER_VERTICAL);

        return name.textView(context);
    }


    private ImageView fullscreenButtonView(Context context)
    {
        ImageViewBuilder button = new ImageViewBuilder();

        button.layoutType       = LayoutType.RELATIVE;
        button.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        button.image            = R.drawable.ic_form_dialog_text_field_open;
        button.color            = R.color.dark_blue_1;

        button.addRule(RelativeLayout.ALIGN_PARENT_END);
        button.addRule(RelativeLayout.CENTER_VERTICAL);

        return button.imageView(context);
    }


    private LinearLayout dividerView(Context context)
    {
        LinearLayoutBuilder divider = new LinearLayoutBuilder();

        divider.width       = LinearLayout.LayoutParams.MATCH_PARENT;
        divider.heightDp    = 1;

        divider.backgroundColor = R.color.dark_blue_5;

        return divider.linearLayout(context);
    }


    private EditText editValueView(Context context)
    {
        EditTextBuilder value = new EditTextBuilder();

        value.width                 = LinearLayout.LayoutParams.MATCH_PARENT;
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.text                  = this.field.value();

        value.font                  = Font.serifFontRegular(context);
        value.color                 = R.color.dark_blue_hl_1;
        value.sizeSp                = 20f;

        value.backgroundColor       = R.color.dark_blue_9;
        value.backgroundResource    = R.drawable.bg_edit_text_no_style;

        value.underlineColor        = R.color.dark_blue_hl_1;

        value.minHeightDp           = 100f;

        value.gravity               = Gravity.TOP;

        value.margin.topDp          = 12f;
        value.margin.bottomDp       = 12f;

        value.margin.rightDp        = 12f;
        value.padding.leftDp        = 3f;

        return value.editText(context);
    }


    private LinearLayout footerView(Context context)
    {
        LinearLayout layout = footerViewLayout(context);

        // Full Editor Button
        layout.addView(lastSavedView(context));

        // Done Button
        layout.addView(saveButton(context));

        return layout;
    }


    private LinearLayout footerViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity          = Gravity.CENTER_VERTICAL | Gravity.END;

        layout.margin.bottomDp  = 10f;

        return layout.linearLayout(context);
    }


    private TextView lastSavedView(Context context)
    {
        TextViewBuilder date = new TextViewBuilder();

        date.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        date.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        date.text               = "03/21/17 14:31";
        date.font               = Font.serifFontItalic(context);
        date.color              = R.color.dark_blue_1;
        date.sizeSp             = 16f;

        date.margin.rightDp     = 10f;

        return date.textView(context);
    }


    private LinearLayout saveButton(Context context)
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

        layout.backgroundColor      = R.color.dark_blue_7;
        layout.backgroundResource   = R.drawable.bg_sheet_corners_small;

        layout.padding.topDp        = 6f;
        layout.padding.bottomDp     = 6f;
        layout.padding.leftDp       = 6f;
        layout.padding.rightDp      = 10f;

        layout.onClick              = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendTextFieldUpdate();
                dismiss();
            }
        };

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = R.drawable.ic_form_text_field_dialog_save;

        icon.color                  = R.color.green_medium_dark;

        icon.margin.rightDp         = 3f;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text                  = context.getString(R.string.save).toUpperCase();
        label.font                  = Font.serifFontBold(context);
        label.color                 = R.color.green_medium_dark;
        label.sizeSp                = 14f;


        return layout.linearLayout(context);
    }

}
