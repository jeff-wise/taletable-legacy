
package com.kispoko.tome.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.util.ui.EditTextBuilder;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.view.Calculator;


/**
 * Number Activity
 */
public class NumberActivity extends AppCompatActivity
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------



    // > ACTIVITY EVENTS
    // -------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_number);

        String  widgetName = null;
        int     value = 0;

        if (getIntent().hasExtra("widget_name")) {
            widgetName = getIntent().getStringExtra("widget_name");
        }
        if (getIntent().hasExtra("value")) {
            value = getIntent().getIntExtra("value", 0);
        }

        initializeToolbar(widgetName);

        initializeView(value);
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

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // > INTERNAL
    // -------------------------------------------------------------------------------------------


    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar(String widgetName)
    {
        // > Initialize action bar
        UI.initializeToolbar(this);
        ActionBar actionBar = getSupportActionBar();

        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_toolbar_back);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // > Set the title
        String title = widgetName;
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(title);
    }


    /**
     * Initialize the template list view.
     */
    private void initializeView(int value)
    {
        LinearLayout contentView = (LinearLayout) findViewById(R.id.activity_content);
        contentView.addView((view(value)));
    }


    private LinearLayout view(int value)
    {
        LinearLayout layout = viewLayout();

        // > Add Calculator to view
        LinearLayout calculator = Calculator.view(value, this);
        layout.addView(calculator);

        return layout;
    }


    /**
     * The layout for the activity content view.
     * @return The Linear Layout.
     */
    private LinearLayout viewLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;

        return layout.linearLayout(this);
    }


}
