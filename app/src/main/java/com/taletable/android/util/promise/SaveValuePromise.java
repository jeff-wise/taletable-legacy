
package com.taletable.android.util.promise;


import android.os.AsyncTask;



/**
 * Promise: Save Shared Value
 */
public class SaveValuePromise
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private AsyncTask<Void,Void,Boolean> asyncTask;
    private OnReady                      onReady;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public SaveValuePromise(final Action action)
    {
        this.asyncTask = new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                action.run();
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                if (onReady != null)
                    onReady.run();
            }
        };
    }


    // API
    // --------------------------------------------------------------------------------------

    public void run(OnReady onReady)
    {
        this.onReady = onReady;

        this.asyncTask.execute();
    }


    public abstract static class Action
    {
        abstract public void run();
    }


    public abstract static class OnReady
    {
        abstract public void run();
    }

}
