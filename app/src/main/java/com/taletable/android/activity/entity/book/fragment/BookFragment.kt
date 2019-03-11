
package com.taletable.android.activity.entity.book.fragment


import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import androidx.fragment.app.Fragment
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
            val bookActivity = context as SessionActivity
            book(bookId).doMaybe {
                view = BookUI(it, bookActivity, officialThemeLight).view()
            }
        }

        return view
    }


}



class BookUI(val book : Book,
             private val sessionActivity : SessionActivity,
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

//        book.introductionContent().forEach { content ->
//            groups(content.groupReferences(), book.entityId()).forEach {
//                layout.addView(it.view(book.entityId(), context))
//            }
//        }

        layout.addView(this.headerView())

        //layout.addView(this.chaptersHeaderView())

        layout.addView(this.metadataView())


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

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_9"))))
        layout.backgroundColor      = theme.colorOrBlack(bgColorTheme)

        return layout.linearLayout(context)
    }


    // VIEWS > Header
    // -----------------------------------------------------------------------------------------

    private fun chaptersHeaderView() : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.MATCH_PARENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        // header.backgroundColor  = Color.WHITE

        header.text             = context.getString(R.string.chapters).toUpperCase()

        header.font             = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Bold,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_20"))))
        header.color            = theme.colorOrBlack(colorTheme)

        header.sizeSp           = 15f

        header.padding.topDp     = 12f
        header.padding.bottomDp  = 8f

        header.padding.leftDp    = 10f
        header.padding.rightDp   = 12f

        return header.textView(context)
    }

    // VIEWS > Content
    // -----------------------------------------------------------------------------------------

    private fun contentView(content : BookContent) : LinearLayout
    {
        val layout = this.contentViewLayout()

        groups(content.groupReferences(), book.entityId()).forEach {
            layout.addView(it.group.view(book.entityId(), context))
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

        layout.addView(this.chapterHeaderView())

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


    private fun chapterHeaderView() : TextView
    {
        val headerViewBuilder               = TextViewBuilder()

        headerViewBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
        headerViewBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        headerViewBuilder.padding.bottomDp   = 4f
        headerViewBuilder.padding.leftDp    = 16f
        headerViewBuilder.padding.rightDp   = 16f
        headerViewBuilder.padding.topDp     = 16f

        headerViewBuilder.backgroundColor   = Color.WHITE

        headerViewBuilder.addRule(RelativeLayout.ALIGN_PARENT_START)
        headerViewBuilder.addRule(RelativeLayout.CENTER_VERTICAL)

        headerViewBuilder.text              = "Chapters"


        headerViewBuilder.font          = Font.typeface(TextFont.RobotoCondensed,
                                                        TextFontStyle.Bold,
                                                        context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_18"))))
        headerViewBuilder.color         = theme.colorOrBlack(colorTheme)

        headerViewBuilder.sizeSp              = 17f

        return headerViewBuilder.textView(context)

    }


    private fun chapterSummaryView(chapter : BookChapter) : ViewGroup
    {
        val layout = this.chapterSummaryViewLayout()

        layout.addView(this.chapterSummaryTextView(chapter.title().value))

        layout.addView(this.chapterSummaryIconView())

        layout.setOnClickListener {
            val chapterReference = BookReferenceChapter(book.entityId(), chapter.chapterId())
            sessionActivity.setCurrentBookReference(chapterReference)
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

        layout.padding.topDp        = 18f
        layout.padding.bottomDp     = 18f
        layout.padding.leftDp       = 16f
        layout.padding.rightDp      = 16f

        layout.margin.bottomDp      = 1f

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
                                                    TextFontStyle.Regular,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_8"))))
        summary.color               = theme.colorOrBlack(colorTheme)

        summary.sizeSp              = 19f

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

        icon.widthDp                = 17
        icon.heightDp               = 17

        icon.image                  = R.drawable.icon_arrow_forward

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_20"))))
        icon.color               = theme.colorOrBlack(iconColorTheme)

        return layout.linearLayout(context)
    }

    // VIEWS > Header
    // --------------------------------------------------------------------------------------------

    private fun headerView() : LinearLayout
    {
        val layout = this.headerViewLayout()

        layout.addView(this.titleView())

        book.bookInfo().subtitle()?.let {
            layout.addView(this.subtitleView(it.value))
        }

        layout.addView(this.summaryView())

        // layout.addView(this.authorView())

        //layout.addView(this.toolbarView())

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
        //layout.padding.topDp    = 6f
        layout.padding.bottomDp = 32f

        return layout.linearLayout(context)
    }


    // VIEWS > Header > Title
    // --------------------------------------------------------------------------------------------

    private fun titleView() : TextView
    {
        val title                   = TextViewBuilder()

        title.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        title.margin.topDp          = 8f

        title.text                  = book.bookInfo().title.value

        title.font                  = Font.typeface(TextFont.Merriweather,
                                                    TextFontStyle.ExtraBold,
                                                    context)

        title.color                 = Color.WHITE

        title.sizeSp                = 54f

        title.lineSpacingAdd        = 10f
        title.lineSpacingMult       = 0.82f

        return title.textView(context)
    }


    private fun subtitleView(subtitle : String) : TextView
    {
        val builder = SpannableStringBuilder()

        builder.append("For ")
        builder.append(subtitle)

        // Format for

        val forColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_14"))))
        val forColor = theme.colorOrBlack(forColorTheme)
        val colorSpan = ForegroundColorSpan(forColor)
        builder.setSpan(colorSpan, 0, 4, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        val subtitleViewBuilder                 = TextViewBuilder()

        subtitleViewBuilder.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        subtitleViewBuilder.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        subtitleViewBuilder.margin.topDp        = 0f

        subtitleViewBuilder.textSpan            = builder

        subtitleViewBuilder.font                = Font.typeface(TextFont.Merriweather,
                                                                TextFontStyle.ExtraBold,
                                                                context)

        subtitleViewBuilder.color               = Color.WHITE

        subtitleViewBuilder.sizeSp              = 26f

        subtitleViewBuilder.lineSpacingAdd      = 10f
        subtitleViewBuilder.lineSpacingMult     = 0.85f

        return subtitleViewBuilder.textView(context)
    }


    private fun summaryView() : TextView
    {
        val title               = TextViewBuilder()

        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        title.margin.topDp      = 24f

        title.text              = book.summary()

        title.font              = Font.typeface(TextFont.Merriweather,
                                                TextFontStyle.Light,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_8"))))
        title.color              = theme.colorOrBlack(colorTheme)
//        title.color             = Color.WHITE

        title.sizeSp             = 16f

        title.lineSpacingAdd      = 4f
        title.lineSpacingMult     = 1.05f

        return title.textView(context)
    }


    private fun metadataView() : LinearLayout
    {
        val layout = metadataViewLayout()

        layout.addView(authorsView())

        layout.addView(lastEditView())
//
//        layout.addView(moreInfoView())

        return layout
    }



    private fun metadataViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_13"))))
//        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)


        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f
        layout.padding.topDp    = 16f
        layout.padding.bottomDp = 16f

        return layout.linearLayout(context)
    }


    private fun authorsView() : LinearLayout
    {
        // 1 | Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()
        val label               = TextViewBuilder()

        // 2 | Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.orientation      = LinearLayout.HORIZONTAL


        //layout.backgroundColor  = Color.WHITE
        layout.backgroundResource  = R.drawable.bg_card_flat

        layout.padding.topDp    = 16f
        layout.padding.bottomDp = 16f
        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f


        layout.child(icon)
              .child(label)

        // 3 | Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 21
        icon.heightDp           = 21

        icon.image              = R.drawable.icon_user

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_12"))))
        icon.color              = theme.colorOrBlack(iconColorTheme)

        icon.margin.rightDp     = 16f

        // 3 | Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text              = book.bookInfo().authorListString()

        label.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Bold,
                                                context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_14"))))
        label.color              = theme.colorOrBlack(labelColorTheme)

        label.sizeSp            = 18.5f

        return layout.linearLayout(context)
    }


    private fun lastEditView() : LinearLayout
    {
        // 1 | Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()
        val label               = TextViewBuilder()

        // 2 | Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.orientation      = LinearLayout.HORIZONTAL


        //layout.backgroundColor  = Color.WHITE
        layout.backgroundResource  = R.drawable.bg_card_flat

        layout.padding.topDp    = 16f
        layout.padding.bottomDp = 16f
        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        layout.child(icon)
              .child(label)

        // 3 | Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 20
        icon.heightDp           = 20

        icon.image              = R.drawable.icon_calendar

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_12"))))
        icon.color              = theme.colorOrBlack(iconColorTheme)

        icon.margin.rightDp     = 16f

        // 3 | Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text              = "March 6th, 2019"

        label.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Bold,
                                                context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_12"))))
        label.color              = theme.colorOrBlack(labelColorTheme)

        label.sizeSp            = 18.5f

        return layout.linearLayout(context)
    }


    // | Search Button View
    // -----------------------------------------------------------------------------------------

    private fun searchButtonView() : LinearLayout
    {
        // 1 | Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()
        val label               = TextViewBuilder()

        // 2 | Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.orientation      = LinearLayout.HORIZONTAL


        layout.backgroundColor  = Color.WHITE

        layout.padding.topDp    = 20f
        layout.padding.bottomDp = 20f
        layout.padding.leftDp   = 16f
        layout.padding.rightDp  = 16f

        layout.margin.bottomDp  = 1f

        layout.onClick          = View.OnClickListener {
            sessionActivity.setSearchView(book.bookId)
        }

        layout.child(icon)
              .child(label)

        // 3 | Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 21
        icon.heightDp           = 21

        icon.image              = R.drawable.icon_search

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_18"))))
        icon.color              = theme.colorOrBlack(iconColorTheme)

        icon.margin.rightDp     = 16f

        // 3 | Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = R.string.search_book

        label.font              = Font.typeface(TextFont.Roboto,
                                                TextFontStyle.Regular,
                                                context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_18"))))
        label.color              = theme.colorOrBlack(iconColorTheme)

        label.sizeSp            = 18f

        return layout.linearLayout(context)
    }


    // | Toolbar View
    // -----------------------------------------------------------------------------------------

    private fun toolbarView() : LinearLayout
    {
        val layout = this.toolbarViewLayout()

        layout.addView(this.toolbarButtonView())

        return layout
    }


    private fun toolbarViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.margin.topDp     = 30f
        layout.margin.bottomDp  = 12f
        layout.margin.rightDp   = 12f

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.END

        return layout.linearLayout(context)
    }


    private fun toolbarButtonView() : LinearLayout
    {
        // 1 | Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()
        val label               = TextViewBuilder()

        // 2 | Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.orientation      = LinearLayout.HORIZONTAL


        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_15"))))
        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

        layout.padding.topDp    = 6f
        layout.padding.bottomDp = 6f
        layout.padding.leftDp   = 10f
        layout.padding.rightDp  = 10f

        layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)


        layout.child(icon)
              .child(label)

        // 3 | Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 21
        icon.heightDp           = 21

        icon.image              = R.drawable.icon_gears

        icon.color              = Color.WHITE

        icon.margin.rightDp     = 8f

        // 3 | Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.textId            = R.string.settings

        label.font              = Font.typeface(TextFont.Roboto,
                                                TextFontStyle.Regular,
                                                context)

        label.color             = Color.WHITE

        label.sizeSp            = 18f

        return layout.linearLayout(context)
    }



}
