
package com.kispoko.tome.lib.model.form;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;



/**
 * Form
 */
public class Form
{

    // > Layout
    // -----------------------------------------------------------------------------------------

    public static LinearLayout layout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.topDp        = 5f;
        layout.padding.bottomDp     = 15f;

        layout.backgroundColor      = R.color.dark_theme_primary_84;

        return layout.linearLayout(context);
    }


    // > Divider
    // -----------------------------------------------------------------------------------------

    public static LinearLayout dividerView(Context context)
    {
        LinearLayoutBuilder divider = new LinearLayoutBuilder();

        divider.width           = LinearLayout.LayoutParams.MATCH_PARENT;
        divider.heightDp        = 1;

        divider.backgroundColor = R.color.dark_theme_primary_86;

        return divider.linearLayout(context);
    }


    // > Header
    // -----------------------------------------------------------------------------------------

    public static TextView headerView(String headerString, Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.text             = headerString.toUpperCase();

        header.font             = Font.serifFontBold(context);
        header.color            = R.color.dark_theme_primary_55;
        header.sizeSp           = 12f;

        header.margin.topDp     = 25f;
        header.margin.bottomDp  = 5f;
        header.margin.leftDp    = 13f;

        return header.textView(context);
    }


    // > Toolbar
    // -----------------------------------------------------------------------------------------

    public static LinearLayout toolbarView(Context context)
    {
        LinearLayout layout = toolbarViewLayout(context);

        // > Help Button
        layout.addView(toolbarButtonView(R.drawable.ic_form_toolbar_help,
                                         R.string.help,
                                         context));

        // > Duplicate Button
        layout.addView(toolbarButtonView(R.drawable.ic_form_toolbar_duplicate,
                                         R.string.duplicate,
                                         context));

        // > Export Button
        layout.addView(toolbarButtonView(R.drawable.ic_form_toolbar_export,
                                         R.string.export,
                                         context));

        // > Delete Button
        layout.addView(toolbarButtonView(R.drawable.ic_form_toolbar_delete,
                                         R.string.delete,
                                         context));

        return layout;
    }


    private static LinearLayout toolbarViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.topDp         = 25f;
        layout.margin.leftDp        = 10f;
        layout.margin.rightDp       = 10f;

        return layout.linearLayout(context);
    }


    private static LinearLayout toolbarButtonView(int iconId, int labelId, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation      = LinearLayout.VERTICAL;

        layout.width            = 0;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.weight           = 1f;

        layout.gravity          = Gravity.CENTER;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image              = iconId;
        icon.color              = R.color.dark_blue_hlx_4;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.textId            = labelId;

        label.font              = Font.serifFontRegular(context);
        label.color             = R.color.dark_blue_hl_4;
        label.sizeSp            = 10f;

        label.margin.topDp      = 3f;


        return layout.linearLayout(context);
    }

}
