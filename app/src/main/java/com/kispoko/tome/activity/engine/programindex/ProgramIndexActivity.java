
package com.kispoko.tome.activity.engine.programindex;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.engine.program.ProgramEditorActivity;
import com.kispoko.tome.engine.program.ProgramIndex;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.UI;



/**
 * Program Index Activity
 */
public class ProgramIndexActivity extends AppCompatActivity
{

    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_program_index);

        // [2] Get Program Index
        // -------------------------------------------------------------------------------------

        ProgramIndex programIndex = SheetManager.currentSheet().engine().programIndex();

        // [3] Initialize UI
        // -------------------------------------------------------------------------------------

        initializeToolbar();
        initializeView(programIndex);
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


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        UI.initializeToolbar(this, getString(R.string.programs));
    }


    /**
     * Initialize the template list view.
     */
    private void initializeView(ProgramIndex programIndex)
    {
        // [1] Configure Program List View
        // -------------------------------------------------------------------------------------

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.program_list_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(
                new SimpleDividerItemDecoration(this, R.color.dark_theme_primary_86));
        recyclerView.setAdapter(
                new ProgramListRecyclerViewAdapter(programIndex.programs(), this));

        // [2] Configure New Program FAB
        // -------------------------------------------------------------------------------------

        FloatingActionButton addValueSetButton =
                (FloatingActionButton) findViewById(R.id.button_new_program);
        addValueSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProgramIndexActivity.this, ProgramEditorActivity.class);
                startActivity(intent);
            }
        });

    }

}
