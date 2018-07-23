
package com.taletable.android.activity.sheet


import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.taletable.android.R
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialThemeLight



// app options
// session
// > load
// > new
// updates
// upgrades
// donate


/**
 * Sheet Options UI (Sidebar)
 */
class SheetOptionsUI(val sheetActivity : SheetActivity)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val theme : Theme = officialThemeLight

    val context = sheetActivity as Context


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : LinearLayout
    {
        val layout = this.viewLayout()

        // Edit
        val editLayout = buttonGroupLayout()
        layout.addView(editLayout)

        editLayout.addView(this.headerView(R.string.edit, null))

        // ------------------------
        layout.addView(this.dividerView())

        // Layout Editor
        editLayout.addView(this.editModeView())

        // Edit State
        editLayout.addView(this.buttonView(R.drawable.icon_apps,
                                           R.string.edit_state))

        // Edit Rules
        editLayout.addView(this.buttonView(R.drawable.icon_bulleted_list,
                                           R.string.edit_rules))

        // Settings
        val settingsLayout = buttonGroupLayout()
        layout.addView(settingsLayout)

        settingsLayout.addView(this.headerView(R.string.settings, null))

        // Theme Button
        settingsLayout.addView(this.buttonView(R.drawable.icon_change_theme,
                                               R.string.manage_themes))

        // ------------------------
        layout.addView(this.dividerView())


        // Save
        val saveLayout = buttonGroupLayout()
        layout.addView(saveLayout)

        saveLayout.addView(this.headerView(R.string.save, null))

        // Save Copy
        val onSave = View.OnClickListener {
            val intent = Intent(sheetActivity, SaveSheetActivity::class.java)

            val sheetId = sheetActivity.sheetId
            if (sheetId != null)
                intent.putExtra("sheet_id", sheetId)
            sheetActivity.startActivity(intent)
        }
        saveLayout.addView(this.buttonView(R.drawable.icon_save,
                                           R.string.save_sheet_copy,
                                           onSave))

        // ------------------------
        layout.addView(this.dividerView())

        // Share
        val shareLayout = buttonGroupLayout()
        layout.addView(shareLayout)

        shareLayout.addView(this.headerView(R.string.share, null))

        // Export to File
        shareLayout.addView(this.buttonView(R.drawable.icon_download,
                                           R.string.export_to_file))


        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.topDp    = 25f

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // HEADER
    // -----------------------------------------------------------------------------------------

    private fun headerView(labelId : Int, annotationString : String?) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val header                  = TextViewBuilder()
        val annotation              = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.margin.leftDp        = 14f

        layout.padding.topDp        = 10f
        layout.padding.bottomDp     = 10f

        layout.child(header)

        if (annotationString != null)
            layout.child(annotation)

        // (3 A) Header
        // -------------------------------------------------------------------------------------

        header.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text                 = context.getString(labelId).toUpperCase()

        val headerColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_20"))))
        header.color                = theme.colorOrBlack(headerColorTheme)

        header.font                 = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Bold,
                                                    context)

        header.sizeSp               = 14f

        // (3 B) Annotation
        // -------------------------------------------------------------------------------------

        annotation.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        annotation.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        annotation.text             = annotationString

        val annotationColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("red_90"))))
        annotation.color            = theme.colorOrBlack(annotationColorTheme)

        annotation.font             = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        annotation.sizeSp           = 14f

        annotation.margin.leftDp    = 8f

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // GENERAL
    // -----------------------------------------------------------------------------------------

    private fun dividerView(): LinearLayout
    {
        val divider                 = LinearLayoutBuilder()

        divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp            = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_5"))))
        divider.backgroundColor     = theme.colorOrBlack(colorTheme)

        return divider.linearLayout(context)
    }


    private fun buttonView(iconId : Int,
                           labelId : Int,
                           onClick : View.OnClickListener? = null) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val icon            = ImageViewBuilder()
        val label           = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.topDp    = 12f
        layout.padding.bottomDp = 12f

        layout.margin.leftDp    = 16f
        layout.margin.rightDp   = 10f

        layout.onClick          = onClick

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 19
        icon.heightDp       = 19

        icon.image          = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_14"))))
        icon.color          = theme.colorOrBlack(iconColorTheme)

        icon.margin.rightDp = 16f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = labelId

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_12"))))
        label.color             = theme.colorOrBlack(colorTheme)

        label.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        label.padding.bottomDp  = 1f

        label.sizeSp            = 17f


        return layout.linearLayout(context)
    }


    private fun buttonGroupLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.topDp    = 12f
        layout.padding.bottomDp = 12f

        return layout.linearLayout(context)
    }


    private fun editModeView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val label                   = TextViewBuilder()
        val switch                  = SwitchBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.margin.leftDp        = 16f
        layout.margin.rightDp       = 10f

        layout.padding.topDp        = 5f
        layout.padding.bottomDp     = 5f

        layout.child(label)
              .child(switch)

        // (3 A) Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT
        label.weight                = 1f

        label.textId                = R.string.layout_editor

        label.font                  = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Medium,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_12"))))
        label.color                 = theme.colorOrBlack(colorTheme)

        label.sizeSp                = 17f

        // (3 B) Switcher
        // -------------------------------------------------------------------------------------

        switch.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        switch.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        switch.checked              = false

        switch.scaleX               = 0.9f
        switch.scaleY               = 0.9f

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // SETTINGS VIEW
    // -----------------------------------------------------------------------------------------

    private fun settingsView() : LinearLayout
    {
        val layout = this.settingsViewLayout()

        return layout
    }


    private fun settingsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // ADVANCED VIEW
    // -----------------------------------------------------------------------------------------

    private fun advancedView() : LinearLayout
    {
        val layout = this.advancedViewLayout()

        // State Button
        val stateButton = this.buttonView(R.drawable.icon_console,
                                          R.string.view_state)

        stateButton.setOnClickListener {
//            val intent = Intent(sheetActivity, SheetStateActivity::class.java)
//            intent.putExtra("sheet_id", sheetUIContext.sheetId)
//            sheetActivity.startActivity(intent)
        }
        layout.addView(stateButton)

        return layout
    }


    private fun advancedViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

        return layout.linearLayout(context)
    }


}


