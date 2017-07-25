
package com.kispoko.tome.rts.game


import android.content.Context
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppEngineError
import com.kispoko.tome.app.AppGameError
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.load.*
import com.kispoko.tome.model.game.Game
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.Engine
import com.kispoko.tome.model.game.engine.value.*
import com.kispoko.tome.official.OfficialGame
import com.kispoko.tome.rts.game.engine.EngineError
import effect.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run
import lulo.File as LuloFile
import lulo.document.SpecDoc
import lulo.spec.Spec
import java.io.InputStream



/**
 * Game Manager
 */
object GameManager
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var specification : Spec? = null

    private val game = "game"

    private val gameById : MutableMap<GameId,Game> = hashMapOf()


    // -----------------------------------------------------------------------------------------
    // SPECIFICATION
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Game specification (Lulo). If it is null, try to load it.
     */
    fun specification(context : Context) : Spec?
    {
        if (this.specification == null)
            this.loadSpecification(context)

        return this.specification
    }


    /**
     * Get the specification in the Loader context.
     */
    fun specificationLoader(context : Context) : Loader<Spec>
    {
        val currentSpecification = this.specification(context)
        if (currentSpecification != null)
            return effValue(currentSpecification)
        else
            return effError(SpecIsNull(game))
    }


    /**
     * Load the specification. If it fails, report any errors.
     */
    fun loadSpecification(context : Context)
    {
        val specLoader = loadLuloSpecification(game, context)
        when (specLoader)
        {
            is Val -> this.specification = specLoader.value
            is Err -> ApplicationLog.error(specLoader.error)
        }
    }


    // -----------------------------------------------------------------------------------------
    // OFFICIAL
    // -----------------------------------------------------------------------------------------

    suspend fun loadOfficialGame(officialGame : OfficialGame,
                                 context : Context) : LoadResult<Game> = run(CommonPool,
    {

        val gameLoader = _loadOfficialGame(officialGame, context)
        when (gameLoader)
        {
            is Val ->
            {
                val game = gameLoader.value
                this.gameById.put(officialGame.gameId, game)
                LoadResultValue(game)
            }
            is Err ->
            {
                val loadError = gameLoader.error
                ApplicationLog.error(loadError)
                LoadResultError<Game>(loadError.userMessage())
            }
        }
    })


    private fun _loadOfficialGame(officialGame : OfficialGame,
                                  context : Context) : Loader<Game>
    {
        // LET...
        fun templateFileString(inputStream: InputStream) : Loader<String> =
            effValue(inputStream.bufferedReader().use { it.readText() })

        fun templateDocument(templateString : String, gameSpec : Spec) : Loader<SpecDoc>
        {
            val docParse = gameSpec.parseDocument(templateString, listOf())
            when (docParse)
            {
                is Val -> return effValue(docParse.value)
                is Err -> return effError(DocumentParseError(officialGame.gameId.value,
                                                             game,
                                                             docParse.error))
            }
        }

        fun gameFromDocument(specDoc : SpecDoc) : Loader<Game>
        {
            val gameParse = Game.fromDocument(specDoc)
            when (gameParse)
            {
                is Val -> return effValue(gameParse.value)
                is Err -> return effError(ValueParseError(officialGame.gameId.value,
                                                          gameParse.error))
            }
        }

        // DO...
        return assetInputStream(context, officialGame.filePath)
                .apply(::templateFileString)
                .applyWith(::templateDocument,
                           this.specificationLoader(context))
                .apply(::gameFromDocument)
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun openGames() : List<Game> = this.gameById.values.toList()


    fun engine(gameId : GameId) : AppEff<Engine> =
            note(this.gameById[gameId]?.engine(),
                 AppGameError(GameDoesNotExist(gameId)))


    fun hasGameWithId(gameId : GameId) : Boolean = this.gameById.containsKey(gameId)

}


// ---------------------------------------------------------------------------------------------
// INTERFACES
// ---------------------------------------------------------------------------------------------

// Game Data
// ---------------------------------------------------------------------------------------------

/**
 * Game Data
 */
interface GameData
{
    fun valueSet(gameId : GameId, valueSetId : ValueSetId) : ValueSet?

    fun textValue(gameId : GameId, valueReference : ValueReference) : ValueText?

}


typealias EngineEff<A> = Eff<EngineError, Identity, A>


fun <A> fromEngineEff(engineEff : EngineEff<A>) : AppEff<A> = when (engineEff)
{
    is Val -> effValue(engineEff.value)
    is Err -> effError(AppEngineError(engineEff.error))
}


typealias GameEff<A> = Eff<GameError, Identity, A>


fun <A> fromGameEff(gameEff : GameEff<A>) : AppEff<A> = when (gameEff)
{
    is Val -> effValue(gameEff.value)
    is Err -> effError(AppGameError(gameEff.error))
}
