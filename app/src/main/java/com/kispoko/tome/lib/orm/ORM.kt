
package com.kispoko.tome.lib.orm


import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabaseLockedException
import android.util.Log
import com.kispoko.tome.lib.orm.schema.*
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.lib.orm.sql.query.UpdateQuery
import com.kispoko.tome.lib.orm.sql.query.UpsertQuery
import effect.Just


fun savePrim(sqlValue : SQLValue, columnName : String, prodType : ProdType)
{

    val tableName = prodType.rowValue().table().tableName

    DatabaseManager.database().beginTransaction()
    try
    {
        val startTime = System.nanoTime()
        ORMLog.event(BeginTranscation(tableName))

        Schema.defineTable(prodType, listOf())

        val updateQuery = UpdateQuery(tableName,
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
        ORMLog.event(EndTranscation(tableName))
    }

}


fun saveProdType(prodType : ProdType,
                 oneToManyRelationRows : List<OneToManyRelationRow>,
                 isRecursive : Boolean,
                 isTransaction : Boolean)
{
    val startTime = System.nanoTime()

    val tableName = prodType.rowValue().table().tableName

    if (isTransaction)
    {
        // TODO need to run separate transactions for each model, but need to do it in single thread?
        // I don't currently understand why I'm getting an error without the try, though it make sense.
        // Need to find a better way to resolve this.
        try {
            DatabaseManager.database().beginTransaction()
            ORMLog.event(BeginTranscation(tableName))
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
            ORMLog.event(EndTranscation(tableName))
        }
    }
}


fun insertModelRow(prodType : ProdType, oneToManyRelationRows : List<OneToManyRelationRow>)
{
    val contentValues = ContentValues()

    val tableName = prodType.rowValue().table().tableName

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
            is OneToOneRelationRow -> {
                insertProductRowValue(it.relation, contentValues)
                insertSumTypeRowValue(typeColumnName(it.relation.name), it.path, contentValues)
            }
            else -> {
                ORMLog.event(SumFunctorNotSupported(tableName))
            }
        }
    }

    // (2) Insert Row Data
    // -------------------------------------------------------------------------------------

    val upsertQuery = UpsertQuery(tableName, prodType.id, contentValues)

    upsertQuery.run()
}


private fun insertModelChildRows(prodType : ProdType, recursive : Boolean)
{
    // Save Child Models (One-to-One)
    for ((_, columnValue) in prodType.rowValue().columns()) {
        when (columnValue) {
            is ProdValue<*> -> saveProdType(columnValue.product, listOf(), true, false)
        }
    }

    // Save Collections (One-to-Many)
    oneToManyRelations(prodType).forEach { (collValue, relation) ->
        collValue.list.forEach {
            saveProdType(it, listOf(relation), true, false)
        }
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


private fun insertProductRowValue(oneToOneRelation : OneToOneRelationRow, row : ContentValues)
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
    prodType.rowValue().columns().mapNotNull { (columnName, columnValue) ->
        when (columnValue) {
            is PrimValue<*> -> ValueRelation(columnName, columnValue.prim.asSQLValue())
            is MaybePrimValue<*> -> {
                val maybeValue = columnValue.maybePrim
                when (maybeValue) {
                    is Just -> ValueRelation(columnName, maybeValue.value.asSQLValue())
//                    else    -> ValueRelation(it.columnName(), SQLNull)
                    else    -> null
                }
            }
            else            -> null
        }
    }


private fun modelOneToOneRelations(prodType : ProdType) : List<OneToOneRelationRow> =
    prodType.rowValue().columns().mapNotNull { (columnName, columnValue) ->
        when (columnValue)
        {
            is ProdValue<*> -> OneToOneRelationRow(prodType.rowValue().table().tableName,
                                                   columnValue.product.id)
            else            -> null
        }
    }


private fun oneToManyRelations(prodType : ProdType)
                : List<Pair<CollValue<*>, OneToManyRelationRow>> =
    prodType.rowValue().columns().mapNotNull { (columnName, columnValue) ->
        when (columnValue)
        {
            is CollValue<*> -> {
                val rel = OneToManyRelationRow(prodType.rowValue().table().tableName,
                                               columnName,
                                               prodType.id)
                Pair(columnValue, rel)
            }
            else       -> null
        }
    }


// -----------------------------------------------------------------------------------------
// SUM RELATIONS
// -----------------------------------------------------------------------------------------

private fun resolveColumnValue(columnValue : ColumnValue,
                               path : List<String>) : Pair<String, ColumnValue> =
    when (columnValue)
{
    is SumValue<*> -> resolveColumnValue(columnValue.sum.columnValue(),
                                         path.plus(columnValue.sum.case()))
    else           -> Pair(path.joinToString(":"), columnValue)
}


//private fun resolveFunctor(functor : Val<*>, case : String) : Pair<String, Val<*>> =
//        _resolveFunctor(functor, listOf(case))


private fun sumRelations(prodType : ProdType) : List<SumRelation> =
    prodType.rowValue().columns().mapNotNull { (columnName, columnValue) ->
        when (columnValue)
        {
            is SumValue<*> ->
            {
                val result = resolveColumnValue(columnValue.sum.columnValue(),
                                                listOf(columnValue.sum.case()))
                val (path, resolvedColumnValue) = result
                when (resolvedColumnValue)
                {
                    is PrimValue<*> -> {
                        val relationRow = ValueRelation(columnName,
                                                        resolvedColumnValue.prim.asSQLValue())
                        SumRelation(relationRow, path)
                    }
                    is ProdValue<*> -> {
                        val relationRow = OneToOneRelationRow(columnName,
                                                              resolvedColumnValue.product.id)
                        SumRelation(relationRow, path)
                    }
                    else            -> null
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

