
package com.kispoko.tome.util.database.query;


import android.content.ContentValues;

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

    public void run()
    {

    }


}
