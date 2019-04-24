
package com.taletable.android.model.sheet.widget.text.official_view


import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
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
import maybe.Maybe



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
    "paragraph"    -> textWidgetMetricParagraphView(textWidget, entityId, groupContext, context)
    "vertical_box" -> textWidgetMetricVerticalBoxView(textWidget, entityId, groupContext, context)
    else           -> textWidgetMetricParagraphView(textWidget, entityId, groupContext, context)
}


// ---------------------------------------------------------------------------------------------
// | PARAGRAPH STYLE
// ---------------------------------------------------------------------------------------------

/**
 * Paragraph Style
 *
 * Small and legible body text.
 */
private fun textWidgetMetricParagraphView(
        textWidget : TextWidget,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : View
{
    val valueString = textWidget.valueString(entityId, groupContext)
    val paragraphs = valueString.split("\n")

    return if (paragraphs.isEmpty()) {
        textWidgetMetricParagraphTextView(textWidget, false, entityId, groupContext, context)
    }
    else {
        textWidgetMetricParagraphViewLayout(context).also { it
            paragraphs.forEachIndexed { index, s ->
                val isParagraph = index < paragraphs.size - 1
                val view = textWidgetMetricParagraphTextView(
                               textWidget,
                               isParagraph,
                               entityId,
                               groupContext,
                               context)
                it.addView(view)
            }
        }
    }
}

private fun textWidgetMetricParagraphViewLayout(
        context : Context
) : LinearLayout
{
    val layoutBuilder           = LinearLayoutBuilder()

    layoutBuilder.width         = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height        = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation   = LinearLayout.VERTICAL

    return layoutBuilder.linearLayout(context)
}



private fun textWidgetMetricParagraphTextView(
        textWidget : TextWidget,
        isParagraph : Boolean,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : TextView
{
    val textViewBuilder = TextViewBuilder()

    textViewBuilder.width           = LinearLayout.LayoutParams.MATCH_PARENT
    textViewBuilder.height          = LinearLayout.LayoutParams.WRAP_CONTENT

    if (isParagraph)
        textViewBuilder.margin.bottomDp = 8f

    textViewBuilder.text            = textWidget.valueString(entityId, groupContext)

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
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
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
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_18"))))
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

    labelViewBuilder.sizeSp     = 26f

    return layoutBuilder.linearLayout(context)
}
