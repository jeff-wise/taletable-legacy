
package com.kispoko.tome.rts.game


import com.kispoko.tome.app.ApplicationError
import com.kispoko.tome.model.game.GameId



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


class GameManifestParseError(val errorString : String) : GameError()
{
    override fun debugMessage() : String = """
            |Game Manifest Parse Error:
            |    $errorString
            """

    override fun logMessage(): String = userMessage()
}

