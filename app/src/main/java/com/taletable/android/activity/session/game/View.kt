
package com.taletable.android.activity.session.game


import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.taletable.android.R
import com.taletable.android.activity.session.OptionCardSection
import com.taletable.android.activity.session.optionCardView
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.model.theme.*





fun gameView(theme : Theme, context : Context) : View
{
    val layout = gameViewLayout(theme, context)

    layout.addView(gameNavView(theme, context))

    return layout
}


private fun gameViewLayout(theme : Theme, context : Context) : LinearLayout
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


private fun gameNavView(theme : Theme, context : Context) : LinearLayout
{
    val layout = gameNavViewLayout(theme, context)

    layout.addView(optionCardView(R.drawable.icon_user_manual,
                                  R.string.game_basic_info,
                                  R.string.game_basic_info_summary,
                                  listOf(),
                                  theme,
                                  context))


    layout.addView(optionCardView(R.drawable.icon_automation,
                                  R.string.game_engine,
                                  R.string.game_engine_summary,
                                  listOf(OptionCardSection("Functions")),
                                  theme,
                                  context))

    return layout
}



private fun gameNavViewLayout(theme : Theme, context : Context) : LinearLayout
{
    val layout              = LinearLayoutBuilder()

    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.orientation      = LinearLayout.VERTICAL

    return layout.linearLayout(context)

}


