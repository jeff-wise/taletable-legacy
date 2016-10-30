
package com.kispoko.tome.util;


import android.content.ContentValues;

import static android.R.attr.key;

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

}