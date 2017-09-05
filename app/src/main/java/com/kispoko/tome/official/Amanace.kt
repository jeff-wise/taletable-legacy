
package com.kispoko.tome.official


import com.kispoko.culebra.*
import effect.apply



// ---------------------------------------------------------------------------------------------
// CHARACTER SHEETS
// ---------------------------------------------------------------------------------------------

data class AmanaceCharacterSheetManifest(val summaries : List<AmanaceCharacterSheetSummary>)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<AmanaceCharacterSheetManifest> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::AmanaceCharacterSheetManifest,
                          // Summaries
                          yamlValue.array("summaries") ap {
                              it.mapApply { AmanaceCharacterSheetSummary.fromYaml(it) }}
                    )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }

}


/**
 * Amanace Character Sheet Summary
 */
data class AmanaceCharacterSheetSummary(val name : String,
                                        val description : String,
                                        val race : String,
                                        val _class : String,
                                        val variants : List<AmanaceCharacterSheetVariant>)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<AmanaceCharacterSheetSummary> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::AmanaceCharacterSheetSummary,
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
                              it.mapApply { AmanaceCharacterSheetVariant.fromYaml(it) }}
                    )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }

}


data class AmanaceCharacterSheetVariant(val id : String, val label : String)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<AmanaceCharacterSheetVariant> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::AmanaceCharacterSheetVariant,
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
 * Amanace Creature Sheet Manifest
 */
data class AmanaceCreatureSheetManifest(val summaries : List<AmanaceCreatureSheetSummary>)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<AmanaceCreatureSheetManifest> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::AmanaceCreatureSheetManifest,
                          // Summaries
                          yamlValue.array("summaries") ap {
                              it.mapApply { AmanaceCreatureSheetSummary.fromYaml(it) }}
                    )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }

}


/**
 * Amanace Creature Sheet Summary
 */
data class AmanaceCreatureSheetSummary(val name : String,
                                       val type : String,
                                       val description : String,
                                       val challengeRating : Int)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<AmanaceCreatureSheetSummary> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::AmanaceCreatureSheetSummary,
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
 * Amanace NPC Sheet Manifest
 */
data class AmanaceNPCSheetManifest(val summaries : List<AmanaceNPCSheetSummary>)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<AmanaceNPCSheetManifest> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::AmanaceNPCSheetManifest,
                          // Summaries
                          yamlValue.array("summaries") ap {
                              it.mapApply { AmanaceNPCSheetSummary.fromYaml(it) }}
                    )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }

}


/**
 * Amanace NPC Sheet Summary
 */
data class AmanaceNPCSheetSummary(val name : String,
                                  val type : String,
                                  val description : String,
                                  val challengeRating : Int)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<AmanaceNPCSheetSummary> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::AmanaceNPCSheetSummary,
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


