
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
import com.kispoko.tome.activity.load.LoadActivity
import com.kispoko.tome.activity.sheet.page.PagePagerAdapter
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.procedure.ProcedureInvocation
import com.kispoko.tome.model.game.engine.variable.TextVariable
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.model.sheet.Sheet
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.widget.table.TableWidgetRow
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.router.Router
import com.kispoko.tome.rts.entity.*
import com.kispoko.tome.rts.entity.OnVariableChangeListener
import com.kispoko.tome.rts.entity.sheet.*
import com.kispoko.tome.rts.entity.theme.ThemeManager
import com.kispoko.tome.util.Util
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
class SheetActivity : AppCompatActivity(), SheetUI
{

    // -----------------------------------------------------------------------------------------
    // STATE
    // -----------------------------------------------------------------------------------------

    // STATE > Sheet
    // -----------------------------------------------------------------------------------------

    private var sheetId : SheetId? = null


    // STATE > Views
    // -----------------------------------------------------------------------------------------

    private var pagePagerAdapter : PagePagerAdapter?   = null
    private var viewPager : ViewPager? = null
//    private var bottomNavigation : AHBottomNavigation? = null

    private var bottomNavigation : LinearLayout? = null

    private var fab : FloatingActionButton? = null
    private var bottomSheet : RelativeLayout? = null
    private var bottomSheetBehavior : BottomSheetBehavior<RelativeLayout>? = null

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

        this.initializeBottomNavigation()

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


    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?)
    {
        if (requestCode == SheetActivityRequest.PROCEDURE_INVOCATION) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if (data != null && data.hasExtra("procedure_invocation")) {
                    val procedureInvocation = data.getSerializableExtra("procedure_invocation") as ProcedureInvocation
                    Log.d("***SHEET ACTIVITY", "got procedure invocation")
                }
            }
        }

    }


    override fun onDestroy()
    {
        super.onDestroy()
        this.messageListenerDisposable.clear()
    }



    private fun returnToLoad()
    {
        val intent = Intent(this, LoadActivity::class.java)
        finish()
        startActivity(intent)
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
        this.viewPager?.setPadding(0, 0, 0, Util.dpToPixel(60f))

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


    private fun initializeBottomNavigation()
    {
//        val bottomNavigation = this.findViewById(R.id.bottom_navigation) as AHBottomNavigation
//        this.bottomNavigation = bottomNavigation
        val bottomNavigation = this.findViewById(R.id.bottom_navigation) as LinearLayout
        this.bottomNavigation = bottomNavigation
    }


//    override fun onDestroy() {
//        super.onDestroy()
//        // TODO sheet manager remove context reference
//
//    }


    /**
     * Initialize the sidebars.
     */
    override fun initializeSidebars()
    {
        val drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout

        // Right Sidebar
        // -------------------------------------------------------------------------------------
        val menuRight = findViewById(R.id.toolbar_options_button) as ImageView

        val rightNavView = findViewById(R.id.right_nav_view) as NavigationView
        val sheetOptionsViewBuilder = SheetOptionsViewBuilder(this)
        rightNavView.addView(sheetOptionsViewBuilder.view())

        menuRight.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END)
            else
                drawerLayout.openDrawer(GravityCompat.END)
        }
    }


    private fun configureBottomNavigation(sheetId : SheetId, uiColors : UIColors)
    {
        val viewBuilder = BottomNavigationViewBuilder(uiColors, sheetId, this)
        this.bottomNavigation?.addView(viewBuilder.view())
    }


    // -----------------------------------------------------------------------------------------
    // SHEET UI
    // -----------------------------------------------------------------------------------------

    override fun pagePagerAdatper() : PagePagerAdapter = this.pagePagerAdapter!!


//    override fun bottomNavigation() : AHBottomNavigation = this.bottomNavigation!!


    override fun rootSheetView() : View? = this.viewPager


    fun applyTheme(sheetId : SheetId, uiColors : UIColors)
    {
        val entityId = EntitySheetId(sheetId)

        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = this.window

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = colorOrBlack(uiColors.toolbarBackgroundColorId(), entityId)
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        // Toolbar > Background
        toolbar.setBackgroundColor(colorOrBlack(uiColors.toolbarBackgroundColorId(), entityId))

        // Toolbar > Icons
        var iconColor = colorOrBlack(uiColors.toolbarIconsColorId(), entityId)

        val menuLeftButton = this.findViewById(R.id.menuLeft) as ImageView
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

//        val navButton = this.findViewById(R.id.toolbar_nav_button) as ImageView
//        navButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val optionsButton = this.findViewById(R.id.toolbar_options_button) as ImageView
        optionsButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById(R.id.toolbar_title) as TextView
        titleView.setTextColor(colorOrBlack(uiColors.toolbarTitleColorId(), entityId))

        // TAB LAYOUT
        // -------------------------------------------------------------------------------------
        val tabLayout = this.findViewById(R.id.tab_layout) as CustomTabLayout

        // Tab Layout > Background
        tabLayout.setBackgroundColor(colorOrBlack(uiColors.tabBarBackgroundColorId(), entityId))

        // Tab Layout > Text
        tabLayout.setTabTextColors(colorOrBlack(uiColors.tabTextNormalColorId(), entityId),
                                   colorOrBlack(uiColors.tabTextSelectedColorId(), entityId))

        // Tab Layout > Underline
        tabLayout.setSelectedTabIndicatorColor(
                colorOrBlack(uiColors.tabUnderlineColorId(), entityId))

        // BOTTOM NAVIGATION VIEW
        // -------------------------------------------------------------------------------------
        this.configureBottomNavigation(sheetId, uiColors)

    }


    override fun context() : Context = this


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

        val updateToolbarOnNameChange = OnVariableChangeListener(
                { updateToolbar(it, sheet.sheetId()) },
                {})
        addOnVariableChangeListener(VariableId("name"),
                                    updateToolbarOnNameChange,
                                    entityId)

        this.renderSheet(sheet)
    }


    fun renderSheet(sheet : Sheet)
    {
        // Theme UI
        val theme = ThemeManager.theme(sheet.settings().themeId())
        when (theme)
        {
            is Val -> this.applyTheme(sheet.sheetId(), theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        val start = System.currentTimeMillis()

        val section = sheet.sections().firstOrNull()
        if (section != null) {
            pagePagerAdatper().setPages(section.pages(), sheet.sheetId())
        }

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

