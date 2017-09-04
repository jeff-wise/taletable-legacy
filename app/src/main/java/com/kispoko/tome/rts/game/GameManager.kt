
package com.kispoko.tome.rts.game


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.AppGameError
import com.kispoko.tome.model.game.*
import com.kispoko.tome.model.game.engine.Engine
import com.kispoko.tome.official.*
import effect.*
import lulo.schema.Schema



/**
 * Game Manager
 */
object GameManager
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var specification : Schema? = null
    private var manifest : GameManifest? = null

    private val game = "game"

    private val gameById : MutableMap<GameId,Game> = hashMapOf()


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    // Game
    // -----------------------------------------------------------------------------------------

    fun addGame(game : Game)
    {
        this.gameById.put(game.gameId(), game)
    }


    fun openGames() : List<Game> = this.gameById.values.toList()


    fun hasGameWithId(gameId : GameId) : Boolean = this.gameById.containsKey(gameId)


    fun gameWithId(gameId : GameId) : AppEff<Game> =
            note(this.gameById[gameId],
                    AppGameError(GameDoesNotExist(gameId)))

    // Engine
    // -----------------------------------------------------------------------------------------

    fun engine(gameId : GameId) : AppEff<Engine> =
            note(this.gameById[gameId]?.engine(),
                 AppGameError(GameDoesNotExist(gameId)))

    // Rulebook
    // -----------------------------------------------------------------------------------------

    fun rulebook(gameId : GameId) : AppEff<Rulebook> =
            this.gameWithId(gameId) ap { effValue<AppError,Rulebook>(it.rulebook()) }


    fun rulebookSubsection(gameId : GameId,
                           rulebookReference : RulebookReference) : AppEff<RulebookSubsection?> =
        GameManager.rulebook(gameId)
                .apply { effValue<AppError,RulebookSubsection?>(it.subsection(rulebookReference)) }


}


