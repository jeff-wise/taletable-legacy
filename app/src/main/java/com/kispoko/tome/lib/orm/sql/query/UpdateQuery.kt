
package com.kispoko.tome.lib.orm.sql.query


import android.content.ContentValues
import com.kispoko.tome.lib.orm.*
import com.kispoko.tome.lib.orm.sql.*
import java.util.*



/**
 * Update Query
 */
data class UpdateQuery(val tableName : String,
                       val columnName : String,
                       val rowId : UUID,
                       val value : SQLValue)
{

    fun run()
    {
        val database = DatabaseManager.database()

        val validTableName = SQL.validIdentifier(tableName)
        val validColumnName = SQL.validIdentifier(columnName)

        val contentValues = ContentValues()
        contentValues.addSQLValue(validColumnName, value)

        val startTime = System.nanoTime()

        val rowsUpdated = database.update(validTableName,
                                          contentValues,
                                          "_id = '$rowId'",
                                          null)

        val endTime = System.nanoTime()

        if (ORMLog.logLevel >= ORMLogLevel.NORMAL)
            ORMLog.event(ValueUpdate(validTableName, validColumnName, rowId, value, (endTime - startTime)))
    }


}