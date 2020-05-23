
package com.taletable.android.model.sheet.widget.text.official_view


import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.lib.ui.CustomTypefaceSpan
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.sheet.widget.WidgetStyle
import com.taletable.android.model.sheet.widget.WidgetStyleVariation
import com.taletable.android.model.sheet.widget.text.TextWidgetViewData
import com.taletable.android.model.theme.*
import com.taletable.android.util.Util
import maybe.Just
import maybe.Maybe
import maybe.Nothing


/**
 * Metric View
 */
fun textWidgetOfficialMetricView(
        style : WidgetStyle,
        variations : List<WidgetStyleVariation>,
        data : TextWidgetViewData,
        theme : Theme,
        context : Context,
        groupContext : Maybe<GroupContext>
) : View = when (style.value)
{
    "paragraph"        -> paragraphView(data, variations, theme, groupContext, context)
    "paragraph_header" ->
        textWidgetMetricParagraphHeaderView(data, variations, theme, groupContext, context)
    "vertical_box"     ->
        textWidgetMetricVerticalBoxView(data, variations, theme, groupContext, context)
    "horizontal_box"   ->
        horizontalBoxView(data, variations, theme, groupContext, context)
    "entity_section_label" ->
        entitySectionLabelView(data, theme, groupContext, context)
    "entity_section_tag" ->
        entitySectionEntryTagView(data, theme, groupContext, context)
    else               ->
        paragraphView(data, variations, theme, groupContext, context)
}


// ---------------------------------------------------------------------------------------------
// | PARAGRAPH STYLE
// ---------------------------------------------------------------------------------------------

/**
 * Paragraph Style
 *
 * Small and legible body text.
 */
private fun paragraphView(
        data : TextWidgetViewData,
        variations : List<WidgetStyleVariation>,
        theme : Theme,
        groupContext : Maybe<GroupContext>,
        context : Context
) : View
{
    // Get paragraphs
    var paragraphs = data.value.split("\n")


    Log.d("***TEXT METRIC", "header: ${data.label}  body: ${data.value}")

    if (paragraphs.isEmpty()) {
        paragraphs = listOf(data.value)
    }


    // Get label
    val layout = paragraphViewLayout(context)

    // Add first paragraph
    paragraphs.firstOrNull()?.let { paragraph ->
        layout.addView(paragraphTextView(data.label.toNullable(), paragraph, paragraphs.size > 1, variations, theme, context))
        //val labelString =
//        when (data.label) {
//            is Just -> {
//            }
//            is Nothing -> {
//                layout.addView(paragraphTextView(null, paragraph, false, theme, context))
//            }
//        }
    }

    paragraphs.drop(1).forEachIndexed { index, paragraphString ->
        val isParagraph = index < paragraphs.size - 2
        val view = paragraphTextView(
                        null,
                       paragraphString,
                       isParagraph,
                        variations,
                       theme,
                       context)
        layout.addView(view)
    }

    return layout
}

private fun paragraphViewLayout(
        context : Context
) : LinearLayout
{
    val layoutBuilder           = LinearLayoutBuilder()

    layoutBuilder.width         = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height        = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation   = LinearLayout.VERTICAL

    return layoutBuilder.linearLayout(context)
}



private fun paragraphTextView(
        header : String?,
        body : String,
        isParagraph : Boolean,
        variations : List<WidgetStyleVariation>,
        theme : Theme,
        context : Context
) : TextView
{
    val textViewBuilder = TextViewBuilder()

    textViewBuilder.width           = LinearLayout.LayoutParams.MATCH_PARENT
    textViewBuilder.height          = LinearLayout.LayoutParams.WRAP_CONTENT

    if (isParagraph)
        textViewBuilder.margin.bottomDp = 16f

    textViewBuilder.textSpan        = paragraphSpannable(header, variations, body, theme, context)

    textViewBuilder.font            = Font.typeface(TextFont.RobotoSlab,
                                                    TextFontStyle.Regular,
                                                    context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    textViewBuilder.color           = theme.colorOrBlack(colorTheme)

    textViewBuilder.sizeSp          = 15.7f

    textViewBuilder.lineSpacingAdd  = 2f
    textViewBuilder.lineSpacingMult = 1.05f

    return textViewBuilder.textView(context)
}


private fun paragraphSpannable(
        header : String?,
        variations : List<WidgetStyleVariation>,
        body : String,
        theme : Theme,
        context : Context
) : SpannableStringBuilder
{
    val builder = SpannableStringBuilder()

    if (header != null)
    {
        builder.append(header)
        builder.append(" ")

        val typeface = Font.typeface(TextFont.RobotoSlab, TextFontStyle.Bold, context)
        val typefaceSpan = CustomTypefaceSpan(typeface)
        builder.setSpan(typefaceSpan, 0, header.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        var labelSizeSpan = AbsoluteSizeSpan(Util.spToPx(16.7f, context))

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_8"))))
        var labelColor = theme.colorOrBlack(labelColorTheme)
        val labelColorSpan = ForegroundColorSpan(labelColor)

        if (variations.isNotEmpty()) {
            val variation = variations[0]
            when (variation.value) {
                "normal" -> {
                    labelSizeSpan = AbsoluteSizeSpan(Util.spToPx(16.7f, context))
                }
                "large" -> {
                    labelSizeSpan = AbsoluteSizeSpan(Util.spToPx(17.5f, context))
                }
            }
        }

        builder.setSpan(labelSizeSpan, 0, header.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        builder.setSpan(labelColorSpan, 0, header.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }

    builder.append(body)

    return builder
}


// ---------------------------------------------------------------------------------------------
// | PARAGRAPH HEADER STYLE
// ---------------------------------------------------------------------------------------------

/**
 * Paragraph Header Style
 *
 * Small and legible body text.
 */
private fun textWidgetMetricParagraphHeaderView(
        data : TextWidgetViewData,
        variations : List<WidgetStyleVariation>,
        theme : Theme,
        groupContext : Maybe<GroupContext>,
        context : Context
) : View
{
    val layout = textWidgetMetricParagraphHeaderViewLayout(context)

    val valueView = textWidgetMetricParagraphHeaderTextView(data, variations, theme, groupContext, context)
    layout.addView(valueView)

    return layout
}

private fun textWidgetMetricParagraphHeaderViewLayout(
        context : Context
) : LinearLayout
{
    val layoutBuilder           = LinearLayoutBuilder()

    layoutBuilder.width         = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height        = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation   = LinearLayout.VERTICAL

    return layoutBuilder.linearLayout(context)
}



private fun textWidgetMetricParagraphHeaderTextView(
        data : TextWidgetViewData,
        variations : List<WidgetStyleVariation>,
        theme : Theme,
        groupContext : Maybe<GroupContext>,
        context : Context
) : TextView
{
    val textViewBuilder = TextViewBuilder()

    textViewBuilder.width           = LinearLayout.LayoutParams.MATCH_PARENT
    textViewBuilder.height          = LinearLayout.LayoutParams.WRAP_CONTENT

    textViewBuilder.text            = data.value

    textViewBuilder.font            = Font.typeface(TextFont.RobotoSlab,
                                                    TextFontStyle.Bold,
                                                    context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    textViewBuilder.color           = theme.colorOrBlack(colorTheme)

    if (variations.isNotEmpty()) {
        val variation = variations[0]
        when (variation.value) {
            "small" -> {
                textViewBuilder.sizeSp = 15.7f
            }
            "normal" -> {
                textViewBuilder.sizeSp = 17f
            }
            "large" -> {
                textViewBuilder.sizeSp = 21f
            }
        }
    }

    textViewBuilder.lineSpacingAdd  = 8f
    textViewBuilder.lineSpacingMult = 1f

    return textViewBuilder.textView(context)
}




// ---------------------------------------------------------------------------------------------
// | VERTICAL BOX STYLE
// ---------------------------------------------------------------------------------------------

/**
 * Vertical Box
 */
private fun textWidgetMetricVerticalBoxView(
        data : TextWidgetViewData,
        variations : List<WidgetStyleVariation>,
        theme : Theme,
        groupContext : Maybe<GroupContext>,
        context : Context
) : LinearLayout
{
    val layout = textWidgetMetricVerticalBoxViewLayout(context)

    layout.addView(textWidgetMetricVerticalBoxLabelView(data, theme, context))
    layout.addView(textWidgetMetricVerticalBoxValueView(data, variations, theme, groupContext, context))

    return layout
}


/**
 * Vertical Box Layout
 */
private fun textWidgetMetricVerticalBoxViewLayout(
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
private fun textWidgetMetricVerticalBoxLabelView(
        data : TextWidgetViewData,
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
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))
    layoutBuilder.backgroundColor   = theme.colorOrBlack(bgColorTheme)

    layoutBuilder.corners   = Corners(4.0, 4.0, 0.0, 0.0)

    layoutBuilder.child(labelViewBuilder)

    // | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width      = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height     = LinearLayout.LayoutParams.WRAP_CONTENT

    data.label.doMaybe {
        labelViewBuilder.text = it.toUpperCase()
    }

    labelViewBuilder.font       = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)
    val textColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_20"))))
    labelViewBuilder.color      = theme.colorOrBlack(textColorTheme)

    labelViewBuilder.sizeSp     = 11.5f

    return layoutBuilder.linearLayout(context)
}


/**
 * Vertical Box Value
 */
private fun textWidgetMetricVerticalBoxValueView(
        data : TextWidgetViewData,
        variations : List<WidgetStyleVariation>,
        theme : Theme,
        groupContext : Maybe<GroupContext>,
        context : Context
) : LinearLayout
{

    // | Declarations
    // -----------------------------------------------------------------------------------------

    val layoutBuilder       = LinearLayoutBuilder()
    val labelViewBuilder    = TextViewBuilder()

    // | Layout
    // -----------------------------------------------------------------------------------------

    layoutBuilder.width                 = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.heightDp              = 44

    layoutBuilder.padding.leftDp        = 8f
    layoutBuilder.padding.rightDp       = 8f

    layoutBuilder.gravity               = Gravity.CENTER_VERTICAL or Gravity.START

    if (WidgetStyleVariation("filled") in variations) {
        layoutBuilder.backgroundResource    = R.drawable.bg_style_vertical_box_filled
        layoutBuilder.margin.topDp      = 1f
    }
    else {
        layoutBuilder.backgroundResource    = R.drawable.bg_style_vertical_box
    }


    layoutBuilder.child(labelViewBuilder)

    // | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width      = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height     = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.text       = data.value

    labelViewBuilder.font       = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_11"))))
    labelViewBuilder.color           = theme.colorOrBlack(labelColorTheme)

    if (WidgetStyleVariation("large") in variations) {
        labelViewBuilder.sizeSp     = 19.2f
    }
    else {
        labelViewBuilder.sizeSp     = 17f
    }

    return layoutBuilder.linearLayout(context)
}


// ---------------------------------------------------------------------------------------------
// | ENTRY LABEL
// ---------------------------------------------------------------------------------------------

/**
 * Entity Section Label View
 */
private fun entitySectionLabelView(
        data : TextWidgetViewData,
        theme : Theme,
        groupContext : Maybe<GroupContext>,
        context : Context
) : TextView
{
    val textViewBuilder             = TextViewBuilder()

    textViewBuilder.width           = LinearLayout.LayoutParams.WRAP_CONTENT
    textViewBuilder.height          = LinearLayout.LayoutParams.WRAP_CONTENT

    textViewBuilder.text            = data.value

    textViewBuilder.font            = Font.typeface(TextFont.Roboto,
                                                    TextFontStyle.Medium,
                                                    context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
    textViewBuilder.color           = theme.colorOrBlack(colorTheme)

    textViewBuilder.sizeSp          = 17f

    textViewBuilder.lineSpacingAdd  = 8f
    textViewBuilder.lineSpacingMult = 1f

    //textViewBuilder.layoutGravity   = textWidget.widgetFormat().elementFormat().alignment().gravityConstant()

    return textViewBuilder.textView(context)
}


// ---------------------------------------------------------------------------------------------
// | ENTITY SECTION ENTRY TAG
// ---------------------------------------------------------------------------------------------

/**
 * Entity Section Label View
 */
private fun entitySectionEntryTagView(
        data : TextWidgetViewData,
        theme : Theme,
        groupContext : Maybe<GroupContext>,
        context : Context
) : TextView
{
    val textViewBuilder             = TextViewBuilder()

    textViewBuilder.width           = LinearLayout.LayoutParams.WRAP_CONTENT
    textViewBuilder.height          = LinearLayout.LayoutParams.WRAP_CONTENT

    textViewBuilder.corners         = Corners(2.0, 2.0, 2.0, 2.0)

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_5"))))
    textViewBuilder.backgroundColor = theme.colorOrBlack(bgColorTheme)

    textViewBuilder.padding.topDp       = 4f
    textViewBuilder.padding.bottomDp    = 4f
    textViewBuilder.padding.leftDp      = 8f
    textViewBuilder.padding.rightDp      = 8f

    textViewBuilder.text            = data.value

    textViewBuilder.font            = Font.typeface(TextFont.Roboto,
                                                    TextFontStyle.Medium,
                                                    context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
    textViewBuilder.color           = theme.colorOrBlack(colorTheme)

    textViewBuilder.sizeSp          = 13f

    textViewBuilder.lineSpacingAdd  = 8f
    textViewBuilder.lineSpacingMult = 1f

    //textViewBuilder.layoutGravity   = textWidget.widgetFormat().elementFormat().alignment().gravityConstant()

    return textViewBuilder.textView(context)
}


// ---------------------------------------------------------------------------------------------
// | HORIZONTAL BOX STYLE
// ---------------------------------------------------------------------------------------------

/**
 * Horizontal box
 */
private fun horizontalBoxView(
        data : TextWidgetViewData,
        variation : List<WidgetStyleVariation>,
        theme : Theme,
        groupContext : Maybe<GroupContext>,
        context : Context
) : LinearLayout
{
    val layout = horizontalBoxViewLayout(context)

    val maybeLabel = data.label
    when (maybeLabel) {
        is Just -> {
            layout.addView(horizontalBoxLabelView(maybeLabel.value, variation, theme, context))
            layout.addView(horizontalBoxValueView(data, variation, theme, false, groupContext, context))
        }
        is Nothing -> {
            layout.addView(horizontalBoxValueView(data, variation, theme, true, groupContext, context))
        }
    }

    return layout
}



/**
 * Horizontal box
 */
private fun horizontalBoxViewLayout(
        context : Context
) : LinearLayout
{
    val layoutBuilder           = LinearLayoutBuilder()

    layoutBuilder.width         = LinearLayout.LayoutParams.WRAP_CONTENT
    layoutBuilder.height        = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation   = LinearLayout.HORIZONTAL


    return layoutBuilder.linearLayout(context)
}


/**
 * Horizontal box
 */
private fun horizontalBoxLabelView(
        label : String,
        variations : List<WidgetStyleVariation>,
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

    layoutBuilder.width     = LinearLayout.LayoutParams.WRAP_CONTENT
    layoutBuilder.heightDp  = 44

    layoutBuilder.gravity   = Gravity.CENTER

    layoutBuilder.padding.leftDp    = 12f
    layoutBuilder.padding.rightDp    = 12f

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))
    layoutBuilder.backgroundColor   = theme.colorOrBlack(bgColorTheme)

    layoutBuilder.corners   = Corners(4.0, 0.0, 0.0, 4.0)

    layoutBuilder.child(labelViewBuilder)

    // | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width      = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height     = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.text = label

    labelViewBuilder.font       = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_20"))))
    labelViewBuilder.color           = theme.colorOrBlack(labelColorTheme)

    if (variations.size > 0) {
        val variation = variations[0]
        when (variation.value) {
            "normal" -> {
                labelViewBuilder.sizeSp = 17f
            }
            "large" -> {
                labelViewBuilder.sizeSp = 21f
            }
        }
    }

    return layoutBuilder.linearLayout(context)
}


/**
 * Horizontal box
 */
private fun horizontalBoxValueView(
        data : TextWidgetViewData,
        variations : List<WidgetStyleVariation>,
        theme : Theme,
        withoutLabel : Boolean,
        groupContext : Maybe<GroupContext>,
        context : Context
) : LinearLayout
{

    // | Declarations
    // -----------------------------------------------------------------------------------------

    val layoutBuilder       = LinearLayoutBuilder()
    val labelViewBuilder    = TextViewBuilder()

    // | Layout
    // -----------------------------------------------------------------------------------------

    layoutBuilder.width     = LinearLayout.LayoutParams.WRAP_CONTENT

    if (variations.size > 0)
    {
        val variation = variations[0]
        when (variation.value) {
            "normal" -> {
                layoutBuilder.heightDp  = 38
            }
            "large" -> {
                layoutBuilder.heightDp  = 44
            }
        }
    }

    layoutBuilder.padding.leftDp    = 12f
    layoutBuilder.padding.rightDp    = 12f

    layoutBuilder.gravity   = Gravity.CENTER


    if (withoutLabel) {
        layoutBuilder.backgroundResource = R.drawable.bg_style_horizontal_box_without_label
    }
    else {
        layoutBuilder.backgroundResource = R.drawable.bg_style_horizontal_box
    }

    layoutBuilder.child(labelViewBuilder)

    // | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width      = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height     = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.text       = data.value

    labelViewBuilder.font       = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
            //ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
    labelViewBuilder.color           = theme.colorOrBlack(labelColorTheme)
//    labelViewBuilder.color           = Color.WHITE


    if (variations.size > 0)
    {
        val variation = variations[0]
        when (variation.value) {
            "normal" -> {
                labelViewBuilder.sizeSp     = 16f
            }
            "large" -> {
                labelViewBuilder.sizeSp     = 21f
            }
        }
    }

    return layoutBuilder.linearLayout(context)
}

