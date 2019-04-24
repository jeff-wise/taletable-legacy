
package com.taletable.android.model.sheet.widget.number


import android.content.Context
import android.view.View
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.widget.NumberWidget
import com.taletable.android.model.sheet.widget.WidgetOfficialTheme
import com.taletable.android.model.sheet.widget.number.official_view.numberWidgetOfficialMetricView
import com.taletable.android.rts.entity.EntityId
import maybe.Maybe
import maybe.Nothing



/**
 * Official view
 */
fun numberWidgetOfficialView(
        format : NumberWidgetFormatOfficial,
        numberWidget : NumberWidget,
        entityId : EntityId,
        context : Context,
        groupContext : Maybe<GroupContext> = Nothing()

) : View = when (format.format.theme)
{
    is WidgetOfficialTheme.Metric ->
        numberWidgetOfficialMetricView(format.format.style, numberWidget, entityId, context, groupContext)
}