
package com.taletable.android.activity.home


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.*
import android.widget.*
import com.taletable.android.R
import com.taletable.android.activity.session.NewSessionActivity
import com.taletable.android.activity.sheet.SheetActivity
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.*
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.router.Router
import com.taletable.android.rts.session.*
import com.taletable.android.util.Util
import com.taletable.android.util.configureToolbar
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import io.reactivex.disposables.CompositeDisposable

// pinned
// news
// welcome to tome beta
// > still prototype, schedule
// > content still being added, no licensed content yet
// > about Tome / plan
// > FAQ
//
// support app, donate
//
// core rulebook quick link
//
// random creature
//
// random character sheet
//
// player quote / moment
//
// survey
//
// random spell
//
// random weapon
//
// random rule



/**
 * Feed Activity
 */
class HomeActivity : AppCompatActivity() //, RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener<Any>
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var rfabHelper : RapidFloatingActionHelper? = null


    var hasSavedSessions : Boolean = false

    var selectedSession : Session? = null

    private val messageListenerDisposable : CompositeDisposable = CompositeDisposable()


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_home)

        // (2) Read Parameters (or saved state)
        // -------------------------------------------------------------------------------------

        // (3) Configure View
        // -------------------------------------------------------------------------------------

        this.configureToolbar(getString(R.string.tale_table), TextFont.Cabin, TextFontStyle.Medium)

        this.findViewById<TextView>(R.id.toolbar_title)?.let { titleTextView ->
//            titleTextView.text     = " tome "
            titleTextView.textSize = Util.spToPx(4.8f, this).toFloat()
//            titleTextView.typeface = Font.typeface(TextFont.Kaushan, TextFontStyle.Regular, this)
        }

        this.applyTheme(officialAppThemeLight)

//        this.initializeFAB()

        this.initializeNavigationView()

//        this.initializeView()

        this.initializeListeners()

        this.initializeViewPager()

    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    override fun onDestroy()
    {
        super.onDestroy()
        this.messageListenerDisposable.clear()
    }


    // -----------------------------------------------------------------------------------------
    // MESSAGING
    // -----------------------------------------------------------------------------------------

    private fun initializeListeners()
    {
//        val newSessionMessageDisposable = Router.listen(NewSessionMessage::class.java)
//                                                .subscribe(this::onMessage)
//        this.messageListenerDisposable.add(newSessionMessageDisposable)

        val sessionMessageDisposable = Router.listen(MessageSessionLoad::class.java)
                                             .subscribe(this::onSessionLoadMessage)
        this.messageListenerDisposable.add(sessionMessageDisposable)
    }


    private fun onSessionLoadMessage(message : MessageSessionLoad)
    {
        when (message)
        {
            is MessageSessionEntityLoaded ->
            {
                val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
                progressBar?.let { bar ->
                    val updateAmount = bar.progress + (72 / message.update.totalEntities)
                    bar.progress = updateAmount
                }
            }
            is MessageSessionLoaded ->
            {
                this.selectedSession?.let {
                    val mainEntityId = it.mainEntityId
                    val intent = Intent(this, SheetActivity::class.java)
                    intent.putExtra("sheet_id", mainEntityId)
                    startActivity(intent)
                }
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeViewPager()
    {
        val viewPager = this.findViewById<ViewPager>(R.id.view_pager)
        viewPager?.adapter = HomePagerAdapter(supportFragmentManager)

        val tabLayout = this.findViewById<TabLayout>(R.id.tab_layout)


        val context = this


        tabLayout.addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                override fun onTabSelected(tab : TabLayout.Tab) {
                    super.onTabSelected(tab)
                    val tabIconColor = ContextCompat.getColor(context, R.color.light_theme_light_blue)

                    val iconView = tab.customView?.findViewById<ImageView>(R.id.tab_icon)
                    iconView?.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)

                    val labelView = tab.customView?.findViewById<TextView>(R.id.tab_label)
                    labelView?.setTextColor(tabIconColor)
                }

                override fun onTabUnselected(tab : TabLayout.Tab) {
                    super.onTabUnselected(tab)
                    val tabIconColor = ContextCompat.getColor(context, R.color.light_theme_dark_grey_18)

                    val iconView = tab.customView?.findViewById<ImageView>(R.id.tab_icon)
                    iconView?.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)

                    val labelView = tab.customView?.findViewById<TextView>(R.id.tab_label)
                    labelView?.setTextColor(tabIconColor)
                }

                override fun onTabReselected(tab : TabLayout.Tab) {
                    super.onTabReselected(tab)
                }
        })

        tabLayout?.setupWithViewPager(viewPager)

        tabLayout.getTabAt(0)?.customView = tabView(R.drawable.icon_activity_feed, 19, R.string.feed, officialAppThemeLight)
        tabLayout.getTabAt(1)?.customView = tabView(R.drawable.icon_die, 19, R.string.play, officialAppThemeLight)
        tabLayout.getTabAt(2)?.customView = tabView(R.drawable.icon_group, 22, R.string.share, officialAppThemeLight)


        val tab = tabLayout.getTabAt(0)
        val tabIconColor = ContextCompat.getColor(context, R.color.light_theme_light_blue)
        val iconView = tab?.customView?.findViewById<ImageView>(R.id.tab_icon)
        iconView?.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)

        val labelView = tab?.customView?.findViewById<TextView>(R.id.tab_label)
        labelView?.setTextColor(tabIconColor)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val subToolbar = findViewById<LinearLayout>(R.id.sub_toolbar)
        val tabDivider = findViewById<LinearLayout>(R.id.tab_divider)

        val playUI = PlayUI(officialAppThemeLight, context)
//        subToolbar?.addView(playUI.savedSessionListHeaderView())

        fab.setOnClickListener {
            val intent = Intent(this, NewSessionActivity::class.java)
            startActivity(intent)
        }

        fab.hide()

        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position : Int, positionOffset : Float, positionOffsetPixels : Int) {
            }

            override fun onPageSelected(position : Int)
            {
                when (position)
                {
                    0 -> {
                        subToolbar?.visibility = View.GONE
                        tabDivider?.visibility = View.GONE
                        fab?.hide()
                    }
                    1 -> {
                        if (hasSavedSessions)
                        {
                            subToolbar?.visibility = View.VISIBLE
                            tabDivider?.visibility = View.VISIBLE

                            fab?.show()
                        }
                        else {
                            fab?.hide()
                            subToolbar?.visibility = View.GONE
                            tabDivider?.visibility = View.GONE
                        }
                    }
                    else -> {
                        subToolbar?.visibility = View.GONE
                        tabDivider?.visibility = View.GONE
                        fab?.hide()
                    }
                }
            }


            override fun onPageScrollStateChanged(state : Int) {

            }
    });

    }


    private fun initializeView()
    {
//        val contentView = this.findViewById<LinearLayout>(R.id.content)
//        val feed = this.feed()
//        addFeed(feed)
//        val feedUI = FeedUI(feed, officialAppThemeLight, this)
//        contentView?.addView(feedUI.view())
    }


    /**
     * Initialize the navigation view.
     */
    private fun initializeNavigationView()
    {
        val drawerLayout = this.findViewById<DrawerLayout>(R.id.drawer_layout)
        val mainButtonView = this.findViewById<ImageView>(R.id.toolbar_main_button)
        val navView = this.findViewById<NavigationView>(R.id.left_nav_view)

        val homeOptionsUI = HomeOptionsUI(officialAppThemeLight, this)
        navView?.addView(homeOptionsUI.view())

        mainButtonView?.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START)
            else
                drawerLayout.openDrawer(GravityCompat.START)
        }
    }



    private fun applyTheme(theme : Theme)
    {
        val uiColors = theme.uiColors()

        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = this.window

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            val statusBarColorTheme = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
            window.statusBarColor = theme.colorOrBlack(statusBarColorTheme)
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            val flags = window.decorView.getSystemUiVisibility() or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            window.decorView.setSystemUiVisibility(flags)
//            this.getWindow().setStatusBarColor(Color.WHITE);
//        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

        // Toolbar > Background
        toolbar.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById<ImageButton>(R.id.toolbar_main_button)
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById<ImageButton>(R.id.toolbar_options_button)
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById<TextView>(R.id.toolbar_title)
        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))

        // TAB LAYOUT
        // -------------------------------------------------------------------------------------
        val tabLayout = this.findViewById<HomeTabLayout>(R.id.tab_layout) as HomeTabLayout

        // Tab Layout > Background
        tabLayout.setBackgroundColor(theme.colorOrBlack(uiColors.tabBarBackgroundColorId()))

        val hlColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))

        // Tab Layout > Text
        tabLayout.setTabTextColors(theme.colorOrBlack(uiColors.tabTextNormalColorId()),
                                   theme.colorOrBlack(hlColorTheme))

        // Tab Layout > Underline
        tabLayout.setSelectedTabIndicatorColor(theme.colorOrBlack(hlColorTheme))

    }


    // -----------------------------------------------------------------------------------------
    // FLOATING ACTION BUTTON
    // -----------------------------------------------------------------------------------------

//    private fun initializeFAB()
//    {
//        val rfaContent = RapidFloatingActionContentLabelList(this)
//        rfaContent.setOnRapidFloatingActionContentLabelListListener(this)
//
//        val items = mutableListOf<RFACLabelItem<Int>>()
//        items.add(RFACLabelItem<Int>()
//                .setLabel("Open Session")
//                .setResId(R.drawable.ic_fab_open)
//                .setIconNormalColor(Color.parseColor("#469FCD"))
//                .setIconPressedColor(-0xf2acfe)
//                .setLabelColor(Color.parseColor("#1887C0"))
//                .setWrapper(2)
//        )
//        items.add(RFACLabelItem<Int>()
//                .setLabel("New Session")
//                .setResId(R.drawable.ic_fab_add)
//                .setIconNormalColor(Color.parseColor("#58C480"))
//                .setIconPressedColor(Color.parseColor("#6ACA8E"))
//                .setLabelColor(Color.parseColor("#45BD72"))
//                .setWrapper(3)
//        )
//        rfaContent
//                .setItems(items.toList())
//                .setIconShadowColor(Color.parseColor("#646464"))
//                .setIconShadowRadius(Util.dpToPixel(5f))
//
//        val fabLayout = this.findViewById<RapidFloatingActionLayout>(R.id.fab_layout)
//        val fabView = this.findViewById<RapidFloatingActionButton>(R.id.fab)
//
//        this.rfabHelper = RapidFloatingActionHelper(this,
//                                                    fabLayout,
//                                                    fabView,
//                                                    rfaContent
//                                                    ).build()
//
//    }
//
//
//
//    override fun onRFACItemIconClick(position : Int, item : RFACLabelItem<Any>?)
//    {
//        this.rfabHelper?.toggleContent()
//
//        when (position) {
//            1 -> this.openSession()
//        }
//    }
//
//
//    override fun onRFACItemLabelClick(position : Int, item : RFACLabelItem<Any>?)
//    {
//        this.rfabHelper?.toggleContent()
//
//        when (position) {
//            1 -> this.openSession()
//        }
//    }
//

    // -----------------------------------------------------------------------------------------
    // SESSIONS
    // -----------------------------------------------------------------------------------------

    private fun newSession()
    {
        val intent = Intent(this, NewSessionActivity::class.java)
        this.startActivity(intent)
    }


    // -----------------------------------------------------------------------------------------
    // TAB VIEW
    // -----------------------------------------------------------------------------------------

    private fun tabView(iconId : Int,
                        iconSize : Int,
                        labelId : Int,
                        theme : Theme) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val iconView                = ImageViewBuilder()
        val labelView               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER_VERTICAL


        layout.backgroundColor      = Color.WHITE

        layout.child(iconView)
              .child(labelView)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        iconView.id                 = R.id.tab_icon

        iconView.widthDp            = iconSize
        iconView.heightDp           = iconSize

        iconView.image              = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        iconView.color              = theme.colorOrBlack(iconColorTheme)

        iconView.margin.rightDp     = 4f

        iconView.padding.topDp      = 1f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        labelView.id                = R.id.tab_label

        labelView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.text              = getString(labelId).toUpperCase()

        labelView.font              = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Regular,
                                                    this)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        labelView.color             = theme.colorOrBlack(labelColorTheme)

        labelView.sizeSp            = 17f

        return layout.linearLayout(this)
    }


}


class HomeTabLayout(context : Context, attrs : AttributeSet) : TabLayout(context, attrs)
{

    override fun addTab(tab : Tab, setSelected : Boolean)
    {
        super.addTab(tab, setSelected)

        val mainView = getChildAt(0) as ViewGroup
        val tabView = mainView.getChildAt(tab.position) as ViewGroup

        val tabChildCount = tabView.childCount

        val typeFace = Font.typeface(TextFont.RobotoCondensed,
                                     TextFontStyle.Regular,
                                     context)

        for (i in 0..tabChildCount)
        {
            val tabViewChild = tabView.getChildAt(i)
            if (tabViewChild is TextView) {
                val tabTextView = tabViewChild as TextView

                tabTextView.setTypeface(typeFace, Typeface.BOLD)
            }
        }

    }

}
