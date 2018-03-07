
package com.kispoko.tome.rts.entity.engine


import com.kispoko.tome.app.ApplicationError
import com.kispoko.tome.model.game.engine.function.FunctionId
import com.kispoko.tome.model.game.engine.mechanic.MechanicCategoryReference
import com.kispoko.tome.model.game.engine.mechanic.MechanicId
import com.kispoko.tome.model.game.engine.procedure.ProcedureId
import com.kispoko.tome.model.game.engine.program.ProgramId
import com.kispoko.tome.model.game.engine.reference.TextReference
import com.kispoko.tome.model.game.engine.summation.SummationId
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


class ValueSetIsNotBase(val valueSetId : ValueSetId) : EngineError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Value Set Is Not a Base Value Set
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


class MechanicDoesNotExist(val mechanicId : MechanicId) : EngineError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Mechanic Does Not Exist
                Mechanic Id: $mechanicId
            """

    override fun logMessage(): String = userMessage()
}


class MechanicCategoryDoesNotExist(val mechanicCategoryId : MechanicCategoryReference) : EngineError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Mechanic Category Does Not Exist
                Mechanic Category Id: $mechanicCategoryId
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


class SummationDoesNotExist(val summationId : SummationId) : EngineError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Summation Does Not Exist
                Summation Id: $summationId
            """

    override fun logMessage(): String = userMessage()
}


class SummationIsNotDiceRoll(val summationId : SummationId) : EngineError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Summation Is Not Dice Roll
                Summation Id: $summationId
            """

    override fun logMessage(): String = userMessage()
}


class ProcedureDoesNotExist(val procedureId : ProcedureId) : EngineError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Procedure Does Not Exist
                Procedure Id: $procedureId
            """

    override fun logMessage(): String = userMessage()
}


class ProcedureDoesNotHaveUpdates(val procedureId : ProcedureId) : EngineError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Procedure Does Not Have Updates
                Procedure Id: $procedureId
            """

    override fun logMessage(): String = userMessage()
}


class TextReferenceIsNull(val reference : TextReference) : EngineError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Text Reference is Null
                Text Reference: $reference
            """

    override fun logMessage(): String = userMessage()
}
