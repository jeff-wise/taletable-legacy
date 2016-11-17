
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

import com.kispoko.tome.Global;
import com.kispoko.tome.R;
import com.kispoko.tome.activity.sheet.ChooseImageAction;
import com.kispoko.tome.activity.sheet.PagePagerAdapter;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.activity.sheet.PageFragment;
import com.kispoko.tome.DatabaseManager;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.util.Util;



/**
 * The sheet activity for the application.
 * All of the sheet ComponentUtil components are constructed and maintained here.
 */
public class SheetActivity
       extends AppCompatActivity
       implements PageFragment.EventListener
{

    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    // >> Requests
    public static final int CHOOSE_IMAGE_FROM_FILE = 0;
    public static final int COMPONENT_EDIT = 1;

    // ComponentUtil
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    // Data
    private Sheet sheet;

    private ChooseImageAction chooseImageAction;


    // > ACTIVITY EVENTS
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
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
            editResult.applyResult(this, this.sheet);
        }
    }



    // > FRAGMENT EVENTS
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


    // > API
    // -------------------------------------------------------------------------------------------

    /**
     * Set the activity's sheet. This is used primarily as a callback for asynchronous tasks
     * that fetch the sheet from database or network.
     * @param sheet The sheet.
     */
    public void setSheet(Sheet sheet)
    {
        this.sheet = sheet;
    }


    public void renderSheet()
    {
        PagePagerAdapter pagePagerAdapter =
                new PagePagerAdapter(getSupportFragmentManager(), this.sheet);

        ViewPager viewPager = (ViewPager) findViewById(R.id.page_pager);
        viewPager.setAdapter(pagePagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText( ((TextWidget) this.sheet.componentWithLabel("Name")).getValue());
    }


    public void saveSheet(boolean recursive)
    {
        this.sheet.save(this, recursive);
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



    // >> Data
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

        DatabaseManager databaseManager = new DatabaseManager(this);
        Global.setDatabase(databaseManager.getWritableDatabase());

        // This will be a new character sheet
        if (templateId != null) {
            Sheet.loadFromFile(this, templateId);
        }
        // Load the most recently used character sheet
        else {
            Sheet.loadMostRecent(this);
        }

    }

}
