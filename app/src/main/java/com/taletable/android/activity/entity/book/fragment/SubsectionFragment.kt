
package com.taletable.android.activity.entity.book.fragment


import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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



/**
 * Subsection Fragment
 */
class SubsectionFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var subsectionId : BookSubsectionId? = null
    private var sectionId    : BookSectionId? = null
    private var chapterId    : BookChapterId? = null
    private var bookId       : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(subsectionId: BookSubsectionId,
                        sectionId : BookSectionId,
                        chapterId : BookChapterId,
                        bookId : EntityId) : SubsectionFragment
        {
            val fragment = SubsectionFragment()

            val args = Bundle()
            args.putSerializable("subsection_id", subsectionId)
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

        this.subsectionId = arguments?.getSerializable("subsection_id") as BookSubsectionId
        this.sectionId    = arguments?.getSerializable("section_id") as BookSectionId
        this.chapterId    = arguments?.getSerializable("chapter_id") as BookChapterId
        this.bookId       = arguments?.getSerializable("book_id") as EntityId
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val subsectionId = this.subsectionId
        val sectionId = this.sectionId
        val chapterId = this.chapterId
        val bookId    = this.bookId
        val context   = getContext()

        var view : View? = null

        if (subsectionId != null && sectionId != null && chapterId != null && bookId != null && context != null)
        {
            val sessionActivity = context as SessionActivity

            book(bookId).doMaybe { book ->
            book.chapter(chapterId).doMaybe { chapter ->
            book.section(chapterId, sectionId).doMaybe { section ->
            book.subsection(chapterId, sectionId, subsectionId).doMaybe { subsection ->
                view = SubsectionUI(subsection, book, officialThemeLight, sessionActivity).view()
            } } } }

        }

        return view
    }


}




/**
 * Subsection UI
 */
class SubsectionUI(val subsection : BookSubsection,
                   val book : Book,
                   val theme : Theme,
                   val sessionActivity : SessionActivity)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val entityId = book.entityId()
    val context = sessionActivity


    // -----------------------------------------------------------------------------------------
    // | VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        layout.addView(this.subsectionView())

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

    // | VIEWS > Subsection
    // -----------------------------------------------------------------------------------------

    fun subsectionView() : View
    {
        val scrollView = this.subsectionScrollView()
        val cardLayout = this.subsectionViewLayout()

        cardLayout.addView(this.headerView())

        cardLayout.addView(this.floatingBarView())

        cardLayout.addView(this.contentView(subsection.bodyContent(book)))

        scrollView.addView(cardLayout)

        return scrollView
    }


    private fun subsectionScrollView() : ScrollView
    {
        val scrollView          = ScrollViewBuilder()

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height       = LinearLayout.LayoutParams.MATCH_PARENT

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
//        scrollView.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        scrollView.backgroundColor  = Color.WHITE

        return scrollView.scrollView(context)
    }


    private fun subsectionViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.bottomDp = 70f

        return layout.linearLayout(context)
    }

    // | VIEWS > Header
    // --------------------------------------------------------------------------------------------

    private fun headerView() : LinearLayout
    {
        val layout = this.headerViewLayout()

        layout.addView(this.titleView())

        layout.addView(this.toolbarView())

        return layout
    }

    // | VIEWS > Header > Layout
    // --------------------------------------------------------------------------------------------

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
        layout.padding.rightDp  = 16f
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

        title.text               = subsection.title().value

        title.font               = Font.typeface(TextFont.RobotoSlab,
                                                 TextFontStyle.ExtraBold,
                                                 context)

        title.color              = Color.WHITE

        title.sizeSp             = 44f

        title.lineSpacingAdd     = 10f
        title.lineSpacingMult    = 0.8f

        return title.textView(context)
    }

    // VIEWS > Floating Bar
    // --------------------------------------------------------------------------------------------

    private fun floatingBarView() : LinearLayout
    {
        val layout = this.floatingBarViewLayout()

        val buttonView = this.bookmarkButtonView()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            layout.translationZ = 10000f
            buttonView.translationZ = 10000f
        }

        layout.addView(buttonView)

        return layout
    }

    // VIEWS > Floating Bar > Layout
    // --------------------------------------------------------------------------------------------

    private fun floatingBarViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundColor  = Color.TRANSPARENT

        layout.margin.rightDp   = 14f

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL or Gravity.END

        layout.margin.topDp         = -44f

        return layout.linearLayout(context)
    }

    // | VIEWS > Floating Bar > Button
    // --------------------------------------------------------------------------------------------

    private fun bookmarkButtonView() : LinearLayout
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

        icon.color              = Color.WHITE

        icon.margin.rightDp     = 4f

        // (3) Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Bold,
                                                context)

        label.text              = "MARK"

        label.color             = Color.WHITE

        label.sizeSp            = 17f

        return outerLayout.linearLayout(context)
    }

    // VIEWS | Toolbar
    // -----------------------------------------------------------------------------------------

    private fun toolbarView() : LinearLayout
    {
        val layout = toolbarViewLayout()

        layout.addView(toolbarButtonView(R.drawable.icon_pencil, 15, R.string.edit))

        layout.addView(toolbarButtonView(R.drawable.icon_questions, 20, R.string.help))

        return layout
    }

    // VIEWS | Toolbar > Layout
    // -----------------------------------------------------------------------------------------
    private fun toolbarViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        layout.padding.topDp    = 48f

        return layout.linearLayout(context)
    }

    // VIEWS | Toolbar > Button
    // -----------------------------------------------------------------------------------------

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

        icon.widthDp            = iconSize
        icon.heightDp           = iconSize

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

    // | VIEWS > Content
    // --------------------------------------------------------------------------------------------

    private fun contentView(contentList : List<BookContent>) : LinearLayout
    {
        val layout = this.contentViewLayout()

        contentList.forEach { content ->
            groups(content.groupReferences(), book.entityId()).forEach { groupInvocation ->
                val group = groupInvocation.group
                val groupContext = when (content.context()) {
                    is Just -> content.context()
                    else    -> groupInvocation.groupContext
                }
                layout.addView(group.view(book.entityId(), context, groupContext))
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

}


