
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
import android.widget.ScrollView
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexboxLayout
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.game.engine.dice.RollPartSummary
import com.kispoko.tome.model.game.engine.dice.RollSummary
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext



/**
 * Dice Roller Dialog
 */
class DiceRollerDialogFragment : DialogFragment()
{


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var diceRoll        : DiceRoll? = null
    private var sheetContext    : SheetContext? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(diceRoll : DiceRoll,
                        sheetContext : SheetContext) : DiceRollerDialogFragment
        {
            val dialog = DiceRollerDialogFragment()

            val args = Bundle()
            args.putSerializable("dice_roll", diceRoll)
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

        this.diceRoll     = arguments.getSerializable("dice_roll") as DiceRoll
        this.sheetContext = arguments.getSerializable("sheet_context") as SheetContext


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val sheetContext = this.sheetContext
        if (sheetContext != null)
        {
            val dialogLayout = this.dialogLayout(context)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.setContentView(dialogLayout)

            val width  = context.resources.getDimension(R.dimen.action_dialog_width)
            val height = LinearLayout.LayoutParams.WRAP_CONTENT

            dialog.window.setLayout(width.toInt(), height)
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

            val diceRoll = this.diceRoll

            if (diceRoll != null)
                return DiceRollerView.view(diceRoll, sheetUIContext)
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

    fun dialogLayout(context : Context) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(context)
    }

}



object DiceRollerView
{


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(diceRoll : DiceRoll, sheetUIContext : SheetUIContext) : View
    {
        val layout = this.viewLayout(sheetUIContext)

        // Header
        val diceRollName = diceRoll.rollName() ?: ""
        layout.addView(this.headerView(diceRoll.toString(), diceRollName, sheetUIContext))

        // Rolls
        val rollsScrollView = this.rollsScrollView(sheetUIContext.context)
        val rollsListView = this.rollListView(sheetUIContext)
        rollsScrollView.addView(rollsListView)
        layout.addView(rollsScrollView)

        // Footer
        layout.addView(this.footerView(diceRoll, rollsListView, sheetUIContext))


        return layout
    }


    fun viewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_11")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.corners              = Corners(TopLeftCornerRadius(2f),
                                              TopRightCornerRadius(2f),
                                              BottomRightCornerRadius(2f),
                                              BottomLeftCornerRadius(2f))

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // HEADER VIEW
    // -----------------------------------------------------------------------------------------

    fun headerView(rollString : String,
                   rollName : String,
                   sheetUIContext : SheetUIContext) : View
    {
        val layout = this.headerViewLayout(sheetUIContext)

        // Description
        layout.addView(this.rollNameView(rollName, sheetUIContext))

        // Roll
        layout.addView(this.rollView(rollString, sheetUIContext))

        return layout
    }


    private fun headerViewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity              = Gravity.CENTER_HORIZONTAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.corners              = Corners(TopLeftCornerRadius(2f),
                                              TopRightCornerRadius(2f),
                                              BottomRightCornerRadius(0f),
                                              BottomLeftCornerRadius(0f))

//        layout.margin.topDp         = 4f
//        layout.margin.leftDp        = 4f
//        layout.margin.rightDp       = 4f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun rollNameView(nameString : String,
                             sheetUIContext : SheetUIContext) : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.gravity            = Gravity.CENTER_HORIZONTAL

        name.text               = nameString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        name.color              = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        name.font               = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        name.padding.topDp      = 7f
        name.padding.bottomDp   = 5f

        name.sizeSp             = 15f

        return name.textView(sheetUIContext.context)
    }


    private fun rollView(rollString : String, sheetUIContext : SheetUIContext) : TextView
    {
        val description                 = TextViewBuilder()

        description.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height              = LinearLayout.LayoutParams.WRAP_CONTENT


        description.padding.bottomDp    = 9f

        description.text                = rollString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        description.color               = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        description.font                = Font.typeface(TextFont.FiraSans,
                                                        TextFontStyle.Italic,
                                                        sheetUIContext.context)

        description.sizeSp              = 14f


        return description.textView(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // FOOTER VIEW
    // -----------------------------------------------------------------------------------------

    private fun footerView(diceRoll : DiceRoll,
                           rollsListView : LinearLayout,
                           sheetUIContext : SheetUIContext) : View
    {
        val layout = this.footerViewLayout(sheetUIContext)

        layout.addView(this.actionsButtonView(sheetUIContext))
        layout.addView(this.rollButtonView(diceRoll, rollsListView, sheetUIContext))

        return layout
    }


    private fun footerViewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.HORIZONTAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.corners              = Corners(TopLeftCornerRadius(0f),
                                              TopRightCornerRadius(0f),
                                              BottomRightCornerRadius(2f),
                                              BottomLeftCornerRadius(2f))

//        layout.margin.bottomDp      = 4f
//        layout.margin.leftDp        = 4f
//        layout.margin.rightDp       = 4f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun actionsButtonView(sheetUIContext : SheetUIContext) : TextView
    {
        val name                = TextViewBuilder()

        name.width              = 0
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT
        name.weight             = 1f

        name.textId             = R.string.actions

        name.gravity            = Gravity.CENTER_HORIZONTAL

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        name.color              = SheetManager.color(sheetUIContext.sheetId, textColorTheme)

        name.font               = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        name.padding.topDp      = 10f
        name.padding.bottomDp   = 10f

        name.sizeSp             = 15f

        return name.textView(sheetUIContext.context)
    }


    private fun rollButtonView(diceRoll : DiceRoll,
                               rollsListView : LinearLayout,
                               sheetUIContext : SheetUIContext) : LinearLayout
    {

        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val icon                    = ImageViewBuilder()
        val label                   = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = 0
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight               = 1f

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("green_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor       = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners               = Corners(TopLeftCornerRadius(0f),
                                               TopRightCornerRadius(0f),
                                               BottomRightCornerRadius(2f),
                                               BottomLeftCornerRadius(0f))

        layout.padding.topDp        = 10f
        layout.padding.bottomDp     = 10f

        layout.onClick              = View.OnClickListener {
            rollsListView.addView(this.rollView(diceRoll.rollSummary(), sheetUIContext))
        }

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp                = 19
        icon.heightDp               = 19

        icon.image                  = R.drawable.icon_dice_roll_filled

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color                  = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        icon.margin.rightDp         = 5f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text                  = sheetUIContext.context.getString(R.string.roll).toUpperCase()

        label.gravity               = Gravity.CENTER_HORIZONTAL

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color                 = SheetManager.color(sheetUIContext.sheetId, textColorTheme)


        label.font                  = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Bold,
                                                    sheetUIContext.context)

        label.sizeSp                = 16.5f

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // ROLLS VIEW
    // -----------------------------------------------------------------------------------------


//    private fun rollsView(sheetUIContext : SheetUIContext) : ScrollView
//    {
//        val scrollView = this.rollsScrollView(sheetUIContext.context)
//
//        scrollView.addView(this.rollListView(sheetUIContext))
//
//        return scrollView
//    }


    private fun rollsScrollView(context : Context) : ScrollView
    {
        val scrollView = ScrollViewBuilder()

        scrollView.width            = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.heightDp         = 300

        return scrollView.scrollView(context)
    }


    private fun rollListView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // ROLL VIEW
    // -----------------------------------------------------------------------------------------


    private fun rollView(rollSummary : RollSummary,
                         sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.rollViewLayout(sheetUIContext.context)

        layout.addView(this.rollContentView(rollSummary, sheetUIContext))

        layout.addView(this.rollDividerView(sheetUIContext))

        return layout
    }


    private fun rollViewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        return layout.linearLayout(context)
    }


    private fun rollContentView(rollSummary : RollSummary,
                         sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.rollContentViewLayout(sheetUIContext)

        layout.addView(this.rollValueView(rollSummary.value, sheetUIContext))

        layout.addView(this.rollSummaryView(rollSummary.parts, sheetUIContext))

        return layout
    }


    private fun rollDividerView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val divider = LinearLayoutBuilder()

        divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp            = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_13")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        divider.backgroundColor     = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        return divider.linearLayout(sheetUIContext.context)
    }


    private fun rollContentViewLayout(sheetUIContext : SheetUIContext) : LinearLayout
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


    private fun rollValueView(rollValue : Int, sheetUIContext : SheetUIContext) : TextView
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


    private fun rollSummaryView(partSummaries : List<RollPartSummary>,
                                sheetUIContext : SheetUIContext) : FlexboxLayout
    {
        val layout = FlexboxLayoutBuilder()

        layout.width                    = 0
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight                   = 3.5f

        layout.contentAlignment         = AlignContent.CENTER

        partSummaries.forEach {
            layout.child(this.rollPartSummaryView(it, sheetUIContext))
        }

        return layout.flexboxLayout(sheetUIContext.context)
    }


    private fun rollPartSummaryView(rollPartSummary : RollPartSummary,
                                    sheetUIContext : SheetUIContext) : LinearLayoutBuilder
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

