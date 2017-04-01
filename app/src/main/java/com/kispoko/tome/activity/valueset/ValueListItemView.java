
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

        layout.addView(descriptionView(context));

        return layout;
    }


    private static LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.id                   = R.id.value_list_item_layout;

        layout.orientation          = LinearLayout.VERTICAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.leftDp       = 17f;
        layout.padding.rightDp      = 17f;
        layout.padding.topDp        = 17f;
        layout.padding.bottomDp     = 17f;

        return layout.linearLayout(context);
    }


    private static TextView valueView(Context context)
    {
        TextViewBuilder value = new TextViewBuilder();

        value.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.id                = R.id.value_list_item_value;

        value.font              = Font.serifFontRegular(context);
        value.sizeSp            = 18f;
        value.color             = R.color.gold_light;

        return value.textView(context);
    }


    private static TextView descriptionView(Context context)
    {
        TextViewBuilder description = new TextViewBuilder();

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
        description.heightDp        = LinearLayout.LayoutParams.WRAP_CONTENT;

        description.id              = R.id.value_list_item_description;

        description.font            = Font.serifFontRegular(context);
        description.color           = R.color.dark_blue_hl_8;
        description.sizeSp          = 14f;

        description.margin.topDp    = 8f;

        return description.textView(context);
    }


}
