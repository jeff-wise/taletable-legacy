
package com.kispoko.tome.activity.entity.engine.valueset


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.LinearLayout
import com.kispoko.tome.R
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.value.ValueReference
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.theme.ThemeManager



/**
 * Value Action Dialog
 */
class ValueActionDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var valueReference : ValueReference? = null
    private var gameId : GameId? = null

    private val appSettings : AppSettings = AppSettings(ThemeId.Dark)


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(valueReference : ValueReference,
                        gameId : GameId) : ValueActionDialog
        {
            val dialog = ValueActionDialog()

            val args = Bundle()
            args.putSerializable("value_reference", valueReference)
            args.putSerializable("game_id", gameId)
            dialog.arguments = args

            return dialog
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreateDialog(savedInstanceState : Bundle?) : Dialog
    {
        // (1) Read State
        // -------------------------------------------------------------------------------------

        this.valueReference = arguments.getSerializable("value_reference") as ValueReference
        this.gameId         = arguments.getSerializable("game_id") as GameId

        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val dialogLayout = this.dialogLayout()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setContentView(dialogLayout)

        val width  = context.resources.getDimension(R.dimen.action_dialog_width)
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        dialog.window.setLayout(width.toInt(), height)

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val valueReference = this.valueReference
        val gameId = this.gameId
        if (valueReference != null && gameId != null)
        {
            val viewBuilder = ValueActionViewBuilder(valueReference,
                                                     gameId,
                                                     this.appSettings.themeId(),
                                                     context,
                                                     this)
            return viewBuilder.view()
        }
        else
        {
            return super.onCreateView(inflater, container, savedInstanceState)
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG LAYOUT
    // -----------------------------------------------------------------------------------------

    fun dialogLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(context)
    }

}


// ---------------------------------------------------------------------------------------------
// VALUE ACTION VIEW BUILDER
// ---------------------------------------------------------------------------------------------

class ValueActionViewBuilder(val valueReference : ValueReference,
                             val gameId : GameId,
                             val themeId : ThemeId,
                             val context : Context,
                             val dialog : DialogFragment)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val valueSetActivity  = context as BaseValueSetActivity


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Delete Value Button
        val deleteOnClick = View.OnClickListener {
//            GameManager.engine(gameId)
//                    .apply { it.baseValueSet(valueReference.valueSetId) }
//                    .apDo { it.removeValue(valueReference.valueId) }
//            valueSetActivity.onUpdate()
//            dialog.dismiss()
        }
        layout.addView(this.buttonView(R.drawable.icon_delete_bin,
                                       R.string.delete_value,
                                       22,
                                       deleteOnClick))

        return layout
    }


    fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = ThemeManager.color(themeId, colorTheme)

        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f
        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f

        layout.corners         = Corners(3.0, 3.0, 3.0, 3.0)

        return layout.linearLayout(context)
    }


    private fun buttonView(iconId : Int,
                           labelId : Int,
                           iconSize : Int,
                           onClick : View.OnClickListener) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout = LinearLayoutBuilder()
        val icon   = ImageViewBuilder()
        val label  = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.margin.topDp         = 14f
        layout.margin.bottomDp      = 14f

        layout.onClick              = onClick

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp                = iconSize
        icon.heightDp               = iconSize

        icon.image                  = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_24")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color                  = ThemeManager.color(themeId, iconColorTheme)

        icon.margin.rightDp         = 7f

        // (3 B) Name
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        label.font                  = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    context)

        label.textId                = labelId

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_14")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color                 = ThemeManager.color(themeId, labelColorTheme)

        label.sizeSp                = 16f

        return layout.linearLayout(context)
    }


}
