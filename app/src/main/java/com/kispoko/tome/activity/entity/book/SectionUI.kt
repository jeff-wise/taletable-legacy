
package com.kispoko.tome.activity.entity.book


import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.ScrollViewBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.book.Book
import com.kispoko.tome.model.book.BookReference
import com.kispoko.tome.model.book.BookReferenceSection
import com.kispoko.tome.model.book.BookSection
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.EntityBookId
import com.kispoko.tome.rts.entity.colorOrBlack
import maybe.Just
import kotlin.text.Typography.section


/**
 * Subsection UI
 */
class SectionUI(val book : Book,
                val sectionReference : BookReferenceSection,
                val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val entityId = EntityBookId(book.bookId)

    var chapterName : String? = null

    var section : BookSection? = null

    init {

        // Chapter Name


        // Section
    }

    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    private fun chapterName() : String?
    {
        val chapterId = sectionReference.chapterId
        val maybeChapter = book.chapter(chapterId)
        return when (maybeChapter) {
            is Just -> maybeChapter.value.title().value
            else    -> null
        }
    }


    private fun section() : BookSection?
    {
        val maybeSection = book.section(sectionReference.chapterId, sectionReference.sectionId)
        return when (maybeSection) {
            is Just -> maybeSection.value
            else    -> null
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        val section = this.section()
        val chapterName = this.chapterName()

        if (section != null && chapterName != null)
        {
            // Header
            layout.addView(this.headerView(section, chapterName))

            // Content
            layout.addView(this.contentView(section))
        }

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

    fun headerView(section : BookSection, chapterName : String) : LinearLayout
    {
        val layout = this.headerViewLayout()

        layout.addView(this.chapterNameView(chapterName))

        layout.addView(this.sectionNameView(section.title.value))

        layout.addView(this.bottomBorderView())

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

        return layout.linearLayout(context)
    }


    fun chapterNameView(chapterNameString : String) : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.text               = chapterNameString

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        name.color              = colorOrBlack(colorTheme, entityId)

        name.sizeSp             = 15f

        name.padding.leftDp   = 8f
        name.padding.rightDp  = 8f

        return name.textView(context)
    }


    fun sectionNameView(sectionTitleString : String) : TextView
    {
        val name                = TextViewBuilder()

        name.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        name.text               = sectionTitleString

        name.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Bold,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("red_90"))))
        name.color              = colorOrBlack(colorTheme, entityId)

        name.sizeSp             = 22f

        name.padding.leftDp   = 8f
        name.padding.rightDp  = 8f

        return name.textView(context)
    }


    private fun bottomBorderView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_7"))))
        layout.backgroundColor  = colorOrBlack(colorTheme, entityId)

        layout.margin.topDp     = 10f

        return layout.linearLayout(context)
    }


    // VIEWS > Content
    // -----------------------------------------------------------------------------------------

    fun contentView(section : BookSection) : ScrollView
    {
        val scrollView = this.contentScrollView()

        scrollView.addView(this.contentGroupsView(section))

        return scrollView
    }


    fun contentScrollView() : ScrollView
    {
        val scrollView = ScrollViewBuilder()

        scrollView.width     = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height    = LinearLayout.LayoutParams.MATCH_PARENT

        return scrollView.scrollView(context)
    }


    fun contentGroupsView(section : BookSection) : LinearLayout
    {
        val layout = this.contentGroupsViewLayout()

        section.body().groups().forEach {
            Log.d("***SECTION UI", "adding group")
            val groupView = it.view(entityId, context)
            layout.addView(groupView)
        }

        return layout
    }


    fun contentGroupsViewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation  = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }

}