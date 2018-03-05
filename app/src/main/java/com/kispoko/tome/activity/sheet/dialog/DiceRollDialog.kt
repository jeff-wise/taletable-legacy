
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.game.engine.dice.DiceRollGroup
import com.kispoko.tome.model.game.engine.dice.RollPartSummary
import com.kispoko.tome.model.game.engine.dice.RollSummary
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId



/**
 * Dice Roller Dialog
 */
//class DiceRollDialog : DialogFragment()
//{
//
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    private var diceRollGroup : DiceRollGroup? = null
//
//    private var autoRolls     : Int             = 0
//
//    private var sheetContext  : SheetContext? = null
//
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object
//    {
//        fun newInstance(diceRollGroup : DiceRollGroup,
//                        sheetContext : SheetContext,
//                        autoRolls : Int = 0) : DiceRollDialog
//        {
//            val dialog = DiceRollDialog()
//
//            val args = Bundle()
//            args.putSerializable("dice_roll_group", diceRollGroup)
//            args.putInt("auto_rolls", autoRolls)
//            args.putSerializable("sheet_context", sheetContext)
//
//            dialog.arguments = args
//
//            return dialog
//        }
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // DIALOG FRAGMENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun onCreateDialog(savedInstanceState : Bundle?) : Dialog
//    {
//        // (1) Read State
//        // -------------------------------------------------------------------------------------
//
//        this.diceRollGroup  = arguments.getSerializable("dice_roll_group") as DiceRollGroup
//
//        this.autoRolls      = arguments.getInt("auto_rolls")
//
//        this.sheetContext   = arguments.getSerializable("sheet_context") as SheetContext
//
//        // (2) Initialize UI
//        // -------------------------------------------------------------------------------------
//
//        val dialog = Dialog(activity)
//
//        val sheetContext = this.sheetContext
//        if (sheetContext != null)
//        {
//            val dialogLayout = this.dialogLayout(context)
//
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//            dialog.setContentView(dialogLayout)
//
//            val width  = context.resources.getDimension(R.dimen.action_dialog_width)
//            val height = LinearLayout.LayoutParams.WRAP_CONTENT
//
//            dialog.window.setLayout(width.toInt(), height)
//        }
//
//        return dialog
//    }
//
//
//    override fun onCreateView(inflater : LayoutInflater?,
//                              container : ViewGroup?,
//                              savedInstanceState : Bundle?) : View?
//    {
//        val sheetContext = this.sheetContext
//        if (sheetContext != null)
//        {
//            val sheetUIContext  = SheetUIContext(sheetContext, context)
//
//            val diceRollGroup = this.diceRollGroup
//
//            return if (diceRollGroup != null) {
//                val viewBuilder = DiceRollerViewBuilder(diceRollGroup, autoRolls, sheetUIContext)
//                viewBuilder.view()
//            }
//            else {
//                super.onCreateView(inflater, container, savedInstanceState)
//            }
//        }
//        else
//        {
//            return super.onCreateView(inflater, container, savedInstanceState)
//        }
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // DIALOG LAYOUT
//    // -----------------------------------------------------------------------------------------
//
//    fun dialogLayout(context : Context) : LinearLayout
//    {
//        val layout                  = LinearLayoutBuilder()
//
//        layout.orientation          = LinearLayout.VERTICAL
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
//
//        return layout.linearLayout(context)
//    }
//
//}
//
//
//
//class DiceRollerViewBuilder(val diceRollGroup : DiceRollGroup,
//                            val autoRolls : Int,
//                            val sheetUIContext : SheetUIContext)
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    var rolls : Int = 0
//
//    val diceRolls : List<DiceRoll> = diceRollGroup.diceRolls(SheetContext(sheetUIContext))
//
//    var rollListView : LinearLayout? = null
//
//
//    private fun roll()
//    {
//        this.rolls += 1
//
//        this.diceRolls.forEach {
//            this.rollListView?.addView(this.rollView(it.rollSummary(), sheetUIContext))
//        }
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // VIEW
//    // -----------------------------------------------------------------------------------------
//
//    fun view() : View
//    {
//        val layout = this.viewLayout(sheetUIContext)
//
//        // Rolls
//        val rollsScrollView = this.rollsScrollView(sheetUIContext)
//        val rollListView = this.rollListView(sheetUIContext)
//        this.rollListView = rollListView
//        rollsScrollView.addView(rollListView)
//        layout.addView(rollsScrollView)
//
//        // Footer
//        layout.addView(this.footerView())
//
//        for (i in 1..autoRolls) {
//            this.roll()
//        }
//
//        return layout
//    }
//
//
//    fun viewLayout(sheetUIContext : SheetUIContext) : LinearLayout
//    {
//        val layout = LinearLayoutBuilder()
//
//        layout.orientation      = LinearLayout.VERTICAL
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
//        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)
//
//        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)
//
//        return layout.linearLayout(sheetUIContext.context)
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // FOOTER VIEW
//    // -----------------------------------------------------------------------------------------
//
//    private fun footerView() : View
//    {
//        val layout = this.footerViewLayout()
//
//        layout.addView(this.footerTopBorderView())
//
//        val mainLayout = this.footerMainViewLayout()
//
//        mainLayout.addView(this.rollSettingsButtonView())
//
//        mainLayout.addView(this.rollButtonView())
//
//        layout.addView(mainLayout)
//
//        return layout
//    }
//
//
//    private fun footerViewLayout() : LinearLayout
//    {
//        val layout = LinearLayoutBuilder()
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.heightDp             = 60
//
//        layout.orientation          = LinearLayout.VERTICAL
//
//        layout.corners          = Corners(0.0, 0.0, 2.0, 2.0)
//
////        val colorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
//        layout.backgroundColor      = Color.WHITE
//
//        layout.padding.bottomDp     = 6f
//        layout.padding.rightDp      = 6f
//        layout.padding.leftDp       = 6f
//
//        return layout.linearLayout(sheetUIContext.context)
//    }
//
//
//    private fun footerMainViewLayout() : LinearLayout
//    {
//        val layout = LinearLayoutBuilder()
//
//        layout.orientation          = LinearLayout.HORIZONTAL
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
//
////        layout.corners          = Corners(0.0, 0.0, 2.0, 2.0)
////
////        val colorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
////        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)
////
////        layout.padding.topDp        = 6f
////        layout.padding.bottomDp     = 6f
////        layout.padding.rightDp      = 10f
////        layout.padding.leftDp       = 10f
//
//        return layout.linearLayout(sheetUIContext.context)
//    }
//
//
//    private fun footerTopBorderView() : LinearLayout
//    {
//        val divider = LinearLayoutBuilder()
//
//        divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
//        divider.heightDp            = 1
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_4"))))
//        divider.backgroundColor     = SheetManager.color(sheetUIContext.sheetId, colorTheme)
//
//        divider.margin.bottomDp     = 6f
//
//        return divider.linearLayout(sheetUIContext.context)
//    }
//
//
//    private fun rollSettingsButtonView() : LinearLayout
//    {
//
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout                  = LinearLayoutBuilder()
//        val icon                    = ImageViewBuilder()
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
////        layout.weight               = 2f
//
//        layout.orientation          = LinearLayout.HORIZONTAL
//
//        layout.gravity              = Gravity.CENTER
//
////        val bgColorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_green_4")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
//        //layout.backgroundColor       = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)
//        layout.backgroundColor       = Color.WHITE
//
//        layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)
//
//        layout.padding.topDp        = 8f
//        layout.padding.bottomDp     = 8f
//        layout.padding.leftDp       = 12f
//        layout.padding.rightDp      = 14f
////
////        layout.margin.leftDp        = 1f
//
////        layout.onClick              = View.OnClickListener {
////        }
//
//        layout.child(icon)
//
//        // (3 A) Icon
//        // -------------------------------------------------------------------------------------
//
//        icon.widthDp                = 29
//        icon.heightDp               = 29
//
//        icon.image                  = R.drawable.icon_gears_filled
//
//        val iconColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
//        icon.color                  = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)
//
//        return layout.linearLayout(sheetUIContext.context)
//    }
//
//
//    private fun rollButtonView() : LinearLayout
//    {
//
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout                  = LinearLayoutBuilder()
//        val icon                    = ImageViewBuilder()
//        val label                   = TextViewBuilder()
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.width                = 0
//        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.weight               = 1f
//
//        layout.orientation          = LinearLayout.HORIZONTAL
//
//        layout.gravity              = Gravity.CENTER
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_green_4")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
//        layout.backgroundColor       = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)
//
//        layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)
//
//        layout.padding.topDp        = 8f
//        layout.padding.bottomDp     = 8f
//        layout.padding.leftDp       = 10f
//        layout.padding.rightDp      = 10f
//
//        layout.margin.leftDp        = 4f
//        layout.margin.rightDp       = 4f
////
////        layout.margin.leftDp        = 1f
//
//        layout.onClick              = View.OnClickListener {
//            this.roll()
////            this.rolls += 1
////            rollsListView.addView(this.rollView(diceRoll.rollSummary(), sheetUIContext))
//        }
//
//        layout.child(icon)
//                .child(label)
//
//        // (3 A) Icon
//        // -------------------------------------------------------------------------------------
//
//        icon.widthDp                = 25
//        icon.heightDp               = 25
//
//        icon.image                  = R.drawable.icon_dice_roll_filled
//
////        val iconColorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//        //icon.color                  = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)
//        icon.color                  = Color.WHITE
//
//        icon.margin.rightDp         = 5f
//
//        // (3 B) Label
//        // -------------------------------------------------------------------------------------
//
//        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
//        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        //label.text                  = sheetUIContext.context.getString(R.string.roll) //.toUpperCase()
//
//        if (this.diceRolls.size == 1) {
//            label.text                  = this.diceRolls[0].toString()
//        } else {
//            label.text                  = this.diceRollGroup.groupName().toNullable()?.value ?: ""
//        }
//
//        label.gravity               = Gravity.CENTER_HORIZONTAL
//
////        val textColorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
////        label.color                 = SheetManager.color(sheetUIContext.sheetId, textColorTheme)
//        label.color                 = Color.WHITE
//
//
//        label.font                  = Font.typeface(TextFont.default(),
//                                                    TextFontStyle.Medium,
//                                                    sheetUIContext.context)
//
//        label.sizeSp                = 20f
//
//        return layout.linearLayout(sheetUIContext.context)
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // ROLLS VIEW
//    // -----------------------------------------------------------------------------------------
//
//
////    private fun rollsView(sheetUIContext : SheetUIContext) : ScrollView
////    {
////        val scrollView = this.rollsScrollView(sheetUIContext.context)
////
////        scrollView.addView(this.rollListView(sheetUIContext))
////
////        return scrollView
////    }
//
//
//    private fun rollsScrollView(sheetUIContext : SheetUIContext) : ScrollView
//    {
//        val scrollView = ScrollViewBuilder()
//
//        scrollView.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        scrollView.heightDp         = 300
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
//        scrollView.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)
//
//        scrollView.fadingEnabled        = false
//
//        return scrollView.scrollView(sheetUIContext.context)
//    }
//
//
//    private fun rollListView(sheetUIContext : SheetUIContext) : LinearLayout
//    {
//        val layout                  = LinearLayoutBuilder()
//
//        layout.orientation          = LinearLayout.VERTICAL
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
//        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)
//
//        return layout.linearLayout(sheetUIContext.context)
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // ROLL VIEW
//    // -----------------------------------------------------------------------------------------
//
//
//    private fun rollView(rollSummary : RollSummary,
//                         sheetUIContext : SheetUIContext) : LinearLayout
//    {
//        val layout = this.rollViewLayout(sheetUIContext.context)
//
//        layout.addView(this.rollResultView(rollSummary, sheetUIContext))
//
//        layout.addView(this.rollResultRightBorderView())
//
//        layout.addView(this.rollInfoView(rollSummary.name, rollSummary.parts, sheetUIContext))
//
//        return layout
//    }
//
//
//    private fun rollViewLayout(context : Context) : LinearLayout
//    {
//        val layout = LinearLayoutBuilder()
//
//        layout.orientation          = LinearLayout.HORIZONTAL
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
////        val bgColorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//        layout.backgroundColor     = Color.WHITE
//
//        layout.margin.leftDp        = 3f
//        layout.margin.rightDp       = 3f
//        layout.margin.topDp         = 4f
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun rollResultView(rollSummary : RollSummary,
//                               sheetUIContext : SheetUIContext) : RelativeLayout
//    {
//        val layout = this.rollResultViewLayout(sheetUIContext)
//
//        layout.addView(this.rollValueView(rollSummary.value, sheetUIContext))
//
//        layout.addView(this.rollIndexView(sheetUIContext))
//
//        return layout
//    }
//
//
//    private fun rollResultViewLayout(sheetUIContext : SheetUIContext) : RelativeLayout
//    {
//        val layout                  = RelativeLayoutBuilder()
//
//        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
//
//        layout.orientation          = LinearLayout.VERTICAL
//
//        //layout.gravity              = Gravity.CENTER_VERTICAL
//
////        val colorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
////        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)
//        layout.backgroundColor      = Color.WHITE
//
////        layout.padding.topDp        = 12f
////        layout.padding.bottomDp     = 12f
//
//
//        layout.padding.topDp         = 8f
//        layout.padding.bottomDp      = 5f
//
//        layout.margin.leftDp        = 6f
//        layout.margin.rightDp       = 6f
//
//        layout.padding.leftDp       = 5f
//        layout.padding.rightDp       = 5f
//
//        return layout.relativeLayout(sheetUIContext.context)
//    }
//
//
//    private fun rollValueView(rollValue : Int, sheetUIContext : SheetUIContext) : TextView
//    {
//        val value = TextViewBuilder()
//
//        val valueString = rollValue.toString()
//
//        value.layoutType            = LayoutType.RELATIVE
//        value.width                 = RelativeLayout.LayoutParams.WRAP_CONTENT
//        value.height                = RelativeLayout.LayoutParams.WRAP_CONTENT
//
//        value.addRule(RelativeLayout.ALIGN_PARENT_TOP)
//        value.addRule(RelativeLayout.CENTER_HORIZONTAL)
//
//        value.gravity               = Gravity.CENTER
//
//        value.text                  = valueString
//
////        val colorTheme = ColorTheme(setOf(
////                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
////                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
//        //value.color                 = SheetManager.color(sheetUIContext.sheetId, colorTheme)
//        value.color                 = Color.WHITE
//
//        value.backgroundResource    = R.drawable.bg_roll_result
//
//        value.font                  = Font.typeface(TextFont.default(),
//                                                    TextFontStyle.Medium,
//                                                    sheetUIContext.context)
//
//        value.sizeSp                = 22f
//
//        return value.textView(sheetUIContext.context)
//    }
//
//
//    private fun rollIndexView(sheetUIContext : SheetUIContext) : TextView
//    {
//        val value = TextViewBuilder()
//
//        value.layoutType            = LayoutType.RELATIVE
//        value.width                 = RelativeLayout.LayoutParams.WRAP_CONTENT
//        value.height                = RelativeLayout.LayoutParams.WRAP_CONTENT
//
//        value.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//        value.addRule(RelativeLayout.CENTER_HORIZONTAL)
//
//        value.text                  = "roll ${this.rolls}"
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_24"))))
//        value.color                 = SheetManager.color(sheetUIContext.sheetId, colorTheme)
//
//        value.font                  = Font.typeface(TextFont.default(),
//                                                    TextFontStyle.Regular,
//                                                    sheetUIContext.context)
//
//        value.margin.topDp          = 5f
//
//        value.sizeSp                = 14f
//
//        return value.textView(sheetUIContext.context)
//    }
//
//
//    private fun rollInfoView(rollName : String,
//                             partSummaries : List<RollPartSummary>,
//                             sheetUIContext : SheetUIContext) : LinearLayout
//    {
//        val layout = rollInfoViewLayout(sheetUIContext)
//
//        layout.addView(this.rollDescriptionView(rollName, sheetUIContext))
//
//        layout.addView(this.rollDetailsView(partSummaries, sheetUIContext))
//
//        return layout
//    }
//
//
//    private fun rollInfoViewLayout(sheetUIContext : SheetUIContext) : LinearLayout
//    {
//        val layout                      = LinearLayoutBuilder()
//
//        layout.width                    = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation              = LinearLayout.VERTICAL
//
//        layout.padding.topDp         = 6f
//        layout.padding.bottomDp      = 6f
//
//        layout.margin.leftDp        = 10f
//
//
//        return layout.linearLayout(sheetUIContext.context)
//    }
//
//
//    private fun rollDescriptionView(rollName : String, sheetUIContext : SheetUIContext) : TextView
//    {
//        val description                 = TextViewBuilder()
//
//        description.width               = LinearLayout.LayoutParams.WRAP_CONTENT
//        description.height              = LinearLayout.LayoutParams.WRAP_CONTENT
//
////        when (this.title) {
////            is Just    -> description.text = this.title.value
////            is Nothing -> description.text = diceRoll.rollName().toNullable()?.value ?: ""
////        }
//
//        description.text            = rollName
//
//        val textColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
//        description.color              = SheetManager.color(sheetUIContext.sheetId, textColorTheme)
//
//        description.font               = Font.typeface(TextFont.default(),
//                                                TextFontStyle.Medium,
//                                                sheetUIContext.context)
//
//        description.sizeSp             = 20f
//
//        return description.textView(sheetUIContext.context)
//    }
//
//
//    private fun rollDetailsView(partSummaries : List<RollPartSummary>,
//                                sheetUIContext : SheetUIContext) : FlexboxLayout
//    {
//        val layout = FlexboxLayoutBuilder()
//
//        layout.width                    = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.contentAlignment         = AlignContent.CENTER
//        layout.wrap                     = FlexWrap.WRAP
//
//        layout.margin.topDp             = 8f
//
////        layout.padding.leftDp           = 3f
//        layout.padding.rightDp          = 6f
//
//        partSummaries.forEach {
//            layout.child(this.rollPartSummaryView(it, sheetUIContext))
//        }
//
//        return layout.flexboxLayout(sheetUIContext.context)
//    }
//
//
//    private fun rollPartSummaryView(rollPartSummary : RollPartSummary,
//                                    sheetUIContext : SheetUIContext) : LinearLayoutBuilder
//    {
//
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout          = LinearLayoutBuilder()
//        val value           = TextViewBuilder()
//        val dice            = TextViewBuilder()
//        val description     = TextViewBuilder()
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.layoutType           = LayoutType.FLEXBOX
//        layout.width                = FlexboxLayout.LayoutParams.WRAP_CONTENT
//        layout.height               = FlexboxLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation          = LinearLayout.HORIZONTAL
//
//        layout.gravity              = Gravity.BOTTOM
//
//        //layout.margin.leftDp        = 5f
//        layout.margin.rightDp       = 5f
//        layout.margin.bottomDp      = 3f
//
////        layout.padding.leftDp       = 6f
////        layout.padding.rightDp      = 6f
//        layout.padding.topDp        = 2f
//        layout.padding.bottomDp     = 2f
//        layout.padding.leftDp     = 3f
//        layout.padding.rightDp     = 3f
//
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_7")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_2"))))
//        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)
////        layout.backgroundColor      = Color.WHITE
//
//        layout.corners              = Corners(2.0, 2.0, 2.0, 2.0)
//
//        layout.child(value)
//
//        if (rollPartSummary.dice.isNotBlank())
//            layout.child(dice)
//
//        if (rollPartSummary.tag.isNotBlank())
//            layout.child(description)
//
//        // (3 A) Value
//        // -------------------------------------------------------------------------------------
//
//        value.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
//        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        value.text                  = rollPartSummary.value.toString()
//
//        val valueColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_8")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
//        value.color                 = SheetManager.color(sheetUIContext.sheetId, valueColorTheme)
//
//        value.sizeSp                = 13.5f
//
//        value.font                  = Font.typeface(TextFont.default(),
//                                                    TextFontStyle.Regular,
//                                                    sheetUIContext.context)
//
//        value.margin.rightDp        = 4f
//
//        // (3 B) Dice
//        // -------------------------------------------------------------------------------------
//
//        dice.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
//        dice.height                 = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        dice.text                   = rollPartSummary.dice
//
//        dice.margin.rightDp         = 4f
//
//        val diceColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
//        dice.color                  = SheetManager.color(sheetUIContext.sheetId, diceColorTheme)
//
//        dice.sizeSp                 = 12f
//
//        dice.font                   = Font.typeface(TextFont.default(),
//                                                    TextFontStyle.Regular,
//                                                    sheetUIContext.context)
//
//        // (3 C) Description
//        // -------------------------------------------------------------------------------------
//
//        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT
//        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        description.text            = rollPartSummary.tag
//
//        val descColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
//        description.color           = SheetManager.color(sheetUIContext.sheetId, descColorTheme)
//
//        description.font            = Font.typeface(TextFont.default(),
//                                                    TextFontStyle.Regular,
//                                                    sheetUIContext.context)
//
//        description.sizeSp          = 12f
//
//        return layout
//    }
//
//
//    private fun rollResultRightBorderView() : LinearLayout
//    {
//        val divider = LinearLayoutBuilder()
//
//        divider.widthDp               = 1
//        divider.height            = LinearLayout.LayoutParams.MATCH_PARENT
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//        divider.backgroundColor     = SheetManager.color(sheetUIContext.sheetId, colorTheme)
//
//        return divider.linearLayout(sheetUIContext.context)
//    }
//
//}

