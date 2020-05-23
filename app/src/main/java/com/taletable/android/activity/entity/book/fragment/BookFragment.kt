
package com.taletable.android.activity.entity.book.fragment


import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import com.google.android.material.tabs.TabLayout
import com.taletable.android.R
import com.taletable.android.activity.session.SessionActivity
import com.taletable.android.lib.ui.*
import com.taletable.android.model.book.*
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.Spacing
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.sheet.widget.TabWidgetViewType
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialThemeLight
import com.taletable.android.model.user.UserId
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.book
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.groups
import com.taletable.android.util.RoundedTextBackgroundSpan
import com.taletable.android.util.Util
import java.util.*


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

    private var currentTabIndex : Int = 1
    private val tabViewMap : MutableMap<Int,View> = mutableMapOf()


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

//        layout.addView(this.summaryView())

        //layout.addView(this.chaptersHeaderView())

//        layout.addView(this.metadataView())

        layout.addView(this.tabView())

        //layout.addView(this.chapterListView())

        val summaryView = this.summaryView()
        summaryView.visibility = View.GONE
        layout.addView(summaryView)

        val chapterListView = this.chapterListView()
        //chapterListView.visibility = View.GONE
        layout.addView(chapterListView)

        this.tabViewMap[0] = summaryView
        this.tabViewMap[1] = chapterListView


        //layout.addView(this.otherDataView())

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


    // VIEWS > Tab View
    // -----------------------------------------------------------------------------------------

    private fun tabView() : View
    {
//        val selectedTextFormat = tabWidget.format().selectedTabFormat()
//        val unselectedTextFormat = tabWidget.format().unselectedTabFormat()
//        val tabFormat = tabWidget.format().tabFormat()


        val selectedColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))

        val unselectedColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_20"))))

        val underlineColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_3"))))

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_8"))))
        val bgColor = colorOrBlack(bgColorTheme, book.entityId())

        val entityId = book.entityId()

        val tabLayout = CustomTabLayout(context,
                                        TextFont.RobotoSlab,
                                        TextFontStyle.Bold)

        tabLayout.setBackgroundColor(bgColor)

        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE

        val overviewTab = tabLayout.newTab()
        overviewTab.customView = tabTextView("Overview", unselectedColorTheme, entityId)

        val chaptersTab = tabLayout.newTab()
        chaptersTab.customView = tabTextView("Chapters", selectedColorTheme, entityId)

        val creditsTab = tabLayout.newTab()
        creditsTab.customView = tabTextView("Credits", unselectedColorTheme, entityId)

        val imagesTab = tabLayout.newTab()
        imagesTab.customView = tabTextView("Images", unselectedColorTheme, entityId)

        tabLayout.addTab(overviewTab)
        tabLayout.addTab(chaptersTab)
        tabLayout.addTab(creditsTab)
        tabLayout.addTab(imagesTab)

        tabLayout.setTabTextColors(
                colorOrBlack(unselectedColorTheme, entityId),
                colorOrBlack(selectedColorTheme, entityId)
        )

        tabLayout.setSelectedTabIndicatorHeight(Util.dpToPixel(2f))

        tabLayout.setSelectedTabIndicatorColor(
                colorOrBlack(underlineColorTheme, entityId))

        tabLayout.selectTab(chaptersTab)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: TabLayout.Tab?) { }

            override fun onTabSelected(tab : TabLayout.Tab?) {

                val tabTextView = tab?.customView as TextView?
                tabTextView?.setTextColor(colorOrBlack(selectedColorTheme, entityId))

                if (tab != null) {
                    val pos = tab.position
                    showTab(pos)
//
//                        val tabTextView = tab?.customView as TextView?
//                        tabTextView?.setTextColor(colorOrBlack(selectedTextFormat.colorTheme(), entityId))
                }
                else {
                    showTab(0)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?)
            {
                val tabTextView = tab?.customView as TextView?
                tabTextView?.setTextColor(colorOrBlack(unselectedColorTheme, entityId))
            }
        })

        showTab(1)

        return tabLayout
    }


    private fun tabTextView(labelString : String,
                            theme : ColorTheme,
                            entityId : EntityId
    ) : TextView
    {
        val label               = TextViewBuilder()

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.layoutGravity     = Gravity.CENTER

//        label.backgroundColor   = colorOrBlack(format.elementFormat().backgroundColorTheme(), entityId)

        label.text              = labelString

        label.color             = colorOrBlack(theme, entityId)

        label.sizeSp            = 17.5f

        label.font              = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Bold,
                                                context)

        label.paddingSpacing    = Spacing(0.0, 6.0, 0.0, 6.0)
//        label.marginSpacing     = Spacing(10.0, 10.0, 10.0, 10.0)

        return label.textView(context)
    }


    fun showTab(index : Int)
    {
        if (index >= 0)
        {
            this.tabViewMap[currentTabIndex]?.let { view ->
                view.visibility = View.GONE
            }

            if (this.tabViewMap.containsKey(index))
            {
                this.tabViewMap[index]!!.visibility = View.VISIBLE
                Log.d("***BOOK FRAGMENT", "making tab view visible.")
            }

            this.currentTabIndex = index
        }
    }


    // VIEWS > Chapter List
    // -----------------------------------------------------------------------------------------

    private fun chapterListView() : LinearLayout
    {
        val layout = this.chapterListViewLayout()

        //layout.addView(this.chapterHeaderView("Chapters", 4f))

        book.chapters().forEach {
            layout.addView(this.chapterSummaryView(it))
//            layout.addView(this.dividerView(0f))
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


    private fun chapterHeaderView(label : String, paddingBottomDp : Float) : TextView
    {
        val headerViewBuilder               = TextViewBuilder()

        headerViewBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
        headerViewBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        headerViewBuilder.padding.bottomDp  = paddingBottomDp
        headerViewBuilder.padding.leftDp    = 16f
        headerViewBuilder.padding.rightDp   = 16f
        headerViewBuilder.padding.topDp     = 16f

        //headerViewBuilder.backgroundColor   = Color.WHITE

        headerViewBuilder.addRule(RelativeLayout.ALIGN_PARENT_START)
        headerViewBuilder.addRule(RelativeLayout.CENTER_VERTICAL)

        headerViewBuilder.text              = label


        headerViewBuilder.font          = Font.typeface(TextFont.Roboto,
                                                        TextFontStyle.Medium,
                                                        context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_8"))))
        headerViewBuilder.color         = theme.colorOrBlack(colorTheme)

        headerViewBuilder.sizeSp              = 17f

        return headerViewBuilder.textView(context)

    }


    private fun chapterSummaryView(chapter : BookChapter) : ViewGroup
    {
        val layout = this.chapterSummaryViewLayout()

        layout.addView(this.chapterSummaryTextView(chapter.title().value))

        layout.addView(this.chapterSummaryDescripionTextView())

//        layout.addView(this.chapterSummaryIconView())

        layout.setOnClickListener {
            val chapterReference = BookReferenceChapter(book.entityId(), chapter.chapterId())
            sessionActivity.setCurrentBookReference(chapterReference)
        }

        return layout
    }


    private fun chapterSummaryViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

        layout.backgroundColor      = Color.WHITE

        layout.padding.topDp        = 16f
        layout.padding.bottomDp     = 16f
        layout.padding.leftDp       = 12f
        layout.padding.rightDp      = 12f

        layout.margin.leftDp        = 4f
        layout.margin.rightDp       = 4f
        layout.margin.topDp         = 6f

       // layout.margin.bottomDp      = -36f

        return layout.linearLayout(context)

    }


    private fun chapterSummaryTextView(summaryString : String) : TextView
    {
        val summary                 = TextViewBuilder()

        summary.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        summary.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        summary.text                = summaryString

        summary.font                = Font.typeface(TextFont.RobotoSlab,
                                                    TextFontStyle.Bold,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_8"))))
        summary.color               = theme.colorOrBlack(colorTheme)

        summary.sizeSp              = 19f

        summary.backgroundColor     = Color.WHITE

        return summary.textView(context)

    }


    private fun chapterSummaryDescripionTextView() : TextView
    {
        val summary                 = TextViewBuilder()

        summary.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        summary.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        summary.text                = "This is the description for a chapter about whatever."

        summary.margin.topDp        = 4f

        summary.font                = Font.typeface(TextFont.RobotoSlab,
                                                    TextFontStyle.Regular,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_14"))))
        summary.color               = theme.colorOrBlack(colorTheme)

        summary.sizeSp              = 14.5f

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

        val titleView = this.titleView()
        titleView.setShadowLayer(Util.dpToPixel(10f).toFloat(), 0f, 0f, 0)

        val hlColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_9_5"))))
        val hlColor = colorOrBlack(hlColorTheme, book.entityId())

        val span = RoundedTextBackgroundSpan(
                backgroundColor = hlColor,
                padding = Util.dpToPixel(10f).toFloat(),
                radius = Util.dpToPixel(8f).toFloat()
        )

        titleView.text = buildSpannedString { inSpans(span) { append(book.bookInfo().title().value) } }

        layout.addView(titleView)


        book.bookInfo().subtitle()?.let {
            layout.addView(this.subtitleView(it.value))
        }

        //layout.addView(this.searchbarView())

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

        //layout.zIndex           = 10000

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_8"))))
        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

//        layout.backgroundResource   = R.drawable.bg_book_default_header

        layout.padding.bottomDp     = 60f
        layout.padding.topDp     = 12f

//        layout.padding.leftDp   = 15f
//        layout.padding.rightDp  = 15f
        //layout.padding.topDp    = 6f
        //layout.padding.bottomDp = 24f

        return layout.linearLayout(context)
    }


    // VIEWS > Header > Title
    // --------------------------------------------------------------------------------------------

    private fun titleView() : TextView
    {
        val title                   = TextViewBuilder()

        title.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        //title.margin.topDp          = 8f

        title.padding.leftDp    = 15f
        title.padding.rightDp   = 15f

        //title.text                  = book.bookInfo().title.value

        title.font                  = Font.typeface(TextFont.RobotoSlab,
                                                    TextFontStyle.Bold,
                                                    context)

        title.color                 = Color.WHITE

        title.sizeSp                = 54f

        title.lineSpacingAdd        = 10f
        title.lineSpacingMult       = 0.88f

        return title.textView(context)
    }


    private fun subtitleView(subtitle : String) : TextView
    {
        val builder = SpannableStringBuilder()

        builder.append("For ")
        builder.append(subtitle)

        // Format for

        val forColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_14"))))
        val forColor = theme.colorOrBlack(forColorTheme)
        val colorSpan = ForegroundColorSpan(forColor)
        builder.setSpan(colorSpan, 0, 4, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        val subtitleViewBuilder                 = TextViewBuilder()

        subtitleViewBuilder.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        subtitleViewBuilder.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        subtitleViewBuilder.margin.topDp        = 12f

        subtitleViewBuilder.padding.leftDp    = 15f
        subtitleViewBuilder.padding.rightDp   = 15f

        subtitleViewBuilder.textSpan            = builder

        subtitleViewBuilder.font                = Font.typeface(TextFont.RobotoSlab,
                                                                TextFontStyle.ExtraBold,
                                                                context)

        subtitleViewBuilder.color               = Color.WHITE

        subtitleViewBuilder.sizeSp              = 19f

        subtitleViewBuilder.lineSpacingAdd      = 10f
        subtitleViewBuilder.lineSpacingMult     = 0.85f

        return subtitleViewBuilder.textView(context)
    }



    private fun searchbarView() : LinearLayout
    {
        val layout = this.searchbarViewLayout()

        val buttonView = this.searchbarButtonView(R.drawable.icon_bookmark)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            layout.translationZ = 10000f
            buttonView.translationZ = 10000f
        }

//        buttonView.setOnClickListener {
//            val newFragment = BookmarkCollectionListFragment.newInstance(
//                                  UserId(UUID.fromString("0244f109-22e6-431b-95ad-8dede7e274c1")))
//            newFragment.enterTransition = Slide(Gravity.BOTTOM)
//            newFragment.exitTransition  = Slide(Gravity.TOP)
//
//            val transaction = sessionActivity.supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.session_content, newFragment)
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }

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

        layout.margin.topDp         = -36f

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

        innerLayout.padding.topDp       = 10f
        innerLayout.padding.bottomDp    = 10f
        innerLayout.padding.leftDp      = 12f
        innerLayout.padding.rightDp     = 12f

        innerLayout.gravity             = Gravity.CENTER_VERTICAL

        val innerBgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_green"))))
        innerLayout.backgroundColor     = theme.colorOrBlack(innerBgColorTheme)

        innerLayout.child(icon)
                   .child(label)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 17
        icon.heightDp           = 17

        icon.image              = R.drawable.icon_bookmark_filled

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

        //label.textId            = R.string.search
        label.text              = "MARK"

//        val labelColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_8"))))
//        label.color             = theme.colorOrBlack(labelColorTheme)
        label.color             = Color.WHITE

        label.sizeSp            = 17f

        return outerLayout.linearLayout(context)
    }


    private fun summaryView() : LinearLayout
    {
        val layout = summaryViewLayout()

        layout.addView(summaryTextView())

        return layout
    }

    private fun summaryViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.margin.topDp     = -20f
        layout.margin.bottomDp     = -20f
        //layout.margin.bottomDp     = -24f

        layout.zIndex           = 10000

        layout.padding.leftDp    = 15f
        layout.padding.rightDp   = 15f

        layout.orientation      = LinearLayout.VERTICAL

        layout.corners          = Corners(18.0, 18.0, 18.0, 18.0)

        layout.backgroundColor  = Color.WHITE


        return layout.linearLayout(context)
    }


    private fun summaryTextView() : TextView
    {
        val title               = TextViewBuilder()

        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        title.margin.topDp      = 44f

        title.text              = book.summary()

        title.font              = Font.typeface(TextFont.RobotoSlab,
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_8"))))
        title.color              = theme.colorOrBlack(colorTheme)
//        title.color             = Color.WHITE

        title.sizeSp             = 16f

        title.lineSpacingAdd      = 4f
        title.lineSpacingMult     = 1f

        return title.textView(context)
    }


    private fun metadataView() : LinearLayout
    {
        val layout = metadataViewLayout()

        layout.addView(chapterHeaderView("About", 4f))

        book.bookInfo().publisher.doMaybe {
            layout.addView(metadataButtonView("Published by", it.value))
        }
        val creditsButtonView = metadataButtonView(null, book.bookInfo.credits.label)
        creditsButtonView.setOnClickListener {
            val creditsFragment = BookCreditsFragment.newInstance(book.entityId())
            val transaction = sessionActivity.supportFragmentManager.beginTransaction()
            transaction.replace(com.taletable.android.R.id.session_content, creditsFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        layout.addView(dividerView(0f, 16f))
        layout.addView(creditsButtonView)

        book.bookInfo().licenseInfo.doMaybe {
            layout.addView(dividerView(0f, 16f))
            layout.addView(metadataButtonView("Licensed under", it.label))
        }

        layout.addView(dividerView(8f))

        return layout
    }


    private fun otherDataView() : LinearLayout
    {
        val layout = metadataViewLayout()

        layout.addView(chapterHeaderView("Last edited on", 4f))

        layout.addView(lastEditView())

        layout.addView(dividerView(0f))

        return layout
    }


    private fun metadataViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.topDp    = 24f
        //layout.margin.topDp    = -50f


        layout.orientation      = LinearLayout.VERTICAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_3")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_6"))))
        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

//        layout.padding.leftDp    = 16f
//        layout.padding.rightDp   = 16f

//        layout.padding.leftDp   = 8f
//        layout.padding.rightDp  = 8f
        //layout.padding.topDp    = 16f
        //layout.padding.bottomDp = 16f

        return layout.linearLayout(context)
    }


    private fun metadataButtonView(label : String?, value : String) : LinearLayout
    {
        // 1 | Declarations
        // -------------------------------------------------------------------------------------

        val layout                  = LinearLayoutBuilder()
        val labelViewBuilder        = TextViewBuilder()
        val valueViewBuilder        = TextViewBuilder()

        // 2 | Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.orientation          = LinearLayout.HORIZONTAL

        //layout.backgroundResource   = R.drawable.bg_session_button

        layout.padding.topDp        = 16f
        layout.padding.bottomDp     = 16f
        layout.padding.leftDp       = 16f
        layout.padding.rightDp      = 16f

//        layout.margin.leftDp        = 16f
//        layout.margin.rightDp       = 16f
        //layout.margin.topDp         = 12f

        layout.child(labelViewBuilder)
              .child(valueViewBuilder)

        // | Label
        // -------------------------------------------------------------------------------------

        labelViewBuilder.width      = LinearLayout.LayoutParams.WRAP_CONTENT
        labelViewBuilder.height     = LinearLayout.LayoutParams.WRAP_CONTENT

        if (label != null)
            labelViewBuilder.margin.rightDp = 8f

        labelViewBuilder.text       = label

        labelViewBuilder.font       = Font.typeface(TextFont.Roboto,
                                                    TextFontStyle.Medium,
                                                    context)

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_14"))))
        labelViewBuilder.color      = theme.colorOrBlack(labelColorTheme)

        labelViewBuilder.sizeSp     = 17f

        // | Value
        // -------------------------------------------------------------------------------------

        valueViewBuilder.width      = LinearLayout.LayoutParams.WRAP_CONTENT
        valueViewBuilder.height     = LinearLayout.LayoutParams.WRAP_CONTENT

        valueViewBuilder.text       = value

        valueViewBuilder.font       = Font.typeface(TextFont.Roboto,
                                                    TextFontStyle.Medium,
                                                    context)

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        valueViewBuilder.color      = theme.colorOrBlack(valueColorTheme)

        valueViewBuilder.sizeSp     = 17f


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
        //layout.backgroundResource  = R.drawable.bg_card_flat

        layout.padding.topDp    = 16f
        layout.padding.bottomDp = 16f
        layout.padding.leftDp   = 16f
        layout.padding.rightDp  = 16f

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

    private fun dividerView(marginTopDp : Float, horizontalMargin : Float = 0f) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        layout.margin.topDp     = marginTopDp
        layout.margin.leftDp    = horizontalMargin
        layout.margin.rightDp    = horizontalMargin

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_5"))))
        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

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
