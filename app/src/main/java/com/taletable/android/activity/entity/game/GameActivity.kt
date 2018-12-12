
package com.taletable.android.activity.entity.game


import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.app.AppSettings
import com.taletable.android.app.ApplicationLog
import com.taletable.android.lib.ui.CustomTabLayout
import com.taletable.android.model.game.Game
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.model.theme.UIColors
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.game.GameManager
import com.taletable.android.rts.entity.theme.ThemeManager
import com.taletable.android.util.configureToolbar
import effect.Err
import effect.Val



/**
 * Game Activity
 */
class GameActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var gameId : EntityId? = null
    private var game : Game? = null

    private val appSettings : AppSettings = AppSettings(ThemeId.Light)


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

        if (this.intent.hasExtra("game_id"))
            this.gameId = this.intent.getSerializableExtra("game_id") as EntityId

        // >> Load Game
        val gameId = this.gameId
        if (gameId != null) {
            val game = GameManager.gameWithId(gameId)
            when (game) {
                is Val -> this.game = game.value
                is Err -> ApplicationLog.error(game.error)
            }
        }

        if (savedInstanceState != null) {
            this.gameId = savedInstanceState.getSerializable("game_id") as EntityId
        }


        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        // > Toolbar
        val game = this.game
        if (game != null)
            this.configureToolbar(game.gameInfo().name.value)

        // > Theme
        val theme = ThemeManager.theme(this.appSettings.themeId())
        when (theme) {
            is Val -> this.applyTheme(theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        // > Tab Views
        this.initializeViews()
    }


    override fun onSaveInstanceState(outState : Bundle)
    {

        val gameId = this.gameId
        if (gameId != null)
            outState.putSerializable("game_id", gameId)

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState)
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
        val viewPager = this.findViewById<ViewPager>(R.id.view_pager)

        val game = this.game
        if (game != null)
        {
            viewPager.adapter = GamePagerAdapter(supportFragmentManager,
                                                 game,
                                                 this.appSettings.themeId())
        }

        val tabLayout = this.findViewById<TabLayout>(R.id.tab_layout)
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
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

        // Toolbar > Background
        toolbar.setBackgroundColor(this.appSettings.color(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = this.appSettings.color(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById<ImageButton>(R.id.toolbar_back_button)
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById<ImageButton>(R.id.toolbar_options_button)
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById<TextView>(R.id.toolbar_title)
        titleView.setTextColor(this.appSettings.color(uiColors.toolbarTitleColorId()))

        // TAB LAYOUT
        // -------------------------------------------------------------------------------------
        val tabLayout = this.findViewById<CustomTabLayout>(R.id.tab_layout)

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
 * Game Pager Adapter
 */
class GamePagerAdapter(fragmentManager : FragmentManager,
                       val game : Game,
                       val appThemeId : ThemeId)
                        : FragmentStatePagerAdapter(fragmentManager)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTEIS
    // -----------------------------------------------------------------------------------------

    private val pageCount = 2


    // -----------------------------------------------------------------------------------------
    // PAGER ADAPTER
    // -----------------------------------------------------------------------------------------

    override fun getItem(position : Int) : Fragment =
        when (position)
        {
            0    -> BooksFragment.newInstance(this.game.entityId(), this.appThemeId)
            1    -> EngineFragment.newInstance(this.game.entityId(), this.appThemeId)
            else -> EngineFragment.newInstance(this.game.entityId(), this.appThemeId)
        }


    override fun getCount() : Int = this.pageCount


    override fun getPageTitle(position : Int) : CharSequence =
        when (position)
        {
            0    -> "Books"
            1    -> "Rules Engine"
            else -> "Other"
        }

}


