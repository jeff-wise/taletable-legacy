
package com.kispoko.tome.rts.game


import android.content.Context
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.load.*
import com.kispoko.tome.model.game.Game
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.EngineData
import com.kispoko.tome.model.game.engine.value.*
import com.kispoko.tome.official.OfficialGame
import effect.Err
import effect.Val
import effect.effError
import effect.effValue
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run
import lulo.File as LuloFile
import lulo.Spec
import lulo.document.SpecDoc
import java.io.InputStream



/**
 * Game Manager
 */
object GameManager : GameData
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


//    suspend fun loadOfficialGameAsync(officialGame: OfficialGame,
//                                      context : Context) : LoadResult<Game> =
//        run(CommonPool,
//        {
//            loadOfficialGame(officialGame, context)
//        })


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
            val docParse = gameSpec.documentParse(templateString, listOf())
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
                is Err -> return effError(ValueParseError(game, gameParse.error))
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
    // GAME DATA
    // -----------------------------------------------------------------------------------------

    override fun textValue(gameId : GameId, valueReference: ValueReference) : ValueText?
    {
        val engineData = this.engineData(gameId)

        if (engineData != null)
            return this.valueSet(gameId, valueReference.valueSetId.value)
                       ?.textValue(valueReference.valueId.value, engineData)

        return null
    }


    /**
     * A value set in the game with the given id.
     */
    override fun valueSet(gameId: GameId, valueSetId: ValueSetId): ValueSet? =
        gameById[gameId]?.engine?.value?.valueSet(valueSetId)



    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun engineData(gameId : GameId) : EngineData? = this.gameById[gameId]?.engine?.value


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

