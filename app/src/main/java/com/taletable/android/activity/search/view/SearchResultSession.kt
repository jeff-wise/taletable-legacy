
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



fun searchResultSessionView(theme : Theme, context : Context) : LinearLayout
{
    val layout = searchResultSessionViewLayout(theme, context)

    layout.addView(searchResultSessionHeaderView(theme, context))

    layout.addView(searchResultSessionDescriptionView(theme, context))

    layout.addView(searchResultSessionDividerView(theme, context))

    layout.addView(searchResultSessionFooterView(theme, context))

    return layout
}


private fun searchResultSessionViewLayout(theme : Theme, context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.id               = R.id.layout

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.backgroundResource   = R.drawable.search_session_bg

    layout.orientation      = LinearLayout.VERTICAL

    layout.margin.topDp     = 12f
    layout.margin.leftDp   = 12f
    layout.margin.rightDp  = 12f

    layout.padding.topDp    = 8f
    layout.padding.bottomDp    = 8f
//    layout.padding.leftDp    = 8f
//    layout.padding.rightDp    = 8f

    return layout.linearLayout(context)
}


private fun searchResultSessionHeaderView(theme : Theme, context : Context) : LinearLayout
{
    val layout = searchResultSessionHeaderViewLayout(theme, context)

//    layout.addView(searchResultSessionImageView(theme, context))

    layout.addView(searchResultSessionNameView(theme, context))

    return layout
}


private fun searchResultSessionHeaderViewLayout(theme : Theme, context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.HORIZONTAL

    layout.padding.leftDp    = 8f
    layout.padding.rightDp    = 8f

//    layout.margin.leftDp    = 12f
//    layout.margin.rightDp   = 12f

    return layout.linearLayout(context)
}

private fun searchResultSessionNameView(theme : Theme, context : Context) : TextView
{
    val title               = TextViewBuilder()

    title.id                = R.id.name_view

    title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
    title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

//    title.padding.leftDp    = 12f
    title.layoutGravity        = Gravity.TOP
    //title.padding.topDp     = 10f

    //title.corners           = Corners(3.0, 3.0, 0.0, 0.0)

//    val bgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
//    title.backgroundColor   = theme.colorOrBlack(bgColorTheme)

    title.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Bold,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_12"))))
    title.color           = theme.colorOrBlack(colorTheme)

    title.sizeSp          = 20f

    return title.textView(context)
}


private fun searchResultSessionImageView(theme : Theme, context : Context) : LinearLayout
{
    // 1 | Declarations
    // -------------------------------------------------------------------------

    val layout                  = LinearLayoutBuilder()
    val iconViewBuilder         = ImageViewBuilder()

    // 2 | Layout
    // -------------------------------------------------------------------------

    layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.backgroundResource   = R.drawable.search_session_image_bg

//    layout.corners              = Corners(4.0,4.0,4.0,4.0)

//    layout.padding.topDp        = 10f
//    layout.padding.bottomDp     = 10f
//    layout.padding.leftDp       = 10f
//    layout.padding.rightDp      = 10f

//    val bgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
//    layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

    layout.gravity              = Gravity.CENTER
    layout.layoutGravity        = Gravity.TOP

//    layout.margin.topDp         = 6f

    layout.child(iconViewBuilder)

    // 2 | Icon View Builder
    // -------------------------------------------------------------------------

    iconViewBuilder.widthDp         = 30
    iconViewBuilder.heightDp        = 30

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_14"))))

    iconViewBuilder.image           = R.drawable.icon_book_shelf

    iconViewBuilder.color           = theme.colorOrBlack(iconColorTheme)

    return layout.linearLayout(context)
}


private fun searchResultSessionDescriptionView(theme : Theme, context : Context) : TextView
{
    val viewBuilder               = TextViewBuilder()

    viewBuilder.id                = R.id.description_view

    viewBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    viewBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

//    viewBuilder.padding.leftDp    = 12f
//    viewBuilder.padding.rightDp   = 12f

    viewBuilder.padding.topDp    = 4f
    //viewBuilder.padding.bottomDp = 4f

    viewBuilder.padding.leftDp    = 8f
    viewBuilder.padding.rightDp    = 8f

//    val bgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("white"))))
//    viewBuilder.backgroundColor   = theme.colorOrBlack(bgColorTheme)

    viewBuilder.font              = Font.typeface(TextFont.Roboto,
                                            TextFontStyle.Regular,
                                            context)

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_14"))))
    viewBuilder.color           = theme.colorOrBlack(colorTheme)

    viewBuilder.sizeSp          = 17f

//    viewBuilder.lineSpacingAdd  = 10f
//    viewBuilder.lineSpacingMult = .82f

    return viewBuilder.textView(context)
}



private fun searchResultSessionFooterView(theme : Theme, context : Context) : LinearLayout
{
    val layout = searchResultSessionFooterViewLayout(context)


    layout.addView(searchResultSessionOpenButtonView(theme, context))


    return layout
}


private fun searchResultSessionFooterViewLayout(context : Context) : LinearLayout
{
    val layout                  = LinearLayoutBuilder()

    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation          = LinearLayout.VERTICAL

//    layout.backgroundColor      = Color.WHITE

    layout.padding.topDp        = 8f
    layout.padding.bottomDp        = 8f
    layout.padding.leftDp    = 8f
    layout.padding.rightDp    = 8f

//    layout.margin.leftDp        = 12f
//    layout.margin.rightDp       = 12f

    return layout.linearLayout(context)
}


private fun searchResultSessionDetailsButtonView(theme : Theme, context : Context) : LinearLayout
{

    // | Declarations
    // -----------------------------------------------------------------------------------------

    val layoutBuilder                   = LinearLayoutBuilder()
    val iconViewBuilder                 = ImageViewBuilder()
    val labelViewBuilder                = TextViewBuilder()

    // | Layout
    // -----------------------------------------------------------------------------------------

    layoutBuilder.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
    layoutBuilder.height                = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation           = LinearLayout.HORIZONTAL

    layoutBuilder.padding.topDp         = 8f
    layoutBuilder.padding.bottomDp      = 8f
    layoutBuilder.padding.leftDp        = 12f
    layoutBuilder.padding.rightDp       = 12f

//    layoutBuilder.margin.leftDp         = 10f
    layoutBuilder.margin.rightDp        = 12f

//    layoutBuilder.corners               = Corners(3.0, 3.0, 3.0, 3.0)

    layoutBuilder.gravity               = Gravity.CENTER
    layoutBuilder.layoutGravity         = Gravity.END

//    val bgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
//    layoutBuilder.backgroundColor       = theme.colorOrBlack(bgColorTheme)

    layoutBuilder.child(iconViewBuilder)
                 .child(labelViewBuilder)

    // | Icon
    // -----------------------------------------------------------------------------------------

    iconViewBuilder.widthDp             = 20
    iconViewBuilder.heightDp            = 20

    iconViewBuilder.image               = R.drawable.icon_list

    iconViewBuilder.color               = Color.WHITE

    iconViewBuilder.margin.rightDp      = 6f

    // | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.text               = "DETAILS"

    labelViewBuilder.font               = Font.typeface(TextFont.Roboto,
                                                        TextFontStyle.Medium,
                                                        context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_22"))))
    labelViewBuilder.backgroundColor       = theme.colorOrBlack(labelColorTheme)

    labelViewBuilder.gravity            = Gravity.CENTER

    labelViewBuilder.sizeSp             = 16f

    return layoutBuilder.linearLayout(context)
}


private fun searchResultSessionOpenButtonView(theme : Theme, context : Context) : LinearLayout
{

    // | Declarations
    // -----------------------------------------------------------------------------------------

    val layoutBuilder                   = LinearLayoutBuilder()
    val iconViewBuilder                 = ImageViewBuilder()
    val labelViewBuilder                = TextViewBuilder()

    // | Layout
    // -----------------------------------------------------------------------------------------

    layoutBuilder.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
    layoutBuilder.height                = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation           = LinearLayout.HORIZONTAL

//    layoutBuilder.padding.topDp         = 8f
//    layoutBuilder.padding.bottomDp      = 8f
//    layoutBuilder.padding.leftDp        = 12f
//    layoutBuilder.padding.rightDp       = 12f

//    layoutBuilder.corners               = Corners(3.0, 3.0, 3.0, 3.0)

    layoutBuilder.gravity               = Gravity.CENTER
    layoutBuilder.layoutGravity         = Gravity.START

//    val bgColorTheme = ColorTheme(setOf(
//            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
//    layoutBuilder.backgroundColor       = theme.colorOrBlack(bgColorTheme)

    layoutBuilder.child(iconViewBuilder)
                 .child(labelViewBuilder)

    // | Icon
    // -----------------------------------------------------------------------------------------

    iconViewBuilder.widthDp             = 21
    iconViewBuilder.heightDp            = 21

    iconViewBuilder.image               = R.drawable.icon_open

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
    iconViewBuilder.color               = theme.colorOrBlack(iconColorTheme)

    iconViewBuilder.margin.rightDp      = 6f

    // | Label
    // -----------------------------------------------------------------------------------------

    labelViewBuilder.width              = LinearLayout.LayoutParams.WRAP_CONTENT
    labelViewBuilder.height             = LinearLayout.LayoutParams.WRAP_CONTENT

    labelViewBuilder.text               = context.getString(R.string.open_session).toUpperCase()

    labelViewBuilder.font               = Font.typeface(TextFont.Roboto,
                                                        TextFontStyle.Bold,
                                                        context)

    val labelColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
    labelViewBuilder.color               = theme.colorOrBlack(labelColorTheme)

    labelViewBuilder.gravity            = Gravity.CENTER

    labelViewBuilder.sizeSp             = 17f

    return layoutBuilder.linearLayout(context)
}



fun searchResultSessionDividerView(theme : Theme, context: Context) : LinearLayout
{
    val layoutBuilder               = LinearLayoutBuilder()

    layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.heightDp          = 1

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
    layoutBuilder.backgroundColor   = theme.colorOrBlack(colorTheme)

    layoutBuilder.margin.topDp      = 16f

    return layoutBuilder.linearLayout(context)
}




class SearchResultSessionViewHolder(itemView : View, val theme : Theme, val context : Context)
                        : RecyclerView.ViewHolder(itemView)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layout          : LinearLayout? = null
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

