
package com.taletable.android.activity.entity.book


import android.util.Log
import com.taletable.android.model.book.Book
import com.taletable.android.model.book.BookCard
import com.taletable.android.model.book.BookCardId
import com.taletable.android.model.entity.SearchAttributeKeyword
import org.apache.commons.collections4.trie.PatriciaTrie



data class BookSearcher(val book : Book)
{

    // | Tries
    // -----------------------------------------------------------------------------------------

    private val keywordTrie : PatriciaTrie<BookSearchEntry> = PatriciaTrie()


    // | API
    // -----------------------------------------------------------------------------------------

    fun search(queryString : String) : List<BookSearchEntry>
    {
        val entries : MutableList<BookSearchEntry> = mutableListOf()

        val keywordEntries = searchKeyword(queryString)
        keywordEntries.forEach { entry ->
            entries.add(entry)
        }

        entries.sortBy { it.weight() }

        return entries
    }


    fun indexBook()
    {
        indexCards(book.cards)
    }


    // | Private > Search
    // -----------------------------------------------------------------------------------------

    private fun searchKeyword(query : String) : Set<BookSearchEntry>
    {
        val results : MutableSet<BookSearchEntry> = mutableSetOf()

        val keywordMap = this.keywordTrie.prefixMap(query)
        keywordMap.keys.forEach { keyword ->
            keywordMap[keyword]?.let { entry ->
                results.add(entry)
            }
        }

        return results
    }



    // | Private > Index
    // -----------------------------------------------------------------------------------------

    private fun indexCards(cards : List<BookCard>)
    {
        cards.forEach { card ->
            card.searchData.attributes.forEach { attr ->
                when (attr) {
                    is SearchAttributeKeyword -> {
                        val entry = BookSearchEntryCard(card.cardId, card.name().value, attr.weight.value)
                        keywordTrie[attr.keyword.value] = entry
                    }
                }
            }
        }

    }

}



data class BookSearchResult(val entry : BookSearchEntry, val rank : Double)



sealed class BookSearchEntry()
{
    abstract fun weight() : Double

}


data class BookSearchEntryCard(val cardId : BookCardId,
                               val cardName : String,
                               val weight : Double) : BookSearchEntry()
{

    override fun weight() : Double = weight
}