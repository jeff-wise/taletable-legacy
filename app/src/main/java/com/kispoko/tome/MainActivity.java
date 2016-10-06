
package com.kispoko.tome;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.kispoko.tome.component.Component;
import com.kispoko.tome.fragment.roleplay.PageFragment;
import com.kispoko.tome.rules.RulesEngine;
import com.kispoko.tome.sheet.Sheet;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static com.kispoko.tome.EditResult.ResultType.TEXT_VALUE;


/**
 * The main activity for the application.
 * All of the main Util components are constructed and maintained here.
 */
public class MainActivity
       extends AppCompatActivity
       implements PageFragment.EventListener
{


    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    // >> Requests
    public static final int CHOOSE_IMAGE_FROM_FILE = 0;
    public static final int EDIT_COMPONENT = 1;

    // Util
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    // Data
    private Sheet sheet;
    private RulesEngine rulesEngine;

    private ChooseImageAction chooseImageAction;


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
        initializeTabs();
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


    // > INTERNAL
    // -------------------------------------------------------------------------------------------

    // >> User Interface
    // -------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar Util components.
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
     * Initialize the drawer Util components.
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
        try {
            InputStream yamlIS = this.getAssets().open("sheet/dnd_ed_5.yaml");
            Yaml yaml = new Yaml();
            Object yamlObject = yaml.load(yamlIS);
            this.sheet = Sheet.fromYaml((Map<String,Object>) yamlObject);
        } catch (IOException e) {
        }
    }

}
