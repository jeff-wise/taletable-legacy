
package com.kispoko.tome.activity.entity.book


import android.content.Context
import android.graphics.Color
import android.net.LinkAddress
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.kispoko.tome.R.string.name
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.ScrollViewBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.book.Book
import com.kispoko.tome.model.book.BookContent
import com.kispoko.tome.model.sheet.style.BorderEdge
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.EntityBookId
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack



class CardUI(val book : Book,
             val content : BookContent,
             val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val entityId = EntityBookId(this.book.bookId)


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