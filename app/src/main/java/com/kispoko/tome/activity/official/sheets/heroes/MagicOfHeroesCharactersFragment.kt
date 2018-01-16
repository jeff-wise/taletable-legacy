
package com.kispoko.tome.activity.official.sheets.heroes


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.official.sheets.OpenSheetOfficialSheetsActivity
import com.kispoko.tome.activity.official.sheets.SheetVariant
import com.kispoko.tome.activity.official.sheets.VariantsViewBuilder
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.RecyclerViewBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.official.HeroesCharacterSheetSummary
import com.kispoko.tome.rts.official.OfficialManager
import com.kispoko.tome.rts.theme.ThemeManager



/**
 * Amanace Character Sheet List Fragment
 */
class MagicOfHeroesCharactersFragment : Fragment()
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
        fun newInstance(themeId : ThemeId) : MagicOfHeroesCharactersFragment
        {
            val fragment = MagicOfHeroesCharactersFragment()

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
        val characterSheetManifest = OfficialManager.heroesCharacterSheetManifest(context)
        if (characterSheetManifest != null)
        {
            val summaries = characterSheetManifest.summaries
            layout.addView(this.characterSummaryRecyclerView(summaries, themeId, context))
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


    private fun characterSummaryRecyclerView(summaries : List<HeroesCharacterSheetSummary>,
                                             themeId : ThemeId,
                                             context : Context) : RecyclerView
    {
        val recyclerView                = RecyclerViewBuilder()

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

        recyclerView.layoutManager      = LinearLayoutManager(context)

        recyclerView.adapter            = CharactersRecyclerViewAdapter(summaries,
                                                                        themeId,
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

class CharactersRecyclerViewAdapter(val items : List<HeroesCharacterSheetSummary>,
                                    val themeId : ThemeId,
                                    val context : Context)
        : RecyclerView.Adapter<SummaryItemViewHolder>()
{

    // -------------------------------------------------------------------------------------
    // RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : SummaryItemViewHolder
    {
        return SummaryItemViewHolder(SummaryItemView.view(themeId, parent.context), themeId, parent.context)
    }


    override fun onBindViewHolder(viewHolder : SummaryItemViewHolder, position : Int)
    {
        val summary = this.items[position]

        viewHolder.setNameText(summary.name)
        viewHolder.setDescriptionText(summary.description)
        viewHolder.setSummaryText(summary.summary)

        val sheetVariants = summary.variants.map { SheetVariant(it.label, it.id) }

        val activity = context as OpenSheetOfficialSheetsActivity
        viewHolder.setOnClick(summary.id, summary.name, sheetVariants)

    }


    override fun getItemCount() = this.items.size

}



// ---------------------------------------------------------------------------------------------
// SUMMARY VIEW HOLDER
// ---------------------------------------------------------------------------------------------

/**
 * The View Holder caches a view for each item.
 */
class SummaryItemViewHolder(itemView : View,
                            val themeId : ThemeId,
                            val context : Context)
                             : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layoutView  : LinearLayout? = null
    var nameView    : TextView?  = null
    var descView    : TextView?  = null
    var summaryView : TextView?  = null
    var variantsLayout : LinearLayout?  = null


    var variantsOpen : Boolean = false

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layoutView = itemView.findViewById(R.id.heroes_chars_item_layout) as LinearLayout
        this.nameView  = itemView.findViewById(R.id.heroes_chars_item_name) as TextView
        this.descView  = itemView.findViewById(R.id.heroes_chars_item_desc) as TextView
        this.summaryView = itemView.findViewById(R.id.heroes_chars_item_summary) as TextView
        this.variantsLayout = itemView.findViewById(R.id.heroes_chars_item_variants) as LinearLayout
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setOnClick(sheetId : String, sheetName : String, variants : List<SheetVariant>)
    {
        this.layoutView?.setOnClickListener {
            if (this.variantsOpen)
            {
                this.variantsOpen = false
                this.variantsLayout?.removeAllViews()
            }
            else
            {
                this.variantsOpen = true
                val viewBuilder = VariantsViewBuilder(variants,
                                                      "character",
                                                      sheetName,
                                                      sheetId,
                                                      themeId,
                                                      context)
                this.variantsLayout?.addView(viewBuilder.view())
            }
        }
    }


    fun setNameText(nameString : String)
    {
        this.nameView?.text = nameString
    }


    fun setDescriptionText(descriptionString : String)
    {
        this.descView?.text = descriptionString
    }


    fun setSummaryText(summaryString : String)
    {
        val spannable = SpannableStringBuilder()

        spannable.append("I am a ")
        spannable.append(summaryString)

        var color = ThemeManager.color(themeId, colorTheme)
        if (color != null)
        {
            val colorSpan = ForegroundColorSpan(color)
            spannable.setSpan(colorSpan, 7, spannable.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        this.summaryView?.text = spannable
    }

}


object SummaryItemView
{


    fun view(themeId : ThemeId, context : Context) : View
    {
        val layout = this.viewLayout(themeId, context)

        // Name
        layout.addView(this.nameView(themeId, context))

//        layout.addView(this.dividerView(themeId, context))

        // Summary
        layout.addView(this.summaryView(themeId, context))

//        layout.addView(this.dividerView(themeId, context))

        // Description
        layout.addView(this.descriptionView(themeId, context))

        // Variants
        layout.addView(this.variantsLayout(themeId, context))


        return layout
    }


    private fun viewLayout(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.id               = R.id.heroes_chars_item_layout

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.topDp     = 10f

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor  = Color.WHITE

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        layout.padding.topDp    = 8f

        return layout.linearLayout(context)
    }


    private fun dividerView(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_2"))))
        layout.backgroundColor  = ThemeManager.color(themeId, colorTheme)

        layout.margin.topDp     = 6f
        layout.margin.bottomDp  = 6f

        return layout.linearLayout(context)
    }


    private fun nameView(themeId : ThemeId, context : Context) : TextView
    {
        val name                = TextViewBuilder()

        name.id                 = R.id.heroes_chars_item_name

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.padding.leftDp  = 8f
        name.padding.rightDp = 8f

        name.margin.bottomDp    = 4f

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        name.color              = ThemeManager.color(themeId, colorTheme)


        name.sizeSp             = 21f

        return name.textView(context)
    }


    private fun descriptionView(themeId : ThemeId, context : Context) : TextView
    {
        val name                = TextViewBuilder()

        name.id                 = R.id.heroes_chars_item_desc

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.padding.leftDp  = 8f
        name.padding.rightDp = 8f

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        name.color              = ThemeManager.color(themeId, colorTheme)


        name.sizeSp             = 15f

        name.padding.bottomDp = 8f

        return name.textView(context)
    }


    private fun summaryView(themeId : ThemeId,
                            context : Context) : TextView
    {
        val summary             = TextViewBuilder()

        summary.id              = R.id.heroes_chars_item_summary

        summary.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        summary.padding.leftDp  = 8f
        summary.padding.rightDp = 8f

        summary.margin.bottomDp    = 4f

        summary.font            = Font.typeface(TextFont.default(),
                                                TextFontStyle.SemiBold,
                                                context)

//        val labelColorTheme     = ColorTheme(setOf(
//                                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
//                                    ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
//        summary.color           = ThemeManager.color(themeId, labelColorTheme)

        summary.sizeSp          = 15f

        return summary.textView(context)
    }


    private fun variantsLayout(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.id               = R.id.heroes_chars_item_variants

        return layout.linearLayout(context)
    }

}
