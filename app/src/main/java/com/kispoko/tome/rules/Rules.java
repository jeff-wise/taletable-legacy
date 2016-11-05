
package com.kispoko.tome.rules;


import com.kispoko.tome.rules.function.Function;
import com.kispoko.tome.rules.function.FunctionIndex;
import com.kispoko.tome.rules.program.Program;
import com.kispoko.tome.rules.program.ProgramIndex;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.util.TrackerId;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Rules Engine
 */
public class Rules implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID sheetId;
    private Types types;
    private FunctionIndex functionIndex;
    private ProgramIndex programIndex;

    private static Map<UUID,AsyncTracker> asyncTrackerMap = new HashMap<>();



    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Rules(UUID sheetId)
    {
        this.sheetId = sheetId;
        this.types = new Types(sheetId);
        this.functionIndex = new FunctionIndex(sheetId);
        this.programIndex = new ProgramIndex(sheetId);
    }


    public Rules(UUID sheetId, Types types, List<Function> functions, List<Program> programs)
    {
        this.sheetId = sheetId;
        this.types = types;

        this.functionIndex = new FunctionIndex(sheetId, functions);
        this.programIndex = new ProgramIndex(sheetId, programs);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > State
    // ------------------------------------------------------------------------------------------

    public Types getTypes() {
        return this.types;
    }


    public UUID getSheetId() {
        return this.sheetId;
    }


    // > Async Tracker
    // ------------------------------------------------------------------------------------------

    private TrackerId addAsyncTracker(TrackerId sheetTrackerId)
    {
        UUID trackerCode = UUID.randomUUID();
        Rules.asyncTrackerMap.put(trackerCode, new AsyncTracker(sheetTrackerId));
        return new TrackerId(trackerCode, TrackerId.Target.RULES);
    }


    public static AsyncTracker getAsyncTracker(UUID trackerCode)
    {
        return Rules.asyncTrackerMap.get(trackerCode);
    }


    // > Database
    // ------------------------------------------------------------------------------------------

    public void load(TrackerId sheetTrackerId)
    {
        TrackerId rulesTrackerId = this.addAsyncTracker( sheetTrackerId);

        // Load Rules components asynchronously
        this.types.load(rulesTrackerId, this.getSheetId());
        this.functionIndex.load(rulesTrackerId);
        this.programIndex.load(rulesTrackerId);
    }


    public void save(TrackerId sheetTrackerId, boolean recursive)
    {
        if (!recursive) return;

        TrackerId rulesTrackerId = this.addAsyncTracker(sheetTrackerId);

        // Save all Rules components
        this.types.save(rulesTrackerId, this.getSheetId(), true);
        this.functionIndex.save(rulesTrackerId);
        this.programIndex.save(rulesTrackerId);
    }


    /**
     * Track state of Sheet.
     */
    public static class AsyncTracker
    {
        private TrackerId sheetTrackerId;

        private boolean types;
        private boolean functionIndex;
        private boolean programIndex;

        public AsyncTracker(TrackerId sheetTrackerId) {
            this.sheetTrackerId = sheetTrackerId;
            this.types = false;
        }

        synchronized public void markTypes() {
            this.types = true;
            if (isReady()) ready();
        }

        synchronized public void markFunctionIndex() {
            this.functionIndex = true;
            if (isReady()) ready();
        }

        synchronized public void markProgramIndex() {
            this.programIndex = true;
            if (isReady()) ready();
        }

        private boolean isReady() {
            return this.types &&
                   this.functionIndex &&
                   this.programIndex;
        }

        private void ready() {
            Sheet.getAsyncTracker(this.sheetTrackerId.getCode()).markRules();
        }

    }

}
