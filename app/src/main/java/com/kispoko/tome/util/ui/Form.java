
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;


/**
 * Form Builder
 */
public class Form
{

    // FIELD
    // -----------------------------------------------------------------------------------------

    public static LinearLayout field(int headerStringId,
                                     int descriptionStringId,
                                     View inputView,
                                     Context context)
    {
        LinearLayout layout = fieldLayout(context);

        // > Header
        layout.addView(fieldHeaderView(headerStringId, context));

        // > Description
        layout.addView(fieldDescriptionView(descriptionStringId, context));

        // > Input View
        layout.addView(inputView);

        return layout;
    }


    private static LinearLayout fieldLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.bottom        = R.dimen.field_margin_bottom;

        return layout.linearLayout(context);
    }


    private static TextView fieldHeaderView(int headerTextId, Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.textId               = headerTextId;
        header.font                 = Font.sansSerifFontBold(context);
        header.color                = R.color.gold_5;
        header.size                 = R.dimen.field_header_text_size;

        header.margin.bottom        = R.dimen.field_header_margin_bottom;

        return header.textView(context);
    }


    private static TextView fieldDescriptionView(int descriptionTextId, Context context)
    {
        TextViewBuilder description = new TextViewBuilder();

        description.width               = LinearLayout.LayoutParams.MATCH_PARENT;
        description.height              = LinearLayout.LayoutParams.WRAP_CONTENT;

        description.textId              = descriptionTextId;
        description.font                = Font.sansSerifFontRegular(context);
        description.color               = R.color.dark_blue_hl_8;
        description.size                = R.dimen.field_description_text_size;

        description.margin.bottom       = R.dimen.field_description_margin_bottom;

        return description.textView(context);
    }


    // TEXT INPUT
    // -----------------------------------------------------------------------------------------

    public static LinearLayout textInput(String value, Context context)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout    = new LinearLayoutBuilder();
        EditTextBuilder     inputText = new EditTextBuilder();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.child(inputText);

        // [3] Input Text
        // --------------------------------------------------------------------------------------

        inputText.width                 = LinearLayout.LayoutParams.MATCH_PARENT;
        inputText.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        inputText.font                  = Font.sansSerifFontRegular(context);
        inputText.color                 = R.color.dark_blue_hl_5;
        inputText.size                  = R.dimen.field_text_input_text_size;
        inputText.text                  = value;

        inputText.backgroundResource    = R.drawable.bg_edit_text;

        return layout.linearLayout(context);
    }

}
