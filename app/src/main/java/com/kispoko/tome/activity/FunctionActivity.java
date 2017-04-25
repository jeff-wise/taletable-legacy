
package com.kispoko.tome.activity;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.function.FunctionPagerAdapter;
import com.kispoko.tome.engine.function.Function;
import com.kispoko.tome.engine.function.FunctionIndex;
import com.kispoko.tome.engine.function.Tuple;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.util.UI;



/**
 * Function Activity
 */
public class FunctionActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Function             function;

    private FloatingActionButton addTupleButton;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_function);

        // > Read Parameters
        String functionName = null;
        if (getIntent().hasExtra("function_name")) {
            functionName = getIntent().getStringExtra("function_name");
        }

        // > Lookup Function
        FunctionIndex functionIndex = SheetManager.currentSheet().engine().functionIndex();
        this.function = functionIndex.functionWithName(functionName);

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
        String title = this.function.label();

        UI.initializeToolbar(this, title);
    }


    private void initializeView()
    {
        // > Store reference to FAB
        this.addTupleButton =
                (FloatingActionButton) findViewById(R.id.button_new_tuple);
        this.addTupleButton.hide();

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

        FunctionPagerAdapter functionPagerAdapter
                = new FunctionPagerAdapter(getSupportFragmentManager(), this.function);

        // > Configure Pager
        // --------------------------------------------------------------------------------------

        ViewPager viewPager = (ViewPager) findViewById(R.id.function_pager);
        viewPager.setAdapter(functionPagerAdapter);

        // > Configure Pager Tabs
        // --------------------------------------------------------------------------------------

        TabLayout tabLayout = (TabLayout) findViewById(R.id.function_tab_layout);
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
                    addTupleButton.show();
                }
                else {
                    addTupleButton.hide();
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

    public void onNewTuple(Tuple tuple)
    {
    }



}
