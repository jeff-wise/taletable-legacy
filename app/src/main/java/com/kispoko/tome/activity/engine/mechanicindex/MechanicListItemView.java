
package com.kispoko.tome.activity.engine.mechanicindex;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;



/**
 * Mechanic List Item View
 */
public class MechanicListItemView
{

    // HEADER
    // -----------------------------------------------------------------------------------------

    public static LinearLayout header(Context context)
    {
        LinearLayout layout = headerLayout(context);

        layout.addView(headerCategoryView(context));

        return layout;
    }


    public static LinearLayout headerLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundColor  = R.color.dark_theme_primary_86;

        layout.padding.topDp    = 14f;
        layout.padding.bottomDp = 14f;
        layout.padding.leftDp   = 12f;

        return layout.linearLayout(context);
    }


    public static TextView headerCategoryView(Context context)
    {
        TextViewBuilder category = new TextViewBuilder();

        category.id                 = R.id.mechanic_category_header;

        category.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        category.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        category.font               = Font.serifFontBoldItalic(context);
        category.color              = R.color.dark_theme_primary_12;
        category.sizeSp             = 16.5f;

        return category.textView(context);
    }


    // HEADER
    // -----------------------------------------------------------------------------------------

    public static LinearLayout mechanic(Context context)
    {
        LinearLayout layout = mechanicLayout(context);

        // > Label
        layout.addView(mechanicLabelView(context));

        // > Status
        layout.addView(mechanicStatusView(context));

        // > Summary
        layout.addView(mechanicSummaryView(context));

        return layout;
    }


    private static LinearLayout mechanicLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.id               = R.id.mechanic_list_item_layout;

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation      = LinearLayout.VERTICAL;

        layout.padding.leftDp   = 12f;
        layout.padding.rightDp  = 12f;
        layout.padding.topDp    = 16f;
        layout.padding.bottomDp = 16f;

        return layout.linearLayout(context);
    }


    private static TextView mechanicLabelView(Context context)
    {
        TextViewBuilder label = new TextViewBuilder();

        label.id                = R.id.mechanic_name;

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.font              = Font.serifFontRegular(context);
        label.color             = R.color.gold_light;
        label.sizeSp            = 16f;

        return label.textView(context);
    }


    private static TextView mechanicStatusView(Context context)
    {
        TextViewBuilder status = new TextViewBuilder();

        status.id                   = R.id.mechanic_status;

        status.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        status.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        status.backgroundResource   = R.drawable.bg_mechanic_status;
        status.backgroundColor      = R.color.dark_theme_primary_81;

        status.margin.topDp         = 10f;

        status.padding.topDp        = 4f;
        status.padding.bottomDp     = 4f;
        status.padding.leftDp       = 7f;
        status.padding.rightDp      = 7f;

        status.visibility           = View.GONE;

        status.text                 = context.getString(R.string.active).toUpperCase();

        status.font                 = Font.serifFontRegular(context);
        status.color                = R.color.green_medium_dark;
        status.sizeSp               = 11.5f;

        return status.textView(context);
    }



    private static TextView mechanicSummaryView(Context context)
    {
        TextViewBuilder summary = new TextViewBuilder();

        summary.id              = R.id.mechanic_summary;

        summary.width           = LinearLayout.LayoutParams.MATCH_PARENT;
        summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        summary.margin.topDp    = 10f;

        summary.font            = Font.serifFontRegular(context);
        summary.color           = R.color.dark_theme_primary_55;
        summary.sizeSp          = 14f;

        return summary.textView(context);
    }

}
