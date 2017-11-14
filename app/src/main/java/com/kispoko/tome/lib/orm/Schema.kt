
package com.kispoko.tome.lib.orm


import android.util.Log
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.lib.orm.sql.SQL
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.orm.sql.SQLValueType
import com.kispoko.tome.lib.orm.sql.SQL_TEXT_TYPE_STRING
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
        val row = prodType.row()
        val tableName = row.tableName()

        // Check if table is already defined and has correct columns
        if (this.tables.containsKey(tableName))
        {
            val tableColumnNames = tables[tableName]!!.columnNames

            row.columns().forEach {
                when (it.value) {
                    is Prim<*> ->
                        if (!tableColumnNames.contains(it.columnName())) {
                            addPrimColumnToTable(prodType.row().tableName(),
                                    it.columnName(),
                                    it.value.asSQLValue().type())
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

        val table = Table(row.tableName(), tableDefinition.columnNames.toMutableSet())
        this.tables.put(tableName, table)

        val endTime = System.nanoTime()

        ORMLog.event(DefineTable(tableName,
                                 tableDefinition.definitionSQLString,
                                 (endTime-startTime)))

        // TODO This will need to be called beforehand when we add foreign key constraints
        row.columns().forEach { column ->
            val value = column.value
            when (value)
            {
                is Prod<*> -> Schema.defineTable(value.value, listOf())
                is Coll<*> -> value.list.forEach { itemValue ->
                    val relation = OneToManyRelation(row.name, column.name)
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

        val row = prodType.row()

        // (1) Create Table
        // --------------------------------------------------------------------------------------
        query.append("CREATE TABLE IF NOT EXISTS ")
        query.append(row.tableName())
        query.append(" ( ")

        // (2) Id Column
        // --------------------------------------------------------------------------------------

        query.append("_id")
        query.append(" ")
        query.append(SQL_TEXT_TYPE_STRING)
        query.append(" PRIMARY KEY")

        // (3) Primitive & Foreign Key Columns
        // --------------------------------------------------------------------------------------

        val columns = row.columns()

        for (column in columns)
        {
            when (column.value)
            {
                is Prim<*> ->
                {
                    columnNames.add(column.columnName())
                    val columnType = column.value.asSQLValue().type().name

                    query.append(", ")
                    query.append(column.columnName())
                    query.append(" ")
                    query.append(columnType)
                }
                is Prod<*> ->
                {
                    columnNames.add(column.columnName())

                    query.append(", ")
                    query.append(column.columnName())
                    query.append(" ")
                    query.append(SQL_TEXT_TYPE_STRING)
                }
                is Sum<*> ->
                {
                    columnNames.add(column.columnName())
                    val typeColumnName = "__type_${column.columnName()}"

                    query.append(", ")
                    query.append(column.columnName())
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
        val tableName = prodType.row().tableName()
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


data class OneToOneRelation(val name : String,
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




data class Col<A>(val name : String, val value : A)
{

    fun columnName() : String = SQL.validIdentifier(this.name)

}


sealed class Row(open val name : String)
{

    fun tableName() : String = SQL.validIdentifier(this.name)


    abstract fun columns() : List<Col<*>>

}


// ROW 1
// ---------------------------------------------------------------------------------------------

data class Row1<A,AA>(
        override val name : String, val a : Col<A>) : Row(name)
        where A : Val<AA>
{

    override fun columns() : List<Col<*>> = listOf(this.a)

}


// ROW 2
// ---------------------------------------------------------------------------------------------

data class Row2<A,AA,B,BB>(
        override val name : String,
        val a : Col<A>,
        val b : Col<B>) : Row(name)
        where A : Val<AA>, B : Val<BB>
{

    override fun columns() : List<Col<*>> = listOf(this.a, this.b)

}

// ROW 3
// ---------------------------------------------------------------------------------------------

data class Row3<A,AA,B,BB,C,CC>(
        override val name : String,
        val a : Col<A>,
        val b : Col<B>,
        val c : Col<C>) : Row(name)
        where A : Val<AA>, B : Val<BB>, C : Val<CC>
{

    override fun columns() : List<Col<*>> = listOf(this.a, this.b, this.c)

}

// ROW 4
// ---------------------------------------------------------------------------------------------

data class Row4<A,AA,B,BB,C,CC,D,DD>(
        override val name : String,
        val a : Col<A>,
        val b : Col<B>,
        val c : Col<C>,
        val d : Col<D>) : Row(name)
        where A : Val<AA>, B : Val<BB>, C : Val<CC>, D : Val<DD>
{

    override fun columns() : List<Col<*>> = listOf(this.a, this.b, this.c, this.d)

}

// ROW 5
// ---------------------------------------------------------------------------------------------

data class Row5<A,AA,B,BB,C,CC,D,DD,E,EE>(
        override val name : String,
        val a : Col<A>,
        val b : Col<B>,
        val c : Col<C>,
        val d : Col<D>,
        val e : Col<E>) : Row(name)
        where A : Val<AA>, B : Val<BB>, C : Val<CC>, D : Val<DD>, E : Val<EE>
{

    override fun columns() : List<Col<*>> = listOf(this.a,
                                                  this.b,
                                                  this.c,
                                                  this.d,
                                                  this.e)

}

// ROW 6
// ---------------------------------------------------------------------------------------------

data class Row6<A,AA,B,BB,C,CC,D,DD,E,EE,F,FF>(
        override val name : String,
        val a : Col<A>,
        val b : Col<B>,
        val c : Col<C>,
        val d : Col<D>,
        val e : Col<E>,
        val f : Col<F>) : Row(name)
        where A : Val<AA>, B : Val<BB>, C : Val<CC>, D : Val<DD>, E : Val<EE>, F : Val<FF>
{

    override fun columns() : List<Col<*>> = listOf(this.a,
                                                  this.b,
                                                  this.c,
                                                  this.d,
                                                  this.e,
                                                  this.f)

}

// ROW 7
// ---------------------------------------------------------------------------------------------

data class Row7<A,AA,B,BB,C,CC,D,DD,E,EE,F,FF,G,GG>(
        override val name : String,
        val a : Col<A>,
        val b : Col<B>,
        val c : Col<C>,
        val d : Col<D>,
        val e : Col<E>,
        val f : Col<F>,
        val g : Col<G>) : Row(name)
        where A : Val<AA>, B : Val<BB>, C : Val<CC>, D : Val<DD>, E : Val<EE>, F : Val<FF>,
              G : Val<GG>
{

    override fun columns() : List<Col<*>> = listOf(this.a,
                                                  this.b,
                                                  this.c,
                                                  this.d,
                                                  this.e,
                                                  this.f,
                                                  this.g)

}

// ROW 8
// ---------------------------------------------------------------------------------------------

data class Row8<A,AA,B,BB,C,CC,D,DD,E,EE,F,FF,G,GG,H,HH>(
        override val name : String,
        val a : Col<A>,
        val b : Col<B>,
        val c : Col<C>,
        val d : Col<D>,
        val e : Col<E>,
        val f : Col<F>,
        val g : Col<G>,
        val h : Col<H>) : Row(name)
        where A : Val<AA>, B : Val<BB>, C : Val<CC>, D : Val<DD>, E : Val<EE>, F : Val<FF>,
              G : Val<GG>, H : Val<HH>
{

    override fun columns() : List<Col<*>> = listOf(this.a,
                                                  this.b,
                                                  this.c,
                                                  this.d,
                                                  this.e,
                                                  this.f,
                                                  this.g,
                                                  this.h)

}

// ROW 9
// ---------------------------------------------------------------------------------------------

data class Row9<A,AA,B,BB,C,CC,D,DD,E,EE,F,FF,G,GG,H,HH,I,II>(
        override val name : String,
        val a : Col<A>,
        val b : Col<B>,
        val c : Col<C>,
        val d : Col<D>,
        val e : Col<E>,
        val f : Col<F>,
        val g : Col<G>,
        val h : Col<H>,
        val i : Col<I>) : Row(name)
        where A : Val<AA>, B : Val<BB>, C : Val<CC>, D : Val<DD>, E : Val<EE>, F : Val<FF>,
              G : Val<GG>, H : Val<HH>, I : Val<II>
{

    override fun columns() : List<Col<*>> = listOf(this.a,
                                                  this.b,
                                                  this.c,
                                                  this.d,
                                                  this.e,
                                                  this.f,
                                                  this.g,
                                                  this.h,
                                                  this.i)

}

// ROW 10
// ---------------------------------------------------------------------------------------------

data class Row10<A,AA,B,BB,C,CC,D,DD,E,EE,F,FF,G,GG,H,HH,I,II,J,JJ>(
        override val name : String,
        val a : Col<A>,
        val b : Col<B>,
        val c : Col<C>,
        val d : Col<D>,
        val e : Col<E>,
        val f : Col<F>,
        val g : Col<G>,
        val h : Col<H>,
        val i : Col<I>,
        val j : Col<J>) : Row(name)
        where A : Val<AA>, B : Val<BB>, C : Val<CC>, D : Val<DD>, E : Val<EE>, F : Val<FF>,
              G : Val<GG>, H : Val<HH>, I : Val<II>, J : Val<JJ>
{

    override fun columns() : List<Col<*>> = listOf(this.a,
                                                  this.b,
                                                  this.c,
                                                  this.d,
                                                  this.e,
                                                  this.f,
                                                  this.g,
                                                  this.h,
                                                  this.i,
                                                  this.j)

}

