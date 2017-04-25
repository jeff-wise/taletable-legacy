
package com.kispoko.tome.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.pagelist.PageListRecyclerViewAdpater;
import com.kispoko.tome.sheet.Section;
import com.kispoko.tome.sheet.SectionType;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.util.UI;



/**
 * Pages Activity
 */
public class PageListActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Section section;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_page_list);

        SectionType sectionType = null;
        if (getIntent().hasExtra("section_type")) {
            sectionType = (SectionType) getIntent().getSerializableExtra("section_type");
        }

        switch (sectionType)
        {
            case PROFILE:
                this.section = SheetManager.currentSheet().profileSection();
                break;
            case ENCOUNTER:
                this.section = SheetManager.currentSheet().encounterSection();
                break;
            case CAMPAIGN:
                break;
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
        String title = this.section.type().toString(this) + " Pages";

        UI.initializeToolbar(this, title);
    }


    /**
     * Initialize the template list view.
     */
    private void initializeView()
    {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.page_list_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        recyclerView.setAdapter(new PageListRecyclerViewAdpater(this.section.pages(), this));

        FloatingActionButton addValueSetButton =
                (FloatingActionButton) findViewById(R.id.button_new_page);
        addValueSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PageListActivity.this, PageActivity.class);
                startActivity(intent);
            }
        });

    }


}
