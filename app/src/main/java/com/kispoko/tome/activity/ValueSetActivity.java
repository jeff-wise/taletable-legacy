
package com.kispoko.tome.activity;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.valueset.NewValueDialogFragment;
import com.kispoko.tome.activity.valueset.ValueSetPagerAdapter;
import com.kispoko.tome.engine.value.Dictionary;
import com.kispoko.tome.engine.value.ValueSet;
import com.kispoko.tome.engine.value.ValueUnion;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.util.UI;



/**
 * Value Set Activity
 */
public class ValueSetActivity extends AppCompatActivity
                              implements NewValueDialogFragment.NewValueDialogListener
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ValueSet             valueSet;

    private FloatingActionButton addNewValueButton;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_value_set);

        // > Read Parameters
        String valueSetName = null;
        if (getIntent().hasExtra("value_set_name")) {
            valueSetName = getIntent().getStringExtra("value_set_name");
        }

        // > Lookup ValueSet
        Dictionary dictionary = SheetManager.currentSheet().engine().dictionary();
        this.valueSet = dictionary.lookup(valueSetName);

        initializeToolbar();

        initializeView();

        setupTabs(valueSet);
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
        // > Initialize action bar
        UI.initializeToolbar(this);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        // > Set the title
        String title = this.valueSet.label();
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(title);
    }


    private void initializeView()
    {
        // > Store reference to FAB
        this.addNewValueButton =
                (FloatingActionButton) findViewById(R.id.button_new_value_set_item);
        this.addNewValueButton.hide();

        this.addNewValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewValueDialogFragment newFragment = new NewValueDialogFragment();
                newFragment.show(getSupportFragmentManager(), "new_value");
            }
        });
    }


    private void setupTabs(ValueSet valueSet)
    {
        // > Create Pager Adapter
        // --------------------------------------------------------------------------------------

        ValueSetPagerAdapter valueSetPagerAdapter
                = new ValueSetPagerAdapter(getSupportFragmentManager(), valueSet);

        // > Configure Pager
        // --------------------------------------------------------------------------------------

        ViewPager viewPager = (ViewPager) findViewById(R.id.value_set_pager);
        viewPager.setAdapter(valueSetPagerAdapter);

        // > Configure Pager Tabs
        // --------------------------------------------------------------------------------------

        TabLayout tabLayout = (TabLayout) findViewById(R.id.value_set_tab_layout);
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
                    addNewValueButton.show();
                }
                else {
                    addNewValueButton.hide();
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

    public void onNewValue(ValueUnion valueUnion)
    {
        valueSet.values().add(valueUnion);
    }



}
