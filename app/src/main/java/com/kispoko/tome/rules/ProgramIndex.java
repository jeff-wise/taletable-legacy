
package com.kispoko.tome.rules;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kispoko.tome.Global;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.TrackerId;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.R.attr.name;


/**
 * Program Index
 */
public class ProgramIndex
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID sheetId;
    private Map<String,Program> programByName;

    // Static
    private static Map<UUID,AsyncTracker> asyncTrackerMap = new HashMap<>();


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ProgramIndex(UUID sheetId)
    {
        this.sheetId = sheetId;

        programByName = new HashMap<>();
    }


    public ProgramIndex(UUID sheetId, List<Program> programs)
    {
        this.sheetId = sheetId;

        // TODO ensure all programs have unique names

        programByName = new HashMap<>();
        for (Program program : programs) {
            programByName.put(program.getName(), program);
        }
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > State
    // ------------------------------------------------------------------------------------------

    public UUID getSheetId() {
        return this.sheetId;
    }


    public boolean hasProgram(String programName) {
        return this.programByName.containsKey(programName);
    }


    public Program getProgram(String programName) {
        return this.programByName.get(programName);
    }


    public void addProgram(Program program) {
        // TODO make sure not duplicate
        this.programByName.put(program.getName(), program);
    }


    public Collection<Program> getAllPrograms() {
        return this.programByName.values();
    }


    // > Async Tracker
    // ------------------------------------------------------------------------------------------

    /**
     * Create a new asynchronous tracker for this program index.
     * @param rulesTrackerId The async tracker ID of the caller.
     * @return The unique ID of the tracker.
     */
    private TrackerId addAsyncTracker(TrackerId rulesTrackerId)
    {
        UUID trackerCode = UUID.randomUUID();
        ProgramIndex.asyncTrackerMap.put(trackerCode, new AsyncTracker(this, rulesTrackerId));
        return new TrackerId(trackerCode, TrackerId.Target.PROGRAM_INDEX);
    }


    /**
     * Lookup a reference to a asynchronous tracker for a program index.
     * @param trackerCode The tracker's ID.
     * @return The asynchronous tracker.
     */
    public static AsyncTracker getAsyncTracker(UUID trackerCode)
    {
        return ProgramIndex.asyncTrackerMap.get(trackerCode);
    }


    // > Database
    // ------------------------------------------------------------------------------------------

    /**
     * Load all of the programs for the sheet into the index.
     */
    public void load(final TrackerId rulesTrackerId)
    {
        final ProgramIndex thisProgramIndex = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // Query Program Data
                String query =
                    "SELECT p.program_id, p.program_name " +
                    "FROM Program p " +
                    "WHERE p.sheet_id =  " + SQL.quoted(thisProgramIndex.getSheetId().toString());

                Cursor cursor = database.rawQuery(query, null);

                try {
                    while (cursor.moveToNext()) {
                        // Create "empty" program object for each entry
                        // The remaining program data is loaded asynchronously into the objects
                        UUID id     = UUID.fromString(cursor.getString(0));
                        String name = cursor.getString(1);
                        thisProgramIndex.addProgram(
                                new Program(id, name, thisProgramIndex.getSheetId()));
                    }
                } catch (Exception e) {
                    Log.d("***PROGRAM_INDEX", Log.getStackTraceString(e));
                }
                finally {
                    cursor.close();
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                TrackerId programIndexTrackerId =
                        thisProgramIndex.addAsyncTracker(rulesTrackerId);

                for (Program program : thisProgramIndex.getAllPrograms()) {
                    program.load(programIndexTrackerId);
                }
            }

        }.execute();

    }


    /**
     * Save all of the programs in the index to the database.
     * @param rulesTrackerId The ID of the caller's async tracker.
     */
    public void save(TrackerId rulesTrackerId)
    {
        TrackerId programIndexTrackerId = this.addAsyncTracker(rulesTrackerId);

        for (Program program : this.getAllPrograms()) {
            program.save(programIndexTrackerId);
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

        private ProgramIndex programIndex;
        private TrackerId rulesTrackerId;

        private Map<String,Boolean> programTracker;


        // PROPERTIES
        // --------------------------------------------------------------------------------------

        public AsyncTracker(ProgramIndex programIndex, TrackerId rulesTrackerId)
        {
            this.programIndex = programIndex;
            this.rulesTrackerId = rulesTrackerId;

            this.programTracker = new HashMap<>();
            for (Program program : programIndex.getAllPrograms()) {
                this.programTracker.put(program.getName(), false);
            }
        }


        // API
        // --------------------------------------------------------------------------------------

        synchronized public void markProgram(String programName) {
            if (programName != null && this.programTracker.containsKey(programName))
                this.programTracker.put(programName, true);
            if (isReady()) ready();
        }

        private boolean isReady() {
            for (Boolean flag : programTracker.values()) {
                if (!flag) return false;
            }
            return true;
        }

        private void ready() {
            Rules.getAsyncTracker(this.rulesTrackerId.getCode()).markProgramIndex();
        }

    }


}

