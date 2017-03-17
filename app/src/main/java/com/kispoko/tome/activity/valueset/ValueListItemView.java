
package com.kispoko.tome.activity.valueset;


import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;



/**
 * Value List Item View
 */
public class ValueListItemView
{


    public static LinearLayout view(Context context)
    {
        LinearLayout layout = viewLayout(context);

        layout.addView(valueView(context));

        return layout;
    }


    private static LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left         = R.dimen.value_set_item_padding_horz;
        layout.padding.right        = R.dimen.value_set_item_padding_horz;
        layout.padding.top          = R.dimen.value_set_item_padding_vert;
        layout.padding.bottom       = R.dimen.value_set_item_padding_vert;

        return layout.linearLayout(context);
    }


    private static TextView valueView(Context context)
    {
        TextViewBuilder value = new TextViewBuilder();

        value.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.id                = R.id.value_list_item_value;
        value.font              = Font.sansSerifFontRegular(context);
        value.size              = R.dimen.value_set_item_value_text_size;
        value.color             = R.color.dark_blue_hl_1;

        return value.textView(context);
    }



}
