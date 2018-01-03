
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kispoko.tome.R
import com.kispoko.tome.R.id.button
import com.kispoko.tome.R.string.value
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.game.engine.dice.RollModifier
import com.kispoko.tome.model.game.engine.dice.RollSummary
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.sheet.*
import com.kispoko.tome.util.Util
import java.io.Serializable



/**
 * Adder Dialog Fragment
 */
class AdderDialogFragment : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var adderState   : AdderState? = null
    private var sheetContext : SheetContext? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(adderState : AdderState,
                        sheetContext : SheetContext) : AdderDialogFragment
        {
            val dialog = AdderDialogFragment()

            val args = Bundle()
            args.putSerializable("adder_state", adderState)
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

        this.adderState   = arguments.getSerializable("adder_state") as AdderState
        this.sheetContext = arguments.getSerializable("sheet_context") as SheetContext


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(context)

        val sheetContext = this.sheetContext
        if (sheetContext != null)
        {
            val sheetUIContext = SheetUIContext(sheetContext, context)

            val dialogLayout = this.dialogLayout(sheetUIContext)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.window.attributes.windowAnimations = R.style.DialogAnimation

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

            val adderState = this.adderState

            if (adderState != null)
            {
                val adderEditorView = AdderEditorViewBuilder(adderState,
                                                             sheetUIContext,
                                                             this)
                return adderEditorView.view()
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


// ---------------------------------------------------------------------------------------------
// ADDER STATE
// ---------------------------------------------------------------------------------------------

data class AdderState(val originalValue : Double,
                      val delta : Double,
                      val diceRolls : Set<DiceRoll>,
                      val valueName : String?,
                      val updateTarget : UpdateTarget) : Serializable


// ---------------------------------------------------------------------------------------------
// ADDER EDITOR VIEW
// ---------------------------------------------------------------------------------------------

class AdderEditorViewBuilder(val adderState : AdderState,
                             val sheetUIContext : SheetUIContext,
                             val dialog : DialogFragment)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    private var delta : Double = this.adderState.delta
    private var currentValue : Double = this.adderState.originalValue + this.adderState.delta
    private val history : MutableList<Double> = mutableListOf()


//    private var singleValueTextView : TextView? = null
//    private var explainTextView : TextView? = null

    private var valueView : FlexboxLayout? = null

    private var valueTextView : TextView? = null
    private var modifierTextView : TextView? = null
    private var diceRollsTextView : TextView? = null

    private var rollView : LinearLayout? = null

    private var currentRoll : RollSummary? = null

    private val sheetContext = SheetContext(sheetUIContext)
    private val activity = sheetUIContext.context as AppCompatActivity


//    val valueStaticColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//    val valueStaticColor = SheetManager.color(sheetUIContext.sheetId, valueStaticColorTheme)


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    private fun update(delta : Double)
    {
        this.delta += delta
        this.history.add(delta)

        this.currentValue = adderState.originalValue + this.delta

        this.updateValueView()
    }


    private fun undo()
    {
        if (this.history.size > 0)
        {
            val lastDelta = this.history.last()
            this.history.removeAt(this.history.size -1)
            this.currentValue -= lastDelta
            this.delta -= lastDelta
            this.updateValueView()
        }
    }


    private fun updateValueView()
    {
        val currentDelta = this.delta
        if (this.delta >= 0) {
            this.modifierTextView?.text = " + ${Util.doubleString(currentDelta)}"
        } else {
            this.modifierTextView?.text = " - ${Util.doubleString(Math.abs(currentDelta))}"
        }
        //this.modifierTextView?.text = " + ${Util.doubleString(this.delta)}"


        if (this.adderState.diceRolls.isEmpty())
            this.valueTextView?.text = Util.doubleString(this.currentValue)
        else
            this.valueTextView?.text = this.diceRoll().rangeString(this.adderState.originalValue)

//            singleValueTextView?.text = Util.doubleString(this.currentValue)
//
//            explainTextView?.text = explainString
//        }
//        else
//        {
//            modifierTextView?.text = " + ${Util.doubleString(this.delta)}"
//        }
    }


    private fun createValueView()
    {
        val currentDelta = this.delta
        if (this.delta >= 0) {
            this.modifierTextView?.text = " + ${Util.doubleString(currentDelta)}"
        } else {
            this.modifierTextView?.text = " - ${Util.doubleString(Math.abs(currentDelta))}"
        }

        val valueTextView = this.valueTextView()
        this.valueView?.addView(valueTextView)
        this.valueTextView = valueTextView

        if (this.adderState.diceRolls.isEmpty())
            this.valueTextView?.text = Util.doubleString(this.currentValue)
        else
            this.valueTextView?.text = this.diceRoll().rangeString(this.adderState.originalValue)
        //this.valueTextView?.text = Util.doubleString(this.currentValue)


//            val valuePartView = this.valuePartView(Util.doubleString(this.currentValue))
//            this.singleValueTextView = valuePartView
//            this.valueView?.addView(valuePartView)
//
//            val explainView = this.explainView()
//            val explainString = Util.doubleString(this.adderState.originalValue) + " + " +
//                                    Util.doubleString(this.delta)
//            explainView.text = explainString
//            this.explainTextView = explainView
//            this.valueView?.addView(explainView)
//        }
//        else
//        {
            // Original Value
//            val originalPartView = this.valuePartView(Util.doubleString(this.adderState.originalValue))
//            this.originalValueTextView = originalPartView
//            this.valueView?.addView(originalPartView)
//            this.originalValueTextView?.setTextColor(valueStaticColor)
//
//            // Dice
//            adderState.diceRolls.forEach {
//                this.valueView?.addView(this.valuePartView(" + " + it.toString()))
//            }
//
//            // Modifier Value
//            val modifierPartView = this.valuePartView("")
//            this.modifierTextView = modifierPartView
//            this.valueView?.addView(modifierPartView)
//
//            if (this.delta != 0.0) {
//                modifierPartView.text = " + ${Util.doubleString(this.delta)}"
//            }
//        }
    }


    private fun currentAdderState() : AdderState =
            adderState.copy(delta = this.delta)


    // -----------------------------------------------------------------------------------------
    // ROLL
    // -----------------------------------------------------------------------------------------


    private fun diceRoll() : DiceRoll
    {
        var diceRoll = this.adderState.diceRolls.fold(DiceRoll(),
                                                      {roll1, roll2 -> roll1.add(roll2)})

        if (this.delta != 0.0)
            diceRoll = diceRoll.addModifier(RollModifier(this.delta))

        return diceRoll
    }

    private fun roll()
    {
        if (this.adderState.diceRolls.isNotEmpty())
        {
            val diceRoll = this.diceRoll()

            val rollSummary = diceRoll.rollSummary()
            this.currentRoll = rollSummary

            this.valueView?.removeAllViews()
            this.valueView?.addView(this.rollResultView(rollSummary.value.toString()))
            // this.valueView?.addView(this.valueTextView)

//            this.rollView?.visibility = View.VISIBLE
//            this.rollView?.removeAllViews()
//            this.rollView?.addView(rollResultView)
        }
    }


    // -----------------------------------------------------------------------------------------
    // FINISH
    // -----------------------------------------------------------------------------------------

    private fun finishWithResult()
    {
        var finalValue = 0.0
        val currentRoll = this.currentRoll
        if (this.adderState.diceRolls.isEmpty())
            finalValue = this.currentValue
        else if (currentRoll != null)
            finalValue = currentRoll.value.toDouble()

        when (this.adderState.updateTarget)
        {
            is UpdateTargetPointsWidget ->
            {
                val pointsWidgeUpdate =
                        PointsWidgetUpdateSetCurrentValue(
                                adderState.updateTarget.pointsWidgetId,
                                finalValue)
                SheetManager.updateSheet(sheetUIContext.sheetId,
                                         pointsWidgeUpdate,
                                         sheetUIContext.sheetUI())
            }
            is UpdateTargetNumberCell ->
            {
                val numberCellUpdate =
                        TableWidgetUpdateSetNumberCell(adderState.updateTarget.tableWidgetId,
                                                       adderState.updateTarget.cellId,
                                                       finalValue)
                SheetManager.updateSheet(sheetUIContext.sheetId,
                                         numberCellUpdate,
                                         sheetUIContext.sheetUI())
            }
            is UpdateTargetStoryWidgetPart ->
            {
                val numberPartUpdate =
                        StoryWidgetUpdateNumberPart(adderState.updateTarget.storyWidgetId,
                                                    adderState.updateTarget.partIndex,
                                                    finalValue)
                SheetManager.updateSheet(sheetUIContext.sheetId,
                                         numberPartUpdate,
                                         sheetUIContext.sheetUI())
            }
        }

        dialog.dismiss()
    }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

//        val rollView = this.rollView()
//        this.rollView = rollView
//        layout.addView(rollView)

        layout.addView(this.screenView())

        layout.addView(this.buttonsView())

        this.createValueView()

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.padding.bottomDp = 10f

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // SCREEN VIEW
    // -----------------------------------------------------------------------------------------

    private fun screenView() : LinearLayout
    {
        val layout = this.screenViewLayout()

        if (adderState.valueName != null)
            layout.addView(this.valueNameView(adderState.valueName))

        layout.addView(this.valueRowView())

        layout.addView(this.equationRowView())

        return layout
    }


    private fun screenViewLayout() : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.VERTICAL

        return layout.linearLayout(this.sheetUIContext.context)
    }


    // Name
    // -----------------------------------------------------------------------------------------

    private fun valueNameView(valueName : String) : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.MATCH_PARENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.margin.topDp        = 4f
        name.margin.leftDp        = 4f
        name.margin.rightDp        = 4f

        name.padding.topDp       = 5f
        name.padding.leftDp      = 8f

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
        name.backgroundColor    = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        name.text               = valueName // .toLowerCase() // .toUpperCase()

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        name.color              = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        name.font               = Font.typeface(TextFont.default(),
                                            TextFontStyle.Regular,
                                            sheetUIContext.context)

        name.sizeSp             = 15f

        return name.textView(this.sheetUIContext.context)
    }


    // Value Row View
    // -----------------------------------------------------------------------------------------

    private fun valueRowView() : LinearLayout
    {
        val layout = this.valueRowViewLayout()

        // Value
        val valueView = this.valueView()
        this.valueView = valueView
        layout.addView(valueView)

        // Undo
        layout.addView(this.undoButtonView())

        return layout
    }


    private fun valueRowViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity          = Gravity.CENTER_VERTICAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.margin.leftDp    = 4f
        layout.margin.rightDp   = 4f
        layout.margin.bottomDp  = 2f

        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 16f
        layout.padding.bottomDp   = 2f

        layout.corners      = Corners(1.0, 1.0, 1.0, 1.0)

        return layout.linearLayout(this.sheetUIContext.context)
    }


    // Name
    // -----------------------------------------------------------------------------------------

    private fun valueView() : FlexboxLayout
    {
        val layout = FlexboxLayoutBuilder()

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight           = 5f

        layout.wrap             = FlexWrap.WRAP

        return layout.flexboxLayout(sheetUIContext.context)
    }


    private fun valueTextView() : TextView
    {
        val value               = TextViewBuilder()

        value.layoutType        = LayoutType.FLEXBOX
        value.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
        value.color             = SheetManager.color(sheetUIContext.sheetId, valueColorTheme)

        value.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        value.sizeSp            = 32f

        return value.textView(sheetUIContext.context)
    }


    private fun rollResultView(resultString : String) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()

        val iconLayout          = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        val value               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType       = LayoutType.FLEXBOX
        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.child(iconLayout)
              .child(value)

        // (3 A) Icon Layout
        // -------------------------------------------------------------------------------------

        iconLayout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        iconLayout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        iconLayout.margin.rightDp   = 5f

        iconLayout.layoutGravity    = Gravity.CENTER_VERTICAL

        iconLayout.child(icon)

        // (3 B) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 25
        icon.heightDp           = 25

        icon.image              = R.drawable.icon_dice_roll_filled

        val iconColorTheme      = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color              = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        // (4) Value
        // -------------------------------------------------------------------------------------

        value.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        value.text              = resultString

        value.layoutGravity     = Gravity.CENTER_VERTICAL

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        value.color             = SheetManager.color(sheetUIContext.sheetId, valueColorTheme)

        value.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        value.sizeSp            = 32f

        value.margin.rightDp    = 15f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun undoButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val icon            = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.onClick      = View.OnClickListener {
            this.undo()
        }

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp          = 26
        icon.heightDp         = 26
        icon.weight           = 1f

        icon.image            = R.drawable.icon_undo

        val undoColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_15"))))
        icon.color            = SheetManager.color(sheetUIContext.sheetId, undoColorTheme)

        return layout.linearLayout(sheetUIContext.context)
    }




    // Equation Row View
    // -----------------------------------------------------------------------------------------

    private fun equationRowView() : LinearLayout
    {
        val layout = this.equationRowViewLayout()

        // Equation
        val equationView = this.equationView()

        // Original Value
        equationView.addView(this.orginalValueTextView())

        // Dice
        adderState.diceRolls.forEach {
            equationView.addView(this.diceRollTextView(" + $it"))
        }

        // Modifier View
        val modifierView = this.modifierTextView()
        this.modifierTextView = modifierView
        equationView.addView(modifierView)

        layout.addView(equationView)

        return layout
    }


    private fun equationRowViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity          = Gravity.CENTER_VERTICAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_2"))))
        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f
        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f

        layout.margin.leftDp    = 4f
        layout.margin.rightDp   = 4f
        layout.margin.bottomDp  = 2f

        layout.corners      = Corners(1.0, 1.0, 1.0, 1.0)

        return layout.linearLayout(this.sheetUIContext.context)
    }


    // Equation View
    // -----------------------------------------------------------------------------------------

    private fun equationView() : FlexboxLayout
    {
        val layout = FlexboxLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.wrap             = FlexWrap.WRAP

        return layout.flexboxLayout(sheetUIContext.context)
    }


    private fun orginalValueTextView() : TextView
    {
        val value               = TextViewBuilder()

        value.layoutType        = LayoutType.FLEXBOX
        value.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        value.layoutGravity     = Gravity.CENTER_VERTICAL

        value.text              = Util.doubleString(this.adderState.originalValue)

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        value.color             = SheetManager.color(sheetUIContext.sheetId, valueColorTheme)

        value.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Light,
                                                sheetUIContext.context)

        value.margin.rightDp    = 3f

        value.corners           = Corners(1.0, 1.0, 1.0, 1.0)

        value.sizeSp            = 20f

        return value.textView(sheetUIContext.context)
    }


    private fun modifierTextView() : TextView
    {
        val value               = TextViewBuilder()

        value.layoutType        = LayoutType.FLEXBOX
        value.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        value.layoutGravity     = Gravity.CENTER_VERTICAL

        val currentDelta = this.delta
        if (currentDelta >= 0) {
            value.text              = " + ${Util.doubleString(currentDelta)}"
        } else {
            value.text              = " - ${Util.doubleString(Math.abs(currentDelta))}"
        }

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("red_70"))))
        value.color             = SheetManager.color(sheetUIContext.sheetId, valueColorTheme)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_4"))))
        value.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        value.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Light,
                                                sheetUIContext.context)

        value.sizeSp            = 20f

        value.padding.rightDp   = 3f

        return value.textView(sheetUIContext.context)
    }


    private fun diceRollTextView(diceRollString : String) : TextView
    {
        val value               = TextViewBuilder()

        value.layoutType        = LayoutType.FLEXBOX
        value.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        value.layoutGravity     = Gravity.CENTER_VERTICAL

        value.text              = diceRollString

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        value.color             = SheetManager.color(sheetUIContext.sheetId, valueColorTheme)

        value.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Light,
                                                sheetUIContext.context)

        value.sizeSp            = 20f

        return value.textView(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // BUTTONS VIEW
    // -----------------------------------------------------------------------------------------

    private fun buttonsView() : LinearLayout
    {
        val layout  = this.buttonsViewLayout()

        layout.addView(this.staticAddView())

        layout.addView(this.dynamicAddView())

        layout.addView(this.bottomRowView())

        return layout
    }


    private fun buttonsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun textButtonView(labelId : Int,
                               onClick : View.OnClickListener,
                               textColor : Int? = null) : TextView
    {
        val button                  = TextViewBuilder()

        button.width                = 0
        button.height               = LinearLayout.LayoutParams.MATCH_PARENT
        button.weight               = 1f

        button.gravity              = Gravity.CENTER

        button.margin.leftDp        = 1f
        button.margin.rightDp       = 1f

        button.textId               = labelId

        button.sizeSp               = 20f

        if (textColor == null) {
            val textColorTheme = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
            button.color = SheetManager.color(sheetUIContext.sheetId, textColorTheme)
        } else {
            button.color = textColor
        }

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
        button.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        button.corners              = Corners(1.0, 1.0, 1.0, 1.0)

        button.onClick              = onClick

        return button.textView(sheetUIContext.context)
    }


    private fun buttonsRowView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 60

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.margin.bottomDp  = 2f
        layout.margin.leftDp    = 2f
        layout.margin.rightDp   = 2f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun dynamicAddView() : LinearLayout
    {
        val layout          = this.buttonsRowView()

        val activity = sheetUIContext.context as AppCompatActivity

        // -X
        val minusNumOnClick = View.OnClickListener {
            val dialog = AddAmountDialogFragment.newInstance(AddOperation.SUBTRACT,
                                                             this.currentAdderState(),
                                                             SheetContext(sheetUIContext))
            dialog.show(activity.supportFragmentManager, "")
            this.dialog.dismiss()
        }
        layout.addView(this.textButtonView(R.string.minus_num, minusNumOnClick))

        // -ndX
        val minusDiceOnClick = View.OnClickListener {
            val dialog = AddDiceDialogFragment.newInstance(DiceOperation.SUBTRACT,
                                                        this.currentAdderState(),
                                                        SheetContext(sheetUIContext))
            dialog.show(activity.supportFragmentManager, "")
            this.dialog.dismiss()
        }
        layout.addView(this.textButtonView(R.string.minus_dice, minusDiceOnClick))

        // +ndX
        val plusDiceOnClick = View.OnClickListener {
            val dialog = AddDiceDialogFragment.newInstance(DiceOperation.ADD,
                                                        this.currentAdderState(),
                                                        SheetContext(sheetUIContext))
            dialog.show(activity.supportFragmentManager, "")
            this.dialog.dismiss()
        }
        layout.addView(this.textButtonView(R.string.plus_dice, plusDiceOnClick))

        // +X
        val plusNumOnClick = View.OnClickListener {
            val dialog = AddAmountDialogFragment.newInstance(AddOperation.ADD,
                                                             this.currentAdderState(),
                                                             SheetContext(sheetUIContext))
            dialog.show(activity.supportFragmentManager, "")
            this.dialog.dismiss()
        }
        layout.addView(this.textButtonView(R.string.plus_num, plusNumOnClick))

        return layout
    }


    private fun staticAddView() : LinearLayout
    {
        val layout          = this.buttonsRowView()

        val blueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
        val blueColor = SheetManager.color(sheetUIContext.sheetId, blueColorTheme)

        // -1
        val minusOneOnClick = View.OnClickListener {
            this.update(-1.0)
        }
        layout.addView(this.textButtonView(R.string.minus_one, minusOneOnClick, null))

        // -10
        val minusTenOnClick = View.OnClickListener {
            this.update(-10.0)
        }
        layout.addView(this.textButtonView(R.string.minus_ten, minusTenOnClick, null))

        // +10
        val plusTenOnClick = View.OnClickListener {
            this.update(10.0)
        }
        layout.addView(this.textButtonView(R.string.plus_ten, plusTenOnClick, null))

        // +1
        val plusOneOnClick = View.OnClickListener {
            this.update(1.0)
        }
        layout.addView(this.textButtonView(R.string.plus_one, plusOneOnClick, null))

        return layout
    }


    // -----------------------------------------------------------------------------------------
    // BOTTOM ROW VIEW
    // -----------------------------------------------------------------------------------------

    private fun bottomRowView() : LinearLayout
    {
        val layout = this.bottomRowViewLayout()

        // History Button
        layout.addView(this.historyButtonView())

        // Calculator Button
        layout.addView(this.calcButtonView())

        // Done Button
        layout.addView(this.actionButtonView())

        return layout
    }


    private fun bottomRowViewLayout() : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 60

        layout.margin.rightDp   = 4f
        layout.margin.leftDp    = 4f

        layout.orientation      = LinearLayout.HORIZONTAL

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun historyButtonView() : LinearLayout
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
        layout.weight           = 2f

        layout.orientation      = LinearLayout.VERTICAL

        layout.gravity          = Gravity.CENTER

        layout.margin.rightDp   = 2f
//        layout.margin.leftDp    = 2f

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_9")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
        layout.backgroundColor   = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners              = Corners(1.0, 1.0, 1.0, 1.0)

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 22
        icon.heightDp       = 22

        icon.image          = R.drawable.icon_history

        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        icon.color          = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId        = R.string.do_previous

        val labelColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        label.color         = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font          = Font.typeface(TextFont.default(),
                                            TextFontStyle.Regular,
                                            sheetUIContext.context)

        label.sizeSp        = 12f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun calcButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()
        val icon        = ImageViewBuilder()
        val label       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = 0
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight               = 2f

        layout.orientation          = LinearLayout.VERTICAL

        layout.gravity              = Gravity.CENTER

        layout.margin.rightDp       = 2f
//        layout.margin.leftDp    = 2f

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_9")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners              = Corners(1.0, 1.0, 1.0, 1.0)

        layout.onClick              = View.OnClickListener {
            val simpleDialog = NumberEditorDialog.newInstance(adderState.originalValue,
                                                              adderState.valueName ?: "",
                                                              adderState.updateTarget,
                                                              sheetContext)
            simpleDialog.show(activity.supportFragmentManager, "")
            dialog.dismiss()
        }

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 22
        icon.heightDp       = 22

        icon.image          = R.drawable.icon_calculator

        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        icon.color          = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId        = R.string.keypad

        val labelColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        label.color         = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font          = Font.typeface(TextFont.default(),
                                            TextFontStyle.Regular,
                                            sheetUIContext.context)

        label.sizeSp        = 12f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun actionButtonView() : LinearLayout =
        if (this.adderState.diceRolls.isNotEmpty())
            rollButtonView()
        else
            doneButtonView()


    private fun doneButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()
        val icon        = ImageViewBuilder()
        val label       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = 0
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight               = 3f

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER

//        layout.margin.rightDp   = 2f
//        layout.margin.leftDp    = 2f

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_green_4")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green_90"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners              = Corners(0.0, 0.0, 0.0, 0.0)

        layout.onClick              = View.OnClickListener {
            this.finishWithResult()
        }

        layout.child(icon)
                .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 18
        icon.heightDp       = 18

        icon.image          = R.drawable.icon_check

//        val iconColorTheme  = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
//        icon.color          = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)
        icon.color          = Color.WHITE

        icon.margin.rightDp = 5f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text          = sheetUIContext.context.getString(R.string.done).toUpperCase()
        //label.textId        = R.string.done

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
        label.color         = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font          = Font.typeface(TextFont.default(),
                                            TextFontStyle.Medium,
                                            sheetUIContext.context)

        label.sizeSp        = 18f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun rollButtonView() : LinearLayout
    {
        val layout          = this.rollButtonLayout()

        val labelLayout     = this.rollButtonLabelLayout()
        labelLayout.addView(this.rollButtonIconView())
        labelLayout.addView(this.rollButtonLabelView())

        layout.addView(labelLayout)
        layout.addView(this.rollButtonMessageView())

        return layout
    }


    private fun rollButtonLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = 0
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight               = 3f

        layout.orientation          = LinearLayout.VERTICAL

        layout.gravity              = Gravity.CENTER

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_green_4")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners              = Corners(1.0, 1.0, 1.0, 1.0)


        layout.onClick              = View.OnClickListener {
            this.roll()
        }

        layout.onLongClick          = View.OnLongClickListener {
            finishWithResult()
            true
        }

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun rollButtonLabelLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun rollButtonIconView() : ImageView
    {
        val icon            = ImageViewBuilder()

        icon.widthDp        = 19
        icon.heightDp       = 19

        icon.image          = R.drawable.icon_dice_roll_filled

        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
        icon.color          = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        icon.margin.rightDp = 5f

        return icon.imageView(sheetUIContext.context)
    }


    private fun rollButtonLabelView() : TextView
    {
        val label               = TextViewBuilder()

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text          = sheetUIContext.context.getString(R.string.roll).toUpperCase()
//        label.textId        = R.string.roll

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
        label.color         = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font          = Font.typeface(TextFont.default(),
                                            TextFontStyle.Bold,
                                            sheetUIContext.context)

        label.sizeSp        = 16.5f

        return label.textView(sheetUIContext.context)
    }


    private fun rollButtonMessageView() : TextView
    {
        val label           = TextViewBuilder()

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

//        label.text          = sheetUIContext.context.getString(R.string.done).toUpperCase()
        label.textId        = R.string.hold_to_accept

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        label.color         = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font          = Font.typeface(TextFont.default(),
                                            TextFontStyle.Regular,
                                            sheetUIContext.context)

        label.sizeSp        = 12f

        return label.textView(sheetUIContext.context)
    }

}


