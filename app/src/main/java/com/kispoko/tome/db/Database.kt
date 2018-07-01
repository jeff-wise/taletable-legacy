
package com.kispoko.tome.db


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.kispoko.tome.app.*
import com.kispoko.tome.model.sheet.Sheet
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.entityRecord
import com.kispoko.tome.rts.session.*
import effect.effError
import effect.effValue
import maybe.Just
import org.apache.commons.lang3.SerializationUtils
import java.util.*


object Schema
{

    object SessionTable : BaseColumns
    {
        const val TABLE_NAME                        = "session"
        const val COLUMN_NAME_SESSION_ID            = "session_id"
        const val COLUMN_NAME_SESSION_NAME          = "session_name"
        const val COLUMN_NAME_SESSION_TAGLINE       = "session_tagline"
        const val COLUMN_NAME_SESSION_DESCRIPTION   = "session_description"
        const val COLUMN_NAME_GAME_ID               = "game_id"
        const val COLUMN_NAME_TYPE                  = "session_type"
        const val COLUMN_NAME_SESSION_LAST_USED     = "last_used_time"
        const val COLUMN_NAME_SESSION_LOADER_BLOB   = "session_blob"
    }

    object EntityTable : BaseColumns
    {
        const val TABLE_NAME                = "entity"
        const val COLUMN_NAME_NAME          = "entity_name"
        const val COLUMN_NAME_ENTITY_TYPE   = "entity_type"
        const val COLUMN_NAME_ENTITY_ID     = "entity_id"
        const val COLUMN_NAME_ENTITY_BLOB   = "blob"
    }

    object EntityUpdateTable : BaseColumns
    {
        const val TABLE_NAME                = "entity_update"
        const val COLUMN_NAME_ENTITY_ID     = "entity_id"
        const val COLUMN_NAME_UPDATE        = "entity_update"
    }

}


private val createSessionTableSQL =
    "CREATE TABLE ${Schema.SessionTable.TABLE_NAME} (" +
        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
        "${Schema.SessionTable.COLUMN_NAME_SESSION_ID} TEXT," +
        "${Schema.SessionTable.COLUMN_NAME_SESSION_NAME} TEXT," +
        "${Schema.SessionTable.COLUMN_NAME_SESSION_TAGLINE} TEXT," +
        "${Schema.SessionTable.COLUMN_NAME_SESSION_DESCRIPTION} TEXT," +
        "${Schema.SessionTable.COLUMN_NAME_GAME_ID} TEXT," +
        "${Schema.SessionTable.COLUMN_NAME_TYPE} TEXT," +
        "${Schema.SessionTable.COLUMN_NAME_SESSION_LAST_USED} INTEGER," +
        "${Schema.SessionTable.COLUMN_NAME_SESSION_LOADER_BLOB} BLOB)"


private val createEntityTableSQL =
    "CREATE TABLE ${Schema.EntityTable.TABLE_NAME} (" +
        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
        "${Schema.EntityTable.COLUMN_NAME_NAME} TEXT," +
        "${Schema.EntityTable.COLUMN_NAME_ENTITY_ID} TEXT," +
        "${Schema.EntityTable.COLUMN_NAME_ENTITY_TYPE} TEXT," +
        "${Schema.EntityTable.COLUMN_NAME_ENTITY_BLOB} BLOB)"


private val createEntityUpdateTableSQL =
    "CREATE TABLE ${Schema.EntityUpdateTable.TABLE_NAME} (" +
        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
        "${Schema.EntityUpdateTable.COLUMN_NAME_ENTITY_ID} TEXT," +
        "${Schema.EntityUpdateTable.COLUMN_NAME_UPDATE} BLOB)"

//    "CREATE INDEX entity_id_index ON ${Schema.EntityUpdateTable.TABLE_NAME}(${Schema.EntityUpdateTable.COLUMN_NAME_ENTITY_ID})"


private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${Schema.EntityTable.TABLE_NAME}"



class DatabaseManager(context: Context)
            : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
{

    override fun onCreate(db : SQLiteDatabase)
    {
        db.execSQL(createSessionTableSQL)
        db.execSQL(createEntityTableSQL)
        db.execSQL(createEntityUpdateTableSQL)
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


fun saveEntity(entityId : EntityId, name : String, context : Context) : AppEff<Long> =
    entityRecord(entityId) ap {
        val db = DatabaseManager(context).writableDatabase

        if (db != null)
        {
            val entity = it.entity()
            when (entity)
            {
                is Sheet -> {
                    val sheetBinary = SerializationUtils.serialize(entity)

                    // Create a new map of values, where column names are the keys
                    val values = ContentValues().apply {
                        put(Schema.EntityTable.COLUMN_NAME_NAME, name)
                        put(Schema.EntityTable.COLUMN_NAME_ENTITY_TYPE, it.entityType.toString())
                        put(Schema.EntityTable.COLUMN_NAME_ENTITY_ID, entityId.toString())
                        put(Schema.EntityTable.COLUMN_NAME_ENTITY_BLOB, sheetBinary)
                    }

                    val startNS = System.nanoTime()

                    // Insert the new row, returning the primary key value of the new row
                    val newRowId = db.insert(Schema.EntityTable.TABLE_NAME, null, values)

                    val endNS = System.nanoTime()

                    Log.d("***DATABASE", "new row id: $newRowId")
                    ApplicationLog.event(AppDBEvent(DatabaseEntitySaved(entityId, (endNS - startNS))))

                    effValue(newRowId)

                }
                else -> {
                    effError<AppError,Long>(AppDBError(CouldNotAccessDatabase()))

                }
            }
        }
        else
        {
            effError<AppError,Long>(AppDBError(CouldNotAccessDatabase()))
        }
    }



fun saveUpdate(entityId : EntityId, context : Context) { }


fun saveSession(sessionRecord : SessionRecord, context : Context) : AppEff<Long>
{
    val db = DatabaseManager(context).writableDatabase

    return if (db != null)
    {
        val sessionBlob = SerializationUtils.serialize(sessionRecord.loader)

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(Schema.SessionTable.COLUMN_NAME_SESSION_ID, sessionRecord.sessionId.value.toString())
            put(Schema.SessionTable.COLUMN_NAME_SESSION_NAME, sessionRecord.sessionName.value)
            put(Schema.SessionTable.COLUMN_NAME_SESSION_TAGLINE, sessionRecord.sessionTagline)
            put(Schema.SessionTable.COLUMN_NAME_SESSION_DESCRIPTION, sessionRecord.sessionDescription.value)
            put(Schema.SessionTable.COLUMN_NAME_SESSION_LAST_USED, System.currentTimeMillis())
            put(Schema.SessionTable.COLUMN_NAME_SESSION_LOADER_BLOB, sessionBlob)
        }

        val startNS = System.nanoTime()

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(Schema.SessionTable.TABLE_NAME, null, values)

        Log.d("***DATABASE", "save session: new row id: $newRowId")

        val endNS = System.nanoTime()

        ApplicationLog.event(AppDBEvent(DatabaseSessionSaved(sessionRecord.sessionId, (endNS - startNS))))

        effValue(newRowId)
    }
    else
    {
        effError(AppDBError(CouldNotAccessDatabase()))
    }
}


fun loadSessionList(context : Context) : List<SessionRecord>
{

    val db = DatabaseManager(context).readableDatabase

    val projection = arrayOf(BaseColumns._ID,
                             Schema.SessionTable.COLUMN_NAME_SESSION_ID,
                             Schema.SessionTable.COLUMN_NAME_SESSION_NAME,
                             Schema.SessionTable.COLUMN_NAME_SESSION_TAGLINE,
                             Schema.SessionTable.COLUMN_NAME_SESSION_DESCRIPTION,
                             Schema.SessionTable.COLUMN_NAME_SESSION_LAST_USED,
                             Schema.SessionTable.COLUMN_NAME_SESSION_LOADER_BLOB)

    val sortOrder = "datetime(${Schema.SessionTable.COLUMN_NAME_SESSION_LAST_USED}) DESC"

    val cursor = db.query(
            Schema.SessionTable.TABLE_NAME, // The table to query
            projection,                     // The array of columns to return (pass null to get all)
            null,                             // The columns for the WHERE clause
            arrayOf(),                      // The values for the WHERE clause
            null,                           // don't group the rows
            null,                           // don't filter by row groups
            sortOrder)                      // The sort order


    Log.d("***DATABASE", "cursor: $cursor")


    val records : MutableList<SessionRecord> = mutableListOf()

    with(cursor) {
        while (moveToNext()) {

            val sessionId = SessionId.fromString(getString(getColumnIndexOrThrow(Schema.SessionTable.COLUMN_NAME_SESSION_ID)))
            val sessionName = SessionName(getString(getColumnIndexOrThrow(Schema.SessionTable.COLUMN_NAME_SESSION_NAME)))
            val sessionTagline = getString(getColumnIndexOrThrow(Schema.SessionTable.COLUMN_NAME_SESSION_TAGLINE))
            val sessionDescription = SessionDescription(getString(getColumnIndexOrThrow(Schema.SessionTable.COLUMN_NAME_SESSION_DESCRIPTION)))

            val lastUsed = Calendar.getInstance()
            lastUsed.timeInMillis = getLong(getColumnIndexOrThrow(Schema.SessionTable.COLUMN_NAME_SESSION_LAST_USED))

            val loader = SerializationUtils.deserialize<SessionLoader>(
                             getBlob(getColumnIndexOrThrow(Schema.SessionTable.COLUMN_NAME_SESSION_LOADER_BLOB)))

            Log.d("***DATABASE", "found session: $sessionName")

            if (sessionId != null) {
                val sessionRecord = SessionRecord(sessionId,
                                                  sessionName,
                                                  sessionTagline,
                                                  sessionDescription,
                                                  lastUsed,
                                                  loader)
                records.add(sessionRecord)
            }
        }
    }

    return records
}


fun saveCurrentSession(context : Context)
{
    val maybeSession = activeSession()
    when (maybeSession) {
        is Just -> {
            saveSession(maybeSession.value.record(), context)
        }
    }
}

