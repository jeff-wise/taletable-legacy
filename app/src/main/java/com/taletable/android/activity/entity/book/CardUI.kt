
package com.taletable.android.activity.entity.book


import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.ScrollViewBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.book.Book
import com.taletable.android.model.book.BookContent
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.colorOrBlack



class CardUI(val book : Book,
             val content : BookContent,
             val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val entityId = book.entityId()


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------


    fun view() : View
    {
        val layout = this.viewLayout()

        layout.addView(this.headerView())

        layout.addView(this.cardView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS > Header
    // -----------------------------------------------------------------------------------------

    private fun headerView() : LinearLayout
    {
        val layout = this.headerViewLayout()

        layout.addView(this.headerBorderView())

        layout.addView(this.headerTextView())

        layout.addView(this.headerBorderView())

        return layout
    }


    private fun headerViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE


        return layout.linearLayout(context)
    }


    private fun headerTextView() : TextView
    {
        val header                = TextViewBuilder()

        header.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text               = content.title().value

        header.font               = Font.typeface(TextFont.default(),
                                                  TextFontStyle.Medium,
                                                  context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        header.color              = colorOrBlack(colorTheme, entityId)

        header.sizeSp             = 22f

        header.padding.leftDp       = 8f
        header.padding.topDp        = 10f
        header.padding.bottomDp     = 10f

        return header.textView(context)

    }


    private fun headerBorderView() : LinearLayout
    {
        val border              = LinearLayoutBuilder()

        border.width            = LinearLayout.LayoutParams.MATCH_PARENT
        border.heightDp         = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_10"))))
        border.backgroundColor  = colorOrBlack(colorTheme, entityId)

        return border.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS > Card
    // -----------------------------------------------------------------------------------------

    fun cardView() : View
    {
        val scrollView = this.cardScrollView()
        val cardLayout = this.cardLayout()

        content.groups().forEach {
            cardLayout.addView(it.view(book.entityId(), context))
        }

        scrollView.addView(cardLayout)

        return scrollView
    }


    private fun cardScrollView() : ScrollView
    {
        val scrollView          = ScrollViewBuilder()

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height       = LinearLayout.LayoutParams.MATCH_PARENT

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_7"))))
        scrollView.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        return scrollView.scrollView(context)
    }


    private fun cardLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.bottomDp  = 70f

        return layout.linearLayout(context)
    }

}