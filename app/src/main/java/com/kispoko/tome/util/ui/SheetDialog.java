
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;



/**
 * SheetDialog UI Components
 */
public class SheetDialog
{


    public static LinearLayout headerView(String widgetName, String widgetType, Context context)
    {
        LinearLayout layout = headerViewLayout(context);

        // > Label (You Clicked)
        layout.addView(topRowView(context));

        // > Target
        layout.addView(targetView(widgetName, widgetType, context));

        return layout;
    }


    private static LinearLayout headerViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundResource  = R.drawable.bg_dialog_header;

        layout.padding.left     = R.dimen.sheet_dialog_header_padding_horz;
        layout.padding.right    = R.dimen.sheet_dialog_header_padding_horz;
        layout.padding.top      = R.dimen.sheet_dialog_header_padding_top;

        return layout.linearLayout(context);
    }


    private static RelativeLayout topRowView(Context context)
    {
        RelativeLayout layout = topRowViewLayout(context);

        // > Label
        layout.addView(youClickedView(context));

        // > Close Button
        layout.addView(closeButtonView(context));

        return layout;
    }


    private static RelativeLayout topRowViewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_VERTICAL;

        return layout.relativeLayout(context);
    }


    private static TextView youClickedView(Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.layoutType       = LayoutType.RELATIVE;
        header.width            = RelativeLayout.LayoutParams.WRAP_CONTENT;
        header.height           = RelativeLayout.LayoutParams.WRAP_CONTENT;

        header.textId           = R.string.you_clicked;
        header.font             = Font.sansSerifFontRegular(context);
        header.color            = R.color.dark_blue_hl_9;
        header.size             = R.dimen.sheet_dialog_heading_text_size;

        header.margin.top       = R.dimen.one_dp;

        header.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        return header.textView(context);
    }


    private static ImageView closeButtonView(Context context)
    {
        ImageViewBuilder button = new ImageViewBuilder();

        button.layoutType           = LayoutType.RELATIVE;
        button.width                = RelativeLayout.LayoutParams.WRAP_CONTENT;
        button.height               = RelativeLayout.LayoutParams.WRAP_CONTENT;
        button.layoutGravity        = Gravity.CENTER;

        button.image                = R.drawable.ic_dialog_close;

        button.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        return button.imageView(context);
    }



    private static LinearLayout targetView(String widgetName, String widgetType, Context context)
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

        layout.padding.top          = R.dimen.sheet_dialog_target_padding_vert;
        layout.padding.bottom       = R.dimen.sheet_dialog_target_padding_vert;

        layout.child(nameLayout)
              .child(type);


        // [3] Name Layout
        // -------------------------------------------------------------------------------------

        nameLayout.orientation      = LinearLayout.HORIZONTAL;
        nameLayout.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        nameLayout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        nameLayout.gravity          = Gravity.CENTER_VERTICAL;

        nameLayout.margin.bottom    = R.dimen.two_dp;

        nameLayout.child(icon)
                  .child(name);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image              = R.drawable.ic_launch;

        icon.margin.right       = R.dimen.sheet_dialog_target_icon_margin_right;

        // [3 B] Name
        // -------------------------------------------------------------------------------------

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.text               = widgetName;
        name.font               = Font.sansSerifFontRegular(context);
        name.color              = R.color.gold_bright;
        name.size               = R.dimen.sheet_dialog_target_name_text_size;

        name.margin.right       = R.dimen.two_dp;

        // [4] Type
        // -------------------------------------------------------------------------------------

        type.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        type.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        type.text               = widgetType;
        type.font               = Font.sansSerifFontRegular(context);
        type.color              = R.color.dark_blue_1;
        type.size               = R.dimen.sheet_dialog_target_type_text_size;


        return layout.linearLayout(context);
    }


    public static LinearLayout actionsView(Context context)
    {
        LinearLayout layout = actionsLayout(context);

        // > Info Button
        layout.addView(infoButtonView(context));

        // > Visibility Button
        layout.addView(visibilityButtonView(context));

        return layout;
    }


    private static LinearLayout actionsLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.padding.top          = R.dimen.sheet_dialog_action_layout_padding_vert;
        layout.padding.bottom       = R.dimen.sheet_dialog_action_layout_padding_vert;

        return layout.linearLayout(context);
    }


    private static LinearLayout infoButtonView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.id                   = R.id.dialog_info_button;

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = 0;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.weight               = 1.0f;
        layout.gravity              = Gravity.CENTER;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = R.drawable.ic_info;

        icon.margin.right           = R.dimen.sheet_dialog_action_icon_margin_right;
        icon.margin.bottom          = R.dimen.one_dp;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.textId                = R.string.rules;
        label.font                  = Font.sansSerifFontRegular(context);
        label.color                 = R.color.dark_blue_hl_6;
        label.size                  = R.dimen.sheet_dialog_action_text_size;


        return layout.linearLayout(context);
    }


    private static LinearLayout visibilityButtonView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout       = new LinearLayoutBuilder();
        ImageViewBuilder    leftChevron  = new ImageViewBuilder();
        ImageViewBuilder    rightChevron = new ImageViewBuilder();
        TextViewBuilder     label        = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.id                       = R.id.dialog_visibility_button;

        layout.orientation              = LinearLayout.HORIZONTAL;
        layout.width                    = 0;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity                  = Gravity.CENTER;
        layout.weight                   = 1.0f;

        layout.margin.right             = R.dimen.sheet_dialog_action_visibility_margin_right;

//        layout.margin.top               = R.dimen.one_dp;

        // layout.backgroundColor          = R.color.dark_blue_1;

//        layout.onClick                  = new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//
//            }
//        };

        layout.child(leftChevron)
              .child(label)
              .child(rightChevron);

        // [3 A] Left Chevron
        // -------------------------------------------------------------------------------------

        leftChevron.width               = LinearLayout.LayoutParams.WRAP_CONTENT;
        leftChevron.height              = LinearLayout.LayoutParams.WRAP_CONTENT;

        leftChevron.image               = R.drawable.ic_picker_left;

        // [3 B] Right Chevron
        // -------------------------------------------------------------------------------------

        rightChevron.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        rightChevron.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        rightChevron.image              = R.drawable.ic_picker_right;

        // [3 C] Label
        // -------------------------------------------------------------------------------------

        label.width                     = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                    = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text                      = "Private";
        label.font                      = Font.sansSerifFontRegular(context);
        label.color                     = R.color.dark_blue_hl_6;
        label.size                      = R.dimen.sheet_dialog_action_text_size;


        return layout.linearLayout(context);
    }


}
