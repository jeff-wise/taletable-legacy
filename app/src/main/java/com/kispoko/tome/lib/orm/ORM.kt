
package com.kispoko.tome.lib.orm


import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.lib.orm.sql.query.UpsertQuery
import com.kispoko.tome.util.Util
import effect.Eff
import effect.Identity
import java.util.*


typealias ORMEff<A> = Eff<ORMError,Identity,A>


data class CollectionData(val tableName : String, val fieldName : String)



object ORM
{


    fun saveModel(model : Model,
                  collectionData : CollectionData?,
                  isRecursive : Boolean,
                  isTransaction : Boolean)
    {
        val startTime = System.nanoTime()

        try
        {
            if (isTransaction)
            {
                DatabaseManager.database().beginTransaction()
                ORMLog.event(BeginTranscation(model.name))
            }

            // (1) Save the model's row.
            // -------------------------------------------------------------------------------------

            Schema.defineTable(model)

            // (1) Save the model's row.
            // -------------------------------------------------------------------------------------

            insertModelRow(model, collectionData)

            // (2) [IF RECURSIVE] Save the model's child model rows.
            // -------------------------------------------------------------------------------------

            if (isRecursive)
                insertModelChildRows(model, isRecursive)

            // (3) Finish.
            // -------------------------------------------------------------------------------------

            if (isTransaction)
                DatabaseManager.database().setTransactionSuccessful()

            val endTime = System.nanoTime()

            ORMLog.event(ModelSaved(model, (endTime - startTime)))
        }
        finally
        {
            if (isTransaction) {
                DatabaseManager.database().endTransaction()
                ORMLog.event(EndTranscation(model.name))
            }
        }
    }


    private fun insertModelRow(model : Model, collectionData : CollectionData?)
    {
        val row = ContentValues()

        val startTime = System.nanoTime()

        // (1) Collect Row Data
        // -------------------------------------------------------------------------------------

        // () Save Id
        row.put("_id", model.id.toString())

        // (2) Save Primitive Values
        ORM.modelValueRelations(model).forEach {
            insertPrimitiveRowValue(it, row)
        }

        // (3) Save Foreign Keys (One-to-One)
        ORM.modelOneToOneRelations(model).forEach {
            insertProductRowValue(it, row)
        }

        val endTime = System.nanoTime()

        Log.d("***ORM", "insert model row '${model.name}' in " + Util.timeDifferenceString(startTime, endTime))

        // (2) Insert Row Data
        // -------------------------------------------------------------------------------------

        val upsertQuery = UpsertQuery(model.name, model.id, row)

        upsertQuery.run()

    }


    private fun insertModelChildRows(model : Model, recursive : Boolean)
    {
        // Save Child Models (One-to-One)
        Model.functors(model).forEach {
            when (it) {
                is Comp<*> -> it.save(recursive)
            }
        }

        // Save Collections (One-to-Many)
        Model.functors(model).forEach {
            when (it) {
                is Coll<*>  -> it.save(recursive)
                is CollS<*> -> it.save(recursive)
            }
        }
//            parentRelations.add(new OneToManyRelation(ORM.name(model),
//                                                      collectionFunctor.name(),
//                                                      model.getId()));
//            collectionFunctor.save(parentRelations);
//        }
    }




    private fun insertPrimitiveRowValue(valueRelation : ValueRelation, row : ContentValues)
    {
        val columnName = SQL.validIdentifier(valueRelation.name)
        val sqlValue = valueRelation.value

        when (sqlValue)
        {
            is SQLInt  -> row.put(columnName, sqlValue.value())
            is SQLReal -> row.put(columnName, sqlValue.value())
            is SQLReal -> row.put(columnName, sqlValue.value())
            is SQLBlob -> row.put(columnName, sqlValue.value())
            is SQLNull -> row.putNull(columnName)
        }
    }


    private fun insertProductRowValue(oneToOneRelation : OneToOneRelation, row : ContentValues)
    {
        val columnName = SQL.validIdentifier(oneToOneRelation.name)
        row.put(columnName, oneToOneRelation.childRowId.toString())
    }


    // -----------------------------------------------------------------------------------------
    // RELATIONS
    // -----------------------------------------------------------------------------------------


    data class ValueRelation(val name : String,
                             val value : SQLValue)


    data class OneToOneRelation(val name : String,
                                val childTableName : String,
                                val childRowId : UUID)


    data class OneToManyRelation(val oneTableName : String,
                                 val oneFieldName : String,
                                 val manyTableName : String)


    fun modelValueRelations(model : Model) : List<ValueRelation> =
        Model.functors(model).mapNotNull {
            when (it)
            {
                is Prim<*> ->
                {
                    val funcName = it.name
                    if (funcName != null) {
                        ValueRelation(funcName, it.asSQLValue())
                    }
                    else {
                        ORMLog.event(FunctorIsMissingNameField(model.name))
                        null
                    }
                }
                else -> null
            }
        }


    fun modelOneToOneRelations(model : Model) : List<OneToOneRelation> =
        Model.functors(model).mapNotNull {
            when (it)
            {
                is Comp<*> ->
                {
                    val funcName = it.name
                    if (funcName != null) {
                        OneToOneRelation(funcName, it.value.name, it.value.id)
                    }
                    else {
                        ORMLog.event(FunctorIsMissingNameField(model.name))
                        null
                    }
                }
                else -> null
            }
        }


    fun modelOneToManyRelations(model : Model) : List<OneToManyRelation>
    {
        fun functorRelations(func : Func<*>) : List<OneToManyRelation>
        {
            val collName = func.name
            if (collName == null) {
                ORMLog.event(FunctorIsMissingNameField(model::class.qualifiedName ?: "unknown"))
                return listOf()
            }

            val collTableNames = mutableSetOf<String>()
            when (func)
            {
                is Coll<*>  -> func.list.mapTo(collTableNames) { it.name }
                is CollS<*> -> func.list.mapTo(collTableNames) { it.name }
            }

            return collTableNames.map {
                OneToManyRelation(ORM.modelTableName(model), it, collName)
            }
        }

        return Model.functors(model).flatMap { functorRelations(it) }
    }


    /**
     * The model's SQL table name.
     */
    fun <A : Model> modelTableName(model : A) : String = SQL.validIdentifier(model.name)


    fun collectionColumnName(tableName : String, fieldName : String) : String =
        "parent_" + fieldName + "_" + tableName + "_id"


}




object ORMLog
{

    var logLevel : ORMLogLevel = ORMLogLevel.DEBUG


    fun event(event : ORMEvent)
    {
        when(event)
        {
            is FunctorIsMissingNameField ->
            {
                if (logLevel >= ORMLogLevel.VERBOSE)
                    Log.d("***ORM EVENT", event.prettyEventMessage())
            }
            is FunctorIsMissingValueClassField ->
            {
                if (logLevel >= ORMLogLevel.VERBOSE)
                    Log.d("***ORM EVENT", event.prettyEventMessage())
            }
            is GeneratedTableDefinition ->
            {
                if (logLevel >= ORMLogLevel.DEBUG)
                    Log.d("***ORM EVENT", event.prettyEventMessage())
            }
            is ModelSaved ->
            {
                if (logLevel >= ORMLogLevel.VERBOSE)
                    Log.d("***ORM EVENT", event.prettyEventMessage())
            }
            is RowInsert ->
            {
                if (logLevel >= ORMLogLevel.DEBUG)
                    Log.d("***ORM EVENT", event.prettyEventMessage())
            }
            is DefineTable ->
            {
                if (logLevel >= ORMLogLevel.DEBUG)
                    Log.d("***ORM EVENT", event.prettyEventMessage())
            }
            is BeginTranscation ->
            {
                if (logLevel >= ORMLogLevel.NORMAL)
                    Log.d("***ORM EVENT", event.prettyEventMessage())
            }
            is EndTranscation ->
            {
                if (logLevel >= ORMLogLevel.NORMAL)
                    Log.d("***ORM EVENT", event.prettyEventMessage())
            }
            is OpeningDatabase ->
            {
                if (logLevel >= ORMLogLevel.NORMAL)
                    Log.d("***ORM EVENT", event.prettyEventMessage())
            }
        }
    }


}


enum class ORMLogLevel
{
    NORMAL,
    VERBOSE,
    DEBUG
}


object DatabaseManager
{

    private var database  : SQLiteDatabase? = null


    fun database() : SQLiteDatabase
    {
        val currentDB = this.database
        if (currentDB != null)
            return currentDB

        return this.openDatabase()
    }


    fun openDatabase() : SQLiteDatabase
    {
        val db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.kispoko.tome/main.db", null)
        this.database = db
        ORMLog.event(OpeningDatabase())
        return db
    }

}
