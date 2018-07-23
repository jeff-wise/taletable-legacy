
package com.taletable.android.activity.official


import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.RoundedBackgroundHeightSpan
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.style.IconSize
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.util.Util


fun descriptionView(theme : Theme, context : Context) : TextView
{
    val description                 = TextViewBuilder()

    description.id                  = R.id.item_description

    description.width               = LinearLayout.LayoutParams.MATCH_PARENT
    description.height              = LinearLayout.LayoutParams.WRAP_CONTENT

    description.font                = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
    description.color               = theme.colorOrBlack(colorTheme)


    description.sizeSp              = 17f

    description.visibility          = View.GONE

    return description.textView(context)
}


fun summaryView(theme : Theme, context : Context) : TextView
{
    val summary                 = TextViewBuilder()

    summary.id                  = R.id.item_summary

    summary.width               = LinearLayout.LayoutParams.MATCH_PARENT
    summary.height              = LinearLayout.LayoutParams.WRAP_CONTENT

    summary.font                = Font.typeface(TextFont.default(),
                                            TextFontStyle.Regular,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
    summary.color              = theme.colorOrBlack(colorTheme)


    summary.sizeSp             = 17f

    summary.padding.bottomDp    = 5f

    return summary.textView(context)
}


fun summarySpannable(summaryString : String,
                            theme : Theme,
                            context : Context) : SpannableStringBuilder
{
    val builder = SpannableStringBuilder()

    builder.append("$summaryString ")


    builder.append(" \u2026 ")


    val sizePx = Util.spToPx(28f, context)
    val sizeSpan = AbsoluteSizeSpan(sizePx)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
    val color              = theme.colorOrBlack(colorTheme)
    val colorSpan = ForegroundColorSpan(color)

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
    var bgColor = theme.colorOrBlack(bgColorTheme)
//    val bgColorSpan = BackgroundColorSpan(bgColor)

    val bgSpan = RoundedBackgroundHeightSpan(60,
                                             15,
                                             0.85f,
                                             20,
                                             color,
                                             bgColor,
                                             null,
                                             IconSize(17, 17),
                                             Color.WHITE)

    val startIndex = summaryString.length + 1

    builder.setSpan(sizeSpan, startIndex, startIndex + 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    builder.setSpan(colorSpan, startIndex, startIndex + 3 , Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    builder.setSpan(bgSpan, startIndex, startIndex + 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

    return builder
}


fun footerView(theme : Theme,
               context : Context) : LinearLayout
{
    val layout = footerViewLayout(theme, context)

    // MORE INFO
    layout.addView(footerButtonView(context.getString(R.string.more_info).toUpperCase(),
                                    R.id.item_more_info,
                                    theme,
                                    context))

    // OPEN
    layout.addView(footerButtonView(context.getString(R.string.open).toUpperCase(),
                                    R.id.item_open_button,
                                    theme,
                                    context))

    return layout
}


private fun footerViewLayout(theme : Theme, context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.HORIZONTAL

    layout.gravity          = Gravity.END

    layout.padding.topDp    = 6f
    layout.padding.bottomDp = 6f
    layout.padding.rightDp  = 6f

    return layout.linearLayout(context)
}


private fun footerButtonView(label : String, id : Int, theme : Theme, context : Context) : TextView
{
    val button                  = TextViewBuilder()

    button.id                   = id

    button.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    button.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    button.text                 = label

    button.font                 = Font.typeface(TextFont.default(),
                                                TextFontStyle.Bold,
                                                context)

    button.margin.leftDp       = 25f

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
    button.color                = theme.colorOrBlack(colorTheme)

    button.sizeSp               = 15f

    return button.textView(context)
}