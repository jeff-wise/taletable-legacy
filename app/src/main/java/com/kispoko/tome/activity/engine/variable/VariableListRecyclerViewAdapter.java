
package com.kispoko.tome.activity.engine.variable;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.model.engine.variable.NullVariableException;

import java.util.List;



/**
 * Variable List Recycler View Adapter
 */
public class VariableListRecyclerViewAdapter
        extends RecyclerView.Adapter<VariableListRecyclerViewAdapter.ViewHolder>
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private List<VariableUnion>  variableList;

    private Context              context;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public VariableListRecyclerViewAdapter(List<VariableUnion> variableList, Context context)
    {
        this.variableList   = variableList;
        this.context        = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = VariableListItemView.view(parent.getContext());
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(VariableListRecyclerViewAdapter.ViewHolder viewHolder,
                                 int position)
    {
        VariableUnion variableUnion = this.variableList.get(position);

        // > Name
        viewHolder.setName(variableUnion.variable().label());

        // > Description
        viewHolder.setDescription(variableUnion.variable().description());

        // > Type
        viewHolder.setType(variableUnion.type().toString().toUpperCase());

        // > Kind
        switch (variableUnion.type())
        {
            case TEXT:
                viewHolder.setKind(variableUnion.textVariable().kind().toString());
                break;
            case NUMBER:
                viewHolder.setKind(variableUnion.numberVariable().kind().toString());
                break;
            case BOOLEAN:
                viewHolder.setKind(variableUnion.booleanVariable().kind().toString());
                break;
            case DICE:
                viewHolder.setKind("LITERAL");
                break;
        }

        // > Value
        try {
            viewHolder.setValue(variableUnion.variable().valueString());
        }
        catch (NullVariableException exception) {
            viewHolder.setValue(null);
        }

        // > On Click Listener
        viewHolder.setOnClick(variableUnion, this.context);
    }


    // The number of value sets to display
    @Override
    public int getItemCount()
    {
        return this.variableList.size();
    }


    /**
     * The View Holder caches a view for each item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        // PROPERTIES
        // -------------------------------------------------------------------------------------

        private LinearLayout layoutView;

        private TextView     nameView;
        private TextView     descriptionView;

        private TextView     typeView;
        private TextView     kindView;

        private LinearLayout valueLayout;
        private TextView     valueView;


        // CONSTRUCTORS
        // -------------------------------------------------------------------------------------

        public ViewHolder(final View itemView)
        {
            super(itemView);

            this.layoutView  = (LinearLayout) itemView.findViewById(R.id.variable_list_item_layout);

            this.nameView  = (TextView) itemView.findViewById(R.id.variable_list_item_name);
            this.descriptionView =
                    (TextView) itemView.findViewById(R.id.variable_list_item_description);

            this.typeView  = (TextView) itemView.findViewById(R.id.variable_list_item_type);
            this.kindView  = (TextView) itemView.findViewById(R.id.variable_list_item_kind);

            this.valueLayout =
                    (LinearLayout) itemView.findViewById(R.id.variable_list_item_value_layout);
            this.valueView = (TextView) itemView.findViewById(R.id.variable_list_item_value);
        }


        // API
        // -------------------------------------------------------------------------------------

        public void setName(String name)
        {
            this.nameView.setText(name);
        }


        public void setType(String type)
        {
            this.typeView.setText(type);
        }


        public void setKind(String kind)
        {
            this.kindView.setText(kind);
        }


        public void setDescription(String description)
        {
            this.descriptionView.setText(description);
        }


        public void setValue(String value)
        {
            if (value != null)
                this.valueView.setText(value);
            else
                this.valueLayout.setVisibility(View.GONE);
        }


        public void setOnClick(final VariableUnion variable, final Context context)
        {
            this.layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    switch (variable.type())
                    {
                        case TEXT:
                            Intent textIntent = new Intent(context, TextVariableActivity.class);
                            textIntent.putExtra("text_variable", variable.textVariable());
                            context.startActivity(textIntent);
                            break;
                        case DICE:
                            Intent diceIntent = new Intent(context, DiceVariableActivity.class);
                            diceIntent.putExtra("dice_variable", variable.diceVariable());
                            context.startActivity(diceIntent);
                            break;
                    }
                }
            });
        }

    }


}
