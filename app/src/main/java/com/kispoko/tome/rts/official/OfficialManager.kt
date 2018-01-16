
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

    private var heroesCharacterSheetManifest : HeroesCharacterSheetManifest? = null
    private var heroesCreatureSheetManifest : HeroesCreatureSheetManifest? = null
    private var heroesNPCSheetManifest : HeroesNPCSheetManifest? = null


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
                launch(UI) { SheetManager.addSheetToCurrentSession(sheet, sheetUI, false) }
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
                CampaignManager.addCampaignToSession(campaign, false)
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
                GameManager.addGameToSession(game, false)
                ApplicationLog.event(OfficialGameLoaded(game.gameName().value))
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

    fun heroesNPCSheetManifest(context : Context) : HeroesNPCSheetManifest?
    {
        if (heroesNPCSheetManifest == null)
        {
            val summaries = loadHeroesNPCSheetManifest(context)
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


    private fun loadHeroesNPCSheetManifest(context : Context) : AppEff<HeroesNPCSheetManifest>
    {
        val filePath = ApplicationAssets.officialHeroesDirectoryPath +
                                        "/npc_sheet_manifest.yaml"

        val sheetManifestParser = parseYaml(context.assets.open(filePath),
                                            HeroesNPCSheetManifest.Companion::fromYaml)
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
