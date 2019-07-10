
package com.taletable.android.activity.entity.book.fragment


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.taletable.android.activity.session.SessionActivity
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.ScrollViewBuilder
import com.taletable.android.model.book.Book
import com.taletable.android.model.theme.Theme
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.model.theme.official.officialThemeLight
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.book
import com.taletable.android.rts.entity.groups


/**
 * Book Credits Fragment
 */
class BookCreditsFragment : Fragment()
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
        fun newInstance(bookId : EntityId) : BookCreditsFragment
        {
            val fragment = BookCreditsFragment()

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
        val context = context

        var view : View? = null

        if (bookId != null && context != null)
        {
            val bookActivity = context as SessionActivity
            book(bookId).doMaybe {
                view = bookCreditsView(it, officialAppThemeLight, context)
            }
        }

        return view
    }


}


private fun bookCreditsView(book : Book, theme : Theme, context : Context) : ViewGroup
{
    val scrollView = bookCreditsScrollView(context)
    val layout = bookCreditsViewLayout(context)

    val creditsContent = book.bookInfo().credits.content.apply { contentId ->
        book.content(contentId)
    }

    Log.d("***CREDITS FRAGMENT", "here")
    creditsContent.doMaybe { content ->
        Log.d("***CREDITS FRAGMENT", "content found")
        groups(content.groupReferences(), book.entityId()).forEach {
            Log.d("***CREDITS FRAGMENT", "groups found")
            layout.addView(it.group.view(book.entityId(), context, content.context()))
        }
    }

    scrollView.addView(layout)

    return scrollView
}


private fun bookCreditsScrollView(context : Context) : ScrollView
{
    val scrollViewBuilder           = ScrollViewBuilder()

    scrollViewBuilder.width         = LinearLayout.LayoutParams.MATCH_PARENT
    scrollViewBuilder.height        = LinearLayout.LayoutParams.MATCH_PARENT

    return scrollViewBuilder.scrollView(context)
}


private fun bookCreditsViewLayout(context : Context) : LinearLayout
{
    val layoutBuilder           = LinearLayoutBuilder()

    layoutBuilder.width         = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height        = LinearLayout.LayoutParams.MATCH_PARENT

    layoutBuilder.backgroundColor   = Color.WHITE

    return layoutBuilder.linearLayout(context)
}
