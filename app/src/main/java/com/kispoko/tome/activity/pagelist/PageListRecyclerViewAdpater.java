
package com.kispoko.tome.activity.pagelist;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.PageActivity;
import com.kispoko.tome.sheet.Page;

import java.util.List;



/**
 * Page List Recycler View Adapter
 */
public class PageListRecyclerViewAdpater
       extends RecyclerView.Adapter<PageListRecyclerViewAdpater.ViewHolder>
{


    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private List<Page>  pageList;

    private Context     context;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public PageListRecyclerViewAdpater(List<Page> pageList, Context context)
    {
        this.pageList   = pageList;
        this.context    = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = PageListItemView.view(parent.getContext());
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(PageListRecyclerViewAdpater.ViewHolder viewHolder,
                                 int position)
    {
        Page page = this.pageList.get(position);

        // > Name
        viewHolder.setName(page.name());

        // > Group Count
        viewHolder.setGroupCount(page.groups().size());

        // > On Click Listener
        viewHolder.setOnClick(page, this.context);
    }


    // The number of value sets to display
    @Override
    public int getItemCount()
    {
        return this.pageList.size();
    }


    /**
     * The View Holder caches a view for each item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private RelativeLayout  layoutView;

        private TextView        nameView;
        private TextView        groupCountView;
        private TextView        groupLabelView;


        public ViewHolder(final View itemView)
        {
            super(itemView);

            this.layoutView = (RelativeLayout) itemView.findViewById(R.id.page_list_item_layout);

            this.nameView = (TextView) itemView.findViewById(R.id.page_list_item_name);
            this.groupCountView = (TextView) itemView.findViewById(R.id.page_list_item_group_count);
            this.groupLabelView = (TextView) itemView.findViewById(R.id.page_list_item_group_label);
        }


        public void setName(String name)
        {
            this.nameView.setText(name);
        }


        public void setGroupCount(Integer count)
        {
            this.groupCountView.setText(count.toString());

            if (count == 1)
                this.groupLabelView.setText(R.string.group_upper);
            else
                this.groupLabelView.setText(R.string.groups_upper);
        }


        public void setOnClick(final Page page, final Context context)
        {
            this.layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, PageActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("page", page);
                    intent.putExtras(bundle);

                    context.startActivity(intent);
                }
            });
        }

    }


}
