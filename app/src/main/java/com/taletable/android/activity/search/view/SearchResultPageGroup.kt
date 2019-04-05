
package com.taletable.android.activity.search.view


import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.taletable.android.R
import com.taletable.android.activity.home.HomeActivity
import com.taletable.android.activity.search.SearchQuery
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*



fun searchResultPageGroupHeaderView(theme : Theme, context : Context) : RelativeLayout
{
    val layout = searchResultPageGroupHeaderViewLayout(context, theme)

    layout.addView(searchResultPageGroupHeaderNameView(theme, context))

    return layout
}


fun searchResultPageGroupHeaderViewLayout(context : Context, theme : Theme) : RelativeLayout
{
    val layout              = RelativeLayoutBuilder()

    layout.id               = R.id.layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_9"))))
    layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

    //layout.padding.topDp    = 4f

    return layout.relativeLayout(context)
}


private fun searchResultPageGroupHeaderNameView(theme : Theme, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.id                = R.id.name_view

    title.layoutType        = LayoutType.RELATIVE
    title.width             = RelativeLayout.LayoutParams.MATCH_PARENT
    title.height            = RelativeLayout.LayoutParams.WRAP_CONTENT

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
    title.backgroundColor  = theme.colorOrBlack(bgColorTheme)

    title.padding.leftDp    = 12f
    title.padding.rightDp   = 12f
    title.padding.topDp     = 12f
    title.padding.bottomDp  = 20f

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Medium,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_24"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 16f

    return title.textView(context)
}



fun searchResultPageGroupSearchView(theme : Theme, context : Context) : LinearLayout
{
    val layout = searchResultPageGroupSearchViewLayout(theme, context)

    layout.addView(searchResultPageGroupSearchButtonView(theme, context))

//    layout.addView(searchResultPageGroupDividerView(theme, context))

    return layout
}


fun searchResultPageGroupSearchViewLayout(theme : Theme, context: Context) : LinearLayout
{
    val layoutBuilder               = LinearLayoutBuilder()

    layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation       = LinearLayout.VERTICAL

    return layoutBuilder.linearLayout(context)
}


fun searchResultPageGroupSearchButtonView(theme : Theme, context : Context) : LinearLayout
{
    // (1) Declarations
    // -------------------------------------------------------------------------

    val layout              = LinearLayoutBuilder()
    val iconViewBuilder     = ImageViewBuilder()
    val title               = TextViewBuilder()

    // (2) Layout
    // -------------------------------------------------------------------------

    layout.id               = R.id.layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))
    layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

    layout.margin.leftDp    = 12f
    layout.margin.rightDp   = 12f
    //layout.margin.topDp     = 8f
    layout.margin.bottomDp  = 16f

    layout.padding.topDp    = 8f
    layout.padding.bottomDp = 8f
    layout.padding.leftDp   = 12f
    layout.padding.rightDp  = 8f

    layout.corners          = Corners(12.0, 12.0, 12.0, 12.0)

    layout.gravity          = Gravity.CENTER_VERTICAL

    layout.child(iconViewBuilder).child(title)

    // (2) Icon
    // -------------------------------------------------------------------------

    iconViewBuilder.widthDp     = 18
    iconViewBuilder.heightDp    = 18

    iconViewBuilder.image       = R.drawable.icon_search

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_16"))))
    iconViewBuilder.color              = theme.colorOrBlack(iconColorTheme)

    iconViewBuilder.margin.rightDp  = 12f

    // (3) Text
    // -------------------------------------------------------------------------

    title.id                = R.id.name_view

    title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
    title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Medium,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 18f

    return layout.linearLayout(context)
}


fun searchResultPageGroupDividerView(theme : Theme, context: Context) : LinearLayout
{
    val layoutBuilder               = LinearLayoutBuilder()

    layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.heightDp          = 1

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
    layoutBuilder.backgroundColor   = theme.colorOrBlack(colorTheme)

    return layoutBuilder.linearLayout(context)
}



private fun searchResultPageGroupSearchIconView(theme : Theme, context : Context) : LinearLayout
{
    // 1 | Declarations
    // -----------------------------------------------------------------------------------------

    val layout                  = LinearLayoutBuilder()
    val iconView                = ImageViewBuilder()

    // 2 | Layout
    // -----------------------------------------------------------------------------------------

    //layout.layoutType           = LayoutType.RELATIVE
    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

//    val bgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
//    layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

    layout.padding.topDp        = 8f
    layout.padding.bottomDp     = 8f
    layout.padding.rightDp      = 12f

    layout.gravity              = Gravity.CENTER_VERTICAL

//    layout.addRule(RelativeLayout.CENTER_VERTICAL)
//    layout.addRule(RelativeLayout.ALIGN_PARENT_END)

    layout.child(iconView)

    // 3 | Icon
    // -----------------------------------------------------------------------------------------

    iconView.widthDp            = 18
    iconView.heightDp           = 18

    iconView.image              = R.drawable.icon_arrow_forward

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_16"))))
    iconView.color              = theme.colorOrBlack(colorTheme)

    return layout.linearLayout(context)
}



class SearchResultPageGroupHeaderViewHolder(itemView : View, val theme : Theme, val context : Context)
                        : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout      : RelativeLayout? = null
    var nameView    : TextView? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout      = itemView.findViewById(R.id.layout)
        this.nameView    = itemView.findViewById(R.id.name_view)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------


    fun setName(name : String)
    {
        this.nameView?.text = name
    }


    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }

}


class SearchResultPageGroupSearchViewHolder(itemView : View, val theme : Theme, val context : Context)
                        : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout      : LinearLayout? = null
    var nameView    : TextView? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout      = itemView.findViewById(R.id.layout)
        this.nameView    = itemView.findViewById(R.id.name_view)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------


    fun setSearchText(query : SearchQuery)
    {
        val builder = SpannableStringBuilder()

        //builder.append("Search ") // 7
        builder.append(query.term)
        builder.append(" in ")
        builder.append(query.context)

        val inItalicSpan = CustomTypefaceSpan(Font.typeface(TextFont.Roboto,
                                                            TextFontStyle.MediumItalic,
                                                            context))

        val inColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_22"))))
        val inColor = theme.colorOrBlack(inColorTheme)
        val inColorSpan = ForegroundColorSpan(inColor)

        builder.setSpan(inItalicSpan,
                        query.term.length,
                        query.term.length + 4,
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        builder.setSpan(inColorSpan,
                query.term.length,
                query.term.length + 4,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        this.nameView?.text = builder
    }


    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }

}



