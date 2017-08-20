
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
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.game.engine.dice.RollModifier
import com.kispoko.tome.model.game.engine.dice.RollPartSummary
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
    private var currentValue : Double = this.adderState.originalValue
    private val history : MutableList<Double> = mutableListOf()

    private var valueView : FlexboxLayout? = null

    private var singleValueTextView : TextView? = null
    private var explainTextView : TextView? = null

    private var originalValueTextView : TextView? = null
    private var modifierTextView : TextView? = null

    private var rollView : LinearLayout? = null

    private var currentRoll : RollSummary? = null


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
            this.updateValueView()
        }
    }


    private fun updateValueView()
    {
        if (this.adderState.diceRolls.isEmpty())
        {
            singleValueTextView?.text = Util.doubleString(this.currentValue)

            val explainString = Util.doubleString(this.adderState.originalValue) + " + " +
                    Util.doubleString(this.delta)

            explainTextView?.text = explainString
        }
        else
        {
            modifierTextView?.text = Util.doubleString(this.delta) + " + "
        }
    }


    private fun createValueView()
    {
        if (this.adderState.diceRolls.isEmpty())
        {
            val valuePartView = this.valuePartView(Util.doubleString(this.currentValue))
            this.singleValueTextView = valuePartView
            this.valueView?.addView(valuePartView)

            val explainView = this.explainView()
            val explainString = Util.doubleString(this.adderState.originalValue) + " + " +
                                    Util.doubleString(this.delta)
            explainView.text = explainString
            this.explainTextView = explainView
            this.valueView?.addView(explainView)

        }
        else
        {
            // Dice
            adderState.diceRolls.forEach {
                this.valueView?.addView(this.valuePartView(it.toString() + " + "))
            }

            // Modifier Value
            val modifierPartView = this.valuePartView("")
            this.modifierTextView = modifierPartView
            this.valueView?.addView(modifierPartView)

            if (this.delta != 0.0) {
                modifierPartView.text = Util.doubleString(this.delta) + " + "
            }

            // Original Value
            val originalPartView = this.valuePartView(Util.doubleString(this.adderState.originalValue))
            this.originalValueTextView = originalPartView
            this.valueView?.addView(originalPartView)
        }
    }


    private fun currentAdderState() : AdderState =
            adderState.copy(delta = this.delta)


    // -----------------------------------------------------------------------------------------
    // ROLL
    // -----------------------------------------------------------------------------------------

    private fun roll()
    {
        if (this.adderState.diceRolls.isNotEmpty())
        {
            var diceRoll = this.adderState.diceRolls.fold(DiceRoll(),
                                                          {roll1, roll2 -> roll1.add(roll2)})

            if (this.delta != 0.0)
                diceRoll = diceRoll.addModifier(RollModifier(this.delta))

            val rollSummary = diceRoll.rollSummary()
            this.currentRoll = rollSummary
            val rollResultView = this.rollResultView(rollSummary)

            this.rollView?.visibility = View.VISIBLE
            this.rollView?.removeAllViews()
            this.rollView?.addView(rollResultView)
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
                SheetManager.updateSheet(sheetUIContext.sheetId, pointsWidgeUpdate)
            }
            is UpdateTargetNumberCell ->
            {
                val numberCellUpdate =
                        TableWidgetUpdateSetNumberCell(adderState.updateTarget.tableWidgetId,
                                                       adderState.updateTarget.cellId,
                                                       finalValue)
                SheetManager.updateSheet(sheetUIContext.sheetId, numberCellUpdate)
            }
            is UpdateTargetStoryWidgetPart ->
            {
                val numberPartUpdate =
                        StoryWidgetUpdateNumberPart(adderState.updateTarget.storyWidgetId,
                                                    adderState.updateTarget.partIndex,
                                                    finalValue)
                SheetManager.updateSheet(sheetUIContext.sheetId, numberPartUpdate)
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

        val rollView = this.rollView()
        this.rollView = rollView
        layout.addView(rollView)

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
        val layout = this.screenViewLayout()

        if (adderState.valueName != null)
            layout.addView(this.valueNameView(adderState.valueName))

        layout.addView(this.valueRowView())

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

        return name.textView(this.sheetUIContext.context)
    }


    // Name
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
        layout.heightDp         = 70

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.leftDp   = 14f
        layout.padding.rightDp  = 20f

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


    private fun valuePartView(valueString : String) : TextView
    {
        val value               = TextViewBuilder()

        value.layoutType        = LayoutType.FLEXBOX
        value.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        value.text              = valueString

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        value.color             = SheetManager.color(sheetUIContext.sheetId, valueColorTheme)

        value.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Light,
                                                sheetUIContext.context)

        value.sizeSp            = 32f

        return value.textView(sheetUIContext.context)
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color            = SheetManager.color(sheetUIContext.sheetId, undoColorTheme)

        return layout.linearLayout(sheetUIContext.context)
    }


    // Explain View
    // -----------------------------------------------------------------------------------------

    private fun explainView() : TextView
    {
        val value               = TextViewBuilder()

        value.layoutType        = LayoutType.FLEXBOX
        value.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height            = LinearLayout.LayoutParams.MATCH_PARENT

        value.layoutGravity     = Gravity.BOTTOM
        value.gravity     = Gravity.BOTTOM

//        value.text              = Util.doubleString(this.adderState.value) + " + " +
//                                    Util.doubleString(this.delta)

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        value.backgroundColor   = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        value.color             = SheetManager.color(sheetUIContext.sheetId, valueColorTheme)

        value.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Light,
                                                sheetUIContext.context)

        value.corners           = Corners(TopLeftCornerRadius(1f),
                                          TopRightCornerRadius(1f),
                                          BottomRightCornerRadius(1f),
                                          BottomLeftCornerRadius(1f))

        value.margin.leftDp     = 20f

//        value.padding.bottomDp  = 4f

//        value.padding.leftDp     = 10f
//        value.padding.rightDp    = 10f

        value.sizeSp            = 32f

        return value.textView(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // BUTTONS VIEW
    // -----------------------------------------------------------------------------------------

    private fun buttonsView() : LinearLayout
    {
        val layout  = this.buttonsViewLayout()

        layout.addView(this.dynamicAddView())

        layout.addView(this.staticAddView())

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
                               onClick : View.OnClickListener) : TextView
    {
        val button                  = TextViewBuilder()

        button.width                = 0
        button.height               = LinearLayout.LayoutParams.MATCH_PARENT
        button.weight               = 1f

        button.gravity              = Gravity.CENTER

        button.margin.leftDp        = 2f
        button.margin.rightDp       = 2f

        button.textId               = labelId

        button.sizeSp               = 16f

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        button.color                = SheetManager.color(sheetUIContext.sheetId, textColorTheme)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        button.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        button.corners              = Corners(TopLeftCornerRadius(1f),
                                              TopRightCornerRadius(1f),
                                              BottomRightCornerRadius(1f),
                                              BottomLeftCornerRadius(1f))

        button.onClick              = onClick

        return button.textView(sheetUIContext.context)
    }


    private fun buttonsRowView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 60

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.margin.bottomDp  = 5f
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
                                                             SheetContext(sheetUIContext))
            dialog.show(activity.supportFragmentManager, "")
            this.dialog.dismiss()
        }
        layout.addView(this.textButtonView(R.string.minus_num, minusNumOnClick))

        // -ndX
        val minusDiceOnClick = View.OnClickListener {
            val dialog = DiceDialogFragment.newInstance(DiceOperation.SUBTRACT,
                                                        this.currentAdderState(),
                                                        SheetContext(sheetUIContext))
            dialog.show(activity.supportFragmentManager, "")
            this.dialog.dismiss()
        }
        layout.addView(this.textButtonView(R.string.minus_dice, minusDiceOnClick))

        // +ndX
        val plusDiceOnClick = View.OnClickListener {
            val dialog = DiceDialogFragment.newInstance(DiceOperation.ADD,
                                                        this.currentAdderState(),
                                                        SheetContext(sheetUIContext))
            dialog.show(activity.supportFragmentManager, "")
            this.dialog.dismiss()
        }
        layout.addView(this.textButtonView(R.string.plus_dice, plusDiceOnClick))

        // +X
        val plusNumOnClick = View.OnClickListener {
            val dialog = AddAmountDialogFragment.newInstance(AddOperation.ADD,
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

        // -1
        val minusOneOnClick = View.OnClickListener {
            this.update(-1.0)
        }
        layout.addView(this.textButtonView(R.string.minus_one, minusOneOnClick))

        // -10
        val minusTenOnClick = View.OnClickListener {
            this.update(-10.0)
        }
        layout.addView(this.textButtonView(R.string.minus_ten, minusTenOnClick))

        // +10
        val plusTenOnClick = View.OnClickListener {
            this.update(10.0)
        }
        layout.addView(this.textButtonView(R.string.plus_ten, plusTenOnClick))

        // +1
        val plusOneOnClick = View.OnClickListener {
            this.update(1.0)
        }
        layout.addView(this.textButtonView(R.string.plus_one, plusOneOnClick))

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

        layout.margin.rightDp   = 4f
//        layout.margin.leftDp    = 2f

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

        icon.widthDp        = 22
        icon.heightDp       = 22

        icon.image          = R.drawable.icon_history

        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color          = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId        = R.string.use_previous

        val labelColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color         = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font          = Font.typeface(TextFont.FiraSans,
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

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight           = 2f

        layout.orientation      = LinearLayout.VERTICAL

        layout.gravity          = Gravity.CENTER

        layout.margin.rightDp   = 4f
//        layout.margin.leftDp    = 2f

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

        icon.widthDp        = 22
        icon.heightDp       = 22

        icon.image          = R.drawable.icon_calculator

        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color          = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners              = Corners(TopLeftCornerRadius(1f),
                                           TopRightCornerRadius(1f),
                                           BottomRightCornerRadius(1f),
                                           BottomLeftCornerRadius(1f))

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

        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color          = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        icon.margin.rightDp = 5f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

//        label.text          = sheetUIContext.context.getString(R.string.done).toUpperCase()
        label.textId        = R.string.done

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color         = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font          = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            sheetUIContext.context)

        label.sizeSp        = 17.5f

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners              = Corners(TopLeftCornerRadius(1f),
                                              TopRightCornerRadius(1f),
                                              BottomRightCornerRadius(1f),
                                              BottomLeftCornerRadius(1f))


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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color         = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font          = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color         = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font          = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            sheetUIContext.context)

        label.sizeSp        = 12f

        return label.textView(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // ROLL VIEW
    // -----------------------------------------------------------------------------------------

    private fun rollView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.visibility       = View.GONE

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun rollResultView(rollSummary : RollSummary) : LinearLayout
    {
        val layout = this.rollResultViewLayout()

        layout.addView(this.rollValueView(rollSummary.value))

        layout.addView(this.rollSummaryView(rollSummary.parts))

        return layout
    }


    private fun rollResultViewLayout() : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.HORIZONTAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity              = Gravity.CENTER_VERTICAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_11")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.padding.topDp        = 12f
        layout.padding.bottomDp     = 12f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun rollValueView(rollValue : Int) : TextView
    {
        val value = TextViewBuilder()

        value.width                 = 0
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT
        value.weight                = 1f

        value.gravity               = Gravity.CENTER

        value.text                  = rollValue.toString()

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        value.color                 = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        value.font                  = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Light,
                                                    sheetUIContext.context)

        value.sizeSp                = 25f

        return value.textView(sheetUIContext.context)
    }


    private fun rollSummaryView(partSummaries : List<RollPartSummary>) : FlexboxLayout
    {
        val layout = FlexboxLayoutBuilder()

        layout.width                    = 0
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight                   = 3.5f

        layout.contentAlignment         = AlignContent.CENTER

        partSummaries.forEach {
            layout.child(this.rollPartSummaryView(it))
        }

        return layout.flexboxLayout(sheetUIContext.context)
    }


    private fun rollPartSummaryView(rollPartSummary : RollPartSummary) : LinearLayoutBuilder
    {

        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val value           = TextViewBuilder()
        val dice            = TextViewBuilder()
        val description     = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType           = LayoutType.FLEXBOX
        layout.width                = FlexboxLayout.LayoutParams.WRAP_CONTENT
        layout.height               = FlexboxLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.margin.leftDp        = 5f
        layout.margin.rightDp       = 5f

        layout.padding.leftDp       = 6f
        layout.padding.rightDp      = 6f
        layout.padding.topDp        = 3f
        layout.padding.bottomDp     = 3f

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners               = Corners(TopLeftCornerRadius(2f),
                                               TopRightCornerRadius(2f),
                                               BottomRightCornerRadius(2f),
                                               BottomLeftCornerRadius(2f))

        if (rollPartSummary.tag.isNotBlank())
            layout.child(description)

        if (rollPartSummary.dice.isNotBlank())
            layout.child(dice)

        layout.child(value)

        // (3 A) Value
        // -------------------------------------------------------------------------------------

        value.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        value.text                  = rollPartSummary.value.toString()

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        value.color                 = SheetManager.color(sheetUIContext.sheetId, valueColorTheme)

        value.sizeSp                = 13.5f

        value.font                  = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    sheetUIContext.context)

        // (3 B) Dice
        // -------------------------------------------------------------------------------------

        dice.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
        dice.height                 = LinearLayout.LayoutParams.WRAP_CONTENT

        dice.text                   = rollPartSummary.dice

        dice.margin.rightDp         = 4f

        val diceColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        dice.color                  = SheetManager.color(sheetUIContext.sheetId, diceColorTheme)

        dice.sizeSp                 = 12f

        dice.font                   = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    sheetUIContext.context)

        // (3 C) Description
        // -------------------------------------------------------------------------------------

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        description.text            = rollPartSummary.tag

        description.margin.rightDp  = 5f

        val descColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        description.color           = SheetManager.color(sheetUIContext.sheetId, descColorTheme)

        description.font            = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    sheetUIContext.context)

        description.sizeSp          = 12f

        return layout
    }

}


