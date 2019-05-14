
package com.taletable.android.activity.entity.book.fragment


import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
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
import com.taletable.android.rts.entity.groups



/**
 * Book Card Fragment
 */
class BookCardFragment : Fragment()
{

    // | Properties
    // -----------------------------------------------------------------------------------------

    private var bookId : EntityId? = null
    private var cardId : BookCardId? = null


    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(bookId: EntityId,
                        cardId : BookCardId) : BookCardFragment
        {
            val fragment = BookCardFragment()

            val args = Bundle()
            args.putSerializable("book_id", bookId)
            args.putSerializable("card_id", cardId)
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
        this.cardId = arguments?.getSerializable("card_id") as BookCardId
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val bookId  = this.bookId
        val cardId  = this.cardId
        val context = getContext()

        var view : View? = null

        if (bookId != null && cardId != null && context != null)
        {
            val sessionActivity = context as SessionActivity

            book(bookId).doMaybe { book ->
            book.card(cardId).doMaybe { card ->
                view = BookCardUI(book, card, officialThemeLight, sessionActivity).view()
            } }
        }

        return view
    }


}




class BookCardUI(val book : Book,
                 val card : BookCard,
                 val theme : Theme,
                 val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val entityId = book.entityId()


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------


    fun view() : View
    {
        val layout = this.viewLayout()

        //layout.addView(blankHeaderView())

        //val mainView = blankMainView()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            mainView.z = 10000f

        //layout.addView(mainView)

        layout.addView(contentView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun blankHeaderView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 40

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_9"))))
        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)

        return layout.linearLayout(context)
    }


//    private fun blankMainView() : LinearLayout
//    {
//        val layout = blankMainViewLayout()
//
//        layout.addView(this.contentView())
//
//        return layout
//    }


//    private fun blankMainViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_9"))))
//        layout.backgroundColor  = theme.colorOrBlack(bgColorTheme)
//
//        return layout.linearLayout(context)
//    }



    // -----------------------------------------------------------------------------------------
    // VIEWS > Card
    // -----------------------------------------------------------------------------------------

    private fun cardHeaderView() : ViewGroup
    {
        val layout = cardHeaderViewLayout()

        layout.addView(cardHeaderNameView())

        layout.addView(cardHeaderIconView())

        return layout
    }



    private fun cardHeaderViewLayout() : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        //layout.backgroundColor  = Color.TRANSPARENT

        layout.backgroundResource   = R.drawable.bg_book_card_header

        layout.padding.leftDp   = 12f
        layout.padding.rightDp  = 12f
        layout.padding.topDp    = 12f
        layout.padding.bottomDp = 12f

        return layout.relativeLayout(context)
    }


    private fun cardHeaderNameView() : TextView
    {
        val nameViewBuilder             = TextViewBuilder()

        nameViewBuilder.layoutType      = LayoutType.RELATIVE
        nameViewBuilder.width           = RelativeLayout.LayoutParams.WRAP_CONTENT
        nameViewBuilder.height          = RelativeLayout.LayoutParams.WRAP_CONTENT

        nameViewBuilder.addRule(RelativeLayout.CENTER_VERTICAL)
        nameViewBuilder.addRule(RelativeLayout.ALIGN_PARENT_START)

        nameViewBuilder.text            = card.name().value

        nameViewBuilder.font            = Font.typeface(TextFont.Roboto,
                                                        TextFontStyle.Bold,
                                                        context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_12"))))
        nameViewBuilder.color           = theme.colorOrBlack(colorTheme)

        nameViewBuilder.sizeSp          = 20f

        nameViewBuilder.backgroundColor = Color.WHITE

        return nameViewBuilder.textView(context)
    }


    private fun cardHeaderIconView() : LinearLayout
    {
        // | Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val iconViewBuilder     = ImageViewBuilder()

        // | Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType       = LayoutType.RELATIVE
        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.addRule(RelativeLayout.CENTER_VERTICAL)
        layout.addRule(RelativeLayout.ALIGN_PARENT_END)

        layout.child(iconViewBuilder)

        // | Icon
        // -------------------------------------------------------------------------------------

        iconViewBuilder.widthDp     = 24
        iconViewBuilder.heightDp    = 24

        iconViewBuilder.image       = R.drawable.icon_vertical_ellipsis

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_18"))))
        iconViewBuilder.color           = theme.colorOrBlack(iconColorTheme)

        return layout.linearLayout(context)
    }


    private fun cardHeaderDividerView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        layout.orientation      = LinearLayout.HORIZONTAL

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_5"))))
        layout.backgroundColor  = theme.colorOrBlack(colorTheme)

        return layout.linearLayout(context)
    }


    fun contentView() : View
    {
        val scrollView = this.cardScrollView()
        val cardLayout = this.cardLayout()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            scrollView.elevation = 8f
            cardLayout.elevation = 8f
        }

        cardLayout.addView(cardHeaderView())

        cardLayout.addView(cardHeaderDividerView())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            scrollView.z = 200000f

        card.content(book).forEach { content ->
            groups(content.groupReferences(), book.entityId()).forEach {
                cardLayout.addView(it.group.view(book.entityId(), context, content.context()))
            }
        }

        cardLayout.addView(cardFooterView())

        scrollView.addView(cardLayout)

        return scrollView
    }


    private fun cardScrollView() : ScrollView
    {
        val scrollView          = ScrollViewBuilder()

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height       = LinearLayout.LayoutParams.MATCH_PARENT

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_9"))))
//        scrollView.backgroundColor  = theme.colorOrBlack(bgColorTheme)

        //scrollView.backgroundResource   = R.drawable.bg_book_card_header

        scrollView.margin.leftDp    = 8f
        scrollView.margin.rightDp   = 8f
        scrollView.margin.topDp     = 16f

        return scrollView.scrollView(context)
    }


    private fun cardLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.bottomDp = 70f
        layout.margin.bottomDp  = 10f

        return layout.linearLayout(context)
    }

    private fun cardFooterView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 4

        layout.corners          = Corners(0.0, 0.0, 4.0, 4.0)

        layout.backgroundColor  = Color.WHITE

        return layout.linearLayout(context)
    }
}
