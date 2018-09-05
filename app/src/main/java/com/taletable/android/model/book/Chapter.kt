
package com.taletable.android.model.book


import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import effect.apply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import maybe.filterJust
import java.io.Serializable



/**
 * Chapter
 */
data class BookChapter(val chapterId : BookChapterId,
                       val title : BookChapterTitle,
                       val summary : Maybe<BookChapterSummary>,
                       val content : List<BookContentId>,
                       val sections : MutableList<BookSection>)
                        : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INDEXES
    // -----------------------------------------------------------------------------------------

    private val sectionById : MutableMap<BookSectionId, BookSection> =
                                        sections().associateBy { it.sectionId() }
                                                as MutableMap<BookSectionId, BookSection>


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookChapter>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookChapter> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookChapter,
                      // Chapter Id
                      doc.at("id") apply { BookChapterId.fromDocument(it) },
                      // Title
                      doc.at("title") apply { BookChapterTitle.fromDocument(it) },
                      // Summary
                      split(doc.maybeAt("summary"),
                            effValue<ValueError,Maybe<BookChapterSummary>>(Nothing()),
                            { apply(::Just, BookChapterSummary.fromDocument(it))  }),
                      // Conclusion
                      split(doc.maybeList("content"),
                            effValue(listOf()),
                            { it.map { BookContentId.fromDocument(it) } }),
                      // Sections
                      doc.list("sections") apply {
                          it.mapMut { BookSection.fromDocument(it) }
                      })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.chapterId().toDocument(),
        "title" to this.title().toDocument(),
        "sections" to DocList(this.sections().map { it.toDocument() } )
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun chapterId() : BookChapterId = this.chapterId


    fun title() : BookChapterTitle = this.title


    fun summary() : Maybe<BookChapterSummary> = this.summary


    fun sections() : List<BookSection> = this.sections


    fun content() : List<BookContentId> = this.content


    // | Sections
    // -----------------------------------------------------------------------------------------

    fun sectionWithId(sectionId : BookSectionId) : BookSection? =
        this.sectionById[sectionId]


    // | Content
    // -----------------------------------------------------------------------------------------

    fun content(book : Book) : List<BookContent> =
            this.content.map { book.content(it) }.filterJust()

}


/**
 * Rulebook Chapter Id
 */
data class BookChapterId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookChapterId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BookChapterId> = when (doc)
        {
            is DocText -> effValue(BookChapterId(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Book Chapter Title
 */
data class BookChapterTitle(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookChapterTitle>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BookChapterTitle> = when (doc)
        {
            is DocText -> effValue(BookChapterTitle(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}



/**
 * Book Chapter Summary
 */
data class BookChapterSummary(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookChapterSummary>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<BookChapterSummary> = when (doc)
        {
            is DocText -> effValue(BookChapterSummary(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


}


