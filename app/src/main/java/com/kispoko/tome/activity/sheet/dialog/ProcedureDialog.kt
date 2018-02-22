
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.EngineValueType
import com.kispoko.tome.model.game.engine.procedure.Procedure
import com.kispoko.tome.model.game.engine.procedure.ProcedureId
import com.kispoko.tome.model.game.engine.program.ProgramParameter
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.sheet.*
import effect.Err
import effect.Val
import maybe.Just



/**
 * Procedure Dialog
 */
class ProcedureDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var procedureId   : ProcedureId? = null
    private var updateTarget  : UpdateTarget? = null
    private var sheetContext  : SheetContext? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(procedureId : ProcedureId,
                        updateTarget : UpdateTarget,
                        sheetContext : SheetContext) : ProcedureDialog
        {
            val dialog = ProcedureDialog()

            val args = Bundle()
            args.putSerializable("procedure_id", procedureId)
            args.putSerializable("update_target", updateTarget)
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

        this.procedureId  = arguments.getSerializable("procedure_id") as ProcedureId
        this.updateTarget = arguments.getSerializable("update_target") as UpdateTarget
        this.sheetContext = arguments.getSerializable("sheet_context") as SheetContext

        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val dialogLayout = this.dialogLayout()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val lp : WindowManager.LayoutParams = dialog.window.attributes
        lp.dimAmount = 0.7f

        //dialog.window.attributes.windowAnimations = R.style.DialogAnimation

        dialog.setContentView(dialogLayout)


        val width  = context.resources.getDimension(R.dimen.action_dialog_width)
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        dialog.window.setLayout(width.toInt(), height)

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val sheetContext = this.sheetContext
        val procedureId = this.procedureId

        if (sheetContext != null && procedureId != null)
        {
            val sheetUIContext  = SheetUIContext(sheetContext, context)

            val procedure = SheetManager.procedure(procedureId, sheetContext)

            return when (procedure) {
                is Val -> {
                    val viewBuilder = ProcedureViewBuilder(procedure.value,
                                                           updateTarget,
                                                           sheetUIContext,
                                                           this)
                    viewBuilder.view()
                }
                is Err -> {
                    ApplicationLog.error(procedure.error)
                    super.onCreateView(inflater, container, savedInstanceState)
                }
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

    fun dialogLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(context)
    }

}



class ProcedureViewBuilder(val procedure : Procedure,
                           val updateTarget : UpdateTarget?,
                           val sheetUIContext : SheetUIContext,
                           val dialog : ProcedureDialog)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val sheetContext = SheetContext(sheetUIContext)

    val parameters : MutableList<ProgramParameter> = mutableListOf()

    init {
        val program = procedure.program(sheetContext)
        when (program) {
            is Val -> {
                this.parameters.addAll(program.value.typeSignature().parameters())
            }
        }
    }


    var currentParameter : Int = 0

    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        layout.addView(this.descriptionView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_1"))))
        layout.backgroundColor  = Color.WHITE

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 12f
        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f

        layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // DESCRIPTION VIEW
    // -----------------------------------------------------------------------------------------

    private fun descriptionView() : LinearLayout
    {
        val layout      = this.descriptionViewLayout()

        layout.addView(this.descriptionTextView())

        layout.addView(this.parametersView())

        layout.addView(this.descriptionFooterView())

        return layout
    }


    private fun descriptionViewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation  = LinearLayout.VERTICAL

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun descriptionTextView() : TextView
    {
        val description           = TextViewBuilder()

        description.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        val descriptionTemplate = procedure.description()
        when (descriptionTemplate) {
            is Just -> {
                val sheetContext = SheetContext(sheetUIContext)
                description.text = descriptionTemplate.value.toString(sheetContext)
            }
        }

        description.font          = Font.typeface(TextFont.default(),
                                                  TextFontStyle.Regular,
                                                  sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        description.color           = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        description.sizeSp          = 18f

        return description.textView(sheetUIContext.context)
    }


    private fun descriptionFooterView() : LinearLayout
    {
        val layout = this.descriptionFooterViewLayout()

        layout.addView(this.doButtonView())

        return layout
    }


    private fun descriptionFooterViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.padding.topDp    = 20f

        layout.gravity          = Gravity.END

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun doButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val label           = TextViewBuilder()
        val icon            = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.HORIZONTAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.gravity          = Gravity.CENTER

        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f
        layout.padding.leftDp   = 12f
        layout.padding.rightDp  = 12f

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        layout.onClick          = View.OnClickListener {
            procedure.run(SheetContext(sheetUIContext))

            if (updateTarget != null)
            {
                when (updateTarget)
                {
                    is UpdateTargetActionWidget ->
                    {
                        SheetManager.updateSheet(sheetUIContext.sheetId,
                                                 ActionWidgetUpdate(updateTarget.actionWidgetId),
                                                 sheetUIContext.sheetUI())
                    }
                }
            }

            dialog.dismiss()
        }

        layout.child(label)
        //      .child(icon)

        // (3 A) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.margin.rightDp    = 5f

        val labelString = procedure.actionLabel()
        label.text = labelString.value

        label.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.SemiBold,
                                                sheetUIContext.context)

//        val labelColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
//        label.color           = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)
        label.color           = Color.WHITE

        label.sizeSp          = 17f

        // (3 B) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 22
        icon.heightDp       = 22

        icon.image          = R.drawable.icon_arrow_right

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
        //icon.color          = SheetManager.color(sheetUIContext.sheetId, colorTheme)
        icon.color          = Color.WHITE

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // PARAMETERS
    // -----------------------------------------------------------------------------------------

    private fun parametersView() : LinearLayout
    {
        val layout  = this.parametersViewLayout()

//        this.parameters.forEach {
//            layout.addView(this.parameterView(it))
//        }

        return layout
    }



    private fun parametersViewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation  = LinearLayout.VERTICAL

        return layout.linearLayout(sheetUIContext.context)
    }



    private fun parameterView() : LinearLayout
    {
        val layout  = this.parameterViewLayout()

        return layout
    }


    private fun parameterViewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation  = LinearLayout.VERTICAL

        return layout.linearLayout(sheetUIContext.context)
    }

}
