
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
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem

import com.kispoko.tome.R
import com.kispoko.tome.activity.nav.NavigationActivity
import com.kispoko.tome.activity.sheet.state.SheetStateActivity
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.variable.TextVariable
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.model.sheet.Sheet
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.sheet.widget.table.TableWidgetRow
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.rts.sheet.*
import com.kispoko.tome.util.Util
import com.kispoko.tome.util.configureToolbar
import com.kispoko.tome.util.initializeState
import effect.Err
import effect.Just
import effect.Val



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



/**
 * Sheet Activity
 */
class SheetActivity : AppCompatActivity(), SheetUI
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var pagePagerAdapter : PagePagerAdapter?   = null
    private var viewPager : ViewPager? = null
    private var bottomNavigation : AHBottomNavigation? = null

    private var fab : FloatingActionButton? = null
    private var bottomSheet : RelativeLayout? = null
    private var bottomSheetBehavior : BottomSheetBehavior<RelativeLayout>? = null

    private var toolbarView : FrameLayout? = null
    private var activeTableRow : TableWidgetRow? = null



    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_sheet)

        // (2) Configure UI
        // -------------------------------------------------------------------------------------

        this.configureToolbar("")

        this.initializeViews()

        this.initializeBottomNavigation()

        // (3) Initialize State
        // -------------------------------------------------------------------------------------

//        val sheetListener = SheetListener({ onSheetReady(it) })
//        SheetManager.addSheetListener(SheetId("casmey_beginner"), sheetListener)

        this.initializeState()

    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // UI
    // -----------------------------------------------------------------------------------------

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
                val sheetContext = SheetManager.currentSheetContext()
                if (sheetContext != null)
                    hideActionBar()
                SheetActivityGlobal.cancelLongPressRunnable()
            }
        })

        val tabLayout = this.findViewById(R.id.tab_layout) as TabLayout
        tabLayout.setupWithViewPager(viewPager)

        val navButtonView = this.findViewById(R.id.toolbar_nav_button) as ImageView
        navButtonView.setOnClickListener {
            val intent = Intent(this, NavigationActivity::class.java)
            this.startActivity(intent)
        }
    }


    private fun initializeBottomNavigation()
    {
        val bottomNavigation = this.findViewById(R.id.bottom_navigation) as AHBottomNavigation
        this.bottomNavigation = bottomNavigation
    }


    override fun onDestroy() {
        super.onDestroy()
        // TODO sheet manager remove context reference

    }


    /**
     * Initialize the sidebars.
     */
    override fun initializeSidebars(sheetContext : SheetContext)
    {
        val drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout

        // Right Sidebar
        // -------------------------------------------------------------------------------------
        val menuRight = findViewById(R.id.toolbar_options_button) as ImageView

        val rightNavView = findViewById(R.id.right_nav_view) as NavigationView
        val sheetOptionsViewBuilder = SheetOptionsViewBuilder(SheetUIContext(sheetContext, this))
        rightNavView.addView(sheetOptionsViewBuilder.view())

        menuRight.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END)
            else
                drawerLayout.openDrawer(GravityCompat.END)
        }

        //
//        menuLeft.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                    drawerLayout.closeDrawer(GravityCompat.START);
//                } else {
//                    drawerLayout.openDrawer(GravityCompat.START);
//                }
//            }
//        });
    }


    private fun configureBottomNavigation(sheetId : SheetId, uiColors : UIColors)
    {
        val sheetEff = SheetManager.sheet(sheetId)

        when (sheetEff)
        {
            is Val ->
            {
                val sheet = sheetEff.value
                val sections = sheet.sections()

                val section1 = sections[0]
                val item1 = AHBottomNavigationItem(section1.nameString(),
                                                   section1.icon().drawableResId())
                bottomNavigation?.addItem(item1)

                if (sections.size > 1)
                {
                    val section2 = sections[1]
                    val item2 = AHBottomNavigationItem(section2.nameString(),
                            section2.icon().drawableResId())
                    bottomNavigation?.addItem(item2)
                }

                if (sections.size > 2)
                {
                    val section3 = sections[2]
                    val item3 = AHBottomNavigationItem(section3.nameString(),
                            section3.icon().drawableResId())
                    bottomNavigation?.addItem(item3)
                }


                bottomNavigation?.defaultBackgroundColor =
                                SheetManager.color(sheetId, uiColors.bottomBarBackgroundColorId())
                bottomNavigation?.accentColor =
                                SheetManager.color(sheetId, uiColors.bottomBarActiveColorId())
                bottomNavigation?.inactiveColor =
                                SheetManager.color(sheetId, uiColors.bottomBarInactiveColorId())
            }
            is Err -> ApplicationLog.error(sheetEff.error)
        }

    }


    fun showTableEditor(tableWidgetRow : TableWidgetRow,
                        updateTarget : UpdateTarget,
                        sheetContext : SheetContext)
    {

        val sheetUIContext = SheetUIContext(sheetContext, this)
        activeTableRow?.onEditorClose(sheetUIContext)

        val toolbarView = this.findViewById(R.id.sheet_toolbar) as FrameLayout
        toolbarView.visibility = View.VISIBLE
        val tableActionBarBuilder = TableActionBarViewBuilder(updateTarget, sheetUIContext)
        this.toolbarView = toolbarView
        this.activeTableRow = tableWidgetRow

        toolbarView.removeAllViews()
        toolbarView.addView(tableActionBarBuilder.view())

        val viewPager = this.viewPager
        viewPager?.setPadding(0, 0, 0, Util.dpToPixel(130f))
    }


    override fun hideActionBar()
    {
        this.toolbarView?.visibility = View.GONE

        val viewPager = this.viewPager
        //viewPager?.setPadding(0, 0, 0, 0)
        this.viewPager?.setPadding(0, 0, 0, Util.dpToPixel(60f))

        SheetManager.currentSheetContext() apDo {
            val sheetUIContext = SheetUIContext(it, this)
            this.activeTableRow?.onEditorClose(sheetUIContext)
            this.activeTableRow = null
        }
    }


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


    // -----------------------------------------------------------------------------------------
    // SHEET UI
    // -----------------------------------------------------------------------------------------

    override fun pagePagerAdatper() : PagePagerAdapter = this.pagePagerAdapter!!


    override fun bottomNavigation() : AHBottomNavigation = this.bottomNavigation!!


    override fun rootSheetView() : View? = this.viewPager


    override fun applyTheme(sheetId : SheetId, uiColors : UIColors)
    {
        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = this.window

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = SheetManager.color(sheetId, uiColors.toolbarBackgroundColorId())
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        // Toolbar > Background
        toolbar.setBackgroundColor(SheetManager.color(sheetId, uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = SheetManager.color(sheetId, uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById(R.id.menuLeft) as ImageView
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val navButton = this.findViewById(R.id.toolbar_nav_button) as ImageView
        navButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val optionsButton = this.findViewById(R.id.toolbar_options_button) as ImageView
        optionsButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById(R.id.toolbar_title) as TextView
        titleView.setTextColor(SheetManager.color(sheetId, uiColors.toolbarTitleColorId()))

        // TAB LAYOUT
        // -------------------------------------------------------------------------------------
        val tabLayout = this.findViewById(R.id.tab_layout) as CustomTabLayout

        // Tab Layout > Background
        tabLayout.setBackgroundColor(SheetManager.color(sheetId, uiColors.tabBarBackgroundColorId()))

        // Tab Layout > Text
        tabLayout.setTabTextColors(SheetManager.color(sheetId, uiColors.tabTextNormalColorId()),
                                   SheetManager.color(sheetId, uiColors.tabTextSelectedColorId()))

        // Tab Layout > Underline
        tabLayout.setSelectedTabIndicatorColor(
                SheetManager.color(sheetId, uiColors.tabUnderlineColorId()))

        // BOTTOM NAVIGATION VIEW
        // -------------------------------------------------------------------------------------
        this.configureBottomNavigation(sheetId, uiColors)

    }


    override fun context() : Context = this


    // -----------------------------------------------------------------------------------------
    // SHEET
    // -----------------------------------------------------------------------------------------

    override fun onSheetActive(sheet : Sheet)
    {
        // Set sheet to be active
        // -------------------------------------------------------------------------------------

        val coordinatorLayout = this.findViewById(R.id.coordinator_layout) as CoordinatorLayout
        coordinatorLayout.visibility = View.VISIBLE


        // Configure toolbar to be character name
        // -------------------------------------------------------------------------------------

        val maybeName = SheetManager.sheetState(sheet.sheetId())     ap { state ->
                        state.textVariableWithId(VariableId("name")) ap { textVar ->
                            textVar.value(state.sheetContext)
                        } }

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
        SheetManager.sheetContext(sheet) apDo { sheetContext ->
            SheetManager.addOnVariableChangeListener(
                    sheet.sheetId(),
                    VariableId("name"),
                    OnVariableChangeListener(
                        { this.updateToolbar(it, sheetContext)},
                        {})
                    )
        }

    }


    private fun updateToolbar(variable : Variable, sheetContext : SheetContext)
    {
        when (variable)
        {
            is TextVariable ->
            {
                val mText = variable.variableValue().value(sheetContext)
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


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    private fun optionsView(sheetUIContext : SheetUIContext) : View
    {
        val layout          = this.optionsViewLayout(sheetUIContext)

        return layout
    }


    private fun optionsViewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(sheetUIContext.context)
    }

}


class SheetOptionsViewBuilder(val sheetUIContext : SheetUIContext)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val sheetActivity : SheetActivity = sheetUIContext.context as SheetActivity


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------


    fun view() : LinearLayout
    {
        val layout = this.viewLayout()

        // Header
        layout.addView(this.headerView())

        // ------------------------
        layout.addView(this.dividerView())

        // Edit Layout View
        layout.addView(this.editLayoutView())

        // ------------------------
        layout.addView(this.dividerView())

        // Settings
        layout.addView(this.settingsView())

        // ------------------------
        layout.addView(this.dividerView())

        // State Button
        layout.addView(this.advancedView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.topDp    = 34f

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // HEADER
    // -----------------------------------------------------------------------------------------

    private fun headerView() : TextView
    {
        val header                  = TextViewBuilder()

        header.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        header.textId               = R.string.sheet_options

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        header.color                = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        header.font                 = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Bold,
                                                    sheetUIContext.context)

        header.sizeSp               = 16f

        header.margin.leftDp        = 10f
        header.margin.bottomDp      = 12f

        return header.textView(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // GENERAL
    // -----------------------------------------------------------------------------------------

    private fun dividerView(): LinearLayout
    {
        val divider                 = LinearLayoutBuilder()

        divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp            = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        divider.backgroundColor     = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        return divider.linearLayout(sheetUIContext.context)
    }


    private fun buttonView(iconId : Int,
                           labelId : Int) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val icon            = ImageViewBuilder()
        val label           = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

        layout.margin.leftDp    = 10f
        layout.margin.rightDp   = 10f

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 20
        icon.heightDp       = 20

        icon.image          = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color          = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        icon.margin.rightDp = 12f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId        = labelId

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color         = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        label.font          = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            sheetUIContext.context)

        label.sizeSp         = 17f


        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // EDIT LAYOUT VIEW
    // -----------------------------------------------------------------------------------------

    private fun editLayoutView() : LinearLayout
    {
        val layout = this.editLayoutViewLayout()

        layout.addView(editModeView())

        layout.addView(this.buttonView(R.drawable.icon_layout,
                                       R.string.layout_editor))

        return layout
    }


    private fun editLayoutViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun editModeView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val label                   = TextViewBuilder()
        val switch                  = SwitchBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.margin.leftDp        = 10f
        layout.margin.rightDp       = 10f

        layout.padding.topDp        = 5f
        layout.padding.bottomDp     = 5f

        layout.child(label)
              .child(switch)

        // (3 A) Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT
        label.weight                = 1f

        label.textId                = R.string.edit_mode

        label.font                  = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color                 = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        label.sizeSp                = 17f

        // (3 B) Switcher
        // -------------------------------------------------------------------------------------

        switch.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        switch.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        switch.checked              = false

        switch.scaleX               = 0.9f
        switch.scaleY               = 0.9f

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // SETTINGS VIEW
    // -----------------------------------------------------------------------------------------

    private fun settingsView() : LinearLayout
    {
        val layout = this.settingsViewLayout()

        // Theme Button
        layout.addView(this.buttonView(R.drawable.icon_change_theme,
                                       R.string.manage_themes))

        // Settings Button
        layout.addView(this.buttonView(R.drawable.icon_settings,
                                       R.string.settings))

        return layout
    }


    private fun settingsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // ADVANCED VIEW
    // -----------------------------------------------------------------------------------------

    private fun advancedView() : LinearLayout
    {
        val layout = this.advancedViewLayout()

        // State Button
        val stateButton = this.buttonView(R.drawable.icon_console,
                                          R.string.view_state)

        stateButton.setOnClickListener {
            val intent = Intent(sheetActivity, SheetStateActivity::class.java)
            intent.putExtra("sheet_id", sheetUIContext.sheetId)
            sheetActivity.startActivity(intent)
        }
        layout.addView(stateButton)

        return layout
    }


    private fun advancedViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

        return layout.linearLayout(sheetUIContext.context)
    }

//
//    private fun stateButtonView(sheetUIContext : SheetUIContext) : TextView
//    {
//        val button                  = TextViewBuilder()
//
//        button.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        button.textId               = R.string.view_state
//
//        button.gravity              = Gravity.CENTER
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        button.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)
//
//        button.corners              = Corners(TopLeftCornerRadius(2f),
//                                              TopRightCornerRadius(2f),
//                                              BottomRightCornerRadius(2f),
//                                              BottomLeftCornerRadius(2f))
//
//        val textColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        button.color                = SheetManager.color(sheetUIContext.sheetId, textColorTheme)
//
//        button.font                 = Font.typeface(TextFont.FiraSans,
//                                                    TextFontStyle.Regular,
//                                                    sheetUIContext.context)
//
//        button.sizeSp               = 16f
//
//        button.margin.leftDp        = 10f
//        button.margin.rightDp       = 10f
//
//        button.margin.topDp         = 12f
//        button.margin.bottomDp      = 12f
//
//        button.padding.topDp        = 8f
//        button.padding.bottomDp     = 8f
//        button.padding.leftDp       = 8f
//        button.padding.rightDp      = 8f
//
//        return button.textView(sheetUIContext.context)
//    }
}



//
//    // INTERNAL
//    // -------------------------------------------------------------------------------------------
//
//    // > Initialization Methods
//    // -------------------------------------------------------------------------------------------

//    /**
//     * Initialize the navigation menu.
//     */
//    private void initializeNavigation()
//    {
//        // App Navigation View
//        NavigationView appNavigationView = (NavigationView) findViewById(R.id.app_navigation_view);
//        appNavigationView.addView(this.appNavigationView(this));
//
//        // Sheet Navigation View
//        NavigationView sheetNavigationView =
//                                    (NavigationView) findViewById(R.id.sheet_navigation_view);
//        sheetNavigationView.addView(this.sheetNavigationView(this));
//    }
//
//
//    /**
//     * Initialize the bottom navigation menu.
//     */
//    private void initializeBottomNavigation()
//    {
//        BottomNavigationView bottomNavigationView =
//                (BottomNavigationView) findViewById(R.id.bottom_navigation);
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(
//                new BottomNavigationView.OnNavigationItemSelectedListener()
//        {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item)
//            {
//                // TODO make sure sheet is loaded
//                switch (item.getItemId())
//                {
////                    case R.id.button_section_profile:
////                        SheetManagerOld.currentSheet().profileSection().render(pagePagerAdapter);
////                        break;
////                    case R.id.button_section_encounter:
////                        SheetManagerOld.currentSheet().encounterSection().render(pagePagerAdapter);
////                        break;
////                    case R.id.button_section_campaign:
////                        SheetManagerOld.currentSheet().campaignSection().render(pagePagerAdapter);
////                        break;
//                }
//                return true;
//            }
//        });
//    }
//
//
//    /**
//     * Setup the main sheet activity views. The PagePagerAdapter controls the left-right-swiping
//     * between different character sheet pages.. It is connected to a tab layout, so that users
//     * may select the character sheet pages by name.
//     */

//
//    // > Data
//    // -------------------------------------------------------------------------------------------
//
//    /**
//     * Load a sheet from a yaml file.
//     */
//    private void loadSheet()
//    {
//        // If previous activity was template chooser, get id of chosen template
//        String templateId = null;
//        if (getIntent().hasExtra("official_template_id"))
//            templateId = getIntent().getStringExtra("official_template_id");
//
////        CampaignIndex.initialize(this.getApplicationContext());
////
////        // > This will be a new character sheet
////        if (templateId != null)
////            SheetManagerOld.goToTemplate(this, templateId, this);
////        // > Load the most recently used character sheet
////        else
////            SheetManagerOld.goToMostRecent(this, this);
//    }
//
//
//    // > Views
//    // -------------------------------------------------------------------------------------------
//
//    // ** APP Navigation View
//    // -----------------------------------------------------------------------------------------
//
//    private ScrollView appNavigationView(Context context)
//    {
//        ScrollView scrollView = this.navigationScrollView(context);
//
//        LinearLayout layout = this.appNavigationLayout(context);
//
//        // > Account
//        layout.addView(this.accountView(context));
//
//        layout.addView(this.dividerView(context));
//
//        // > Sheet Buttons
//        layout.addView(this.sheetButtonsView(context));
//
//        layout.addView(this.dividerView(context));
//
//        // > App Buttons
//        layout.addView(this.appButtonsView(context));
//
//        scrollView.addView(layout);
//
//        return scrollView;
//    }
//
//
//    private LinearLayout appNavigationLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation      = LinearLayout.VERTICAL;
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;
//
//        layout.padding.top      = R.dimen.nav_view_padding_top;
//
//        layout.backgroundColor  = R.color.dark_blue_11;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout accountView(Context context)
//    {
//        LinearLayout layout = this.accountViewLayout(context);
//
//        // > User
//        layout.addView(this.userView(context));
//
//        // > Controls
//        layout.addView(this.accountControlsView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout accountViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation      = LinearLayout.VERTICAL;
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.padding.leftDp   = 10f;
//        layout.padding.rightDp  = 10f;
//        layout.padding.topDp    = 10f;
//        layout.padding.bottomDp = 20f;
//
//        return layout.linearLayout(context);
//    }
//
//
//
//    private LinearLayout userView(Context context)
//    {
//        LinearLayout layout = this.userViewLayout(context);
//
//        // > Account Picture
//        layout.addView(accountIconView(context));
//
//        // > Account Name
//        layout.addView(accountNameView(context));
//
//        return layout;
//    }
//
//
//    private ImageView accountIconView(Context context)
//    {
//        ImageViewBuilder icon = new ImageViewBuilder();
//
//        icon.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
//        icon.height         = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        icon.image          = R.drawable.ic_app_nav_account;
//
//        icon.color          = R.color.dark_blue_hl_2;
//
//        return icon.imageView(context);
//    }
//
//
//    private TextView accountNameView(Context context)
//    {
//        TextViewBuilder name = new TextViewBuilder();
//
//        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
//        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        name.text               = "Jeff Wise";
//
//        name.font               = Font.serifFontRegular(context);
//        name.color              = R.color.gold_light;
//        name.sizeSp             = 17f;
//
//        name.margin.leftDp      = 10f;
//
//        return name.textView(context);
//    }
//
//
//    private LinearLayout userViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation      = LinearLayout.HORIZONTAL;
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity          = Gravity.CENTER_VERTICAL;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout accountControlsView(Context context)
//    {
//        LinearLayout layout = this.accountControlsViewLayout(context);
//
//        layout.addView(this.createAccountButtonView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout accountControlsViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation      = LinearLayout.HORIZONTAL;
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity          = Gravity.CENTER_HORIZONTAL;
//
//        layout.margin.topDp     = 20f;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout createAccountButtonView(Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//        ImageViewBuilder    icon   = new ImageViewBuilder();
//        TextViewBuilder     label  = new TextViewBuilder();
//
//        // [2] Declarations
//        // -------------------------------------------------------------------------------------
//
//        layout.orientation          = LinearLayout.HORIZONTAL;
//
//        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity              = Gravity.CENTER_VERTICAL;
//
//        layout.child(icon)
//              .child(label);
//
//        // [3 A] Icon
//        // -------------------------------------------------------------------------------------
//
//        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
//        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        icon.image                  = R.drawable.ic_app_nav_create_account;
//
//        icon.color                  = R.color.dark_blue_hl_8;
//
//        // [3 B] Label
//        // -------------------------------------------------------------------------------------
//
//        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
//        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        label.textId                = R.string.create_account;
//
//        label.font                  = Font.serifFontRegular(context);
//        label.color                 = R.color.dark_blue_hl_8;
//        label.sizeSp                = 13f;
//
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout sheetButtonsView(Context context)
//    {
//        LinearLayout layout = this.buttonsLayout(context);
//
//        // > My Sheets
//        // -------------------------------------------------------------------------------------
//        LinearLayout mySheetsButton = this.buttonView(R.string.my_sheets,
//                                                      R.drawable.ic_app_nav_my_sheets,
//                                                      context);
//        layout.addView(mySheetsButton);
//
//        // > New Sheet
//        // -------------------------------------------------------------------------------------
//        LinearLayout newSheetButton = this.buttonView(R.string.new_sheet,
//                                                      R.drawable.ic_app_nav_new_sheet,
//                                                      context);
//        layout.addView(newSheetButton);
//
//        return layout;
//    }
//
//
//    private LinearLayout appButtonsView(Context context)
//    {
//        LinearLayout layout = this.buttonsLayout(context);
//
//        // > Tutorials
//        // -------------------------------------------------------------------------------------
//        LinearLayout tutorialsButton = this.buttonView(R.string.tutorials,
//                                                       R.drawable.ic_app_nav_help,
//                                                       context);
//        layout.addView(tutorialsButton);
//
//        // > Settings
//        // -------------------------------------------------------------------------------------
//        LinearLayout settingsButton = this.buttonView(R.string.settings,
//                                                      R.drawable.ic_app_nav_settings,
//                                                      context);
//        layout.addView(settingsButton);
//
//        // > Feedback
//        // -------------------------------------------------------------------------------------
//        LinearLayout feedbackButton = this.buttonView(R.string.give_us_your_feedback,
//                R.drawable.ic_app_nav_feedback,
//                context);
//        layout.addView(feedbackButton);
//
//        // > Upgrades
//        // -------------------------------------------------------------------------------------
//        LinearLayout upgradesButton = this.buttonView(R.string.upgrades,
//                                                      R.drawable.ic_app_nav_upgrades,
//                                                      context);
//        layout.addView(upgradesButton);
//
//        // > News
//        // -------------------------------------------------------------------------------------
//        LinearLayout newsButton = this.buttonView(R.string.news_slash_updates,
//                                                  R.drawable.ic_app_nav_news,
//                                                  context);
//        layout.addView(newsButton);
//
//        // > About This App
//        // -------------------------------------------------------------------------------------
//        LinearLayout aboutButton = this.buttonView(R.string.about_this_app,
//                                                   R.drawable.ic_app_nav_about_us,
//                                                   context);
//        layout.addView(aboutButton);
//
//        return layout;
//    }
//

