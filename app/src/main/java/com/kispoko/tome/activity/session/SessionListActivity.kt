
package com.kispoko.tome.activity.session


import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.culebra.parseYaml
import com.kispoko.tome.R
import com.kispoko.tome.app.AppYamlError
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.IconSize
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialThemeLight
import com.kispoko.tome.official.GameSummary
import com.kispoko.tome.official.officialManifestPath
import com.kispoko.tome.rts.entity.*
import com.kispoko.tome.rts.session.SessionLoader
import com.kispoko.tome.rts.session.SessionManifest
import com.kispoko.tome.util.Util
import effect.Err
import effect.Val



/**
 * Session List Activity
 */
class SessionListActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var gameSummary : GameSummary? = null
    private var entityKind : EntityKind? = null


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_session_list)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("game_summary"))
            this.gameSummary = this.intent.getSerializableExtra("game_summary") as GameSummary

        if (this.intent.hasExtra("entity_kind"))
            this.entityKind = this.intent.getSerializableExtra("entity_kind") as EntityKind

        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        // Toolbar
        this.initializeToolbarView(officialThemeLight)

        // Theme
        this.applyTheme(officialThemeLight)

        // Session List
        this.initializeSessionList()
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeToolbarView(theme : Theme)
    {
        // Back label text
        val backLabelView = this.findViewById<TextView>(R.id.toolbar_back_label)
        backLabelView.typeface = Font.typeface(TextFont.default(), TextFontStyle.default(), this)
        backLabelView.text     = getString(R.string.back_to_game_items)

        val backLabelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_15"))))
        backLabelView.setTextColor(theme.colorOrBlack(backLabelColorTheme))

        // Back button
        val backButton = this.findViewById<LinearLayout>(R.id.toolbar_back_button)
        backButton?.setOnClickListener {
            this.finish()
        }

        // Breadcrumbs
        val breadcrumbsLayout = this.findViewById<LinearLayout>(R.id.breadcrumbs)

        val breadcrumbs : MutableList<String> = mutableListOf()
        this.gameSummary?.let { breadcrumbs.add(it.name) }
        this.entityKind?.let { breadcrumbs.add(it.name) }

        val breadcrumbsUI = SessionBreadcrumbsUI(breadcrumbs, false, officialThemeLight, this)
        breadcrumbsLayout?.addView(breadcrumbsUI.view())
    }


    private fun initializeSessionList()
    {
        val content = this.findViewById<LinearLayout>(R.id.content)

        val gameId = this.gameSummary?.gameId
        val entityTypeId = this.entityKind?.id

        if (gameId != null && entityTypeId != null)
        {
            val filePath = officialManifestPath(gameId, entityTypeId)
            // TODO why does this crash on fail?
            val manifestParser = parseYaml(assets.open(filePath), SessionManifest.Companion::fromYaml)

            when (manifestParser)
            {
                is Val -> {
                    val sessionLoaders = manifestParser.value.summaries
                    val sessionListUI = SessionListUI(sessionLoaders, officialThemeLight, this)
                    content?.addView(sessionListUI.view())

                }
                is Err -> {
                    ApplicationLog.error(AppYamlError(manifestParser.error))
                }
            }
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

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = theme.colorOrBlack(uiColors.toolbarBackgroundColorId())
        }
    }

}


class SessionListUI(val sessionSummaryList : List<SessionLoader>,
                    val theme : Theme,
                    val context : Context)
{

    fun view() : View
    {
        val layout = this.viewLayout()

        // Recycler View
        layout.addView(this.sessionRecyclerView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_7"))))
        layout.backgroundColor  = theme.colorOrBlack(colorTheme)

        return layout.linearLayout(context)
    }


    private fun sessionRecyclerView() : RecyclerView
    {
        val recyclerView                = RecyclerViewBuilder()

        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT

        recyclerView.layoutManager      = LinearLayoutManager(context)

        recyclerView.adapter            = SessionRecyclerViewAdapter(sessionSummaryList,
                                                                     theme,
                                                                     context)

        recyclerView.padding.leftDp     = 6f
        recyclerView.padding.rightDp    = 6f
        recyclerView.padding.bottomDp   = 60f

        recyclerView.clipToPadding      = false

        return recyclerView.recyclerView(context)
    }


    // -----------------------------------------------------------------------------------------
    // RECYCLER VIEW ADPATER
    // -----------------------------------------------------------------------------------------

    class SessionRecyclerViewAdapter(val summaries : List<SessionLoader>,
                                     val theme : Theme,
                                     val context : Context)
            : RecyclerView.Adapter<SummaryItemViewHolder>()
    {

        // -------------------------------------------------------------------------------------
        // RECYCLER VIEW ADAPTER API
        // -------------------------------------------------------------------------------------

        override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : SummaryItemViewHolder
        {
            return SummaryItemViewHolder(SessionSummaryItem(theme, parent.context).view(),
                                         theme,
                                         parent.context)
        }


        override fun onBindViewHolder(viewHolder : SummaryItemViewHolder, position : Int)
        {
            val summary = this.summaries[position]
            viewHolder.updateView(summary)
        }


        override fun getItemCount() = this.summaries.size

    }


    // ---------------------------------------------------------------------------------------------
    // VIEW HOLDER
    // ---------------------------------------------------------------------------------------------

    /**
     * The View Holder caches a view for each item.
     */
    class SummaryItemViewHolder(itemView : View,
                                val theme : Theme,
                                val context : Context)
                                 : RecyclerView.ViewHolder(itemView)
    {

        // -----------------------------------------------------------------------------------------
        // PROPERTIES
        // -----------------------------------------------------------------------------------------

        var layout           : LinearLayout? = null
        var attributesLayout : LinearLayout? = null
        var nameView         : TextView?  = null
        var descriptionView  : TextView?  = null
        var summaryView      : TextView?  = null
        var openButtonView   : TextView?  = null

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))

        // -----------------------------------------------------------------------------------------
        // INIT
        // -----------------------------------------------------------------------------------------

        init
        {
            this.layout           = itemView.findViewById(R.id.session_summary_layout)
            this.attributesLayout = itemView.findViewById(R.id.session_summary_attributes_layout)
            this.nameView         = itemView.findViewById(R.id.session_summary_name)
            this.descriptionView  = itemView.findViewById(R.id.session_summary_description)
            this.summaryView      = itemView.findViewById(R.id.session_summary_summary)
            this.openButtonView   = itemView.findViewById(R.id.session_summary_open_button)

            this.summaryView?.setOnClickListener {
                this.summaryView?.visibility = View.GONE
                this.descriptionView?.visibility = View.VISIBLE
            }

            this.descriptionView?.setOnClickListener {
                this.descriptionView?.visibility = View.GONE
                this.summaryView?.visibility = View.VISIBLE
            }

        }


        fun updateView(sessionLoader : SessionLoader)
        {
            val sessionSummaryItem = SessionSummaryItem(theme, context)

            this.nameView?.text = sessionLoader.sessionName.value

            this.summaryView?.text = sessionSummaryItem.summarySpannable(
                                        sessionLoader.sessionInfo.sessionSummary.value)

            this.descriptionView?.text = sessionLoader.sessionInfo.sessionDescription.value

            this.attributesLayout?.addView(
                    sessionSummaryItem.primaryAttributeView(sessionLoader.sessionInfo.primaryTag.value))

            sessionLoader.sessionInfo.secondaryTags.forEach {
                this.attributesLayout?.addView(sessionSummaryItem.secondaryAttributeView(it.value))
            }

            val activity = context as AppCompatActivity
            configureOpenButton(sessionLoader, activity)
        }



        private fun configureOpenButton(sessionLoader : SessionLoader, activity : AppCompatActivity)
        {
//            val sheetLoader = OfficialSheetLoader("Casmey",
//                                                  SheetId(sheetId),
//                                                  CampaignId("isara"),
//                                                  GameId("magic_of_heroes"))
//
//            val campaignLoader = OfficialCampaignLoader("Isara",
//                                                        CampaignId("isara"),
//                                                        GameId("magic_of_heroes"))
//
//            val gameLoader = OfficialGameLoader("Magic of Heroes", GameId("magic_of_heroes"))
//
//            val coreRulebookLoader = OfficialBookLoader("Core Rules",
//                                                        BookId("core_rules"),
//                                                        GameId("magic_of_heroes"))
//


//            val loaders = listOf(sheetLoader, campaignLoader, gameLoader, coreRulebookLoader)

            this.openButtonView?.setOnClickListener {
//                val sessionLoader = SessionLoader(SessionId(UUID.randomUUID()),
//                                                  SessionName(""),
//                                                  Nothing(),
//                                                  GameId("magic_of_heroes"),
//                                                  Calendar.getInstance(),
//                                                  loaders,
//                                                  EntitySheetId(SheetId(sheetId)))
                val dialog = LoadSessionProgressDialog.newInstance(sessionLoader,
                                                                   "Session Name")
                dialog.show(activity.supportFragmentManager, "")
            }

        }

    }


}


class SessionSummaryItem(val theme : Theme, val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // SUMMARY VIEW
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = viewLayout()

        // Name
        layout.addView(nameView())

        // Attributes
        layout.addView(attributesView())

        // Summary
        layout.addView(summaryView())

        // Description
        layout.addView(descriptionView())

        // Footer
        layout.addView(footerView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.id               = R.id.session_summary_layout

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


    // SUMMARY VIEW > Summary View
    // -----------------------------------------------------------------------------------------

    private fun attributesView() : LinearLayout
    {
        val layout = attributesViewLayout()

        return layout
    }


    private fun attributesViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.id               = R.id.session_summary_attributes_layout

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.BOTTOM

        layout.padding.topDp    = 2f
        layout.padding.bottomDp = 2f

        return layout.linearLayout(context)
    }


    fun primaryAttributeView(attrString : String) : TextView
    {
        val attr                    = TextViewBuilder()

        attr.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
        attr.height                 = LinearLayout.LayoutParams.WRAP_CONTENT

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_80"))))
        attr.backgroundColor        = theme.colorOrBlack(bgColorTheme)

        attr.text                   = attrString

        attr.padding.leftDp         = 10f
        attr.padding.rightDp        = 10f
        attr.padding.topDp          = 3f
        attr.padding.bottomDp       = 3f

        attr.margin.rightDp         = 4f

        attr.gravity                = Gravity.CENTER

        attr.color                  = Color.WHITE

        attr.font                   = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Bold,
                                                    context)

        attr.corners                = Corners(3.0, 3.0, 3.0, 3.0)

        attr.sizeSp                 = 16f

        return attr.textView(context)
    }


    fun secondaryAttributeView(attrString : String) : TextView
    {
        val attr                    = TextViewBuilder()

        attr.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
        attr.height                 = LinearLayout.LayoutParams.WRAP_CONTENT

        attr.text                   = attrString

        attr.padding.leftDp         = 6f
        attr.padding.rightDp        = 6f
        attr.padding.topDp          = 3f
        attr.padding.bottomDp       = 3f

        attr.margin.rightDp         = 4f


        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        attr.backgroundColor        = theme.colorOrBlack(bgColorTheme)

        attr.gravity                = Gravity.CENTER

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
        attr.color                = theme.colorOrBlack(colorTheme)

        attr.font                 = Font.typeface(TextFont.default(),
                                                  TextFontStyle.SemiBold,
                                                  context)

        attr.corners              = Corners(3.0, 3.0, 3.0, 3.0)

        attr.sizeSp               = 16f

        return attr.textView(context)
    }


    private fun nameView() : TextView
    {
        val name                = TextViewBuilder()

        name.id                 = R.id.session_summary_name

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


    private fun summaryView() : TextView
    {
        val summary                 = TextViewBuilder()

        summary.id                  = R.id.session_summary_summary

        summary.width               = LinearLayout.LayoutParams.MATCH_PARENT
        summary.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        summary.font                = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        summary.color              = theme.colorOrBlack(colorTheme)


        summary.sizeSp             = 17f

        summary.padding.bottomDp    = 5f

        return summary.textView(context)
    }


    fun summarySpannable(summaryString : String) : SpannableStringBuilder
    {
        val builder = SpannableStringBuilder()

        builder.append("$summaryString ")

        builder.append(" \u2026 ")

        val sizePx = Util.spToPx(28f, context)
        val sizeSpan = AbsoluteSizeSpan(sizePx)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        val color              = theme.colorOrBlack(colorTheme)
        val colorSpan = ForegroundColorSpan(color)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        var bgColor = theme.colorOrBlack(bgColorTheme)

        val bgSpan = RoundedBackgroundHeightSpan(60,
                                                 15,
                                                 0.85f,
                                                 20,
                                                 color,
                                                 bgColor,
                                                 null,
                                                 IconSize(17, 17),
                                                 Color.WHITE)

        val startIndex = summaryString.length + 1

        builder.setSpan(sizeSpan, startIndex, startIndex + 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        builder.setSpan(colorSpan, startIndex, startIndex + 3 , Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        builder.setSpan(bgSpan, startIndex, startIndex + 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        return builder
    }


    private fun descriptionView() : TextView
    {
        val description                 = TextViewBuilder()

        description.id                  = R.id.session_summary_description

        description.width               = LinearLayout.LayoutParams.MATCH_PARENT
        description.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        description.font                = Font.typeface(TextFont.default(),
                                                        TextFontStyle.Regular,
                                                        context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        description.color               = theme.colorOrBlack(colorTheme)


        description.sizeSp              = 17f

        description.visibility          = View.GONE

        return description.textView(context)
    }


    private fun footerView() : LinearLayout
    {
        val layout = footerViewLayout()

        // MORE INFO
        layout.addView(openButtonView(context.getString(R.string.more_info).toUpperCase(),
                                      R.id.session_summary_info_button))

        // OPEN
        layout.addView(openButtonView(context.getString(R.string.open).toUpperCase(),
                                      R.id.session_summary_open_button))

        return layout
    }


    private fun footerViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.END

        return layout.linearLayout(context)
    }


    private fun openButtonView(label : String, id : Int) : TextView
    {
        val button                  = TextViewBuilder()

        button.id                   = id

        button.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        button.text                 = label

        button.font                 = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Bold,
                                                    context)

        button.margin.leftDp        = 5f


        button.padding.leftDp       = 10f
        button.padding.rightDp      = 10f
        button.padding.topDp        = 6f
        button.padding.bottomDp     = 6f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        button.color                = theme.colorOrBlack(colorTheme)

        button.sizeSp               = 15f

        return button.textView(context)
    }


}

