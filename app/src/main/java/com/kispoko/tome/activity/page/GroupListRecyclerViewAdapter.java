
package com.kispoko.tome.activity.page;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.GroupActivity;
import com.kispoko.tome.model.sheet.group.Group;

import java.util.List;



/**
 * Gropu List Recycler View Adapater
 */
public class GroupListRecyclerViewAdapter
       extends RecyclerView.Adapter<GroupListRecyclerViewAdapter.ViewHolder>
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private List<Group> groupList;

    private Context     context;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public GroupListRecyclerViewAdapter(List<Group> groupList, Context context)
    {
        this.groupList  = groupList;
        this.context    = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = GroupListItemView.view(parent.getContext());
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(GroupListRecyclerViewAdapter.ViewHolder viewHolder,
                                 int position)
    {
        Group group = this.groupList.get(position);

        // > Name
//        viewHolder.setName(group.name());
//
//        // > Rows
//        viewHolder.setRows(group.rows().size());

        // > On Click Listener
        viewHolder.setOnClick(group, this.context);
    }


    // The number of value sets to display
    @Override
    public int getItemCount()
    {
        return this.groupList.size();
    }


    /**
     * The View Holder caches a view for each item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private RelativeLayout  layoutView;
        private TextView        nameView;
        private TextView        rowCountView;
        private TextView        rowCountLabelView;


        public ViewHolder(final View itemView)
        {
            super(itemView);

            this.layoutView =
                    (RelativeLayout) itemView.findViewById(R.id.group_list_item_layout);

            this.nameView =
                    (TextView) itemView.findViewById(R.id.group_list_item_name);

            this.rowCountView =
                    (TextView) itemView.findViewById(R.id.group_list_item_row_count);

            this.rowCountLabelView =
                    (TextView) itemView.findViewById(R.id.group_list_item_row_count_label);
        }


        public void setName(String name)
        {
            this.nameView.setText(name);
        }


        public void setRows(Integer rowCount)
        {
            this.rowCountView.setText(rowCount.toString());

            if (rowCount == 1)
                this.rowCountLabelView.setText(R.string.row_upper);
            else
                this.rowCountLabelView.setText(R.string.rows_upper);
        }


        public void setOnClick(final Group group, final Context context)
        {
            this.layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, GroupActivity.class);

                    Bundle bundle = new Bundle();
      //              bundle.putSerializable("group", group);
                    intent.putExtras(bundle);

                    context.startActivity(intent);
                }
            });
        }

    }


}
