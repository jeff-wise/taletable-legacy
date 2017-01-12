
package com.kispoko.tome.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.util.UI;



/**
 * Edit Activity
 */
public class EditActivity extends AppCompatActivity
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private TextWidget widget;


    // ACTIVITY LIFECYCLE EVENTS
    // -------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Read parameters passed from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.widget = (TextWidget) extras.getSerializable("WIDGET");
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
        getMenuInflater().inflate(R.menu.sheet, menu);
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
    // -------------------------------------------------------------------------------------------

    // > User Interface
    // -------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar ComponentUtil components.
     */
    private void initializeToolbar()
    {
        UI.initializeToolbar(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_toolbar_back);

        String title = "Edit "; // + this.widgetData.label();
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(title);

        TextView editActionView = (TextView) findViewById(R.id.edit_action_label);

        TextWidget textWidget = (TextWidget) this.widget;

        String widgetLabel = this.widget.data().format().label();
        if (textWidget.valueVariable().hasRefinement())
            editActionView.setText("Select a " + widgetLabel);
        else
            editActionView.setText(widgetLabel);
    }


    /**
     * Create the editor view for the widgetData.
     */
    private void initializeEditorView()
    {
        LinearLayout layout = (LinearLayout) findViewById(R.id.edit_content);

        layout.addView(this.widget.editorView(this));
    }


}
