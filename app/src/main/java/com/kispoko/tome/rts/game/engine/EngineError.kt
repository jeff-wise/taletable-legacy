
package com.kispoko.tome.rts.game.engine


import com.kispoko.tome.app.ApplicationError
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.value.ValueId
import com.kispoko.tome.model.game.engine.value.ValueSetId
import com.kispoko.tome.model.game.engine.value.ValueType
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.model.game.engine.variable.VariableType
import com.kispoko.tome.model.sheet.SheetId



/**
 * Engine Error
 */
sealed class EngineError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}



class ValueSetDoesNotExist(val gameId : GameId, val valueSetId : ValueSetId) : EngineError()
{
    override fun debugMessage(): String =
            """
            Engine Error: Value Set Does Not Exist
                Game Id: $gameId
                Value Set Id: $valueSetId
            """

    override fun logMessage(): String = userMessage()
}


class ValueSetDoesNotContainValue(val gameId : GameId,
                                  val valueSetId : ValueSetId,
                                  val valueId : ValueId) : EngineError()
{
    override fun debugMessage(): String =
            """
            Game Error: Value Set Does Not Contain Value
                Game Id: $gameId
                Value Set Id: $valueSetId
                Value Id: $valueId
            """

    override fun logMessage(): String = userMessage()
}


class ValueIsOfUnexpectedType(val gameId : GameId,
                              val valueSetId : ValueSetId,
                              val valueId : ValueId,
                              val expectedType : ValueType,
                              val actualType : ValueType) : EngineError()
{
    override fun debugMessage(): String =
            """
            Game Error: Value Is Of Unexpected Type
                Game Id: $gameId
                Value Set Id: $valueSetId
                Value Id: $valueId
                Expected Type: $expectedType
                Actual Type: $actualType
            """

    override fun logMessage(): String = userMessage()
}


