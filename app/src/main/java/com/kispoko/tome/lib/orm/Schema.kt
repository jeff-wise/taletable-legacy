
package com.kispoko.tome.lib.orm


import android.util.Log
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQL
import com.kispoko.tome.lib.orm.sql.SQL_TEXT_TYPE_STRING



object Schema
{


    data class Table(val name : String, val columnNames : MutableSet<String>)

    val tables : MutableMap<String,Table> = mutableMapOf()


    fun defineTable(model : Model, collectionData : CollectionData? = null)
    {
        if (tables.containsKey(model.name))
            return

        val startTime = System.nanoTime()

        val tableDefinition = this.tableDefinitionSQLString(model, collectionData)
        DatabaseManager.database().execSQL(tableDefinition.definitionSQLString)

        this.tables.put(model.name, Table(model.name, tableDefinition.columnNames.toMutableSet()))

        val endTime = System.nanoTime()

        ORMLog.event(DefineTable(model.name, (endTime-startTime)))


        Model.functors(model).forEach { func ->
            when (func)
            {
                is Comp<*>  -> Schema.defineTable(func.value)
                is Coll<*>  -> func.list.forEach { modelItem ->
                    val funcName = func.name
                    if (funcName != null)
                        Schema.defineTable(modelItem, CollectionData(model.name, funcName))
                }
                is CollS<*> -> func.list.forEach { modelItem ->
                    val funcName = func.name
                    if (funcName != null)
                        Schema.defineTable(modelItem, CollectionData(model.name, funcName))
                }
            }
        }

    }



    // -----------------------------------------------------------------------------------------
    // QUERIES
    // -----------------------------------------------------------------------------------------


    data class TableDefinition(val columnNames : Set<String>, val definitionSQLString : String)


    fun <A : Model> tableDefinitionSQLString(model : A, collectionData : CollectionData?)
                    : TableDefinition
    {
        val columnNames : MutableSet<String> = mutableSetOf()
        val queryBuilder = StringBuilder()

        // (1) Create Table
        // --------------------------------------------------------------------------------------
        queryBuilder.append("CREATE TABLE IF NOT EXISTS ")
        queryBuilder.append(ORM.modelTableName(model))
        queryBuilder.append(" ( ")

        // (2) Id Column
        // --------------------------------------------------------------------------------------

        queryBuilder.append("_id")
        queryBuilder.append(" ")
        queryBuilder.append(SQL_TEXT_TYPE_STRING)
        queryBuilder.append(" PRIMARY KEY")

        // (3) Primitive & Foreign Key Columns
        // --------------------------------------------------------------------------------------

        val functors = Model.functors(model)

        for (functor in functors)
        {
            when (functor)
            {
                is Prim ->
                {
                    val columnName = functor.name
                    if (columnName != null)
                    {
                        val validColumnName = SQL.validIdentifier(columnName)
                        columnNames.add(validColumnName)
                        val columnType = functor.asSQLValue().type().name

                        queryBuilder.append(", ")
                        queryBuilder.append(validColumnName)
                        queryBuilder.append(" ")
                        queryBuilder.append(columnType)
                    }

                }
                is Comp ->
                {
                    val columnName = functor.name
                    if (columnName != null)
                    {
                        val validColumnName = SQL.validIdentifier(columnName)
                        columnNames.add(validColumnName)

                        queryBuilder.append(", ")
                        queryBuilder.append(validColumnName)
                        queryBuilder.append(" ")
                        queryBuilder.append(SQL_TEXT_TYPE_STRING)
                    }
                }
            }

        }

        // (4) One To Many ID Columns
        // --------------------------------------------------------------------------------------

        if (collectionData != null)
        {
            val columnName = ORM.collectionColumnName(collectionData.tableName,
                                                      collectionData.fieldName)

            queryBuilder.append(", ")
            queryBuilder.append(columnName)
            queryBuilder.append(" ")
            queryBuilder.append(SQL_TEXT_TYPE_STRING)
        }

        // (5) End
        // --------------------------------------------------------------------------------------

        queryBuilder.append(" )")

        return TableDefinition(columnNames, queryBuilder.toString())
    }

}

