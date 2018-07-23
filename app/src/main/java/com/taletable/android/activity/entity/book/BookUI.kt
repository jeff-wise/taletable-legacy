
package com.taletable.android.activity.entity.book


import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.lib.ui.*
import com.taletable.android.model.book.Book
import com.taletable.android.model.book.BookChapter
import com.taletable.android.model.book.BookContent
import com.taletable.android.model.book.BookReferenceChapter
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*



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

        layout.addView(this.titleView())

        layout.addView(this.abstractView())

        layout.addView(this.authorsView())

        layout.addView(this.headerView(R.string.introduction))

        book.introductionContent().firstOrNull()?.let {
            Log.d("***BOOK UI", "adding content view")
            layout.addView(this.contentView(it))
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

        title.text               = book.bookInfo().title().value

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


    // VIEWS > Abstract
    // --------------------------------------------------------------------------------------------

    private fun abstractView() : TextView
    {
        val abstract                = TextViewBuilder()

        abstract.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        abstract.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        abstract.text               = book.bookInfo().abstract().value

        abstract.font               = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        abstract.color              = theme.colorOrBlack(colorTheme)

        abstract.sizeSp             = 18f

        abstract.corners            = Corners(2.0, 2.0, 2.0, 2.0)

        abstract.backgroundColor    = Color.WHITE

        abstract.padding.topDp      = 8f
        abstract.padding.bottomDp   = 8f
        abstract.padding.leftDp     = 8f
        abstract.padding.rightDp    = 8f

        abstract.margin.topDp       = 8f

        return abstract.textView(context)
    }


    // VIEWS > Authors
    // -----------------------------------------------------------------------------------------

    private fun authorsView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val header                  = TextViewBuilder()
        val authors                 = TextViewBuilder()

        val authorList              = book.bookInfo().authors()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.backgroundColor      = Color.WHITE

        layout.padding.topDp        = 8f
        layout.padding.bottomDp     = 8f
        layout.padding.leftDp       = 8f
        layout.padding.rightDp      = 8f

        layout.margin.topDp         = 8f

        layout.corners              = Corners(2.0, 2.0, 2.0, 2.0)

        layout.child(header)
              .child(authors)

        // (3) Header
        // -------------------------------------------------------------------------------------

        header.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT

//        if (authorList.size <= 1)
//            header.text             = context.getString(R.string.author).toUpperCase()
//        else
//            header.text             = context.getString(R.string.authors).toUpperCase()

        header.text             = context.getString(R.string.written_by).toUpperCase()

        header.font                 = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Bold,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        header.color                = theme.colorOrBlack(colorTheme)

        header.sizeSp               = 13f

        header.margin.bottomDp      = 4f

        // (4) Authors
        // -------------------------------------------------------------------------------------

        authors.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        authors.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        authors.text                = book.bookInfo().authorListString()

        authors.font                = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Medium,
                                                    context)

        val authorsColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        authors.color               = theme.colorOrBlack(authorsColorTheme)

        authors.sizeSp              = 19f

        return layout.linearLayout(context)
    }


    // VIEWS > Header
    // -----------------------------------------------------------------------------------------

    private fun headerView(headerStringId : Int) : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.textId           = headerStringId

        header.font             = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        header.color            = theme.colorOrBlack(colorTheme)

        header.sizeSp           = 24f

        header.margin.topDp     = 6f
        header.margin.bottomDp  = 6f

        return header.textView(context)
    }

    // VIEWS > Content
    // -----------------------------------------------------------------------------------------

    private fun contentView(content : BookContent) : LinearLayout
    {
        val layout = this.contentViewLayout()

        content.groups().forEach {
            Log.d("***BOOK UI", "adding group view")
            layout.addView(it.view(book.entityId(), context))
        }

        layout.addView(this.contentReadMoreView())

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


    private fun contentReadMoreView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val label               = TextViewBuilder()
        val icon                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL or Gravity.END

        layout.padding.topDp    = 4f
        layout.padding.bottomDp = 4f
        layout.padding.rightDp  = 4f

        layout.child(label)
              .child(icon)

        // (3 A ) Label
        // -------------------------------------------------------------------------------------

        label.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId              = R.string.read_more

        label.font                = Font.typeface(TextFont.default(),
                                                  TextFontStyle.Medium,
                                                  context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        label.color               = theme.colorOrBlack(labelColorTheme)

        label.sizeSp              = 16f

        // (3 B) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp                = 17
        icon.heightDp               = 17

        icon.image                  = R.drawable.icon_arrow_right

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_22"))))
        icon.color                  = theme.colorOrBlack(iconColorTheme)

        icon.margin.leftDp          = 4f
        icon.margin.topDp           = 1f

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

        layout.corners              = Corners(1.0, 1.0, 1.0, 1.0)

        layout.padding.topDp        = 10f
        layout.padding.bottomDp     = 10f
        layout.padding.leftDp       = 8f
        layout.padding.rightDp      = 8f

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
