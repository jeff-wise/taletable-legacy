
package com.kispoko.tome.activity.functionindex;


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
import com.kispoko.tome.engine.programming.function.Function;

import java.util.List;



/**
 * Function List RecyclerView Adapter
 */
public class FunctionListRecyclerViewAdapter
       extends RecyclerView.Adapter<FunctionListRecyclerViewAdapter.ViewHolder>
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private List<Function>  functionList;

    private Context         context;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public FunctionListRecyclerViewAdapter(List<Function> functionList, Context context)
    {
        this.functionList   = functionList;
        this.context        = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = FunctionListItemView.view(parent.getContext());
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(FunctionListRecyclerViewAdapter.ViewHolder viewHolder,
                                 int position)
    {
        Function function = this.functionList.get(position);

        // > Header
        viewHolder.setHeaderText(function.label());

        // > Description
        viewHolder.setDescriptionText(function.description());

        // > Parameter Types
        int arity = function.arity();

        if (arity >= 1)
            viewHolder.setParameterType1(function.parameterTypes().get(0).toString().toUpperCase());

        if (arity >= 2)
            viewHolder.setParameterType2(function.parameterTypes().get(1).toString().toUpperCase());

        if (arity >= 3)
            viewHolder.setParameterType3(function.parameterTypes().get(2).toString().toUpperCase());

        // > Result Type
        viewHolder.setResultType(function.resultType().toString().toUpperCase());

        // > On Click Listener
        viewHolder.setOnClick(function.name(), this.context);
    }


    // The number of value sets to display
    @Override
    public int getItemCount()
    {
        return this.functionList.size();
    }


    /**
     * The View Holder caches a view for each item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private LinearLayout layoutView;
        private TextView     headerView;
        private TextView     descriptionView;

        private TextView     parameterType1View;
        private TextView     parameterType2View;
        private TextView     parameterType3View;

        private TextView     resultTypeView;


        public ViewHolder(final View itemView)
        {
            super(itemView);

            this.layoutView  = (LinearLayout) itemView.findViewById(R.id.function_list_item_layout);
            this.headerView     = (TextView) itemView.findViewById(R.id.function_list_item_header);
            this.descriptionView =
                    (TextView) itemView.findViewById(R.id.function_list_item_description);

            this.parameterType1View =
                    (TextView) itemView.findViewById(R.id.function_list_item_parameter_type_1);

            this.parameterType2View =
                    (TextView) itemView.findViewById(R.id.function_list_item_parameter_type_2);

            this.parameterType3View =
                    (TextView) itemView.findViewById(R.id.function_list_item_parameter_type_3);

            this.resultTypeView =
                    (TextView) itemView.findViewById(R.id.function_list_item_result_type);
        }


        public void setHeaderText(String headerText)
        {
            this.headerView.setText(headerText);
        }


        public void setParameterType1(String typeText)
        {
            this.parameterType1View.setText(typeText);
            this.parameterType1View.setVisibility(View.VISIBLE);
        }


        public void setParameterType2(String typeText)
        {
            this.parameterType2View.setText(typeText);
            this.parameterType2View.setVisibility(View.VISIBLE);
        }


        public void setParameterType3(String typeText)
        {
            this.parameterType3View.setText(typeText);
            this.parameterType3View.setVisibility(View.VISIBLE);
        }


        public void setResultType(String typeText)
        {
            this.resultTypeView.setText(typeText);
        }

        public void setDescriptionText(String descriptionText)
        {
            this.descriptionView.setText(descriptionText);
        }


        public void setOnClick(final String functionName, final Context context)
        {
            this.layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, FunctionActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("function_name", functionName);
                    intent.putExtras(bundle);

                    context.startActivity(intent);
                }
            });
        }

    }



}