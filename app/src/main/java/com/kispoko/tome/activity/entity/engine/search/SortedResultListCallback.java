
package com.kispoko.tome.activity.entity.engine.search;


import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.kispoko.tome.rts.entity.engine.search.EngineActiveSearchResult;


/**
 * Sorted Result List Callback
 */
public class SortedResultListCallback extends SortedList.Callback<EngineActiveSearchResult>
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private RecyclerView.Adapter adapter;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public SortedResultListCallback(RecyclerView.Adapter adapter)
    {
        this.adapter = adapter;
    }


    // EXTENDED API
    // -----------------------------------------------------------------------------------------

    @Override
    public void onInserted(int position, int count)
    {
        this.adapter.notifyItemRangeInserted(position, count);
        Log.d("***CALLBACK", "on inserted");
    }


    @Override
    public void onRemoved(int position, int count)
    {
        this.adapter.notifyItemRangeRemoved(position, count);
    }

    @Override
    public void onMoved(int fromPosition, int toPosition)
    {
        this.adapter.notifyItemMoved(fromPosition, toPosition);
    }


    @Override
    public void onChanged(int position, int count)
    {
        this.adapter.notifyItemRangeChanged(position, count);
    }


    @Override
    public int compare(EngineActiveSearchResult result1, EngineActiveSearchResult result2)
    {
        // Order by ranking, high to low
        if (result1.ranking() > result2.ranking())
            return 1;
        else if (result1.ranking() < result2.ranking())
            return -1;
        else
            return 0;
    }


    @Override
    public boolean areContentsTheSame(EngineActiveSearchResult oldResult,
                                      EngineActiveSearchResult newResult)
    {
        return oldResult.equals(newResult);
    }


    @Override
    public boolean areItemsTheSame(EngineActiveSearchResult result1,
                                   EngineActiveSearchResult result2)
    {
        if (result1.type() == EngineActiveSearchResult.Type.VARIABLE &&
            result2.type() == EngineActiveSearchResult.Type.VARIABLE)
        {
            if (result1.variableSearchResult().name() ==
                result2.variableSearchResult().name()) {
                return true;
            }
        }
        else if (result1.type() == EngineActiveSearchResult.Type.MECHANIC &&
                result2.type() == EngineActiveSearchResult.Type.MECHANIC)
        {
            if (result1.mechanicSearchResult().name() ==
                result2.mechanicSearchResult().name()) {
                return true;
            }
        }

        return false;
    }

}
