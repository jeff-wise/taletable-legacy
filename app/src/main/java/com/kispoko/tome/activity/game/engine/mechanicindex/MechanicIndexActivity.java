
package com.kispoko.tome.activity.game.engine.mechanicindex;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.game.engine.mechanic.MechanicActivity;
import com.kispoko.tome.rts.game.engine.MechanicIndex;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.UI;



/**
 * Mechanic Index Activity
 */
public class MechanicIndexActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private MechanicListRecyclerViewAdapter mechanicListAdapter;


    // ACTIVITY API
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_mechanic_index);

        // [2] Initialize UI
        // -------------------------------------------------------------------------------------

        initializeToolbar();

//        MechanicIndex mechanicIndex = SheetManagerOld.currentSheet().engine().mechanicIndex();
 //       initializeView(mechanicIndex);
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


    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        if (this.mechanicListAdapter != null) {
            this.mechanicListAdapter.notifyDataSetChanged();
        }
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        String title = getString(R.string.mechanics);
        UI.initializeToolbar(this, title);
    }


    /**
     * Initialize the template list view.
     */
    private void initializeView(MechanicIndex mechanicIndex)
    {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mechanic_list_view);

        this.mechanicListAdapter =
                new MechanicListRecyclerViewAdapter(mechanicIndex.mechanicByCategoryList(), this);
        recyclerView.setAdapter(this.mechanicListAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addItemDecoration(
                new SimpleDividerItemDecoration(this, R.color.dark_theme_primary_86));

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
