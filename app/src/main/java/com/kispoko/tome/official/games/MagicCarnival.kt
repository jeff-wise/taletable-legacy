
package com.kispoko.tome.official.games


import com.kispoko.culebra.*





// ---------------------------------------------------------------------------------------------
// CREATURE SHEETS
// ---------------------------------------------------------------------------------------------

/**
 * Carnival Creature Sheet Manifest
 */
data class CarnivalCreatureSheetManifest(val summaries : List<CarnivalCreatureSheetSummary>)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<CarnivalCreatureSheetManifest> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    effect.apply(::CarnivalCreatureSheetManifest,
                          // Summaries
                          yamlValue.array("summaries") ap {
                              it.mapApply { CarnivalCreatureSheetSummary.fromYaml(it) }}
                    )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }

}


/**
 * Carnvival Creature Sheet Summary
 */
data class CarnivalCreatureSheetSummary(val name : String,
                                        val id : String,
                                        val summary : String,
                                        val description : String,
                                        val challengeRating : Int,
                                        val size : String,
                                        val type : String)
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<CarnivalCreatureSheetSummary> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    effect.apply(::CarnivalCreatureSheetSummary,
                          // Name
                          yamlValue.text("name"),
                          // Id
                          yamlValue.text("id"),
                          // Summary
                          yamlValue.text("summary"),
                          // Description
                          yamlValue.text("description"),
                          // Challenge Rating
                          yamlValue.integer("cr"),
                          // Size
                          yamlValue.text("size"),
                          // Type
                          yamlValue.text("type")
                          )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT,
                                                  yamlType(yamlValue),
                                                  yamlValue.path))
            }
    }

}
