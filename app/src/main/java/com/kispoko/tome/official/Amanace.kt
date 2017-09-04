
package com.kispoko.tome.official


import com.kispoko.culebra.*
import effect.apply


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
