
package com.kispoko.tome.activity.sheet.group;


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
import com.kispoko.tome.model.sheet.group.Group;
import com.kispoko.tome.lib.ui.RecyclerViewBuilder;



/**
 * Group Row List Fragment
 */
public class GroupRowListFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Group group;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static GroupRowListFragment newInstance(Group group)
    {
        GroupRowListFragment groupRowListFragment = new GroupRowListFragment();

        Bundle args = new Bundle();
        //args.putSerializable("group", group);
        groupRowListFragment.setArguments(args);

        return groupRowListFragment;
    }


    // FRAGMENT API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    //    this.group = (Group) getArguments().getSerializable("group");
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
//        recyclerView.adapter            = new GroupRowListRecyclerViewAdapter(this.group.rows(),
//                                                                              this.group.name(),
//                                                                              getContext());

        recyclerView.padding.top        = R.dimen.group_row_list_padding_top;

        return recyclerView.recyclerView(context);
    }


}
