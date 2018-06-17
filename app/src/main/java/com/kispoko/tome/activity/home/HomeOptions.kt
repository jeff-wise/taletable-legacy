
package com.kispoko.tome.activity.home


import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*



class HomeOptionsUI(val theme : Theme,
                    val activity : AppCompatActivity)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val context = activity


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : LinearLayout
    {
        val layout = this.viewLayout()

        // News / Updates
        val newsOnClick = View.OnClickListener {  }
        layout.addView(this.buttonView(R.drawable.icon_rss_feed, R.string.news_slash_updates, newsOnClick))

        // Feedback
        val feedbackOnClick = View.OnClickListener {  }
        layout.addView(this.buttonView(R.drawable.icon_announcement, R.string.feedback, feedbackOnClick))

        // Upgrades
        val upgradesOnClick = View.OnClickListener {  }
        layout.addView(this.buttonView(R.drawable.icon_monetization, R.string.upgrades, upgradesOnClick))

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.topDp    = 40f

        return layout.linearLayout(context)
    }


    private fun buttonView(iconId : Int,
                           labelId : Int,
                           onClick : View.OnClickListener? = null) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val icon            = ImageViewBuilder()
        val label           = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.topDp    = 12f
        layout.padding.bottomDp = 12f

        layout.margin.leftDp    = 16f
        layout.margin.rightDp   = 10f

        layout.onClick          = onClick

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 21
        icon.heightDp           = 21

        icon.image              = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color              = theme.colorOrBlack(iconColorTheme)

        icon.margin.rightDp     = 18f
        icon.margin.topDp       = 1f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = labelId

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color             = theme.colorOrBlack(colorTheme)

        label.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        label.padding.bottomDp  = 1f

        label.sizeSp            = 17f


        return layout.linearLayout(context)
    }


}