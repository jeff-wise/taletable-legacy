
package com.kispoko.tome.official


import com.kispoko.culebra.*
import com.kispoko.tome.model.game.GameId
import effect.apply
import effect.effValue


// ---------------------------------------------------------------------------------------------
// Game Manifest
// ---------------------------------------------------------------------------------------------

/**
 * Game Manifest
 */
data class GameManifest(val gameSummaries : List<GameSummary>)
{

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

}


data class GameSummary(val gameId : GameId,
                       val name : String,
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
                      yamlValue.integer("likes")
                      )
            }
            else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
        }

    }

}

