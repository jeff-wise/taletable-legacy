
package com.kispoko.tome.activity.game


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.engine.Engine
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.game.GameManager
import com.kispoko.tome.rts.entity.theme.ThemeManager
import effect.Err
import effect.Val



/**
 * Engine Fragment
 */
class EngineFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var gameId  : GameId? = null
    private var themeId : ThemeId? = null

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(gameId : GameId, themeId : ThemeId) : EngineFragment
        {
            val fragment = EngineFragment()

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

        this.gameId  = arguments?.getSerializable("game_id") as GameId
        this.themeId = arguments?.getSerializable("theme_id") as ThemeId
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {

        val gameId  = this.gameId
        val themeId = this.themeId
        val context = this.context

        return if (gameId != null && themeId != null && context != null)
        {
            val game = GameManager.gameWithId(gameId)
            when (game) {
                is Val -> {
                    this.view(game.value.engine(), themeId, context)
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


    // -----------------------------------------------------------------------------------------
    // INTERNAL
    // -----------------------------------------------------------------------------------------

    private fun view(engine : Engine, themeId : ThemeId, context : Context) : View
    {
        val scrollView = this.scrollView(context)

        val layout     = this.viewLayout(themeId, context)
        scrollView.addView(layout)

        val activity = context as AppCompatActivity

        // Value Sets
        val valueSetsOnClick = View.OnClickListener {
            //val intent = Intent(activity, ValueSetsActivity::class.java)
            // intent.putExtra("game_id", engine.gameId)
            //activity.startActivity(intent)
        }

        layout.addView(this.buttonView(R.string.engine_value_sets,
                                       R.string.engine_value_sets_description,
                                       valueSetsOnClick,
                                       themeId,
                                       context))

        // Mechanics
        val mechanicsOnClick = View.OnClickListener {  }
        layout.addView(this.buttonView(R.string.engine_mechanics,
                                       R.string.engine_mechanics_description,
                                       mechanicsOnClick,
                                       themeId,
                                       context))

        // Functions
        val functionsOnClick = View.OnClickListener {
//            val intent = Intent(activity, FunctionListActivity::class.java)
//            intent.putExtra("game_id", engine.gameId)
//            activity.startActivity(intent)
        }
        layout.addView(this.buttonView(R.string.engine_functions,
                                       R.string.engine_functions_description,
                                       functionsOnClick,
                                       themeId,
                                       context))

        // Programs
        val programsOnClick = View.OnClickListener {  }
        layout.addView(this.buttonView(R.string.engine_programs,
                                       R.string.engine_programs_description,
                                       programsOnClick,
                                       themeId,
                                       context))

        // Summations
        val summationsOnClick = View.OnClickListener {  }
        layout.addView(this.buttonView(R.string.engine_summations,
                                       R.string.engine_summations_description,
                                       summationsOnClick,
                                       themeId,
                                       context))

        // Procedures
        val proceduresOnClick = View.OnClickListener {  }
        layout.addView(this.buttonView(R.string.engine_procedures,
                                       R.string.engine_procedures_description,
                                       proceduresOnClick,
                                       themeId,
                                       context))

        // Variables
        val variablesOnClick = View.OnClickListener {  }
        layout.addView(this.buttonView(R.string.engine_variables,
                                       R.string.engine_variables_description,
                                       variablesOnClick,
                                       themeId,
                                       context))

        return scrollView
    }


    private fun scrollView(context : Context) : ScrollView
    {
        val scrollView = ScrollViewBuilder()

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height       = LinearLayout.LayoutParams.MATCH_PARENT

        return scrollView.scrollView(context)
    }


    private fun viewLayout(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        layout.backgroundColor  = ThemeManager.color(themeId, colorTheme)

        layout.padding.leftDp   = 4f
        layout.padding.rightDp  = 4f
        layout.padding.bottomDp = 4f

        return layout.linearLayout(context)

    }


    private fun buttonView(headerId : Int,
                           descriptionId : Int,
                           onClick : View.OnClickListener,
                           themeId : ThemeId,
                           context : Context) : RelativeLayout
    {
        val layout          = this.buttonViewLayout(themeId, context)

        layout.setOnClickListener(onClick)

        // Header
        layout.addView(this.buttonHeaderView(headerId, themeId, context))

        // Description
        val descriptionView = this.buttonDescriptionView(descriptionId, themeId, context)
        val layoutParams = descriptionView.layoutParams as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.BELOW, R.id.top_element)
        layout.addView(descriptionView)

        // Buttons
        // layout.addView(this.buttonRowView(themeId, context))

        return layout
    }


    private fun buttonViewLayout(themeId : ThemeId, context : Context) : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

//        layout.gravity          = Gravity.CENTER_VERTICAL

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        layout.backgroundColor  = ThemeManager.color(themeId, colorTheme)
        layout.backgroundColor  = Color.WHITE

        layout.margin.topDp     = 8f

        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f
        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        return layout.relativeLayout(context)
    }


    private fun buttonHeaderView(headerId : Int,
                                 themeId : ThemeId,
                                 context : Context) : TextView
    {
        val header              = TextViewBuilder()

        header.id               = R.id.top_element

        header.layoutType       = LayoutType.RELATIVE
        header.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
        header.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        header.textId           = headerId

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        header.color            = ThemeManager.color(themeId, colorTheme)

        header.font             = Font.typeface(TextFont.default(),
                                                TextFontStyle.SemiBold,
                                                context)

        header.sizeSp           = 18f

        return header.textView(context)
    }


    private fun buttonDescriptionView(descriptionId : Int,
                                      themeId : ThemeId,
                                      context : Context) : TextView
    {
        val description             = TextViewBuilder()

        description.layoutType      = LayoutType.RELATIVE
        description.width           = RelativeLayout.LayoutParams.WRAP_CONTENT
        description.height          = RelativeLayout.LayoutParams.WRAP_CONTENT

        description.textId          = descriptionId

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        description.color           = ThemeManager.color(themeId, colorTheme)

        description.font            = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        description.sizeSp          = 16f

        return description.textView(context)
    }


    private fun buttonRowView(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout = this.buttonRowViewLayout(context)

        layout.addView(this.helpButtonView(themeId, context))

        return layout
    }


    private fun buttonRowViewLayout(context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.layoutType       = LayoutType.RELATIVE
        layout.width            = RelativeLayout.LayoutParams.MATCH_PARENT
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.margin.bottomDp  = 8f
        layout.margin.rightDp   = 3f

        layout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

        layout.gravity          = Gravity.END

        return layout.linearLayout(context)
    }


    private fun helpButtonView(themeId : ThemeId, context : Context) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()
        val label               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 17
        icon.heightDp           = 17

        icon.image              = R.drawable.icon_ask_question

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_27")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color              = ThemeManager.color(themeId, iconColorTheme)

        icon.margin.rightDp     = 5f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId           = R.string.help

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_27")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color            = ThemeManager.color(themeId, labelColorTheme)

        label.font             = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        label.sizeSp           = 16f

        return layout.linearLayout(context)
    }

}
