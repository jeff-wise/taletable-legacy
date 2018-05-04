
package com.kispoko.tome.rts.entity


import com.kispoko.tome.app.ApplicationError
import com.kispoko.tome.model.engine.function.FunctionId
import com.kispoko.tome.model.engine.mechanic.MechanicCategoryReference
import com.kispoko.tome.model.engine.mechanic.MechanicId
import com.kispoko.tome.model.engine.procedure.ProcedureId
import com.kispoko.tome.model.engine.program.ProgramId
import com.kispoko.tome.model.engine.summation.SummationId
import com.kispoko.tome.model.engine.value.ValueReference
import com.kispoko.tome.model.engine.value.ValueSetId


/**
 * Entity Error
 */
sealed class EntityError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}


class EntityDoesNotExist(val entityId : EntityId) : EntityError()
{
    override fun debugMessage(): String =
            """
            Entity Error: Entity Does Not Exist
                Entity Id: $entityId
            """

    override fun logMessage(): String = userMessage()
}


class EntityDoesNotHaveFunction(val entityId : EntityId,
                                val functionId : FunctionId) : EntityError()
{
    override fun debugMessage(): String =
            """
            Entity Error: Entity Does Not Have Function
                Entity Id: $entityId
                Function Id: $functionId
            """

    override fun logMessage(): String = userMessage()
}


class EntityDoesNotHaveMechanic(val entityId : EntityId,
                                val mechanicId : MechanicId) : EntityError()
{
    override fun debugMessage(): String =
            """
            Entity Error: Entity Does Not Have Mechanic
                Entity Id: $entityId
                Mechanic Id: $mechanicId
            """

    override fun logMessage(): String = userMessage()
}


class EntityDoesNotHaveMechanicCategory(val entityId : EntityId,
                                        val mechanicCategoryId : MechanicCategoryReference) : EntityError()
{
    override fun debugMessage(): String =
            """
            Entity Error: Entity Does Not Have Mechanic Category
                Entity Id: $entityId
                Mechanic Category Id: $mechanicCategoryId
            """

    override fun logMessage(): String = userMessage()
}


class EntityDoesNotHaveProcedure(val entityId : EntityId,
                                 val procedureId : ProcedureId) : EntityError()
{
    override fun debugMessage(): String =
            """
            Entity Error: Entity Does Not Have Procedure
                Entity Id: $entityId
                Procedure Id: $procedureId
            """

    override fun logMessage(): String = userMessage()
}


class EntityDoesNotHaveProgram(val entityId : EntityId,
                               val programId : ProgramId) : EntityError()
{
    override fun debugMessage(): String =
            """
            Entity Error: Entity Does Not Have Program
                Entity Id: $entityId
                Program Id: $programId
            """

    override fun logMessage(): String = userMessage()
}


class EntityDoesNotHaveSummation(val entityId : EntityId,
                                 val summationId : SummationId) : EntityError()
{
    override fun debugMessage(): String =
            """
            Entity Error: Entity Does Not Have Summation
                Entity Id: $entityId
                Summation Id: $summationId
            """

    override fun logMessage(): String = userMessage()
}


class EntityDoesNotHaveValueSet(val entityId : EntityId,
                                val valueSetId : ValueSetId) : EntityError()
{
    override fun debugMessage(): String =
            """
            Entity Error: Entity Does Not Have Value Set
                Entity Id: $entityId
                Value Set Id: $valueSetId
            """

    override fun logMessage(): String = userMessage()
}


class EntityDoesNotHaveValue(val entityId : EntityId,
                             val valueReference : ValueReference) : EntityError()
{
    override fun debugMessage(): String =
            """
            Entity Error: Entity Does Not Have Value
                Entity Id: $entityId
                Value Reference: $valueReference
            """

    override fun logMessage(): String = userMessage()
}


class EntityDoesNotHaveTheme(val entityId : EntityId) : EntityError()
{
    override fun debugMessage(): String =
            """
            Entity Error: Entity Does Not Have A Theme
                Entity Id: $entityId
            """

    override fun logMessage(): String = userMessage()
}


class EntityIsUnexpectedType(val entityId : EntityId,
                             val expectedType : EntityType,
                             val actualType : EntityType) : EntityError()
{
    override fun debugMessage(): String =
            """
            Entity Error: Entity Is Unexpected Type
                Entity Id: $entityId
                Expected Type: $expectedType
                Actual Type: $actualType
            """

    override fun logMessage(): String = userMessage()
}
