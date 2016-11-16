
package com.kispoko.tome.util.promise;


import android.os.AsyncTask;



/**
 * Prommise
 */
public class Promise<A>
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private AsyncTask<Void,Void,A> asyncTask;
    private OnReady<A>             onReady;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public Promise(final Action<A> action)
    {
        this.asyncTask = new AsyncTask<Void,Void,A>()
        {

            @Override
            protected A doInBackground(Void... args)
            {
                return action.run();
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

    public void run(OnReady<A> onReady)
    {
        this.onReady            = onReady;
        this.asyncTask.execute();
    }


    public abstract static class Action<A>
    {
        abstract public A run();
    }


    public abstract static class OnReady<A>
    {
        abstract public void run(A result);
    }

}
