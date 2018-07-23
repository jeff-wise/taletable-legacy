
package com.taletable.android.lib.orm.sql.query


import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.taletable.android.lib.orm.DatabaseManager
import com.taletable.android.lib.orm.ORMLog
import com.taletable.android.lib.orm.ORMLogLevel
import com.taletable.android.lib.orm.RowInsert
import com.taletable.android.lib.orm.sql.SQL
import java.util.*



/**
 * Upsert Query
 */
data class UpsertQuery(val tableName : String, val rowId : UUID, val row : ContentValues)
{

    fun run()
    {
        val database = DatabaseManager.database()

        val validTableName = SQL.validIdentifier(tableName)

        val startTime = System.nanoTime()

        database.insertWithOnConflict(validTableName,
                                      null,
                                      this.row,
                                      SQLiteDatabase.CONFLICT_REPLACE)

        val endTime = System.nanoTime()

        if (ORMLog.logLevel < ORMLogLevel.EVERYTHING)
            ORMLog.event(RowInsert(validTableName, rowId, (endTime - startTime)))
        else
            ORMLog.event(RowInsert(validTableName, rowId, (endTime - startTime), row))
    }


}
