
package com.kispoko.tome.activity.grouprow;


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
import com.kispoko.tome.sheet.widget.Widget;

import java.util.List;

import static android.R.attr.type;
import static com.kispoko.tome.R.string.group;


/**
 * Widget List Recycler View Adapter
 */
public class WidgetListRecyclerViewAdapter
       extends RecyclerView.Adapter<WidgetListRecyclerViewAdapter.ViewHolder>
{


    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private List<Widget> widgetList;

    private Context         context;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public WidgetListRecyclerViewAdapter(List<Widget> widgetList, Context context)
    {
        this.widgetList = widgetList;
        this.context    = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = WidgetListItemView.view(parent.getContext());
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(WidgetListRecyclerViewAdapter.ViewHolder viewHolder,
                                 int position)
    {
        Widget widget = this.widgetList.get(position);

        // > Name
        //viewHolder.setName(group.name());

        // > Rows
        //viewHolder.setRows(group.rows().size());

        // > On Click Listener
        //viewHolder.setOnClick(group, this.context);
    }


    // The number of value sets to display
    @Override
    public int getItemCount()
    {
        return this.widgetList.size();
    }


    /**
     * The View Holder caches a view for each item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private RelativeLayout  layoutView;
        private TextView        nameView;
        private TextView        typeView;


        public ViewHolder(final View itemView)
        {
            super(itemView);

            this.layoutView = (RelativeLayout) itemView.findViewById(R.id.draggable_card_layout);
            this.nameView = (TextView) itemView.findViewById(R.id.draggable_card_title);
            this.typeView = (TextView) itemView.findViewById(R.id.widget_list_item_type);
        }


        public void setName(String name)
        {
            this.nameView.setText(name);
        }


        public void setType(String type)
        {
            this.typeView.setText(type);
        }


        public void setOnClick(final Widget widget, final Context context)
        {
            this.layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, GroupActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("group", group);
                    intent.putExtras(bundle);

                    context.startActivity(intent);
                }
            });
        }

    }



}
