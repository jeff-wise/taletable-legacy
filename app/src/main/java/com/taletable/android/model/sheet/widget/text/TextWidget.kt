
package com.taletable.android.model.sheet.widget.text


import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.taletable.android.lib.Factory
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.group.RowLayoutType
import com.taletable.android.model.sheet.widget.TextWidget
import com.taletable.android.model.sheet.widget.Widget
import com.taletable.android.model.sheet.widget.WidgetStyle
import com.taletable.android.model.sheet.widget.WidgetView
import com.taletable.android.model.theme.Theme
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.EntityTypeSheet
import com.taletable.android.rts.entity.entityType
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable


// -----------------------------------------------------------------------------------------
// | VIEW DATA
// -----------------------------------------------------------------------------------------

data class TextWidgetViewData(
        val value : String,
        val label : Maybe<String>
)


// -----------------------------------------------------------------------------------------
// | VIEW
// -----------------------------------------------------------------------------------------

/**
 * TextWidget view
 */
fun textWidgetViewGroup(
        textWidget : TextWidget,
        rowLayoutType : RowLayoutType,
        entityId : EntityId,
        context : Context
) : ViewGroup
{
    val layout = WidgetView.layout(textWidget.widgetFormat(), entityId, context, rowLayoutType)

    configureTextWidgetViewClick(layout, textWidget, entityId, context)

    return layout
}

/**
 * TextWidget view
 */
private fun configureTextWidgetViewClick(
        viewGroup : ViewGroup,
        widget : Widget,
        entityId : EntityId,
        context : Context
)
{
    entityType(entityId).apDo { entityType ->
        when (entityType)
        {
            is EntityTypeSheet ->
            {
                // NORMAL CLICK
                viewGroup.setOnClickListener {
                    widget.primaryAction(entityId, context)
                }
                // LONG CLICK
                viewGroup.setOnLongClickListener {
                    widget.secondaryAction(entityId, context)
                    true
                }
            }
        }

    }
}


fun textWidgetView(
        textWidget : TextWidget,
        data : TextWidgetViewData,
        theme : Theme,
        context : Context,
        groupContext : Maybe<GroupContext> = Nothing()) : View
{
    val format = textWidget.format

    return when (format) {
        is TextWidgetFormatCustom ->
            textWidgetCustomView(format, data, theme, context, groupContext)
        is TextWidgetFormatOfficial ->
            textWidgetOfficialView(format, data, theme, context, groupContext)
    }
}


//fun updateView(textWidget : TextWidget,
//               entityId : EntityId,
//               layout : LinearLayout,
//               context : Context,
//               groupContext : Maybe<GroupContext>)
//{
//
//    val contentLayout = layout.findViewById<LinearLayout>(R.id.widget_content_layout)
//
//    val format = textWidget.format()
//    when (format) {
//        is TextWidgetFormatCustom -> {
//            contentLayout.removeAllViews()
//            contentLayout.addView(mainView(textWidget, format, entityId, context, groupContext))
//        }
//        is TextWidgetFormatOfficial -> {
//            contentLayout.removeAllViews()
////                contentLayout.addView(mainView(textWidget, entityId, context, groupContext))
//        }
//    }
//}



/**
 * Text Widget Default Value
 */
data class TextWidgetDefaultValue(val value : String) : ToDocument, Serializable
{

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextWidgetDefaultValue>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TextWidgetDefaultValue> = when (doc) {
            is DocText -> effValue(TextWidgetDefaultValue(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    // | TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}

