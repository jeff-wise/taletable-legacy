
package com.kispoko.tome.activity.page;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.RecyclerViewBuilder;



/**
 * Group List Fragment
 */
public class GroupListFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Page page;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static GroupListFragment newInstance(Page page)
    {
        GroupListFragment groupListFragment = new GroupListFragment();

        Bundle args = new Bundle();
        args.putSerializable("page", page);
        groupListFragment.setArguments(args);

        return groupListFragment;
    }


    // FRAGMENT API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.page = (Page) getArguments().getSerializable("page");
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

        // > Adapter
        recyclerView.adapter            = new GroupListRecyclerViewAdapter(this.page.groups(),
                                                                           getContext());

        recyclerView.padding.top        = R.dimen.page_group_list_padding_top;

        return recyclerView.recyclerView(context);
    }


}
