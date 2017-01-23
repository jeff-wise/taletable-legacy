
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;



/**
 * Draggable Card UI Widget
 */
public class DraggableCard
{


    public static View view(View rightView, Context context)
    {
        RelativeLayout layout = viewLayout(context);

        // > Left Layout
        LinearLayout leftView = leftLayout(context);
        layout.addView(leftView);

        // ** Drag Icon
        leftView.addView(dragIcon(context));

        // ** Name View
        leftView.addView(titleView(context));

        // > Right View
        layout.addView(rightView);

        return layout;
    }


    private static RelativeLayout viewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.id                       = R.id.draggable_card_layout;
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

        icon.image                  = R.drawable.ic_list_item_drag;

        icon.margin.right           = R.dimen.group_row_list_item_drag_margin_right;

        icon.addRule(RelativeLayout.CENTER_VERTICAL);
        icon.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        return icon.imageView(context);
    }


    private static TextView titleView(Context context)
    {
        TextViewBuilder index = new TextViewBuilder();

        index.id                 = R.id.draggable_card_title;
        index.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        index.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        index.addRule(RelativeLayout.CENTER_VERTICAL);
        index.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        index.font               = Font.sansSerifFontRegular(context);
        index.size               = R.dimen.group_row_list_item_name_text_size;
        index.color              = R.color.dark_blue_hlx_8;

        return index.textView(context);
    }


}
