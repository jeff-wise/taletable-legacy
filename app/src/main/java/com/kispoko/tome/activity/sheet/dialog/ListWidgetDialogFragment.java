
package com.kispoko.tome.activity.sheet.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.EditDialog;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.SwitchBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;



/**
 * List Widget Dialog
 */
//public class ListWidgetDialogFragment extends DialogFragment
//{
//
//    // PROPERTIES
//    // ------------------------------------------------------------------------------------------
//
//    private ListWidget listWidget;
//
//    /** 1-indexed */
//    private int        itemClicked;
//
//
//    // CONSTRUCTORS
//    // ------------------------------------------------------------------------------------------
//
//    public ListWidgetDialogFragment() { }
//
//
//    public static ListWidgetDialogFragment newInstance(ListWidget listWidget, int itemClicked)
//    {
//        ListWidgetDialogFragment listWidgetDialogFragment = new ListWidgetDialogFragment();
//
//        Bundle args = new Bundle();
//        args.putSerializable("list_widget", listWidget);
//        args.putInt("item_clicked", itemClicked);
//        listWidgetDialogFragment.setArguments(args);
//
//        return listWidgetDialogFragment;
//    }
//
//
//    // DIALOG FRAGMENT
//    // ------------------------------------------------------------------------------------------
//
//    @Override @NonNull
//    public Dialog onCreateDialog(Bundle savedInstanceState)
//    {
//        LinearLayout dialogLayout = EditDialog.layout(getContext());
//
//        final Dialog dialog = new Dialog(getActivity());
//
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        dialog.setContentView(dialogLayout);
//
//        int width = (int) getContext().getResources().getDimension(R.dimen.action_dialog_width);
//        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        dialog.getWindow().setLayout(width, height);
//
//        // > Read State
//        this.listWidget  = (ListWidget) getArguments().getSerializable("list_widget");
//        this.itemClicked = getArguments().getInt("item_clicked");
//
//        return dialog;
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater layoutInflater,
//                             ViewGroup container,
//                             Bundle savedInstanceState)
//    {
//        return this.view(getContext());
//    }
//
//
//    @Override
//    public void onAttach(Context context)
//    {
//        super.onAttach(context);
//    }
//
//
//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    // > Item Clicked Value String
//    // ------------------------------------------------------------------------------------------
//
//    private String itemClickedValueString()
//    {
////        if (this.listWidget != null)
////        {
////            if (this.itemClicked > 0 &&
////                this.itemClicked <= this.listWidget.values().size())
////            {
////                // > Get value variable union
////                VariableUnion itemVariableUnion = this.listWidget.values()
////                                                      .get(this.itemClicked - 1);
////
////                // > Get its value string
////                try {
////                    return itemVariableUnion.variable().valueString();
////                }
////                catch (NullVariableException exception) {
////                    ApplicationFailure.nullVariable(exception);
////                    return "";
////                }
////            }
////        }
//
//        return "";
//    }
//
//
//    // > Views
//    // ------------------------------------------------------------------------------------------
//
//    private LinearLayout view(Context context)
//    {
//        LinearLayout layout = viewLayout(context);
//
//        // > List
//        // layout.addView(listEditorButtonView(context));
//
//        // > Tab (Main Content) View
//        layout.addView(tabView(context));
//
//        // > Content
//        layout.addView(contentView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout viewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.VERTICAL;
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.backgroundColor      = R.color.dark_blue_10;
//        layout.backgroundResource   = R.drawable.bg_dialog;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout tabView(Context context)
//    {
//        LinearLayout layout = tabViewLayout(context);
//
//        // > Tab Bar
//        layout.addView(tabBarView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout tabViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.VERTICAL;
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout tabBarView(Context context)
//    {
//        LinearLayout layout = tabBarViewLayout(context);
//
//        // Edit Button
//        String editButtonLabel = context.getString(R.string.edit_item);
//        LinearLayout editTabView = this.tabButtonView(editButtonLabel, null, context);
//        layout.addView(editTabView);
//
//        // New Button
//        String newButtonLabel = context.getString(R.string.new_item);
//        LinearLayout newTabView = this.tabButtonView(newButtonLabel,
//                                                     R.drawable.ic_dialog_list_new_item,
//                                                     context);
//        layout.addView(newTabView);
//
//        selectTabView(editTabView, newTabView, context);
//
//        return layout;
//    }
//
//
//    private void selectTabView(LinearLayout selectedTabView,
//                               LinearLayout unselectedTabView,
//                               Context context)
//    {
//        // Set Tab Backgrounds
//        selectedTabView.setBackground(
//                ContextCompat.getDrawable(context, R.drawable.bg_dialog_list_widget_tab));
//        unselectedTabView.setBackgroundResource(0);
//
//        // Set Tab Text Colors
//        TextView tabLabelView = (TextView) selectedTabView.findViewById(R.id.tab_label);
//        tabLabelView.setTextColor(ContextCompat.getColor(context, R.color.dark_blue_hl_2));
//    }
//
//
//    private LinearLayout tabBarViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.HORIZONTAL;
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.margin.topDp         = 20f;
//
//        layout.margin.leftDp        = 10f;
//        layout.margin.rightDp       = 10f;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout tabButtonView(String labelText, Integer iconId, Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//        ImageViewBuilder    icon   = new ImageViewBuilder();
//        TextViewBuilder     label  = new TextViewBuilder();
//
//        // [2] Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.orientation          = LinearLayout.HORIZONTAL;
//
//        layout.width                = 0;
//        layout.heightDp             = 34;
//        layout.weight               = 1f;
//
//        layout.gravity              = Gravity.CENTER;
//
//        if (iconId != null)
//            layout.child(icon);
//
//        layout.child(label);
//
//        // [3 A] Icon
//        // -------------------------------------------------------------------------------------
//
//        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
//        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        icon.image                  = iconId;
//
//        icon.color                  = R.color.dark_blue_1;
//
//        icon.margin.rightDp         = 3f;
//
//        // [3 B] Label
//        // -------------------------------------------------------------------------------------
//
//        label.id                    = R.id.tab_label;
//
//        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
//        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        label.text                  = labelText.toUpperCase();
////        label.font                  = Font.serifFontRegular(context);
//        label.color                 = R.color.dark_blue_1;
//        label.sizeSp                = 14f;
//
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout contentView(Context context)
//    {
//        LinearLayout layout = this.contentViewLayout(context);
//
//        // > Value View
//        View itemValueView = this.itemValueView(context);
//        if (itemValueView != null)
//            layout.addView(itemValueView);
//
//        // > Highlight Item Button
//        layout.addView(this.highlightButtonView(context));
//
//        // > Delete Button
//        layout.addView(this.deleteButtonView(context));
//
//        // > Divider View
//        layout.addView(this.dividerView(context));
//
//        // > General Buttons
//        layout.addView(this.itemGeneralButtonsView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout contentViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.VERTICAL;
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.margin.leftDp        = 10f;
//        layout.margin.rightDp       = 10f;
//
//        layout.margin.bottomDp      = 10f;
//
//        layout.padding.leftDp       = 10f;
//        layout.padding.rightDp      = 10f;
//        layout.padding.topDp        = 20f;
//
//        layout.backgroundColor      = R.color.dark_blue_7;
//        layout.backgroundResource   = R.drawable.bg_dialog_list_widget_content;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private View itemValueView(Context context)
//    {
////        if (this.listWidget != null)
////        {
////            if (this.listWidget.valueSetName() != null)
////                return this.valueChooserButtonView(context);
////            else
////                return this.defaultValueView(context);
////        }
//
//        return null;
//    }
//
//
//    private TextView defaultValueView(Context context)
//    {
//        TextViewBuilder button = new TextViewBuilder();
//
//        button.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        button.text             = this.itemClickedValueString();
////        button.font             = Font.serifFontRegular(context);
//        button.color            = R.color.dark_blue_hl_2;
//        button.sizeSp           = 17f;
//
//        return button.textView(context);
//    }
//
//
//    private LinearLayout valueChooserButtonView(Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        LinearLayoutBuilder layout  = new LinearLayoutBuilder();
//        TextViewBuilder     label   = new TextViewBuilder();
//        TextViewBuilder     value   = new TextViewBuilder();
//
//        // [2] Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.orientation          = LinearLayout.HORIZONTAL;
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
////        layout.padding.bottomDp     = 2f;
////        layout.padding.leftDp       = 8f;
////        layout.padding.rightDp      = 7f;
//
//        layout.backgroundResource   = R.drawable.bg_dialog_list_widget_chooser;
//        layout.backgroundColor      = R.color.dark_blue_3;
//
//        layout.elevation            = 12f;
//
//        layout.margin.leftDp        = 3f;
//        layout.margin.rightDp       = 3f;
//
//        layout.padding.topDp        = 10f;
//        layout.padding.bottomDp     = 10f;
//        layout.padding.leftDp       = 8f;
//        layout.padding.rightDp      = 8f;
//
//        layout.gravity              = Gravity.CENTER_VERTICAL;
//
//        layout//.child(label)
//              .child(value);
//
//        // [3] Button
//        // -------------------------------------------------------------------------------------
//
//        label.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
//        label.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        label.text                 = "LANGUAGE";
//
////        label.font                 = Font.serifFontRegular(context);
//        label.color                = R.color.dark_blue_hl_8;
//        label.sizeSp               = 13f;
//
//        label.margin.rightDp       = 7f;
//
//        // [4] Value
//        // -------------------------------------------------------------------------------------
//
//        value.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
//        value.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        value.text                 = this.itemClickedValueString();
////        value.font                 = Font.serifFontRegular(context);
//        value.color                = R.color.gold_light;
//        value.sizeSp               = 18f;
//
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout highlightButtonView(Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        LinearLayoutBuilder layout     = new LinearLayoutBuilder();
//        SwitchBuilder       switchView = new SwitchBuilder(R.style.SwitchStyle);
//        TextViewBuilder     label      = new TextViewBuilder();
//
//        // [2] Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.orientation          = LinearLayout.HORIZONTAL;
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity              = Gravity.CENTER_VERTICAL;
//
//        layout.margin.topDp         = 15f;
//
//        layout.child(switchView)
//              .child(label);
//
//        // [3 A] Switch
//        // -------------------------------------------------------------------------------------
//
//        switchView.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
//        switchView.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        switchView.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//
//        switchView.checked              = false;
//
//        switchView.scaleX               = 0.85f;
//        switchView.scaleY               = 0.85f;
//
//        // [3 B] Label
//        // -------------------------------------------------------------------------------------
//
//        label.width                     = LinearLayout.LayoutParams.WRAP_CONTENT;
//        label.height                    = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        label.textId                    = R.string.highlight_item;
////        label.font                      = Font.serifFontRegular(context);
//        label.color                     = R.color.dark_blue_hl_4;
//        label.sizeSp                    = 14f;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout deleteButtonView(Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//        ImageViewBuilder    icon   = new ImageViewBuilder();
//        TextViewBuilder     label  = new TextViewBuilder();
//
//        // [2] Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.orientation      = LinearLayout.HORIZONTAL;
//
//        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity          = Gravity.CENTER_VERTICAL;
//
//        layout.margin.topDp     = 15f;
//        layout.margin.bottomDp  = 15f;
//
//        layout.child(icon)
//              .child(label);
//
//        // [3 A] Icon
//        // -------------------------------------------------------------------------------------
//
//        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
//        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        icon.image              = R.drawable.ic_dialog_list_delete_item;
//
//        icon.color              = R.color.dark_blue_hl_5;
//
//        // [3 B] Label
//        // -------------------------------------------------------------------------------------
//
//        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
//        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;
//
////        label.font              = Font.serifFontRegular(context);
//        label.color             = R.color.dark_blue_hl_4;
//        label.sizeSp            = 14f;
//
//        label.textId            = R.string.delete_item;
//
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout dividerView(Context context)
//    {
//        LinearLayoutBuilder divider = new LinearLayoutBuilder();
//
//        divider.width       = LinearLayout.LayoutParams.MATCH_PARENT;
//        divider.heightDp    = 1;
//
//        divider.backgroundColor = R.color.dark_blue_6;
//
//        return divider.linearLayout(context);
//    }
//
//
//    private LinearLayout itemGeneralButtonsView(Context context)
//    {
//        LinearLayout layout = itemGeneralButtonsViewLayout(context);
//
//        // > Style Button
//        String styleString = context.getString(R.string.style);
//        layout.addView(itemGeneralButtonView(styleString, R.drawable.ic_dialog_style, context));
//
//        // > Widget Button
//        String configureWidgetString = context.getString(R.string.widget);
//        layout.addView(itemGeneralButtonView(configureWidgetString,
//                                             R.drawable.ic_dialog_widget,
//                                             context));
//
//        return layout;
//    }
//
//
//    private LinearLayout itemGeneralButtonsViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.HORIZONTAL;
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity              = Gravity.CENTER_VERTICAL;
//
//        layout.padding.topDp        = 10f;
//        layout.padding.bottomDp     = 10f;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout itemGeneralButtonView(String labelText, int iconId, Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//        ImageViewBuilder    icon   = new ImageViewBuilder();
//        TextViewBuilder     label  = new TextViewBuilder();
//
//        // [2] Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.orientation      = LinearLayout.HORIZONTAL;
//
//        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity          = Gravity.CENTER_VERTICAL;
//
//        layout.margin.rightDp   = 25f;
//
//        layout.child(icon)
//              .child(label);
//
//        // [3 A] Icon
//        // -------------------------------------------------------------------------------------
//
//        icon.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
//        icon.height         = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        icon.image          = iconId;
//
//        icon.color          = R.color.dark_blue_1;
//
//        icon.margin.rightDp = 4f;
//
//        // [3 B] Label
//        // -------------------------------------------------------------------------------------
//
//        label.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
//        label.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        label.gravity              = Gravity.CENTER_HORIZONTAL;
//
//        label.text                 = labelText;
//        label.sizeSp               = 16.0f;
//        label.color                = R.color.dark_blue_1;
////        label.font                 = Font.serifFontRegular(context);
//
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout listEditorButtonView(Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//        ImageViewBuilder    icon   = new ImageViewBuilder();
//        TextViewBuilder     label  = new TextViewBuilder();
//
//        // [2] Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.orientation          = LinearLayout.HORIZONTAL;
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity              = Gravity.CENTER_HORIZONTAL;
//
//        layout.child(icon)
//              .child(label);
//
//        // [3 A] Icon
//        // -------------------------------------------------------------------------------------
//
//        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
//        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        icon.image                  = R.drawable.ic_dialog_list_editor;
//
//        icon.color                  = R.color.dark_blue_hl_5;
//
//        // [3 B] Label
//        // -------------------------------------------------------------------------------------
//
//        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
//        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;
//
////        label.font                  = Font.serifFontRegular(context);
//        label.color                 = R.color.dark_blue_hl_5;
//        label.sizeSp                = 17f;
//
//        label.textId                = R.string.list_editor;
//
//
//        return layout.linearLayout(context);
//    }
//
//
//}
