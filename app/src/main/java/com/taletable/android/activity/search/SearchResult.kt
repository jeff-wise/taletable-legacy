
package com.taletable.android.activity.search

import com.taletable.android.rts.session.SessionId



sealed class SearchResult(term : String)



data class SearchResultSimple(val term : String,
                              val prefix : String? = null,
                              val suggestion : String? = null) : SearchResult(term)


data class SearchResultPageGroupHeader(val pageGroupHeader : String) : SearchResult(pageGroupHeader)


data class SearchResultPageGroupSearch(val context : String,
                                       val search : SearchQuery) : SearchResult("")


data class SearchResultHeader(val header : String) : SearchResult(header)


data class SearchResultIcon(val term : String,
                            val description : String,
                            val iconId : Int,
                            val newSearchQuery : SearchQuery,
                            val iconSize : Int? = null) : SearchResult(term)


data class SearchResultPage(val term : String,
                            val description : String,
                            val newSearchQuery : SearchQuery,
                            val iconId : Int? = null) : SearchResult(term)



data class SearchResultSession(val name : String,
                               val description : String,
                               val sessionId : SessionId,
                               val iconId : Int? = null) : SearchResult("")


data class SearchResultToolbar(val currentQuery : SearchQuery) : SearchResult("")





sealed class SearchResultPageTarget

data class SearchResultPageTargetSession(val sessionId : SessionId) : SearchResultPageTarget()
