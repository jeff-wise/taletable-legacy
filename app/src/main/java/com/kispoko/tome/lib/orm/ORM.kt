
package com.kispoko.tome.lib.orm


import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabaseLockedException
import android.util.Log
import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.lib.orm.sql.query.UpdateQuery
import com.kispoko.tome.lib.orm.sql.query.UpsertQuery
import effect.Just
import effect.Val
import lulo.schema.Prim
import lulo.schema.Sum


fun savePrim(sqlValue : SQLValue, columnName : String, prodType : ProdType)
{

    DatabaseManager.database().beginTransaction()
    try
    {
        val startTime = System.nanoTime()
        ORMLog.event(BeginTranscation(prodType.row().name))

        Schema.defineTable(prodType, listOf())

        val updateQuery = UpdateQuery(prodType.row().tableName(),
                                      columnName,
                                      prodType.id,
                                      sqlValue)
        updateQuery.run()

        DatabaseManager.database().setTransactionSuccessful()
        val endTime = System.nanoTime()

    }
    finally
    {
        DatabaseManager.database().endTransaction()
        ORMLog.event(EndTranscation(prodType.row().name))
    }

}


fun saveProdType(prodType : ProdType,
                 oneToManyRelationRows : List<OneToManyRelationRow>,
                 isRecursive : Boolean,
                 isTransaction : Boolean)
{
    val startTime = System.nanoTime()

    if (isTransaction)
    {
        // TODO need to run separate transactions for each model, but need to do it in single thread?
        // I don't currently understand why I'm getting an error without the try, though it make sense.
        // Need to find a better way to resolve this.
        try {
            DatabaseManager.database().beginTransaction()
            ORMLog.event(BeginTranscation(prodType.row().name))
        }
        catch (e : SQLiteDatabaseLockedException) {
            Log.d("***ORM", "sqlite db locked exception")
        }
    }

    // TODO what if transaction doesn't start. need to figure out what's going on
    // here on real device

    try
    {

        // (1) Make sure the prodType's table is defined.
        // ---------------------------------------------------------------------------------

        Schema.defineTable(prodType, oneToManyRelationRows.map { it.oneToManyRelation() })

        // (2) Save the prodType's row.
        // ---------------------------------------------------------------------------------

        insertModelRow(prodType, oneToManyRelationRows)

        // (3) [IF RECURSIVE] Save the prodType's child prodType rows.
        // ---------------------------------------------------------------------------------

        if (isRecursive)
            insertModelChildRows(prodType, isRecursive)

        // (4) Finish.
        // ---------------------------------------------------------------------------------

        if (isTransaction)
            DatabaseManager.database().setTransactionSuccessful()

        val endTime = System.nanoTime()

        ORMLog.event(ModelSaved(prodType, (endTime - startTime)))
    }
    finally
    {
        if (isTransaction) {
            DatabaseManager.database().endTransaction()
            ORMLog.event(EndTranscation(prodType.row().name))
        }
    }
}


fun insertModelRow(prodType : ProdType, oneToManyRelationRows : List<OneToManyRelationRow>)
{
    val contentValues = ContentValues()

    val row = prodType.row()

    // (1) Collect Row Data
    // -------------------------------------------------------------------------------------

    // (A) Save Id
    contentValues.put("_id", prodType.id.toString())

    // (B) Save Primitive Values
    modelValueRelations(prodType).forEach {
        insertPrimitiveRowValue(it, contentValues)
    }

    // (C) Save Foreign Keys (One-to-One)
    modelOneToOneRelations(prodType).forEach {
        insertProductRowValue(it, contentValues)
    }

    // (D) Save Foreign Keys (One-to-Many)
    oneToManyRelationRows.forEach {
        insertCollValue(it, contentValues)
    }

    // (E) Save Sum Types (Primitive or One-to-One)
    sumRelations(prodType).forEach {
        when (it.relation) {
            is ValueRelation -> {
                insertPrimitiveRowValue(it.relation, contentValues)
                insertSumTypeRowValue(typeColumnName(it.relation.name), it.path, contentValues)
            }
            is OneToOneRelation -> {
                insertProductRowValue(it.relation, contentValues)
                insertSumTypeRowValue(typeColumnName(it.relation.name), it.path, contentValues)
            }
            else -> {
                ORMLog.event(SumFunctorNotSupported(prodType.row().name))
            }
        }
    }

    // (2) Insert Row Data
    // -------------------------------------------------------------------------------------

    val upsertQuery = UpsertQuery(row.tableName(), prodType.id, contentValues)

    upsertQuery.run()
}


private fun insertModelChildRows(prodType : ProdType, recursive : Boolean)
{
    // Save Child Models (One-to-One)
    prodType.row().columns().forEach { column ->
        when (column.value) {
            is Prod<*> -> column.value.save(recursive)
        }
    }

    // Save Collections (One-to-Many)
    oneToManyRelations(prodType).forEach {
        it.first.save(recursive, it.second)
    }
}


private fun insertPrimitiveRowValue(valueRelation : ValueRelation, row : ContentValues)
{
    val columnName = valueRelation.columnName()
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


private fun insertCollValue(oneToManyRelationRow : OneToManyRelationRow, row : ContentValues)
{
    row.put(oneToManyRelationRow.columnName(), oneToManyRelationRow.oneRowId.toString())
}


private fun insertSumTypeRowValue(columnName : String, path : String, row : ContentValues)
{
    row.put(columnName, path)
}



private fun modelValueRelations(prodType : ProdType) : List<ValueRelation> =
    prodType.row().columns().mapNotNull {
        when (it.value) {
            is Prim<*> -> ValueRelation(it.columnName(), it.value.asSQLValue())
            is MaybePrim<*> -> {
                val maybeValue = it.value.value
                when (maybeValue) {
                    is Just -> ValueRelation(it.columnName(), maybeValue.value.asSQLValue())
//                    else    -> ValueRelation(it.columnName(), SQLNull)
                    else    -> null
                }
            }
            else            -> null
        }
    }


private fun modelOneToOneRelations(prodType : ProdType) : List<OneToOneRelation> =
    prodType.row().columns().mapNotNull {
        when (it.value)
        {
            is Prod<*> -> OneToOneRelation(it.name, it.value.value.id)
            else       -> null
        }
    }


private fun oneToManyRelations(prodType : ProdType) : List<Pair<Coll<*>, OneToManyRelationRow>>
{
    fun funcRelations(column : Col<*>) : Pair<Coll<*>, OneToManyRelationRow>? = when (column.value)
    {
        is Coll<*> -> {
            val rel = OneToManyRelationRow(prodType.row().name, column.name, prodType.id)
            Pair(column.value, rel)
        }
        else       -> null
    }

    return prodType.row().columns().mapNotNull { funcRelations(it) }
}


// -----------------------------------------------------------------------------------------
// SUM RELATIONS
// -----------------------------------------------------------------------------------------

private fun resolveFunctor(functor : Val<*>,
                           path : List<String>) : Pair<String, Val<*>> = when (functor)
{
    is Sum<*> -> resolveFunctor(functor.value.functor(),
                                path.plus(functor.value.case()))
    else      -> Pair(path.joinToString(":"), functor)
}


//private fun resolveFunctor(functor : Val<*>, case : String) : Pair<String, Val<*>> =
//        _resolveFunctor(functor, listOf(case))


private fun sumRelations(prodType: ProdType) : List<SumRelation> =
    prodType.row().columns().mapNotNull {
        when (it.value)
        {
            is Sum<*> ->
            {
                val result = resolveFunctor(it.value.value.functor(), listOf(it.value.value.case()))
                val (path, functor) = result
                when (functor)
                {
                    is Prim<*> -> SumRelation(ValueRelation(it.columnName(), functor.asSQLValue()), path)
                    is Prod<*> -> SumRelation(OneToOneRelation(it.columnName(), functor.value.id), path)
                    else       -> null
                }
            }
            else      -> null
        }
    }



object ORMLog
{

    var logLevel : ORMLogLevel = ORMLogLevel.NORMAL


    // STATISTICS
    var transactionInserts = 0
    var timeSpentCreatingSchemaObjectsNS : Long = 0
    var transactionStartTime : Long = 0


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
                if (logLevel >= ORMLogLevel.NORMAL)
                    Log.d("***ORM EVENT", event.prettyEventMessage())

                timeSpentCreatingSchemaObjectsNS += event.duration
            }
            is ColumnAdded ->
            {
                if (logLevel >= ORMLogLevel.NORMAL)
                    Log.d("***ORM EVENT", event.prettyEventMessage())
            }
            is BeginTranscation ->
            {
                if (logLevel >= ORMLogLevel.NORMAL) {
                    // TODO track multiple transactions
                    transactionInserts = 0
                    timeSpentCreatingSchemaObjectsNS = 0
                    transactionStartTime = System.nanoTime()
                    Log.d("***ORM EVENT", event.prettyEventMessage())
                }
            }
            is EndTranscation ->
            {
                if (logLevel >= ORMLogLevel.NORMAL) {
                    event.totalRowInserts = transactionInserts
                    event.timeSpentCreatingSchemaObjectsNS = timeSpentCreatingSchemaObjectsNS
                    event.totalDurationNS = (System.nanoTime() - transactionStartTime)
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
    DEBUG,
    EVERYTHING
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

