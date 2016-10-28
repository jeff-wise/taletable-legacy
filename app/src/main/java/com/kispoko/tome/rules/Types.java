
package com.kispoko.tome.rules;


import android.util.Log;

import com.kispoko.tome.type.ListType;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.TrackerId;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.R.attr.type;


/**
 * Types
 */
public class Types implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID sheetId;

    private Map<String, ListType> listTypeIndex;

    // >> STATIC
//    private static Map<UUID,AsyncConstructor> asyncConstructorMap = new HashMap<>();

    private static Map<UUID,AsyncTracker> asyncTrackerMap = new HashMap<>();


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Types(UUID sheetId)
    {
        this.listTypeIndex = new HashMap<>();
    }


    public Types(List<? extends Type> types)
    {
        // Initialize indexes
        listTypeIndex = new HashMap<>();


        for (Type _type : types) {
            this.addType(_type);
        }
    }


    // > API
    // ------------------------------------------------------------------------------------------

    public int getSize()
    {
        return this.listTypeIndex.size();
    }

    public String printListTypeIndex()
    {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, ListType> e : this.listTypeIndex.entrySet())
        {
            builder.append(e.getKey());
            builder.append("    ");
        }
        return builder.toString();
    }

    /**
     * Add new types to the engine.
     */
    public void addType(Type _type)
    {
        Type.Id typeId = _type.getId();
        String kind = typeId.getKind();
        switch (kind)
        {
            case "list":
                this.listTypeIndex.put(typeId.getId(), (ListType) _type);
                break;
        }
    }


    public Type getType(Type.Id typeId)
    {
        String kind = typeId.getKind();
        switch (kind) {
            case "list":
                return this.listTypeIndex.get(typeId.getId());
            default:
                return null;
        }
    }


    // >> Async Constructor
    // ------------------------------------------------------------------------------------------

//    private static UUID addAsyncConstructor(UUID rulesConstructorId) {
//        UUID constructorId = UUID.randomUUID();
//        Types.asyncConstructorMap.put(constructorId, new AsyncConstructor(rulesConstructorId));
//        return constructorId;
//    }
//
//
//    public static AsyncConstructor getAsyncConstructor(UUID constructorId)
//    {
//        return Types.asyncConstructorMap.get(constructorId);
//    }


    // >> Tracking
    // ------------------------------------------------------------------------------------------

    /**
     * Create a tracker for asynchronously tracking the state of a sheet.
     * @return The new tracker's ID.
     */
    private TrackerId addAsyncTracker(TrackerId rulesTrackerId)
    {
        UUID trackerCode = UUID.randomUUID();
        Types.asyncTrackerMap.put(trackerCode, new AsyncTracker(rulesTrackerId));
        return new TrackerId(trackerCode, TrackerId.Target.TYPES);
    }


    public static AsyncTracker getAsyncTracker(UUID trackerCode)
    {
        return Types.asyncTrackerMap.get(trackerCode);
    }


    // >> Database
    // ------------------------------------------------------------------------------------------

    public void load(TrackerId rulesTrackerId, UUID sheetId)
    {
        TrackerId typesTrackerId = this.addAsyncTracker(rulesTrackerId);

        // Load Types asynchronously
        ListType.loadAll(typesTrackerId, this, sheetId);
    }


    public void save(TrackerId rulesTrackerId, UUID sheetId, boolean recusrive)
    {
        if (!recusrive) return;

        Log.d("***TYPES", "save types");

        TrackerId typesTrackerId = this.addAsyncTracker(rulesTrackerId);

        for (ListType listType : this.listTypeIndex.values()) {
            listType.save(typesTrackerId, sheetId);
        }
    }



    // > NESTED CLASSES
    // ------------------------------------------------------------------------------------------

    /*
    public static class AsyncConstructor
    {
        private UUID rulesConstructorId;

        private List<? extends Type> listTypes;

        public AsyncConstructor(UUID rulesConstructorId)
        {
            this.rulesConstructorId = rulesConstructorId;

            // Initialize async values
            this.listTypes = null;
        }

        synchronized public void setListTypes(List<ListType> listTypes) {
            Log.d("***TYPES", "set list types " + Integer.toString(listTypes.size()));
            this.listTypes = listTypes;
            if (isReady()) ready();
        }

        private boolean isReady() {
            return this.listTypes != null;
        }

        private void ready() {
            Types types = new Types(this.listTypes);
            Rules.getAsyncConstructor(rulesConstructorId).setTypes(types);
        }

    }*/


    /**
     * Track state of Sheet.
     */
    public static class AsyncTracker
    {
        private TrackerId rulesTrackerId;

        private boolean listTypes;

        public AsyncTracker(TrackerId rulesTrackerId) {
            this.rulesTrackerId = rulesTrackerId;
        }

        synchronized public void markListTypes() {
            this.listTypes = true;
            if (isReady()) ready();
        }

        private boolean isReady() {
            return this.listTypes;
        }

        private void ready() {
            Rules.getAsyncTracker(this.rulesTrackerId.getCode()).markTypes();
        }

    }



}
