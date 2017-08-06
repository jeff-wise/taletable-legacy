
package com.kispoko.tome.activity.official.sheets


import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.official.sheets.amanace.AmanaceCharactersFragment
import com.kispoko.tome.activity.official.sheets.amanace.AmanaceCreaturesFragment
import com.kispoko.tome.activity.official.sheets.amanace.AmanaceNPCsFragment
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.CustomTabLayout
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.model.theme.UIColors
import com.kispoko.tome.rts.theme.ThemeManager
import com.kispoko.tome.util.configureToolbar
import effect.Err
import effect.Val



/**
 * Open Sheet - Official Sheets Activity
 *
 * Lets the user choose from a collection of pre-made sheets for some official game.
 */
class OpenSheetOfficialSheetsActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var officialGameId : GameId? = null

    private val appSettings : AppSettings = AppSettings(ThemeId.Dark)


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_official_sheets)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("game_id"))
            this.officialGameId = this.intent.getSerializableExtra("game_id") as GameId

        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        // > Toolbar
        this.configureToolbar(getString(R.string.amanace_sheets))

        // > Theme
        val theme = ThemeManager.theme(this.appSettings.themeId())
        when (theme) {
            is Val -> this.applyTheme(theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        // > Tab Views
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

        viewPager.adapter = OfficialSheetsPagerAdapter(supportFragmentManager,
                                                       this.appSettings.themeId())

        val tabLayout = this.findViewById(R.id.tab_layout) as TabLayout
        tabLayout.setupWithViewPager(viewPager)
    }


    private fun applyTheme(uiColors : UIColors)
    {
        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = this.window

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = this.appSettings.color(uiColors.toolbarBackgroundColorId())
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        // Toolbar > Background
        toolbar.setBackgroundColor(this.appSettings.color(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = this.appSettings.color(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById(R.id.toolbar_back_button) as ImageButton
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById(R.id.toolbar_options_button) as ImageButton
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById(R.id.toolbar_title) as TextView
        titleView.setTextColor(this.appSettings.color(uiColors.toolbarTitleColorId()))

        // TAB LAYOUT
        // -------------------------------------------------------------------------------------
        val tabLayout = this.findViewById(R.id.tab_layout) as CustomTabLayout

        // Tab Layout > Background
        tabLayout.setBackgroundColor(this.appSettings.color(uiColors.tabBarBackgroundColorId()))

        // Tab Layout > Text
        tabLayout.setTabTextColors(this.appSettings.color(uiColors.tabTextNormalColorId()),
                                   this.appSettings.color(uiColors.tabTextSelectedColorId()))

        // Tab Layout > Underline
        tabLayout.setSelectedTabIndicatorColor(
                this.appSettings.color(uiColors.tabUnderlineColorId()))
    }

}


/**
 * Sheets Pager Adapter
 */
class OfficialSheetsPagerAdapter(fragmentManager : FragmentManager,
                                 val appThemeId : ThemeId)
            : FragmentStatePagerAdapter(fragmentManager)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTEIS
    // -----------------------------------------------------------------------------------------

    private val pageCount = 3


    // -----------------------------------------------------------------------------------------
    // PAGER ADAPTER
    // -----------------------------------------------------------------------------------------

    override fun getItem(position : Int) : Fragment =
        when (position)
        {
            0    -> AmanaceCharactersFragment.newInstance(appThemeId)
            1    -> AmanaceNPCsFragment.newInstance(appThemeId)
            2    -> AmanaceCreaturesFragment.newInstance(appThemeId)
            else -> AmanaceCharactersFragment.newInstance(appThemeId)
        }


    override fun getCount() : Int = this.pageCount


    override fun getPageTitle(position : Int) : CharSequence =
        when (position)
        {
            0    -> "Characters"
            1    -> "NPCs"
            2    -> "Creatures"
            else -> "Other"
        }

}

