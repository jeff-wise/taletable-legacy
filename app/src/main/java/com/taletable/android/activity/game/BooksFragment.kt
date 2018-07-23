
package com.taletable.android.activity.game


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import com.taletable.android.app.ApplicationLog
import com.taletable.android.lib.ui.*
import com.taletable.android.model.game.Game
import com.taletable.android.model.book.Book
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.game.GameManager
import com.taletable.android.rts.entity.theme.ThemeManager
import effect.Err
import effect.Val



class BooksFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var gameId : EntityId? = null
    private var themeId : ThemeId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(gameId : EntityId, themeId : ThemeId) : BooksFragment
        {
            val fragment = BooksFragment()

            val args = Bundle()
            args.putSerializable("game_id", gameId)
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

        this.gameId = arguments?.getSerializable("game_id") as EntityId
        this.themeId = arguments?.getSerializable("theme_id") as ThemeId
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) : View?
    {
        val gameId = this.gameId
        val themeId = this.themeId
        val context = this.context

        return if (gameId != null && themeId != null && context != null)
        {
            val game = GameManager.gameWithId(gameId)
            when (game) {
                is Val -> {
                    Log.d("***BOOKS FRAGMENT", "found game")
                    val viewBuilder = GameInfoViewBuilder(game.value, themeId, context)
                    viewBuilder.view()
                }
                is Err -> {
                    ApplicationLog.error(game.error)
                    null
                }
            }
        }
        else
        {
            null
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

        Log.d("***BOOKS FRAGMENT", "creating view")

        val listLayout = this.buttonListViewLayout()

        scrollView.addView(listLayout)

//        game.rulebooks().forEach {
//            Log.d("***BOOKS FRAGMENT", "adding rulebook")
//            listLayout.addView(bookButtonView(it))
//        }

        return scrollView
    }


    private fun scrollView() : ScrollView
    {
        val scrollView          = ScrollViewBuilder()

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height        = LinearLayout.LayoutParams.MATCH_PARENT

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        scrollView.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)

        return scrollView.scrollView(context)
    }


    private fun buttonListViewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation  = LinearLayout.VERTICAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        layout.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)

        return layout.linearLayout(context)
    }



    private fun bookButtonView(rulebook : Book) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val title               = TextViewBuilder()
        val summary             = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.leftDp    = 4f
        layout.margin.rightDp   = 4f
        layout.margin.topDp     = 6f

        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f
        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        layout.backgroundColor  = Color.WHITE

        layout.onClick          = View.OnClickListener {
//            val activity = context as AppCompatActivity
//            val intent = Intent(activity, RulebookActivity::class.java)
//            intent.putExtra("game_id", game.gameId)
//            intent.putExtra("rulebook_id", rulebook.bookId)
//            activity.startActivity(intent)
        }

        layout.child(title)
              .child(summary)

        // (3 A) Title
        // -------------------------------------------------------------------------------------

        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

//        title.text              = rulebook.title().value

        val titleColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        title.color             = ThemeManager.color(themeId, titleColorTheme)

        title.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.SemiBold,
                                                context)

        title.sizeSp            = 19f

        // (3 B) Summary
        // -------------------------------------------------------------------------------------

        summary.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT

//        summary.text            = rulebook.abstract().value

        val summaryColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        summary.color           = ThemeManager.color(themeId, summaryColorTheme)

        summary.font            = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        summary.sizeSp          = 16f

        return layout.linearLayout(context)
    }


}

