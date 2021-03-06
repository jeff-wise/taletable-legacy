
package com.taletable.android.activity.sheet.dialog


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.session.SessionActivity
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.*
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import java.io.Serializable



/**
 * Add/Subtract Amount Dialog Fragment
 */
class AddAmountDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var operation   : AddOperation? = null
    private var title       : String? = null
    private var adderState  : AdderState? = null
    private var entityId    : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(operation : AddOperation,
                        title : String,
                        adderState : AdderState,
                        entityId : EntityId) : AddAmountDialog
        {
            val dialog = AddAmountDialog()

            val args = Bundle()
            args.putSerializable("operation", operation)
            args.putString("title", title)
            args.putSerializable("adder_state", adderState)
            args.putSerializable("entity_id", entityId)
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

        this.operation  = arguments?.getSerializable("operation") as AddOperation
        this.title      = arguments?.getString("title")
        this.adderState = arguments?.getSerializable("adder_state") as AdderState
        this.entityId   = arguments?.getSerializable("entity_id") as EntityId


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val dialogLayout = this.dialogLayout()

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

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val title = this.title
        val operation = this.operation
        val adderState = this.adderState
        val entityId = this.entityId
        val context = this.context

        return if (title != null && entityId != null && adderState != null && operation != null && context != null)
        {
            val viewBuilder = AddAmountEditorViewBuilder(operation,
                                                         title,
                                                         adderState,
                                                         this,
                                                         entityId,
                                                         context)
            viewBuilder.view()

        }
        else
        {
            super.onCreateView(inflater, container, savedInstanceState)
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
                                 val title : String,
                                 val adderState : AdderState,
                                 val dialog : DialogFragment,
                                 val entityId : EntityId,
                                 val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var value : Double = 0.0
    private var numberString : String = ""

    private var valueTextView : TextView? = null

    private var isActive = false


    val activeValueColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
    val activeValueColor  = colorOrBlack(activeValueColorTheme, entityId)

    val inActiveValueColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_24"))))
    val inActiveValueColor  = colorOrBlack(inActiveValueColorTheme, entityId)


    private fun append(string : String)
    {
        this.numberString += string

        if (!isActive)
            this.valueTextView?.setTextColor(activeValueColor)

        this.valueTextView?.text = this.numberString
    }


    private fun delete()
    {
        if (this.numberString.isNotBlank())
        {
            this.numberString = this.numberString.dropLast(1)

            if (this.numberString.isBlank()) {
                this.valueTextView?.text = "0"
                this.valueTextView?.setTextColor(inActiveValueColor)
            }
            else {
                this.valueTextView?.text = this.numberString
            }
        }
    }


    private fun currentAdderState() : AdderState
    {
        val newDelta = adderState.delta + this.numberString.toDouble()
        return adderState.copy(delta = newDelta)
    }


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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        layout.backgroundColor  = colorOrBlack(colorTheme, entityId)

        layout.padding.bottomDp = 4f

        return layout.linearLayout(context)
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

        return layout.linearLayout(context)
    }


    private fun operationView() : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.MATCH_PARENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.margin.leftDp      = 3f
        name.margin.rightDp     = 3f

        name.padding.leftDp     = 8f
        name.padding.topDp      = 6f

        if (operation == AddOperation.ADD)
            name.text           = "${context.getString(R.string.add)} $title"
        else if (operation == AddOperation.SUBTRACT)
            name.text           = "${context.getString(R.string.subtract)} $title" // .toUpperCase()

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        name.color              = colorOrBlack(colorTheme, entityId)

        name.backgroundColor    = Color.WHITE

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        name.sizeSp             = 15f

        name.corners            = Corners(1.0, 1.0, 0.0, 0.0)

        return name.textView(context)
    }


    private fun valueRowView() : RelativeLayout
    {
        val layout = this.valueRowViewLayout()

        // Operator Sign
        layout.addView(this.operatorSignView())

        // Value
        val valueView = this.valueView()

        val valueLayoutParams = valueView.layoutParams as RelativeLayout.LayoutParams
        valueLayoutParams.addRule(RelativeLayout.END_OF, R.id.dialog_add_amount_operator)
        valueLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
//        valueView.layoutParams =

        this.valueTextView = valueView
        layout.addView(valueView)

        // Backspace Button
        layout.addView(this.backspaceButtonView())

        return layout
    }


    private fun valueRowViewLayout() : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.leftDp   = 6f
        layout.padding.rightDp  = 6f

//        val bgColorTheme  = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        //layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)
        layout.backgroundColor  = Color.WHITE

        layout.margin.leftDp        = 2f
        layout.margin.rightDp       = 2f
        layout.margin.bottomDp      = 2f

        layout.corners          = Corners(0.0, 0.0, 2.0, 1.0)

        return layout.relativeLayout(context)
    }


    private fun valueView() : TextView
    {
        val value               = TextViewBuilder()

        value.id                = R.id.dialog_add_amount_value

        value.layoutType        = LayoutType.RELATIVE

        value.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
        value.height            = RelativeLayout.LayoutParams.WRAP_CONTENT


        value.text              = "0"

        value.color             = inActiveValueColor

        value.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        value.sizeSp            = 32f

        return value.textView(context)
    }


    private fun backspaceButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()

        val icon        = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.id               = R.id.dialog_add_amount_delete

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)
        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.layoutType       = LayoutType.RELATIVE
        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.padding.topDp    = 15f
        layout.padding.bottomDp = 15f
        layout.padding.leftDp   = 15f
        layout.padding.rightDp  = 15f

        layout.onClick          = View.OnClickListener { this.delete() }

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        icon.color          = colorOrBlack(bsButtonColorTheme, entityId)

        icon.addRule(RelativeLayout.ALIGN_END)


        return layout.linearLayout(context)
    }


    private fun operatorSignView() : LinearLayout
    {

        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val icon            = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.id               = R.id.dialog_add_amount_operator

        layout.addRule(RelativeLayout.ALIGN_PARENT_START)
        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.layoutType       = LayoutType.RELATIVE
        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.margin.rightDp   = 2f
        layout.padding.topDp    = 4f

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 26
        icon.heightDp           = 26

        if (this.operation == AddOperation.ADD)
            icon.image = R.drawable.icon_plus_sign
        else
            icon.image = R.drawable.icon_minus_sign

        val bsButtonColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        icon.color          = colorOrBlack(bsButtonColorTheme, entityId)

        icon.addRule(RelativeLayout.ALIGN_END)


        return layout.linearLayout(context)
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

        return layout.linearLayout(context)
    }


    private fun numberButtonView(label : String, onClick : View.OnClickListener) : TextView
    {
        val number                  = TextViewBuilder()

        number.width                = 0
        number.height               = LinearLayout.LayoutParams.MATCH_PARENT
        number.weight               = 1f

        number.gravity              = Gravity.CENTER

        number.margin.leftDp        = 1f
        number.margin.rightDp       = 1f

        number.text                 = label

        number.sizeSp               = 26f

        number.font                 = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_15"))))
        number.color                = colorOrBlack(textColorTheme, entityId)

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
//        number.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)
        number.backgroundColor      = Color.WHITE

        number.corners              = Corners(1.0, 1.0, 1.0, 1.0)

        number.onClick              = onClick

        return number.textView(context)
    }


//    private fun decimalPointButtonView() : TextView
//    {
//        val number                  = TextViewBuilder()
//
//        number.width                = 0
//        number.height               = LinearLayout.LayoutParams.MATCH_PARENT
//        number.weight               = 1f
//
//        number.gravity              = Gravity.CENTER
//
//        number.margin.leftDp        = 1f
//        number.margin.rightDp       = 1f
//
//        number.text                 = "."
//
//        number.sizeSp               = 33f
//
//        number.font               = Font.typeface(TextFont.default(),
//                                        TextFontStyle.Regular,
//                                        sheetUIContext.context)
//
//        val textColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//        number.color                = SheetManager.color(sheetUIContext.sheetId, textColorTheme)
//
////        val bgColorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
////        number.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)
//        number.backgroundColor      = Color.WHITE
//
//        number.corners              = Corners(1.0, 1.0, 1.0, 1.0)
//
//        //number.onClick              = onClick
//
//        return number.textView(sheetUIContext.context)
//    }


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
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_green_4")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green_80"))))
        layout.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        layout.corners          = Corners(1.0, 1.0, 1.0, 1.0)

        layout.onClick          = View.OnClickListener {
            val sheetActivity = context as SessionActivity
            val adderDialog = AdderDialog.newInstance(this.currentAdderState(), entityId)
            adderDialog.show(sheetActivity.supportFragmentManager, "")
            dialog.dismiss()
        }

        layout.margin.leftDp    = 1f
        layout.margin.rightDp   = 1f

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 26
        icon.heightDp           = 26

        icon.image              = R.drawable.icon_check_bold

//        val iconColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        icon.color              = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)
        icon.color              = Color.WHITE


        return layout.linearLayout(context)
    }


    private fun keyPadRowViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        //layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.heightDp         = 60

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.margin.bottomDp  = 2f
        layout.margin.leftDp    = 2f
        layout.margin.rightDp   = 2f

        return layout.linearLayout(context)
    }


    private fun keyPadRow1View() : LinearLayout
    {
        val layout = this.keyPadRowViewLayout()

        // 1
        val oneOnClick = View.OnClickListener {
            this.append("1")
        }
        layout.addView(this.numberButtonView("1", oneOnClick))

        // 2
        val twoOnClick = View.OnClickListener {
            this.append("2")
        }
        layout.addView(this.numberButtonView("2", twoOnClick))

        // 3
        val threeOnClick = View.OnClickListener {
            this.append("3")
        }
        layout.addView(this.numberButtonView("3", threeOnClick))

        // 0
        val zeroOnClick = View.OnClickListener {
            this.append("0")
        }
        layout.addView(this.numberButtonView("0", zeroOnClick))

        return layout
    }


    private fun keyPadRow2View() : LinearLayout
    {
        val layout = this.keyPadRowViewLayout()

        // 4
        val fourOnClick = View.OnClickListener {
            this.append("4")
        }
        layout.addView(this.numberButtonView("4", fourOnClick))

        // 5
        val fiveOnClick = View.OnClickListener {
            this.append("5")
        }
        layout.addView(this.numberButtonView("5", fiveOnClick))

        // 6
        val sixOnClick = View.OnClickListener {
            this.append("6")
        }
        layout.addView(this.numberButtonView("6", sixOnClick))

        // .
        val dotOnClick = View.OnClickListener {
            this.append(".")
        }
        layout.addView(this.numberButtonView(".", dotOnClick))

        return layout
    }


    private fun keyPadRow3View() : LinearLayout
    {
        val layout = this.keyPadRowViewLayout()

        // 7
        val sevenOnClick = View.OnClickListener {
            this.append("7")
        }
        layout.addView(this.numberButtonView("7", sevenOnClick))

        // 8
        val eightOnClick = View.OnClickListener {
            this.append("8")
        }
        layout.addView(this.numberButtonView("8", eightOnClick))

        // 9
        val nineOnClick = View.OnClickListener {
            this.append("9")
        }
        layout.addView(this.numberButtonView("9", nineOnClick))

        // Done
        layout.addView(this.doneButtonView())

        return layout
    }

}

