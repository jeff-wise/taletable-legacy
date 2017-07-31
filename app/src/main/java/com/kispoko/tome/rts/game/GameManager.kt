
package com.kispoko.tome.rts.game


import android.content.Context
import com.kispoko.culebra.*
import com.kispoko.tome.ApplicationAssets
import com.kispoko.culebra.Parser as YamlParser
import com.kispoko.culebra.Result as YamlResult
import com.kispoko.culebra.Error as YamlError
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppGameError
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.load.*
import com.kispoko.tome.model.game.Game
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.Engine
import com.kispoko.tome.official.*
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
    private var manifest : GameManifest? = null

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
    // MANIFEST
    // -----------------------------------------------------------------------------------------

    fun manifest(context : Context) : GameManifest?
    {
        if (this.manifest == null)
        {
            val manifest = this.loadManifest(context)
            when (manifest) {
                is Val -> return manifest.value
                is Err -> {
                    ApplicationLog.error(manifest.error)
                    return null
                }
            }
        }
        else
        {
            return null
        }
    }


    private fun loadManifest(context : Context) : AppEff<GameManifest>
    {
        val manifestFilePath = ApplicationAssets.officialDirectoryPath + "/game_manifest.yaml"

        val parse = YamlString.parse(context.assets.open(manifestFilePath))
        when (parse)
        {
            is StringResult ->
            {
                val yamlParse = GameManifest.fromYaml(parse.value)
                when (yamlParse)
                {
                    is YamlResult -> return effValue(yamlParse.value)
                    is YamlError -> {
                        return effError(AppGameError(GameManifestParseError(yamlParse.toString())))
                    }
                }
            }
            is StringErrors ->
            {
                val errorString = parse.errors.map { it.toString() }.joinToString("\n")
                return effError(AppGameError(GameManifestParseError(errorString)))
            }
        }
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
// Game Manifest
// ---------------------------------------------------------------------------------------------


data class GameManifest(val gameSummaries : List<GameSummary>)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<GameManifest> = when (yamlValue)
        {
            is YamlDict ->
            {
                parserApply(::GameManifest,
                            // Summaries
                            yamlValue.array("summaries") ap { yamlList ->
                                yamlList.map { GameSummary.fromYaml(it) }}
                            )
            }
            else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue)))
        }

    }

}


data class GameSummary(val name : String,
                       val description : String,
                       val genre : String,
                       val players : Int,
                       val likes : Int)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<GameSummary> = when (yamlValue)
        {
            is YamlDict ->
            {
                parserApply5(::GameSummary,
                             // Name
                             yamlValue.text("name"),
                             // Description
                             yamlValue.text("description"),
                             // Genre
                             yamlValue.text("genre"),
                             // Players
                             yamlValue.integer("players"),
                             // Likes
                             yamlValue.integer("likes")
                             )
            }
            else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue)))
        }

    }

}


