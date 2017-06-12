
package com.kispoko.tome.rts.sheet


import android.content.Context
import android.view.View
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.load.*
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.sheet.Sheet
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.Theme
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.official.OfficialIndex
import com.kispoko.tome.official.OfficialSheet
import com.kispoko.tome.rts.ThemeManager
import com.kispoko.tome.rts.campaign.CampaignManager
import com.kispoko.tome.rts.game.GameManager
import effect.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run
import lulo.document.SpecDoc
import lulo.spec.Spec
import java.io.InputStream
import lulo.File as LuloFile



/**
 * Sheet Manager
 *
 * Manages storing and loading sheets.
 */
object SheetManager
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var specification : Spec? = null

    private val sheet = "sheet"

    private val sheetById : MutableMap<SheetId,SheetRecord> = hashMapOf()


    // -----------------------------------------------------------------------------------------
    // SPECIFICATION
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Sheet specification (Lulo). If it is null, try to load it.
     */
    fun specification(context : Context) : Spec?
    {
        if (this.specification == null)
            this.loadSpecification(context)

        return this.specification
    }


    /**
     * Get the specification in the Loader context.
     */
    fun specificationLoader(context : Context) : Loader<Spec>
    {
        val currentSpecification = this.specification(context)
        if (currentSpecification != null)
            return effValue(currentSpecification)
        else
            return effError(SpecIsNull("sheet"))
    }


    /**
     * Load the specification. If it fails, report any errors.
     */
    fun loadSpecification(context : Context)
    {
        val specLoader = loadLuloSpecification(sheet, context)
        when (specLoader)
        {
            is Val -> this.specification = specLoader.value
            is Err -> ApplicationLog.error(specLoader.error)
        }
    }


    // -----------------------------------------------------------------------------------------
    // OFFICIAL
    // -----------------------------------------------------------------------------------------

    suspend fun loadOfficialSheet(officialSheet: OfficialSheet,
                          officialIndex : OfficialIndex,
                          context : Context) : LoadResult<SheetRecord> = run(CommonPool,
    {

        loadOfficialCampaign(officialSheet, officialIndex, context)

        val sheetLoader = _loadOfficialSheet(officialSheet, context)
        when (sheetLoader)
        {
            is Val ->
            {
                val sheet = sheetLoader.value
                val sheetRecord = SheetRecord(sheet, SheetState(sheet))
                this.sheetById.put(sheet.sheetId.value, sheetRecord)
                LoadResultValue(sheetRecord)
            }
            is Err ->
            {
                val loadError = sheetLoader.error
                ApplicationLog.error(loadError)
                LoadResultError<SheetRecord>(loadError.userMessage())
            }
        }
    })


    private fun _loadOfficialSheet(officialSheet: OfficialSheet, context : Context) : Loader<Sheet>
    {
        // LET...
        fun templateFileString(inputStream: InputStream) : Loader<String> =
            effValue(inputStream.bufferedReader().use { it.readText() })

        fun templateDocument(templateString : String,
                             sheetSpec : Spec,
                             campaignSpec : Spec,
                             gameSpec : Spec) : Loader<SpecDoc>
        {
            val docParse = sheetSpec.parseDocument(templateString,
                                                   listOf(campaignSpec, gameSpec))
            when (docParse)
            {
                is Val -> return effValue(docParse.value)
                is Err -> return effError(DocumentParseError(
                                        officialSheet.sheetId.name, sheet, docParse.error))
            }
        }

        fun sheetFromDocument(specDoc : SpecDoc) : Loader<Sheet>
        {
            val sheetParse = Sheet.fromDocument(specDoc)
            when (sheetParse)
            {
                is Val -> return effValue(sheetParse.value)
                is Err -> return effError(ValueParseError(sheet, sheetParse.error))
            }
        }

        // DO...
        return assetInputStream(context, officialSheet.filePath)
                .apply(::templateFileString)
                .applyWith(::templateDocument,
                           SheetManager.specificationLoader(context),
                           CampaignManager.specificationLoader(context),
                           GameManager.specificationLoader(context))
                .apply(::sheetFromDocument)
    }


    private suspend fun loadOfficialCampaign(officialSheet : OfficialSheet,
                                             officialIndex : OfficialIndex,
                                             context : Context)
    {
        if (!CampaignManager.hasCampaignWithId(officialSheet.campaignId))
        {
            val officialCampaign = officialIndex.campaignById[officialSheet.campaignId]
            if (officialCampaign != null)
                CampaignManager.loadOfficialCampaign(officialCampaign, officialIndex, context)
            // TODO errors here
        }
    }


    // -----------------------------------------------------------------------------------------
    // STATE
    // -----------------------------------------------------------------------------------------

    fun state(sheetId : SheetId) : SheetState? = this.sheetById[sheetId]?.sheetState


    // -----------------------------------------------------------------------------------------
    // RENDER
    // -----------------------------------------------------------------------------------------

    fun render()
    {
    }


    // -----------------------------------------------------------------------------------------
    // THEME
    // -----------------------------------------------------------------------------------------

    fun color(sheetId : SheetId, colorTheme : ColorTheme) : Int?
    {
        val color = this.colorEff(sheetId, colorTheme)

        when (color)
        {
            is Val -> return color.value
            is Err -> ApplicationLog.error(color.error)
        }

        return null
    }


    /**
     * The color value for an object in a sheet based on some color theme.
     */
    fun colorEff(sheetId : SheetId, colorTheme : ColorTheme) : SheetEff<Int>
    {
        fun sheetThemeId() : SheetEff<ThemeId> =
            note(this.sheetById[sheetId]?.sheet?.settings()?.themeId(),
                 SheetDoesNotExist(sheetId))

        fun colorId(themeId : ThemeId) : SheetEff<ColorId> =
            note(colorTheme.themeColorId(themeId), ThemeNotSupported(sheetId, themeId))

        fun theme(themeId : ThemeId) : SheetEff<Theme> =
            note(ThemeManager.theme(themeId), ThemeDoesNotExist(themeId))

        fun color(colorId : ColorId, theme : Theme) : SheetEff<Int> =
            note(theme.color(colorId), ThemeDoesNotHaveColor(theme.themeId(), colorId))

        return sheetThemeId()        ap { themeId ->
               colorId(themeId)      ap { colorId ->
               theme(themeId)        ap { theme   ->
               color(colorId, theme)
               } } }
    }

}


// ---------------------------------------------------------------------------------------------
// COMPONENTS
// ---------------------------------------------------------------------------------------------

data class SheetRecord(val sheet : Sheet, val sheetState: SheetState)
{

    init {
        sheet.onActive(sheetState)
    }


    fun context(context : Context) : SheetContext?
    {
        val sheetId    = sheet.sheetId.value
        val campaignId = sheet.campaignId.value
        val gameId     = CampaignManager.campaignWithId(campaignId)?.gameId?.value

        if (gameId != null)
            return SheetContext(sheetId, campaignId, gameId, context)
        else
            return null
    }


}


data class SheetContext(val sheetId : SheetId,
                        val campaignId : CampaignId,
                        val gameId : GameId,
                        val context : Context)


interface SheetComponent
{
    fun onSheetComponentActive(sheetContext : SheetContext)

    fun view(sheetContext : SheetContext) : View
}


typealias SheetEff<A> = Eff<SheetError,Identity,A>

