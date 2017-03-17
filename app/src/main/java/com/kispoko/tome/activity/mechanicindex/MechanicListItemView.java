
package com.kispoko.tome.activity.mechanicindex;


import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.LayoutType;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.RelativeLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;



/**
 * Mechanic List Item View
 */
public class MechanicListItemView
{

    public static RelativeLayout view(Context context)
    {
        RelativeLayout layout = viewLayout(context);

        // > Name
        layout.addView(nameView(context));

        // > Variables
        layout.addView(variablesView(context));

        return layout;
    }


    private static RelativeLayout viewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.id                   = R.id.mechanic_list_item_layout;
        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.padding.left         = R.dimen.mechanic_list_item_padding_horz;
        layout.padding.right        = R.dimen.mechanic_list_item_padding_horz;
        layout.padding.top          = R.dimen.mechanic_list_item_padding_vert;
        layout.padding.bottom       = R.dimen.mechanic_list_item_padding_vert;

        return layout.relativeLayout(context);
    }


    private static TextView nameView(Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.id                 = R.id.mechanic_list_item_name;
        name.layoutType         = LayoutType.RELATIVE;
        name.width              = RelativeLayout.LayoutParams.WRAP_CONTENT;
        name.height             = RelativeLayout.LayoutParams.WRAP_CONTENT;

        name.addRule(RelativeLayout.CENTER_VERTICAL);
        name.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        name.font               = Font.sansSerifFontRegular(context);
        name.size               = R.dimen.mechanic_list_item_name_text_size;
        name.color              = R.color.dark_blue_hl_1;

        return name.textView(context);
    }


    private static LinearLayout variablesView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout  = new LinearLayoutBuilder();
        TextViewBuilder     count   = new TextViewBuilder();
        TextViewBuilder     label   = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType           = LayoutType.RELATIVE;
        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_HORIZONTAL;

        layout.addRule(RelativeLayout.ALIGN_PARENT_END);

        layout.child(count)
              .child(label);

        // [3 A] Count
        // -------------------------------------------------------------------------------------

        count.id                    = R.id.mechanic_list_item_variables_count;
        count.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        count.height                = LinearLayout.LayoutParams.WRAP_CONTENT;
        count.layoutGravity         = Gravity.CENTER_HORIZONTAL;

        count.font                  = Font.sansSerifFontBold(context);
        count.color                 = R.color.dark_blue_hl_4;
        count.size                  = R.dimen.mechanic_list_item_variables_count_text_size;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.id                    = R.id.mechanic_list_item_variables_label;
        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.layoutGravity         = Gravity.CENTER_HORIZONTAL;

        label.font                  = Font.sansSerifFontRegular(context);
        label.color                 = R.color.dark_blue_hl_8;
        label.size                  = R.dimen.mechanic_list_item_variables_label_text_size;


        return layout.linearLayout(context);
    }

}
