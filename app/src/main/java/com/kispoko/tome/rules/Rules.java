
package com.kispoko.tome.rules;


import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.sheet.component.Table;
import com.kispoko.tome.util.TrackerId;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



/**
 * Rules Engine
 */
public class Rules implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID sheetId;
    private Types types;


    // >> STATIC
    //private static Map<UUID,AsyncConstructor> asyncConstructorMap = new HashMap<>();

    private static Map<UUID,AsyncTracker> asyncTrackerMap = new HashMap<>();


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

//    public Rules()
//    {
//        this.types = new Types();
//    }


    public Rules(UUID sheetId)
    {
        this.sheetId = sheetId;
        this.types = new Types(sheetId);
    }


    public Rules(UUID sheetId, Types types)
    {
        this.sheetId = sheetId;
        this.types = types;
    }


    // > API
    // ------------------------------------------------------------------------------------------

    // >> State
    // ------------------------------------------------------------------------------------------

    public Types getTypes() {
        return this.types;
    }


    public UUID getSheetId() {
        return this.sheetId;
    }


    // >> Async Constructor
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


    // >> Tracking
    // ------------------------------------------------------------------------------------------

    /**
     * Create a tracker for asynchronously tracking the state of a sheet.
     * @return The new tracker's ID.
     */
//    private static UUID addTracker(UUID sheetTrackerId) {
//        UUID trackerCode = UUID.randomUUID();
//        Rules.asyncTrackerMap.put(trackerId, new AsyncTracker(sheetTrackerId));
//        return trackerId;
//    }
//
//
//    public static AsyncTracker getTracker(UUID trackerId) {
//        return Rules.asyncTrackerMap.get(trackerId);
//    }



    // >> Database
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



    // > NESTED CLASSES
    // ------------------------------------------------------------------------------------------

    /*
    public static class AsyncConstructor
    {
        private UUID sheetConstructorId;

        private Types types;

        public AsyncConstructor(UUID sheetConstructorId)
        {
            this.sheetConstructorId = sheetConstructorId;

            // Initialize async values
            this.types = null;
        }

        synchronized public void setTypes(Types types)
        {
            this.types = types;
            if (isReady()) ready();
        }


        private boolean isReady() {
            return this.types != null;
        }

        private void ready() {
            Rules rules = new Rules(this.types);
            Sheet.getAsyncConstructor(sheetConstructorId).setRules(rules);
        }

    }*/


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


}
