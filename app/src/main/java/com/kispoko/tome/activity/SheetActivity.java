
package com.kispoko.tome.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.sheet.ChooseImageAction;
import com.kispoko.tome.activity.sheet.PagePagerAdapter;
import com.kispoko.tome.rules.programming.variable.TextVariable;
import com.kispoko.tome.rules.programming.variable.VariableKind;
import com.kispoko.tome.sheet.Page;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.activity.sheet.PageFragment;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.util.Util;

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

    private static String characterName;

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
        String characterName = "Sheet";

        TextVariable nameVariable = sheet.getRulesEngine().getVariableIndex()
                                         .variableWithName("name").getText();
        if (!nameVariable.isNull() && nameVariable.getKind() == VariableKind.LITERAL) {
            characterName = nameVariable.getValue();
        }

        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(characterName);
    }


    // > INTERNAL
    // -------------------------------------------------------------------------------------------

    // >> User Interface
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
        navigationView.setItemIconTintList(null);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener()
        {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {
                //Checking if the item is in checked state or not, if not make it in checked state
//                if(menuItem.isChecked()) menuItem.setChecked(false);
//                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId())
                {
                    //Replacing the sheet content with ContentFragment Which is our Inbox View;
                    case R.id.nav_characters:
                        Intent intent = new Intent(SheetActivity.this, ManageSheetsActivity.class);
                        startActivity(intent);
                        return true;
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
        titleView.setText(SheetActivity.characterName);
    }


    // > Data
    // -------------------------------------------------------------------------------------------

    /**
     * Load a sheet from a yaml file.
     */
    private void loadSheet()
    {
        // Ensure the sheet manager is ready to be used
        SheetManager.initialize();

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

}
