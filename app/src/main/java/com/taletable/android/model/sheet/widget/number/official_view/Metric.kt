
package com.taletable.android.model.sheet.widget.number.official_view


import android.content.Context
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
import com.taletable.android.model.sheet.widget.*
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
fun numberWidgetOfficialMetricView(
        style : WidgetStyle,
        variations : List<WidgetStyleVariation>,
        numberWidget : NumberWidget,
        entityId : EntityId,
        context : Context,
        groupContext : Maybe<GroupContext>
) : View = when (style.value)
{
    "horizontal_box" -> numberWidgetMetricHorizontalBoxView(numberWidget, entityId, groupContext, context)
    "vertical_box"   -> numberWidgetMetricVerticalBoxView(numberWidget, variations, entityId, groupContext, context)
    "entity_section_tag" -> entitySectionEntryTagView(numberWidget, entityId, groupContext, context)
    "entity_section_label_tag" -> entitySectionLabelTagView(numberWidget, entityId, groupContext, context)
    else             -> numberWidgetMetricHorizontalBoxView(numberWidget, entityId, groupContext, context)
}




// ---------------------------------------------------------------------------------------------
// | HORIZONTAL BOX STYLE
// ---------------------------------------------------------------------------------------------

/**
 * Horizontal box
 */
private fun numberWidgetMetricHorizontalBoxView(
        numberWidget : NumberWidget,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : LinearLayout
{
    val layout = numberWidgetMetricHorizontalBoxViewLayout(context)

    layout.addView(numberWidgetMetricHorizontalBoxLabelView(numberWidget, entityId, context))
    layout.addView(numberWidgetMetricHorizontalBoxValueView(numberWidget, entityId, groupContext, context))

    return layout
}



/**
 * Horizontal box
 */
private fun numberWidgetMetricHorizontalBoxViewLayout(
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
private fun numberWidgetMetricHorizontalBoxLabelView(
        numberWidget : NumberWidget,
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

    numberWidget.labelValue(entityId).apDo {
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
private fun numberWidgetMetricHorizontalBoxValueView(
        numberWidget : NumberWidget,
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
    //layoutBuilder.height    = LinearLayout.LayoutParams.WRAP_CONTENT
    layoutBuilder.heightDp  = 44

    layoutBuilder.padding.leftDp    = 12f
    layoutBuilder.padding.rightDp    = 12f

    layoutBuilder.gravity   = Gravity.CENTER
//    layoutBuilder.padding.topDp     = 4f
//    layoutBuilder.padding.bottomDp  = 4f
//    layoutBuilder.padding.leftDp    = 4f
//    layoutBuilder.padding.rightDp   = 4f

//    val bgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_20"))))
//    layoutBuilder.backgroundColor   = colorOrBlack(bgColorTheme, entityId)
    //layoutBuilder.backgroundColor   = Color.WHITE
    layoutBuilder.backgroundResource    = R.drawable.bg_style_horizontal_box

//    layoutBuilder.corners   = Corners(4.0, 0.0, 0.0, 4.0)

    layoutBuilder.child(labelViewBuilder)

    // | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width      = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height     = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.text       = numberWidget.value(entityId, groupContext).let {
                                      numberWidget.kind.formattedString(it)
                                  }

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
// | VERTICAL BOX STYLE
// ---------------------------------------------------------------------------------------------

/**
 * Vertical Box
 */
private fun numberWidgetMetricVerticalBoxView(
        numberWidget : NumberWidget,
        variations : List<WidgetStyleVariation>,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : LinearLayout
{
    val layout = numberWidgetMetricVerticalBoxViewLayout(context)

    layout.addView(numberWidgetMetricVerticalBoxLabelView(numberWidget, entityId, context))

    val valueLayout = numberWidgetMetricVerticalBoxValueViewLayout(variations, context)
    valueLayout.addView(numberWidgetMetricVerticalBoxValueView(numberWidget, variations, entityId, groupContext, context))
    valueLayout.addView(numberWidgetMetricVerticalBoxPostfixView(numberWidget, entityId, context))
    layout.addView(valueLayout)

    return layout
}


/**
 * Vertical Box Layout
 */
private fun numberWidgetMetricVerticalBoxViewLayout(
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
private fun numberWidgetMetricVerticalBoxLabelView(
        numberWidget : NumberWidget,
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

    numberWidget.labelValue(entityId).apDo {
        labelViewBuilder.text = it.toUpperCase()
    }

    labelViewBuilder.font       = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)
    val textColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_20"))))
    labelViewBuilder.color      = colorOrBlack(textColorTheme, entityId)

    labelViewBuilder.sizeSp     = 11f

    return layoutBuilder.linearLayout(context)
}


/**
 * Vertical Box Value
 */
private fun numberWidgetMetricVerticalBoxValueViewLayout(
        variations : List<WidgetStyleVariation>,
        context : Context
) : LinearLayout
{
    val layoutBuilder       = LinearLayoutBuilder()

    layoutBuilder.width     = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.heightDp  = 44

    layoutBuilder.padding.leftDp    = 8f
    layoutBuilder.padding.rightDp    = 8f

    layoutBuilder.gravity   = Gravity.CENTER_VERTICAL or Gravity.START

    if (WidgetStyleVariation("filled") in variations) {
        layoutBuilder.backgroundResource = R.drawable.bg_style_vertical_box_filled
        layoutBuilder.margin.topDp = 1f

    } else {
        layoutBuilder.backgroundResource = R.drawable.bg_style_vertical_box
    }

    return layoutBuilder.linearLayout(context)
}

/**
 * Vertical Box Value
 */
private fun numberWidgetMetricVerticalBoxValueView(
        numberWidget : NumberWidget,
        variations : List<WidgetStyleVariation>,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : TextView
{

    val labelViewBuilder    = TextViewBuilder()

    labelViewBuilder.width      = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height     = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.text       = numberWidget.value(entityId, groupContext).let {
                                     numberWidget.kind.formattedString(it)
                                 }

    labelViewBuilder.font       = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
    labelViewBuilder.color           = colorOrBlack(labelColorTheme, entityId)
//    labelViewBuilder.color           = Color.WHITE

    if (WidgetStyleVariation("large") in variations) {
        labelViewBuilder.sizeSp = 21f
    } else {
        labelViewBuilder.sizeSp = 17f
    }


    return labelViewBuilder.textView(context)
}


/**
 * Vertical Box Postfix Value
 */
private fun numberWidgetMetricVerticalBoxPostfixView(
        numberWidget : NumberWidget,
        entityId : EntityId,
        context : Context
) : TextView
{

    val labelViewBuilder            = TextViewBuilder()

    labelViewBuilder.width          = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height         = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.margin.leftDp  = 4f

    numberWidget.postfixValue(entityId).apDo {
        labelViewBuilder.text = it
    }

    labelViewBuilder.font       = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
            //ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_20"))))
    labelViewBuilder.color           = colorOrBlack(labelColorTheme, entityId)
//    labelViewBuilder.color           = Color.WHITE

    labelViewBuilder.sizeSp     = 23f

    return labelViewBuilder.textView(context)
}


// ---------------------------------------------------------------------------------------------
// | ENTITY SECTION ENTRY TAG
// ---------------------------------------------------------------------------------------------

/**
 * Entity Section Label View
 */
private fun entitySectionEntryTagView(
        numberWidget : NumberWidget,
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

    var valueText = numberWidget.value(entityId, groupContext).let {
        numberWidget.kind.formattedString(it)
    }

    numberWidget.prefixValue(entityId).apDo {
        valueText = "$it $valueText"
    }

    textViewBuilder.text            = valueText

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

    textViewBuilder.layoutGravity   = numberWidget.widgetFormat().elementFormat().alignment().gravityConstant()

    return textViewBuilder.textView(context)
}


// ---------------------------------------------------------------------------------------------
// | ENTITY SECTION LABEL TAG
// ---------------------------------------------------------------------------------------------

private fun entitySectionLabelTagView(
        numberWidget : NumberWidget,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : LinearLayout
{
    val layout = entitySectionLabelTagViewLayout(numberWidget.widgetFormat(), context)

    numberWidget.prefixValue(entityId).apDo {
        layout.addView(entitySectionLabelTagPrefixView(it, entityId, context))
    }

    numberWidget.value(entityId, groupContext).let {
        val valueText = numberWidget.kind.formattedString(it)
        layout.addView(entitySectionLabelTagValueView(valueText, entityId, context))
    }

    return layout
}


/**
 * Entity Section Label Tag Layout
 */
private fun entitySectionLabelTagViewLayout(
        widgetFormat : WidgetFormat,
        context : Context
) : LinearLayout
{
    val layoutBuilder               = LinearLayoutBuilder()

    layoutBuilder.width             = LinearLayout.LayoutParams.WRAP_CONTENT
    layoutBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation       = LinearLayout.HORIZONTAL

//    layoutBuilder.padding.leftDp    = 8f
//    layoutBuilder.padding.rightDp   = 8f

    layoutBuilder.layoutGravity     = widgetFormat.elementFormat().alignment().gravityConstant()
//    layoutBuilder.backgroundResource    = R.drawable.bg_style_vertical_box

    return layoutBuilder.linearLayout(context)
}

/**
 * Entity Section Label Tag View
 */
private fun entitySectionLabelTagPrefixView(
        prefix : String,
        entityId : EntityId,
        context : Context
) : TextView
{
    val textViewBuilder             = TextViewBuilder()

    textViewBuilder.width           = LinearLayout.LayoutParams.WRAP_CONTENT
    textViewBuilder.height          = LinearLayout.LayoutParams.WRAP_CONTENT

    textViewBuilder.corners         = Corners(2.0, 0.0, 0.0, 2.0)

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))
    textViewBuilder.backgroundColor = colorOrBlack(bgColorTheme, entityId)

    textViewBuilder.padding.topDp       = 4f
    textViewBuilder.padding.bottomDp    = 4f
    textViewBuilder.padding.leftDp      = 8f
    textViewBuilder.padding.rightDp     = 8f

    textViewBuilder.text            = prefix

    textViewBuilder.font            = Font.typeface(TextFont.Roboto,
                                                    TextFontStyle.Medium,
                                                    context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
    textViewBuilder.color           = colorOrBlack(colorTheme, entityId)

    textViewBuilder.sizeSp          = 13f

    textViewBuilder.lineSpacingAdd  = 8f
    textViewBuilder.lineSpacingMult = 1f

    return textViewBuilder.textView(context)
}



/**
 * Entity Section Label Tag View
 */
private fun entitySectionLabelTagValueView(
        value : String,
        entityId : EntityId,
        context : Context
) : TextView
{
    val textViewBuilder             = TextViewBuilder()

    textViewBuilder.width           = LinearLayout.LayoutParams.WRAP_CONTENT
    textViewBuilder.height          = LinearLayout.LayoutParams.WRAP_CONTENT

    textViewBuilder.corners         = Corners(0.0, 2.0, 2.0, 0.0)

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
    textViewBuilder.backgroundColor = colorOrBlack(bgColorTheme, entityId)

    textViewBuilder.padding.topDp       = 4f
    textViewBuilder.padding.bottomDp    = 4f
    textViewBuilder.padding.leftDp      = 8f
    textViewBuilder.padding.rightDp     = 8f

    textViewBuilder.text            = value

    textViewBuilder.font            = Font.typeface(TextFont.Roboto,
                                                    TextFontStyle.Bold,
                                                    context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
    textViewBuilder.color           = colorOrBlack(colorTheme, entityId)

    textViewBuilder.sizeSp          = 13f

    textViewBuilder.lineSpacingAdd  = 8f
    textViewBuilder.lineSpacingMult = 1f

//    textViewBuilder.layoutGravity   = numberWidget.widgetFormat().elementFormat().alignment().gravityConstant()

    return textViewBuilder.textView(context)
}
