
package com.taletable.android.activity.session


import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.transition.Slide
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import com.taletable.android.R
import com.taletable.android.activity.sheet.page.PagePagerAdapter
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.Sheet
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialThemeLight
import com.taletable.android.router.Router
import com.taletable.android.rts.entity.*
import com.taletable.android.rts.entity.sheet.*
import maybe.Just
import io.reactivex.disposables.CompositeDisposable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.taletable.android.activity.entity.book.fragment.*
import com.taletable.android.activity.entity.book.navView
import com.taletable.android.activity.home.HomeActivity
import com.taletable.android.activity.session.campaign.campaignView
import com.taletable.android.activity.session.game.gameView
import com.taletable.android.app.AppError
import com.taletable.android.model.book.*
import com.taletable.android.model.campaign.Campaign
import com.taletable.android.model.entity.PersistedEntity
import com.taletable.android.model.game.Game
import com.taletable.android.model.session.sessionManifest
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.rts.session.*
import com.taletable.android.util.SimpleDividerItemDecoration
import effect.Val
import effect.effValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import maybe.Maybe
import java.util.*



object SheetActivityGlobal
{
    val touchHandler = Handler()

    private var longPressRunnable : Runnable? = null

    fun setLongPressRunnable(r : Runnable) {
        touchHandler.postDelayed(r, 1100)
        longPressRunnable = r
    }

    fun cancelLongPressRunnable() {
        touchHandler.removeCallbacks(longPressRunnable)
    }
}


object SheetActivityRequest
{

    val PROCEDURE_INVOCATION = 1

}



/**
 * Sheet Activity
 */
class SessionActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // STATE
    // -----------------------------------------------------------------------------------------

    var sessionId : SessionId? = null

    var entityId : EntityId? = null


    // STATE > Views
    // -----------------------------------------------------------------------------------------

    var pagePagerAdapter : PagePagerAdapter?   = null
    private var viewPager : ViewPager? = null

    private var bottomSheet : LinearLayout? = null
    var bottomSheetBehavior : BottomSheetBehavior<LinearLayout>? = null

    private var progressBar : ProgressBar? = null

    private val messageListenerDisposable : CompositeDisposable = CompositeDisposable()

    private var bookNavHistory : List<BookReference> = mutableListOf()


    private var entityListRecyclerViewAdapter : ActiveSessionRecyclerViewAdapter? = null


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        with(window) {

            // Check if we're running on Android 5.0 or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
                //exitTransition = Slide(Gravity.END)
                exitTransition = Slide(Gravity.END)
                allowEnterTransitionOverlap = true
            } else {
                // Swap without transition
            }
        }


        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_session)

        // (2) Read Parameters (or saved state)
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("session_id"))
            this.sessionId = this.intent.getSerializableExtra("session_id") as SessionId

        if (savedInstanceState != null)
            this.sessionId = savedInstanceState.getSerializable("session_id") as SessionId

        // Test session
        if (this.sessionId == null) {
            //this.sessionId = SessionId(UUID.fromString("56897634-288a-478e-bb59-eedf09d8aab6"))
            this.sessionId = SessionId(UUID.fromString("2c383a1b-b695-4553-bcf3-22eb4ed16b1c"))
        }

        // | Initialize Activity
        // -------------------------------------------------------------------------------------

        this.initializeListeners()

        this.applyTheme(officialAppThemeLight)

        this.initializeViews()

        this.initializeBottomSheetView()

        this.initializeSession()


        // (4) Configure UI
        // -------------------------------------------------------------------------------------

//        this.configureToolbar("Character Sheet", TextFont.RobotoCondensed, TextFontStyle.Regular, 18f)


        //this.initializeBottomSheetView()


    }


    override fun onDestroy()
    {
        super.onDestroy()
        this.messageListenerDisposable.clear()
    }


    // | Session
    // -----------------------------------------------------------------------------------------

    private fun initializeSession()
    {
        this.sessionId?.let { sessionId ->

            val activeSession = activeSession()
            when (activeSession) {
                is Just -> {
                    Log.d("***SHEET ACTVITIY", "active session found: ${activeSession.value}")
                }
                else -> {
                    Log.d("***SHEET ACTVITIY", "No active session")
                    this.sessionLoader(sessionId).doMaybe {
                        Log.d("***SHEET ACTVITIY", "Loading session: $it")
                        loadSession(it)
                    }
                }
            }
        }
    }


    private fun loadSession(sessionLoader : Session)
    {
        this.bottomSheetBehavior?.let {
            Log.d("***SHEET ACTVITIY", "set bottom sheet behavior to expanded")
            it.state = BottomSheetBehavior.STATE_EXPANDED
            // TODO use global scope? use view model? use activity scope??
            val context = this
            GlobalScope.launch(Dispatchers.Main) {
                openSession(sessionLoader, context)
            }
        }
    }


    private fun sessionLoader(sessionId : SessionId) : Maybe<Session> =
        sessionManifest(this).apply { it.session(sessionId) }


    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeListeners()
    {
        // Sheet messages
        val sheetMessageDisposable = Router.listen(MessageSheet::class.java)
                                           .subscribe(this::onSheetMessage)
        this.messageListenerDisposable.add(sheetMessageDisposable)

        // Session load messages
        val sessionMessageDisposable = Router.listen(MessageSessionLoad::class.java)
                                             .subscribe(this::onSessionLoadMessage)
        this.messageListenerDisposable.add(sessionMessageDisposable)
    }


    fun updateLoadProgress(sessionLoadUpdate : SessionLoadUpdate)
    {
        this.progressBar?.let { bar ->
            val updateAmount = bar.progress + (72 / sessionLoadUpdate.totalEntities)
            bar.progress = updateAmount
        }
    }


    private fun onSheetMessage(message : MessageSheet)
    {
        when (message)
        {
            is MessageSheetUpdate -> {
                this.sessionId?.let { sessionId ->
                    session(sessionId).doMaybe { session ->
                        // Temporry fix. need to add sheet id to message update
                        sheet(session.mainEntityId).doMaybe {
                            this.rootSheetView()?.let { rootView ->
                                it.update(message.update, rootView, this)
                            }
                        }
                    }
                }
            }
            is MessageSheetAction -> { }
        }

    }



    private fun onSessionLoadMessage(message : MessageSessionLoad)
    {
        when (message)
        {
            is MessageSessionEntityLoaded ->
            {
                this.updateLoadProgress(message.update)
            }
            is MessageSessionLoaded ->
            {
                this.progressBar?.progress = 100
                Log.d("***SHEET ACTVITIY", "Session loaded")
                session(message.sessionId).doMaybe { session ->
                    session.selectedEntityId = session.mainEntityId
                    setEntityActive(session.mainEntityId)
                    sheet(session.mainEntityId).doMaybe {
                        Log.d("***SHEET ACTVITIY", "Main entity is sheet")
                    }
                    this.entityListRecyclerViewAdapter?.items = session.entityViewList(this)
                }

                findViewById<FrameLayout>(R.id.session_loader_container)?.let {
                    it.visibility = View.GONE
                }

                findViewById<FrameLayout>(R.id.bottom_sheet_toolbar)?.let {
                    it.visibility = View.VISIBLE
                    it.addView(bottomSheetToolbarView(officialAppThemeLight, this))
                }
            }
        }
    }


    private fun initializeViews()
    {
        val nameView = this.findViewById(R.id.entity_name) as TextView?
        nameView?.typeface = Font.typeface(TextFont.Roboto, TextFontStyle.Medium, this)

//        val categoryView = this.findViewById(R.id.entity_category) as TextView?
//        categoryView?.typeface = Font.typeface(TextFont.Roboto, TextFontStyle.Italic, this)

        val pagePagerAdapter = PagePagerAdapter(supportFragmentManager)

        this.pagePagerAdapter = pagePagerAdapter

        this.viewPager = this.findViewById<ViewPager>(R.id.view_pager)

        this.bottomSheet = this.findViewById<LinearLayout>(R.id.bottom_sheet)
        this.bottomSheetBehavior = BottomSheetBehavior.from(this.bottomSheet)

        if (this.bottomSheet == null)
            Log.d("***SHEET ACTIVITY", "bottom sheet is null")

        this.viewPager?.adapter = pagePagerAdapter

        this.viewPager?.addOnPageChangeListener(object: ViewPager.OnPageChangeListener
        {
            override fun onPageScrollStateChanged(state : Int) { }

            override fun onPageScrolled(position : Int,
                                        positionOffset : Float,
                                        positionOffsetPixels : Int) { }

            override fun onPageSelected(position : Int) {
                SheetActivityGlobal.cancelLongPressRunnable()
            }
        })

        val tabLayout = this.findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)

    }


    private fun initializeBottomSheetView()
    {
        this.sessionId?.let { this.sessionLoader(it).toNullable() }?.let { session ->

            findViewById<TextView>(R.id.session_name)?.let {
                it.typeface = Font.typeface(TextFont.RobotoCondensed, TextFontStyle.Bold, this)
                it.text = session.sessionName.value
            }

            findViewById<TextView>(R.id.session_count_view)?.let {
                it.typeface = Font.typeface(TextFont.RobotoCondensed, TextFontStyle.Bold, this)
                it.text = session.entityIds.size.toString()
            }

            findViewById<FrameLayout>(R.id.bottom_sheet_content)?.let {
                val recyclerView = activeSessionRecyclerView(session, officialAppThemeLight, this)
                val adapter = ActiveSessionRecyclerViewAdapter(officialAppThemeLight, this)
                this.entityListRecyclerViewAdapter = adapter
                recyclerView.adapter = adapter
                it.addView(recyclerView)
            }

//            findViewById<TextView>(R.id.session_description)?.let { textView ->
//                activeSession().doMaybe {
//                    textView.text = it.sessionInfo.sessionSummary.value
//                    textView.typeface = Font.typeface(TextFont.RobotoCondensed, TextFontStyle.Regular, this)
//                }
//            }

            findViewById<FrameLayout>(R.id.session_loader_container)?.let {
                val progressBar = openSessionProgressBar(officialAppThemeLight, this)
                this.progressBar = progressBar
                it.addView(progressBar)
            }

        }

    }


    // -----------------------------------------------------------------------------------------
    // SHEET UI
    // -----------------------------------------------------------------------------------------

    fun pagePagerAdatper() : PagePagerAdapter = this.pagePagerAdapter!!


    fun rootSheetView() : View? = this.viewPager


    fun applyTheme(theme : Theme)
    {
        val uiColors = theme.uiColors()

        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = this.window

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            val toolbarColorTheme = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_9"))))
            window.statusBarColor = theme.colorOrBlack(toolbarColorTheme)
        }

    }


    fun applyEntityTheme(theme : Theme)
    {
        val uiColors = theme.uiColors()

        // STATUS BAR
        // -------------------------------------------------------------------------------------
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//        {
//            val window = this.window
//
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//
//            window.statusBarColor = theme.colorOrBlack(uiColors.toolbarBackgroundColorId())
//        }

        // TITLE
        // -------------------------------------------------------------------------------------
//        val titleView = this.findViewById<TextView>(R.id.toolbar_title) as TextView
//        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))

        // TAB LAYOUT
        // -------------------------------------------------------------------------------------
        val tabLayout = this.findViewById<CustomTabLayout>(R.id.tab_layout) as CustomTabLayout

        // Tab Layout > Background
        tabLayout.setBackgroundColor(theme.colorOrBlack(uiColors.tabBarBackgroundColorId()))

        // Tab Layout > Text
        tabLayout.setTabTextColors(theme.colorOrBlack(uiColors.tabTextNormalColorId()),
                                   theme.colorOrBlack(uiColors.tabTextSelectedColorId()))

        // Tab Layout > Underline
        tabLayout.setSelectedTabIndicatorColor(theme.colorOrBlack(uiColors.tabUnderlineColorId()))
    }


    fun context() : Context = this



    // -----------------------------------------------------------------------------------------
    // SHEET
    // -----------------------------------------------------------------------------------------

    /**
     * Just display sheet.
     */
    fun setEntityActive(entityId : EntityId)
    {
        val entity = entityRecord(entityId) ap { effValue<AppError,Entity>(it.entity())}
        when (entity) {
            is Val -> {
                this._setEntityActive(entity.value)
            }
        }
    }


    fun _setEntityActive(entity : Entity)
    {
        Log.d("***SESSION ACTIVITY", "setting entity active")

        if (this.entityId != null && this.entityId == entity.entityId())
            return

        this.entityId = entity.entityId()

        val nameView = this.findViewById(R.id.entity_name) as TextView?
        nameView?.text = entity.name()

//        val categoryView = this.findViewById(R.id.entity_category) as TextView?
//        categoryView?.text = entity.category()

        val entityButtonView = this.findViewById(R.id.entity_button) as ImageButton?
        //entityButtonView?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_open_book))

        entityButtonView?.setOnClickListener {
            Log.d("***SESSION ACTIVITY", "setting entity button on click")
            val newFragment = BookSettingsFragment.newInstance(entity.entityId())

            newFragment.enterTransition = Slide(Gravity.BOTTOM)
            newFragment.exitTransition  = Slide(Gravity.TOP)


            this.findViewById<HorizontalScrollView>(R.id.toolbar_nav)?.let {
                it.visibility = View.GONE
            }

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.session_content, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        when (entity)
        {
            is Sheet -> {
                entity.onActive(entity.entityId(), this)
                this.renderSheet(entity)
            }
            is Campaign -> {
                this.renderCampaign(entity)
            }
            is Game -> {
                this.renderGame(entity)
            }
            is Book -> {
                entity.onActive(entity.entityId(), this)
                this.renderBook(entity)
            }
            else -> { }
        }
    }


    fun renderSheet(sheet : Sheet)
    {
        this.applyEntityTheme(officialThemeLight)

        val start = System.currentTimeMillis()

        this.findViewById<TabLayout>(R.id.tab_layout)?.let {
            it.visibility = View.VISIBLE
            it.setupWithViewPager(viewPager)
        }

        this.findViewById<HorizontalScrollView>(R.id.toolbar_nav)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<LinearLayout>(R.id.session_content)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<ViewPager>(R.id.view_pager)?.let {
            it.visibility = View.VISIBLE
        }

        this.findViewById<LinearLayout>(R.id.toolbar_bottom_padding)?.let {
            it.visibility = View.GONE
        }

        val section = sheet.sections().firstOrNull()
        if (section != null) {
            pagePagerAdatper().setPages(section.pages(), sheet.entityId())
        }

        val end = System.currentTimeMillis()

        Log.d("***SHEET ACTIVITY", "time to render ms: " + (end - start).toString())
    }


    fun renderCampaign(campaign : Campaign)
    {
        this.findViewById<TabLayout>(R.id.tab_layout)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<HorizontalScrollView>(R.id.toolbar_nav)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<ViewPager>(R.id.view_pager)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<LinearLayout>(R.id.toolbar_bottom_padding)?.let {
            it.visibility = View.VISIBLE
        }

        this.findViewById<LinearLayout>(R.id.session_content)?.let {
            it.visibility = View.VISIBLE
            it.removeAllViews()
            it.addView(campaignView(officialAppThemeLight, this))
        }
    }


    fun renderGame(game : Game)
    {
        this.findViewById<TabLayout>(R.id.tab_layout)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<HorizontalScrollView>(R.id.toolbar_nav)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<ViewPager>(R.id.view_pager)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<LinearLayout>(R.id.toolbar_bottom_padding)?.let {
            it.visibility = View.VISIBLE
        }

        this.findViewById<LinearLayout>(R.id.session_content)?.let {
            it.visibility = View.VISIBLE
            it.removeAllViews()
            it.addView(gameView(officialAppThemeLight, this))
        }
    }


    fun renderBook(book : Book)
    {
        this.findViewById<TabLayout>(R.id.tab_layout)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<HorizontalScrollView>(R.id.toolbar_nav)?.let {
            it.visibility = View.VISIBLE
        }

        this.findViewById<ViewPager>(R.id.view_pager)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<LinearLayout>(R.id.toolbar_bottom_padding)?.let {
            it.visibility = View.VISIBLE
        }

        this.findViewById<LinearLayout>(R.id.session_content)?.let {
            it.visibility = View.VISIBLE
            it.removeAllViews()
            it.addView(gameView(officialAppThemeLight, this))
        }

        this.setCurrentBookReference(BookReferenceBook(book.bookId))
    }


    fun previousBookReference()
    {
        val history = this.bookNavHistory
        if (history.size > 1) {
            val setToRef = history[history.size - 2]
            this.bookNavHistory = bookNavHistory.dropLast(1)
            this.setCurrentBookReference(setToRef)
        }
    }


    fun setSearchView(bookId : EntityId)
    {
        val newFragment = BookSearchFragment.newInstance(bookId)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.session_content, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    fun setCurrentBookReference(bookReference : BookReference)
    {
        this.findViewById<LinearLayout>(R.id.session_content)?.let {
            it.visibility = View.VISIBLE
            it.removeAllViews()
        }

        this.findViewById<HorizontalScrollView>(R.id.toolbar_nav)?.let {
            it.visibility = View.VISIBLE
            it.removeAllViews()
        }

        this.bookNavHistory = this.bookNavHistory.plus(bookReference)

        val prevBookReference = this.bookNavHistory.lastOrNull()

        when (bookReference)
        {
            is BookReferenceBook ->
            {
                val newFragment = BookFragment.newInstance(bookReference.bookId())

                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.session_content, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
            is BookReferenceChapter ->
            {
                val newFragment = ChapterFragment.newInstance(bookReference.chapterId(), bookReference.bookId())

                when (prevBookReference) {
                    is BookReferenceBook -> {
                        newFragment.enterTransition = Slide(Gravity.END)
                    }
                    else -> {
                        newFragment.enterTransition = Slide(Gravity.START)
                    }
                }


                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.session_content, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                this.findViewById<HorizontalScrollView>(R.id.toolbar_nav)?.let {
                    it.addView(navView(bookReference, officialAppThemeLight, this))
                }
            }
            is BookReferenceSection ->
            {
                val newFragment = SectionFragment.newInstance(bookReference.sectionId(),
                                                              bookReference.chapterId(),
                                                              bookReference.bookId())

                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.session_content, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                this.findViewById<HorizontalScrollView>(R.id.toolbar_nav)?.let {
                    it.addView(navView(bookReference, officialAppThemeLight, this))
                }
            }
            is BookReferenceSubsection ->
            {
                val newFragment = SubsectionFragment.newInstance(bookReference.subsectionId(),
                                                                 bookReference.sectionId(),
                                                                 bookReference.chapterId(),
                                                                 bookReference.bookId())

                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.session_content, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                this.findViewById<HorizontalScrollView>(R.id.toolbar_nav)?.let {
                    it.addView(navView(bookReference, officialAppThemeLight, this))
                }

            }
            is BookReferenceContent -> { }
            is BookReferenceCard -> {
                val newFragment = BookCardFragment.newInstance(bookReference.bookId(), bookReference.cardId())

                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.session_content, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                this.findViewById<HorizontalScrollView>(R.id.toolbar_nav)?.let {
                    it.addView(navView(bookReference, officialAppThemeLight, this))
                }
                Log.d("***SESSION ACTIVITY", "going to book card")
            }
        }
    }



}


//
//class MainTabBarUI(val sheetId : EntityId,
//                   val theme : Theme,
//                   val sheetActivity : SessionActivity)
//{
//
//    val context = sheetActivity
//
//    var pagesTabLayoutView   : LinearLayout? = null
//    var pagesTabTextView     : TextView? = null
//    var tasksTabLayoutView   : LinearLayout? = null
//    var tasksTabTextView     : TextView? = null
//    var historyTabLayoutView : LinearLayout? = null
//    var historyTabTextView   : TextView? = null
//
//
//    val bgNormalColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
//
//
//    val bgSelectedColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//
//
//    val selectedColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//
//    val normalColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
//
//
//    private fun setSelectedTab(tabIndex : Int)
//    {
//        when (tabIndex)
//        {
//            1 -> {
////                pagesTabLayoutView?.setBackgroundColor(theme.colorOrBlack(bgSelectedColorTheme))
////                pagesTabLayoutView?.setBackgroundColor(Color.WHITE)
////                tasksTabLayoutView?.setBackgroundColor(theme.colorOrBlack(bgNormalColorTheme))
////                historyTabLayoutView?.setBackgroundColor(theme.colorOrBlack(bgNormalColorTheme))
//
//                pagesTabTextView?.setTextColor(theme.colorOrBlack(selectedColorTheme))
//                tasksTabTextView?.setTextColor(theme.colorOrBlack(normalColorTheme))
//                historyTabTextView?.setTextColor(theme.colorOrBlack(normalColorTheme))
//            }
//            2 -> {
////                pagesTabLayoutView?.setBackgroundColor(theme.colorOrBlack(bgNormalColorTheme))
//////                tasksTabLayoutView?.setBackgroundColor(theme.colorOrBlack(bgSelectedColorTheme))
////                tasksTabLayoutView?.setBackgroundColor(Color.WHITE)
////                historyTabLayoutView?.setBackgroundColor(theme.colorOrBlack(bgNormalColorTheme))
//
//                pagesTabTextView?.setTextColor(theme.colorOrBlack(normalColorTheme))
//                tasksTabTextView?.setTextColor(theme.colorOrBlack(selectedColorTheme))
//                historyTabTextView?.setTextColor(theme.colorOrBlack(normalColorTheme))
//            }
//            3 -> {
////                pagesTabLayoutView?.setBackgroundColor(theme.colorOrBlack(bgNormalColorTheme))
////                tasksTabLayoutView?.setBackgroundColor(theme.colorOrBlack(bgNormalColorTheme))
////                historyTabLayoutView?.setBackgroundColor(Color.WHITE)
//
//                pagesTabTextView?.setTextColor(theme.colorOrBlack(normalColorTheme))
//                tasksTabTextView?.setTextColor(theme.colorOrBlack(normalColorTheme))
//                historyTabTextView?.setTextColor(theme.colorOrBlack(selectedColorTheme))
//            }
//        }
//
//    }
//
//
//    fun view() : View
//    {
//        val layout = this.viewLayout()
//
//        // Pages
//        val pagesOnClick = View.OnClickListener {
//
//            val tabLayout = sheetActivity.findViewById<TabLayout>(R.id.tab_layout)
//            tabLayout?.visibility = View.VISIBLE
//            tabLayout?.tabMode = TabLayout.MODE_SCROLLABLE
//
//            val viewPager = sheetActivity.findViewById<View>(R.id.view_pager) as ViewPager?
//            viewPager?.adapter = sheetActivity.pagePagerAdapter
//            this.setSelectedTab(1)
//        }
//        val pagesTabView = this.buttonView(R.string.pages, 1, pagesOnClick)
//        this.pagesTabLayoutView = pagesTabView
//        layout.addView(pagesTabView)
//
//        // Tasks
//        val tasksOnClick = View.OnClickListener {
//            sheetActivity.findViewById<View>(R.id.tab_layout)?.visibility = View.GONE
//            val viewPager = sheetActivity.findViewById<View>(R.id.view_pager) as ViewPager?
//            viewPager?.adapter = TaskPagerAdapter(sheetActivity.supportFragmentManager, sheetId)
//            this.setSelectedTab(2)
//        }
//        val tasksTabView = this.buttonView(R.string.tasks, 2, tasksOnClick)
//        this.tasksTabLayoutView = tasksTabView
//        layout.addView(tasksTabView)
//
//        // History
//        val historyOnClick = View.OnClickListener {
//
//            val tabLayout = sheetActivity.findViewById<TabLayout>(R.id.tab_layout)
//            tabLayout?.visibility = View.VISIBLE
//            tabLayout?.tabMode = TabLayout.MODE_FIXED
//            val viewPager = sheetActivity.findViewById<View>(R.id.view_pager) as ViewPager?
//            viewPager?.adapter = HistoryPagerAdapter(sheetActivity.supportFragmentManager, sheetId)
//            this.setSelectedTab(3)
//        }
//        val historyTabView = this.buttonView(R.string.history, 3, historyOnClick)
//        this.historyTabLayoutView = historyTabView
//        layout.addView(historyTabView)
//
//        this.setSelectedTab(1)
//
//        return layout
//    }
//
//
//    private fun viewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.HORIZONTAL
//
////        layout.margin.topDp     = 2f
////        layout.margin.bottomDp  = 2f
////        layout.margin.leftDp    = 1f
////        layout.margin.rightDp   = 1f
//
//        return layout.linearLayout(context)
//    }
//
//
//
//    private fun buttonView(labelStringId : Int,
//                           index : Int,
//                           onClick : View.OnClickListener) : LinearLayout
//    {
//        val layout = this.buttonViewLayout()
//
//        val labelView = this.buttonLabelView(labelStringId)
//        when (index) {
//            1 -> this.pagesTabTextView = labelView
//            2 -> this.tasksTabTextView = labelView
//            3 -> this.historyTabTextView = labelView
//        }
//
//        layout.addView(labelView)
//
//        layout.setOnClickListener(onClick)
//
//        layout.addView(this.buttonBottomBorderView())
//
//        return layout
//    }
//
//
//    private fun buttonViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = 0
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.weight           = 1f
//
//        layout.gravity          = Gravity.CENTER
//
//        layout.orientation      = LinearLayout.VERTICAL
//
//        layout.backgroundColor  = Color.WHITE
//
//        layout.padding.topDp    = 8f
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun buttonLabelView(labelId : Int) : TextView
//    {
//        val label                   = TextViewBuilder()
//
//        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
//        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        label.text                  = context.getString(labelId) // .toUpperCase()
//
//        label.gravity               = Gravity.CENTER
//
//        label.font                  = Font.typeface(TextFont.default(),
//                                                    TextFontStyle.Medium,
//                                                    context)
//
//        label.sizeSp                 = 16f
//
//        return label.textView(context)
//    }
//
//
//    private fun buttonBottomBorderView() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.heightDp         = 1
//
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
//        layout.backgroundColor  = colorOrBlack(colorTheme, sheetId)
//
//        layout.margin.topDp = 8f
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun countView(countString : String) : TextView
//    {
//        val count                   = TextViewBuilder()
//
//        count.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
//        count.height                = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        count.backgroundResource    = R.drawable.bg_session_step
//
//        val indexColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
//
//        count.color                 = theme.colorOrBlack(indexColorTheme)
//
//        count.font                  = Font.typeface(TextFont.default(),
//                                                    TextFontStyle.Bold,
//                                                    context)
//
//        count.text                  = countString
//
//        count.gravity               = Gravity.CENTER
//
//        count.sizeSp                = 17f
//
//        count.margin.rightDp        = 10f
//
//        return count.textView(context)
//    }
//
//}



//
//fun sessionView(session : Session, theme : Theme, sessionActivity : SessionActivity) :  View
//{
//    return activeSessionRecyclerView(session, theme, sessionActivity)
//}


fun activeSessionRecyclerView(session : Session,
                              theme : Theme,
                              sessionActivity : SessionActivity) : RecyclerView
{
    val recyclerView                = RecyclerViewBuilder()
    val context = sessionActivity

    recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
    recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

    recyclerView.layoutManager      = LinearLayoutManager(context)

//    recyclerView.adapter            = ActiveSessionRecyclerViewAdapter(theme, context)

//    recyclerView.padding.leftDp     = 8f
//    recyclerView.padding.rightDp    = 8f

    val dividerColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_5"))))
    val dividerColor              = theme.colorOrBlack(dividerColorTheme)
    recyclerView.divider            = SimpleDividerItemDecoration(context, dividerColor)

//    recyclerView.padding.topDp      = 6f

//    recyclerView.padding.bottomDp   = 60f
    recyclerView.clipToPadding      = false

    return recyclerView.recyclerView(context)
}


// | Bottom Sheet > Toolbar View
// -----------------------------------------------------------------------------------------

fun bottomSheetToolbarView(theme : Theme, activity : AppCompatActivity) : ViewGroup
{
    val layout              = bottomSheetToolbarViewLayout(activity)

    val homeButtonView = bottomSheetToolbarHomeButtonView(theme, activity)
    homeButtonView.setOnClickListener {
        val intent = Intent(activity, HomeActivity::class.java)
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        } else {
            activity.startActivity(intent)
        }

    }

    val editButtonView = bottomSheetToolbarEditButtonView(theme, activity)

    layout.addView(homeButtonView)
    layout.addView(editButtonView)

    return layout
}


fun bottomSheetToolbarViewLayout(context : Context) : RelativeLayout
{
    val layoutBuilder               = RelativeLayoutBuilder()

    layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height            = LinearLayout.LayoutParams.MATCH_PARENT

    layoutBuilder.padding.leftDp    = 16f
    layoutBuilder.padding.rightDp   = 16f

    layoutBuilder.backgroundColor   = Color.WHITE

    return layoutBuilder.relativeLayout(context)
}


private fun bottomSheetToolbarHomeButtonView(theme : Theme, context : Context) : LinearLayout
{
    // 1 | Declarations
    // -----------------------------------------------------------------------------------------

    val layoutBuilder                   = LinearLayoutBuilder()
    val iconViewBuilder                 = ImageViewBuilder()
    val labelViewBuilder                = TextViewBuilder()

    // 2 | Layout
    // -----------------------------------------------------------------------------------------

    layoutBuilder.layoutType            = LayoutType.RELATIVE
    layoutBuilder.width                 = RelativeLayout.LayoutParams.WRAP_CONTENT
    layoutBuilder.height                = RelativeLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation           = LinearLayout.HORIZONTAL

//    layoutBuilder.layoutGravity         = Gravity.START or Gravity.CENTER_VERTICAL
//    layoutBuilder.gravity               = Gravity.CENTER_VERTICAL

    layoutBuilder.addRule(RelativeLayout.CENTER_VERTICAL)
    layoutBuilder.addRule(RelativeLayout.ALIGN_PARENT_START)

    layoutBuilder.child(iconViewBuilder)
                 .child(labelViewBuilder)

    // 3 | Icon
    // -----------------------------------------------------------------------------------------

    iconViewBuilder.widthDp             = 25
    iconViewBuilder.heightDp            = 25

    iconViewBuilder.image               = R.drawable.icon_exit
    iconViewBuilder.scaleX              = -1f

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_18"))))
    iconViewBuilder.color               = theme.colorOrBlack(iconColorTheme)

    iconViewBuilder.margin.rightDp      = 6f

    // 3 | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.text               = "Leave Session"

    labelViewBuilder.font               = Font.typeface(TextFont.RobotoCondensed,
                                                        TextFontStyle.Bold,
                                                        context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_18"))))
    labelViewBuilder.color              = theme.colorOrBlack(labelColorTheme)

    labelViewBuilder.sizeSp             = 19f

    return layoutBuilder.linearLayout(context)
}


private fun bottomSheetToolbarEditButtonView(theme : Theme, context : Context) : LinearLayout
{
    // 1 | Declarations
    // -----------------------------------------------------------------------------------------

    val layoutBuilder                   = LinearLayoutBuilder()
    val iconViewBuilder                 = ImageViewBuilder()
    val labelViewBuilder                = TextViewBuilder()

    // 2 | Layout
    // -----------------------------------------------------------------------------------------

    layoutBuilder.layoutType            = LayoutType.RELATIVE
    layoutBuilder.width                 = RelativeLayout.LayoutParams.WRAP_CONTENT
    layoutBuilder.height                = RelativeLayout.LayoutParams.WRAP_CONTENT

//    layoutBuilder.orientation           = LinearLayout.HORIZONTAL

//    layoutBuilder.layoutGravity         = Gravity.END or Gravity.CENTER_VERTICAL
//    layoutBuilder.gravity               = Gravity.CENTER_VERTICAL

    layoutBuilder.addRule(RelativeLayout.CENTER_VERTICAL)
    layoutBuilder.addRule(RelativeLayout.ALIGN_PARENT_END)

    layoutBuilder.child(iconViewBuilder)
                 // .child(labelViewBuilder)

    // 3 | Icon
    // -----------------------------------------------------------------------------------------

    iconViewBuilder.widthDp             = 24
    iconViewBuilder.heightDp            = 24

    iconViewBuilder.image               = R.drawable.icon_menu

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_10"))))
    iconViewBuilder.color               = theme.colorOrBlack(iconColorTheme)

//    iconViewBuilder.margin.rightDp      = f

    // 3 | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.textId             = R.string.edit

    labelViewBuilder.font               = Font.typeface(TextFont.RobotoCondensed,
                                                        TextFontStyle.Bold,
                                                        context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_20"))))
    labelViewBuilder.color              = theme.colorOrBlack(labelColorTheme)

    labelViewBuilder.sizeSp             = 19f

    return layoutBuilder.linearLayout(context)
}


fun otherEntitiesHeaderView(numOfComponents : Int, theme : Theme, context : Context) : RelativeLayout
{
    val layout = otherEntitiesHeaderViewLayout(context)

    layout.addView(otherEntitiesHeaderTitleView(theme, context))

    layout.addView(entityToolbarButtonsView(theme, context))

    return layout
}


private fun otherEntitiesHeaderViewLayout(context : Context) : RelativeLayout
{
    val layout              = RelativeLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.padding.topDp    = 16f
    layout.padding.bottomDp = 16f

    layout.padding.leftDp   = 16f
    layout.padding.rightDp  = 16f

    layout.backgroundColor  = Color.WHITE

    return layout.relativeLayout(context)
}


private fun otherEntitiesHeaderTitleView(theme : Theme, context : Context) : TextView
{
    val name                = TextViewBuilder()

    name.layoutType         = LayoutType.RELATIVE
    name.width              = RelativeLayout.LayoutParams.WRAP_CONTENT
    name.height             = RelativeLayout.LayoutParams.WRAP_CONTENT

    name.addRule(RelativeLayout.CENTER_VERTICAL)
    name.addRule(RelativeLayout.ALIGN_PARENT_START)

    name.text               = "Books"

    name.font               = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Bold,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_22"))))
    name.color              = theme.colorOrBlack(colorTheme)

    name.sizeSp             = 19.5f

    return name.textView(context)
}


private fun entityToolbarButtonsView(theme : Theme, context : Context) : LinearLayout
{
    val layout = entityToolbarButtonsViewLayout(context)

    layout.addView(entityToolbarButtonView(R.drawable.icon_slider, 24, false, theme, context))
    layout.addView(entityToolbarButtonView(R.drawable.icon_day_view, 26, true, theme, context))

    return layout
}



private fun entityToolbarButtonsViewLayout(context : Context) : LinearLayout
{
    val layout                  = LinearLayoutBuilder()

    layout.layoutType           = LayoutType.RELATIVE
    layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
    layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

    layout.addRule(RelativeLayout.CENTER_VERTICAL)
    layout.addRule(RelativeLayout.ALIGN_PARENT_END)

    layout.orientation          = LinearLayout.HORIZONTAL

    layout.gravity              = Gravity.CENTER_VERTICAL

    return layout.linearLayout(context)
}


private fun entityToolbarButtonView(iconId : Int, iconSize : Int, addMargin : Boolean, theme : Theme, context : Context) : LinearLayout
{
    // 1 | Declarations
    // -----------------------------------------------------------------------------------------

    val layout                  = LinearLayoutBuilder()
    val iconView                = ImageViewBuilder()

    // 2 | Layout
    // -----------------------------------------------------------------------------------------

    layout.layoutType           = LayoutType.RELATIVE
    layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
    layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

    if (addMargin)
        layout.margin.leftDp    = 24f

    layout.child(iconView)

    // 3 | Icon
    // -----------------------------------------------------------------------------------------

    iconView.widthDp            = iconSize
    iconView.heightDp           = iconSize

    iconView.image              = iconId

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_18"))))
    iconView.color              = theme.colorOrBlack(colorTheme)

    return layout.linearLayout(context)
}


// | VIEW > Entiy Card
// -----------------------------------------------------------------------------------------

fun entityCardView(theme : Theme, context : Context) : LinearLayout
{
    val layout = entityCardViewLayout(theme, context)

    layout.addView(entityCardLeftView(theme, context))

    layout.addView(entityCardRightView(theme, context))

    return layout
}


private fun entityCardViewLayout(theme : Theme, context : Context) : LinearLayout
{
    val layout                  = LinearLayoutBuilder()

    layout.id                   = R.id.entity_card_layout

    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation          = LinearLayout.HORIZONTAL

//    layout.backgroundResource   = R.drawable.bg_card_flat
    layout.backgroundColor      = Color.WHITE

    //layout.corners              = Corners(2.0, 2.0, 2.0, 2.0)

    layout.gravity              = Gravity.TOP

    layout.padding.topDp        = 8f
    layout.padding.bottomDp     = 12f

    layout.padding.leftDp       = 16f
    layout.padding.rightDp      = 16f

//    layout.margin.bottomDp      = 1f

    return layout.linearLayout(context)
}


private fun entityCardLeftView(theme : Theme, context : Context) : LinearLayout
{
    val layout = entityCardLeftLayout(theme, context)

    layout.addView(entityCardImageView(theme, context))

    return layout
}


private fun entityCardLeftLayout(theme : Theme, context : Context) : LinearLayout
{
    val layout                  = LinearLayoutBuilder()

    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation          = LinearLayout.VERTICAL

    layout.padding.topDp        = 4f

    return layout.linearLayout(context)
}


private fun entityCardImageView(theme : Theme, context : Context) : LinearLayout
{
    // 1 | Declarations
    // -------------------------------------------------------------------------

    val layout                  = LinearLayoutBuilder()
    val iconViewBuilder         = ImageViewBuilder()

    // 2 | Layout
    // -------------------------------------------------------------------------

    layout.widthDp              = 50
    layout.heightDp             = 50

//    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.corners              = Corners(3.0,3.0,3.0,3.0)

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
    layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

    layout.gravity              = Gravity.CENTER

    layout.child(iconViewBuilder)

    // 2 | Icon View Builder
    // -------------------------------------------------------------------------

    iconViewBuilder.widthDp         = 30
    iconViewBuilder.heightDp        = 30

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_22"))))

    iconViewBuilder.image           = R.drawable.icon_book

    iconViewBuilder.color           = theme.colorOrBlack(iconColorTheme)

    return layout.linearLayout(context)
}


private fun entityCardRightView(theme : Theme, context : Context) : LinearLayout
{
    val layout = entityCardRightLayout(theme, context)

    layout.addView(entityCardNameView(theme, context))

    layout.addView(entityCardSummaryView(theme, context))

    return layout
}


private fun entityCardRightLayout(theme : Theme, context : Context) : LinearLayout
{
    val layout                  = LinearLayoutBuilder()

    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation          = LinearLayout.VERTICAL

    layout.margin.leftDp        = 12f
    layout.margin.rightDp       = 12f

    return layout.linearLayout(context)
}


private fun entityCardNameView(theme : Theme,
                               context : Context) : TextView
{
    val name                = TextViewBuilder()

    name.id                 = R.id.entity_card_name

    name.width              = LinearLayout.LayoutParams.MATCH_PARENT
    name.height             = LinearLayout.LayoutParams.WRAP_CONTENT


    name.font               = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Bold,
                                            context)


    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    name.color              = theme.colorOrBlack(colorTheme)
    // name.color              = Color.WHITE

    name.sizeSp             = 19f

    return name.textView(context)
}



private fun entityCardSummaryView(theme : Theme,
                                  context : Context) : TextView
{
    val summary             = TextViewBuilder()

    summary.id              = R.id.entity_card_summary

    summary.width            = LinearLayout.LayoutParams.WRAP_CONTENT
    summary.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    summary.font             = Font.typeface(TextFont.Roboto,
                                             TextFontStyle.Regular,
                                             context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
    summary.color            = theme.colorOrBlack(colorTheme)

    summary.sizeSp           = 16f

    return summary.textView(context)
}


// -----------------------------------------------------------------------------------------
// RECYCLER VIEW ADPATER
// -----------------------------------------------------------------------------------------

class ActiveSessionRecyclerViewAdapter(val theme : Theme,
                                       val sessionActivity : SessionActivity)
                                        : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    // | PROPERTIES
    // -------------------------------------------------------------------------------------

    val context = sessionActivity

//    private val CURRENT_HEADER = 0
//    private val CURRENT_ENTITY  = 1
    private val HEADER  = 0
    private val ENTITY  = 1


    // | ITEMS
    // -------------------------------------------------------------------------------------

    var items : List<Any> = listOf()
        set(newItems) {
            field = newItems
            this.notifyDataSetChanged()
        }


    // | RECYCLER VIEW ADAPTER
    // -------------------------------------------------------------------------------------

    override fun getItemViewType(position : Int) : Int
    {
        val itemAtPosition = this.items[position]
        return when (itemAtPosition) {
            is SessionListHeader -> HEADER
            else                 -> ENTITY
        }
    }


    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : RecyclerView.ViewHolder = when (viewType)
    {
        HEADER ->
        {
            val toolbarView = otherEntitiesHeaderView(items.size, theme, context)
            OtherHeaderViewHolder(toolbarView)
        }
        else ->
        {
            val cardView = entityCardView(theme, context)
            EntityCardViewHolder(cardView, theme, sessionActivity)
        }
    }


    override fun onBindViewHolder(viewHolder : RecyclerView.ViewHolder, position : Int)
    {
        val item = this.items[position]
        when (item) {
            is SessionListHeader -> {
            }
            is PersistedEntity -> {
                val entityViewHolder = viewHolder as EntityCardViewHolder
                entityViewHolder.setEntity(item)
            }
        }
    }


    override fun getItemCount() = this.items.size

}





// ---------------------------------------------------------------------------------------------
// | View Holder: Entity Card
// ---------------------------------------------------------------------------------------------

/**
 * The View Holder caches a view for each item.
 */
class EntityCardViewHolder(itemView : View,
                           val theme : Theme,
                           val sessionActivity : SessionActivity)
                           : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout                  : LinearLayout? = null
    var nameView                : TextView? = null
    var summaryView             : TextView? = null

//    val context = sessionActivity


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout             = itemView.findViewById(R.id.entity_card_layout)
        this.nameView           = itemView.findViewById(R.id.entity_card_name)
        this.summaryView        = itemView.findViewById(R.id.entity_card_summary)
    }



    fun setEntity(entity : PersistedEntity)
    {
//        this.layout?.setOnClickListener {
//            when (entity) {
//                is Book -> {
//                    val intent = Intent(sessionActivity, BookActivity::class.java)
//                    val bookReference = BookReferenceBook(entity.entityId())
//                    intent.putExtra("book_reference", bookReference)
//                    sessionActivity.startActivity(intent)
//                }
//            }
//        }

        this.nameView?.text = entity.name
        this.summaryView?.text = entity.summary

        this.layout?.setOnClickListener {
            sessionActivity.setEntityActive(entity.entityId)

            sessionActivity.bottomSheetBehavior?.let {
                it.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }



}



fun openSessionProgressBar(theme : Theme, context : Context) : ProgressBar
{
    val bar                 = ProgressBarBuilder()

    bar.id                  = R.id.progress_bar

    bar.width               = LinearLayout.LayoutParams.MATCH_PARENT
    bar.height              = LinearLayout.LayoutParams.WRAP_CONTENT

    bar.progressDrawableId  = R.drawable.progress_bar_load_session

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
    bar.backgroundColor     = theme.colorOrBlack(bgColorTheme)

    return bar.progressBar(context)
}




class OtherHeaderViewHolder(val toolbarView : ViewGroup) : RecyclerView.ViewHolder(toolbarView)

class CurrentHeaderViewHolder(val toolbarView : ViewGroup) : RecyclerView.ViewHolder(toolbarView)




//class SessionCurrentEntityHeader()
//
//data class SessionCurrentEntity(val entity : PersistedEntity)

class SessionListHeader()
