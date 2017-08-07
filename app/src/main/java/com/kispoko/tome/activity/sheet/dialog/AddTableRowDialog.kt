
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.sheet.widget.table.TableWidgetColumn
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.UpdateTargetInsertTableRow
import java.io.Serializable



/**
 * Add Table Row Dialog
 */
class AddTableRowDialog : DialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var updateTarget : UpdateTargetInsertTableRow? = null
    private var tableName : String? = null
    private var tableColumns : List<TableWidgetColumn> = listOf()
    private var sheetContext : SheetContext? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(updateTarget : UpdateTargetInsertTableRow,
                        tableName : String,
                        tableColumns : List<TableWidgetColumn>,
                        sheetContext : SheetContext) : AddTableRowDialog
        {
            val dialog = AddTableRowDialog()

            val args = Bundle()
            args.putSerializable("update_target", updateTarget)
            args.putString("table_name", tableName)
            args.putSerializable("table_columns", tableColumns as Serializable)
            args.putSerializable("sheet_context", sheetContext)
            dialog.arguments = args

            return dialog
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG FRAGMENT
    // -----------------------------------------------------------------------------------------

    @Suppress("UNCHECKED_CAST")
    override fun onCreateDialog(savedInstanceState : Bundle?) : Dialog
    {
        // (1) Read State
        // -------------------------------------------------------------------------------------

        this.updateTarget = arguments.getSerializable("update_target") as UpdateTargetInsertTableRow
        this.tableName    = arguments.getString("table_name")
        this.tableColumns = arguments.getSerializable("table_columns") as List<TableWidgetColumn>
        this.sheetContext = arguments.getSerializable("sheet_context") as SheetContext


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val sheetContext = this.sheetContext
        if (sheetContext != null)
        {
            val sheetUIContext = SheetUIContext(sheetContext, context)

            val dialogLayout = this.dialogLayout(sheetUIContext)

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

            val updateTarget = this.updateTarget
            val tableName    = this.tableName

            if (updateTarget != null && tableName != null)
            {
                val adderEditorView = AddTableRowViewBuilder(updateTarget,
                                                             tableName,
                                                             tableColumns,
                                                             sheetUIContext,
                                                             this)
                return adderEditorView.view()
            }
            else
            {
                return super.onCreateView(inflater, container, savedInstanceState)
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

    fun dialogLayout(sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        return layout.linearLayout(context)
    }

}



// ---------------------------------------------------------------------------------------------
// ADD TABLE ROW VIEW BUILDER
// ---------------------------------------------------------------------------------------------

class AddTableRowViewBuilder(val updateTarget : UpdateTargetInsertTableRow,
                             val tableName : String,
                             val tableColumns : List<TableWidgetColumn>,
                             val sheetUIContext : SheetUIContext,
                             val dialog : DialogFragment)
{


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Header
        layout.addView(this.headerView())

        // Cells
        layout.addView(this.cellsView())

        // Footer
        layout.addView(this.footerView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners          = Corners(TopLeftCornerRadius(3f),
                                          TopRightCornerRadius(3f),
                                          BottomRightCornerRadius(3f),
                                          BottomLeftCornerRadius(3f))

        return layout.linearLayout(sheetUIContext.context)
    }


    // Header
    // -----------------------------------------------------------------------------------------

    private fun headerView() : LinearLayout
    {
        val layout = this.headerViewLayout()

        // Title
        layout.addView(this.titleView())

        // Table Name
        layout.addView(this.tableNameView())

        return layout
    }


    private fun headerViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.margin.topDp     = 2f
        layout.margin.bottomDp  = 2f
        layout.margin.leftDp    = 2f
        layout.margin.rightDp   = 2f

        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f
        layout.padding.leftDp   = 6f

        layout.corners           = Corners(TopLeftCornerRadius(3f),
                                           TopRightCornerRadius(3f),
                                           BottomRightCornerRadius(0f),
                                           BottomLeftCornerRadius(0f))

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun titleView() : TextView
    {
        val title               = TextViewBuilder()

        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        title.textId            = R.string.add_row

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        title.color             = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        title.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        title.sizeSp            = 15f

        return title.textView(this.sheetUIContext.context)
    }


    private fun tableNameView() : TextView
    {
        val title               = TextViewBuilder()

        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        title.text              = sheetUIContext.context.getString(R.string.to) + " " + this.tableName + " Table"

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        title.color             = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        title.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Italic,
                                                sheetUIContext.context)

        title.sizeSp            = 12f

        return title.textView(this.sheetUIContext.context)
    }


    // Cells
    // -----------------------------------------------------------------------------------------

    private fun cellsView() : LinearLayout
    {
        val layout = this.cellsViewLayout()

        Log.d("***ADDTABLEROWDIALOG", "columns: " + this.tableColumns.size.toString())
        this.tableColumns.forEach { column ->
            layout.addView(this.cellView(column.nameString(), column.defaultValueString()))
        }

        return layout
    }


    private fun cellsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.leftDp    = 2f
        layout.margin.rightDp   = 2f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun cellView(nameString : String, valueString : String) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()
        val name        = TextViewBuilder()
        val value       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.bottomDp  = 2f

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor   = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners           = Corners(TopLeftCornerRadius(1f),
                                           TopRightCornerRadius(1f),
                                           BottomRightCornerRadius(1f),
                                           BottomLeftCornerRadius(1f))

        layout.padding.leftDp   = 6f
        layout.padding.rightDp  = 6f
        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

        layout.child(name)
              .child(value)

        // (3 A) Name
        // -------------------------------------------------------------------------------------

        name.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height         = LinearLayout.LayoutParams.WRAP_CONTENT

        name.text           = nameString

        val nameColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        name.color          = SheetManager.color(sheetUIContext.sheetId, nameColorTheme)

        name.font           = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            sheetUIContext.context)

        name.sizeSp         = 12f

        name.margin.bottomDp    = 6f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        value.text          = valueString

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        value.color         = SheetManager.color(sheetUIContext.sheetId, valueColorTheme)

        value.font          = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Light,
                                            sheetUIContext.context)

        value.sizeSp        = 18f

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // FOOTER VIEW
    // -----------------------------------------------------------------------------------------

    private fun footerView() : LinearLayout
    {
        val layout = this.footerViewLayout()

        // Keep Editing Button
        layout.addView(this.addMoreButtonView())

        // Done Button
        layout.addView(this.doneButtonView())

        return layout
    }


    private fun footerViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp             = 44

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.margin.rightDp       = 2f
        layout.margin.leftDp        = 2f
        layout.margin.bottomDp      = 2f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun addMoreButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()
        val icon        = ImageViewBuilder()
        val label       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight           = 1f

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor   = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners           = Corners(TopLeftCornerRadius(0f),
                                           TopRightCornerRadius(0f),
                                           BottomRightCornerRadius(0f),
                                           BottomLeftCornerRadius(2f))

        layout.margin.rightDp   = 1f

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 18
        icon.heightDp       = 18

        icon.image          = R.drawable.icon_replay

        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color          = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        icon.margin.rightDp = 4f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = R.string.add_more_rows

        val labelColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color             = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        label.sizeSp            = 14f

        return layout.linearLayout(sheetUIContext.context)
    }



    private fun doneButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()
        val icon        = ImageViewBuilder()
        val label       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
        layout.weight           = 1f

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_green_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor   = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners           = Corners(TopLeftCornerRadius(0f),
                                           TopRightCornerRadius(0f),
                                           BottomRightCornerRadius(2f),
                                           BottomLeftCornerRadius(0f))

        layout.margin.leftDp   = 1f

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 20
        icon.heightDp       = 20

        icon.image          = R.drawable.icon_plus_sign

        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_14")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color          = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        icon.margin.rightDp = 4f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId        = R.string.add_row

        val labelColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_14")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color         = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font          = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            sheetUIContext.context)

        label.sizeSp        = 15f

        return layout.linearLayout(sheetUIContext.context)
    }


}
