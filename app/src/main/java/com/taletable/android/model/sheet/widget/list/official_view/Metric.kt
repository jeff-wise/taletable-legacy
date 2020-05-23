
package com.taletable.android.model.sheet.widget.list.official_view


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
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.taletable.android.R
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.sheet.widget.WidgetStyle
import com.taletable.android.model.sheet.widget.WidgetStyleVariation
import com.taletable.android.model.sheet.widget.list.ListWidgetViewData
import com.taletable.android.model.theme.*
import com.taletable.android.util.Util
import maybe.Maybe



/**
 * Metric View
 */
fun listWidgetOfficialMetricView(
        style : WidgetStyle,
        variations : List<WidgetStyleVariation>,
        data : ListWidgetViewData,
        theme : Theme,
        context : Context,
        groupContext : Maybe<GroupContext>
) : View = when (style.value)
{
    "rows" -> rowsView(data, theme, groupContext, context)
    "inline" -> inlineView(data, theme, groupContext, context)
    "inline_textual" -> inlineTextualView(data, variations, theme, groupContext, context)
    "bullet" -> bulletView(data, variations, theme, context)
    else   -> rowsView(data, theme, groupContext, context)
}



// ---------------------------------------------------------------------------------------------
// | ROWS STYLE
// ---------------------------------------------------------------------------------------------


/**
 * Paragraph Style
 */
private fun rowsView(
        data : ListWidgetViewData,
        theme : Theme,
        groupContext : Maybe<GroupContext>,
        context : Context
) : View
{
    val layout = rowsViewLayout(context)

    data.label.doMaybe {
        layout.addView(rowsLabelView(it, theme, context))
    }

    layout.addView(rowsValuesView(data.values, theme, groupContext, context))

    return layout
}


private fun rowsViewLayout(
        context : Context
) : LinearLayout
{
    val layoutBuilder           = LinearLayoutBuilder()

    layoutBuilder.width         = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height        = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation   = LinearLayout.VERTICAL

    return layoutBuilder.linearLayout(context)
}


/**
 * Vertical box
 */
private fun rowsLabelView(
        label : String,
        theme : Theme,
        context : Context
) : LinearLayout
{

    // | Declarations
    // -----------------------------------------------------------------------------------------

    val layoutBuilder       = LinearLayoutBuilder()
    val labelViewBuilder    = TextViewBuilder()

    // | Layout
    // -----------------------------------------------------------------------------------------

    layoutBuilder.width             = LinearLayout.LayoutParams.WRAP_CONTENT
    layoutBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.gravity           = Gravity.CENTER

    layoutBuilder.padding.leftDp    = 8f
    layoutBuilder.padding.rightDp   = 8f
    layoutBuilder.padding.topDp     = 4f
    layoutBuilder.padding.bottomDp     = 4f

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
    layoutBuilder.backgroundColor   = theme.colorOrBlack(bgColorTheme)

    layoutBuilder.corners   = Corners(4.0, 4.0, 0.0, 0.0)

    layoutBuilder.child(labelViewBuilder)

    // | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width      = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height     = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.text       = label

    labelViewBuilder.font       = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)
    val textColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_18"))))
    labelViewBuilder.color      = theme.colorOrBlack(textColorTheme)

    labelViewBuilder.sizeSp     = 12f

    return layoutBuilder.linearLayout(context)
}



private fun rowsValuesView(
        values : List<String>,
        theme : Theme,
        groupContext : Maybe<GroupContext>,
        context : Context
) : LinearLayout
{
    val layout = rowsValuesLayout(context)

    values.forEachIndexed { index, s ->
        val hasDivider = index > 0
        val rowView = rowsValueView(s, hasDivider, theme, context)
        layout.addView(rowView)
    }

    return layout
}


/**
 * Vertical Box Value
 */
private fun rowsValuesLayout(
        context : Context
) : LinearLayout
{

    val layoutBuilder                   = LinearLayoutBuilder()

    layoutBuilder.width                 = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height                = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.backgroundResource    = R.drawable.bg_style_vertical_box

    layoutBuilder.orientation           = LinearLayout.VERTICAL

    return layoutBuilder.linearLayout(context)
}


/**
 * Vertical Box Value
 */
private fun rowsValueView(
        label : String,
        hasDivider : Boolean,
        theme : Theme,
        context : Context
) : LinearLayout
{

    // | Declarations
    // -----------------------------------------------------------------------------------------

    val layoutBuilder               = LinearLayoutBuilder()
    val labelViewBuilder            = TextViewBuilder()
    val dividerViewBuilder          = LinearLayoutBuilder()

    // | Layout
    // -----------------------------------------------------------------------------------------

    layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation       = LinearLayout.VERTICAL

    if (hasDivider)
        layoutBuilder.child(dividerViewBuilder)

    layoutBuilder.child(labelViewBuilder)

    // | Divider
    // -----------------------------------------------------------------------------------------

    dividerViewBuilder.width           = LinearLayout.LayoutParams.MATCH_PARENT
    dividerViewBuilder.heightDp         = 1

    val dividerColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
    dividerViewBuilder.backgroundColor  = theme.colorOrBlack(dividerColorTheme)

    // | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width      = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height     = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.margin.leftDp    = 12f
    labelViewBuilder.margin.rightDp   = 12f
    labelViewBuilder.margin.topDp     = 12f
    labelViewBuilder.margin.bottomDp  = 12f

    labelViewBuilder.text       = label

    labelViewBuilder.font       = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
    labelViewBuilder.color           = theme.colorOrBlack(labelColorTheme)

    labelViewBuilder.sizeSp     = 18f

    return layoutBuilder.linearLayout(context)
}



private fun inlineView(
        data : ListWidgetViewData,
        theme : Theme,
        groupContext : Maybe<GroupContext>,
        context : Context
) : View
{
    val linearLayoutBuilder             = LinearLayoutBuilder()

    linearLayoutBuilder.width           = LinearLayout.LayoutParams.MATCH_PARENT
    linearLayoutBuilder.height          = LinearLayout.LayoutParams.WRAP_CONTENT

    linearLayoutBuilder.backgroundResource   = R.drawable.bg_book_card_header
    linearLayoutBuilder.padding.topDp        = 8f
    linearLayoutBuilder.padding.bottomDp        = 8f
    linearLayoutBuilder.padding.leftDp        = 16f
    linearLayoutBuilder.padding.rightDp        = 16f

    linearLayoutBuilder.child(inlineFlexView(data, theme, groupContext, context))

    return linearLayoutBuilder.linearLayout(context)
}


/**
 * Inline Style
 */
private fun inlineFlexView(
        data : ListWidgetViewData,
        theme : Theme,
        groupContext : Maybe<GroupContext>,
        context : Context
) : ViewBuilder
{
    val layout          = FlexboxLayoutBuilder()

    layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.contentAlignment         = AlignContent.CENTER
    layout.wrap                     = FlexWrap.WRAP

    data.values.forEach { value ->
        layout.child(inlineItemView(value, theme, context))
    }

    return layout
}


private fun inlineItemView(
        value : String,
        theme: Theme,
        context : Context
) : TextViewBuilder
{

    val textViewBuilder         = TextViewBuilder()

    textViewBuilder.width       = LinearLayout.LayoutParams.WRAP_CONTENT
    textViewBuilder.heightDp      = 26

    textViewBuilder.gravity     = Gravity.CENTER

    textViewBuilder.font        = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)

    val textColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_14"))))
    textViewBuilder.color = theme.colorOrBlack(textColorTheme)

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_2"))))
    textViewBuilder.backgroundColor = theme.colorOrBlack(bgColorTheme)
    textViewBuilder.backgroundColor = Color.WHITE

    textViewBuilder.sizeSp          = 11.5f

    textViewBuilder.text        = value

    textViewBuilder.padding.leftDp      = 10f
    textViewBuilder.padding.rightDp      = 10f
//    textViewBuilder.padding.topDp      = 8f
//    textViewBuilder.padding.bottomDp      = 8f

    textViewBuilder.corners             = Corners(8.0, 8.0, 8.0, 8.0)

    return textViewBuilder
}



private fun inlineTextualView(
        data : ListWidgetViewData,
        variations : List<WidgetStyleVariation>,
        theme : Theme,
        groupContext : Maybe<GroupContext>,
        context : Context
) : LinearLayout
{
    val layout = inlineTextualViewLayout(context)

    layout.addView(inlineTextualItemsView(data.label, variations, data.values, theme, context))

    return layout
}


private fun inlineTextualViewLayout(context : Context) : LinearLayout
{
    val linearLayoutBuilder = LinearLayoutBuilder()

    linearLayoutBuilder.width           = LinearLayout.LayoutParams.MATCH_PARENT
    linearLayoutBuilder.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    return linearLayoutBuilder.linearLayout(context)
}


private fun inlineTextualItemsView(
        label : Maybe<String>,
        variations : List<WidgetStyleVariation>,
        values : List<String>,
        theme : Theme,
        context : Context
) : TextView
{
    val textViewBuilder         = TextViewBuilder()
//
    textViewBuilder.width       = LinearLayout.LayoutParams.MATCH_PARENT
    textViewBuilder.height      = LinearLayout.LayoutParams.WRAP_CONTENT

    textViewBuilder.font        = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Regular,
                                                context)

    val defaultColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    textViewBuilder.color = theme.colorOrBlack(defaultColorTheme)


    if (variations.isNotEmpty()) {
        val variation = variations[0]
        when (variation.value) {
            "normal" -> {
                textViewBuilder.sizeSp          = 15.7f
            }
            "large" -> {
                textViewBuilder.sizeSp          = 17.3f
            }
        }
    }

    textViewBuilder.lineSpacingAdd  = 4f
    textViewBuilder.lineSpacingMult = 1f

    val valuesText            = when (values.size) {
        0 -> ""
        1 -> values[0]
        else -> values.joinToString(", ")
    }

    textViewBuilder.textSpan        = inlineTextualItemsSpanView(label, variations, valuesText, theme, context)

    return textViewBuilder.textView(context)
}


private fun inlineTextualItemsSpanView(
        maybeLabel : Maybe<String>,
        variations : List<WidgetStyleVariation>,
        valuesText : String,
        theme : Theme,
        context : Context
) : SpannableStringBuilder
{
    val stringBuilder = SpannableStringBuilder()

    var index = 0

    maybeLabel.doMaybe { label ->
        stringBuilder.append(label)


        var labelSizeSpan = AbsoluteSizeSpan(Util.spToPx(15.7f, context))

        if (variations.isNotEmpty()) {
            val variation = variations[0]
            when (variation.value) {
                "normal" -> {
                    labelSizeSpan = AbsoluteSizeSpan(Util.spToPx(15.7f, context))
                }
                "large" -> {
                    labelSizeSpan = AbsoluteSizeSpan(Util.spToPx(17.8f, context))
                }
            }
        }


        val typeface = Font.typeface(TextFont.RobotoSlab, TextFontStyle.Bold, context)
        val labelTypefaceSpan = CustomTypefaceSpan(typeface)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        var labelColor = theme.colorOrBlack(labelColorTheme)
        val labelColorSpan = ForegroundColorSpan(labelColor)

        stringBuilder.setSpan(labelSizeSpan, 0, label.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        stringBuilder.setSpan(labelTypefaceSpan, 0, label.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        stringBuilder.setSpan(labelColorSpan, 0, label.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        index = label.length
    }

    stringBuilder.append(" $valuesText")

    return stringBuilder
}


// -------------------------------------------------------------------------------------------------
// BULLET_VIEW
// -------------------------------------------------------------------------------------------------

private fun bulletView(
        data : ListWidgetViewData,
        variations : List<WidgetStyleVariation>,
        theme : Theme,
        context : Context
) : LinearLayout
{
    val layout = bulletViewLayout(context)

    data.label.doMaybe {
        layout.addView(bulletHeaderView(it, variations, theme, context))
    }

    data.values.forEachIndexed { index, value ->
        val isFirst = index == 0
        layout.addView(bulletItemView(value, variations, isFirst, theme, context))
    }

    return layout
}


private fun bulletViewLayout(context : Context) : LinearLayout
{
    val linearLayoutBuilder = LinearLayoutBuilder()

    linearLayoutBuilder.width           = LinearLayout.LayoutParams.MATCH_PARENT
    linearLayoutBuilder.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    linearLayoutBuilder.orientation     = LinearLayout.VERTICAL

    return linearLayoutBuilder.linearLayout(context)
}

private fun bulletHeaderView(
        header : String,
        variations : List<WidgetStyleVariation>,
        theme : Theme,
        context : Context
) : TextView
{
    val textViewBuilder         = TextViewBuilder()
//
    textViewBuilder.width       = LinearLayout.LayoutParams.MATCH_PARENT
    textViewBuilder.height      = LinearLayout.LayoutParams.WRAP_CONTENT

    textViewBuilder.font        = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)

    textViewBuilder.margin.bottomDp = 6f

    val defaultColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_8"))))
    textViewBuilder.color = theme.colorOrBlack(defaultColorTheme)

    textViewBuilder.sizeSp                      = 16.7f

    if (variations.isNotEmpty()) {
        val variation = variations[0]
        when (variation.value) {
            "normal" -> {
                textViewBuilder.sizeSp          = 16.7f
            }
            "large" -> {
                textViewBuilder.sizeSp          = 17.3f
            }
        }
    }

    textViewBuilder.lineSpacingAdd  = 4f
    textViewBuilder.lineSpacingMult = 1f

    textViewBuilder.text            = header

    return textViewBuilder.textView(context)
}


private fun bulletItemView(
        value : String,
        variations : List<WidgetStyleVariation>,
        isFirst : Boolean,
        theme : Theme,
        context : Context
) : LinearLayout
{
    val layoutBuilder           = LinearLayoutBuilder()

    layoutBuilder.width         = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height        = LinearLayout.LayoutParams.WRAP_CONTENT

    if (!isFirst) {
        layoutBuilder.margin.topDp  = 8f
    }

    layoutBuilder.orientation   = LinearLayout.HORIZONTAL

    layoutBuilder.gravity       = Gravity.TOP

    //layoutBuilder.child(bulletItemBulletViewBuilder(variations, theme, context))
    layoutBuilder.child(bulletItemTextViewBuilder(value, variations, theme, context))

    return layoutBuilder.linearLayout(context)
}


private fun bulletItemBulletViewBuilder(
        variations : List<WidgetStyleVariation>,
        theme : Theme,
        context : Context
) : TextViewBuilder
{
    val textViewBuilder         = TextViewBuilder()
//
    textViewBuilder.width       = LinearLayout.LayoutParams.WRAP_CONTENT
    textViewBuilder.height      = LinearLayout.LayoutParams.WRAP_CONTENT

    textViewBuilder.font        = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Regular,
                                                context)

    textViewBuilder.margin.leftDp   = 4f
    textViewBuilder.margin.rightDp   = 4f
    textViewBuilder.padding.topDp   = 4f

    val defaultColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    textViewBuilder.color = theme.colorOrBlack(defaultColorTheme)

    textViewBuilder.sizeSp                      = 23f

    if (variations.isNotEmpty()) {
        val variation = variations[0]
        when (variation.value) {
            "normal" -> {
                textViewBuilder.sizeSp          = 23f
            }
            "large" -> {
                textViewBuilder.sizeSp          = 17.3f
            }
        }
    }

//    textViewBuilder.lineSpacingAdd  = 4f
//    textViewBuilder.lineSpacingMult = 1f

    textViewBuilder.text            = "•"


    return textViewBuilder
}

private fun bulletItemTextViewBuilder(
        value : String,
        variations : List<WidgetStyleVariation>,
        theme : Theme,
        context : Context
) : TextViewBuilder
{
    val textViewBuilder         = TextViewBuilder()
//
    textViewBuilder.width       = LinearLayout.LayoutParams.MATCH_PARENT
    textViewBuilder.height      = LinearLayout.LayoutParams.WRAP_CONTENT

    textViewBuilder.font        = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Regular,
                                                context)

    val defaultColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    textViewBuilder.color = theme.colorOrBlack(defaultColorTheme)

    textViewBuilder.sizeSp                      = 15.7f

    if (variations.isNotEmpty()) {
        val variation = variations[0]
        when (variation.value) {
            "normal" -> {
                textViewBuilder.sizeSp          = 15.7f
            }
            "large" -> {
                textViewBuilder.sizeSp          = 17.3f
            }
        }
    }

//    textViewBuilder.lineSpacingAdd  = 4f
//    textViewBuilder.lineSpacingMult = 1f

    textViewBuilder.lineSpacingAdd  = 2f
    textViewBuilder.lineSpacingMult = 1.05f

    textViewBuilder.text            = value

    return textViewBuilder
}

