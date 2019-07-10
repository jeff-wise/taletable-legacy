
package com.taletable.android.activity.search.view


import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.taletable.android.R
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.util.Util



fun searchResultIconView(theme : Theme, context : Context) : LinearLayout
{
    val layout = searchResultIconViewLayout(context)

    layout.addView(searchResultIconMainView(theme, context))

    //layout.addView(searchResultIconDescriptionView(theme, context))

    layout.addView(searchResultIconDividerView(theme, context))

    return layout
}


private fun searchResultIconViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.id               = R.id.layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.VERTICAL

    layout.margin.leftDp    = 21f
    layout.margin.rightDp   = 20f

    layout.padding.topDp    = 16f

    return layout.linearLayout(context)
}



fun searchResultIconMainView(theme : Theme, context : Context) : ViewGroup
{
    val layout = searchResultIconMainViewLayout(context)

    val leftLayout = searchResultIconLeftViewLayout(context)
    val rightLayout = searchResultIconRightViewLayout(context)

    layout.addView(searchResultIconIconView(theme, context))

    val nameView = searchResultIconNameView(theme, context)
    val descriptionView = searchResultIconDescriptionView(theme, context)

    layout.addView(nameView)
    layout.addView(descriptionView)

//    layout.addView(leftLayout)
//    layout.addView(rightLayout)

    return layout
}


private fun searchResultIconMainViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

//    layout.margin.leftDp    = 13f
//    layout.margin.rightDp   = 13f
//
//    layout.padding.topDp    = 12f
//    layout.padding.bottomDp = 12f
    //layout.padding.bottomDp = 12f

    return layout.linearLayout(context)
}


private fun searchResultIconDividerView(theme : Theme, context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.heightDp         = 1

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_5"))))
    layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

    layout.margin.topDp    = 16f

    return layout.linearLayout(context)
}



private fun searchResultIconLeftViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

    layout.gravity          = Gravity.CENTER_VERTICAL

    return layout.linearLayout(context)
}


private fun searchResultIconRightViewLayout(context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.VERTICAL

    layout.padding.leftDp   = 9f

    return layout.linearLayout(context)
}


private fun searchResultIconNameView(theme : Theme, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.id                = R.id.name_view

    title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
    title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    title.addRule(RelativeLayout.CENTER_VERTICAL)
    title.addRule(RelativeLayout.ALIGN_PARENT_START)

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Medium,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_10"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 19f

    return title.textView(context)
}


private fun searchResultIconDescriptionView(theme : Theme, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.id                = R.id.description_view

    title.layoutType        = LayoutType.RELATIVE
    title.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
    title.height            = RelativeLayout.LayoutParams.WRAP_CONTENT

    title.margin.leftDp     = 8f

    title.addRule(RelativeLayout.CENTER_VERTICAL)
    title.addRule(RelativeLayout.ALIGN_PARENT_START)

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Regular,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_22"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 18f

    return title.textView(context)
}


private fun searchResultIconIconView(theme : Theme, context : Context) : LinearLayout
{
    // 1 | Declarations
    // -----------------------------------------------------------------------------------------

    val layout                  = LinearLayoutBuilder()
    val iconView                = ImageViewBuilder()

    // 2 | Layout
    // -----------------------------------------------------------------------------------------

    layout.layoutType           = LayoutType.RELATIVE
    layout.widthDp              = 42
    layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

    //layout.margin.rightDp       = 18f
    layout.margin.topDp         = 2f

    layout.addRule(RelativeLayout.CENTER_VERTICAL)
    layout.addRule(RelativeLayout.ALIGN_PARENT_END)

    layout.child(iconView)

    // 3 | Icon
    // -----------------------------------------------------------------------------------------

    iconView.id                 = R.id.icon_view

    iconView.widthDp            = 24
    iconView.heightDp           = 24

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_12"))))
    iconView.color              = theme.colorOrBlack(colorTheme)

    return layout.linearLayout(context)
}



class SearchResultIconViewHolder(itemView : View, val theme : Theme, val context : Context)
                                    : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout          : ViewGroup? = null
    var iconView        : ImageView? = null
    var nameView        : TextView? = null
    var descriptionView : TextView? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout          = itemView.findViewById(R.id.layout)
        this.iconView        = itemView.findViewById(R.id.icon_view)
        this.nameView        = itemView.findViewById(R.id.name_view)
        this.descriptionView = itemView.findViewById(R.id.description_view)
    }


    // -----------------------------------------------------------------------------------------
    // VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    fun setIcon(iconId : Int, iconSize : Int? = null)
    {
        this.iconView?.let {

            it.setImageDrawable(ContextCompat.getDrawable(context, iconId))

            Log.d("***SEARCH RSULT ICON", "iconSize: $iconSize")

            if (iconSize != null)
            {
                val layoutParams = it.layoutParams
                layoutParams.width = Util.dpToPixel(iconSize.toFloat())
                layoutParams.height = Util.dpToPixel(iconSize.toFloat())

                it.layoutParams = layoutParams
            }
        }
    }


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

