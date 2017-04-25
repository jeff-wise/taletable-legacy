
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
import com.kispoko.tome.activity.programindex.ProgramListRecyclerViewAdapter;
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

        setContentView(R.layout.activity_program_index);

        initializeToolbar();

        ProgramIndex programIndex = SheetManager.currentSheet().engine().programIndex();

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
        String title = "Programs";

        UI.initializeToolbar(this, title);
    }


    /**
     * Initialize the template list view.
     */
    private void initializeView(ProgramIndex programIndex)
    {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.program_index_list_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        recyclerView.setAdapter(
                new ProgramListRecyclerViewAdapter(programIndex.programs(), this));

        FloatingActionButton addValueSetButton =
                (FloatingActionButton) findViewById(R.id.button_new_program);
        addValueSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProgramIndexActivity.this, ProgramActivity.class);
                startActivity(intent);
            }
        });

    }

}
