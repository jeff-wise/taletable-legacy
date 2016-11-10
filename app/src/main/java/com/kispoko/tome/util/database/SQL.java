
package com.kispoko.tome.util.database;


import android.content.ContentValues;



/**
 * SQL Utility Class
 */
public class SQL
{

    public static String quoted(String innerString)
    {
        return "'" + innerString + "'";
    }


    public static void putOptString(ContentValues row, String columnName, Object object)
    {
        if (object != null)
            row.put(columnName, object.toString());
        else
            row.putNull(columnName);
    }


    public static Integer boolAsInt(Boolean bool)
    {
        if (bool != null)
            return bool ? 1 : 0;
        return null;
    }


    public static Boolean intAsBool(Integer i)
    {
        if (i != null)
            return i != 0;
        return null;
    }



    // TYPES
    // ------------------------------------------------------------------------------------------

    public enum Constraint
    {
        PRIMARY_KEY
    }


    public enum DataType
    {
        TEXT,
        INTEGER,
        REAL,
        BLOB
    }

}