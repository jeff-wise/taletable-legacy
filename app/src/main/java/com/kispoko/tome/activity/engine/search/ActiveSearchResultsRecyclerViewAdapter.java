
package com.kispoko.tome.activity.engine.search;


import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.mechanic.ActiveMechanicSearchResult;
import com.kispoko.tome.engine.search.EngineActiveSearchResult;
import com.kispoko.tome.engine.variable.ActiveVariableSearchResult;

import java.util.Set;

import static android.R.attr.type;


/**
 * Active Search Results Recycler View Adapter
 */
public class ActiveSearchResultsRecyclerViewAdapter
                    extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private final int VARIABLE = 0;
    private final int MECHANIC = 1;

    private SortedList<EngineActiveSearchResult> resultSortedList;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public ActiveSearchResultsRecyclerViewAdapter()
    {
        this.resultSortedList = new SortedList<>(EngineActiveSearchResult.class,
                                                 new SortedResultListCallback(this));
    }


    // RECYCLER VIEW API
    // -----------------------------------------------------------------------------------------

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case VARIABLE:
                View variableResultView = ActiveResultView.variable(parent.getContext());
                return new VariableViewHolder(variableResultView);
            case MECHANIC:
                View mechanicResultView = ActiveResultView.mechanic(parent.getContext());
                return new VariableViewHolder(mechanicResultView);
            default:
                return null;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        EngineActiveSearchResult result = this.resultSortedList.get(position);

        Log.d("***ADAPTER", "on bind view holder");

        switch (result.type())
        {
            case VARIABLE:
                VariableViewHolder variableViewHolder = (VariableViewHolder) viewHolder;
                ActiveVariableSearchResult variableResult = result.variableSearchResult();
                variableViewHolder.setType(R.string.variable);
                variableViewHolder.setName(variableResult.variableName());
                variableViewHolder.setLabel(variableResult.variableLabel());
                Log.d("***ADAPTER", "variable name " + variableResult.variableName());
                break;
            case MECHANIC:
                MechanicViewHolder mechanicViewHolder = (MechanicViewHolder) viewHolder;
                ActiveMechanicSearchResult mechanicResult = result.mechanicSearchResult();
                mechanicViewHolder.setType(R.string.mechanic);
                mechanicViewHolder.setName(mechanicResult.mechanicName());
                mechanicViewHolder.setLabel(mechanicResult.mechanicLabel());
                Log.d("***ADAPTER", "mechanic name " + mechanicResult.mechanicName());
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

        switch (result.type())
        {
            case VARIABLE:
                return VARIABLE;
            case MECHANIC:
                return MECHANIC;
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
    public void updateSearchResults(Set<EngineActiveSearchResult> newResults)
    {
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

        Log.d("***ADAPTER", "results " + Integer.toString(this.resultSortedList.size()));
    }


    // VARIABLE VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    /**
     * The View Holder caches a view for each item.
     */
    public class VariableViewHolder extends RecyclerView.ViewHolder
    {

        private LinearLayout layout;
        private TextView     typeView;
        private TextView     nameView;
        private TextView     labelView;

        public VariableViewHolder(final View itemView)
        {
            super(itemView);

            this.layout = (LinearLayout) itemView.findViewById(R.id.search_result_layout);

            this.typeView = (TextView) itemView.findViewById(R.id.search_result_type);

            this.nameView = (TextView) itemView.findViewById(R.id.search_result_variable_name);
            this.labelView = (TextView) itemView.findViewById(R.id.search_result_variable_label);
        }

        public void setType(int typeStringId)
        {
            this.typeView.setText(typeStringId);
        }

        public void setName(String name)
        {
            this.nameView.setText(name);
        }

        public void setLabel(String label)
        {
            this.labelView.setText(label);
        }

    }


    // MECHANIC VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    /**
     * The View Holder caches a view for each item.
     */
    public class MechanicViewHolder extends RecyclerView.ViewHolder
    {

        private LinearLayout layout;

        private TextView     typeView;

        private TextView     nameView;
        private TextView     labelView;

        public MechanicViewHolder(final View itemView)
        {
            super(itemView);

            this.layout = (LinearLayout) itemView.findViewById(R.id.search_result_layout);

            this.typeView = (TextView) itemView.findViewById(R.id.search_result_type);

            this.nameView = (TextView) itemView.findViewById(R.id.search_result_mechanic_name);
            this.labelView = (TextView) itemView.findViewById(R.id.search_result_mechanic_label);
        }

        public void setType(int typeStringId)
        {
            this.typeView.setText(typeStringId);
        }

        public void setName(String name)
        {
            this.nameView.setText(name);
        }

        public void setLabel(String label)
        {
            this.labelView.setText(label);
        }

    }
}
