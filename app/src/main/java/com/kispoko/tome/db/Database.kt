
package com.kispoko.tome.db


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.kispoko.tome.app.AppDBError
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.entityRecord
import com.kispoko.tome.rts.session.Session
import effect.effError
import effect.effValue
import org.apache.commons.lang3.SerializationUtils



object Schema
{

    object SessionTable : BaseColumns
    {
        const val TABLE_NAME                        = "session"
        const val COLUMN_NAME_SESSION_ID            = "session_id"
        const val COLUMN_NAME_SESSION_LOADER_BLOB   = "session_blob"
    }

    object EntityTable : BaseColumns
    {
        const val TABLE_NAME                = "entity"
        const val COLUMN_NAME_NAME          = "name"
        const val COLUMN_NAME_ENTITY_TYPE   = "type"
        const val COLUMN_NAME_ENTITY_ID     = "entity_id"
        const val COLUMN_NAME_ENTITY_BLOB   = "blob"
    }

    object EntityUpdateTable : BaseColumns
    {
        const val TABLE_NAME                = "entity_update"
        const val COLUMN_NAME_ENTITY_ID     = "entity_id"
        const val COLUMN_NAME_UPDATE        = "update"
    }

}


private val SQL_CREATE_ENTRIES =

    "CREATE TABLE ${Schema.SessionTable.TABLE_NAME} (" +
        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
        "${Schema.SessionTable.COLUMN_NAME_SESSION_ID} TEXT," +
        "${Schema.SessionTable.COLUMN_NAME_SESSION_LOADER_BLOB} BLOB);" +

    "CREATE TABLE ${Schema.EntityTable.TABLE_NAME} (" +
        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
        "${Schema.EntityTable.COLUMN_NAME_NAME} TEXT," +
        "${Schema.EntityTable.COLUMN_NAME_ENTITY_TYPE} TEXT," +
        "${Schema.EntityTable.COLUMN_NAME_ENTITY_BLOB} BLOB);" +

    "CREATE TABLE ${Schema.EntityUpdateTable.TABLE_NAME} (" +
        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
        "${Schema.EntityUpdateTable.COLUMN_NAME_ENTITY_ID} TEXT," +
        "${Schema.EntityUpdateTable.COLUMN_NAME_UPDATE} BLOB);" +

    "CREATE INDEX entity_id_index ON ${Schema.EntityUpdateTable.TABLE_NAME}(${Schema.EntityUpdateTable.COLUMN_NAME_ENTITY_ID})"


private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${Schema.EntityTable.TABLE_NAME}"



class DatabaseManager(context: Context)
            : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
{

    override fun onCreate(db : SQLiteDatabase)
    {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db : SQLiteDatabase, oldVersion : Int, newVersion : Int)
    {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object
    {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Main.db"
    }

}



//const val COLUMN_NAME_NAME          = "name"
//const val COLUMN_NAME_ENTITY_TYPE   = "entity_type"
//const val COLUMN_NAME_FILE          = "file"

suspend fun saveEntity(entityId : EntityId, name : String, context : Context) : AppEff<Long> =
    entityRecord(entityId) ap {
        val db = DatabaseManager(context).writableDatabase

        if (db != null)
        {
            val sheetBinary = SerializationUtils.serialize(it.entity())

            // Create a new map of values, where column names are the keys
            val values = ContentValues().apply {
                put(Schema.EntityTable.COLUMN_NAME_NAME, name)
                put(Schema.EntityTable.COLUMN_NAME_ENTITY_TYPE, it.entityType.toString())
                put(Schema.EntityTable.COLUMN_NAME_ENTITY_ID, entityId.toString())
                put(Schema.EntityTable.COLUMN_NAME_ENTITY_BLOB, sheetBinary)
            }

            // Insert the new row, returning the primary key value of the new row
            val newRowId = db.insert(Schema.EntityTable.TABLE_NAME, null, values)

            effValue(newRowId)
        }
        else
        {
            effError<AppError,Long>(AppDBError(CouldNotAccessDatabase()))
        }
    }



suspend fun saveUpdate(entityId : EntityId, context : Context) { }


suspend fun saveSession(session : Session, context : Context) : AppEff<Long>
{
    val db = DatabaseManager(context).writableDatabase

    return if (db != null)
    {
        val sessionBlob = SerializationUtils.serialize(session.loader())

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(Schema.SessionTable.COLUMN_NAME_SESSION_ID, session.sessionId.value)
            put(Schema.SessionTable.COLUMN_NAME_SESSION_LOADER_BLOB, sessionBlob)
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(Schema.EntityTable.TABLE_NAME, null, values)

        effValue(newRowId)
    }
    else
    {
        effError<AppError,Long>(AppDBError(CouldNotAccessDatabase()))
    }
}

