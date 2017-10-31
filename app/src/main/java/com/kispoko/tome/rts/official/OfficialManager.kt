
package com.kispoko.tome.rts.official


import android.content.Context
import com.kispoko.culebra.parseYaml
import com.kispoko.tome.ApplicationAssets
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.AppOfficialError
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.load.*
import com.kispoko.tome.official.*
import com.kispoko.tome.rts.campaign.CampaignManager
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUI
import com.kispoko.tome.rts.theme.ThemeManager
import effect.Err
import effect.Val
import effect.effError
import effect.effValue
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.run
import java.io.IOException
import java.io.InputStream



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
    private var amanaceCreatureSheetManifest : AmanaceCreatureSheetManifest? = null
    private var amanaceNPCSheetManifest : AmanaceNPCSheetManifest? = null


    // -----------------------------------------------------------------------------------------
    // LOAD
    // -----------------------------------------------------------------------------------------

    // Load > Sheet
    // -----------------------------------------------------------------------------------------

    suspend fun loadSheet(officialSheet : OfficialSheet,
                          sheetUI : SheetUI) = run(CommonPool,
    {
        val context = sheetUI.context()

        this.loadCampaign(officialSheet.officialCampaign(), context)

        val sheetLoader = assetInputStream(context, officialSheet.filePath)
                            .apply { TomeDoc.loadSheet(it, officialSheet.sheetId.value, context) }
        when (sheetLoader)
        {
            is Val ->
            {
                val sheet = sheetLoader.value
                // Needs to run in UI thread (renders the sheet)
                launch(UI) { SheetManager.addSheetToSession(sheet, sheetUI, false) }
                ApplicationLog.event(OfficialSheetLoaded(sheet.sheetId().value))
            }
            is Err -> ApplicationLog.error(sheetLoader.error)
        }
    })


    // Load > Campaign
    // -----------------------------------------------------------------------------------------

    suspend fun loadCampaign(officialCampaign : OfficialCampaign,
                             context : Context) = run(CommonPool,
    {

        this.loadGame(officialCampaign.officialGame(), context)

        val campaignLoader = assetInputStream(context, officialCampaign.filePath) apply {
                                TomeDoc.loadCampaign(it,
                                                     officialCampaign.campaignId.value,
                                                     context)
                             }

        when (campaignLoader)
        {
            is Val ->
            {
                val campaign = campaignLoader.value
                ApplicationLog.event(OfficialCampaignLoaded(campaign.campaignName()))
                CampaignManager.addCampaign(campaign)
            }
            is Err -> ApplicationLog.error(campaignLoader.error)
        }
    })


    // Load > Game
    // -----------------------------------------------------------------------------------------

    suspend fun loadGame(officialGame : OfficialGame,
                         context : Context) = run(CommonPool,
    {

        val gameLoader = assetInputStream(context, officialGame.filePath)
                            .apply { TomeDoc.loadGame(it, officialGame.gameId.value, context) }
        when (gameLoader)
        {
            is Val ->
            {
                val game = gameLoader.value
                GameManager.addGame(game)
                ApplicationLog.event(OfficialGameLoaded(game.description().gameNameString()))
            }
            is Err -> ApplicationLog.error(gameLoader.error)
        }
    })


    // Load > Theme
    // -----------------------------------------------------------------------------------------

    suspend fun loadTheme(officialTheme : OfficialTheme,
                          context : Context) = run(CommonPool,
    {

        val themeLoader = assetInputStream(context, officialTheme.filePath)
                            .apply { TomeDoc.loadTheme(it, officialTheme.toString(), context) }
        when (themeLoader)
        {
            is Val ->
            {
                val theme = themeLoader.value
                ThemeManager.addTheme(theme)
                ApplicationLog.event(OfficialThemeLoaded(theme.themeId().toString()))
            }
            is Err -> ApplicationLog.error(themeLoader.error)
        }
    })


    // -----------------------------------------------------------------------------------------
    // GAME MANIFEST
    // -----------------------------------------------------------------------------------------

    fun gameManifest(context : Context) : GameManifest?
    {
        if (gameManifest == null)
        {
            val manifest = loadGameManifest(context)
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

        val gameManifestParser = parseYaml(context.assets.open(manifestFilePath),
                                           GameManifest.Companion::fromYaml)

        return when (gameManifestParser)
        {
            is Val -> effValue(gameManifestParser.value)
            is Err -> effError<AppError, GameManifest>(AppOfficialError(
                    GameManifestParseError(gameManifestParser.error.toString())))
        }
    }


    // -----------------------------------------------------------------------------------------
    // AMANACE
    // -----------------------------------------------------------------------------------------

    // Amanace > Sheet Manifest
    // -----------------------------------------------------------------------------------------

    fun amanaceCharacterSheetManifest(context : Context) : AmanaceCharacterSheetManifest?
    {
        if (amanaceCharacterSheetManifest == null)
        {
            val summaries = loadAmanaceCharacterSheetManifest(context)
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

        val sheetManifestParser = parseYaml(context.assets.open(filePath),
                                            AmanaceCharacterSheetManifest.Companion::fromYaml)
        return when (sheetManifestParser)
        {
            is Val -> effValue(sheetManifestParser.value)
            is Err -> effError(AppOfficialError(
                    AmanaceCharSheetManifestParseError(sheetManifestParser.toString())))
        }

    }


    // Amanace > Creature Manifest
    // -----------------------------------------------------------------------------------------

    fun amanaceCreatureSheetManifest(context : Context) : AmanaceCreatureSheetManifest?
    {
        if (amanaceCreatureSheetManifest == null)
        {
            val summaries = loadAmanaceCreatureSheetManifest(context)
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


    private fun loadAmanaceCreatureSheetManifest(context : Context)
                                : AppEff<AmanaceCreatureSheetManifest>
    {
        val filePath = ApplicationAssets.officialAmanaceDirectoryPath +
                                        "/creature_sheet_manifest.yaml"

        val sheetManifestParser = parseYaml(context.assets.open(filePath),
                                            AmanaceCreatureSheetManifest.Companion::fromYaml)
        return when (sheetManifestParser)
        {
            is Val -> effValue(sheetManifestParser.value)
            is Err -> effError(AppOfficialError(
                    AmanaceCreatureSheetManifestParseError(sheetManifestParser.toString())))
        }

    }


    // Amanace > NPC Manifest
    // -----------------------------------------------------------------------------------------

    fun amanaceNPCSheetManifest(context : Context) : AmanaceNPCSheetManifest?
    {
        if (amanaceNPCSheetManifest == null)
        {
            val summaries = loadAmanaceNPCSheetManifest(context)
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


    private fun loadAmanaceNPCSheetManifest(context : Context) : AppEff<AmanaceNPCSheetManifest>
    {
        val filePath = ApplicationAssets.officialAmanaceDirectoryPath +
                                        "/npc_sheet_manifest.yaml"

        val sheetManifestParser = parseYaml(context.assets.open(filePath),
                                            AmanaceNPCSheetManifest.Companion::fromYaml)
        return when (sheetManifestParser)
        {
            is Val -> effValue(sheetManifestParser.value)
            is Err -> effError(AppOfficialError(
                    AmanaceNPCSheetManifestParseError(sheetManifestParser.toString())))
        }

    }




    /**
     * Get an input stream for an Android asset.
     */
    fun assetInputStream(context : Context, assetFilePath : String) : DocLoader<InputStream> =
        try {
            effValue(context.assets.open(assetFilePath))
        }
        catch (e : IOException) {
            effError(CannotOpenTemplateFile(assetFilePath))
        }



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
