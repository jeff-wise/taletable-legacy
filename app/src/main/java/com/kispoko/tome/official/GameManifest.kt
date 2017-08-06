
package com.kispoko.tome.official


import com.kispoko.culebra.*
import com.kispoko.tome.model.game.GameId
import com.kispoko.culebra.Parser as YamlParser



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
                parserApply6(::GameSummary,
                             // Game Id
                             yamlValue.text("game_id") ap { result(GameId(it)) },
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

