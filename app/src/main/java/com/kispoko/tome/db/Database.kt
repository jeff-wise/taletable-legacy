
package com.kispoko.tome.db


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns



object Schema
{

    // Table contents are grouped together in an anonymous object.
    object EntityTable : BaseColumns
    {
        const val TABLE_NAME                = "entity"
        const val COLUMN_NAME_NAME          = "name"
        const val COLUMN_NAME_ENTITY_TYPE   = "entity_type"
        const val COLUMN_NAME_FILE          = "file"
    }

    object EntityUpdateTable : BaseColumns
    {
        const val TABLE_NAME                = "entity_update"
        const val COLUMN_NAME_ENTITY_ID     = "entity_id"
        const val COLUMN_NAME_UPDATE        = "update"
    }

}


private val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${Schema.EntityTable.TABLE_NAME} (" +
        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
        "${Schema.EntityTable.COLUMN_NAME_NAME} TEXT," +
        "${Schema.EntityTable.COLUMN_NAME_ENTITY_TYPE} TEXT," +
        "${Schema.EntityTable.COLUMN_NAME_FILE} BLOB);" +

    "CREATE TABLE ${Schema.EntityUpdateTable.TABLE_NAME} (" +
        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
        "${Schema.EntityUpdateTable.COLUMN_NAME_ENTITY_ID} TEXT," +
        "${Schema.EntityUpdateTable.COLUMN_NAME_UPDATE} BLOB);" +

    "CREATE INDEX entity_id_index ON ${Schema.EntityUpdateTable.TABLE_NAME}(${Schema.EntityUpdateTable.COLUMN_NAME_ENTITY_ID})"


private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${Schema.EntityTable.TABLE_NAME}"



class Database(context: Context)
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


//fun saveSheet(sheetId : SheetId) : AppEff<Int>
//{
//    entitySheetRecord(sheetId) apDo {
//
//    }
//}
