
package com.kispoko.tome.activity.engine.search;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;



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
        layout.addView(fieldView(nameFieldName, R.id.search_result_variable_name, context));

        // > Label Field
        String labelFieldName = context.getString(R.string.label);
        layout.addView(fieldView(labelFieldName, R.id.search_result_variable_label, context));

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
        layout.addView(fieldView(nameFieldName, R.id.search_result_mechanic_name, context));

        // > Label Field
        String labelFieldName = context.getString(R.string.label);
        layout.addView(fieldView(labelFieldName, R.id.search_result_mechanic_label, context));

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

        return layout.linearLayout(context);
    }


    private static TextView resultTypeView(Context context)
    {
        TextViewBuilder type = new TextViewBuilder();

        type.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        type.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        type.id                 = R.id.search_result_type;

        type.font               = Font.sansSerifFontBold(context);
        type.color              = R.color.dark_theme_primary_15;
        type.sizeSp             = 15f;

        return type.textView(context);
    }


    private static LinearLayout fieldView(String fieldNameString,
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

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation          = LinearLayout.HORIZONTAL;

        layout.child(name)
              .child(value);

        // [3 A] Name
        // -------------------------------------------------------------------------------------

        name.width                  = 0;
        name.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.weight                 = 1f;

        name.text                   = fieldNameString;

        name.font                   = Font.serifFontRegular(context);
        name.color                  = R.color.dark_theme_primary_25;
        name.sizeSp                 = 17f;

        // [3 B] Value
        // -------------------------------------------------------------------------------------

        value.id                    = fieldValueId;

        value.width                 = 0;
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.weight                = 1f;

        value.font                  = Font.serifFontRegular(context);
        value.color                 = R.color.dark_theme_primary_25;
        value.sizeSp                = 17f;

        return layout.linearLayout(context);
    }

}
