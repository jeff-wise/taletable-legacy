
package com.kispoko.tome.activity;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.sheet.PagePagerAdapter;
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.activity.sheet.PageFragment;
import com.kispoko.tome.db.SheetDatabaseManager;
import com.kispoko.tome.rules.RulesEngine;
import com.kispoko.tome.sheet.Sheet;


/**
 * The main activity for the application.
 * All of the main ComponentUtil components are constructed and maintained here.
 */
public class SheetActivity
       extends AppCompatActivity
       implements PageFragment.EventListener
{

    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    // >> Requests
    public static final int CHOOSE_IMAGE_FROM_FILE = 0;
    public static final int EDIT_COMPONENT = 1;

    // ComponentUtil
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    // Data
    private Sheet sheet;
    private RulesEngine rulesEngine;

    private ChooseImageAction chooseImageAction;

    private SQLiteDatabase database;


    // > ACTIVITY EVENTS
    // -------------------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.rulesEngine = new RulesEngine();

        loadSheet();

        initializeToolbar();
        initializeDrawer();
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
        getMenuInflater().inflate(R.menu.main, menu);
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

        // Process Chosen Image
        if (requestCode == CHOOSE_IMAGE_FROM_FILE)
        {
            Uri uri = data.getData();
            this.chooseImageAction.setImage(uri);
            this.chooseImageAction = null;
        }

        // Update component with new values
        else if (requestCode == EDIT_COMPONENT)
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
    public void openEditActivity(Component component)
    {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("COMPONENT", component);
        startActivityForResult(intent, EDIT_COMPONENT);
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


    /**
     * Show Page View. Used as a callback so the pages can be rendered once they are actually
     * loaded.
     */
    public void showPageView()
    {

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
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    /**
     * Initialize the drawer ComponentUtil components.
     */
    private void initializeDrawer()
    {
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }


    /**
     * Initialize the tabs
     */
    private void initializeTabs()
    {
        PagePagerAdapter pagePagerAdapter =
                new PagePagerAdapter(getSupportFragmentManager(), this.sheet);

        ViewPager viewPager = (ViewPager) findViewById(R.id.page_pager);
        viewPager.setAdapter(pagePagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }


    // >> Data
    // -------------------------------------------------------------------------------------------

    /**
     * Load a sheet from a yaml file.
     */
    private void loadSheet()
    {

        // Get game id passed from previous activity
        Bundle extras = getIntent().getExtras();
        String templateId = extras.getString("FROM_TEMPLATE_ID");

        SheetDatabaseManager sheetDatabaseManager = new SheetDatabaseManager(this);
        this.database = sheetDatabaseManager.getWritableDatabase();

        // This will be a new character sheet
        if (templateId == null) {

        }
        // Load the most recently used character sheet
        else {
            Sheet.loadMostRecent(database, this);
        }

//        try {
//            InputStream yamlIS = this.getAssets().open("sheet/dnd_ed_5.yaml");
//            Yaml yaml = new Yaml();
//            Object yamlObject = yaml.load(yamlIS);
//            this.sheet = Sheet.fromYaml((Map<String,Object>) yamlObject);
//        } catch (IOException e) {
//        }
    }

}
