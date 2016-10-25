
package com.kispoko.tome.rules;


import android.util.Log;

import com.kispoko.tome.type.ListType;
import com.kispoko.tome.type.Type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Types
 */
public class Types implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Map<String, ListType> listTypeIndex;

    // >> STATIC
    private static Map<UUID,AsyncConstructor> asyncConstructorMap = new HashMap<>();

    private static Map<UUID,SaveTracker> trackerMap = new HashMap<>();


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Types()
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

    private static UUID addAsyncConstructor(UUID rulesConstructorId) {
        UUID constructorId = UUID.randomUUID();
        Types.asyncConstructorMap.put(constructorId, new AsyncConstructor(rulesConstructorId));
        return constructorId;
    }


    public static AsyncConstructor getAsyncConstructor(UUID constructorId)
    {
        return Types.asyncConstructorMap.get(constructorId);
    }


    // >> Tracking
    // ------------------------------------------------------------------------------------------

    /**
     * Create a tracker for asynchronously tracking the state of a sheet.
     * @return The new tracker's ID.
     */
    private static UUID addTracker(UUID rulesTrackerId, int listTypesCount) {
        UUID trackerId = UUID.randomUUID();
        Types.trackerMap.put(trackerId, new SaveTracker(rulesTrackerId, listTypesCount));
        return trackerId;
    }


    public static SaveTracker getTracker(UUID trackerId) {
        return Types.trackerMap.get(trackerId);
    }


    // >> Database
    // ------------------------------------------------------------------------------------------

    public static void loadAll(UUID rulesConstructorId, UUID sheetId)
    {
        UUID typesConstructorId = Types.addAsyncConstructor(rulesConstructorId);

        // Load Types asynchronously
        ListType.loadAll(typesConstructorId, sheetId);
    }


    public void save(UUID rulesTrackerId, UUID sheetId, boolean recusrive)
    {
        if (!recusrive) return;

        UUID typesTrackerId = Types.addTracker(rulesTrackerId,
                                               this.listTypeIndex.values().size());

        for (ListType listType : this.listTypeIndex.values())
        {
            listType.save(typesTrackerId, sheetId);
        }
    }



    // > NESTED CLASSES
    // ------------------------------------------------------------------------------------------

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

    }


    /**
     * Track state of Sheet.
     */
    public static class SaveTracker
    {
        private UUID rulesTrackerId;

        private int listTypesTracker;

        public SaveTracker(UUID rulesTrackerId, int listTypesCount) {
            this.rulesTrackerId = rulesTrackerId;
            this.listTypesTracker = listTypesCount;
        }

        synchronized public void trackListType() {
            this.listTypesTracker -= 1;
            if (isReady()) ready();
        }

        private boolean isReady() {
            return this.listTypesTracker == 0;
        }

        private void ready() {
            Log.d("**TYPES", "ready");
            Rules.getTracker(this.rulesTrackerId).setTypes();
        }

    }



}
