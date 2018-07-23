
package com.taletable.android.activity.entity.engine.function;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.taletable.android.R;
import com.taletable.android.lib.ui.LayoutType;
import com.taletable.android.lib.ui.LinearLayoutBuilder;
import com.taletable.android.lib.ui.ScrollViewBuilder;
import com.taletable.android.lib.ui.TableLayoutBuilder;
import com.taletable.android.lib.ui.TableRowBuilder;
import com.taletable.android.lib.ui.TextViewBuilder;
import com.taletable.android.model.engine.function.Function;
import com.taletable.android.model.engine.function.Tuple;
import com.taletable.android.util.UI;



/**
 * Tuples Editor Activity
 */
public class TuplesEditorActivity extends AppCompatActivity
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private Function function;


    // ACTIVITY LIFECYCLE EVENTS
    // -----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set activity view
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_tuples_editor);

        // [2] Read parameters
        // -------------------------------------------------------------------------------------

        String functionName = null;
        if (getIntent().hasExtra("function_name")) {
            functionName = getIntent().getStringExtra("function_name");
        }

        // > Lookup Function
//        FunctionIndex functionIndex = SheetManagerOld.currentSheet().engine().functionIndex();
//        this.function = functionIndex.functionWithName(functionName);

        // [3] Initialize UI
        // --------------------------------------------------------------------------------------

        initializeToolbar();
        initializeView();
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.empty, menu);
        return true;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        HorizontalScrollView horizontalScrollView =
                (HorizontalScrollView) findViewById(R.id.horizontal_scroll_view);

        int column1Width = (horizontalScrollView.findViewById(R.id.table_column_1)).getWidth();
        int column2Width = (horizontalScrollView.findViewById(R.id.table_column_2)).getWidth();

        TextView header1 = (TextView) horizontalScrollView.findViewById(R.id.table_header_column_1);
        TextView header2 = (TextView) horizontalScrollView.findViewById(R.id.table_header_column_2);

        header1.setWidth(column1Width);
        header2.setWidth(column2Width);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        UI.initializeToolbar(this, getString(R.string.tuples_editor));
    }


    private void initializeView()
    {
        HorizontalScrollView horizontalScrollView =
                                (HorizontalScrollView) findViewById(R.id.horizontal_scroll_view);
        horizontalScrollView.addView(this.view(this));

    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private LinearLayout view(Context context)
    {
        LinearLayout layout = this.viewLayout(context);

        // Table Header
        layout.addView(this.tableHeaderView(context));

        // Table Rows
        layout.addView(this.tableBodyView(context));

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = FrameLayout.LayoutParams.MATCH_PARENT;
        layout.height           = FrameLayout.LayoutParams.MATCH_PARENT;

        layout.orientation      = LinearLayout.VERTICAL;

        layout.backgroundColor  = R.color.dark_theme_primary_50;

        return layout.linearLayout(context);
    }


    // > Table Header
    // ------------------------------------------------------------------------------------------

    private TableLayout tableHeaderView(Context context)
    {
        TableLayout layout = tableHeaderViewLayout(context);

        layout.addView(this.headerRowView(context));

        return layout;
    }


    private TableLayout tableHeaderViewLayout(Context context)
    {
        TableLayoutBuilder layout = new TableLayoutBuilder();

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundColor      = R.color.dark_theme_primary_88;

        layout.stretchAllColumns    = true;

        layout.padding.topDp        = 14f;
        layout.padding.bottomDp     = 14f;

        return layout.tableLayout(context);
    }


    private TableRow headerRowView(Context context)
    {
        TableRow row = this.headerRowTableRow(context);

        if (function != null)
        {
            int i = 1;
//            for (EngineType parameterType : function.parameterTypes()) {
//                row.addView(headerCellView(parameterType.shortName(), i, context));
//                i++;
//            }
//
//            row.addView(headerCellView(function.resultType().shortName(), i, context));
        }

        return row;
    }


    private TableRow headerRowTableRow(Context context)
    {
        TableRowBuilder row = new TableRowBuilder();

        row.width           = TableLayout.LayoutParams.MATCH_PARENT;
        row.height          = TableLayout.LayoutParams.WRAP_CONTENT;

        return row.tableRow(context);
    }


    private TextView headerCellView(String label, int column, Context context)
    {
        TextViewBuilder cell = new TextViewBuilder();

        cell.layoutType         = LayoutType.TABLE_ROW;
        cell.width              = TableRow.LayoutParams.WRAP_CONTENT;
        cell.height             = TableRow.LayoutParams.WRAP_CONTENT;

        cell.text               = label.toUpperCase();

//        cell.font               = Font.serifFontRegular(context);
        cell.color              = R.color.dark_theme_primary_25;
        cell.sizeSp             = 12.5f;

        cell.padding.leftDp     = 12f;
        cell.padding.rightDp    = 12f;

        switch (column)
        {
            case 1:
                cell.id         = R.id.table_header_column_1;
                cell.backgroundColor    = R.color.dark_theme_primary_80;
                break;
            case 2:
                cell.id         = R.id.table_header_column_2;
                cell.backgroundColor    = R.color.dark_theme_primary_75;
                break;
        }

        return cell.textView(context);
    }


    // > Table Rows
    // -----------------------------------------------------------------------------------------

    private ScrollView tableBodyView(Context context)
    {
        ScrollView scrollView = this.tableBodyScrollView(context);

        scrollView.addView(this.tableRowsView(context));

        return scrollView;
    }


    private ScrollView tableBodyScrollView(Context context)
    {
        ScrollViewBuilder scrollView = new ScrollViewBuilder();

        scrollView.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        scrollView.height           = LinearLayout.LayoutParams.MATCH_PARENT;

        scrollView.backgroundColor  = R.color.dark_theme_primary_80;

        return scrollView.scrollView(context);
    }


    private TableLayout tableRowsView(Context context)
    {
        TableLayout layout = this.tableRowsViewLayout(context);

//        for (Tuple tuple : function.tuples()) {
//            layout.addView(this.tableRowView(tuple, context));
//        }

        return layout;
    }


    private TableLayout tableRowsViewLayout(Context context)
    {
        TableLayoutBuilder layout = new TableLayoutBuilder();

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.stretchAllColumns    = true;

        layout.divider              = ContextCompat.getDrawable(context,
                                                        R.drawable.tuples_table_row_divider);

        layout.backgroundColor      = R.color.dark_theme_primary_84;

        return layout.tableLayout(context);
    }


    private TableRow tableRowView(Tuple tuple, Context context)
    {
        TableRow row = this.tableRowTableRowView(context);

        int i = 1;
//        for (EngineValueUnion valueUnion : tuple.parameters()) {
//            row.addView(this.tupleCellView(valueUnion.toString(), i, context));
//            i++;
//        }

        //row.addView(this.tupleCellView(tuple.result().toString(), i, context));

        return row;
    }


    private TableRow tableRowTableRowView(Context context)
    {
        TableRowBuilder row = new TableRowBuilder();

        row.width               = TableRow.LayoutParams.MATCH_PARENT;
        row.height              = TableRow.LayoutParams.WRAP_CONTENT;

        return row.tableRow(context);
    }


    private TextView tupleCellView(String value, int column, Context context)
    {
        TextViewBuilder cell = new TextViewBuilder();

        cell.layoutType         = LayoutType.TABLE_ROW;
        cell.width              = TableRow.LayoutParams.WRAP_CONTENT;
        cell.height             = TableRow.LayoutParams.WRAP_CONTENT;

        cell.text               = value;

//        cell.font               = Font.serifFontRegular(context);
        cell.color              = R.color.dark_theme_primary_8;
        cell.sizeSp             = 15f;

        cell.padding.topDp      = 14f;
        cell.padding.bottomDp   = 14f;

        cell.padding.leftDp     = 12f;
        cell.padding.rightDp    = 12f;

        switch (column)
        {
            case 1:
                cell.id         = R.id.table_column_1;
                cell.backgroundColor    = R.color.dark_theme_primary_80;
                break;
            case 2:
                cell.id         = R.id.table_column_2;
                cell.backgroundColor    = R.color.dark_theme_primary_75;
                break;
            case 3:
                cell.id         = R.id.table_column_3;
                break;
            case 4:
                cell.id         = R.id.table_column_4;
                break;
        }

        return cell.textView(context);
    }

}
