
package com.kispoko.tome.rules;


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

    private static Map<UUID,AsyncTracker> asyncTrackerMap = new HashMap<>();

    private Map<String, Function> functionMap;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Rules(UUID sheetId)
    {
        this.sheetId = sheetId;
        this.types = new Types(sheetId);
    }


    public Rules(UUID sheetId, Types types, List<Function> functionDefinitions)
    {
        this.sheetId = sheetId;
        this.types = types;

        createFunctions(functionDefinitions);
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
    }


    public void save(TrackerId sheetTrackerId, boolean recursive)
    {
        if (!recursive) return;

        TrackerId rulesTrackerId = this.addAsyncTracker(sheetTrackerId);

        this.types.save(rulesTrackerId, this.getSheetId(), true);
    }


    /**
     * Track state of Sheet.
     */
    public static class AsyncTracker
    {
        private TrackerId sheetTrackerId;

        private boolean types;

        public AsyncTracker(TrackerId sheetTrackerId) {
            this.sheetTrackerId = sheetTrackerId;
            this.types = false;
        }

        synchronized public void markTypes() {
            this.types = true;
            if (isReady()) ready();
        }

        private boolean isReady() {
            return this.types;
        }

        private void ready() {
            Sheet.getAsyncTracker(this.sheetTrackerId.getCode()).markRules();
        }

    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------


    private void createFunctions(List<Function> functionDefinitions) {

    }


}
