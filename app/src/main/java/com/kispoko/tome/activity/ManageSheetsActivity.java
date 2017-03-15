
package com.kispoko.tome.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.managesheets.SheetSummaryRecyclerViewAdpater;
import com.kispoko.tome.sheet.Summary;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.functor.CollectionFunctor;

import java.util.List;



/**
 * Switch Character Screen
 */
public class ManageSheetsActivity extends AppCompatActivity
{

    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_sheets);

        initializeToolbar();

        loadSummaries();
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
        titleView.setText(R.string.activity_manage_sheets_title);
    }


    /**
     * Initialize the template list view.
     */
    private void initializeView(List<Summary> summaryList)
    {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.sheet_list_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        recyclerView.setAdapter(new SheetSummaryRecyclerViewAdpater(summaryList, this));

//        FloatingActionButton addValueSetButton =
//                (FloatingActionButton) findViewById(R.id.button_new_function);
//        addValueSetButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FunctionIndexActivity.this, FunctionActivity.class);
//                startActivity(intent);
//            }
//        });

    }


    private void loadSummaries()
    {
        CollectionFunctor<Summary> summaries = CollectionFunctor.empty(Summary.class);

        summaries.setOnLoadListener(new CollectionFunctor.OnLoadListener<Summary>()
        {
            @Override
            public void onLoad(List<Summary> value)
            {
                initializeView(value);
            }

            @Override
            public void onLoadDBError(DatabaseException exception)
            {
                // TODO
            }

            @Override
            public void onLoadError(Exception exception)
            {
                // TODO
            }
        });

        //summaries.load();
    }

}
