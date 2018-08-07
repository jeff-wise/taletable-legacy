
package com.taletable.android.activity


import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.taletable.android.R
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.ImageViewBuilder
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialThemeLight



/**
 * App Options UI (Sidebar)
 */
class AppOptionsUI(val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val theme: Theme = officialThemeLight


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : LinearLayout
    {
        val layout = this.viewLayout()

        // Switch Sessions
        layout.addView(this.buttonView(R.drawable.icon_sync,
                                       R.string.switch_session))

        // Learn / Tutorials
        layout.addView(this.buttonView(R.drawable.icon_book,
                                       R.string.tutorials))

        // Home
        layout.addView(this.buttonView(R.drawable.icon_house,
                                       R.string.home))

        // News / Updates
        layout.addView(this.buttonView(R.drawable.icon_rss_feed,
                                       R.string.news_slash_updates))

        // Tools
        layout.addView(this.buttonView(R.drawable.icon_wrench,
                                       R.string.tools))

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.topDp    = 25f

        return layout.linearLayout(context)
    }


    private fun buttonView(iconId: Int,
                           labelId: Int,
                           onClick: View.OnClickListener? = null) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()
        val label               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

//        layout.backgroundColor  = Color.WHITE

//        layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

        layout.padding.leftDp    = 10f
        layout.padding.rightDp  = 10f

        layout.margin.leftDp    = 6f
//        layout.margin.rightDp   = 2f

//        layout.margin.topDp     = 4f

        layout.onClick          = onClick

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 19
        icon.heightDp           = 19

        icon.image              = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
        icon.color              = theme.colorOrBlack(iconColorTheme)

        icon.margin.rightDp     = 16f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId                = labelId

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
        label.color                 = theme.colorOrBlack(colorTheme)

        label.font                  = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Medium,
                                                    context)

        label.padding.bottomDp      = 1f

        label.sizeSp                = 17f

        return layout.linearLayout(context)
    }
}
