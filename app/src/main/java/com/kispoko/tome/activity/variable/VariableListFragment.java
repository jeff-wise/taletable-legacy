
package com.kispoko.tome.activity.variable;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kispoko.tome.activity.program.StatementListFragment;
import com.kispoko.tome.activity.program.StatementListRecyclerViewAdapter;
import com.kispoko.tome.engine.programming.mechanic.Mechanic;
import com.kispoko.tome.engine.programming.program.Program;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.ui.RecyclerViewBuilder;

import java.util.List;



/**
 * Variable List Fragment
 */
public class VariableListFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Mechanic mechanic;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static VariableListFragment newInstance(Mechanic mechanic)
    {
        VariableListFragment variableListFragment = new VariableListFragment();

        Bundle args = new Bundle();
        args.putSerializable("mechanic", mechanic);
        variableListFragment.setArguments(args);

        return variableListFragment;
    }


    // FRAGMENT API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.mechanic = (Mechanic) getArguments().getSerializable("mechanic");
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {

        return this.view(getContext());
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    private RecyclerView view(Context context)
    {
        RecyclerViewBuilder recyclerView = new RecyclerViewBuilder();

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT;
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT;

        recyclerView.layoutManager      = new LinearLayoutManager(context);
        recyclerView.divider            = new SimpleDividerItemDecoration(getContext());

        // > Adapter
        List<VariableUnion> variables = mechanic.variables();
        recyclerView.adapter            = new VariableListRecyclerViewAdapter(variables,
                                                                              getContext());

        return recyclerView.recyclerView(context);
    }



}
