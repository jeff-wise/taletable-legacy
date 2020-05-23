
package com.taletable.android.model.sheet.widget.list


import android.content.Context
import android.widget.LinearLayout
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.theme.Theme
import maybe.Maybe



/**
 * Custom view
 */
fun listWidgetCustomView(
        format : ListWidgetFormatCustom,
        data : ListWidgetViewData,
        theme : Theme,
        context : Context,
        groupContext : Maybe<GroupContext>) : LinearLayout
{
    val layout = customViewLayout(context)

    return layout
}


/**
 * Custom view
 */
fun customViewLayout(context : Context) : LinearLayout
{
    val layout = LinearLayoutBuilder()

    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    return layout.linearLayout(context)
}
