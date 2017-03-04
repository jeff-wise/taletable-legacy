
package com.kispoko.tome.activity.pagelist;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.RelativeLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;



/**
 * Page List Item View
 */
public class PageListItemView
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
        leftView.addView(nameView(context));

        // > Groups View
        layout.addView(groupsView(context));

        return layout;
    }


    private static RelativeLayout viewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.id                       = R.id.page_list_item_layout;
        layout.orientation              = LinearLayout.HORIZONTAL;
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.left              = R.dimen.page_list_item_margin_horz;
        layout.margin.right             = R.dimen.page_list_item_margin_horz;
        layout.margin.top               = R.dimen.page_list_item_margin_vert;
        layout.margin.bottom            = R.dimen.page_list_item_margin_vert;

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

        icon.margin.right           = R.dimen.page_list_item_drag_margin_right;

        icon.addRule(RelativeLayout.CENTER_VERTICAL);
        icon.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        return icon.imageView(context);
    }


    private static TextView nameView(Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.id                 = R.id.page_list_item_name;
        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.addRule(RelativeLayout.CENTER_VERTICAL);
        name.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        name.font               = Font.sansSerifFontRegular(context);
        name.size               = R.dimen.page_list_item_name_text_size;
        name.color              = R.color.dark_blue_hlx_8;

        return name.textView(context);
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

        count.id                        = R.id.page_list_item_group_count;
        count.width                     = LinearLayout.LayoutParams.WRAP_CONTENT;
        count.height                    = LinearLayout.LayoutParams.WRAP_CONTENT;

        count.font                      = Font.sansSerifFontBold(context);
        count.size                      = R.dimen.page_list_item_group_count_text_size;
        count.color                     = R.color.dark_blue_hl_8;

        count.margin.right              = R.dimen.page_list_item_group_count_margin_right;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.id                        = R.id.page_list_item_group_label;
        label.width                     = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                    = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.font                      = Font.sansSerifFontBold(context);
        label.size                      = R.dimen.page_list_item_group_label_text_size;
        label.color                     = R.color.dark_blue_hl_8;


        return layout.linearLayout(context);
    }

}
