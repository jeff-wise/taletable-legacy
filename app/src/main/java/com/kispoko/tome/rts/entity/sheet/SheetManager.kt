
package com.kispoko.tome.rts.entity.sheet


import android.content.Context
import android.view.View
import com.kispoko.tome.activity.sheet.page.PagePagerAdapter
import com.kispoko.tome.rts.entity.EntityId


/**
 * Sheet Manager
 *
 * Manages storing and loading sheets.
 */
//object SheetManager
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    private var session : SheetSession = SheetSession()
//
//
//    // -----------------------------------------------------------------------------------------
//    // INIT
//    // -----------------------------------------------------------------------------------------
//
//    init {
//        // TODO do this lazily
//        session = SheetSession()
//        launch(UI) {
//            Log.d("***SHEETMANAGER", "saving sheet session")
//            //session.saveAsync(true, true)
//        }
//    }


    // -----------------------------------------------------------------------------------------
    // SHEET
    // -----------------------------------------------------------------------------------------

//    fun openSheets() : List<Sheet> = this.session.sheets()
//
//
//    fun sheetRecord(sheetId : SheetId) : AppEff<SheetSessionRecord> =
//            session.sheetRecordWithId(sheetId)
//
//
//    fun currentSheetContext() : AppEff<SheetContext> =
//        session.activeSheet().apply { sheetContext(it) }
//
//
//    fun sheet(sheetId : SheetId) : AppEff<Sheet> = session.sheetWithId(sheetId)


//    fun sheetContext(sheet : Sheet) : AppEff<SheetContext>
//    {
//        fun campaign(sheet : Sheet) : AppEff<Campaign> =
//                CampaignManager.campaignWithId(sheet.campaignId())
//
//
//        fun gameId(campaign : Campaign) : AppEff<GameId> = effValue(campaign.gameId())
//
//        fun context(gameId : GameId) : AppEff<SheetContext> =
//            effValue(SheetContext(sheet.sheetId(), sheet.campaignId(), gameId))
//
//        return campaign(sheet)
//                .apply { gameId(it) }
//                .apply { context(it) }
//    }


//    fun sheetState(sheetId : SheetId) : AppEff<EntityState> =
//            this.sheetRecord(sheetId) ap { effValue<AppError, EntityState>(it.state()) }
//
//
//    fun addVariable(sheetId : SheetId, variableId : VariableId) =
//        SheetManager.sheetRecord(sheetId) apDo {
//            val variable = it.sheet().variableWithId(variableId)
//            if (variable != null)
//                it.state().addVariable(variable)
//        }
//
//
//    fun addVariable(sheetId : SheetId, variable : Variable) =
//        SheetManager.sheetRecord(sheetId) apDo {
//                it.state().addVariable(variable)
//        }
//
//
//    fun onVariableUpdate(sheetId : SheetId, variable : Variable) =
//        SheetManager.sheetRecord(sheetId) apDo {
//            it.state().onVariableUpdate(variable)
//        }


//    fun evalSheetName(sheetName : SheetName) : String
//    {
//        return sheetName.value
//    }
//
//
//    fun evalSheetSummary(sheetSummary : SheetSummary) : String
//    {
//        return sheetSummary.value
//    }
//
//
//    fun addOnVariableChangeListener(sheetId : SheetId,
//                                    variableId : VariableId,
//                                    onChangeListener : OnVariableChangeListener) =
//        SheetManager.sheetRecord(sheetId)
//                .apDo { it.state().addVariableOnChangeListener(variableId, onChangeListener) }


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
//    suspend fun startSession(sheetUI : SheetUI)
//    {
//        // There are no active sheets in memory. Load the last session from the DB or present
//        // the user with an option to open a new sheet.
//        if (!this.session.isActive())
//        {
//            // Testing Case
//            val casmeyOfficialSheet = OfficialSheetId(SheetId("character_casmey_level_1"),
//                                                    CampaignId("isara"),
//                                                    GameId("magic_of_heroes"))
////            val casmeyOfficialSheet = OfficialSheetId(SheetId("generic_npc_town_guard"),
////                                                    CampaignId("isara"),
////                                                    GameId("magic_of_heroes"))
//
//            OfficialManager.loadSheet(casmeyOfficialSheet, sheetUI)
//        }
//        // There is an active sheet in memory. Render it.
//        else
//        {
//            Log.d("***SHEETMANAGER", "set sheet active")
//            session.reload(sheetUI)
//        }
//    }


//    fun lastActiveSheetId()
//    {
        // query sessions, order by last active time, top 1, select lastActiveSheetId

        //class Query
        //{
        //
        //}
        //
        //
        //typealias MyRow = Row2<Prim<TextValue>, TextValue, Prim<NumberValue>, NumberValue>
        //
        //
        //fun query(f : Query.() -> Unit) : Query {
        //    val query = Query()
        //    query.f()
        //    return query
        //}
        //
        //
        //val q = Query() { myRow ->
        //    val (_, x, _) = myRow
        //    first()
        //    sortBy(x, Sort.DESC)
        //    where(x isEqualTo 123)
        //}

    //}


//    fun addSheetToCurrentSession(sheet : Sheet, sheetUI : SheetUI, isSaved : Boolean = true)
//    {
//        session.addSheet(sheet, isSaved, sheetUI)
//
//        // TODO this should be inside addSheet
////        launch(UI) {
////            session.saveAsync(true, true)
////        }
//    }


    // -----------------------------------------------------------------------------------------
    // RENDER
    // -----------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

//    fun updateSheet(sheetId : SheetId, sheetUpdate : SheetUpdate, sheetUI : SheetUI)
//    {
//        val sheetRecordEff = SheetManager.sheetRecord(sheetId)
//        when (sheetRecordEff)
//        {
//            is effect.Val ->
//            {
//                val sheetRecord = sheetRecordEff.value
//                val sheetUpdateEvent = SheetUpdateEvent(sheetUpdate, sheetId)
//                ApplicationLog.event(sheetUpdateEvent)
//
//                when (sheetUpdate)
//                {
//                    is WidgetUpdate ->
//                    {
//                        val rootView = sheetUI.rootSheetView()
//                        val context = sheetUI.context()
//                        if (rootView != null)
//                        {
//                            sheetRecord.sheet().update(sheetUpdate,
//                                                       sheetRecord.sheetContext(),
//                                                       rootView,
//                                                       context)
//                        }
//                    }
//                }
//            }
//        }
//    }


    // -----------------------------------------------------------------------------------------
    // THEME
    // -----------------------------------------------------------------------------------------
//
//    fun color(sheetId : SheetId, colorTheme : ColorTheme) : Int
//    {
//        val color = this.themeColor(sheetId, colorTheme)
//
//        when (color)
//        {
//            is effect.Val -> return color.value
//            is Err -> ApplicationLog.error(color.error)
//        }
//
//        return Color.BLACK
//    }
//
//
//    fun uiColor(sheetId : SheetId, colorTheme : ColorTheme) : Int
//    {
//        val color = this.uiThemeColor(sheetId, colorTheme)
//
//        when (color)
//        {
//            is effect.Val -> return color.value
//            is Err -> ApplicationLog.error(color.error)
//        }
//
//        return Color.BLACK
//    }
//
//
//    fun color(sheetId : SheetId, colorId : ColorId) : Int
//    {
//        val color = sheetThemeId(sheetId)
//                      .apply(ThemeManager::theme)
//                      .applyWith(SheetManager::color, effValue(colorId))
//
//        when (color)
//        {
//            is effect.Val -> return color.value
//            is Err -> ApplicationLog.error(color.error)
//        }
//
//        return Color.BLACK
//    }
//
//
//    /**
//     * The color value for an object in a sheet based on some color theme.
//     */
//    fun themeColor(sheetId : SheetId, colorTheme : ColorTheme) : AppEff<Int> =
//        sheetThemeId(sheetId)                 ap { themeId ->
//        colorId(sheetId, themeId, colorTheme) ap { colorId ->
//        ThemeManager.theme(themeId)           ap { theme   ->
//        color(theme, colorId)
//        } } }
//
//
//    /**
//     * The color value for an object in a sheet based on some color theme.
//     */
//    fun uiThemeColor(sheetId : SheetId, colorTheme : ColorTheme) : AppEff<Int> =
//        uiSheetThemeId(sheetId)               ap { themeId ->
//        colorId(sheetId, themeId, colorTheme) ap { colorId ->
//        ThemeManager.theme(themeId)           ap { theme   ->
//        color(theme, colorId)
//        } } }
//
//
//    private fun sheetThemeId(sheetId : SheetId) : AppEff<ThemeId> =
//        this.session.sheetWithId(sheetId)
//            .apply { effValue<AppError,ThemeId>(it.settings().themeId()) }
//
//
//    private fun uiSheetThemeId(sheetId : SheetId) : AppEff<ThemeId> =
//        sheetThemeId(sheetId) ap {
//            when (it) {
//                is ThemeId.Custom -> effValue<AppError,ThemeId>(ThemeId.Dark)
//                else              -> effValue(it)
//            }
//        }
//
//
//    private fun colorId(sheetId : SheetId,
//                        themeId : ThemeId,
//                        colorTheme : ColorTheme) : AppEff<ColorId> =
//            note(colorTheme.themeColorId(themeId),
//                 AppThemeError(ThemeNotSupported(themeId)))
//
//
//    private fun color(theme : Theme, colorId : ColorId) : AppEff<Int> =
//            note(theme.color(colorId),
//                 AppThemeError(ThemeDoesNotHaveColor(theme.themeId(), colorId)))


//}


// ---------------------------------------------------------------------------------------------
// COMPONENTS
// ---------------------------------------------------------------------------------------------




//
//open class SheetContext(open val sheetId : SheetId,
//                        open val campaignId : CampaignId,
//                        open val gameId : GameId) : Serializable
//{
//
//    constructor(sheetUIContext : SheetUIContext) :
//            this(sheetUIContext.sheetId,
//                 sheetUIContext.campaignId,
//                 sheetUIContext.gameId)
//
//}
//
//
//data class SheetUIContext(val sheetId : SheetId,
//                          val campaignId : CampaignId,
//                          val gameId : GameId,
//                          val context : Context)
//{
//
//    constructor(sheetContext : SheetContext, context : Context)
//        : this(sheetContext.sheetId,
//               sheetContext.campaignId,
//               sheetContext.gameId,
//               context)
//
//
//    fun sheetUI() : SheetUI = this.context as SheetUI
//
//}


interface SheetComponent
{
    fun onSheetComponentActive(entityId : EntityId, context : Context)

}


interface SheetUI
{

    fun pagePagerAdatper() : PagePagerAdapter

//    fun applyTheme(sheetId : SheetId, uiColors : UIColors)

    fun initializeSidebars()

    fun context() : Context

    fun rootSheetView() : View?

//    fun hideActionBar()

//    fun onSheetActive(sheet : Sheet)

}


sealed class SessionSheet

//data class SessionSheetOfficial(val officialSheet : OfficialSheetId) : SessionSheet()
//
//data class SessionSheetDatabase(val sheetContext : SheetContext) : SessionSheet()


// Query({ Session() })
//   .filter






