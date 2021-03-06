
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
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.taletable.android.R
import com.taletable.android.activity.session.SessionActivity
import com.taletable.android.lib.ui.*
import com.taletable.android.model.engine.dice.*
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
class AddDiceDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var operation    : DiceOperation? = null
    private var adderState   : AdderState? = null
    private var entityId     : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(operation : DiceOperation,
                        adderState : AdderState,
                        entityId : EntityId) : AddDiceDialog
        {
            val dialog = AddDiceDialog()

            val args = Bundle()
            args.putSerializable("operation", operation)
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

        this.operation  = arguments?.getSerializable("operation") as DiceOperation
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
        val operation = this.operation
        val adderState = this.adderState
        val entityId = this.entityId
        val context = this.context

        return if (operation != null && adderState != null && entityId != null && context != null)
        {
            val viewBuilder = DiceViewBuilder(operation, adderState, this, entityId, context)
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
// DICE OPERATION
// ---------------------------------------------------------------------------------------------

enum class DiceOperation : Serializable
{
    ADD,
    SUBTRACT,
    NOOP
}


// ---------------------------------------------------------------------------------------------
// DICE VIEW BUILDER
// ---------------------------------------------------------------------------------------------

class DiceViewBuilder(val operation : DiceOperation,
                      val adderState : AdderState,
                      val dialog : DialogFragment,
                      val entityId : EntityId,
                      val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private val diceCountMap : MutableMap<Int,Int> = mutableMapOf(3 to 0,
                                                                  4 to 0,
                                                                  6 to 0,
                                                                  8 to 0,
                                                                  10 to 0,
                                                                  12 to 0,
                                                                  20 to 0,
                                                                  100 to 0)

    private val viewByDie : MutableMap<Int,TextView> = mutableMapOf()

    private var diceView : FlexboxLayout? = null

    private val history : MutableList<Int> = mutableListOf()

    private var optionView : TextView? = null
    private var optionsView : LinearLayout? = null

    private var currentOption : Int = 0

    private val rollOptionList : List<DiceRollModifierFunction> =
            listOf(DiceRollModifierFunction.DropHighest,
                   DiceRollModifierFunction.DropLowest)

    private var rollOptionsVisible = false


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    private fun incDieCount(die : Int)
    {
        val dieCount = this.diceCountMap[die]
        if (dieCount != null) {
            val newCount = dieCount + 1
            this.diceCountMap.put(die, newCount)
            this.history.add(die)
            this.updateView(die, newCount)
        }
    }


    private fun undo()
    {
        if (this.history.size > 0)
        {
            val lastDieIncremented = this.history.last()
            val dieCount = this.diceCountMap[lastDieIncremented]
            if (dieCount != null)
            {
                val newCount = dieCount - 1
                this.diceCountMap.put(lastDieIncremented, newCount)
                this.history.removeAt(this.history.size - 1)
                this.updateView(lastDieIncremented, newCount)
            }
        }
    }


    private fun updateView(dieChanged : Int, newValue : Int)
    {
        if (viewByDie.containsKey(dieChanged))
        {
            val dieView = this.viewByDie[dieChanged]
            dieView?.text = newValue.toString() + "d" + dieChanged.toString()
        }
        else
        {
            this.diceView?.removeAllViews()
            val sortedEntries = this.diceCountMap.entries.sortedBy { it.key }
            var index = 0
            sortedEntries.forEach { (sides, count) ->
                if (count != 0)
                {
//                    if (index > 0)
//                    {
                        this.diceView?.addView(this.diePlusView())
                    //}
                    this.diceView?.addView(this.dieView(sides, count))
                    index += 1
                }
            }
        }
    }


    private fun showNextRollOption()
    {
        val currentOption = this.currentOption
        if (currentOption == (this.rollOptionList.size - 1))
            this.currentOption = 0
        else
            this.currentOption = currentOption + 1

        this.updateOptionView()
    }


    private fun showPreviousRollOption()
    {
        val currentOption = this.currentOption
        if (currentOption == 0)
            this.currentOption = this.rollOptionList.size - 1
        else
            this.currentOption = currentOption - 1

        this.updateOptionView()
    }


    private fun updateOptionView()
    {
        val modifier = this.rollOptionList[this.currentOption]
        if (modifier != null)
            this.optionView?.text = modifier.toString()
    }


    private fun currentAdderState() : AdderState
    {
        val quantities = this.diceCountMap.mapNotNull {
            if (it.value > 0)
                DiceQuantity(DiceSides(it.key), DiceRollQuantity(it.value))
            else
                null
        }
        val diceRoll = DiceRoll(quantities, listOf(), listOf())

        return adderState.copy(diceRolls = adderState.diceRolls.plusElement(diceRoll))
    }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Screen
        layout.addView(this.screenView())

        // Keypad
        layout.addView(this.keyPadView())

        // Roll Options
        val optionsView = this.rollOptionsView()
        this.optionsView = optionsView
        layout.addView(optionsView)

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

        layout.padding.bottomDp = 5f

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // SCREEN VIEW
    // -----------------------------------------------------------------------------------------

    private fun screenView() : LinearLayout
    {
        val layout = this.valueViewLayout()

        // Title View
        layout.addView(this.titleView())

        // Main View
        layout.addView(this.screenMainView())

        // Modifier View
        // layout.addView(this.modifierView())

        return layout
    }


    private fun valueViewLayout() : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE

        layout.corners      = Corners(2.0, 2.0, 2.0, 2.0)

        layout.margin.leftDp    = 3f
        layout.margin.rightDp   = 3f
        layout.margin.topDp    = 2f
        layout.margin.bottomDp    = 2f

        return layout.linearLayout(context)
    }


    // Title View
    // -----------------------------------------------------------------------------------------

    private fun titleView() : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.margin.topDp       = 10f
        name.margin.leftDp      = 10f

        if (operation == DiceOperation.ADD)
            name.text           = context.getString(R.string.add_dice)
        else if (operation == DiceOperation.SUBTRACT)
            name.text           = context.getString(R.string.subtract)

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        name.color              = colorOrBlack(colorTheme, entityId)

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        name.sizeSp             = 16f

        return name.textView(context)
    }


    // Screen Main View
    // -----------------------------------------------------------------------------------------

    private fun screenMainView() : RelativeLayout
    {
        val layout = this.screenMainViewLayout()

        val leftLayout = this.screenLeftViewLayout()
        layout.addView(leftLayout)

        // Operator
//        leftLayout.addView(this.operatorView())


        // Dice
        val diceView = this.diceView()
        this.diceView = diceView
        leftLayout.addView(diceView)

        // Undo Button
        //layout.addView(this.undoButtonView())

        return layout
    }


    private fun screenMainViewLayout() : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL
//        layout.gravity          = Gravity.CENTER_VERTICAL

//        val bgColorTheme  = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_2"))))
//        layout.backgroundColor         = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f
//        layout.padding.leftDp   = 10f

        return layout.relativeLayout(context)
    }


    private fun screenLeftViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.layoutType       = LayoutType.RELATIVE
        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL
        layout.gravity          = Gravity.BOTTOM

        layout.addRule(RelativeLayout.ALIGN_PARENT_START)
//        layout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

        return layout.linearLayout(context)
    }


    private fun operatorView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val icon            = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.layoutGravity  = Gravity.BOTTOM
        layout.gravity      = Gravity.CENTER

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 30
        icon.heightDp       = 30
        icon.weight         = 1f

        //icon.padding.topDp   = 4f
        icon.margin.rightDp   = 5f

        if (this.operation == DiceOperation.ADD)
            icon.image = R.drawable.icon_plus_sign
        else
            icon.image = R.drawable.icon_minus_sign

        val bsButtonColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        icon.color          = colorOrBlack(bsButtonColorTheme, entityId)

        icon.addRule(RelativeLayout.ALIGN_END)

        return layout.linearLayout(context)
    }


    private fun diceView() : FlexboxLayout
    {
        val layout = this.diceViewLayout()

        layout.addView(this.chooseDiceTextView())

        return layout
    }


    private fun diceViewLayout() : FlexboxLayout
    {
        val layout = FlexboxLayoutBuilder()

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight           = 5f

        layout.wrap             = FlexWrap.WRAP

        return layout.flexboxLayout(context)
    }


    private fun dieView(sides : Int, count : Int) : TextView
    {
        val die                 = TextViewBuilder()

        die.layoutType          = LayoutType.FLEXBOX
        die.width               = FlexboxLayout.LayoutParams.WRAP_CONTENT
        die.height              = FlexboxLayout.LayoutParams.WRAP_CONTENT

        die.text                = count.toString() + "d" + sides.toString()

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        die.color               = colorOrBlack(colorTheme, entityId)

        die.font                = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        die.sizeSp              = 30f

        return die.textView(context)
    }


    private fun chooseDiceTextView() : TextView
    {
        val die                 = TextViewBuilder()

        die.layoutType          = LayoutType.FLEXBOX
        die.width               = FlexboxLayout.LayoutParams.WRAP_CONTENT
        die.height              = FlexboxLayout.LayoutParams.WRAP_CONTENT

        die.text                = "Choose dice\u2026"

//        val colorTheme  = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_26"))))
//        die.color               = SheetManager.color(sheetUIContext.sheetId, colorTheme)
        die.color               = Color.WHITE

        die.font                = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        die.sizeSp              = 30f

        return die.textView(context)
    }


    private fun diePlusView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val icon            = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType   = LayoutType.FLEXBOX
        layout.width        = FlexboxLayout.LayoutParams.WRAP_CONTENT
        layout.height       = FlexboxLayout.LayoutParams.WRAP_CONTENT

        layout.gravity      = Gravity.CENTER

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 25
        icon.heightDp           = 25
        icon.weight             = 1f

        icon.margin.leftDp      = 5f
        icon.margin.rightDp     = 5f

        icon.image = R.drawable.icon_plus_sign

        val bsButtonColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_17")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_15"))))
        icon.color              = colorOrBlack(bsButtonColorTheme, entityId)


        return layout.linearLayout(context)
    }

//
//    private fun undoButtonView() : LinearLayout
//    {
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout          = LinearLayoutBuilder()
//        val icon            = ImageViewBuilder()
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.layoutType       = LayoutType.RELATIVE
//        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
//        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT
//
//        layout.addRule(RelativeLayout.ALIGN_PARENT_END)
////        layout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//
////        layout.padding.topDp    = 12f
////        layout.padding.bottomDp = 12f
//        layout.padding.leftDp   = 12f
////        layout.padding.rightDp  = 12f
//
////        layout.gravity          = Gravity.CENTER
//
//        layout.margin.rightDp   = 10f
//
//        layout.onClick          = View.OnClickListener { this.undo() }
//
//        layout.child(icon)
//
//        // (3) Icon
//        // -------------------------------------------------------------------------------------
//
//        icon.widthDp          = 26
//        icon.heightDp         = 26
//        icon.weight           = 1f
//
//        icon.image            = R.drawable.icon_undo
//
//        val undoColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_15"))))
//        icon.color            = SheetManager.color(sheetUIContext.sheetId, undoColorTheme)
//
//        return layout.linearLayout(sheetUIContext.context)
//    }


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


    private fun diceButtonView(label : String, onClick : View.OnClickListener) : TextView
    {
        val dice                = TextViewBuilder()

        dice.width              = 0
        dice.height             = LinearLayout.LayoutParams.MATCH_PARENT
        dice.weight             = 1f

        dice.gravity            = Gravity.CENTER

        dice.margin.leftDp      = 1f
        dice.margin.rightDp     = 1f

        dice.text               = label

        dice.sizeSp             = 20f

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        dice.color              = colorOrBlack(textColorTheme, entityId)

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
//        dice.backgroundColor    = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)
        dice.backgroundColor    = Color.WHITE

        dice.corners            = Corners(2.0, 2.0, 2.0, 2.0)

        dice.onClick            = onClick

        return dice.textView(context)
    }



    private fun keyPadRowViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
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

        // d3
        val d3OnClick = View.OnClickListener {
            this.incDieCount(3)
        }
        layout.addView(this.diceButtonView("d3", d3OnClick))

        // d4
        val d4OnClick = View.OnClickListener {
            this.incDieCount(4)
        }
        layout.addView(this.diceButtonView("d4", d4OnClick))

        // d6
        val d6OnClick = View.OnClickListener {
            this.incDieCount(6)
        }
        layout.addView(this.diceButtonView("d6", d6OnClick))

        // d8
        val d8OnClick = View.OnClickListener {
            this.incDieCount(8)
        }
        layout.addView(this.diceButtonView("d8", d8OnClick))

        return layout
    }


    private fun keyPadRow2View() : LinearLayout
    {
        val layout = this.keyPadRowViewLayout()

        // d10
        val d10OnClick = View.OnClickListener {
            this.incDieCount(10)
        }
        layout.addView(this.diceButtonView("d10", d10OnClick))

        // d12
        val d12OnClick = View.OnClickListener {
            this.incDieCount(12)
        }
        layout.addView(this.diceButtonView("d12", d12OnClick))

        // d20
        val d20OnClick = View.OnClickListener {
            this.incDieCount(20)
        }
        layout.addView(this.diceButtonView("d20", d20OnClick))

        // d100
        val d100OnClick = View.OnClickListener {
            this.incDieCount(100)
        }
        layout.addView(this.diceButtonView("d100", d100OnClick))

        return layout
    }


    private fun keyPadRow3View() : LinearLayout
    {
        val layout = this.keyPadRowViewLayout()

        // Roll Button
        layout.addView(this.rollButtonView())

        // Settings Button
        layout.addView(this.settingsButtonView())

        // Done Button
        layout.addView(this.doneButtonView())

        return layout
    }


    // ----------------------------------------------------------------------------------------
    // SPECIAL BUTTONS
    // ----------------------------------------------------------------------------------------

    private fun rollButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()
        val label               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight           = 2f

        layout.orientation      = LinearLayout.VERTICAL

        layout.gravity          = Gravity.CENTER

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_2"))))
        layout.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        layout.margin.leftDp    = 1f
        layout.margin.rightDp   = 1f

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 25
        icon.heightDp           = 25

        icon.image              = R.drawable.icon_dice_roll_filled

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color              = colorOrBlack(iconColorTheme, entityId)

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId        = R.string.test_roll

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color         = colorOrBlack(labelColorTheme, entityId)

        label.font          = Font.typeface(TextFont.default(),
                                            TextFontStyle.Regular,
                                            context)

        label.sizeSp        = 12f

        return layout.linearLayout(context)
    }


    private fun settingsButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()
        val label               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight           = 2f

        layout.orientation      = LinearLayout.VERTICAL

        layout.gravity          = Gravity.CENTER

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_2"))))
        layout.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        layout.onClick          = View.OnClickListener {
            if (this.rollOptionsVisible) {
                this.optionsView?.visibility = View.GONE
                this.rollOptionsVisible = false
            }
            else {
                this.optionsView?.visibility = View.VISIBLE
                this.rollOptionsVisible = true
            }
        }

        layout.margin.leftDp    = 1f
        layout.margin.rightDp   = 1f

        layout.child(icon)
             .child(label)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 18
        icon.heightDp           = 18

        icon.image              = R.drawable.icon_settings

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        icon.color              = colorOrBlack(iconColorTheme, entityId)

        icon.margin.bottomDp    = 6f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId        = R.string.roll_options

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        label.color         = colorOrBlack(labelColorTheme, entityId)

        label.font          = Font.typeface(TextFont.default(),
                                            TextFontStyle.Regular,
                                            context)

        label.sizeSp        = 12f

        return layout.linearLayout(context)
    }


    private fun doneButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()
        val label               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight           = 3f

        layout.gravity          = Gravity.CENTER

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_green_4")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green_80"))))
        layout.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        layout.corners            = Corners(2.0, 2.0, 2.0, 2.0)

        layout.onClick          = View.OnClickListener {
            val sheetActivity = context as SessionActivity
            val adderDialog = AdderDialog.newInstance(this.currentAdderState(), entityId)
            adderDialog.show(sheetActivity.supportFragmentManager, "")
            dialog.dismiss()
        }

        layout.margin.leftDp    = 2f
        layout.margin.rightDp   = 2f

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 18
        icon.heightDp           = 18

        icon.image              = R.drawable.icon_check_bold

//        val iconColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_14")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        icon.color              = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)
        icon.color              = Color.WHITE

        icon.margin.rightDp     = 4f

        // (3 A) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text          = context.getString(R.string.done).toUpperCase()

//        val labelColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_14")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        label.color         = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)
        label.color         = Color.WHITE

        label.font          = Font.typeface(TextFont.default(),
                                            TextFontStyle.Bold,
                                            context)

        label.sizeSp        = 18f

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // ROLL OPTIONS VIEW
    // -----------------------------------------------------------------------------------------

    private fun rollOptionsView() : LinearLayout
    {
        val layout  = this.rollOptionsViewLayout()

        // Left Button
        layout.addView(this.rollOptionsLeftButtonView())

        // Option
        val optionView = this.rollOptionView()
        this.optionView = optionView
        layout.addView(optionView)

        // Right Button
        layout.addView(this.rollOptionsRightButtonView())

        return layout
    }


    private fun rollOptionsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 50

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.visibility       = View.GONE

//        val colorTheme  = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        return layout.linearLayout(context)
    }


    private fun rollOptionView() : TextView
    {
        val option              = TextViewBuilder()

        option.width            = 0
        option.height           = LinearLayout.LayoutParams.WRAP_CONTENT
        option.weight           = 4f

        option.text             = this.rollOptionList[this.currentOption].toString()

        option.gravity          = Gravity.CENTER

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_blue_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_80"))))
        option.color            = colorOrBlack(colorTheme, entityId)

        option.font             = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        option.sizeSp           = 18f

        return option.textView(context)
    }


    private fun rollOptionsLeftButtonView() : LinearLayout
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


        layout.onClick          = View.OnClickListener { this.showPreviousRollOption() }

        layout.margin.leftDp    = 4f

        layout.child(icon)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 35
        icon.heightDp           = 35

        icon.image              = R.drawable.icon_chevron_left

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_27")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        icon.color              = colorOrBlack(iconColorTheme, entityId)

        return layout.linearLayout(context)
    }


    private fun rollOptionsRightButtonView() : LinearLayout
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

        layout.onClick          = View.OnClickListener { this.showNextRollOption() }

        layout.margin.rightDp   = 4f

        layout.child(icon)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 35
        icon.heightDp           = 35

        icon.image              = R.drawable.icon_chevron_right_bold

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_27")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        icon.color              = colorOrBlack(iconColorTheme, entityId)

        return layout.linearLayout(context)
    }


}
