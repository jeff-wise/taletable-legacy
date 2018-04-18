
package com.kispoko.tome.activity.official.heroes


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.session.LoadSessionProgressDialog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.book.BookId
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.official.games.HeroesCharacterSheetSummary
import com.kispoko.tome.rts.entity.*
import com.kispoko.tome.rts.official.OfficialManager
import com.kispoko.tome.rts.session.SessionId
import com.kispoko.tome.rts.session.SessionLoader
import com.kispoko.tome.rts.session.SessionName
import com.kispoko.tome.util.Util
import maybe.Nothing
import java.util.*


/**
 * Magic Of Heroes Official Characters
 */
//class MagicOfHeroesCharactersFragment : Fragment()
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    private var theme : Theme? = null
//
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object
//    {
//        fun newInstance(theme : Theme) : MagicOfHeroesCharactersFragment
//        {
//            val fragment = MagicOfHeroesCharactersFragment()
//
//            val args = Bundle()
//            args.putSerializable("theme", theme)
//            fragment.arguments = args
//
//            return fragment
//        }
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // FRAGMENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun onCreate(savedInstanceState : Bundle?)
//    {
//        super.onCreate(savedInstanceState)
//
//        if (arguments != null)
//        {
//            this.theme = arguments.getSerializable("theme") as Theme
//        }
//    }
//
//
//    override fun onCreateView(inflater : LayoutInflater?,
//                              container : ViewGroup?,
//                              savedInstanceState : Bundle?) : View?
//    {
//        val theme = this.theme
//
//        return if (theme != null)
//            this.view(theme, context)
//        else
//            null
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // VIEWS
//    // -----------------------------------------------------------------------------------------
//
//    private fun view(theme : Theme, context : Context) : View
//    {
//        val layout = this.viewLayout(theme, context)
//
//        // Recycler View
//        val characterSheetManifest = OfficialManager.heroesCharacterSheetManifest(context)
//        if (characterSheetManifest != null)
//        {
//            val summaries = characterSheetManifest.summaries
//            layout.addView(this.characterSummaryRecyclerView(summaries, theme, context))
//        }
//
//        return layout
//    }
//
//
//    private fun viewLayout(theme : Theme, context : Context) : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
//        layout.backgroundColor  = theme.colorOrBlack(colorTheme)
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun characterSummaryRecyclerView(summaries : List<HeroesCharacterSheetSummary>,
//                                             theme : Theme,
//                                             context : Context) : RecyclerView
//    {
//        val recyclerView                = RecyclerViewBuilder()
//
//        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
//        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT
//
//        recyclerView.layoutManager      = LinearLayoutManager(context)
//
//        recyclerView.adapter            = CharactersRecyclerViewAdapter(summaries,
//                                                                        theme,
//                                                                        context)
//
//        recyclerView.padding.leftDp     = 6f
//        recyclerView.padding.rightDp    = 6f
//        recyclerView.padding.bottomDp   = 60f
//
//        recyclerView.clipToPadding      = false
//
//        return recyclerView.recyclerView(context)
//    }
//
//}
//
//
//// -----------------------------------------------------------------------------------------
//// SHEET RECYCLER VIEW ADPATER
//// -----------------------------------------------------------------------------------------
//
//class CharactersRecyclerViewAdapter(val items : List<HeroesCharacterSheetSummary>,
//                                    val theme : Theme,
//                                    val context : Context)
//        : RecyclerView.Adapter<SummaryItemViewHolder>()
//{
//
//    // -------------------------------------------------------------------------------------
//    // RECYCLER VIEW ADAPTER API
//    // -------------------------------------------------------------------------------------
//
//    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : SummaryItemViewHolder
//    {
//        return SummaryItemViewHolder(pcView(theme, parent.context), theme, parent.context)
//    }
//
//
//    override fun onBindViewHolder(viewHolder : SummaryItemViewHolder, position : Int)
//    {
//        val summary = this.items[position]
//
//        viewHolder.setNameText(summary.name)
//        viewHolder.setSummaryText(summary.summary)
//        viewHolder.setDescriptionText(summary.description)
//        viewHolder.setLevelText(summary.level.toString())
//        viewHolder.setRaceText(summary.race)
//        viewHolder.setClassText(summary._class)
//
//        val activity = context as AppCompatActivity
//        viewHolder.configureOpenButton(summary.id, summary.name, activity)
//
//
//
////        viewHolder.setOnClick(summary.id, summary.name, sheetVariants)
//
//    }
//
//
//    override fun getItemCount() = this.items.size
//
//}
//
//
//// ---------------------------------------------------------------------------------------------
//// SUMMARY VIEW HOLDER
//// ---------------------------------------------------------------------------------------------
//
///**
// * The View Holder caches a view for each item.
// */
//class SummaryItemViewHolder(itemView : View,
//                            val theme : Theme,
//                            val context : Context)
//                             : RecyclerView.ViewHolder(itemView)
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    var layoutView     : LinearLayout? = null
//    var nameView       : TextView?  = null
//    var levelView      : TextView?  = null
//    var raceView       : TextView?  = null
//    var classView      : TextView?  = null
//    var descView       : TextView?  = null
//    var summaryView    : TextView?  = null
//    var openButtonView : TextView?  = null
//
//    var fullView : Boolean = false
//
//    val colorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
//
//
//
//    // -----------------------------------------------------------------------------------------
//    // INIT
//    // -----------------------------------------------------------------------------------------
//
//    init
//    {
//        this.layoutView     = itemView.findViewById(R.id.heroes_chars_item_layout) as LinearLayout
//        this.nameView       = itemView.findViewById(R.id.heroes_chars_item_name) as TextView
//        this.descView   = itemView.findViewById(R.id.heroes_chars_item_desc) as TextView
//        this.levelView      = itemView.findViewById(R.id.heroes_chars_item_level) as TextView
//        this.raceView       = itemView.findViewById(R.id.heroes_chars_item_race) as TextView
//        this.classView      = itemView.findViewById(R.id.heroes_chars_item_class) as TextView
//        this.summaryView    = itemView.findViewById(R.id.heroes_chars_item_summary) as TextView
//        this.openButtonView = itemView.findViewById(R.id.heroes_chars_item_open_button) as TextView
//
//        this.summaryView?.setOnClickListener {
//            this.summaryView?.visibility = View.GONE
//            this.descView?.visibility = View.VISIBLE
//        }
//
//        this.descView?.setOnClickListener {
//            this.descView?.visibility = View.GONE
//            this.summaryView?.visibility = View.VISIBLE
//        }
//
//    }
//
//
//    fun setNameText(nameString : String)
//    {
//        this.nameView?.text = nameString
//    }
//
//
//    fun setLevelText(levelString : String)
//    {
//        this.levelView?.text = "Level $levelString"
//    }
//
//
//    fun setRaceText(raceString : String)
//    {
//        this.raceView?.text = raceString
//    }
//
//
//    fun setClassText(classString : String)
//    {
//        this.classView?.text = classString
//    }
//
//
//    fun setSummaryText(summaryString : String)
//    {
//        this.summaryView?.text = pcSummarySpannable(summaryString, theme, context)
//    }
//
//
//    fun setDescriptionText(descriptionString : String)
//    {
//        this.descView?.text = descriptionString
//    }
//
//
//    fun configureOpenButton(sheetId : String, name: String, activity : AppCompatActivity)
//    {
//        val sheetLoader = OfficialSheetLoader(SheetId(sheetId),
//                                              GameId("magic_of_heroes"))
//
//        val campaignLoader = OfficialCampaignLoader(CampaignId("isara"),
//                                                    GameId("magic_of_heroes"))
//
//        val gameLoader = OfficialGameLoader(GameId("magic_of_heroes"))
//
//        val coreRulebookLoader = OfficialBookLoader(BookId("core_rules"),
//                                                    GameId("magic_of_heroes"))
//
//
//
//        val loaders = listOf(sheetLoader, campaignLoader, gameLoader, coreRulebookLoader)
//
//        this.openButtonView?.setOnClickListener {
////            val sessionLoader = SessionLoader(SessionId(UUID.randomUUID()),
////                                              SessionName(""),
////                                              No(),
////                                              GameId("magic_of_heroes"),
////                                              Calendar.getInstance(),
////                                              loaders,
////                                              EntitySheetId(SheetId(sheetId)))
////            val dialog = LoadSessionProgressDialog.newInstance(sessionLoader,
////                                                       name)
////            dialog.show(activity.supportFragmentManager, "")
//        }
//
//    }
//
//
//}
//
//
//fun pcView(theme : Theme, context : Context) : View
//{
//    val layout = pcViewLayout(theme, context)
//
//    // Name
//    layout.addView(pcNameView(theme, context))
//
//    // Overview
//    layout.addView(pcOverviewView(theme, context))
//
//    // Summary
//    layout.addView(pcSummaryView(theme, context))
//
//    // Description
//    layout.addView(pcDescriptionView(theme, context))
//
//    // Footer
//    layout.addView(pcFooterView(theme, context))
//
//    return layout
//}
//
//
//private fun pcViewLayout(theme : Theme, context : Context) : LinearLayout
//{
//    val layout              = LinearLayoutBuilder()
//
//    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    layout.id               = R.id.heroes_chars_item_layout
//
//    layout.orientation      = LinearLayout.VERTICAL
//
//    layout.margin.topDp     = 10f
//
////        val colorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//    layout.backgroundColor  = Color.WHITE
//
//    layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)
//
//    layout.padding.topDp    = 6f
//    layout.padding.bottomDp    = 6f
//    layout.padding.leftDp    = 6f
//    layout.padding.rightDp    = 6f
//
//    return layout.linearLayout(context)
//}
//
//
//// SUMMARY VIEW
//// -----------------------------------------------------------------------------------------
//
//private fun pcOverviewView(theme : Theme, context : Context) : LinearLayout
//{
//    val layout = pcOverviewViewLayout(theme, context)
//
//    layout.addView(pcLevelView(theme, context))
//
//    layout.addView(pcRaceView(theme, context))
//
//    layout.addView(pcClassView(theme, context))
//
//    return layout
//}
//
//
//private fun pcOverviewViewLayout(theme : Theme, context : Context) : LinearLayout
//{
//    val layout              = LinearLayoutBuilder()
//
//    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//    layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
//
//    layout.orientation      = LinearLayout.HORIZONTAL
//
//    layout.gravity          = Gravity.BOTTOM
//
//    layout.padding.topDp    = 2f
//    layout.padding.bottomDp = 2f
//
//    return layout.linearLayout(context)
//}
//
//
//private fun pcLevelView(theme : Theme, context : Context) : TextView
//{
//    val level                  = TextViewBuilder()
//
//    level.id                   = R.id.heroes_chars_item_level
//
//    level.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//    level.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    val bgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_80"))))
//    level.backgroundColor      = theme.colorOrBlack(bgColorTheme)
//
//    level.padding.leftDp       = 10f
//    level.padding.rightDp      = 10f
//    level.padding.topDp        = 3f
//    level.padding.bottomDp     = 3f
//
//    level.margin.rightDp       = 4f
//
//    level.gravity              = Gravity.CENTER
//
//    level.color                = Color.WHITE
//
//    level.font                 = Font.typeface(TextFont.default(),
//                                               TextFontStyle.Bold,
//                                               context)
//
//    level.corners              = Corners(3.0, 3.0, 3.0, 3.0)
//
//    level.sizeSp               = 16f
//
//    return level.textView(context)
//}
//
//
//private fun pcRaceView(theme : Theme, context : Context) : TextView
//{
//    val race                  = TextViewBuilder()
//
//    race.id                   = R.id.heroes_chars_item_race
//
//    race.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//    race.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    race.padding.leftDp       = 6f
//    race.padding.rightDp      = 6f
//    race.padding.topDp        = 3f
//    race.padding.bottomDp     = 3f
//
//    race.margin.rightDp       = 4f
//
//
//    val bgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//    race.backgroundColor      = theme.colorOrBlack(bgColorTheme)
//
//    race.gravity              = Gravity.CENTER
//
//    val colorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
//    race.color                = theme.colorOrBlack(colorTheme)
//
//    race.font                 = Font.typeface(TextFont.default(),
//                                              TextFontStyle.SemiBold,
//                                              context)
//
//    race.corners              = Corners(3.0, 3.0, 3.0, 3.0)
//
//    race.sizeSp               = 16f
//
//    return race.textView(context)
//}
//
//
//private fun pcClassView(theme : Theme, context : Context) : TextView
//{
//    val _class                  = TextViewBuilder()
//
//    _class.id                   = R.id.heroes_chars_item_class
//
//    _class.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//    _class.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    _class.padding.leftDp       = 6f
//    _class.padding.rightDp      = 6f
//    _class.padding.topDp        = 3f
//    _class.padding.bottomDp     = 3f
//
//    val bgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//    _class.backgroundColor      = theme.colorOrBlack(bgColorTheme)
//
//    _class.gravity              = Gravity.CENTER
//
//    val colorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
//    _class.color                = theme.colorOrBlack(colorTheme)
//
//    _class.font                 = Font.typeface(TextFont.default(),
//                                            TextFontStyle.SemiBold,
//                                            context)
//
//    _class.corners              = Corners(3.0, 3.0, 3.0, 3.0)
//
//    _class.sizeSp               = 16f
//
//    return _class.textView(context)
//}
//
//
//private fun pcNameView(theme : Theme, context : Context) : TextView
//{
//    val name                = TextViewBuilder()
//
//    name.id                 = R.id.heroes_chars_item_name
//
//    name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
//    name.height             = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    name.padding.bottomDp   = 3f
//
//    name.font               = Font.typeface(TextFont.default(),
//                                            TextFontStyle.SemiBold,
//                                            context)
//
//    val colorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//    name.color              = theme.colorOrBlack(colorTheme)
//
//
//    name.sizeSp             = 22f
//
//    return name.textView(context)
//}
//
//
//private fun pcDescriptionView(theme : Theme, context : Context) : TextView
//{
//    val description                 = TextViewBuilder()
//
//    description.id                  = R.id.heroes_chars_item_desc
//
//    description.width               = LinearLayout.LayoutParams.MATCH_PARENT
//    description.height              = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    description.font                = Font.typeface(TextFont.default(),
//                                                    TextFontStyle.Regular,
//                                                    context)
//
//    val colorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//    description.color               = theme.colorOrBlack(colorTheme)
//
//
//    description.sizeSp              = 17f
//
//    description.visibility          = View.GONE
//
//    return description.textView(context)
//}
//
//
//private fun pcVariantsLayout(theme : Theme, context : Context) : LinearLayout
//{
//    val layout              = LinearLayoutBuilder()
//
//    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    layout.id               = R.id.heroes_chars_item_variants
//
//    return layout.linearLayout(context)
//}
//
//
//
//private fun pcSummaryView(theme : Theme, context : Context) : TextView
//{
//    val summary                 = TextViewBuilder()
//
//    summary.id                  = R.id.heroes_chars_item_summary
//
//    summary.width               = LinearLayout.LayoutParams.MATCH_PARENT
//    summary.height              = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    summary.font                = Font.typeface(TextFont.default(),
//                                            TextFontStyle.Regular,
//                                            context)
//
//    val colorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//    summary.color              = theme.colorOrBlack(colorTheme)
//
//
//    summary.sizeSp             = 17f
//
//    summary.padding.bottomDp    = 5f
//
//    return summary.textView(context)
//}
//
//
//
//private fun pcSummarySpannable(summaryString : String,
//                               theme : Theme,
//                               context : Context) : SpannableStringBuilder
//{
//    val builder = SpannableStringBuilder()
//
//    builder.append("$summaryString ")
//
//
//    builder.append(" \u2026 ")
//
//
//    val sizePx = Util.spToPx(28f, context)
//    val sizeSpan = AbsoluteSizeSpan(sizePx)
//
//    val colorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//    val color              = theme.colorOrBlack(colorTheme)
//    val colorSpan = ForegroundColorSpan(color)
//
//    val bgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//    var bgColor = theme.colorOrBlack(bgColorTheme)
////    val bgColorSpan = BackgroundColorSpan(bgColor)
//
//    val bgSpan = RoundedBackgroundHeightSpan(60,
//                                             15,
//                                             0.85f,
//                                             20,
//                                             color,
//                                             bgColor,
//                                             null,
//                                             IconSize(17, 17),
//                                             Color.WHITE)
//
//    val startIndex = summaryString.length + 1
//
//    builder.setSpan(sizeSpan, startIndex, startIndex + 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//    builder.setSpan(colorSpan, startIndex, startIndex + 3 , Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//    builder.setSpan(bgSpan, startIndex, startIndex + 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//
//    return builder
//}
//
//
//
//private fun pcFooterView(theme : Theme, context : Context) : LinearLayout
//{
//    val layout = pcFooterViewLayout(theme, context)
//
//    // MORE INFO
//    layout.addView(pcOpenButtonView(context.getString(R.string.more_info).toUpperCase(),
//                                    R.id.heroes_chars_item_info_button,
//                                    theme,
//                                    context))
//
//    // OPEN
//    layout.addView(pcOpenButtonView(context.getString(R.string.open).toUpperCase(),
//                                    R.id.heroes_chars_item_open_button,
//                                    theme,
//                                    context))
//
//    return layout
//}
//
//
//private fun pcFooterViewLayout(theme : Theme, context : Context) : LinearLayout
//{
//    val layout              = LinearLayoutBuilder()
//
//    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    layout.orientation      = LinearLayout.HORIZONTAL
//
//    layout.gravity          = Gravity.END
//
//    return layout.linearLayout(context)
//}
//
//
//private fun pcOpenButtonView(label : String, id : Int, theme : Theme, context : Context) : TextView
//{
//    val button                  = TextViewBuilder()
//
//    button.id                   = id
//
//    button.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//    button.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    button.text                 = label
//
//    button.font                 = Font.typeface(TextFont.default(),
//                                                TextFontStyle.Bold,
//                                                context)
//
//    button.margin.leftDp        = 5f
//
//
//    button.padding.leftDp       = 10f
//    button.padding.rightDp      = 10f
//    button.padding.topDp        = 6f
//    button.padding.bottomDp     = 6f
//
//    val colorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
//    button.color                = theme.colorOrBlack(colorTheme)
//
//    button.sizeSp               = 15f
//
//    return button.textView(context)
//}
