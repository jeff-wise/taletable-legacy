
package com.kispoko.tome.activity.sheet


import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem

import com.kispoko.tome.R
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.load.LoadResultError
import com.kispoko.tome.load.LoadResultValue
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.official.OfficialIndex
import com.kispoko.tome.rts.campaign.CampaignManager
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.theme.ThemeManager
import com.kispoko.tome.rts.sheet.*
import com.kispoko.tome.util.configureToolbar
import effect.Err
import effect.Val
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch



/**
 * Sheet Activity
 */
class SheetActivity : AppCompatActivity(), SheetUI
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var pagePagerAdapter : PagePagerAdapter?   = null

    var bottomNavigation : AHBottomNavigation? = null


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

        // (3) Load Sheet
        // -------------------------------------------------------------------------------------

        val officialIndex = OfficialIndex.load(this)
        if (officialIndex != null)
            this.loadSheet(officialIndex)

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

        val viewPager = this.findViewById(R.id.page_pager) as ViewPager
        viewPager.adapter = pagePagerAdapter

        val tabLayout = this.findViewById(R.id.tab_layout) as TabLayout
        tabLayout.setupWithViewPager(viewPager)
    }


    /**
     * Initialize the side drawers.
     */
    override fun updateSwitcherView(sheetContext : SheetContext)
    {
        val sheetUIContext = SheetUIContext(sheetContext, this)
        val drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout

        // Right Sidebar
        // -------------------------------------------------------------------------------------
        val menuRight = findViewById(R.id.menuRight) as ImageButton

        val rightNavView = findViewById(R.id.right_nav_view) as NavigationView
        rightNavView.addView(SwitcherSidebarView.view(sheetUIContext))

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
        val bottomNavigation = this.findViewById(R.id.bottom_navigation) as AHBottomNavigation

        this.bottomNavigation = bottomNavigation

        val sheetEff = SheetManager.sheet(sheetId)

        when (sheetEff)
        {
            is Val ->
            {
                val sheet = sheetEff.value

                val section1 = sheet.sections()[0]
                val item1 = AHBottomNavigationItem(section1.nameString(),
                                                   R.drawable.ic_bottom_nav_profile)
                val section2 = sheet.sections()[1]
                val item2 = AHBottomNavigationItem(section2.nameString(),
                                                   R.drawable.ic_bottom_nav_campaign)
                val section3 = sheet.sections()[2]
                val item3 = AHBottomNavigationItem(section3.nameString(),
                                                   R.drawable.ic_bottom_nav_encounter)

                bottomNavigation.addItem(item1)
                bottomNavigation.addItem(item2)
                bottomNavigation.addItem(item3)

                bottomNavigation.defaultBackgroundColor =
                                SheetManager.color(sheetId, uiColors.bottomBarBackgroundColorId())
                bottomNavigation.accentColor =
                                SheetManager.color(sheetId, uiColors.bottomBarActiveColorId())
                bottomNavigation.inactiveColor =
                                SheetManager.color(sheetId, uiColors.bottomBarInactiveColorId())
            }
            is Err -> ApplicationLog.error(sheetEff.error)
        }

    }


    // -----------------------------------------------------------------------------------------
    // SHEET UI
    // -----------------------------------------------------------------------------------------

    override fun pagePagerAdatper() : PagePagerAdapter = this.pagePagerAdapter!!


    override fun bottomNavigation() : AHBottomNavigation = this.bottomNavigation!!


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

        val menuLeftButton = this.findViewById(R.id.menuLeft) as ImageButton
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById(R.id.menuRight) as ImageButton
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

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

    fun loadSheet(officialIndex : OfficialIndex)
    {
        val sheetId = SheetId("casmey_beginner")

        val sheetRecord = SheetManager.sheetRecord(sheetId)
        when (sheetRecord)
        {
            is Val -> {
                val start = System.currentTimeMillis()
                SheetManager.render(sheetId, this)
                val end = System.currentTimeMillis()

                Log.d("***SHEETACTIVITY", "time to render ms: " + (end - start).toString())
            }
            is Err -> this.loadTemplateSheet(officialIndex, sheetId)
        }

    }


    fun loadTemplateSheet(officialIndex : OfficialIndex, sheetId : SheetId)
    {
        val officialSheet = officialIndex.sheetById[sheetId]

        if (officialSheet != null)
        {
            val sheetActivity : AppCompatActivity = this
            val sheetUI : SheetUI = this
            launch(UI) {

                ThemeManager.loadOfficialThemes(officialIndex.themes, sheetActivity)

                val sheetLoad = SheetManager.loadOfficialSheet(officialSheet,
                                                               officialIndex,
                                                               sheetActivity)

                when (sheetLoad)
                {
                    is LoadResultValue ->
                    {
                        val sheet = sheetLoad.value

                        SheetManager.setNewSheet(sheet, sheetUI)

                        val characterName =
                                SheetManager.sheetRecord(sheet.sheetId())    ap { (_, sheetContext, state) ->
                                state.textVariableWithId(VariableId("name")) ap { textVar ->
                                    textVar.value(sheetContext)
                                } }

                        when (characterName)
                        {
                            is Val -> sheetActivity.configureToolbar(characterName.value)
                            is Err -> ApplicationLog.error(characterName.error)
                        }

    //                        SheetManager.sheetRecord(sheet.sheetId()) apDo {
    //                            launch(UI) {
    //                                it.sheet.saveAsync(true, true)
    //                            }
    //                        }
                    }
                    is LoadResultError -> Log.d("***SHEET_ACTIVITY", sheetLoad.userMessage)
                }

            }

        }

    }

}



object SwitcherSidebarView
{

    fun view(sheetUIContext : SheetUIContext) : View
    {
        val layout = this.viewLayout(sheetUIContext)

        layout.addView(this.sheetSwitcherView(sheetUIContext))

        layout.addView(this.campaignSwitcherView(sheetUIContext))

        layout.addView(this.gameSwitcherView(sheetUIContext))

        return layout
    }


    fun viewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.topDp    = 40f

        val labelCcolorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, labelCcolorTheme)

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // GENERAL VIEWS
    // -----------------------------------------------------------------------------------------

    fun switcherViewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.bottomDp  = 25f

        return layout.linearLayout(context)
    }


    fun switcherHeaderView(labelId : Int,
                           sheetUIContext : SheetUIContext) : LinearLayout
    {

        val layout          = this.switcherHeaderViewLayout(sheetUIContext)

        layout.addView(this.switcherLabelView(labelId, sheetUIContext))

        layout.addView(this.newButtonView(sheetUIContext))

        return layout
    }


    private fun switcherHeaderViewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 54

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.margin.leftDp    = 5f
        layout.margin.rightDp   = 5f

        layout.padding.leftDp   = 5f

        val labelBgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        layout.backgroundColor   = SheetManager.color(sheetUIContext.sheetId, labelBgColorTheme)

        layout.corners          = Corners(TopLeftCornerRadius(1f),
                                          TopRightCornerRadius(1f),
                                          BottomRightCornerRadius(1f),
                                          BottomLeftCornerRadius(1f))

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun switcherLabelView(labelId : Int, sheetUIContext : SheetUIContext) : TextView
    {
        val label               = TextViewBuilder()

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT
        label.weight            = 4f

        label.text              = sheetUIContext.context.getString(labelId).toUpperCase()

        label.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        label.layoutGravity     = Gravity.CENTER_VERTICAL

        val labelCcolorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_14")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color             = SheetManager.color(sheetUIContext.sheetId, labelCcolorTheme)


        label.sizeSp            = 13f

        return label.textView(sheetUIContext.context)
    }


    private fun newButtonView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout  = LinearLayoutBuilder()
        val icon    = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.padding.leftDp   = 15f
        layout.padding.rightDp  = 15f

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        layout.backgroundColor   = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp           = 25
        icon.heightDp          = 25
        icon.weight            = 1f

        icon.image             = R.drawable.ic_switcher_new

        icon.layoutGravity     = Gravity.CENTER_VERTICAL

        val buttonColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color             = SheetManager.color(sheetUIContext.sheetId, buttonColorTheme)

        return layout.linearLayout(sheetUIContext.context)
    }


    fun switcherCardHeaderView(nameString : String, sheetUIContext : SheetUIContext) : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.font             = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        header.text             = nameString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        header.color            = SheetManager.color(sheetUIContext.sheetId, colorTheme)


        header.sizeSp           = 15.5f

        return header.textView(sheetUIContext.context)
    }


    fun switcherCardSummaryView(summaryString : String,
                                sheetUIContext : SheetUIContext) : TextView
    {
        val summary             = TextViewBuilder()

        summary.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        summary.font            = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        summary.text            = summaryString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        summary.color           = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        summary.sizeSp          = 13f

        return summary.textView(sheetUIContext.context)
    }


    fun switcherListViewLayout(context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.leftDp   = 5f
        layout.padding.rightDp  = 5f

        layout.margin.topDp     = 5f

        return layout.linearLayout(context)
    }


    fun switcherCardViewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.leftDp   = 6f
        layout.padding.rightDp  = 6f
        layout.padding.topDp    = 7f
        layout.padding.bottomDp = 7f

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners          = Corners(TopLeftCornerRadius(1f),
                                          TopRightCornerRadius(1f),
                                          BottomRightCornerRadius(1f),
                                          BottomLeftCornerRadius(1f))

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // GAME SWITCHER VIEW
    // -----------------------------------------------------------------------------------------

    fun gameSwitcherView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherViewLayout(sheetUIContext.context)

        layout.addView(this.switcherHeaderView(R.string.open_games, sheetUIContext))

        layout.addView(this.openGamesView(sheetUIContext))

        return layout
    }


    fun openGamesView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherListViewLayout(sheetUIContext.context)

        GameManager.openGames().forEach {
            layout.addView(this.openGameView(it.description().gameName(),
                                             it.description().summary(),
                                             sheetUIContext))
        }

        return layout
    }


    fun openGameView(gameName : String,
                     gameSummary : String,
                     sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherCardViewLayout(sheetUIContext)

        layout.addView(this.switcherCardHeaderView(gameName, sheetUIContext))

        layout.addView(this.switcherCardSummaryView(gameSummary, sheetUIContext))

        return layout
    }



    // -----------------------------------------------------------------------------------------
    // CAMPAIGN SWITCHER VIEW
    // -----------------------------------------------------------------------------------------

    fun campaignSwitcherView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherViewLayout(sheetUIContext.context)

        layout.addView(this.switcherHeaderView(R.string.open_campaigns, sheetUIContext))

        layout.addView(this.openCampaignsView(sheetUIContext))

        return layout
    }


    fun openCampaignsView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherListViewLayout(sheetUIContext.context)

        CampaignManager.openCampaigns().forEach {
            layout.addView(this.openCampaignView(it.campaignName(),
                                                 it.campaignSummary(),
                                                 sheetUIContext))
        }

        return layout
    }


    fun openCampaignView(campaignName : String,
                         campaignSummary : String,
                         sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherCardViewLayout(sheetUIContext)

        layout.addView(this.switcherCardHeaderView(campaignName, sheetUIContext))

        layout.addView(this.switcherCardSummaryView(campaignSummary, sheetUIContext))

        return layout
    }


    // -----------------------------------------------------------------------------------------
    // SHEET SWITCHER VIEW
    // -----------------------------------------------------------------------------------------

    fun sheetSwitcherView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherViewLayout(sheetUIContext.context)

        layout.addView(this.switcherHeaderView(R.string.open_sheets, sheetUIContext))

        layout.addView(this.openSheetsView(sheetUIContext))

        return layout
    }


    fun openSheetsView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherListViewLayout(sheetUIContext.context)

        SheetManager.openSheets().forEach {
            val sheetName = SheetManager.evalSheetName(sheetUIContext.sheetId,
                                                       it.settings().sheetName())
            val sheetSummary = SheetManager.evalSheetSummary(sheetUIContext.sheetId,
                                                             it.settings().sheetSummary())
            layout.addView(this.openSheetView(sheetName, sheetSummary, sheetUIContext))
        }

        return layout
    }


    fun openSheetView(sheetName : String,
                      sheetSummary : String,
                      sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.switcherCardViewLayout(sheetUIContext)

        layout.addView(this.switcherCardHeaderView(sheetName, sheetUIContext))

        layout.addView(this.switcherCardSummaryView(sheetSummary, sheetUIContext))

        return layout
    }


}



//public class SheetActivityOld
//       extends AppCompatActivity
//{
//
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    // > Requests
//    public static final int CHOOSE_IMAGE_FROM_FILE = 0;
//
//    // ComponentUtil
//    private Toolbar             toolbar;
//
//    private DrawerLayout        drawerLayout;
//
////    private ChooseImageAction   chooseImageAction;
//
//    private String              characterName;
//
//    private TextView            sheetNavCharacterNameView;
//
//    private PagePagerAdapter    pagePagerAdapter;
//
//
//    public static ViewPager     viewPager;
//
//
//    // ACTIVITY EVENTS
//    // -----------------------------------------------------------------------------------------
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_sheet);
//
//        loadSheet();
//
//        initializeToolbar();
//        initializeDrawers();
//        initializeNavigation();
//        initializeBottomNavigation();
//        prepareSheetViews();
//    }
//
//
//    @Override
//    public void onStart()
//    {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//
//    @Override
//    public void onStop()
//    {
//        EventBus.getDefault().unregister(this);
//        super.onStop();
//    }
//
//
//    @Override
//    public void onBackPressed()
//    {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.empty, menu);
//        return true;
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        // TODO clearn this up. maybe delete
//        // Skip Errors
//        if (resultCode != RESULT_OK) return;
//
//        // Process Chosen ImageWidget
////        if (requestCode == CHOOSE_IMAGE_FROM_FILE)
////        {
////            Uri uri = data.getData();
////            this.chooseImageAction.setImage(this, uri);
////            this.chooseImageAction = null;
////        }
//
//    }
//
//
//    // FRAGMENT EVENTS
//    // -------------------------------------------------------------------------------------------
//
//    /**
//     *
//     */
////    public void setChooseImageAction(ChooseImageAction chooseImageAction)
////    {
////        this.chooseImageAction = chooseImageAction;
////    }
//
//
//    // API
//    // -------------------------------------------------------------------------------------------
//
//    public void onSheet(Sheet sheet)
//    {
//        // Render the sheet
////        sheet.render(this.pagePagerAdapter);
////
////        // Set the title to the character's name, if available
////        this.characterName = "Sheet";
////
////        TextVariable nameVariable = State.variableWithName("name").textVariable();
////        if (!nameVariable.isNull())
////        {
////            try {
////                this.characterName = nameVariable.value();
////            }
////            catch (NullVariableException exception) {
////                this.characterName = "N/A";
////            }
////        }
//
//        TextView titleView = (TextView) findViewById(R.id.toolbar_title);
//        titleView.setText(this.characterName);
//
//        if (this.sheetNavCharacterNameView != null) {
//            this.sheetNavCharacterNameView.setText(this.characterName);
//        }
//
//    }


    // > Events
    //   (Event Bus Subscriptions)
    // -------------------------------------------------------------------------------------------

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onTextWidgetUpdateLiteralEvent(TextWidget.UpdateLiteralEvent event)
//    {
//        WidgetUnion widgetUnion = SheetManagerOld.currentSheet().widgetWithId(event.widgetId());
//
//        if (widgetUnion != null && widgetUnion.type() == WidgetType.TEXT)
//        {
//            TextWidget textWidget = widgetUnion.textWidget();
//            textWidget.setLiteralValue(event.newValue(), this);
//        }
    //}


//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onTextCellUpdateLiteralEvent(TextCell.UpdateLiteralEvent event)
//    {
//        WidgetUnion widgetUnion = SheetManagerOld.currentSheet().widgetWithId(event.tableWidgetId());
//
//        if (widgetUnion != null && widgetUnion.type() == WidgetType.TABLE)
//        {
//            TableWidget tableWidget = widgetUnion.tableWidget();
//            CellUnion cellUnion = tableWidget.cellWithId(event.cellId());
//
//            if (cellUnion != null && cellUnion.type() == CellType.TEXT)
//            {
//                TextCell textCell = cellUnion.textCell();
//                textCell.setLiteralValue(event.newValue(), this);
//            }
//        }
    //}

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

