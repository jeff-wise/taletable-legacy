
package com.kispoko.tome.rts.sheet


import com.kispoko.tome.app.ApplicationError
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.model.game.engine.variable.VariableReference
import com.kispoko.tome.model.game.engine.variable.VariableTag
import com.kispoko.tome.model.game.engine.variable.VariableType
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.rts.game.engine.EngineError



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
            Engine Error: Variable Not Found
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

