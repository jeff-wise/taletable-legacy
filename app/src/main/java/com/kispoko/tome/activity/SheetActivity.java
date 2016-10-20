
package com.kispoko.tome.activity;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.sheet.ChooseImageAction;
import com.kispoko.tome.activity.sheet.PagePagerAdapter;
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.activity.sheet.PageFragment;
import com.kispoko.tome.db.SheetDatabaseManager;
import com.kispoko.tome.rules.RulesEngine;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.sheet.component.Text;
import com.kispoko.tome.util.Util;



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

        initializeDrawer();
        initializeToolbar();
        initializeNavigation();
        initializeEditSheet();
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
            this.chooseImageAction.setImage(this, uri, this.database);
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


    public void renderSheet()
    {
        PagePagerAdapter pagePagerAdapter =
                new PagePagerAdapter(getSupportFragmentManager(), this.sheet);

        ViewPager viewPager = (ViewPager) findViewById(R.id.page_pager);
        viewPager.setAdapter(pagePagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText( ((Text) this.sheet.componentWithLabel("Name")).getValue());
    }


    public void saveSheet(boolean recursive)
    {
        this.sheet.save(this.database, this, recursive);
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
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.navigation_item_manage_sheets:
                        Intent intent = new Intent(SheetActivity.this, ManageSheetsActivity.class);
                        startActivity(intent);
                        return true;
                }

                return true;
            }
        });
    }


    private void initializeEditSheet()
    {
        View editSheet = findViewById(R.id.edit_sheet);
        final BottomSheetBehavior editSheetBehavior = BottomSheetBehavior.from(editSheet);

        // Set default collapse height of bottom sheet
        int peekHeight = (int) Util.getDim(this, R.dimen.edit_sheet_peek_height);
        editSheetBehavior.setPeekHeight(peekHeight);

        // Allow bottom sheet to be hidden and hide it initially
        editSheetBehavior.setHideable(true);
        editSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        final TextView editSheetTitleView =
                (TextView) editSheet.findViewById(R.id.edit_sheet_action_label);
        editSheetTitleView.setTypeface(Util.sansSerifFontBold(this));

        final TextView editSheetTargetTitleView =
                (TextView) editSheet.findViewById(R.id.edit_sheet_target_label);
        editSheetTargetTitleView.setTypeface(Util.sansSerifFontRegular(this));

        final ImageView editSheetActionIcon =
                (ImageView) editSheet.findViewById(R.id.edit_sheet_action_icon);

        final SheetActivity thisSheetActivity = this;

        editSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(final View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    editSheetTitleView.setText("SAVE");
                    editSheetTitleView.setTextColor(
                            ContextCompat.getColor(thisSheetActivity, R.color.green_soft));
                    editSheetActionIcon.setImageDrawable(
                            ContextCompat.getDrawable(thisSheetActivity,
                                                      R.drawable.ic_edit_sheet_cancel));
                    editSheetActionIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }
                    });
                }
                else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    editSheetTitleView.setText("EDIT");
                    editSheetTitleView.setTextColor(
                            ContextCompat.getColor(thisSheetActivity, R.color.yellow_soft));
                    editSheetActionIcon.setImageDrawable(
                            ContextCompat.getDrawable(thisSheetActivity,
                                    R.drawable.ic_edit_sheet_open));
                    editSheetActionIcon.setOnClickListener(null);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
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

        SheetDatabaseManager sheetDatabaseManager = new SheetDatabaseManager(this);
        this.database = sheetDatabaseManager.getWritableDatabase();

        // This will be a new character sheet
        if (templateId != null) {
            Sheet.loadFromFile(this, templateId);
        }
        // Load the most recently used character sheet
        else {
            Sheet.loadMostRecent(database, this);
        }

    }

}
