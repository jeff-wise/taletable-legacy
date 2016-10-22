
package com.kispoko.tome.sheet.component.text;


import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kispoko.tome.activity.EditResult;
import com.kispoko.tome.R;
import com.kispoko.tome.sheet.component.Text;
import com.kispoko.tome.type.ListType;


/**
 * Recycler View Adapter for ListType Types
 */
public class TextEditRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    // The items to display in your RecyclerView
    private Text text;
    private ListType listType;

    private final int HEADER = 0, ITEM = 1;


    // > CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    // Provide a suitable constructor (depends on the kind of dataset)
    public TextEditRecyclerViewAdapter(Text text, ListType listType)
    {
        this.text = text;
        this.listType = listType;
    }


    // > API
    // -------------------------------------------------------------------------------------------

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return this.listType.size();
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
                                  this.text.getTypeEditorHeaderView(viewGroup.getContext()));
                break;
            case ITEM:
                viewHolder = new ItemViewHolder(this.listType.getItemView(viewGroup.getContext()),
                                                this.listType, this.text);
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
        textView.setText(this.listType.getValue(position));

        if (this.listType.getValue(position).equals(this.text.getValue())) {
            ImageView iconView = (ImageView) itemView.findViewById(R.id.type_list_item_icon);
            iconView.setImageDrawable(
                ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_item_selected_24dp));


            int selectedColor = ContextCompat.getColor(itemView.getContext(), R.color.bluegrey_800);
            iconView.setColorFilter(selectedColor);
            textView.setTextColor(selectedColor);

        }
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
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder
    {

        private View itemView;

        public ItemViewHolder(final View itemView, final ListType listType, final Text text)
        {
            super(itemView);
            this.itemView = itemView;


            // On Click Listener
            final Activity activity = (Activity) itemView.getContext();
            final RecyclerView.ViewHolder thisViewHolder = this;
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newValue = listType.getValue(thisViewHolder.getAdapterPosition());

                    // Set chosen value as result of activity and finish
                    EditResult editResult = new EditResult(EditResult.ResultType.TEXT_VALUE,
                                                           text.getId(), newValue);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("RESULT", editResult);
                    activity.setResult(Activity.RESULT_OK, resultIntent);
                    activity.finish();
                }
            });

        }

        public View getItemView()
        {
            return this.itemView;
        }
    }
}

