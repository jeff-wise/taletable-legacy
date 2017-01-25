
package com.kispoko.tome.activity.mechanicindex;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.MechanicActivity;
import com.kispoko.tome.engine.mechanic.Mechanic;

import java.util.List;



/**
 * Mechanic List Recycler View Adapter
 */
public class MechanicListRecyclerViewAdapter
        extends RecyclerView.Adapter<MechanicListRecyclerViewAdapter.ViewHolder>
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private List<Mechanic>  mechanicList;

    private Context         context;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public MechanicListRecyclerViewAdapter(List<Mechanic> mechanicList, Context context)
    {
        this.mechanicList   = mechanicList;
        this.context        = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = MechanicListItemView.view(parent.getContext());
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MechanicListRecyclerViewAdapter.ViewHolder viewHolder,
                                 int position)
    {
        Mechanic mechanic = this.mechanicList.get(position);

        // > Name
        viewHolder.setName(mechanic.label());

        // > Variables
        viewHolder.setVariables(mechanic.variableCount());

        // > On Click Listener
        viewHolder.setOnClick(mechanic.name(), this.context);
    }


    // The number of value sets to display
    @Override
    public int getItemCount()
    {
        return this.mechanicList.size();
    }


    /**
     * The View Holder caches a view for each item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private RelativeLayout  layoutView;
        private TextView        nameView;
        private TextView        variablesCountView;
        private TextView        variablesLabelView;


        public ViewHolder(final View itemView)
        {
            super(itemView);

            this.layoutView =
                    (RelativeLayout) itemView.findViewById(R.id.mechanic_list_item_layout);

            this.nameView =
                    (TextView) itemView.findViewById(R.id.mechanic_list_item_name);

            this.variablesCountView =
                    (TextView) itemView.findViewById(R.id.mechanic_list_item_variables_count);

            this.variablesLabelView =
                    (TextView) itemView.findViewById(R.id.mechanic_list_item_variables_label);
        }


        public void setName(String name)
        {
            this.nameView.setText(name);
        }


        public void setVariables(Integer variableCount)
        {
            this.variablesCountView.setText(variableCount.toString());

            if (variableCount == 1)
                this.variablesLabelView.setText(R.string.mechanic_list_item_variable);
            else
                this.variablesLabelView.setText(R.string.mechanic_list_item_variables);
        }


        public void setOnClick(final String mechanicName, final Context context)
        {
            this.layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, MechanicActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("mechanic_name", mechanicName);
                    intent.putExtras(bundle);

                    context.startActivity(intent);
                }
            });
        }

    }


}
