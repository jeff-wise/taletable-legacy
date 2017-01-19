
package com.kispoko.tome.activity.variable;


import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;



/**
 * Variable List Item View
 */
public class VariableListItemView
{


    public static LinearLayout view(Context context)
    {
        LinearLayout layout = viewLayout(context);

        // > Name
        layout.addView(nameView(context));

        // > Types
        layout.addView(typesView(context));

        return layout;
    }


    private static LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.id                   = R.id.variable_list_item_layout;
        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left         = R.dimen.variable_list_item_padding_horz;
        layout.padding.right        = R.dimen.variable_list_item_padding_horz;
        layout.padding.top          = R.dimen.variable_list_item_padding_vert;
        layout.padding.bottom       = R.dimen.variable_list_item_padding_vert;

        return layout.linearLayout(context);
    }


    private static TextView nameView(Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.id                 = R.id.variable_list_item_name;
        name.width              = LinearLayout.LayoutParams.MATCH_PARENT;
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.font               = Font.sansSerifFontRegular(context);
        name.size               = R.dimen.variable_list_item_name_text_size;
        name.color              = R.color.dark_blue_hl_1;

        name.margin.bottom      = R.dimen.variable_list_item_name_margin_bottom;

        return name.textView(context);
    }


    private static LinearLayout typesView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout  = new LinearLayoutBuilder();
        TextViewBuilder     type    = new TextViewBuilder();
        TextViewBuilder     kind    = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.child(type)
              .child(kind);

        // [3 A] Type
        // -------------------------------------------------------------------------------------

        type.id                     = R.id.variable_list_item_type;
        type.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        type.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        type.backgroundResource     = R.drawable.bg_variable_type;

        type.font                   = Font.sansSerifFontBold(context);
        type.color                  = R.color.dark_blue_hl_8;
        type.size                   = R.dimen.variable_list_item_type_text_size;

        type.margin.right           = R.dimen.variable_list_item_type_margin_right;

        // [3 B] Kind
        // -------------------------------------------------------------------------------------

        kind.id                     = R.id.variable_list_item_kind;
        kind.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        kind.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        kind.backgroundResource     = R.drawable.bg_variable_type;

        kind.font                   = Font.sansSerifFontBold(context);
        kind.color                  = R.color.dark_blue_hl_8;
        kind.size                   = R.dimen.variable_list_item_type_text_size;

        return layout.linearLayout(context);
    }

}
