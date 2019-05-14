
package com.taletable.android.model.sheet.widget.expander.official_view


import android.content.Context
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.taletable.android.R
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.sheet.widget.ExpanderWidget
import com.taletable.android.model.sheet.widget.WidgetStyle
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import maybe.Maybe



/**
 * Metric View
 */
fun expanderWidgetOfficialMetricView(
        style : WidgetStyle,
        expanderWidget : ExpanderWidget,
        entityId : EntityId,
        context : Context,
        groupContext : Maybe<GroupContext>
) : View = when (style.value)
{
    "inline" -> inlineView(expanderWidget, entityId, groupContext, context)
    else     -> inlineView(expanderWidget, entityId, groupContext, context)
}



// ---------------------------------------------------------------------------------------------
// | INLINE STYLE
// ---------------------------------------------------------------------------------------------

/**
 * Inline View
 */
private fun inlineView(
        expanderWidget : ExpanderWidget,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : View
{
    val layout = inlineViewLayout(context)

    layout.addView(inlineHeaderView(expanderWidget, entityId, groupContext, context))

    val groupLayout = inlineGroupLayout(context)
    layout.addView(groupLayout)

    var isOpen = false

    layout.setOnClickListener {
        if (isOpen)
        {
            isOpen = false
            groupLayout.removeAllViews()
        }
        // OPEN
        else
        {
            isOpen = true

            expanderWidget.contentGroups(entityId).forEach {
                groupLayout.addView(it.group.view(entityId, context, it.groupContext))
            }
        }
    }

    return layout
}


// | INLINE STYLE > Layout
// ---------------------------------------------------------------------------------------------

private fun inlineViewLayout(
        context : Context
) : LinearLayout
{
    val layoutBuilder           = LinearLayoutBuilder()

    layoutBuilder.width         = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height        = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation   = LinearLayout.VERTICAL

    return layoutBuilder.linearLayout(context)
}

// | INLINE STYLE > Header
// ---------------------------------------------------------------------------------------------

private fun inlineHeaderView(
        expanderWidget : ExpanderWidget,
        entityId : EntityId,
        groupContext : Maybe<GroupContext>,
        context : Context
) : RelativeLayout
{
    // | Declarations
    // -----------------------------------------------------------------------------------------

    val layoutBuilder               = RelativeLayoutBuilder()
    val labelViewBuilder            = TextViewBuilder()
    val iconViewBuilder             = ImageViewBuilder()

    // | Layout
    // -----------------------------------------------------------------------------------------

    layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.padding.topDp     = 16f
    layoutBuilder.padding.bottomDp  = 16f
    layoutBuilder.padding.leftDp    = 16f
    layoutBuilder.padding.rightDp   = 16f

    layoutBuilder.child(labelViewBuilder)
                 .child(iconViewBuilder)

    // | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.layoutType     = LayoutType.RELATIVE
    labelViewBuilder.width          = RelativeLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height         = RelativeLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.addRule(RelativeLayout.CENTER_VERTICAL)
    labelViewBuilder.addRule(RelativeLayout.ALIGN_PARENT_START)

    labelViewBuilder.text           = expanderWidget.labelValue(entityId, groupContext)

    labelViewBuilder.font           = Font.typeface(TextFont.RobotoSlab,
                                                    TextFontStyle.Bold,
                                                    context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_12"))))
    labelViewBuilder.color           = colorOrBlack(labelColorTheme, entityId)

    labelViewBuilder.sizeSp         = 18f

    // | Icon
    // -----------------------------------------------------------------------------------------

    iconViewBuilder.layoutType      = LayoutType.RELATIVE
    iconViewBuilder.widthDp         = 20
    iconViewBuilder.heightDp        = 20

    iconViewBuilder.addRule(RelativeLayout.CENTER_VERTICAL)
    iconViewBuilder.addRule(RelativeLayout.ALIGN_PARENT_END)

    iconViewBuilder.image           = R.drawable.icon_chevron_down_light

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_7")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_20"))))
    iconViewBuilder.color           = colorOrBlack(iconColorTheme, entityId)

    return layoutBuilder.relativeLayout(context)
}


// | INLINE STYLE > Layout
// ---------------------------------------------------------------------------------------------

private fun inlineGroupLayout(
        context : Context
) : LinearLayout
{
    val layoutBuilder           = LinearLayoutBuilder()

    layoutBuilder.width         = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height        = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation   = LinearLayout.VERTICAL

    return layoutBuilder.linearLayout(context)
}


//
//    private fun onClick(contentLayout : LinearLayout)
//    {
//        // CLOSE
//        if (this.isOpen)
//        {
//            this.isOpen = false
//            contentLayout.removeAllViews()
//
//            val headerView = this.headerView()
//            headerView.setOnClickListener { onClick(contentLayout) }
//            contentLayout.addView(headerView)
//        }
//        // OPEN
//        else
//        {
//            this.isOpen = true
//
//            contentLayout.removeAllViews()
//            val headerView = this.headerView()
//            headerView.setOnClickListener { onClick(contentLayout) }
//            contentLayout.addView(headerView)
//
//            val groupsLayout = groupsLayout()
//            contentLayout.addView(groupsLayout)
//
//            expanderWidget.contentGroups(entityId).forEach {
//                Log.d("***EXPANDER WIDGET", "context is : ${it.groupContext}")
//                groupsLayout.addView(it.group.view(entityId, context, it.groupContext))
//            }
//        }
//    }


//    private fun groupsLayout() : LinearLayout
//    {
//        val layout                  = LinearLayoutBuilder()
//
//        val contentFormat           = expanderWidget.format().contentFormat()
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation          = LinearLayout.VERTICAL
//
//        layout.backgroundColor      = colorOrBlack(contentFormat.backgroundColorTheme(), entityId)
//
//        layout.paddingSpacing       = contentFormat.padding()
//
//        return layout.linearLayout(context)
//    }
//
