
package com.kispoko.tome.lib.orm


import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.lib.orm.sql.query.UpsertQuery
import effect.Eff
import effect.Identity
import java.util.*



typealias ORMEff<A> = Eff<ORMError,Identity,A>





object ORM
{


    fun saveModel(model : Model,
                  oneToManyRelations : Set<OneToManyRelation>,
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
            // ---------------------------------------------------------------------------------

            Schema.defineTable(model)

            // (1) Save the model's row.
            // ---------------------------------------------------------------------------------

            insertModelRow(model, oneToManyRelations)

            // (2) [IF RECURSIVE] Save the model's child model rows.
            // ---------------------------------------------------------------------------------

            if (isRecursive)
                insertModelChildRows(model, isRecursive)

            // (3) Finish.
            // ---------------------------------------------------------------------------------

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


    private fun insertModelRow(model : Model, oneToManyRelations : Set<OneToManyRelation>)
    {
        val row = ContentValues()

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

        // (4) Save Foreign Keys (One-to-Many)
        oneToManyRelations.forEach {
            insertCollValue(it, row)
        }

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
        oneToManyRelations(model).forEach {
            it.first.save(recursive, it.second)
        }

    }




    private fun insertPrimitiveRowValue(valueRelation : ValueRelation, row : ContentValues)
    {
        val columnName = SQL.validIdentifier(valueRelation.name)
        val sqlValue = valueRelation.value

        when (sqlValue)
        {
            is SQLInt  -> row.put(columnName, sqlValue.value())
            is SQLReal -> row.put(columnName, sqlValue.value())
            is SQLText -> row.put(columnName, sqlValue.value())
            is SQLBlob -> row.put(columnName, sqlValue.value())
            is SQLNull -> row.putNull(columnName)
        }
    }


    private fun insertProductRowValue(oneToOneRelation : OneToOneRelation, row : ContentValues)
    {
        val columnName = SQL.validIdentifier(oneToOneRelation.name)
        row.put(columnName, oneToOneRelation.childRowId.toString())
    }


    private fun insertCollValue(oneToManyRelation : OneToManyRelation, row : ContentValues)
    {
        val columnName = ORM.collectionColumnName(oneToManyRelation)

        row.put(columnName, oneToManyRelation.oneRowId.toString())
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
                                 val oneRowId : UUID)


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


    fun oneToManyRelations(model : Model) : List<Pair<CollFunc, OneToManyRelation>>
    {
        fun funcRelations(func : Func<*>) : Pair<CollFunc, OneToManyRelation>?
        {
            val collName = func.name
            if (collName == null) {
                ORMLog.event(FunctorIsMissingNameField(model.name))
                return null
            }

            when (func)
            {
                is Coll<*>  ->
                {
                    val rel = OneToManyRelation(ORM.modelTableName(model), collName, model.id)
                    return Pair(func, rel)
                }
                is CollS<*> ->
                {
                    val rel = OneToManyRelation(ORM.modelTableName(model), collName, model.id)
                    return Pair(func, rel)
                }
            }

            return null
        }

        return Model.functors(model).mapNotNull { funcRelations(it) }
    }


    /**
     * The model's SQL table name.
     */
    fun <A : Model> modelTableName(model : A) : String = SQL.validIdentifier(model.name)


    fun collectionColumnName(tableName : String, fieldName : String) : String =
        "parent_" + fieldName + "_" + tableName + "_id"


    fun collectionColumnName(oneToManyRelation : OneToManyRelation) : String =
            "parent_" + oneToManyRelation.oneFieldName + "_" +
                        oneToManyRelation.oneTableName + "_id"


}




object ORMLog
{

    var logLevel : ORMLogLevel = ORMLogLevel.DEBUG


    var transactionInserts = 0


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
            is ModelSaved ->
            {
                if (logLevel >= ORMLogLevel.VERBOSE)
                    Log.d("***ORM EVENT", event.prettyEventMessage())
            }
            is RowInsert ->
            {
                if (logLevel >= ORMLogLevel.DEBUG)
                    Log.d("***ORM EVENT", event.prettyEventMessage())

                transactionInserts += 1
            }
            is DefineTable ->
            {
                if (logLevel >= ORMLogLevel.DEBUG)
                    Log.d("***ORM EVENT", event.prettyEventMessage())
            }
            is BeginTranscation ->
            {
                if (logLevel >= ORMLogLevel.NORMAL) {
                    // TODO track multiple transactions
                    transactionInserts = 0
                    Log.d("***ORM EVENT", event.prettyEventMessage())
                }
            }
            is EndTranscation ->
            {
                if (logLevel >= ORMLogLevel.NORMAL) {
                    event.totalRowInserts = transactionInserts
                    Log.d("***ORM EVENT", event.prettyEventMessage())
                }
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
