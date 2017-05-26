
package com.kispoko.tome.activity.mechanic.modifiers;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.kispoko.tome.R;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.UI;

import java.util.ArrayList;
import java.util.List;



/**
 * Dice Modifier List Activity
 */
public class DiceModifierListActivity extends AppCompatActivity
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private List<RollModifier>              modifierList;

    private DiceModifierRecyclerViewAdapter recyclerViewAdapter;


    // ACTIVITY LIFECYCLE EVENTS
    // -----------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_list);

        // [2] Get Parameters
        // -------------------------------------------------------------------------------------

        this.modifierList = new ArrayList<>();
        if (getIntent().hasExtra("modifiers")) {
            this.modifierList = (List<RollModifier>) getIntent().getSerializableExtra("modifiers");
        }

        // [3] Initialize UI
        // -------------------------------------------------------------------------------------

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
        getMenuInflater().inflate(R.menu.empty, menu);
        return true;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

//        if (this.valuesAdapter != null) {
//            this.valueSet.sortAscByLabel();
//            this.valuesAdapter.notifyDataSetChanged();
//        }
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        UI.initializeToolbar(this, getString(R.string.dice_modifiers_editor));
    }


    private void initializeView()
    {
        // [1] Initalize RecyclerView
        // -------------------------------------------------------------------------------------

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SimpleDividerItemDecoration dividerItemDecoration =
                        new SimpleDividerItemDecoration(this, R.color.dark_theme_primary_86);
        recyclerView.addItemDecoration(dividerItemDecoration);

        this.recyclerViewAdapter = new DiceModifierRecyclerViewAdapter(this.modifierList, this);
        recyclerView.setAdapter(this.recyclerViewAdapter);

        // [2] Initalize Floating Action Button
        // -------------------------------------------------------------------------------------
//        FloatingActionButton addNewValueButton =
//                (FloatingActionButton) findViewById(R.id.button_new_variable);
    }

}
