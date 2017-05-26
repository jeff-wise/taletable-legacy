
package com.kispoko.tome.activity.engine.search;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.model.engine.mechanic.ActiveMechanicSearchResult;
import com.kispoko.tome.engine.search.EngineActiveSearchResult;
import com.kispoko.tome.engine.ActiveVariableSearchResult;
import com.kispoko.tome.lib.ui.FormattedString;

import java.util.Set;



/**
 * Active Search Results Recycler View Adapter
 */
public class ActiveSearchResultsRecyclerViewAdapter
                    extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private final int VARIABLE_VIEW = 0;
    private final int MECHANIC_VIEW = 1;


    private String query;
    private SortedList<EngineActiveSearchResult> resultSortedList;

    private Context context;

    private final int HL_COLOR_RES_ID;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ActiveSearchResultsRecyclerViewAdapter(Context context)
    {
        this.query = "";
        this.resultSortedList = new SortedList<>(EngineActiveSearchResult.class,
                                                 new SortedResultListCallback(this));
        this.context = context;

        this.HL_COLOR_RES_ID = R.color.purple_light;
    }


    // RECYCLER VIEW API
    // -----------------------------------------------------------------------------------------

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case VARIABLE_VIEW:
                View variableResultView = ActiveResultView.variable(parent.getContext());
                return new VariableViewHolder(variableResultView);
            case MECHANIC_VIEW:
                View mechanicResultView = ActiveResultView.mechanic(parent.getContext());
                return new MechanicViewHolder(mechanicResultView);
            default:
                return null;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        EngineActiveSearchResult result = this.resultSortedList.get(position);

        Log.d("***ADAPTER", "on bind view position: " + Integer.toString(position) + " " +
                     result.type().toString());

        switch (result.type())
        {
            case VARIABLE:

                VariableViewHolder variableViewHolder = (VariableViewHolder) viewHolder;
                ActiveVariableSearchResult variableResult = result.variableSearchResult();

                String variableString = this.context.getString(R.string.variable).toUpperCase();
                variableViewHolder.setType(variableString);

                if (variableResult.nameIsMatched())
                    variableViewHolder.setName(variableResult.name(), true);
                else
                    variableViewHolder.setName(variableResult.name(), false);

                if (variableResult.labelIsMatched())
                    variableViewHolder.setLabel(variableResult.label(), true);
                else
                    variableViewHolder.setLabel(variableResult.label(), false);

                break;

            case MECHANIC:

                MechanicViewHolder mechanicViewHolder = (MechanicViewHolder) viewHolder;
                ActiveMechanicSearchResult mechanicResult = result.mechanicSearchResult();

                String mechanicString = this.context.getString(R.string.mechanic).toUpperCase();
                mechanicViewHolder.setType(mechanicString);

                if (mechanicResult.nameIsMatch())
                    mechanicViewHolder.setName(mechanicResult.name(), true);
                else
                    mechanicViewHolder.setName(mechanicResult.name(), false);

                if (mechanicResult.labelIsMatch())
                    mechanicViewHolder.setLabel(mechanicResult.label(), true);
                else
                    mechanicViewHolder.setLabel(mechanicResult.label(), false);

                if (mechanicResult.variablesIsMatch())
                    mechanicViewHolder.setVariables(mechanicResult.variables(), true);
                else
                    mechanicViewHolder.setVariables(mechanicResult.variables(), false);

                break;
        }
    }


    @Override
    public int getItemCount()
    {
        return this.resultSortedList.size();
    }


    @Override
    public int getItemViewType(int position)
    {
        EngineActiveSearchResult result = this.resultSortedList.get(position);

        Log.d("***ADAPTER", "get view type position: " + Integer.toString(position) + " "
                                + result.type().toString());

        switch (result.type())
        {
            case VARIABLE:
                return VARIABLE_VIEW;
            case MECHANIC:
                return MECHANIC_VIEW;
            default:
                return -1;
        }
    }


    // API
    // -----------------------------------------------------------------------------------------

    /**
     * Update the collection of search results for the recycler view adapter by updaing the
     * sorted list.
     * @param newResults The new search results.
     */
    public void updateSearchResults(Set<EngineActiveSearchResult> newResults, String query)
    {
        this.query = query;

        this.resultSortedList.beginBatchedUpdates();

        for (int i = this.resultSortedList.size() - 1; i >= 0; i--)
        {
            final EngineActiveSearchResult result = this.resultSortedList.get(i);
            if (!newResults.contains(result)) {
                this.resultSortedList.remove(result);
            }
        }

        this.resultSortedList.addAll(newResults);
        this.resultSortedList.endBatchedUpdates();

        for (int i = 0; i < this.resultSortedList.size(); i++) {
            EngineActiveSearchResult result = this.resultSortedList.get(i);
            Log.d("***ADAPTER", result.type().toString());
        }
    }


    // VARIABLE_VIEW VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    /**
     * The View Holder caches a view for each item.
     */
    public class VariableViewHolder extends RecyclerView.ViewHolder
    {

        // PROPERTIES
        // -------------------------------------------------------------------------------------

        private LinearLayout    layout;

        private TextView        typeView;

        private LinearLayout    labelLayout;

        private TextView        nameView;
        private TextView        labelView;


        // CONSTRUCTORS
        // -------------------------------------------------------------------------------------

        public VariableViewHolder(final View itemView)
        {
            super(itemView);

            this.layout = (LinearLayout) itemView.findViewById(R.id.search_result_layout);

            this.typeView = (TextView) itemView.findViewById(R.id.search_result_type);

            this.labelLayout =
                    (LinearLayout) itemView.findViewById(R.id.search_result_label_layout);

            this.nameView = (TextView) itemView.findViewById(R.id.search_result_variable_name);
            this.labelView = (TextView) itemView.findViewById(R.id.search_result_variable_label);
        }

        // API
        // -------------------------------------------------------------------------------------

        public void setType(String typeString)
        {
            this.typeView.setText(typeString);
        }


        public void setName(String name, boolean highlight)
        {
            if (highlight)
                this.nameView.setText(searchHighlightSpan(name));
            else
                this.nameView.setText(name);
        }


        public void setLabel(String label, boolean highlight)
        {
            if (label != null)
            {
                if (highlight)
                    this.labelView.setText(searchHighlightSpan(label));
                else
                    this.labelView.setText(label);
            }
            else
            {
                this.labelLayout.setVisibility(View.GONE);
            }
        }

    }


    // MECHANIC_VIEW VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    /**
     * The View Holder caches a view for each item.
     */
    public class MechanicViewHolder extends RecyclerView.ViewHolder
    {

        // PROPERTIES
        // -------------------------------------------------------------------------------------

        private LinearLayout layout;

        private TextView     typeView;


        private TextView     nameView;

        private TextView     labelView;
        private LinearLayout labelLayout;

        private TextView     variablesView;
        private LinearLayout variablesLayout;


        // CONSTRUCTORS
        // -------------------------------------------------------------------------------------

        public MechanicViewHolder(final View itemView)
        {
            super(itemView);

            this.layout = (LinearLayout) itemView.findViewById(R.id.search_result_layout);

            this.typeView = (TextView) itemView.findViewById(R.id.search_result_type);

            this.nameView = (TextView) itemView.findViewById(R.id.search_result_mechanic_name);

            this.labelLayout =
                    (LinearLayout) itemView.findViewById(R.id.search_result_label_layout);
            this.labelView = (TextView) itemView.findViewById(R.id.search_result_mechanic_label);

            this.variablesLayout =
                    (LinearLayout) itemView.findViewById(R.id.search_result_variables_layout);
            this.variablesView =
                    (TextView) itemView.findViewById(R.id.search_result_mechanic_variables);
        }


        // API
        // -------------------------------------------------------------------------------------

        public void setType(String typeString)
        {
            this.typeView.setText(typeString);
        }


        public void setName(String name, boolean highlight)
        {
            if (highlight)
                this.nameView.setText(searchHighlightSpan(name));
            else
                this.nameView.setText(name);
        }


        public void setLabel(String label, boolean highlight)
        {
            if (label != null)
            {
                if (highlight)
                    this.labelView.setText(searchHighlightSpan(label));
                else
                    this.labelView.setText(label);
            }
            else
            {
                this.labelLayout.setVisibility(View.GONE);
            }
        }


        public void setVariables(String variables, boolean highlight)
        {
            if (variables != null)
            {
                if (highlight)
                    this.variablesView.setText(searchHighlightSpan(variables));
                else
                    this.variablesView.setText(variables);
            }
            else
            {
                this.variablesLayout.setVisibility(View.GONE);
            }
        }

    }


    // HELPER
    // -----------------------------------------------------------------------------------------

    private SpannableStringBuilder searchHighlightSpan(String text)
    {
        int hlColor = ContextCompat.getColor(this.context, HL_COLOR_RES_ID);
        FormattedString.Span span = new FormattedString.Span(this.query, hlColor);
        return FormattedString.spannableStringBuilder(text, span);
    }
}
