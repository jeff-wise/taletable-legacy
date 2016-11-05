
package com.kispoko.tome;


import android.database.sqlite.SQLiteDatabase;

import com.kispoko.tome.sheet.component.Table;
import com.kispoko.tome.util.Tracker;
import com.kispoko.tome.util.TrackerId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Global class. Stores global variables
 */
public class Global
{

    private static SQLiteDatabase database;

    private static Map<UUID,Tracker> trackerMap = new HashMap<>();


    public static void setDatabase(SQLiteDatabase database) {
        Global.database = database;
    }

    public static SQLiteDatabase getDatabase() {
        return database;
    }


    // Tracker
    // -----------------------------------------------------------------------------------------

    public static UUID addTracker(Tracker tracker)
    {
        UUID trackerId = UUID.randomUUID();
        trackerMap.put(trackerId, tracker);
        return trackerId;
    }


    public static Tracker getTracker(UUID trackerId)
    {
        return trackerMap.get(trackerId);
    }


}
