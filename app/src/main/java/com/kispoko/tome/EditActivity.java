
package com.kispoko.tome;


import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kispoko.tome.component.Component;
import com.kispoko.tome.component.Text;



/**
 * Edit Activity
 */
public class EditActivity extends AppCompatActivity
{

    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    private Component component;

    private String componentName;


    // > ACTIVITY EVENTS
    // -------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Read parameters passed from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.component = (Component) extras.getSerializable("COMPONENT");
        }

        setContentView(R.layout.activity_edit);

        initializeToolbar();

        initializeEditorView();
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
        getMenuInflater().inflate(R.menu.actions_edit, menu);
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

    // >> User Interface
    // -------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar UI components.
     */
    private void initializeToolbar()
    {
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);

        String title = "Edit " + this.component.getName();
        actionBar.setTitle(title);
    }


    /**
     * Create the editor view for the component.
     */
    private void initializeEditorView()
    {
        LinearLayout layout = (LinearLayout) findViewById(R.id.editor);

        layout.addView(this.component.getEditorView(this));

    }


}
