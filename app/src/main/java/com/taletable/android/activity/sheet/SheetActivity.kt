
package com.taletable.android.activity.sheet


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.*
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.taletable.android.R
import com.taletable.android.activity.AppOptionsUI
import com.taletable.android.activity.entity.engine.procedure.ProcedureUpdateDialog
import com.taletable.android.activity.session.SessionActivity
import com.taletable.android.activity.sheet.history.HistoryPagerAdapter
import com.taletable.android.activity.sheet.page.PagePagerAdapter
import com.taletable.android.activity.sheet.task.TaskPagerAdapter
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.Sheet
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.sheet.widget.table.TableWidgetRow
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialThemeLight
import com.taletable.android.router.Router
import com.taletable.android.rts.entity.*
import com.taletable.android.rts.entity.sheet.*
import com.taletable.android.util.configureToolbar
import maybe.Just
import io.reactivex.disposables.CompositeDisposable



object SheetActivityGlobal
{
    val touchHandler = Handler()

    private var longPressRunnable : Runnable? = null

    fun setLongPressRunnable(r : Runnable) {
        touchHandler.postDelayed(r, 1100)
        this.longPressRunnable = r
    }

    fun cancelLongPressRunnable() {
        touchHandler.removeCallbacks(this.longPressRunnable)
    }
}


object SheetActivityRequest
{

    val PROCEDURE_INVOCATION = 1

}



/**
 * Sheet Activity
 */
class SheetActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // STATE
    // -----------------------------------------------------------------------------------------

    // STATE > Sheet
    // -----------------------------------------------------------------------------------------

    var sheetId : EntityId? = null


    // STATE > Views
    // -----------------------------------------------------------------------------------------

    var pagePagerAdapter : PagePagerAdapter?   = null
    private var viewPager : ViewPager? = null
//    private var bottomNavigation : AHBottomNavigation? = null

    private var bottomNavigation : LinearLayout? = null

    private var fab : FloatingActionButton? = null
    private var bottomSheet : FrameLayout? = null
    var bottomSheetBehavior : BottomSheetBehavior<FrameLayout>? = null

    private var toolbarView : FrameLayout? = null
    private var activeTableRow : TableWidgetRow? = null

    private val messageListenerDisposable : CompositeDisposable = CompositeDisposable()


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_sheet)

        // (2) Read Parameters (or saved state)
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("sheet_id"))
            this.sheetId = this.intent.getSerializableExtra("sheet_id") as EntityId

        if (savedInstanceState != null)
            this.sheetId = savedInstanceState.getSerializable("sheet_id") as EntityId

        // (3) Initialize Listeners
        // -------------------------------------------------------------------------------------

        this.initializeListeners()

        // (4) Configure UI
        // -------------------------------------------------------------------------------------

        this.configureToolbar("Character Sheet", TextFont.Cabin, TextFontStyle.Medium, 17f)

        this.initializeViews()

        this.initializeSidebars()

        this.initializeFAB()

        // (5) Initialize Sheet
        // -------------------------------------------------------------------------------------

        val sheetId = this.sheetId
        if (sheetId != null)
        {
            val sessonSheet = sheet(sheetId)
            when (sessonSheet) {
                // Render sheet.
                is Just -> {
                    this.setSheetActive(sessonSheet.value)
                }
                // Session does not have sheet, so nothing we can do here.
                // This may happen if process dies while this activity was activity, restarts,
                // and session is empty (needs to be reloaded).
                else -> {
                    returnToLoad()
                }
            }
        }
        else
        {
            returnToLoad()
        }
    }



    override fun onSaveInstanceState(outState : Bundle?)
    {
        val sheetId = this.sheetId
        if (sheetId != null)
            outState?.putSerializable("sheet_id", sheetId)

        super.onSaveInstanceState(outState)
    }


    override fun onDestroy()
    {
        super.onDestroy()
        this.messageListenerDisposable.clear()
    }



    private fun returnToLoad()
    {
//        val intent = Intent(this, TestSessionActivity::class.java)
//        finish()
//        startActivity(intent)
    }


    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeListeners()
    {
        val disposable = Router.listen(MessageSheet::class.java)
                               .subscribe(this::onMessage)
        this.messageListenerDisposable.add(disposable)
    }


    private fun onMessage(message : MessageSheet)
    {
        val sheetId = this.sheetId
        val viewPager = this.viewPager

        if (sheetId != null)
        {
            when (message)
            {
                is MessageSheetUpdate ->
                {
                    if (viewPager != null) {
                        sheetOrError(sheetId) apDo {
                            it.updateAndSave(message.update, viewPager, this)
                        }
                    }
                }
                is MessageSheetAction ->
                {
                    when (message)
                    {
                        is MessageSheetActionRunProcedure ->
                        {
                            val dialog = ProcedureUpdateDialog.newInstance(
                                                        message.procedureInvocation,
                                                        sheetId)
                            dialog.show(supportFragmentManager, "")
                        }
                    }

                }
            }

        }

    }


    private fun initializeViews()
    {
        val pagePagerAdapter = PagePagerAdapter(supportFragmentManager)

        this.pagePagerAdapter = pagePagerAdapter

        this.viewPager = this.findViewById<ViewPager>(R.id.view_pager)
//        this.viewPager?.setPadding(0, 0, 0, Util.dpToPixel(60f))

        this.bottomSheet = this.findViewById<FrameLayout>(R.id.bottom_sheet)
        this.bottomSheetBehavior = BottomSheetBehavior.from(this.bottomSheet)

        if (this.bottomSheet == null)
            Log.d("***SHEET ACTIVITY", "bottom sheet is null")

        this.viewPager?.adapter = pagePagerAdapter
        val sheetActivity = this

        this.viewPager?.addOnPageChangeListener(object: ViewPager.OnPageChangeListener
        {
            override fun onPageScrollStateChanged(state : Int) { }

            override fun onPageScrolled(position : Int,
                                        positionOffset : Float,
                                        positionOffsetPixels : Int) { }

            override fun onPageSelected(position : Int) {
//                val sheetContext = SheetManager.currentSheetContext()
//                if (sheetContext != null)
//                    hideActionBar()
                SheetActivityGlobal.cancelLongPressRunnable()
            }
        })

        val tabLayout = this.findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)

//        val navButtonView = this.findViewById(R.id.toolbar_nav_button) as ImageView
//        navButtonView.setOnClickListener {
//            val intent = Intent(this, NavigationActivity::class.java)
//            this.startActivity(intent)
//        }

        val toolbarContentLayout = this.findViewById<LinearLayout>(R.id.toolbar_content)


        this.sheetId?.let {
            val mainTabBarUI = MainTabBarUI(it, officialThemeLight, this)
            toolbarContentLayout?.addView(mainTabBarUI.view())
        }
    }


    private fun initializeFAB()
    {
        val fab = findViewById<FloatingActionButton>(R.id.session_button)
        fab.setOnClickListener {
            val intent = Intent(this, SessionActivity::class.java)
            startActivity(intent)
        }
    }


//    override fun onDestroy() {
//        super.onDestroy()
//        // TODO sheet manager remove context reference
//
//    }


    /**
     * Initialize the sidebars.
     */
    fun initializeSidebars()
    {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        // Left Sidebar
        // -------------------------------------------------------------------------------------
        val menuLeft = this.findViewById<ImageView>(R.id.toolbar_menu_button)

        val leftNavView = this.findViewById<NavigationView>(R.id.left_nav_view)
        val appOptionsViewBuilder = AppOptionsUI(this)
        leftNavView.addView(appOptionsViewBuilder.view())

        menuLeft.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START)
            else
                drawerLayout.openDrawer(GravityCompat.START)
        }

        // Right Sidebar
        // -------------------------------------------------------------------------------------
        val menuRight = this.findViewById<ImageView>(R.id.toolbar_options_button)

        val rightNavView = this.findViewById<NavigationView>(R.id.right_nav_view)
        val sheetOptionsViewBuilder = SheetOptionsUI(this)
        rightNavView.addView(sheetOptionsViewBuilder.view())

        menuRight.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END)
            else
                drawerLayout.openDrawer(GravityCompat.END)
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

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = theme.colorOrBlack(uiColors.toolbarBackgroundColorId())
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

        // Toolbar > Background
        toolbar.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById<ImageView>(R.id.toolbar_menu_button)
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

//        val navButton = this.findViewById(R.id.toolbar_nav_button) as ImageView
//        navButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val optionsButton = this.findViewById<ImageView>(R.id.toolbar_options_button)
        optionsButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById<TextView>(R.id.toolbar_title) as TextView
        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))

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

        // BOTTOM NAVIGATION VIEW
        // -------------------------------------------------------------------------------------
//        this.configureBottomNavigation(sheetId, uiColors)

    }


    fun context() : Context = this


    // -----------------------------------------------------------------------------------------
    // SHEET
    // -----------------------------------------------------------------------------------------

    /**
     * Just display sheet.
     */
    fun setSheetActive(sheet : Sheet)
    {
        sheet.onActive(sheet.entityId(), this)

        // TODO why?
        val coordinatorLayout = this.findViewById<CoordinatorLayout>(R.id.coordinator_layout)
        coordinatorLayout.visibility = View.VISIBLE

        this.renderSheet(sheet)
    }


    fun renderSheet(sheet : Sheet)
    {
        this.applyTheme(officialThemeLight)

        val start = System.currentTimeMillis()

        val section = sheet.sections().firstOrNull()
        if (section != null) {
            pagePagerAdatper().setPages(section.pages(), sheet.entityId())
        }

        val end = System.currentTimeMillis()

        Log.d("***SHEET ACTIVITY", "time to render ms: " + (end - start).toString())
    }


}



class MainTabBarUI(val sheetId : EntityId,
                   val theme : Theme,
                   val sheetActivity : SheetActivity)
{

    val context = sheetActivity

    var pagesTabLayoutView   : LinearLayout? = null
    var pagesTabTextView     : TextView? = null
    var tasksTabLayoutView   : LinearLayout? = null
    var tasksTabTextView     : TextView? = null
    var historyTabLayoutView : LinearLayout? = null
    var historyTabTextView   : TextView? = null


    val bgNormalColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))


    val bgSelectedColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))


    val selectedColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))

    val normalColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))


    private fun setSelectedTab(tabIndex : Int)
    {
        when (tabIndex)
        {
            1 -> {
//                pagesTabLayoutView?.setBackgroundColor(theme.colorOrBlack(bgSelectedColorTheme))
                pagesTabLayoutView?.setBackgroundColor(Color.WHITE)
                tasksTabLayoutView?.setBackgroundColor(theme.colorOrBlack(bgNormalColorTheme))
                historyTabLayoutView?.setBackgroundColor(theme.colorOrBlack(bgNormalColorTheme))

                pagesTabTextView?.setTextColor(theme.colorOrBlack(selectedColorTheme))
                tasksTabTextView?.setTextColor(theme.colorOrBlack(normalColorTheme))
                historyTabTextView?.setTextColor(theme.colorOrBlack(normalColorTheme))
            }
            2 -> {
                pagesTabLayoutView?.setBackgroundColor(theme.colorOrBlack(bgNormalColorTheme))
//                tasksTabLayoutView?.setBackgroundColor(theme.colorOrBlack(bgSelectedColorTheme))
                tasksTabLayoutView?.setBackgroundColor(Color.WHITE)
                historyTabLayoutView?.setBackgroundColor(theme.colorOrBlack(bgNormalColorTheme))

                pagesTabTextView?.setTextColor(theme.colorOrBlack(normalColorTheme))
                tasksTabTextView?.setTextColor(theme.colorOrBlack(selectedColorTheme))
                historyTabTextView?.setTextColor(theme.colorOrBlack(normalColorTheme))
            }
            3 -> {
                pagesTabLayoutView?.setBackgroundColor(theme.colorOrBlack(bgNormalColorTheme))
                tasksTabLayoutView?.setBackgroundColor(theme.colorOrBlack(bgNormalColorTheme))
                historyTabLayoutView?.setBackgroundColor(Color.WHITE)

                pagesTabTextView?.setTextColor(theme.colorOrBlack(normalColorTheme))
                tasksTabTextView?.setTextColor(theme.colorOrBlack(normalColorTheme))
                historyTabTextView?.setTextColor(theme.colorOrBlack(selectedColorTheme))
            }
        }

    }


    fun view() : View
    {
        val layout = this.viewLayout()

        // Pages
        val pagesOnClick = View.OnClickListener {

            val tabLayout = sheetActivity.findViewById<TabLayout>(R.id.tab_layout)
            tabLayout?.visibility = View.VISIBLE
            tabLayout?.tabMode = TabLayout.MODE_SCROLLABLE

            val viewPager = sheetActivity.findViewById<View>(R.id.view_pager) as ViewPager?
            viewPager?.adapter = sheetActivity.pagePagerAdapter
            this.setSelectedTab(1)
        }
        val pagesTabView = this.buttonView(R.string.pages, 1, pagesOnClick)
        this.pagesTabLayoutView = pagesTabView
        layout.addView(pagesTabView)

        // Tasks
        val tasksOnClick = View.OnClickListener {
            sheetActivity.findViewById<View>(R.id.tab_layout)?.visibility = View.GONE
            val viewPager = sheetActivity.findViewById<View>(R.id.view_pager) as ViewPager?
            viewPager?.adapter = TaskPagerAdapter(sheetActivity.supportFragmentManager, sheetId)
            this.setSelectedTab(2)
        }
        val tasksTabView = this.buttonView(R.string.tasks, 2, tasksOnClick)
        this.tasksTabLayoutView = tasksTabView
        layout.addView(tasksTabView)

        // History
        val historyOnClick = View.OnClickListener {

            val tabLayout = sheetActivity.findViewById<TabLayout>(R.id.tab_layout)
            tabLayout?.visibility = View.VISIBLE
            tabLayout?.tabMode = TabLayout.MODE_FIXED
            val viewPager = sheetActivity.findViewById<View>(R.id.view_pager) as ViewPager?
            viewPager?.adapter = HistoryPagerAdapter(sheetActivity.supportFragmentManager, sheetId)
            this.setSelectedTab(3)
        }
        val historyTabView = this.buttonView(R.string.history, 3, historyOnClick)
        this.historyTabLayoutView = historyTabView
        layout.addView(historyTabView)

        this.setSelectedTab(1)

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

//        layout.margin.topDp     = 2f
//        layout.margin.bottomDp  = 2f
//        layout.margin.leftDp    = 1f
//        layout.margin.rightDp   = 1f

        return layout.linearLayout(context)
    }



    private fun buttonView(labelStringId : Int,
                           index : Int,
                           onClick : View.OnClickListener) : LinearLayout
    {
        val layout = this.buttonViewLayout()

        val labelView = this.buttonLabelView(labelStringId)
        when (index) {
            1 -> this.pagesTabTextView = labelView
            2 -> this.tasksTabTextView = labelView
            3 -> this.historyTabTextView = labelView
        }

        layout.addView(labelView)

        layout.setOnClickListener(onClick)

        layout.addView(this.buttonBottomBorderView())

        return layout
    }


    private fun buttonViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight           = 1f

        layout.gravity          = Gravity.CENTER

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.topDp    = 8f

        return layout.linearLayout(context)
    }


    private fun buttonLabelView(labelId : Int) : TextView
    {
        val label                   = TextViewBuilder()

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text                  = context.getString(labelId) // .toUpperCase()

        label.gravity               = Gravity.CENTER

        label.font                  = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Medium,
                                                    context)

        label.sizeSp                 = 16f

        return label.textView(context)
    }


    private fun buttonBottomBorderView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1


        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        layout.backgroundColor  = colorOrBlack(colorTheme, sheetId)

        layout.margin.topDp = 8f

        return layout.linearLayout(context)
    }


    private fun countView(countString : String) : TextView
    {
        val count                   = TextViewBuilder()

        count.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        count.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        count.backgroundResource    = R.drawable.bg_session_step

        val indexColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))

        count.color                 = theme.colorOrBlack(indexColorTheme)

        count.font                  = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Bold,
                                                    context)

        count.text                  = countString

        count.gravity               = Gravity.CENTER

        count.sizeSp                = 17f

        count.margin.rightDp        = 10f

        return count.textView(context)
    }

}

