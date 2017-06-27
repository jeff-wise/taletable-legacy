
package com.kispoko.tome.rts.sheet


import android.content.Context
import android.graphics.Color
import com.kispoko.tome.activity.sheet.PagePagerAdapter
import com.kispoko.tome.app.*
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.load.*
import com.kispoko.tome.model.campaign.Campaign
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.game.engine.reference.*
import com.kispoko.tome.model.sheet.Sheet
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.section.SectionName
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.official.OfficialIndex
import com.kispoko.tome.official.OfficialSheet
import com.kispoko.tome.rts.theme.ThemeManager
import com.kispoko.tome.rts.campaign.CampaignManager
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.theme.ThemeDoesNotHaveColor
import com.kispoko.tome.rts.theme.ThemeNotSupported
import effect.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run
import lulo.document.SpecDoc
import lulo.spec.Spec
import java.io.InputStream
import java.io.Serializable
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
    // SHEET
    // -----------------------------------------------------------------------------------------

    fun sheetRecord(sheetId : SheetId) : AppEff<SheetRecord> =
            note(this.sheetById[sheetId], AppSheetError(SheetDoesNotExist(sheetId)))


    fun sheet(sheetId : SheetId) : AppEff<Sheet> =
            note(this.sheetById[sheetId]?.sheet(), AppSheetError(SheetDoesNotExist(sheetId)))


    fun state(sheetId : SheetId) : SheetEff<SheetState> =
            note(this.sheetById[sheetId]?.state, SheetDoesNotExist(sheetId))


    fun sheetState(sheetId : SheetId) : AppEff<SheetState> =
            note(this.sheetById[sheetId]?.state,
                    AppSheetError(SheetDoesNotExist(sheetId)))


    fun setNewSheet(sheet : Sheet, sheetUI : SheetUI)
    {
        // Create & Index Sheet Record
        val sheetRecord = SheetRecord.withDefaultView(sheet, SheetState(sheet))
        this.sheetById.put(sheet.sheetId(), sheetRecord)

        // Initialize Sheet
        sheetRecord.onActive(sheetUI.context())

        // Theme UI
        val theme = ThemeManager.theme(sheet.settings().themeId())
        when (theme)
        {
            is Val -> sheetUI.applyTheme(sheet.sheetId(), theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        // Render
        this.render(sheet.sheetId(), sheetUI.pagePagerAdatper())
    }


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
                          context : Context) : LoadResult<Sheet> = run(CommonPool,
    {

        loadOfficialCampaign(officialSheet, officialIndex, context)

        val sheetLoader = _loadOfficialSheet(officialSheet, context)
        when (sheetLoader)
        {
            is Val ->
            {
                val sheet = sheetLoader.value
//                val value = SheetRecord.withDefaultView(sheet, SheetState(sheet))
//                this.sheetById.put(sheet.sheetId.value, value)
                LoadResultValue(sheet)
            }
            is Err ->
            {
                val loadError = sheetLoader.error
                ApplicationLog.error(loadError)
                LoadResultError<Sheet>(loadError.userMessage())
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
                             gameSpec : Spec,
                             themeSpec : Spec) : Loader<SpecDoc>
        {
            val docParse = sheetSpec.parseDocument(templateString,
                                                   listOf(campaignSpec, gameSpec, themeSpec))
            when (docParse)
            {
                is Val -> return effValue(docParse.value)
                is Err -> return effError(DocumentParseError(
                                        officialSheet.sheetId.value, sheet, docParse.error))
            }
        }

        fun sheetFromDocument(specDoc : SpecDoc) : Loader<Sheet>
        {
            val sheetParse = Sheet.fromDocument(specDoc)
            when (sheetParse)
            {
                is Val -> return effValue(sheetParse.value)
                is Err -> return effError(ValueParseError(officialSheet.sheetId.value,
                                                          sheetParse.error))
            }
        }

        // DO...
        return assetInputStream(context, officialSheet.filePath)
                .apply(::templateFileString)
                .applyWith(::templateDocument,
                           SheetManager.specificationLoader(context),
                           CampaignManager.specificationLoader(context),
                           GameManager.specificationLoader(context),
                           ThemeManager.specificationLoader(context))
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
    // RENDER
    // -----------------------------------------------------------------------------------------

    fun render(sheetId : SheetId, pagePagerAdapter : PagePagerAdapter)
    {
        val sheetRecordEff = this.sheetRecord(sheetId)

        when (sheetRecordEff)
        {
            is Val ->
            {
                val sheetRecord = sheetRecordEff.value
                val selectedSectionName = sheetRecord.viewState.selectedSection
                val section = sheetRecord.sheet().sectionWithName(selectedSectionName)
                if (section != null)
                {
                    val gameContext = sheetRecord.gameContext()
                    when (gameContext)
                    {
                        is Val -> pagePagerAdapter.setPages(section.pages(), gameContext.value)
                        is Err -> ApplicationLog.error(gameContext.error)
                    }
                }
                else
                {
                    ApplicationLog.error(SectionDoesNotExist(sheetId, selectedSectionName))
                }
            }
            is Err -> ApplicationLog.error(sheetRecordEff.error)
        }
    }


    // -----------------------------------------------------------------------------------------
    // THEME
    // -----------------------------------------------------------------------------------------

    fun color(sheetId : SheetId, colorTheme : ColorTheme) : Int
    {
        val color = this.themeColor(sheetId, colorTheme)

        when (color)
        {
            is Val -> return color.value
            is Err -> ApplicationLog.error(color.error)
        }

        return Color.BLACK
    }


    fun color(sheetId : SheetId, colorId : ColorId) : Int
    {
        val color = sheetThemeId(sheetId)
                      .apply(ThemeManager::theme)
                      .applyWith(SheetManager::color, effValue(colorId))

        when (color)
        {
            is Val -> return color.value
            is Err -> ApplicationLog.error(color.error)
        }

        return Color.BLACK
    }


    /**
     * The color value for an object in a sheet based on some color theme.
     */
    fun themeColor(sheetId : SheetId, colorTheme : ColorTheme) : AppEff<Int> =
        sheetThemeId(sheetId)                 ap { themeId ->
        colorId(sheetId, themeId, colorTheme) ap { colorId ->
        ThemeManager.theme(themeId)           ap { theme   ->
        color(theme, colorId)
        } } }


    private fun sheetThemeId(sheetId : SheetId) : AppEff<ThemeId> =
            note(this.sheetById[sheetId]?.sheet()?.settings()?.themeId(),
                 AppSheetError(SheetDoesNotExist(sheetId)))

    private fun colorId(sheetId : SheetId,
                        themeId : ThemeId,
                        colorTheme : ColorTheme) : AppEff<ColorId> =
            note(colorTheme.themeColorId(themeId),
                 AppThemeError(ThemeNotSupported(sheetId, themeId)))


    private fun color(theme : Theme, colorId : ColorId) : AppEff<Int> =
            note(theme.color(colorId),
                 AppThemeError(ThemeDoesNotHaveColor(theme.themeId(), colorId)))




    // -----------------------------------------------------------------------------------------
    // STATE
    // -----------------------------------------------------------------------------------------

    object State
    {

        // -----------------------------------------------------------------------------------------
        // REFERENCES
        // -----------------------------------------------------------------------------------------

        /**
         * Resolve a boolean reference.
         */
        fun boolean(sheetContext : SheetContext,
                    reference : BooleanReference) : AppEff<Boolean> =
            when (reference)
            {
                is BooleanReferenceLiteral  -> effValue(reference.value)
                is BooleanReferenceVariable -> SheetManager.sheetState(sheetContext.sheetId)
                        .apply( { it.booleanVariable(reference.variableReference)})
                        .apply( { it.value() })

            }


        /**
         * Resolve a dice roll reference.
         */
        fun diceRoll(sheetContext : SheetContext,
                     reference : DiceRollReference) : AppEff<DiceRoll> =
            when (reference)
            {
                is DiceRollReferenceLiteral  -> effValue(reference.value)
                is DiceRollReferenceVariable -> SheetManager.sheetState(sheetContext.sheetId)
                        .apply( { it.diceRollVariable(reference.variableReference)})
                        .apply( { effValue<AppError,DiceRoll>(it.value()) })

            }


        /**
         * Resolve a number reference.
         */
        fun number(sheetContext : SheetContext,
                   numberReference : NumberReference) : AppEff<Double> =
            when (numberReference)
            {
                is NumberReferenceLiteral  -> effValue(numberReference.value)
                is NumberReferenceValue    ->
                        GameManager.engine(sheetContext.gameId)
                            .apply({ it.numberValue(numberReference.valueReference) })
                            .apply({ effValue<AppError,Double>(it.value()) })
                is NumberReferenceVariable -> SheetManager.sheetState(sheetContext.sheetId)
                        .apply( { it.numberVariable(numberReference.variableReference)})
                        .apply( { it.value(sheetContext) })

            }


        /**
         * Resolve a text reference.
         */
        fun text(sheetContext : SheetContext,
                 reference : TextReference) : AppEff<String> =
            when (reference)
            {
                is TextReferenceLiteral  -> effValue(reference.value)
                is TextReferenceValue    ->
                        GameManager.engine(sheetContext.gameId)
                            .apply({ it.textValue(reference.valueReference) })
                            .apply({ effValue<AppError,String>(it.value()) })
                is TextReferenceVariable -> SheetManager.sheetState(sheetContext.sheetId)
                        .apply( { it.textVariable(reference.variableReference)})
                        .apply( { it.value(sheetContext) })
            }
    }

}


// ---------------------------------------------------------------------------------------------
// COMPONENTS
// ---------------------------------------------------------------------------------------------

data class SheetViewState(val selectedSection : SectionName)


data class SheetRecord(val sheet : Comp<Sheet>,
                       val state : SheetState,
                       val viewState : SheetViewState)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun withDefaultView(sheet : Sheet, state : SheetState) : SheetRecord
        {
            val sections = sheet.sections()

            val sectionName = if (sections.isNotEmpty()) sections[0].name()
                                                         else SectionName("NA")

            val viewState = SheetViewState(sectionName)
            return SheetRecord(Comp(sheet), state, viewState)
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun sheet() : Sheet = this.sheet.value


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun onActive(context : Context)
    {
        val sheetContext = this.context(context)

        when (sheetContext)
        {
            is Val -> this.sheet().onActive(sheetContext.value)
            is Err -> ApplicationLog.error(sheetContext.error)
        }
    }


    fun context(context : Context) : AppEff<SheetContext>
    {
        val sheetId    = sheet().sheetId.value
        val campaignId = sheet().campaignId.value

        val campaign : AppEff<Campaign> =
                note(CampaignManager.campaignWithId(campaignId),
                     AppSheetError(CampaignDoesNotExist(sheetId, campaignId)))

        val gameId = campaign.apply { effValue<AppError,GameId>(it.gameId.value) }

        return effApply(::SheetContext, effValue(sheetId),
                                        effValue(campaignId),
                                        gameId,
                                        effValue(context))
    }


    fun gameContext() : SheetEff<SheetGameContext>
    {
        val sheetId    = sheet().sheetId.value
        val campaignId = sheet().campaignId.value

        val campaign : SheetEff<Campaign> = note(CampaignManager.campaignWithId(campaignId),
                                                 CampaignDoesNotExist(sheetId, campaignId))
        val gameId = campaign.apply { effValue<SheetError,GameId>(it.gameId.value) }

        return effApply(::SheetGameContext, effValue(sheetId),
                                            effValue(campaignId),
                                            gameId)
    }

}


data class SheetGameContext(val sheetId : SheetId,
                            val campaignId : CampaignId,
                            val gameId : GameId) : Serializable


data class SheetContext(val sheetId : SheetId,
                        val campaignId : CampaignId,
                        val gameId : GameId,
                        val context : Context)


interface SheetComponent
{
    fun onSheetComponentActive(sheetContext : SheetContext)

//     fun view(sheetContext : SheetContext) : View
}


typealias SheetEff<A> = Eff<SheetError,Identity,A>


fun <A> fromSheetEff(sheetEff : SheetEff<A>) : AppEff<A> = when (sheetEff)
{
    is Val -> effValue(sheetEff.value)
    is Err -> effError(AppSheetError(sheetEff.error))
}

// fun <A> sheetEffValue(value : A) : SheetEff<A> = Val(value, Identity())


interface SheetUI
{

    fun pagePagerAdatper() : PagePagerAdapter

    fun applyTheme(sheetId : SheetId, uiColors : UIColors)

    fun context() : Context

}

