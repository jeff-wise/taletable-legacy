
package com.taletable.android.activity.session


import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import com.taletable.android.R
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.ImageViewBuilder
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*




data class OptionCardSection(val label : String)


fun optionCardView(iconId : Int,
                   titleStringId : Int,
                   summaryStringId : Int,
                   options : List<OptionCardSection>,
                   theme : Theme,
                   context : Context) : LinearLayout
{
    val layout = optionCardViewLayout(theme, context)

    layout.addView(optionCardMainView(iconId, titleStringId, summaryStringId, theme, context))

    if (options.isNotEmpty()) {
        layout.addView(optionCardDividerView(theme, context))
        layout.addView(optionCardSectionsButtonView(theme, context))
    }

    return layout
}


private fun optionCardViewLayout(theme : Theme, context : Context) : LinearLayout
{
    val layout                  = LinearLayoutBuilder()

    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation          = LinearLayout.VERTICAL

    layout.gravity              = Gravity.CENTER_VERTICAL

    layout.backgroundResource   = R.drawable.bg_card_flat

    layout.margin.bottomDp      = 12f

    return layout.linearLayout(context)
}


fun optionCardMainView(iconId : Int,
                       titleStringId : Int,
                       summaryStringId : Int,
                       theme : Theme,
                       context : Context) : LinearLayout
{
    val layout = optionCardMainViewLayout(theme, context)

    layout.addView(optionCardLeftView(iconId, theme, context))

    layout.addView(optionCardRightView(titleStringId, summaryStringId, theme, context))

    return layout
}


private fun optionCardMainViewLayout(theme : Theme, context : Context) : LinearLayout
{
    val layout                  = LinearLayoutBuilder()

    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation          = LinearLayout.HORIZONTAL

    layout.gravity              = Gravity.CENTER_VERTICAL

    layout.padding.topDp        = 12f
    layout.padding.bottomDp     = 12f
    layout.padding.leftDp       = 12f
    layout.padding.rightDp      = 12f

    return layout.linearLayout(context)
}



private fun optionCardLeftView(iconId : Int, theme : Theme, context : Context) : LinearLayout
{
    // | Declarations
    // -----------------------------------------------------------------------------------------

    val layout              = LinearLayoutBuilder()
    val icon                = ImageViewBuilder()

    // | Layout
    // -----------------------------------------------------------------------------------------

    layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.HORIZONTAL

    layout.padding.rightDp  = 12f

    layout.child(icon)

    // | Icon
    // -----------------------------------------------------------------------------------------

    icon.image              = iconId

    icon.widthDp            = 60
    icon.heightDp           = 60

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_10"))))
    icon.color              = theme.colorOrBlack(colorTheme)

    return layout.linearLayout(context)
}


private fun optionCardRightView(titleStringId : Int,
                                summaryStringId : Int,
                                theme : Theme,
                                context : Context) : LinearLayout
{
    // | Declarations
    // -----------------------------------------------------------------------------------------

    val layout              = LinearLayoutBuilder()
    val title               = TextViewBuilder()
    val summary             = TextViewBuilder()

    // | Layout
    // -----------------------------------------------------------------------------------------

    layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.VERTICAL

    layout.child(title)
          .child(summary)

    // | Title
    // -----------------------------------------------------------------------------------------

    title.textId            = titleStringId

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Bold,
                                            context)

    val titleColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_10"))))
    title.color              = theme.colorOrBlack(titleColorTheme)

    title.sizeSp             = 20f

    // | Summary
    // -----------------------------------------------------------------------------------------

    summary.textId            = summaryStringId

    summary.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Regular,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_16"))))
    summary.color              = theme.colorOrBlack(colorTheme)

    summary.sizeSp             = 18f

    return layout.linearLayout(context)
}


fun optionCardSectionsButtonView(theme : Theme,
                                 context : Context) : LinearLayout
{
    // | Declarations
    // -----------------------------------------------------------------------------------------

    val layout                  = LinearLayoutBuilder()
    val label                   = TextViewBuilder()
    val icon                    = ImageViewBuilder()

    // | Layout
    // -----------------------------------------------------------------------------------------

    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation          = LinearLayout.HORIZONTAL

    layout.gravity              = Gravity.CENTER

    layout.padding.topDp        = 11f
    layout.padding.bottomDp     = 12f

    layout.child(label)
          .child(icon)

    // | Label
    // -----------------------------------------------------------------------------------------

    label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
    label.height                = LinearLayout.LayoutParams.WRAP_CONTENT

    label.textId                = R.string.show_options

    label.font                  = Font.typeface(TextFont.Roboto,
                                                TextFontStyle.Regular,
                                                context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
    label.color              = theme.colorOrBlack(labelColorTheme)

    label.sizeSp                = 16f

    label.margin.rightDp        = 5f


    // | Icon
    // -----------------------------------------------------------------------------------------

    icon.widthDp                = 27
    icon.heightDp               = 27

    icon.padding.topDp          = 2f

    icon.image                  = R.drawable.icon_chevron_down

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
    icon.color                  = theme.colorOrBlack(iconColorTheme)

    return layout.linearLayout(context)
}


private fun optionCardDividerView(theme : Theme, context : Context) : LinearLayout
{
    val layout                  = LinearLayoutBuilder()

    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
    layout.heightDp             = 1

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_6"))))
    layout.backgroundColor      = theme.colorOrBlack(colorTheme)

    return layout.linearLayout(context)
}

