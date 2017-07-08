
package com.kispoko.tome.rts.game.engine


import com.kispoko.tome.app.ApplicationError
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.function.FunctionId
import com.kispoko.tome.model.game.engine.program.ProgramId
import com.kispoko.tome.model.game.engine.value.ValueId
import com.kispoko.tome.model.game.engine.value.ValueSetId
import com.kispoko.tome.model.game.engine.value.ValueType



/**
 * Engine Error
 */
sealed class EngineError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}



class ValueSetDoesNotExist(val valueSetId : ValueSetId) : EngineError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Value Set Does Not Exist
                Value Set Id: $valueSetId
            """

    override fun logMessage(): String = userMessage()
}


class ValueSetDoesNotContainValue(val valueSetId : ValueSetId,
                                  val valueId : ValueId) : EngineError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Value Set Does Not Contain Value
                Value Set Id: $valueSetId
                Value Id: $valueId
            """

    override fun logMessage(): String = userMessage()
}


class ValueIsOfUnexpectedType(val valueSetId : ValueSetId,
                              val valueId : ValueId,
                              val expectedType : ValueType,
                              val actualType : ValueType) : EngineError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Value Is Of Unexpected Type
                Value Set Id: $valueSetId
                Value Id: $valueId
                Expected Type: $expectedType
                Actual Type: $actualType
            """

    override fun logMessage(): String = userMessage()
}


class ProgramDoesNotExist(val programId : ProgramId) : EngineError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Program Does Not Exist
                Program Id: $programId
            """

    override fun logMessage(): String = userMessage()
}


class FunctionDoesNotExist(val functionId : FunctionId) : EngineError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Function Does Not Exist
                Function Id: $functionId
            """

    override fun logMessage(): String = userMessage()
}
