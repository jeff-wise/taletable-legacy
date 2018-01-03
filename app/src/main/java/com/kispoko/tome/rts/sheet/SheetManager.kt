
package com.kispoko.tome.rts.sheet


import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.kispoko.tome.activity.sheet.PagePagerAdapter
import com.kispoko.tome.app.*
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.RowValue4
import com.kispoko.tome.lib.orm.schema.*
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.campaign.Campaign
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.summation.Summation
import com.kispoko.tome.model.game.engine.summation.SummationId
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
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



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

    private var session : Session = Session()


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init {
        // TODO do this lazily
        session = Session()
        launch(UI) {
            Log.d("***SHEETMANAGER", "saving sheet session")
            //session.saveAsync(true, true)
        }
    }


    // -----------------------------------------------------------------------------------------
    // SHEET
    // -----------------------------------------------------------------------------------------

    fun openSheets() : List<Sheet> = this.session.sheets()


    fun sheetRecord(sheetId : SheetId) : AppEff<SessionSheetRecord> =
            session.sheetRecordWithId(sheetId)


    fun currentSheetContext() : AppEff<SheetContext> =
        session.activeSheet().apply { sheetContext(it) }


    fun sheet(sheetId : SheetId) : AppEff<Sheet> = session.sheetWithId(sheetId)


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
            this.sheetRecord(sheetId) ap { effValue<AppError,SheetState>(it.state()) }


    fun addVariable(sheetId : SheetId, variableId : VariableId) =
        SheetManager.sheetRecord(sheetId) apDo {
            val variable = it.sheet().variableWithId(variableId)
            if (variable != null)
                it.state().addVariable(variable)
        }


    fun addVariable(sheetId : SheetId, variable : Variable) =
        SheetManager.sheetRecord(sheetId) apDo {
                it.state().addVariable(variable)
        }


    fun onVariableUpdate(sheetId : SheetId, variable : Variable) =
        SheetManager.sheetRecord(sheetId) apDo {
            it.state().onVariableUpdate(variable)
        }


    fun evalSheetName(sheetName : SheetName) : String
    {
        return sheetName.value
    }


    fun evalSheetSummary(sheetSummary : SheetSummary) : String
    {
        return sheetSummary.value
    }


    fun addOnVariableChangeListener(sheetId : SheetId,
                                    variableId : VariableId,
                                    onChangeListener : OnVariableChangeListener) =
        SheetManager.sheetRecord(sheetId)
                .apDo { it.state().addVariableOnChangeListener(variableId, onChangeListener) }


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
    suspend fun startSession(sheetUI : SheetUI)
    {
        // There are no active sheets in memory. Load the last session from the DB or present
        // the user with an option to open a new sheet.
        if (!this.session.isActive())
        {
            // Testing Case
            val casmeyOfficialSheet = OfficialSheet(SheetId("character_casmey_level_1"),
                                                    CampaignId("isara"),
                                                    GameId("magic_of_heroes"))

            OfficialManager.loadSheet(casmeyOfficialSheet, sheetUI)
        }
        // There is an active sheet in memory. Render it.
        else
        {
            Log.d("***SHEETMANAGER", "set sheet active")
            session.reload(sheetUI)
        }
    }


    fun lastActiveSheetId()
    {
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

    }


    fun addSheetToSession(sheet : Sheet, sheetUI : SheetUI, isSaved : Boolean = true)
    {
        session.addSheet(sheet, isSaved, sheetUI)

        // TODO this should be inside addSheet
//        launch(UI) {
//            session.saveAsync(true, true)
//        }
    }


    // -----------------------------------------------------------------------------------------
    // RENDER
    // -----------------------------------------------------------------------------------------

    fun render(sheetId : SheetId, sheetUI : SheetUI)
    {
        val sheetRecordEff = this.sheetRecord(sheetId)

        when (sheetRecordEff)
        {
            is effect.Val -> {
                val sheetRecord = sheetRecordEff.value
                val selectedSectionName = sheetRecord.viewState().selectedSection
                val section = sheetRecord.sheet().sectionWithName(selectedSectionName)

                // Theme UI
                val theme = ThemeManager.theme(sheetRecord.sheet().settings().themeId())
                when (theme)
                {
                    is effect.Val -> sheetUI.applyTheme(sheetId, theme.value.uiColors())
                    is Err -> ApplicationLog.error(theme.error)
                }

                val start = System.currentTimeMillis()

                if (section != null) {
                    sheetUI.pagePagerAdatper()
                           .setPages(section.pages(), sheetRecord.sheetContext())
                    Log.d("***SHEETMANAGER", "set pages")
                }
                else {
                    ApplicationLog.error(SectionDoesNotExist(sheetId, selectedSectionName))
                }

                // Configure bottom nav
                /*
                sheetUI.bottomNavigation().setOnTabSelectedListener { position, _ ->
                    val selectedSection = sheetRecord.sheet().sectionWithIndex(position)
                    if (selectedSection != null) {
                        sheetUI.pagePagerAdatper()
                                .setPages(selectedSection.pages(), sheetRecord.sheetContext())
                    }
                    sheetUI.hideActionBar()
                    true
                }*/

                sheetUI.initializeSidebars(sheetRecord.sheetContext())

                val end = System.currentTimeMillis()

                Log.d("***SHEETMAN", "time to render ms: " + (end - start).toString())
            }
            is Err -> ApplicationLog.error(sheetRecordEff.error)
        }
    }


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun updateSheet(sheetId : SheetId, sheetUpdate : SheetUpdate, sheetUI : SheetUI)
    {
        val sheetRecordEff = SheetManager.sheetRecord(sheetId)
        when (sheetRecordEff)
        {
            is effect.Val ->
            {
                val sheetRecord = sheetRecordEff.value
                val sheetUpdateEvent = SheetUpdateEvent(sheetUpdate, sheetId)
                ApplicationLog.event(sheetUpdateEvent)

                when (sheetUpdate)
                {
                    is WidgetUpdate ->
                    {
                        val rootView = sheetUI.rootSheetView()
                        val context = sheetUI.context()
                        if (rootView != null)
                        {
                            sheetRecord.sheet().update(sheetUpdate,
                                                       sheetRecord.sheetContext(),
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
            is effect.Val -> return color.value
            is Err -> ApplicationLog.error(color.error)
        }

        return Color.BLACK
    }


    fun uiColor(sheetId : SheetId, colorTheme : ColorTheme) : Int
    {
        val color = this.uiThemeColor(sheetId, colorTheme)

        when (color)
        {
            is effect.Val -> return color.value
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
            is effect.Val -> return color.value
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
        this.session.sheetWithId(sheetId)
            .apply { effValue<AppError,ThemeId>(it.settings().themeId()) }


    private fun uiSheetThemeId(sheetId : SheetId) : AppEff<ThemeId> =
        sheetThemeId(sheetId) ap {
            when (it) {
                is ThemeId.Custom -> effValue<AppError,ThemeId>(ThemeId.Dark)
                else              -> effValue(it)
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



    // -----------------------------------------------------------------------------------------
    // ENGINE
    // -----------------------------------------------------------------------------------------

    fun summation(summationId : SummationId, sheetContext : SheetContext) : AppEff<Summation>
    {
        // Reverse apply for when keep going if is error / until success
        val sheetSummation = SheetManager.sheetRecord(sheetContext.sheetId) ap {
            it.sheet().engine().summation(summationId)
        }

        return when (sheetSummation)
        {
            is effect.Val -> sheetSummation
            is Err -> GameManager.engine(sheetContext.gameId) ap {
                          it.summation(summationId)
                      }
        }
    }


}


// ---------------------------------------------------------------------------------------------
// COMPONENTS
// ---------------------------------------------------------------------------------------------

data class SheetViewState(val selectedSection : SectionName)


data class SessionSheetRecord(override val id : UUID,
                              private val sheet : Sheet,
                              private val sheetId : SheetId,
                              private var sessionIndex : SessionRecordIndex,
                              private var lastActive : SheetLastActiveTime,
                              private val sheetContext : SheetContext,
                              private val state : SheetState,
                              private val viewState : SheetViewState)
                               : ProdType
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun withDefaultView(sheet : Sheet,
                            sheetContext : SheetContext,
                            state : SheetState) : SessionSheetRecord
        {
            val sections = sheet.sections()

            val sectionName = if (sections.isNotEmpty()) sections[0].name()
                                                         else SectionName("NA")

            val viewState = SheetViewState(sectionName)
            return SessionSheetRecord(UUID.randomUUID(),
                                      sheet,
                                      sheet.sheetId(),
                                      SessionRecordIndex(1),
                                      SheetLastActiveTime(System.currentTimeMillis()),
                                      sheetContext,
                                      state,
                                      viewState)
        }
    }


    // -----------------------------------------------------------------------------------------
    // STATE
    // -----------------------------------------------------------------------------------------

    fun sheet() : Sheet = this.sheet


    fun state() : SheetState = this.state


    fun viewState() : SheetViewState = this.viewState


    fun sheetId() : SheetId = this.sheet.sheetId()


    fun sheetContext() : SheetContext = this.sheetContext


    fun setIndex(index : Int) {
        this.sessionIndex = SessionRecordIndex(index)
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun onActive(context : Context)
    {
        val sheetContext = SheetManager.sheetContext(this.sheet())

        when (sheetContext)
        {
            is effect.Val -> this.sheet().onActive(SheetUIContext(sheetContext.value, context))
            is Err -> ApplicationLog.error(sheetContext.error)
        }
    }


    /**
     * This method saves the entire sheet in the database. It is intended to be used to save
     * a sheet that has just been loaded and has not ever been saved.
     *
     * This method is run asynchronously in the `CommonPool` context.
     */
    suspend fun saveSheet()
    {
        this.sheet.saveAll()
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_SessionSheetRecordValue =
        RowValue3(sessionSheetRecordTable,
                  PrimValue(this.sheetId),
                  PrimValue(this.sessionIndex),
                  PrimValue(this.lastActive))

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


    fun sheetUI() : SheetUI = this.context as SheetUI

}


data class SheetSummary(val name : String, val description : String)


interface SheetComponent
{
    fun onSheetComponentActive(sheetUIContext : SheetUIContext)

}


typealias SheetEff<A> = Eff<SheetError,Identity,A>


interface SheetUI
{

    fun pagePagerAdatper() : PagePagerAdapter

//    fun bottomNavigation() : AHBottomNavigation

    fun applyTheme(sheetId : SheetId, uiColors : UIColors)

    fun initializeSidebars(sheetContext : SheetContext)

    fun context() : Context

    fun rootSheetView() : View?

    fun hideActionBar()

    fun onSheetActive(sheet : Sheet)

}


sealed class SessionSheet

data class SessionSheetOfficial(val officialSheet : OfficialSheet) : SessionSheet()

data class SessionSheetDatabase(val sheetContext : SheetContext) : SessionSheet()


// Query({ Session() })
//   .filter



data class Session(override val id : UUID,
                   val sessionName : SessionName,
                   val lastActiveTime : SessionLastActiveTime,
                   var activeSheetId : Maybe<SheetId>,
                   val sheetRecords : MutableList<SessionSheetRecord>)
                    : ProdType
{


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor() : this(UUID.randomUUID(),
                         SessionName("Untitled Session"),
                         SessionLastActiveTime(System.currentTimeMillis()),
                         Nothing(),
                         mutableListOf())


    // -----------------------------------------------------------------------------------------
    // INDEXES
    // -----------------------------------------------------------------------------------------

    private val sheetRecordById : MutableMap<SheetId, SessionSheetRecord> =
                                this.sheetRecords.associateBy { it.sheetId() }
                                        as MutableMap<SheetId, SessionSheetRecord>


    // -----------------------------------------------------------------------------------------
    // READ
    // -----------------------------------------------------------------------------------------

    fun sheetRecords() : List<SessionSheetRecord> = this.sheetRecords


    fun isEmpty() : Boolean = this.sheetRecords().isEmpty()


    fun sheets() : List<Sheet> = this.sheetRecords().map { it.sheet() }


    fun activeSheet() : AppEff<Sheet>
    {
        val activeSheetId = this.activeSheetId
        return when (activeSheetId) {
            is Just -> sheetWithId(activeSheetId.value)
            else    -> effError(AppSheetError(NoActiveSheetInSession()))
        }
    }


    fun sheetRecordWithId(sheetId : SheetId) : AppEff<SessionSheetRecord> =
        note(this.sheetRecordById[sheetId], AppSheetError(SessionDoesNotHaveSheet(sheetId)))


    fun sheetWithId(sheetId : SheetId) : AppEff<Sheet> =
        note(this.sheetRecordById[sheetId]?.sheet(), AppSheetError(SessionDoesNotHaveSheet(sheetId)))


    // -----------------------------------------------------------------------------------------
    // OPERATIONS
    // -----------------------------------------------------------------------------------------

    fun addSheet(sheet : Sheet, isSaved : Boolean, sheetUI : SheetUI)
    {

        SheetManager.sheetContext(sheet)        apDo { sheetContext ->
        GameManager.engine(sheetContext.gameId) apDo { engine ->
            val sheetRecord = SessionSheetRecord.withDefaultView(sheet, sheetContext,
                                        SheetState(sheetContext, engine.mechanics()))

            sheetRecord.setIndex(sheetRecords().size)

            this.sheetRecords.add(sheetRecord)
            this.sheetRecordById.put(sheet.sheetId(), sheetRecord)

//            if (!isSaved)
//                launch(UI) { sheetRecord.saveSheet() }

            setSheetActive(sheet.sheetId(), sheetUI)

            sheetUI.onSheetActive(sheet)

            // launch(UI) { sheetRecords.save() }
        } }
    }


    private fun setSheetActive(sheetId : SheetId, sheetUI : SheetUI)
    {
        Log.d("***SHEET MANAGER", "set sheet active: $sheetId")
        this.sheetRecordWithId(sheetId) apDo {
            // Initialize Sheet
            it.onActive(sheetUI.context())

            // Set Active
            this.activeSheetId = Just(sheetId)
//            this.activeSheetId.doMaybe {
//                launch(UI) { save() }
//            }

            // Render
            SheetManager.render(sheetId, sheetUI)
        }
    }


    fun isActive() : Boolean = when (this.activeSheetId) {
        is Just -> true
        else    -> false
    }


    fun reload(sheetUI : SheetUI)
    {
        this.activeSheet() apDo {
            this.setSheetActive(it.sheetId(), sheetUI)
            sheetUI.onSheetActive(it)
        }
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_SessionValue =
        RowValue4(sessionTable,
                  PrimValue(this.sessionName),
                  PrimValue(this.lastActiveTime),
                  MaybePrimValue(this.activeSheetId),
                  CollValue(this.sheetRecords))

}


/**
 * Session Name
 */
data class SessionName(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SessionName>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<SessionName> = when (doc)
        {
            is DocText -> effValue(SessionName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Session Record Index
 */
data class SessionRecordIndex(val value : Int) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SessionRecordIndex>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<SessionRecordIndex> = when (doc)
        {
            is DocNumber -> effValue(SessionRecordIndex(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({this.value.toLong()})

}


/**
 * Session Record Index
 */
data class SessionLastActiveTime(val value : Long) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SessionLastActiveTime>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<SessionLastActiveTime> = when (doc)
        {
            is DocNumber -> effValue(SessionLastActiveTime(doc.number.toLong()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({this.value})

}



/**
 * Session Record Index
 */
data class SheetLastActiveTime(val value : Long) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SheetLastActiveTime>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<SheetLastActiveTime> = when (doc)
        {
            is DocNumber -> effValue(SheetLastActiveTime(doc.number.toLong()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({this.value})

}




