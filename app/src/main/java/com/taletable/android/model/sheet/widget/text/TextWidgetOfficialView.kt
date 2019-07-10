
package com.taletable.android.model.sheet.widget.text


import android.content.Context
import android.view.View
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.widget.TextWidget
import com.taletable.android.model.sheet.widget.WidgetOfficialTheme
import com.taletable.android.model.sheet.widget.text.official_view.textWidgetOfficialMetricView
import com.taletable.android.rts.entity.EntityId
import maybe.Maybe
import maybe.Nothing



/**
 * Official view
 */
fun textWidgetOfficialView(
        format : TextWidgetFormatOfficial,
        textWidget : TextWidget,
        entityId : EntityId,
        context : Context,
        groupContext : Maybe<GroupContext> = Nothing()

) : View = when (format.format.theme)
{
    is WidgetOfficialTheme.Metric ->
        textWidgetOfficialMetricView(
                format.format.style,
                format.format.variation,
                textWidget,
                entityId,
                context,
                groupContext)

}