
package com.kispoko.tome.activity.official.sheets


import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.dialog.AdderDialogFragment
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.theme.ThemeManager



// ---------------------------------------------------------------------------------------------
// SHEET VARIANT
// ---------------------------------------------------------------------------------------------

data class SheetVariant(val name : String,
                        val variantId : String)


// ---------------------------------------------------------------------------------------------
// VARIANTS VIEW
// ---------------------------------------------------------------------------------------------

class VariantsViewBuilder(val variants : List<SheetVariant>,
                          val sheetType : String,
                          val sheetName : String,
                          val sheetPartId : String,
                          val themeId : ThemeId,
                          val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Header
         layout.addView(this.headerView())

        // Buttons
        layout.addView(this.buttonsView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_2"))))
        layout.backgroundColor  = ThemeManager.color(themeId, colorTheme)

        layout.padding.bottomDp = 4f

        return layout.linearLayout(context)
    }


    private fun headerView() : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.textId           = R.string.choose_variant

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        header.color            = ThemeManager.color(themeId, colorTheme)

        header.font             = Font.typeface(TextFont.default(),
                                                TextFontStyle.SemiBold,
                                                context)

        header.sizeSp           = 15f

        header.margin.leftDp    = 8f
        header.margin.rightDp   = 8f
        header.margin.topDp     = 8f
//        header.margin.bottomDp     = 8f

        return header.textView(context)
    }


    private fun buttonsView() : FlexboxLayout
    {
        val layout = this.buttonsViewLayout()

        variants.forEach {
            layout.addView(buttonView(it))
        }

        return layout
    }


    private fun buttonsViewLayout() : FlexboxLayout
    {
        val layout = FlexboxLayoutBuilder()

        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.contentAlignment         = AlignContent.CENTER
        layout.wrap                     = FlexWrap.WRAP

        layout.margin.topDp             = 8f

        layout.padding.leftDp           = 8f
        layout.padding.rightDp          = 8f

        return layout.flexboxLayout(context)
    }


    private fun buttonView(variant : SheetVariant) : TextView
    {
        val button              = TextViewBuilder()

        button.layoutType       = LayoutType.FLEXBOX
        button.width            = FlexboxLayout.LayoutParams.WRAP_CONTENT
        button.height           = FlexboxLayout.LayoutParams.WRAP_CONTENT

        button.text             = variant.name

        button.backgroundColor  = Color.WHITE

        button.corners          = Corners(3.0, 3.0, 3.0, 3.0)

        val textColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_18")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green_90"))))
        button.color            = ThemeManager.color(themeId, textColorTheme)

        button.font             = Font.typeface(TextFont.default(),
                                                TextFontStyle.SemiBold,
                                                context)
        button.sizeSp           = 16f

        button.padding.topDp    = 4f
        button.padding.bottomDp = 4f

        button.padding.leftDp   = 6f
        button.padding.rightDp  = 6f

        button.margin.topDp     = 6f
        button.margin.bottomDp     = 6f
        button.margin.rightDp     = 8f

        button.onClick          = View.OnClickListener {
            val activity = context as AppCompatActivity
            val sheetId = "${this.sheetType}_${this.sheetPartId}_${variant.variantId}"
            val sheetVariantName = "$sheetName (${variant.name})"
            val dialog = OpenSheetDialog.newInstance(sheetVariantName, SheetId(sheetId))
            dialog.show(activity.supportFragmentManager, "")

        }

        return button.textView(context)
    }


}