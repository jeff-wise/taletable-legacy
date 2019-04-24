
package com.taletable.android.model.sheet.widget.number.official_view


import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.taletable.android.R
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.NumberFormat
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.sheet.widget.NumberWidget
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
fun numberWidgetOfficialMetricView(
        style : WidgetStyle,
        numberWidget : NumberWidget,
        entityId : EntityId,
        context : Context,
        groupContext : Maybe<GroupContext>
) : View = when (style.value)
{
    "horizontal_box" -> numberWidgetMetricHorizontalBoxView(numberWidget, entityId, groupContext, context)
    "vertical_box"   -> numberWidgetMetricVerticalBoxView(numberWidget, entityId, groupContext, context)
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
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
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

    numberWidget.prefixValue(entityId).apDo {
        labelViewBuilder.text = it
    }

    labelViewBuilder.font       = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_18"))))
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

    labelViewBuilder.sizeSp     = 26f

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
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : LinearLayout
{
    val layout = numberWidgetMetricVerticalBoxViewLayout(context)

    layout.addView(numberWidgetMetricVerticalBoxLabelView(numberWidget, entityId, context))
    layout.addView(numberWidgetMetricVerticalBoxValueView(numberWidget, entityId, groupContext, context))

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
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
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
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_18"))))
    labelViewBuilder.color      = colorOrBlack(textColorTheme, entityId)

    labelViewBuilder.sizeSp     = 12f

    return layoutBuilder.linearLayout(context)
}


/**
 * Vertical Box Value
 */
private fun numberWidgetMetricVerticalBoxValueView(
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

    layoutBuilder.width     = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.heightDp  = 44

    layoutBuilder.padding.leftDp    = 8f
    layoutBuilder.padding.rightDp    = 8f

    layoutBuilder.gravity   = Gravity.CENTER_VERTICAL or Gravity.START
//    layoutBuilder.padding.topDp     = 4f
//    layoutBuilder.padding.bottomDp  = 4f
//    layoutBuilder.padding.leftDp    = 4f
//    layoutBuilder.padding.rightDp   = 4f

//    val bgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_20"))))
//    layoutBuilder.backgroundColor   = colorOrBlack(bgColorTheme, entityId)
    //layoutBuilder.backgroundColor   = Color.WHITE
    layoutBuilder.backgroundResource    = R.drawable.bg_style_vertical_box

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

    labelViewBuilder.sizeSp     = 26f

    return layoutBuilder.linearLayout(context)
}
