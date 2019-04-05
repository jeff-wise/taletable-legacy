
package com.taletable.android.activity.search


import com.taletable.android.R
import com.taletable.android.model.book.Book
import com.taletable.android.rts.session.SessionId
import java.util.*



class SearchEngine
{

    // | Properties
    // -----------------------------------------------------------------------------------------

    //private var currentQuery : String = ""

    private val history : MutableList<SearchQuery> = mutableListOf()


    // | Search
    // -----------------------------------------------------------------------------------------


//    fun search(query : String) : List<SearchResult>
//    {
//
//    }

    fun results(query : SearchQuery) : List<SearchResult>
    {
        this.history.add(query)

        return if (query.context.isNotBlank())
        {
            if (query.context == "Pathfinder 2" && query.term == "books") {
                this.pathfinder2BookSearchResults(query)
            } else {
                this.defaultSearchResults()
            }
        }
        else {
            when (query.term) {
                "books" -> this.bookSearchResults()
                else    -> this.defaultSearchResults()
            }
        }
    }


    fun previousSearchResults() : List<SearchResult> =
        // No history, doesn't make sense to assume anything
        if (history.isEmpty())
        {
            listOf()
        }
        // Just research current query
        else if (history.size == 1)
        {
            val currentQuery = this.history.removeAt(this.history.size - 1)
            this.results(currentQuery)
        }
        else
        {
            // Discard current query
            this.history.removeAt(this.history.size - 1)
            // Get last query
            val lastQuery = this.history.last()
            // Return results for last query
            this.results(lastQuery)
        }


//            = when (query) {
//
//        "games"                             -> this.gamesSearchResults()
//        "games pathfinder 2"                -> this.pathfinder2Results()
//        "games pathfinder 2 session casmey dalseya" -> this.casmeyDalseyaSearchResults()
//        "books pathfinder 2 playtest rulebook" -> this.pathfinder2BookSearchResults()
//        else                                -> this.defaultSearchResults()
//    }


    private fun defaultSearchResults() : List<SearchResult>
    {
        val news  = SearchResultIcon("News", "Gaming related news and updates.", R.drawable.icon_news, SearchQuery("", "news"), 23)
        val games = SearchResultIcon("Games", "Browse, play and create games.", R.drawable.icon_die, SearchQuery("", "games"), 24)
        val books = SearchResultIcon("Books", "Discover and read interactive books.", R.drawable.icon_books, SearchQuery("", "books"), 25)
        val recommended = SearchResultIcon("Recommended", "Suggestions based on what you like.", R.drawable.icon_wand, SearchQuery("", "recommended"))
        val people = SearchResultIcon("People", "Players, authors, game designers, ...", R.drawable.icon_people, SearchQuery("", "people"), 25)

        return listOf(recommended, games, books, news, people)
    }


    private fun bookSearchResults() : List<SearchResult>
    {
        val pageGroupHeaderResult = SearchResultPageGroupHeader("Pathfinder 2 Playtest Books")

        val rulebookResult = SearchResultPage("Core Rulebook",
                                              "Pathfinder 2 Core Rulebook (OGL)",
                                              SearchQuery("", "books pathfinder 2 playtest rulebook"))

        val pageGroupSearchResult = SearchResultPageGroupSearch("Pathfinder 2", SearchQuery("Pathfinder 2", "books"))

        val bookSessionId = SessionId(UUID.fromString("2c383a1b-b695-4553-bcf3-22eb4ed16b1c"))
        val sessionResult = SearchResultSession("Core Rulebooks for Pathfinder 2", "Collection of rulebooks (OGL) for the core rules of Pathfinder 2", bookSessionId)

        return listOf(pageGroupHeaderResult, rulebookResult, pageGroupSearchResult, sessionResult)
    }


    private fun pathfinder2BookSearchResults(query : SearchQuery) : List<SearchResult>
    {
        val bookSessionId = SessionId(UUID.fromString("2c383a1b-b695-4553-bcf3-22eb4ed16b1c"))

        val toolbarResult = SearchResultToolbar(query)

        val sessionResult = SearchResultSession("Core Rulebooks for Pathfinder 2", "Collection of rulebooks (OGL) for the core rules of Pathfinder 2", bookSessionId)

        return listOf(toolbarResult, sessionResult)
    }


    private fun gamesSearchResults() : List<SearchResult>
    {
        val pageGroupHeaderResult = SearchResultPageGroupHeader("Games")

        val game1Result = SearchResultPage("Pathfinder 2",
                                           "Pathfinder 2 OGL ruleset (Paizo)",
                                           SearchQuery("", "games pathfinder 2"))
        val game2Result = SearchResultPage("5th Edition",
                                           "5th Edition OGL ruleset",
                                           SearchQuery("", "games 5th edition"))
        val game3Result = SearchResultPage("Starfinder",
                                           "Starfinder OGL ruleset (Paizo)",
                                            SearchQuery("", "games 5th edition"))

        val pageGroupSearchResult = SearchResultPageGroupSearch("", SearchQuery("", "More games"))

        return listOf(pageGroupHeaderResult, game1Result, game2Result, game3Result, pageGroupSearchResult)
    }


    private fun pathfinder2Results() : List<SearchResult>
    {

        val pathfinder2SessionId = SessionId(UUID.fromString("b3b4894d-7f2a-4f11-9b27-6e8133f7000f"))
        val gameSessionResult = SearchResultSession("Pathfinder 2", "The roleplaying game for Pathfinder 2", pathfinder2SessionId)

        val suggestion1Result = SearchResultSimple("Pathfinder 2", "Pathfinder 2", "Sessions")
        val suggestion2Result = SearchResultSimple("Starfinder", "Pathfinder 2", "Rules")

        val charGroupHeaderResult = SearchResultPageGroupHeader("Player Characters")

        val char1Result = SearchResultPage("Casmey Dalseya",
                                           "1st Level Human Rogue",
                                           SearchQuery("", "games pathfinder 2 session casmey dalseya"))
        val char2Result = SearchResultPage("Mazar (of The Clan)",
                                           "1st Level Half-Orc Barbarian",
                                           SearchQuery("", "casmey dalseya"))
        val char3Result = SearchResultPage("Darius Bristlebottoms",
                                           "1st Level Human Bard",
                                           SearchQuery("", "casmey dalseya"))
                                           //kSearchResultPageTargetSession(SessionId(UUID.fromString("b3b4894d-7f2a-4f11-9b27-6e8133f7000f"))))

        val charGroupSearchResult = SearchResultPageGroupSearch("Characters", SearchQuery("", "More PCs"))


        return listOf(gameSessionResult,
                      suggestion1Result,
                      suggestion2Result,
                      charGroupHeaderResult,
                      char1Result,
                      char2Result,
                      char3Result,
                      charGroupSearchResult)
    }


    private fun casmeyDalseyaSearchResults() : List<SearchResult>
    {
        val casmeySessionId = SessionId(UUID.fromString("56897634-288a-478e-bb59-eedf09d8aab6"))
        val sessionResult = SearchResultSession("Casmey Dalseya", "1st Level Human Rogue", casmeySessionId)

        return listOf(sessionResult)
    }




}



data class SearchQuery(val context : String, val term : String)
{


    companion object
    {
        fun default() : SearchQuery = SearchQuery("", "")
    }


}