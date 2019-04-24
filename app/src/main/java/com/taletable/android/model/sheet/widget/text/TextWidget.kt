
package com.taletable.android.model.sheet.widget.text


import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.group.RowLayoutType
import com.taletable.android.model.sheet.widget.TextWidget
import com.taletable.android.model.sheet.widget.Widget
import com.taletable.android.model.sheet.widget.WidgetView
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.EntityTypeSheet
import com.taletable.android.rts.entity.entityType
import maybe.Maybe
import maybe.Nothing



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
        entityId : EntityId,
        context : Context,
        groupContext : Maybe<GroupContext> = Nothing()) : View
{
    val format = textWidget.format

    return when (format) {
        is TextWidgetFormatCustom ->
            textWidgetCustomView(textWidget, format, entityId, context, groupContext)
        is TextWidgetFormatOfficial ->
            textWidgetOfficialView(format, textWidget, entityId, context, groupContext)
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




