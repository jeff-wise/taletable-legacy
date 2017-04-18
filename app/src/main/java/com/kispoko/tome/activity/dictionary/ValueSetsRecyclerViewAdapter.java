
package com.kispoko.tome.activity.dictionary;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.BaseValueSetEditorActivity;
import com.kispoko.tome.engine.value.ValueSet;
import com.kispoko.tome.engine.value.ValueSetUnion;

import java.util.List;



/**
 * Dictionary Recycler View Adapter
 */
public class ValueSetsRecyclerViewAdapter
       extends RecyclerView.Adapter<ValueSetsRecyclerViewAdapter.ViewHolder>
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private List<ValueSetUnion> valueSetList;

    private Context             context;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public ValueSetsRecyclerViewAdapter(List<ValueSetUnion> valueSetList, Context context)
    {
        this.valueSetList   = valueSetList;
        this.context        = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = ValueSetRowView.view(parent.getContext());
        return new ViewHolder(itemView);
    }


    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ValueSetsRecyclerViewAdapter.ViewHolder viewHolder, int position)
    {
        ValueSetUnion valueSetUnion = this.valueSetList.get(position);
        ValueSet valueSet = valueSetUnion.valueSet();

        viewHolder.setHeaderText(valueSet.label());
        viewHolder.setDescriptionText(valueSet.description());
        viewHolder.setItemCountText(Integer.toString(valueSet.size()));
        viewHolder.setOnClick(valueSet.name(), this.context);
    }


    // The number of value sets to display
    @Override
    public int getItemCount()
    {
        return this.valueSetList.size();
    }


    /**
     * The View Holder caches a view for each item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private LinearLayout layoutView;
        private TextView     headerView;
        private TextView     descriptionView;
        private TextView     itemCountView;


        public ViewHolder(final View itemView)
        {
            super(itemView);

            this.layoutView      = (LinearLayout) itemView.findViewById(R.id.value_set_row_layout);
            this.headerView      = (TextView) itemView.findViewById(R.id.value_set_row_header);
            this.descriptionView = (TextView) itemView.findViewById(R.id.value_set_row_description);
            this.itemCountView   = (TextView) itemView.findViewById(R.id.value_set_row_items);
        }


        public void setHeaderText(String headerText)
        {
            this.headerView.setText(headerText);
        }


        public void setDescriptionText(String descriptionText)
        {
            this.descriptionView.setText(descriptionText);
        }


        public void setItemCountText(String itemCountText)
        {
            this.itemCountView.setText(itemCountText);
        }


        public void setOnClick(final String valueSetName, final Context context)
        {
            this.layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, BaseValueSetEditorActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("value_set_name", valueSetName);
                    intent.putExtras(bundle);

                    context.startActivity(intent);
                }
            });
        }

    }

}
