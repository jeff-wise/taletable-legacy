
package com.kispoko.tome.rts.official


import android.content.Context
import com.kispoko.culebra.YamlParser
import com.kispoko.culebra.YamlValue
import com.kispoko.culebra.parseYaml
import com.kispoko.tome.ApplicationAssets
import com.kispoko.tome.app.*
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.official.*
import com.kispoko.tome.official.games.*
import effect.Err
import effect.Val
import effect.effError
import effect.effValue



/**
 * Official Manager
 */
object OfficialManager
{

    // -----------------------------------------------------------------------------------------
    // PROPERTEIS
    // -----------------------------------------------------------------------------------------

    private var gameManifest : GameManifest? = null

    private var heroesCharacterSheetManifest  : HeroesCharacterSheetManifest? = null
    private var heroesCreatureSheetManifest   : HeroesCreatureSheetManifest? = null
    private var heroesGenericNPCSheetManifest : HeroesGenericNPCSheetManifest? = null
    private var heroesNamedNPCSheetManifest   : HeroesNamedNPCSheetManifest? = null
    private var heroesGMToolsSheetManifest    : HeroesGMToolsSheetManifest? = null


    // -----------------------------------------------------------------------------------------
    // GAME MANIFEST
    // -----------------------------------------------------------------------------------------

    fun gameManifest(context : Context) : GameManifest?
    {
        return if (gameManifest == null)
        {
            val manifest = loadGameManifest(context)
            when (manifest) {
                is Val -> manifest.value
                is Err -> {
                    ApplicationLog.error(manifest.error)
                    null
                }
            }
        }
        else
        {
            null
        }
    }


    private fun loadGameManifest(context : Context) : AppEff<GameManifest>
    {
        val manifestFilePath = ApplicationAssets.officialDirectoryPath + "/game_manifest.yaml"

        val gameManifestParser = parseYaml(context.assets.open(manifestFilePath),
                                           GameManifest.Companion::fromYaml)

        return when (gameManifestParser)
        {
            is Val -> effValue(gameManifestParser.value)
            is Err -> effError(AppOfficialError(
                          GameManifestParseError(gameManifestParser.error.toString())))
        }
    }


    // -----------------------------------------------------------------------------------------
    // THE MAGIC OF HEROES
    // -----------------------------------------------------------------------------------------

    // The Magic of Heroes > Sheet Manifest
    // -----------------------------------------------------------------------------------------

    fun heroesCharacterSheetManifest(context : Context) : HeroesCharacterSheetManifest?
    {
        if (heroesCharacterSheetManifest == null)
        {
            val summaries = loadHeroesCharacterSheetManifest(context)
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


    private fun loadHeroesCharacterSheetManifest(context : Context)
                    : AppEff<HeroesCharacterSheetManifest>
    {
        val filePath = ApplicationAssets.officialHeroesDirectoryPath +
                            "/character_sheet_manifest.yaml"

        val sheetManifestParser = parseYaml(context.assets.open(filePath),
                                            HeroesCharacterSheetManifest.Companion::fromYaml)
        return when (sheetManifestParser)
        {
            is Val -> effValue(sheetManifestParser.value)
            is Err -> effError(AppOfficialError(
                    HeroesCharSheetManifestParseError(sheetManifestParser.toString())))
        }

    }


    // The Magic of Heroes > Creature Manifest
    // -----------------------------------------------------------------------------------------

    fun heroesCreatureSheetManifest(context : Context) : HeroesCreatureSheetManifest?
    {
        if (heroesCreatureSheetManifest == null)
        {
            val summaries = loadHeroesCreatureSheetManifest(context)
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


    private fun loadHeroesCreatureSheetManifest(context : Context)
                                : AppEff<HeroesCreatureSheetManifest>
    {
        val filePath = ApplicationAssets.officialHeroesDirectoryPath +
                                        "/creature_sheet_manifest.yaml"

        val sheetManifestParser = parseYaml(context.assets.open(filePath),
                                            HeroesCreatureSheetManifest.Companion::fromYaml)
        return when (sheetManifestParser)
        {
            is Val -> effValue(sheetManifestParser.value)
            is Err -> effError(AppOfficialError(
                    HeroesCreatureSheetManifestParseError(sheetManifestParser.toString())))
        }

    }


    // The Magic of Heroes > NPC Manifest
    // -----------------------------------------------------------------------------------------

    fun heroesGenericNPCSheetManifest(context : Context) : HeroesGenericNPCSheetManifest?
    {
        if (heroesGenericNPCSheetManifest == null)
        {
            val summaries = loadHeroesGenericNPCSheetManifest(context)
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


    private fun loadHeroesGenericNPCSheetManifest(context : Context) : AppEff<HeroesGenericNPCSheetManifest>
    {
        val filePath = ApplicationAssets.officialHeroesDirectoryPath +
                                        "/generic_npc_sheet_manifest.yaml"

        val sheetManifestParser = parseYaml(context.assets.open(filePath),
                                            HeroesGenericNPCSheetManifest.Companion::fromYaml)
        return when (sheetManifestParser)
        {
            is Val -> effValue(sheetManifestParser.value)
            is Err -> effError(AppOfficialError(
                    HeroesNPCSheetManifestParseError(sheetManifestParser.toString())))
        }

    }




    /**
     * Get an input stream for an Android asset.
     */
//    fun assetInputStream(context : Context, assetFilePath : String) : DocLoader<InputStream> =
//        try {
//            effValue(context.assets.open(assetFilePath))
//        }
//        catch (e : IOException) {
//            effError(CannotOpenTemplateFile(assetFilePath))
//        }



//
//    // -----------------------------------------------------------------------------------------
//    // API
//    // -----------------------------------------------------------------------------------------
//
//    fun officialIndex() : OfficialIndex
//    {
//        if (this.officialIndex != null)
//            this.officialIndex
//
//        this.officialIndex = OfficialIndex.load(this)
//        if (officialIndex != null)
//            this.loadSheet(officialIndex)
//
//    }

}



// The Magic of Heroes > NPC Manifest
// -----------------------------------------------------------------------------------------

//fun <A> manifest(context : Context) : A?
//{
//    if (heroesGenericNPCSheetManifest == null)
//    {
//        val summaries = loadHeroesGenericNPCSheetManifest(context)
//        when (summaries) {
//            is Val -> return summaries.value
//            is Err -> {
//                ApplicationLog.error(summaries.error)
//                return null
//            }
//        }
//    }
//    else
//    {
//        return null
//    }
//}


fun <A> sheetManifest(filepath : String,
                      yamlParser : (YamlValue) -> YamlParser<A>,
                      context : Context) : AppEff<A>
{
//    val filePath = ApplicationAssets.officialHeroesDirectoryPath +
//                                    "/generic_npc_sheet_manifest.yaml"
//

    val sheetManifestParser = parseYaml(context.assets.open(filepath), yamlParser)
    return when (sheetManifestParser)
    {
        is Val -> effValue(sheetManifestParser.value)
        is Err -> effError(AppOfficialError(
                      SheetManifestParseError(sheetManifestParser.toString())))
    }

}


fun sheetManifestFilepath(gameId : GameId, sheetType : String) : String =
    when (gameId.value)
    {
        "magic_carnival" -> {
            "official/magic_carnival/${sheetType}_sheet_manifest.yaml"
        }
        else             -> "unknown"
    }
