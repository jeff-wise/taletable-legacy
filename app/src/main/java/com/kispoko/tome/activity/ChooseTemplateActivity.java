
package com.kispoko.tome.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.Sheet;

import java.util.ArrayList;


/**
 * Choose Template Activity
 */
public class ChooseTemplateActivity extends AppCompatActivity
{

    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    private ArrayList<Sheet.Name> templateNames;


    // > ACTIVITY EVENTS
    // -------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_template);

        initializeToolbar();

        this.templateNames = Sheet.templateNames(this);

        renderTemplates();

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


    // > INTERNAL
    // -------------------------------------------------------------------------------------------


    /**
     * Initialize the toolbar ComponentUtil components.
     */
    private void initializeToolbar()
    {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Choose Template");
        actionBar.setElevation(0);

        actionBar.setHomeAsUpIndicator(R.drawable.ic_toolbar_back_arrow_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    /**
     * Render the list of templates.
     */
    private void renderTemplates()
    {

    }

}
