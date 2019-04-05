
package com.taletable.android.activity.search.view


import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.taletable.android.R
import com.taletable.android.activity.home.HomeActivity
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.LayoutType
import com.taletable.android.lib.ui.RelativeLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.util.Util



fun searchResultSimpleView(theme : Theme, context : Context) : RelativeLayout
{
    val layout = searchResultSimpleViewLayout(context)

    layout.addView(searchResultSimpleNameView(theme, context))

    layout.addView(searchResultAddIconView(theme, context))

    return layout
}


private fun searchResultSimpleViewLayout(context : Context) : RelativeLayout
{
    val layout              = RelativeLayoutBuilder()

    layout.id               = R.id.layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.margin.topDp     = 4f
    layout.margin.leftDp    = 12f
    layout.margin.rightDp   = 12f

    layout.padding.topDp    = 12f
    layout.padding.bottomDp = 12f

    return layout.relativeLayout(context)
}



private fun searchResultSimpleNameView(theme : Theme, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.id                = R.id.name_view

    title.layoutType        = LayoutType.RELATIVE
    title.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
    title.height            = RelativeLayout.LayoutParams.WRAP_CONTENT

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Medium,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_12"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 20f

    return title.textView(context)
}


private fun searchResultSimpleNameSpannable(prefix : String,
                                            suffix : String,
                                            theme : Theme,
                                            context : Context) : SpannableStringBuilder
{
    val builder = SpannableStringBuilder()

    val totalLength = prefix.length + suffix.length

    val sizePx = Util.spToPx(21f, context)
    val sizeSpan = AbsoluteSizeSpan(sizePx)

    // | Prefix
    // ------------------------------------------------

    builder.append(prefix)

    val prefixColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
    val prefixColor              = theme.colorOrBlack(prefixColorTheme)
    val prefixColorSpan = ForegroundColorSpan(prefixColor)

    builder.setSpan(prefixColorSpan, 0, prefix.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    builder.setSpan(sizeSpan, 0, prefix.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

    // | Suffix
    // ------------------------------------------------

    builder.append(" ")
    builder.append(suffix)

    val suffixColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
    val suffixColor              = theme.colorOrBlack(suffixColorTheme)
    val suffixColorSpan = ForegroundColorSpan(suffixColor)

    builder.setSpan(suffixColorSpan, prefix.length + 1, totalLength + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    builder.setSpan(sizeSpan, prefix.length + 1, totalLength + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

    return builder
}



class SearchResultSimpleViewHolder(itemView : View, val theme : Theme, val context : Context)
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


    fun setSimpleTerm(name : String)
    {
        this.nameView?.text = name
    }


    fun setComplexTerm(prefix : String, suffix : String)
    {
        this.nameView?.text = searchResultSimpleNameSpannable(prefix, suffix, theme, context)
    }


    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }

}

