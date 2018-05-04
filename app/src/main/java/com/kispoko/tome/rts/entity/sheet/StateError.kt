
package com.kispoko.tome.rts.entity.sheet


import com.kispoko.tome.app.ApplicationError
import com.kispoko.tome.model.engine.reference.NumberReference
import com.kispoko.tome.model.engine.variable.*
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.rts.entity.EntityId



/**
 * State Error
 */
sealed class StateError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}


class VariableWithIdDoesNotExist(val entityId : EntityId,
                                 val variableId : VariableId) : StateError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Variable Not Found
                Entity Id: $entityId
                Variable Id: $variableId
            """

    override fun logMessage(): String = userMessage()
}


class VariableWithTagDoesNotExist(val entityId : EntityId,
                                  val variableTag : VariableTag) : StateError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Variable With Tag Does Not Exist
                Entity Id: $entityId
                Variable Tag: ${variableTag.value}
            """

    override fun logMessage(): String = userMessage()
}


class VariableDoesNotExist(val entityId : EntityId,
                           val variableReference : VariableReference) : StateError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Variable Does Not Exist
                Entity Id: $entityId
                Variable Reference: $variableReference
            """

    override fun logMessage(): String = userMessage()
}


class VariableDoesNotHaveValueSet(val variableId : VariableId) : StateError()
{
    override fun debugMessage(): String =
            """
            State Error: Variable Does Not Have Value Set
                Variable Id: $variableId
            """

    override fun logMessage(): String = userMessage()
}


class VariableIsOfUnexpectedType(val entityId : EntityId,
                                 val variableReference : VariableReference,
                                 val expectedType : VariableType,
                                 val actualType : VariableType) : StateError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Variable Is Of Unexpected Type
                Entity Id: $entityId
                Variable Reference: $variableReference
                Expected Type: $expectedType
                Actual Type: $actualType
            """

    override fun logMessage(): String = userMessage()
}


class VariableDoesNotHaveRelation(val variableId : VariableId,
                                  val relation : VariableRelation) : StateError()
{
    override fun debugMessage(): String =
            """
            State Error: Variable Does Not Have Relation
                Variable Id: $variableId
                Relation: ${relation.value}
            """

    override fun logMessage(): String = userMessage()
}


class VariableDoesNotHaveValue(val variableId : VariableId) : StateError()
{
    override fun debugMessage(): String =
            """
            State Error: Variable Does Not Have Value
                Variable Id: $variableId
            """

    override fun logMessage(): String = userMessage()
}


class NumberReferenceDoesNotHaveValue(val numberReference : NumberReference) : StateError()
{
    override fun debugMessage(): String =
            """
            State Error: Number Reference Does Not Have Value
                Number Reference: $numberReference
            """

    override fun logMessage(): String = userMessage()
}


class NoContext(val variableReference : VariableReference) : StateError()
{
    override fun debugMessage(): String =
            """
            State Error: No Context Given for Context Variable Reference
                Variable Reference: $variableReference
            """

    override fun logMessage(): String = userMessage()
}

