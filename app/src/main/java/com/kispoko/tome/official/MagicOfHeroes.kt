
package com.kispoko.tome.official


import com.kispoko.culebra.*
import effect.apply



// ---------------------------------------------------------------------------------------------
// CHARACTER SHEETS
// ---------------------------------------------------------------------------------------------

data class HeroesCharacterSheetManifest(val summaries : List<HeroesCharacterSheetSummary>)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<HeroesCharacterSheetManifest> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::HeroesCharacterSheetManifest,
                          // Summaries
                          yamlValue.array("summaries") ap {
                              it.mapApply { HeroesCharacterSheetSummary.fromYaml(it) }}
                    )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }

}


/**
 * Heroes Character Sheet Summary
 */
data class HeroesCharacterSheetSummary(val name : String,
                                       val id : String,
                                       val summary : String,
                                       val description : String,
                                       val level : Int,
                                       val race : String,
                                       val _class : String)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<HeroesCharacterSheetSummary> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::HeroesCharacterSheetSummary,
                          // Name
                          yamlValue.text("name"),
                          // Id
                          yamlValue.text("id"),
                          // Summary
                          yamlValue.text("summary"),
                          // Description
                          yamlValue.text("description"),
                          // Level
                          yamlValue.integer("level"),
                          // Race
                          yamlValue.text("race"),
                          // Class
                          yamlValue.text("class"))
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }

}


data class HeroesCharacterSheetVariant(val id : String, val label : String)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<HeroesCharacterSheetVariant> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::HeroesCharacterSheetVariant,
                           // Id
                           yamlValue.text("id"),
                           // Label
                           yamlValue.text("label")
                           )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }

}


// ---------------------------------------------------------------------------------------------
// CREATURE SHEETS
// ---------------------------------------------------------------------------------------------

/**
 * Heroes Creature Sheet Manifest
 */
data class HeroesCreatureSheetManifest(val summaries : List<HeroesCreatureSheetSummary>)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<HeroesCreatureSheetManifest> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::HeroesCreatureSheetManifest,
                          // Summaries
                          yamlValue.array("summaries") ap {
                              it.mapApply { HeroesCreatureSheetSummary.fromYaml(it) }}
                    )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }

}


/**
 * Heroes Creature Sheet Summary
 */
data class HeroesCreatureSheetSummary(val name : String,
                                      val id : String,
                                      val summary : String,
                                      val description : String,
                                      val challengeRating : Int)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<HeroesCreatureSheetSummary> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::HeroesCreatureSheetSummary,
                          // Name
                          yamlValue.text("name"),
                          // Id
                          yamlValue.text("id"),
                          // Summary
                          yamlValue.text("summary"),
                          // Description
                          yamlValue.text("description"),
                          // Challenge Rating
                          yamlValue.integer("cr")
                          )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT,
                                                  yamlType(yamlValue),
                                                  yamlValue.path))
            }
    }

}


// ---------------------------------------------------------------------------------------------
// GENERIC NPC SHEETS
// ---------------------------------------------------------------------------------------------

/**
 * Heroes Generic NPC Sheet Manifest
 */
data class HeroesGenericNPCSheetManifest(val summaries : List<HeroesGenericNPCSheetSummary>)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<HeroesGenericNPCSheetManifest> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::HeroesGenericNPCSheetManifest,
                          // Summaries
                          yamlValue.array("summaries") ap {
                              it.mapApply { HeroesGenericNPCSheetSummary.fromYaml(it) }}
                    )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }

}


/**
 * Heroes Generic NPC Sheet Summary
 */
data class HeroesGenericNPCSheetSummary(val name : String,
                                        val id : String,
                                        val summary : String,
                                        val description : String,
                                        val challengeRating : String)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<HeroesGenericNPCSheetSummary> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::HeroesGenericNPCSheetSummary,
                          // Name
                          yamlValue.text("name"),
                          // Id
                          yamlValue.text("id"),
                          // Type
                          yamlValue.text("summary"),
                          // Description
                          yamlValue.text("description"),
                          // Challenge Rating
                          yamlValue.text("cr")
                          )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT,
                                                  yamlType(yamlValue),
                                                  yamlValue.path))
            }
    }

}


// ---------------------------------------------------------------------------------------------
// GENERIC NPC SHEETS
// ---------------------------------------------------------------------------------------------

/**
 * Heroes Generic NPC Sheet Manifest
 */
data class HeroesNamedNPCSheetManifest(val summaries : List<HeroesNamedNPCSheetSummary>)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<HeroesNamedNPCSheetManifest> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::HeroesNamedNPCSheetManifest,
                          // Summaries
                          yamlValue.array("summaries") ap {
                              it.mapApply { HeroesNamedNPCSheetSummary.fromYaml(it) }}
                    )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }

}


/**
 * Heroes Named NPC Sheet Summary
 */
data class HeroesNamedNPCSheetSummary(val name : String,
                                      val id: String,
                                      val summary : String,
                                      val description : String,
                                      val challengeRating : String)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<HeroesNamedNPCSheetSummary> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::HeroesNamedNPCSheetSummary,
                          // Name
                          yamlValue.text("name"),
                          // Id
                          yamlValue.text("id"),
                          // Summary
                          yamlValue.text("summary"),
                          // Description
                          yamlValue.text("description"),
                          // Challenge Rating
                          yamlValue.text("cr")
                          )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT,
                                                  yamlType(yamlValue),
                                                  yamlValue.path))
            }
    }

}


// ---------------------------------------------------------------------------------------------
// GM TOOLS SHEETS
// ---------------------------------------------------------------------------------------------

/**
 * Heroes GM TOOLS Sheet Manifest
 */
data class HeroesGMToolsSheetManifest(val summaries : List<HeroesGMToolsSheetSummary>)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<HeroesGMToolsSheetManifest> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::HeroesGMToolsSheetManifest,
                          // Summaries
                          yamlValue.array("summaries") ap {
                              it.mapApply { HeroesGMToolsSheetSummary.fromYaml(it) }}
                    )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }

}


/**
 * Heroes GM Tools Sheet Summary
 */
data class HeroesGMToolsSheetSummary(val name : String,
                                     val type : String,
                                     val description : String,
                                     val challengeRating : Int)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<HeroesGMToolsSheetSummary> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::HeroesGMToolsSheetSummary,
                          // Name
                          yamlValue.text("name"),
                          // Type
                          yamlValue.text("type"),
                          // Description
                          yamlValue.text("description"),
                          // Challenge Rating
                          yamlValue.integer("cr")
                          )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT,
                                                  yamlType(yamlValue),
                                                  yamlValue.path))
            }
    }

}


