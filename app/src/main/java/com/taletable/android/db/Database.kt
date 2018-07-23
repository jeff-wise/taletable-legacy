
package com.taletable.android.db


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.taletable.android.app.*
import com.taletable.android.model.entity.EntityUpdate
import com.taletable.android.model.sheet.Sheet
import com.taletable.android.rts.entity.Entity
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.entityRecord
import com.taletable.android.rts.session.*
import effect.effError
import effect.effValue
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import org.apache.commons.lang3.SerializationUtils
import java.util.*



object Schema
{

    object SessionTable : BaseColumns
    {
        const val TABLE_NAME                        = "session"
        const val COLUMN_NAME_SESSION_ID            = "session_id"
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
        "${Schema.SessionTable.COLUMN_NAME_SESSION_LAST_USED} INTEGER," +
        "${Schema.SessionTable.COLUMN_NAME_SESSION_LOADER_BLOB} BLOB)"


private val createEntityTableSQL =
    "CREATE TABLE ${Schema.EntityTable.TABLE_NAME} (" +
        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
        "${Schema.EntityTable.COLUMN_NAME_NAME} TEXT," +
        "${Schema.EntityTable.COLUMN_NAME_ENTITY_ID} TEXT," +
        "${Schema.EntityTable.COLUMN_NAME_ENTITY_TYPE} TEXT," +
        "${Schema.EntityTable.COLUMN_NAME_ENTITY_BLOB} BLOB)"

private val createEntityTableIndexes =
    "CREATE INDEX entity_id_idx ON ${Schema.EntityTable.TABLE_NAME}(${Schema.EntityTable.COLUMN_NAME_ENTITY_ID});"


private val createEntityUpdateTableSQL =
    "CREATE TABLE ${Schema.EntityUpdateTable.TABLE_NAME} (" +
        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
        "${Schema.EntityUpdateTable.COLUMN_NAME_ENTITY_ID} TEXT," +
        "${Schema.EntityUpdateTable.COLUMN_NAME_UPDATE} BLOB)"

private val createEntityUpdateTableIndexes =
        "CREATE INDEX entity_update_id_idx ON ${Schema.EntityUpdateTable.TABLE_NAME}(${Schema.EntityUpdateTable.COLUMN_NAME_ENTITY_ID});"


private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${Schema.EntityTable.TABLE_NAME}"



class DatabaseManager(context: Context)
            : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
{

    override fun onCreate(db : SQLiteDatabase)
    {
        db.execSQL(createSessionTableSQL)

        db.execSQL(createEntityTableSQL)
        db.execSQL(createEntityTableIndexes)

        db.execSQL(createEntityUpdateTableSQL)
        db.execSQL(createEntityUpdateTableIndexes)
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


fun writeEntity(entityId : EntityId, context : Context) : AppEff<Long> =
    entityRecord(entityId) ap { entityRecord ->
        val db = DatabaseManager(context).writableDatabase

        if (db != null)
        {
            val entity = entityRecord.entity()
            when (entity)
            {
                is Sheet -> {
                    val sheetBinary = SerializationUtils.serialize(entity)

                    // Create a new map of values, where column names are the keys
                    val values = ContentValues().apply {
                        put(Schema.EntityTable.COLUMN_NAME_NAME, entity.name())
                        put(Schema.EntityTable.COLUMN_NAME_ENTITY_TYPE, entityRecord.entityType.toString())
                        put(Schema.EntityTable.COLUMN_NAME_ENTITY_ID, entityId.toString())
                        put(Schema.EntityTable.COLUMN_NAME_ENTITY_BLOB, sheetBinary)
                    }

                    val startNS = System.nanoTime()

                    // Insert the new row, returning the primary key value of the new row
                    val newRowId = db.insert(Schema.EntityTable.TABLE_NAME, null, values)

                    val endNS = System.nanoTime()

                    Log.d("***DATABASE", "new entity row id: $newRowId")
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



fun readEntity(entityId : EntityId, context : Context) : Maybe<Entity>
{

    val db = DatabaseManager(context).readableDatabase

    val projection = arrayOf(BaseColumns._ID,
                             Schema.EntityTable.COLUMN_NAME_NAME,
                             Schema.EntityTable.COLUMN_NAME_ENTITY_ID,
                             Schema.EntityTable.COLUMN_NAME_ENTITY_TYPE,
                             Schema.EntityTable.COLUMN_NAME_ENTITY_BLOB)

    val selection = "${Schema.EntityTable.COLUMN_NAME_ENTITY_ID} = ?"
    val selectionArgs = arrayOf(entityId.toString())

    val cursor = db.query(
            Schema.EntityTable.TABLE_NAME, // The table to query
            projection,                     // The array of columns to return (pass null to get all)
            selection,                      // The columns for the WHERE clause
            selectionArgs,                  // The values for the WHERE clause
            null,                           // don't group the rows
            null,                           // don't filter by row groups
            null)                           // The sort order

    Log.d("***DATABASE", "Read Entity")

    val entities : MutableList<Entity> = mutableListOf()

    with(cursor) {
        while (moveToNext()) {

//            val entityId   = EntityId.fromString(getString(getColumnIndexOrThrow(Schema.EntityTable.COLUMN_NAME_ENTITY_ID)))
            val entityName = getString(getColumnIndexOrThrow(Schema.EntityTable.COLUMN_NAME_NAME))
//            val entityType = SessionName(getString(getColumnIndexOrThrow(Schema.EntityTable.COLUMN_NAME_ENTITY_TYPE)))

            val entity = SerializationUtils.deserialize<Entity>(
                                getBlob(getColumnIndexOrThrow(Schema.EntityTable.COLUMN_NAME_ENTITY_BLOB)))
//            val lastUsed = Calendar.getInstance()
//            lastUsed.timeInMillis = getLong(getColumnIndexOrThrow(Schema.SessionTable.COLUMN_NAME_SESSION_LAST_USED))

            entities.add(entity)
        }
    }

    var result : Maybe<Entity> = Nothing()

    entities.firstOrNull()?.let { entity ->

        result = Just(entity)

        Log.d("***DATABASE", "found entity: ${entity.name()}")

        val updates = readEntityUpdates(entityId, context)
        Log.d("***DATABASE", "entity updates: $updates")
        when (entity)
        {
            is Sheet -> {
                Log.d("***DATABASE", "entity is sheet")
                updates.forEach {
                    Log.d("***DATABASE", "applying entity update")
                    entity.update(it, null, context)
                }
            }
        }
    }

    return result
}


fun readEntityUpdates(entityId : EntityId, context : Context) : List<EntityUpdate>
{

    val db = DatabaseManager(context).readableDatabase

    val projection = arrayOf(BaseColumns._ID,
                             Schema.EntityUpdateTable.COLUMN_NAME_UPDATE)

    val selection = "${Schema.EntityUpdateTable.COLUMN_NAME_ENTITY_ID} = ?"
    val selectionArgs = arrayOf(entityId.toString())

    val cursor = db.query(
            Schema.EntityUpdateTable.TABLE_NAME, // The table to query
            projection,                     // The array of columns to return (pass null to get all)
            selection,                      // The columns for the WHERE clause
            selectionArgs,                  // The values for the WHERE clause
            null,                           // don't group the rows
            null,                           // don't filter by row groups
            null)                           // The sort order


    val updates : MutableList<EntityUpdate> = mutableListOf()

    with(cursor) {
        while (moveToNext()) {

            val entityUpdate = SerializationUtils.deserialize<EntityUpdate>(
                                 getBlob(getColumnIndexOrThrow(Schema.EntityUpdateTable.COLUMN_NAME_UPDATE)))
            updates.add(entityUpdate)
        }
    }

    return updates
}




fun writeEntityUpdate(entityId : EntityId,
                      entityUpdate : EntityUpdate,
                      context : Context) : AppEff<Long>
{
    val db = DatabaseManager(context).writableDatabase

    return if (db != null)
    {
        val entityUpdateBlob = SerializationUtils.serialize(entityUpdate)

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(Schema.EntityUpdateTable.COLUMN_NAME_ENTITY_ID, entityId.toString())
            put(Schema.EntityUpdateTable.COLUMN_NAME_UPDATE, entityUpdateBlob)
        }

        val startNS = System.nanoTime()

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(Schema.EntityUpdateTable.TABLE_NAME, null, values)

        Log.d("***DATABASE", "save entity update: $entityUpdate, row id: $newRowId")

        val endNS = System.nanoTime()

//        ApplicationLog.event(AppDBEvent(DatabaseSessionSaved(session.sessionId, (endNS - startNS))))

        effValue(newRowId)
    }
    else
    {
        effError(AppDBError(CouldNotAccessDatabase()))
    }

}


fun writeSession(session : Session, context : Context) : AppEff<Long>
{
    val db = DatabaseManager(context).writableDatabase

    return if (db != null)
    {
        val sessionBlob = SerializationUtils.serialize(session)

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(Schema.SessionTable.COLUMN_NAME_SESSION_ID, session.sessionId.value.toString())
            put(Schema.SessionTable.COLUMN_NAME_SESSION_LAST_USED, System.currentTimeMillis())
            put(Schema.SessionTable.COLUMN_NAME_SESSION_LOADER_BLOB, sessionBlob)
        }

        val startNS = System.nanoTime()

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(Schema.SessionTable.TABLE_NAME, null, values)

        Log.d("***DATABASE", "save session: new row id: $newRowId")

        val endNS = System.nanoTime()

        ApplicationLog.event(AppDBEvent(DatabaseSessionSaved(session.sessionId, (endNS - startNS))))

        effValue(newRowId)
    }
    else
    {
        effError(AppDBError(CouldNotAccessDatabase()))
    }
}


fun readSessionList(context : Context) : List<Session>
{

    val db = DatabaseManager(context).readableDatabase

    val projection = arrayOf(BaseColumns._ID,
                             Schema.SessionTable.COLUMN_NAME_SESSION_ID,
                             Schema.SessionTable.COLUMN_NAME_SESSION_LAST_USED,
                             Schema.SessionTable.COLUMN_NAME_SESSION_LOADER_BLOB)

    val sortOrder = "datetime(${Schema.SessionTable.COLUMN_NAME_SESSION_LAST_USED}) DESC"

    val cursor = db.query(
            Schema.SessionTable.TABLE_NAME, // The table to query
            projection,                     // The array of columns to return (pass null to get all)
            null,                           // The columns for the WHERE clause
            arrayOf(),                      // The values for the WHERE clause
            null,                           // don't group the rows
            null,                           // don't filter by row groups
            sortOrder)                      // The sort order


    Log.d("***DATABASE", "cursor: $cursor")


    val records : MutableList<Session> = mutableListOf()

    with(cursor) {
        while (moveToNext()) {

            // val sessionId = SessionId.fromString(getString(getColumnIndexOrThrow(Schema.SessionTable.COLUMN_NAME_SESSION_ID)))
            // val sessionName = SessionName(getString(getColumnIndexOrThrow(Schema.SessionTable.COLUMN_NAME_SESSION_NAME)))
            // val sessionTagline = getString(getColumnIndexOrThrow(Schema.SessionTable.COLUMN_NAME_SESSION_TAGLINE))
            // val sessionDescription = SessionDescription(getString(getColumnIndexOrThrow(Schema.SessionTable.COLUMN_NAME_SESSION_DESCRIPTION)))

            val lastUsed = Calendar.getInstance()
            lastUsed.timeInMillis = getLong(getColumnIndexOrThrow(Schema.SessionTable.COLUMN_NAME_SESSION_LAST_USED))

            val session = SerializationUtils.deserialize<Session>(
                             getBlob(getColumnIndexOrThrow(Schema.SessionTable.COLUMN_NAME_SESSION_LOADER_BLOB)))

            // Log.d("***DATABASE", "found session: $sessionName")

            records.add(session)
        }
    }

    return records
}


fun saveCurrentSession(context : Context)
{
    val maybeSession = activeSession()
    when (maybeSession) {
        is Just -> {
            writeSession(maybeSession.value, context)
        }
    }
}

