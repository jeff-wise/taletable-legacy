
package com.kispoko.tome.activity.home


import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.R.string.groups
import com.kispoko.tome.activity.entity.feed.FeedUI
import com.kispoko.tome.activity.session.NewSessionActivity
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.app.assetInputStream
import com.kispoko.tome.load.TomeDoc
import com.kispoko.tome.model.AppActionOpenSession
import com.kispoko.tome.model.engine.variable.TextVariable
import com.kispoko.tome.model.engine.variable.TextVariableLiteralValue
import com.kispoko.tome.model.engine.variable.Variable
import com.kispoko.tome.model.engine.variable.VariableId
import com.kispoko.tome.model.feed.*
import com.kispoko.tome.model.sheet.group.*
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.sheet.widget.TextWidget
import com.kispoko.tome.model.sheet.widget.TextWidgetFormat
import com.kispoko.tome.model.sheet.widget.WidgetFormat
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialAppThemeLight
import com.kispoko.tome.rts.entity.addFeed
import com.kispoko.tome.rts.session.SessionId
import com.kispoko.tome.util.Util
import com.kispoko.tome.util.configureToolbar
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout
import effect.Err
import effect.Val
import maybe.Just
import java.io.IOException
import java.util.*


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
class FeedActivity : AppCompatActivity(), RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener<Any>
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var rfabHelper : RapidFloatingActionHelper? = null


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_feed)

        // (2) Read Parameters (or saved state)
        // -------------------------------------------------------------------------------------

        // (3) Configure View
        // -------------------------------------------------------------------------------------

        this.configureToolbar(getString(R.string.tome))

        this.findViewById<TextView>(R.id.toolbar_title)?.let { titleTextView ->
            titleTextView.textSize = Util.spToPx(7.2f, this).toFloat()

        }

        this.applyTheme(officialAppThemeLight)

        this.initializeFAB()

        this.initializeNavigationView()

        this.initializeView()

    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeView()
    {
        val contentView = this.findViewById<LinearLayout>(R.id.content)
        val feed = this.feed()
        addFeed(feed)
        val feedUI = FeedUI(feed, officialAppThemeLight, this)
        contentView?.addView(feedUI.view())
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

    }


    // -----------------------------------------------------------------------------------------
    // FLOATING ACTION BUTTON
    // -----------------------------------------------------------------------------------------

    private fun initializeFAB()
    {
        val rfaContent = RapidFloatingActionContentLabelList(this)
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this)

        val items = mutableListOf<RFACLabelItem<Int>>()
        items.add(RFACLabelItem<Int>()
                .setLabel("Open Session")
                .setResId(R.drawable.ic_fab_open)
                .setIconNormalColor(Color.parseColor("#469FCD"))
                .setIconPressedColor(-0xf2acfe)
                .setLabelColor(Color.parseColor("#1887C0"))
                .setWrapper(2)
        )
        items.add(RFACLabelItem<Int>()
                .setLabel("New Session")
                .setResId(R.drawable.ic_fab_add)
                .setIconNormalColor(Color.parseColor("#58C480"))
                .setIconPressedColor(Color.parseColor("#6ACA8E"))
                .setLabelColor(Color.parseColor("#45BD72"))
                .setWrapper(3)
        )
        rfaContent
                .setItems(items.toList())
                .setIconShadowColor(Color.parseColor("#646464"))
                .setIconShadowRadius(Util.dpToPixel(5f))

        val fabLayout = this.findViewById<RapidFloatingActionLayout>(R.id.fab_layout)
        val fabView = this.findViewById<RapidFloatingActionButton>(R.id.fab)

        this.rfabHelper = RapidFloatingActionHelper(this,
                                                    fabLayout,
                                                    fabView,
                                                    rfaContent
                                                    ).build()

    }



    override fun onRFACItemIconClick(position : Int, item : RFACLabelItem<Any>?)
    {
        this.rfabHelper?.toggleContent()

        when (position) {
            1 -> this.newSession()
        }
    }


    override fun onRFACItemLabelClick(position : Int, item : RFACLabelItem<Any>?)
    {
        this.rfabHelper?.toggleContent()

        when (position) {
            1 -> this.newSession()
        }
    }


    // -----------------------------------------------------------------------------------------
    // SESSIONS
    // -----------------------------------------------------------------------------------------

    private fun newSession()
    {
        val intent = Intent(this, NewSessionActivity::class.java)
        this.startActivity(intent)
    }



    // -----------------------------------------------------------------------------------------
    // FEED
    // -----------------------------------------------------------------------------------------

    private fun feed() : Feed
    {
        val feed = newsFeed() ?: Feed.empty()

        feed.appendCard(this.rulebook5eSessionCard())
        feed.appendCard(this.casmeySessionCard())

        return feed
    }


    private fun newsFeed() : Feed?
    {
        return try {
            val feedLoader = assetInputStream(this, "feed/news.yaml")
                               .apply { TomeDoc.loadFeed(it, "Home Feed", this) }

            when (feedLoader)
            {
                is Val -> {
                    feedLoader.value
                }
                is Err -> {
                    ApplicationLog.error(feedLoader.error)
                    null
                }
            }
        }
        catch (e : IOException) {
            Log.d("***FEED ACTIVITY", "io exception loading news feed")
            null
        }
    }


    private fun rulebook5eSessionCard() : CardItem
    {

        val titleTextFormat =
                TextFormat.default()
                   .withColorTheme(ColorTheme(setOf(
                       ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8")))))
                   .withSize(TextSize(22f))
                   .withElementFormat(ElementFormat.default().withHorizontalAlignment(Alignment.Left))
        val titleTextWidgetFormat =
                TextWidgetFormat(
                        WidgetFormat(ElementFormat.default().withHorizontalAlignment(Alignment.Left)),
                        titleTextFormat)
        val titleTextWidget = TextWidget(titleTextWidgetFormat,
                                         VariableId("session_book_5esrd_title"))
        val descriptionTextFormat =
                TextFormat.default()
                   .withColorTheme(ColorTheme(setOf(
                       ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8")))))
                   .withSize(TextSize(16.5f))
                   .withElementFormat(ElementFormat.default().withHorizontalAlignment(Alignment.Left))
        val descriptionTextWidgetFormat =
                    TextWidgetFormat(
                        WidgetFormat(ElementFormat.default().withHorizontalAlignment(Alignment.Left)),
                        descriptionTextFormat)
        val descriptionTextWidget = TextWidget(descriptionTextWidgetFormat,
                                               VariableId("session_book_5esrd_description"))

        val row1Format = GroupRowFormat(ElementFormat.default()
                                          .withHorizontalAlignment(Alignment.Left)
                                          .withLeftMargin(8.0)
                                          .withRightMargin(8.0))

        val row2Format = GroupRowFormat(ElementFormat.default()
                                          .withHorizontalAlignment(Alignment.Left)
                                          .withLeftMargin(8.0)
                                          .withRightMargin(8.0))

        val groupRows = listOf(
                GroupRow(row1Format,
                         GroupRowIndex(0),
                         listOf(titleTextWidget)),
                GroupRow(row2Format,
                         GroupRowIndex(0),
                         listOf(descriptionTextWidget))
        )
        val groupElementFormat = ElementFormat.default()
                                              .withTopMargin(6.0)
                                              .withTopPadding(6.0)
                                              .withBottomPadding(8.0)
        val groupTopBorder = Border.top(BorderEdge(
                                ColorTheme(setOf(
                                        ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_4")))),
                                BorderThickness(1)
                             ))
        val groupFormat = GroupFormat(groupElementFormat, Just(groupTopBorder))
        val groups = listOf(Group(groupFormat, groupRows))

        val card = Card(CardTitle("Featured Session"),
                        CardIsPinned(false),
                        Just(AppActionOpenSession(SessionId(UUID.randomUUID()))),
                        Just(CardActionLabel("Open Session")),
                        groups.map { GroupReferenceLiteral(it) })


        val title = "5E SRD Rulebook"
        val description = "All of the rules from the 5th Edition System Reference Document."
        val variables = listOf<Variable>(
                TextVariable(VariableId("session_book_5esrd_title"),
                        TextVariableLiteralValue(title)),
                TextVariable(VariableId("session_book_5esrd_description"),
                             TextVariableLiteralValue(description))
        )

        return CardItem(card, variables)

    }


    private fun casmeySessionCard() : CardItem
    {

        val titleTextFormat =
                TextFormat.default()
                   .withColorTheme(ColorTheme(setOf(
                       ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8")))))
                   .withSize(TextSize(22f))
                   .withElementFormat(ElementFormat.default().withHorizontalAlignment(Alignment.Left))
        val titleTextWidgetFormat =
                TextWidgetFormat(
                        WidgetFormat(ElementFormat.default().withHorizontalAlignment(Alignment.Left)),
                        titleTextFormat)
        val titleTextWidget = TextWidget(titleTextWidgetFormat,
                                         VariableId("session_casmey_title"))
        val descriptionTextFormat =
                TextFormat.default()
                   .withColorTheme(ColorTheme(setOf(
                       ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8")))))
                   .withSize(TextSize(16.5f))
                   .withElementFormat(ElementFormat.default().withHorizontalAlignment(Alignment.Left))
        val descriptionTextWidgetFormat =
                    TextWidgetFormat(
                        WidgetFormat(ElementFormat.default().withHorizontalAlignment(Alignment.Left)),
                        descriptionTextFormat)
        val descriptionTextWidget = TextWidget(descriptionTextWidgetFormat,
                                               VariableId("session_casmey_description"))

        val row1Format = GroupRowFormat(ElementFormat.default()
                                          .withHorizontalAlignment(Alignment.Left)
                                          .withLeftMargin(8.0)
                                          .withRightMargin(8.0))

        val row2Format = GroupRowFormat(ElementFormat.default()
                                          .withHorizontalAlignment(Alignment.Left)
                                          .withLeftMargin(8.0)
                                          .withRightMargin(8.0))

        val groupRows = listOf(
                GroupRow(row1Format,
                         GroupRowIndex(0),
                         listOf(titleTextWidget)),
                GroupRow(row2Format,
                         GroupRowIndex(0),
                         listOf(descriptionTextWidget))
        )
        val groupElementFormat = ElementFormat.default()
                                              .withTopMargin(6.0)
                                              .withTopPadding(6.0)
                                              .withBottomPadding(8.0)
        val groupTopBorder = Border.top(BorderEdge(
                                ColorTheme(setOf(
                                        ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_4")))),
                                BorderThickness(1)
                             ))
        val groupFormat = GroupFormat(groupElementFormat, Just(groupTopBorder))
        val groups = listOf(Group(groupFormat, groupRows))

        val card = Card(CardTitle("Featured Session"),
                        CardIsPinned(false),
                        Just(AppActionOpenSession(SessionId(UUID.randomUUID()))),
                        Just(CardActionLabel("Open Session")),
                        groups.map { GroupReferenceLiteral(it) })


        val title = "Casmey, Level 1 Human Rogue"
        val description = "A circus performer as a kid and a pirate as a teengaer, " +
                          "Casmey is now a reluctant adult looking for a new adventure"
        val variables = listOf<Variable>(
                TextVariable(VariableId("session_casmey_title"),
                        TextVariableLiteralValue(title)),
                TextVariable(VariableId("session_casmey_description"),
                             TextVariableLiteralValue(description))
        )

        return CardItem(card, variables)

    }


}


