
package com.kispoko.tome;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.kispoko.tome.fragment.roleplay.AbilitiesFragment;
import com.kispoko.tome.fragment.roleplay.BackpackFragment;
import com.kispoko.tome.fragment.roleplay.ProfileFragment;
import com.kispoko.tome.fragment.roleplay.SpellbookFragment;
import com.kispoko.tome.fragment.roleplay.StatsFragment;


/**
 * The main activity for the application.
 * All of the main UI components are constructed and maintained here.
 */
public class MainActivity
       extends AppCompatActivity
       implements NavigationView.OnNavigationItemSelectedListener,
                  ProfileFragment.EventListener,
                  StatsFragment.EventListener,
                  AbilitiesFragment.EventListener,
                  BackpackFragment.EventListener,
                  SpellbookFragment.EventListener
{

    private Toolbar toolbar;
    private AHBottomNavigation bottomNavigation;


    // > ACTIVITY EVENTS
    // -------------------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeToolbar();
        initializeDrawer();
        initializeBottomNavigation();
        initializePager();
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // > FRAGMENT EVENTS
    // -------------------------------------------------------------------------------------------


    /**
     *
     */
    public void onProfileSelected()
    {

    }

    /**
     *
     */
    public void onStatsSelected()
    {

    }

    /**
     *
     */
    public void onAbilitiesSelected()
    {

    }

    /**
     *
     */
    public void onBackpackSelected()
    {

    }


    /**
     *
     */
    public void onSpellbookSelected()
    {

    }


    // > INTERNAL
    // -------------------------------------------------------------------------------------------


    /**
     * Initialize the toolbar UI components.
     */
    private void initializeToolbar()
    {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
    }


    /**
     * Initialize the drawer UI components.
     */
    private void initializeDrawer()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, this.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    /**
     * Initialize the bottom navigation UI components.
     */
    private void initializeBottomNavigation()
    {
        this.bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        // (1) Create the tabs
        // --------------------------------------------------------------------------------------
        AHBottomNavigationItem profileTab   = new AHBottomNavigationItem(
                                                    R.string.profile_tab,
                                                    R.drawable.ic_profile_24dp,
                                                    R.color.theme_primary );

        AHBottomNavigationItem statsTab     = new AHBottomNavigationItem(
                                                    R.string.stats_tab,
                                                    R.drawable.ic_stats_24dp,
                                                    R.color.theme_primary );

        AHBottomNavigationItem abilitiesTab = new AHBottomNavigationItem(
                                                    R.string.abilities_tab,
                                                    R.drawable.ic_abilities_24dp,
                                                    R.color.theme_primary );

        AHBottomNavigationItem backpackTab = new AHBottomNavigationItem(
                                                    R.string.backpack_tab,
                                                    R.drawable.ic_backpack_24dp,
                                                    R.color.theme_primary );

        AHBottomNavigationItem spellbookTab = new AHBottomNavigationItem(
                                                    R.string.spellbook_tab,
                                                    R.drawable.ic_spellbook_24dp,
                                                    R.color.theme_primary );

        // (2) Add the tabs to bottom navigation bar
        // --------------------------------------------------------------------------------------
        this.bottomNavigation.addItem(profileTab);
        this.bottomNavigation.addItem(statsTab);
        this.bottomNavigation.addItem(abilitiesTab);
        this.bottomNavigation.addItem(backpackTab);
        this.bottomNavigation.addItem(spellbookTab);

        // (3) Navigation bar configuration
        // --------------------------------------------------------------------------------------
        this.bottomNavigation.setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.bottom_nav_bg));
        this.bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.tab_accent));
        this.bottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.tab_inactive));
        this.bottomNavigation.setForceTint(true);
        this.bottomNavigation.setCurrentItem(0);
    }


    private void initializePager()
    {
        ViewPager rpPager = (ViewPager) findViewById(R.id.roleplay_pager);
        RoleplayPagerAdapter rpPagerAdapter = new RoleplayPagerAdapter(getSupportFragmentManager());
        rpPager.setAdapter(rpPagerAdapter);
        // Attach the page change listener inside the activity
        rpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                bottomNavigation.setCurrentItem(position);
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }
        });
    }

}
