
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

import static android.R.attr.button;


/**
 * Action Dialog Fragment
 */
public class ActionDialogFragment extends DialogFragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private NewValueDialogListener newValueDialogListener;

    private String      widgetName;
    private Widget.Type widgetType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ActionDialogFragment() { }


    public static ActionDialogFragment newInstance(String widgetName, Widget.Type widgetType)
    {
        ActionDialogFragment actionDialogFragment = new ActionDialogFragment();

        Bundle args = new Bundle();
        args.putString("widget_name", widgetName);
        args.putSerializable("widget_type", widgetType);
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
        this.widgetType = (Widget.Type) getArguments().getSerializable("widget_type");

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

        // > Widget
        // -------------------------------------------------------------------------------------

        layout.addView(targetView(context));

        // > Buttons
        // -------------------------------------------------------------------------------------

        layout.addView(parentsView(context));

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



    private LinearLayout targetView(Context context)
    {
        LinearLayout layout = targetViewLayout(context);

        // > Header
        layout.addView(targetHeaderView(context));

        // > Name
        layout.addView(targetNameView(context));

        return layout;
    }


    private LinearLayout targetViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundResource  = R.drawable.bg_dialog_header;

        layout.padding.left     = R.dimen.widget_action_dialog_target_padding_horz;
        layout.padding.right    = R.dimen.widget_action_dialog_target_padding_horz;
        layout.padding.top      = R.dimen.widget_action_dialog_target_padding_top;
        layout.padding.bottom   = R.dimen.widget_action_dialog_target_padding_bottom;

        return layout.linearLayout(context);
    }


    private TextView targetHeaderView(Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.textId           = R.string.you_clicked;
        header.font             = Font.sansSerifFontBold(context);
        header.color            = R.color.dark_blue_hl_8;
        header.size             = R.dimen.widget_action_dialog_heading_text_size;

        header.margin.bottom    = R.dimen.widget_action_dialog_heading_margin_bottom;

        return header.textView(context);
    }


    private LinearLayout targetNameView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout      = new LinearLayoutBuilder();

        LinearLayoutBuilder nameLayout  = new LinearLayoutBuilder();

        ImageViewBuilder    icon        = new ImageViewBuilder();
        TextViewBuilder     name        = new TextViewBuilder();

        TextViewBuilder     type        = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER;

        layout.child(nameLayout)
              .child(type);


        // [3] Name Layout
        // -------------------------------------------------------------------------------------

        nameLayout.orientation      = LinearLayout.HORIZONTAL;
        nameLayout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        nameLayout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        nameLayout.gravity          = Gravity.CENTER_VERTICAL;

        nameLayout.margin.bottom    = R.dimen.widget_action_dialog_target_name_margin_bottom;

        nameLayout.child(icon)
                  .child(name);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image              = R.drawable.ic_launch;

        icon.margin.right       = R.dimen.widget_action_dialog_target_icon_margin_right;

        // [3 B] Name
        // -------------------------------------------------------------------------------------

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.text               = this.widgetName;
        name.font               = Font.sansSerifFontRegular(context);
        name.color              = R.color.dark_blue_hl_4;
        name.size               = R.dimen.widget_action_dialog_target_name_text_size;

        // [4] Type
        // -------------------------------------------------------------------------------------

        type.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        type.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        type.text               = getString(this.widgetType.stringLabelResourceId()).toUpperCase();
        type.font               = Font.sansSerifFontRegular(context);
        type.color              = R.color.dark_blue_1;
        type.size               = R.dimen.widget_action_dialog_target_type_text_size;


        return layout.linearLayout(context);
    }


    private LinearLayout parentsView(Context context)
    {
        LinearLayout layout = parentsLayout(context);

        // > Header
        layout.addView(parentsHeaderView(context));

        // > Buttons
        LinearLayout buttonsLayout = parentsButtonsLayout(context);

        buttonsLayout.addView(buttonView(R.string.row, context));
        buttonsLayout.addView(buttonView(R.string.group, context));
        buttonsLayout.addView(buttonView(R.string.page, context));

        layout.addView(buttonsLayout);


        return layout;
    }


    private LinearLayout parentsLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left     = R.dimen.widget_action_dialog_parents_padding_horz;
        layout.padding.right    = R.dimen.widget_action_dialog_parents_padding_horz;
        layout.padding.top      = R.dimen.widget_action_dialog_parents_padding_top;
        layout.padding.bottom   = R.dimen.widget_action_dialog_parents_padding_bottom;

        return layout.linearLayout(context);
    }


    private TextView parentsHeaderView(Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.textId           = R.string.edit_its;
        header.font             = Font.sansSerifFontBold(context);
        header.color            = R.color.gold_9;
        header.size             = R.dimen.widget_action_dialog_heading_text_size;

        header.margin.bottom    = R.dimen.widget_action_dialog_heading_margin_bottom;

        return header.textView(context);
    }


    private LinearLayout parentsButtonsLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private LinearLayout buttonView(int labelId, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     parent = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.padding.top          = R.dimen.widget_action_dialog_button_padding_vert;
        layout.padding.bottom       = R.dimen.widget_action_dialog_button_padding_vert;

        layout.child(icon)
              .child(parent);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = R.drawable.ic_launch;

        icon.margin.right           = R.dimen.widget_action_dialog_target_icon_margin_right;

        // [3 B]
        // -------------------------------------------------------------------------------------

        parent.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        parent.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        parent.textId           = labelId;
        parent.font             = Font.sansSerifFontRegular(context);
        parent.color            = R.color.dark_blue_hl_2;
        parent.size             = R.dimen.widget_action_dialog_button_text_size;


        return layout.linearLayout(context);
    }


}
