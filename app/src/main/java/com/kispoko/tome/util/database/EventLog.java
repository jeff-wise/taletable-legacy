
package com.kispoko.tome.util.database;


import android.util.Log;

/**
 * Database Event Log
 */
public class EventLog
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private static boolean logToConsole = true;
    private static Level   logLevel     = Level.LOW;


    // API
    // -----------------------------------------------------------------------------------------

    public static void add(EventType eventType, String message)
    {
        if (logToConsole)
        {
            switch (eventType)
            {
                case ROW_INSERT:
                    if (logLevel == Level.HIGH)
                        Log.d("***DATABASE EVENT", eventType.name().toUpperCase() + "  " + message);
            }
        }
    }



    // LEVEL
    // -----------------------------------------------------------------------------------------

    public enum Level
    {
        LOW,
        MEDIUM,
        HIGH
    }


    // EVENT TYPE
    // -----------------------------------------------------------------------------------------

    public enum EventType
    {
        ROW_INSERT
    }
}
