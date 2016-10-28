
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

}