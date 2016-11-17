
package com.kispoko.tome.rules.programming.function;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kispoko.tome.Global;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.util.database.SQL;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Function Index
 */
public class FunctionIndex
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID sheetId;
    private Map<String,Function> functionByName;

    // Static
    private static Map<UUID,AsyncTracker> asyncTrackerMap = new HashMap<>();


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public FunctionIndex(UUID sheetId)
    {
        this.sheetId = sheetId;

        functionByName = new HashMap<>();
    }


    public FunctionIndex(UUID sheetId, List<Function> functions)
    {
        this.sheetId = sheetId;

        functionByName = new HashMap<>();
        for (Function function : functions)
        {
            functionByName.put(function.getName(), function);
        }
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > State
    // ------------------------------------------------------------------------------------------

    // ** Sheet Id
    // ------------------------------------------------------------------------------------------

    public UUID getSheetId() {
        return this.sheetId;
    }


    public void addFunction(Function function) {
        this.functionByName.put(function.getName(), function);
    }


    public boolean hasFunction(String functionName) {
        return this.functionByName.containsKey(functionName);
    }


    public Function getFunction(String functionName) {
        return this.functionByName.get(functionName);
    }


    public Collection<Function> getAllFunctions() {
        return this.functionByName.values();
    }


    // > Async Tracker
    // ------------------------------------------------------------------------------------------

    /**
     * Create a new asynchronous tracker for this function index.
     * @param rulesTrackerId The async tracker ID of the caller.
     * @return The unique ID of the tracker.
     */
    private TrackerId addAsyncTracker(TrackerId rulesTrackerId)
    {
        UUID trackerCode = UUID.randomUUID();
        FunctionIndex.asyncTrackerMap.put(trackerCode, new AsyncTracker(this, rulesTrackerId));
        return new TrackerId(trackerCode, TrackerId.Target.FUNCTION_INDEX);
    }


    /**
     * Lookup a reference to a asynchronous tracker for a function index.
     * @param trackerCode The tracker's ID.
     * @return The asynchronous tracker.
     */
    public static AsyncTracker getAsyncTracker(UUID trackerCode)
    {
        return FunctionIndex.asyncTrackerMap.get(trackerCode);
    }


    // > Database
    // ------------------------------------------------------------------------------------------

    /**
     * Load all of the functions for the sheet into the index.
     */
    public void load(final TrackerId rulesTrackerId)
    {
        final FunctionIndex thisFunctionIndex = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // ModelQuery Function Data
                String query =
                    "SELECT f.function_name " +
                    "FROM Function f " +
                    "WHERE f.sheet_id =  " + SQL.quoted(thisFunctionIndex.getSheetId().toString());

                Cursor cursor = database.rawQuery(query, null);

                try
                {
                    while (cursor.moveToNext())
                    {
                        UUID id     = UUID.fromString(cursor.getString(0));
                        String name = cursor.getString(1);

                        thisFunctionIndex.addFunction(
                                new Function(id, name, thisFunctionIndex.getSheetId()));
                    }

                } catch (Exception e) {
                    Log.d("***GROUP", Log.getStackTraceString(e));
                }
                finally {
                    cursor.close();
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                TrackerId functionIndexTrackerId =
                        thisFunctionIndex.addAsyncTracker(rulesTrackerId);

                for (Function function : thisFunctionIndex.getAllFunctions()) {
                    function.load(functionIndexTrackerId);
                }
            }

        }.execute();

    }


    /**
     * Save all of the functions in the index.
     * @param rulesTrackerId The async tracker ID of the caller.
     */
    public void save(TrackerId rulesTrackerId)
    {
        TrackerId functionIndexTrackerId = this.addAsyncTracker(rulesTrackerId);

        for (Function function : this.getAllFunctions()) {
            function.save(functionIndexTrackerId);
        }
    }


    // NESTED CLASSES
    // ------------------------------------------------------------------------------------------

    /**
     * Track state of Function Index
     */
    public static class AsyncTracker
    {

        // PROPERTIES
        // --------------------------------------------------------------------------------------

        private FunctionIndex functionIndex;
        private TrackerId rulesTrackerId;

        private Map<String,Boolean> functionTracker;


        // PROPERTIES
        // --------------------------------------------------------------------------------------

        public AsyncTracker(FunctionIndex functionIndex, TrackerId rulesTrackerId)
        {
            this.functionIndex = functionIndex;
            this.rulesTrackerId = rulesTrackerId;

            this.functionTracker = new HashMap<>();
            for (Function function : functionIndex.getAllFunctions()) {
                functionTracker.put(function.getName(), false);
            }
        }


        // API
        // --------------------------------------------------------------------------------------

        synchronized public void markFunction(String functionName) {
            if (functionName != null && this.functionTracker.containsKey(functionName))
                this.functionTracker.put(functionName, true);
            if (isReady()) ready();
        }

        private boolean isReady() {
            for (Boolean flag : functionTracker.values()) {
                if (!flag) return false;
            }
            return true;
        }

        private void ready() {
            Rules.getAsyncTracker(this.rulesTrackerId.getCode()).markFunctionIndex();
        }

    }


}
