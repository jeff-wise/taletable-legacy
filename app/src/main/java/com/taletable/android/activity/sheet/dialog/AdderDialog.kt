
package com.taletable.android.activity.sheet.dialog


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AppCompatActivity
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.taletable.android.R
import com.taletable.android.R.string.value
import com.taletable.android.lib.ui.*
import com.taletable.android.model.engine.dice.DiceRoll
import com.taletable.android.model.engine.dice.RollModifier
import com.taletable.android.model.engine.dice.RollSummary
import com.taletable.android.model.engine.variable.VariableId
import com.taletable.android.model.entity.NumberWidgetUpdateValue
import com.taletable.android.model.entity.PointsWidgetUpdateSetCurrentValue
import com.taletable.android.model.entity.StoryWidgetUpdateNumberPart
import com.taletable.android.model.entity.TableWidgetUpdateSetNumberCell
import com.taletable.android.model.sheet.style.*
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.router.Router
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.sheet.*
import com.taletable.android.util.Util
import java.io.Serializable



/**
 * Adder Dialog Fragment
 */
class AdderDialog : BottomSheetDialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var adderState : AdderState? = null
    private var entityId   : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(adderState : AdderState,
                        entityId : EntityId) : AdderDialog
        {
            val dialog = AdderDialog()

            val args = Bundle()
            args.putSerializable("adder_state", adderState)
            args.putSerializable("entity_id", entityId)
            dialog.arguments = args

            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomSheetDialog)

            return dialog
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG FRAGMENT
    // -----------------------------------------------------------------------------------------

//    override fun onCreateDialog(savedInstanceState : Bundle?) : Dialog
//    {
//        // (1) Read State
//        // -------------------------------------------------------------------------------------
//
//        this.adderState = arguments?.getSerializable("adder_state") as AdderState
//        this.entityId   = arguments?.getSerializable("entity_id") as EntityId
//
//
//        // (2) Initialize UI
//        // -------------------------------------------------------------------------------------
//
//        val dialog = Dialog(context)
//
//        val dialogLayout = this.dialogLayout()
//
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//        dialog.window.attributes.windowAnimations = R.style.DialogAnimation
//
//        dialog.setContentView(dialogLayout)
//
//        val window = dialog.window
//        val wlp = window.attributes
//
//        wlp.gravity = Gravity.BOTTOM
//        window.attributes = wlp
//
//        val width  = LinearLayout.LayoutParams.MATCH_PARENT
//        val height = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        dialog.window.setLayout(width, height)
//
//        return dialog
//    }



    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {

        this.adderState = arguments?.getSerializable("adder_state") as AdderState
        this.entityId   = arguments?.getSerializable("entity_id") as EntityId

        val adderState = this.adderState
        val entityId = this.entityId
        val context = this.context

        return if (adderState != null && entityId != null && context != null)
        {
            val adderEditorView = AdderEditorViewBuilder(adderState,
                                                         this,
                                                         entityId,
                                                         context)
            adderEditorView.view()
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
// ADDER STATE
// ---------------------------------------------------------------------------------------------

data class AdderState(val originalValue : Double,
                      val delta : Double,
                      val diceRolls : Set<DiceRoll>,
                      val valueName : String?,
                      val updateTarget : UpdateTarget,
                      val variableId : VariableId?) : Serializable


// ---------------------------------------------------------------------------------------------
// ADDER EDITOR VIEW
// ---------------------------------------------------------------------------------------------

class AdderEditorViewBuilder(val adderState : AdderState,
                             val dialog : DialogFragment,
                             val entityId : EntityId,
                             val context : Context)
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

    private val activity = context as AppCompatActivity


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
            this.valueTextView?.text = this.diceRoll().rangeString(0.0)

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
            this.valueTextView?.text = this.diceRoll().rangeString(0.0)
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
        val diceRoll = this.adderState.diceRolls.fold(DiceRoll(),
                                                      {roll1, roll2 -> roll1.add(roll2)})

        if (this.delta != 0.0)
            diceRoll.addModifier(RollModifier(this.delta))

        diceRoll.addModifier(RollModifier(adderState.originalValue))

        return diceRoll
    }

    private fun roll()
    {
        if (this.adderState.diceRolls.isNotEmpty())
        {
            val diceRoll = this.diceRoll()

            val rollSummary = diceRoll.rollSummary()
            this.currentRoll = rollSummary

            val newValue = rollSummary.value

            this.valueView?.removeAllViews()
            this.valueView?.addView(this.rollResultView(newValue.toString()))
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
                Router.send(MessageSheetUpdate(pointsWidgeUpdate))
            }
            is UpdateTargetNumberWidget ->
            {
                val numberWidgetUpdate = NumberWidgetUpdateValue(adderState.updateTarget.numberWidgetId,
                                                                 finalValue)
                Router.send(MessageSheetUpdate(numberWidgetUpdate))
            }
            is UpdateTargetNumberCell ->
            {
                val numberCellUpdate =
                        TableWidgetUpdateSetNumberCell(adderState.updateTarget.tableWidgetId,
                                                       adderState.updateTarget.cellId,
                                                       finalValue)
                Router.send(MessageSheetUpdate(numberCellUpdate))
            }
            is UpdateTargetStoryWidgetPart ->
            {
                val numberPartUpdate =
                        StoryWidgetUpdateNumberPart(adderState.updateTarget.storyWidgetId,
                                                    adderState.updateTarget.partIndex,
                                                    finalValue)
                Router.send(MessageSheetUpdate(numberPartUpdate))
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

//        layout.corners          = Corners(10.0, 10.0, 0.0, 0.0)

//        layout.padding.bottomDp = 4f

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // SCREEN VIEW
    // -----------------------------------------------------------------------------------------

    private fun screenView() : LinearLayout
    {
        val layout = this.screenViewLayout()

        if (adderState.valueName != null)
            layout.addView(this.headerView(adderState.valueName))

        layout.addView(this.dividerView())

        val mainScreenLayout = this.mainScreenLayout()

        mainScreenLayout.addView(this.valueRowView())

        mainScreenLayout().addView(this.equationRowView())

        layout.addView(mainScreenLayout)

        layout.addView(this.dividerView())

        return layout
    }


    private fun dividerView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))
        layout.backgroundColor  = colorOrBlack(colorTheme, entityId)

        return layout.linearLayout(context)
    }


    private fun mainScreenLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.backgroundColor  = Color.WHITE

        return layout.linearLayout(context)
    }


    private fun screenViewLayout() : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.TRANSPARENT

        layout.backgroundColor  = Color.WHITE

        layout.corners          = Corners(10.0, 10.0, 0.0, 0.0)

        //layout.margin.bottomDp  = 20f

        return layout.linearLayout(context)
    }



    // HEADER
    // -----------------------------------------------------------------------------------------

    private fun headerView(valueName : String) : RelativeLayout
    {
        val layout = this.headerViewLayout()

        layout.addView(this.valueNameView(valueName))

        layout.addView(this.modeSwitcherView())

        return layout
    }


    private fun headerViewLayout() : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f
        layout.padding.topDp    = 12f
        layout.padding.bottomDp = 12f

//        layout.backgroundColor  = Color.TRANSPARENT
//
//        layout.corners          = Corners(10.0, 10.0, 0.0, 0.0)

        return layout.relativeLayout(context)
    }


    // Name
    // -----------------------------------------------------------------------------------------

    private fun valueNameView(valueName : String) : TextView
    {
        val name                = TextViewBuilder()

        name.layoutType         = LayoutType.RELATIVE
        name.width              = RelativeLayout.LayoutParams.WRAP_CONTENT
        name.height             = RelativeLayout.LayoutParams.WRAP_CONTENT

        name.addRule(RelativeLayout.CENTER_VERTICAL)
        name.addRule(RelativeLayout.ALIGN_PARENT_START)

        name.backgroundColor    = Color.WHITE

        name.text               = valueName // .toLowerCase() // .toUpperCase()

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
        name.color              = colorOrBlack(colorTheme, entityId)

        name.font               = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        name.sizeSp             = 19f

        return name.textView(context)
    }


    private fun modeSwitcherView() : LinearLayout
    {
        val layout = this.modeSwitcherViewLayout()

        layout.addView(this.modeSwitchButtonView("ADD", true))

        layout.addView(this.modeSwitchButtonView("SET", false))

        return layout
    }


    private fun modeSwitcherViewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.layoutType   = LayoutType.RELATIVE
        layout.width        = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height       = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.HORIZONTAL

        layout.margin.rightDp   = 2f

        layout.addRule(RelativeLayout.CENTER_VERTICAL)
        layout.addRule(RelativeLayout.ALIGN_PARENT_END)

        return layout.linearLayout(context)
    }



    private fun modeSwitchButtonView(label : String, isSelected : Boolean) : TextView
    {
        val buttonView              = TextViewBuilder()

        buttonView.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        buttonView.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        buttonView.margin.leftDp    = 12f

        buttonView.text             = label

        val defaultColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_24"))))

        val selectedColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_3"))))

        if (isSelected)
            buttonView.color        = colorOrBlack(selectedColorTheme, entityId)
        else
            buttonView.color        = colorOrBlack(defaultColorTheme, entityId)

        buttonView.font             = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Bold,
                                                    context)

        buttonView.sizeSp           = 17f


        return buttonView.textView(context)
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
//        layout.addView(this.undoButtonView())

        return layout
    }


    private fun valueRowViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.backgroundColor  = Color.WHITE

        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f
        layout.padding.bottomDp = 4f
        layout.padding.topDp    = 8f

        return layout.linearLayout(context)
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

        return layout.flexboxLayout(context)
    }


    private fun valueTextView() : TextView
    {
        val value               = TextViewBuilder()

        value.layoutType        = LayoutType.FLEXBOX
        value.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        value.color             = colorOrBlack(valueColorTheme, entityId)

        value.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        value.sizeSp            = 42f

        return value.textView(context)
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
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_3")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color              = colorOrBlack(iconColorTheme, entityId)

        // (4) Value
        // -------------------------------------------------------------------------------------

        value.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        value.text              = resultString

        value.layoutGravity     = Gravity.CENTER_VERTICAL

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        value.color             = colorOrBlack(valueColorTheme, entityId)

        value.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        value.sizeSp            = 32f

        value.margin.rightDp    = 15f

        return layout.linearLayout(context)
    }


    private fun undoButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val iconView        = ImageViewBuilder()
        val labelView       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))
        layout.backgroundColor      = colorOrBlack(bgColorTheme, entityId)

//        layout.corners              = Corners(12.0, 12.0, 12.0, 12.0)

        layout.padding.topDp        = 18f
        layout.padding.bottomDp     = 18f
        layout.padding.leftDp       = 20f
        layout.padding.rightDp      = 20f

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.onClick      = View.OnClickListener {
            this.undo()
        }

        layout.child(iconView)
                .child(labelView)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        iconView.widthDp          = 19
        iconView.heightDp         = 19

        iconView.image            = R.drawable.icon_undo

        val undoColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_16"))))
        iconView.color            = colorOrBlack(undoColorTheme, entityId)
//        iconView.color            = Color.WHITE

        iconView.margin.rightDp     = 5f

        // (3 B) Label View
        // -------------------------------------------------------------------------------------

        labelView.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        labelView.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        labelView.text              = context.getString(R.string.undo).toUpperCase()

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_16"))))
        labelView.color             = colorOrBlack(labelColorTheme, entityId)

        labelView.font              = Font.typeface(TextFont.Roboto,
                                                TextFontStyle.Bold,
                                                context)

        labelView.sizeSp            = 18f


        return layout.linearLayout(context)
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

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))
//        layout.backgroundColor  = colorOrBlack(bgColorTheme, entityId)


        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f

        layout.padding.topDp    = 2f
        layout.padding.bottomDp = 16f

        return layout.linearLayout(context)
    }


    // Equation View
    // -----------------------------------------------------------------------------------------

    private fun equationView() : FlexboxLayout
    {
        val layout = FlexboxLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.wrap             = FlexWrap.WRAP

        return layout.flexboxLayout(context)
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
        value.color             = colorOrBlack(valueColorTheme, entityId)

        value.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))
        value.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        value.padding.leftDp    = 6f
        value.padding.rightDp    = 6f

        value.padding.topDp    = 2f
        value.padding.bottomDp    = 2f

        value.sizeSp            = 18f

        return value.textView(context)
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("red_tint_2"))))
        value.color             = colorOrBlack(valueColorTheme, entityId)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))
        value.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        value.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        value.sizeSp            = 18f

        value.padding.rightDp   = 6f
        value.padding.leftDp    = 3f
        value.padding.topDp    = 2f
        value.padding.bottomDp    = 2f

        return value.textView(context)
    }


    private fun diceRollTextView(diceRollString : String) : TextView
    {
        val value               = TextViewBuilder()

        value.layoutType        = LayoutType.FLEXBOX
        value.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        value.padding.topDp    = 2f
        value.padding.bottomDp    = 2f

        value.layoutGravity     = Gravity.CENTER_VERTICAL

        value.text              = diceRollString

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("red_tint_2"))))
        value.color             = colorOrBlack(valueColorTheme, entityId)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))
        value.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        value.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        value.sizeSp            = 18f

        return value.textView(context)
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

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))
        layout.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        return layout.linearLayout(context)
    }


    private fun textButtonView(labelId : Int,
                               onClick : View.OnClickListener,
                               textColor : Int? = null,
                               textSize : Float? = null) : TextView
    {
        val button                  = TextViewBuilder()

        button.width                = 0
        button.height               = LinearLayout.LayoutParams.MATCH_PARENT
        button.weight               = 1f

        button.gravity              = Gravity.CENTER

        button.margin.leftDp        = 0.5f
        button.margin.rightDp       = 0.5f

        //button.padding.leftDp       = 12f

        button.textId               = labelId

        if (textSize == null)
            button.sizeSp               = 26f
        else
            button.sizeSp               = textSize

        if (textColor == null) {
            val textColorTheme = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
            button.color = colorOrBlack(textColorTheme, entityId)
        } else {
            button.color = textColor
        }

        button.font          = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Regular,
                                            context)

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
        button.backgroundColor      = Color.WHITE

        button.onClick              = onClick

        return button.textView(context)
    }


    private fun addNumberButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val label               = TextViewBuilder()

        val iconLayout          = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = 0
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight               = 2f

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.backgroundColor      = Color.WHITE

        layout.padding.leftDp       = 10f

        layout.onClick              = View.OnClickListener {  }

        layout.child(iconLayout)
              .child(label)

        // (3) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.sizeSp            = 21f

        label.text              = "ADD NUMBER"

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        label.color             = colorOrBlack(textColorTheme, entityId)

        label.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        // (4 A) Icon Layout
        // -------------------------------------------------------------------------------------

        iconLayout.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        iconLayout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        iconLayout.gravity      = Gravity.CENTER
        iconLayout.layoutGravity      = Gravity.CENTER

        iconLayout.child(icon)

        iconLayout.margin.rightDp   = 6f

        icon.widthDp            = 22
        icon.heightDp           = 22

        icon.image              = R.drawable.icon_calculator_outline

        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        icon.color          = colorOrBlack(iconColorTheme, entityId)


        return layout.linearLayout(context)
    }


    private fun addDiceButtonView(labelString : String,
                                  onClick : View.OnClickListener) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val label               = TextViewBuilder()

        val iconLayout          = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = 0
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight               = 2f

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.backgroundColor      = Color.WHITE

        layout.onClick              = onClick

        layout.padding.leftDp       = 10f

        layout.child(iconLayout)
              .child(label)

        // (3) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.sizeSp            = 21f

        label.text              = labelString

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        label.color             = colorOrBlack(textColorTheme, entityId)

        label.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        // (4 A) Icon Layout
        // -------------------------------------------------------------------------------------

        iconLayout.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        iconLayout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        iconLayout.gravity      = Gravity.CENTER
        iconLayout.layoutGravity      = Gravity.CENTER

        iconLayout.child(icon)

        iconLayout.margin.rightDp   = 6f

        icon.widthDp            = 22
        icon.heightDp           = 22

        icon.image              = R.drawable.icon_die

        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        icon.color          = colorOrBlack(iconColorTheme, entityId)


        return layout.linearLayout(context)
    }


    private fun buttonsRowView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 70

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.margin.bottomDp  = 1f
//        layout.margin.leftDp    = 2f
//        layout.margin.rightDp   = 2f

        return layout.linearLayout(context)
    }


    private fun dynamicAddView() : LinearLayout
    {
        val layout          = this.buttonsRowView()

        val activity = context as AppCompatActivity

        layout.addView(this.addNumberButtonView())

        val minusTenOnClick = View.OnClickListener {
            this.update(-10.0)
        }
        layout.addView(this.textButtonView(R.string.minus_ten, minusTenOnClick, null))

        val minusOneOnClick = View.OnClickListener {
            this.update(-1.0)
        }
        layout.addView(this.textButtonView(R.string.minus_one, minusOneOnClick, null))


//        // -X
//        val minusNumOnClick = View.OnClickListener {
//            val dialog = AddAmountDialog.newInstance(AddOperation.SUBTRACT,
//                                                             adderState.valueName ?: "",
//                                                             this.currentAdderState(),
//                                                             entityId)
//            dialog.show(activity.supportFragmentManager, "")
//            this.dialog.dismiss()
//        }
//        layout.addView(this.numberButtonView(false, minusNumOnClick))

//        // +ndX
//        val plusDiceOnClick = View.OnClickListener {
//            val dialog = AddDiceDialog.newInstance(DiceOperation.ADD,
//                                                           this.currentAdderState(),
//                                                           entityId)
//            dialog.show(activity.supportFragmentManager, "")
//            this.dialog.dismiss()
//        }
//        layout.addView(this.addDiceButtonView("+", plusDiceOnClick))
//
//        // +X
//        val plusNumOnClick = View.OnClickListener {
//            val dialog = AddAmountDialog.newInstance(AddOperation.ADD,
//                                                             adderState.valueName ?: "",
//                                                             this.currentAdderState(),
//                                                             entityId)
//            dialog.show(activity.supportFragmentManager, "")
//            this.dialog.dismiss()
//        }
//        layout.addView(this.numberButtonView(true, plusNumOnClick))

        return layout
    }


    private fun staticAddView() : LinearLayout
    {
        val layout          = this.buttonsRowView()

//        val blueColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
//        val blueColor = SheetManager.color(sheetUIContext.sheetId, blueColorTheme)

        // -1

//        // -ndX
        val minusDiceOnClick = View.OnClickListener {
            val dialog = AddDiceDialog.newInstance(DiceOperation.SUBTRACT,
                                                           this.currentAdderState(),
                                                           entityId)
            dialog.show(activity.supportFragmentManager, "")
            this.dialog.dismiss()
        }
        layout.addView(this.addDiceButtonView("ADD DICE", minusDiceOnClick))


//        val plusTenOnClick = View.OnClickListener {
//            this.update(10.0)
//        }
//        layout.addView(this.textButtonView(R.string.plus_ten, plusTenOnClick, null))

//        val plusTenOnClick = View.OnClickListener {
//            this.update(10.0)
//        }
        //layout.addView(this.textButtonView(R.string.plus_ten, plusTenOnClick, null))

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
//        layout.addView(this.historyButtonView())

        // Calculator Button
//        layout.addView(this.calcButtonView())

        // Done Button

        layout.addView(this.undoButtonView())

        layout.addView(this.actionButtonView())

        return layout
    }


    private fun bottomRowViewLayout() : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

//        layout.padding.rightDp  = 8f
        layout.padding.leftDp   = 8f
//        layout.padding.bottomDp = 8f
//        layout.padding.topDp    = 12f

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.END

        layout.backgroundColor  = Color.WHITE

        return layout.linearLayout(context)
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

//        val bgColorTheme  = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_9")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
        layout.backgroundColor   = Color.WHITE

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
        icon.color          = colorOrBlack(iconColorTheme, entityId)

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId        = R.string.do_previous

        val labelColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        label.color         = colorOrBlack(labelColorTheme, entityId)

        label.font          = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Regular,
                                            context)

        label.sizeSp        = 12f

        return layout.linearLayout(context)
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

//        val bgColorTheme  = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_9")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
        layout.backgroundColor      = Color.WHITE

        layout.corners              = Corners(1.0, 1.0, 1.0, 1.0)

        layout.onClick              = View.OnClickListener {
//            val simpleDialog = NumberEditorDialog.newInstance(adderState.originalValue,
//                                                              adderState.valueName ?: "",
//                                                              entityId)
            //simpleDialog.show(activity.supportFragmentManager, "")
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
        icon.color          = colorOrBlack(iconColorTheme, entityId)

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId        = R.string.keypad

        val labelColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        label.color         = colorOrBlack(labelColorTheme, entityId)

        label.font          = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Regular,
                                            context)

        label.sizeSp        = 12f

        return layout.linearLayout(context)
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

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_green_4")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green_tint_3"))))
        layout.backgroundColor      = colorOrBlack(bgColorTheme, entityId)

//        layout.corners              = Corners(12.0, 12.0, 12.0, 12.0)

        layout.onClick              = View.OnClickListener {
            this.finishWithResult()
        }

        //layout.margin.leftDp        = 12f

        layout.padding.topDp        = 18f
        layout.padding.bottomDp     = 18f
        layout.padding.leftDp       = 20f
        layout.padding.rightDp      = 20f

        layout //.child(icon)
                .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 19
        icon.heightDp       = 19

        icon.image          = R.drawable.icon_check_bold

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

        label.text          = "DONE"
        //label.textId        = R.string.done

//        val labelColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
        label.color         = Color.WHITE

        label.font          = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Bold,
                                            context)

        label.sizeSp        = 18f

        return layout.linearLayout(context)
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("green_80"))))
        layout.backgroundColor      = colorOrBlack(bgColorTheme, entityId)

        layout.corners              = Corners(1.0, 1.0, 1.0, 1.0)


        layout.onClick              = View.OnClickListener {
            this.roll()
        }

        layout.onLongClick          = View.OnLongClickListener {
            finishWithResult()
            true
        }

        return layout.linearLayout(context)
    }


    private fun rollButtonLabelLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        return layout.linearLayout(context)
    }


    private fun rollButtonIconView() : ImageView
    {
        val icon            = ImageViewBuilder()

        icon.widthDp        = 19
        icon.heightDp       = 19

        icon.image          = R.drawable.icon_dice_roll_filled

//        val iconColorTheme  = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
        icon.color          = Color.WHITE

        icon.margin.rightDp = 5f

        return icon.imageView(context)
    }


    private fun rollButtonLabelView() : TextView
    {
        val label               = TextViewBuilder()

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text          = context.getString(R.string.roll).toUpperCase()
//        label.textId        = R.string.roll

//        val labelColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
        label.color         = Color.WHITE

        label.font          = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Bold,
                                            context)

        label.sizeSp        = 18f

        return label.textView(context)
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
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        label.color         = colorOrBlack(labelColorTheme, entityId)

        label.font          = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Regular,
                                            context)

        label.sizeSp        = 14f

        return label.textView(context)
    }

}


