
package com.kispoko.tome.activity.entity.book


import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.book.Book
import com.kispoko.tome.model.book.BookChapter
import com.kispoko.tome.model.book.BookContent
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*



class ChapterUI(val chapter : BookChapter,
                val book : Book,
                val bookActivity : BookActivity,
                val theme : Theme)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val context = bookActivity


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val scrollView = this.scrollView()
        val layout = this.viewLayout()
        scrollView.addView(layout)

        // Title
        layout.addView(this.titleView())

        // Introduction
        layout.addView(this.contentView(chapter.introductionContent(book)))

        // Section List
        layout.addView(this.sectionListView())

        // Conclusion

        return scrollView
    }


    private fun scrollView() : ScrollView
    {
        val scrollView          = ScrollViewBuilder()

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height       = LinearLayout.LayoutParams.MATCH_PARENT

        return scrollView.scrollView(context)

    }


    private fun viewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.margin.leftDp        = 6f
        layout.margin.rightDp       = 6f

        layout.padding.bottomDp     = 70f

        return layout.linearLayout(context)
    }


    // VIEWS > Title
    // --------------------------------------------------------------------------------------------

    private fun titleView() : TextView
    {
        val title                = TextViewBuilder()

        title.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        title.text               = chapter.title().value

        title.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        title.color              = theme.colorOrBlack(colorTheme)

        title.sizeSp             = 28f

        title.corners            = Corners(2.0, 2.0, 2.0, 2.0)

        title.backgroundColor    = Color.WHITE

        title.padding.topDp      = 8f
        title.padding.bottomDp   = 8f
        title.padding.leftDp     = 10f
        title.padding.rightDp    = 10f

        title.margin.topDp       = 10f

        return title.textView(context)
    }


    // VIEWS > Content
    // --------------------------------------------------------------------------------------------

    private fun contentView(contentList : List<BookContent>) : LinearLayout
    {
        val layout = this.contentViewLayout()

        contentList.forEach { content ->
            content.groups().forEach {
                layout.addView(it.view(book.entityId(), context))
            }
        }

        return layout
    }


    private fun contentViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.backgroundColor      = Color.WHITE

        layout.corners              = Corners(2.0, 2.0, 2.0, 2.0)

        layout.padding.topDp        = 8f
        layout.padding.bottomDp     = 8f
        layout.padding.leftDp       = 8f
        layout.padding.rightDp      = 8f

        return layout.linearLayout(context)
    }


    // VIEWS > Section List
    // -----------------------------------------------------------------------------------------

    private fun sectionListView() : LinearLayout
    {
        val layout = this.sectionListViewLayout()

        chapter.sections().forEach {
            layout.addView(this.sectionSummaryView(it.title().value))
        }

        return layout
    }


    private fun sectionListViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun sectionSummaryView(summaryString : String) : ViewGroup
    {
        val layout = this.sectionSummaryViewLayout()

        layout.addView(this.sectionSummaryTextView(summaryString))

        layout.addView(this.sectionSummaryIconView())

        return layout
    }


    private fun sectionSummaryViewLayout() : RelativeLayout
    {
        val layout                  = RelativeLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.backgroundColor      = Color.WHITE

        layout.corners              = Corners(1.0, 1.0, 1.0, 1.0)

        layout.padding.topDp        = 10f
        layout.padding.bottomDp     = 10f
        layout.padding.leftDp       = 8f
        layout.padding.rightDp      = 8f

        layout.margin.topDp         = 1f

        return layout.relativeLayout(context)

    }


    private fun sectionSummaryTextView(summaryString : String) : TextView
    {
        val summary                 = TextViewBuilder()

        summary.layoutType          = LayoutType.RELATIVE
        summary.width               = RelativeLayout.LayoutParams.WRAP_CONTENT
        summary.height              = RelativeLayout.LayoutParams.WRAP_CONTENT

        summary.addRule(RelativeLayout.ALIGN_PARENT_START)
        summary.addRule(RelativeLayout.CENTER_VERTICAL)

        summary.text                = summaryString

        summary.font                = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        summary.color               = theme.colorOrBlack(colorTheme)

        summary.sizeSp              = 19f

        summary.corners             = Corners(2.0, 2.0, 2.0, 2.0)

        summary.backgroundColor     = Color.WHITE

        return summary.textView(context)

    }


    private fun sectionSummaryIconView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val icon                    = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType           = LayoutType.RELATIVE
        layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)
        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp                = 28
        icon.heightDp               = 28

        icon.image                  = R.drawable.icon_chevron_right

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        icon.color               = theme.colorOrBlack(iconColorTheme)

        return layout.linearLayout(context)
    }

}