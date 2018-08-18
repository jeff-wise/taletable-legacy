
package com.taletable.android.activity.entity.book.fragment


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
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
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialThemeLight
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.book
import com.taletable.android.rts.entity.groups
import maybe.Just



/**
 * Book Fragment
 */
class BookFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var bookId : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(bookId : EntityId) : BookFragment
        {
            val fragment = BookFragment()

            val args = Bundle()
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

        this.bookId = arguments?.getSerializable("book_id") as EntityId
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val bookId  = this.bookId
        val context = getContext()

        var view : View? =null

        if (bookId != null && context != null)
        {
            val bookActivity = context as BookActivity
            book(bookId).doMaybe {
                view = BookUI(it, bookActivity, officialThemeLight).view()
            }
        }

        return view
    }


}



class BookUI(val book : Book,
             private val bookActivity : BookActivity,
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

        book.introductionContent().forEach { content ->
            groups(content.groupReferences(), book.entityId()).forEach {
                layout.addView(it.view(book.entityId(), context))
            }
        }

        layout.addView(this.headerView(R.string.chapters))

        layout.addView(this.chapterListView())

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
    // -----------------------------------------------------------------------------------------

    private fun headerView(headerStringId : Int) : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.MATCH_PARENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.backgroundColor  = Color.WHITE

        header.textId           = headerStringId

        header.font             = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        header.color            = theme.colorOrBlack(colorTheme)

        header.sizeSp           = 16f

        header.padding.topDp     = 8f
        header.padding.bottomDp  = 8f

        header.padding.leftDp    = 12f
        header.padding.rightDp   = 12f

        return header.textView(context)
    }

    // VIEWS > Content
    // -----------------------------------------------------------------------------------------

    private fun contentView(content : BookContent) : LinearLayout
    {
        val layout = this.contentViewLayout()

        groups(content.groupReferences(), book.entityId()).forEach {
            layout.addView(it.view(book.entityId(), context))
        }

        //layout.addView(this.contentReadMoreView())

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


    // VIEWS > Chapter List
    // -----------------------------------------------------------------------------------------

    private fun chapterListView() : LinearLayout
    {
        val layout = this.chapterListViewLayout()

        book.chapters().forEach {
            layout.addView(this.chapterSummaryView(it))
        }

        return layout
    }


    private fun chapterListViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun chapterSummaryView(chapter : BookChapter) : ViewGroup
    {
        val layout = this.chapterSummaryViewLayout()

        layout.addView(this.chapterSummaryTextView(chapter.title().value))

        layout.addView(this.chapterSummaryIconView())

        layout.setOnClickListener {
            val chapterReference = BookReferenceChapter(book.entityId(), chapter.chapterId())
            bookActivity.setCurrentBookReference(chapterReference)
        }

        return layout
    }


    private fun chapterSummaryViewLayout() : RelativeLayout
    {
        val layout                  = RelativeLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.backgroundColor      = Color.WHITE

        layout.padding.topDp        = 10f
        layout.padding.bottomDp     = 10f
        layout.padding.leftDp       = 12f
        layout.padding.rightDp      = 14f

        layout.margin.topDp         = 1f

        return layout.relativeLayout(context)

    }


    private fun chapterSummaryTextView(summaryString : String) : TextView
    {
        val summary                 = TextViewBuilder()

        summary.layoutType          = LayoutType.RELATIVE
        summary.width               = RelativeLayout.LayoutParams.WRAP_CONTENT
        summary.height              = RelativeLayout.LayoutParams.WRAP_CONTENT

        summary.addRule(RelativeLayout.ALIGN_PARENT_START)
        summary.addRule(RelativeLayout.CENTER_VERTICAL)

        summary.text                = summaryString

        summary.font                = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Bold,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        summary.color               = theme.colorOrBlack(colorTheme)

        summary.sizeSp              = 18f

        summary.backgroundColor     = Color.WHITE

        return summary.textView(context)

    }


    private fun chapterSummaryIconView() : LinearLayout
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
