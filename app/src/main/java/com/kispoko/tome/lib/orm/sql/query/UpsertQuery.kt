
package com.kispoko.tome.lib.orm.sql.query


import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.kispoko.tome.lib.orm.DatabaseManager
import com.kispoko.tome.lib.orm.ORMLog
import com.kispoko.tome.lib.orm.ORMLogLevel
import com.kispoko.tome.lib.orm.RowInsert
import com.kispoko.tome.lib.orm.sql.SQL
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
