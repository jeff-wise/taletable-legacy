
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
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.engine.EngineValue
import com.kispoko.tome.model.engine.EngineValueNumber
import com.kispoko.tome.model.entity.StoryWidgetUpdateNumberPart
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.router.Router
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.sheet.*
import com.kispoko.tome.rts.entity.updateVariable
import com.kispoko.tome.util.Util



/**
 * Number Editor
 */
class NumberEditorDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var currentValue : Double?       = null
    private var title        : String?       = null
    private var updateTarget : UpdateTarget? = null
    private var entityId     : EntityId?     = null

    // TODO use update target to get these parameters

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(currentValue : Double,
                        title : String,
                        updateTarget : UpdateTarget,
                        entityId : EntityId) : NumberEditorDialog
        {
            val dialog = NumberEditorDialog()

            val args = Bundle()
            args.putDouble("current_value", currentValue)
            args.putString("title", title)
            args.putSerializable("update_target", updateTarget)
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

        this.currentValue = arguments?.getDouble("current_value")
        this.title        = arguments?.getString("title")
        this.updateTarget = arguments?.getSerializable("update_target") as UpdateTarget
        this.entityId     = arguments?.getSerializable("entity_id") as EntityId

        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val dialogLayout = this.dialogLayout()

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

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val currentValue = this.currentValue
        val updateTarget = this.updateTarget
        val entityId     = this.entityId
        val context      = this.context

        return if (currentValue != null && updateTarget != null && entityId != null && context != null)
        {
            val viewBuilder = NumberEditorViewBuilder(currentValue,
                                                      this.title,
                                                      updateTarget,
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
// NUMBER EDITOR VIEW BUILDER
// ---------------------------------------------------------------------------------------------

class NumberEditorViewBuilder(val currentValue : Double,
                              val title : String?,
                              val updateTarget : UpdateTarget,
                              val dialog : DialogFragment,
                              val entityId : EntityId,
                              val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var valueString = Util.doubleString(currentValue)
    private var valueTextView : TextView? = null


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun appendToValue(numString : String)
    {
        this.valueString += numString
        this.valueTextView?.text = this.valueString
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

//        layout.padding.bottomDp = 5f

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // SCREEN VIEW
    // -----------------------------------------------------------------------------------------

    private fun screenView() : LinearLayout
    {
        val layout = this.valueViewLayout()

        // Title
        layout.addView(this.titleView())

        // Value Row
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


    private fun titleView() : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.MATCH_PARENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.padding.leftDp      = 10f
        name.padding.topDp      = 6f

        if (this.title != null) {
//            name.text = sheetUIContext.context.getString(R.string.edit).toUpperCase() +
//                            " " + this.title.toUpperCase()
            name.text = this.title
        }
        else {
            name.text           = context.getString(R.string.edit_number).toUpperCase()
        }

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_2")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        name.color              = colorOrBlack(colorTheme, entityId)

        name.backgroundColor    = Color.WHITE

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        name.sizeSp             = 15f

        return name.textView(context)
    }


    private fun valueRowView() : LinearLayout
    {
        val layout = this.valueRowViewLayout()

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
        layout.heightDp         = 65

        layout.orientation      = LinearLayout.HORIZONTAL
        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f

        layout.margin.bottomDp  = 2f

        layout.backgroundColor  = Color.WHITE

        return layout.linearLayout(context)
    }


    private fun valueView() : TextView
    {
        val value               = TextViewBuilder()

        value.width             = LinearLayout.LayoutParams.MATCH_PARENT
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT
        value.weight            = 5f

        value.text              = Util.doubleString(this.currentValue)

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        value.color             = colorOrBlack(valueColorTheme, entityId)

        value.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        value.sizeSp            = 30f

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

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.topDp    = 15f
        layout.padding.bottomDp = 15f
        layout.padding.leftDp   = 15f
        layout.padding.rightDp  = 15f

        layout.onClick        = View.OnClickListener {
            if (this.valueString.isNotBlank()) {
                this.valueString = this.valueString.dropLast(1)
                this.valueTextView?.text = this.valueString
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
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


    private fun numberButtonView(label : String,
                                 textSize : Float,
                                 onClick : View.OnClickListener) : TextView
    {
        val number                  = TextViewBuilder()

        number.width                = 0
        number.height               = LinearLayout.LayoutParams.MATCH_PARENT
        number.weight               = 1f

        number.gravity              = Gravity.CENTER

        number.margin.leftDp        = 1f
        number.margin.rightDp       = 1f

        number.text                 = label

        number.sizeSp               = textSize

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_15"))))
        number.color                = colorOrBlack(textColorTheme, entityId)

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//        number.backgroundColor      = colorOrBlack(bgColorTheme, entityId)
        number.backgroundColor      = Color.WHITE

        number.corners              = Corners(1.0, 1.0, 1.0, 1.0)

        number.onClick              = onClick

        return number.textView(context)
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
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_green_4")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green_80"))))
        layout.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        layout.corners              = Corners(2.0, 2.0, 2.0, 2.0)

        layout.margin.leftDp    = 1f
        layout.margin.rightDp   = 1f


        layout.onClick          = View.OnClickListener {
            when (this.updateTarget)
            {
//                is UpdateTargetNumberCell ->
//                {
//                    val numberCellUpdate =
//                            TableWidgetUpdateSetNumberCell(updateTarget.tableWidgetId,
//                                                           updateTarget.cellId,
//                                                           this.valueString.toDouble())
//                    SheetManager.updateSheet(sheetUIContext.sheetId,
//                                             numberCellUpdate,
//                                             sheetUIContext.sheetUI())
//                    dialog.dismiss()
//                }
//                is UpdateTargetPointsWidget ->
//                {
//                    val pointsWidgeUpdate = PointsWidgetUpdateSetCurrentValue(
//                                                        updateTarget.pointsWidgetId,
//                                                        this.valueString.toDouble())
//                    SheetManager.updateSheet(sheetUIContext.sheetId,
//                                             pointsWidgeUpdate,
//                                             sheetUIContext.sheetUI())
//                    dialog.dismiss()
//                }
                is UpdateTargetStoryWidgetPart ->
                {
                    val numberPartUpdate =
                            StoryWidgetUpdateNumberPart(updateTarget.storyWidgetId,
                                                        updateTarget.partIndex,
                                                        this.valueString.toDouble())
                    Log.d("***NUMBER EDITOR DIALOG", "number part update: $numberPartUpdate")
                    Router.send(MessageSheetUpdate(numberPartUpdate))
                    dialog.dismiss()
                }
                is UpdateTargetVariable ->
                {
                    val newValue = EngineValueNumber(this.valueString.toDouble())
                    updateVariable(updateTarget.variableId, newValue, entityId)
                    dialog.dismiss()
                }
//                is UpdateTargetSummationNumberTerm ->
//                {
//                    val message = MessageUpdateSummationNumberTerm(updateTarget.termId,
//                                                                   this.valueString.toDouble())
//                    Router.send(message)
//                    dialog.dismiss()
//                }
            }
        }

        layout.child(icon)


        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 22
        icon.heightDp           = 22

        icon.image              = R.drawable.icon_check_bold

//        val iconColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
        //icon.color              = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)
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
        layout.margin.leftDp    = 1f
        layout.margin.rightDp   = 1f

        return layout.linearLayout(context)
    }


    private fun keyPadRow1View() : LinearLayout
    {
        val layout = this.keyPadRowViewLayout()

        // 1
        val oneOnClick = View.OnClickListener {
            this.appendToValue("1")
        }
        layout.addView(this.numberButtonView("1", 22f, oneOnClick))

        // 2
        val twoOnClick = View.OnClickListener {
            this.appendToValue("2")
        }
        layout.addView(this.numberButtonView("2", 22f, twoOnClick))

        // 3
        val threeOnClick = View.OnClickListener {
            this.appendToValue("3")
        }
        layout.addView(this.numberButtonView("3", 22f, threeOnClick))

        // 0
        val zeroOnClick = View.OnClickListener {
            this.appendToValue("0")
        }
        layout.addView(this.numberButtonView("0", 22f, zeroOnClick))

        return layout
    }


    private fun keyPadRow2View() : LinearLayout
    {
        val layout = this.keyPadRowViewLayout()

        // 4
        val fourOnClick = View.OnClickListener {
            this.appendToValue("4")
        }
        layout.addView(this.numberButtonView("4", 22f, fourOnClick))

        // 5
        val fiveOnClick = View.OnClickListener {
            this.appendToValue("5")
        }
        layout.addView(this.numberButtonView("5", 22f, fiveOnClick))

        // 6
        val sixOnClick = View.OnClickListener {
            this.appendToValue("6")
        }
        layout.addView(this.numberButtonView("6", 22f, sixOnClick))

        // .
        val dotOnClick = View.OnClickListener {
            this.appendToValue(".")
        }
        layout.addView(this.numberButtonView(".", 32f, dotOnClick))

        return layout
    }


    private fun keyPadRow3View() : LinearLayout
    {
        val layout = this.keyPadRowViewLayout()

        // 7
        val sevenOnClick = View.OnClickListener {
            this.appendToValue("7")
        }
        layout.addView(this.numberButtonView("7", 22f, sevenOnClick))

        // 8
        val eightOnClick = View.OnClickListener {
            this.appendToValue("8")
        }
        layout.addView(this.numberButtonView("8", 22f, eightOnClick))

        // 9
        val nineOnClick = View.OnClickListener {
            this.appendToValue("9")
        }
        layout.addView(this.numberButtonView("9", 22f, nineOnClick))

        // Done
        layout.addView(this.doneButtonView())

        return layout
    }



}

