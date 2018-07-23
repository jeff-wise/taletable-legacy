
package com.taletable.android.official


import android.content.Context
import com.kispoko.culebra.*
import com.taletable.android.ApplicationAssets
import com.taletable.android.app.AppEff
import com.taletable.android.app.AppOfficialError
import com.taletable.android.app.ApplicationLog
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.EntityKind
import com.taletable.android.rts.entity.EntityKindId
import com.taletable.android.rts.session.SessionId
import effect.*
import maybe.Maybe
import maybe.maybe
import java.io.Serializable



// ---------------------------------------------------------------------------------------------
// | FUNCTIONS
// ---------------------------------------------------------------------------------------------


private var gameManifest: GameManifest? = null


// -----------------------------------------------------------------------------------------
// GAME MANIFEST
// -----------------------------------------------------------------------------------------

fun gameManifest(context : Context): GameManifest? {
    return if (gameManifest == null) {
        val manifest = loadGameManifest(context)
        when (manifest) {
            is Val -> manifest.value
            is Err -> {
                ApplicationLog.error(manifest.error)
                null
            }
        }
    } else {
        null
    }
}


private fun loadGameManifest(context : Context) : AppEff<GameManifest>
{
    val manifestFilePath = ApplicationAssets.officialDirectoryPath + "/game_manifest.yaml"

    val gameManifestParser = parseYaml(context.assets.open(manifestFilePath),
            GameManifest.Companion::fromYaml)

    return when (gameManifestParser) {
        is Val -> {
            effValue(gameManifestParser.value)
        }
        is Err -> effError(AppOfficialError(
                    GameManifestParseError(gameManifestParser.error.toString())))
    }
}


// ---------------------------------------------------------------------------------------------
// | DATA TYPES
// ---------------------------------------------------------------------------------------------

/**
 * Game Manifest
 */
data class GameManifest(val gameSummaries : List<GameSummary>)
{

    // -----------------------------------------------------------------------------------------
    // | Properties
    // -----------------------------------------------------------------------------------------

    private val summaryById : MutableMap<EntityId,GameSummary> =
                                    gameSummaries.associateBy { it.gameId }
                                            as MutableMap<EntityId,GameSummary>


    // -----------------------------------------------------------------------------------------
    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<GameManifest> = when (yamlValue)
        {
            is YamlDict ->
            {
                apply(::GameManifest,
                      // Summaries
                      yamlValue.array("summaries") ap {
                          it.mapApply { GameSummary.fromYaml(it) }}
                      )
            }
            else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
        }

    }


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    fun game(gameId : EntityId) : GameSummary? = this.summaryById[gameId]

}


data class GameSummary(val gameId : EntityId,
                       val path : String,
                       val name : String,
                       val description : String,
                       val genre : String,
                       val players : Int,
                       val likes : Int,
                       val defaultSessionId : SessionId,
                       val entityKinds : List<EntityKind>) : Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    private val entityKindById : MutableMap<EntityKindId,EntityKind> =
                        entityKinds.associateBy { it.id }
                                as MutableMap<EntityKindId,EntityKind>


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<GameSummary> = when (yamlValue)
        {
            is YamlDict ->
            {
                apply(::GameSummary,
                      // Game Id
                      yamlValue.at("game_id") ap { EntityId.fromYaml(it) },
                      // Path
                      yamlValue.text("path"),
                      // Name
                      yamlValue.text("name"),
                      // Description
                      yamlValue.text("description"),
                      // Genre
                      yamlValue.text("genre"),
                      // Players
                      yamlValue.integer("players"),
                      // Likes
                      yamlValue.integer("likes"),
                      // Default Session Id
                      yamlValue.at("default_session_id") ap { SessionId.fromYaml(it) },
                      // Entity Kinds
                      split(yamlValue.maybeArray("entity_kinds"),
                            effValue(listOf()),
                            { it.mapApply { EntityKind.fromYaml(it) }})
                      )
            }
            else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
        }

    }


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    fun entityKind(entityKindId : EntityKindId) : Maybe<EntityKind> =
            maybe(this.entityKindById[entityKindId])


}

