
package com.kispoko.tome.activity.official.heroes


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.RecyclerViewBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.official.HeroesGenericNPCSheetSummary
import com.kispoko.tome.rts.official.OfficialManager
import com.kispoko.tome.rts.entity.theme.ThemeManager



/**
 * The Magic of Heroes NPCs Fragment
 */
class MagicOfHeroesGenericNPCsFragment : Fragment()
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
        fun newInstance(themeId : ThemeId) : MagicOfHeroesGenericNPCsFragment
        {
            val fragment = MagicOfHeroesGenericNPCsFragment()

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
        val npcManifest = OfficialManager.heroesGenericNPCSheetManifest(context)
        if (npcManifest != null)
        {
            val summaries = npcManifest.summaries
            layout.addView(this.npcSummaryRecyclerView(summaries, themeId, context))
        }

        return layout
    }


    private fun viewLayout(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        layout.backgroundColor  = ThemeManager.color(themeId, colorTheme)

        return layout.linearLayout(context)
    }


    private fun npcSummaryRecyclerView(summaries : List<HeroesGenericNPCSheetSummary>,
                                       themeId : ThemeId,
                                       context : Context) : RecyclerView
    {
        val recyclerView                = RecyclerViewBuilder()

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

        recyclerView.layoutManager      = LinearLayoutManager(context)

        recyclerView.adapter            = NPCSummaryRecyclerViewAdapter(summaries,
                                                                        themeId,
                                                                        context)

        recyclerView.padding.leftDp     = 6f
        recyclerView.padding.rightDp    = 6f

        return recyclerView.recyclerView(context)
    }


}



// -----------------------------------------------------------------------------------------
// SHEET RECYCLER VIEW ADPATER
// -----------------------------------------------------------------------------------------

class NPCSummaryRecyclerViewAdapter(val items : List<HeroesGenericNPCSheetSummary>,
                                    val themeId : ThemeId,
                                    val context : Context)
        : RecyclerView.Adapter<NPCSummaryViewHolder>()
{

    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : NPCSummaryViewHolder
    {
        return NPCSummaryViewHolder(GenericNPCSummaryView.view(themeId, parent.context))
    }


    override fun onBindViewHolder(viewHolder : NPCSummaryViewHolder, position : Int)
    {
        val summary = this.items[position]

        viewHolder.setNameText(summary.name)
        viewHolder.setDescriptionText(summary.description)
        viewHolder.setSummaryText(summary.summary)
        viewHolder.setChallengeText(summary.challengeRating.toString())

//        val activity = context as OpenSheetOfficialSheetsActivity
//        viewHolder.setOnClick(View.OnClickListener {
//            val viewBuilder = VariantsViewBuilder(summary.name,
//                                                  sheetVariants,
//                                                  themeId,
//                                                  activity)
//            activity.openBottomSheet(viewBuilder.view())
//        })
    }


    override fun getItemCount() = this.items.size

}



// ---------------------------------------------------------------------------------------------
// SUMMARY VIEW HOLDER
// ---------------------------------------------------------------------------------------------

/**
 * The View Holder caches a view for each item.
 */
class NPCSummaryViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layoutView : LinearLayout? = null
    var nameView   : TextView?  = null
    var descView   : TextView?  = null
    var typeView   : TextView?  = null
    var crView     : TextView?  = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layoutView = itemView.findViewById(R.id.heroes_generic_npc_item_layout) as LinearLayout
        this.nameView   = itemView.findViewById(R.id.heroes_generic_npc_item_name) as TextView
        this.descView   = itemView.findViewById(R.id.heroes_generic_npc_item_description) as TextView
        this.typeView   = itemView.findViewById(R.id.heroes_generic_npc_item_summary) as TextView
        this.crView     = itemView.findViewById(R.id.heroes_generic_npc_item_challenge) as TextView
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setOnClick(onClick : View.OnClickListener)
    {
        this.layoutView?.setOnClickListener(onClick)
    }


    fun setNameText(nameString : String)
    {
        this.nameView?.text = nameString
    }


    fun setDescriptionText(descriptionString : String)
    {
        this.descView?.text = descriptionString
    }


    fun setSummaryText(typeString : String)
    {
        this.typeView?.text = typeString
    }


    fun setChallengeText(crString : String)
    {
        this.crView?.text = crString
    }

}


object GenericNPCSummaryView
{


    fun view(themeId : ThemeId, context : Context) : View
    {
        val layout = this.viewLayout(themeId, context)

        // Header
        layout.addView(this.headerView(themeId, context))

        // Name
        layout.addView(this.nameView(themeId, context))

        // Description
        layout.addView(this.descriptionView(themeId, context))


        return layout
    }


    private fun viewLayout(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.id               = R.id.heroes_generic_npc_item_layout

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.topDp     = 10f

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor  = Color.WHITE

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        return layout.linearLayout(context)
    }


    private fun headerView(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout          = this.headerViewLayout(themeId, context)

        layout.addView(this.challengeView(themeId, context))

        layout.addView(this.summaryView(themeId, context))

        return layout
    }


    private fun headerViewLayout(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.BOTTOM

        layout.padding.topDp    = 8f
        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        return layout.linearLayout(context)
    }


    private fun challengeView(themeId : ThemeId, context : Context) : TextView
    {
        val cr                  = TextViewBuilder()

        cr.id                   = R.id.heroes_generic_npc_item_challenge

        cr.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        cr.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        cr.padding.topDp        = 6f
        cr.padding.bottomDp     = 6f
        cr.padding.leftDp       = 8f
        cr.padding.rightDp      = 8f

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("red_90"))))
        cr.backgroundColor      = ThemeManager.color(themeId, bgColorTheme)

        cr.gravity              = Gravity.CENTER

        cr.color                = Color.WHITE

        cr.font                 = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        cr.corners              = Corners(3.0, 3.0, 3.0, 3.0)

        cr.sizeSp               = 16f

        return cr.textView(context)
    }


    private fun nameView(themeId : ThemeId, context : Context) : TextView
    {
        val name                = TextViewBuilder()

        name.id                 = R.id.heroes_generic_npc_item_name

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.padding.leftDp   = 8f
        name.padding.rightDp  = 8f

        name.padding.topDp      = 3f
        name.padding.bottomDp      = 3f

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
        name.color              = ThemeManager.color(themeId, colorTheme)


        name.sizeSp             = 21f

        return name.textView(context)
    }


    private fun descriptionView(themeId : ThemeId, context : Context) : TextView
    {
        val desc                = TextViewBuilder()

        desc.id                 = R.id.heroes_generic_npc_item_description

        desc.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        desc.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        desc.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        desc.color              = ThemeManager.color(themeId, colorTheme)


        desc.sizeSp             = 15f

        desc.padding.leftDp     = 8f
        desc.padding.rightDp    = 8f

        desc.margin.bottomDp    = 8f

        return desc.textView(context)
    }


    private fun summaryView(themeId : ThemeId,
                            context : Context) : TextView
    {
        val summary             = TextViewBuilder()

        summary.id              = R.id.heroes_generic_npc_item_summary

        summary.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        summary.font            = Font.typeface(TextFont.default(),
                                                TextFontStyle.SemiBold,
                                                context)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        summary.backgroundColor      = ThemeManager.color(themeId, bgColorTheme)

        val labelColorTheme     = ColorTheme(setOf(
                                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        summary.color           = ThemeManager.color(themeId, labelColorTheme)

        summary.sizeSp              = 16f

        summary.padding.leftDp      = 8f
        summary.padding.rightDp     = 8f
        summary.padding.topDp       = 6f
        summary.padding.bottomDp    = 6f

        summary.margin.leftDp   = 6f

        summary.corners              = Corners(3.0, 3.0, 3.0, 3.0)

        return summary.textView(context)
    }

}
