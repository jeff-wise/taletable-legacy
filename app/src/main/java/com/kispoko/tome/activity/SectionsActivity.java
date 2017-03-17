
package com.kispoko.tome.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.SectionType;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.SectionCard;



/**
 * Sections Activity
 */
public class SectionsActivity extends AppCompatActivity
{

    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sections);

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
        titleView.setText(R.string.activity_sections_title);
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    private void initializeView()
    {
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.sections_content);
        contentLayout.addView(view());
    }


    private View view()
    {
        LinearLayout layout = viewLayout();

        // [1 A] Profile Button
        // -------------------------------------------------------------------------------------

        RelativeLayout profileButton =
                SectionCard.view(R.string.sections_button_profile_label,
                                 R.drawable.ic_sections_profile,
                                 R.string.sections_button_profile_description,
                                 SectionCard.Color.GOLD,
                                 this);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SectionsActivity.this, PageListActivity.class);
                intent.putExtra("section_type", SectionType.PROFILE);
                startActivity(intent);
            }
        });

        // [1 B] Encounter Button
        // -------------------------------------------------------------------------------------

        RelativeLayout encounterButton =
                SectionCard.view(R.string.sections_button_encounter_label,
                                 R.drawable.ic_sections_encounter,
                                 R.string.sections_button_encounter_description,
                                 SectionCard.Color.GOLD,
                                 this);

        // [1 C] Campaign Button
        // -------------------------------------------------------------------------------------

        RelativeLayout campaignButton =
                SectionCard.view(R.string.sections_button_campaign_label,
                                 R.drawable.ic_sections_campaign,
                                 R.string.sections_button_campaign_description,
                                 SectionCard.Color.GOLD,
                                 this);

        // [2] Build Layout
        // -------------------------------------------------------------------------------------

        layout.addView(profileButton);
        layout.addView(encounterButton);
        layout.addView(campaignButton);


        return layout;
    }


    private LinearLayout viewLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.orientation      = LinearLayout.VERTICAL;

        layout.backgroundColor  = R.color.dark_blue_5;
        layout.padding.bottom   = R.dimen.sections_padding_bottom;
        layout.padding.left     = R.dimen.sections_padding_horz;
        layout.padding.right    = R.dimen.sections_padding_horz;

        return layout.linearLayout(this);
    }


}
