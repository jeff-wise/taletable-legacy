
package com.kispoko.tome.activity.entity.engine.dice


import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kispoko.tome.R
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.game.engine.dice.DiceRollGroup
import com.kispoko.tome.model.game.engine.dice.RollPartSummary
import com.kispoko.tome.model.game.engine.dice.RollSummary
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.entityThemeId
import com.kispoko.tome.rts.entity.theme.ThemeManager
import com.kispoko.tome.util.configureToolbar
import effect.Err
import effect.Val



/**
 * Dice Roll Activity
 */
class DiceRollerActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // Input
    // -----------------------------------------------------------------------------------------

    private var diceRollGroup : DiceRollGroup? = null
    private var autoRolls     : Int = 0
    private var entityId      : EntityId? = null

    // UI
    // -----------------------------------------------------------------------------------------

    private var diceRollerUI : DiceRollerUI? = null


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_dice_roller)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("dice_roll_group"))
            this.diceRollGroup = this.intent.getSerializableExtra("dice_roll_group") as DiceRollGroup

        if (this.intent.hasExtra("auto_rolls"))
            this.autoRolls = this.intent.getIntExtra("auto_rolls", 0)

        if (this.intent.hasExtra("entity_id"))
            this.entityId = this.intent.getSerializableExtra("entity_id") as EntityId

        // (3) Initialize Views
        // -------------------------------------------------------------------------------------

        val entityId = this.entityId

        // Toolbar
        this.configureToolbar(getString(R.string.dice_roller), TextFontStyle.Medium)

        // Theme
        if (entityId != null)
        {
            val theme = entityThemeId(entityId) ap { ThemeManager.theme(it) }
            when (theme)
            {
                is Val -> this.applyTheme(theme.value)
                is Err -> ApplicationLog.error(theme.error)
            }

        }

        // Dice Roller UI
        this.initializeViews()

        // Floating Action Button
        this.initializeFABView()
    }


    override fun onSaveInstanceState(outState : Bundle)
    {
//        val gameId = this.gameId
//        if (gameId != null)
//            outState.putSerializable("game_id", gameId)

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState)
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeFABView()
    {
        val fabView = this.findViewById(R.id.roll_button)
        fabView?.setOnClickListener {
            this.diceRollerUI?.roll()
        }
    }



    private fun applyTheme(theme : Theme)
    {
        val uiColors = theme.uiColors()

        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = this.window

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = theme.colorOrBlack(uiColors.toolbarBackgroundColorId())
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        // Toolbar > Background
        toolbar.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById(R.id.toolbar_back_button) as ImageView
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val optionsButton = this.findViewById(R.id.toolbar_options_button) as ImageView
        optionsButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------

        val titleView = this.findViewById(R.id.toolbar_title) as TextView
        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))

    }


    private fun initializeViews()
    {
        val layout = findViewById(R.id.dice_roller_layout) as LinearLayout

        val diceRollGroup = this.diceRollGroup
        val entityId = this.entityId

        if (diceRollGroup != null && entityId != null)
        {
            val diceRollerUI = DiceRollerUI(diceRollGroup, autoRolls, entityId, this)
            this.diceRollerUI = diceRollerUI
            layout?.addView(diceRollerUI.view())
        }
    }


}



class DiceRollerUI(val diceRollGroup : DiceRollGroup,
                   val autoRolls : Int,
                   val entityId : EntityId,
                   val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var rolls : Int = 0

    val diceRolls : List<DiceRoll> = diceRollGroup.diceRolls(entityId)

    var rollListView : LinearLayout? = null


    fun roll()
    {
        this.rolls += 1

        this.diceRolls.forEach {
            this.rollListView?.addView(this.rollView(it.rollSummary()))
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        layout.addView(this.headerView())

        // Rolls
        val rollsScrollView = this.rollsScrollView()
        val rollListView = this.rollListView()
        this.rollListView = rollListView
        rollsScrollView.addView(rollListView)
        layout.addView(rollsScrollView)

        // Footer
//        layout.addView(this.footerView())

        for (i in 1..autoRolls) {
            this.roll()
        }

        return layout
    }


    fun viewLayout() : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        layout.backgroundColor  = colorOrBlack(colorTheme, entityId)

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // HEADER VIEW
    // -----------------------------------------------------------------------------------------

    private fun headerView() : LinearLayout
    {
        val layout      = this.headerViewLayout()

        val buttonLayout  = this.headerButtonViewLayout()

        buttonLayout.addView(this.headerButtonView(R.string.clear, R.drawable.icon_delete, 17))

        buttonLayout.addView(this.headerButtonView(R.string.options, R.drawable.icon_gear, 16))

        buttonLayout.addView(this.headerButtonView(R.string.stats, R.drawable.icon_chart, 16))

        layout.addView(buttonLayout)

        layout.addView(this.headerBottomBorderView())

        return layout
    }


    private fun headerButtonViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.backgroundColor  = Color.WHITE

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f
        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        return layout.linearLayout(context)
    }


    private fun headerViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE

        return layout.linearLayout(context)
    }


    private fun headerBottomBorderView() : LinearLayout
    {
        val divider                 = LinearLayoutBuilder()

        divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp            = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_7"))))
        divider.backgroundColor     = colorOrBlack(colorTheme, entityId)

        return divider.linearLayout(context)
    }

    private fun headerButtonView(labelId : Int, iconId : Int, iconSize : Int) : LinearLayout
    {

        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val icon            = ImageViewBuilder()
        val label           = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = 0
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight               = 1f

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER

        layout.margin.rightDp       = 2f
        layout.margin.leftDp        = 2f

        layout.padding.topDp        = 8f
        layout.padding.bottomDp     = 8f

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_2"))))
        layout.backgroundColor      = colorOrBlack(bgColorTheme, entityId)

        layout.corners              = Corners(2.0, 2.0, 2.0, 2.0)

        layout.child(icon)
              .child(label)

        // (3 B) Dice
        // -------------------------------------------------------------------------------------

        icon.widthDp            = iconSize
        icon.heightDp           = iconSize

        icon.image              = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        icon.color              = colorOrBlack(iconColorTheme, entityId)

        icon.margin.rightDp     = 5f

        // (3 C) Description
        // -------------------------------------------------------------------------------------

        label.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId          = labelId

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color           = colorOrBlack(labelColorTheme, entityId)

        label.font            = Font.typeface(TextFont.default(),
                                              TextFontStyle.Medium,
                                              context)

        label.sizeSp          = 17f

        label.padding.bottomDp  = 1f

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // ROLLS VIEW
    // -----------------------------------------------------------------------------------------

    private fun rollsScrollView() : ScrollView
    {
        val scrollView              = ScrollViewBuilder()

        scrollView.width            = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height           = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        scrollView.backgroundColor  = colorOrBlack(colorTheme, entityId)

        scrollView.fadingEnabled    = false

        return scrollView.scrollView(context)
    }


    private fun rollListView() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        layout.backgroundColor      = colorOrBlack(colorTheme, entityId)

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // ROLL VIEW
    // -----------------------------------------------------------------------------------------


    private fun rollView(rollSummary : RollSummary) : LinearLayout
    {
        val layout = this.rollViewLayout()

        layout.addView(this.rollResultView(rollSummary))

        layout.addView(this.rollResultRightBorderView())

        layout.addView(this.rollInfoView(rollSummary.name, rollSummary.parts))

        return layout
    }


    private fun rollViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor     = Color.WHITE

        layout.margin.leftDp        = 4f
        layout.margin.rightDp       = 4f
        layout.margin.topDp         = 4f

        return layout.linearLayout(context)
    }


    private fun rollResultView(rollSummary : RollSummary) : RelativeLayout
    {
        val layout = this.rollResultViewLayout()

        layout.addView(this.rollValueView(rollSummary.value))

        layout.addView(this.rollIndexView())

        return layout
    }


    private fun rollResultViewLayout() : RelativeLayout
    {
        val layout                  = RelativeLayoutBuilder()

        layout.widthDp              = 55
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation          = LinearLayout.VERTICAL


//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
//        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)
        layout.backgroundColor      = Color.WHITE


        layout.padding.topDp         = 8f
        layout.padding.bottomDp      = 5f

        layout.margin.leftDp        = 6f
        layout.margin.rightDp       = 6f

        layout.padding.leftDp       = 5f
        layout.padding.rightDp       = 5f

        return layout.relativeLayout(context)
    }


    private fun rollValueView(rollValue : Int) : TextView
    {
        val value                   = TextViewBuilder()

        val valueString             = rollValue.toString()

        value.layoutType            = LayoutType.RELATIVE
        value.width                 = RelativeLayout.LayoutParams.WRAP_CONTENT
        value.height                = RelativeLayout.LayoutParams.WRAP_CONTENT

        value.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        value.addRule(RelativeLayout.CENTER_HORIZONTAL)

        value.gravity               = Gravity.CENTER

        value.text                  = valueString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        value.color                 = colorOrBlack(colorTheme, entityId)
//        value.color                 = Color.WHITE

//        value.backgroundResource    = R.drawable.bg_roll_result

        value.font                  = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Bold,
                                                    context)

        value.sizeSp                = 28f

        return value.textView(context)
    }


    private fun rollIndexView() : TextView
    {
        val value                   = TextViewBuilder()

        value.layoutType            = LayoutType.RELATIVE
        value.width                 = RelativeLayout.LayoutParams.WRAP_CONTENT
        value.height                = RelativeLayout.LayoutParams.WRAP_CONTENT

        value.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        value.addRule(RelativeLayout.CENTER_HORIZONTAL)

        value.text                  = "roll ${this.rolls}"

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_24"))))
        value.color                 = colorOrBlack(colorTheme, entityId)

        value.font                  = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        value.margin.topDp          = 5f

        value.sizeSp                = 14f

        return value.textView(context)
    }


    private fun rollInfoView(rollName : String,
                             partSummaries : List<RollPartSummary>) : LinearLayout
    {
        val layout = rollInfoViewLayout()

        layout.addView(this.rollDescriptionView(rollName))

        layout.addView(this.rollDetailsView(partSummaries))

        return layout
    }


    private fun rollInfoViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.padding.topDp        = 6f
        layout.padding.bottomDp     = 6f

        layout.margin.leftDp        = 10f


        return layout.linearLayout(context)
    }


    private fun rollDescriptionView(rollName : String) : TextView
    {
        val description                 = TextViewBuilder()

        description.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        description.text                = rollName

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        description.color              = colorOrBlack(textColorTheme, entityId)

        description.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        description.sizeSp             = 21f

        return description.textView(context)
    }


    private fun rollDetailsView(partSummaries : List<RollPartSummary>) : FlexboxLayout
    {
        val layout                      = FlexboxLayoutBuilder()

        layout.width                    = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.contentAlignment         = AlignContent.CENTER
        layout.wrap                     = FlexWrap.WRAP

        layout.margin.topDp             = 8f

        layout.padding.rightDp          = 6f

        partSummaries.forEach {
            layout.child(this.rollPartSummaryView(it))
        }

        return layout.flexboxLayout(context)
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

        layout.gravity              = Gravity.BOTTOM

        layout.margin.rightDp       = 5f
        layout.margin.bottomDp      = 3f

        layout.padding.topDp        = 2f
        layout.padding.bottomDp     = 2f
        layout.padding.leftDp       = 5f
        layout.padding.rightDp      = 5f


        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_2"))))
        layout.backgroundColor      = colorOrBlack(bgColorTheme, entityId)

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
        value.color                 = colorOrBlack(valueColorTheme, entityId)

        value.sizeSp                = 17f

        value.font                  = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Medium,
                                                    context)

        value.margin.rightDp        = 4f

        // (3 B) Dice
        // -------------------------------------------------------------------------------------

        dice.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
        dice.height                 = LinearLayout.LayoutParams.WRAP_CONTENT

        dice.text                   = rollPartSummary.dice

        dice.margin.rightDp         = 4f

        val diceColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        dice.color                  = colorOrBlack(diceColorTheme, entityId)

        dice.sizeSp                 = 15f

        dice.font                   = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        // (3 C) Description
        // -------------------------------------------------------------------------------------

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        description.text            = rollPartSummary.tag

        val descColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        description.color           = colorOrBlack(descColorTheme, entityId)

        description.font            = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        description.sizeSp          = 15f

        return layout
    }


    private fun rollResultRightBorderView() : LinearLayout
    {
        val divider = LinearLayoutBuilder()

        divider.widthDp             = 1
        divider.height              = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        divider.backgroundColor     = colorOrBlack(colorTheme, entityId)

        return divider.linearLayout(context)
    }

}

