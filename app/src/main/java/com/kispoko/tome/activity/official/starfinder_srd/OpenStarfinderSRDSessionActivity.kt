
package com.kispoko.tome.activity.official.starfinder_srd


import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.official.fifth_ed_srd.FifthEdSRDCharactersFragment
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.CustomTabLayout
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.theme.Theme
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.model.theme.UIColors
import com.kispoko.tome.model.theme.official.officialThemeLight
import com.kispoko.tome.rts.entity.theme.ThemeManager
import com.kispoko.tome.util.configureToolbar
import effect.Err
import effect.Val



/**
 * Open Starfinder SRD Session
 */
class OpenStarfinderSRDSessionActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var officialGameId : GameId? = null

    private val appSettings : AppSettings = AppSettings(ThemeId.Light)

    private var fab : FloatingActionButton? = null
    private var bottomSheet : LinearLayout? = null
    private var bottomSheetBehavior : BottomSheetBehavior<LinearLayout>? = null


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
        this.configureToolbar("5th Edition SRD")

        // > Theme
        val theme = ThemeManager.theme(this.appSettings.themeId())
        when (theme) {
            is Val -> this.applyTheme(theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        // > Tab Views
        this.initializeViews()

        // > Initialize FAB
        this.initializeFAB()

        // > Initialize Bottom Sheet
        this.initializeBottomSheet()
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeFAB()
    {
        val fab = this.findViewById(R.id.button) as FloatingActionButton
        //fab.hide()
        this.fab = fab
    }


    private fun initializeBottomSheet()
    {
        val bottomSheet = this.findViewById(R.id.bottom_sheet) as LinearLayout

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//        val bgColor = ThemeManager.color(this.appSettings.themeId, bgColorTheme)
//        bgColor?.let { bottomSheet.setBackgroundColor(it) }

        val behavior = BottomSheetBehavior.from(bottomSheet)

        val activity = this
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet : View, slideOffset : Float) { }

            override fun onStateChanged(bottomSheet : View, newState : Int)
            {
                if (newState == BottomSheetBehavior.STATE_EXPANDED)
                {
                    fab?.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_fab_forward))
                }
                else
                {
                    fab?.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_fab_random))
                }
//                else {
//                    fab?.setAncdd
//                    fab?.hide()
//                }
//                else if (newState == BottomSheetBehavior.STATE_HIDDEN)
//                    fab?.hide()
//                else if (newState == BottomSheetBehavior.STATE_COLLAPSED)
//                    fab?.hide()
            }

        })

        this.bottomSheet = bottomSheet
        this.bottomSheetBehavior = behavior
    }


    private fun initializeViews()
    {
        val viewPager = this.findViewById(R.id.view_pager) as ViewPager

        viewPager.adapter = OfficialSheetsPagerAdapter(supportFragmentManager, officialThemeLight)

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


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun openBottomSheet(view : View)
    {
        this.bottomSheet?.removeAllViews()
        this.bottomSheet?.addView(view)

//        val behavior = BottomSheetBehavior.from(bottomSheet)
        this.bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }


}

/**
 * Sheets Pager Adapter
 */
class OfficialSheetsPagerAdapter(fragmentManager : FragmentManager,
                                 val theme : Theme)
            : FragmentStatePagerAdapter(fragmentManager)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTEIS
    // -----------------------------------------------------------------------------------------

    private val pageCount = 5


    // -----------------------------------------------------------------------------------------
    // PAGER ADAPTER
    // -----------------------------------------------------------------------------------------

    override fun getItem(position : Int) : Fragment =
        when (position)
        {
            0    -> StarfinderSRDCharactersFragment.newInstance(theme)
            1    -> StarfinderSRDCharactersFragment.newInstance(theme)
            2    -> StarfinderSRDCharactersFragment.newInstance(theme)
            3    -> StarfinderSRDCharactersFragment.newInstance(theme)
            4    -> StarfinderSRDCharactersFragment.newInstance(theme)
            else -> StarfinderSRDCharactersFragment.newInstance(theme)
        }


    override fun getCount() : Int = this.pageCount


    override fun getPageTitle(position : Int) : CharSequence =
        when (position)
        {
            0    -> "Characters"
            1    -> "Creatures"
            2    -> "Generic NPCs"
            3    -> "Named NPCs"
            4    -> "GM Tools"
            else -> "Other"
        }

}