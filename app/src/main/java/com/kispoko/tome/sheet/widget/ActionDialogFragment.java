
package com.kispoko.tome.sheet.widget;


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
import com.kispoko.tome.engine.value.ValueUnion;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;



/**
 * Action Dialog Fragment
 */
public class ActionDialogFragment extends DialogFragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private NewValueDialogListener newValueDialogListener;

    private String widgetName;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ActionDialogFragment() { }


    public static ActionDialogFragment newInstance(String widgetName)
    {
        ActionDialogFragment actionDialogFragment = new ActionDialogFragment();

        Bundle args = new Bundle();
        args.putString("widget_name", widgetName);
        actionDialogFragment.setArguments(args);

        return actionDialogFragment;
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
        this.widgetName = getArguments().getString("widget_name");

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

//        try {
//            // Instantiate the NoticeDialogListener so we can send events to the host
//            this.newValueDialogListener = (NewValueDialogListener) context;
//        } catch (ClassCastException e) {
//            // The activity doesn't implement the interface, throw exception
//            throw new ClassCastException(context.toString()
//                    + " must implement NewValueDialogListener");
//        }
    }


    // NEW VALUE DIAGLOG LISTENER
    // ------------------------------------------------------------------------------------------

    public interface NewValueDialogListener {
        public void onNewValue(ValueUnion newValue);
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    // > Dialog Layout
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

        layout.addView(headerView(context));

        // > Context
        // -------------------------------------------------------------------------------------

        layout.addView(contextView(context));


        // > Buttons
        // -------------------------------------------------------------------------------------


//        TextView widgetButton = buttonView("View Widget", context);
//        TextView rowButton    = buttonView("View its Row", context);
//        TextView groupButton  = buttonView("View its Group", context);
//        TextView pageButton   = buttonView("View its Page", context);
//        TextView editButton   = buttonView("Edit its text value", context);
//
//        layout.addView(widgetButton);
//        layout.addView(rowButton);
//        layout.addView(groupButton);
//        layout.addView(pageButton);
//        layout.addView(editButton);


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
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        TextViewBuilder     title  = new TextViewBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_HORIZONTAL;

        layout.child(title)
              .child(icon);

        // [3 A] Title
        // -------------------------------------------------------------------------------------

        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        title.font              = Font.sansSerifFontRegular(context);
        title.size              = R.dimen.widget_action_dialog_title_text_size;
        title.color             = R.color.dark_blue_hl_4;
        title.textId            = R.string.edit_the_sheet;

        title.margin.bottom     = R.dimen.widget_action_dialog_title_margin_bottom;

        // [3 B] Icon
        // -------------------------------------------------------------------------------------

        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image              = R.drawable.ic_edit_sheet;


        return layout.linearLayout(context);
    }


    private LinearLayout contextView(Context context)
    {
        LinearLayout layout = contextLayout(context);

        // > Header
        layout.addView(contextHeaderView(context));

        // > Widget
        layout.addView(contextWidgetView(context));

        return layout;
    }


    private LinearLayout contextLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private TextView contextHeaderView(Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.textId           = R.string.you_clicked;
        header.font             = Font.sansSerifFontBold(context);
        header.color            = R.color.gold_hl_9;
        header.size             = R.dimen.widget_action_dialog_heading_text_size;

        return header.textView(context);
    }


    private TextView contextWidgetView(Context context)
    {
        TextViewBuilder widget = new TextViewBuilder();

        widget.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        widget.height           = LinearLayout.LayoutParams.WRAP_CONTENT;


        return widget.textView(context);
    }


    private TextView buttonView(String label, Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        button.text             = label;
        button.font             = Font.sansSerifFontBold(context);
        button.color            = R.color.dark_blue_hl_5;
        button.size             = R.dimen.widget_action_dialog_button_text_size;

        button.padding.top      = R.dimen.widget_action_dialog_button_padding_vert;
        button.padding.bottom   = R.dimen.widget_action_dialog_button_padding_vert;
//        button.padding.left     = R.dimen.widget_action_dialog_button_padding_horz;
//        button.padding.right    = R.dimen.widget_action_dialog_button_padding_horz;

        button.backgroundResource   = R.drawable.bg_widget_dark;

        return button.textView(context);
    }


}
