
package com.kispoko.tome.activity.game.engine.variable;


import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;


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

        // > Description
        layout.addView(descriptionView(context));

        // > Value
        layout.addView(valueView(context));

        return layout;
    }


    // > Layout
    // -----------------------------------------------------------------------------------------

    private static LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.id               = R.id.variable_list_item_layout;

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation      = LinearLayout.VERTICAL;

        layout.padding.leftDp   = 12f;
        layout.padding.rightDp  = 12f;
        layout.padding.topDp    = 16f;
        layout.padding.bottomDp = 16f;

        return layout.linearLayout(context);
    }


    // > Name View
    // -----------------------------------------------------------------------------------------

    private static TextView nameView(Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.id                 = R.id.variable_list_item_name;

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

//        name.font               = Font.serifFontRegular(context);
        name.color              = R.color.gold_light;
        name.sizeSp             = 16f;

        return name.textView(context);
    }


    // > Description View
    // -----------------------------------------------------------------------------------------

    private static TextView descriptionView(Context context)
    {
        TextViewBuilder description = new TextViewBuilder();

        description.id              = R.id.variable_list_item_description;

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        description.margin.topDp    = 10f;

        description.padding.leftDp  = 1f;
        description.padding.rightDp = 1f;

//        description.font            = Font.serifFontRegular(context);
        description.color           = R.color.dark_theme_primary_55;
        description.sizeSp          = 14f;

        return description.textView(context);
    }


    // > Types View
    // -----------------------------------------------------------------------------------------

    private static LinearLayout typesView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        TextViewBuilder     type   = new TextViewBuilder();
        TextViewBuilder     kind   = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation      = LinearLayout.HORIZONTAL;

        layout.margin.topDp     = 10f;

        layout.child(type)
              .child(kind);

        // [3 A] Type
        // -------------------------------------------------------------------------------------

        type.id                 = R.id.variable_list_item_type;

        type.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        type.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        type.backgroundResource = R.drawable.bg_variable_type;
        type.backgroundColor    = R.color.dark_theme_primary_81;

//        type.font               = Font.serifFontRegular(context);
        type.color              = R.color.dark_theme_primary_35;
        type.sizeSp             = 11.5f;

        type.padding.topDp      = 4f;
        type.padding.bottomDp   = 4f;
        type.padding.leftDp     = 7f;
        type.padding.rightDp    = 7f;

        type.margin.rightDp     = 7f;

        // [3 B] Kind
        // -------------------------------------------------------------------------------------

        kind.id                 = R.id.variable_list_item_kind;

        kind.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        kind.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        kind.backgroundResource = R.drawable.bg_variable_type;
        kind.backgroundColor    = R.color.dark_theme_primary_81;

//        kind.font               = Font.serifFontRegular(context);
        kind.color              = R.color.dark_theme_primary_35;
        kind.sizeSp             = 11.5f;

        kind.padding.topDp      = 4f;
        kind.padding.bottomDp   = 4f;
        kind.padding.leftDp     = 7f;
        kind.padding.rightDp    = 7f;

        return layout.linearLayout(context);
    }


    // > Value View
    // -----------------------------------------------------------------------------------------

    private static LinearLayout valueView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     value  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.id                   = R.id.variable_list_item_value_layout;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation          = LinearLayout.HORIZONTAL;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.backgroundResource   = R.drawable.bg_variable_value;
        layout.backgroundColor      = R.color.dark_theme_primary_86;

        layout.margin.topDp         = 10f;

        layout.padding.leftDp       = 5f;
        layout.padding.rightDp      = 5f;
        layout.padding.topDp        = 6f;
        layout.padding.bottomDp     = 6f;

        layout.child(icon)
              .child(value);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = R.drawable.ic_current_variable_value;
        icon.color                  = R.color.dark_theme_primary_65;

        icon.margin.rightDp         = 2f;

        // [3 B] Value
        // -------------------------------------------------------------------------------------

        value.id                    = R.id.variable_list_item_value;

        value.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

//        value.font                  = Font.serifFontRegular(context);
        value.color                 = R.color.dark_theme_primary_35;
        value.sizeSp                = 14.5f;

        value.margin.bottomDp       = 1f;

        return layout.linearLayout(context);
    }
}
