
package com.kispoko.tome.activity.sheet


import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext



/**
 * Table Action Bar View
 */
object TableActionBarView
{

    fun view(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.viewLayout(sheetUIContext)

        // Header View
        layout.addView(this.headerView(sheetUIContext))

        // Buttons View
        layout.addView(this.buttonsView(sheetUIContext))

        return layout
    }


    private fun viewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation          = LinearLayout.VERTICAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_4")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.padding.bottomDp     = 10f

        return layout.linearLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // HEADER VIEW
    // -----------------------------------------------------------------------------------------

    private fun headerView(sheetUIContext : SheetUIContext) : RelativeLayout
    {
        val layout = this.headerViewLayout(sheetUIContext)

        // Label
        layout.addView(this.headerLabelView(sheetUIContext))

        // Exit Button
        layout.addView(this.headerExitButtonView(sheetUIContext))

        return layout
    }


    private fun headerViewLayout(sheetUIContext : SheetUIContext) : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f

        return layout.relativeLayout(sheetUIContext.context)
    }


    private fun headerLabelView(sheetUIContext : SheetUIContext) : TextView
    {
        val label               = TextViewBuilder()

        label.layoutType        = LayoutType.RELATIVE
        label.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
        label.height            = RelativeLayout.LayoutParams.WRAP_CONTENT

        label.textId            = R.string.table_editor

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("gold_1")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color             = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        label.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Regular,
                                                sheetUIContext.context)

        label.sizeSp             = 16f

        label.addRule(RelativeLayout.CENTER_VERTICAL)

        return label.textView(sheetUIContext.context)
    }


    private fun headerExitButtonView(sheetUIContext : SheetUIContext) : LinearLayout
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

        layout.padding.topDp    = 20f
        layout.padding.bottomDp = 20f
        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 18
        icon.heightDp           = 18

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
    // BUTTONS VIEW
    // -----------------------------------------------------------------------------------------

    private fun buttonsView(sheetUIContext : SheetUIContext) : RelativeLayout
    {
        val layout = this.buttonsViewLayout(sheetUIContext)

        // Row Action Buttons
        layout.addView(this.rowActionButtonsView(sheetUIContext))

        // Table Action Buttons
        layout.addView(this.tableActionButtonsView(sheetUIContext))

        return layout
    }


    private fun buttonsViewLayout(sheetUIContext : SheetUIContext) : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f

        return layout.relativeLayout(sheetUIContext.context)
    }


    private fun rowActionButtonsView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout      = this.rowActionButtonsViewLayout(sheetUIContext)

        // Insert Row Above
        layout.addView(this.actionButtonView(R.string.insert_row_above,
                                             R.drawable.icon_insert_row_above,
                                             24,
                                             sheetUIContext))

        // Insert Row Below
        layout.addView(this.actionButtonView(R.string.insert_row_below,
                                             R.drawable.icon_insert_row_below,
                                             24,
                                             sheetUIContext))

        // Delete
        layout.addView(this.actionButtonView(R.string.delete,
                                             R.drawable.icon_delete_row,
                                             24,
                                             sheetUIContext))

        return layout
    }


    private fun rowActionButtonsViewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.layoutType   = LayoutType.RELATIVE
        layout.width        = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height       = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.HORIZONTAL

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun tableActionButtonsView(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = this.tableActionButtonsViewLayout(sheetUIContext)

        // Sort
        layout.addView(this.actionButtonView(R.string.sort_table,
                                             R.drawable.icon_sorting_options,
                                             22,
                                             sheetUIContext))

        return layout
    }


    private fun tableActionButtonsViewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.layoutType   = LayoutType.RELATIVE
        layout.width        = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height       = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.HORIZONTAL

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)

        return layout.linearLayout(sheetUIContext.context)
    }



    private fun actionButtonView(textId : Int,
                                 iconId : Int,
                                 iconSize : Int,
                                 sheetUIContext : SheetUIContext) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val icon            = ImageViewBuilder()
        val label           = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.gravity          = Gravity.CENTER

        layout.margin.rightDp   = 10f

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = iconSize
        icon.heightDp           = iconSize

        icon.image              = iconId

        icon.margin.bottomDp    = 3f

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color              = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = textId

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_29")),
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
