

package com.taletable.android.activity.session.campaign


import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.taletable.android.R
import com.taletable.android.activity.session.optionCardView
import com.taletable.android.lib.ui.*
import com.taletable.android.model.theme.*



fun campaignView(theme : Theme, context : Context) : View
{
    val layout = campaignViewLayout(theme, context)

    layout.addView(campaignNavView(theme, context))

    return layout
}


private fun campaignViewLayout(theme : Theme, context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

    layout.orientation      = LinearLayout.VERTICAL

    val bgColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
    layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

    layout.padding.topDp    = 15f

    layout.padding.leftDp   = 6f
    layout.padding.rightDp  = 6f

    return layout.linearLayout(context)
}


private fun campaignNavView(theme : Theme, context : Context) : LinearLayout
{
    val layout = campaignNavViewLayout(theme, context)

    layout.addView(optionCardView(R.drawable.icon_user_manual,
                                  R.string.campaign_basic_info,
                                  R.string.campaign_basic_info_summary,
                                  listOf(),
                                  theme,
                                  context))

    return layout
}



private fun campaignNavViewLayout(theme : Theme, context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.VERTICAL

    return layout.linearLayout(context)

}


