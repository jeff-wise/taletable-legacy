
package com.kispoko.tome.sheet;


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
import com.kispoko.tome.sheet.widget.WidgetType;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.SheetDialog;
import com.kispoko.tome.util.ui.TextViewBuilder;



/**
 * Action SheetDialog Fragment
 */
public class NavigationDialogFragment extends DialogFragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private NewValueDialogListener newValueDialogListener;

    private String      widgetName;
    private WidgetType  widgetType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NavigationDialogFragment() { }


    public static NavigationDialogFragment newInstance(String widgetName, WidgetType widgetType)
    {
        NavigationDialogFragment actionDialogFragment = new NavigationDialogFragment();

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
        this.widgetType = (WidgetType) getArguments().getSerializable("widget_type");

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

        // > Widget
        // -------------------------------------------------------------------------------------

        String widgetTypeString = getString(this.widgetType.stringLabelResourceId()).toUpperCase();
        layout.addView(SheetDialog.headerView(this.widgetName, widgetTypeString, getContext()));

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

        layout.backgroundResource   = R.drawable.bg_dialog_dark;

        return layout.linearLayout(context);
    }


    private LinearLayout parentsView(Context context)
    {
        LinearLayout layout = parentsLayout(context);

        // > Header
        // layout.addView(parentsHeaderView(context));

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

        layout.padding.left     = R.dimen.dialog_padding_horz;
        layout.padding.right    = R.dimen.dialog_padding_horz;

        layout.padding.bottom   = R.dimen.nav_dialog_padding_bottom;

        return layout.linearLayout(context);
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

        layout.backgroundResource   = R.drawable.bg_dialog_button;

        layout.margin.bottom        = R.dimen.nav_dialog_button_margin_bottom;

        layout.child(icon)
              .child(parent);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = R.drawable.ic_launch_button;

        icon.margin.right           = R.dimen.sheet_dialog_target_icon_margin_right;

        // [3 B]
        // -------------------------------------------------------------------------------------

        parent.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        parent.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        parent.textId           = labelId;
        parent.font             = Font.sansSerifFontRegular(context);
        parent.color            = R.color.dark_blue_hl_2;
        parent.size             = R.dimen.nav_dialog_button_text_size;


        return layout.linearLayout(context);
    }


}
