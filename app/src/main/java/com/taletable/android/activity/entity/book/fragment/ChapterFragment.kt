
package com.taletable.android.activity.entity.book.fragment


import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
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
import com.taletable.android.rts.entity.groups
import maybe.Just
import maybe.Nothing


/**
 * Chapter Fragment
 */
class ChapterFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var chapterId : BookChapterId? = null
    private var bookId    : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(chapterId : BookChapterId, bookId : EntityId) : ChapterFragment
        {
            val fragment = ChapterFragment()

            val args = Bundle()
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

        this.chapterId = arguments?.getSerializable("chapter_id") as BookChapterId
        this.bookId    = arguments?.getSerializable("book_id") as EntityId
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val chapterId = this.chapterId
        val bookId    = this.bookId
        val context   = getContext()

        var view : View? = null

        if (chapterId != null && bookId != null && context != null)
        {
            val sessionActivity = context as SessionActivity

            book(bookId).doMaybe { book ->
            book.chapter(chapterId).doMaybe { chapter ->
                view = ChapterUI(chapter, book, sessionActivity, officialThemeLight).view()
            } }

        }

        return view
    }


}



class ChapterUI(val chapter : BookChapter,
                val book : Book,
                val sessionActivity : SessionActivity,
                val theme : Theme)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val context = sessionActivity


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val scrollView = this.scrollView()
        val layout = this.viewLayout()
        scrollView.addView(layout)

        // Header
        layout.addView(this.headerView())

        layout.addView(this.searchbarView())

        // Section List
        layout.addView(this.sectionListView())


        // Content
        layout.addView(this.contentView(chapter.content(book)))

        return scrollView
    }


    private fun scrollView() : ScrollView
    {
        val scrollView          = ScrollViewBuilder()

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height       = LinearLayout.LayoutParams.MATCH_PARENT

        scrollView.backgroundColor  = Color.WHITE

        return scrollView.scrollView(context)

    }


    private fun viewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.padding.bottomDp     = 70f

        return layout.linearLayout(context)
    }

    // VIEWS > Header
    // --------------------------------------------------------------------------------------------

    private fun headerView() : LinearLayout
    {
        val layout = this.headerViewLayout()

        layout.addView(this.titleView())

        layout.addView(this.summaryView())

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
        layout.padding.bottomDp = 20f

        return layout.linearLayout(context)
    }


    // VIEWS > Header > Title
    // --------------------------------------------------------------------------------------------

    private fun titleView() : TextView
    {
        val title                = TextViewBuilder()

        title.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        title.text               = chapter.title().value

        title.font               = Font.typeface(TextFont.Merriweather,
                                                TextFontStyle.ExtraBold,
                                                context)

        title.color              = Color.WHITE

        title.sizeSp             = 38f

        title.lineSpacingAdd     = 10f
        title.lineSpacingMult    = 0.8f

        return title.textView(context)
    }


    // VIEWS > Header > Summary
    // --------------------------------------------------------------------------------------------

    private fun summaryView() : TextView
    {
        val title               = TextViewBuilder()

        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        title.margin.topDp      = 24f

        chapter.summary().doMaybe {
            title.text = it.value
        }

        title.font              = Font.typeface(TextFont.Merriweather,
                                                TextFontStyle.Light,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_8"))))
        title.color             = theme.colorOrBlack(colorTheme)

        title.sizeSp            = 17f

        return title.textView(context)
    }



    // VIEWS > Header > Toolbar
    // --------------------------------------------------------------------------------------------

    private fun toolbarView() : LinearLayout
    {
        val layout = toolbarViewLayout()

        layout.addView(toolbarButtonView(R.drawable.icon_bookmark_filled, 19, R.string.save))

        layout.addView(toolbarButtonView(R.drawable.icon_questions, 20, R.string.help))

        return layout
    }


    private fun toolbarViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.topDp    = 32f

        return layout.linearLayout(context)
    }


    private fun toolbarButtonView(iconId : Int, iconSize : Int, labelId : Int) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                 = LinearLayoutBuilder()
        val icon                        = ImageViewBuilder()
        val label                       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.corners             = Corners(3.0, 3.0, 3.0, 3.0)

        layout.padding.topDp       = 8f
        layout.padding.bottomDp    = 8f
        layout.padding.leftDp      = 12f
        layout.padding.rightDp     = 14f

        layout.gravity             = Gravity.CENTER

        layout.margin.rightDp       = 12f

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_16"))))
        layout.backgroundColor     = theme.colorOrBlack(bgColorTheme)

        layout.child(icon)
              .child(label)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 20
        icon.heightDp           = 20

        icon.image              = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_6"))))
        icon.color              = theme.colorOrBlack(iconColorTheme)
        //icon.color              = Color.WHITE

        icon.margin.rightDp     = 6f

        // (3) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Bold,
                                                context)

        label.textId            = labelId

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_6"))))
        label.color             = theme.colorOrBlack(labelColorTheme)
        //label.color             = Color.WHITE

        label.sizeSp            = 18f

        return layout.linearLayout(context)
    }


    // VIEWS > Header > Searchbar
    // --------------------------------------------------------------------------------------------

    private fun searchbarView() : LinearLayout
    {
        val layout = this.searchbarViewLayout()

        val buttonView = this.searchbarButtonView(R.drawable.icon_bookmark)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            layout.translationZ = 10000f
            buttonView.translationZ = 10000f
        }

        layout.addView(buttonView)
//        layout.addView(this.searchbarButtonView(R.drawable.icon_share))
//        layout.addView(this.searchbarButtonView(R.drawable.icon_ellipsis_filled))

        return layout
    }


    private fun searchbarViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        //layout.backgroundResource   = R.drawable.bg_button_book_search_bg

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_4"))))
        layout.backgroundColor  = Color.TRANSPARENT

//        layout.margin.topDp     = 40f
//        layout.margin.bottomDp  = 12f
        layout.margin.rightDp   = 14f

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL or Gravity.END
//        layout.layoutGravity    = Gravity.END

        layout.margin.topDp         = -28f

        return layout.linearLayout(context)
    }


    private fun searchbarButtonView(iconId : Int) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val outerLayout                 = LinearLayoutBuilder()
        val innerLayout                 = LinearLayoutBuilder()
        val icon                        = ImageViewBuilder()
        val label                       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------


        outerLayout.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        outerLayout.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        outerLayout.corners             = Corners(6.0, 6.0, 6.0, 6.0)

        outerLayout.orientation         = LinearLayout.HORIZONTAL

        outerLayout.padding.topDp       = 8f
        outerLayout.padding.bottomDp    = 8f
        outerLayout.padding.leftDp      = 8f
        outerLayout.padding.rightDp     = 8f

        val outerBgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_13"))))
        outerLayout.backgroundColor     = theme.colorOrBlack(outerBgColorTheme)

        outerLayout.child(innerLayout)

        innerLayout.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        innerLayout.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        innerLayout.corners             = Corners(4.0, 4.0, 4.0, 4.0)

        innerLayout.padding.topDp       = 12f
        innerLayout.padding.bottomDp    = 12f
        innerLayout.padding.leftDp      = 12f
        innerLayout.padding.rightDp     = 14f

        innerLayout.gravity             = Gravity.CENTER_VERTICAL

        val innerBgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_green"))))
        innerLayout.backgroundColor     = theme.colorOrBlack(innerBgColorTheme)

        innerLayout.child(icon)
                   .child(label)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 19
        icon.heightDp           = 19

        icon.image              = R.drawable.icon_search

//        val iconColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_4"))))
//        icon.color              = theme.colorOrBlack(iconColorTheme)
        icon.color              = Color.WHITE

        icon.margin.rightDp     = 6f

        // (3) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Bold,
                                                context)

        label.textId            = R.string.search

//        val labelColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_8"))))
//        label.color             = theme.colorOrBlack(labelColorTheme)
        label.color             = Color.WHITE

        label.sizeSp            = 19f

        return outerLayout.linearLayout(context)
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

        return layout.linearLayout(context)
    }


    // VIEWS > Section List
    // -----------------------------------------------------------------------------------------

//    private fun sectionListView() : LinearLayout
//    {
//        val layout = this.sectionListViewLayout()
//
//        chapter.sections().forEach {
//            layout.addView(this.sectionSummaryView(it))
//        }
//
//        return layout
//    }


    private fun sectionSummaryView(section : BookSection) : ViewGroup
    {
        val layout = this.sectionSummaryViewLayout(section.sectionId)

        layout.addView(this.sectionSummaryTextView(section.title().value))

        layout.addView(this.sectionSummaryIconView())

        return layout
    }


    private fun sectionSummaryViewLayout(sectionId : BookSectionId) : RelativeLayout
    {
        val layout                  = RelativeLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.backgroundColor      = Color.WHITE

        layout.padding.topDp        = 18f
        layout.padding.bottomDp     = 18f
        layout.padding.leftDp       = 16f
        layout.padding.rightDp      = 16f

        layout.margin.bottomDp      = 1f

        layout.onClick              = View.OnClickListener {
            val sectionReference = BookReferenceSection(book.entityId(),
                                                        this.chapter.chapterId,
                                                        sectionId)
            sessionActivity.setCurrentBookReference(sectionReference)
        }


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

        summary.font                = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Regular,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_10"))))
        summary.color               = theme.colorOrBlack(colorTheme)

        summary.sizeSp              = 19f

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

        icon.widthDp                = 17
        icon.heightDp               = 17

        icon.image                  = R.drawable.icon_arrow_forward

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_13"))))
        icon.color               = theme.colorOrBlack(iconColorTheme)

        return layout.linearLayout(context)
    }



    // VIEWS > Section List
    // -----------------------------------------------------------------------------------------

    private fun sectionListView() : LinearLayout
    {
        val layout = this.sectionListViewLayout()

        val noGroup : MutableList<BookSection> = mutableListOf()
        val byGroup : MutableMap<BookSectionGroup,MutableList<BookSection>> = mutableMapOf()

        chapter.sections().forEach {
            val group = it.group()
            when (group) {
                is Just -> {
                    if (!byGroup.containsKey(group.value)) {
                        byGroup[group.value] = mutableListOf()
                    }
                    byGroup[group.value]?.add(it)
                }
                is Nothing -> {
                    noGroup.add(it)
                }
            }

        }

        noGroup.forEach {
            layout.addView(this.sectionSummaryView(it))
        }

        byGroup.keys.forEach { group ->

            layout.addView(this.sectionGroupHeaderView(group.value))

            byGroup[group]?.forEach {
                layout.addView(this.sectionSummaryView(it))
            }
        }

        return layout
    }


    private fun sectionListViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

        return layout.linearLayout(context)
    }


    private fun sectionGroupHeaderView(header : String) : LinearLayout
    {
        // | Declarations
        // -------------------------------------------------------------------------------------

        val layoutBuilder               = LinearLayoutBuilder()
        val headerViewBuilder           = TextViewBuilder()

        // | Layout Builder
        // -------------------------------------------------------------------------------------

        layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
        layoutBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        layoutBuilder.padding.topDp     = 20f
        layoutBuilder.padding.bottomDp  = 4f
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

