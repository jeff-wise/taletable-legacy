
package com.taletable.android.activity.sheet.grouprow;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.taletable.android.R;
import com.taletable.android.model.sheet.group.GroupRow;
import com.taletable.android.lib.ui.RecyclerViewBuilder;



/**
 * Widget List Fragment
 */
public class WidgetListFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private GroupRow groupRow;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static WidgetListFragment newInstance(GroupRow groupRow)
    {
        WidgetListFragment widgetListFragment = new WidgetListFragment();

        Bundle args = new Bundle();
        //args.putSerializable("group_row", groupRow);
        widgetListFragment.setArguments(args);

        return widgetListFragment;
    }


    // FRAGMENT API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //this.groupRow = (GroupRow) getArguments().getSerializable("group_row");
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
//        recyclerView.adapter            = new WidgetListRecyclerViewAdapter(this.groupRow.widgets(),
//                                                                            getContext());

        recyclerView.padding.top        = R.dimen.group_row_widget_list_padding_top;

        return recyclerView.recyclerView(context);
    }


}
