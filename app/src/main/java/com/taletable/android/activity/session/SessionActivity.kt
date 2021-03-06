
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
import androidx.transition.Fade
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
import com.taletable.android.R
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.util.Util
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable


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



val loaderViewIds : MutableMap<EntityId,Int> = mutableMapOf()


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
                //exitTransition = android.transition.Fade()
                allowEnterTransitionOverlap = true
            } else {
                // Swap without transition
            }
        }


        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(com.taletable.android.R.layout.activity_session)

        // (2) Read Parameters (or saved state)
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("session_id"))
            this.sessionId = this.intent.getSerializableExtra("session_id") as SessionId

//        if (savedInstanceState != null)
//            this.sessionId = savedInstanceState.getSerializable("session_id") as SessionId

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


//        this.supportFragmentManager.addOnBackStackChangedListener {
//            Log.d("***SESSION ACTVITY", "on back stack changed")
//        }
    }


    override fun onBackPressed() {
        Log.d("***SESSION ACTVITY", "book nav history:  ${this.bookNavHistory}")
        previousBookReference()
        //super.onBackPressed()
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
                    val session = activeSession.value
                    Log.d("***SHEET ACTIVITY", "active session found: ${activeSession.value}")
                    setEntityActive(session.mainEntityId)
                    this.entityListRecyclerViewAdapter?.items = session.entityViewList(this)
                }
                else -> {
                    this.sessionLoader(sessionId).doMaybe {
                        loadSession(it)
                    }
                }
            }
        }
    }


    private fun loadSession(sessionLoader : Session)
    {
        this.bottomSheetBehavior?.let {
            it.state = BottomSheetBehavior.STATE_EXPANDED
            // TODO use global scope? use view model? use activity scope??
            val context = this
            GlobalScope.launch(Dispatchers.Main) {
                openSession(sessionLoader, context)
            }
            this.entityListRecyclerViewAdapter?.items = sessionLoader.entityViewList(this)
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

                loaderViewIds[message.update.entityId]?.let {
                    findViewById<ProgressBar>(it)?.let {
                        it.visibility =  View.GONE
                    }
                }
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

//                findViewById<FrameLayout>(com.taletable.android.R.id.session_loader_container)?.let {
//                    it.visibility = View.GONE
//                }


            }
        }
    }


    private fun initializeViews()
    {
        val nameView = this.findViewById(com.taletable.android.R.id.entity_name) as TextView?
        nameView?.typeface = Font.typeface(TextFont.Roboto, TextFontStyle.Medium, this)

//        val categoryView = this.findViewById(R.id.entity_category) as TextView?
//        categoryView?.typeface = Font.typeface(TextFont.Roboto, TextFontStyle.Italic, this)

        val pagePagerAdapter = PagePagerAdapter(supportFragmentManager)

        this.pagePagerAdapter = pagePagerAdapter

        this.viewPager = this.findViewById<ViewPager>(com.taletable.android.R.id.view_pager)

        this.bottomSheet = this.findViewById<LinearLayout>(com.taletable.android.R.id.bottom_sheet)
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

        val tabLayout = this.findViewById<TabLayout>(com.taletable.android.R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)

    }


    private fun initializeBottomSheetView()
    {
        this.sessionId?.let { this.sessionLoader(it).toNullable() }?.let { session ->

            findViewById<TextView>(R.id.session_name)?.let {
                it.typeface = Font.typeface(TextFont.Roboto, TextFontStyle.Medium, this)
                it.text = session.sessionName.value
            }

            findViewById<LinearLayout>(R.id.session_toolbar)?.let {
                it.addView(bottomSheetToolbarEditButtonView(R.string.edit_session, R.drawable.icon_edit, officialAppThemeLight, this))
                it.addView(bottomSheetToolbarEditButtonView(R.string.sort_table, R.drawable.icon_slider, officialAppThemeLight, this))
                it.addView(bottomSheetToolbarEditButtonView(R.string.new_session, R.drawable.icon_plus_bold, officialAppThemeLight, this))
                //it.addView(entityToolbarButtonsView(officialAppThemeLight, this))
            }

//            findViewById<TextView>(R.id.button_edit_session)?.let {
//                it.typeface = Font.typeface(TextFont.Roboto, TextFontStyle.Medium, this)
//            }

            findViewById<FrameLayout>(R.id.bottom_sheet_footer)?.let {
                it.addView(bottomSheetToolbarSearchButtonView(officialAppThemeLight, this))

                it.setOnClickListener {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                }
            }

            findViewById<FrameLayout>(com.taletable.android.R.id.bottom_sheet_content)?.let {
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

//            findViewById<FrameLayout>(com.taletable.android.R.id.session_loader_container)?.let {
//                val progressBar = openSessionProgressBar(officialAppThemeLight, this)
//                this.progressBar = progressBar
//                it.addView(progressBar)
//            }

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
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_5"))))
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
        val tabLayout = this.findViewById<CustomTabLayout>(com.taletable.android.R.id.tab_layout) as CustomTabLayout

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

        val nameView = this.findViewById(com.taletable.android.R.id.entity_name) as TextView?
        nameView?.text = entity.name()

//        val categoryView = this.findViewById(R.id.entity_category) as TextView?
//        categoryView?.text = entity.category()

        val entityButtonView = this.findViewById(com.taletable.android.R.id.entity_button) as ImageButton?
        //entityButtonView?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_open_book))

        entityButtonView?.setOnClickListener {
            Log.d("***SESSION ACTIVITY", "setting entity button on click")
            val newFragment = BookSettingsFragment.newInstance(entity.entityId())

//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                newFragment.enterTransition = Slide(Gravity.BOTTOM)
//                newFragment.exitTransition  = Slide(Gravity.TOP)
//            }


            this.findViewById<HorizontalScrollView>(com.taletable.android.R.id.toolbar_nav)?.let {
                it.visibility = View.GONE
            }

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(com.taletable.android.R.id.session_content, newFragment)
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

        this.findViewById<TabLayout>(com.taletable.android.R.id.tab_layout)?.let {
            it.visibility = View.VISIBLE
            it.setupWithViewPager(viewPager)
        }

        this.findViewById<HorizontalScrollView>(com.taletable.android.R.id.toolbar_nav)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<LinearLayout>(com.taletable.android.R.id.session_content)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<ViewPager>(com.taletable.android.R.id.view_pager)?.let {
            it.visibility = View.VISIBLE
        }

        this.findViewById<LinearLayout>(com.taletable.android.R.id.toolbar_bottom_padding)?.let {
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
        this.findViewById<TabLayout>(com.taletable.android.R.id.tab_layout)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<HorizontalScrollView>(com.taletable.android.R.id.toolbar_nav)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<ViewPager>(com.taletable.android.R.id.view_pager)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<LinearLayout>(com.taletable.android.R.id.toolbar_bottom_padding)?.let {
            it.visibility = View.VISIBLE
        }

        this.findViewById<LinearLayout>(com.taletable.android.R.id.session_content)?.let {
            it.visibility = View.VISIBLE
            it.removeAllViews()
            it.addView(campaignView(officialAppThemeLight, this))
        }
    }


    fun renderGame(game : Game)
    {
        this.findViewById<TabLayout>(com.taletable.android.R.id.tab_layout)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<HorizontalScrollView>(com.taletable.android.R.id.toolbar_nav)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<ViewPager>(com.taletable.android.R.id.view_pager)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<LinearLayout>(com.taletable.android.R.id.toolbar_bottom_padding)?.let {
            it.visibility = View.VISIBLE
        }

        this.findViewById<LinearLayout>(com.taletable.android.R.id.session_content)?.let {
            it.visibility = View.VISIBLE
            it.removeAllViews()
            it.addView(gameView(officialAppThemeLight, this))
        }
    }


    fun renderBook(book : Book)
    {
        this.findViewById<TabLayout>(com.taletable.android.R.id.tab_layout)?.let {
            it.visibility = View.GONE
        }

        this.findViewById<HorizontalScrollView>(com.taletable.android.R.id.toolbar_nav)?.let {
            it.visibility = View.VISIBLE
        }

        this.findViewById<ViewPager>(com.taletable.android.R.id.view_pager)?.let {
            it.visibility = View.GONE
        }

//        this.findViewById<LinearLayout>(com.taletable.android.R.id.toolbar_bottom_padding)?.let {
//            it.visibility = View.VISIBLE
//        }

        this.findViewById<LinearLayout>(com.taletable.android.R.id.session_content)?.let {
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
            this.setCurrentBookReference(setToRef, true)
        }
    }


    fun setSearchView(bookId : EntityId)
    {
        val newFragment = BookSearchFragment.newInstance(bookId)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(com.taletable.android.R.id.session_content, newFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    fun setCurrentBookReference(bookReference : BookReference, isBack : Boolean = false)
    {
        this.findViewById<LinearLayout>(com.taletable.android.R.id.session_content)?.let {
            it.visibility = View.VISIBLE
            it.removeAllViews()
        }

        this.findViewById<HorizontalScrollView>(com.taletable.android.R.id.toolbar_nav)?.let {
            it.visibility = View.VISIBLE
            it.removeAllViews()
        }

        if (!isBack) {
            this.bookNavHistory = this.bookNavHistory.plus(bookReference)
        }

        when (bookReference)
        {
            is BookReferenceBook ->
            {
                val newFragment = BookFragment.newInstance(bookReference.bookId())

                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(com.taletable.android.R.id.session_content, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
            is BookReferenceChapter ->
            {
                val newFragment = ChapterFragment.newInstance(bookReference.chapterId(), bookReference.bookId())
//
//                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    // only for gingerbread and newer versions
//                    when (prevBookReference) {
//                        is BookReferenceBook -> {
//                            newFragment.enterTransition = Slide(Gravity.START)
//                        }
//                        else -> {
//                            newFragment.enterTransition = Slide(Gravity.END)
//                        }
//                    }
//                }


                val transaction = supportFragmentManager.beginTransaction()
                //transaction.setCustomAnimations(R.anim.activity_slide_in_right, R.anim.activity_slide_in_left)
                transaction.replace(com.taletable.android.R.id.session_content, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                this.findViewById<HorizontalScrollView>(com.taletable.android.R.id.toolbar_nav)?.let {
                    it.addView(navView(bookReference, officialAppThemeLight, this))
                }
            }
            is BookReferenceSection ->
            {
                val newFragment = SectionFragment.newInstance(bookReference.sectionId(),
                                                              bookReference.chapterId(),
                                                              bookReference.bookId())

                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(com.taletable.android.R.id.session_content, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                this.findViewById<HorizontalScrollView>(com.taletable.android.R.id.toolbar_nav)?.let {
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
                transaction.replace(com.taletable.android.R.id.session_content, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                this.findViewById<HorizontalScrollView>(com.taletable.android.R.id.toolbar_nav)?.let {
                    it.addView(navView(bookReference, officialAppThemeLight, this))
                }

            }
            is BookReferenceContent -> { }
            is BookReferenceCard -> {
                val newFragment = BookCardFragment.newInstance(bookReference.bookId(), bookReference.cardId())

                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(com.taletable.android.R.id.session_content, newFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                this.findViewById<HorizontalScrollView>(com.taletable.android.R.id.toolbar_nav)?.let {
                    it.addView(navView(bookReference, officialAppThemeLight, this))
                }
                Log.d("***SESSION ACTIVITY", "going to book card")
            }
        }
    }



}


fun activeSessionRecyclerView(session : Session,
                              theme : Theme,
                              sessionActivity : SessionActivity) : RecyclerView
{
    val recyclerView                = RecyclerViewBuilder()
    val context = sessionActivity

    recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
    recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

    recyclerView.layoutManager      = LinearLayoutManager(context)

    val dividerColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))
    val dividerColor              = theme.colorOrBlack(dividerColorTheme)
    //recyclerView.divider            = SimpleDividerItemDecoration(context, dividerColor)

    recyclerView.margin.leftDp      = 16f
    recyclerView.margin.rightDp     = 16f
    recyclerView.margin.topDp       = 8f

//    recyclerView.padding.topDp      = 6f

//    recyclerView.padding.bottomDp   = 60f

    recyclerView.clipToPadding      = false

    return recyclerView.recyclerView(context)
}


// | Bottom Sheet > Toolbar View
// -----------------------------------------------------------------------------------------

fun bottomSheetToolbarView(theme : Theme, activity : AppCompatActivity) : ViewGroup
{
    val layout              = bottomSheetToolbarViewLayout(activity, theme)

    layout.addView(bottomSheetToolbarSearchButtonView(theme, activity))

//
//    val editButtonView = bottomSheetToolbarEditButtonView(theme, activity)
//
//    layout.addView(homeButtonView)
//    layout.addView(editButtonView)



    return layout
}


fun bottomSheetToolbarViewLayout(context : Context, theme : Theme) : LinearLayout
{
    val layoutBuilder               = LinearLayoutBuilder()

    layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height            = LinearLayout.LayoutParams.MATCH_PARENT

//    layoutBuilder.padding.leftDp    = 16f
//    layoutBuilder.padding.rightDp   = 16f

//    val colorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_5"))))
//    layoutBuilder.backgroundColor   = theme.colorOrBlack(colorTheme)

    layoutBuilder.backgroundColor   = Color.WHITE

    return layoutBuilder.linearLayout(context)
}

private fun bottomSheetToolbarSearchButtonView(
        theme : Theme,
        context : Context
) : LinearLayout
{
    // 1 | Declarations
    // -----------------------------------------------------------------------------------------

    val layoutBuilder                   = LinearLayoutBuilder()
    val iconViewBuilder                 = ImageViewBuilder()
    val labelViewBuilder                = TextViewBuilder()

    // 2 | Layout
    // -----------------------------------------------------------------------------------------

    layoutBuilder.width                 = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height                = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation           = LinearLayout.HORIZONTAL

//    layoutBuilder.padding.topDp         = 8f
//    layoutBuilder.padding.bottomDp      = 8f
//    layoutBuilder.padding.leftDp        = 12f
//    layoutBuilder.padding.rightDp       = 12f

    layoutBuilder.layoutGravity         = Gravity.CENTER
    layoutBuilder.gravity               = Gravity.CENTER_VERTICAL

    layoutBuilder.backgroundResource    = R.drawable.bg_search_box_button

    layoutBuilder.child(iconViewBuilder)
                 .child(labelViewBuilder)

    // 3 | Icon
    // -----------------------------------------------------------------------------------------

    iconViewBuilder.widthDp             = 18
    iconViewBuilder.heightDp            = 18

    iconViewBuilder.image               = R.drawable.icon_search

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_20"))))
    iconViewBuilder.color               = theme.colorOrBlack(iconColorTheme)

    iconViewBuilder.margin.rightDp      = 16f

    // 3 | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.text               = "Search Tale Table"

    labelViewBuilder.font               = Font.typeface(TextFont.Roboto,
                                                        TextFontStyle.Medium,
                                                        context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_22"))))
    labelViewBuilder.color              = theme.colorOrBlack(labelColorTheme)

    labelViewBuilder.sizeSp             = 17.5f

    return layoutBuilder.linearLayout(context)
}


//
//private fun bottomSheetToolbarHomeButtonView(theme : Theme, context : Context) : LinearLayout
//{
//    // 1 | Declarations
//    // -----------------------------------------------------------------------------------------
//
//    val layoutBuilder                   = LinearLayoutBuilder()
//    val iconViewBuilder                 = ImageViewBuilder()
//    val labelViewBuilder                = TextViewBuilder()
//
//    // 2 | Layout
//    // -----------------------------------------------------------------------------------------
//
//    layoutBuilder.layoutType            = LayoutType.RELATIVE
//    layoutBuilder.width                 = RelativeLayout.LayoutParams.WRAP_CONTENT
//    layoutBuilder.height                = RelativeLayout.LayoutParams.WRAP_CONTENT
//
//    layoutBuilder.orientation           = LinearLayout.HORIZONTAL
//
////    val bgColorTheme = ColorTheme(setOf(
////            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
////            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))
////    layoutBuilder.backgroundColor       = theme.colorOrBlack(bgColorTheme)
//
//    layoutBuilder.padding.topDp         = 8f
//    layoutBuilder.padding.bottomDp      = 5f
//    layoutBuilder.padding.leftDp        = 5f
//    layoutBuilder.padding.rightDp       = 12f
//
////    layoutBuilder.corners               = Corners(2.0, 2.0, 2.0, 2.0)
//
//    layoutBuilder.backgroundResource    = R.drawable.bg_session_button
//
////    layoutBuilder.layoutGravity         = Gravity.START or Gravity.CENTER_VERTICAL
////    layoutBuilder.gravity               = Gravity.CENTER_VERTICAL
//
//    layoutBuilder.addRule(RelativeLayout.CENTER_VERTICAL)
//    layoutBuilder.addRule(RelativeLayout.ALIGN_PARENT_START)
//
//    layoutBuilder.child(iconViewBuilder)
//                 .child(labelViewBuilder)
//
//    // 3 | Icon
//    // -----------------------------------------------------------------------------------------
//
//    iconViewBuilder.widthDp             = 27
//    iconViewBuilder.heightDp            = 27
//
//    iconViewBuilder.image               = R.drawable.icon_chevron_left
//    //iconViewBuilder.scaleX              = -1f
//
//    val iconColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_12"))))
//    iconViewBuilder.color               = theme.colorOrBlack(iconColorTheme)
//
//    iconViewBuilder.margin.rightDp      = 2f
//    //iconViewBuilder.margin.topDp        = 2f
//
//    // 3 | Label
//    // -----------------------------------------------------------------------------------------
//
//    labelViewBuilder.width              = LinearLayout.LayoutParams.WRAP_CONTENT
//    labelViewBuilder.height             = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    labelViewBuilder.text               = "Leave Session"
//
//    labelViewBuilder.font               = Font.typeface(TextFont.Roboto,
//                                                        TextFontStyle.Medium,
//                                                        context)
//
//    val labelColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_12"))))
//    labelViewBuilder.color              = theme.colorOrBlack(labelColorTheme)
//
//    labelViewBuilder.sizeSp             = 18f
//
//    return layoutBuilder.linearLayout(context)
//}


private fun bottomSheetToolbarEditButtonView(labelId : Int?, iconId : Int?, theme : Theme, context : Context) : LinearLayout
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
    layoutBuilder.heightDp              = 36

    layoutBuilder.backgroundResource    = R.drawable.bg_session_button

    //layoutBuilder.elevation             = 8f

    layoutBuilder.gravity               = Gravity.CENTER

    layoutBuilder.padding.leftDp        = 8f
    layoutBuilder.padding.rightDp       = 8f
//    layoutBuilder.padding.topDp         = 8f
//    layoutBuilder.padding.bottomDp      = 8f

    //layoutBuilder.margin.leftDp         = 0f
    layoutBuilder.margin.rightDp         = 12f
//    layoutBuilder.margin.bottomDp         = 4f
//    layoutBuilder.margin.topDp         = 4f

    layoutBuilder.orientation           = LinearLayout.HORIZONTAL

//    layoutBuilder.layoutGravity         = Gravity.END or Gravity.CENTER_VERTICAL
//    layoutBuilder.gravity               = Gravity.CENTER_VERTICAL

    layoutBuilder.addRule(RelativeLayout.CENTER_VERTICAL)
    layoutBuilder.addRule(RelativeLayout.ALIGN_PARENT_START)

    if (iconId != null)
        layoutBuilder.child(iconViewBuilder)

    if (labelId != null)
        layoutBuilder.child(labelViewBuilder)

    // 3 | Icon
    // -----------------------------------------------------------------------------------------

    iconViewBuilder.widthDp             = 17
    iconViewBuilder.heightDp            = 17

    iconViewBuilder.image               = iconId

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_18"))))
    iconViewBuilder.color               = theme.colorOrBlack(iconColorTheme)

    iconViewBuilder.margin.rightDp      = 6f

    // 3 | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.textId             = labelId

    labelViewBuilder.font               = Font.typeface(TextFont.Roboto,
                                                        TextFontStyle.Medium,
                                                        context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_18"))))
    labelViewBuilder.color              = theme.colorOrBlack(labelColorTheme)

    labelViewBuilder.sizeSp             = 16f

    return layoutBuilder.linearLayout(context)
}


fun otherEntitiesHeaderView(theme : Theme, context : Context) : LinearLayout
{
    val layout = otherEntitiesHeaderViewLayout(context)

    layout.addView(otherEntitiesHeaderSummaryView(theme, context))
//    layout.addView(bottomSheetToolbarEditButtonView(theme, context))
//
//    layout.addView(entityToolbarButtonsView(theme, context))

    return layout
}


private fun otherEntitiesHeaderViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.padding.topDp    = 12f
    layout.padding.bottomDp = 12f

//    layout.padding.leftDp   = 16f
//    layout.padding.rightDp  = 16f

//    layout.margin.leftDp    = 4f
//    layout.margin.rightDp   = 4f

    layout.backgroundColor  = Color.WHITE


    return layout.linearLayout(context)
}


private fun otherEntitiesHeaderSummaryView(theme : Theme, context : Context) : TextView
{
    val name                = TextViewBuilder()

    name.id                 = R.id.summary_view

    name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    name.font               = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Regular,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_20"))))
    name.color              = theme.colorOrBlack(colorTheme)

    name.sizeSp             = 16f

    return name.textView(context)
}


private fun entityToolbarButtonsView(theme : Theme, context : Context) : LinearLayout
{
    val layout = entityToolbarButtonsViewLayout(context)

    layout.addView(bottomSheetToolbarEditButtonView(null, R.drawable.icon_slider, theme, context))
    layout.addView(bottomSheetToolbarEditButtonView(null, R.drawable.icon_plus_bold, theme, context))

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

    layout.margin.rightDp       = 8f

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

    //layout.backgroundResource   = R.drawable.bg_session_button

    //layout.backgroundResource    = R.drawable.bg_session_button

//    layout.elevation             = 8f

//    layout.padding.leftDp       = 8f
//    layout.padding.rightDp      8= 8f
//    layout.padding.topDp       = 8f
//    layout.padding.bottomDp      = 8f

//    layout.margin.topDp         = 4f
//    layout.margin.bottomDp      = 4f
//    layout.margin.rightDp       = 4f
    layout.margin.leftDp        = 20f

    layout.gravity              = Gravity.CENTER

    //if (addMargin)

    layout.child(iconView)

    // 3 | Icon
    // -----------------------------------------------------------------------------------------

    iconView.widthDp            = iconSize
    iconView.heightDp           = iconSize

    iconView.image              = iconId

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_1"))))
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

    layout.id                   = com.taletable.android.R.id.entity_card_layout

    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation          = LinearLayout.HORIZONTAL

//    layout.backgroundResource   = R.drawable.bg_card_flat
    layout.backgroundColor      = Color.WHITE

    //layout.corners              = Corners(2.0, 2.0, 2.0, 2.0)

    layout.gravity              = Gravity.TOP

    layout.padding.topDp        = 8f
    layout.padding.bottomDp     = 8f

//    layout.padding.leftDp       = 16f
//    layout.padding.rightDp      = 16f

//    layout.margin.topDp         = 1f
//    layout.margin.leftDp        = 4f
//    layout.margin.rightDp       = 4f

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


private fun entityCardImageView(theme : Theme, context : Context) : FrameLayout
{
    val layout = FrameLayout(context)
    layout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT)

    layout.addView(entityCardImageImageView(theme, context))

    layout.addView(entityCardImageProgressView(theme, context))

    return layout
}



private fun entityCardImageProgressView(theme : Theme, context : Context) : ProgressBar
{
    val progressBar = ProgressBar(context)

    progressBar.id = R.id.progress_bar

//    val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
//                                                FrameLayout.LayoutParams.WRAP_CONTENT)
    val layoutParams = FrameLayout.LayoutParams(Util.dpToPixel(50f), Util.dpToPixel(50f))

    progressBar.layoutParams = layoutParams


    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
    val color      = theme.colorOrBlack(colorTheme)

    progressBar.indeterminateDrawable =
            CircularProgressDrawable.Builder(context)
                    .color(color)
                    .sweepSpeed(0.5f)
                    .strokeWidth(2f)
                    .build()


    return progressBar
}


private fun entityCardImageImageView(theme : Theme, context : Context) : LinearLayout
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

//    layout.corners              = Corners(3.0,3.0,3.0,3.0)
//
//    val bgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
//    layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

    layout.backgroundResource   = R.drawable.bg_session_step

    layout.gravity              = Gravity.CENTER

    layout.child(iconViewBuilder)

    // 2 | Icon View Builder
    // -------------------------------------------------------------------------

    iconViewBuilder.widthDp         = 25
    iconViewBuilder.heightDp        = 25

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_20"))))

    iconViewBuilder.image           = com.taletable.android.R.drawable.icon_book

    //iconViewBuilder.color           = theme.colorOrBlack(iconColorTheme)

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

    layout.margin.leftDp        = 16f
    //layout.margin.rightDp       = 12f

    return layout.linearLayout(context)
}


private fun entityCardNameView(theme : Theme,
                               context : Context) : TextView
{
    val name                = TextViewBuilder()

    name.id                 = com.taletable.android.R.id.entity_card_name

    name.width              = LinearLayout.LayoutParams.MATCH_PARENT
    name.height             = LinearLayout.LayoutParams.WRAP_CONTENT


    name.font               = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Medium,
                                            context)


    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_10"))))
    name.color              = theme.colorOrBlack(colorTheme)
    // name.color              = Color.WHITE

    name.sizeSp             = 17f

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
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_20"))))
    summary.color            = theme.colorOrBlack(colorTheme)

    summary.sizeSp           = 16.5f

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
            loaderViewIds.clear()
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
            val toolbarView = otherEntitiesHeaderView(theme, context)
            OtherHeaderViewHolder(toolbarView)
        }
        else ->
        {
            val cardView = entityCardView(theme, context)
            EntityCardViewHolder(cardView, theme, sessionActivity)
        }
    }

//    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : RecyclerView.ViewHolder
//    {
//        val cardView = entityCardView(theme, context)
//        return EntityCardViewHolder(cardView, theme, sessionActivity)
//    }


    override fun onBindViewHolder(viewHolder : RecyclerView.ViewHolder, position : Int)
    {
        val item = this.items[position]
        when (item) {
            is SessionListHeader -> {
                val headerViewHolder = viewHolder as OtherHeaderViewHolder
                headerViewHolder.setSummary(item.sessionDescription)
            }
            is PersistedEntity -> {
                val loaderViewId = Util.generateViewId()
                val entityViewHolder = viewHolder as EntityCardViewHolder
                entityViewHolder.setEntity(item, loaderViewId)
                loaderViewIds[item.entityId] = loaderViewId
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
    var progressBar             : ProgressBar? = null

//    val context = sessionActivity


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout             = itemView.findViewById(com.taletable.android.R.id.entity_card_layout)
        this.nameView           = itemView.findViewById(com.taletable.android.R.id.entity_card_name)
        this.summaryView        = itemView.findViewById(com.taletable.android.R.id.entity_card_summary)
        this.progressBar        = itemView.findViewById(com.taletable.android.R.id.progress_bar)
    }



    fun setEntity(entity : PersistedEntity, viewId : Int)
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

        this.progressBar?.id = viewId

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

    bar.id                  = com.taletable.android.R.id.progress_bar

    bar.width               = LinearLayout.LayoutParams.MATCH_PARENT
    bar.height              = LinearLayout.LayoutParams.WRAP_CONTENT

    bar.progressDrawableId  = com.taletable.android.R.drawable.progress_bar_load_session

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
    bar.backgroundColor     = theme.colorOrBlack(bgColorTheme)

    return bar.progressBar(context)
}




class OtherHeaderViewHolder(val toolbarView : ViewGroup) : RecyclerView.ViewHolder(toolbarView)
{

    // | Properties
    // -----------------------------------------------------------------------------------------

    var summaryView             : TextView? = null

    // | Init
    // -----------------------------------------------------------------------------------------

    init
    {
        this.summaryView        = itemView.findViewById(R.id.summary_view)
    }

    // | Methods
    // -----------------------------------------------------------------------------------------

    fun setSummary(summary : String)
    {
        this.summaryView?.text = summary
    }

}



class CurrentHeaderViewHolder(val toolbarView : ViewGroup) : RecyclerView.ViewHolder(toolbarView)




//class SessionCurrentEntityHeader()
//
//data class SessionCurrentEntity(val entity : PersistedEntity)

data class SessionListHeader(val sessionDescription : String)
