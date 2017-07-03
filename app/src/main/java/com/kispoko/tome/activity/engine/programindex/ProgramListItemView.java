package com.kispoko.tome.activity.engine.programindex;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;



/**
 * Program List Item View
 */
public class ProgramListItemView
{


    public static LinearLayout view(Context context)
    {
        LinearLayout layout = viewLayout(context);

        // > Header
        layout.addView(headerView(context));

        // > Description
        layout.addView(descriptionView(context));

        // > Types
        layout.addView(typesView(context));

        return layout;
    }


    private static LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.id               = R.id.program_list_item_layout;

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation      = LinearLayout.VERTICAL;

        layout.padding.leftDp   = 12f;
        layout.padding.rightDp  = 12f;
        layout.padding.topDp    = 18f;
        layout.padding.bottomDp = 18f;

        return layout.linearLayout(context);
    }


    private static TextView headerView(Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.id                   = R.id.program_list_item_header;

        header.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

//        header.font                 = Font.serifFontRegular(context);
        header.color                = R.color.gold_light;
        header.sizeSp               = 15f;

        header.margin.bottomDp      = 10f;

        return header.textView(context);
    }


    private static TextView descriptionView(Context context)
    {
        TextViewBuilder description = new TextViewBuilder();

        description.id                  = R.id.program_list_item_description;

        description.width               = LinearLayout.LayoutParams.MATCH_PARENT;
        description.height              = LinearLayout.LayoutParams.WRAP_CONTENT;

//        description.font                = Font.serifFontRegular(context);
        description.color               = R.color.dark_theme_primary_50;
        description.sizeSp              = 13f;

        description.margin.bottomDp     = 11f;

        return description.textView(context);
    }


    private static LinearLayout typesView(Context context)
    {
        LinearLayout layout = typesLayout(context);

        // > Parameter Type Views
        layout.addView(parameterTypeView(1, context));
        layout.addView(parameterTypeView(2, context));
        layout.addView(parameterTypeView(3, context));

        // > Arrow
        layout.addView(arrowView(context));

        // > Result Type View
        layout.addView(resultTypeView(context));

        return layout;
    }


    private static LinearLayout typesLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_VERTICAL;

        return layout.linearLayout(context);
    }


    private static ImageView arrowView(Context context)
    {
        ImageViewBuilder arrow = new ImageViewBuilder();

        arrow.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        arrow.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        arrow.image             = R.drawable.ic_type_arrow;

        arrow.margin.rightDp    = 6f;

        return arrow.imageView(context);
    }


    private static TextView parameterTypeView(int parameterIndex, Context context)
    {
        TextViewBuilder type = new TextViewBuilder();

        type.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        type.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        type.color              = R.color.dark_theme_primary_30;
//        type.font               = Font.serifFontRegular(context);
        type.sizeSp             = 11f;

        type.backgroundResource = R.drawable.bg_parameter_type;
        type.backgroundColor    = R.color.dark_theme_primary_80;

        type.visibility         = View.GONE;

        type.margin.rightDp     = 6f;

        type.padding.topDp      = 6f;
        type.padding.bottomDp   = 6f;
        type.padding.leftDp     = 9f;
        type.padding.rightDp    = 9f;

        // > Set the id
        switch (parameterIndex)
        {
            case 1:
                type.id = R.id.program_list_item_parameter_type_1;
                break;
            case 2:
                type.id = R.id.program_list_item_parameter_type_2;
                break;
            case 3:
                type.id = R.id.program_list_item_parameter_type_3;
                break;
        }

        return type.textView(context);
    }


    private static TextView resultTypeView(Context context)
    {
        TextViewBuilder type = new TextViewBuilder();

        type.id                 = R.id.program_list_item_result_type;

        type.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        type.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        type.color              = R.color.dark_theme_primary_30;
//        type.font               = Font.serifFontRegular(context);
        type.sizeSp             = 11.5f;

        type.backgroundResource = R.drawable.bg_result_type;
        type.backgroundColor    = R.color.dark_theme_primary_80;

        type.padding.topDp      = 6f;
        type.padding.bottomDp   = 6f;
        type.padding.leftDp     = 9f;
        type.padding.rightDp    = 9f;

        return type.textView(context);
    }


}
