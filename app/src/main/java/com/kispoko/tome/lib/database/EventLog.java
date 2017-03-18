
package com.kispoko.tome.lib.database;


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
        String line = eventType.name().toUpperCase() + "  " + message;

        if (logToConsole)
        {
            switch (eventType)
            {
                case MODEL_SAVE:
                    if (logLevel == Level.HIGH)
                        logToConsole(line);
                    break;
                case ROW_INSERT:
                    if (logLevel == Level.HIGH)
                        logToConsole(line);
                    break;
            }
        }
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    private static void logToConsole(String line)
    {
        Log.d("***DATABASE EVENT", line);
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
        MODEL_SAVE,
        ROW_INSERT
    }
}
