
package com.kispoko.tome.rts.entity.game


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppGameError
import com.kispoko.tome.model.game.*
import com.kispoko.tome.model.game.engine.Engine
import com.kispoko.tome.official.*
import effect.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
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

    private val session : MutableMap<GameId,GameRecord> = hashMapOf()


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    // Game
    // -----------------------------------------------------------------------------------------

    fun addGameToSession(game : Game, isSaved : Boolean)
    {
        val gameRecord = GameRecord(game)

        this.session.put(game.gameId(), gameRecord)

        // Save if needed
        if (!isSaved)
            launch(UI) { gameRecord.save() }
    }


    fun openGames() : List<Game> = this.session.values.map { it.game() }


    fun gameWithId(gameId : GameId) : AppEff<Game> =
            note(this.session[gameId]?.game(),
                    AppGameError(GameDoesNotExist(gameId)))

    // Engine
    // -----------------------------------------------------------------------------------------

    fun engine(gameId : GameId) : AppEff<Engine> =
            note(this.session[gameId]?.game()?.engine(),
                 AppGameError(GameDoesNotExist(gameId)))

    // Rulebook
    // -----------------------------------------------------------------------------------------

//    fun rulebook(gameId : GameId, rulebookId : BookId) : AppEff<Book> =
//            this.gameWithId(gameId) ap {
//                note<AppError, Book>(it.rulebookWithId(rulebookId),
//                     AppGameError(GameDoesNotHaveRulebook(gameId, rulebookId)))
//            }


//    fun rulebookSubsection(gameId : GameId,
//                           rulebookReference : BookReference) : AppEff<BookSubsection?> =
//        GameManager.rulebook(gameId)
//                .apply { effValue<AppError,BookSubsection?>(it.subsection(rulebookReference)) }


}


// ---------------------------------------------------------------------------------------------
// GAME RECORD
// ---------------------------------------------------------------------------------------------

data class GameRecord(val game : Game)
{


    fun game() : Game = this.game

    /**
     * This method saves the entire campaign in the database. It is intended to be used to saveSheet
     * a campaign that has just been loaded and has not ever been saved.
     *
     * This method is run asynchronously in the `CommonPool` context.
     */
    suspend fun save()
    {
        //this.game.saveAsync(true, true)
    }

}
