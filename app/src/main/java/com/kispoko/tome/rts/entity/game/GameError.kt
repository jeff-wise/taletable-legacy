
package com.kispoko.tome.rts.entity.game


import com.kispoko.tome.app.ApplicationError
import com.kispoko.tome.rts.entity.EntityId



/**
 * Game Error
 */
sealed class GameError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}


class GameDoesNotExist(val gameId : EntityId) : GameError()
{
    override fun debugMessage(): String =
            """
            Game Error: Game Not Found
                Game Id: $gameId
            """

    override fun logMessage(): String = userMessage()
}


//class GameDoesNotHaveRulebook(val gameId : GameId, val rulebookId : BookId) : GameError()
//{
//    override fun debugMessage(): String =
//            """
//            Game Error: Game Does Not Have Rulebook
//                Game Id: $gameId
//                Rulebook Id: $rulebookId
//            """
//
//    override fun logMessage(): String = userMessage()
//}

