
package com.kispoko.tome.activity.engine;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.util.UI;



/**
 * Engine Activity
 */
public class EngineActivity extends AppCompatActivity
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_engine);

        this.initializeToolbar();
        this.initializeTabs();
    }


    // UI
    // -----------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        UI.initializeToolbar(this, false, false);

        // > Set the title
        // -------------------------------------------------------------------------------------
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(R.string.engine);
    }


    /**
     * Initialize the pager adapter.
     */
    private void initializeTabs()
    {
        EnginePagerAdapter pagerAdapter = new EnginePagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

}
