
package com.kispoko.tome.activity.sheet


import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.theme.ThemeManager



/**
 * Sheet Options View (Sidebar)
 */
class SheetOptionsViewBuilder(val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val themeId : ThemeId = ThemeId.Light


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------


    fun view() : LinearLayout
    {
        val layout = this.viewLayout()

        // Header
        layout.addView(this.headerView())

        // ------------------------
        layout.addView(this.dividerView())

        // Edit Layout View
        layout.addView(this.editLayoutView())

        // ------------------------
        layout.addView(this.dividerView())

        // Settings
        layout.addView(this.settingsView())

        // ------------------------
        layout.addView(this.dividerView())

        // State Button
        layout.addView(this.advancedView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.topDp    = 34f

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // HEADER
    // -----------------------------------------------------------------------------------------

    private fun headerView() : TextView
    {
        val header                  = TextViewBuilder()

        header.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        header.textId               = R.string.sheet_options

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        header.color                = ThemeManager.color(themeId, colorTheme)

        header.font                 = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Bold,
                                                    context)

        header.sizeSp               = 16f

        header.margin.leftDp        = 10f
        header.margin.bottomDp      = 12f

        return header.textView(context)
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        divider.backgroundColor     = ThemeManager.color(themeId, colorTheme)

        return divider.linearLayout(context)
    }


    private fun buttonView(iconId : Int,
                           labelId : Int) : LinearLayout
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

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

        layout.margin.leftDp    = 10f
        layout.margin.rightDp   = 10f

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 20
        icon.heightDp       = 20

        icon.image          = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color          = ThemeManager.color(themeId, iconColorTheme)

        icon.margin.rightDp = 12f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId        = labelId

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color         = ThemeManager.color(themeId, colorTheme)

        label.font          = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            context)

        label.sizeSp         = 17f


        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // EDIT LAYOUT VIEW
    // -----------------------------------------------------------------------------------------

    private fun editLayoutView() : LinearLayout
    {
        val layout = this.editLayoutViewLayout()

        layout.addView(editModeView())

        layout.addView(this.buttonView(R.drawable.icon_layout,
                                       R.string.layout_editor))

        return layout
    }


    private fun editLayoutViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

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

        layout.margin.leftDp        = 10f
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

        label.textId                = R.string.edit_mode

        label.font                  = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color                 = ThemeManager.color(themeId, colorTheme)

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

        // Theme Button
        layout.addView(this.buttonView(R.drawable.icon_change_theme,
                                       R.string.manage_themes))

        // Settings Button
        layout.addView(this.buttonView(R.drawable.icon_settings,
                                       R.string.settings))

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

//
//    private fun stateButtonView(sheetUIContext : SheetUIContext) : TextView
//    {
//        val button                  = TextViewBuilder()
//
//        button.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        button.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        button.textId               = R.string.view_state
//
//        button.gravity              = Gravity.CENTER
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        button.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)
//
//        button.corners              = Corners(TopLeftCornerRadius(2f),
//                                              TopRightCornerRadius(2f),
//                                              BottomRightCornerRadius(2f),
//                                              BottomLeftCornerRadius(2f))
//
//        val textColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        button.color                = SheetManager.color(sheetUIContext.sheetId, textColorTheme)
//
//        button.font                 = Font.typeface(TextFont.FiraSans,
//                                                    TextFontStyle.Regular,
//                                                    sheetUIContext.context)
//
//        button.sizeSp               = 16f
//
//        button.margin.leftDp        = 10f
//        button.margin.rightDp       = 10f
//
//        button.margin.topDp         = 12f
//        button.margin.bottomDp      = 12f
//
//        button.padding.topDp        = 8f
//        button.padding.bottomDp     = 8f
//        button.padding.leftDp       = 8f
//        button.padding.rightDp      = 8f
//
//        return button.textView(sheetUIContext.context)
//    }
}


