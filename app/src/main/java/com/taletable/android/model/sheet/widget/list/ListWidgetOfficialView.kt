
package com.taletable.android.model.sheet.widget.list


import android.content.Context
import android.view.View
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.widget.ListWidget
import com.taletable.android.model.sheet.widget.WidgetOfficialTheme
import com.taletable.android.model.sheet.widget.list.official_view.listWidgetOfficialMetricView
import com.taletable.android.rts.entity.EntityId
import maybe.Maybe
import maybe.Nothing



/**
 * Official view
 */
fun listWidgetOfficialView(
        format : ListWidgetFormatOfficial,
        listWidget : ListWidget,
        entityId : EntityId,
        context : Context,
        groupContext : Maybe<GroupContext> = Nothing()

) : View = when (format.format.theme)
{
    is WidgetOfficialTheme.Metric ->
        listWidgetOfficialMetricView(format.format.style, listWidget, entityId, context, groupContext)

}