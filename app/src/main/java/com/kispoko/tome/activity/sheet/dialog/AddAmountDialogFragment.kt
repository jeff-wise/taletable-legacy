
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.util.Util
import java.io.Serializable



/**
 * Add/Subtract Amount Dialog Fragment
 */
class AddAmountDialogFragment : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var operation       : AddOperation? = null
    private var sheetContext    : SheetContext? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(operation : AddOperation,
                        sheetContext : SheetContext) : AddAmountDialogFragment
        {
            val dialog = AddAmountDialogFragment()

            val args = Bundle()
            args.putSerializable("operation", operation)
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

        this.operation    = arguments.getSerializable("operation") as AddOperation
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

            val operation = this.operation

            if (operation != null)
            {
                val viewBuilder = AddAmountEditorViewBuilder(operation, sheetUIContext)
                return viewBuilder.view()
            }
            else
            {
                return super.onCreateView(inflater, container, savedInstanceState)
            }
        }
        else
        {
            return super.onCreateView(inflater, container, savedInstanceState)
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG LAYOUT
    // -----------------------------------------------------------------------------------------

    fun dialogLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(context)
    }

}


// ---------------------------------------------------------------------------------------------
// ADD OPERATION
// ---------------------------------------------------------------------------------------------

enum class AddOperation : Serializable
{
    ADD,
    SUBTRACT
}


// ---------------------------------------------------------------------------------------------
// EDITOR VIEW BUILDER
// ---------------------------------------------------------------------------------------------

class AddAmountEditorViewBuilder(val operation : AddOperation,
                                 val sheetUIContext : SheetUIContext)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var value : Double = 0.0

    private var numberString : String = Util.doubleString(value)

    private var valueTextView : TextView? = null



    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout  = this.viewLayout()

        // Screen
        layout.addView(this.screenView())

        // Keypad
        layout.addView(this.keyPadView())

        return layout
    }


    private fun viewLayout() : LinearLayout
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


    // -----------------------------------------------------------------------------------------
    // SCREEN VIEW
    // -----------------------------------------------------------------------------------------

    private fun screenView() : LinearLayout
    {
        val layout = this.valueViewLayout()

        // Operation View
        layout.addView(this.operationView())

        layout.addView(this.valueRowView())

        return layout
    }


    private fun valueViewLayout() : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.VERTICAL

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun operationView() : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.margin.topDp       = 10f
        name.margin.leftDp      = 12f

        if (operation == AddOperation.ADD)
            name.text           = sheetUIContext.context.getString(R.string.add).toUpperCase()
        else if (operation == AddOperation.SUBTRACT)
            name.text           = sheetUIContext.context.getString(R.string.subtract).toUpperCase()

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


    private fun valueRowView() : LinearLayout
    {
        val layout = this.valueRowViewLayout()

        // Operator Sign
        layout.addView(this.operatorSignView())

        // Value
        val valueView = this.valueView()
        this.valueTextView = valueView
        layout.addView(valueView)

        // Backspace Button
        layout.addView(this.backspaceButtonView())

        return layout
    }


    private fun valueRowViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 70

        layout.orientation      = LinearLayout.HORIZONTAL
        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun valueView() : TextView
    {
        val value               = TextViewBuilder()

        value.width             = LinearLayout.LayoutParams.MATCH_PARENT
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT
        value.weight            = 5f

        value.text              = this.numberString

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        value.color             = SheetManager.color(sheetUIContext.sheetId, valueColorTheme)

        value.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Light,
                                                sheetUIContext.context)

        value.sizeSp            = 32f

        return value.textView(sheetUIContext.context)
    }


    private fun backspaceButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()
        val icon        = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.topDp    = 15f
        layout.padding.bottomDp = 15f
        layout.padding.leftDp   = 15f
        layout.padding.rightDp  = 15f

        layout.onClick        = View.OnClickListener {
            if (this.numberString.isNotBlank()) {
                this.numberString = this.numberString.dropLast(1)
                this.valueTextView?.text = this.numberString
            }
        }

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 31
        icon.heightDp       = 31
        icon.weight         = 1f

        icon.image          = R.drawable.icon_backspace

        icon.padding.topDp  = 4f

        val bsButtonColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color          = SheetManager.color(sheetUIContext.sheetId, bsButtonColorTheme)

        icon.addRule(RelativeLayout.ALIGN_END)


        return layout.linearLayout(sheetUIContext.context)
    }


    private fun operatorSignView() : ImageView
    {
        val operator            = ImageViewBuilder()

        operator.widthDp        = 30
        operator.heightDp       = 30
        operator.weight         = 1f

        operator.padding.topDp   = 4f
        operator.margin.rightDp   = 5f

        if (this.operation == AddOperation.ADD)
            operator.image = R.drawable.icon_plus_sign
        else
            operator.image = R.drawable.icon_minus_sign

        val bsButtonColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        operator.color          = SheetManager.color(sheetUIContext.sheetId, bsButtonColorTheme)

        operator.addRule(RelativeLayout.ALIGN_END)

        return operator.imageView(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // KEY PAD VIEW
    // -----------------------------------------------------------------------------------------

    private fun keyPadView() : LinearLayout
    {
        val layout = this.keyPadViewLayout()

        // Row 1 [1 2 3]
        layout.addView(this.keyPadRow1View())

        // Row 2 [4 5 6]
        layout.addView(this.keyPadRow2View())

        // Row 3 [7 8 9]
        layout.addView(this.keyPadRow3View())

        // Row 4 [. 0 x]

        return layout
    }


    private fun keyPadViewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.VERTICAL

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun numberButtonView(label : String, onClick : View.OnClickListener) : TextView
    {
        val number                  = TextViewBuilder()

        number.width                = 0
        number.height               = LinearLayout.LayoutParams.MATCH_PARENT
        number.weight               = 1f

        number.gravity              = Gravity.CENTER

        number.margin.leftDp        = 2f
        number.margin.rightDp       = 2f

        number.text                 = label

        number.sizeSp               = 16f

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        number.color                = SheetManager.color(sheetUIContext.sheetId, textColorTheme)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        number.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        number.corners              = Corners(TopLeftCornerRadius(2f),
                                              TopRightCornerRadius(2f),
                                              BottomRightCornerRadius(2f),
                                              BottomLeftCornerRadius(2f))

        number.onClick              = onClick

        return number.textView(sheetUIContext.context)
    }


    private fun doneButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight           = 1f

        layout.gravity          = Gravity.CENTER

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("green_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners          = Corners(TopLeftCornerRadius(2f),
                                          TopRightCornerRadius(2f),
                                          BottomRightCornerRadius(2f),
                                          BottomLeftCornerRadius(2f))

        layout.margin.leftDp    = 2f
        layout.margin.rightDp   = 2f

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 22
        icon.heightDp           = 22

        icon.image              = R.drawable.icon_check

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_3")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color              = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)


        return layout.linearLayout(sheetUIContext.context)
    }


    private fun keyPadRowViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        //layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.heightDp         = 60

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.margin.bottomDp  = 5f
        layout.margin.leftDp    = 2f
        layout.margin.rightDp   = 2f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun keyPadRow1View() : LinearLayout
    {
        val layout = this.keyPadRowViewLayout()

        // 1
        val oneOnClick = View.OnClickListener {
            this.numberString += "1"
            this.valueTextView?.text = this.numberString
        }
        layout.addView(this.numberButtonView("1", oneOnClick))

        // 2
        val twoOnClick = View.OnClickListener {
            this.numberString += "2"
            this.valueTextView?.text = this.numberString
        }
        layout.addView(this.numberButtonView("2", twoOnClick))

        // 3
        val threeOnClick = View.OnClickListener {
            this.numberString += "3"
            this.valueTextView?.text = this.numberString
        }
        layout.addView(this.numberButtonView("3", threeOnClick))

        // 0
        val zeroOnClick = View.OnClickListener {
            this.numberString += "0"
            this.valueTextView?.text = this.numberString
        }
        layout.addView(this.numberButtonView("0", zeroOnClick))

        return layout
    }


    private fun keyPadRow2View() : LinearLayout
    {
        val layout = this.keyPadRowViewLayout()

        // 4
        val fourOnClick = View.OnClickListener {
            this.numberString += "4"
            this.valueTextView?.text = this.numberString
        }
        layout.addView(this.numberButtonView("4", fourOnClick))

        // 5
        val fiveOnClick = View.OnClickListener {
            this.numberString += "5"
            this.valueTextView?.text = this.numberString
        }
        layout.addView(this.numberButtonView("5", fiveOnClick))

        // 6
        val sixOnClick = View.OnClickListener {
            this.numberString += "6"
            this.valueTextView?.text = this.numberString
        }
        layout.addView(this.numberButtonView("6", sixOnClick))

        // .
        val dotOnClick = View.OnClickListener {
            this.numberString += "."
            this.valueTextView?.text = this.numberString
        }
        layout.addView(this.numberButtonView(".", dotOnClick))

        return layout
    }


    private fun keyPadRow3View() : LinearLayout
    {
        val layout = this.keyPadRowViewLayout()

        // 7
        val sevenOnClick = View.OnClickListener {
            this.numberString += "7"
            this.valueTextView?.text = this.numberString
        }
        layout.addView(this.numberButtonView("7", sevenOnClick))

        // 8
        val eightOnClick = View.OnClickListener {
            this.numberString += "8"
            this.valueTextView?.text = this.numberString
        }
        layout.addView(this.numberButtonView("8", eightOnClick))

        // 9
        val nineOnClick = View.OnClickListener {
            this.numberString += "9"
            this.valueTextView?.text = this.numberString
        }
        layout.addView(this.numberButtonView("9", nineOnClick))

        // Done
        layout.addView(this.doneButtonView())

        return layout
    }

}

