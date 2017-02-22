
package com.kispoko.tome.activity.sheet.dialog;


import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.summation.Summation;
import com.kispoko.tome.engine.summation.term.TermSummary;
import com.kispoko.tome.util.tuple.Tuple2;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.RelativeLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;



/**
 * Shared Summation View Components
 */
public class SummationView
{

    public static LinearLayout componentsView(String summationLabel,
                                              Summation summation,
                                              Context context)
    {
        LinearLayout layout = componentsViewLayout(context);

        // > Components
        for (TermSummary summary: summation.summary()) {
            layout.addView(componentView(summary, context));
        }

        // > Total
        layout.addView(totalView(summationLabel, summation.value().toString(), context));

        return layout;
    }


    private static LinearLayout componentView(TermSummary summary, Context context)
    {
        LinearLayout layout = componentViewLayout(context);

        if (summary.name() != null)
            layout.addView(componentHeaderView(summary.name(), context));

        for (Tuple2<String,String> component : summary.components())
        {
            String name  = component.getItem1();
            String value = component.getItem2();
            layout.addView(componentItemView(name, value, context));
        }

        layout.addView(componentDividerView(context));

        return layout;
    }


    public static LinearLayout componentDividerView(Context context)
    {
        LinearLayoutBuilder divider = new LinearLayoutBuilder();

        divider.width           = LinearLayout.LayoutParams.MATCH_PARENT;
        divider.height          = R.dimen.one_dp;

        divider.backgroundColor = R.color.dark_blue_7;

        return divider.linearLayout(context);
    }


    private static LinearLayout componentViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private static RelativeLayout componentItemView(String nameText, String valueText, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();
        TextViewBuilder       name   = new TextViewBuilder();
        TextViewBuilder       value  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation              = LinearLayout.HORIZONTAL;
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left             = R.dimen.dialog_summ_component_padding_left;
        layout.padding.right            = R.dimen.dialog_summ_component_padding_right;

        layout.padding.top              = R.dimen.dialog_summ_component_padding_vert;
        layout.padding.bottom           = R.dimen.dialog_summ_component_padding_vert;

        layout.child(name)
              .child(value);

        // [3 A] Name
        // -------------------------------------------------------------------------------------

        name.layoutType                 = LayoutType.RELATIVE;
        name.width                      = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height                     = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        name.text                       = nameText;
        name.font                       = Font.serifFontItalic(context);
        name.size                       = R.dimen.dialog_summ_component_name_text_size;
        name.color                      = R.color.dark_blue_hl_8;

        // [3 B] Value
        // -------------------------------------------------------------------------------------

        value.layoutType                = LayoutType.RELATIVE;
        value.width                     = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height                    = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        value.text                      = valueText;
        value.font                      = Font.serifFontBold(context);
        value.size                      = R.dimen.dialog_summ_component_value_text_size;
        value.color                     = R.color.dark_blue_hlx_9;


        return layout.relativeLayout(context);
    }


    private static TextView componentHeaderView(String headerText, Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.text                 = headerText;
        header.font                 = Font.serifFontRegular(context);
        header.color                = R.color.dark_blue_hl_5;
        header.size                 = R.dimen.dialog_summ_component_header_text_size;

        return header.textView(context);
    }


    private static LinearLayout componentsViewLayout(Context context)
    {
        LinearLayoutBuilder layout =  new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    public static RelativeLayout totalView(String totalLabel, String totalText, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();
        TextViewBuilder       label  = new TextViewBuilder();
        TextViewBuilder       value  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation              = LinearLayout.HORIZONTAL;
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundColor          = R.color.dark_blue_7;

        layout.padding.left             = R.dimen.dialog_summ_component_padding_left;
        layout.padding.right            = R.dimen.dialog_summ_component_padding_right;

        layout.padding.top              = R.dimen.dialog_summ_component_padding_vert;
        layout.padding.bottom           = R.dimen.dialog_summ_component_padding_vert;

        layout.child(label)
              .child(value);

        // [3 A] Name
        // -------------------------------------------------------------------------------------

        label.layoutType                = LayoutType.RELATIVE;
        label.width                     = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                    = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        label.text                      = totalLabel;
        label.font                      = Font.serifFontItalic(context);
        label.size                      = R.dimen.dialog_summ_component_name_text_size;
        label.color                     = R.color.dark_blue_hlx_7;

        label.margin.left               = R.dimen.one_dp;

        // [3 B] Value
        // -------------------------------------------------------------------------------------

        value.layoutType                = LayoutType.RELATIVE;
        value.width                     = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height                    = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        value.text                      = totalText;
        value.font                      = Font.serifFontBold(context);
        value.size                      = R.dimen.dialog_summ_component_value_text_size;
        value.color                     = R.color.dark_blue_hlx_7;


        return layout.relativeLayout(context);
    }


}
