
package com.kispoko.tome.activity.official.sheets


import android.content.Context
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.theme.ThemeManager



// ---------------------------------------------------------------------------------------------
// SHEET VARIANT
// ---------------------------------------------------------------------------------------------

data class SheetVariant(val name : String, val id : String)


// ---------------------------------------------------------------------------------------------
// VARIANTS VIEW
// ---------------------------------------------------------------------------------------------

class VariantsViewBuilder(val sheetName : String,
                          val variants : List<SheetVariant>,
                          val themeId : ThemeId,
                          val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var selectedIndex : Int = 0


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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor  = ThemeManager.color(themeId, colorTheme)

        return layout.linearLayout(context)
    }


    private fun headerView() : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text             = "Open " + sheetName

        val colorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
        header.color            = ThemeManager.color(themeId, colorTheme)

        header.font             = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        header.sizeSp           = 19f

        header.margin.leftDp    = 10f
        header.margin.rightDp   = 10f
        header.margin.topDp     = 10f

        return header.textView(context)
    }


    private fun buttonsView() : LinearLayout
    {
        val layout = this.buttonsViewLayout()

        val buttonViews : MutableList<TextView> = mutableListOf()

        val selectedTextColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_8")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        val selectedTextColor = ThemeManager.color(themeId, selectedTextColorTheme)

        val unselectedTextColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_18")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        val unselectedTextColor = ThemeManager.color(themeId, unselectedTextColorTheme)

        variants.forEachIndexed { index, variant ->
            Log.d("***VARIANTS VIEW", "$index found variant")
            val buttonView = this.buttonView(variant, index)
            buttonViews.add(buttonView)
            layout.addView(buttonView)
        }

        buttonViews.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (selectedIndex != index)
                {
                    selectedIndex = index
                    buttonViews.forEachIndexed { buttonIndex, buttonView ->
                        if (buttonIndex == selectedIndex)
                        {
                            buttonView.setBackgroundResource(R.drawable.bg_variant_button_selected)
                            if (selectedTextColor != null)
                                buttonView.setTextColor(selectedTextColor)
                        }
                        else
                        {
                            buttonView.setBackgroundResource(R.drawable.bg_variant_button_unselected)
                            if (unselectedTextColor != null)
                                buttonView.setTextColor(unselectedTextColor)
                        }
                    }
                }
            }

        }

        return layout
    }


    private fun buttonsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.margin.topDp     = 10f

        return layout.linearLayout(context)
    }


    private fun buttonView(variant : SheetVariant, index : Int) : TextView
    {
        val button              = TextViewBuilder()

        button.width            = LinearLayout.LayoutParams.MATCH_PARENT
        button.height           = 0
        button.weight           = 1f

        button.text             = variant.name


        if (index == this.selectedIndex)
        {
            val textColorTheme  = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_8")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
            button.color            = ThemeManager.color(themeId, textColorTheme)
        }
        else
        {
            val textColorTheme  = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_18")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
            button.color            = ThemeManager.color(themeId, textColorTheme)
        }

        button.font             = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

//        val bgColorTheme  = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_12")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
//        button.backgroundColor  = ThemeManager.color(themeId, bgColorTheme)

//        if (index == this.selectedIndex)
//            button.backgroundResource   = R.drawable.bg_variant_button_selected
//        else
//            button.backgroundResource   = R.drawable.bg_variant_button_unselected

//        button.strokeWidth      = 1
//        val strokeColorTheme  = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_4")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        button.strokeColor      = ThemeManager.color(themeId, strokeColorTheme)

        button.sizeSp           = 19f

        button.padding.topDp    = 8f
        button.padding.bottomDp = 8f

        button.padding.leftDp   = 8f
        button.padding.rightDp  = 8f

        button.margin.leftDp    = 10f
        button.margin.rightDp   = 10f
        button.margin.bottomDp  = 10f

        return button.textView(context)
    }


}