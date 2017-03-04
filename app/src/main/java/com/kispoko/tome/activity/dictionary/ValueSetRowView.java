
package com.kispoko.tome.activity.dictionary;


import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;



/**
 * Value Set View
 */
public class ValueSetRowView
{

    // API
    // ------------------------------------------------------------------------------------------

    public static LinearLayout view(Context context)
    {
        LinearLayout layout = viewLayout(context);

        // > Items (Count)
        layout.addView(itemsView(context));

        // > Info
        layout.addView(infoView(context));

        return layout;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private static LinearLayout viewLayout(final Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.id                   = R.id.value_set_row_layout;
        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.padding.left         = R.dimen.dictionary_item_padding_horz;
        layout.padding.right        = R.dimen.dictionary_item_padding_horz;
        layout.padding.top          = R.dimen.dictionary_item_padding_vert;
        layout.padding.bottom       = R.dimen.dictionary_item_padding_vert;

        return layout.linearLayout(context);
    }


    private static LinearLayout infoView(Context context)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        TextViewBuilder     header = new TextViewBuilder();
        TextViewBuilder     description = new TextViewBuilder();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.child(header)
              .child(description);

        // [3 A] Header
        // --------------------------------------------------------------------------------------

        header.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.id                   = R.id.value_set_row_header;
        header.size                 = R.dimen.dictionary_item_header_text_size;
        header.color                = R.color.gold_light;
        header.font                 = Font.sansSerifFontBold(context);

        header.margin.bottom        = R.dimen.dictionary_item_header_margin_bottom;

        // [3 B] Description
        // --------------------------------------------------------------------------------------

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        description.id              = R.id.value_set_row_description;
        description.size            = R.dimen.dictionary_item_description_text_size;
        description.color           = R.color.dark_blue_hl_8;
        description.font            = Font.sansSerifFontRegular(context);


        return layout.linearLayout(context);
    }


    private static TextView itemsView(Context context)
    {
        TextViewBuilder count = new TextViewBuilder();

        count.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        count.height                = LinearLayout.LayoutParams.WRAP_CONTENT;
        count.gravity               = Gravity.CENTER;

        count.id                    = R.id.value_set_row_items;
        count.font                  = Font.sansSerifFontBold(context);
        count.color                 = R.color.dark_blue_hl_4;
        count.size                  = R.dimen.dictionary_item_count_text_size;

        count.backgroundResource    = R.drawable.bg_value_set_size;

        count.margin.right          = R.dimen.dictionary_item_count_margin_right;

        return count.textView(context);
    }

}
