package com.kispoko.tome.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.programming.program.statement.Statement;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.util.ui.Form;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;

import java.util.Arrays;
import java.util.List;


/**
 * Statement Activity
 */
public class StatementActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Statement statement;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statement);

        // > Read Parameters
        this.statement = null;
        if (getIntent().hasExtra("statement")) {
            this.statement = (Statement) getIntent().getSerializableExtra("statement");
        }

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
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        // > Initialize action bar
        UI.initializeToolbar(this);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        // > Set the title
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(R.string.statement_activity_title);
    }


    private void initializeView()
    {
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.statement_content);
        contentLayout.addView(this.view());
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    private LinearLayout view()
    {
        LinearLayout layout = viewLayout();

        // [1] Define Fields
        // -------------------------------------------------------------------------------------

        // > Function Field
        // -------------------------------------------------------------------------------------

        // > Buttons
        String builtInButtonLabel = getString(R.string.statement_field_function_button_builtin);
        String definedButtonLabel = getString(R.string.statement_field_function_button_defined);

        TextView builtInButton = Form.textInputButton(builtInButtonLabel, this);
        TextView definedButton = Form.textInputButton(definedButtonLabel, this);

        List<TextView> functionButtons = Arrays.asList(builtInButton, definedButton);
        LinearLayout inputView = Form.textInput(this.statement.functionName(),
                                                functionButtons, this);

        LinearLayout functionField = Form.field(R.string.statement_field_function_label,
                                                R.string.statement_field_function_description,
                                                inputView,
                                                this);

        // [2] Add Fields
        // -------------------------------------------------------------------------------------

        layout.addView(functionField);


        return layout;
    }


    private LinearLayout viewLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation              = LinearLayout.VERTICAL;
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left             = R.dimen.statement_padding_horz;
        layout.padding.right            = R.dimen.statement_padding_horz;
        layout.padding.top              = R.dimen.statement_padding_vert;
        layout.padding.bottom           = R.dimen.statement_padding_vert;

        return layout.linearLayout(this);
    }


}