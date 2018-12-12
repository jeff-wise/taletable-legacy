
package com.taletable.android.activity.session


import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.*
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.model.theme.official.officialThemeLight
import com.taletable.android.official.GameManifest
import com.taletable.android.official.GameSummary
import com.taletable.android.official.gameManifest
import com.taletable.android.router.Router
import com.taletable.android.util.configureToolbar
import java.io.Serializable



/**
 * Games List Activity
 */
class GamesListActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var gameAction : GameAction? = null


    // -----------------------------------------------------------------------------------------
    // ACTIVITY
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_game_list)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("game_action"))
            this.gameAction = this.intent.getSerializableExtra("game_action") as GameAction

        // (3) Initialize UI
        // -------------------------------------------------------------------------------------

        this.configureToolbar(getString(R.string.games),
                              TextFont.RobotoCondensed,
                              TextFontStyle.Regular,
                              19f)

        this.applyTheme(officialAppThemeLight)

        val gameManifest = gameManifest(this)
        val gameAction = this.gameAction
        if (gameManifest != null && gameAction != null)
        {
            val contentLayout = this.findViewById<LinearLayout>(R.id.content)
            val gamesListUI = GamesListUI(gameManifest, gameAction, officialThemeLight, this)
            contentLayout.addView(gamesListUI.view())
        }
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

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

            val statusBarColorTheme = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
            window.statusBarColor = theme.colorOrBlack(statusBarColorTheme)
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

        // Toolbar > Background
        toolbar.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById<ImageButton>(R.id.toolbar_back_button)
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById<ImageButton>(R.id.toolbar_options_button)
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById<TextView>(R.id.toolbar_title)
        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))
    }

}



class GamesListUI(val gameManifest : GameManifest,
                  val gameAction : GameAction,
                  val theme : Theme,
                  val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val activity = context as AppCompatActivity


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : ScrollView
    {
        val scrollView = this.scrollView()

        scrollView.addView(this.gameListView())

        return scrollView
    }


    private fun scrollView() : ScrollView
    {
        val scrollView = ScrollViewBuilder()

        scrollView.width            = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height           = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_9"))))
        scrollView.backgroundColor  = theme.colorOrBlack(colorTheme)

        return scrollView.scrollView(context)

    }


    private fun gameListView() : LinearLayout
    {
        val layout          = this.gameListViewLayout()

        gameManifest.gameSummaries.forEach {
            layout.addView(this.gameView(it))
        }

        return layout
    }


    private fun gameListViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun gameView(gameSummary : GameSummary) : LinearLayout
    {
        val layout          = this.gameViewLayout(gameSummary)

        // Name
        layout.addView(this.gameHeaderView(gameSummary.name))

        // Genre
//        layout.addView(this.gameGenreView(gameSummary.genre, context))

        // Description
        layout.addView(this.gameDescriptionView(gameSummary.description))

        // Info
//        layout.addView(this.infoRowView(gameSummary))

        return layout
    }


    private fun gameViewLayout(gameSummary : GameSummary) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE

        layout.margin.topDp     = 1f

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f
        layout.padding.leftDp   = 12f
        layout.padding.rightDp  = 12f

        layout.onClick          = View.OnClickListener {
            Router.send(NewSessionMessageGame(gameSummary.gameId))
            activity.finish()
        }

        return layout.linearLayout(context)
    }


    private fun gameHeaderView(headerString : String) : TextView
    {
        val header          = TextViewBuilder()

        header.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text         = headerString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
        header.color        = theme.colorOrBlack(colorTheme)

        header.font         = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Bold,
                                            context)

        header.sizeSp       = 18f

        return header.textView(context)
    }


    private fun gameDescriptionView(descriptionString : String) : TextView
    {
        val description         = TextViewBuilder()

        description.width       = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height      = LinearLayout.LayoutParams.WRAP_CONTENT

        description.text        = descriptionString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_18")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        description.color        = theme.colorOrBlack(colorTheme)

        description.font         = Font.typeface(TextFont.RobotoCondensed,
                                                 TextFontStyle.Regular,
                                                 context)

        description.sizeSp       = 17f

        description.margin.topDp = 2f

        return description.textView(context)
    }


    private fun gameGenreView(genreString : String) : TextView
    {
        val genre           = TextViewBuilder()

        genre.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        genre.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        genre.text          = genreString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        genre.color         = theme.colorOrBlack(colorTheme)

        genre.font          = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Regular,
                                            context)

        genre.sizeSp        = 18f

//        genre.margin.topDp  = 4f

        return genre.textView(context)
    }


    private fun infoRowView(gameSummary: GameSummary) : LinearLayout
    {
        val layout = this.infoRowViewLayout()

        val usersColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green_70"))))

        val likesColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("red_70"))))

//        layout.addView(this.gameGenreView(gameSummary.genre))

        val genre = gameSummary.genre
//        when (genre)
//        {
//            "Fantasy" -> {
//                layout.addView(this.infoView(R.drawable.icon_castle, genre, usersColorTheme))
//            }
//
//            "Sci-Fi" -> {
//                layout.addView(this.infoView(R.drawable.icon_planet, genre, usersColorTheme))
//            }
//            "Mixed-Bag" -> {
//                layout.addView(this.infoView(R.drawable.icon_magic_lamp, genre, usersColorTheme))
//            }
//        }

        // Players
        layout.addView(this.infoView(R.drawable.icon_account, gameSummary.players.toString(), usersColorTheme))

        // Likes
        layout.addView(this.infoView(R.drawable.icon_heart, gameSummary.likes.toString(), likesColorTheme))

        return layout
    }


    private fun infoRowViewLayout() : LinearLayout
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
                         colorTheme : ColorTheme) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()
        val info                = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.margin.rightDp   = 15f

        layout.child(icon)
              .child(info)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 18
        icon.heightDp           = 18

        icon.image              = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        icon.color              = theme.colorOrBlack(iconColorTheme)
//        icon.color              = theme.colorOrBlack(colorTheme)

        icon.margin.rightDp     = 4f

        // (3 B) Info
        // -------------------------------------------------------------------------------------

        info.width       = LinearLayout.LayoutParams.WRAP_CONTENT
        info.height      = LinearLayout.LayoutParams.WRAP_CONTENT

        info.text        = descriptionString

        val infoColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        info.color        = theme.colorOrBlack(infoColorTheme)
//        info.color        = theme.colorOrBlack(colorTheme)


        info.font         = Font.typeface(TextFont.RobotoCondensed,
                                          TextFontStyle.Regular,
                                          context)

        info.sizeSp       = 18f

        return layout.linearLayout(context)
    }

}


sealed class GameAction : Serializable

object GameActionNewSession : GameAction()

object GameActionLoadSession : GameAction()



