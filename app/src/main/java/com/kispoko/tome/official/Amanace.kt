
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
                                       val description : String,
                                       val race : String,
                                       val _class : String,
                                       val variants : List<HeroesCharacterSheetVariant>)
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
                          // Description
                          yamlValue.text("description"),
                          // Race
                          yamlValue.text("race"),
                          // Class
                          yamlValue.text("class"),
                          // Variant
                          yamlValue.array("variants") ap {
                              it.mapApply { HeroesCharacterSheetVariant.fromYaml(it) }}
                    )
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
                                       val type : String,
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


// ---------------------------------------------------------------------------------------------
// NPC SHEETS
// ---------------------------------------------------------------------------------------------

/**
 * Heroes NPC Sheet Manifest
 */
data class HeroesNPCSheetManifest(val summaries : List<HeroesNPCSheetSummary>)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<HeroesNPCSheetManifest> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::HeroesNPCSheetManifest,
                          // Summaries
                          yamlValue.array("summaries") ap {
                              it.mapApply { HeroesNPCSheetSummary.fromYaml(it) }}
                    )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }

}


/**
 * Heroes NPC Sheet Summary
 */
data class HeroesNPCSheetSummary(val name : String,
                                  val type : String,
                                  val description : String,
                                  val challengeRating : Int)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<HeroesNPCSheetSummary> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::HeroesNPCSheetSummary,
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


