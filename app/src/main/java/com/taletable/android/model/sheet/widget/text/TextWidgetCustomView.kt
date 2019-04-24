
package com.taletable.android.model.sheet.widget.text


import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.widget.TextWidget
import com.taletable.android.rts.entity.EntityId
import effect.Val
import maybe.Maybe



/**
 * Custom view
 */
fun textWidgetCustomView(
        textWidget : TextWidget,
        format : TextWidgetFormatCustom,
        entityId : EntityId,
        context : Context,
        groupContext : Maybe<GroupContext>) : LinearLayout
{
    val layout = mainLayout(textWidget, context)

    // > Outside Top/Left Label View
//        if (format.outsideLabel() != null) {
//            if (format.outsideLabelFormat().position().isTop() ||
//                format.outsideLabelFormat().position().isLeft()) {
//                layout.addView(this.outsideLabelView(format, sheetUIContext))
//            }
//        }

    // > Value
    layout.addView(valueMainView(textWidget, format, entityId, context, groupContext))

    // > Outside Bottom/Right Label View
//        if (format.outsideLabel() != null) {
//            if (format.outsideLabelFormat().position().isBottom() ||
//                format.outsideLabelFormat().position().isRight()) {
//                layout.addView(this.outsideLabelView(format, sheetUIContext))
//            }
//        }


    return layout
}


private fun mainLayout(textWidget : TextWidget,
                       context : Context) : LinearLayout
{
    val layout = LinearLayoutBuilder()

    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

//        layout.orientation          = textWidget.format().outsideLabelFormat()
//                                            .position().linearLayoutOrientation()

//        layout.gravity              = textWidget.widgetFormat().elementFormat().alignment().gravityConstant()
    //Log.d("***TEXTWIDGET", "${textWidget.widgetFormat().elementFormat().alignment()}" )

    // layout.marginSpacing        = textWidget.widgetFormat().elementFormat().margins()


    return layout.linearLayout(context)
}


/**
 * The view that holds the value as well as the inside labels around the value.
 */
private fun valueMainView(textWidget : TextWidget,
                          format : TextWidgetFormatCustom,
                          entityId : EntityId,
                          context : Context,
                          groupContext : Maybe<GroupContext>) : LinearLayout
{
    val layout = valueMainViewLayout(format, entityId, context)

    // > Inside Top/Left Label View
//        if (format.insideLabel() != null && textWidget.description() == null) {
//            if (format.insideLabelFormat().position().isTop() ||
//                format.insideLabelFormat().position().isLeft()) {
//                layout.addView(this.insideLabelView(format, sheetUIContext))
//            }
//        }

    val insideLabel = textWidget.labelValue(entityId)

    // > Inside Top/Left Label View
    when (insideLabel) {
        is Val -> {
            val position = format.prefixFormat.elementFormat().position()
//            if (position.isTop() || position.isLeft()) {
//                layout.addView(insideLabelView(format, insideLabel.value, entityId, context))
//            }
        }
    }

    val valueString = textWidget.valueString(entityId, groupContext)
    val paragraphs = valueString.split("\n")
    paragraphs.forEachIndexed { index, s ->
        val isParagraph = index < paragraphs.size - 1
        layout.addView(valueTextView(s, isParagraph, textWidget, format, entityId, context, groupContext))
    }

    // > Inside Bottom/Right Label View
//        if (format.insideLabel() != null && textWidget.description() == null) {
//            if (format.insideLabelFormat().position().isBottom() ||
//                format.insideLabelFormat().position().isRight()) {
//                layout.addView(this.insideLabelView(format, sheetUIContext))
//            }
//        }

    return layout
}


private fun valueMainViewLayout(format : TextWidgetFormatCustom,
                                entityId : EntityId,
                                context : Context) : LinearLayout
{
    val layout = LinearLayoutBuilder()

//        layout.orientation          = format.insideLabelFormat()
//                                            .position().linearLayoutOrientation()

    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation          = LinearLayout.VERTICAL

//        val height = format.widgetFormat().elementFormat().height()
//        when (height)
//        {
//            is Height.Wrap  -> layout.height   = LinearLayout.LayoutParams.WRAP_CONTENT
//            is Height.Fixed -> layout.heightDp = height.value.toInt()
//        }
    //layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

//        layout.backgroundColor      = SheetManager.color(
//                                                sheetUIContext.sheetId,
//                                                format.widgetFormat().backgroundColorTheme())

    layout.gravity              = format.valueFormat.elementFormat().alignment().gravityConstant() or
                                    Gravity.CENTER_VERTICAL

//        layout.backgroundResource   = format.valueFormat().height()
//                                            .resourceId(format.widgetFormat().corners())

    if (format.valueFormat.elementFormat().height().isWrap())
    {
        layout.padding.topDp    = format.valueFormat.elementFormat().padding().topDp()
        layout.padding.bottomDp = format.valueFormat.elementFormat().padding().bottomDp()
    }

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


private fun valueTextView(paragraph : String,
                          isParagraph : Boolean,
                          textWidget : TextWidget,
                          format : TextWidgetFormatCustom,
                          entityId : EntityId,
                          context : Context,
                          groupContext : Maybe<GroupContext>) : TextView
{
    val value = TextViewBuilder()

//        textWidget.viewId   = Util.generateViewId()
//        value.id            = textWidget.viewId

    value.width         = LinearLayout.LayoutParams.WRAP_CONTENT
    value.height        = LinearLayout.LayoutParams.WRAP_CONTENT

    if (isParagraph) {
        Log.d("***TEXT WIDGET", "is paragraph")
        value.margin.bottomDp = format.valueFormat.paragraphSpacing().value
    }

    value.layoutGravity = format.valueFormat.elementFormat().alignment().gravityConstant() or
                            Gravity.CENTER_VERTICAL
    value.gravity       = format.valueFormat.elementFormat().alignment().gravityConstant()

    //value.text          = textWidget.valueString(entityId, groupContext)
    value.text          = paragraph

    format.valueFormat.styleTextViewBuilder(value, entityId, context)

    return value.textView(context)
}


//    private fun outsideLabelView(format : TextWidgetFormatCustom,
//                                 sheetUIContext: SheetUIContext) : TextView
//    {
//        val label = TextViewBuilder()
//
//        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
//        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        label.layoutGravity     = format.outsideLabelFormat().elementFormat().alignment().gravityConstant()
//
//        //label.text              = format.outsideLabel()
//
//        format.outsideLabelFormat().styleTextViewBuilder(label, sheetUIContext)
//
//        label.marginSpacing     = format.outsideLabelFormat().elementFormat().margins()
//
//        return label.textView(sheetUIContext.context)
//    }
//


//    private fun insideLabelView(format : TextWidgetFormatCustom,
//                                labelString : String,
//                                entityId : EntityId,
//                                context : Context) : TextView
//    {
//        val label               = TextViewBuilder()
//
//        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
//        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        label.text              = labelString
//
//        label.layoutGravity     = format.prefixFormat.elementFormat().alignment().gravityConstant() or
//                                      Gravity.CENTER_VERTICAL
//
//        format.prefixFormat.styleTextViewBuilder(label, entityId, context)
//
//        label.marginSpacing     = format.prefixFormat.elementFormat().margins()
//
//        return label.textView(context)
//    }
