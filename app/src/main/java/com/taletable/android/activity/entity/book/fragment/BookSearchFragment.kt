
package com.taletable.android.activity.entity.book.fragment


import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taletable.android.R
import com.taletable.android.activity.entity.book.BookSearchEntry
import com.taletable.android.activity.entity.book.BookSearchEntryCard
import com.taletable.android.activity.entity.book.BookSearchResult
import com.taletable.android.activity.entity.book.BookSearcher
import com.taletable.android.activity.session.SessionActivity
import com.taletable.android.lib.ui.*
import com.taletable.android.model.book.Book
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialThemeLight
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.book



/**
 * Book Search Fragment
 */
class BookSearchFragment : Fragment()
{

    // | Properties
    // -----------------------------------------------------------------------------------------

    private var bookId : EntityId? = null


    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(bookId : EntityId) : BookSearchFragment
        {
            val fragment = BookSearchFragment()

            val args = Bundle()
            args.putSerializable("book_id", bookId)
            fragment.arguments = args

            return fragment
        }
    }


    // | Fragment
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

        var view : ViewGroup? = null

        if (bookId != null && context != null)
        {
            val bookActivity = context as SessionActivity
            book(bookId).doMaybe {
                val searchView = BookSearchUI(it, bookActivity, officialThemeLight).view()

                searchView.viewTreeObserver.addOnGlobalLayoutListener {
                    val r = Rect()
                    //r will be populated with the coordinates of your view that area still visible.
                    view?.getWindowVisibleDisplayFrame(r)

                    view?.rootView?.height?.let { height ->
                        val heightDiff = height - (r.bottom - r.top)
                        if (heightDiff > 500) { // if more than 100 pixels, its probably a keyboard...
                            context.findViewById<ViewGroup>(R.id.bottom_sheet)?.let {
                                it.visibility = View.GONE
                            }
                            context.findViewById<View>(R.id.shadow)?.let {
                                it.visibility = View.GONE
                            }
                        }
                        else {
                            context.findViewById<ViewGroup>(R.id.bottom_sheet)?.let {
                                it.visibility = View.VISIBLE
                            }
                            context.findViewById<View>(R.id.shadow)?.let {
                                it.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                view = searchView
            }
        }

        return view
    }


}


class BookSearchUI(val book : Book,
                   private val sessionActivity : SessionActivity,
                   val theme : Theme)
{

    // | Properties
    // -----------------------------------------------------------------------------------------

    val context = sessionActivity


    // | Properties > Search
    // -----------------------------------------------------------------------------------------

    val searcher = BookSearcher(book)

    val searchResults : MutableList<BookSearchEntry> = mutableListOf()

    val searchResultAdapter : SearchResultsRecyclerViewAdapter =
            SearchResultsRecyclerViewAdapter(this.searchResults as MutableList<Any>,
                                             book,
                                             theme,
                                             context)



    // | Initialize
    // -----------------------------------------------------------------------------------------

    init {
        searcher.indexBook()
    }


    // | Views
    // -----------------------------------------------------------------------------------------

    fun view() : LinearLayout
    {
        val layout              = viewLayout()

        layout.addView(this.searchBarView())

        layout.addView(this.dividerView())

        layout.addView(this.searchResultsView())

        return layout
    }



    private fun viewLayout() : LinearLayout
    {
        val layoutBuilder               = LinearLayoutBuilder()

        layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
        layoutBuilder.height            = LinearLayout.LayoutParams.MATCH_PARENT

        layoutBuilder.orientation       = LinearLayout.VERTICAL

        layoutBuilder.backgroundColor   = Color.WHITE

        return layoutBuilder.linearLayout(context)
    }


    // | Search Button View
    // -----------------------------------------------------------------------------------------

    private fun dividerView() : LinearLayout
    {
        val layoutBuilder               = LinearLayoutBuilder()

        layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
        layoutBuilder.heightDp          = 1

        layoutBuilder.orientation       = LinearLayout.VERTICAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_9"))))
        layoutBuilder.backgroundColor   = theme.colorOrBlack(bgColorTheme)

        return layoutBuilder.linearLayout(context)
    }


    // | Search Button View
    // -----------------------------------------------------------------------------------------

    private fun searchBarView() : RelativeLayout
    {
        val layout = searchBarViewLayout()

        layout.addView(searchBarLeftButtonView())

        val searchBarTextView = searchBarTextView()
        val searchBarTextViewLayoutParams = searchBarTextView.layoutParams as RelativeLayout.LayoutParams
        searchBarTextViewLayoutParams.addRule(RelativeLayout.END_OF, R.id.searchbar_icon_left)
        searchBarTextView.layoutParams = searchBarTextViewLayoutParams

        layout.addView(searchBarTextView)

        layout.addView(searchBarRightButtonView())


        searchBarTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s : Editable) {
            }

            override fun beforeTextChanged(s : CharSequence, start : Int,
                                           count : Int, after : Int) {
            }

            override fun onTextChanged(s : CharSequence, start : Int,
                                       before : Int, count : Int)
            {
                Log.d("***BOOK SEARCH", "new text: ${s.toString()}")
                searchResults.clear()
                searchResults.addAll(searcher.search(s.toString()))
                searchResultAdapter.notifyDataSetChanged()
            }

        })

        return layout
    }


    private fun searchBarViewLayout() : RelativeLayout
    {
        val layoutBuilder               = RelativeLayoutBuilder()

        layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
        layoutBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        layoutBuilder.gravity           = Gravity.CENTER_VERTICAL

        layoutBuilder.padding.topDp     = 12f
        layoutBuilder.padding.bottomDp  = 12f
        layoutBuilder.padding.leftDp    = 8f
        layoutBuilder.padding.rightDp   = 8f

        return layoutBuilder.relativeLayout(context)
    }


    private fun searchBarLeftButtonView() : LinearLayout
    {
        // 1 | Declarations
        // -------------------------------------------------------------------------------------

        val layoutBuilder               = LinearLayoutBuilder()
        val imageViewBuilder            = ImageViewBuilder()

        // 2 | Layout
        // -------------------------------------------------------------------------------------

        layoutBuilder.id                = R.id.searchbar_icon_left

        layoutBuilder.layoutType        = LayoutType.RELATIVE
        layoutBuilder.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
        layoutBuilder.height            = RelativeLayout.LayoutParams.WRAP_CONTENT

        layoutBuilder.margin.leftDp     = 10f
        layoutBuilder.margin.rightDp    = 26f

        layoutBuilder.gravity           = Gravity.CENTER

        layoutBuilder.addRule(RelativeLayout.ALIGN_PARENT_START)
        layoutBuilder.addRule(RelativeLayout.CENTER_VERTICAL)

        layoutBuilder.child(imageViewBuilder)

        // 3 | Icon
        // -------------------------------------------------------------------------------------

        imageViewBuilder.widthDp           = 19
        imageViewBuilder.heightDp          = 19


        imageViewBuilder.image              = R.drawable.icon_arrow_back

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_18"))))
        imageViewBuilder.color              = theme.colorOrBlack(iconColorTheme)

        return layoutBuilder.linearLayout(context)
    }


    private fun searchBarRightButtonView() : LinearLayout
    {
        // 1 | Declarations
        // -------------------------------------------------------------------------------------

        val layoutBuilder               = LinearLayoutBuilder()
        val imageViewBuilder            = ImageViewBuilder()

        // 2 | Layout
        // -------------------------------------------------------------------------------------

        layoutBuilder.layoutType        = LayoutType.RELATIVE
        layoutBuilder.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
        layoutBuilder.height            = RelativeLayout.LayoutParams.WRAP_CONTENT

        layoutBuilder.gravity           = Gravity.CENTER

        layoutBuilder.addRule(RelativeLayout.ALIGN_PARENT_END)
        layoutBuilder.addRule(RelativeLayout.CENTER_VERTICAL)

        layoutBuilder.child(imageViewBuilder)

        // 3 | Icon
        // -------------------------------------------------------------------------------------

        imageViewBuilder.widthDp           = 20
        imageViewBuilder.heightDp          = 20


        imageViewBuilder.image              = R.drawable.icon_delete

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_18"))))
        imageViewBuilder.color              = theme.colorOrBlack(iconColorTheme)

        imageViewBuilder.margin.rightDp     = 16f

        return layoutBuilder.linearLayout(context)
    }


    private fun searchBarTextView() : EditText
    {
        val editTextBuilder                 = EditTextBuilder()

        editTextBuilder.layoutType          = LayoutType.RELATIVE
        editTextBuilder.width               = RelativeLayout.LayoutParams.WRAP_CONTENT
        editTextBuilder.height              = RelativeLayout.LayoutParams.WRAP_CONTENT

        editTextBuilder.addRule(RelativeLayout.CENTER_VERTICAL)

        editTextBuilder.font                = Font.typeface(TextFont.Roboto,
                                                            TextFontStyle.Regular,
                                                            context)

        val searchColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        editTextBuilder.color               = theme.colorOrBlack(searchColorTheme)

        editTextBuilder.backgroundResource  = R.drawable.bg_edit_text_no_style

        editTextBuilder.sizeSp              = 20f

        editTextBuilder.hint                = context.getString(R.string.search_book)

        return editTextBuilder.editText(context)
    }


    private fun searchResultsView() : RecyclerView
    {
        val recyclerViewBuilder             = RecyclerViewBuilder()

        recyclerViewBuilder.width           = LinearLayout.LayoutParams.MATCH_PARENT
        recyclerViewBuilder.height          = LinearLayout.LayoutParams.MATCH_PARENT

        recyclerViewBuilder.layoutManager   = LinearLayoutManager(context)

        recyclerViewBuilder.adapter         = this.searchResultAdapter

        return recyclerViewBuilder.recyclerView(context)
    }

}




class SearchResultsRecyclerViewAdapter(val results : MutableList<Any>,
                                       val book : Book,
                                       val theme : Theme,
                                       val context : Context)
                                        : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    // | Properties
    // -----------------------------------------------------------------------------------------

    private val RESULT_CARD = 0


    // RecyclerViewAdapter
    // -----------------------------------------------------------------------------------------

    override fun getItemViewType(position : Int) : Int
    {
        val resultAtPosition = this.results[position]

        return when (resultAtPosition) {
            is BookSearchEntryCard -> RESULT_CARD
            else                   -> RESULT_CARD
        }
    }


    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : RecyclerView.ViewHolder =
        when (viewType)
        {
            RESULT_CARD ->
            {
                val cardView = searchResultCardView(theme, context)
                SearchResultCardViewHolder(cardView, theme, context)
            }
            else ->
            {
                val otherView = searchResultCardView(theme, context)
                SearchResultCardViewHolder(otherView, theme, context)
            }
        }


    override fun onBindViewHolder(viewHolder : RecyclerView.ViewHolder, position : Int)
    {
        val result = this.results[position]

        when (result)
        {
            is BookSearchEntryCard -> {
                val resultCardViewHolder = viewHolder as SearchResultCardViewHolder
                resultCardViewHolder.setName(result.cardName)
            }
        }
    }


    override fun getItemCount() = this.results.size

}


class SearchResultCardViewHolder(itemView : View,
                                 val theme : Theme,
                                 val context : Context)
                                  : RecyclerView.ViewHolder(itemView)
{

    // | Properties
    // -----------------------------------------------------------------------------------------

    var layout      : LinearLayout? = null
    var nameView    : TextView? = null


    // | Initializee
    // -----------------------------------------------------------------------------------------

    init
    {
        this.layout      = itemView.findViewById(R.id.layout)
        this.nameView    = itemView.findViewById(R.id.name_view)
    }


    // | ViewHolder
    // -----------------------------------------------------------------------------------------

    fun setName(name : String)
    {
        Log.d("***BOOK SEARCH", "setting name: $name")
        this.nameView?.text = name
    }


    fun setOnClick(onClickListener : View.OnClickListener)
    {
        this.layout?.setOnClickListener(onClickListener)
    }

}




private fun searchResultCardView(theme : Theme, context : Context) : LinearLayout
{
    val layout = searchResultCardViewLayout(context)

    layout.addView(searchResultCardLeftView(theme, context))

    layout.addView(searchResultCardRightView(theme, context))

    return layout
}



private fun searchResultCardViewLayout(context : Context) : LinearLayout
{
    val layoutBuilder               = LinearLayoutBuilder()

    layoutBuilder.width             = LinearLayout.LayoutParams.MATCH_PARENT
    layoutBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation       = LinearLayout.HORIZONTAL

    layoutBuilder.backgroundColor   = Color.WHITE

    layoutBuilder.gravity           = Gravity.CENTER_VERTICAL

    layoutBuilder.padding.topDp     = 12f
    layoutBuilder.padding.bottomDp  = 12f

    return layoutBuilder.linearLayout(context)
}


private fun searchResultCardLeftView(theme : Theme, context : Context) : LinearLayout
{

    // | Declarations
    // -----------------------------------------------------------------------------------------

    val layoutBuilder               = LinearLayoutBuilder()
    val iconBuilder                 = ImageViewBuilder()

    // | Layout
    // -----------------------------------------------------------------------------------------

    layoutBuilder.width             = LinearLayout.LayoutParams.WRAP_CONTENT
    layoutBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation       = LinearLayout.HORIZONTAL

    layoutBuilder.margin.leftDp     = 16f

    layoutBuilder.child(iconBuilder)

    // | Icon
    // -----------------------------------------------------------------------------------------

    iconBuilder.id                  = R.id.icon_view

    iconBuilder.widthDp             = 24
    iconBuilder.heightDp            = 24

    iconBuilder.image               = R.drawable.icon_document

    val iconColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_16"))))
    iconBuilder.color               = theme.colorOrBlack(iconColorTheme)

    return layoutBuilder.linearLayout(context)
}


private fun searchResultCardRightView(theme : Theme, context : Context) : LinearLayout
{

    // | Declarations
    // -----------------------------------------------------------------------------------------

    val layoutBuilder               = LinearLayoutBuilder()
    val nameViewBuilder             = TextViewBuilder()

    // | Layout
    // -----------------------------------------------------------------------------------------

    layoutBuilder.width             = LinearLayout.LayoutParams.WRAP_CONTENT
    layoutBuilder.height            = LinearLayout.LayoutParams.WRAP_CONTENT

    layoutBuilder.orientation       = LinearLayout.HORIZONTAL

    layoutBuilder.margin.leftDp     = 20f

    layoutBuilder.child(nameViewBuilder)

    // | Name View
    // -----------------------------------------------------------------------------------------

    nameViewBuilder.id              = R.id.name_view

    nameViewBuilder.width           = LinearLayout.LayoutParams.WRAP_CONTENT
    nameViewBuilder.height          = LinearLayout.LayoutParams.WRAP_CONTENT

    val nameColorTheme = ColorTheme(setOf(
            ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
            ThemeColorId(ThemeId.Light, ColorId.Theme("dark_blue_grey_12"))))
    nameViewBuilder.color           = theme.colorOrBlack(nameColorTheme)

    nameViewBuilder.sizeSp          = 20f

    nameViewBuilder.font            = Font.typeface(TextFont.Roboto,
                                                    TextFontStyle.Medium,
                                                    context)

    return layoutBuilder.linearLayout(context)
}




