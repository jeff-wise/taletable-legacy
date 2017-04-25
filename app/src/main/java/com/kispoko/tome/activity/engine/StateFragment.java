
package com.kispoko.tome.activity.engine;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.RecyclerViewBuilder;
import com.kispoko.tome.lib.ui.RelativeLayoutBuilder;



/**
 * Engine Fragment: State
 */
public class StateFragment extends Fragment
{

    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public StateFragment() {
        // Required empty public constructor
    }


    /**
     * @return A new instance of ProfileFragment.
     */
    public static StateFragment newInstance()
    {
        return new StateFragment();
    }


    // API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return this.view(getContext());
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    private LinearLayout view(Context context)
    {
        LinearLayout layout = this.viewLayout(context);

        // > Header
        layout.addView(this.headerView(context));

        // > Variable List
        layout.addView(this.variableListView(context));

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.orientation      = LinearLayout.VERTICAL;

        return layout.linearLayout(context);
    }


    // ** Header
    // ------------------------------------------------------------------------------------------

    private RelativeLayout headerView(Context context)
    {
        RelativeLayout layout = this.headerViewLayout(context);


        return layout;
    }


    private RelativeLayout headerViewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.relativeLayout(context);
    }


    // ** Variable List
    // ------------------------------------------------------------------------------------------

    private RecyclerView variableListView(Context context)
    {
        RecyclerViewBuilder recyclerView = new RecyclerViewBuilder();

        recyclerView.layoutManager      = new LinearLayoutManager(context);

        //recyclerView.adapter            = new ActiveVariablesRecyclerViewAdpater(dictionary.valueSets(), this);

//        SimpleDividerItemDecoration dividerItemDecoration =
//                new SimpleDividerItemDecoration(this, R.color.dark_theme_primary_85);
//        recyclerView.addItemDecoration(dividerItemDecoration);

        return recyclerView.recyclerView(context);
    }

}
