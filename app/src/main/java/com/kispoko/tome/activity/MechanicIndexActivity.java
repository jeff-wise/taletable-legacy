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
import com.kispoko.tome.activity.functionindex.FunctionListRecyclerViewAdapter;
import com.kispoko.tome.activity.mechanicindex.MechanicListRecyclerViewAdapter;
import com.kispoko.tome.engine.programming.function.FunctionIndex;
import com.kispoko.tome.engine.programming.mechanic.MechanicIndex;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.UI;



/**
 * Mechanic Index Activity
 */
public class MechanicIndexActivity extends AppCompatActivity
{

    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mechanic_index);

        initializeToolbar();

        MechanicIndex mechanicIndex = SheetManager.currentSheet().engine().mechanicIndex();

        initializeView(mechanicIndex);
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
        String title = "Mechanics";
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(title);
    }


    /**
     * Initialize the template list view.
     */
    private void initializeView(MechanicIndex mechanicIndex)
    {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mechanic_index_list_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        recyclerView.setAdapter(
                new MechanicListRecyclerViewAdapter(mechanicIndex.mechanics(), this));

        FloatingActionButton addValueSetButton =
                (FloatingActionButton) findViewById(R.id.button_new_mechanic);
        addValueSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MechanicIndexActivity.this, MechanicActivity.class);
                startActivity(intent);
            }
        });

    }



}
