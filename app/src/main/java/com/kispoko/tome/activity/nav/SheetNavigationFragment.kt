
package com.kispoko.tome.activity.nav


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.RecyclerViewBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.campaign.CampaignManager
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.theme.ThemeManager
import effect.Err
import effect.Val



/**
 * Sheet Navigation Fragment
 */
class SheetNavigationFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var themeId : ThemeId? = null

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(themeId : ThemeId) : SheetNavigationFragment
        {
            val fragment = SheetNavigationFragment()

            val args = Bundle()
            args.putSerializable("theme_id", themeId)
            fragment.arguments = args

            return fragment
        }
    }


    // -----------------------------------------------------------------------------------------
    // FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        if (arguments != null)
        {
            this.themeId = arguments.getSerializable("theme_id") as ThemeId
        }
    }


    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {

        val themeId = this.themeId

        if (themeId != null)
            return this.view(themeId, context)
        else
            return null
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    private fun view(themeId : ThemeId, context : Context) : View
    {
        val layout = this.viewLayout(themeId, context)

        // Recycler View
        val sheetItems = SheetManager.openSheets().map {
            val sheetContext = SheetManager.sheetContext(it)

            val campaign = sheetContext ap { CampaignManager.campaignWithId(it.campaignId) }
            val game = sheetContext ap { GameManager.gameWithId(it.gameId) }
//
            var campaignName : String? = null
            var gameName : String? = null

            when (campaign) {
                is Val -> campaignName = campaign.value.campaignName()
                is Err -> ApplicationLog.error(campaign.error)
            }

            when (game) {
                is Val -> gameName = game.value.description().gameName()
                is Err -> ApplicationLog.error(game.error)
            }

            SheetItem(it.settings().sheetName(),
                      it.settings().sheetSummary(),
                      campaignName,
                      gameName)
        }

        layout.addView(this.sheetRecyclerView(sheetItems, themeId, context))

        return layout
    }


    private fun viewLayout(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = ThemeManager.color(themeId, colorTheme)

        return layout.linearLayout(context)
    }


    private fun sheetRecyclerView(sheetItems : List<SheetItem>,
                                  themeId : ThemeId,
                                  context : Context) : RecyclerView
    {
        val recyclerView                = RecyclerViewBuilder()

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

        recyclerView.layoutManager      = LinearLayoutManager(context)

        recyclerView.adapter            = SheetRecyclerViewAdapter(sheetItems, themeId)

        recyclerView.padding.leftDp     = 6f
        recyclerView.padding.rightDp    = 6f

        return recyclerView.recyclerView(context)
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

        if (item.campaignName != null)
            viewHolder.setCampaignText(item.campaignName)

        if (item.gameName != null)
            viewHolder.setGameText(item.gameName)
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
        this.campaignView = itemView.findViewById(R.id.sheet_nav_item_campaign) as TextView
        this.gameView     = itemView.findViewById(R.id.sheet_nav_item_game) as TextView
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
        layout.addView(this.contextView(themeId, context))

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        layout.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)

        layout.corners          = Corners(TopLeftCornerRadius(1f),
                                          TopRightCornerRadius(1f),
                                          BottomRightCornerRadius(1f),
                                          BottomLeftCornerRadius(1f))

        layout.margin.topDp     = 6f

        layout.padding.topDp    = 6f
        layout.padding.leftDp   = 6f
        layout.padding.rightDp  = 6f
        layout.padding.bottomDp = 6f

        return layout.linearLayout(context)
    }


    private fun headerView(themeId : ThemeId, context : Context) : TextView
    {
        val header              = TextViewBuilder()

        header.id               = R.id.sheet_nav_item_header

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.font             = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        header.color            = ThemeManager.color(themeId, colorTheme)


        header.sizeSp           = 17f

        return header.textView(context)
    }


    private fun summaryView(themeId : ThemeId, context : Context) : TextView
    {
        val summary             = TextViewBuilder()

        summary.id              = R.id.sheet_nav_item_summary

        summary.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        summary.font            = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        summary.color           = ThemeManager.color(themeId, colorTheme)

        summary.sizeSp          = 14f

        return summary.textView(context)
    }


    // -----------------------------------------------------------------------------------------
    // CONTEXT VIEW
    // -----------------------------------------------------------------------------------------

    private fun contextView(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout = this.contextViewLayout(themeId, context)

        // Campaign Context
        layout.addView(this.contextItemView(context.getString(R.string.campaign),
                                            themeId,
                                            context))

        // Game Context
        layout.addView(this.contextItemView(context.getString(R.string.game),
                                            themeId,
                                            context))

        return layout
    }


    private fun contextViewLayout(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.topDp     = 8f

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

        layout.orientation      = LinearLayout.HORIZONTAL

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_3")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//        layout.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)

        layout.padding.topDp    = 4f
        layout.padding.bottomDp = 4f
        layout.padding.rightDp  = 4f
        layout.padding.leftDp   = 4f

        layout.margin.bottomDp  = 6f

        layout.child(name)
              .child(value)

        // (3 A) Name
        // -------------------------------------------------------------------------------------

        name.widthDp            = 70
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.text               = nameString

        name.font               = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                context)

        val nameColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        name.color              = ThemeManager.color(themeId, nameColorTheme)

        name.sizeSp             = 13f

        // (3 B) Value
        // -------------------------------------------------------------------------------------

        value.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        if (nameString == "Campaign")
            value.id             = R.id.sheet_nav_item_campaign
        else if (nameString == "Game")
            value.id             = R.id.sheet_nav_item_game

        value.font               = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                context)

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_18")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        value.color              = ThemeManager.color(themeId, valueColorTheme)

        value.sizeSp             = 13f

        return layout.linearLayout(context)
    }

}
