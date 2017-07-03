
package com.kispoko.tome.activity.engine.functionindex;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.engine.function.FunctionEditorActivity;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.UI;



/**
 * Function Index Activity
 */
public class FunctionIndexActivity extends AppCompatActivity
{

    // ACTIVITY LIFECYCLE EVENTS
    // -----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_function_index);

        // [2] Get Function Index
        // -------------------------------------------------------------------------------------

    //    FunctionIndex functionIndex = SheetManagerOld.currentSheet().engine().functionIndex();

        // [3] Initialize UI
        // -------------------------------------------------------------------------------------

        initializeToolbar();
    //    initializeView(functionIndex);
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.empty, menu);
        return true;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        UI.initializeToolbar(this, getString(R.string.functions));
    }


    /**
     * Initialize the template list view.
     */
    private void initializeView()
    {
        // [1] Configure RECYCLER View
        // -------------------------------------------------------------------------------------

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.function_list_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addItemDecoration(
                new SimpleDividerItemDecoration(this, R.color.dark_theme_primary_86));
        //recyclerView.setAdapter(new FunctionRecyclerViewAdapter(functionIndex.functions(), this));

        // [2] Configure FAB
        // -------------------------------------------------------------------------------------

        FloatingActionButton addValueSetButton =
                (FloatingActionButton) findViewById(R.id.button_new_function);
        addValueSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionIndexActivity.this, FunctionEditorActivity.class);
                startActivity(intent);
            }
        });

    }


}
