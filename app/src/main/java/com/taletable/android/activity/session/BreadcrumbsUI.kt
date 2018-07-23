
package com.taletable.android.activity.session


import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.taletable.android.R
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*



/**
 * Session Breadcrumbs UI
 */
class SessionBreadcrumbsUI(val steps : List<String>,
                           val highlightLast : Boolean,
                           val theme : Theme,
                           val context : Context)
{

    fun view() : View
    {
        val layout = this.viewLayout()

        steps.forEachIndexed { index, s ->
            layout.addView(this.stepView(index + 1, s))
        }

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.leftDp    = 15f

        return layout.linearLayout(context)
    }


    private fun stepView(i : Int, stepString : String) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val index               = TextViewBuilder()
        val label               = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.margin.topDp     = 14f

        layout.child(index)
              .child(label)

        // (3 A) Index
        // -------------------------------------------------------------------------------------

        index.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        index.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        index.backgroundResource    = R.drawable.bg_session_step

        val indexColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))

        index.color             = theme.colorOrBlack(indexColorTheme)

        index.font                  = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Bold,
                                                    context)

        index.text                  = i.toString()

        index.gravity               = Gravity.CENTER

        index.sizeSp                = 17f

        index.margin.rightDp        = 10f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text              = stepString

        label.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        val labelHlColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_tint_8"))))

        val labelNormalColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))

        if (i == steps.size && highlightLast)
            label.color             = theme.colorOrBlack(labelHlColorTheme)
        else
            label.color             = theme.colorOrBlack(labelNormalColorTheme)

        label.sizeSp            = 18f

        return layout.linearLayout(context)
    }

}