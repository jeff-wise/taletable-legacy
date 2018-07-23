
package com.taletable.android.activity.entity.engine.procedure


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.app.ApplicationLog
import com.taletable.android.lib.ui.*
import com.taletable.android.model.engine.procedure.Procedure
import com.taletable.android.model.engine.procedure.ProcedureId
import com.taletable.android.model.engine.procedure.ProcedureInvocation
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.router.Router
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.procedure
import com.taletable.android.rts.entity.sheet.*
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
    private var entityId      : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(procedureId : ProcedureId,
                        updateTarget : UpdateTarget,
                        entityId : EntityId) : ProcedureDialog
        {
            val dialog = ProcedureDialog()

            val args = Bundle()
            args.putSerializable("procedure_id", procedureId)
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

        this.procedureId  = arguments?.getSerializable("procedure_id") as ProcedureId
        this.updateTarget = arguments?.getSerializable("update_target") as UpdateTarget
        this.entityId     = arguments?.getSerializable("entity_id") as EntityId

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


        val width  = context?.resources?.getDimension(R.dimen.action_dialog_width)
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        width?.let {
            dialog.window.setLayout(width.toInt(), height)
        }

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val entityId = this.entityId
        val procedureId = this.procedureId
        val context = this.context

        if (entityId != null && procedureId != null && context != null)
        {
            val procedure = procedure(procedureId, entityId)

            return when (procedure) {
                is Val -> {
                    val viewBuilder = ProcedureViewBuilder(procedure.value,
                            updateTarget,
                            this,
                            entityId,
                            context)
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
                           val dialog : ProcedureDialog,
                           val entityId : EntityId,
                           val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

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

        return layout.linearLayout(context)
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

        return layout.linearLayout(context)
    }


    private fun descriptionTextView() : TextView
    {
        val description           = TextViewBuilder()

        description.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        val descriptionTemplate = procedure.description()
        when (descriptionTemplate) {
            is Just -> {
                description.text = descriptionTemplate.value.stringFromVariables(entityId)
            }
        }

        description.font          = Font.typeface(TextFont.default(),
                                                  TextFontStyle.Regular,
                                                  context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        description.color           = colorOrBlack(colorTheme, entityId)

        description.sizeSp          = 18f

        return description.textView(context)
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

        return layout.linearLayout(context)
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

        layout.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.HORIZONTAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
        layout.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        layout.gravity          = Gravity.CENTER

        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f
        layout.padding.leftDp   = 12f
        layout.padding.rightDp  = 12f

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        layout.onClick          = View.OnClickListener {
            val invocation = ProcedureInvocation(procedure.procedureId(), mapOf())
            Router.send(MessageSheetActionRunProcedure(invocation))
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
                                                context)

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

        return layout.linearLayout(context)
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

        return layout.linearLayout(context)
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

        return layout.linearLayout(context)
    }

}
