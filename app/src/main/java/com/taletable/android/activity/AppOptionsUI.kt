
package com.taletable.android.activity


import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.home.HomeActivity
import com.taletable.android.activity.session.SavedSessionListActivity
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.ImageViewBuilder
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialThemeLight



/**
 * App Options UI (Sidebar)
 */
class AppOptionsUI(val activity : AppCompatActivity)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val theme : Theme = officialThemeLight

    val context = activity


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : LinearLayout
    {
        val layout = this.viewLayout()

        layout.addView(this.headerView(R.string.navigate))

        // Home
        val homeButtonOnClick = View.OnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            activity.startActivity(intent)
        }
        layout.addView(this.buttonView(R.drawable.icon_house,
                                       R.string.home,
                                       homeButtonOnClick))

        // Switch Sessions
        val switchSessionButtonOnClick = View.OnClickListener {
            val intent = Intent(activity, SavedSessionListActivity::class.java)
            activity.startActivity(intent)
        }
        layout.addView(this.buttonView(R.drawable.icon_sync,
                                       R.string.switch_session,
                                       switchSessionButtonOnClick))

        // Learn / Tutorials
//        layout.addView(this.buttonView(R.drawable.icon_book,
////                                       R.string.tutorials))


        // News / Updates
//        layout.addView(this.buttonView(R.drawable.icon_rss_feed,
//                                       R.string.news_slash_updates))

        // Tools
//        layout.addView(this.buttonView(R.drawable.icon_wrench,
//                                       R.string.tools))

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.topDp    = 30f

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

        layout.backgroundColor  = Color.WHITE

//        layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.topDp    = 14f
        layout.padding.bottomDp = 14f

        layout.margin.bottomDp  = 1f

        layout.padding.leftDp   = 12f
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

        icon.margin.rightDp     = 18f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId                = labelId

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        label.color                 = theme.colorOrBlack(colorTheme)

        label.font                  = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Bold,
                                                    context)

        label.padding.bottomDp      = 1f

        label.sizeSp                = 17f

        return layout.linearLayout(context)
    }



    private fun headerView(labelId : Int) : TextView
    {
        val header                  = TextViewBuilder()

        header.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        header.margin.leftDp        = 12f
        header.margin.bottomDp      = 6f
        header.margin.topDp         = 8f

        header.textId               = labelId

        val headerColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        header.color                = theme.colorOrBlack(headerColorTheme)

        header.font                 = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Regular,
                                                    context)

        header.sizeSp               = 16f

        return header.textView(context)
    }

}
