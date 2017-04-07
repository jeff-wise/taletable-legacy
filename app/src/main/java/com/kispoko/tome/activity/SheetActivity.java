
package com.kispoko.tome.activity;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.sheet.ChooseImageAction;
import com.kispoko.tome.activity.sheet.PagePagerAdapter;
import com.kispoko.tome.campaign.CampaignIndex;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.sheet.Page;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.TableWidget;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.ScrollViewBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.sheet.widget.WidgetType;
import com.kispoko.tome.sheet.widget.WidgetUnion;
import com.kispoko.tome.sheet.widget.table.cell.CellType;
import com.kispoko.tome.sheet.widget.table.cell.CellUnion;
import com.kispoko.tome.sheet.widget.table.cell.TextCell;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;



/**
 * The sheet activity for the application.
 * All of the sheet ComponentUtil components are constructed and maintained here.
 */
public class SheetActivity
       extends AppCompatActivity
       implements Sheet.OnSheetListener
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    // > Requests
    public static final int CHOOSE_IMAGE_FROM_FILE = 0;

    // ComponentUtil
    private Toolbar             toolbar;

    private DrawerLayout        drawerLayout;

    private ChooseImageAction   chooseImageAction;

    private String              characterName;

    private TextView            sheetNavCharacterNameView;

    private PagePagerAdapter    pagePagerAdapter;


    public static ViewPager viewPager;


    // ACTIVITY EVENTS
    // -------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sheet);

        loadSheet();

        initializeToolbar();
        initializeDrawers();
        initializeNavigation();
        initializeBottomNavigation();
        prepareSheetViews();
    }


    @Override
    public void onStart()
    {
        super.onStart();
        EventBus.getDefault().register(this);
    }


    @Override
    public void onStop()
    {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.empty, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO clearn this up. maybe delete
        // Skip Errors
        if (resultCode != RESULT_OK) return;

        // Process Chosen ImageWidget
        if (requestCode == CHOOSE_IMAGE_FROM_FILE)
        {
            Uri uri = data.getData();
            this.chooseImageAction.setImage(this, uri);
            this.chooseImageAction = null;
        }

    }


    // FRAGMENT EVENTS
    // -------------------------------------------------------------------------------------------

    /**
     *
     */
    public void setChooseImageAction(ChooseImageAction chooseImageAction)
    {
        this.chooseImageAction = chooseImageAction;
    }


    // API
    // -------------------------------------------------------------------------------------------

    public void onSheet(Sheet sheet)
    {
        // Render the sheet
        sheet.render(this.pagePagerAdapter);

        // Set the title to the character's name, if available
        this.characterName = "Sheet";

        TextVariable nameVariable = State.variableWithName("name").textVariable();
        if (!nameVariable.isNull())
        {
            try {
                this.characterName = nameVariable.value();
            }
            catch (NullVariableException exception) {
                this.characterName = "N/A";
            }
        }

        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(this.characterName);

        if (this.sheetNavCharacterNameView != null) {
            this.sheetNavCharacterNameView.setText(this.characterName);
        }

    }


    // > Events
    //   (Event Bus Subscriptions)
    // -------------------------------------------------------------------------------------------

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTextWidgetUpdateLiteralEvent(TextWidget.UpdateLiteralEvent event)
    {
        WidgetUnion widgetUnion = SheetManager.currentSheet().widgetWithId(event.widgetId());

        if (widgetUnion != null && widgetUnion.type() == WidgetType.TEXT)
        {
            TextWidget textWidget = widgetUnion.textWidget();
            textWidget.setLiteralValue(event.newValue(), this);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTextCellUpdateLiteralEvent(TextCell.UpdateLiteralEvent event)
    {
        WidgetUnion widgetUnion = SheetManager.currentSheet().widgetWithId(event.tableWidgetId());

        if (widgetUnion != null && widgetUnion.type() == WidgetType.TABLE)
        {
            TableWidget tableWidget = widgetUnion.tableWidget();
            CellUnion cellUnion = tableWidget.cellWithId(event.cellId());

            if (cellUnion != null && cellUnion.type() == CellType.TEXT)
            {
                TextCell textCell = cellUnion.textCell();
                textCell.setLiteralValue(event.newValue(), this);
            }
        }
    }


    // INTERNAL
    // -------------------------------------------------------------------------------------------

    // > Initialization Methods
    // -------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar ComponentUtil components.
     */
    private void initializeToolbar()
    {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);

        TextView titleView = (TextView) this.toolbar.findViewById(R.id.page_title);
        titleView.setTypeface(Font.serifFontRegular(this));
    }


    /**
     * Initialize the drawer ComponentUtil components.
     */
    private void initializeDrawers()
    {
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ImageButton menuLeft = (ImageButton) findViewById(R.id.menuLeft);
        ImageButton menuRight = (ImageButton) findViewById(R.id.menuRight);

        menuLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        menuRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });

    }


    /**
     * Initialize the navigation menu.
     */
    private void initializeNavigation()
    {
        // App Navigation View
        NavigationView appNavigationView = (NavigationView) findViewById(R.id.app_navigation_view);
        appNavigationView.addView(this.appNavigationView(this));

        // Sheet Navigation View
        NavigationView sheetNavigationView =
                                    (NavigationView) findViewById(R.id.sheet_navigation_view);
        sheetNavigationView.addView(this.sheetNavigationView(this));
    }


    /**
     * Initialize the bottom navigation menu.
     */
    private void initializeBottomNavigation()
    {
        BottomNavigationView bottomNavigationView =
                (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                // TODO make sure sheet is loaded
                switch (item.getItemId())
                {
                    case R.id.button_section_profile:
                        SheetManager.currentSheet().profileSection().render(pagePagerAdapter);
                        break;
                    case R.id.button_section_encounter:
                        SheetManager.currentSheet().encounterSection().render(pagePagerAdapter);
                        break;
                    case R.id.button_section_campaign:
                        SheetManager.currentSheet().campaignSection().render(pagePagerAdapter);
                        break;
                }
                return true;
            }
        });
    }


    /**
     * Setup the main sheet activity views. The PagePagerAdapter controls the left-right-swiping
     * between different character sheet pages.. It is connected to a tab layout, so that users
     * may select the character sheet pages by name.
     */
    private void prepareSheetViews()
    {
        PagePagerAdapter pagePagerAdapter =
                new PagePagerAdapter(getSupportFragmentManager(), new ArrayList<Page>());
        this.pagePagerAdapter = pagePagerAdapter;

        ViewPager viewPager = (ViewPager) findViewById(R.id.page_pager);
        viewPager.setAdapter(pagePagerAdapter);
        SheetActivity.viewPager = viewPager;

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(this.characterName);
    }


    // > Data
    // -------------------------------------------------------------------------------------------

    /**
     * Load a sheet from a yaml file.
     */
    private void loadSheet()
    {
        // If previous activity was template chooser, get id of chosen template
        String templateId = null;
        if (getIntent().hasExtra("TEMPLATE_ID"))
            templateId = getIntent().getStringExtra("TEMPLATE_ID");

        CampaignIndex.initialize(this.getApplicationContext());

        // > This will be a new character sheet
        if (templateId != null)
            SheetManager.goToTemplate(this, templateId, this);
        // > Load the most recently used character sheet
        else
            SheetManager.goToMostRecent(this, this);
    }


    // > Views
    // -------------------------------------------------------------------------------------------

    // ** APP Navigation View
    // -----------------------------------------------------------------------------------------

    private ScrollView appNavigationView(Context context)
    {
        ScrollView scrollView = this.navigationScrollView(context);

        LinearLayout layout = this.appNavigationLayout(context);

        // > Account
        layout.addView(this.accountView(context));

        layout.addView(this.dividerView(context));

        // > Sheet Buttons
        layout.addView(this.sheetButtonsView(context));

        layout.addView(this.dividerView(context));

        // > App Buttons
        layout.addView(this.appButtonsView(context));

        scrollView.addView(layout);

        return scrollView;
    }


    private LinearLayout appNavigationLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.padding.top      = R.dimen.nav_view_padding_top;

        layout.backgroundColor  = R.color.dark_blue_11;

        return layout.linearLayout(context);
    }


    private LinearLayout accountView(Context context)
    {
        LinearLayout layout = this.accountViewLayout(context);

        // > User
        layout.addView(this.userView(context));

        // > Controls
        layout.addView(this.accountControlsView(context));

        return layout;
    }


    private LinearLayout accountViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.leftDp   = 10f;
        layout.padding.rightDp  = 10f;
        layout.padding.topDp    = 10f;
        layout.padding.bottomDp = 20f;

        return layout.linearLayout(context);
    }



    private LinearLayout userView(Context context)
    {
        LinearLayout layout = this.userViewLayout(context);

        // > Account Picture
        layout.addView(accountIconView(context));

        // > Account Name
        layout.addView(accountNameView(context));

        return layout;
    }


    private ImageView accountIconView(Context context)
    {
        ImageViewBuilder icon = new ImageViewBuilder();

        icon.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image          = R.drawable.ic_app_nav_account;

        icon.color          = R.color.dark_blue_hl_2;

        return icon.imageView(context);
    }


    private TextView accountNameView(Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.text               = "Jeff Wise";

        name.font               = Font.serifFontRegular(context);
        name.color              = R.color.gold_light;
        name.sizeSp             = 17f;

        name.margin.leftDp      = 10f;

        return name.textView(context);
    }


    private LinearLayout userViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.HORIZONTAL;

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity          = Gravity.CENTER_VERTICAL;

        return layout.linearLayout(context);
    }


    private LinearLayout accountControlsView(Context context)
    {
        LinearLayout layout = this.accountControlsViewLayout(context);

        layout.addView(this.createAccountButtonView(context));

        return layout;
    }


    private LinearLayout accountControlsViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.HORIZONTAL;

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity          = Gravity.CENTER_HORIZONTAL;

        layout.margin.topDp     = 20f;

        return layout.linearLayout(context);
    }


    private LinearLayout createAccountButtonView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Declarations
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = R.drawable.ic_app_nav_create_account;

        icon.color                  = R.color.dark_blue_hl_8;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.textId                = R.string.create_account;

        label.font                  = Font.serifFontRegular(context);
        label.color                 = R.color.dark_blue_hl_8;
        label.sizeSp                = 13f;


        return layout.linearLayout(context);
    }


    private LinearLayout sheetButtonsView(Context context)
    {
        LinearLayout layout = this.buttonsLayout(context);

        // > My Sheets
        // -------------------------------------------------------------------------------------
        LinearLayout mySheetsButton = this.buttonView(R.string.my_sheets,
                                                      R.drawable.ic_app_nav_my_sheets,
                                                      context);
        layout.addView(mySheetsButton);

        // > New Sheet
        // -------------------------------------------------------------------------------------
        LinearLayout newSheetButton = this.buttonView(R.string.new_sheet,
                                                      R.drawable.ic_app_nav_new_sheet,
                                                      context);
        layout.addView(newSheetButton);

        return layout;
    }


    private LinearLayout appButtonsView(Context context)
    {
        LinearLayout layout = this.buttonsLayout(context);

        // > Tutorials
        // -------------------------------------------------------------------------------------
        LinearLayout tutorialsButton = this.buttonView(R.string.tutorials,
                                                       R.drawable.ic_app_nav_help,
                                                       context);
        layout.addView(tutorialsButton);

        // > Settings
        // -------------------------------------------------------------------------------------
        LinearLayout settingsButton = this.buttonView(R.string.settings,
                                                      R.drawable.ic_app_nav_settings,
                                                      context);
        layout.addView(settingsButton);

        // > Feedback
        // -------------------------------------------------------------------------------------
        LinearLayout feedbackButton = this.buttonView(R.string.give_us_your_feedback,
                R.drawable.ic_app_nav_feedback,
                context);
        layout.addView(feedbackButton);

        // > Upgrades
        // -------------------------------------------------------------------------------------
        LinearLayout upgradesButton = this.buttonView(R.string.upgrades,
                                                      R.drawable.ic_app_nav_upgrades,
                                                      context);
        layout.addView(upgradesButton);

        // > News
        // -------------------------------------------------------------------------------------
        LinearLayout newsButton = this.buttonView(R.string.news_slash_updates,
                                                  R.drawable.ic_app_nav_news,
                                                  context);
        layout.addView(newsButton);

        // > About This App
        // -------------------------------------------------------------------------------------
        LinearLayout aboutButton = this.buttonView(R.string.about_this_app,
                                                   R.drawable.ic_app_nav_about_us,
                                                   context);
        layout.addView(aboutButton);

        return layout;
    }


    // ** SHEET Navigation View
    // -----------------------------------------------------------------------------------------

    private ScrollView sheetNavigationView(Context context)
    {
        ScrollView scrollView = this.navigationScrollView(context);

        LinearLayout layout = this.sheetNavigationLayout(context);

        // > Character View
        layout.addView(this.characterView(context));

        // --- Divider
        layout.addView(this.dividerView(context));

        // > Programming Options
        layout.addView(this.programmingButtonsView(context));

        // --- Divider
        layout.addView(this.dividerView(context));

        // > Style Options
        layout.addView(this.styleButtonsView(context));

        scrollView.addView(layout);

        return scrollView;
    }


    private LinearLayout sheetNavigationLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.padding.top      = R.dimen.nav_view_padding_top;

        return layout.linearLayout(context);
    }


    private LinearLayout characterView(Context context)
    {
        LinearLayout layout = this.characterViewLayout(context);

        // > Avatar
        layout.addView(characterAvatarView(context));

        // > Name
        this.sheetNavCharacterNameView = this.characterNameView(context);
        layout.addView(this.sheetNavCharacterNameView);

        // > Description
        layout.addView(characterDescriptionView(context));

        return layout;
    }


    private LinearLayout characterViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER_HORIZONTAL;

        layout.padding.topDp        = 15f;
        layout.padding.bottomDp     = 20f;

        return layout.linearLayout(context);
    }


    private ImageView characterAvatarView(Context context)
    {
        ImageViewBuilder avatar = new ImageViewBuilder();

        avatar.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        avatar.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        avatar.image                = R.drawable.ic_sheet_menu_default_avatar;

        avatar.color                = R.color.dark_blue_hl_2;

        return avatar.imageView(context);
    }


    private TextView characterNameView(Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.text               = this.characterName;

        name.font               = Font.serifFontRegular(context);
        name.color              = R.color.gold_light;
        name.sizeSp             = 24f;

        name.margin.topDp       = 8f;

        return name.textView(context);
    }


    private TextView characterDescriptionView(Context context)
    {
        TextViewBuilder description = new TextViewBuilder();

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT;

        description.text            = "Level 1 Human Fighter";

        description.font            = Font.serifFontRegular(context);
        description.color           = R.color.dark_blue_hl_6;
        description.sizeSp          = 14f;

        description.margin.topDp    = 8f;

        return description.textView(context);
    }


    private LinearLayout programmingButtonsView(Context context)
    {
        LinearLayout layout = this.buttonsLayout(context);

        // > Dictionary
        // -------------------------------------------------------------------------------------

        LinearLayout dictionaryButton = this.buttonView(R.string.dictionary,
                                                        R.drawable.ic_sheet_nav_dictionary,
                                                        context);

        dictionaryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SheetActivity.this, DictionaryActivity.class);
                startActivity(intent);
            }
        });

        layout.addView(dictionaryButton);

        // > Functions
        // -------------------------------------------------------------------------------------
        LinearLayout functionsButton = this.buttonView(R.string.functions,
                                                      R.drawable.ic_sheet_nav_functions,
                                                      context);
        functionsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SheetActivity.this, FunctionIndexActivity.class);
                startActivity(intent);
            }
        });
        layout.addView(functionsButton);

        // > Programs
        // -------------------------------------------------------------------------------------
        LinearLayout programsButton = this.buttonView(R.string.programs,
                                                      R.drawable.ic_sheet_nav_programs,
                                                      context);
        programsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SheetActivity.this, ProgramIndexActivity.class);
                startActivity(intent);
            }
        });
        layout.addView(programsButton);

        // > Mechanics
        // -------------------------------------------------------------------------------------
        LinearLayout mechanicsButton = this.buttonView(R.string.mechanics,
                                                       R.drawable.ic_sheet_nav_mechanics,
                                                       context);
        mechanicsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SheetActivity.this, MechanicIndexActivity.class);
                startActivity(intent);
            }
        });
        layout.addView(mechanicsButton);

        // > Debugger
        // -------------------------------------------------------------------------------------
        LinearLayout debuggerButton = this.buttonView(R.string.debugger,
                                                      R.drawable.ic_sheet_nav_debugger,
                                                      context);
        layout.addView(debuggerButton);

        return layout;
    }


    private LinearLayout styleButtonsView(Context context)
    {
        LinearLayout layout = this.buttonsLayout(context);

        // > Layout
        LinearLayout layoutButton = this.buttonView(R.string.layout,
                                                    R.drawable.ic_sheet_nav_layout,
                                                    context);
        layout.addView(layoutButton);

        // > Theme
        LinearLayout themeButton = this.buttonView(R.string.theme,
                                                   R.drawable.ic_sheet_nav_theme,
                                                   context);
        layout.addView(themeButton);

        return layout;
    }


    // ** SHARED views & layouts
    // -----------------------------------------------------------------------------------------

    private ScrollView navigationScrollView(Context context)
    {
        ScrollViewBuilder scrollView = new ScrollViewBuilder();

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT;
        scrollView.height       = LinearLayout.LayoutParams.MATCH_PARENT;

        return scrollView.scrollView(context);
    }


    private LinearLayout buttonsLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.padding.leftDp       = 15f;
        layout.padding.topDp        = 10f;
        layout.padding.bottomDp     = 10f;

        return layout.linearLayout(context);
    }


    private LinearLayout buttonView(int labelId, int iconId, Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.padding.topDp        = 15f;
        layout.padding.bottomDp     = 15f;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = iconId;

        icon.color                  = R.color.dark_blue_hl_2;

        icon.margin.rightDp         = 20f;

        // [3 B] Button
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.textId                = labelId;

        label.font                  = Font.serifFontRegular(context);
        label.color                 = R.color.dark_blue_hlx_10;
        label.sizeSp                = 17f;


        return layout.linearLayout(context);
    }


    private LinearLayout dividerView(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.heightDp         = 1;

        layout.backgroundColor  = R.color.dark_blue_7;

        return layout.linearLayout(context);
    }

}
