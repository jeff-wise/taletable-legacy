
package com.kispoko.tome.model.book


import com.kispoko.tome.db.DB_BookChapterValue
import com.kispoko.tome.db.bookChapterTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.schema.CollValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import effect.apply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import maybe.filterJust
import java.io.Serializable
import java.util.*



/**
 * Chapter
 */
data class BookChapter(override val id : UUID,
                       val chapterId : BookChapterId,
                       val title : BookChapterTitle,
                       val introduction : List<BookContentId>,
                       val conclusion : List<BookContentId>,
                       val sections : MutableList<BookSection>)
                            : ToDocument, ProdType, Serializable
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

    constructor(chapterId : BookChapterId,
                title : BookChapterTitle,
                introduction: List<BookContentId>,
                conclusion: List<BookContentId>,
                sections : List<BookSection>)
        : this(UUID.randomUUID(),
               chapterId,
               title,
               introduction,
               conclusion,
               sections.toMutableList())


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
                      // Introduction
                      split(doc.maybeList("introduction"),
                            effValue(listOf()),
                            { it.map { BookContentId.fromDocument(it) } }),
                      // Conclusion
                      split(doc.maybeList("conclusion"),
                            effValue(listOf()),
                            { it.map { BookContentId.fromDocument(it) } }),
                      // Sections
                      doc.list("sections") apply {
                          it.map { BookSection.fromDocument(it) }
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


    fun sections() : List<BookSection> = this.sections


    fun introduction() : List<BookContentId> = this.introduction


    fun conclusion() : List<BookContentId> = this.conclusion


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun sectionWithId(sectionId : BookSectionId) : BookSection? =
        this.sectionById[sectionId]


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_BookChapterValue =
        RowValue3(bookChapterTable, PrimValue(this.chapterId),
                                    PrimValue(this.title),
                                    CollValue(this.sections))


    // -----------------------------------------------------------------------------------------
    // CONTENT
    // -----------------------------------------------------------------------------------------

    fun introductionContent(book : Book) : List<BookContent> =
            this.introduction.map { book.content(it) }.filterJust()

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
data class BookChapterTitle(val value : String) : ToDocument, SQLSerializable, Serializable
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


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}

