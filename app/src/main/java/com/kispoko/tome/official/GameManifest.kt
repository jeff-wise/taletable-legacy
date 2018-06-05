
package com.kispoko.tome.official


import com.kispoko.culebra.*
import com.kispoko.tome.model.engine.mechanic.MechanicCategory
import com.kispoko.tome.model.engine.mechanic.MechanicCategoryReference
import com.kispoko.tome.model.engine.value.ValueSet
import com.kispoko.tome.model.engine.value.ValueSetId
import com.kispoko.tome.model.game.Game
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.rts.entity.Entity
import com.kispoko.tome.rts.entity.EntityKind
import com.kispoko.tome.rts.entity.EntityKindId
import com.kispoko.tome.rts.session.SessionId
import effect.apply
import effect.effValue
import effect.split
import maybe.Maybe
import maybe.maybe
import java.io.Serializable



// ---------------------------------------------------------------------------------------------
// Game Manifest
// ---------------------------------------------------------------------------------------------

/**
 * Game Manifest
 */
data class GameManifest(val gameSummaries : List<GameSummary>)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private val summaryById : MutableMap<GameId,GameSummary> =
                                    gameSummaries.associateBy { it.gameId }
                                            as MutableMap<GameId,GameSummary>


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
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

    fun game(gameId : GameId) : GameSummary? = this.summaryById[gameId]

}


data class GameSummary(val gameId : GameId,
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
                      yamlValue.text("game_id") ap {
                          effValue<YamlParseError,GameId>(GameId(it))
                      },
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

