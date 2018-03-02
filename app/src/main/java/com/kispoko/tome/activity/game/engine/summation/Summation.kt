
package com.kispoko.tome.activity.game.engine.summation


import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.dialog.NumberEditorDialog
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.reference.NumberReferenceVariable
import com.kispoko.tome.model.game.engine.summation.Summation
import com.kispoko.tome.model.game.engine.summation.SummationId
import com.kispoko.tome.model.game.engine.summation.term.SummationTermNumber
import com.kispoko.tome.model.game.engine.summation.term.TermSummary
import com.kispoko.tome.model.game.engine.variable.NumberVariable
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.router.MessageUpdateSummationNumberTerm
import com.kispoko.tome.router.Router
import com.kispoko.tome.rts.entity.game.GameManager
import com.kispoko.tome.rts.entity.sheet.SheetContext
import com.kispoko.tome.rts.entity.sheet.SheetManager
import com.kispoko.tome.rts.entity.sheet.UpdateTargetSummationNumberTerm
import com.kispoko.tome.rts.entity.theme.ThemeManager
import com.kispoko.tome.util.configureToolbar
import effect.Err
import maybe.Just
import effect.Val



/**
 * Summation Activity
 */
class SummationActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var summationId  : SummationId? = null
    private var sheetContext : SheetContext? = null

    private var summation    : Summation? = null

    private val appSettings  : AppSettings = AppSettings(ThemeId.Dark)


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_summation)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("summation_id"))
            this.summationId = this.intent.getSerializableExtra("summation_id") as SummationId

        if (this.intent.hasExtra("sheet_context"))
            this.sheetContext = this.intent.getSerializableExtra("sheet_context") as SheetContext

        // (3) Lookup Value Set
        // -------------------------------------------------------------------------------------

        val sheetContext = this.sheetContext
        val summationId = this.summationId
        if (sheetContext != null && summationId != null)
        {
            val summation = GameManager.engine(sheetContext.gameId)
                                       .apply { it.summation(summationId) }

            when (summation) {
                is Val -> this.summation = summation.value
                is Err -> ApplicationLog.error(summation.error)
            }
        }

        // (4) Initialize Views
        // -------------------------------------------------------------------------------------

        val summation = this.summation

        // Toolbar
        if (summation != null)
            this.configureToolbar(summation.summationNameString())

        // Theme
        val theme = ThemeManager.theme(this.appSettings.themeId())
        when (theme) {
            is Val -> this.applyTheme(theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        // Summation View
        if (summation != null && sheetContext != null)
            this.renderView(summation, sheetContext)

        // (5) Other
        // -------------------------------------------------------------------------------------

        this.initializeMessaging()

    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun renderView(summation : Summation, sheetContext : SheetContext)
    {
        val contentScrollView = this.findViewById(R.id.content) as ScrollView?
        val viewBuilder = SummationViewBuilder(summation,
                                               this.appSettings.themeId(),
                                               sheetContext,
                                               this)
        contentScrollView?.removeAllViews()
        contentScrollView?.addView(viewBuilder.view())
    }

    private fun applyTheme(uiColors : UIColors)
    {
        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = this.window

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = this.appSettings.color(uiColors.toolbarBackgroundColorId())
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        // Toolbar > Background
        toolbar.setBackgroundColor(this.appSettings.color(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = this.appSettings.color(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById(R.id.toolbar_back_button) as ImageButton
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val searchButton = this.findViewById(R.id.toolbar_options_button) as ImageButton
        searchButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TOOLBAR TITLE
        // -------------------------------------------------------------------------------------
        val toolbarTitleView = this.findViewById(R.id.toolbar_title) as TextView
        toolbarTitleView.setTextColor(this.appSettings.color(uiColors.toolbarTitleColorId()))

        // TITLE
        // -------------------------------------------------------------------------------------

        val titleView = this.findViewById(R.id.title) as TextView
        titleView.typeface = Font.typeface(TextFont.FiraSans, TextFontStyle.Regular, this)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        val color = ThemeManager.color(this.appSettings.themeId(), colorTheme)
        if (color != null)
            titleView.setTextColor(color)

    }


    // -----------------------------------------------------------------------------------------
    // MESSAGING
    // -----------------------------------------------------------------------------------------

    private fun initializeMessaging()
    {
        Router.listen(MessageUpdateSummationNumberTerm::class.java).subscribe({
            this.onNumberTermUpdate(it)
        })
    }


    private fun onNumberTermUpdate(message : MessageUpdateSummationNumberTerm)
    {
        Log.d("***SUMMATION", "on number term update")

        val term = summation?.termWithId(message.id)
        val sheetContext = this.sheetContext
        if (term != null && sheetContext != null)
        {
            when (term)
            {
                is SummationTermNumber ->
                {
                    val numberReference = term.numberReference()
                    when (numberReference)
                    {
                        is NumberReferenceVariable ->
                        {
                            val variableReference = numberReference.variableReference
                            when (variableReference)
                            {
                                is VariableId ->
                                {
                                    val variableEff = SheetManager.sheetState(sheetContext.sheetId)
                                                     .apply { it.variableWithId(variableReference) }
                                    when (variableEff)
                                    {
                                        is Val -> {
                                            val variable = variableEff.value
                                            when (variable)
                                            {
                                                is NumberVariable ->
                                                {
                                                    variable.updateValue(message.newValue, sheetContext)
                                                    val summation = this.summation
                                                    if (summation != null)
                                                        this.renderView(summation, sheetContext)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }



        }
    }


}


class SummationViewBuilder(val summation : Summation,
                           val themeId : ThemeId,
                           val sheetContext : SheetContext,
                           val context : AppCompatActivity)
{

    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        layout.addView(this.summationView())

        return layout
    }


    fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    // Summation View
    // -----------------------------------------------------------------------------------------

    private fun summationView() : LinearLayout
    {
        val layout = this.summationViewLayout()

        // Components
        val termSummaries = summation.summary(sheetContext)
        layout.addView(this.componentsView(termSummaries))

        return layout
    }


    private fun summationViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.margin.bottomDp      = 10f
        layout.margin.topDp      = 15f

        return layout.linearLayout(context)
    }


    private fun componentsView(termSummaries : List<TermSummary>) : LinearLayout
    {
        val layout = this.componentsViewLayout()

        termSummaries.forEach { layout.addView(this.componentView(it)) }

        return layout
    }


    private fun componentsViewLayout() : LinearLayout
    {
        val layout              =  LinearLayoutBuilder()

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        return layout.linearLayout(context)
    }


    private fun componentView(termSummary : TermSummary) : LinearLayout
    {
        val layout = this.componentViewLayout()

        // Name
        if (termSummary.name != null)
            layout.addView(this.componentHeaderView(termSummary.name))

        // Components
        termSummary.components.forEach {
            layout.addView(this.componentItemView(it.name, it.value, termSummary))
        }

        return layout
    }


    private fun componentViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        return layout.linearLayout(context)
    }


    private fun componentHeaderView(termName : String) : TextView
    {
        val header                  = TextViewBuilder()

        header.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        header.padding.topDp        = 8f
        header.padding.leftDp       = 11f
        header.padding.bottomDp     = 3f

        header.text                 = termName

        header.font                 = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_29")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        header.color                = ThemeManager.color(themeId, colorTheme)

        header.sizeSp               = 12f

        return header.textView(context)
    }



    private fun componentItemView(nameString : String,
                                  valueString : String,
                                  termSummary : TermSummary) : RelativeLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout  = RelativeLayoutBuilder()
        val name    = TextViewBuilder()
        val value   = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.orientation              = LinearLayout.HORIZONTAL
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.leftDp           = 8f
        layout.padding.rightDp          = 12f
        layout.padding.topDp            = 12f
        layout.padding.bottomDp         = 12f

        layout.margin.leftDp            = 10f
        layout.margin.rightDp           = 10f
        layout.margin.bottomDp          = 10f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor          = ThemeManager.color(themeId, colorTheme)

        layout.corners                  = Corners(2.0, 2.0, 2.0, 2.0)

        layout.onClick                  = View.OnClickListener {
            val term = termSummary.term
            when (term)
            {
                is SummationTermNumber ->
                {
                    val numberReference = term.numberReference()
                    when (numberReference)
                    {
                        is NumberReferenceVariable ->
                        {
                            val variableReference = numberReference.variableReference
                            when (variableReference)
                            {
                                is VariableId ->
                                {
                                    val termValue = termSummary.term.value(sheetContext)
                                    val updateTarget = UpdateTargetSummationNumberTerm(termSummary.term.id)
                                    when (termValue) {
                                        is Just -> {
                                            val dialog = NumberEditorDialog.newInstance(
                                                            termValue.value,
                                                            termSummary.name ?: "",
                                                            updateTarget,
                                                            sheetContext)
                                            dialog.show(context.supportFragmentManager, "")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


        }

        layout.child(name)
              .child(value)

        // (3 A) Name
        // -------------------------------------------------------------------------------------

        name.layoutType                 = LayoutType.RELATIVE
        name.width                      = RelativeLayout.LayoutParams.WRAP_CONTENT
        name.height                     = RelativeLayout.LayoutParams.WRAP_CONTENT

        name.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        name.addRule(RelativeLayout.CENTER_VERTICAL)

        name.text                       = nameString

        name.font                       = Font.typeface(TextFont.FiraSans,
                                                        TextFontStyle.Regular,
                                                        context)

        name.sizeSp                     = 16f

        val nameColorTheme = ColorTheme(setOf(
                                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_17")),
                                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        name.color                      = ThemeManager.color(themeId, nameColorTheme)

        // (3 B) Value
        // -------------------------------------------------------------------------------------

        value.layoutType                = LayoutType.RELATIVE
        value.width                     = RelativeLayout.LayoutParams.WRAP_CONTENT
        value.height                    = RelativeLayout.LayoutParams.WRAP_CONTENT

        value.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        name.addRule(RelativeLayout.CENTER_VERTICAL)

        value.text                      = valueString

        value.font                      = Font.typeface(TextFont.FiraSans,
                                                        TextFontStyle.Bold,
                                                        context)

        value.sizeSp                    = 17f

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_9")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        value.color                     = ThemeManager.color(themeId, valueColorTheme)

        return layout.relativeLayout(context)
    }


}
