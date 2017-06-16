
package com.kispoko.tome.activity.sheet.dialog;


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
import com.kispoko.tome.activity.sheet.widget.text.TextEditorActivity;
import com.kispoko.tome.lib.ui.EditDialog;
import com.kispoko.tome.lib.ui.EditTextBuilder;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.model.sheet.widget.QuoteWidget;

import org.greenrobot.eventbus.EventBus;



/**
 * Quote Widget Dialog Fragment
 */
public class QuoteWidgetDialogFragment extends DialogFragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private QuoteWidget quoteWidget;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public QuoteWidgetDialogFragment() { }


    public static QuoteWidgetDialogFragment newInstance(QuoteWidget quoteWidget)
    {
        QuoteWidgetDialogFragment quoteWidgetDialogFragment = new QuoteWidgetDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable("quote_widget", quoteWidget);
        quoteWidgetDialogFragment.setArguments(args);

        return quoteWidgetDialogFragment;
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
        this.quoteWidget = (QuoteWidget) getArguments().getSerializable("quote_widget");

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

    private void sendQuoteWidgetUpdate(String newValue)
    {
//        QuoteWidget.UpdateQuoteEvent event =
//                new QuoteWidget.UpdateQuoteEvent(this.quoteWidget.getId(), newValue);
//
//        EventBus.getDefault().post(event);
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
        EditText editValueView = editQuoteView(context);
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

        layout.padding.leftDp       = 12f;
        layout.padding.rightDp      = 12f;

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
        layout.addView(headerButtonView(configureWidgetString,
                                        R.drawable.ic_dialog_widget,
                                        context));

        return layout;
    }


    private LinearLayout headerViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.topDp        = 6f;
        layout.padding.bottomDp     = 4f;

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
        ImageViewBuilder icon   = new ImageViewBuilder();
        TextViewBuilder label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation      = LinearLayout.HORIZONTAL;

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity          = Gravity.CENTER_VERTICAL;

        layout.margin.rightDp   = 25f;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image          = iconId;

        icon.color          = R.color.dark_blue_2;

        icon.margin.rightDp = 4f;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.gravity              = Gravity.CENTER_HORIZONTAL;

        label.text                 = labelText;
        label.sizeSp               = 16.0f;
        label.color                = R.color.dark_blue_1;
        label.font                 = Font.serifFontRegular(context);

        label.padding.topDp        = 12f;
        label.padding.bottomDp     = 12f;


        return layout.linearLayout(context);
    }


    private EditText editQuoteView(Context context)
    {
        EditTextBuilder value = new EditTextBuilder();

        value.width                 = LinearLayout.LayoutParams.MATCH_PARENT;
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

//        if (this.quoteWidget != null)
//            value.text              = this.quoteWidget.quote();

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

        layout.margin.bottomDp  = 10f;

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

        layout.margin.rightDp   = 15f;
        layout.margin.topDp     = 2f;

        layout.onClick          = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getContext(), TextEditorActivity.class);
                intent.putExtra("quote_widget", quoteWidget);
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
                sendQuoteWidgetUpdate(editValueView.getText().toString());
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

        icon.margin.rightDp         = 3f;

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
