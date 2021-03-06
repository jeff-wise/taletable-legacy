package com.taletable.android.activity.sheet.widget.table;


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
import com.taletable.android.lib.ui.RecyclerViewBuilder;
import com.taletable.android.model.sheet.widget.TableWidget;


/**
 * Column List Fragment
 */
public class ColumnListFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private TableWidget tableWidget;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static ColumnListFragment newInstance(TableWidget tableWidget)
    {
        ColumnListFragment columnListFragment = new ColumnListFragment();

        Bundle args = new Bundle();
        args.putSerializable("table_widget", tableWidget);
        columnListFragment.setArguments(args);

        return columnListFragment;
    }


    // FRAGMENT API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.tableWidget = (TableWidget) getArguments().getSerializable("table_widget");
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
//        recyclerView.adapter            = new ColumnListRecyclerViewAdapter(
//                                                                this.tableWidget.columns(),
//                                                                getContext());

        recyclerView.padding.top        = R.dimen.draggable_list_padding_top;

        return recyclerView.recyclerView(context);
    }


}
