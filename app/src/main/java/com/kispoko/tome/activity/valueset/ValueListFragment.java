
package com.kispoko.tome.activity.valueset;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kispoko.tome.engine.value.ValueSet;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.lib.ui.RecyclerViewBuilder;



/**
 * ValueSet Value List Fragment
 */
public class ValueListFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ValueSet valueSet;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ValueListFragment() {
        // Empty Fragment Constructor
    }


    public static ValueListFragment newInstance(ValueSet valueSet)
    {
        ValueListFragment valueListFragment = new ValueListFragment();


        Bundle args = new Bundle();
        args.putSerializable("value_set", valueSet);
        valueListFragment.setArguments(args);

        return valueListFragment;
    }


    // FRAGMENT API
    // ------------------------------------------------------------------------------------------

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.valueSet = (ValueSet) getArguments().getSerializable("value_set");
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        return view();
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    private RecyclerView view()
    {
        RecyclerViewBuilder recyclerView = new RecyclerViewBuilder();

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT;
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT;

        recyclerView.layoutManager      = new LinearLayoutManager(getContext());
        recyclerView.divider            = new SimpleDividerItemDecoration(getContext());
        recyclerView.adapter            = new ValuesRecyclerViewAdapter(this.valueSet.values());


        return recyclerView.recyclerView(getContext());
    }

}
