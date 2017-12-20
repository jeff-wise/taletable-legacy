
package com.kispoko.tome.lib.orm


import android.content.ContentValues
import com.kispoko.tome.lib.orm.sql.SQLValue
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
            "(Parent ProdType : $parentModelName)"

    override fun prettyEventMessage() : String = """
            Functor Is Missing Name Field (Will Not Be Serialized):
                 Missing Name Field
                 Parent ProdType Name: $parentModelName
            """
}


data class FunctorIsMissingValueClassField(val parentModelName : String) : ORMEvent()
{
    override fun eventMessage() : String = "Functor Will Not Be Serialized: " +
                                           "Missing Value Class: " +
                                           "(Parent ProdType : $parentModelName)"

    override fun prettyEventMessage() : String = """
            Functor Will Not Be Serialized:
                 Missing Value Class Field
                 Parent ProdType Name: $parentModelName
            """
}


/**
 * The type of functor in a sum prodType is not permitted i.e. collection functors.
 */
data class SumFunctorNotSupported(val parentModelName : String) : ORMEvent()
{
    override fun eventMessage() : String = "Functor Is Not Supported As Sum in $parentModelName: "

    override fun prettyEventMessage() : String = """
            |Functor Is Not Supported As Sum:
            |     Parent ProdType Name: $parentModelName
            """.trimMargin()
}


//data class PrimSaved(val prodType: ProdType, val duration : Long) : ORMEvent()
//{
//    override fun eventMessage() : String = "ProdType '${prodType.name}' with id '${prodType.id}' " +
//                                           "saved in : $duration ms"
//
//    override fun prettyEventMessage() : String = """
//            |ProdType saved:
//            |    ProdType Name: ${prodType.name}
//            |    ProdType Id: ${prodType.id}
//            |    Time (ms): ${Util.timeDifferenceString(duration)}
//            """.trimMargin()
//}


data class ModelSaved(val prodType : ProdType, val duration : Long) : ORMEvent()
{
    override fun eventMessage() : String = "ProdType '${prodType}' with id '${prodType.id}' " +
                                           "saved in : $duration ms"

    override fun prettyEventMessage() : String = """
            |ProdType saved:
            |    ProdType Name: ${prodType}
            |    ProdType Id: ${prodType.id}
            |    Time (ms): ${Util.timeDifferenceString(duration)}
            """.trimMargin()
}


/**
 * Duration reflects SQL time.
 */
data class RowInsert(val tableName : String,
                     private val rowId : UUID,
                     private val duration : Long,
                     private val contentValues : ContentValues? = null) : ORMEvent()
{
    override fun eventMessage() : String = "Inserted row with id '$rowId' into table '$tableName'" +
                                           " in $duration ms."


    override fun prettyEventMessage() : String = """
        |Row inserted:
        |    Table Name: $tableName
        |    Row Id: $rowId
        |    Time (ms): ${Util.timeDifferenceString(duration)}
        |    Values: ${contentValues?.valueSet() ?: "Use Debug Mode: Everything"}
        """.trimMargin()

}


/**
 * Duration reflects SQL time.
 */
data class ValueUpdate(val tableName : String,
                       val columName : String,
                       private val rowId : UUID,
                       private val newSQLValue : SQLValue,
                       private val duration : Long) : ORMEvent()
{
    override fun eventMessage() : String = "Inserted value '$newSQLValue' into table '$tableName'" +
                                           " at column $columName and row $rowId" +
                                           " in $duration ms."


    override fun prettyEventMessage() : String = """
        |Row inserted:
        |    Table Name: $tableName
        |    Column: $columName
        |    Row Id: $rowId
        |    Time (ms): ${Util.timeDifferenceString(duration)}
        |    New Value: $newSQLValue
        """.trimMargin()

}


/**
 * Duration reflects SQL time.
 */
data class DefineTable(val tableName : String,
                       val tableSQL : String,
                       val duration : Long) : ORMEvent()
{
    override fun eventMessage() : String = "Defined table '$tableName' with sql '$tableSQL'" +
                                            " in ${Util.timeDifferenceString(duration)} ms."


    override fun prettyEventMessage() : String = """
            |Define table:
            |    Table Name: $tableName
            |    SQL: $tableSQL
            |    Time (ms): ${Util.timeDifferenceString(duration)}
            """.trimMargin()
}


/**
 * Duration reflects SQL time.
 */
data class ColumnAdded(val tableName : String,
                       val columName : String,
                       val columnType : String,
                       val sqlString : String,
                       val duration : Long) : ORMEvent()
{

    override fun eventMessage() : String =
            "Added column '$columName' with type '$columnType'" +
            " to table $tableName in  ${Util.timeDifferenceString(duration)} ms."


    override fun prettyEventMessage() : String = """
            |Column Added:
            |    Table Name: $tableName
            |    Column Name: $columName
            |    Column Type: $columnType
            |    SQL: $sqlString
            |    Time (ms): ${Util.timeDifferenceString(duration)}
            """.trimMargin()
}


data class BeginTranscation(val modelName : String) : ORMEvent()
{
    override fun eventMessage() : String = "Start of transaction to saveSheet '$modelName'"


    override fun prettyEventMessage() : String = """
            |Start Of Transaction:
            |   ProdType Name: $modelName
            """.trimMargin()
}


data class EndTranscation(val modelName : String,
                          var totalRowInserts : Int? = null,
                          var timeSpentCreatingSchemaObjectsNS : Long? = null,
                          var totalDurationNS : Long? = null) : ORMEvent()
{
    override fun eventMessage() : String = "End of transaction to saveSheet '$modelName'"


    override fun prettyEventMessage() : String = """
            |End Of Transaction:
            |    ProdType Name: $modelName
            |    Row Inserts: ${totalRowInserts ?: "N/A"}
            |    Time Creating Schema (ms): ${Util.timeDifferenceString(timeSpentCreatingSchemaObjectsNS) ?: "N/A"}
            |    Total Time (ms): ${Util.timeDifferenceString(totalDurationNS) ?: "N/A"}
            """.trimMargin()
}


class OpeningDatabase : ORMEvent()
{
    override fun eventMessage() : String = "Opening a connection to the database."


    override fun prettyEventMessage() : String = """
            Opening a connection to the database.
            """
}
