
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext



/**
 * Adder Dialog Fragment
 */
class AdderDialogFragment : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var value           : Double? = null
    private var valueName       : String? = null
    private var sheetContext    : SheetContext? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(value : Double,
                        valueName : String?,
                        sheetContext : SheetContext) : AdderDialogFragment
        {
            val dialog = AdderDialogFragment()

            val args = Bundle()
            args.putDouble("value", value)
            args.putString("value_name", valueName)
            args.putSerializable("sheet_context", sheetContext)
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

        this.value        = arguments.getDouble("value")
        this.valueName    = arguments.getString("value_name")
        this.sheetContext = arguments.getSerializable("sheet_context") as SheetContext


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val sheetContext = this.sheetContext
        if (sheetContext != null)
        {
            val sheetUIContext = SheetUIContext(sheetContext, context)

            val dialogLayout = this.dialogLayout(sheetUIContext)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.setContentView(dialogLayout)

            val window = dialog.window
            val wlp = window.attributes

            wlp.gravity = Gravity.BOTTOM
            window.attributes = wlp

            val width  = LinearLayout.LayoutParams.MATCH_PARENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT

            dialog.window.setLayout(width, height)
        }

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val sheetContext = this.sheetContext
        if (sheetContext != null)
        {
            val sheetUIContext  = SheetUIContext(sheetContext, context)

            val value = this.value

            if (value != null)
                return AdderEditorView.view(value.toInt(), this.valueName, sheetUIContext)
            else
                return super.onCreateView(inflater, container, savedInstanceState)
        }
        else
        {
            return super.onCreateView(inflater, container, savedInstanceState)
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG LAYOUT
    // -----------------------------------------------------------------------------------------

    fun dialogLayout(sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        return layout.linearLayout(context)
    }

}


object AdderEditorView
{

    fun view(value : Int, valueName : String?, sheetUIContext : SheetUIContext) : View
    {
        val layout = this.viewLayout(sheetUIContext)

        layout.addView(this.valueView(value, valueName, sheetUIContext))

        layout.addView(this.dynamicAddView(sheetUIContext))

        layout.addView(this.staticAddView(sheetUIContext))

        layout.addView(this.bottomRowView(sheetUIContext))

        return layout
    }


    private fun viewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.padding.bottomDp = 10f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun valueView(intValue : Int,
                          valueName : String?,
                          sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.valueViewLayout(sheetUIContext.context)

        if (valueName != null)
            layout.addView(this.valueNameView(valueName, sheetUIContext))

        layout.addView(this.valueMainView(intValue, sheetUIContext))

        return layout
    }


    private fun valueViewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun valueNameView(valueName : String,
                              sheetUIContext : SheetUIContext) : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.margin.topDp       = 10f
        name.margin.leftDp      = 12f

        name.text               = valueName.toUpperCase()

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        name.color              = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        name.font               = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            sheetUIContext.context)

        name.sizeSp             = 11f

        return name.textView(sheetUIContext.context)
    }


    private fun valueMainView(intValue : Int, sheetUIContext : SheetUIContext) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val value           = TextViewBuilder()
        val undoButton      = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 70

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.leftDp   = 15f
        layout.padding.rightDp  = 20f

        layout.child(value)
              .child(undoButton)

        // (3 A) Value
        // -------------------------------------------------------------------------------------

        value.width             = LinearLayout.LayoutParams.MATCH_PARENT
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT
        value.weight            = 5f

        value.text              = intValue.toString()

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        value.color             = SheetManager.color(sheetUIContext.sheetId, valueColorTheme)

        value.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Light,
                                                sheetUIContext.context)

        value.sizeSp            = 32f

        value.addRule(RelativeLayout.ALIGN_START)

        // (3 B) Undo Button
        // -------------------------------------------------------------------------------------

        undoButton.widthDp          = 32
        undoButton.heightDp         = 32
        undoButton.weight           = 1f

        undoButton.image            = R.drawable.icon_undo

        val undoColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        undoButton.color            = SheetManager.color(sheetUIContext.sheetId, undoColorTheme)

        undoButton.addRule(RelativeLayout.ALIGN_END)

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun dynamicAddView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val plusNum         = TextViewBuilder()
        val plusDice        = TextViewBuilder()
        val minusNum        = TextViewBuilder()
        val minusDice       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 60

        layout.margin.bottomDp  = 5f
        layout.margin.leftDp    = 2f
        layout.margin.rightDp   = 2f

        layout.child(minusNum)
              .child(minusDice)
              .child(plusDice)
              .child(plusNum)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))

        // (3 A) Plus One
        // -------------------------------------------------------------------------------------

        plusNum.width               = 0
        plusNum.height              = LinearLayout.LayoutParams.MATCH_PARENT
        plusNum.weight              = 1f

        plusNum.gravity             = Gravity.CENTER

        plusNum.margin.leftDp       = 2f
        plusNum.margin.rightDp      = 2f

        plusNum.textId              = R.string.plus_num

        plusNum.sizeSp              = 16f
        plusNum.color               = SheetManager.color(sheetUIContext.sheetId, textColorTheme)
        plusNum.backgroundColor     = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        plusNum.corners             = Corners(TopLeftCornerRadius(1f),
                                              TopRightCornerRadius(1f),
                                              BottomRightCornerRadius(1f),
                                              BottomLeftCornerRadius(1f))

        // (3 B) Plus Ten
        // -------------------------------------------------------------------------------------

        plusDice.width               = 0
        plusDice.height              = LinearLayout.LayoutParams.MATCH_PARENT
        plusDice.weight              = 1f

        plusDice.gravity             = Gravity.CENTER

        plusDice.margin.leftDp       = 2f
        plusDice.margin.rightDp      = 2f

        plusDice.textId              = R.string.plus_dice

        plusDice.sizeSp              = 16f
        plusDice.color               = SheetManager.color(sheetUIContext.sheetId, textColorTheme)
        plusDice.backgroundColor     = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        plusDice.corners             = Corners(TopLeftCornerRadius(1f),
                                              TopRightCornerRadius(1f),
                                              BottomRightCornerRadius(1f),
                                              BottomLeftCornerRadius(1f))

        // (3 C) Minus One
        // -------------------------------------------------------------------------------------

        minusNum.width              = 0
        minusNum.height             = LinearLayout.LayoutParams.MATCH_PARENT
        minusNum.weight             = 1f

        minusNum.gravity            = Gravity.CENTER

        minusNum.margin.leftDp      = 2f
        minusNum.margin.rightDp     = 2f

        minusNum.textId             = R.string.minus_num

        minusNum.sizeSp             = 16f
        minusNum.color              = SheetManager.color(sheetUIContext.sheetId, textColorTheme)
        minusNum.backgroundColor    = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        minusNum.corners            = Corners(TopLeftCornerRadius(1f),
                                              TopRightCornerRadius(1f),
                                              BottomRightCornerRadius(1f),
                                              BottomLeftCornerRadius(1f))

        // (3 C) Minus One
        // -------------------------------------------------------------------------------------

        minusDice.width              = 0
        minusDice.height             = LinearLayout.LayoutParams.MATCH_PARENT
        minusDice.weight             = 1f

        minusDice.gravity            = Gravity.CENTER

        minusDice.margin.rightDp     = 2f
        minusDice.margin.leftDp      = 2f

        minusDice.textId             = R.string.minus_dice

        minusDice.sizeSp             = 16f
        minusDice.color              = SheetManager.color(sheetUIContext.sheetId, textColorTheme)
        minusDice.backgroundColor    = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        minusDice.corners            = Corners(TopLeftCornerRadius(1f),
                                              TopRightCornerRadius(1f),
                                              BottomRightCornerRadius(1f),
                                              BottomLeftCornerRadius(1f))

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun staticAddView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val plusOne         = TextViewBuilder()
        val plusTen         = TextViewBuilder()
        val minusOne        = TextViewBuilder()
        val minusTen        = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 60

        layout.margin.bottomDp  = 5f
        layout.margin.leftDp    = 2f
        layout.margin.rightDp   = 2f

        layout.child(minusOne)
              .child(minusTen)
              .child(plusTen)
              .child(plusOne)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))

        // (3 A) Plus One
        // -------------------------------------------------------------------------------------

        plusOne.width               = 0
        plusOne.height              = LinearLayout.LayoutParams.MATCH_PARENT
        plusOne.weight              = 1f

        plusOne.gravity             = Gravity.CENTER

        plusOne.margin.leftDp       = 2f
        plusOne.margin.rightDp      = 2f

        plusOne.textId              = R.string.plus_one

        plusOne.sizeSp              = 16f
        plusOne.color               = SheetManager.color(sheetUIContext.sheetId, textColorTheme)
        plusOne.backgroundColor     = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        plusOne.corners             = Corners(TopLeftCornerRadius(1f),
                                              TopRightCornerRadius(1f),
                                              BottomRightCornerRadius(1f),
                                              BottomLeftCornerRadius(1f))

        // (3 B) Plus Ten
        // -------------------------------------------------------------------------------------

        plusTen.width               = 0
        plusTen.height              = LinearLayout.LayoutParams.MATCH_PARENT
        plusTen.weight              = 1f

        plusTen.gravity             = Gravity.CENTER

        plusTen.margin.leftDp       = 2f
        plusTen.margin.rightDp      = 2f

        plusTen.textId              = R.string.plus_ten

        plusTen.sizeSp              = 16f
        plusTen.color               = SheetManager.color(sheetUIContext.sheetId, textColorTheme)
        plusTen.backgroundColor     = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        plusTen.corners             = Corners(TopLeftCornerRadius(1f),
                                              TopRightCornerRadius(1f),
                                              BottomRightCornerRadius(1f),
                                              BottomLeftCornerRadius(1f))

        // (3 C) Minus One
        // -------------------------------------------------------------------------------------

        minusOne.width              = 0
        minusOne.height             = LinearLayout.LayoutParams.MATCH_PARENT
        minusOne.weight             = 1f

        minusOne.gravity            = Gravity.CENTER

        minusOne.margin.leftDp      = 2f
        minusOne.margin.rightDp     = 2f

        minusOne.textId             = R.string.minus_one

        minusOne.sizeSp             = 16f
        minusOne.color              = SheetManager.color(sheetUIContext.sheetId, textColorTheme)
        minusOne.backgroundColor    = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        minusOne.corners            = Corners(TopLeftCornerRadius(1f),
                                              TopRightCornerRadius(1f),
                                              BottomRightCornerRadius(1f),
                                              BottomLeftCornerRadius(1f))

        // (3 C) Minus One
        // -------------------------------------------------------------------------------------

        minusTen.width              = 0
        minusTen.height             = LinearLayout.LayoutParams.MATCH_PARENT
        minusTen.weight             = 1f

        minusTen.gravity            = Gravity.CENTER

        minusTen.margin.rightDp     = 2f
        minusTen.margin.leftDp      = 2f

        minusTen.textId             = R.string.minus_ten

        minusTen.sizeSp             = 16f
        minusTen.color              = SheetManager.color(sheetUIContext.sheetId, textColorTheme)
        minusTen.backgroundColor    = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        minusTen.corners            = Corners(TopLeftCornerRadius(1f),
                                              TopRightCornerRadius(1f),
                                              BottomRightCornerRadius(1f),
                                              BottomLeftCornerRadius(1f))

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun bottomRowView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.bottomRowViewLayout(sheetUIContext.context)

        layout.addView(this.calcButtonView(sheetUIContext))

        layout.addView(this.doneButtonView(sheetUIContext))

        return layout
    }


    private fun bottomRowViewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 60

        layout.margin.rightDp   = 2f
        layout.margin.leftDp    = 2f

        layout.orientation      = LinearLayout.HORIZONTAL

        return layout.linearLayout(context)
    }


    private fun calcButtonView(sheetUIContext : SheetUIContext) : LinearLayout
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

        layout.margin.rightDp   = 2f
        layout.margin.leftDp    = 2f

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_9")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor   = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners           = Corners(TopLeftCornerRadius(1f),
                                           TopRightCornerRadius(1f),
                                           BottomRightCornerRadius(1f),
                                           BottomLeftCornerRadius(1f))

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 18
        icon.heightDp       = 19

        icon.image          = R.drawable.icon_dialog_adder_calculator

        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color          = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        icon.margin.rightDp = 5f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId        = R.string.calculator

        val labelColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color         = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font          = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            sheetUIContext.context)

        label.sizeSp        = 17f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun doneButtonView(sheetUIContext : SheetUIContext) : LinearLayout
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

        layout.margin.rightDp   = 2f
        layout.margin.leftDp    = 2f

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("green_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor   = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners           = Corners(TopLeftCornerRadius(1f),
                                           TopRightCornerRadius(1f),
                                           BottomRightCornerRadius(1f),
                                           BottomLeftCornerRadius(1f))

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 19
        icon.heightDp       = 19

        icon.image          = R.drawable.icon_dialog_adder_done

        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color          = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        icon.margin.rightDp = 5f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text          = sheetUIContext.context.getString(R.string.done).toUpperCase()

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color         = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font          = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            sheetUIContext.context)

        label.sizeSp        = 17f

        return layout.linearLayout(sheetUIContext.context)
    }

}


