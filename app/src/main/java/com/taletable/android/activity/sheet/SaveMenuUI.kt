
package com.taletable.android.activity.sheet


import android.content.Intent
import android.support.design.widget.BottomSheetBehavior
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.ImageViewBuilder
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*



class SaveMenuUI(val sheetActivity : SheetActivity, val theme : Theme)
{


    val context = sheetActivity

    fun view() : View
    {
        val layout = this.viewLayout()

        layout.addView(this.topBorderView())

        layout.addView(this.messageView())

        val onSave = View.OnClickListener {
            val intent = Intent(sheetActivity, SaveSheetActivity::class.java)

            val sheetId = sheetActivity.sheetId
            if (sheetId != null)
                intent.putExtra("sheet_id", sheetId)
            sheetActivity.startActivity(intent)
        }
        layout.addView(this.buttonView(R.drawable.icon_save, R.string.save_a_copy_of_sheet, onSave))

        val onDismiss = View.OnClickListener {
            sheetActivity.bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        }
        layout.addView(this.buttonView(R.drawable.icon_block, R.string.dont_save_save_later, onDismiss))

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
        layout.backgroundColor     = theme.colorOrBlack(bgColorTheme)
//        layout.backgroundColor  = Color.WHITE

        layout.padding.bottomDp = 12f

        return layout.linearLayout(context)
    }


    private fun topBorderView(): LinearLayout
    {
        val divider                 = LinearLayoutBuilder()

        divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp            = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_9"))))
        divider.backgroundColor     = theme.colorOrBlack(colorTheme)

        return divider.linearLayout(context)
    }


    private fun messageView() : TextView
    {
        val message                  = TextViewBuilder()

        message.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        message.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        message.text                 = "Sheets are not saved by default."

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_20"))))
        message.color                = theme.colorOrBlack(colorTheme)

        message.font                 = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        message.sizeSp               = 13f

        message.margin.leftDp        = 14f
        message.margin.topDp         = 6f
        message.margin.bottomDp      = 8f

        return message.textView(context)
    }


    private fun buttonView(iconId : Int,
                           labelId : Int,
                           onClickListener : View.OnClickListener) : LinearLayout
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

        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f

        layout.margin.leftDp    = 14f
        layout.margin.rightDp   = 10f

        layout.onClick          = onClickListener

        layout.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 19
        icon.heightDp       = 19

        icon.image          = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_4"))))
        icon.color          = theme.colorOrBlack(iconColorTheme)

        icon.margin.rightDp = 18f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = labelId

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        label.color             = theme.colorOrBlack(colorTheme)

        label.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        label.padding.bottomDp  = 1f

        label.sizeSp            = 18f


        return layout.linearLayout(context)
    }




}
