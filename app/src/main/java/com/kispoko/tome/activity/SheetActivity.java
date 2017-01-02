
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.sheet.ChooseImageAction;
import com.kispoko.tome.activity.sheet.PagePagerAdapter;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.sheet.Page;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.activity.sheet.PageFragment;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
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

        setContentView(R.layout.activity_main);

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
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("COMPONENT", widgetData);
        startActivityForResult(intent, COMPONENT_EDIT);
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
        if (!nameVariable.isNull()) {
            this.characterName = nameVariable.value();
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
        titleView.setTypeface(Util.sansSerifFontBold(this));

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
                        // Switch to page three
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

        // > This will be a new character sheet
        if (templateId != null)
            SheetManager.goToTemplate(this, templateId, this);
        // > Load the most recently used character sheet
        else
            SheetManager.goToMostRecent(this, this);

    }


    // > Views
    // -------------------------------------------------------------------------------------------

    private LinearLayout navigationView()
    {
        LinearLayout layout = this.navigationLayout();

        layout.addView(this.navigationItemHeaderView("SHEET",
                                                     this.characterName,
                                                     R.id.nav_view_header_sheet_desc));
        layout.addView(this.navigationItemView("VALUES"));
        layout.addView(this.navigationItemView("FUNCTIONS"));
        layout.addView(this.navigationItemView("PROGRAMS"));
        layout.addView(this.navigationItemView("MECHANICS"));
        layout.addView(this.navigationItemHeaderView("SHEETS", null, null));
        layout.addView(this.navigationItemView("MANAGE"));

        return layout;
    }


    private LinearLayout navigationLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation  = LinearLayout.VERTICAL;
        layout.padding.top  = R.dimen.nav_view_padding_top;

        return layout.linearLayout(this);
    }


    private LinearLayout navigationItemHeaderView(String headerName, String description, Integer id)
    {
        // [1] Views
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout     = new LinearLayoutBuilder();
        TextViewBuilder     headerView = new TextViewBuilder();
        TextViewBuilder     descView   = new TextViewBuilder();


        // [2 A] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation        = LinearLayout.VERTICAL;
        layout.width              = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height             = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.padding.top        = R.dimen.nav_view_header_padding_vert;
        layout.padding.bottom     = R.dimen.nav_view_header_padding_vert;
        layout.backgroundColor    = R.color.dark_blue_8;

        layout.child(headerView)
              .child(descView);

        // [2 B] Header Text
        // --------------------------------------------------------------------------------------

        headerView.width              = LinearLayout.LayoutParams.MATCH_PARENT;
        headerView.height             = LinearLayout.LayoutParams.WRAP_CONTENT;
        headerView.text               = headerName;
        headerView.font               = Font.sansSerifFontBold(this);

        headerView.padding.left       = R.dimen.nav_view_header_padding_left;
        headerView.size               = R.dimen.nav_view_header_text_size;
        headerView.color              = R.color.light_grey_9;

        // [2 C] Description
        // --------------------------------------------------------------------------------------

        descView.id                 = id;
        descView.width              = LinearLayout.LayoutParams.MATCH_PARENT;
        descView.height             = LinearLayout.LayoutParams.WRAP_CONTENT;
        descView.padding.left       = R.dimen.nav_view_header_padding_left;
        descView.padding.top        = R.dimen.nav_view_header_desc_padding_top;
        descView.font               = Font.sansSerifFontBold(this);
        descView.size               = R.dimen.nav_view_header_desc_text_size;
        descView.color              = R.color.gold_5;
        descView.text               = description;
        descView.visibility         = ViewPager.GONE;

        return layout.linearLayout(this);
    }


    private TextView navigationItemView(String itemName)
    {
        TextViewBuilder itemView = new TextViewBuilder();

        itemView.width              = LinearLayout.LayoutParams.MATCH_PARENT;
        itemView.height             = LinearLayout.LayoutParams.WRAP_CONTENT;
        itemView.text               = itemName;
        itemView.font               = Font.sansSerifFontBold(this);
        itemView.padding.top        = R.dimen.nav_view_item_padding_vert;
        itemView.padding.bottom     = R.dimen.nav_view_item_padding_vert;

        itemView.backgroundColor    = R.color.dark_blue_6;
        itemView.padding.left       = R.dimen.nav_view_item_padding_left;
        itemView.size               = R.dimen.nav_view_item_text_size;
        itemView.color              = R.color.grey_4;

        return itemView.textView(this);
    }


}
