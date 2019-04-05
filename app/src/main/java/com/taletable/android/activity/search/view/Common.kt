
package com.taletable.android.activity.search.view


import android.content.Context
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.taletable.android.R
import com.taletable.android.lib.ui.ImageViewBuilder
import com.taletable.android.lib.ui.LayoutType
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.model.theme.*



fun searchResultAddIconView(theme : Theme, context : Context) : LinearLayout
{
    // 1 | Declarations
    // -----------------------------------------------------------------------------------------

    val layout                  = LinearLayoutBuilder()
    val iconView                = ImageViewBuilder()

    // 2 | Layout
    // -----------------------------------------------------------------------------------------

    layout.layoutType           = LayoutType.RELATIVE
    layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
    layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

    layout.margin.rightDp       = 4f

    layout.addRule(RelativeLayout.CENTER_VERTICAL)
    layout.addRule(RelativeLayout.ALIGN_PARENT_END)

    layout.child(iconView)

    // 3 | Icon
    // -----------------------------------------------------------------------------------------

    iconView.widthDp            = 21
    iconView.heightDp           = 21

    iconView.image              = R.drawable.icon_arrow_up_left

    val colorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_16"))))
    iconView.color              = theme.colorOrBlack(colorTheme)

    return layout.linearLayout(context)
}
