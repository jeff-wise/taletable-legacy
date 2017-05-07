
package com.kispoko.tome.activity.official.template;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import com.kispoko.tome.R;
import com.kispoko.tome.official.template.Game;
import com.kispoko.tome.official.template.TemplateIndex;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.lib.yaml.YamlParseException;



/**
 * Choose Template Activity
 */
public class OfficialTemplatesActivity extends AppCompatActivity
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private Game            game;
    private TemplateIndex   templateIndex;


    // ACTIVITY LIFECYCLE EVENTS
    // -----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // [1] Set Content View
        // ------------------------------------------------------------------------------------

        setContentView(R.layout.activity_official_templates);

        // [2] Get Parameters
        // ------------------------------------------------------------------------------------

        this.game = null;
        if (getIntent().hasExtra("game")) {
            this.game = (Game) getIntent().getSerializableExtra("game");
        }

        this.templateIndex = null;
        try {
            this.templateIndex = TemplateIndex.fromManifest(this);
        }
        catch (YamlParseException exception) {
            Log.d("***TEMPLATES", exception.errorMessage(), exception);
        }
        catch (Exception exception) {
            // TODO something better
            Log.d("***TEMPLATES", exception.toString(), exception);
        }

        // [3] Initialize UI
        // -----------------------------------------------------------------------------------

        this.initializeToolbar();
        this.initializeTabs();
        // initializeView();
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
        getMenuInflater().inflate(R.menu.empty, menu);
        return true;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        UI.initializeToolbar(this, getString(R.string.who_will_you_be));
    }


    private void initializeTabs()
    {
        SkillLevelPagerAdapter pagerAdapter =
                new SkillLevelPagerAdapter(getSupportFragmentManager(),
                                           this.game,
                                           this.templateIndex,
                                           this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }


}
