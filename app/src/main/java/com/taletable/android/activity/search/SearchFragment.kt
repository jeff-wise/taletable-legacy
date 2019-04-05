
package com.taletable.android.activity.search


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taletable.android.R
import com.taletable.android.lib.ui.RecyclerViewBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialThemeLight
import com.taletable.android.util.Util


/**
 * Search Fragment
 */
class SearchFragment : Fragment()
{

    // | Properties
    // -----------------------------------------------------------------------------------------

    private lateinit var searchViewModel : SearchViewModel


    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance() : SearchFragment
        {
            val fragment = SearchFragment()

//            val args = Bundle()
//            args.putSerializable("book_id", bookId)
//            fragment.arguments = args

            return fragment
        }
    }


    // -----------------------------------------------------------------------------------------
    // FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

//        this.bookId = arguments?.getSerializable("book_id") as EntityId

        searchViewModel = activity?.run {
            ViewModelProviders.of(this).get(SearchViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        var view : View? = null

        this.context?.let { context ->
            val recyclerView = searchResultsRecyclerView(officialThemeLight, context)
            val resultsAdapter = SearchResultsRecyclerViewAdapter(listOf(),
                                                                  officialThemeLight,
                                                                  searchViewModel,
                                                                  context)
            recyclerView.adapter = resultsAdapter

            searchViewModel.results().observe(this, Observer<List<SearchResult>>{ results ->
                resultsAdapter.results = results
                resultsAdapter.notifyDataSetChanged()
                updateSearchBar()
            })

            searchViewModel.search(SearchQuery.default())

            view = recyclerView
        }

        return view
    }


    private fun updateSearchBar()
    {
        val currentQuery = this.searchViewModel.query()

        this.context?.let { context ->

            this.activity?.findViewById<TextView>(R.id.searchbar_text_view)?.let { textView ->
                textView.text = currentQuery.term
            }

            this.activity?.findViewById<ImageView>(R.id.searchbar_left_button_view)?.let { buttonView ->

                if (currentQuery.term.isBlank())
                {
                    buttonView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_toolbar_search))
                    buttonView.setPadding(Util.dpToPixel(10f), 0, Util.dpToPixel(10f), 0)
                }
                else
                {
                    buttonView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_toolbar_back))
                    buttonView.setOnClickListener {
                        searchViewModel.previousSearch()
                    }
                    buttonView.setPadding(Util.dpToPixel(3f), 0, Util.dpToPixel(4f), 0)
                }
            }

        }


    }

//    fun updateSearchView()
//    {

//
//        this.findViewById<TextView>(R.id.searchbar_text_view)?.let { textView ->
//            //            textView.textSize = Util.spToPx(4.8f, this).toFloat()
//            //textView.typeface = Font.typeface(TextFont.Roboto, TextFontStyle.Regular, this)
//            textView.text = this.currentQuery
//        }
//
//        this.findViewById<ImageView>(R.id.searchbar_left_button_view)?.let { buttonView ->
//            if (this.currentQuery.isEmpty())
//            {
//                buttonView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_searchbar_search))
//            }
//            else
//            {
//                buttonView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_searchbar_back))
//                buttonView.setOnClickListener {
//                    previousSearch()
//                }
//            }
//        }
//
//        this.findViewById<ImageView>(R.id.searchbar_right_button_view)?.let { buttonView ->
//            if (this.currentQuery.isEmpty())
//            {
//                buttonView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_toolbar_options))
//            }
//            else
//            {
//                buttonView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_toolbar_close))
//                buttonView.setOnClickListener {
//                    clearSearch()
//                }
//            }
//        }
    //}


}



private fun searchResultsRecyclerView(theme : Theme, context : Context) : RecyclerView
{
    val recyclerViewBuilder             = RecyclerViewBuilder()

    recyclerViewBuilder.width           = LinearLayout.LayoutParams.MATCH_PARENT
    recyclerViewBuilder.height          = LinearLayout.LayoutParams.MATCH_PARENT

    recyclerViewBuilder.padding.topDp   = 8f

    recyclerViewBuilder.layoutManager   = LinearLayoutManager(context)

//        val dividerColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_5"))))
//        val dividerColor              = theme.colorOrBlack(dividerColorTheme)
//        recyclerViewBuilder.divider         = SimpleDividerItemDecoration(context, dividerColor)

    recyclerViewBuilder.clipToPadding   = false


    return recyclerViewBuilder.recyclerView(context)
}

//class SearchUI(val theme : Theme,
//               val searchFragment : SearchFragment)
//{
//
//    // | View
//    // -------------------------------------------------------------------------
//
//    fun view(context : Context) : View
//    {
//        val recyclerView = resultsRecyclerView(theme, context)
//
//
//        return recyclerView
//    }
//
//
//
//
//}

