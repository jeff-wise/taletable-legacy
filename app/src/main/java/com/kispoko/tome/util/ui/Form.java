
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.R;

import java.util.List;

import static android.R.attr.button;
import static android.R.attr.gravity;
import static android.R.attr.value;
import static android.R.attr.width;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


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
        return textInput(value, null, context);
    }


    public static LinearLayout textInput(String value,
                                         List<LinearLayout> buttonViews,
                                         Context context)
    {
        LinearLayout layout = textInputLayout(context);

        // > Input Field
        layout.addView(textInputView(value, context));

        // > Buttons
        if (buttonViews != null)
        {
            LinearLayout buttonsLayout = textInputButtonsLayout(context);

            for (View buttonView : buttonViews) {
                buttonsLayout.addView(buttonView);
            }

            layout.addView(buttonsLayout);
        }

        return layout;
    }


    private static LinearLayout textInputLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation              = LinearLayout.VERTICAL;
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private static LinearLayout textInputView(String value, Context context)
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


    private static LinearLayout textInputButtonsLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation              = LinearLayout.HORIZONTAL;
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    // > Buttons
    // -----------------------------------------------------------------------------------------

    public static LinearLayout textInputButton(String buttonLabel, Integer iconId, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.width                = 0;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.weight               = 1.0f;
        layout.gravity              = Gravity.CENTER;

        layout.margin.top           = R.dimen.field_text_input_button_margin_top;

        if (iconId != null)  layout.child(icon);

        layout.child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = iconId;

        icon.margin.right           = R.dimen.field_text_input_button_icon_margin_right;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text                 = buttonLabel.toUpperCase();
        label.font                 = Font.sansSerifFontBold(context);
        label.size                 = R.dimen.field_text_input_button_text_size;
        label.color                = R.color.dark_blue_hl_2;


        return layout.linearLayout(context);
    }


    // BUTTON INPUT
    // -----------------------------------------------------------------------------------------

    public static TextView buttonInput(String buttonLabel, Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        button.font                 = Font.sansSerifFontRegular(context);
        button.color                = R.color.dark_blue_hl_5;
        button.size                 = R.dimen.field_text_input_text_size;
        button.text                 = buttonLabel;

        button.backgroundResource   = R.drawable.bg_edit_text;

        return button.textView(context);
    }


    // TABLE INPUT
    // -----------------------------------------------------------------------------------------

    public static TableLayout tableInput(List<String> columnNames, Context context)
    {
        TableLayout layout = tableInputLayout(context);

        // > Header
        // -------------------------------------------------------------------------------------

        TableRow headerRow = tableInputHeaderRow(context);

        for (String columnName : columnNames) {
            headerRow.addView(tableInputHeaderCellView(columnName, context));
        }

        layout.addView(headerRow);


        return layout;
    }


    public static TableRow tableInputRow(Context context)
    {
        TableRowBuilder row = new TableRowBuilder();

        row.layoutType          = LayoutType.TABLE;
        row.width               = TableLayout.LayoutParams.WRAP_CONTENT;
        row.height              = TableLayout.LayoutParams.WRAP_CONTENT;

        return row.tableRow(context);
    }


    public static LinearLayout textInputCell(String value, Context context)
    {
         // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout    = new LinearLayoutBuilder();
        EditTextBuilder     inputText = new EditTextBuilder();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.layoutType           = LayoutType.TABLE_ROW;
        layout.width                = TableRow.LayoutParams.MATCH_PARENT;
        layout.height               = TableRow.LayoutParams.WRAP_CONTENT;

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

        inputText.margin.right          = R.dimen.field_table_input_cell_margin_right;

        return layout.linearLayout(context);
    }


    public static TextView buttonInputCell(String buttonLabel, Context context)
    {
        TextViewBuilder button = new TextViewBuilder();

        button.layoutType           = LayoutType.TABLE_ROW;
        button.width                = TableRow.LayoutParams.MATCH_PARENT;
        button.height               = TableRow.LayoutParams.WRAP_CONTENT;

        button.font                 = Font.sansSerifFontRegular(context);
        button.color                = R.color.dark_blue_hl_5;
        button.size                 = R.dimen.field_text_input_text_size;
        button.text                 = buttonLabel;

        button.backgroundResource   = R.drawable.bg_edit_text;

        button.margin.right         = R.dimen.field_table_input_cell_margin_right;

        return button.textView(context);
    }


    private static TableLayout tableInputLayout(Context context)
    {
        TableLayoutBuilder layout = new TableLayoutBuilder();

        layout.layoutType           = LayoutType.LINEAR;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.stretchAllColumns    = true;

        return layout.tableLayout(context);
    }


    private static TableRow tableInputHeaderRow(Context context)
    {
        TableRowBuilder row = new TableRowBuilder();

        row.layoutType              = LayoutType.TABLE;
        row.width                   = TableLayout.LayoutParams.MATCH_PARENT;
        row.height                  = TableLayout.LayoutParams.WRAP_CONTENT;

        row.padding.top             = R.dimen.field_table_input_header_row_padding_vert;
        row.padding.bottom          = R.dimen.field_table_input_header_row_padding_vert;

        return row.tableRow(context);
    }


    private static TextView tableInputHeaderCellView(String header, Context context)
    {
        TextViewBuilder cell = new TextViewBuilder();

        cell.layoutType             = LayoutType.TABLE_ROW;
        cell.width                  = TableRow.LayoutParams.WRAP_CONTENT;
        cell.height                 = TableRow.LayoutParams.WRAP_CONTENT;

        cell.text                   = header.toUpperCase();
        cell.size                   = R.dimen.field_table_input_header_text_size;
        cell.font                   = Font.sansSerifFontBold(context);
        cell.color                  = R.color.dark_blue_hl_5;

        cell.margin.right           = R.dimen.field_table_input_cell_margin_right;

        return cell.textView(context);
    }


}
