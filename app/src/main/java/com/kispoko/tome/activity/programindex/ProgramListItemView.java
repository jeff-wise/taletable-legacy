package com.kispoko.tome.activity.programindex;


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

        layout.id                       = R.id.program_list_item_layout;
        layout.orientation              = LinearLayout.VERTICAL;
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left             = R.dimen.function_list_item_padding_horz;
        layout.padding.right            = R.dimen.function_list_item_padding_horz;
        layout.padding.top              = R.dimen.function_list_item_padding_vert;
        layout.padding.bottom           = R.dimen.function_list_item_padding_vert;

        return layout.linearLayout(context);
    }


    private static TextView headerView(Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.id                   = R.id.program_list_item_header;
        header.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.font                 = Font.sansSerifFontBold(context);
        header.color                = R.color.gold_light;
        header.size                 = R.dimen.program_list_item_header_text_size;

        header.margin.bottom        = R.dimen.program_list_item_header_margin_bottom;

        return header.textView(context);
    }


    private static TextView descriptionView(Context context)
    {
        TextViewBuilder description = new TextViewBuilder();

        description.id                  = R.id.program_list_item_description;
        description.width               = LinearLayout.LayoutParams.MATCH_PARENT;
        description.height              = LinearLayout.LayoutParams.WRAP_CONTENT;

        description.font                = Font.sansSerifFontRegular(context);
        description.color               = R.color.dark_blue_hl_8;
        description.size                = R.dimen.program_list_item_description_text_size;

        description.margin.bottom       = R.dimen.program_list_item_description_margin_bottom;

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

        arrow.margin.right      = R.dimen.program_list_item_type_margin_right;

        return arrow.imageView(context);
    }


    private static TextView parameterTypeView(int parameterIndex, Context context)
    {
        TextViewBuilder type = new TextViewBuilder();

        type.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        type.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        type.color              = R.color.dark_blue_hl_8;
        type.font               = Font.sansSerifFontRegular(context);
        type.size               = R.dimen.program_list_item_type_text_size;

        type.backgroundResource = R.drawable.bg_parameter_type;
        type.visibility         = View.GONE;

        type.margin.right       = R.dimen.program_list_item_type_margin_right;

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

        type.color              = R.color.dark_blue_hl_8;
        type.font               = Font.sansSerifFontRegular(context);
        type.size               = R.dimen.program_list_item_type_text_size;

        type.backgroundResource = R.drawable.bg_result_type;

        return type.textView(context);
    }



}
