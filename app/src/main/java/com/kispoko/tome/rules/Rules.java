
package com.kispoko.tome.rules;


import com.kispoko.tome.sheet.Sheet;

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

    private Types types;


    // >> STATIC
    private static Map<UUID,AsyncConstructor> asyncConstructorMap = new HashMap<>();

    private static Map<UUID,SaveTracker> trackerMap = new HashMap<>();


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Rules()
    {
        this.types = new Types();
    }


    public Rules(Types types)
    {
        this.types = types;
    }


    // > API
    // ------------------------------------------------------------------------------------------

    // >> Getters/Setters
    // ------------------------------------------------------------------------------------------

    public Types getTypes() {
        return this.types;
    }


    // >> Async Constructor
    // ------------------------------------------------------------------------------------------

    private static UUID addAsyncConstructor(UUID sheetConstructorId) {
        UUID constructorId = UUID.randomUUID();
        Rules.asyncConstructorMap.put(constructorId, new AsyncConstructor(sheetConstructorId));
        return constructorId;
    }


    public static AsyncConstructor getAsyncConstructor(UUID constructorId)
    {
        return Rules.asyncConstructorMap.get(constructorId);
    }


    // >> Tracking
    // ------------------------------------------------------------------------------------------

    /**
     * Create a tracker for asynchronously tracking the state of a sheet.
     * @return The new tracker's ID.
     */
    private static UUID addTracker(UUID sheetTrackerId) {
        UUID trackerId = UUID.randomUUID();
        Rules.trackerMap.put(trackerId, new SaveTracker(sheetTrackerId));
        return trackerId;
    }


    public static SaveTracker getTracker(UUID trackerId) {
        return Rules.trackerMap.get(trackerId);
    }



    // >> Database
    // ------------------------------------------------------------------------------------------

    public static void load(UUID sheetConstructorId, UUID sheetId)
    {
        UUID rulesConstructorId = Rules.addAsyncConstructor(sheetConstructorId);

        // Load Rules components asynchronously
        Types.loadAll(rulesConstructorId, sheetId);
    }


    public void save(UUID sheetTrackerId, UUID sheetId, boolean recursive)
    {
        if (!recursive) return;

        UUID rulesTrackerId = Rules.addTracker(sheetTrackerId);

        this.types.save(rulesTrackerId, sheetId, true);
    }



    // > NESTED CLASSES
    // ------------------------------------------------------------------------------------------

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

    }


    /**
     * Track state of Sheet.
     */
    public static class SaveTracker
    {
        private UUID sheetTrackerId;

        private boolean types;

        public SaveTracker(UUID sheetTrackerId) {
            this.sheetTrackerId = sheetTrackerId;
            this.types = false;
        }

        synchronized public void setTypes() {
            this.types = true;
            if (isReady()) ready();
        }

        private boolean isReady() {
            return this.types;
        }

        private void ready() {
            Sheet.getTracker(this.sheetTrackerId).setRules();
        }

    }


}
