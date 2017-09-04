
package com.kispoko.tome.rts.sheet


import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.kispoko.tome.activity.sheet.PagePagerAdapter
import com.kispoko.tome.app.*
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.model.campaign.Campaign
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.model.sheet.Sheet
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.SheetName
import com.kispoko.tome.model.sheet.SheetSummary
import com.kispoko.tome.model.sheet.section.SectionName
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.official.OfficialSheet
import com.kispoko.tome.rts.theme.ThemeManager
import com.kispoko.tome.rts.campaign.CampaignManager
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.official.OfficialManager
import com.kispoko.tome.rts.theme.ThemeDoesNotHaveColor
import com.kispoko.tome.rts.theme.ThemeNotSupported
import effect.*
import java.io.Serializable



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


    private val sheet = "sheet"

    private val sheetById : MutableMap<SheetId,SheetRecord> = hashMapOf()

    private var currentSheet : SheetId? = null
    private var currentSheetUI : SheetUI? = null


    private val listenerBySheet : MutableMap<SheetId,SheetListener> = hashMapOf()


    // -----------------------------------------------------------------------------------------
    // SHEET
    // -----------------------------------------------------------------------------------------

    fun openSheets() : List<Sheet> = this.sheetById.values.map { it.sheet() }


    fun sheetRecord(sheetId : SheetId) : AppEff<SheetRecord> =
            note(this.sheetById[sheetId], AppSheetError(SheetDoesNotExist(sheetId)))


    fun sheet(sheetId : SheetId) : AppEff<Sheet> =
            note(this.sheetById[sheetId]?.sheet(), AppSheetError(SheetDoesNotExist(sheetId)))


    fun state(sheetId : SheetId) : SheetEff<SheetState> =
            note(this.sheetById[sheetId]?.state, SheetDoesNotExist(sheetId))


    fun sheetContext(sheet : Sheet) : AppEff<SheetContext>
    {
        fun campaign(sheet : Sheet) : AppEff<Campaign> =
                CampaignManager.campaignWithId(sheet.campaignId())


        fun gameId(campaign : Campaign) : AppEff<GameId> = effValue(campaign.gameId())

        fun context(gameId : GameId) : AppEff<SheetContext> =
            effValue(SheetContext(sheet.sheetId(), sheet.campaignId(), gameId))

        return campaign(sheet)
                .apply { gameId(it) }
                .apply { context(it) }
    }


    fun sheetState(sheetId : SheetId) : AppEff<SheetState> =
            note(this.sheetById[sheetId]?.state,
                    AppSheetError(SheetDoesNotExist(sheetId)))



    fun addVariable(sheetId : SheetId, variableId : VariableId) =
        SheetManager.sheetRecord(sheetId) apDo {
            val variable = it.sheet().variableWithId(variableId)
            if (variable != null)
                it.state.addVariable(variable)
        }


    fun addVariable(sheetId : SheetId, variable : Variable) =
        SheetManager.sheetRecord(sheetId) apDo {
                it.state.addVariable(variable)
        }


    fun onVariableUpdate(sheetId : SheetId, variable : Variable) =
        SheetManager.sheetRecord(sheetId) apDo {
            it.state.onVariableUpdate(variable)
        }


    fun evalSheetName(sheetId : SheetId, sheetName : SheetName) : String
    {
        return sheetName.value
    }


    fun evalSheetSummary(sheetId : SheetId, sheetSummary : SheetSummary) : String
    {
        return sheetSummary.value
    }


    fun addOnVariableChangeListener(sheetId : SheetId,
                                    variableId : VariableId,
                                    onChangeListener : OnVariableChangeListener) =
        SheetManager.sheetRecord(sheetId)
                .apDo { it.state.addVariableOnChangeListener(variableId, onChangeListener) }


    // -----------------------------------------------------------------------------------------
    // SESSIONS
    // -----------------------------------------------------------------------------------------

    /**
     * Initializes the current session.
     *
     * If sheets are already loaded, then the session is already active.
     *
     * If no sheets are loaded, then it finds out what the last session was and loads that.
     *
     * Since persistence is not yet implemented, just loads Casmey for now.
     */
    suspend fun startSession(context : Context)
    {
        if (this.sheetById.values.isEmpty())
        {
            val casmeyOfficialSheet = OfficialSheet(SheetId("casmey_beginner"),
                                                    CampaignId("harmony"),
                                                    GameId("amanace"))
            val lastSession = Session(listOf(SessionSheetOfficial(casmeyOfficialSheet)))

            loadSessionSheets(lastSession, context)
        }
    }


    private suspend fun loadSessionSheets(session : Session, context : Context)
    {
        session.sheets.forEach {
            when (it) {
                is SessionSheetOfficial -> OfficialManager.loadSheet(it.officialSheet, context)
            }
        }

    }


    fun addSheetToSession(sheet : Sheet)
    {
        SheetManager.sheetContext(sheet)        apDo { sheetContext ->
        GameManager.engine(sheetContext.gameId) apDo { engine ->
            val sheetRecord = SheetRecord.withDefaultView(sheet, sheetContext,
                                        SheetState(sheetContext, engine.mechanics()))

            // Create & Index Sheet Record
            this.sheetById.put(sheet.sheetId(), sheetRecord)
            this.currentSheet = sheet.sheetId()

            val listener = this.listenerBySheet[sheet.sheetId()]
            if (listener != null)
                listener.onSheetAdd(sheet)

        } }
    }


    fun setSheetActive(sheetId : SheetId, sheetUI : SheetUI)
    {
        SheetManager.sheetRecord(sheetId) apDo {

            this.currentSheetUI = sheetUI

            // Initialize Sheet
            it.onActive(sheetUI.context())

            // Render
            SheetManager.render(sheetId, sheetUI)
        }
    }


    fun addSheetListener(sheetId : SheetId, sheetListener : SheetListener)
    {
        this.listenerBySheet.put(sheetId, sheetListener)
    }


    // -----------------------------------------------------------------------------------------
    // RENDER
    // -----------------------------------------------------------------------------------------

    fun render(sheetId : SheetId, sheetUI : SheetUI)
    {
        val sheetRecordEff = this.sheetRecord(sheetId)

        when (sheetRecordEff)
        {
            is Val -> {
                val sheetRecord = sheetRecordEff.value
                val selectedSectionName = sheetRecord.viewState.selectedSection
                val section = sheetRecord.sheet().sectionWithName(selectedSectionName)

                // Theme UI
                val theme = ThemeManager.theme(sheetRecord.sheet.value.settings().themeId())
                when (theme)
                {
                    is Val -> sheetUI.applyTheme(sheetId, theme.value.uiColors())
                    is Err -> ApplicationLog.error(theme.error)
                }

                val start = System.currentTimeMillis()

                if (section != null) {
                    sheetUI.pagePagerAdatper()
                           .setPages(section.pages(), sheetRecord.sheetContext)
                }
                else {
                    ApplicationLog.error(SectionDoesNotExist(sheetId, selectedSectionName))
                }

                // Configure bottom nav
                sheetUI.bottomNavigation().setOnTabSelectedListener { position, _ ->
                    val selectedSection = sheetRecord.sheet().sectionWithIndex(position)
                    if (selectedSection != null) {
                        sheetUI.pagePagerAdatper()
                                .setPages(selectedSection.pages(), sheetRecord.sheetContext)
                    }
//                    sheetUI.hideActionBar()
                    true
                }

                sheetUI.initializeSidebars(sheetRecord.sheetContext)

                val end = System.currentTimeMillis()

                Log.d("***SHEETMAN", "time to render ms: " + (end - start).toString())
            }
            is Err -> ApplicationLog.error(sheetRecordEff.error)
        }
    }


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun updateSheet(sheetId : SheetId, sheetUpdate : SheetUpdate)
    {
        val sheetRecordEff = SheetManager.sheetRecord(sheetId)
        when (sheetRecordEff)
        {
            is Val ->
            {
                val sheetRecord = sheetRecordEff.value
                val sheetUpdateEvent = SheetUpdateEvent(sheetUpdate, sheetId)
                ApplicationLog.event(sheetUpdateEvent)

                when (sheetUpdate)
                {
                    is WidgetUpdate ->
                    {
                        val rootView = this.currentSheetUI?.rootSheetView()
                        val context = this.currentSheetUI?.context()
                        if (rootView != null && context != null)
                        {
                            sheetRecord.sheet().update(sheetUpdate,
                                                       sheetRecord.sheetContext,
                                                       rootView,
                                                       context)
                        }
                    }
                }
            }
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


    fun uiColor(sheetId : SheetId, colorTheme : ColorTheme) : Int
    {
        val color = this.uiThemeColor(sheetId, colorTheme)

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


    /**
     * The color value for an object in a sheet based on some color theme.
     */
    fun uiThemeColor(sheetId : SheetId, colorTheme : ColorTheme) : AppEff<Int> =
        uiSheetThemeId(sheetId)               ap { themeId ->
        colorId(sheetId, themeId, colorTheme) ap { colorId ->
        ThemeManager.theme(themeId)           ap { theme   ->
        color(theme, colorId)
        } } }


    private fun sheetThemeId(sheetId : SheetId) : AppEff<ThemeId> =
            note(this.sheetById[sheetId]?.sheet()?.settings()?.themeId(),
                 AppSheetError(SheetDoesNotExist(sheetId)))


    private fun uiSheetThemeId(sheetId : SheetId) : AppEff<ThemeId>
    {
        val themeId = this.sheetById[sheetId]?.sheet()?.settings()?.themeId()

        if (themeId != null) {
            when (themeId) {
                is ThemeId.Custom -> return effValue(ThemeId.Dark)
                else              -> return effValue(themeId)
            }
        }
        else {
            return effError(AppSheetError(SheetDoesNotExist(sheetId)))
        }
    }


    private fun colorId(sheetId : SheetId,
                        themeId : ThemeId,
                        colorTheme : ColorTheme) : AppEff<ColorId> =
            note(colorTheme.themeColorId(themeId),
                 AppThemeError(ThemeNotSupported(sheetId, themeId)))


    private fun color(theme : Theme, colorId : ColorId) : AppEff<Int> =
            note(theme.color(colorId),
                 AppThemeError(ThemeDoesNotHaveColor(theme.themeId(), colorId)))


}


// ---------------------------------------------------------------------------------------------
// COMPONENTS
// ---------------------------------------------------------------------------------------------

data class SheetViewState(val selectedSection : SectionName)


data class SheetRecord(val sheet : Comp<Sheet>,
                       val sheetContext : SheetContext,
                       val state : SheetState,
                       val viewState : SheetViewState)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun withDefaultView(sheet : Sheet,
                            sheetContext : SheetContext,
                            state : SheetState) : SheetRecord
        {
            val sections = sheet.sections()

            val sectionName = if (sections.isNotEmpty()) sections[0].name()
                                                         else SectionName("NA")

            val viewState = SheetViewState(sectionName)
            return SheetRecord(Comp(sheet), sheetContext, state, viewState)
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
        val sheetContext = SheetManager.sheetContext(this.sheet())

        when (sheetContext)
        {
            is Val -> this.sheet().onActive(sheetContext.value)
            is Err -> ApplicationLog.error(sheetContext.error)
        }
    }

}


open class SheetContext(open val sheetId : SheetId,
                        open val campaignId : CampaignId,
                        open val gameId : GameId) : Serializable
{

    constructor(sheetUIContext : SheetUIContext) :
            this(sheetUIContext.sheetId,
                 sheetUIContext.campaignId,
                 sheetUIContext.gameId)

}


data class SheetUIContext(val sheetId : SheetId,
                          val campaignId : CampaignId,
                          val gameId : GameId,
                          val context : Context)
{

    constructor(sheetContext : SheetContext, context : Context)
        : this(sheetContext.sheetId,
               sheetContext.campaignId,
               sheetContext.gameId,
               context)

}


data class SheetSummary(val name : String, val description : String)


interface SheetComponent
{
    fun onSheetComponentActive(sheetContext : SheetContext)

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

    fun bottomNavigation() : AHBottomNavigation

    fun applyTheme(sheetId : SheetId, uiColors : UIColors)

    fun initializeSidebars(sheetContext : SheetContext)

    fun context() : Context

    fun rootSheetView() : View?

//    fun hideActionBar()
//
//    fun showActionBar(sheetAction : SheetAction,
//                      sheetContext : SheetContext)

}


data class Session(val sheets : List<SessionSheet>)


sealed class SessionSheet

data class SessionSheetOfficial(val officialSheet : OfficialSheet) : SessionSheet()

data class SessionSheetDatabase(val sheetContext : SheetContext) : SessionSheet()



data class SheetListener(val onSheetAdd : (Sheet) -> Unit)


