
package com.taletable.android.activity.session


import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.lib.ui.*
import com.taletable.android.model.session.sessionManifest
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.IconSize
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.model.theme.official.officialThemeLight
import com.taletable.android.router.Router
import com.taletable.android.rts.entity.*
import com.taletable.android.rts.session.Session
import com.taletable.android.util.Util
import com.taletable.android.util.configureToolbar



/**
 * Session List Activity
 */
//class SessionListActivity : AppCompatActivity()
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    private var gameId     : EntityId? = null
//    private var entityKind : EntityKind? = null
//
//
//    // -----------------------------------------------------------------------------------------
//    // ACTIVITY API
//    // -----------------------------------------------------------------------------------------
//
//    override fun onCreate(savedInstanceState : Bundle?)
//    {
//        super.onCreate(savedInstanceState)
//
//        // (1) Set Content View
//        // -------------------------------------------------------------------------------------
//
//        setContentView(R.layout.activity_session_list)
//
//        // (2) Read Parameters
//        // -------------------------------------------------------------------------------------
//
//        if (this.intent.hasExtra("game_id"))
//            this.gameId = this.intent.getSerializableExtra("game_id") as EntityId
//
//        if (this.intent.hasExtra("entity_kind"))
//            this.entityKind = this.intent.getSerializableExtra("entity_kind") as EntityKind
//
//        // (3) Initialize Views
//        // -------------------------------------------------------------------------------------
//
//        // Toolbar
//        this.configureToolbar(getString(R.string.choose_a_session), TextFont.RobotoCondensed, TextFontStyle.Bold)
//
//
//        // Theme
//        this.applyTheme(officialAppThemeLight)
//
//        // Session List
//        this.initializeSessionList()
//    }
//
//
//    override fun onCreateOptionsMenu(menu : Menu) : Boolean
//    {
//        menuInflater.inflate(R.menu.empty, menu)
//        return true
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // UI
//    // -----------------------------------------------------------------------------------------
//
//    private fun initializeSessionList()
//    {
//        val content = this.findViewById<LinearLayout>(R.id.content)
//
//        val gameId = this.gameId
//        val entityTypeId = this.entityKind?.id
//
//        if (gameId != null && entityTypeId != null)
//        {
//            sessionManifest(this).doMaybe {
////                val sessionLoaders = it.sessionLoaders(entityTypeId)
//                val sessionListUI = SessionListUI(it.summaries, officialThemeLight, this)
//                content?.addView(sessionListUI.view())
//            }
//        }
//
//    }
//
//    private fun applyTheme(theme : Theme)
//    {
//        val uiColors = theme.uiColors()
//
//        // STATUS BAR
//        // -------------------------------------------------------------------------------------
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//        {
//            val window = this.window
//
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//
//            val statusBarColorTheme = ColorTheme(setOf(
//                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
//                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
//            window.statusBarColor = theme.colorOrBlack(statusBarColorTheme)
//        }
//
//        // TOOLBAR
//        // -------------------------------------------------------------------------------------
//        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)
//
//        // Toolbar > Background
//        toolbar.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))
//
//        // Toolbar > Icons
//        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())
//
//        val menuLeftButton = this.findViewById<ImageButton>(R.id.toolbar_back_button)
//        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
//
//        val menuRightButton = this.findViewById<ImageButton>(R.id.toolbar_options_button)
//        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
//
//        // TITLE
//        // -------------------------------------------------------------------------------------
//        val titleView = this.findViewById<TextView>(R.id.toolbar_title)
//        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))
//
//    }
//
//
//
//}
//
//
//class SessionListUI(val sessions : List<Session>,
//                    val theme : Theme,
//                    val context : Context)
//{
//
//    fun view() : View
//    {
//        val layout = this.viewLayout()
//
//        // Recycler View
//        layout.addView(this.sessionRecyclerView())
//
//        return layout
//    }
//
//
//    private fun viewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_9"))))
//        layout.backgroundColor  = theme.colorOrBlack(colorTheme)
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun sessionRecyclerView() : RecyclerView
//    {
//        val recyclerView                = RecyclerViewBuilder()
//
//        recyclerView.width              = LinearLayout.LayoutParams.MATCH_PARENT
//        recyclerView.height             = LinearLayout.LayoutParams.MATCH_PARENT
//
//        recyclerView.layoutManager      = LinearLayoutManager(context)
//
//        recyclerView.adapter            = SessionRecyclerViewAdapter(sessions,
//                                                                     theme,
//                                                                     context)
//
//        recyclerView.padding.leftDp     = 8f
//        recyclerView.padding.rightDp    = 8f
//
//        recyclerView.padding.bottomDp   = 60f
//        recyclerView.clipToPadding      = false
//
//        return recyclerView.recyclerView(context)
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // RECYCLER VIEW ADPATER
//    // -----------------------------------------------------------------------------------------
//
//    class SessionRecyclerViewAdapter(val summaries : List<Session>,
//                                     val theme : Theme,
//                                     val context : Context)
//            : RecyclerView.Adapter<SummaryItemViewHolder>()
//    {
//
//        // -------------------------------------------------------------------------------------
//        // RECYCLER VIEW ADAPTER API
//        // -------------------------------------------------------------------------------------
//
//        override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : SummaryItemViewHolder
//        {
//            return SummaryItemViewHolder(SessionSummaryItem(theme, parent.context).view(),
//                                         theme,
//                                         parent.context)
//        }
//
//
//        override fun onBindViewHolder(viewHolder : SummaryItemViewHolder, position : Int)
//        {
//            val summary = this.summaries[position]
//            viewHolder.updateView(summary)
//        }
//
//
//        override fun getItemCount() = this.summaries.size
//
//    }
//
//
//    // ---------------------------------------------------------------------------------------------
//    // VIEW HOLDER
//    // ---------------------------------------------------------------------------------------------
//
//    /**
//     * The View Holder caches a view for each item.
//     */
//    class SummaryItemViewHolder(itemView : View,
//                                val theme : Theme,
//                                val context : Context)
//                                 : RecyclerView.ViewHolder(itemView)
//    {
//
//        // -----------------------------------------------------------------------------------------
//        // PROPERTIES
//        // -----------------------------------------------------------------------------------------
//
//        var layout           : LinearLayout? = null
//        var attributesLayout : LinearLayout? = null
//        var nameView         : TextView?  = null
//        var descriptionView  : TextView?  = null
//        var summaryView      : TextView?  = null
//        var openButtonView   : TextView?  = null
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
//
//        // -----------------------------------------------------------------------------------------
//        // INIT
//        // -----------------------------------------------------------------------------------------
//
//        init
//        {
//            this.layout           = itemView.findViewById(R.id.session_summary_layout)
//            this.attributesLayout = itemView.findViewById(R.id.session_summary_attributes_layout)
//            this.nameView         = itemView.findViewById(R.id.session_summary_name)
//            this.descriptionView  = itemView.findViewById(R.id.session_summary_description)
//            this.summaryView      = itemView.findViewById(R.id.session_summary_summary)
//            this.openButtonView   = itemView.findViewById(R.id.session_summary_open_button)
//
//            this.summaryView?.setOnClickListener {
//                this.summaryView?.visibility = View.GONE
//                this.descriptionView?.visibility = View.VISIBLE
//            }
//
//            this.descriptionView?.setOnClickListener {
//                this.descriptionView?.visibility = View.GONE
//                this.summaryView?.visibility = View.VISIBLE
//            }
//
//        }
//
//
//        fun updateView(sessionLoader : Session)
//        {
//            val sessionSummaryItem = SessionSummaryItem(theme, context)
//
//            this.nameView?.text = sessionLoader.sessionName.value
//
//            this.summaryView?.text = sessionSummaryItem.summarySpannable(
//                                        sessionLoader.sessionInfo.sessionSummary.value)
//
//            this.descriptionView?.text = sessionLoader.sessionInfo.sessionDescription.value
//
//            this.attributesLayout?.addView(
//                    sessionSummaryItem.primaryAttributeView(sessionLoader.sessionInfo.primaryTag.value))
//
//            sessionLoader.sessionInfo.secondaryTags.forEach {
//                this.attributesLayout?.addView(sessionSummaryItem.secondaryAttributeView(it.value))
//            }
//
//            val activity = context as AppCompatActivity
//            configureOpenButton(sessionLoader, activity)
//        }
//
//
//
//        private fun configureOpenButton(sessionLoader : Session, activity : AppCompatActivity)
//        {
//            this.openButtonView?.setOnClickListener {
//                Router.send(NewSessionMessageSession(sessionLoader.sessionId))
//                activity.setResult(1)
//                activity.finish()
//            }
//        }
//
//    }
//
//
//}
//
//
//class SessionSummaryItem(val theme : Theme, val context : Context)
//{
//
//    // -----------------------------------------------------------------------------------------
//    // SUMMARY VIEW
//    // -----------------------------------------------------------------------------------------
//
//    fun view() : View
//    {
//        val layout = viewLayout()
//
//        // Name
//        layout.addView(nameView())
//
//        // Attributes
//        layout.addView(attributesView())
//
//        // Summary
//        layout.addView(summaryView())
//
//        // Description
//        layout.addView(descriptionView())
//
//        // Footer
//        layout.addView(footerView())
//
//        return layout
//    }
//
//
//    private fun viewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.id               = R.id.session_summary_layout
//
//        layout.orientation      = LinearLayout.VERTICAL
//
//        layout.margin.topDp     = 8f
//
//    //        val colorTheme = ColorTheme(setOf(
//    //                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//    //                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//        layout.backgroundColor  = Color.WHITE
//
//        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)
//
//        layout.padding.topDp    = 8f
//        layout.padding.bottomDp = 8f
//        layout.padding.leftDp   = 8f
//        layout.padding.rightDp  = 8f
//
//        layout.elevation        = 2f
//
//        return layout.linearLayout(context)
//    }
//
//
//    // SUMMARY VIEW > Summary View
//    // -----------------------------------------------------------------------------------------
//
//    private fun attributesView() : LinearLayout
//    {
//        val layout = attributesViewLayout()
//
//        return layout
//    }
//
//
//    private fun attributesViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.id               = R.id.session_summary_attributes_layout
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
//
//        layout.orientation      = LinearLayout.HORIZONTAL
//
//        layout.gravity          = Gravity.BOTTOM
//
//        layout.padding.bottomDp = 8f
//
//        return layout.linearLayout(context)
//    }
//
//
//    fun primaryAttributeView(attrString : String) : TextView
//    {
//        val attr                    = TextViewBuilder()
//
//        attr.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
//        attr.height                 = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//        attr.backgroundColor        = theme.colorOrBlack(bgColorTheme)
//
//        attr.text                   = attrString
//
//        attr.padding.leftDp         = 10f
//        attr.padding.rightDp        = 10f
//        attr.padding.topDp          = 3f
//        attr.padding.bottomDp       = 3f
//
//        attr.margin.rightDp         = 4f
//
//        attr.gravity                = Gravity.CENTER
//
//        val textColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//        attr.color                  = theme.colorOrBlack(textColorTheme)
//
//        attr.font                   = Font.typeface(TextFont.RobotoCondensed,
//                                                    TextFontStyle.Bold,
//                                                    context)
//
//        attr.corners                = Corners(3.0, 3.0, 3.0, 3.0)
//
//        attr.sizeSp                 = 16f
//
//        return attr.textView(context)
//    }
//
//
//    fun secondaryAttributeView(attrString : String) : TextView
//    {
//        val attr                    = TextViewBuilder()
//
//        attr.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
//        attr.height                 = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        attr.text                   = attrString
//
//        attr.padding.leftDp         = 6f
//        attr.padding.rightDp        = 6f
//        attr.padding.topDp          = 3f
//        attr.padding.bottomDp       = 3f
//
//        attr.margin.rightDp         = 4f
//
//
////        val bgColorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
////        attr.backgroundColor        = theme.colorOrBlack(bgColorTheme)
//
//        attr.gravity                = Gravity.CENTER
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
//        attr.color                = theme.colorOrBlack(colorTheme)
//
//        attr.font                 = Font.typeface(TextFont.RobotoCondensed,
//                                                  TextFontStyle.SemiBold,
//                                                  context)
//
//        attr.corners              = Corners(3.0, 3.0, 3.0, 3.0)
//
//        attr.sizeSp               = 16f
//
//        return attr.textView(context)
//    }
//
//
//    private fun nameView() : TextView
//    {
//        val name                = TextViewBuilder()
//
//        name.id                 = R.id.session_summary_name
//
//        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
//        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        name.padding.bottomDp   = 8f
//
//        name.font               = Font.typeface(TextFont.RobotoCondensed,
//                                                TextFontStyle.Bold,
//                                                context)
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_9"))))
//        name.color              = theme.colorOrBlack(colorTheme)
//
//
//        name.sizeSp             = 20f
//
//        return name.textView(context)
//    }
//
//
//    private fun summaryView() : TextView
//    {
//        val summary                 = TextViewBuilder()
//
//        summary.id                  = R.id.session_summary_summary
//
//        summary.width               = LinearLayout.LayoutParams.MATCH_PARENT
//        summary.height              = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        summary.font                = Font.typeface(TextFont.RobotoCondensed,
//                                                TextFontStyle.Regular,
//                                                context)
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_9"))))
//        summary.color              = theme.colorOrBlack(colorTheme)
//
//
//        summary.sizeSp             = 17f
//
//        return summary.textView(context)
//    }
//
//
//    fun summarySpannable(summaryString : String) : SpannableStringBuilder
//    {
//        val builder = SpannableStringBuilder()
//
//        builder.append("$summaryString ")
//
//        builder.append("\u2009\u2026\u2009")
//
//        val sizePx = Util.spToPx(35f, context)
//        val sizeSpan = AbsoluteSizeSpan(sizePx)
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
//        val color              = theme.colorOrBlack(colorTheme)
//        val colorSpan = ForegroundColorSpan(color)
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
//        var bgColor = theme.colorOrBlack(bgColorTheme)
//
//        val bgSpan = RoundedBackgroundHeightSpan(60,
//                                                 15,
//                                                 0.85f,
//                                                 20,
//                                                 color,
//                                                 bgColor,
//                                                 null,
//                                                 IconSize(17, 17),
//                                                 Color.WHITE)
//
//        val startIndex = summaryString.length + 1
//
//        builder.setSpan(sizeSpan, startIndex, startIndex + 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//        builder.setSpan(colorSpan, startIndex, startIndex + 3 , Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//        builder.setSpan(bgSpan, startIndex, startIndex + 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//
//        return builder
//    }
//
//
//    private fun descriptionView() : TextView
//    {
//        val description                 = TextViewBuilder()
//
//        description.id                  = R.id.session_summary_description
//
//        description.width               = LinearLayout.LayoutParams.MATCH_PARENT
//        description.height              = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        description.font                = Font.typeface(TextFont.RobotoCondensed,
//                                                        TextFontStyle.Regular,
//                                                        context)
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_9"))))
//        description.color               = theme.colorOrBlack(colorTheme)
//
//
//        description.sizeSp              = 17f
//
//        description.visibility          = View.GONE
//
//        return description.textView(context)
//    }
//
//
//    private fun footerView() : LinearLayout
//    {
//        val layout = footerViewLayout()
//
//        // MORE INFO
//        layout.addView(this.infoButtonView())
//
//        // OPEN
//        layout.addView(this.selectButtonView())
//
//        return layout
//    }
//
//
//    private fun footerViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.HORIZONTAL
//
//        layout.gravity          = Gravity.END
//
//        layout.margin.topDp     = 16f
//        layout.margin.bottomDp  = 8f
//
//        layout.margin.rightDp   = 2f
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun infoButtonView() : TextView
//    {
//        val button                  = TextViewBuilder()
//
//        button.id                   = R.id.session_summary_info_button
//
//        button.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        button.text                 = context.getString(R.string.more_info).toUpperCase()
//
//
//        button.font                 = Font.typeface(TextFont.RobotoCondensed,
//                                                    TextFontStyle.Bold,
//                                                    context)
//
//        button.margin.leftDp        = 5f
//
//
//        button.padding.leftDp       = 10f
//        button.padding.rightDp      = 10f
//        button.padding.topDp        = 6f
//        button.padding.bottomDp     = 6f
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
//        button.color                = theme.colorOrBlack(colorTheme)
//
//        button.sizeSp               = 15f
//
//        return button.textView(context)
//    }
//
//
//    private fun selectButtonView() : TextView
//    {
//        val button                  = TextViewBuilder()
//
//        button.id                   = R.id.session_summary_open_button
//
//        button.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
//        button.backgroundColor      = theme.colorOrBlack(bgColorTheme)
//
//        button.text                 = context.getString(R.string.select).toUpperCase()
//
//        button.font                 = Font.typeface(TextFont.RobotoCondensed,
//                                                    TextFontStyle.BoldItalic,
//                                                    context)
//
//        button.margin.leftDp        = 5f
//
//        button.corners              = Corners(2.0, 2.0, 2.0, 2.0)
//
//
//        button.padding.leftDp       = 10f
//        button.padding.rightDp      = 10f
//        button.padding.topDp        = 6f
//        button.padding.bottomDp     = 6f
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
//        button.color                = theme.colorOrBlack(colorTheme)
//        button.color                = Color.WHITE
//
//        button.sizeSp               = 15f
//
//        return button.textView(context)
//    }
//
//
//}

