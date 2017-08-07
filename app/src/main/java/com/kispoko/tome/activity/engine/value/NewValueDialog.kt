
package com.kispoko.tome.activity.engine.value


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.dialog.AdderDialogFragment
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.EngineValueType
import com.kispoko.tome.model.game.engine.value.ValueSetBase
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.theme.ThemeManager



/**
 * New Value Dialog
 */
class NewValueDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var valueSet     : ValueSetBase? = null

    private val appSettings : AppSettings = AppSettings(ThemeId.Dark)


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(valueSet : ValueSetBase) : NewValueDialog
        {
            val dialog = NewValueDialog()

            val args = Bundle()
            args.putSerializable("value_set", valueSet)
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

        this.valueSet = arguments.getSerializable("value_set") as ValueSetBase

        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val dialogLayout = this.dialogLayout()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // dialog.window.attributes.windowAnimations = R.style.DialogAnimation

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
        val valueSet = this.valueSet
        if (valueSet != null)
        {
            val viewBuilder = NewValueViewBuilder(valueSet, this.appSettings.themeId(), context)
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


class NewValueViewBuilder(val valueSet : ValueSetBase,
                          val themeId : ThemeId,
                          val context : Context)
{

    fun view() : View
    {
        val layout = this.viewLayout()

        // Header
        layout.addView(this.headerView())

        // Form
        layout.addView(this.formView())

        // Footer
        layout.addView(this.footerView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = ThemeManager.color(themeId, colorTheme)

        layout.corners          = Corners(TopLeftCornerRadius(3f),
                                            TopRightCornerRadius(3f),
                                            BottomRightCornerRadius(3f),
                                            BottomLeftCornerRadius(3f))

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // HEADER VIEW
    // -----------------------------------------------------------------------------------------

    private fun headerView() : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.MATCH_PARENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        header.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)

        header.text             = context.getString(R.string.new_s) + " " +
                                    valueSet.labelSingular()

        val textColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        header.color            = ThemeManager.color(themeId, textColorTheme)

        header.font             = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                context)

        header.sizeSp           = 15f

        header.margin.bottomDp  = 2f
        header.margin.leftDp  = 2f
        header.margin.rightDp  = 2f
        header.margin.topDp  = 2f

        header.padding.topDp    = 10f
        header.padding.bottomDp = 10f
        header.padding.leftDp   = 8f

        header.corners          = Corners(TopLeftCornerRadius(2f),
                                          TopRightCornerRadius(2f),
                                          BottomRightCornerRadius(0f),
                                          BottomLeftCornerRadius(0f))

        return header.textView(context)
    }


    // -----------------------------------------------------------------------------------------
    // FORM VIEW
    // -----------------------------------------------------------------------------------------

    private fun formView() : LinearLayout
    {
        val layout = this.formViewLayout()

        // Value Field
        val valueHintString = context.getString(R.string.my) + " " + this.valueSet.labelSingular()
        layout.addView(this.fieldView(context.getString(R.string.value), valueHintString))

//        layout.addView(this.fieldDividerView())

        // Description Field
        val descHintString = context.getString(R.string.my) + " " +
                                    this.valueSet.labelSingular() + " description"
        layout.addView(this.fieldView(context.getString(R.string.description), descHintString))

        return layout
    }


    private fun formViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL


//        layout.margin.bottomDp  = 2f

        layout.margin.leftDp  = 2f
        layout.margin.rightDp  = 2f

        return layout.linearLayout(context)
    }


    private fun fieldView(headerString : String, hintString : String) : LinearLayout
    {
        val layout = this.fieldViewLayout()

        // Header
        layout.addView(this.fieldHeaderView(headerString))

        // Edit Text
        layout.addView(this.fieldEditView(hintString))

        return layout
    }


    private fun fieldViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.bottomDp  = 2f

        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)

        return layout.linearLayout(context)
    }


    private fun fieldHeaderView(headerString : String) : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text             = headerString

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        header.color            = ThemeManager.color(themeId, colorTheme)

        header.font             = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                context)

        header.sizeSp           = 12f

        header.margin.topDp     = 6f

        return header.textView(context)
    }


    private fun fieldEditView(hintString : String) : EditText
    {
        val value = EditTextBuilder()

        value.width                 = LinearLayout.LayoutParams.MATCH_PARENT
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        value.minHeightDp           = 85f
        value.gravity               = Gravity.TOP

        value.backgroundResource    = R.drawable.bg_edit_text_no_style

        value.font                  = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    context)

//        value.hint                  = hintString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        value.color                 = ThemeManager.color(themeId, colorTheme)

        value.sizeSp                = 16f

//        value.underlineColor        = R.color.dark_blue_hl_1

        return value.editText(context)

    }


    // -----------------------------------------------------------------------------------------
    // FOOTER VIEW
    // -----------------------------------------------------------------------------------------

    private fun footerView() : LinearLayout
    {
        val layout = this.footerViewLayout()

        // Add..and keep going
        layout.addView(this.addMoreButtonView())

        // Add
        layout.addView(this.doneButtonView())

        return layout
    }


    private fun footerViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 40

        layout.orientation      = LinearLayout.HORIZONTAL

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)

        layout.margin.leftDp  = 2f
        layout.margin.rightDp  = 2f
        layout.margin.bottomDp = 2f

        return layout.linearLayout(context)
    }


    private fun addMoreButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()
        val icon        = ImageViewBuilder()
        val label       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight           = 1f

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor   = ThemeManager.color(themeId, bgColorTheme)

        layout.corners           = Corners(TopLeftCornerRadius(0f),
                                           TopRightCornerRadius(0f),
                                           BottomRightCornerRadius(0f),
                                           BottomLeftCornerRadius(2f))

        layout.margin.rightDp   = 1f

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 18
        icon.heightDp       = 18

        icon.image          = R.drawable.icon_replay

        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color          = ThemeManager.color(themeId, iconColorTheme)

        icon.margin.rightDp = 4f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = R.string.add_more_values

        val labelColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color             = ThemeManager.color(themeId, labelColorTheme)

        label.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                context)

        label.sizeSp            = 14f

        return layout.linearLayout(context)
    }



    private fun doneButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()
        val icon        = ImageViewBuilder()
        val label       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight           = 1f

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_green_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor   = ThemeManager.color(themeId, bgColorTheme)

        layout.corners           = Corners(TopLeftCornerRadius(0f),
                                           TopRightCornerRadius(0f),
                                           BottomRightCornerRadius(2f),
                                           BottomLeftCornerRadius(0f))

        layout.margin.leftDp   = 1f

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 20
        icon.heightDp       = 20

        icon.image          = R.drawable.icon_plus_sign

        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_14")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color          = ThemeManager.color(themeId, iconColorTheme)

        icon.margin.rightDp = 4f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId        = R.string.add_value

        val labelColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_14")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color         = ThemeManager.color(themeId, labelColorTheme)

        label.font          = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            context)

        label.sizeSp        = 15f

        return layout.linearLayout(context)
    }
}
