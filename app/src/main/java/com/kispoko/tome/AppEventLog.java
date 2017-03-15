
package com.kispoko.tome;


import android.util.Log;

import static com.kispoko.tome.util.database.EventLog.EventType.ROW_INSERT;


/**
 * Application Event Log
 */
public class AppEventLog
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private static boolean logToConsole = true;
    private static Level   logLevel     = Level.HIGH;


    // API
    // -----------------------------------------------------------------------------------------

    public static void add(EventType eventType, String message)
    {
        String logLine = eventType.name().toUpperCase() + "  " + message;

        if (logToConsole)
        {
            switch (eventType)
            {
                case MEASUREMENT_SAVE_TIME:
                    if (logLevel == Level.HIGH)
                        logToConsole(logLine);
            }
        }
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    private static void logToConsole(String line)
    {
        Log.d("***APP EVENT", line);
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
        MEASUREMENT_SAVE_TIME
    }

}
