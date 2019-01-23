
package com.taletable.android.activity.entity.book.fragment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.taletable.android.activity.session.SessionActivity
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.ScrollViewBuilder
import com.taletable.android.model.book.*
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

        layout.addView(this.contentView())

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
    // VIEWS > Card
    // -----------------------------------------------------------------------------------------

    fun contentView() : View
    {
        val scrollView = this.cardScrollView()
        val cardLayout = this.cardLayout()


        card.content(book).forEach { content ->
            groups(content.groupReferences(), book.entityId()).forEach {
                cardLayout.addView(it.group.view(book.entityId(), context, content.context()))
            }
        }

        scrollView.addView(cardLayout)

        return scrollView
    }


    private fun cardScrollView() : ScrollView
    {
        val scrollView          = ScrollViewBuilder()

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height       = LinearLayout.LayoutParams.MATCH_PARENT

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_7"))))
        scrollView.backgroundColor  = theme.colorOrBlack(bgColorTheme)

        return scrollView.scrollView(context)
    }


    private fun cardLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.bottomDp  = 70f

        return layout.linearLayout(context)
    }

}
