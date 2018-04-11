
package com.kispoko.tome.activity.nav


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.game.GameActivity
import com.kispoko.tome.activity.session.GamesListActivity
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.game.Game
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.rts.entity.game.GameManager
import com.kispoko.tome.rts.entity.theme.ThemeManager
import com.kispoko.tome.util.configureToolbar
import effect.Err
import effect.Val



/**
 * Game Navigation Activity
 */
class GameNavigationActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private val appSettings : AppSettings = AppSettings(ThemeId.Light)


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_nav_games)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

//        if (this.intent.hasExtra("sheet_context"))
//            this.sheetContext = this.intent.getSerializableExtra("sheet_context") as SheetContext

        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        // > Toolbar
        this.configureToolbar(getString(R.string.games))

        // > Theme
        val theme = ThemeManager.theme(this.appSettings.themeId())
        when (theme) {
            is Val -> this.applyTheme(theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        // Floating Action Button
        this.initializeFAB()

        // Recycler View
        this.initializeRecyclerView()
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
        val fabView = this.findViewById(R.id.button_open_game)
        fabView.setOnClickListener {
//            val intent = Intent(this, OpenSheetActivity::class.java)
//            this.startActivity(intent)

            val intent = Intent(this, GamesListActivity::class.java)
            this.startActivity(intent)
        }
    }


    private fun initializeRecyclerView()
    {
        val coordLayout = findViewById(R.id.game_list_coordinator) as CoordinatorLayout
        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        val bgColor = ThemeManager.color(this.appSettings.themeId, bgColorTheme)
        bgColor?.let { coordLayout.setBackgroundColor(bgColor) }

        var games : MutableList<Game> = mutableListOf()

        val recyclerView = this.findViewById(R.id.game_list_view) as RecyclerView
        val adapter = GameRecyclerViewAdapter(games, this, this.appSettings.themeId)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Recycler View
        GameManager.openGames().forEach {
            games.add(it)
        }

        adapter.notifyDataSetChanged()
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

    }

}


// -----------------------------------------------------------------------------------------
// GAME RECYCLER VIEW ADPATER
// -----------------------------------------------------------------------------------------

class GameRecyclerViewAdapter(val games : List<Game>,
                              val activity : AppCompatActivity,
                              val themeId : ThemeId)
                                : RecyclerView.Adapter<OpenGameSummaryViewHolder>()
{

    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup,
                                    viewType : Int) : OpenGameSummaryViewHolder
    {
        return OpenGameSummaryViewHolder(OpenGameSummaryView.view(themeId, parent.context))
    }


    override fun onBindViewHolder(viewHolder : OpenGameSummaryViewHolder, position : Int)
    {
        val game : Game = this.games[position]

        viewHolder.setOnClick(View.OnClickListener {
            val intent = Intent(activity, GameActivity::class.java)
            intent.putExtra("game_id", game.gameId)
            activity.startActivity(intent)
        })

        viewHolder.setNameText(game.gameInfo().name.value)
        viewHolder.setSummaryText(game.gameInfo().name.value)
    }


    override fun getItemCount() = this.games.size

}


// ---------------------------------------------------------------------------------------------
// VIEW HOLDER
// ---------------------------------------------------------------------------------------------

/**
 * The View Holder caches a view for each item.
 */
class OpenGameSummaryViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layoutView  : LinearLayout? = null
    var nameView    : TextView?  = null
    var summaryView : TextView?  = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layoutView  = itemView.findViewById(R.id.game_nav_item_layout) as LinearLayout
        this.nameView    = itemView.findViewById(R.id.game_nav_item_header) as TextView
        this.summaryView = itemView.findViewById(R.id.game_nav_item_summary) as TextView
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setNameText(nameString : String)
    {
        this.nameView?.text = nameString
    }


    fun setSummaryText(summaryString : String)
    {
        this.summaryView?.text = summaryString
    }


    fun setOnClick(onClick : View.OnClickListener)
    {
        this.layoutView?.setOnClickListener(onClick)
    }

}


object OpenGameSummaryView
{

    fun view(themeId : ThemeId, context : Context) : View
    {
        val layout = this.viewLayout(themeId, context)

        // Header
        layout.addView(this.headerView(themeId, context))

        // Summary
        layout.addView(this.summaryView(themeId, context))

        return layout
    }


    private fun viewLayout(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.id               = R.id.game_nav_item_layout

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//        layout.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)
        layout.backgroundColor  = Color.WHITE

        layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)

        layout.margin.topDp     = 6f
        layout.margin.leftDp    = 4f
        layout.margin.rightDp   = 4f

        layout.padding.topDp    = 8f
        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f
        layout.padding.bottomDp = 8f

        return layout.linearLayout(context)
    }


    private fun headerView(themeId : ThemeId, context : Context) : TextView
    {
        val header              = TextViewBuilder()

        header.id               = R.id.game_nav_item_header

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.font             = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        header.color            = ThemeManager.color(themeId, colorTheme)


        header.sizeSp           = 18f

        return header.textView(context)
    }


    private fun summaryView(themeId : ThemeId, context : Context) : TextView
    {
        val summary             = TextViewBuilder()

        summary.id              = R.id.game_nav_item_summary

        summary.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        summary.font            = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        summary.color           = ThemeManager.color(themeId, colorTheme)

        summary.sizeSp          = 16f

        return summary.textView(context)
    }


}
