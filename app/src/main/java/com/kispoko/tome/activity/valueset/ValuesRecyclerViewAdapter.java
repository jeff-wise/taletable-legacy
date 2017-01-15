
package com.kispoko.tome.activity.valueset;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.value.ValueUnion;

import java.util.List;



/**
 * ValueSet RecyclerView Adapter
 */
public class ValuesRecyclerViewAdapter
       extends RecyclerView.Adapter<ValuesRecyclerViewAdapter.ViewHolder>
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private List<ValueUnion> values;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public ValuesRecyclerViewAdapter(List<ValueUnion> values)
    {
        this.values  = values;
    }


    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------------

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = ValueListItemView.view(parent.getContext());
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ValuesRecyclerViewAdapter.ViewHolder viewHolder, int position)
    {
        ValueUnion valueUnion = this.values.get(position);

        switch (valueUnion.type())
        {
            case TEXT:
                viewHolder.setValueText(valueUnion.textValue().value());
                break;
            case NUMBER:
                viewHolder.setValueText(valueUnion.numberValue().value().toString());
                break;
        }
    }


    @Override
    public int getItemCount()
    {
        return this.values.size();
    }


    // VIEW HOLDER
    // -------------------------------------------------------------------------------------------

    /**
     * The View Holder caches a view for each item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private TextView valueView;


        public ViewHolder(final View itemView)
        {
            super(itemView);

            this.valueView = (TextView) itemView.findViewById(R.id.value_list_item_value);
        }


        public void setValueText(String valueText)
        {
            this.valueView.setText(valueText);
        }

    }

}
