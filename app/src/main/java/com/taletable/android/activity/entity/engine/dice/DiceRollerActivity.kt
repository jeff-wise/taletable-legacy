
package com.taletable.android.activity.entity.engine.dice


import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.widget.NestedScrollView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.taletable.android.R
import com.taletable.android.R.string.value
import com.taletable.android.lib.ui.*
import com.taletable.android.model.engine.dice.DiceRoll
import com.taletable.android.model.engine.dice.DiceRollGroup
import com.taletable.android.model.engine.dice.RollPartSummary
import com.taletable.android.model.engine.dice.RollSummary
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.util.configureToolbar



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
        this.configureToolbar(getString(R.string.dice_roller),
                              TextFont.RobotoCondensed,
                              TextFontStyle.Bold)

        // Theme
//        if (entityId != null)
//        {
//            val theme = entityThemeId(entityId) ap { ThemeManager.theme(it) }
//            when (theme)
//            {
//                is Val -> this.applyTheme(theme.value)
//                is Err -> ApplicationLog.error(theme.error)
          //   }

        // }

        this.applyTheme(officialAppThemeLight)

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
        val fabView = this.findViewById<View>(R.id.fab)
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

            val statusBarColorTheme = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
            window.statusBarColor = theme.colorOrBlack(statusBarColorTheme)
//            window.statusBarColor = theme.colorOrBlack(uiColors.toolbarBackgroundColorId())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val flags = window.decorView.getSystemUiVisibility() or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.setSystemUiVisibility(flags)
            this.getWindow().setStatusBarColor(Color.WHITE);
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

        // Toolbar > Background
        toolbar.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById<ImageView>(R.id.toolbar_back_button)
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val optionsButton = this.findViewById<ImageView>(R.id.toolbar_options_button)
        optionsButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------

        val titleView = this.findViewById<TextView>(R.id.toolbar_title)
        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))

    }


    private fun initializeViews()
    {
        val scrollView = this.findViewById<NestedScrollView>(R.id.roll_list)

        val diceRollGroup = this.diceRollGroup
        val entityId = this.entityId

        if (diceRollGroup != null && entityId != null)
        {
            val diceRollerUI = DiceRollerUI(diceRollGroup, autoRolls, entityId, this)
            this.diceRollerUI = diceRollerUI
            scrollView?.addView(diceRollerUI.view())
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
        // Rolls
        val rollListView = this.rollListView()
        this.rollListView = rollListView

        for (i in 1..autoRolls) {
            this.roll()
        }

        return rollListView
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
    // ROLLS VIEW
    // -----------------------------------------------------------------------------------------

    fun rollListView() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
//        layout.backgroundColor      = colorOrBlack(colorTheme, entityId)

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // ROLL VIEW
    // -----------------------------------------------------------------------------------------

    private fun rollView(rollSummary : RollSummary) : LinearLayout
    {
        val layout = this.rollViewLayout()

        layout.addView(this.rollDescriptionView(rollSummary.name))

        layout.addView(this.rollMainView(rollSummary))


        return layout
    }


    private fun rollViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundColor     = Color.WHITE

        layout.corners              = Corners(2.0, 2.0, 2.0, 2.0)

//        layout.elevation            = 5f

        layout.margin.leftDp        = 6f
        layout.margin.rightDp       = 6f
        layout.margin.topDp         = 6f
//        layout.margin.bottomDp      = 3f

        return layout.linearLayout(context)
    }



    private fun rollMainView(rollSummary : RollSummary) : LinearLayout
    {
        val layout = this.rollMainViewLayout()

        layout.addView(this.rollResultView(rollSummary))

        // layout.addView(this.rollResultRightBorderView())

        layout.addView(this.rollInfoView(rollSummary.name, rollSummary.parts))

        return layout
    }


    private fun rollMainViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        // layout.backgroundColor     = Color.WHITE

//        layout.corners              = Corners(2.0, 2.0, 2.0, 2.0)
//
//        layout.margin.leftDp        = 6f
//        layout.margin.rightDp       = 6f
//        layout.margin.topDp         = 12f

        return layout.linearLayout(context)
    }


    private fun rollResultView(rollSummary : RollSummary) : RelativeLayout
    {
        val layout = this.rollResultViewLayout()

        layout.addView(this.rollValueView(rollSummary.value))

        // layout.addView(this.rollIndexView())

        return layout
    }


    private fun rollResultViewLayout() : RelativeLayout
    {
        val layout                  = RelativeLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation          = LinearLayout.VERTICAL


        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("purple_tint_2"))))
        layout.backgroundColor      = colorOrBlack(colorTheme, entityId)
        layout.backgroundColor      = Color.WHITE

        layout.corners              = Corners(3.0, 0.0, 0.0, 3.0)


//        layout.padding.topDp         = 8f
//        layout.padding.bottomDp      = 5f
//
//        layout.margin.leftDp        = 6f
//        layout.margin.rightDp       = 6f
//
//        layout.padding.leftDp       = 15f
//        layout.padding.rightDp       = 6f

        layout.padding.leftDp           = 10f

        // layout.corners              = Corners(26.0, 0.0, 0.0, 26.0)

        layout.gravity              = Gravity.CENTER_VERTICAL

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
//        value.addRule(RelativeLayout.CENTER_HORIZONTAL)

        value.gravity               = Gravity.CENTER

        value.text                  = valueString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_1"))))
        value.color                 = colorOrBlack(colorTheme, entityId)

//        value.backgroundResource    = R.drawable.bg_roll_result

        value.font                  = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Bold,
                                                    context)

        value.sizeSp                = 60f

        value.margin.topDp          = -10f

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

        value.font                  = Font.typeface(TextFont.RobotoCondensed,
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

    //     layout.addView(this.rollDescriptionView(rollName))

        layout.addView(this.rollDetailsView(partSummaries))

        return layout
    }


    private fun rollInfoViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.padding.bottomDp     = 6f

        layout.padding.leftDp        = 10f

        layout.corners              = Corners(0.0, 3.0, 3.0, 0.0)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("purple_tint_3"))))
        layout.backgroundColor      = colorOrBlack(bgColorTheme, entityId)
        layout.backgroundColor      = Color.WHITE

        layout.gravity              = Gravity.TOP

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_18"))))
        description.color              = colorOrBlack(textColorTheme, entityId)

        description.font               = Font.typeface(TextFont.Roboto,
                                                TextFontStyle.Regular,
                                                context)

        description.sizeSp             = 18f

        description.margin.leftDp       = 10f
        description.margin.topDp       = 8f

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

        layout.padding.topDp        = 3f
        layout.padding.bottomDp     = 3f
        layout.padding.leftDp       = 8f
        layout.padding.rightDp      = 8f


        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_4"))))
        layout.backgroundColor      = colorOrBlack(bgColorTheme, entityId)

        layout.corners              = Corners(8.0, 8.0, 8.0, 8.0)

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_2"))))
        value.color                 = colorOrBlack(valueColorTheme, entityId)

        value.sizeSp                = 18f

        value.font                  = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Bold,
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        dice.color                  = colorOrBlack(diceColorTheme, entityId)

        dice.sizeSp                 = 18f

        dice.font                   = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Regular,
                                                    context)

        // (3 C) Description
        // -------------------------------------------------------------------------------------

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        description.text            = rollPartSummary.tag

        val descColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_26")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        description.color           = colorOrBlack(descColorTheme, entityId)

        description.font            = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Regular,
                                                    context)

        description.sizeSp          = 18f

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

