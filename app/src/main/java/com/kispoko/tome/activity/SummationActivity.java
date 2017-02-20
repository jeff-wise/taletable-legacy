
package com.kispoko.tome.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.summation.Summation;
import com.kispoko.tome.engine.summation.SummationException;
import com.kispoko.tome.engine.summation.term.TermUnion;
import com.kispoko.tome.engine.variable.VariableException;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.util.tuple.Tuple2;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;

import java.util.List;



/**
 * Summation Activity
 */
public class SummationActivity extends AppCompatActivity
{

    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_summation);

        // Get the parameters
        // --------------------------------------------------------------------------------------

        String    widgetName = null;
        Summation summation  = null;

        if (getIntent().hasExtra("widget_name")) {
            widgetName = getIntent().getStringExtra("widget_name");
        }

        if (getIntent().hasExtra("summation")) {
            summation = (Summation) getIntent().getSerializableExtra("summation");
        }

        // Build the UI
        // --------------------------------------------------------------------------------------

        initializeToolbar(widgetName);

        initializeView(summation);
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
        getMenuInflater().inflate(R.menu.toolbar_choose_template, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar(String widgetName)
    {
        // > Initialize action bar
        UI.initializeToolbar(this);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        // > Set the title
        String title = widgetName;
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(title);
    }


    /**
     * Initialize the template list view.
     */
    private void initializeView(Summation summation)
    {
        ScrollView scrollView = (ScrollView) findViewById(R.id.summation_view);
        scrollView.addView(view(summation));
    }


    /**
     * The summation view.
     * @param summation The summation.
     * @return The Linear Layout.
     */
    private LinearLayout view(Summation summation)
    {
        // > Layout
        LinearLayout layout = summationLayout();

        // > Add Total
        layout.addView(totalView(summation));

        // > Add Summation Terms
        layout.addView(termsView(summation));

        return layout;
    }


    /**
     * The summation layout.
     * @return The Linear Layout.
     */
    private LinearLayout summationLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.orientation          = LinearLayout.VERTICAL;

        layout.backgroundResource   = R.color.dark_blue_9;

        return layout.linearLayout(this);
    }


    private TextView totalView(Summation summation)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        TextViewBuilder total = new TextViewBuilder();

        String totalString = summation.value().toString();

        // [2] Attributes
        // --------------------------------------------------------------------------------------

        total.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        total.height            = LinearLayout.LayoutParams.WRAP_CONTENT;
        total.layoutGravity     = Gravity.CENTER_HORIZONTAL;

        total.text              = totalString;
        total.size              = R.dimen.summation_total_text_size;
        total.font              = Font.serifFontRegular(this);
        total.color             = R.color.dark_blue_hlx_5;

        total.padding.top       = R.dimen.summation_total_padding_vert;
        total.padding.bottom    = R.dimen.summation_total_padding_vert;

        return total.textView(this);
    }


    /**
     * The terms view.
     * @param summation The summation.
     * @return The Linear Layout.
     */
    private LinearLayout termsView(Summation summation)
    {
        LinearLayout layout = termsLayout();

        for (TermUnion termUnion : summation.termsSorted())
        {
            switch (termUnion.type())
            {
                case INTEGER:
//                    List<Tuple2<Integer,String>> termSummaries = termUnion.integerTerm().summary();
//                    for (Tuple2<Integer,String> summary : termSummaries) {
//                        layout.addView(integerTermLayout(summary.getItem1(), summary.getItem2()));
//                    }
                    break;
            }
        }

        return layout;
    }


    /**
     * The term list layout.
     * @return The Linear Layout.
     */
    private LinearLayout termsLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(this);
    }


    private LinearLayout integerTermLayout(Integer termValue, String termDescription)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout      = new LinearLayoutBuilder();
        TextViewBuilder     value       = new TextViewBuilder();
        TextViewBuilder     description = new TextViewBuilder();

        // [2 A] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity          = Gravity.CENTER_VERTICAL;

        layout.backgroundColor  = R.color.dark_blue_8;
        layout.padding.left     = R.dimen.summation_term_padding_horz;
        layout.padding.right    = R.dimen.summation_term_padding_horz;
        layout.padding.top      = R.dimen.summation_term_padding_vert;
        layout.padding.bottom   = R.dimen.summation_term_padding_vert;
        layout.margin.bottom    = R.dimen.summation_term_margin_bottom;

        layout.child(value)
              .child(description);

        // [2 B] Value
        // --------------------------------------------------------------------------------------

        value.width             = 0;
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.weight            = 1.0f;

        value.backgroundColor   = R.color.dark_blue_8;

        value.text              = termValue.toString();
        value.font              = Font.sansSerifFontBold(this);
        value.color             = R.color.dark_blue_hl_1;
        value.size              = R.dimen.summation_term_value_text_size;

        // [2 C] Description
        // --------------------------------------------------------------------------------------

        description.width           = 0;
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT;
        description.weight          = 3.0f;

        description.backgroundColor = R.color.dark_blue_8;

        description.text            = termDescription;
        description.font            = Font.sansSerifFontRegular(this);
        description.color           = R.color.dark_blue_hl_5;
        description.size            = R.dimen.summation_term_description_text_size;

        return layout.linearLayout(this);
    }


}
