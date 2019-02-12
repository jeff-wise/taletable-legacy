
package com.taletable.android.activity.entity.book.fragment


import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.taletable.android.R
import com.taletable.android.activity.session.SessionActivity
import com.taletable.android.lib.ui.*
import com.taletable.android.model.book.*
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialThemeLight
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.book
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.groups
import maybe.Just
import maybe.Nothing


/**
 * Section Fragment
 */
class SectionFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var sectionId : BookSectionId? = null
    private var chapterId : BookChapterId? = null
    private var bookId    : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(sectionId : BookSectionId,
                        chapterId : BookChapterId,
                        bookId : EntityId) : SectionFragment
        {
            val fragment = SectionFragment()

            val args = Bundle()
            args.putSerializable("section_id", sectionId)
            args.putSerializable("chapter_id", chapterId)
            args.putSerializable("book_id", bookId)
            fragment.arguments = args

            return fragment
        }
    }


    // -----------------------------------------------------------------------------------------
    // FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        this.sectionId = arguments?.getSerializable("section_id") as BookSectionId
        this.chapterId = arguments?.getSerializable("chapter_id") as BookChapterId
        this.bookId    = arguments?.getSerializable("book_id") as EntityId
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val sectionId = this.sectionId
        val chapterId = this.chapterId
        val bookId    = this.bookId
        val context   = getContext()

        var view : View? = null

        if (sectionId != null && chapterId != null && bookId != null && context != null)
        {
            val sessionActivity = context as SessionActivity

            book(bookId).doMaybe { book ->
            book.chapter(chapterId).doMaybe { chapter ->
            book.section(chapterId, sectionId).doMaybe { section ->
                view = SectionUI(section, chapter, book, sessionActivity, officialThemeLight).view()
            } } }

        }

        return view
    }


}



/**
 * Section UI
 */
class SectionUI(val section : BookSection,
                val chapter : BookChapter,
                val book : Book,
                val sessionActivity : SessionActivity,
                val theme : Theme)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val entityId = book.entityId()
    val context = sessionActivity


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------


    fun view() : View
    {
        val layout = this.viewLayout()

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

    // VIEWS > Header
    // --------------------------------------------------------------------------------------------

    private fun headerView() : LinearLayout
    {
        val layout = this.headerViewLayout()

        layout.addView(this.titleView())

        layout.addView(this.toolbarView())

        return layout
    }


    private fun headerViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_13"))))
        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

        //layout.margin.topDp     = 10f

        layout.padding.leftDp   = 15f
        layout.padding.rightDp  = 15f
        layout.padding.topDp    = 14f
        layout.padding.bottomDp = 14f

        return layout.linearLayout(context)
    }


    // VIEWS > Header > Title
    // --------------------------------------------------------------------------------------------

    private fun titleView() : TextView
    {
        val title                = TextViewBuilder()

        title.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        title.text               = section.title().value

        title.font               = Font.typeface(TextFont.Merriweather,
                                                 TextFontStyle.ExtraBold,
                                                 context)

        title.color              = Color.WHITE

        title.sizeSp             = 36f

        title.lineSpacingAdd     = 10f
        title.lineSpacingMult    = 0.8f

        return title.textView(context)
    }


    // VIEWS > Header > Summary
    // --------------------------------------------------------------------------------------------

//    private fun summaryView() : TextView
//    {
//        val title               = TextViewBuilder()
//
//        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
//        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        title.margin.topDp      = 20f
//
//        section.summary().doMaybe {
//            title.text = it.value
//        }
//
//        title.font              = Font.typeface(TextFont.Garamond,
//                                                TextFontStyle.Regular,
//                                                context)
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_8"))))
//        title.color              = theme.colorOrBlack(colorTheme)
//
//        title.sizeSp             = 22f
//
//        return title.textView(context)
//    }


    // VIEWS > Header > Toolbar
    // --------------------------------------------------------------------------------------------

    private fun toolbarView() : LinearLayout
    {
        val layout = this.toolbarViewLayout()

        layout.addView(this.toolbarButtonView(R.drawable.icon_bookmark))
        layout.addView(this.toolbarButtonView(R.drawable.icon_share))
        layout.addView(this.toolbarButtonView(R.drawable.icon_ellipsis_filled))

        return layout
    }


    private fun toolbarViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.margin.topDp     = 30f
        layout.margin.bottomDp  = 12f
        layout.margin.rightDp   = 12f

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL
        layout.layoutGravity    = Gravity.END

        return layout.linearLayout(context)
    }


    private fun toolbarButtonView(iconId : Int) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.margin.leftDp    = 26f

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 21
        icon.heightDp           = 21

        icon.image              = iconId

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_8"))))
        icon.color              = theme.colorOrBlack(colorTheme)
        //icon.color              = Color.WHITE

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS > Section
    // -----------------------------------------------------------------------------------------

    fun sectionView() : View
    {
        val scrollView = this.sectionScrollView()
        val cardLayout = this.sectionViewLayout()

        cardLayout.addView(this.headerView())

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
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

        return layout.linearLayout(context)
    }


    // VIEWS > Content
    // --------------------------------------------------------------------------------------------

    private fun contentView(contentList : List<BookContent>) : LinearLayout
    {
        val layout = this.contentViewLayout()

        contentList.forEach { content ->
            groups(content.groupReferences(), book.entityId()).forEach {
                layout.addView(it.group.view(book.entityId(), context))
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

        //layout.corners              = Corners(2.0, 2.0, 2.0, 2.0)

//        layout.padding.topDp        = 16f
//        layout.padding.bottomDp     = 16f
//        layout.padding.leftDp       = 8f
//        layout.padding.rightDp      = 8f

        return layout.linearLayout(context)
    }


    // VIEWS > Subsection List
    // -----------------------------------------------------------------------------------------

    private fun subsectionListView() : LinearLayout
    {
        val layout = this.subsectionListViewLayout()

        val noGroup : MutableList<BookSubsection> = mutableListOf()
        val byGroup : MutableMap<BookSubsectionGroup,MutableList<BookSubsection>> = mutableMapOf()

        section.subsections().forEach {
            val group = it.group()
            when (group) {
                is Just    -> {
                    if (!byGroup.containsKey(group.value)) {
                        byGroup[group.value] = mutableListOf()
                    }
                    byGroup[group.value]?.add(it)
                }
                is Nothing -> noGroup.add(it)
            }

        }

        noGroup.forEach {
            layout.addView(this.subsectionSummaryView(it))
        }

        byGroup.keys.forEach { group ->

            layout.addView(this.subsectionGroupHeaderView(group.value))

            byGroup[group]?.forEach {
                layout.addView(this.subsectionSummaryView(it))
            }
        }

        return layout
    }


    private fun subsectionListViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun subsectionSummaryView(subsection : BookSubsection) : ViewGroup
    {
        val layout = this.subsectionSummaryViewLayout()

        layout.addView(this.subsectionSummaryTitleView(subsection))

        layout.addView(this.subsectionSummaryIconView())

        layout.setOnClickListener {
            val subsectionReference = BookReferenceSubsection(book.entityId(),
                                                              chapter.chapterId,
                                                              section.sectionId(),
                                                              subsection.subsectionId())
            sessionActivity.setCurrentBookReference(subsectionReference)
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

        layout.padding.topDp        = 16f
        layout.padding.bottomDp     = 16f
        layout.padding.leftDp       = 16f
        layout.padding.rightDp      = 16f

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

        summary.font                = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Regular,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
        summary.color               = theme.colorOrBlack(colorTheme)

        summary.sizeSp              = 19f

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

        icon.widthDp                = 17
        icon.heightDp               = 17

        icon.image                  = R.drawable.icon_arrow_forward

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        icon.color               = theme.colorOrBlack(iconColorTheme)

        return layout.linearLayout(context)
    }


    private fun subsectionGroupHeaderView(header : String) : LinearLayout
    {
        // | Declarations
        // -------------------------------------------------------------------------------------

        val layoutBuilder               = LinearLayoutBuilder()
        val headerViewBuilder           = TextViewBuilder()

        // | Layout Builder
        // -------------------------------------------------------------------------------------

        layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
        layoutBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        layoutBuilder.padding.topDp     = 16f
        layoutBuilder.padding.bottomDp  = 8f
        layoutBuilder.padding.leftDp    = 16f
        layoutBuilder.padding.rightDp   = 16f

        layoutBuilder.backgroundColor   = Color.WHITE

        layoutBuilder.child(headerViewBuilder)

        // | Header View Builder
        // -------------------------------------------------------------------------------------

        headerViewBuilder.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        headerViewBuilder.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        headerViewBuilder.text          = header

        headerViewBuilder.font          = Font.typeface(TextFont.RobotoCondensed,
                                                        TextFontStyle.Bold,
                                                        context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_18"))))
        headerViewBuilder.color         = theme.colorOrBlack(colorTheme)

        headerViewBuilder.sizeSp        = 16f

        return layoutBuilder.linearLayout(context)
    }
}
