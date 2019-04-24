
package com.taletable.android.model.sheet.widget.number


import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.widget.NumberWidget
import com.taletable.android.model.sheet.widget.WidgetView
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.EntityTypeSheet
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.entityType
import maybe.Maybe
import maybe.Nothing



fun numberWidgetCustomView(
        numberWidget : NumberWidget,
        format : NumberWidgetFormatCustom,
        entityId : EntityId,
        context : Context,
        groupContext : Maybe<GroupContext> = Nothing()
) : View
{
    val layout = WidgetView.layout(format.widgetFormat, entityId, context)

//    val layoutId = Util.generateViewId()
//    numberWidget.layoutId = layoutId
//    layout.id  = layoutId

    layout.addView(mainView(numberWidget, format, entityId, context, groupContext))

    entityType(entityId).apDo { entityType ->
        when (entityType) {
            is EntityTypeSheet -> {
                layout.setOnClickListener {
                    numberWidget.primaryAction(entityId, context)
                }

                layout.setOnLongClickListener {
                    numberWidget.secondaryAction(entityId, context)
                    true
                }
            }
        }
    }

//    updateView(numberWidget, entityId, layout, context, groupContext)

    return layout
}


//fun updateView(numberWidget : NumberWidget,
//               entityId : EntityId,
//               layout : LinearLayout,
//               context : Context,
//               groupContext : Maybe<GroupContext>)
//{
//
//    val contentLayout = layout.findViewById<LinearLayout>(R.id.widget_content_layout)
//    contentLayout.removeAllViews()
//    contentLayout.addView(mainView(numberWidget, entityId, context, groupContext))
//}


/**
 * The outermost view that holds the outside labels and the value view.
 *
 *                      top label
 *             --------------------------
 *             |                         |
 *  left label |        Value View       | right label
 *             |                         |
 *             ---------------------------
 *                    bottom label
 *
 */
private fun mainView(numberWidget : NumberWidget,
                     format : NumberWidgetFormatCustom,
                     entityId : EntityId,
                     context : Context,
                     groupContext : Maybe<GroupContext>) : LinearLayout
{

    val layout = mainLayout(format, context)

    // > Outside Top/Left Label View
//        if (format.outsideLabelString() != null) {
//            if (format.outsideLabelFormat().position().isTop() ||
//                format.outsideLabelFormat().position().isLeft()) {
//                layout.addView(this.outsideLabelView(format, sheetUIContext))
//            }
//        }

    // > Value
    layout.addView(valueMainView(numberWidget, format, entityId, context, groupContext))

    // > Outside Bottom/Right Label View
//        if (format.outsideLabelString() != null) {
//            if (format.outsideLabelFormat().position().isBottom() ||
//                format.outsideLabelFormat().position().isRight()) {
//                layout.addView(this.outsideLabelView(format, sheetUIContext))
//            }
//        }

    return layout
}


private fun mainLayout(format : NumberWidgetFormat, context : Context) : LinearLayout
{
    val layout = LinearLayoutBuilder()

    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

//    layout.orientation          = format.outsideLabelFormat().elementFormat().position()
//                                        .linearLayoutOrientation()

//        layout.layoutGravity        = format.widgetFormat().elementFormat().alignment().gravityConstant() or
//                                        Gravity.CENTER_VERTICAL

    ///layout.backgroundColor      = Color.CYAN

//        layout.padding.leftDp       = format.widgetFormat().padding().leftDp()
//        layout.padding.rightDp      = format.widgetFormat().padding().rightDp()

    return layout.linearLayout(context)
}


/**
 * The view that holds the value as well as the inside labels around the value.
 */
private fun valueMainView(numberWidget : NumberWidget,
                          format : NumberWidgetFormatCustom,
                          entityId : EntityId,
                          context : Context,
                          groupContext : Maybe<GroupContext>) : LinearLayout
{
    val layout = valueMainViewLayout(format, entityId, context)

//    val insideLabel = numberWidget.insideLabelValue(entityId)

    // > Inside Top/Left Label View
//    when (insideLabel) {
//        is Val -> {
//            val position = format.insideLabelFormat().elementFormat().position()
//            if (position.isTop() || position.isLeft()) {
//                layout.addView(insideLabelView(format, insideLabel.value, entityId, context))
//            }
//        }
//    }

    //layout.addView(this.valueView(numberWidget, format, entityId, context, groupContext))

    layout.addView(valueTextView(numberWidget, format, entityId, context, groupContext))

    // > Inside Bottom/Right Label View
//    when (insideLabel) {
//        is Val -> {
//            val position = format.insideLabelFormat().elementFormat().position()
//            if (position.isBottom() || position.isRight()) {
//                layout.addView(insideLabelView(format, insideLabel.value, entityId, context))
//            }
//        }
//    }

    return layout
}


private fun valueMainViewLayout(format : NumberWidgetFormatCustom,
                                entityId : EntityId,
                                context : Context) : LinearLayout
{
    val layout = LinearLayoutBuilder()

//    layout.orientation          = format.insideLabelFormat().elementFormat().position()
//                                        .linearLayoutOrientation()

    // > Width
    //   If no padding is specified, the value (and its background) stretches to fill the
    //   space. Otherwise it only stretches as far as the padding allows
    // -------------------------------------------------------------------------------------
//        if (this.format().valuePaddingHorizontal() != null ||
//            this.data().format().background() == BackgroundColor.EMPTY) {

    //layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

//        val height = format.widgetFormat().elementFormat().height()
//        when (height)
//        {
//            is Height.Wrap  -> layout.height   = LinearLayout.LayoutParams.WRAP_CONTENT
//            is Height.Fixed -> layout.heightDp = height.value.toInt()
//        }



//        if (this.data().format().underlineThickness() > 0)
//        {
//            layout.backgroundColor    = this.data().format().underlineColor().resourceId();
//            layout.backgroundResource = R.drawable.bg_widget_bottom_border;
//        }
//        else if (this.data().format().background() != BackgroundColor.EMPTY &&
//                 this.data().format().background() != BackgroundColor.NONE)
//        {

    layout.backgroundColor      = colorOrBlack(
                                      format.valueFormat.elementFormat().backgroundColorTheme(), entityId)

//        layout.backgroundResource   = format.valueFormat().height()
//                                            .resourceId(format.widgetFormat().corners())

    layout.corners              = format.valueFormat.elementFormat().corners()

//        layout.gravity              = Gravity.CENTER_VERTICAL
//
//        layout.gravity = format.valueFormat().elementFormat().alignment().gravityConstant() or
//                format.valueFormat().elementFormat().verticalAlignment().gravityConstant()

    layout.layoutGravity = format.valueFormat.elementFormat().alignment().gravityConstant() or
            format.valueFormat.elementFormat().verticalAlignment().gravityConstant()

    layout.paddingSpacing       = format.valueFormat.elementFormat().padding()


//        if (format.valueFormat().elementFormat().height().isWrap())
//        {
//            layout.padding.topDp    = format.valueFormat().elementFormat().padding().topDp()
//            layout.padding.bottomDp = format.valueFormat().elementFormat().padding().bottomDp()
//        }



//        if (format.widgetFormat.background() == BackgroundColor.EMPTY)
//            layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT

//        if (this.data().format().underlineThickness() > 0)
//        {
//            layout.backgroundColor    = this.data().format().underlineColor().resourceId();
//            layout.backgroundResource = R.drawable.bg_widget_bottom_border;
//        }

//        else if (this.data().format().background() != BackgroundColor.EMPTY &&
//                 this.data().format().background() != BackgroundColor.NONE)
//        {

    return layout.linearLayout(context)
}



private fun valueView(numberWidget : NumberWidget,
                      format : NumberWidgetFormatCustom,
                      entityId : EntityId,
                      context : Context,
                      groupContext : Maybe<GroupContext>) : LinearLayout
{
    val layout = valueViewLayout(format, context)

    // > Prefix
//        val prefixString = format.valuePrefixString()
//        if (prefixString != null)
//            layout.addView(this.valueFixView(prefixString,
//                                             format.valuePrefixStyle(),
//                                             format.valueFormat(),
//                    sheetUIContext))

    // > Value
    layout.addView(valueTextView(numberWidget, format, entityId, context, groupContext))

    // > Base Value
//        if (this.baseValueVariableName() != null)
//            layout.addView(baseValueView(context));

    // > Postfix
//        val postfixString = format.valuePostfixString()
//        if (postfixString != null)
//            layout.addView(this.valueFixView(postfixString,
//                                             format.valuePostfixStyle(),
//                                             format.valueFormat(),
//                    sheetUIContext))

    return layout
}


private fun valueViewLayout(format : NumberWidgetFormatCustom, context : Context) : LinearLayout
{
    val layout = LinearLayoutBuilder()

    layout.orientation      = LinearLayout.HORIZONTAL

    layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT

    //if (format.widgetFormat().elementFormat().height().isWrap())
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT
//        else
//            layout.height       = LinearLayout.LayoutParams.MATCH_PARENT

    layout.paddingSpacing   = format.valueFormat.elementFormat().padding()

    layout.gravity = format.valueFormat.elementFormat().alignment().gravityConstant() or
                                format.valueFormat.elementFormat().verticalAlignment().gravityConstant()

    layout.layoutGravity = format.valueFormat.elementFormat().alignment().gravityConstant() or
            format.valueFormat.elementFormat().verticalAlignment().gravityConstant()

    return layout.linearLayout(context)
}


private fun valueTextView(numberWidget : NumberWidget,
                          format : NumberWidgetFormatCustom,
                          entityId : EntityId,
                          context : Context,
                          groupContext : Maybe<GroupContext>) : TextView
{
    val value = TextViewBuilder()

    value.width         = LinearLayout.LayoutParams.WRAP_CONTENT
    value.height        = LinearLayout.LayoutParams.WRAP_CONTENT

    value.gravity       = format.valueFormat.elementFormat().alignment().gravityConstant() or
                            format.valueFormat.elementFormat().verticalAlignment().gravityConstant()

    value.layoutGravity       = format.valueFormat.elementFormat().alignment().gravityConstant() or
            format.valueFormat.elementFormat().verticalAlignment().gravityConstant()

    value.paddingSpacing    = format.valueFormat.elementFormat().padding()

//        if (numberWidget.description() != null)
//        {
//            value.layoutGravity = format.descriptionStyle().alignment().gravityConstant() or
//                                    Gravity.CENTER_VERTICAL
//            value.gravity       = format.descriptionStyle().alignment().gravityConstant()
//
//            format.descriptionStyle().styleTextViewBuilder(value, sheetUIContext)
//
//            val spans = mutableListOf<FormattedString.Span>()
//
//            val labelSpan =
//                FormattedString.Span(
//                        format.insideLabelString(),
//                        SheetManager.color(sheetUIContext.sheetId,
//                                           format.insideLabelFormat().style().colorTheme()),
//                        format.insideLabelFormat().style().sizeSp(),
//                        format.insideLabelFormat().style().font())
//
//            val valueSpan =
//                FormattedString.Span(numberWidget.valueString(sheetContext),
//                                     sheetUIContext.context.getString(R.string.placeholder_value),
//                                     SheetManager.color(sheetUIContext.sheetId,
//                                                        format.valueFormat().style().colorTheme()),
//                                     format.valueFormat().style().sizeSp(),
//                                     format.valueFormat().style().font())
//
//
//            if (format.insideLabelString() != null)
//                spans.add(labelSpan)
//
//            spans.add(valueSpan)
//
//            value.textSpan  = FormattedString.spannableStringBuilder(numberWidget.description(),
//                                                                     spans)
//        }

//        var valueString = ""
//        valueString = numberWidget.valueString(sheetContext)

    val valueDouble = numberWidget.value(entityId, groupContext)
    val valueString = format.valueFormat.numberFormat().formattedString(valueDouble)

    value.text = valueString

    format.valueFormat.styleTextViewBuilder(value, entityId, context)

    return value.textView(context)
}

//
//    private fun valueFixView(fixString : String,
//                             style : TextFormat,
//                             valueFormat : TextFormat,
//                             sheetUIContext: SheetUIContext) : TextView
//    {
//        val prefix              = TextViewBuilder()
//
//        prefix.width            = LinearLayout.LayoutParams.WRAP_CONTENT
//        prefix.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        prefix.layoutGravity    = valueFormat.elementFormat().alignment().gravityConstant() or
//                                        Gravity.CENTER_VERTICAL
//        prefix.gravity          = valueFormat.elementFormat().alignment().gravityConstant()
//
//        prefix.text             = fixString
//
//        style.styleTextViewBuilder(prefix, sheetUIContext)
//
//        return prefix.textView(sheetUIContext.context)
//    }
//
//
//    private fun baseValueView(format : NumberWidgetFormat, sheetUIContext: SheetUIContext) : LinearLayout
//    {
//        val layout = this.baseValueViewLayout(sheetUIContext.context)
//
//        // > Separator
//        layout.addView(baseValueSeparatorView(format, sheetUIContext))
//
//        // > Value
//        layout.addView(baseValueTextView(sheetUIContext.context))
//
//        return layout;
//    }


private fun baseValueViewLayout(context : Context) : LinearLayout
{
    val layout = LinearLayoutBuilder()

    layout.orientation          = LinearLayout.HORIZONTAL

    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    // layout.layoutGravity        = format.baseValueVerticalAlignment().gravityConstant()
    layout.gravity              = Gravity.CENTER_VERTICAL

    return layout.linearLayout(context)
}


//    private fun baseValueSeparatorView(format : NumberWidgetFormat,
//                                       sheetUIContext: SheetUIContext) : TextView
//    {
//        val separator = TextViewBuilder()
//
//        separator.width         = LinearLayout.LayoutParams.WRAP_CONTENT
//        separator.height        = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    //    separator.text          = format.valueSeparatorString()
//
////        format.valueSeparatorFormat()?.style()?.styleTextViewBuilder(separator, sheetUIContext)
////
////        separator.marginSpacing = format.valueSeparatorFormat()?.margins()
//
//        return separator.textView(sheetUIContext.context)
//    }
//
//
//    private fun baseValueTextView(context : Context) : TextView
//    {
//        val value = TextViewBuilder()
//
//        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT
//        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        // value.text          = this.baseValue().toString()
//
//        // this.format().baseValueStyle().styleTextViewBuilder(value, context)
//
//        // value.marginSpacing = format.baseValueMargins()
//
//        return value.textView(context)
//    }
//
//
//    private fun outsideLabelView(format : NumberWidgetFormat,
//                                 sheetUIContext: SheetUIContext) : TextView
//    {
//        val label = TextViewBuilder()
//
//        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
//        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        label.layoutGravity     = format.outsideLabelFormat().elementFormat().alignment().gravityConstant() or
//                                    Gravity.CENTER_VERTICAL
//
//        //label.text              = format.outsideLabelString()
//
//        format.outsideLabelFormat().styleTextViewBuilder(label, sheetUIContext)
//
//        label.marginSpacing     = format.outsideLabelFormat().elementFormat().margins()
//
//        return label.textView(sheetUIContext.context)
//    }

//
//private fun insideLabelView(format : NumberWidgetFormat,
//                            labelString : String,
//                            entityId : EntityId,
//                            context : Context) : TextView
//{
//    val label               = TextViewBuilder()
//
//    label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
//    label.height            = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    label.text              = labelString
//
//    label.layoutGravity     = format.insideLabelFormat().elementFormat().alignment().gravityConstant() or
//                                  Gravity.CENTER_VERTICAL;
//
//    format.insideLabelFormat().styleTextViewBuilder(label, entityId, context)
//
//    label.marginSpacing     = format.insideLabelFormat().elementFormat().margins()
//
//    label.paddingSpacing    = format.insideLabelFormat().elementFormat().padding()
//
//    return label.textView(context)
//}

