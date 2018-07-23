
package com.taletable.android.util.promise;


import android.os.AsyncTask;

import com.taletable.android.lib.orm.ProdType;

import java.util.List;



/**
 * Promise: Load Collection Value
 */
public class CollectionValuePromise<A extends ProdType>
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private AsyncTask<Void,Void,List<A>> asyncTask;
    private OnReady<A>             onReady;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public CollectionValuePromise(final Action<A> action)
    {
        this.asyncTask = new AsyncTask<Void,Void,List<A>>()
        {

            @Override
            protected List<A> doInBackground(Void... args)
            {
                return action.run();
            }

            @Override
            protected void onPostExecute(List<A> result)
            {
                onReady.run(result);
            }
        };
    }


    // API
    // --------------------------------------------------------------------------------------

    public void run(OnReady<A> onReady)
    {
        this.onReady            = onReady;
        this.asyncTask.execute();
    }


    public abstract static class Action<A>
    {
        abstract public List<A> run();
    }


    public abstract static class OnReady<A>
    {
        abstract public void run(List<A> result);
    }
}
