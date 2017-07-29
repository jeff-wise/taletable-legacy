package com.kispoko.tome.activity.official

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.ScrollViewBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.game.GameManifest
import com.kispoko.tome.rts.game.GameSummary
import com.kispoko.tome.rts.theme.ThemeManager
import com.kispoko.tome.util.configureToolbar
import effect.Err
import effect.Val



/**
 * Open Sheet Activity
 */
class OpenSheetGamesActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private val appSettings: AppSettings = AppSettings(ThemeId.Dark)


    // -----------------------------------------------------------------------------------------
    // ACTIVITY
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_open_sheet)

        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        this.configureToolbar(getString(R.string.open_sheet))

        val theme = ThemeManager.theme(this.appSettings.themeId())
        when (theme) {
            is Val -> this.applyTheme(theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        val gameManifest = GameManager.manifest(this)
        if (gameManifest != null)
        {
            val contentLayout = this.findViewById(R.id.content) as LinearLayout
            contentLayout.addView(this.view(gameManifest, this))
        }
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------


    private fun applyTheme(uiColors: UIColors) {
        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun view(gameManifest : GameManifest, context : Context) : ScrollView
    {
        val scrollView = this.scrollView(context)

        scrollView.addView(this.gameListView(gameManifest, context))

        return scrollView
    }


    private fun scrollView(context : Context) : ScrollView
    {
        val scrollView = ScrollViewBuilder()

        scrollView.width            = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height           = LinearLayout.LayoutParams.MATCH_PARENT

        return scrollView.scrollView(context)

    }


    private fun gameListView(gameManifest : GameManifest, context : Context) : LinearLayout
    {
        val layout          = this.gameListViewLayout(context)

        gameManifest.gameSummaries.forEach {
            layout.addView(this.gameView(it, context))
        }

        return layout
    }


    private fun gameListViewLayout(context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun gameView(gameSummary : GameSummary, context : Context) : LinearLayout
    {
        val layout          = this.gameViewLayout(context)

        // Name
        layout.addView(this.gameHeaderView(gameSummary.name, context))

        // Genre
        layout.addView(this.gameGenreView(gameSummary.genre, context))

        // Description
        layout.addView(this.gameDescriptionView(gameSummary.description, context))

        return layout
    }


    private fun gameViewLayout(context : Context) : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.VERTICAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = this.appSettings.color(colorTheme)

        return layout.linearLayout(context)
    }


    private fun gameHeaderView(headerString : String, context : Context) : TextView
    {
        val header          = TextViewBuilder()

        header.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text         = headerString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        header.color        = this.appSettings.color(colorTheme)

        header.font         = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            context)

        header.sizeSp       = 16f

        return header.textView(context)
    }


    private fun gameDescriptionView(descriptionString : String, context : Context) : TextView
    {
        val description         = TextViewBuilder()

        description.width       = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height      = LinearLayout.LayoutParams.WRAP_CONTENT

        description.text        = descriptionString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        description.color        = this.appSettings.color(colorTheme)

        description.font         = Font.typeface(TextFont.FiraSans,
                                                 TextFontStyle.Regular,
                                                 context)

        description.sizeSp       = 14f

        return description.textView(context)
    }


    private fun gameGenreView(genreString : String, context : Context) : TextView
    {
        val genre           = TextViewBuilder()

        genre.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        genre.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        genre.text          = genreString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        genre.color         = this.appSettings.color(colorTheme)

        genre.font          = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            context)

        genre.sizeSp        = 14f

        return genre.textView(context)
    }



    private fun infoRowViewLayout(context : Context) : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.HORIZONTAL

        return layout.linearLayout(context)
    }


    private fun infoView(labelId : Int,
                         descriptionString : String,
                         context : Context) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val label               = TextViewBuilder()
        val info                = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight           = 1f

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.child(label)
              .child(info)

        // (3 A) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = labelId

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color             = this.appSettings.color(labelColorTheme)

        label.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                context)

        label.sizeSp            = 14f

        // (3 B) Info
        // -------------------------------------------------------------------------------------

        info.width       = LinearLayout.LayoutParams.WRAP_CONTENT
        info.height      = LinearLayout.LayoutParams.WRAP_CONTENT

        info.text        = descriptionString

        val infoColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        info.color        = this.appSettings.color(infoColorTheme)

        info.font         = Font.typeface(TextFont.FiraSans,
                                          TextFontStyle.Regular,
                                          context)

        info.sizeSp       = 14f

        return layout.linearLayout(context)
    }

}

