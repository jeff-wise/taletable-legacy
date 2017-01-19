
package com.kispoko.tome.activity.program;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kispoko.tome.engine.programming.program.Program;
import com.kispoko.tome.engine.programming.program.statement.Statement;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.ui.RecyclerViewBuilder;

import java.util.ArrayList;
import java.util.List;



/**
 * Statements Fragment
 */
public class StatementListFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Program program;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static StatementListFragment newInstance(Program program)
    {
        StatementListFragment statementListFragment = new StatementListFragment();

        Bundle args = new Bundle();
        args.putSerializable("program", program);
        statementListFragment.setArguments(args);

        return statementListFragment;
    }


    // FRAGMENT API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.program = (Program) getArguments().getSerializable("program");
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
        //recyclerView.divider            = new SimpleDividerItemDecoration(getContext());

        // > Adapter
        List<Statement> statements      = new ArrayList<>();
        statements.addAll(this.program.statements());
        statements.add(this.program.resultStatement());
        recyclerView.adapter            = new StatementListRecyclerViewAdapter(statements,
                                                                               getContext());


        return recyclerView.recyclerView(context);
    }


}
