
package com.kispoko.tome.official


import android.content.Context
import com.kispoko.culebra.StringErrors
import com.kispoko.culebra.StringResult
import com.kispoko.culebra.YamlString
import com.kispoko.culebra.Parser as YamlParser
import com.kispoko.culebra.Result as YamlResult
import com.kispoko.culebra.Error as YamlError
import com.kispoko.tome.ApplicationAssets
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppOfficialError
import com.kispoko.tome.app.ApplicationLog
import effect.Err
import effect.Val
import effect.effError
import effect.effValue



/**
 * Official Manage
 */
object OfficialManager
{

    // -----------------------------------------------------------------------------------------
    // PROPERTEIS
    // -----------------------------------------------------------------------------------------

    private var gameManifest : GameManifest? = null

    private var amanaceCharacterSheetManifest : AmanaceCharacterSheetManifest? = null


    // -----------------------------------------------------------------------------------------
    // GAME MANIFEST
    // -----------------------------------------------------------------------------------------

    fun gameManifest(context : Context) : GameManifest?
    {
        if (this.gameManifest == null)
        {
            val manifest = this.loadGameManifest(context)
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


    private fun loadGameManifest(context : Context) : AppEff<GameManifest>
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
                        return effError(AppOfficialError(GameManifestParseError(yamlParse.toString())))
                    }
                }
            }
            is StringErrors ->
            {
                val errorString = parse.errors.map { it.toString() }.joinToString("\n")
                return effError(AppOfficialError(GameManifestParseError(errorString)))
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // AMANACE
    // -----------------------------------------------------------------------------------------

    fun amanaceCharacterSheetManifest(context : Context) : AmanaceCharacterSheetManifest?
    {
        if (this.amanaceCharacterSheetManifest == null)
        {
            val summaries = this.loadAmanaceCharacterSheetManifest(context)
            when (summaries) {
                is Val -> return summaries.value
                is Err -> {
                    ApplicationLog.error(summaries.error)
                    return null
                }
            }
        }
        else
        {
            return null
        }
    }


    private fun loadAmanaceCharacterSheetManifest(context : Context)
                    : AppEff<AmanaceCharacterSheetManifest>
    {
        val filePath = ApplicationAssets.officialAmanaceDirectoryPath +
                            "/character_sheet_manifest.yaml"

        val parse = YamlString.parse(context.assets.open(filePath))
        when (parse)
        {
            is StringResult ->
            {
                val yamlParse = AmanaceCharacterSheetManifest.fromYaml(parse.value)
                when (yamlParse)
                {
                    is YamlResult -> return effValue(yamlParse.value)
                    is YamlError -> {
                        return effError(AppOfficialError(
                                   AmanaceCharSheetManifestParseError(yamlParse.toString())))
                    }
                }
            }
            is StringErrors ->
            {
                val errorString = parse.errors.map { it.toString() }.joinToString("\n")
                return effError(AppOfficialError(
                            AmanaceCharSheetManifestParseError(errorString)))
            }
        }

    }

}
