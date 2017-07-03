
package com.kispoko.tome.activity.sheet.group;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LayoutType;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.RelativeLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;



/**
 * Group Row List Item View
 */
public class GroupRowListItemView
{


    public static View view(Context context)
    {
        RelativeLayout layout = viewLayout(context);

        // > Left Layout
        LinearLayout leftView = leftLayout(context);
        layout.addView(leftView);

        // ** Drag Icon
        leftView.addView(dragIcon(context));

        // ** Name View
        leftView.addView(indexView(context));

        // > Groups View
        layout.addView(groupsView(context));

        return layout;
    }


    private static RelativeLayout viewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.id                       = R.id.group_row_list_item_layout;
        layout.orientation              = LinearLayout.HORIZONTAL;
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.left              = R.dimen.group_row_list_item_margin_horz;
        layout.margin.right             = R.dimen.group_row_list_item_margin_horz;
        layout.margin.top               = R.dimen.group_row_list_item_margin_vert;
        layout.margin.bottom            = R.dimen.group_row_list_item_margin_vert;

        layout.backgroundResource       = R.drawable.bg_draggable_list_item;

        return layout.relativeLayout(context);
    }


    private static LinearLayout leftLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation              = LinearLayout.HORIZONTAL;
        layout.width                    = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout.height                   = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout.layoutGravity            = Gravity.CENTER_VERTICAL;
        layout.gravity                  = Gravity.CENTER_VERTICAL;

        return layout.linearLayout(context);
    }


    private static ImageView dragIcon(Context context)
    {
        ImageViewBuilder icon = new ImageViewBuilder();

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        // icon.image                  = R.drawable.ic_list_item_drag;

        icon.margin.right           = R.dimen.group_row_list_item_drag_margin_right;

        icon.addRule(RelativeLayout.CENTER_VERTICAL);
        icon.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        return icon.imageView(context);
    }


    private static TextView indexView(Context context)
    {
        TextViewBuilder index = new TextViewBuilder();

        index.id                 = R.id.group_row_list_item_index;
        index.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        index.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        index.addRule(RelativeLayout.CENTER_VERTICAL);
        index.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

//        index.font               = Font.sansSerifFontRegular(context);
        index.size               = R.dimen.group_row_list_item_name_text_size;
        index.color              = R.color.dark_blue_hlx_8;

        return index.textView(context);
    }


    private static LinearLayout groupsView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout  = new LinearLayoutBuilder();
        TextViewBuilder     count   = new TextViewBuilder();
        TextViewBuilder     label   = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType               = LayoutType.RELATIVE;
        layout.width                    = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout.height                   = RelativeLayout.LayoutParams.WRAP_CONTENT;

        layout.addRule(RelativeLayout.ALIGN_PARENT_END);
        layout.addRule(RelativeLayout.CENTER_VERTICAL);

        layout.child(count)
              .child(label);

        // [3 A] Count
        // -------------------------------------------------------------------------------------

        count.id                        = R.id.group_row_list_item_widget_count;
        count.width                     = LinearLayout.LayoutParams.WRAP_CONTENT;
        count.height                    = LinearLayout.LayoutParams.WRAP_CONTENT;

//        count.font                      = Font.sansSerifFontBold(context);
        count.size                      = R.dimen.group_row_list_item_group_count_text_size;
        count.color                     = R.color.dark_blue_hl_8;

        count.margin.right              = R.dimen.group_row_list_item_group_count_margin_right;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.id                        = R.id.group_row_list_item_widget_count_label;
        label.width                     = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                    = LinearLayout.LayoutParams.WRAP_CONTENT;

//        label.font                      = Font.sansSerifFontBold(context);
        label.size                      = R.dimen.group_row_list_item_group_label_text_size;
        label.color                     = R.color.dark_blue_hl_8;


        return layout.linearLayout(context);
    }


}
