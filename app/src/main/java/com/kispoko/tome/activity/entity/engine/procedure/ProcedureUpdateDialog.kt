
package com.kispoko.tome.activity.entity.engine.procedure


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.SpannableStringBuilder
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.engine.procedure.Procedure
import com.kispoko.tome.model.engine.procedure.ProcedureInvocation
import com.kispoko.tome.model.engine.procedure.ProcedureUpdateResult
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.procedure



/**
 * Procedure Update Dialog
 */
class ProcedureUpdateDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var invocation : ProcedureInvocation? = null
    private var entityId   : EntityId?            = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(invocation : ProcedureInvocation,
                        entityId : EntityId) : ProcedureUpdateDialog
        {
            val dialog = ProcedureUpdateDialog()

            val args = Bundle()
            args.putSerializable("invocation", invocation)
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

        this.invocation = arguments.getSerializable("invocation") as ProcedureInvocation
        this.entityId   = arguments.getSerializable("entity_id") as EntityId

        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val dialogLayout = this.dialogLayout()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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
        val invocation = this.invocation
        val entityId   = this.entityId

        return if (invocation != null && entityId != null)
        {
            val procedureUpdateUI = ProcedureUpdateUI(invocation, this, entityId, context)
            procedureUpdateUI.view()
        }
        else
        {
            super.onCreateView(inflater, container, savedInstanceState)
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


class ProcedureUpdateUI(val invocation : ProcedureInvocation,
                        val dialog : DialogFragment,
                        val entityId : EntityId,
                        val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------



    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        procedure(invocation.procedureId, entityId) apDo {

            val results = it.results(invocation, entityId, context)

            // Header
            layout.addView(this.headerView(it.procedureName().value))

            // Description
            results.firstOrNull()?.let {
                layout.addView(this.messageView(it.programResult.message))
            }

            // Footer View
            layout.addView(this.footerView(it, results))
        }

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp             = LinearLayout.LayoutParams.MATCH_PARENT

        layout.backgroundColor      = Color.WHITE // SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.corners              = Corners(3.0, 3.0, 3.0, 3.0)

        return layout.linearLayout(context)
    }


    // Header
    // -----------------------------------------------------------------------------------------

    private fun headerView(procedureName : String) : LinearLayout
    {
        val layout = this.headerViewLayout()

        // Name
        layout.addView(this.nameView(procedureName))


        // Divider
//        layout.addView(this.dividerView())

        return layout
    }


    private fun headerViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.topDp        = 8f
        layout.padding.bottomDp     = 8f

        layout.padding.leftDp       = 10f
        layout.padding.rightDp      = 10f

        layout.backgroundColor      = Color.WHITE //  SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.corners              = Corners(3.0, 3.0, 0.0, 0.0)

        return layout.linearLayout(context)
    }


    private fun nameView(procedureName : String) : TextView
    {
        val name            = TextViewBuilder()

        name.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height         = LinearLayout.LayoutParams.WRAP_CONTENT

        name.text           = procedureName

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
        name.color          = colorOrBlack(colorTheme, entityId)

        name.font           = Font.typeface(TextFont.default(),
                                            TextFontStyle.SemiBold,
                                            context)

        name.sizeSp         = 19f

        return name.textView(context)
    }


    // Description
    // -----------------------------------------------------------------------------------------

    private fun messageView(resultSpannable : SpannableStringBuilder) : TextView
    {
        val message            = TextViewBuilder()

        message.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        message.height         = LinearLayout.LayoutParams.WRAP_CONTENT

        message.textSpan       = resultSpannable

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        message.color          = colorOrBlack(colorTheme, entityId)

        message.font           = Font.typeface(TextFont.default(),
                                               TextFontStyle.Regular,
                                               context)

        message.sizeSp                  = 18f

        message.padding.leftDp       = 10f
        message.padding.rightDp      = 10f
        message.padding.topDp        = 4f
        message.padding.bottomDp     = 8f

        return message.textView(context)
    }

    // Footer
    // -----------------------------------------------------------------------------------------

    private fun footerView(procedure : Procedure,
                           results : List<ProcedureUpdateResult>) : LinearLayout
    {
        val layout = footerViewLayout()

        val mainLayout = this.footerMainViewLayout()

        // Full Editor Button
        mainLayout.addView(this.redoButtonView())

        // Done Button
        mainLayout.addView(this.doneButtonView(procedure, results))

        //layout.addView(this.dividerView())

        layout.addView(mainLayout)


        return layout
    }


    private fun footerViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE

        layout.corners          = Corners(0.0, 0.0, 2.0, 2.0)

        return layout.linearLayout(context)
    }


    private fun footerMainViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.END or Gravity.CENTER_VERTICAL

        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f

        layout.padding.leftDp = 8f
        layout.padding.rightDp = 8f

        return layout.linearLayout(context)
    }


    private fun doneButtonView(procedure : Procedure,
                               results : List<ProcedureUpdateResult>) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()
        val icon        = ImageViewBuilder()
        val label       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green_80"))))
        layout.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        layout.corners              = Corners(4.0, 4.0, 4.0, 4.0)

        layout.padding.topDp    = 6f
        layout.padding.bottomDp = 6f
        layout.padding.leftDp = 16f
        layout.padding.rightDp = 16f


        layout.onClick          = View.OnClickListener {
            procedure.run(results, entityId)
            dialog.dismiss()
        }

        layout// .child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 30
        icon.heightDp       = 30

        icon.image          = R.drawable.icon_check

        icon.color          = Color.WHITE //SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text          = context.getString(R.string.ok).toUpperCase()

        label.color         = Color.WHITE

        label.font          = Font.typeface(TextFont.default(),
                                            TextFontStyle.Bold,
                                            context)

        label.sizeSp        = 17f

        return layout.linearLayout(context)
    }


    private fun redoButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()
        val icon        = ImageViewBuilder()
        val label       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity          = Gravity.CENTER

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

         layout.corners              = Corners(4.0, 4.0, 4.0, 4.0)

        layout.margin.rightDp    = 10f

        layout.padding.topDp    = 6f
        layout.padding.bottomDp = 6f
        layout.padding.leftDp = 10f
        layout.padding.rightDp = 10f

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 18
        icon.heightDp       = 18

        icon.image          = R.drawable.icon_dice_roll_filled

        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_15"))))
        icon.color          = colorOrBlack(iconColorTheme, entityId)

        icon.margin.rightDp = 5f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId        = R.string.reroll

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color         = colorOrBlack(labelColorTheme, entityId)

        label.font          = Font.typeface(TextFont.default(),
                                            TextFontStyle.Bold,
                                            context)

        label.sizeSp        = 17f

        return layout.linearLayout(context)
    }


    private fun dividerView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_4"))))
        layout.backgroundColor  = colorOrBlack(colorTheme, entityId)

        return layout.linearLayout(context)
    }

}

