
package com.kispoko.tome.activity.sheet


import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.sheet.*



/**
 * Table Action Bar View
 */
class TableActionBarViewBuilder(val updateTarget : UpdateTarget,
                                val sheetUIContext : SheetUIContext)
{

    fun view() : LinearLayout
    {
        val layout = this.viewLayout()

        // Header View
        // layout.addView(this.headerView())

        // Buttons View
        layout.addView(this.buttonsView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp             = 70

        layout.orientation          = LinearLayout.VERTICAL

        layout.gravity              = Gravity.CENTER_VERTICAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_red_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun buttonsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.margin.leftDp    = 4f
        layout.margin.rightDp   = 4f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun buttonsView() : LinearLayout
    {
        val layout      = this.buttonsViewLayout()

        // Colors
        val whiteColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_1")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        val whiteColor = SheetManager.color(sheetUIContext.sheetId, whiteColorTheme)

        // Done
        layout.addView(this.doneButtonView())

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
                                                            updateTarget.tableWidget.id,
                                                            selectedRow)
                        SheetManager.updateSheet(this.sheetUIContext.sheetId,
                                                 tableUpdate,
                                                 this.sheetUIContext.sheetUI())
                    }
                }
            }
        }
        layout.addView(this.actionButtonView(R.string.insert_row_above,
                                             R.drawable.icon_insert_row_above,
                                             24,
                                             whiteColor,
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
                                                            updateTarget.tableWidget.id,
                                                            selectedRow)
                        SheetManager.updateSheet(this.sheetUIContext.sheetId,
                                                 tableUpdate,
                                                 this.sheetUIContext.sheetUI())
                    }
                }
            }
        }
        layout.addView(this.actionButtonView(R.string.insert_row_below,
                                             R.drawable.icon_insert_row_below,
                                             24,
                                             whiteColor,
                                             insertRowBelowOnClick))

        // Delete
        val moreOnClick = View.OnClickListener { }
        layout.addView(this.actionButtonView(R.string.delete_row,
                                             R.drawable.icon_delete_row,
                                             24,
                                             whiteColor,
                                             moreOnClick))

        return layout
    }


    private fun doneButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val iconLayout      = LinearLayoutBuilder()
        val icon            = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight           = 0.8f

        layout.gravity          = Gravity.CENTER

        layout.child(iconLayout)

        // (3) Icon Layout
        // -------------------------------------------------------------------------------------

        iconLayout.widthDp          = 50
        iconLayout.heightDp         = 50

        iconLayout.orientation      = LinearLayout.VERTICAL

        iconLayout.gravity          = Gravity.CENTER

        iconLayout.onClick          = View.OnClickListener {
            val sheetActivity = sheetUIContext.context as SheetActivity
            sheetActivity.hideActionBar()
        }

        iconLayout.corners          = Corners(3.0, 3.0, 3.0, 3.0)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_red_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        iconLayout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        iconLayout.child(icon)

        // (4) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 25
        icon.heightDp           = 25

        icon.image              = R.drawable.icon_delete

        icon.layoutGravity      = Gravity.CENTER

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color              = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun actionButtonView(textId : Int,
                                 iconId : Int,
                                 iconSize : Int,
                                 iconColor : Int,
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

        layout.onClick          = onClick

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = iconSize
        icon.heightDp           = iconSize

        icon.image              = iconId

        icon.margin.bottomDp    = 3f

        icon.color              = iconColor

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = textId

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color             = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        label.sizeSp            = 11f

        return layout.linearLayout(sheetUIContext.context)
    }


}
