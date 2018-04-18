
package com.kispoko.tome.activity.sheet.procedure


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.dialog.SimpleAdderDialog
import com.kispoko.tome.app.AppSettings
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.EngineValue
import com.kispoko.tome.model.game.engine.EngineValueNumber
import com.kispoko.tome.model.game.engine.procedure.Procedure
import com.kispoko.tome.model.game.engine.procedure.ProcedureId
import com.kispoko.tome.model.game.engine.procedure.ProcedureInvocation
import com.kispoko.tome.model.game.engine.program.ProgramId
import com.kispoko.tome.model.game.engine.program.ProgramParameter
import com.kispoko.tome.model.game.engine.program.ProgramParameterNumber
import com.kispoko.tome.model.game.engine.program.ProgramParameterValues
import com.kispoko.tome.model.game.engine.variable.constraint.NumberConstraint
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.IconSize
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.sheet.widget.HighlightSkew
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.color
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.procedure
import com.kispoko.tome.rts.entity.theme.ThemeManager
import com.kispoko.tome.util.Util
import com.kispoko.tome.util.configureToolbar
import effect.Err
import effect.Val
import maybe.Just
import maybe.Maybe
import java.io.Serializable



/**
 * Procedure Activity
 */
class RunProcedureActivity : NumberUpdater, AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var procedureId   : ProcedureId? = null
    private var entityId : EntityId? = null

    private val appSettings : AppSettings = AppSettings(ThemeId.Light)

    private var entityProcedure : Procedure? = null

    private var procedureUI : ComplexProcedureUI? = null


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_run_procedure)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("procedure_id"))
            this.procedureId = this.intent.getSerializableExtra("procedure_id") as ProcedureId

        if (this.intent.hasExtra("entity_id"))
            this.entityId = this.intent.getSerializableExtra("entity_id") as EntityId

        // Load Procedure
        val procedureId = this.procedureId
        val entityId = this.entityId
        if (procedureId != null && entityId != null) {
            val _procedure = procedure(procedureId, entityId)
            when (_procedure) {
                is Val -> this.entityProcedure = _procedure.value
                is Err -> ApplicationLog.error(_procedure.error)
            }
        }

        // (3) Initialize Views
        // -------------------------------------------------------------------------------------


        // > Toolbar
        this.configureToolbar(getString(R.string.perform_action), TextFontStyle.Medium)

        val statusBarColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        ThemeManager.color(this.appSettings.themeId, statusBarColorTheme)?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = it
            }
        }

        // > Theme
        val theme = ThemeManager.theme(this.appSettings.themeId())
        when (theme) {
            is Val -> this.applyTheme(theme.value.uiColors())
            is Err -> ApplicationLog.error(theme.error)
        }

        this.initializeFABView()

        // > Tab Views
        this.initializeViews()
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
        val fabView = this.findViewById<View>(R.id.run_procedure_button)
        fabView.setOnClickListener {
            val entityProcedure = this.entityProcedure
            val procedureUI = this.procedureUI
            val entityId = this.entityId
            if (entityProcedure != null && procedureUI != null && entityId != null)
            {
                val invocation = ProcedureInvocation(entityProcedure.procedureId(),
                                                     procedureUI.parameterValuesByProgram())
                val resultData = Intent()
                resultData.putExtra("procedure_invocation", invocation)

                this.setResult(RESULT_OK, resultData)
                this.finish()
            }
        }
    }


    private fun initializeViews()
    {
        val entityProcedure = this.entityProcedure
        val entityId = this.entityId
        if (entityProcedure != null && entityId != null)
        {
            val contentLayout = this.findViewById<LinearLayout>(R.id.content)

            val procedureUI = ComplexProcedureUI(entityProcedure, entityId, this)
            contentLayout.addView(procedureUI.view())
            this.procedureUI = procedureUI

//            val footerLayout = findViewById(R.id.footer) as LinearLayout
//            footerLayout.addView(procedureUI.procedureUsedCountView())
        }
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
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

        val toolbarBgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))

        // Toolbar > Background
        ThemeManager.color(this.appSettings.themeId, toolbarBgColorTheme)?.let {
            toolbar.setBackgroundColor(it)
        }

        // Toolbar > Icons
        val toolbarIconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))

        ThemeManager.color(this.appSettings.themeId, toolbarIconColorTheme)?.let {
            val menuLeftButton = this.findViewById<ImageButton>(R.id.toolbar_close_button)
            menuLeftButton.colorFilter = PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)

            val menuRightButton = this.findViewById<ImageButton>(R.id.toolbar_options_button)
            menuRightButton.colorFilter = PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN)
        }


        // TITLE
        // -------------------------------------------------------------------------------------

        val titleColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))

        ThemeManager.color(this.appSettings.themeId, toolbarIconColorTheme)?.let {
            val titleView = this.findViewById<TextView>(R.id.toolbar_title)
            titleView.setTextColor(it)
        }

    }


    // -----------------------------------------------------------------------------------------
    // UPDATES
    // -----------------------------------------------------------------------------------------

    override fun updateNumber(key : String, value : Double) {

    }

}



class ComplexProcedureUI(val procedure : Procedure,
                         val entityId : EntityId,
                         val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val parametersByProgram = procedure.parameters(entityId)

    val parameterValuesByProgram : MutableMap<ProgramId,Map<String,EngineValue>> = mutableMapOf()

    val parameters : MutableList<ProgramParameter> = mutableListOf()

    init {
        parametersByProgram.entries.forEach { e ->
            val programId = e.key
            val parameters = e.value
            val parameterValues : MutableMap<String,EngineValue> = mutableMapOf()

            parameters.forEach { parameter ->
                val defaultValue = parameter.parameterDefaultValue()
                when (defaultValue) {
                    is Just -> parameterValues.put(parameter.name().value, defaultValue.value)
                }
            }

        }

        parametersByProgram.values.forEach {
            it.forEach {
                parameters.add(it)
            }
        }

    }


    // -----------------------------------------------------------------------------------------
    // PARAMETER VALUES
    // -----------------------------------------------------------------------------------------

    fun parameterValuesByProgram() : Map<ProgramId,ProgramParameterValues>
    {
        val m : MutableMap<ProgramId, ProgramParameterValues> = mutableMapOf()

        this.parameterValuesByProgram.forEach {
            m.put(it.key, ProgramParameterValues(it.value))
        }

        return m
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        layout.addView(this.procedureNameView())

        layout.addView(this.procedureDescriptionView())

        var index = 0
        parametersByProgram.entries.forEach { e ->
            val programId = e.key
            e.value.forEach { parameter ->
                layout.addView(this.parameterView(parameter, programId, index))
                index += 1
            }

        }

        layout.addView(this.procedureUsedCountView())

        return layout
    }



    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.leftDp    = 6f
        layout.margin.rightDp   = 6f
        layout.margin.topDp     = 6f

        return layout.linearLayout(context)
    }


    private fun procedureNameView() : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.MATCH_PARENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.backgroundColor    = Color.WHITE

        name.corners            = Corners(3.0, 3.0, 0.0, 0.0)

        name.padding.topDp      = 10f
        name.padding.bottomDp   = 10f
        name.padding.leftDp     = 8f
        name.padding.rightDp    = 8f

        name.text               = procedure.actionLabel().value

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        name.sizeSp             = 23f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        name.color              = colorOrBlack(colorTheme, entityId)

        return name.textView(context)

    }


    private fun procedureDescriptionView() : TextView
    {
        val description                = TextViewBuilder()

        description.width              = LinearLayout.LayoutParams.MATCH_PARENT
        description.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        description.backgroundColor    = Color.WHITE

        description.margin.topDp       = 2f

        description.padding.topDp      = 10f
        description.padding.bottomDp   = 10f
        description.padding.leftDp     = 8f
        description.padding.rightDp    = 8f

        val descriptionTemplate = procedure.description()
        when (descriptionTemplate) {
            is Just -> {
                description.text = descriptionTemplate.value.stringFromVariables(entityId)
            }
        }

        description.font               = Font.typeface(TextFont.default(),
                                                       TextFontStyle.Regular,
                                                       context)

        description.sizeSp             = 18f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        description.color              = colorOrBlack(colorTheme, entityId)

        return description.textView(context)

    }


    private fun parameterView(parameter : ProgramParameter,
                              programId : ProgramId,
                              index : Int) : LinearLayout
    {
        val layout = this.parmeterViewLayout(index)

        layout.addView(this.parameterEditIconView())

        layout.addView(this.parameterDescriptionView(parameter))

        layout.setOnClickListener {

            when (parameter)
            {
                is ProgramParameterNumber ->
                {
                    if (this.parameterValuesByProgram.containsKey(programId))
                    {
                        val parameterValues = this.parameterValuesByProgram[programId]!!

                        val parameterName = parameter.name.value
                        if (parameterValues.containsKey(parameterName))
                        {
                            val parameterValue = parameterValues[parameterName]!!

                            when (parameterValue)
                            {
                                is EngineValueNumber ->
                                {
                                    val activity = context as RunProcedureActivity
                                    val updateRequest = NumberUpdateRequest(index.toString(),
                                                                            parameterValue.value,
                                                                            parameter.constraint)
                                    val adderDialog = SimpleAdderDialog.newInstance(parameter.label().value,
                                                                                    updateRequest,
                                                                                    entityId)
                                    adderDialog.show(activity.supportFragmentManager, "")
                                }
                            }

                        }

                    }
                }
            }

        }

        return layout
    }


    private fun parmeterViewLayout(index : Int) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.backgroundColor      = Color.WHITE

        layout.margin.topDp         = 2f

        layout.padding.topDp        = 8f
        layout.padding.bottomDp     = 8f
        layout.padding.leftDp       = 8f
        layout.padding.rightDp      = 8f

        if (index == (parameters.size - 1)) {
            layout.corners          = Corners(0.0, 0.0, 3.0, 3.0)
        }

        return layout.linearLayout(context)
    }


    private fun parameterEditIconView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val icon                    = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundResource   = R.drawable.bg_procedure_parameter_edit

        layout.gravity              = Gravity.CENTER

        layout.padding.topDp        = 2f

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp                = 17
        icon.heightDp               = 17

        icon.image                  = R.drawable.icon_edit

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color                  = colorOrBlack(colorTheme, entityId)

        return layout.linearLayout(context)
    }


    private fun parameterDescriptionView(parameter : ProgramParameter) : TextView
    {
        val param                   = TextViewBuilder()

        param.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        param.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        param.padding.leftDp        = 8f

        param.textSpan              = this.parameterSpannableString(parameter.inputMessage().stringFromVariables(entityId),
                                                                    parameter.defaultValueString())

        param.font                  = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Medium,
                                                    context)

        param.sizeSp                = 18f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        param.color                 = colorOrBlack(colorTheme, entityId)

        return param.textView(context)
    }


    private fun parameterSpannableString(description : String,
                                         valueString : String) : SpannableStringBuilder
    {
        val builder = SpannableStringBuilder()
        var currentIndex = 0

        val paddedValueString = "   $valueString   "

        // Colors
        val paramBgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_80"))))

        // Spans

        // > Size
//
//        // > Typeface
//        val typeface = Font.typeface(textStyle.font(), textStyle.fontStyle(), sheetUIContext.context)
//        val typefaceSpan = CustomTypefaceSpan(typeface)
//
//        // > Color
//        val colorSpan = ForegroundColorSpan(Color.WHITE)


        // String Parts
        val parts = description.split("&&&")

        // Part 1
        // -------------------------------------------------------------------------------------
        if (parts.isNotEmpty()) {
            builder.append(parts[0])
            val nextIndex = parts[0].length

//            builder.setSpan(sizeSpan, currentIndex, nextIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//            builder.setSpan(typefaceSpan, currentIndex, nextIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//            builder.setSpan(colorSpan, currentIndex, nextIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

            currentIndex = nextIndex
        }

        // Value
        // -------------------------------------------------------------------------------------

        builder.append(paddedValueString)

        val nextIndex = currentIndex + paddedValueString.length

//        builder.setSpan(sizeSpan, currentIndex, nextIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//        builder.setSpan(typefaceSpan, currentIndex, nextIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//        builder.setSpan(colorSpan, currentIndex, nextIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

//        val drawable = VectorDrawableCompat.create(sheetUIContext.context.resources,
//                                                   R.drawable.icon_edit,
//                                                   null)
//
//        drawable?.colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)

        val lineSpacingPx = Util.spToPx(4f, context)
        val lineHeightPx = Util.spToPx(26f, context)
        color(paramBgColorTheme, entityId) apDo { bgColor ->

//            val bgSpan = BackgroundColorSpan(bgColor)
            val bgSpan = RoundedBackgroundHeightSpan(lineHeightPx,
                                                     lineSpacingPx,
                                                     HighlightSkew.default().value,
                                                     12,
                                                     Color.WHITE,
                                                     bgColor,
                                                     null,
                                                     IconSize(17, 17),
                                                     Color.WHITE)

            builder.setSpan(bgSpan, currentIndex, nextIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//            builder.setSpan(colorSpan, currentIndex, nextIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        val sizeSpan = AbsoluteSizeSpan(Util.spToPx(20f, context))
        builder.setSpan(sizeSpan, currentIndex, nextIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        currentIndex = nextIndex

        // Part 2
        // -------------------------------------------------------------------------------------

        if (parts.size >= 2) {
            builder.append(parts[1])
            val nextIndex = currentIndex + parts[1].length

//            builder.setSpan(sizeSpan, currentIndex, nextIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//            builder.setSpan(typefaceSpan, currentIndex, nextIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//            builder.setSpan(colorSpan, currentIndex, nextIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

            currentIndex = nextIndex
        }


//
//        if (iconColor != null) {
//            drawable?.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
//        }




        return builder
    }


    fun procedureUsedCountView() : TextView
    {
        val uses                = TextViewBuilder()

        uses.width              = LinearLayout.LayoutParams.MATCH_PARENT
        uses.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        uses.margin.topDp       = 10f
        uses.margin.leftDp      = 2f

        uses.layoutGravity      = Gravity.CENTER_VERTICAL

        val usedCountMessage = procedure.statistics().usedCountMessage()
        when (usedCountMessage) {
            is Just -> {
                val usedCountString = procedure.statistics().usedCount.toString()
                Log.d("***RUN PROC ACTIVITY", "used: $usedCountString")
                uses.text       = usedCountMessage.value.templateString(listOf(usedCountString))
            }
        }

        uses.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        uses.sizeSp             = 16f

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        uses.color              = colorOrBlack(colorTheme, entityId)

        return uses.textView(context)

    }


}


data class NumberUpdateRequest(val key : String,
                               val currentValue : Double,
                               val constraint : Maybe<NumberConstraint>) : Serializable


interface NumberUpdater
{
    fun updateNumber(key : String, value : Double)
}
