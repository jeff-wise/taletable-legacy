
package com.taletable.android.model.sheet.widget.list.official_view


import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.app.ApplicationLog
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.sheet.widget.ListWidget
import com.taletable.android.model.sheet.widget.WidgetStyle
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import effect.Err
import effect.Val
import maybe.Maybe



/**
 * Metric View
 */
fun listWidgetOfficialMetricView(
        style : WidgetStyle,
        listWidget : ListWidget,
        entityId : EntityId,
        context : Context,
        groupContext : Maybe<GroupContext>
) : View = when (style.value)
{
    "rows" -> rowsView(listWidget, entityId, groupContext, context)
    else   -> rowsView(listWidget, entityId, groupContext, context)
}



// ---------------------------------------------------------------------------------------------
// | ROWS STYLE
// ---------------------------------------------------------------------------------------------


/**
 * Paragraph Style
 */
private fun rowsView(
        listWidget : ListWidget,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : View
{
    val layout = rowsViewLayout(context)

    val label = listWidget.labelValue(entityId)
    label.apDo {
        layout.addView(rowsLabelView(it, entityId, context))
    }

    layout.addView(rowsValuesView(listWidget, entityId, groupContext, context))

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

    labelViewBuilder.text       = label

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



private fun rowsValuesView(
        listWidget : ListWidget,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : LinearLayout
{
    val layout = rowsValuesLayout(context)

    val itemStrings = listWidget.valueIdStrings(entityId, groupContext)
    when (itemStrings)
    {
        is Val -> {
            itemStrings.value.forEachIndexed { index, s ->
                val hasDivider = index > 0
                val rowView = rowsValueView(s, hasDivider, entityId, context)
                layout.addView(rowView)
            }
        }
        is Err -> {
            ApplicationLog.error(itemStrings.error)
        }
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
        entityId : EntityId,
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
    dividerViewBuilder.backgroundColor  = colorOrBlack(dividerColorTheme, entityId)

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
    labelViewBuilder.color           = colorOrBlack(labelColorTheme, entityId)

    labelViewBuilder.sizeSp     = 18f

    return layoutBuilder.linearLayout(context)
}


