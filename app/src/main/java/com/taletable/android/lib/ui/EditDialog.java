
package com.taletable.android.lib.ui;


import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taletable.android.R;

import java.util.List;



/**
 * Edit Dialog UI Common Components
 */
public class EditDialog
{

    // TYPE
    // -----------------------------------------------------------------------------------------

    public enum Shade
    {
        LIGHT,
        DARK
    }


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
    public static LinearLayout viewLayout(Shade shade, Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        switch (shade)
        {
            case LIGHT:
                layout.backgroundResource = R.drawable.bg_session_back_button;
                break;
            case DARK:
                layout.backgroundColor  = R.color.dark_blue_7;
                layout.backgroundResource = R.drawable.bg_session_back_button;
                break;
        }

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

//        header.font                 = Font.serifFontRegular(context);
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
        layout.padding.bottom          = R.dimen.dialog_edit_header_padding_top;
        layout.padding.left         = R.dimen.dialog_edit_header_padding_horz;
        layout.padding.right        = R.dimen.dialog_edit_header_padding_horz;

        layout.backgroundResource   = R.drawable.bg_dialog_header;

        return layout.linearLayout(context);
    }


    // HEADER TITLE VIEW
    // -----------------------------------------------------------------------------------------

    public static RelativeLayout headerTitleView(String headerString,
                                                 Shade shade,
                                                 Context context)
    {
        RelativeLayout layout = headerTitleViewLayout(context);

        // > Label
        layout.addView(headerTitleTextView(headerString, shade, context));

        // > Close Button
        layout.addView(closeButtonView(shade, context));

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


    private static TextView headerTitleTextView(String headerString,
                                                Shade shade,
                                                Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.layoutType       = LayoutType.RELATIVE;
        header.width            = RelativeLayout.LayoutParams.WRAP_CONTENT;
        header.height           = RelativeLayout.LayoutParams.WRAP_CONTENT;

        header.text             = headerString;
//        header.font             = Font.serifFontRegular(context);

        header.size             = R.dimen.sheet_dialog_heading_text_size;

        // > Color
        switch (shade)
        {
            case LIGHT:
                header.color    = R.color.gold_medium;
                break;
            case DARK:
                header.color    = R.color.dark_blue_hl_9;
                break;
        }

        header.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        return header.textView(context);
    }


    private static ImageView closeButtonView(Shade shade, Context context)
    {
        ImageViewBuilder button = new ImageViewBuilder();

        button.layoutType           = LayoutType.RELATIVE;
        button.width                = RelativeLayout.LayoutParams.WRAP_CONTENT;
        button.height               = RelativeLayout.LayoutParams.WRAP_CONTENT;
        button.layoutGravity        = Gravity.CENTER;

        switch (shade)
        {
            case LIGHT:
                button.image        = R.drawable.ic_dialog_close_light;
                break;
            case DARK:
                button.image        = R.drawable.ic_dialog_close;
                break;
        }

        button.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        return button.imageView(context);
    }


    // FOOTER
    // -----------------------------------------------------------------------------------------

    public static LinearLayout footerView(List<String> secondaryButtons,
                                          String mainButton,
                                          int mainButtonIconId,
                                          Shade shade,
                                          Context context)
    {
        LinearLayout layout = footerViewLayout(shade, context);

        // > Cancel Button
        for (String buttonLabel : secondaryButtons) {
            layout.addView(secondaryFooterButtonView(buttonLabel, context));
        }

        // > Save Button
        layout.addView(mainFooterButtonView(mainButton, mainButtonIconId, context));

        return layout;
    }


    private static LinearLayout footerViewLayout(Shade shade, Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation              = LinearLayout.HORIZONTAL;
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity                  = Gravity.RIGHT | Gravity.CENTER_VERTICAL;

        layout.padding.left             = R.dimen.dialog_edit_footer_padding_horz;
        layout.padding.right            = R.dimen.dialog_edit_footer_padding_horz;

        layout.padding.top              = R.dimen.dialog_edit_footer_padding_vert;
        layout.padding.bottom           = R.dimen.dialog_edit_footer_padding_vert;

        switch (shade)
        {
            case LIGHT:
                layout.backgroundResource = R.drawable.bg_dialog_footer;
                break;
            case DARK:
                layout.backgroundResource = R.drawable.bg_dialog_footer_dark;
                break;
        }

        return layout.linearLayout(context);
    }


    private static TextView secondaryFooterButtonView(String buttonLabel, Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        button.text             = buttonLabel.toUpperCase();
//        button.font             = Font.serifFontBold(context);
        button.color            = R.color.dark_blue_hl_8;
        button.size             = R.dimen.dialog_edit_footer_button_text_size;

        button.margin.right     = R.dimen.sheet_dialog_footer_button_cancel_margin_right;

        return button.textView(context);
    }


    private static LinearLayout mainFooterButtonView(String buttonLabel,
                                                     int iconId,
                                                     Context context)
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

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = iconId;

        icon.margin.right           = R.dimen.sheet_dialog_footer_button_icon_margin_right;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text                  = buttonLabel.toUpperCase();


//        label.font                  = Font.serifFontBold(context);
        label.size                  = R.dimen.dialog_edit_footer_button_text_size;

        label.color                 = R.color.green_medium;


        return layout.linearLayout(context);
    }


}
