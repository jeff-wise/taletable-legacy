
package com.kispoko.tome.util;


import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;



/**
 * ValuePromise
 */
public class AsyncFunction<A>
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private AsyncTask<Void,Void,A> asyncTask;
    private OnReady<A>             onReady;

    private Map<String,Object>     parameters;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public AsyncFunction(final Action<A> action)
    {
        parameters = new HashMap<>();

        this.asyncTask = new AsyncTask<Void,Void,A>()
        {

            @Override
            protected A doInBackground(Void... args)
            {
                return action.run(parameters);
            }

            @Override
            protected void onPostExecute(A result)
            {
                onReady.run(result);
            }
        };
    }


    // API
    // --------------------------------------------------------------------------------------

    public void setParameter(String name, Object value)
    {
        this.parameters.put(name, value);
    }


    public void run(OnReady<A> onReady)
    {
        this.onReady = onReady;
        this.asyncTask.execute();
    }


    public abstract static class Action<A>
    {
        abstract public A run(Map<String,Object> parameters);
    }


    public abstract static class OnReady<A>
    {
        abstract public void run(A result);
    }
}
