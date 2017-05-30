
package com.kispoko.tome.activity.engine.dictionary;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.engine.valueset.BaseValueSetEditorActivity;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.UI;

import java.util.Dictionary;


/**
 * Dictionary Activity
 */
public class DictionaryActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    //private ValueSetsRecyclerViewAdapter valueSetsAdapter;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dictionary);

        initializeToolbar();

//        Dictionary dictionary = SheetManagerOld.currentSheet().engine().dictionary();

//        initializeView(dictionary);
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

//        if (this.valueSetsAdapter != null) {
//            this.valueSetsAdapter.notifyDataSetChanged();
//        }
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        UI.initializeToolbar(this, getString(R.string.dictionary));
    }


    /**
     * Initialize the template list view.
     */
    private void initializeView(Dictionary dictionary)
    {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.value_set_list_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        dictionary.sortAscByLabel();
//        this.valueSetsAdapter = new ValueSetsRecyclerViewAdapter(dictionary.valueSets(), this);
//        recyclerView.setAdapter(this.valueSetsAdapter);

        SimpleDividerItemDecoration dividerItemDecoration =
                new SimpleDividerItemDecoration(this, R.color.dark_theme_primary_86);
        recyclerView.addItemDecoration(dividerItemDecoration);

        FloatingActionButton addValueSetButton =
                (FloatingActionButton) findViewById(R.id.button_new_value_set);
        addValueSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DictionaryActivity.this, BaseValueSetEditorActivity.class);
                startActivity(intent);
            }
        });

    }


}
