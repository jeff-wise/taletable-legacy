
package com.kispoko.tome.lib.ui;


import android.content.Context;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kispoko.tome.R;



/**
 * Search View
 */
public class SearchView
{


    public static RelativeLayout searchBarView(Context context)
    {
        RelativeLayout layout = searchBarViewLayout(context);

        layout.addView(leftView(context));

        // > Clear Button
        layout.addView(clearButtonView(context));

        return layout;
    }


    private static RelativeLayout searchBarViewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.id                   = R.id.search_view;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        //layout.backgroundResource   = R.drawable.bg_searchbar;
//        layout.backgroundColor      = R.color.dark_theme_primary_80;

        layout.layoutGravity        = Gravity.CENTER;

        layout.padding.leftDp       = 8f;
        layout.padding.rightDp      = 9f;
        layout.padding.topDp        = 9f;
        layout.padding.bottomDp     = 9f;

        layout.margin.leftDp        = 7f;
        layout.margin.rightDp       = 7f;

        return layout.relativeLayout(context);
    }


    private static ImageView backButtonView(Context context)
    {
        ImageViewBuilder button = new ImageViewBuilder();

        button.id               = R.id.search_exit;

        button.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        button.image            = R.drawable.ic_searchbar_back;
        button.color            = R.color.dark_theme_primary_25;

        button.addRule(RelativeLayout.ALIGN_PARENT_START);
        button.addRule(RelativeLayout.CENTER_VERTICAL);

        return button.imageView(context);
    }


    private static ImageView clearButtonView(Context context)
    {
        ImageViewBuilder button = new ImageViewBuilder();

        button.id               = R.id.search_clear;

        button.layoutType       = LayoutType.RELATIVE;
        button.width            = RelativeLayout.LayoutParams.WRAP_CONTENT;
        button.height           = RelativeLayout.LayoutParams.WRAP_CONTENT;

        button.image            = R.drawable.ic_searchbar_clear;
        button.color            = R.color.dark_theme_primary_25;

        button.addRule(RelativeLayout.ALIGN_PARENT_END);
        button.addRule(RelativeLayout.CENTER_VERTICAL);

        return button.imageView(context);
    }


    private static LinearLayout leftView(Context context)
    {
        LinearLayout layout = leftViewLayout(context);

        layout.addView(backButtonView(context));

        layout.addView(fieldView(context));

        return layout;
    }


    private static LinearLayout leftViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.layoutType       = LayoutType.RELATIVE;
        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private static EditText fieldView(Context context)
    {
        EditTextBuilder field = new EditTextBuilder();

        field.id                    = R.id.search_field;

        field.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        field.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        field.backgroundResource    = R.drawable.bg_edit_text_no_style;
        field.backgroundColor       = R.color.transparent;

        field.margin.leftDp         = 18f;

        field.hint                  = context.getString(R.string.search_the_engine);
        field.hintColor             = R.color.dark_theme_primary_65;

        field.font                  = Font.serifFontRegular(context);
        field.color                 = R.color.dark_theme_primary_25;
        field.sizeSp                = 16f;

        field.addRule(RelativeLayout.CENTER_IN_PARENT);

        return field.editText(context);
    }
}
