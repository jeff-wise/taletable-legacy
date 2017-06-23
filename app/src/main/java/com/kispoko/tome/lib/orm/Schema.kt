
package com.kispoko.tome.lib.orm


import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQL
import com.kispoko.tome.lib.orm.sql.SQL_TEXT_TYPE_STRING
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.page.Page
import kotlin.reflect.KClass



object Schema
{

    val modelClasses : List<KClass<out Model>> = listOf(Group::class, Page::class)


    /**
     * The model's SQL table name.
     */
    private fun <A : Model> modelTableName(model : A) : String = SQL.validIdentifier(model.name)



    fun reconcileSchema()
    {
        for (modelClass in modelClasses)
        {
            val modelInstance      = modelClass.java.newInstance()
            val tableName          = this.modelTableName(modelInstance)
            val oneToManyRelations = this.modelOneToManyRelations(modelInstance)

            val tableDefString = this.tableQueryString(modelInstance, oneToManyRelations)

            ORMLog.event(GeneratedTableDefinition(tableDefString))

//            fun <A : Model> tableQueryString(model : A,
//                                             oneToManyRelations : List<OneToManyRelation>) : String
        }

    }


    fun <A : Model> modelTableDefinitionSQLString(model : A) : String
    {
        val sqlString = this.tableQueryString(model, this.modelOneToManyRelations(model))

        ORMLog.event(GeneratedTableDefinition(sqlString))

        return sqlString
    }



    fun <A : Model> modelOneToManyRelations(model : A) : List<OneToManyRelation>
    {

        fun functorRelation(coll : CollS<*>) : OneToManyRelation?
        {
            val collName = coll.name
            if (collName == null)
            {
                ORMLog.event(FunctorIsMissingNameField(model::class.qualifiedName ?: "unknown"))
                return null
            }

            if (coll.valueClass == null)
            {
                ORMLog.event(FunctorIsMissingValueClassField(
                                    model::class.qualifiedName ?: "unknown"))
                return null
            }

            return OneToManyRelation(this.modelTableName(model),
                                     this.modelTableName(coll.valueClass.java.newInstance()),
                                     collName)
        }


        fun collFunctors() : List<CollS<*>> =
                ORM.functors(model).filter { it is CollS<*> } as List<CollS<*>>


        return collFunctors()
                .map { functorRelation(it) }
                .filterNotNull()
    }



    // -----------------------------------------------------------------------------------------
    // QUERIES
    // -----------------------------------------------------------------------------------------

    fun <A : Model> tableQueryString(model : A,
                                     oneToManyRelations : List<OneToManyRelation>) : String
    {
        val queryBuilder = StringBuilder()

        // (1) Create Table
        // --------------------------------------------------------------------------------------
        queryBuilder.append("CREATE TABLE IF NOT EXISTS ")
        queryBuilder.append(this.modelTableName(model))
        queryBuilder.append(" ( ")

        // (2) Id Column
        // --------------------------------------------------------------------------------------

        queryBuilder.append("_id")
        queryBuilder.append(" ")
        queryBuilder.append(SQL_TEXT_TYPE_STRING)
        queryBuilder.append(" PRIMARY KEY")

        // (3) Primitive & Foreign Key Columns
        // --------------------------------------------------------------------------------------

        val functors = ORM.functors(model)

        for (functor in functors)
        {
            when (functor)
            {
                is Prim ->
                {
                    val columnName = functor.name
                    val columnType = functor.asSQLValue().type().name

                    queryBuilder.append(", ")
                    queryBuilder.append(columnName)
                    queryBuilder.append(" ")
                    queryBuilder.append(columnType)
                }
                is Comp ->
                {
                    val columnName = functor.name

                    queryBuilder.append(", ")
                    queryBuilder.append(columnName)
                    queryBuilder.append(" ")
                    queryBuilder.append(SQL_TEXT_TYPE_STRING)
                }
            }

        }

        // (4) One To Many ID Columns
        // --------------------------------------------------------------------------------------

        for ((manyTableName, _, oneFieldName) in oneToManyRelations)
        {
            val columnName = "parent_" + oneFieldName + "_" + manyTableName + "_id"

            queryBuilder.append(", ")
            queryBuilder.append(columnName)
            queryBuilder.append(" ")
            queryBuilder.append(SQL_TEXT_TYPE_STRING)
        }


        return queryBuilder.toString()
    }

}



data class OneToManyRelation(val manyTableName : String,
                             val oneTableName : String,
                             val oneFieldName : String)
