
package com.kispoko.tome.lib.orm


import com.kispoko.tome.lib.orm.schema.*
import com.kispoko.tome.lib.orm.sql.*
import maybe.Just
import java.util.*



object Schema
{


    // Name is a valid SQL identifier (i.e row.tableName() not row.name)
    data class Table(val name : String,
                     val columnNames : MutableSet<String>)
//                     ,
//                     val oneToManyRelations : MutableSet<CollectionData>)

    private val tables : MutableMap<String,Table> = mutableMapOf()


    // Represents a collection that the prodType belongs to. That collection is contained in another
    // prodType, but of course in the relation prodType, our collection lives in a separate table and
    // links to the parent.
//    data class CollectionData(val tableName : String,
//                              val fieldName : String)


    fun defineTable(prodType : ProdType, oneToManyRelations : List<OneToManyRelation>)
    {
        val rowValue = prodType.rowValue()
        val tableName = rowValue.table().tableName

        // Check if table is already defined and has correct columns
        if (this.tables.containsKey(tableName))
        {
            val tableColumnNames = tables[tableName]!!.columnNames

            rowValue.columns().forEach { (columnName, columnValue) ->
                when (columnValue) {
                    is PrimValue<*> -> {
                        if (!tableColumnNames.contains(columnName)) {
                            addPrimColumnToTable(tableName,
                                                 columnName,
                                                 columnValue.prim.asSQLValue().type())
                        }
                    }
                    is MaybePrimValue<*> -> {
                        val maybeValue = columnValue.maybePrim
                        when (maybeValue) {
                            is Just -> {
                                if (!tableColumnNames.contains(columnName)) {
                                    addPrimColumnToTable(tableName,
                                                         columnName,
                                                         maybeValue.value.asSQLValue().type())
                                }
                            }
                        }
                    }
                }
            }

            oneToManyRelations.forEach {
                if (!tableColumnNames.contains(it.columnName())) {
                    addCollectionColumnToTable(prodType, it)
                }
            }

            return
        }

        // CREATE new table
        val tableDefinition = tableDefinitionSQLString(prodType, oneToManyRelations)

        val startTime = System.nanoTime()

        DatabaseManager.database().execSQL(tableDefinition.definitionSQLString)

        val table = Table(tableName, tableDefinition.columnNames.toMutableSet())
        this.tables.put(tableName, table)

        val endTime = System.nanoTime()

        ORMLog.event(DefineTable(tableName,
                                 tableDefinition.definitionSQLString,
                                 (endTime-startTime)))

        // TODO This will need to be called beforehand when we add foreign key constraints
        rowValue.columns().forEach { (columnName, columnValue) ->
            when (columnValue)
            {
                is ProdValue<*> -> Schema.defineTable(columnValue.product, listOf())
                is CollValue<*> -> columnValue.list.forEach { itemValue ->
                    val relation = OneToManyRelation(tableName, columnName)
                    Schema.defineTable(itemValue, listOf(relation))
                }
            }
        }

    }


    // -----------------------------------------------------------------------------------------
    // QUERIES
    // -----------------------------------------------------------------------------------------


    data class TableDefinition(val columnNames : Set<String>, val definitionSQLString : String)


    fun <A : ProdType> tableDefinitionSQLString(prodType : A,
                                                                         oneToManyRelations : List<OneToManyRelation>)
                                                 : TableDefinition
    {
        val columnNames : MutableSet<String> = mutableSetOf()
        val query = StringBuilder()

        val rowValue = prodType.rowValue()

        // (1) Create Table
        // --------------------------------------------------------------------------------------
        query.append("CREATE TABLE IF NOT EXISTS ")
        query.append(rowValue.table().tableName)
        query.append(" ( ")

        // (2) Id Column
        // --------------------------------------------------------------------------------------

        query.append("_id")
        query.append(" ")
        query.append(SQL_TEXT_TYPE_STRING)
        query.append(" PRIMARY KEY")

        // (3) Primitive & Foreign Key Columns
        // --------------------------------------------------------------------------------------

        for ((columnName, columnValue) in rowValue.columns())
        {
            when (columnValue)
            {
                is PrimValue<*> ->
                {
                    columnNames.add(columnName)
                    val columnType = columnValue.prim.asSQLValue().type().name

                    query.append(", ")
                    query.append(columnName)
                    query.append(" ")
                    query.append(columnType)
                }
                is MaybePrimValue<*> ->
                {
                    val maybeValue = columnValue.maybePrim
                    when (maybeValue) {
                        is Just -> {
                            columnNames.add(columnName)
                            val columnType = maybeValue.value.asSQLValue().type().name

                            query.append(", ")
                            query.append(columnName)
                            query.append(" ")
                            query.append(columnType)
                        }
                    }
                }
                is ProdValue<*> ->
                {
                    columnNames.add(columnName)

                    query.append(", ")
                    query.append(columnName)
                    query.append(" ")
                    query.append(SQL_TEXT_TYPE_STRING)
                }
                is SumValue<*> ->
                {
                    columnNames.add(columnName)
                    val typeColumnName = "__type_$columnName"

                    query.append(", ")
                    query.append(columnName)
                    query.append(" ")
                    query.append(SQL_TEXT_TYPE_STRING)

                    query.append(", ")
                    query.append(typeColumnName)
                    query.append(" ")
                    query.append(SQL_TEXT_TYPE_STRING)
                }
            }
        }

        // (4) One To Many ID Columns
        // --------------------------------------------------------------------------------------

        oneToManyRelations.forEach {
            columnNames.add(it.columnName())

            query.append(", ")
            query.append(it.columnName())
            query.append(" ")
            query.append(SQL_TEXT_TYPE_STRING)
        }

        // (5) End
        // --------------------------------------------------------------------------------------

        query.append(" )")

        return TableDefinition(columnNames, query.toString())
    }


    fun <A : ProdType> addCollectionColumnToTable(prodType : A, oneToManyRelation : OneToManyRelation)
    {
        // Define the Query
        val tableName = prodType.rowValue().table().tableName
        val columnName = oneToManyRelation.columnName()

        val addColumnQuery = "ALTER TABLE $tableName ADD COLUMN $columnName $SQL_TEXT_TYPE_STRING"

        // Run the Query
        val startTime = System.nanoTime()

        DatabaseManager.database().execSQL(addColumnQuery)

        val endTime = System.nanoTime()

        ORMLog.event(ColumnAdded(tableName,
                                 columnName,
                                 SQL_TEXT_TYPE_STRING,
                                 addColumnQuery,
                                 (endTime-startTime)))

        tables[tableName]?.columnNames?.add(columnName)
    }


    fun addPrimColumnToTable(tableName : String, columnName : String, sqlValueType : SQLValueType)
    {
        val columnType = sqlValueType.name

        val addColumnQuery = "ALTER TABLE $tableName ADD COLUMN $columnName $columnType"

        // Run the Query
        val startTime = System.nanoTime()

        DatabaseManager.database().execSQL(addColumnQuery)

        val endTime = System.nanoTime()

        ORMLog.event(ColumnAdded(tableName,
                                 columnName,
                                 columnType,
                                 addColumnQuery,
                                 (endTime-startTime)))

        tables[tableName]?.columnNames?.add(columnName)
    }

}



/**
 * The prodType's SQL table name.
 */
//fun <A : ProdType> modelTableName(model : A) : String = SQL.validIdentifier(model.name)


fun collectionColumnName(tableName : String, fieldName : String) : String =
    "parent_" + fieldName + "_" + tableName + "_id"


fun typeColumnName(fieldName : String) = "__type_${SQL.validIdentifier(fieldName)}"


//fun collectionColumnName(relation : OneToManyRelation) : String =
//        SQL.validIdentifier("parent_${relation.oneTableName}_${relation.oneFieldName}_id")


// -----------------------------------------------------------------------------------------
// RELATIONS
// -----------------------------------------------------------------------------------------

sealed class RelationRow


data class ValueRelation(val name : String,
                         val value : SQLValue) : RelationRow()
{

    fun columnName() : String = SQL.validIdentifier(this.name)

}


/**
 * One-to-One Relation / Foreign Key
 */
data class OneToOneRelationRow(val name : String,
                               val childRowId : UUID) :  RelationRow()


// Table Name and Field Name should be the normal names, not the SQL-validated versions
data class OneToManyRelation(private val oneTableName : String,
                             private val oneFieldName : String)
{

    fun columnName() : String =
        SQL.validIdentifier("parent_${this.oneTableName}_${this.oneFieldName}_id")

}

data class OneToManyRelationRow(val oneTableName : String,
                                val oneFieldName : String,
                                val oneRowId : UUID) : RelationRow()
{

    fun columnName() : String =
            SQL.validIdentifier("parent_${this.oneTableName}_${this.oneFieldName}_id")


    fun oneToManyRelation() : OneToManyRelation =
            OneToManyRelation(this.oneTableName, this.oneFieldName)

}



data class SumRelation(val relation : RelationRow,
                       val path : String)




data class Column(val name : String, val value : ColumnValue)


sealed class RowValue()
{
    abstract fun table() : Table
    abstract fun columns() : List<Column>
}


class RowValue0<A> : RowValue()
        where A : ColumnValue
{

    override fun columns() = listOf<Column>()

    override fun table() = Table1("", "")

}



// ---------------------------------------------------------------------------------------------
// DB VALUE
// ---------------------------------------------------------------------------------------------

// DB VALUE 1
// ---------------------------------------------------------------------------------------------

data class RowValue1<A>(
        val table : Table1,
        val a : A)
         : RowValue()
        where A : ColumnValue
{

    override fun columns() = listOf(Column(table.column1Name, a))

    override fun table() = table

}


// ROW 2
// ---------------------------------------------------------------------------------------------

data class RowValue2<A,B>(
        val table : Table2,
        val a : A,
        val b : B)
         : RowValue()
        where A : ColumnValue, B : ColumnValue
{

    override fun columns() =
            listOf(Column(table.column1Name, a),
                   Column(table.column2Name, b))

    override fun table() = table

}

data class RowQuery2<A,B,C>(
        val table : Table3,
        val a : A,
        val b : B,
        val c : C)
        where A : ColumnQuery, B : ColumnQuery, C : ColumnQuery


// ROW 3
// ---------------------------------------------------------------------------------------------

data class RowValue3<A,B,C>(
        val table : Table3,
        val a : A,
        val b : B,
        val c : C)
         : RowValue()
        where A : ColumnValue, B : ColumnValue, C : ColumnValue
{

    override fun columns() =
            listOf(Column(table.column1Name, a),
                   Column(table.column2Name, b),
                   Column(table.column3Name, c))

    override fun table() = table

}

data class RowQuery3<A,B,C>(
        val table : Table3,
        val a : A,
        val b : B,
        val c : C)
        where A : ColumnQuery, B : ColumnQuery, C : ColumnQuery


// ROW 4
// ---------------------------------------------------------------------------------------------

data class RowValue4<A,B,C,D>(
        val table : Table4,
        val a : A,
        val b : B,
        val c : C,
        val d : D)
         : RowValue()
        where A : ColumnValue, B : ColumnValue, C : ColumnValue, D : ColumnValue
{

    override fun columns() =
        listOf(Column(table.column1Name, a),
               Column(table.column2Name, b),
               Column(table.column3Name, c),
               Column(table.column4Name, d))

    override fun table() = table

}


// ROW 5
// ---------------------------------------------------------------------------------------------

data class RowValue5<A,B,C,D,E>(
        val table : Table5,
        val a : A,
        val b : B,
        val c : C,
        val d : D,
        val e : E)
         : RowValue()
        where A : ColumnValue, B : ColumnValue, C : ColumnValue, D : ColumnValue, E : ColumnValue
{

    override fun columns() =
        listOf(Column(table.column1Name, a),
               Column(table.column2Name, b),
               Column(table.column3Name, c),
               Column(table.column4Name, d),
               Column(table.column5Name, e))

    override fun table() = table

}


// ROW 6
// ---------------------------------------------------------------------------------------------

data class RowValue6<A,B,C,D,E,F>(
        val table : Table6,
        val a : A,
        val b : B,
        val c : C,
        val d : D,
        val e : E,
        val f : F)
         : RowValue()
        where A : ColumnValue, B : ColumnValue, C : ColumnValue, D : ColumnValue, E : ColumnValue,
              F : ColumnValue
{

    override fun columns() =
            listOf(Column(table.column1Name, a),
                   Column(table.column2Name, b),
                   Column(table.column3Name, c),
                   Column(table.column4Name, d),
                   Column(table.column5Name, e),
                   Column(table.column6Name, f))

    override fun table() = table

}


// ROW 7
// ---------------------------------------------------------------------------------------------

data class RowValue7<A,B,C,D,E,F,G>(
        val table : Table7,
        val a : A,
        val b : B,
        val c : C,
        val d : D,
        val e : E,
        val f : F,
        val g : G)
         : RowValue()
        where A : ColumnValue, B : ColumnValue, C : ColumnValue, D : ColumnValue, E : ColumnValue,
              F : ColumnValue, G : ColumnValue
{

    override fun columns() =
            listOf(Column(table.column1Name, a),
                   Column(table.column2Name, b),
                   Column(table.column3Name, c),
                   Column(table.column4Name, d),
                   Column(table.column5Name, e),
                   Column(table.column6Name, f),
                   Column(table.column7Name, g))

    override fun table() = table

}


// ROW 8
// ---------------------------------------------------------------------------------------------

data class RowValue8<A,B,C,D,E,F,G,H>(
        val table : Table8,
        val a : A,
        val b : B,
        val c : C,
        val d : D,
        val e : E,
        val f : F,
        val g : G,
        val h : H)
         : RowValue()
        where A : ColumnValue, B : ColumnValue, C : ColumnValue, D : ColumnValue, E : ColumnValue,
              F : ColumnValue, G : ColumnValue, H : ColumnValue
{

    override fun columns() =
        listOf(Column(table.column1Name, a),
               Column(table.column2Name, b),
               Column(table.column3Name, c),
               Column(table.column4Name, d),
               Column(table.column5Name, e),
               Column(table.column6Name, f),
               Column(table.column7Name, g),
               Column(table.column8Name, h))

    override fun table() = table

}


// ROW 9
// ---------------------------------------------------------------------------------------------

data class RowValue9<A,B,C,D,E,F,G,H,I>(
        val table : Table9,
        val a : A,
        val b : B,
        val c : C,
        val d : D,
        val e : E,
        val f : F,
        val g : G,
        val h : H,
        val i : I)
         : RowValue()
        where A : ColumnValue, B : ColumnValue, C : ColumnValue, D : ColumnValue, E : ColumnValue,
              F : ColumnValue, G : ColumnValue, H : ColumnValue, I : ColumnValue
{

    override fun columns() =
        listOf(Column(table.column1Name, a),
               Column(table.column2Name, b),
               Column(table.column3Name, c),
               Column(table.column4Name, d),
               Column(table.column5Name, e),
               Column(table.column6Name, f),
               Column(table.column7Name, g),
               Column(table.column8Name, h),
               Column(table.column9Name, i))

    override fun table() = table

}


// ROW 10
// ---------------------------------------------------------------------------------------------

data class RowValue10<A,B,C,D,E,F,G,H,I,J>(
        val table : Table10,
        val a : A,
        val b : B,
        val c : C,
        val d : D,
        val e : E,
        val f : F,
        val g : G,
        val h : H,
        val i : I,
        val j : J)
         : RowValue()
        where A : ColumnValue, B : ColumnValue, C : ColumnValue, D : ColumnValue, E : ColumnValue,
              F : ColumnValue, G : ColumnValue, H : ColumnValue, I : ColumnValue, J : ColumnValue
{

    override fun columns() =
        listOf(Column(table.column1Name, a),
               Column(table.column2Name, b),
               Column(table.column3Name, c),
               Column(table.column4Name, d),
               Column(table.column5Name, e),
               Column(table.column6Name, f),
               Column(table.column7Name, g),
               Column(table.column8Name, h),
               Column(table.column9Name, i),
               Column(table.column10Name, j))

    override fun table() = table

}
