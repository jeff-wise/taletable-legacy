
package com.kispoko.tome.util.database.query;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.kispoko.tome.Global;

import java.util.UUID;



/**
 * Upsert Query
 */
public class UpsertQuery
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String        tableName;
    private UUID          rowId;
    private ContentValues row;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public UpsertQuery(String tableName, UUID rowId, ContentValues row)
    {
        this.tableName = tableName;
        this.rowId     = rowId;
        this.row       = row;
    }


    // API
    // --------------------------------------------------------------------------------------

    /**
     * Run an upsert query using the Android SQLite library insertWithOnConflict function. If the
     * row doesn't exist, it will be created. We must provide all the values every time though.
     */
    public void run()
    {
        SQLiteDatabase database = Global.getDatabase();

        database.insertWithOnConflict(this.tableName,
                                      null,
                                      this.row,
                                      SQLiteDatabase.CONFLICT_REPLACE);
    }


}
