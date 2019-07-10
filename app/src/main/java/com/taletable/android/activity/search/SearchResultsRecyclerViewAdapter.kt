
package com.taletable.android.activity.search


import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taletable.android.activity.search.view.*
import com.taletable.android.model.session.sessionManifest
import com.taletable.android.model.theme.Theme



class SearchResultsRecyclerViewAdapter(
        var results : List<Any>,
        val theme : Theme,
        val searchViewModel : SearchViewModel,
        val context : Context)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    // | PROPERTIES
    // -------------------------------------------------------------------------------------

    private val SEARCH_RESULT_SIMPLE = 0
    private val SEARCH_RESULT_ICON = 1
    private val SEARCH_RESULT_PAGE = 2
    private val SEARCH_RESULT_PAGE_GROUP_HEADER = 3
    private val SEARCH_RESULT_PAGE_GROUP_SEARCH = 4
    private val SEARCH_RESULT_SESSION = 5
    private val SEARCH_RESULT_TOOLBAR = 6


    // | RECYCLER VIEW ADAPTER API
    // -------------------------------------------------------------------------------------

    override fun getItemViewType(position : Int) : Int
    {
        val itemAtPosition = this.results[position]

        return when (itemAtPosition) {
            is SearchResultSimple           -> SEARCH_RESULT_SIMPLE
            is SearchResultIcon             -> SEARCH_RESULT_ICON
            is SearchResultPage             -> SEARCH_RESULT_PAGE
            is SearchResultPageGroupHeader  -> SEARCH_RESULT_PAGE_GROUP_HEADER
            is SearchResultPageGroupSearch  -> SEARCH_RESULT_PAGE_GROUP_SEARCH
            is SearchResultSession          -> SEARCH_RESULT_SESSION
            is SearchResultToolbar          -> SEARCH_RESULT_TOOLBAR
            else                            -> SEARCH_RESULT_ICON
        }
    }


    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : RecyclerView.ViewHolder =
        when (viewType)
        {
            SEARCH_RESULT_TOOLBAR ->
            {
                val toolbarView = searchResultToolbarView(theme, context)
                SearchResultToolbarViewHolder(toolbarView, theme, context)
            }
            SEARCH_RESULT_SIMPLE ->
            {
                val simpleView = searchResultSimpleView(theme, context)
                SearchResultSimpleViewHolder(simpleView, theme, context)
            }
            SEARCH_RESULT_ICON ->
            {
                val iconView = searchResultIconView(theme, context)
                SearchResultIconViewHolder(iconView, theme, context)
            }
            SEARCH_RESULT_PAGE ->
            {
                val imageView = searchResultImageView(theme, context)
                SearchResultImageViewHolder(imageView, theme, context)
            }
            SEARCH_RESULT_PAGE_GROUP_SEARCH ->
            {
                val pageGroupSearchView = searchResultPageGroupSearchView(theme, context)
                SearchResultPageGroupSearchViewHolder(pageGroupSearchView, theme, context)
            }
            SEARCH_RESULT_PAGE_GROUP_HEADER ->
            {
                val pageGroupHeaderView = searchResultPageGroupHeaderView(theme, context)
                SearchResultPageGroupHeaderViewHolder(pageGroupHeaderView, theme, context)
            }
            SEARCH_RESULT_SESSION ->
            {
                val sessionView = searchResultSessionView(theme, context)
                SearchResultSessionViewHolder(sessionView, theme, context)
            }
            else ->
            {
                val otherView = searchResultSimpleView(theme, context)
                SearchResultSimpleViewHolder(otherView, theme, context)
            }
        }


    override fun onBindViewHolder(viewHolder : RecyclerView.ViewHolder, position : Int)
    {
        val item = this.results[position]

        when (item)
        {
            is SearchResultToolbar -> {
                val resultToolbarViewHolder = viewHolder as SearchResultToolbarViewHolder
                resultToolbarViewHolder.setContext(item.currentQuery.context)
            }
            is SearchResultSimple -> {
                val resultSimpleViewHolder = viewHolder as SearchResultSimpleViewHolder
                if (item.prefix != null && item.suggestion != null) {
                    resultSimpleViewHolder.setComplexTerm(item.prefix, item.suggestion)
                }
                else {
                    resultSimpleViewHolder.setSimpleTerm(item.term)
                }
            }
            is SearchResultIcon -> {
                val resultIconViewHolder = viewHolder as SearchResultIconViewHolder
                resultIconViewHolder.setName(item.term)
                resultIconViewHolder.setDescription(item.description)
                resultIconViewHolder.setIcon(item.iconId, item.iconSize)
                resultIconViewHolder.setOnClick(View.OnClickListener {
                    searchViewModel.search(item.newSearchQuery)
                })
            }
            is SearchResultPage -> {
                val resultPageViewHolder = viewHolder as SearchResultImageViewHolder
                resultPageViewHolder.setName(item.term)
                resultPageViewHolder.setDescription(item.description)
                resultPageViewHolder.setOnClick(View.OnClickListener {
                    searchViewModel.search(item.newSearchQuery)
                })
            }
            is SearchResultPageGroupHeader -> {
                val pageGroupHeaderViewHolder = viewHolder as SearchResultPageGroupHeaderViewHolder
                pageGroupHeaderViewHolder.setName(item.pageGroupHeader)
            }
            is SearchResultPageGroupSearch -> {
                val pageGroupSearchViewHolder = viewHolder as SearchResultPageGroupSearchViewHolder
                pageGroupSearchViewHolder.setSearchText(item.search)
                pageGroupSearchViewHolder.setOnClick(View.OnClickListener {
                    searchViewModel.search(item.search)
                })
            }
            is SearchResultSession -> {
                val sessionViewHolder = viewHolder as SearchResultSessionViewHolder
                sessionViewHolder.setName(item.name)
                sessionViewHolder.setDescription(item.description)

                val mSession = sessionManifest(context).apply { it.session(item.sessionId) }
                mSession.doMaybe {
                    val entityNames = it.persistedEntities(context).take(3).map { it.name }
                    sessionViewHolder.setEntityList(entityNames)
                }

//                sessionViewHolder.setOnClick(View.OnClickListener {
//                    val intent = Intent(homeActivity, SessionActivity::class.java)
//                    intent.putExtra("session_id", item.sessionId)
//                    homeActivity.startActivity(intent)
//                })

            }
        }
    }


    override fun getItemCount() = this.results.size

}

