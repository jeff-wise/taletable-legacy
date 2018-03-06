
package com.kispoko.tome.activity.entity.book


import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.book.Book
import com.kispoko.tome.model.book.BookReference
import com.kispoko.tome.model.book.BookSubsection
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.EntityBookId
import com.kispoko.tome.rts.entity.colorOrBlack
import maybe.Just



/**
 * Subsection UI
 */
class SubsectionUI(val book : Book,
                   val bookReference : BookReference,
                   val subsection : BookSubsection,
                   val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val entityId = EntityBookId(bookReference.bookId)

    var sectionName : String? = null
    var subsectionName : String? = null

    // Get section nmae
    init {
        val chapterId = bookReference.chapterId
        val sectionId = bookReference.sectionId.toNullable()
        if (sectionId != null) {
            val maybeSection = book.section(chapterId, sectionId)
            when (maybeSection) {
                is Just -> this.sectionName = maybeSection.value.title.value
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        return layout
    }


    fun viewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    // VIEWS > Name
    // -----------------------------------------------------------------------------------------

    fun headerView() : LinearLayout
    {
        val layout = this.headerViewLayout()

        layout.addView(sectionNameView())

        layout.addView(subsectionNameView())

        return layout
    }


    fun headerViewLayout() : LinearLayout
    {
        val layout
                = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f
        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        return layout.linearLayout(context)
    }


    fun sectionNameView() : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.text               = this.sectionName

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        name.color              = colorOrBlack(colorTheme, entityId)

        name.sizeSp             = 15f

        return name.textView(context)
    }


    fun subsectionNameView() : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.text               = subsection.title().value

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        name.color              = colorOrBlack(colorTheme, entityId)

        name.sizeSp             = 20f

        return name.textView(context)
    }


}
