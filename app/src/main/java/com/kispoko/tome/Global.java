
package com.kispoko.tome;


import android.database.sqlite.SQLiteDatabase;

/**
 * Global class. Stores global variables
 */
public class Global
{

    private static SQLiteDatabase database;


    public static void setDatabase(SQLiteDatabase database) {
        Global.database = database;
    }

    public static SQLiteDatabase getDatabase() {
        return database;
    }
}
