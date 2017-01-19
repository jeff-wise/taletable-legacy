
package com.kispoko.tome.activity.variable;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.FunctionActivity;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.engine.variable.VariableUnion;

import java.util.List;

import static android.R.attr.name;


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
        VariableUnion variable = this.variableList.get(position);

        String kind = "";

        switch (variable.type())
        {
            case TEXT:
                kind = variable.textVariable().kind().toString();
                break;
            case NUMBER:
                kind = variable.numberVariable().kind().toString();
                break;
            case BOOLEAN:
                kind = variable.booleanVariable().kind().toString();
                break;
            case DICE:
                kind = "Literal";
                break;
        }

        // > Name
        viewHolder.setName(variable.variable().label());

        // > Type
        viewHolder.setType(variable.type().toString().toUpperCase());

        // > Kind
        viewHolder.setKind(kind.toUpperCase());

        // > On Click Listener
        viewHolder.setOnClick(variable, this.context);
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

        private LinearLayout layoutView;

        private TextView     nameView;
        private TextView     typeView;
        private TextView     kindView;


        public ViewHolder(final View itemView)
        {
            super(itemView);

            this.layoutView  = (LinearLayout) itemView.findViewById(R.id.variable_list_item_layout);
            this.nameView  = (TextView) itemView.findViewById(R.id.variable_list_item_name);
            this.typeView  = (TextView) itemView.findViewById(R.id.variable_list_item_type);
            this.kindView  = (TextView) itemView.findViewById(R.id.variable_list_item_kind);
        }


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


        public void setOnClick(final VariableUnion variable, final Context context)
        {
            this.layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, FunctionActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("variable", variable);
                    intent.putExtras(bundle);

                    context.startActivity(intent);
                }
            });
        }

    }


}
