
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;

import java.util.List;



/**
 * Edit Dialog UI Common Components
 */
public class EditDialog
{


    // (DIALOG) LAYOUT
    // -----------------------------------------------------------------------------------------

    public static LinearLayout layout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.backgroundColor      = R.color.dark_blue_5;

        return layout.linearLayout(context);
    }


    // VIEW LAYOUT
    // -----------------------------------------------------------------------------------------

    // TODO remove?
    public static LinearLayout viewLayout(Context context)
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


    // HEADER
    // -----------------------------------------------------------------------------------------

    public static TextView headerView(String headerText, Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.margin.bottom        = R.dimen.dialog_edit_text_header_margin_bottom;
        header.margin.left          = R.dimen.one_dp;

        header.text                 = headerText;

        header.font                 = Font.serifFontRegular(context);
        header.color                = R.color.gold_medium_light;
        header.size                 = R.dimen.dialog_edit_header_text_size;

        header.padding.left         = R.dimen.dialog_edit_padding_horz;
        header.padding.right        = R.dimen.dialog_edit_padding_horz;

        return header.textView(context);
    }


    // DARK HEADER VIEW LAYOUT
    // -----------------------------------------------------------------------------------------

    public static LinearLayout headerViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.top          = R.dimen.dialog_edit_header_padding_top;
        layout.padding.left         = R.dimen.dialog_edit_header_padding_horz;
        layout.padding.right        = R.dimen.dialog_edit_header_padding_horz;

        layout.backgroundResource   = R.drawable.bg_dialog_header;

        return layout.linearLayout(context);
    }


    // HEADER TITLE VIEW
    // -----------------------------------------------------------------------------------------

    public static RelativeLayout headerTitleView(String headerString,
                                                 boolean isDark,
                                                 Context context)
    {
        RelativeLayout layout = headerTitleViewLayout(context);

        // > Label
        layout.addView(headerTitleTextView(headerString, isDark, context));

        // > Close Button
        layout.addView(closeButtonView(isDark, context));

        return layout;
    }


    private static RelativeLayout headerTitleViewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_VERTICAL;

        return layout.relativeLayout(context);
    }


    private static TextView headerTitleTextView(String headerString, boolean isDark, Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.layoutType       = LayoutType.RELATIVE;
        header.width            = RelativeLayout.LayoutParams.WRAP_CONTENT;
        header.height           = RelativeLayout.LayoutParams.WRAP_CONTENT;

        header.text             = headerString;
        header.font             = Font.serifFontRegular(context);

        header.size             = R.dimen.sheet_dialog_heading_text_size;

        // > Color
        if (isDark)
            header.color            = R.color.dark_blue_hl_9;
        else
            header.color            = R.color.dark_blue_hl_5;

        header.margin.top       = R.dimen.one_dp;

        header.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        return header.textView(context);
    }


    private static ImageView closeButtonView(boolean isDark, Context context)
    {
        ImageViewBuilder button = new ImageViewBuilder();

        button.layoutType           = LayoutType.RELATIVE;
        button.width                = RelativeLayout.LayoutParams.WRAP_CONTENT;
        button.height               = RelativeLayout.LayoutParams.WRAP_CONTENT;
        button.layoutGravity        = Gravity.CENTER;

        if (isDark)
            button.image                = R.drawable.ic_dialog_close;
        else
            button.image                = R.drawable.ic_dialog_close_light;

        button.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        return button.imageView(context);
    }


    // FOOTER
    // -----------------------------------------------------------------------------------------

    public static LinearLayout footerView(List<String> secondaryButtons,
                                          boolean isDark,
                                          Context context)
    {
        LinearLayout layout = footerViewLayout(isDark, context);

        // > Cancel Button
        for (String buttonLabel : secondaryButtons) {
            layout.addView(secondaryButtonView(buttonLabel, context));
        }

        // > Save Button
        layout.addView(saveButtonView(context));

        return layout;
    }


    private static LinearLayout footerViewLayout(boolean isDark, Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation              = LinearLayout.HORIZONTAL;
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity                  = Gravity.RIGHT | Gravity.CENTER_VERTICAL;

        layout.padding.left             = R.dimen.dialog_edit_padding_horz;
        layout.padding.right            = R.dimen.dialog_edit_padding_horz;

        layout.padding.top              = R.dimen.dialog_edit_footer_margin_bottom;
        layout.padding.bottom           = R.dimen.dialog_edit_footer_margin_bottom;

        if (isDark)
            layout.backgroundResource   = R.drawable.bg_dialog_footer_dark;
        else
            layout.backgroundResource   = R.drawable.bg_dialog_footer;

        return layout.linearLayout(context);
    }


    private static TextView secondaryButtonView(String buttonLabel, Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        button.text             = buttonLabel.toUpperCase();
        button.font             = Font.serifFontBold(context);
        button.color            = R.color.dark_blue_hl_8;
        button.size             = R.dimen.dialog_edit_footer_button_text_size;

        button.margin.right     = R.dimen.sheet_dialog_footer_button_cancel_margin_right;

        return button.textView(context);
    }


    private static LinearLayout saveButtonView(Context context)
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

        //layout.margin.right         = R.dimen.sheet_dialog_footer_button_ok_margin_right;

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

        label.text                  = context.getString(R.string.save).toUpperCase();
        label.color                 = R.color.green_medium;
        label.font                  = Font.serifFontBold(context);
        label.size                  = R.dimen.dialog_edit_footer_button_text_size;


        return layout.linearLayout(context);
    }


}
