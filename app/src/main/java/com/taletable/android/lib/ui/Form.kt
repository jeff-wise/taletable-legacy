
package com.taletable.android.lib.ui


import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.theme.ThemeManager



/**
 * Form Widgets
 */
object Form
{


    // -----------------------------------------------------------------------------------------
    // TOOLBAR
    // -----------------------------------------------------------------------------------------

    fun toolbarView(themeId : ThemeId, context : Context) : LinearLayout
    {
        val layout = this.toolbarViewLayout(context)

        // Help Button
        layout.addView(this.toolbarButtonView(R.drawable.ic_form_toolbar_help,
                                              R.string.help,
                                              themeId,
                                              context))

        // Duplicate Button
        layout.addView(toolbarButtonView(R.drawable.ic_form_toolbar_duplicate,
                                         R.string.duplicate,
                                         themeId,
                                         context))

        // Export Button
        layout.addView(toolbarButtonView(R.drawable.ic_form_toolbar_export,
                                         R.string.export,
                                         themeId,
                                         context))

        // Delete Button
        layout.addView(toolbarButtonView(R.drawable.ic_form_toolbar_delete,
                                         R.string.delete,
                                         themeId,
                                         context))

        return layout
    }


    private fun toolbarViewLayout(context : Context) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.topDp        = 14f
        layout.padding.bottomDp     = 10f

        return layout.linearLayout(context)
    }


    private fun toolbarButtonView(iconId : Int,
                                  labelId : Int,
                                  themeId : ThemeId,
                                  context : Context) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()
        val label               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.orientation      = LinearLayout.VERTICAL

        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight           = 1f

        layout.gravity          = Gravity.CENTER

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 22
        icon.heightDp           = 22

        icon.image              = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color              = ThemeManager.color(themeId, iconColorTheme)

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = labelId

        label.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_27")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color              = ThemeManager.color(themeId, labelColorTheme)

        label.sizeSp            = 11f

        label.margin.topDp      = 3f

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // DIVIDER
    // -----------------------------------------------------------------------------------------

    fun dividerView(themeId : ThemeId, context : Context) : LinearLayout
    {
        val divider                 = LinearLayoutBuilder()

        divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp            = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        divider.backgroundColor     = ThemeManager.color(themeId, colorTheme)

        return divider.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // TEXT FIELD
    // -----------------------------------------------------------------------------------------

    fun textFieldView(label : String,
                      value : String,
                      themeId : ThemeId,
                      context : Context) : LinearLayout
    {
        val layout = this.textFieldViewLayout(context)

        // Label
        layout.addView(this.textFieldLabelView(label, themeId, context))

        // Value
        layout.addView(this.textFieldValueView(value, themeId, context))

        return layout
    }


    private fun textFieldViewLayout(context : Context) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.topDp        = 10f
        layout.padding.bottomDp     = 10f
        layout.padding.leftDp       = 10f
        layout.padding.rightDp      = 10f

        return layout.linearLayout(context)
    }


    private fun textFieldLabelView(labelString : String,
                                   themeId : ThemeId,
                                   context : Context) : TextView
    {
        val label               = TextViewBuilder()

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text              = labelString

        label.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color              = ThemeManager.color(themeId, labelColorTheme)

        label.sizeSp            = 12f

        return label.textView(context)
    }


    private fun textFieldValueView(valueString : String,
                                   themeId : ThemeId,
                                   context : Context) : TextView
    {
        val value               = TextViewBuilder()

        value.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        value.text              = valueString

        value.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)


        if (valueString.length < 35) {
            val colorTheme = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_14")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
            value.color              = ThemeManager.color(themeId, colorTheme)
            value.sizeSp            = 17f
        }
        else {
            val colorTheme = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
            value.color              = ThemeManager.color(themeId, colorTheme)
            value.sizeSp            = 14f
        }

        value.margin.topDp      = 6f

        return value.textView(context)
    }


    // -----------------------------------------------------------------------------------------
    // MODEL FIELD
    // -----------------------------------------------------------------------------------------

    fun modelFieldView(name : String,
                       themeId : ThemeId,
                       context : Context) : LinearLayout
    {
        val layout = this.modelFieldViewLayout(context)

        // Icon
        layout.addView(this.modelFieldIconView(themeId, context))

        // Name
        layout.addView(this.modelFieldNameView(name, themeId, context))

        return layout
    }


    private fun modelFieldViewLayout(context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.leftDp   = 10f
        layout.padding.topDp    = 16f
        layout.padding.bottomDp = 16f

        return layout.linearLayout(context)
    }


    private fun modelFieldIconView(themeId : ThemeId, context : Context) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.margin.rightDp   = 7f

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 22
        icon.heightDp           = 22

        icon.image              = R.drawable.icon_edit_model

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color              = ThemeManager.color(themeId, iconColorTheme)

        return layout.linearLayout(context)
    }


    private fun modelFieldNameView(nameString : String,
                                   themeId: ThemeId,
                                   context: Context) : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.text               = nameString

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        name.color              = ThemeManager.color(themeId, colorTheme)

        name.sizeSp             = 17f

        return name.textView(context)
    }



    // -----------------------------------------------------------------------------------------
    // MODEL FIELD
    // -----------------------------------------------------------------------------------------

    fun listFieldView(name : String,
                      themeId : ThemeId,
                      context : Context) : LinearLayout
    {
        val layout = this.listFieldViewLayout(context)

        // Icon
        layout.addView(this.listFieldIconView(themeId, context))

        // Name
        layout.addView(this.listFieldNameView(name, themeId, context))

        return layout
    }


    private fun listFieldViewLayout(context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.leftDp   = 10f
        layout.padding.topDp    = 16f
        layout.padding.bottomDp = 16f

        return layout.linearLayout(context)
    }


    private fun listFieldIconView(themeId : ThemeId, context : Context) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.margin.rightDp   = 7f

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 22
        icon.heightDp           = 22

        icon.image              = R.drawable.icon_list

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color              = ThemeManager.color(themeId, iconColorTheme)

        return layout.linearLayout(context)
    }


    private fun listFieldNameView(nameString : String,
                                  themeId: ThemeId,
                                  context: Context) : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.text               = nameString

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        name.color              = ThemeManager.color(themeId, colorTheme)

        name.sizeSp             = 17f

        return name.textView(context)
    }

}

