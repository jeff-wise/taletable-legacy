
package com.kispoko.tome.activity.engine;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.engine.search.ActiveSearchResultsRecyclerViewAdapter;
import com.kispoko.tome.engine.RulesEngine;
import com.kispoko.tome.engine.search.EngineActiveSearchResult;
import com.kispoko.tome.lib.ui.SearchView;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.util.UI;

import java.util.List;
import java.util.Set;


/**
 * Engine Activity
 */
public class EngineActivity extends AppCompatActivity
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private ActiveSearchResultsRecyclerViewAdapter searchResultsAdapter;


    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_engine);

        this.initializeToolbar();
        this.initializeTabs();
    }


    // UI
    // -----------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        UI.initializeToolbar(this, getString(R.string.engine));

        // > Enable search button
        // -------------------------------------------------------------------------------------
        ImageButton searchButton = (ImageButton) findViewById(R.id.toolbar_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableSearchMode();
            }
        });
    }


    /**
     * Initialize the pager adapter.
     */
    private void initializeTabs()
    {
        EnginePagerAdapter pagerAdapter = new EnginePagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }


    // SEARCH MODE
    // -----------------------------------------------------------------------------------------

    private void enableSearchMode()
    {
        // [1] Get Views
        // -------------------------------------------------------------------------------------

        RelativeLayout toolbarLayout = (RelativeLayout) findViewById(R.id.toolbar_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        RelativeLayout searchView = SearchView.searchBarView(this);
        final EditText searchFieldView = (EditText) searchView.findViewById(R.id.search_field);
        ImageView searchExitButtonView = (ImageView) searchView.findViewById(R.id.search_exit);

        RecyclerView searchResultsView = (RecyclerView) findViewById(R.id.search_results);

        // [2] Hide / Show Views
        // -------------------------------------------------------------------------------------

        tabLayout.setVisibility(View.GONE);
        toolbarLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        searchResultsView.setVisibility(View.VISIBLE);

        toolbar.addView(searchView);

        // [3] Configure Search Bar
        // -------------------------------------------------------------------------------------

        searchExitButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableSearchMode();
            }
        });


        searchFieldView.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                // DO NOTHING
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                // DO NOTHING
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                RulesEngine engine = SheetManager.currentSheet().engine();
                if (engine != null)
                {
                    String query = searchFieldView.getText().toString();
                    Set<EngineActiveSearchResult> searchResults = engine.searchActive(query);
                    Log.d("***ENGINE_ACTIVITY", "results " + Integer.toString(searchResults.size()));
                    searchResultsAdapter.updateSearchResults(searchResults);
                }
            }

        });

        searchFieldView.requestFocus();
        InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        // [4] Configure Search Results View
        // -------------------------------------------------------------------------------------

        searchResultsView.setLayoutManager(new LinearLayoutManager(this));

        this.searchResultsAdapter = new ActiveSearchResultsRecyclerViewAdapter();
        searchResultsView.setAdapter(this.searchResultsAdapter);

//        SimpleDividerItemDecoration dividerItemDecoration =
//                new SimpleDividerItemDecoration(this, R.color.dark_theme_primary_85);
//        recyclerView.addItemDecoration(dividerItemDecoration);
    }


    private void disableSearchMode()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        RelativeLayout toolbarLayout = (RelativeLayout) findViewById(R.id.toolbar_layout);

        toolbar.removeAllViews();

        tabLayout.setVisibility(View.VISIBLE);
        toolbarLayout.setVisibility(View.VISIBLE);
    }

}
