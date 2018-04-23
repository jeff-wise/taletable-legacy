
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.LinearLayout
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.router.Router
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.sheet.*
import com.kispoko.tome.util.Util



/**
 * Table Dialog
 */
class TableDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var updateTarget : UpdateTarget? = null
    private var entityId     : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(updateTarget : UpdateTarget,
                        entityId : EntityId) : TableDialog
        {
            val dialog = TableDialog()

            val args = Bundle()
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

        this.updateTarget = arguments.getSerializable("update_target") as UpdateTarget
        this.entityId     = arguments.getSerializable("entity_id") as EntityId


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(context)

        val dialogLayout = this.dialogLayout()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setContentView(dialogLayout)

        val widthDp  = 300f
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        dialog.window.setLayout(Util.dpToPixel(widthDp), height)

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {


        val updateTarget = this.updateTarget
        val entityId = this.entityId
        if (updateTarget != null && entityId != null)
        {
            val viewBuilder = TableDialogViewBuilder(updateTarget, this, entityId, context)
            return viewBuilder.view()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG LAYOUT
    // -----------------------------------------------------------------------------------------

    fun dialogLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(context)
    }

}



class TableDialogViewBuilder(val updateTarget : UpdateTarget,
                             val dialog : DialogFragment,
                             val entityId : EntityId,
                             val context : Context)
{


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout          = this.viewLayout()

        layout.addView(this.headerView())

        layout.addView(this.actionsView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        layout.backgroundColor  = colorOrBlack(colorTheme, entityId)

        layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)

        return layout.linearLayout(context)
    }


    private fun headerView() : LinearLayout
    {
        val layout      = this.headerViewLayout()

        layout.addView(this.headerMainView())

        layout.addView(this.dividerView())

        return layout
    }


    private fun headerViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.corners          = Corners(3.0, 3.0, 0.0, 0.0)

        return layout.linearLayout(context)
    }


    private fun headerMainView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val title               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.backgroundColor  = Color.WHITE

        layout.padding.leftDp   = 15f
        layout.padding.rightDp  = 15f
        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

        layout.child(title)

        // (3) Layout
        // -------------------------------------------------------------------------------------

        title.width             = LinearLayout.LayoutParams.MATCH_PARENT
        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        title.textId            = R.string.table_actions

        title.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Bold,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_15"))))
        title.color             = colorOrBlack(colorTheme, entityId)

        title.sizeSp            = 18f

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



    private fun actionsView() : LinearLayout
    {
        val layout          = this.actionsViewLayout()

        // Insert Row Above
        val insertRowAboveOnClick = View.OnClickListener {
            when (updateTarget)
            {
                is UpdateTargetInsertTableRow ->
                {
                    val selectedRow = updateTarget.tableWidget.selectedRow
                    if (selectedRow != null)
                    {
                        val tableUpdate = TableWidgetUpdateInsertRowBefore(
                                                            updateTarget.tableWidget.widgetId(),
                                                            selectedRow)
                        Router.send(MessageSheetUpdate(tableUpdate))
                        dialog.dismiss()
                    }
                }
            }
        }
        layout.addView(this.actionButtonView(R.string.insert_row_above,
                                             R.drawable.icon_insert_row_above,
                                             26,
                                             insertRowAboveOnClick))

        // Insert Row Below
        val insertRowBelowOnClick = View.OnClickListener {
            when (updateTarget)
            {
                is UpdateTargetInsertTableRow ->
                {
                    val selectedRow = updateTarget.tableWidget.selectedRow
                    if (selectedRow != null)
                    {
                        val tableUpdate = TableWidgetUpdateInsertRowAfter(
                                                            updateTarget.tableWidget.widgetId(),
                                                            selectedRow)
                        Router.send(MessageSheetUpdate(tableUpdate))
                        dialog.dismiss()
                    }
                }
            }
        }
        layout.addView(this.actionButtonView(R.string.insert_row_below,
                                             R.drawable.icon_insert_row_below,
                                             26,
                                             insertRowBelowOnClick))

        // Delete
        val moreOnClick = View.OnClickListener { }
        layout.addView(this.actionButtonView(R.string.delete_row,
                                             R.drawable.icon_delete_row,
                                             26,
                                             moreOnClick))

        // Sort
        val sortOnClick = View.OnClickListener { }
        layout.addView(this.actionButtonView(R.string.sort_table,
                                             R.drawable.icon_sorting_options,
                                             26,
                                             sortOnClick))

        return layout
    }


    private fun actionsViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.padding.topDp        = 6f
        layout.padding.bottomDp     = 6f

        layout.backgroundColor      = Color.WHITE

        return layout.linearLayout(context)
    }


    private fun actionButtonView(labelId : Int,
                                 iconId : Int,
                                 iconSize : Int,
                                 onClick : View.OnClickListener) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()

        val icon                = ImageViewBuilder()
        val label               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.onClick          = onClick

        layout.backgroundColor  = Color.WHITE

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.leftDp   = 15f
        layout.padding.rightDp  = 15f
        layout.padding.bottomDp = 10f
        layout.padding.topDp    = 10f

//        layout.margin.leftDp    = 4f
//        layout.margin.rightDp   = 4f
//        layout.margin.topDp     = 4f

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = iconSize
        icon.heightDp           = iconSize

        icon.image              = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color             = colorOrBlack(iconColorTheme, entityId)

        icon.margin.rightDp     = 18f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = labelId

        label.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        label.color             = colorOrBlack(labelColorTheme, entityId)

        label.sizeSp            = 19f

        return layout.linearLayout(context)
    }
}
