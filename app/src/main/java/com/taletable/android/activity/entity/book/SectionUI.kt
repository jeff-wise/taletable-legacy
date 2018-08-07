
package com.taletable.android.activity.entity.book


import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.lib.ui.*
import com.taletable.android.model.book.*
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.groups


/**
 * Subsection UI
 */
class SectionUI(val section : BookSection,
                val book : Book,
                val chapterId : BookChapterId,
                val bookActivity : BookActivity,
                val theme : Theme)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val entityId = book.entityId()
    val context = bookActivity


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------


    fun view() : View
    {
        val layout = this.viewLayout()

        layout.addView(this.headerView())

        layout.addView(this.sectionView())

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

        header.text               = section.title().value

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
    // VIEWS > Section
    // -----------------------------------------------------------------------------------------

    fun sectionView() : View
    {
        val scrollView = this.sectionScrollView()
        val cardLayout = this.sectionViewLayout()

        cardLayout.addView(this.contentView(section.introductionContent(book)))

        cardLayout.addView(this.subsectionListView())

        scrollView.addView(cardLayout)

        return scrollView
    }


    private fun sectionScrollView() : ScrollView
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


    private fun sectionViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.bottomDp = 70f

        layout.padding.topDp    = 8f

        layout.margin.leftDp    = 6f
        layout.margin.rightDp   = 6f

        return layout.linearLayout(context)
    }


    // VIEWS > Content
    // --------------------------------------------------------------------------------------------

    private fun contentView(contentList : List<BookContent>) : LinearLayout
    {
        val layout = this.contentViewLayout()

        contentList.forEach { content ->
            groups(content.groupReferences(), book.entityId()).forEach {
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


    // VIEWS > Subsection List
    // -----------------------------------------------------------------------------------------

    private fun subsectionListView() : LinearLayout
    {
        val layout = this.subsectionListViewLayout()

        section.subsections().forEach {
            layout.addView(this.subsectionSummaryView(it))
        }

        return layout
    }


    private fun subsectionListViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.margin.topDp         = 8f

        return layout.linearLayout(context)
    }


    private fun subsectionSummaryView(subsection : BookSubsection) : ViewGroup
    {
        val layout = this.subsectionSummaryViewLayout()

        layout.addView(this.subsectionSummaryTitleView(subsection))

        layout.addView(this.subsectionSummaryIconView())

        layout.setOnClickListener {
            val subsectionReference = BookReferenceSubsection(book.entityId(),
                                                              chapterId,
                                                              section.sectionId(),
                                                              subsection.subsectionId())
            bookActivity.setCurrentBookReference(subsectionReference)
        }

        return layout
    }


    private fun subsectionSummaryViewLayout() : RelativeLayout
    {
        val layout                  = RelativeLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.backgroundColor      = Color.WHITE

        layout.corners              = Corners(1.0, 1.0, 1.0, 1.0)

        layout.padding.topDp        = 10f
        layout.padding.bottomDp     = 10f
        layout.padding.leftDp       = 8f
        layout.padding.rightDp      = 8f

        layout.margin.topDp         = 1f

        return layout.relativeLayout(context)
    }


    private fun subsectionSummaryTitleView(subsection : BookSubsection) : LinearLayout
    {
        val layout = this.subsectionSummaryTitleViewLayout()

        layout.addView(this.subsectionSummaryTitleTextView(subsection.title().value))

        subsection.subtitle().doMaybe {
            layout.addView(this.subsectionSummarySubtitleTextView(it.value))
        }

        return layout
    }


    private fun subsectionSummaryTitleViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun subsectionSummaryTitleTextView(titleString : String) : TextView
    {
        val summary                 = TextViewBuilder()

        summary.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        summary.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        summary.text                = titleString

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


    private fun subsectionSummarySubtitleTextView(subtitleString : String) : TextView
    {
        val subtitle                 = TextViewBuilder()

        subtitle.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        subtitle.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        subtitle.text                = subtitleString

        subtitle.font                = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_18"))))
        subtitle.color               = theme.colorOrBlack(colorTheme)

        subtitle.sizeSp              = 15f

        subtitle.corners             = Corners(2.0, 2.0, 2.0, 2.0)

        subtitle.backgroundColor     = Color.WHITE

        return subtitle.textView(context)
    }


    private fun subsectionSummaryIconView() : LinearLayout
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