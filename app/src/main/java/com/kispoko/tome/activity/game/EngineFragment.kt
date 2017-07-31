
package com.kispoko.tome.activity.game


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.Engine
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.theme.ThemeManager



/**
 * Engine Fragment
 */
class EngineFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var engine  : Engine? = null
    private var themeId : ThemeId? = null

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(engine : Engine, themeId : ThemeId) : EngineFragment
        {
            val fragment = EngineFragment()

            val args = Bundle()
            args.putSerializable("engine", engine)
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
            this.engine  = arguments.getSerializable("engine") as Engine
            this.themeId = arguments.getSerializable("theme_id") as ThemeId
        }
    }


    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {

        val engine  = this.engine
        val themeId = this.themeId

        if (engine != null && themeId != null)
            return this.view(engine, themeId, context)
        else
            return null
    }


    // -----------------------------------------------------------------------------------------
    // INTERNAL
    // -----------------------------------------------------------------------------------------

    private fun view(engine : Engine, themeId : ThemeId, context : Context) : View
    {
        val layout          = this.viewLayout(themeId, context)

        // Value Sets
        layout.addView(this.buttonView(R.string.engine_value_sets,
                                       R.string.engine_value_sets_description,
                                       themeId,
                                       context))

        // Mechanics
        layout.addView(this.buttonView(R.string.engine_mechanics,
                                       R.string.engine_mechanics_description,
                                       themeId,
                                       context))

        // Functions
        layout.addView(this.buttonView(R.string.engine_functions,
                                       R.string.engine_functions_description,
                                       themeId,
                                       context))

        // Programs
        layout.addView(this.buttonView(R.string.engine_programs,
                                       R.string.engine_programs_description,
                                       themeId,
                                       context))

        return layout
    }


    private fun viewLayout(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = ThemeManager.color(themeId, colorTheme)

        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f
        layout.padding.bottomDp = 8f

        return layout.linearLayout(context)

    }


    private fun buttonView(headerId : Int,
                           descriptionId : Int,
                           themeId : ThemeId,
                           context : Context) : RelativeLayout
    {
        val layout          = this.buttonViewLayout(themeId, context)

        // Header
        layout.addView(this.buttonHeaderView(headerId, themeId, context))

        // Description
        val descriptionView = this.buttonDescriptionView(descriptionId, themeId, context)
        val layoutParams = descriptionView.layoutParams as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.BELOW, R.id.top_element)
        layout.addView(descriptionView)

        // Buttons
        layout.addView(this.buttonRowView(themeId, context))

        return layout
    }


    private fun buttonViewLayout(themeId : ThemeId, context : Context) : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = 0
        layout.weight           = 1f

        layout.orientation      = LinearLayout.VERTICAL

//        layout.gravity          = Gravity.CENTER_VERTICAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = ThemeManager.color(themeId, colorTheme)

        layout.margin.topDp     = 8f

        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f
        layout.padding.topDp    = 8f

        layout.corners          = Corners(TopLeftCornerRadius(1f),
                                          TopRightCornerRadius(1f),
                                          BottomRightCornerRadius(1f),
                                          BottomLeftCornerRadius(1f))

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        header.color            = ThemeManager.color(themeId, colorTheme)

        header.font             = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    context)

        header.sizeSp           = 17f

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        description.color           = ThemeManager.color(themeId, colorTheme)

        description.font            = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    context)

        description.sizeSp          = 14f

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

        label.font             = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    context)

        label.sizeSp           = 16f

        return layout.linearLayout(context)
    }

}
