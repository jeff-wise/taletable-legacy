
package com.kispoko.tome.util;



/**
 * SQL Utility Class
 */
public class SQL
{

    public static String quoted(String innerString)
    {
        return "'" + innerString + "'";
    }
}
