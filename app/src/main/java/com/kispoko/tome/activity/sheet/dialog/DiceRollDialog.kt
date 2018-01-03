
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
import com.google.android.flexbox.FlexWrap
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
import effect.Just
import effect.Maybe
import effect.Nothing



/**
 * Dice Roller Dialog
 */
class DiceRollDialog : DialogFragment()
{


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var diceRoll     : DiceRoll? = null
    private var title        : Maybe<String> = Nothing()
    private var sheetContext : SheetContext? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(diceRoll : DiceRoll,
                        title : Maybe<String>,
                        sheetContext : SheetContext) : DiceRollDialog
        {
            val dialog = DiceRollDialog()

            val args = Bundle()
            args.putSerializable("dice_roll", diceRoll)
            args.putSerializable("title", title)
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
        this.title        = arguments.getSerializable("title") as Maybe<String>
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

            if (diceRoll != null) {
                val viewBuildeer = DiceRollerViewBuilder(diceRoll, title, sheetUIContext)
                return viewBuildeer.view()
            }
            else {
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

    fun dialogLayout(context : Context) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(context)
    }

}



class DiceRollerViewBuilder(val diceRoll : DiceRoll,
                            val title : Maybe<String>,
                            val sheetUIContext : SheetUIContext)
{

    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout(sheetUIContext)

        // Header
        //layout.addView(this.headerView())

        // Rolls
        val rollsScrollView = this.rollsScrollView(sheetUIContext)
        val rollsListView = this.rollListView(sheetUIContext)
        rollsScrollView.addView(rollsListView)
        layout.addView(rollsScrollView)

        // Footer
        layout.addView(this.footerView(rollsListView))


        return layout
    }


    fun viewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        return layout.linearLayout(sheetUIContext.context)
    }




    // -----------------------------------------------------------------------------------------
    // HEADER VIEW
    // -----------------------------------------------------------------------------------------

    fun headerView() : View
    {
        val layout = this.headerViewLayout(sheetUIContext)

        // Description
        layout.addView(this.rollNameView())

        // Roll
        layout.addView(this.rollView())

        return layout
    }


    private fun headerViewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity              = Gravity.CENTER_HORIZONTAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.margin.bottomDp      = 1f

        layout.corners          = Corners(2.0, 2.0, 0.0, 0.0)

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun rollNameView() : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.MATCH_PARENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.gravity            = Gravity.CENTER_HORIZONTAL

        when (this.title) {
            is Just    -> name.text = this.title.value
            is Nothing -> name.text = diceRoll.rollName().toNullable()?.value ?: ""
        }

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        name.color              = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Bold,
                                                sheetUIContext.context)


//        name.padding.topDp      = 7f
//        name.padding.bottomDp   = 2f

        name.sizeSp             = 22f

        return name.textView(sheetUIContext.context)
    }




    // -----------------------------------------------------------------------------------------
    // FOOTER VIEW
    // -----------------------------------------------------------------------------------------

    private fun footerView(rollsListView : LinearLayout) : View
    {
        val layout = this.footerViewLayout()

        layout.addView(this.footerTopBorderView())

        val mainLayout = this.footerMainViewLayout()

        mainLayout.addView(this.rollView())

        //layout.addView(this.actionsButtonView(sheetUIContext))
        mainLayout.addView(this.rollButtonView(rollsListView))

        layout.addView(mainLayout)

        return layout
    }


    private fun footerViewLayout() : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp             = 60

        layout.orientation          = LinearLayout.VERTICAL

        layout.corners          = Corners(0.0, 0.0, 2.0, 2.0)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.padding.bottomDp     = 6f
        layout.padding.rightDp      = 10f
        layout.padding.leftDp       = 10f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun footerMainViewLayout() : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.HORIZONTAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

//        layout.corners          = Corners(0.0, 0.0, 2.0, 2.0)
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)
//
//        layout.padding.topDp        = 6f
//        layout.padding.bottomDp     = 6f
//        layout.padding.rightDp      = 10f
//        layout.padding.leftDp       = 10f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun footerTopBorderView() : LinearLayout
    {
        val divider = LinearLayoutBuilder()

        divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp            = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_4"))))
        divider.backgroundColor     = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        divider.margin.bottomDp     = 6f

        return divider.linearLayout(sheetUIContext.context)
    }


    private fun rollView() : TextView
    {
        val description                 = TextViewBuilder()

        description.width               = 0
        description.height              = LinearLayout.LayoutParams.MATCH_PARENT
        description.weight              = 1.8f

        description.gravity             = Gravity.CENTER

        description.text                = this.diceRoll.toString()

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        description.color               = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        description.font                = Font.typeface(TextFont.default(),
                                                        TextFontStyle.Regular,
                                                        sheetUIContext.context)

        description.sizeSp              = 18f

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
//        description.backgroundColor     = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        description.corners          = Corners(0.0, 0.0, 0.0, 2.0)


        return description.textView(sheetUIContext.context)
    }




    private fun rollButtonView(rollsListView : LinearLayout) : LinearLayout
    {

        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val icon                    = ImageViewBuilder()
        val label                   = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = 0
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight               = 1f

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_green_4")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green"))))
        layout.backgroundColor       = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)

        layout.padding.topDp        = 8f
        layout.padding.bottomDp     = 8f
        layout.padding.leftDp       = 10f
        layout.padding.rightDp      = 10f
//
//        layout.margin.leftDp        = 1f

        layout.onClick              = View.OnClickListener {
            rollsListView.addView(this.rollView(diceRoll.rollSummary(), sheetUIContext))
        }

        layout.child(icon)
                .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp                = 25
        icon.heightDp               = 25

        icon.image                  = R.drawable.icon_dice_roll_filled

//        val iconColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        //icon.color                  = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)
        icon.color                  = Color.WHITE

        icon.margin.rightDp         = 5f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text                  = sheetUIContext.context.getString(R.string.roll) //.toUpperCase()

        label.gravity               = Gravity.CENTER_HORIZONTAL

//        val textColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
//        label.color                 = SheetManager.color(sheetUIContext.sheetId, textColorTheme)
        label.color                 = Color.WHITE


        label.font                  = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Medium,
                                                    sheetUIContext.context)

        label.sizeSp                = 20f

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


    private fun rollsScrollView(sheetUIContext : SheetUIContext) : ScrollView
    {
        val scrollView = ScrollViewBuilder()

        scrollView.width            = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.heightDp         = 360

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        scrollView.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        scrollView.fadingEnabled        = false

        return scrollView.scrollView(sheetUIContext.context)
    }


    private fun rollListView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
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

        layout.addView(this.rollResultView(rollSummary, sheetUIContext))

        layout.addView(this.rollDetailsView(rollSummary.parts, sheetUIContext))

        //layout.addView(this.rollDividerView(sheetUIContext))

        return layout
    }


    private fun rollViewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.topDp         = 8f
        layout.padding.bottomDp      = 8f

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor     = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.margin.leftDp        = 3f
        layout.margin.rightDp       = 3f
        layout.margin.topDp         = 4f

        return layout.linearLayout(context)
    }


    private fun rollResultView(rollSummary : RollSummary,
                               sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.rollResultViewLayout(sheetUIContext)

        layout.addView(this.rollValueView(rollSummary.value, sheetUIContext))

        layout.addView(this.rollDescriptionView(sheetUIContext))

        return layout
    }


    private fun rollDividerView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val divider = LinearLayoutBuilder()

        divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp            = 2

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_6"))))
        divider.backgroundColor     = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        divider.margin.topDp        = 14f

        return divider.linearLayout(sheetUIContext.context)
    }


    private fun rollResultViewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.HORIZONTAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        //layout.gravity              = Gravity.CENTER_VERTICAL

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
//        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)
        layout.backgroundColor      = Color.WHITE

//        layout.padding.topDp        = 12f
//        layout.padding.bottomDp     = 12f

        layout.margin.leftDp        = 6f
        layout.margin.rightDp       = 6f

        layout.padding.leftDp       = 5f
        layout.padding.rightDp       = 5f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun rollValueView(rollValue : Int, sheetUIContext : SheetUIContext) : TextView
    {
        val value = TextViewBuilder()

        val valueString = rollValue.toString()

        value.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        value.text                  = valueString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        value.color                 = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        value.font                  = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Bold,
                                                    sheetUIContext.context)

        if (valueString.length <= 23)
            value.sizeSp                = 25f
        else
            value.sizeSp                = 22f

        value.margin.rightDp        = 6f

        return value.textView(sheetUIContext.context)
    }


    private fun rollDescriptionView(sheetUIContext : SheetUIContext) : TextView
    {
        val description                 = TextViewBuilder()

        description.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        when (this.title) {
            is Just    -> description.text = this.title.value
            is Nothing -> description.text = diceRoll.rollName().toNullable()?.value ?: ""
        }

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_17"))))
        description.color              = SheetManager.color(sheetUIContext.sheetId, textColorTheme)

        description.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Light,
                                                sheetUIContext.context)

        description.sizeSp             = 20f

        return description.textView(sheetUIContext.context)
    }


    private fun rollDetailsView(partSummaries : List<RollPartSummary>,
                                sheetUIContext : SheetUIContext) : FlexboxLayout
    {
        val layout = FlexboxLayoutBuilder()

        layout.width                    = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.contentAlignment         = AlignContent.CENTER
        layout.wrap                     = FlexWrap.WRAP

        layout.margin.topDp             = 8f

        layout.padding.leftDp           = 6f
        layout.padding.rightDp          = 6f

        partSummaries.forEach {
            layout.child(this.rollPartSummaryView(it, sheetUIContext))
            Log.d("***DICE ROLL DIALOG", "summary: $it")
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

        layout.gravity              = Gravity.BOTTOM

        //layout.margin.leftDp        = 5f
        layout.margin.rightDp       = 5f
        layout.margin.bottomDp      = 3f

//        layout.padding.leftDp       = 6f
//        layout.padding.rightDp      = 6f
        layout.padding.topDp        = 2f
        layout.padding.bottomDp     = 2f
        layout.padding.leftDp     = 3f
        layout.padding.rightDp     = 3f


        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)
//        layout.backgroundColor      = Color.WHITE

        layout.corners              = Corners(2.0, 2.0, 2.0, 2.0)

        layout.child(value)

        if (rollPartSummary.dice.isNotBlank())
            layout.child(dice)

        if (rollPartSummary.tag.isNotBlank())
            layout.child(description)

        // (3 A) Value
        // -------------------------------------------------------------------------------------

        value.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        value.text                  = rollPartSummary.value.toString()

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        value.color                 = SheetManager.color(sheetUIContext.sheetId, valueColorTheme)

        value.sizeSp                = 13.5f

        value.font                  = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    sheetUIContext.context)

        value.margin.rightDp        = 4f

        // (3 B) Dice
        // -------------------------------------------------------------------------------------

        dice.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
        dice.height                 = LinearLayout.LayoutParams.WRAP_CONTENT

        dice.text                   = rollPartSummary.dice

        dice.margin.rightDp         = 4f

        val diceColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        dice.color                  = SheetManager.color(sheetUIContext.sheetId, diceColorTheme)

        dice.sizeSp                 = 12f

        dice.font                   = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    sheetUIContext.context)

        // (3 C) Description
        // -------------------------------------------------------------------------------------

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        description.text            = rollPartSummary.tag

        val descColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_23"))))
        description.color           = SheetManager.color(sheetUIContext.sheetId, descColorTheme)

        description.font            = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    sheetUIContext.context)

        description.sizeSp          = 12f

        return layout
    }
}

