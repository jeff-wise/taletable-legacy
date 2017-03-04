
package com.kispoko.tome.activity.program;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.program.statement.Statement;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TableLayoutBuilder;
import com.kispoko.tome.util.ui.TableRowBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;



/**
 * Statement List Item View
 */
class StatementListItemView
{


    static View normalStatementView(Context context)
    {
        LinearLayout layout = cardLayout(context);

        // > Header
        layout.addView(variableNameView(context));

        // > Function
        layout.addView(functionView(context));

        // > Parameters
        layout.addView(parametersView(context));

        return layout;
    }


    static View resultStatementView(Context context)
    {
        LinearLayout layout = cardLayout(context);

        // > Header
        layout.addView(resultStatementHeaderView(context));

        // > Function
        layout.addView(functionView(context));

        // > Parameters
        layout.addView(parametersView(context));

        return layout;
    }


    private static LinearLayout cardLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.id                   = R.id.statement_list_item_layout;
        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundResource   = R.drawable.bg_statement_card;

        layout.margin.left          = R.dimen.program_statements_card_margin_horz;
        layout.margin.right         = R.dimen.program_statements_card_margin_horz;
        layout.margin.top           = R.dimen.program_statements_card_margin_vert;
        layout.margin.bottom        = R.dimen.program_statements_card_margin_vert;

        return layout.linearLayout(context);
    }


    private static TextView variableNameView(Context context)
    {
        TextViewBuilder variable = new TextViewBuilder();

        variable.id                 = R.id.statement_list_item_variable;
        variable.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        variable.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        variable.font               = Font.sansSerifFontBold(context);
        variable.color              = R.color.gold_light;
        variable.size               = R.dimen.program_statements_card_variable_text_size;

        variable.margin.bottom      = R.dimen.program_statements_card_header_margin_bottom;

        return variable.textView(context);
    }


    private static TextView resultStatementHeaderView(Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        header.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.font               = Font.sansSerifFontBold(context);
        header.color              = R.color.gold_light;
        header.size               = R.dimen.program_statements_card_result_header_text_size;
        header.textId             = R.string.statement_result_header;

        header.margin.bottom     = R.dimen.program_statements_card_header_margin_bottom;

        return header.textView(context);
    }


    private static LinearLayout functionView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout   = new LinearLayoutBuilder();
        TextViewBuilder     label    = new TextViewBuilder();
        TextViewBuilder     function = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.bottom        = R.dimen.program_statements_card_function_margin_bottom;

        layout.child(label)
              .child(function);

        // [3 A] Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.textId                = R.string.statement_function_name_label;
        label.size                  = R.dimen.program_statements_card_function_label_text_size;
        label.font                  = Font.sansSerifFontBold(context);
        label.color                 = R.color.dark_blue_hl_8;

        label.margin.bottom         = R.dimen.program_statements_card_function_label_margin_bottom;

        // [3 B] Function
        // -------------------------------------------------------------------------------------

        function.id                     = R.id.statement_list_item_function;
        function.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        function.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        function.font                   = Font.sansSerifFontRegular(context);
        function.size                   = R.dimen.program_statements_card_function_text_size;
        function.color                  = R.color.dark_blue_hlx_7;


        return layout.linearLayout(context);
    }


    private static LinearLayout parametersView(Context context)
    {
        LinearLayout layout = parametersLayout(context);

        layout.addView(parametersHeaderView(context));

        TableLayout parametersTable = parametersTableLayout(context);

        parametersTable.addView(parametersRowHeaderView(context));
        for (int i = 0; i < Statement.MAX_PARAMETERS; i++) {
            parametersTable.addView(parameterRowView(i, context));
        }

        layout.addView(parametersTable);

        return layout;
    }


    private static LinearLayout parametersLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private static TableLayout parametersTableLayout(Context context)
    {
        TableLayoutBuilder layout = new TableLayoutBuilder();

        layout.layoutType           = LayoutType.LINEAR;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.tableLayout(context);
    }


    private static TextView parametersHeaderView(Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.textId           = R.string.statement_parameters_label;
        header.color            = R.color.dark_blue_hl_8;
        header.font             = Font.sansSerifFontBold(context);
        header.size             = R.dimen.program_statements_card_parameters_label_text_size;

        header.margin.bottom    = R.dimen.program_statements_card_parameters_label_margin_bottom;

        return header.textView(context);
    }



    private static TableRow parametersRowHeaderView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        TableRowBuilder     layout = new TableRowBuilder();
        TextViewBuilder     value  = new TextViewBuilder();
        TextViewBuilder     type   = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType           = LayoutType.TABLE;
        layout.width                = TableLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = TableLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.top          = R.dimen.program_statements_card_parameter_row_padding_vert;
        layout.padding.bottom       = R.dimen.program_statements_card_parameter_row_padding_vert;

        layout.child(value)
              .child(type);

        // [3 A] Parameter Value
        // -------------------------------------------------------------------------------------

        value.layoutType        = LayoutType.TABLE_ROW;
        value.width             = TableRow.LayoutParams.WRAP_CONTENT;
        value.height            = TableRow.LayoutParams.WRAP_CONTENT;

        value.font              = Font.sansSerifFontRegular(context);
        value.color             = R.color.dark_blue_hl_8;
        value.size              = R.dimen.program_statements_card_parameters_table_header_text_size;
        value.textId            = R.string.statement_parameters_table_header_value;

        //value.padding.left      = R.dimen.program_statements_card_parameter_cell_padding_horz;
        value.padding.right     = R.dimen.program_statements_card_parameter_cell_padding_horz;

        // [3 A] Parameter Type
        // -------------------------------------------------------------------------------------

        type.layoutType         = LayoutType.TABLE_ROW;
        type.width              = TableRow.LayoutParams.WRAP_CONTENT;
        type.height             = TableRow.LayoutParams.WRAP_CONTENT;

        type.textId             = R.string.statement_parameters_table_header_type;
        type.font               = Font.sansSerifFontRegular(context);
        type.color              = R.color.dark_blue_hl_8;
        type.size               = R.dimen.program_statements_card_parameters_table_header_text_size;

        //type.padding.left       = R.dimen.program_statements_card_parameter_cell_padding_horz;
        type.padding.right      = R.dimen.program_statements_card_parameter_cell_padding_horz;

        return layout.tableRow(context);
    }


    private static TableRow parameterRowView(int parameterIndex, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        TableRowBuilder     layout = new TableRowBuilder();
        TextViewBuilder     value  = new TextViewBuilder();
        TextViewBuilder     type   = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType           = LayoutType.TABLE;
        layout.width                = TableLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = TableLayout.LayoutParams.WRAP_CONTENT;
        layout.visibility           = View.GONE;

        layout.padding.top          = R.dimen.program_statements_card_parameter_row_padding_vert;
        layout.padding.bottom       = R.dimen.program_statements_card_parameter_row_padding_vert;

        switch (parameterIndex)
        {
            case 0:
                layout.id           = R.id.statement_list_item_parameter1_layout;
                break;
            case 1:
                layout.id           = R.id.statement_list_item_parameter2_layout;
                break;
            case 2:
                layout.id           = R.id.statement_list_item_parameter3_layout;
                break;
        }

        layout.child(value)
              .child(type);

        // [3 A] Parameter Value
        // -------------------------------------------------------------------------------------

        value.layoutType            = LayoutType.TABLE_ROW;
        value.width                 = TableRow.LayoutParams.WRAP_CONTENT;
        value.height                = TableRow.LayoutParams.WRAP_CONTENT;

        value.font                  = Font.sansSerifFontRegular(context);
        value.color                 = R.color.dark_blue_hlx_7;
        value.size                  = R.dimen.program_statements_card_parameter_value_text_size;

        //value.padding.left          = R.dimen.program_statements_card_parameter_cell_padding_horz;
        value.padding.right         = R.dimen.program_statements_card_parameter_cell_padding_horz;

        switch (parameterIndex)
        {
            case 0:
                value.id            = R.id.statement_list_item_parameter1_value;
                break;
            case 1:
                value.id            = R.id.statement_list_item_parameter2_value;
                break;
            case 2:
                value.id            = R.id.statement_list_item_parameter3_value;
                break;
        }

        // [3 A] Parameter Type
        // -------------------------------------------------------------------------------------

        type.layoutType             = LayoutType.TABLE_ROW;
        type.width                  = TableRow.LayoutParams.WRAP_CONTENT;
        type.height                 = TableRow.LayoutParams.WRAP_CONTENT;

        type.font                   = Font.sansSerifFontRegular(context);
        type.color                  = R.color.dark_blue_hlx_7;
        type.size                   = R.dimen.program_statements_card_parameter_type_text_size;

        //type.padding.left           = R.dimen.program_statements_card_parameter_cell_padding_horz;
        type.padding.right          = R.dimen.program_statements_card_parameter_cell_padding_horz;

        switch (parameterIndex)
        {
            case 0:
                type.id            = R.id.statement_list_item_parameter1_type;
                break;
            case 1:
                type.id            = R.id.statement_list_item_parameter2_type;
                break;
            case 2:
                type.id            = R.id.statement_list_item_parameter3_type;
                break;
        }

        return layout.tableRow(context);
    }

}
