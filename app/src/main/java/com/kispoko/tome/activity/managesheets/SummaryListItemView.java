
package com.kispoko.tome.activity.managesheets;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;



/**
 * Summary List Item View
 */
public class SummaryListItemView
{

    public static View view(Context context)
    {
        LinearLayout layout = viewLayout(context);

        // > Last Used
        layout.addView(lastUsedView(context));

        // > Name
        layout.addView(nameView(context));

        return layout;
    }


    private static LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.id               = R.id.sheet_summary_list_item_layout;
        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left     = R.dimen.sheet_summary_list_item_padding_horz;
        layout.padding.right    = R.dimen.sheet_summary_list_item_padding_horz;
        layout.padding.bottom   = R.dimen.sheet_summary_list_item_padding_vert;
        layout.padding.top      = R.dimen.sheet_summary_list_item_padding_vert;

        return layout.linearLayout(context);
    }


    private static LinearLayout lastUsedView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        TextViewBuilder     date   = new TextViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.bottom        = R.dimen.sheet_summary_list_item_last_used_margin_bottom;

        layout.child(date)
              .child(label);

        // [3 A] Date
        // -------------------------------------------------------------------------------------

        date.id                     = R.id.sheet_summary_list_item_date;
        date.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        date.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        date.font                   = Font.sansSerifFontBold(context);
        date.color                  = R.color.dark_blue_hl_5;
        date.size                   = R.dimen.sheet_summary_list_item_last_used_text_size;

        date.margin.right           = R.dimen.sheet_summary_list_item_last_used_value_margin_right;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.textId                = R.string.last_used_upper;
        label.font                  = Font.sansSerifFontBold(context);
        label.color                 = R.color.dark_blue_hl_8;
        label.size                  = R.dimen.sheet_summary_list_item_last_used_label_text_size;



        return layout.linearLayout(context);
    }


    private static TextView nameView(Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.width              = LinearLayout.LayoutParams.MATCH_PARENT;
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.id                 = R.id.sheet_summary_list_item_name;
        name.font               = Font.sansSerifFontBold(context);
        name.color              = R.color.gold_light;
        name.size               = R.dimen.sheet_summary_list_item_name_text_size;

        return name.textView(context);
    }

}
