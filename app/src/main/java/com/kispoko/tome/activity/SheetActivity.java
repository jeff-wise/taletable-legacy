
package com.kispoko.tome.activity;


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
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.activity.sheet.PageFragment;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.ScrollViewBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;

import java.util.ArrayList;



/**
 * The sheet activity for the application.
 * All of the sheet ComponentUtil components are constructed and maintained here.
 */
public class SheetActivity
       extends AppCompatActivity
       implements PageFragment.EventListener,
                  Sheet.OnSheetListener
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    // > Requests
    public static final int CHOOSE_IMAGE_FROM_FILE = 0;
    public static final int COMPONENT_EDIT = 1;

    // ComponentUtil
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private ChooseImageAction chooseImageAction;

    private String characterName;

    private PagePagerAdapter pagePagerAdapter;


    // ACTIVITY EVENTS
    // -------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sheet);

        loadSheet();

        initializeDrawer();
        initializeToolbar();
        initializeNavigation();
        initializeBottomNavigation();
        prepareSheetViews();
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
        getMenuInflater().inflate(R.menu.sheet, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Skip Errors // TODO
        if (resultCode != RESULT_OK) return;

        // Process Chosen ImageWidget
        if (requestCode == CHOOSE_IMAGE_FROM_FILE)
        {
            Uri uri = data.getData();
            this.chooseImageAction.setImage(this, uri);
            this.chooseImageAction = null;
        }

        // Update component with new values
        else if (requestCode == COMPONENT_EDIT)
        {
            EditResult editResult = (EditResult) data.getExtras().getSerializable("RESULT");
            editResult.applyResult(this, SheetManager.currentSheet());
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


    /**
     * Open the Edit activity.
     */
    public void openEditActivity(WidgetData widgetData)
    {
//        Intent intent = new Intent(this, EditActivity.class);
//        intent.putExtra("COMPONENT", widgetData);
//        startActivityForResult(intent, COMPONENT_EDIT);
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

        TextView navHeaderDesc = (TextView) findViewById(R.id.nav_view_header_sheet_desc);
        if (navHeaderDesc != null) {
            navHeaderDesc.setText(this.characterName);
            navHeaderDesc.setVisibility(View.VISIBLE);
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
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);

        TextView titleView = (TextView) this.toolbar.findViewById(R.id.page_title);
        titleView.setTypeface(Util.serifFontBold(this));

        //setSupportActionBar(this.toolbar);
    }


    /**
     * Initialize the drawer ComponentUtil components.
     */
    private void initializeDrawer()
    {
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }


    /**
     * Initialize the navigation menu.
     */
    private void initializeNavigation()
    {
        //Initializing NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.addView(this.navigationView());
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

    private ScrollView navigationView()
    {
        ScrollView scrollView = this.navigationScrollView();

        LinearLayout layout = this.navigationLayout();

        // > Top Button Row
        layout.addView(topButtonsRowView());

        // > Divider
        layout.addView(UI.divider(this, R.color.dark_blue_10, 1));
        layout.addView(UI.divider(this, R.color.dark_blue_7, 1));

        // > Sheet Options
        layout.addView(sheetOptionsView());

        // > Manage Sheets Button
        layout.addView(manageSheetsButton());

        // > Tools
        layout.addView(toolsView());

        // > Divider
        layout.addView(UI.divider(this, R.color.dark_blue_10, 1));
        layout.addView(UI.divider(this, R.color.dark_blue_7, 1));

        // > Bottom Button Row
        layout.addView(bottomButtonsRowView());

        scrollView.addView(layout);

        return scrollView;
    }


    private ScrollView navigationScrollView()
    {
        ScrollViewBuilder scrollView = new ScrollViewBuilder();

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT;
        scrollView.height       = LinearLayout.LayoutParams.MATCH_PARENT;

        return scrollView.scrollView(this);
    }


    private LinearLayout navigationLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.padding.top      = R.dimen.nav_view_padding_top;

        return layout.linearLayout(this);
    }


    private LinearLayout topButtonsRowView()
    {
        LinearLayout layout = buttonRowView();

        layout.addView(iconButton(R.string.nav_view_button_feedback,
                                  R.drawable.ic_nav_feedback));
        layout.addView(iconButton(R.string.nav_view_button_help,
                                  R.drawable.ic_nav_help));
        layout.addView(iconButton(R.string.nav_view_button_settings,
                                  R.drawable.ic_nav_settings));

        return layout;
    }


    private LinearLayout bottomButtonsRowView()
    {
        LinearLayout layout = buttonRowView();

        layout.addView(iconButton(R.string.nav_view_button_about_us,
                                  R.drawable.ic_nav_about_us));
        layout.addView(iconButton(R.string.nav_view_button_upgrades,
                                  R.drawable.ic_nav_upgrades));
        layout.addView(iconButton(R.string.nav_view_button_news,
                                  R.drawable.ic_nav_news));

        return layout;
    }


    private LinearLayout buttonRowView()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.top          = R.dimen.nav_view_buttons_row_layout_padding_vert;
        layout.padding.bottom       = R.dimen.nav_view_buttons_row_layout_padding_vert;

        return layout.linearLayout(this);
    }


    private LinearLayout iconButton(int labelId, int iconDrawableId)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     text   = new TextViewBuilder();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = 0;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.weight               = 1.0f;
        layout.gravity              = Gravity.CENTER_HORIZONTAL;

        layout.child(icon)
              .child(text);

        // [3 A] Icon
        // --------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = iconDrawableId;

        icon.margin.bottom          = R.dimen.nav_view_icon_button_icon_margin_bottom;

        // [3 B] Text
        // --------------------------------------------------------------------------------------

        text.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        text.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        text.textId                 = labelId;
        text.color                  = R.color.dark_blue_hl_6;
        text.size                   = R.dimen.nav_view_icon_button_text_size;
        text.font                   = Font.sansSerifFontRegular(this);

        return layout.linearLayout(this);
    }


    private LinearLayout sheetOptionsView()
    {
        LinearLayout layout = sheetOptionsLayout();

        // > Header
        // --------------------------------------------------------------------------------------

        layout.addView(sheetOptionsHeaderView());

        // > Buttons
        // --------------------------------------------------------------------------------------

        LinearLayout buttonsLayout = sheetOptionsButtonsLayout();

        // > Layout Button
        View.OnClickListener onLayoutClick = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SheetActivity.this, SectionsActivity.class);
                startActivity(intent);
            }
        };
        buttonsLayout.addView(sheetOptionsButton(R.string.nav_view_button_layout_label,
                                                 R.string.nav_view_button_layout_description,
                                                 onLayoutClick));

        // > Dictionary Button
        View.OnClickListener onDictionaryClick = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SheetActivity.this, DictionaryActivity.class);
                startActivity(intent);
            }
        };
        buttonsLayout.addView(sheetOptionsButton(R.string.nav_view_button_dictionary_label,
                                                 R.string.nav_view_button_dictionary_description,
                                                 onDictionaryClick));

        // > Functions Button
        View.OnClickListener onFunctionsClick = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SheetActivity.this, FunctionIndexActivity.class);
                startActivity(intent);
            }
        };
        buttonsLayout.addView(sheetOptionsButton(R.string.nav_view_button_functions_label,
                                                 R.string.nav_view_button_functions_description,
                                                 onFunctionsClick));

        // > Programs Button
        View.OnClickListener onProgramsClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SheetActivity.this, ProgramIndexActivity.class);
                startActivity(intent);
            }
        };
        buttonsLayout.addView(sheetOptionsButton(R.string.nav_view_button_programs_label,
                                                 R.string.nav_view_button_programs_description,
                                                 onProgramsClick));

        // > Mechanics Button
        View.OnClickListener onMechanicsClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SheetActivity.this, MechanicIndexActivity.class);
                startActivity(intent);
            }
        };
        buttonsLayout.addView(sheetOptionsButton(R.string.nav_view_button_mechanics_label,
                                                 R.string.nav_view_button_mechanics_description,
                                                 onMechanicsClick));

        layout.addView(buttonsLayout);

        return layout;
    }


    private LinearLayout sheetOptionsLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.top          = R.dimen.nav_view_sheet_options_padding_top;
        layout.padding.left         = R.dimen.nav_view_sheet_options_padding_horz;
        layout.padding.right        = R.dimen.nav_view_sheet_options_padding_horz;

        return layout.linearLayout(this);
    }


    private LinearLayout sheetOptionsButtonsLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(this);
    }


    private LinearLayout sheetOptionsButton(Integer labelStringId,
                                            Integer descriptionStringId,
                                            View.OnClickListener onClickListener)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout          = new LinearLayoutBuilder();
        TextViewBuilder     label           = new TextViewBuilder();
        TextViewBuilder     description     = new TextViewBuilder();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation              = LinearLayout.VERTICAL;
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundResource       = R.drawable.bg_nav_button;
        layout.onClick                  = onClickListener;

        layout.margin.bottom            = R.dimen.nav_view_sheet_options_button_margin_bottom;

        layout.child(label)
              .child(description);

        // [3 A] Label
        // --------------------------------------------------------------------------------------

        label.width                     = LinearLayout.LayoutParams.MATCH_PARENT;
        label.height                    = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.gravity                   = Gravity.CENTER_HORIZONTAL;
        //label.weight                    = 1.0f;

        label.textId                    = labelStringId;
        label.font                      = Font.sansSerifFontBold(this);
        label.size                      = R.dimen.nav_view_sheet_options_button_label_text_size;
        label.color                     = R.color.dark_blue_hlx_7;

        label.margin.bottom             = R.dimen.nav_view_sheet_options_button_label_margin_bottom;

        // [3 B] Description
        // --------------------------------------------------------------------------------------

        description.width           = LinearLayout.LayoutParams.MATCH_PARENT;
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT;
        //description.weight              = 3.0f;

        description.textId          = descriptionStringId;
        description.font            = Font.sansSerifFontRegular(this);
        description.size            = R.dimen.nav_view_sheet_options_button_description_text_size;
        description.color           = R.color.dark_blue_hl_8;

        return layout.linearLayout(this);
    }


    private LinearLayout sheetOptionsHeaderView()
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.bottom        = R.dimen.nav_view_sheet_options_header_margin_bottom;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // --------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        // icon.image                  = R.drawable.ic_nav_sheet;

        icon.padding.right          = R.dimen.nav_view_sheet_options_header_icon_margin_right;

        // [3 B] Label
        // --------------------------------------------------------------------------------------

        label.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.textId               = R.string.nav_view_header_sheet;
        label.color                = R.color.gold_light;
        label.size                 = R.dimen.nav_view_sheet_options_header_text_size;
        label.font                 = Font.sansSerifFontRegular(this);

        return layout.linearLayout(this);
    }


    private LinearLayout manageSheetsButton()
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER;

        layout.backgroundColor      = R.color.dark_blue_6;

        layout.padding.top          = R.dimen.nav_view_manage_sheets_button_padding_vert;
        layout.padding.bottom       = R.dimen.nav_view_manage_sheets_button_padding_vert;
        layout.margin.top           = R.dimen.nav_view_manage_sheets_button_margin_top;

        layout.onClick              = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SheetActivity.this, ManageSheetsActivity.class);
                startActivity(intent);
            }
        };

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // --------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        // icon.image                  = R.drawable.ic_nav_manage_sheets;

        icon.margin.right           = R.dimen.nav_view_manage_sheets_button_icon_margin_right;

        // [3 A] Label
        // --------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.textId                = R.string.button_manage_sheets;
        label.size                  = R.dimen.nav_view_manage_sheets_button_text_size;
        label.font                  = Font.sansSerifFontRegular(this);
        label.color                 = R.color.gold_light;


        return layout.linearLayout(this);
    }


    private LinearLayout toolsView()
    {
        LinearLayout layout = toolsLayout();

        // > Header
        layout.addView(toolsHeaderView());

        // > Buttons
        LinearLayout buttonsLayout = this.toolsButtonsLayout();

        LinearLayout row1 = this.toolsButtonsRowView();
        row1.addView(toolsButton(R.string.nav_view_tools_button_calculator,
                                 R.drawable.ic_nav_calculator));
        row1.addView(toolsButton(R.string.nav_view_tools_button_name_generator,
                                 R.drawable.ic_nav_name_generator));

        buttonsLayout.addView(row1);

        layout.addView(buttonsLayout);

        return layout;
    }


    private LinearLayout toolsLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.top           = R.dimen.nav_view_tools_margin_top;
        layout.margin.bottom        = R.dimen.nav_view_tools_margin_bottom;

        return layout.linearLayout(this);
    }


    private LinearLayout toolsHeaderView()
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.margin.bottom        = R.dimen.nav_view_tools_header_margin_bottom;
        layout.padding.left         = R.dimen.nav_view_tools_header_padding_horz;
        layout.padding.right        = R.dimen.nav_view_tools_header_padding_horz;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // --------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        // icon.image                  = R.drawable.ic_nav_tools;

        icon.margin.right           = R.dimen.nav_view_tools_header_icon_margin_right;

        // [3 B] Label
        // --------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.textId                = R.string.nav_view_header_tools;
        label.size                  = R.dimen.nav_view_tools_header_text_size;
        label.color                 = R.color.gold_light;
        label.font                  = Font.sansSerifFontRegular(this);

        return layout.linearLayout(this);
    }


    private LinearLayout toolsButtonsLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left         = R.dimen.nav_view_tools_buttons_layout_padding_horz;
        layout.padding.right        = R.dimen.nav_view_tools_buttons_layout_padding_horz;

        return layout.linearLayout(this);
    }


    private LinearLayout toolsButtonsRowView()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(this);
    }


    private LinearLayout toolsButton(int labelId, int iconId)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = 0;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.weight               = 1.0f;
        layout.gravity              = Gravity.CENTER;

        layout.backgroundResource   = R.drawable.bg_nav_button;

        layout.margin.left          = R.dimen.nav_view_tools_button_margin_horz;
        layout.margin.right         = R.dimen.nav_view_tools_button_margin_horz;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // --------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = iconId;

        icon.margin.bottom          = R.dimen.nav_view_tools_button_icon_margin_bottom;

        // [3 B] Label
        // --------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.gravity               = Gravity.CENTER;

        label.textId                = labelId;
        label.color                 = R.color.dark_blue_hl_3;
        label.font                  = Font.sansSerifFontRegular(this);
        label.size                  = R.dimen.nav_view_tools_button_text_size;

        return layout.linearLayout(this);
    }




}
