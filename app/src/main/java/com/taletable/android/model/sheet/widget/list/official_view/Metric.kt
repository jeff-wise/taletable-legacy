
package com.taletable.android.model.sheet.widget.list.official_view


import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.taletable.android.R
import com.taletable.android.app.ApplicationLog
import com.taletable.android.lib.ui.CustomTypefaceSpan
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
import com.taletable.android.util.Util
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
    "inline" -> inlineView(listWidget, entityId, groupContext, context)
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


/**
 * Inline Style
 */
private fun inlineView(
        listWidget : ListWidget,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : View
{
    val textViewBuilder         = TextViewBuilder()

    textViewBuilder.width       = LinearLayout.LayoutParams.MATCH_PARENT
    textViewBuilder.height      = LinearLayout.LayoutParams.WRAP_CONTENT

    textViewBuilder.font        = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Regular,
                                                context)
    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_12"))))
    textViewBuilder.color           = colorOrBlack(colorTheme, entityId)

    textViewBuilder.sizeSp          = 17f

    textViewBuilder.lineSpacingAdd  = 4f
    textViewBuilder.lineSpacingMult = 1f

    val builder = SpannableStringBuilder()

    val label = listWidget.labelValueOrBlank(entityId)
    val values = listWidget.valuesOrBlank(entityId, groupContext)

    builder.append(label)
    builder.append("  ")
    builder.append(values)

    val typeface = Font.typeface(TextFont.RobotoSlab, TextFontStyle.Bold, context)
    val typefaceSpan = CustomTypefaceSpan(typeface)
    builder.setSpan(typefaceSpan, 0, label.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

    val sizePx = Util.spToPx(15.5f, context)
    val sizeSpan = AbsoluteSizeSpan(sizePx)
    builder.setSpan(sizeSpan, label.length, label.length + values.length + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

    textViewBuilder.textSpan        = builder

    return textViewBuilder.textView(context)
}




//private fun inlineView(
//        listWidget : ListWidget,
//        entityId : EntityId,
//        groupContext : Maybe<GroupContext>,
//        context : Context
//) : View
//{
//    val paragraph           = TextViewBuilder()
//
//    paragraph.width         = LinearLayout.LayoutParams.MATCH_PARENT
//    paragraph.height        = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    val description = listWidget.description
//    val valueSetId = listWidget.variable(entityId).apply {
//                        note<AppError, ValueSetId>(it.valueSetId().toNullable(),
//                                                  AppStateError(VariableDoesNotHaveValueSet(it.variableId())))
//                     }
//    when (description) {
//        is Just -> {
//            when (valueSetId) {
//                is Val -> {
//                    val values = listWidget.value(entityId) ap { valueIds ->
//                                        valueIds.mapM { valueId ->
//                                            val valueRef = ValueReference(TextReferenceLiteral(valueSetId.value.value),
//                                                                          TextReferenceLiteral(valueId))
//                                            value(valueRef, entityId)
//                                        }
//                                 }
//                    when (values) {
//                        is Val -> {
//                            val valueStrings = values.value.map { it.valueString() }
//                            paragraph.textSpan = inlineViewSpannableString(description.value.value, valueStrings)
//                        }
//                        is Err -> ApplicationLog.error(values.error)
//                    }
//                }
//            }
//        }
//        is Nothing -> {
//            listWidget.value(entityId).apDo { valueStrings ->
//                val joinedStrings = valueStrings.joinToString()
//                paragraph.text = joinedStrings
//
//                paragraph.sizeSp    = 17f
//                paragraph.font      = Font.typeface(TextFont.RobotoSlab,
//                                                    TextFontStyle.Regular,
//                                                    context)
//                val paragraphColorTheme = ColorTheme(setOf(
//                        ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
//                        ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//                paragraph.color           = colorOrBlack(paragraphColorTheme, entityId)
//            }
//        }
//    }
//
//    paragraph.onClick       = View.OnClickListener {
//
//        val textListVariable =  listWidget.variable(entityId)
//        when (textListVariable) {
//            is Val -> {
//                openVariableEditorDialog(textListVariable.value,
//                                         null,
//                                         UpdateTargetListWidget(listWidget.widgetId()),
//                                         entityId,
//                                         context)
//            }
//        }
//
//    }
//
//    return paragraph.textView(context)
//}


//private fun inlineViewSpannableString(
//        listWidget : ListWidget,
//        description : String,
//        valueStrings : List<String>) : SpannableStringBuilder
//{
//    val builder = SpannableStringBuilder()
//    var currentIndex = 0
//
//    val parts = description.split("$$$")
//    val part1 : String = parts[0]
//
//    // > Part 1
//    builder.append(part1)
//
////    inlineViewFormatSpans(listWidget.format.descriptionFormat).forEach {
////        builder.setSpan(it, 0, part1.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
////    }
//
//    currentIndex += part1.length
//
//    val items = valueStrings.take(valueStrings.size - 1)
//    val lastItem = valueStrings.elementAt(valueStrings.size - 1)
//
//    // > Items
//    items.forEach { item ->
//        builder.append(item)
//
//        this.formatSpans(listWidget.format.itemFormat).forEach {
//            builder.setSpan(it, currentIndex, currentIndex + item.length, SPAN_INCLUSIVE_EXCLUSIVE)
//        }
//
//        currentIndex += item.length
//
//        if (items.size > 1) {
//            builder.append(", ")
//
//            inlineViewFormatSpans(listWidget.format.descriptionFormat).forEach {
//                builder.setSpan(it, currentIndex, currentIndex + 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//            }
//
//            currentIndex += 2
//        }
//    }
//
//    if (items.size == 1)
//    {
//        builder.append(" and ")
//
//        inlineViewFormatSpans(listWidget.format.descriptionFormat).forEach {
//            builder.setSpan(it, currentIndex, currentIndex + 5, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//        }
//
//        currentIndex += 5
//    }
//    else if (items.size > 1)
//    {
//        builder.append("and ")
//
//        inlineViewFormatSpans(listWidget.format.descriptionFormat).forEach {
//            builder.setSpan(it, currentIndex, currentIndex + 4, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//        }
//
//        currentIndex += 4
//
//    }
//
//    builder.append(lastItem)
//
//    inlineViewFormatSpans(listWidget.format.itemFormat).forEach {
//        builder.setSpan(it, currentIndex, currentIndex + lastItem.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//    }
//
//    return builder
//}


//private fun inlineViewFormatSpans(textFormat : TextFormat) : List<Any>
//{
//    val sizePx = Util.spToPx(textFormat.sizeSp(), context)
//    val sizeSpan = AbsoluteSizeSpan(sizePx)
//
//    val typeface = Font.typeface(textFormat.font(), textFormat.fontStyle(), context)
//
//    val typefaceSpan = CustomTypefaceSpan(typeface)
//
//    var color = colorOrBlack(textFormat.colorTheme(), entityId)
//    val colorSpan = ForegroundColorSpan(color)
//
//    var bgColor = colorOrBlack(textFormat.elementFormat().backgroundColorTheme(), entityId)
//    val bgColorSpan = BackgroundColorSpan(bgColor)
//
//    return listOf(sizeSpan, typefaceSpan, colorSpan, bgColorSpan)
//}


