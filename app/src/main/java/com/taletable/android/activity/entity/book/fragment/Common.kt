
package com.taletable.android.activity.entity.book.fragment


import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.taletable.android.R
import com.taletable.android.activity.session.SessionActivity
import com.taletable.android.lib.ui.*
import com.taletable.android.model.book.BookContent
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.groups
import maybe.Just
import maybe.Nothing



// ---------------------------------------------------------------------------------------------
// | ENTRY EXPANDER VIEW
// ---------------------------------------------------------------------------------------------

/**
 * Entry Expander View
 */
fun entryExpanderView(
        title : String,
        theme : Theme,
        contentList : List<BookContent>,
        entityId : EntityId,
        context : Context
) : LinearLayout
{
    val layout = entryExpanderViewLayout(context)

    val headerView = entryExpanderHeaderView(title, theme, context)
    val contentLayout = entryExpanderContentlayout(context)

    var isOpen = false

    val openDrawable = ContextCompat.getDrawable(context, R.drawable.icon_chevron_down_light)
    val closedDrawable = ContextCompat.getDrawable(context, R.drawable.icon_chevron_up_light)


    val labelClosedColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    val labelClosedColor = theme.colorOrBlack(labelClosedColorTheme)

    val labelOpenColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_18"))))
    val labelOpenColor = theme.colorOrBlack(labelOpenColorTheme)

    headerView.setOnClickListener {
        isOpen = if (isOpen) {
            contentLayout.removeAllViews()
            headerView.findViewById<ImageView>(R.id.icon_view)?.let {
                it.setImageDrawable(closedDrawable)
            }
//            headerView.findViewById<TextView>(R.id.label_view)?.let {
//                it.setTextColor(labelClosedColor)
//            }
            false
        } else {
            contentList.forEach { content ->
                groups(content.groupReferences(), entityId).forEach {
                    val groupContext = when (content.context()) {
                        is Just -> content.context()
                        is Nothing -> it.groupContext
                    }
                    contentLayout.addView(it.group.view(entityId, context, groupContext))
                }
            }
            headerView.findViewById<ImageView>(R.id.icon_view)?.let {
                it.setImageDrawable(openDrawable)
            }
//            headerView.findViewById<TextView>(R.id.label_view)?.let {
//                it.setTextColor(labelOpenColor)
//            }
            true
        }
    }

    layout.addView(headerView)
    layout.addView(contentLayout)

    return layout
}

/**
 * Entry Expander View Layout
 */
private fun entryExpanderViewLayout(context : Context) : LinearLayout
{
    val layoutBuilder               = LinearLayoutBuilder()

    layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation       = LinearLayout.VERTICAL

    layoutBuilder.margin.bottomDp   = 1f

    return layoutBuilder.linearLayout(context)
}

/**
 * Entry Expander Header View
 */
private fun entryExpanderHeaderView(
        label : String,
        theme : Theme,
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

    layoutBuilder.backgroundColor   = Color.WHITE

    layoutBuilder.child(labelViewBuilder)
                 .child(iconViewBuilder)

    // | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.id             = R.id.label_view

    labelViewBuilder.layoutType     = LayoutType.RELATIVE
    labelViewBuilder.width          = RelativeLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height         = RelativeLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.addRule(RelativeLayout.CENTER_VERTICAL)
    labelViewBuilder.addRule(RelativeLayout.ALIGN_PARENT_START)

    labelViewBuilder.text           = label

    labelViewBuilder.font           = Font.typeface(TextFont.Roboto,
                                                    TextFontStyle.Medium,
                                                    context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    labelViewBuilder.color          = theme.colorOrBlack(colorTheme)

    labelViewBuilder.sizeSp         = 17.5f

    // | Icon
    // -----------------------------------------------------------------------------------------

    iconViewBuilder.id              = R.id.icon_view

    iconViewBuilder.layoutType      = LayoutType.RELATIVE
    iconViewBuilder.widthDp         = 18
    iconViewBuilder.heightDp        = 18

    iconViewBuilder.addRule(RelativeLayout.CENTER_VERTICAL)
    iconViewBuilder.addRule(RelativeLayout.ALIGN_PARENT_END)

    iconViewBuilder.image           = R.drawable.icon_chevron_up_light

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_18"))))
    iconViewBuilder.color           = theme.colorOrBlack(iconColorTheme)

    return layoutBuilder.relativeLayout(context)
}

/**
 * Entry Expander Content Layout
 */
private fun entryExpanderContentlayout(context : Context) : LinearLayout
{
    val layoutBuilder           = LinearLayoutBuilder()

    layoutBuilder.width         = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height        = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation   = LinearLayout.VERTICAL

    return layoutBuilder.linearLayout(context)
}



// ---------------------------------------------------------------------------------------------
// | ENTRY GROUP HEADER VIEW
// ---------------------------------------------------------------------------------------------

fun entryGroupHeaderView(
        header : String,
        theme : Theme,
        context: Context
) : LinearLayout
{
    // | Declarations
    // -------------------------------------------------------------------------------------

    val layoutBuilder               = LinearLayoutBuilder()
    val headerViewBuilder           = TextViewBuilder()

    // | Layout Builder
    // -------------------------------------------------------------------------------------

    layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.padding.topDp     = 24f
    layoutBuilder.padding.bottomDp  = 8f
    layoutBuilder.padding.leftDp    = 16f
    layoutBuilder.padding.rightDp   = 16f

    layoutBuilder.backgroundColor   = Color.WHITE

    layoutBuilder.child(headerViewBuilder)

    // | Header View Builder
    // -------------------------------------------------------------------------------------

    headerViewBuilder.width         = LinearLayout.LayoutParams.WRAP_CONTENT
    headerViewBuilder.height        = LinearLayout.LayoutParams.WRAP_CONTENT

    headerViewBuilder.text          = header

    headerViewBuilder.font          = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Bold,
                                                    context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_18"))))
    headerViewBuilder.color         = theme.colorOrBlack(colorTheme)

    headerViewBuilder.sizeSp        = 17.5f

    return layoutBuilder.linearLayout(context)
}

// ---------------------------------------------------------------------------------------------
// | ENTRY SIMPLE VIEW
// ---------------------------------------------------------------------------------------------

fun entrySimpleView(
        label : String,
        onClick : View.OnClickListener,
        theme : Theme,
        sessionActivity : SessionActivity
) : ViewGroup
{
    val layout = entrySimpleViewLayout(sessionActivity)

    layout.addView(entrySimpleTitleView(label, theme, sessionActivity))

    layout.addView(entrySimpleIconView(theme, sessionActivity))

    layout.setOnClickListener(onClick)
//            .setOnClickListener {
//        val subsectionReference = BookReferenceSubsection(book.entityId(),
//                                                          chapter.chapterId,
//                                                          section.sectionId(),
//                                                          subsection.subsectionId())
//        sessionActivity.setCurrentBookReference(subsectionReference)
//    }

    return layout
}


private fun entrySimpleViewLayout(context : Context) : RelativeLayout
{
    val layout                  = RelativeLayoutBuilder()

    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation          = LinearLayout.HORIZONTAL

    layout.gravity              = Gravity.CENTER_VERTICAL

    layout.backgroundColor      = Color.WHITE

    layout.padding.topDp        = 16f
    layout.padding.bottomDp     = 16f
    layout.padding.leftDp       = 16f
    layout.padding.rightDp      = 16f

    layout.margin.bottomDp      = 1f

    return layout.relativeLayout(context)
}


private fun entrySimpleTitleView(
        title : String,
        theme : Theme,
        context : Context
) : LinearLayout
{
    val layout = entrySimpleTitleViewLayout(context)

    layout.addView(entrySimpleTitleTextView(title, theme, context))

//    subsection.subtitle().doMaybe {
//        layout.addView(entrySimpleSubtitleTextView(it.value))
//    }

    return layout
}


private fun entrySimpleTitleViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.VERTICAL

    return layout.linearLayout(context)
}


private fun entrySimpleTitleTextView(
        titleString : String,
        theme : Theme,
        context : Context
) : TextView
{
    val summary                 = TextViewBuilder()

    summary.width               = LinearLayout.LayoutParams.WRAP_CONTENT
    summary.height              = LinearLayout.LayoutParams.WRAP_CONTENT

    summary.text                = titleString

    summary.font                = Font.typeface(TextFont.Roboto,
                                                TextFontStyle.Medium,
                                                context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    summary.color               = theme.colorOrBlack(colorTheme)

    summary.sizeSp              = 17.5f

    summary.backgroundColor     = Color.WHITE

    return summary.textView(context)
}


private fun entrySimpleSubtitleTextView(
        subtitleString : String,
        theme : Theme,
        context : Context
) : TextView
{
    val subtitle                 = TextViewBuilder()

    subtitle.width               = LinearLayout.LayoutParams.WRAP_CONTENT
    subtitle.height              = LinearLayout.LayoutParams.WRAP_CONTENT

    subtitle.text                = subtitleString

    subtitle.font                = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
    subtitle.color               = theme.colorOrBlack(colorTheme)

    subtitle.sizeSp              = 15f

    subtitle.corners             = Corners(2.0, 2.0, 2.0, 2.0)

    subtitle.backgroundColor     = Color.WHITE

    return subtitle.textView(context)
}


private fun entrySimpleIconView(theme : Theme, context : Context) : LinearLayout
{
    // (1) Declarations
    // -------------------------------------------------------------------------------------

    val layout                  = LinearLayoutBuilder()
    val icon                    = ImageViewBuilder()

    // (2) Layout
    // -------------------------------------------------------------------------------------

    layout.layoutType           = LayoutType.RELATIVE
    layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
    layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

    layout.addRule(RelativeLayout.ALIGN_PARENT_END)
    layout.addRule(RelativeLayout.CENTER_VERTICAL)

    layout.child(icon)

    // (3) Icon
    // -------------------------------------------------------------------------------------

    icon.widthDp                = 17
    icon.heightDp               = 17

    icon.image                  = R.drawable.icon_arrow_forward

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_18"))))
    icon.color               = theme.colorOrBlack(iconColorTheme)

    return layout.linearLayout(context)
}



// ---------------------------------------------------------------------------------------------
// | ENTRY CONTENT VIEW
// ---------------------------------------------------------------------------------------------

/**
 * Entry Content View
 */
fun entryContentView(
        content : BookContent,
        entityId : EntityId,
        context : Context
) : LinearLayout
{
    val layout = entryContentViewLayout(context)

    groups(content.groupReferences(), entityId).forEach {
        val groupContext = when (content.context()) {
            is Just -> content.context()
            is Nothing -> it.groupContext
        }
        Log.d("***COMMON", "group context is: $groupContext")
        layout.addView(it.group.view(entityId, context, groupContext))
    }

    return layout
}

/**
 * Entry Content View Layout
 */
private fun entryContentViewLayout(context : Context) : LinearLayout
{
    val layoutBuilder               = LinearLayoutBuilder()

    layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation       = LinearLayout.VERTICAL

    // layoutBuilder.margin.bottomDp   = 1f

    return layoutBuilder.linearLayout(context)
}

