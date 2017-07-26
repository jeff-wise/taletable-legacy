
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.procedure.Procedure
import com.kispoko.tome.model.game.engine.program.Program
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext
import effect.Err
import effect.Val
import effect.mapM



/**
 * Procedure Dialog Fragment
 */
class ProcedureDialogFragment : DialogFragment()
{


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var procedure    : Procedure? = null
    private var sheetContext : SheetContext? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(procedure : Procedure,
                        sheetContext : SheetContext) : ProcedureDialogFragment
        {
            val dialog = ProcedureDialogFragment()

            val args = Bundle()
            args.putSerializable("procedure", procedure)
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

        this.procedure    = arguments.getSerializable("procedure") as Procedure
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

            val procedure = this.procedure

            if (procedure != null)
                return ProcedureView.view(procedure, sheetUIContext)
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



object ProcedureView
{

    fun view(procedure : Procedure, sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.viewLayout(sheetUIContext)

        // Header View
        layout.addView(this.headerView(procedure.procedureName(), sheetUIContext))

        // Programs View
        val programs = procedure.programIds().mapM { programId ->
                            GameManager.engine(sheetUIContext.gameId)
                                    .apply { it.program(programId) }
                       }
        when (programs) {
            is Val -> layout.addView(this.programsView(programs.value, sheetUIContext))
            is Err -> ApplicationLog.error(programs.error)
        }

        // Buttons View
        layout.addView(this.buttonsView(sheetUIContext))

        return layout
    }


    private fun viewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // HEADER VIEW
    // -----------------------------------------------------------------------------------------

    private fun headerView(procedureName : String,
                           sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.headerViewLayout(sheetUIContext.context)

        layout.addView(this.procedureNameView(procedureName, sheetUIContext))

        return layout
    }


    private fun headerViewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        return layout.linearLayout(context)
    }


    private fun procedureNameView(procedureName : String,
                                  sheetUIContext : SheetUIContext) : TextView
    {
        val name            = TextViewBuilder()

        name.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height         = LinearLayout.LayoutParams.WRAP_CONTENT

        name.text           = procedureName

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        name.color              = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        name.font               = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        name.sizeSp             = 15f

        return name.textView(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // PROGRAMS VIEW
    // -----------------------------------------------------------------------------------------

    private fun programsView(programs : Set<Program>,
                             sheetUIContext : SheetUIContext) : ScrollView
    {
        val scrollView = this.programsScrollView(sheetUIContext.context)

        scrollView.addView(this.programsListView(programs, sheetUIContext))

        return scrollView
    }


    private fun programsScrollView(context : Context) : ScrollView
    {
        val scrollView = ScrollViewBuilder()

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        return scrollView.scrollView(context)
    }


    private fun programsListView(programs : Set<Program>,
                                 sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.programsListViewLayout(sheetUIContext.context)

        programs.forEach {
            layout.addView(this.programView(it, sheetUIContext))
        }

        return layout
    }


    private fun programsListViewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun programView(program : Program,
                            sheetUIContext : SheetUIContext) : TextView
    {
        val description             = TextViewBuilder()

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        description.text            = program.description()

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        description.color           = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        description.font            = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        description.sizeSp          = 15f

        return description.textView(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // BUTTONS VIEW
    // -----------------------------------------------------------------------------------------

    private fun buttonsView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout          = this.buttonsViewLayout(sheetUIContext.context)

        layout.addView(this.cancelButtonView(sheetUIContext))

        layout.addView(this.okButtonView(sheetUIContext))

        return layout
    }


    private fun buttonsViewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 60

        layout.orientation      = LinearLayout.HORIZONTAL

        return layout.linearLayout(context)
    }


    private fun cancelButtonView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()
        val icon        = ImageViewBuilder()
        val label       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = 0
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight               = 1f

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp                = 20
        icon.heightDp               = 20

        icon.image                  = R.drawable.icon_delete

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color                  = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = R.string.cancel

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color             = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        label.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        label.sizeSp            = 15f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun okButtonView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()
        val icon        = ImageViewBuilder()
        val label       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = 0
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight               = 1f

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp                = 20
        icon.heightDp               = 20

        icon.image                  = R.drawable.icon_check

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color                  = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = R.string.ok

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color             = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        label.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        label.sizeSp            = 15f

        return layout.linearLayout(sheetUIContext.context)
    }

}



