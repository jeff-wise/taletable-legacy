
package com.kispoko.tome.activity.nav


import android.content.Context
import android.content.Intent
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
import com.kispoko.tome.activity.session.GamesListActivity
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.rts.entity.theme.ThemeManager
import com.kispoko.tome.util.configureToolbar
import effect.Err
import effect.Val



/**
 * Sheet Navigation Activity
 */
class SheetNavigationActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

//    private var sheetContext : SheetContext? = null

    private val appSettings : AppSettings = AppSettings(ThemeId.Light)


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_nav_sheets)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

//        if (this.intent.hasExtra("sheet_context"))
//            this.sheetContext = this.intent.getSerializableExtra("sheet_context") as SheetContext

        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        // > Toolbar
        this.configureToolbar(getString(R.string.sheets))

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
        val fabView = this.findViewById(R.id.button_open_sheet)
        fabView.setOnClickListener {
//            val intent = Intent(this, OpenSheetActivity::class.java)
//            this.startActivity(intent)

            val intent = Intent(this, GamesListActivity::class.java)
            this.startActivity(intent)
        }
    }


    private fun initializeRecyclerView()
    {
        val coordLayout = findViewById(R.id.sheet_list_coordinator) as CoordinatorLayout
        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        val bgColor = ThemeManager.color(this.appSettings.themeId, bgColorTheme)
        bgColor?.let { coordLayout.setBackgroundColor(bgColor) }

        var sheetItems : MutableList<SheetItem> = mutableListOf()

        val recyclerView = this.findViewById(R.id.sheet_list_view) as RecyclerView
        val adapter = SheetRecyclerViewAdapter(sheetItems, this.appSettings.themeId)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Recycler View
//        SheetManager.openSheets().forEach {
//            Log.d("***SHEET NAV ACT", "found open sheet: ${it.sheetId()}")
//            val sheetContext = SheetManager.sheetContext(it)
//
//            val campaign = sheetContext ap { CampaignManager.campaignWithId(it.campaignId) }
//            val game = sheetContext ap { GameManager.gameWithId(it.gameId) }
////
//            var campaignName : String? = null
//            var gameName : String? = null
//
//            when (campaign) {
//                is Val -> campaignName = campaign.value.campaignName()
//                is Err -> ApplicationLog.error(campaign.error)
//            }
//
//            when (game) {
//                is Val -> gameName = game.value.gameName().value
//                is Err -> ApplicationLog.error(game.error)
//            }
//
//            val item = SheetItem(it.settings().sheetName(),
//                                 it.settings().sheetSummary(),
//                                 campaignName,
//                                 gameName)
//            sheetItems.add(item)
//
//        }

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
// SHEET ITEM
// -----------------------------------------------------------------------------------------

data class SheetItem(val name : String,
                     val description : String,
                     val campaignName : String?,
                     val gameName : String?)


// -----------------------------------------------------------------------------------------
// SHEET RECYCLER VIEW ADPATER
// -----------------------------------------------------------------------------------------

class SheetRecyclerViewAdapter(val items : List<SheetItem>, val themeId : ThemeId)
        : RecyclerView.Adapter<SheetItemViewHolder>()
{

    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : SheetItemViewHolder
    {
        return SheetItemViewHolder(SheetItemView.view(themeId, parent.context))
    }


    override fun onBindViewHolder(viewHolder : SheetItemViewHolder, position : Int)
    {
        val item = this.items[position]

        viewHolder.setNameText(item.name)
        viewHolder.setSummaryText(item.description)

//        if (item.campaignName != null)
//            viewHolder.setCampaignText(item.campaignName)
//
//        if (item.gameName != null)
//            viewHolder.setGameText(item.gameName)
    }


    override fun getItemCount() = this.items.size

}


// ---------------------------------------------------------------------------------------------
// VALUE VIEW HOLDER
// ---------------------------------------------------------------------------------------------

/**
 * The View Holder caches a view for each item.
 */
class SheetItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var nameView     : TextView? = null
    var summaryView  : TextView? = null
    var campaignView : TextView? = null
    var gameView     : TextView? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.nameView     = itemView.findViewById(R.id.sheet_nav_item_header) as TextView
        this.summaryView  = itemView.findViewById(R.id.sheet_nav_item_summary) as TextView
//        this.campaignView = itemView.findViewById(R.id.sheet_nav_item_campaign) as TextView
//        this.gameView     = itemView.findViewById(R.id.sheet_nav_item_game) as TextView
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


    fun setCampaignText(campaignString : String)
    {
        this.campaignView?.text = campaignString
    }


    fun setGameText(gameString : String)
    {
        this.gameView?.text = gameString
    }

}



// ---------------------------------------------------------------------------------------------
// SHEET ITEM VIEW
// ---------------------------------------------------------------------------------------------

object SheetItemView
{

    fun view(themeId : ThemeId, context : Context) : View
    {
        val layout = this.viewLayout(themeId, context)

        // Header
        layout.addView(this.headerView(themeId, context))

        // Summary
        layout.addView(this.summaryView(themeId, context))

        // Context View
        //layout.addView(this.contextView(themeId, context))

        return layout
    }


    private fun viewLayout(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)

        layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)

        layout.margin.topDp     = 6f
        layout.margin.leftDp    = 8f
        layout.margin.rightDp   = 8f

        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f

        return layout.linearLayout(context)
    }


    private fun headerView(themeId : ThemeId, context : Context) : TextView
    {
        val header              = TextViewBuilder()

        header.id               = R.id.sheet_nav_item_header

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.font             = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
        header.color            = ThemeManager.color(themeId, colorTheme)

        header.margin.leftDp    = 8f
        header.margin.rightDp   = 8f

        header.sizeSp           = 19f

        return header.textView(context)
    }


    private fun summaryView(themeId : ThemeId, context : Context) : TextView
    {
        val summary             = TextViewBuilder()

        summary.id              = R.id.sheet_nav_item_summary

        summary.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        summary.font            = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        summary.color           = ThemeManager.color(themeId, colorTheme)

        summary.sizeSp          = 16f

        summary.margin.leftDp    = 8f
        summary.margin.rightDp   = 8f
        summary.margin.bottomDp  = 8f

        return summary.textView(context)
    }


    // -----------------------------------------------------------------------------------------
    // CONTEXT VIEW
    // -----------------------------------------------------------------------------------------

    private fun contextView(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout = this.contextViewLayout(themeId, context)

        layout.addView(this.contextDividerView(themeId, context))

        // Campaign Context
        layout.addView(this.contextItemView(context.getString(R.string.campaign),
                                            themeId,
                                            context))

        layout.addView(this.contextDividerView(themeId, context))

        // Game Context
        layout.addView(this.contextItemView(context.getString(R.string.game),
                                            themeId,
                                            context))

        return layout
    }


    private fun contextDividerView(themeId : ThemeId, context : Context) : LinearLayout
    {
        val divider             = LinearLayoutBuilder()

        divider.width           = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp        = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        divider.backgroundColor = ThemeManager.color(themeId, colorTheme)


        return divider.linearLayout(context)
    }


    private fun contextViewLayout(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        return layout.linearLayout(context)
    }



    private fun contextItemView(nameString : String,
                                themeId : ThemeId,
                                context : Context) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val name                = TextViewBuilder()
        val value               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_3")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        layout.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)

        layout.padding.topDp    = 5f
        layout.padding.bottomDp = 5f
        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        layout.child(name)
              .child(value)

        // (3 A) Name
        // -------------------------------------------------------------------------------------

        name.widthDp            = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.text               = nameString.toUpperCase()

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        name.color              = ThemeManager.color(themeId, nameColorTheme)

        name.sizeSp             = 10.5f

        // (3 B) Value
        // -------------------------------------------------------------------------------------

        value.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        if (nameString == "Campaign")
            value.id             = R.id.sheet_nav_item_campaign
        else if (nameString == "Game")
            value.id             = R.id.sheet_nav_item_game

        value.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        value.color              = ThemeManager.color(themeId, valueColorTheme)

        value.sizeSp             = 15f

        return layout.linearLayout(context)
    }

}
