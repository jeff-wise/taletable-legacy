
package com.kispoko.tome.activity.widget.text;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.util.ui.EditDialog;
import com.kispoko.tome.util.ui.EditTextBuilder;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;

import org.greenrobot.eventbus.EventBus;



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
        LinearLayout dialogLayout = EditDialog.layout(getContext());

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


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Update
    // ------------------------------------------------------------------------------------------

    private void sendTextWidgetUpdate(String newValue)
    {
        TextWidget.UpdateLiteralEvent event =
                new TextWidget.UpdateLiteralEvent(this.textWidget.getId(), newValue);

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
        EditText editValueView = editValueView(context);
        layout.addView(editValueView);

        // > Footer View
        layout.addView(footerView(editValueView, context));

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

        layout.padding.leftDp       = 12;
        layout.padding.rightDp      = 12;

        return layout.linearLayout(context);
    }


    private LinearLayout headerView(Context context)
    {
        LinearLayout layout = headerViewLayout(context);

        // > Style Button
        String styleString = context.getString(R.string.style);
        layout.addView(headerButtonView(styleString, R.drawable.ic_dialog_style, context));

        // > Widget Button
        String configureWidgetString = context.getString(R.string.widget);
        layout.addView(headerButtonView(configureWidgetString, R.drawable.ic_dialog_widget, context));

        return layout;
    }


    private LinearLayout headerViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.topDp        = 6;
        layout.padding.bottomDp     = 4;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        return layout.linearLayout(context);
    }


    private LinearLayout dividerView(Context context)
    {
        LinearLayoutBuilder divider = new LinearLayoutBuilder();

        divider.width       = LinearLayout.LayoutParams.MATCH_PARENT;
        divider.heightDp    = 1;

        divider.backgroundColor = R.color.dark_blue_6;

        return divider.linearLayout(context);
    }


    private LinearLayout headerButtonView(String labelText, int iconId, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation      = LinearLayout.HORIZONTAL;

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity          = Gravity.CENTER_VERTICAL;

        layout.margin.rightDp   = 25;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image          = iconId;

        icon.color          = R.color.dark_blue_2;

        icon.margin.rightDp = 4;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.gravity              = Gravity.CENTER_HORIZONTAL;

        label.text                 = labelText;
        label.sizeSp               = 16.0f;
        label.color                = R.color.dark_blue_1;
        label.font                 = Font.serifFontRegular(context);

        label.padding.topDp        = 12;
        label.padding.bottomDp     = 12;


        return layout.linearLayout(context);
    }


    private EditText editValueView(Context context)
    {
        EditTextBuilder value = new EditTextBuilder();

        value.width                 = LinearLayout.LayoutParams.MATCH_PARENT;
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.text                  = this.textWidget.value();
        value.font                  = Font.serifFontRegular(context);
        value.color                 = R.color.dark_blue_hl_1;
        value.sizeSp                = 20f;

        value.backgroundColor       = R.color.dark_blue_9;
        value.backgroundResource    = R.drawable.bg_edit_text_no_style;

        value.underlineColor        = R.color.dark_blue_hl_1;

        value.minHeightDp           = 100f;

        value.gravity               = Gravity.TOP;

        value.margin.topDp          = 12;
        value.margin.rightDp        = 12;
        value.padding.leftDp        = 3;

        return value.editText(context);
    }


    private LinearLayout footerView(EditText editValueView, Context context)
    {
        LinearLayout layout = footerViewLayout(context);

        // Full Editor Button
        layout.addView(fullEditorButton(context));

        // Done Button
        layout.addView(doneButton(editValueView, context));

        return layout;
    }


    private LinearLayout footerViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity          = Gravity.CENTER_VERTICAL | Gravity.END;

        layout.margin.bottomDp  = 10;

        return layout.linearLayout(context);
    }


    private LinearLayout fullEditorButton(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        TextViewBuilder     button = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.rightDp   = 15;
        layout.margin.topDp     = 2;

        layout.onClick          = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getContext(), TextEditorActivity.class);
                intent.putExtra("text_widget", textWidget);
                dismiss();
                startActivity(intent);
            }
        };


        layout.child(button);

        // [3] Button
        // -------------------------------------------------------------------------------------

        button.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        button.text             = context.getString(R.string.full_editor);
        button.font             = Font.serifFontRegular(context);
        button.color            = R.color.dark_blue_1;
        button.sizeSp           = 16f;

        return layout.linearLayout(context);
    }


    private LinearLayout doneButton(final EditText editValueView, Context context)
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

        layout.backgroundColor      = R.color.dark_blue_7;
        layout.backgroundResource   = R.drawable.bg_widget_wrap_corners_small;

        layout.padding.topDp        = 6;
        layout.padding.bottomDp     = 6;
        layout.padding.leftDp       = 6;
        layout.padding.rightDp      = 10;

        layout.onClick              = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendTextWidgetUpdate(editValueView.getText().toString());
                dismiss();
            }
        };

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = R.drawable.ic_dialog_done;

        icon.color                  = R.color.green_medium_dark;

        icon.margin.rightDp         = 3;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text                  = context.getString(R.string.done).toUpperCase();
        label.font                  = Font.serifFontBold(context);
        label.color                 = R.color.green_medium_dark;
        label.sizeSp                = 14f;


        return layout.linearLayout(context);
    }


}
