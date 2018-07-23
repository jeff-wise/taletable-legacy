
package com.taletable.android.activity.entity.engine.search;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taletable.android.R;
import com.taletable.android.lib.ui.LinearLayoutBuilder;
import com.taletable.android.lib.ui.TextViewBuilder;



/**
 * Engine Active Search Result View
 */
public class ActiveResultView
{


    // VIEWS
    // -----------------------------------------------------------------------------------------

    /**
     * A variable search result view.
     * @param context The context.
     * @return The result view.
     */
    public static View variable(Context context)
    {
        LinearLayout layout = resultViewLayout(context);

        // > Header
        layout.addView(resultTypeView(context));

        // > Name Field
        String nameFieldName = context.getString(R.string.name);
        layout.addView(fieldView(null, nameFieldName, R.id.search_result_variable_name, context));

        // > Label Field
        String labelFieldName = context.getString(R.string.label);
        layout.addView(fieldView(R.id.search_result_label_layout,
                                 labelFieldName,
                                 R.id.search_result_variable_label,
                                 context));

        return layout;
    }


    /**
     * A mechanic search result view.
     * @param context The context.
     * @return The result view.
     */
    public static View mechanic(Context context)
    {
        LinearLayout layout = resultViewLayout(context);

        // > Header
        layout.addView(resultTypeView(context));

        // > Name Field
        String nameFieldName = context.getString(R.string.name);
        layout.addView(fieldView(null, nameFieldName, R.id.search_result_mechanic_name, context));

        // > Label Field
        String labelFieldName = context.getString(R.string.label);
        layout.addView(fieldView(R.id.search_result_label_layout,
                                 labelFieldName,
                                 R.id.search_result_mechanic_label,
                                 context));

        // > Variables Field
        String variablesFieldName = context.getString(R.string.variables);
        layout.addView(fieldView(R.id.search_result_variables_layout,
                                 variablesFieldName,
                                 R.id.search_result_mechanic_variables,
                                 context));

        return layout;
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    private static LinearLayout resultViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.id               = R.id.search_result_layout;

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation      = LinearLayout.VERTICAL;

        layout.padding.topDp    = 16f;
        layout.padding.bottomDp = 16f;

        return layout.linearLayout(context);
    }


    private static TextView resultTypeView(Context context)
    {
        TextViewBuilder type = new TextViewBuilder();

        type.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        type.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        type.id                 = R.id.search_result_type;

//        type.font               = Font.serifFontBoldItalic(context);
        type.color              = R.color.dark_theme_primary_5;
        type.sizeSp             = 14f;

        type.margin.bottomDp    = 4f;

        type.padding.leftDp     = 16f;
        type.padding.rightDp    = 16f;

        return type.textView(context);
    }


    private static LinearLayout fieldView(Integer layoutId,
                                          String fieldNameString,
                                          int fieldValueId,
                                          Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        TextViewBuilder     name   = new TextViewBuilder();
        TextViewBuilder     value  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        if (layoutId != null)
            layout.id               = layoutId;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation          = LinearLayout.HORIZONTAL;

//        layout.backgroundResource   = R.drawable.bg_search_result_field;
//        layout.backgroundColor      = R.color.dark_theme_primary_86;

//        layout.padding.topDp        = 7f;
//        layout.padding.bottomDp     = 7f;

        layout.margin.topDp         = 10f;

        layout.padding.leftDp       = 16f;
        layout.padding.rightDp      = 16f;

        layout.child(name)
              .child(value);

        // [3 A] Name
        // -------------------------------------------------------------------------------------

        name.width                  = 0;
        name.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.weight                 = 1f;

        name.text                   = fieldNameString;

//        name.font                   = Font.serifFontRegular(context);
        name.color                  = R.color.dark_theme_primary_40;
        name.sizeSp                 = 15f;

        // [3 B] Value
        // -------------------------------------------------------------------------------------

        value.id                    = fieldValueId;

        value.width                 = 0;
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.weight                = 3.5f;

//        value.font                  = Font.serifFontRegular(context);
        value.color                 = R.color.dark_theme_primary_25;
        value.sizeSp                = 15f;

        return layout.linearLayout(context);
    }

}
