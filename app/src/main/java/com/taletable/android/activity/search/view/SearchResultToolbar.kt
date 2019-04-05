
package com.taletable.android.activity.search.view


import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.taletable.android.R
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.ImageViewBuilder
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*



fun searchResultToolbarView(theme : Theme, context : Context) : LinearLayout
{
    val layout = searchResultToolbarViewLayout(theme, context)

    layout.addView(searchResultToolbarContextHeaderView(theme, context))

    layout.addView(searchResultToolbarContextButtonView(theme, context))

    layout.addView(searchResultToolbarDividerView(theme, context))

    return layout
}


fun searchResultToolbarViewLayout(theme : Theme, context : Context) : LinearLayout
{
    val layoutBuilder               = LinearLayoutBuilder()

    layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation       = LinearLayout.VERTICAL

    layoutBuilder.padding.topDp     = 8f
    layoutBuilder.padding.bottomDp  = 8f

    return layoutBuilder.linearLayout(context)
}


fun searchResultToolbarContextHeaderView(theme : Theme, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
    title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    title.margin.leftDp     = 12f
    title.margin.rightDp    = 12f
    title.margin.bottomDp   = 6f

    title.text              = "Showing results in"

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Bold,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_20"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 16f

    return title.textView(context)
}


fun searchResultToolbarContextButtonView(theme : Theme, context : Context) : LinearLayout
{
    // (1) Declarations
    // -------------------------------------------------------------------------

    val layout              = LinearLayoutBuilder()
    val iconViewBuilder     = ImageViewBuilder()
    val nameViewBuilder     = TextViewBuilder()

    // (2) Layout
    // -------------------------------------------------------------------------

    layout.id               = R.id.layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_20"))))
    layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

    layout.margin.leftDp    = 12f
    layout.margin.rightDp   = 12f
    layout.margin.bottomDp  = 16f

    layout.padding.topDp    = 10f
    layout.padding.bottomDp = 10f
    layout.padding.leftDp   = 8f
    layout.padding.rightDp  = 8f

    layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

    layout.gravity          = Gravity.CENTER_VERTICAL

    layout.child(iconViewBuilder)
          .child(nameViewBuilder)

    // (3) Icon
    // -------------------------------------------------------------------------

    iconViewBuilder.widthDp            = 22
    iconViewBuilder.heightDp           = 22

    iconViewBuilder.image               = R.drawable.icon_die

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))
    iconViewBuilder.color              = theme.colorOrBlack(iconColorTheme)
    //iconViewBuilder.color              = Color.WHITE

    iconViewBuilder.margin.rightDp      = 6f

    // (3) Text
    // -------------------------------------------------------------------------

    nameViewBuilder.id           = R.id.context_view

    nameViewBuilder.width        = LinearLayout.LayoutParams.WRAP_CONTENT
    nameViewBuilder.height       = LinearLayout.LayoutParams.WRAP_CONTENT

    nameViewBuilder.font         = Font.typeface(TextFont.Roboto,
                                                    TextFontStyle.Bold,
                                                    context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_12"))))
    nameViewBuilder.color        = theme.colorOrBlack(colorTheme)
    nameViewBuilder.color        = Color.WHITE

    nameViewBuilder.sizeSp       = 18f

    return layout.linearLayout(context)
}



fun searchResultToolbarDividerView(theme : Theme, context: Context) : LinearLayout
{
    val layoutBuilder               = LinearLayoutBuilder()

    layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.heightDp          = 1

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_5"))))
    layoutBuilder.backgroundColor   = theme.colorOrBlack(colorTheme)

    return layoutBuilder.linearLayout(context)
}



class SearchResultToolbarViewHolder(itemView : View, val theme : Theme, val context : Context)
                        : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout          : LinearLayout? = null
    var contextView     : TextView? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout      = itemView.findViewById(R.id.layout)
        this.contextView = itemView.findViewById(R.id.context_view)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setContext(context : String)
    {
        this.contextView?.text = context
    }

}

