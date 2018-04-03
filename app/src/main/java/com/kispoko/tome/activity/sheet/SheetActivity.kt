
package com.kispoko.tome.activity.sheet


import android.content.Context
import android.content.Intent
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
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.kispoko.tome.R
import com.kispoko.tome.activity.entity.engine.procedure.ProcedureUpdateDialog
import com.kispoko.tome.activity.session.SessionActivity
import com.kispoko.tome.activity.sheet.page.PagePagerAdapter
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.variable.TextVariable
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.model.sheet.Sheet
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.widget.table.TableWidgetRow
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialThemeLight
import com.kispoko.tome.router.Router
import com.kispoko.tome.rts.entity.*
import com.kispoko.tome.rts.entity.VariableChangeListener
import com.kispoko.tome.rts.entity.sheet.*
import com.kispoko.tome.util.configureToolbar
import effect.Err
import maybe.Just
import effect.Val
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

    var sheetId : SheetId? = null


    // STATE > Views
    // -----------------------------------------------------------------------------------------

    private var pagePagerAdapter : PagePagerAdapter?   = null
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
            this.sheetId = this.intent.getSerializableExtra("sheet_id") as SheetId

        if (savedInstanceState != null)
            this.sheetId = savedInstanceState.getSerializable("sheet_id") as SheetId

        // (3) Initialize Listeners
        // -------------------------------------------------------------------------------------

        this.initializeListeners()

        // (4) Configure UI
        // -------------------------------------------------------------------------------------

        this.configureToolbar("")

        this.initializeViews()

        this.initializeSidebars()

        this.initializeFAB()

//        this.initializeBottomNavigation()

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
                    if (sheetId != null && viewPager != null) {
                        sheetOrError(sheetId) apDo {
                            it.update(message.update, viewPager, this)
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
                                                        EntitySheetId(sheetId))
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

        this.viewPager = this.findViewById(R.id.page_pager) as ViewPager
//        this.viewPager?.setPadding(0, 0, 0, Util.dpToPixel(60f))

        this.bottomSheet = this.findViewById(R.id.bottom_sheet) as FrameLayout
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

        val tabLayout = this.findViewById(R.id.tab_layout) as TabLayout
        tabLayout.setupWithViewPager(viewPager)

//        val navButtonView = this.findViewById(R.id.toolbar_nav_button) as ImageView
//        navButtonView.setOnClickListener {
//            val intent = Intent(this, NavigationActivity::class.java)
//            this.startActivity(intent)
//        }
    }


    private fun initializeFAB()
    {
        val fab = findViewById(R.id.session_button) as FloatingActionButton
        fab.setOnClickListener {
            val intent = Intent(this, SessionActivity::class.java)
            finish()
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
        val drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout

        // Right Sidebar
        // -------------------------------------------------------------------------------------
        val menuRight = findViewById(R.id.toolbar_options_button) as ImageView

        val rightNavView = findViewById(R.id.right_nav_view) as NavigationView
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
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        // Toolbar > Background
        toolbar.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById(R.id.menuLeft) as ImageView
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

//        val navButton = this.findViewById(R.id.toolbar_nav_button) as ImageView
//        navButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val optionsButton = this.findViewById(R.id.toolbar_options_button) as ImageView
        optionsButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById(R.id.toolbar_title) as TextView
        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))

        // TAB LAYOUT
        // -------------------------------------------------------------------------------------
        val tabLayout = this.findViewById(R.id.tab_layout) as CustomTabLayout

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
        val sheetId = sheet.sheetId()
        val entityId = EntitySheetId(sheetId)

        sheet.onActive(entityId, this)

        // TODO why?
        val coordinatorLayout = this.findViewById(R.id.coordinator_layout) as CoordinatorLayout
        coordinatorLayout.visibility = View.VISIBLE

        // Configure toolbar to be character name
        // -------------------------------------------------------------------------------------

        val maybeName =  textVariable(VariableId("name"), entityId) ap { it.value(entityId) }

        when (maybeName)
        {
            is Val -> {
                val name = maybeName.value
                when (name) {
                    is Just -> this.configureToolbar(name.value)
                }
            }
            is Err -> ApplicationLog.error(maybeName.error)
        }


        // Ensure toolbar updates value when name changes
        // -------------------------------------------------------------------------------------

        val updateToolbarOnNameChange = VariableChangeListener(
                { updateToolbar(it, sheet.sheetId()) },
                {})
        addVariableChangeListener(VariableId("name"),
                                    updateToolbarOnNameChange,
                                    entityId)

        this.renderSheet(sheet)
    }


    fun renderSheet(sheet : Sheet)
    {
        this.applyTheme(officialThemeLight)

        val start = System.currentTimeMillis()

        val section = sheet.sections().firstOrNull()
        if (section != null) {
            pagePagerAdatper().setPages(section.pages(), sheet.sheetId())
        }

//        this.bottomSheet?.addView(tableActionBarBuilder.view())

        if (this.bottomSheetBehavior == null)
            Log.d("***SHEET ACTIVITY", "beahvior is null")

//        this.bottomSheetBehavior?.peekHeight = 100
        this.bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        this.bottomSheetBehavior?.peekHeight = 0

        this.bottomSheetBehavior?.state = BottomSheetBehavior.PEEK_HEIGHT_AUTO



//        val saveMenuUI = SaveMenuUI(this, officialThemeLight)
//        this.bottomSheet?.removeAllViews()
//        this.bottomSheet?.addView(saveMenuUI.view())

        val end = System.currentTimeMillis()

        Log.d("***SHEETMAN", "time to render ms: " + (end - start).toString())
    }


    private fun updateToolbar(variable : Variable, sheetId : SheetId)
    {
        when (variable)
        {
            is TextVariable ->
            {
                val mText = variable.variableValue().value(EntitySheetId(sheetId))
                when (mText)
                {
                    is Val -> {
                        val text = mText.value
                        when (text) {
                            is Just -> this.configureToolbar(text.value)
                        }
                    }
                    is Err -> ApplicationLog.error(mText.error)
                }
            }
        }
    }

}




//
//    fun showTableEditor(tableWidgetRow : TableWidgetRow,
//                        updateTarget : UpdateTarget,
//                        sheetContext : SheetContext)
//    {
//
//        val sheetUIContext = SheetUIContext(sheetContext, this)
//        activeTableRow?.onEditorClose(sheetUIContext)
//
//        val toolbarView = this.findViewById(R.id.sheet_toolbar) as FrameLayout
//        toolbarView.visibility = View.VISIBLE
//        val tableActionBarBuilder = TableActionBarViewBuilder(updateTarget, sheetUIContext)
//        this.toolbarView = toolbarView
//        this.activeTableRow = tableWidgetRow
//
//        toolbarView.removeAllViews()
//        toolbarView.addView(tableActionBarBuilder.view())
//
//        val viewPager = this.viewPager
//        viewPager?.setPadding(0, 0, 0, Util.dpToPixel(130f))
//    }


//    override fun hideActionBar()
//    {
//        this.toolbarView?.visibility = View.GONE
//
//        val viewPager = this.viewPager
//        //viewPager?.setPadding(0, 0, 0, 0)
//        this.viewPager?.setPadding(0, 0, 0, Util.dpToPixel(60f))
//
//        SheetManager.currentSheetContext() apDo {
//            val sheetUIContext = SheetUIContext(it, this)
//            this.activeTableRow?.onEditorClose(sheetUIContext)
//            this.activeTableRow = null
//        }
//    }


//    fun showTableEditor(updateTarget : UpdateTarget, sheetContext : SheetContext)
//    {
//        this.bottomNavigation?.visibility = View.GONE
//
//        val tableActionBarBuilder = TableActionBarViewBuilder(updateTarget,
//                                                              SheetUIContext(sheetContext, this))
//
//        this.bottomSheet?.removeAllViews()
//        this.bottomSheet?.addView(tableActionBarBuilder.view())
//
//        this.bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
//
//        this.fab?.setOnClickListener {
//            when (updateTarget)
//            {
//                is UpdateTargetInsertTableRow ->
//                {
////                    val dialog = AddTableRowDialog.newInstance(updateTarget, sheetContext)
////                    dialog.show(supportFragmentManager, "")
//                }
//            }
//        }
//    }

