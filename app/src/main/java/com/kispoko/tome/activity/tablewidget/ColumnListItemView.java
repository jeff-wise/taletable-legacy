
package com.kispoko.tome.activity.tablewidget;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.util.ui.DraggableCard;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;



/**
 * Column List Item View
 */
public class ColumnListItemView
{

    public static View view(Context context)
    {
        return DraggableCard.view(typeView(context), context);
    }


    private static LinearLayout typeView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout  = new LinearLayoutBuilder();
        TextViewBuilder     type    = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType               = LayoutType.RELATIVE;
        layout.width                    = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout.height                   = RelativeLayout.LayoutParams.WRAP_CONTENT;

        layout.addRule(RelativeLayout.ALIGN_PARENT_END);
        layout.addRule(RelativeLayout.CENTER_VERTICAL);

        layout.child(type);

        // [3 A] Count
        // -------------------------------------------------------------------------------------

        type.id                     = R.id.column_list_item_type;
        type.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        type.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        type.font                   = Font.sansSerifFontBold(context);
        type.size                   = R.dimen.column_list_item_type_text_size;
        type.color                  = R.color.dark_blue_hl_8;


        return layout.linearLayout(context);
    }



}
