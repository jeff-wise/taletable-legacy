
package com.taletable.android.model.sheet.widget.expander


import android.content.Context
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.taletable.android.R
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.widget.ExpanderWidget
import com.taletable.android.model.sheet.widget.WidgetView
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.util.Util
import maybe.Maybe



fun expanderWidgetCustomView(
        expanderWidget : ExpanderWidget,
        format : ExpanderWidgetFormatCustom,
        entityId : EntityId,
        context : Context,
        groupContext : Maybe<GroupContext>
) : View
{
    val layout = WidgetView.layout(expanderWidget.widgetFormat(), entityId, context)

    val layoutId = Util.generateViewId()
    layout.id = layoutId
    expanderWidget.layoutId = layoutId

    val contentLayout = layout.findViewById<LinearLayout>(R.id.widget_content_layout)
    contentLayout.orientation       = LinearLayout.VERTICAL

    // Header
//    val headerView = this.headerView()
//    headerView.setOnClickListener { onClick(contentLayout) }
//    contentLayout.addView(headerView)

   // Log.d("***EXPANDER WIDGET", "context is $groupContext")

    return layout
}

