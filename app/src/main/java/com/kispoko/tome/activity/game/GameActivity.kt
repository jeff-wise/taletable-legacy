
package com.kispoko.tome.activity.game


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.PagePagerAdapter
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.model.game.Game
import com.kispoko.tome.model.theme.ThemeId


/**
 * Game Activity
 */
class GameActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var game : Game? = null

    private val appSettings : AppSettings = AppSettings(ThemeId.Dark)


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_game)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("game"))
            this.game = this.intent.getSerializableExtra("game") as Game

        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        this.initializeViews()

    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeViews()
    {
        val viewPager = this.findViewById(R.id.view_pager) as ViewPager

        val game = this.game
        if (game != null)
            viewPager.adapter = GamePagerAdapter(supportFragmentManager, game)

        val tabLayout = this.findViewById(R.id.tab_layout) as TabLayout
        tabLayout.setupWithViewPager(viewPager)

        if (game != null)
        {
            val titleView = this.findViewById(R.id.toolbar_title) as TextView
            titleView.setText(game.description().gameName())
        }

    }


}

