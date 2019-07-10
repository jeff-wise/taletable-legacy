
package com.taletable.android.activity.search.view


import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.taletable.android.R
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.ImageViewBuilder
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*



fun searchResultImageView(theme : Theme, context : Context) : ViewGroup
{
    val layout = searchResultImageViewLayout(context)

    layout.addView(searchResultImageImageView(theme, context))

    val mainLayout = searchResultImageMainViewLayout(context)

    mainLayout.addView(searchResultImageNameView(theme, context))
    mainLayout.addView(searchResultImageDescriptionView(theme, context))

    layout.addView(mainLayout)

    return layout
}


fun searchResultImageViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.id               = R.id.layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.margin.leftDp    = 12f
    layout.margin.rightDp   = 12f

    //layout.padding.topDp    = 16f
    layout.padding.bottomDp = 24f

    return layout.linearLayout(context)
}


fun searchResultImageMainViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.id               = R.id.layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.VERTICAL

    layout.padding.leftDp   = 8f

    return layout.linearLayout(context)
}


private fun searchResultImageNameView(theme : Theme, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.id                = R.id.name_view

    title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
    title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Medium,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_10"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 17.5f

    return title.textView(context)
}


private fun searchResultImageDescriptionView(theme : Theme, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.id                = R.id.description_view

    title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
    title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Regular,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_14"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 16.5f

    return title.textView(context)
}


private fun searchResultImageImageView(theme : Theme, context : Context) : LinearLayout
{
    // 1 | Declarations
    // -------------------------------------------------------------------------

    val layout                  = LinearLayoutBuilder()
    val iconViewBuilder         = ImageViewBuilder()

    // 2 | Layout
    // -------------------------------------------------------------------------

    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT


    layout.backgroundResource   = R.drawable.avatar_game

//    layout.corners              = Corners(3.0,3.0,3.0,3.0)
//
//    val bgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
//    layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

    layout.gravity              = Gravity.CENTER

    layout.child(iconViewBuilder)

    // 2 | Icon View Builder
    // -------------------------------------------------------------------------

    iconViewBuilder.widthDp         = 26
    iconViewBuilder.heightDp        = 26

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_14"))))

    iconViewBuilder.image           = R.drawable.icon_book

    //iconViewBuilder.color           = theme.colorOrBlack(iconColorTheme)

    return layout.linearLayout(context)
}


class SearchResultImageViewHolder(itemView : View, val theme : Theme, val context : Context)
                                    : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout          : LinearLayout? = null
    var iconView        : ImageView? = null
    var nameView        : TextView? = null
    var descriptionView : TextView? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout          = itemView.findViewById(R.id.layout)
        this.nameView        = itemView.findViewById(R.id.name_view)
        this.descriptionView = itemView.findViewById(R.id.description_view)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------
    fun setName(name : String)
    {
        this.nameView?.text = name
    }

    fun setDescription(description : String)
    {
        this.descriptionView?.text = description
    }

    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }

}

