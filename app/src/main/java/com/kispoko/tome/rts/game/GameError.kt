
package com.kispoko.tome.rts.game


import com.kispoko.tome.app.ApplicationError
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.value.ValueId
import com.kispoko.tome.model.game.engine.value.ValueSetId
import com.kispoko.tome.model.game.engine.value.ValueType



/**
 * Game Error
 */
sealed class GameError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}


class GameDoesNotExist(val gameId : GameId) : GameError()
{
    override fun debugMessage(): String =
            """
            Game Error: Game Not Found
                Game Id: $gameId
            """

    override fun logMessage(): String = userMessage()
}

