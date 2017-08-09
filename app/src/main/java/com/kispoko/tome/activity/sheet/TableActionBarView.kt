
package com.kispoko.tome.activity.sheet


import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.dialog.AddTableRowDialog
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.sheet.*



/**
 * Table Action Bar View
 */
class TableActionBarViewBuilder(val action : SheetAction.TableRow,
                                val sheetUIContext : SheetUIContext)
{

    fun view() : LinearLayout
    {
        val layout = this.viewLayout()

        // Header View
        layout.addView(this.headerView())

        // Divider
//        layout.addView(this.dividerView())

        // Buttons View
        layout.addView(this.buttonsView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation          = LinearLayout.VERTICAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.padding.bottomDp     = 10f

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // HEADER VIEW
    // -----------------------------------------------------------------------------------------

    private fun headerView() : RelativeLayout
    {
        val layout = this.headerViewLayout()

        // Label
        layout.addView(this.headerLabelView())

        // Exit Button
    //    layout.addView(this.headerExitButtonView())

        return layout
    }


    private fun headerViewLayout() : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f

        return layout.relativeLayout(sheetUIContext.context)
    }


    private fun headerLabelView() : TextView
    {
        val label               = TextViewBuilder()

        label.layoutType        = LayoutType.RELATIVE
        label.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
        label.height            = RelativeLayout.LayoutParams.WRAP_CONTENT

        label.textId            = R.string.table_editor

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color             = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        label.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        label.sizeSp             = 17f

        label.addRule(RelativeLayout.CENTER_VERTICAL)

        return label.textView(sheetUIContext.context)
    }


    private fun headerExitButtonView() : LinearLayout
    {

        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        // (2) Declarations
        // -------------------------------------------------------------------------------------

        layout.layoutType       = LayoutType.RELATIVE
        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.padding.topDp    = 14f
        layout.padding.bottomDp = 14f
        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 17
        icon.heightDp           = 17

        icon.image              = R.drawable.icon_delete

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_14")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color              = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        icon.onClick            = View.OnClickListener {
            val sheetActivity = sheetUIContext.context as SheetActivity
            sheetActivity.hideActionBar()
        }

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // DIVIDER VIEW
    // -----------------------------------------------------------------------------------------

    private fun dividerView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_1")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        layout.margin.bottomDp  = 10f

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // BUTTONS VIEW
    // -----------------------------------------------------------------------------------------

    private fun buttonsView() : LinearLayout
    {
        val layout = this.buttonsViewLayout()

        // Row Action Buttons
        layout.addView(this.rowActionButtonsView())

        // Table Action Buttons
        layout.addView(this.tableActionButtonsView())

        return layout
    }


    private fun buttonsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun rowActionButtonsView() : LinearLayout
    {
        val layout      = this.rowActionButtonsViewLayout()

        // Insert Row Above
        val insertRowAboveOnClick = View.OnClickListener {
            val sheetActivity = sheetUIContext.context as SheetActivity
            val updateTarget = UpdateTargetInsertTableRow(action.tableWidgetId,
                                                          action.rowClickedIndex + 1)
            val dialog = AddTableRowDialog.newInstance(updateTarget,
                                                       action.tableName,
                                                       action.tableColumns,
                                                       SheetContext(sheetUIContext))
            dialog.show(sheetActivity.supportFragmentManager, "")
            sheetActivity.hideActionBar()
        }
        layout.addView(this.actionButtonView(R.string.insert_row_above,
                                             R.drawable.icon_insert_row_above,
                                             24,
                                             insertRowAboveOnClick))

        // Insert Row Below
        val insertRowBelowOnClick = View.OnClickListener { }
        layout.addView(this.actionButtonView(R.string.insert_row_below,
                                             R.drawable.icon_insert_row_below,
                                             24,
                                             insertRowBelowOnClick))

        // Delete
        val deleteOnClick = View.OnClickListener { }
        layout.addView(this.actionButtonView(R.string.delete,
                                             R.drawable.icon_delete_row,
                                             24,
                                             deleteOnClick))

        return layout
    }


    private fun rowActionButtonsViewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = 0
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight       = 3f

        layout.orientation  = LinearLayout.HORIZONTAL

        layout.margin.rightDp   = 40f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun tableActionButtonsView() : LinearLayout
    {
        val layout = this.tableActionButtonsViewLayout()

        // Sort
        val sortOnClick = View.OnClickListener {  }
        layout.addView(this.actionButtonView(R.string.sort_table,
                                             R.drawable.icon_sorting_options,
                                             22,
                                             sortOnClick))

        return layout
    }


    private fun tableActionButtonsViewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = 0
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight       = 1f

        layout.orientation  = LinearLayout.HORIZONTAL

        return layout.linearLayout(sheetUIContext.context)
    }



    private fun actionButtonView(textId : Int,
                                 iconId : Int,
                                 iconSize : Int,
                                 onClick : View.OnClickListener) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val icon            = ImageViewBuilder()
        val label           = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight           = 1f

        layout.orientation      = LinearLayout.VERTICAL

        layout.gravity          = Gravity.CENTER

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_1")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)
//
//        layout.corners              = Corners(TopLeftCornerRadius(2f),
//                                              TopRightCornerRadius(2f),
//                                              BottomRightCornerRadius(2f),
//                                              BottomLeftCornerRadius(2f))

        layout.padding.topDp    = 5f
        layout.padding.bottomDp    = 5f

        layout.onClick          = onClick

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = iconSize
        icon.heightDp           = iconSize

        icon.image              = iconId

        icon.margin.bottomDp    = 3f

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color              = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = textId

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color             = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        label.sizeSp            = 11f

        //label.singleLine        = true

        return layout.linearLayout(sheetUIContext.context)
    }


}
