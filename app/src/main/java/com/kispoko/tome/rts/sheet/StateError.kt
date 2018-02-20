
package com.kispoko.tome.rts.sheet


import com.kispoko.tome.app.ApplicationError
import com.kispoko.tome.model.game.engine.reference.DataReference
import com.kispoko.tome.model.game.engine.reference.NumberReference
import com.kispoko.tome.model.game.engine.variable.*
import com.kispoko.tome.model.sheet.SheetId



/**
 * State Error
 */
sealed class StateError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}


class VariableWithIdDoesNotExist(val sheetId : SheetId,
                                 val variableId : VariableId) : StateError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Variable Not Found
                Sheet Id: $sheetId
                Variable Id: $variableId
            """

    override fun logMessage(): String = userMessage()
}


class VariableWithTagDoesNotExist(val sheetId : SheetId,
                                  val variableTag : VariableTag) : StateError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Variable With Tag Does Not Exist
                Sheet Id: $sheetId
                Variable Tag: ${variableTag.value}
            """

    override fun logMessage(): String = userMessage()
}


class VariableDoesNotExist(val sheetId : SheetId,
                           val variableReference : VariableReference) : StateError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Variable Not Found
                Sheet Id: $sheetId
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


class VariableIsOfUnexpectedType(val sheetId : SheetId,
                                 val variableId : VariableId,
                                 val expectedType : VariableType,
                                 val actualType : VariableType) : StateError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Variable Is Of Unexpected Type
                Sheet Id: $sheetId
                Variable Id: $variableId
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

