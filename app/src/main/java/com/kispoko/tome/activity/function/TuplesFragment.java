
package com.kispoko.tome.activity.function;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.function.Function;
import com.kispoko.tome.engine.function.Tuple;
import com.kispoko.tome.engine.program.ProgramValueType;
import com.kispoko.tome.engine.program.ProgramValueUnion;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TableLayoutBuilder;
import com.kispoko.tome.util.ui.TableRowBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;

import java.util.ArrayList;
import java.util.List;



/**
 * Tuples Fragment
 */
public class TuplesFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Function function;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static TuplesFragment newInstance(Function function)
    {
        TuplesFragment tuplesFragment = new TuplesFragment();

        Bundle args = new Bundle();
        args.putSerializable("function", function);
        tuplesFragment.setArguments(args);

        return tuplesFragment;
    }


    // FRAGMENT API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.function = (Function) getArguments().getSerializable("function");
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {

        return this.view(getContext());
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    private View view(Context context)
    {
        TableLayout tableLayout = tableLayout(context);

        // > Header
        // -------------------------------------------------------------------------------------

        List<String> headers = new ArrayList<>();
        headers.addAll(this.function.parameterNames());
        headers.add(this.function.resultName());

        tableLayout.addView(headerRowView(headers, context));

        // > Tuples
        // -------------------------------------------------------------------------------------

        for (int i = 0; i < this.function.tuples().size(); i++)
        {
            Tuple tuple = this.function.tuples().get(i);

            Log.d("***TUPLESFRAG", "tuple ");
            List<ProgramValueUnion> elements = new ArrayList<>();
            elements.addAll(tuple.parameters());
            elements.add(tuple.result());

            boolean oddRow = i % 2 == 1;
            tableLayout.addView(tupleRowView(elements, oddRow, context));
        }

        LinearLayout layout = viewLayout(context);
        layout.addView(tableLayout);

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation              = LinearLayout.VERTICAL;
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height                   = LinearLayout.LayoutParams.MATCH_PARENT;

        return layout.linearLayout(context);
    }


    private TableLayout tableLayout(Context context)
    {
        TableLayoutBuilder layout = new TableLayoutBuilder();

        layout.layoutType               = LayoutType.LINEAR;
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.stretchAllColumns        = true;

        return layout.tableLayout(context);
    }


    private TableRow headerRowView(List<String> headers, Context context)
    {
        TableRow row = headerRow(context);

        for (String header : headers) {
            row.addView(headerCellView(header, context));
            Log.d("***TUPLESFRAG", "adding header cell to row");
        }

        return row;
    }


    private TableRow tupleRowView(List<ProgramValueUnion> elements, boolean oddRow, Context context)
    {
        TableRow row = tupleRow(oddRow, context);

        for (ProgramValueUnion element : elements) {
            row.addView(tupleCellView(element, context));
        }

        return row;
    }


    private TableRow headerRow(Context context)
    {
        TableRowBuilder row = new TableRowBuilder();

        row.layoutType          = LayoutType.TABLE;
        row.width               = TableLayout.LayoutParams.MATCH_PARENT;
        row.height              = TableLayout.LayoutParams.WRAP_CONTENT;

        row.padding.top         = R.dimen.function_tuples_header_row_padding_vert;
        row.padding.bottom      = R.dimen.function_tuples_header_row_padding_vert;
        row.padding.left        = R.dimen.function_tuples_row_padding_horz;
        row.padding.right       = R.dimen.function_tuples_row_padding_horz;

        row.backgroundColor     = R.color.dark_blue_7;

        return row.tableRow(context);
    }


    private TableRow tupleRow(boolean oddRow, Context context)
    {
        TableRowBuilder row = new TableRowBuilder();

        row.layoutType          = LayoutType.TABLE;
        row.width               = TableLayout.LayoutParams.MATCH_PARENT;
        row.height              = TableLayout.LayoutParams.WRAP_CONTENT;

        row.padding.top         = R.dimen.function_tuples_tuple_row_padding_vert;
        row.padding.bottom      = R.dimen.function_tuples_tuple_row_padding_vert;
        row.padding.left        = R.dimen.function_tuples_row_padding_horz;
        row.padding.right       = R.dimen.function_tuples_row_padding_horz;

        if (oddRow)
            row.backgroundColor = R.color.dark_blue_4;
        else
            row.backgroundColor = R.color.dark_blue_5;

        return row.tableRow(context);
    }


    private TextView headerCellView(String cellText, Context context)
    {
        TextViewBuilder cell = new TextViewBuilder();

        cell.layoutType             = LayoutType.TABLE_ROW;
        cell.width                  = TableRow.LayoutParams.WRAP_CONTENT;
        cell.height                 = TableRow.LayoutParams.WRAP_CONTENT;

        cell.text                   = cellText;
        cell.color                  = R.color.gold_light;
        cell.size                   = R.dimen.function_tuples_header_cell_text_size;

        return cell.textView(context);
    }


    private TextView tupleCellView(ProgramValueUnion cellValue, Context context)
    {
        TextViewBuilder cell = new TextViewBuilder();

        cell.layoutType             = LayoutType.TABLE_ROW;
        cell.width                  = TableRow.LayoutParams.WRAP_CONTENT;
        cell.height                 = TableRow.LayoutParams.WRAP_CONTENT;

        cell.color                  = R.color.dark_blue_hl_5;
        cell.font                   = Font.sansSerifFontBold(context);
        cell.size                   = R.dimen.function_tuples_tuple_cell_text_size;

        // Cell Text
        if (cellValue.type() == ProgramValueType.LIST)
        {
            StringBuilder listString = new StringBuilder();

            String sep = "";
            for (String value : cellValue.listValue()) {
                listString.append(sep);
                listString.append(value);
                sep = "\n";
            }

            cell.text               = listString.toString();
        }
        else
        {
            cell.text               = cellValue.toString();
        }

        return cell.textView(context);
    }



}
