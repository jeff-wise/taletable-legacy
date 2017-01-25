
package com.kispoko.tome.activity.program;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.StatementActivity;
import com.kispoko.tome.engine.program.statement.Parameter;
import com.kispoko.tome.engine.program.statement.Statement;

import java.util.List;



/**
 * Statement List Recycler View Adapter
 */
public class StatementListRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private final int VARIABLE_STATEMENT = 0;
    private final int RESULT_STATEMENT = 1;

    private List<Statement> statements;

    private Context         context;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public StatementListRecyclerViewAdapter(List<Statement> statements, Context context)
    {
        this.statements = statements;
        this.context    = context;
    }


    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------------

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType)
        {
            case VARIABLE_STATEMENT:
                View normalStatementView =
                        StatementListItemView.normalStatementView(parent.getContext());
                viewHolder = new VariableStatementViewHolder(normalStatementView);
                break;
            case RESULT_STATEMENT:
                View resultStatementView =
                        StatementListItemView.resultStatementView(parent.getContext());
                viewHolder = new StatementViewHolder(resultStatementView);
                break;
        }

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        Statement statement = this.statements.get(position);

        switch (viewHolder.getItemViewType())
        {
            case VARIABLE_STATEMENT:
                this.configureStatementViewHolder(viewHolder, statement);
                this.configureVariableStatementViewHolder(viewHolder, statement);
                break;
            case RESULT_STATEMENT:
                this.configureStatementViewHolder(viewHolder, statement);
                break;
        }
    }


    @Override
    public int getItemCount()
    {
        return this.statements.size();
    }


    @Override
    public int getItemViewType(int position)
    {
        if (position == (this.statements.size() -1))
            return RESULT_STATEMENT;
        else
            return VARIABLE_STATEMENT;
    }


    // CONFIGURE VIEW HOLDERS
    // -------------------------------------------------------------------------------------------

    private void configureStatementViewHolder(RecyclerView.ViewHolder viewHolder,
                                              Statement statement)
    {
        StatementViewHolder statementViewHolder = (StatementViewHolder) viewHolder;

        statementViewHolder.setFunctionName(statement.functionName());

        statementViewHolder.setOnClick(statement);

        int arity = statement.arity();

        if (arity >= 1) {
            Parameter parameter1 = statement.parameters().get(0);
            statementViewHolder.setParameter1Value(parameter1.valueString());
            statementViewHolder.setParameter1Type(parameter1.typeString());
        }

        if (arity >= 2) {
            Parameter parameter2 = statement.parameters().get(1);
            statementViewHolder.setParameter2Value(parameter2.valueString());
            statementViewHolder.setParameter2Type(parameter2.typeString());
        }

        if (arity >= 3) {
            Parameter parameter3 = statement.parameters().get(2);
            statementViewHolder.setParameter3Value(parameter3.valueString());
            statementViewHolder.setParameter3Type(parameter3.typeString());
        }
    }


    private void configureVariableStatementViewHolder(RecyclerView.ViewHolder viewHolder,
                                                      Statement statement)
    {
        VariableStatementViewHolder variableStatementViewHolder =
                                                        (VariableStatementViewHolder) viewHolder;
        variableStatementViewHolder.setVariableName(statement.variableName());
    }


    // VIEW HOLDERS
    // -------------------------------------------------------------------------------------------

    /**
     * The View Holder for non-result program statements.
     */
    public class StatementViewHolder extends RecyclerView.ViewHolder
    {

        private LinearLayout layout;
        private TextView     functionView;

        private LinearLayout parameter1Layout;
        private TextView     parameter1ValueView;
        private TextView     parameter1TypeView;

        private LinearLayout parameter2Layout;
        private TextView     parameter2ValueView;
        private TextView     parameter2TypeView;

        private LinearLayout parameter3Layout;
        private TextView     parameter3ValueView;
        private TextView     parameter3TypeView;


        public StatementViewHolder(final View itemView)
        {
            super(itemView);

            this.layout     = (LinearLayout) itemView.findViewById(R.id.statement_list_item_layout);

            this.functionView = (TextView) itemView.findViewById(R.id.statement_list_item_function);

            this.parameter1Layout =
                  (LinearLayout) itemView.findViewById(R.id.statement_list_item_parameter1_layout);
            this.parameter1ValueView =
                    (TextView) itemView.findViewById(R.id.statement_list_item_parameter1_value);
            this.parameter1TypeView =
                    (TextView) itemView.findViewById(R.id.statement_list_item_parameter1_type);

            this.parameter2Layout =
                   (LinearLayout) itemView.findViewById(R.id.statement_list_item_parameter2_layout);
            this.parameter2ValueView =
                    (TextView) itemView.findViewById(R.id.statement_list_item_parameter2_value);
            this.parameter2TypeView =
                    (TextView) itemView.findViewById(R.id.statement_list_item_parameter2_type);

            this.parameter3Layout =
                  (LinearLayout) itemView.findViewById(R.id.statement_list_item_parameter3_layout);
            this.parameter3ValueView =
                    (TextView) itemView.findViewById(R.id.statement_list_item_parameter3_value);
            this.parameter3TypeView =
                    (TextView) itemView.findViewById(R.id.statement_list_item_parameter3_type);
        }


        public void setFunctionName(String functionName)
        {
            this.functionView.setText(functionName);
        }


        public void setParameter1Value(String value)
        {
            this.parameter1Layout.setVisibility(View.VISIBLE);
            this.parameter1ValueView.setText(value);
        }


        public void setParameter1Type(String type)
        {
            this.parameter1Layout.setVisibility(View.VISIBLE);
            this.parameter1TypeView.setText(type);
        }


        public void setParameter2Value(String value)
        {
            this.parameter2Layout.setVisibility(View.VISIBLE);
            this.parameter2ValueView.setText(value);
        }


        public void setParameter2Type(String type)
        {
            this.parameter2Layout.setVisibility(View.VISIBLE);
            this.parameter2TypeView.setText(type);
        }


        public void setParameter3Value(String value)
        {
            this.parameter3Layout.setVisibility(View.VISIBLE);
            this.parameter3ValueView.setText(value);
        }


        public void setParameter3Type(String type)
        {
            this.parameter3Layout.setVisibility(View.VISIBLE);
            this.parameter3TypeView.setText(type);
        }


        public void setOnClick(final Statement statement)
        {
            this.layout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, StatementActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("statement", statement);
                    intent.putExtras(bundle);

                    context.startActivity(intent);
                }
            });
        }


    }


    public class VariableStatementViewHolder extends StatementViewHolder
    {

        private TextView variableView;


        public VariableStatementViewHolder(final View itemView)
        {
            super(itemView);

            this.variableView = (TextView) itemView.findViewById(R.id.statement_list_item_variable);
        }


        public void setVariableName(String variableName)
        {
            this.variableView.setText(variableName);
        }

    }


}
