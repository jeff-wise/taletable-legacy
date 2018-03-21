
package com.kispoko.tome.activity.official


import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.kispoko.tome.activity.official.fifth_ed_srd.FifthEdSRDCharactersFragment
import com.kispoko.tome.activity.official.fifth_ed_srd.OpenFifthEdSRDSessionActivity
import com.kispoko.tome.activity.official.heroes.OpenHeroesSessionActivity
import com.kispoko.tome.activity.official.magic_carnival.OpenMagicCarnivalSessionActivity
import com.kispoko.tome.activity.official.pathfinder_one_srd.OpenPathfinderOneSRDSessionActivity
import com.kispoko.tome.activity.official.starfinder_srd.OpenStarfinderSRDSessionActivity
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.theme.official.officialThemeLight
import com.kispoko.tome.official.GameManifest
import com.kispoko.tome.official.GameSummary
import com.kispoko.tome.rts.official.OfficialManager
import com.kispoko.tome.util.configureToolbar



/**
 * Official Games Activity
 */
class OfficialGamesListActivity : AppCompatActivity() {

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private val appSettings: AppSettings = AppSettings(ThemeId.Light)


    // -----------------------------------------------------------------------------------------
    // ACTIVITY
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_official_games)

        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        this.configureToolbar(getString(R.string.choose_game))

        this.applyTheme(officialThemeLight)
//        when (theme) {
//            is Val -> this.applyTheme(theme.value.uiColors())
//            is Err -> ApplicationLog.error(theme.error)
//        }

        val gameManifest = OfficialManager.gameManifest(this)
        if (gameManifest != null) {
            val contentLayout = this.findViewById(R.id.content) as LinearLayout
            val gamesListUI = GamesListUI(gameManifest, officialThemeLight, this)
            contentLayout.addView(gamesListUI.view())
        }
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------


    private fun applyTheme(theme: Theme) {
        val uiColors = theme.uiColors()

        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = this.window

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = theme.colorOrBlack(uiColors.toolbarBackgroundColorId())
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        // Toolbar > Background
        toolbar.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById(R.id.toolbar_back_button) as ImageButton
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById(R.id.toolbar_options_button) as ImageButton
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById(R.id.toolbar_title) as TextView
        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))
    }


}



class GamesListUI(val gameManifest: GameManifest,
                  val theme : Theme,
                  val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    val activity = context as AppCompatActivity


    // -----------------------------------------------------------------------------------------
    // UI
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
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

        layout.padding.leftDp   = 6f
        layout.padding.rightDp  = 6f

        return layout.linearLayout(context)
    }


    private fun gameView(gameSummary : GameSummary) : LinearLayout
    {
        val layout          = this.gameViewLayout(gameSummary.gameId)

        // Name
        layout.addView(this.gameHeaderView(gameSummary.name))

        // Genre
//        layout.addView(this.gameGenreView(gameSummary.genre, context))

        // Description
        layout.addView(this.gameDescriptionView(gameSummary.description))

        // Info
        layout.addView(this.infoRowView(gameSummary))

        return layout
    }


    private fun gameViewLayout(gameId : GameId) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor  = Color.WHITE

        layout.margin.topDp     = 4f

        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f
        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        layout.onClick          = View.OnClickListener {
            when (gameId.value)
            {
                "magic_of_heroes" -> {
                    val intent = Intent(activity, OpenHeroesSessionActivity::class.java)
                    intent.putExtra("game_id", gameId)
                    activity.startActivity(intent)
                }
                "5e_srd" -> {
                    val intent = Intent(activity, OpenFifthEdSRDSessionActivity::class.java)
                    intent.putExtra("game_id", gameId)
                    activity.startActivity(intent)
                }
                "pathfinder_srd" -> {
                    val intent = Intent(activity, OpenPathfinderOneSRDSessionActivity::class.java)
                    intent.putExtra("game_id", gameId)
                    activity.startActivity(intent)
                }
                "starfinder_srd" -> {
                    val intent = Intent(activity, OpenStarfinderSRDSessionActivity::class.java)
                    intent.putExtra("game_id", gameId)
                    activity.startActivity(intent)
                }
                "magic_carnival" -> {
                    val intent = Intent(activity, OpenMagicCarnivalSessionActivity::class.java)
                    intent.putExtra("game_id", gameId)
                    activity.startActivity(intent)
                }
            }
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        header.color        = theme.colorOrBlack(colorTheme)

        header.font         = Font.typeface(TextFont.default(),
                                            TextFontStyle.Bold,
                                            context)

        header.sizeSp       = 19f

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        description.color        = theme.colorOrBlack(colorTheme)

        description.font         = Font.typeface(TextFont.default(),
                                                 TextFontStyle.Regular,
                                                 context)

        description.sizeSp       = 17f

        description.margin.topDp = 4f

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

        genre.font          = Font.typeface(TextFont.default(),
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
        when (genre)
        {
            "Fantasy" -> {
                layout.addView(this.infoView(R.drawable.icon_castle, genre, usersColorTheme))
            }

            "Sci-Fi" -> {
                layout.addView(this.infoView(R.drawable.icon_planet, genre, usersColorTheme))
            }
            "Mixed-Bag" -> {
                layout.addView(this.infoView(R.drawable.icon_magic_lamp, genre, usersColorTheme))
            }
        }

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


        info.font         = Font.typeface(TextFont.default(),
                                          TextFontStyle.Regular,
                                          context)

        info.sizeSp       = 18f

        return layout.linearLayout(context)
    }

}

