package com.kispoko.tome.lib.orm

import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.util.Util
import java.util.*


/**
 * ORM Event
 */
sealed class ORMEvent
{
    abstract fun eventMessage() : String

    abstract fun prettyEventMessage() : String
}


data class FunctorIsMissingNameField(val parentModelName : String) : ORMEvent()
{
    override fun eventMessage() : String =
            "Functor Is Missing Name Field (Will Not Be Serialized): " +
            "Missing name: " +
            "(Parent Model : $parentModelName)"

    override fun prettyEventMessage() : String = """
            Functor Is Missing Name Field (Will Not Be Serialized):
                 Missing Name Field
                 Parent Model Name: $parentModelName
            """
}


data class FunctorIsMissingValueClassField(val parentModelName : String) : ORMEvent()
{
    override fun eventMessage() : String = "Functor Will Not Be Serialized: " +
                                           "Missing Value Class: " +
                                           "(Parent Model : $parentModelName)"

    override fun prettyEventMessage() : String = """
            Functor Will Not Be Serialized:
                 Missing Value Class Field
                 Parent Model Name: $parentModelName
            """
}


//data class GeneratedTableDefinition(val tableDefString : String) : ORMEvent()
//{
//    override fun eventMessage() : String = "Generated Table Definition: " + tableDefString
//
//    override fun prettyEventMessage() : String = """
//            Generated Table Definition:
//                $tableDefString
//            """
//}


data class ModelSaved(val model : Model, val duration : Long) : ORMEvent()
{
    override fun eventMessage() : String = "Model '${model.name}' with id '${model.id}' " +
                                           "saved in : $duration ms"

    override fun prettyEventMessage() : String = """
            Model saved:
                Model Name: ${model.name}
                Model Id: ${model.id}
                Time (ms): ${Util.timeDifferenceString(duration)}
            """
}


data class RowInsert(val tableName : String, val rowId : UUID, val duration : Long) : ORMEvent()
{
    override fun eventMessage() : String = "Inserted row with id '$rowId' into table '$tableName'" +
                                           " in $duration ms."


    override fun prettyEventMessage() : String = """
            Row inserted:
                Table Name: $tableName
                Row Id: $rowId
                Time (ms): ${Util.timeDifferenceString(duration)}
            """
}


data class DefineTable(val tableName : String,
                       val tableSQL : String,
                       val duration : Long) : ORMEvent()
{
    override fun eventMessage() : String = "Defined table '$tableName' with sql '$tableSQL'" +
                                            " in $duration ms."


    override fun prettyEventMessage() : String = """
            Define table:
                Table Name: $tableName
                SQL: $tableSQL
                Time (ms): ${Util.timeDifferenceString(duration)}
            """
}


data class BeginTranscation(val modelName : String) : ORMEvent()
{
    override fun eventMessage() : String = "Start of transaction to save '$modelName'"


    override fun prettyEventMessage() : String = """
            Start Of Transaction:
                Model Name: $modelName
            """
}


data class EndTranscation(val modelName : String) : ORMEvent()
{
    override fun eventMessage() : String = "End of transaction to save '$modelName'"


    override fun prettyEventMessage() : String = """
            End Of Transaction:
                Model Name: $modelName
            """
}


class OpeningDatabase() : ORMEvent()
{
    override fun eventMessage() : String = "Opening a connection to the database."


    override fun prettyEventMessage() : String = """
            Opening a connection to the database.
            """
}
