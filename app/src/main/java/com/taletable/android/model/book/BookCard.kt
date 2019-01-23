
package com.taletable.android.model.book


import com.taletable.android.lib.Factory
import com.taletable.android.model.entity.SearchData
import effect.apply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import maybe.filterJust
import java.io.Serializable



/**
 * Book Card
 */
data class BookCard(val cardId : BookCardId,
                    val name : BookCardName,
                    val contentIds : List<BookContentId>,
                    val searchData : SearchData)
                     : ToDocument, Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookCard>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookCard> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookCard,
                      // Card Id
                      doc.at("id") apply { BookCardId.fromDocument(it) },
                      // Card Name
                      doc.at("name") apply { BookCardName.fromDocument(it) },
                      // Content
                      doc.list("content_ids") apply {
                          it.map { BookContentId.fromDocument(it) } },
                      // Search Data
                      split(doc.maybeAt("search_data"),
                            effValue(SearchData.empty()),
                            { SearchData.fromDocument(it) })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // | To Document
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
    ))


    // | Properties
    // -----------------------------------------------------------------------------------------

    fun contentIds() : List<BookContentId> = this.contentIds


    fun name() : BookCardName = this.name


    // | Content
    // -----------------------------------------------------------------------------------------

    fun content(book : Book) : List<BookContent> =
            this.contentIds.map { book.content(it) }.filterJust()

}


/**
 * Book Card Id
 */
data class BookCardId(val value : String) : ToDocument, Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookCardId>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookCardId> = when (doc)
        {
            is DocText -> effValue(BookCardId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // | To Document
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}


/**
 * Book Card Name
 */
data class BookCardName(val value : String) : ToDocument, Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookCardName>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookCardName> = when (doc)
        {
            is DocText -> effValue(BookCardName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // | To Document
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}
