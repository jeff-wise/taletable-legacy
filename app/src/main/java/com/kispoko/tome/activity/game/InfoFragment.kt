
package com.kispoko.tome.activity.game


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.Game
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.theme.ThemeManager



class InfoFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var game : Game? = null
    private var themeId : ThemeId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(game : Game, themeId : ThemeId) : InfoFragment
        {
            val fragment = InfoFragment()

            val args = Bundle()
            args.putSerializable("game", game)
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
            this.game = arguments.getSerializable("game") as Game
            this.themeId = arguments.getSerializable("theme_id") as ThemeId
        }
    }


    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View?
    {
        val game = this.game
        val themeId = this.themeId
        if (game != null && themeId != null)
        {
            val viewBuilder = GameInfoViewBuilder(game, themeId, context)
            return viewBuilder.view()
        }
        else
        {
            return null
        }
    }

}



class GameInfoViewBuilder(val game : Game,
                          val themeId : ThemeId,
                          val context : Context)
{


    fun view() : ScrollView
    {
        val scrollView = this.scrollView()

        val buttonLayout = this.buttonLayout()
        scrollView.addView(buttonLayout)

        // Buttons
        buttonLayout.addView(this.buttonView(R.drawable.icon_document,
                                             R.string.game_info_description_header,
                                             R.string.game_info_description_description))

        buttonLayout.addView(this.buttonView(R.drawable.icon_book,
                                             R.string.game_info_rulebook_header,
                                             R.string.game_info_rulebook_description))

        buttonLayout.addView(this.buttonView(R.drawable.icon_stats,
                                             R.string.game_info_stats_header,
                                             R.string.game_info_stats_description))

        return scrollView
    }


    private fun scrollView() : ScrollView
    {
        val scrollView          = ScrollViewBuilder()

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height        = LinearLayout.LayoutParams.MATCH_PARENT

        return scrollView.scrollView(context)
    }


    private fun buttonLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)

        return layout.linearLayout(context)
    }


    private fun buttonView(iconId : Int,
                           headerStringId : Int,
                           descriptionStringId : Int) : LinearLayout
    {
        val layout = this.buttonViewLayout()

        // Left Pane
        layout.addView(this.leftPaneView(iconId))

        // Right Pane
        layout.addView(this.rightPaneView(headerStringId, descriptionStringId))

        return layout
    }


    private fun buttonViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        layout.margin.topDp     = 8f
        layout.margin.leftDp    = 8f
        layout.margin.rightDp   = 8f

        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f
        layout.padding.rightDp  = 8f

        return layout.linearLayout(context)
    }


    private fun leftPaneView(iconId : Int) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.widthDp          = 0
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight           = 1f

        layout.gravity          = Gravity.CENTER

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 36
        icon.heightDp           = 36

        icon.image              = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color              = ThemeManager.color(themeId, iconColorTheme)

        return layout.linearLayout(context)
    }


    private fun rightPaneView(headerStringId : Int,
                              descriptionStringId : Int) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val header              = TextViewBuilder()
        val description         = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.widthDp          = 0
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight           = 4.5f

        layout.orientation      = LinearLayout.VERTICAL

        layout.child(header)
             .child(description)

        // (3 A) Header
        // -------------------------------------------------------------------------------------

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.textId           = headerStringId

        val headerColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        header.color            = ThemeManager.color(themeId, headerColorTheme)

        header.font             = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                context)

        header.sizeSp           = 17f

        // (3 A) Description
        // -------------------------------------------------------------------------------------

        description.width       = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height      = LinearLayout.LayoutParams.WRAP_CONTENT

        description.textId      = descriptionStringId

        val descriptionColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        description.color       = ThemeManager.color(themeId, descriptionColorTheme)

        description.font        = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                context)

        description.sizeSp      = 13f

        return layout.linearLayout(context)
    }


}

