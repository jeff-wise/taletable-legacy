
package com.kispoko.tome.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.util.Util;


/**
 * New Character Activity
 */
public class NewCharacterActivity extends AppCompatActivity
{

    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    private boolean firstCharacter;

    // > ACTIVITY EVENTS
    // -------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //this.sheetDatabase = new SheetDatabase();

        if (SheetDatabase.size() == 0)
            this.firstCharacter = true;
        else
            this.firstCharacter = false;

        setContentView(R.layout.activity_new_character);

        initializeToolbar();

        initializeButtons();
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
        getMenuInflater().inflate(R.menu.toolbar_new_character, menu);
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

        actionBar.setElevation(0);

        if (!this.firstCharacter) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_toolbar_back_arrow_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    /**
     * Initialize the character creation option buttons.
     */
    private void initializeButtons()
    {

        // Set fonts
        TextView fromOfficialTemplateView = (TextView) findViewById(R.id.from_offical_template);
        fromOfficialTemplateView.setTypeface(Util.serifFontBold(this));

        TextView fromCharacterHubView = (TextView) findViewById(R.id.from_character_hub);
        fromCharacterHubView.setTypeface(Util.serifFontBold(this));

        TextView fromFileView = (TextView) findViewById(R.id.from_file);
        fromFileView.setTypeface(Util.serifFontBold(this));


        // From Template
        LinearLayout fromTemplateButton = (LinearLayout) findViewById(R.id.from_template_button);
        final Activity thisActivity = this;
        fromTemplateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, ChooseTemplateGameActivity.class);
                startActivity(intent);
            }
        });

    }


}
