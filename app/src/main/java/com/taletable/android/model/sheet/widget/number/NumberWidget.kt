
package com.taletable.android.model.sheet.widget.number


import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.group.RowLayoutType
import com.taletable.android.model.sheet.widget.*
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.EntityTypeSheet
import com.taletable.android.rts.entity.entityType
import maybe.Maybe
import maybe.Nothing



// -----------------------------------------------------------------------------------------
// | VIEW
// -----------------------------------------------------------------------------------------

/**
 * Number Widget view
 */
fun numberWidgetViewGroup(
        numberWidget : NumberWidget,
        rowLayoutType : RowLayoutType,
        entityId : EntityId,
        context : Context
) : ViewGroup
{
    val layout = WidgetView.layout(numberWidget.widgetFormat(), entityId, context, rowLayoutType)

    configureNumberWidgetViewClick(layout, numberWidget, entityId, context)

    return layout
}

/**
 * Number Widget view
 */
private fun configureNumberWidgetViewClick(
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


fun numberWidgetView(
        numberWidget : NumberWidget,
        entityId : EntityId,
        context : Context,
        groupContext : Maybe<GroupContext> = Nothing()) : View
{
    val format = numberWidget.format

    return when (format) {
        is NumberWidgetFormatCustom ->
            numberWidgetCustomView(numberWidget, format, entityId, context, groupContext)
        is NumberWidgetFormatOfficial ->
            numberWidgetOfficialView(format, numberWidget, entityId, context, groupContext)
    }
}

