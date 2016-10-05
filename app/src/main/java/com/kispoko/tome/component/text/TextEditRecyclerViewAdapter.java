
package com.kispoko.tome.component.text;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.component.Text;
import com.kispoko.tome.type.List;

import static android.R.id.list;


/**
 * Recycler View Adapter for List Types
 */
public class TextEditRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    // The items to display in your RecyclerView
    private Text text;
    private List list;

    private final int HEADER = 0, ITEM = 1;


    // > CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    // Provide a suitable constructor (depends on the kind of dataset)
    public TextEditRecyclerViewAdapter(Text text, List list)
    {
        this.text = text;
        this.list = list;
    }


    // > API
    // -------------------------------------------------------------------------------------------

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return this.list.size();
    }


    @Override
    public int getItemViewType(int position)
    {
        if (position == 0)
            return HEADER;
        else
            return ITEM;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType)
        {
            case HEADER:
                viewHolder = new HeaderViewHolder(
                                  this.text.getEditorHeaderView(viewGroup.getContext()));
                break;
            case ITEM:
                viewHolder = new ItemViewHolder(this.list.getItemView(viewGroup.getContext()));
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        switch (viewHolder.getItemViewType())
        {
            case HEADER:
                break;
            case ITEM:
                ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
                configureItemView(itemViewHolder.getItemView(), position);
                break;
        }
    }


    // > INTERNAL
    // ------------------------------------------------------------------------------------------


    private void configureItemView(View itemView, int position)
    {
        TextView textView = (TextView) itemView.findViewById(R.id.type_list_item_name);
        textView.setText(this.list.getValue(position));
    }



    // >> Nested Classes
    // ------------------------------------------------------------------------------------------


    public class HeaderViewHolder extends RecyclerView.ViewHolder
    {

        private View headerView;

        public HeaderViewHolder(View headerView)
        {
            super(headerView);
            this.headerView = headerView;
        }

        public View getHeaderView()
        {
            return this.headerView;
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder
    {

        private View itemView;

        public ItemViewHolder(View itemView)
        {
            super(itemView);
            this.itemView = itemView;
        }

        public View getItemView()
        {
            return this.itemView;
        }
    }
}
