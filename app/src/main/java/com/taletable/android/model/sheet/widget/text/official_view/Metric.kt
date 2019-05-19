
package com.taletable.android.model.sheet.widget.text.official_view


import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
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
import com.taletable.android.model.sheet.widget.TextWidget
import com.taletable.android.model.sheet.widget.WidgetStyle
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import effect.Err
import effect.Val
import maybe.Just
import maybe.Maybe
import maybe.Nothing


/**
 * Metric View
 */
fun textWidgetOfficialMetricView(
        style : WidgetStyle,
        textWidget : TextWidget,
        entityId : EntityId,
        context : Context,
        groupContext : Maybe<GroupContext>
) : View = when (style.value)
{
    "paragraph"        -> paragraphView(textWidget, entityId, groupContext, context)
    "paragraph_header" -> textWidgetMetricParagraphHeaderView(textWidget, entityId, groupContext, context)
    "vertical_box"     -> textWidgetMetricVerticalBoxView(textWidget, entityId, groupContext, context)
    "horizontal_box"   -> horizontalBoxView(textWidget, entityId, groupContext, context)
    "entity_section_label" -> entitySectionLabelView(textWidget, entityId, groupContext, context)
    "entity_section_tag" -> entitySectionEntryTagView(textWidget, entityId, groupContext, context)
    else               -> paragraphView(textWidget, entityId, groupContext, context)
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
        textWidget : TextWidget,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : View
{
    val valueString = textWidget.valueString(entityId, groupContext)

    // Get paragraphs
    var paragraphs = listOf<String>()
    paragraphs = valueString.split("\n")
    if (paragraphs.isEmpty()) {
        paragraphs = listOf(valueString)
    }

    // Get label
    val labelString = textWidget.labelValue(entityId)

    val layout = paragraphViewLayout(context)


    // Add first paragraph
    paragraphs.firstOrNull()?.let { paragraph ->
        when (labelString) {
            is Val -> {
                layout.addView(paragraphTextView(labelString.value, paragraph, false, entityId, context))
            }
            is Err -> {
                layout.addView(paragraphTextView(null, paragraph, false, entityId, context))
            }
        }

    }

    paragraphs.drop(1).forEach {
        paragraphViewLayout(context).also { it
            paragraphs.forEachIndexed { index, paragraphString ->
                val isParagraph = index < paragraphs.size - 1
                val view = paragraphTextView(
                                null,
                               paragraphString,
                               isParagraph,
                               entityId,
                               context)
                it.addView(view)
            }
        }
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
        entityId : EntityId,
        context : Context
) : TextView
{
    val textViewBuilder = TextViewBuilder()

    textViewBuilder.width           = LinearLayout.LayoutParams.MATCH_PARENT
    textViewBuilder.height          = LinearLayout.LayoutParams.WRAP_CONTENT

    if (isParagraph)
        textViewBuilder.margin.bottomDp = 16f

    textViewBuilder.textSpan        = paragraphSpannable(header, body, context)

    textViewBuilder.font            = Font.typeface(TextFont.RobotoSlab,
                                                    TextFontStyle.Regular,
                                                    context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    textViewBuilder.color           = colorOrBlack(colorTheme, entityId)

    textViewBuilder.sizeSp          = 15.7f

    textViewBuilder.lineSpacingAdd  = 8f
    textViewBuilder.lineSpacingMult = 1.1f

    return textViewBuilder.textView(context)
}


private fun paragraphSpannable(
        header : String?,
        body : String,
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
        textWidget : TextWidget,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : View
{
    val layout = textWidgetMetricParagraphHeaderViewLayout(context)

    val valueView = textWidgetMetricParagraphHeaderTextView(textWidget, entityId, groupContext, context)
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
        textWidget : TextWidget,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : TextView
{
    val textViewBuilder = TextViewBuilder()

    textViewBuilder.width           = LinearLayout.LayoutParams.MATCH_PARENT
    textViewBuilder.height          = LinearLayout.LayoutParams.WRAP_CONTENT

    textViewBuilder.text            = textWidget.valueString(entityId, groupContext)

    textViewBuilder.font            = Font.typeface(TextFont.RobotoSlab,
                                                    TextFontStyle.Bold,
                                                    context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    textViewBuilder.color           = colorOrBlack(colorTheme, entityId)

    textViewBuilder.sizeSp          = 20f

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
        textWidget : TextWidget,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : LinearLayout
{
    val layout = textWidgetMetricVerticalBoxViewLayout(context)

    layout.addView(textWidgetMetricVerticalBoxLabelView(textWidget, entityId, context))
    layout.addView(textWidgetMetricVerticalBoxValueView(textWidget, entityId, groupContext, context))

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
        textWidget : TextWidget,
        entityId : EntityId,
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
    layoutBuilder.backgroundColor   = colorOrBlack(bgColorTheme, entityId)

    layoutBuilder.corners   = Corners(4.0, 4.0, 0.0, 0.0)

    layoutBuilder.child(labelViewBuilder)

    // | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width      = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height     = LinearLayout.LayoutParams.WRAP_CONTENT

    textWidget.labelValue(entityId).apDo {
        labelViewBuilder.text = it.toUpperCase()
    }

    labelViewBuilder.font       = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)
    val textColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_20"))))
    labelViewBuilder.color      = colorOrBlack(textColorTheme, entityId)

    labelViewBuilder.sizeSp     = 12f

    return layoutBuilder.linearLayout(context)
}


/**
 * Vertical Box Value
 */
private fun textWidgetMetricVerticalBoxValueView(
        textWidget : TextWidget,
        entityId : EntityId,
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

    layoutBuilder.width     = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.heightDp  = 44

    layoutBuilder.padding.leftDp    = 8f
    layoutBuilder.padding.rightDp    = 8f

    layoutBuilder.gravity   = Gravity.CENTER_VERTICAL or Gravity.START
    layoutBuilder.backgroundResource    = R.drawable.bg_style_vertical_box

    layoutBuilder.child(labelViewBuilder)

    // | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width      = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height     = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.text       = textWidget.valueString(entityId, groupContext)

    labelViewBuilder.font       = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
    labelViewBuilder.color           = colorOrBlack(labelColorTheme, entityId)
//    labelViewBuilder.color           = Color.WHITE

    labelViewBuilder.sizeSp     = 22f

    return layoutBuilder.linearLayout(context)
}


// ---------------------------------------------------------------------------------------------
// | ENTRY LABEL
// ---------------------------------------------------------------------------------------------

/**
 * Entity Section Label View
 */
private fun entitySectionLabelView(
        textWidget : TextWidget,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : TextView
{
    val textViewBuilder             = TextViewBuilder()

    textViewBuilder.width           = LinearLayout.LayoutParams.WRAP_CONTENT
    textViewBuilder.height          = LinearLayout.LayoutParams.WRAP_CONTENT

    textViewBuilder.text            = textWidget.valueString(entityId, groupContext)

    textViewBuilder.font            = Font.typeface(TextFont.Roboto,
                                                    TextFontStyle.Medium,
                                                    context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
    textViewBuilder.color           = colorOrBlack(colorTheme, entityId)

    textViewBuilder.sizeSp          = 17.5f

    textViewBuilder.lineSpacingAdd  = 8f
    textViewBuilder.lineSpacingMult = 1f

    textViewBuilder.layoutGravity   = textWidget.widgetFormat().elementFormat().alignment().gravityConstant()

    return textViewBuilder.textView(context)
}


// ---------------------------------------------------------------------------------------------
// | ENTITY SECTION ENTRY TAG
// ---------------------------------------------------------------------------------------------

/**
 * Entity Section Label View
 */
private fun entitySectionEntryTagView(
        textWidget : TextWidget,
        entityId : EntityId,
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
    textViewBuilder.backgroundColor = colorOrBlack(bgColorTheme, entityId)

    textViewBuilder.padding.topDp       = 4f
    textViewBuilder.padding.bottomDp    = 4f
    textViewBuilder.padding.leftDp      = 8f
    textViewBuilder.padding.rightDp      = 8f

    textViewBuilder.text            = textWidget.valueString(entityId, groupContext) // .toUpperCase()

    textViewBuilder.font            = Font.typeface(TextFont.Roboto,
                                                    TextFontStyle.Medium,
                                                    context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
    textViewBuilder.color           = colorOrBlack(colorTheme, entityId)

    textViewBuilder.sizeSp          = 13f

    textViewBuilder.lineSpacingAdd  = 8f
    textViewBuilder.lineSpacingMult = 1f

    textViewBuilder.layoutGravity   = textWidget.widgetFormat().elementFormat().alignment().gravityConstant()

    return textViewBuilder.textView(context)
}


// ---------------------------------------------------------------------------------------------
// | HORIZONTAL BOX STYLE
// ---------------------------------------------------------------------------------------------

/**
 * Horizontal box
 */
private fun horizontalBoxView(
        textWidget : TextWidget,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : LinearLayout
{
    val layout = horizontalBoxViewLayout(context)

    layout.addView(horizontalBoxLabelView(textWidget, entityId, context))
    layout.addView(horizontalBoxValueView(textWidget, entityId, groupContext, context))

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
        textWidget : TextWidget,
        entityId : EntityId,
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
    layoutBuilder.backgroundColor   = colorOrBlack(bgColorTheme, entityId)

    layoutBuilder.corners   = Corners(4.0, 0.0, 0.0, 4.0)

//    layoutBuilder.padding.topDp     = 4f
//    layoutBuilder.padding.bottomDp  = 4f
//    layoutBuilder.padding.leftDp    = 4f
//    layoutBuilder.padding.rightDp   = 4f

    layoutBuilder.child(labelViewBuilder)

    // | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width      = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height     = LinearLayout.LayoutParams.WRAP_CONTENT

    textWidget.labelValue(entityId).apDo {
        labelViewBuilder.text = it
    }

    labelViewBuilder.font       = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_20"))))
    labelViewBuilder.color           = colorOrBlack(labelColorTheme, entityId)
    //labelViewBuilder.color           = Color.WHITE

    labelViewBuilder.sizeSp     = 20f

    return layoutBuilder.linearLayout(context)
}


/**
 * Horizontal box
 */
private fun horizontalBoxValueView(
        textWidget : TextWidget,
        entityId : EntityId,
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
    layoutBuilder.heightDp  = 44

    layoutBuilder.padding.leftDp    = 12f
    layoutBuilder.padding.rightDp    = 12f

    layoutBuilder.gravity   = Gravity.CENTER

    layoutBuilder.backgroundResource    = R.drawable.bg_style_horizontal_box

    layoutBuilder.child(labelViewBuilder)

    // | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width      = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height     = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.text       = textWidget.valueString(entityId, groupContext)

    labelViewBuilder.font       = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
    labelViewBuilder.color           = colorOrBlack(labelColorTheme, entityId)
//    labelViewBuilder.color           = Color.WHITE

    labelViewBuilder.sizeSp     = 22f

    return layoutBuilder.linearLayout(context)
}

