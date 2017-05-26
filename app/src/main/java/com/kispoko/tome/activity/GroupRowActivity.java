
package com.kispoko.tome.activity;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.sheet.grouprow.GroupRowPagerAdapter;
import com.kispoko.tome.model.sheet.group.GroupRow;
import com.kispoko.tome.util.UI;



/**
 * Group Row Activity
 */
public class GroupRowActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private GroupRow             groupRow;
    private String               groupName;

    private FloatingActionButton addWidgetButton;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group_row);

        // > Read Parameters
        if (getIntent().hasExtra("group_row")) {
            this.groupRow = (GroupRow) getIntent().getSerializableExtra("group_row");
        }

        if (getIntent().hasExtra("group_name")) {
            this.groupName = getIntent().getStringExtra("group_name");
        }

        initializeToolbar();

        initializeView();

        setupTabs();
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_choose_template, menu);
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
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        Integer rowIndex = this.groupRow.index() + 1;
        String title = this.groupName + " " + getString(R.string.row) + " " + rowIndex.toString();

        UI.initializeToolbar(this, title);
    }


    private void initializeView()
    {
        // > Store reference to FAB
        this.addWidgetButton =
                (FloatingActionButton) findViewById(R.id.button_new_widget);
        this.addWidgetButton.hide();

//        this.addTupleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NewValueDialogFragment newFragment = new NewValueDialogFragment();
//                newFragment.show(getSupportFragmentManager(), "new_value");
//            }
//        });
    }


    private void setupTabs()
    {
        // > Create Pager Adapter
        // --------------------------------------------------------------------------------------

        GroupRowPagerAdapter groupRowPagerAdapter
                = new GroupRowPagerAdapter(getSupportFragmentManager(), this.groupRow);

        // > Configure Pager
        // --------------------------------------------------------------------------------------

        ViewPager viewPager = (ViewPager) findViewById(R.id.group_row_pager);
        viewPager.setAdapter(groupRowPagerAdapter);

        // > Configure Pager Tabs
        // --------------------------------------------------------------------------------------

        TabLayout tabLayout = (TabLayout) findViewById(R.id.group_row_tab_layout);
        tabLayout.setupWithViewPager(viewPager);


        // > View Pager Listener
        // --------------------------------------------------------------------------------------

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                if (position == 1) {
                    addWidgetButton.show();
                }
                else {
                    addWidgetButton.hide();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
    }


    // NEW VALUE DIALOG LISTENER
    // ------------------------------------------------------------------------------------------

    public void onNewGroup(GroupRow groupRow)
    {
    }


}
