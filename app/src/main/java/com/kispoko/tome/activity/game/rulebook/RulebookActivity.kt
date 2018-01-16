
package com.kispoko.tome.activity.game.rulebook


import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.Menu
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import br.tiagohm.markdownview.css.InternalStyleSheet
import com.kispoko.tome.R
import com.kispoko.tome.R.string.name
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.Rulebook
import com.kispoko.tome.model.game.RulebookId
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.theme.ThemeManager
import com.kispoko.tome.util.Util
import com.kispoko.tome.util.configureToolbar
import effect.Err
import effect.Val



/**
 * Rulebook Activity
 */
class RulebookActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private val appSettings : AppSettings = AppSettings(ThemeId.Light)

    private var gameId     : GameId?     = null
    private var rulebookId : RulebookId? = null


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_rulebook)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("game_id"))
            this.gameId = this.intent.getSerializableExtra("game_id") as GameId

        if (this.intent.hasExtra("rulebook_id"))
            this.rulebookId = this.intent.getSerializableExtra("rulebook_id") as RulebookId

        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        // > Toolbar
        val rulebookId = this.rulebookId
        val gameId = this.gameId
        if (gameId != null && rulebookId != null) {
            val rulebook = GameManager.rulebook(gameId, rulebookId)
            when (rulebook) {
                is Val -> this.configureToolbar(rulebook.value.title().value)
                is Err -> ApplicationLog.error(rulebook.error)
            }
        }

        // > Theme
        val theme = ThemeManager.theme(this.appSettings.themeId())
        when (theme) {
            is Val -> this.applyTheme(theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        // Floating Action Button
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
        val contentView = this.findViewById(R.id.book_content_scroll_view) as ScrollView

        val rulebookId = this.rulebookId
        val gameId = this.gameId
        if (gameId != null && rulebookId != null) {
            val rulebook = GameManager.rulebook(gameId, rulebookId)
            when (rulebook) {
                is Val -> {
                    val viewBuilder = BookViewBuilder(this.appSettings.themeId, rulebook.value, this)
                    contentView.addView(viewBuilder.view())
                }
                is Err -> ApplicationLog.error(rulebook.error)
            }
        }
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

        val menuRightButton = this.findViewById(R.id.toolbar_search_button) as ImageButton
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById(R.id.toolbar_title) as TextView
        titleView.setTextColor(this.appSettings.color(uiColors.toolbarTitleColorId()))

    }

}


class BookViewBuilder(val themeId : ThemeId,
                      val rulebook : Rulebook,
                      val context : Context)
{


    fun view() : LinearLayout
    {
        val layout      = this.viewLayout()

        // Authors
        layout.addView(this.authorsView())

        // Abstract
        layout.addView(this.headerView("Overview"))
        layout.addView(this.abstractView())

        // Introduction
        layout.addView(this.headerView("Introduction"))
        layout.addView(this.introductionView())

        // Chapters
        layout.addView(this.headerView("Chapters"))
        layout.addView(this.chapterListView())

        return layout
    }


    fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)

        return layout.linearLayout(context)
    }


    private fun headerView(headerString : String) : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.font             = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        header.margin.topDp     = 8f
        header.margin.leftDp    = 10f

        header.text             = headerString

        header.sizeSp           = 20f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        header.color  = ThemeManager.color(themeId, colorTheme)

        return header.textView(context)
    }


    private fun authorsView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val authors             = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundColor  = Color.WHITE

        layout.margin.topDp     = 12f
        layout.margin.bottomDp  = 12f

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f

        layout.child(authors)

        // (3) Text
        // -------------------------------------------------------------------------------------

        authors.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        authors.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        authors.font            = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        authors.sizeSp             = 20f

        val descriptionColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        val descriptionColor  = ThemeManager.color(themeId, descriptionColorTheme) ?: Color.BLACK

        val authorColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("purple"))))
        val authorColor  = ThemeManager.color(themeId, authorColorTheme) ?: Color.BLACK

        val builder = SpannableStringBuilder()

        val typeface = Font.typeface(TextFont.default(), TextFontStyle.default(), context)
        val typefaceSpan = CustomTypefaceSpan(typeface)

        builder.append("Written by ")


        val descColorSpan = ForegroundColorSpan(descriptionColor)
        builder.setSpan(descColorSpan, 0, 10, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        val firstAuthor = rulebook.authors().firstOrNull()
        if (firstAuthor != null) {
            val authorName = firstAuthor.authorName.value
            builder.append(authorName)

            val authorColorSpan = ForegroundColorSpan(authorColor)
            builder.setSpan(authorColorSpan, 11, 11 + authorName.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }


        val totalLength = builder.length
        builder.setSpan(typefaceSpan, 0, totalLength, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)


        authors.textSpan            = builder

        return layout.linearLayout(context)
    }


    private fun abstractView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val text                = MarkdownViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundColor  = Color.WHITE

        layout.margin.topDp     = 8f
        layout.margin.bottomDp  = 12f

        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f

        layout.child(text)

        // (3) Text
        // -------------------------------------------------------------------------------------

        text.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        text.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        text.markdownText       = rulebook.abstract().value
        text.stylesheet         = StyleSheet.lightTheme

        return layout.linearLayout(context)
    }


    private fun introductionView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val text                = MarkdownViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundColor  = Color.WHITE

        layout.margin.topDp     = 8f
        layout.margin.bottomDp  = 12f

        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f

        layout.child(text)

        // (3) Text
        // -------------------------------------------------------------------------------------

        text.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        text.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        text.markdownText       = rulebook.introduction().value
        text.stylesheet         = StyleSheet.lightTheme

        return layout.linearLayout(context)
    }


    private fun chapterListView() : LinearLayout
    {
        val layout      = this.chapterListViewLayout()

        rulebook.chapters().forEachIndexed { index, rulebookChapter ->
            layout.addView(this.chapterButtonView(index, rulebookChapter.title().value))
        }

        return layout
    }


    private fun chapterListViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.leftDp    = 10f
        layout.margin.rightDp   = 10f
        layout.margin.bottomDp  = 20f
        layout.margin.topDp     = 4f

        return layout.linearLayout(context)
    }


    private fun chapterButtonView(chapterIndex : Int, titleString : String) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val index           = TextViewBuilder()
        val divider         = LinearLayoutBuilder()
        val title           = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 52

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.margin.topDp     = 8f

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor  = Color.WHITE

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        layout.child(index)
              .child(divider)
              .child(title)

        // (3 A) Index
        // -------------------------------------------------------------------------------------

        index.widthDp           = 26
        index.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        index.text              = (chapterIndex + 1).toString()

        index.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        index.gravity           = Gravity.CENTER_HORIZONTAL

        index.sizeSp            = 22f

        index.margin.leftDp     = 15f
        index.margin.rightDp    = 15f

        val indexColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        index.color             = ThemeManager.color(themeId, indexColorTheme)

        // (3 B) Divider
        // -------------------------------------------------------------------------------------

        divider.widthDp         = 1
        divider.height          = LinearLayout.LayoutParams.MATCH_PARENT

        val dividerColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        divider.backgroundColor = ThemeManager.color(themeId, dividerColorTheme)

        // (3 C) Title
        // -------------------------------------------------------------------------------------

        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        title.text              = titleString

        title.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        title.sizeSp            = 20f

        val titleColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        title.color             = ThemeManager.color(themeId, titleColorTheme)

        title.margin.leftDp     = 12f

        return layout.linearLayout(context)
    }

}


object StyleSheet
{

    val lightTheme = InternalStyleSheet()

    init {
        val pMarginPx    = Util.dpToPixel(8f)
        val pTextSizePx  = Util.dpToPixel(4f)
        val h3TextSizePx = Util.dpToPixel(4.4f)
//        val pLineHeight = Util.dpToPixel(5f)

        lightTheme.addFontFace("Cabin-Regular", null, null, null, "url('file:///android_asset/fonts/Cabin-Regular.ttf')")
        lightTheme.addFontFace("Cabin-Medium", null, null, null, "url('file:///android_asset/fonts/Cabin-Medium.ttf')")
        lightTheme.addRule("body", "color: #333333", "font-family: Cabin-Regular", "padding: 0", "margin: 0")
        lightTheme.addRule("p", "margin: 1em 0", "font-size: ${pTextSizePx}px", "line-height: 1.5em", "padding: 0")
        lightTheme.addRule("h3", "color: #2D2D2D", "font-weight: 400", "font-size: ${h3TextSizePx}px", "font-family: Cabin-Medium")
        lightTheme.addRule("b", "color: #333333")
    }

}



