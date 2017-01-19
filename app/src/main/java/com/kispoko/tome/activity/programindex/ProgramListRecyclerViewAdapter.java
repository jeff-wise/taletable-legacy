
package com.kispoko.tome.activity.programindex;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.ProgramActivity;
import com.kispoko.tome.engine.programming.program.Program;

import java.util.List;



/**
 * Program List Recycler View Adpater
 */
public class ProgramListRecyclerViewAdapter
       extends RecyclerView.Adapter<ProgramListRecyclerViewAdapter.ViewHolder>
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private List<Program>   programList;

    private Context         context;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public ProgramListRecyclerViewAdapter(List<Program> programList, Context context)
    {
        this.programList    = programList;
        this.context        = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = ProgramListItemView.view(parent.getContext());
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ProgramListRecyclerViewAdapter.ViewHolder viewHolder,
                                 int position)
    {
        Program program = this.programList.get(position);

        // > Label
        viewHolder.setHeaderText(program.label());

        // > Description
        viewHolder.setDescriptionText(program.description());

        // > Parameter Types
        int arity = program.arity();

        if (arity >= 1)
            viewHolder.setParameterType1(program.parameterTypes().get(0).toString().toUpperCase());

        if (arity >= 2)
            viewHolder.setParameterType2(program.parameterTypes().get(1).toString().toUpperCase());

        if (arity >= 3)
            viewHolder.setParameterType3(program.parameterTypes().get(2).toString().toUpperCase());

        // > Result Type
        viewHolder.setResultType(program.resultType().toString().toUpperCase());

        // > On Click Listener
        viewHolder.setOnClick(program.name(), this.context);
    }


    // The number of value sets to display
    @Override
    public int getItemCount()
    {
        return this.programList.size();
    }


    /**
     * The View Holder caches a view for each item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private LinearLayout    layoutView;
        private TextView        headerView;
        private TextView        descriptionView;

        private TextView        parameterType1View;
        private TextView        parameterType2View;
        private TextView        parameterType3View;

        private TextView        resultTypeView;


        public ViewHolder(final View itemView)
        {
            super(itemView);

            this.layoutView  = (LinearLayout) itemView.findViewById(R.id.program_list_item_layout);
            this.headerView     = (TextView) itemView.findViewById(R.id.program_list_item_header);
            this.descriptionView =
                    (TextView) itemView.findViewById(R.id.program_list_item_description);

            this.parameterType1View =
                    (TextView) itemView.findViewById(R.id.program_list_item_parameter_type_1);

            this.parameterType2View =
                    (TextView) itemView.findViewById(R.id.program_list_item_parameter_type_2);

            this.parameterType3View =
                    (TextView) itemView.findViewById(R.id.program_list_item_parameter_type_3);

            this.resultTypeView =
                    (TextView) itemView.findViewById(R.id.program_list_item_result_type);
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


        public void setOnClick(final String programName, final Context context)
        {
            this.layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, ProgramActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("program_name", programName);
                    intent.putExtras(bundle);

                    context.startActivity(intent);
                }
            });
        }

    }

}
