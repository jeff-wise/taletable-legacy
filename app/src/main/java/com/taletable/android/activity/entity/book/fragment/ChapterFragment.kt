
package com.taletable.android.activity.entity.book.fragment


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.entity.book.BookActivity
import com.taletable.android.lib.ui.*
import com.taletable.android.model.book.*
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialThemeLight
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.book
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.groups



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
            val bookActivity = context as BookActivity

            book(bookId).doMaybe { book ->
            book.chapter(chapterId).doMaybe { chapter ->
                view = ChapterUI(chapter, book, bookActivity, officialThemeLight).view()
            } }

        }

        return view
    }


}



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

        // Header
        layout.addView(this.headerView())

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_12"))))
        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

        //layout.margin.topDp     = 10f

        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 14f
        layout.padding.topDp    = 16f
        layout.padding.bottomDp = 16f

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
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
        title.color              = theme.colorOrBlack(colorTheme)
        title.color              = Color.WHITE

        title.sizeSp             = 32f

        return title.textView(context)
    }


    // VIEWS > Header > Summary
    // --------------------------------------------------------------------------------------------

    private fun summaryView() : TextView
    {
        val title               = TextViewBuilder()

        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        title.margin.topDp      = 50f

        chapter.summary().doMaybe {
            title.text = it.value
        }

        title.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_10"))))
        title.color              = theme.colorOrBlack(colorTheme)
//        title.color             = Color.WHITE

        title.sizeSp             = 19f

        return title.textView(context)
    }


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

        layout.margin.topDp     = 18f
        layout.margin.bottomDp  = 12f
        layout.margin.rightDp   = 4f

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        icon.color              = theme.colorOrBlack(colorTheme)
        icon.color              = Color.WHITE

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

        return layout.linearLayout(context)
    }


    // VIEWS > Section List
    // -----------------------------------------------------------------------------------------

    private fun sectionListView() : LinearLayout
    {
        val layout = this.sectionListViewLayout()

        chapter.sections().forEach {
            layout.addView(this.sectionSummaryView(it))
        }

        return layout
    }


    private fun sectionListViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.elevation            = 2f

        layout.margin.bottomDp      = 8f


        layout.margin.topDp         = 20f

        return layout.linearLayout(context)
    }


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

        layout.padding.topDp        = 14f
        layout.padding.bottomDp     = 14f
        layout.padding.leftDp       = 10f
        layout.padding.rightDp      = 14f

        layout.margin.topDp         = 1f

        layout.onClick              = View.OnClickListener {
            val sectionReference = BookReferenceSection(book.entityId(),
                                                        this.chapter.chapterId,
                                                        sectionId)
            bookActivity.setCurrentBookReference(sectionReference)
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
        summary.color               = theme.colorOrBlack(colorTheme)

        summary.sizeSp              = 20f

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
