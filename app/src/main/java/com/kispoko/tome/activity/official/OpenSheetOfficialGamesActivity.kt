
package com.kispoko.tome.activity.official


import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.official.sheets.OpenSheetOfficialSheetsActivity
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.official.GameManifest
import com.kispoko.tome.official.GameSummary
import com.kispoko.tome.rts.official.OfficialManager
import com.kispoko.tome.rts.theme.ThemeManager
import com.kispoko.tome.util.configureToolbar
import effect.Err
import effect.Val



/**
 * Open Sheet Activity
 */
class OpenSheetOfficialGamesActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private val appSettings: AppSettings = AppSettings(ThemeId.Light)


    // -----------------------------------------------------------------------------------------
    // ACTIVITY
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_open_sheet_official_games)

        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        this.configureToolbar(getString(R.string.open_official_sheet))

        val theme = ThemeManager.theme(this.appSettings.themeId())
        when (theme) {
            is Val -> this.applyTheme(theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        val gameManifest = OfficialManager.gameManifest(this)
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

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        scrollView.backgroundColor  = this.appSettings.color(colorTheme)

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

        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        return layout.linearLayout(context)
    }


    private fun gameView(gameSummary : GameSummary, context : Context) : LinearLayout
    {
        val layout          = this.gameViewLayout(gameSummary.gameId, context)

        // Name
        layout.addView(this.gameHeaderView(gameSummary.name, context))

        // Genre
        layout.addView(this.gameGenreView(gameSummary.genre, context))

        // Description
        layout.addView(this.gameDescriptionView(gameSummary.description, context))

        // Info
        layout.addView(this.infoRowView(gameSummary.players, gameSummary.likes, context))

        return layout
    }


    private fun gameViewLayout(gameId : GameId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor  = this.appSettings.color(colorTheme)

        layout.margin.topDp     = 8f

        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f
        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        val activity = this
        layout.onClick          = View.OnClickListener {
            val intent = Intent(activity, OpenSheetOfficialSheetsActivity::class.java)
            intent.putExtra("game_id", gameId)
            activity.startActivity(intent)
        }

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
        header.color        = this.appSettings.color(colorTheme)

        header.font         = Font.typeface(TextFont.default(),
                                            TextFontStyle.Medium,
                                            context)

        header.sizeSp       = 18f

        return header.textView(context)
    }


    private fun gameDescriptionView(descriptionString : String, context : Context) : TextView
    {
        val description         = TextViewBuilder()

        description.width       = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height      = LinearLayout.LayoutParams.WRAP_CONTENT

        description.text        = descriptionString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_18")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        description.color        = this.appSettings.color(colorTheme)

        description.font         = Font.typeface(TextFont.default(),
                                                 TextFontStyle.Regular,
                                                 context)

        description.sizeSp       = 15f

        description.margin.topDp = 4f

        return description.textView(context)
    }


    private fun gameGenreView(genreString : String, context : Context) : TextView
    {
        val genre           = TextViewBuilder()

        genre.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        genre.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        genre.text          = genreString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        genre.color         = this.appSettings.color(colorTheme)

        genre.font          = Font.typeface(TextFont.default(),
                                            TextFontStyle.Regular,
                                            context)

        genre.sizeSp        = 14f

        genre.margin.topDp  = 4f

        return genre.textView(context)
    }


    private fun infoRowView(players : Int,
                            likes : Int,
                            context : Context) : LinearLayout
    {
        val layout = this.infoRowViewLayout(context)

        // Players
        layout.addView(this.infoView(R.drawable.icon_account, players.toString(), context))

        // Likes
        layout.addView(this.infoView(R.drawable.icon_heart, likes.toString(), context))

        return layout
    }


    private fun infoRowViewLayout(context : Context) : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.HORIZONTAL

        layout.margin.topDp = 8f

        return layout.linearLayout(context)
    }


    private fun infoView(iconId : Int,
                         descriptionString : String,
                         context : Context) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()
        val info                = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight           = 1f

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.child(icon)
              .child(info)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 16
        icon.heightDp           = 16

        icon.image              = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        icon.color              = this.appSettings.color(iconColorTheme)

        icon.margin.rightDp     = 5f

        // (3 B) Info
        // -------------------------------------------------------------------------------------

        info.width       = LinearLayout.LayoutParams.WRAP_CONTENT
        info.height      = LinearLayout.LayoutParams.WRAP_CONTENT

        info.text        = descriptionString

        val infoColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        info.color        = this.appSettings.color(infoColorTheme)

        info.font         = Font.typeface(TextFont.default(),
                                          TextFontStyle.Regular,
                                          context)

        info.sizeSp       = 15f

        return layout.linearLayout(context)
    }

}

