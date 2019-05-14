
package com.taletable.android.model.sheet.widget.expander


import android.content.Context
import android.view.View
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.widget.ExpanderWidget
import com.taletable.android.model.sheet.widget.WidgetOfficialTheme
import com.taletable.android.model.sheet.widget.expander.official_view.expanderWidgetOfficialMetricView
import com.taletable.android.rts.entity.EntityId
import maybe.Maybe
import maybe.Nothing



/**
 * Official view
 */
fun expanderWidgetOfficialView(
        format : ExpanderWidgetFormatOfficial,
        expanderWidget : ExpanderWidget,
        entityId : EntityId,
        context : Context,
        groupContext : Maybe<GroupContext> = Nothing()

) : View = when (format.format.theme)
{
    is WidgetOfficialTheme.Metric ->
        expanderWidgetOfficialMetricView(format.format.style, expanderWidget, entityId, context, groupContext)
}