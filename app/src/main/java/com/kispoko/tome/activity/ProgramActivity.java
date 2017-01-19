
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
import com.kispoko.tome.activity.program.ProgramPagerAdapter;
import com.kispoko.tome.engine.programming.program.Program;
import com.kispoko.tome.engine.programming.program.ProgramIndex;
import com.kispoko.tome.engine.programming.program.statement.Statement;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.util.UI;



/**
 * Program Activity
 */
public class ProgramActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Program                 program;

    private FloatingActionButton    addStatementButton;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_program);

        // > Read Parameters
        String programName = null;
        if (getIntent().hasExtra("program_name")) {
            programName = getIntent().getStringExtra("program_name");
        }

        // > Lookup Program
        ProgramIndex programIndex = SheetManager.currentSheet().engine().programIndex();
        this.program = programIndex.programWithName(programName);

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
        // > Initialize action bar
        UI.initializeToolbar(this);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        // > Set the title
        String title = this.program.label();
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(title);
    }


    private void initializeView()
    {
        // > Store reference to FAB
        this.addStatementButton =
                (FloatingActionButton) findViewById(R.id.button_new_statement);
        this.addStatementButton.hide();

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

        ProgramPagerAdapter programPagerAdapter
                = new ProgramPagerAdapter(getSupportFragmentManager(), this.program);

        // > Configure Pager
        // --------------------------------------------------------------------------------------

        ViewPager viewPager = (ViewPager) findViewById(R.id.program_pager);
        viewPager.setAdapter(programPagerAdapter);

        // > Configure Pager Tabs
        // --------------------------------------------------------------------------------------

        TabLayout tabLayout = (TabLayout) findViewById(R.id.program_tab_layout);
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
                    addStatementButton.show();
                }
                else {
                    addStatementButton.hide();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
    }


    // NEW STATEMENT DIALOG LISTENER
    // ------------------------------------------------------------------------------------------

    public void onNewStatement(Statement statement)
    {
    }




}
