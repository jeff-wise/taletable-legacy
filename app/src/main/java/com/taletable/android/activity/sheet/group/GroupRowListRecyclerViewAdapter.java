
package com.taletable.android.activity.sheet.group;


import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taletable.android.R;
import com.taletable.android.model.sheet.group.GroupRow;

import java.util.List;



/**
 * Group Row List Recycler View Adapater
 */
public class GroupRowListRecyclerViewAdapter
       extends RecyclerView.Adapter<GroupRowListRecyclerViewAdapter.ViewHolder>
{


    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private List<GroupRow>  rowList;
    private String          groupName;

    private Context         context;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public GroupRowListRecyclerViewAdapter(List<GroupRow> rowList,
                                           String groupName,
                                           Context context)
    {
        this.rowList    = rowList;
        this.groupName  = groupName;

        this.context    = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = GroupRowListItemView.view(parent.getContext());
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(GroupRowListRecyclerViewAdapter.ViewHolder viewHolder,
                                 int position)
    {
        GroupRow groupRow = this.rowList.get(position);

        // > Index
        String indexString = this.context.getString(R.string.row) +
                              " " + Integer.toString(position + 1);
        viewHolder.setIndexString(indexString);

        // > Widgets
        //viewHolder.setWidgets(groupRow.widgets().size());

        // > On Click Listener
        viewHolder.setOnClick(groupRow, this.groupName, this.context);
    }


    // The number of value sets to display
    @Override
    public int getItemCount()
    {
        return this.rowList.size();
    }


    /**
     * The View Holder caches a view for each item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private RelativeLayout  layoutView;
        private TextView        indexView;
        private TextView        widgetCountView;
        private TextView        widgetCountLabelView;


        public ViewHolder(final View itemView)
        {
            super(itemView);

            this.layoutView =
                    (RelativeLayout) itemView.findViewById(R.id.group_row_list_item_layout);

            this.indexView =
                    (TextView) itemView.findViewById(R.id.group_row_list_item_index);

            this.widgetCountView =
                    (TextView) itemView.findViewById(R.id.group_row_list_item_widget_count);

            this.widgetCountLabelView =
                    (TextView) itemView.findViewById(R.id.group_row_list_item_widget_count_label);
        }


        public void setIndexString(String indexString)
        {
            this.indexView.setText(indexString);
        }


        public void setWidgets(Integer widgetCount)
        {
            this.widgetCountView.setText(widgetCount.toString());

            if (widgetCount == 1)
                this.widgetCountLabelView.setText(R.string.widget_upper);
            else
                this.widgetCountLabelView.setText(R.string.widgets_upper);
        }


        public void setOnClick(final GroupRow groupRow,
                               final String groupName,
                               final Context context)
        {
            this.layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
//                    Intent intent = new Intent(context, GroupRowActivity.class);
//
//                    Bundle bundle = new Bundle();
////                    bundle.putSerializable("group_row", groupRow);
//                    bundle.putString("group_name", groupName);
//                    intent.putExtras(bundle);
//
//                    context.startActivity(intent);
                }
            });
        }

    }


}
