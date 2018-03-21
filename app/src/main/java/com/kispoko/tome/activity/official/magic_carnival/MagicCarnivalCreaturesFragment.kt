
package com.kispoko.tome.activity.official.magic_carnival


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.official.*
import com.kispoko.tome.activity.session.LoadSessionDialog
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.book.BookId
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.official.games.CarnivalCreatureSheetManifest
import com.kispoko.tome.official.games.CarnivalCreatureSheetSummary
import com.kispoko.tome.rts.entity.OfficialBookLoader
import com.kispoko.tome.rts.entity.OfficialCampaignLoader
import com.kispoko.tome.rts.entity.OfficialGameLoader
import com.kispoko.tome.rts.entity.OfficialSheetLoader
import com.kispoko.tome.rts.official.sheetManifest
import com.kispoko.tome.rts.official.sheetManifestFilepath
import com.kispoko.tome.rts.session.SessionId
import effect.Err
import effect.Val



/**
 * Magic Carnival Official Creatures
 */
class MagicCarnivalCreaturesFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var theme : Theme? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(theme : Theme) : MagicCarnivalCreaturesFragment
        {
            val fragment = MagicCarnivalCreaturesFragment()

            val args = Bundle()
            args.putSerializable("theme", theme)
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
            this.theme = arguments.getSerializable("theme") as Theme
        }
    }


    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val theme = this.theme

        return if (theme != null)
            this.view(theme, context)
        else
            null
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    private fun view(theme : Theme, context : Context) : View
    {
        val layout = this.viewLayout(theme, context)

        // Recycler View
        val creatureSheetManifest = sheetManifest(sheetManifestFilepath(GameId("magic_carnival"), "creature"),
                                                  CarnivalCreatureSheetManifest.Companion::fromYaml,
                                                  context)
        when (creatureSheetManifest)
        {
            is Val -> {
                val summaries = creatureSheetManifest.value.summaries
                layout.addView(this.creatureSummaryRecyclerView(summaries, theme, context))
            }
            is Err -> {
                ApplicationLog.error(creatureSheetManifest.error)
            }
        }

        return layout
    }


    private fun viewLayout(theme : Theme, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        layout.backgroundColor  = theme.colorOrBlack(colorTheme)

        return layout.linearLayout(context)
    }


    private fun creatureSummaryRecyclerView(summaries : List<CarnivalCreatureSheetSummary>,
                                            theme : Theme,
                                            context : Context) : RecyclerView
    {
        val recyclerView                = RecyclerViewBuilder()

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

        recyclerView.layoutManager      = LinearLayoutManager(context)

        recyclerView.adapter            = CreaturesRecyclerViewAdapter(summaries,
                                                                theme,
                                                                context)

        recyclerView.padding.leftDp     = 6f
        recyclerView.padding.rightDp    = 6f
        recyclerView.padding.bottomDp   = 60f

        recyclerView.clipToPadding      = false

        return recyclerView.recyclerView(context)
    }

}



// -----------------------------------------------------------------------------------------
// SHEET RECYCLER VIEW ADPATER
// -----------------------------------------------------------------------------------------

class CreaturesRecyclerViewAdapter(val items : List<CarnivalCreatureSheetSummary>,
                                   val theme : Theme,
                                   val context : Context)
        : RecyclerView.Adapter<CreatureSummaryViewHolder>()
{

    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : CreatureSummaryViewHolder
    {
        return CreatureSummaryViewHolder(creatureView(theme, parent.context), theme, parent.context)
    }


    override fun onBindViewHolder(viewHolder : CreatureSummaryViewHolder, position : Int)
    {
        val summary = this.items[position]

        viewHolder.setNameText(summary.name)
        viewHolder.setSummaryText(summary.summary)
        viewHolder.setDescriptionText(summary.description)
        viewHolder.setCRText(summary.challengeRating.toString())
        viewHolder.setSizeText(summary.size)
        viewHolder.setTypeText(summary.type)

        val activity = context as AppCompatActivity
        viewHolder.configureOpenButton(summary.id, summary.name, activity)
    }


    override fun getItemCount() = this.items.size

}



// ---------------------------------------------------------------------------------------------
// SUMMARY VIEW HOLDER
// ---------------------------------------------------------------------------------------------

/**
 * The View Holder caches a view for each item.
 */
class CreatureSummaryViewHolder(itemView : View,
                                val theme : Theme,
                                val context : Context)
                                 : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layoutView     : LinearLayout? = null
    var nameView       : TextView?  = null
    var crView         : TextView?  = null
    var sizeView       : TextView?  = null
    var typeView       : TextView?  = null
    var descView       : TextView?  = null
    var summaryView    : TextView?  = null
    var openButtonView : TextView?  = null

    var fullView : Boolean = false

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))



    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layoutView     = itemView.findViewById(R.id.carnival_creatures_item_layout) as LinearLayout
        this.nameView       = itemView.findViewById(R.id.carnival_creatures_item_name) as TextView

        this.summaryView    = itemView.findViewById(R.id.item_summary) as TextView
        this.descView       = itemView.findViewById(R.id.item_description) as TextView

        this.crView         = itemView.findViewById(R.id.carnival_creatures_item_cr) as TextView
        this.sizeView       = itemView.findViewById(R.id.carnival_creatures_item_size) as TextView
        this.typeView       = itemView.findViewById(R.id.carnival_creatures_item_type) as TextView

        this.openButtonView = itemView.findViewById(R.id.item_open_button) as TextView

        this.summaryView?.setOnClickListener {
            this.summaryView?.visibility = View.GONE
            this.descView?.visibility = View.VISIBLE
        }

        this.descView?.setOnClickListener {
            this.descView?.visibility = View.GONE
            this.summaryView?.visibility = View.VISIBLE
        }
    }


    fun setNameText(nameString : String)
    {
        this.nameView?.text = nameString
    }


    fun setCRText(crString : String)
    {
        this.crView?.text = "CR $crString"
    }


    fun setSizeText(sizeString : String)
    {
        this.sizeView?.text = sizeString
    }


    fun setTypeText(typeString : String)
    {
        this.typeView?.text = typeString
    }


    fun setSummaryText(summaryString : String)
    {
        this.summaryView?.text = summarySpannable(summaryString, theme, context)
    }


    fun setDescriptionText(descriptionString : String)
    {
        this.descView?.text = descriptionString
    }


    fun configureOpenButton(sheetId : String, name: String, activity : AppCompatActivity)
    {
        val sheetLoader = OfficialSheetLoader("Sheet",
                                              SheetId(sheetId),
                                              CampaignId("isara"),
                                              GameId("magic_carnival"))

        val campaignLoader = OfficialCampaignLoader("Isara",
                                                    CampaignId("isara"),
                                                    GameId("magic_carnival"))

        val gameLoader = OfficialGameLoader("Magic Carnival", GameId("magic_carnival"))

        val coreRulebookLoader = OfficialBookLoader("Core Rules",
                                                    BookId("core_rules"),
                                                    GameId("magic_carnival"))



        val loaders = listOf(sheetLoader, campaignLoader, gameLoader, coreRulebookLoader)

        this.openButtonView?.setOnClickListener {
            val dialog = LoadSessionDialog.newInstance(loaders.toMutableList(),
                                                       SessionId("casmey"),
                                                       SheetId(sheetId),
                                                       name)
            dialog.show(activity.supportFragmentManager, "")
        }

    }


}


fun creatureView(theme : Theme, context : Context) : View
{
    val layout = creatureViewLayout(theme, context)

    // Name
    layout.addView(creatureNameView(theme, context))

    // Overview
    layout.addView(creatureOverviewView(theme, context))

    // Summary
    layout.addView(summaryView(theme, context))

    // Description
    layout.addView(descriptionView(theme, context))

    // Footer
    layout.addView(footerView(theme, context))

    return layout
}


private fun creatureViewLayout(theme : Theme, context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.id               = R.id.carnival_creatures_item_layout

    layout.orientation      = LinearLayout.VERTICAL

    layout.margin.topDp     = 10f

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
    layout.backgroundColor  = Color.WHITE

    layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

    layout.padding.topDp    = 6f
    layout.padding.bottomDp    = 6f
    layout.padding.leftDp    = 6f
    layout.padding.rightDp    = 6f

    return layout.linearLayout(context)
}


// SUMMARY VIEW
// -----------------------------------------------------------------------------------------

private fun creatureOverviewView(theme : Theme, context : Context) : LinearLayout
{
    val layout = creatureOverviewViewLayout(theme, context)

    layout.addView(creatureCRView(theme, context))

    layout.addView(creatureSizeView(theme, context))

    layout.addView(creatureTypeView(theme, context))

    return layout
}


private fun creatureOverviewViewLayout(theme : Theme, context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

    layout.orientation      = LinearLayout.HORIZONTAL

    layout.gravity          = Gravity.BOTTOM

    layout.padding.topDp    = 2f
    layout.padding.bottomDp = 2f

    return layout.linearLayout(context)
}


private fun creatureCRView(theme : Theme, context : Context) : TextView
{
    val cr                  = TextViewBuilder()

    cr.id                   = R.id.carnival_creatures_item_cr

    cr.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    cr.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_80"))))
    cr.backgroundColor      = theme.colorOrBlack(bgColorTheme)

    cr.padding.leftDp       = 10f
    cr.padding.rightDp      = 10f
    cr.padding.topDp        = 3f
    cr.padding.bottomDp     = 3f

    cr.margin.rightDp       = 4f

    cr.gravity              = Gravity.CENTER

    cr.color                = Color.WHITE

    cr.font                 = Font.typeface(TextFont.default(),
                                               TextFontStyle.Bold,
                                               context)

    cr.corners              = Corners(3.0, 3.0, 3.0, 3.0)

    cr.sizeSp               = 16f

    return cr.textView(context)
}


private fun creatureSizeView(theme : Theme, context : Context) : TextView
{
    val size                  = TextViewBuilder()

    size.id                   = R.id.carnival_creatures_item_size

    size.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    size.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    size.padding.leftDp       = 6f
    size.padding.rightDp      = 6f
    size.padding.topDp        = 3f
    size.padding.bottomDp     = 3f

    size.margin.rightDp       = 4f


    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
    size.backgroundColor      = theme.colorOrBlack(bgColorTheme)

    size.gravity              = Gravity.CENTER

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
    size.color                = theme.colorOrBlack(colorTheme)

    size.font                 = Font.typeface(TextFont.default(),
                                              TextFontStyle.SemiBold,
                                              context)

    size.corners              = Corners(3.0, 3.0, 3.0, 3.0)

    size.sizeSp               = 16f

    return size.textView(context)
}


private fun creatureTypeView(theme : Theme, context : Context) : TextView
{
    val type                  = TextViewBuilder()

    type.id                   = R.id.carnival_creatures_item_type

    type.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    type.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    type.padding.leftDp       = 6f
    type.padding.rightDp      = 6f
    type.padding.topDp        = 3f
    type.padding.bottomDp     = 3f

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
    type.backgroundColor      = theme.colorOrBlack(bgColorTheme)

    type.gravity              = Gravity.CENTER

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
    type.color                = theme.colorOrBlack(colorTheme)

    type.font                 = Font.typeface(TextFont.default(),
                                            TextFontStyle.SemiBold,
                                            context)

    type.corners              = Corners(3.0, 3.0, 3.0, 3.0)

    type.sizeSp               = 16f

    return type.textView(context)
}


private fun creatureNameView(theme : Theme, context : Context) : TextView
{
    val name                = TextViewBuilder()

    name.id                 = R.id.carnival_creatures_item_name

    name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    name.padding.bottomDp   = 3f

    name.font               = Font.typeface(TextFont.default(),
                                            TextFontStyle.SemiBold,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
    name.color              = theme.colorOrBlack(colorTheme)


    name.sizeSp             = 22f

    return name.textView(context)
}

