
package com.kispoko.tome.activity.sheet


import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem

import com.kispoko.tome.R
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.CustomTabLayout
import com.kispoko.tome.load.LoadResultError
import com.kispoko.tome.load.LoadResultValue
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.theme.UIColors
import com.kispoko.tome.official.OfficialIndex
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

    var pagePagerAdapter : PagePagerAdapter? = null


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
        val sheetPagePagerAdapter = PagePagerAdapter(supportFragmentManager)
        this.pagePagerAdapter = sheetPagePagerAdapter

        val viewPager = this.findViewById(R.id.page_pager) as ViewPager
        viewPager.adapter = sheetPagePagerAdapter

        val tabLayout = this.findViewById(R.id.tab_layout) as TabLayout
        tabLayout.setupWithViewPager(viewPager)
    }


    private fun configureBottomNavigation(sheetId : SheetId, uiColors : UIColors)
    {
        val bottomNavigation = this.findViewById(R.id.bottom_navigation) as AHBottomNavigation

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
        val officialSheet = officialIndex.sheetById[SheetId("casmey_beginner")]
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
                                SheetManager.sheetRecord(sheet.sheetId()) apply { sheetRecord ->
                                    val sheetContext = sheetRecord.context(sheetActivity)
                                    sheetRecord.state.textVariableWithId(VariableId("name"))
                                            .applyWith({ textVar, ctx -> textVar.value(ctx)}, sheetContext)
                                }

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
//
//    /**
//     * Initialize the drawer ComponentUtil components.
//     */
//    private void initializeDrawers()
//    {
//        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//
//        ImageButton menuLeft = (ImageButton) findViewById(R.id.menuLeft);
//        ImageButton menuRight = (ImageButton) findViewById(R.id.menuRight);
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
//
//        menuRight.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
//                    drawerLayout.closeDrawer(GravityCompat.END);
//                } else {
//                    drawerLayout.openDrawer(GravityCompat.END);
//                }
//            }
//        });
//
//    }
//
//
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
//
//    // ** SHEET Navigation View
//    // -----------------------------------------------------------------------------------------
//
//    private ScrollView sheetNavigationView(Context context)
//    {
//        ScrollView scrollView = this.navigationScrollView(context);
//
//        LinearLayout layout = this.sheetNavigationLayout(context);
//
//        // > Character View
//        layout.addView(this.characterView(context));
//
//        // --- Divider
//        layout.addView(this.dividerView(context));
//
//        // > Programming Options
//        layout.addView(this.programmingButtonsView(context));
//
//        // --- Divider
//        layout.addView(this.dividerView(context));
//
//        // > Style Options
//        layout.addView(this.styleButtonsView(context));
//
//        scrollView.addView(layout);
//
//        return scrollView;
//    }
//
//
//    private LinearLayout sheetNavigationLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation      = LinearLayout.VERTICAL;
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;
//
//        layout.padding.topDp    = 27f;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout characterView(Context context)
//    {
//        LinearLayout layout = this.characterViewLayout(context);
//
//        // > Avatar
//        layout.addView(characterAvatarView(context));
//
//        // > Name
//        this.sheetNavCharacterNameView = this.characterNameView(context);
//        layout.addView(this.sheetNavCharacterNameView);
//
//        // > Description
//        layout.addView(characterDescriptionView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout characterViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.VERTICAL;
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity              = Gravity.CENTER_HORIZONTAL;
//
//        layout.padding.topDp        = 15f;
//        layout.padding.bottomDp     = 20f;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private ImageView characterAvatarView(Context context)
//    {
//        ImageViewBuilder avatar = new ImageViewBuilder();
//
//        avatar.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        avatar.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        avatar.image                = R.drawable.ic_sheet_menu_default_avatar;
//
//        avatar.color                = R.color.dark_blue_hl_2;
//
//        return avatar.imageView(context);
//    }
//
//
//    private TextView characterNameView(Context context)
//    {
//        TextViewBuilder name = new TextViewBuilder();
//
//        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
//        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        name.text               = this.characterName;
//
//        name.font               = Font.serifFontRegular(context);
//        name.color              = R.color.gold_light;
//        name.sizeSp             = 24f;
//
//        name.margin.topDp       = 8f;
//
//        return name.textView(context);
//    }
//
//
//    private TextView characterDescriptionView(Context context)
//    {
//        TextViewBuilder description = new TextViewBuilder();
//
//        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
//        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        description.text            = "Level 1 Human Fighter";
//
//        description.font            = Font.serifFontRegular(context);
//        description.color           = R.color.dark_blue_hl_6;
//        description.sizeSp          = 14f;
//
//        description.margin.topDp    = 8f;
//
//        return description.textView(context);
//    }
//
//
//    private LinearLayout programmingButtonsView(Context context)
//    {
//        LinearLayout layout = this.buttonsLayout(context);
//
//        // > Dictionary
//        // -------------------------------------------------------------------------------------
//
//        LinearLayout dictionaryButton = this.buttonView(R.string.dictionary,
//                                                        R.drawable.ic_sheet_nav_dictionary,
//                                                        context);
//
//        dictionaryButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent = new Intent(SheetActivityOld.this, DictionaryActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        layout.addView(dictionaryButton);
//
//        // > Functions
//        // -------------------------------------------------------------------------------------
//        LinearLayout functionsButton = this.buttonView(R.string.functions,
//                                                      R.drawable.ic_sheet_nav_functions,
//                                                      context);
//        functionsButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent = new Intent(SheetActivityOld.this, FunctionIndexActivity.class);
//                startActivity(intent);
//            }
//        });
//        layout.addView(functionsButton);
//
//        // > Programs
//        // -------------------------------------------------------------------------------------
//        LinearLayout programsButton = this.buttonView(R.string.programs,
//                                                      R.drawable.ic_sheet_nav_programs,
//                                                      context);
//        programsButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent = new Intent(SheetActivityOld.this, ProgramIndexActivity.class);
//                startActivity(intent);
//            }
//        });
//        layout.addView(programsButton);
//
//        // > Mechanics
//        // -------------------------------------------------------------------------------------
//        LinearLayout mechanicsButton = this.buttonView(R.string.mechanics,
//                                                       R.drawable.ic_sheet_nav_mechanics,
//                                                       context);
//        mechanicsButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent = new Intent(SheetActivityOld.this, MechanicIndexActivity.class);
//                startActivity(intent);
//            }
//        });
//        layout.addView(mechanicsButton);
//
//        // > Engine
//        // -------------------------------------------------------------------------------------
//        LinearLayout engineButton = this.buttonView(R.string.engine,
//                                                    R.drawable.ic_sheet_nav_debugger,
//                                                    context);
//        engineButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent = new Intent(SheetActivityOld.this, EngineActivity.class);
//                startActivity(intent);
//            }
//        });
//        layout.addView(engineButton);
//
//        return layout;
//    }
//
//
//    private LinearLayout styleButtonsView(Context context)
//    {
//        LinearLayout layout = this.buttonsLayout(context);
//
//        // > Layout
//        LinearLayout layoutButton = this.buttonView(R.string.layout,
//                                                    R.drawable.ic_sheet_nav_layout,
//                                                    context);
//        layout.addView(layoutButton);
//
//        // > Theme
//        LinearLayout themeButton = this.buttonView(R.string.theme,
//                                                   R.drawable.ic_sheet_nav_theme,
//                                                   context);
//        layout.addView(themeButton);
//
//        return layout;
//    }
//
//
//    // ** SHARED views & layouts
//    // -----------------------------------------------------------------------------------------
//
//    private ScrollView navigationScrollView(Context context)
//    {
//        ScrollViewBuilder scrollView = new ScrollViewBuilder();
//
//        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT;
//        scrollView.height       = LinearLayout.LayoutParams.MATCH_PARENT;
//
//        return scrollView.scrollView(context);
//    }
//
//
//    private LinearLayout buttonsLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.VERTICAL;
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity              = Gravity.CENTER_VERTICAL;
//
//        layout.padding.leftDp       = 15f;
//        layout.padding.topDp        = 10f;
//        layout.padding.bottomDp     = 10f;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout buttonView(int labelId, int iconId, Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//        ImageViewBuilder    icon   = new ImageViewBuilder();
//        TextViewBuilder     label  = new TextViewBuilder();
//
//        // [2] Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.orientation          = LinearLayout.HORIZONTAL;
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity              = Gravity.CENTER_VERTICAL;
//
//        layout.padding.topDp        = 15f;
//        layout.padding.bottomDp     = 15f;
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
//        icon.image                  = iconId;
//
//        icon.color                  = R.color.dark_blue_hl_2;
//
//        icon.margin.rightDp         = 20f;
//
//        // [3 B] Button
//        // -------------------------------------------------------------------------------------
//
//        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
//        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        label.textId                = labelId;
//
//        label.font                  = Font.serifFontRegular(context);
//        label.color                 = R.color.dark_blue_hlx_10;
//        label.sizeSp                = 17f;
//
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout dividerView(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.heightDp         = 1;
//
//        layout.backgroundColor  = R.color.dark_blue_7;

//        return layout.linearLayout(context);
//    }
//
//}
