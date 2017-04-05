
package com.kispoko.tome.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.dictionary.ValueSetsRecyclerViewAdapter;
import com.kispoko.tome.engine.value.Dictionary;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.sheet.SheetManager;


/**
 * Dictionary Activity
 */
public class DictionaryActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ValueSetsRecyclerViewAdapter valueSetsAdapter;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dictionary);

        initializeToolbar();

        Dictionary dictionary = SheetManager.currentSheet().engine().dictionary();

        initializeView(dictionary);
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

        if (this.valueSetsAdapter != null) {
            this.valueSetsAdapter.notifyDataSetChanged();
        }
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        // > Set the title
        String title = "Dictionary";
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setTypeface(Font.serifFontRegular(this));
        titleView.setText(title);


        ImageView backButtonView   = (ImageView) findViewById(R.id.toolbar_back_button);
        backButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // ImageView searchButtonView = (ImageView) findViewById(R.id.toolbar_search_button);
    }


    /**
     * Initialize the template list view.
     */
    private void initializeView(Dictionary dictionary)
    {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.value_set_list_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dictionary.sortAscByLabel();
        this.valueSetsAdapter = new ValueSetsRecyclerViewAdapter(dictionary.valueSets(), this);
        recyclerView.setAdapter(this.valueSetsAdapter);

        FloatingActionButton addValueSetButton =
                (FloatingActionButton) findViewById(R.id.button_new_value_set);
        addValueSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DictionaryActivity.this, ValueSetEditorActivity.class);
                startActivity(intent);
            }
        });

    }


}
