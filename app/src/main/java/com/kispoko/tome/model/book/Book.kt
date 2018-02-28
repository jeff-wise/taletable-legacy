
package com.kispoko.tome.model.book


import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue4
import com.kispoko.tome.lib.orm.RowValue5
import com.kispoko.tome.lib.orm.schema.CollValue
import com.kispoko.tome.lib.orm.schema.MaybePrimValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.Author
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable
import java.util.*



/**
 * Book
 */
data class Book(override val id : UUID,
                val bookId: BookId,
                val title : BookTitle,
                val authors : List<Author>,
                val abstract : BookAbstract,
                val introduction : BookIntroduction,
                val chapters : MutableList<BookChapter>)
                     : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INDEXES
    // -----------------------------------------------------------------------------------------

    private val chapterById : MutableMap<BookChapterId, BookChapter> =
                                        chapters().associateBy { it.chapterId() }
                                                as MutableMap<BookChapterId, BookChapter>


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(rulebookId : BookId,
                title : BookTitle,
                authors : List<Author>,
                abstract : BookAbstract,
                introduction : BookIntroduction,
                chapters : List<BookChapter>)
        : this(UUID.randomUUID(),
               rulebookId,
               title,
               authors,
               abstract,
               introduction,
               chapters.toMutableList())


    companion object : Factory<Book>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Book> = when (doc)
        {
            is DocDict ->
            {
                apply(::Book,
                      // Rulebook Id
                      doc.at("rulebook_id") apply { BookId.fromDocument(it) },
                      // Title
                      doc.at("title") apply { BookTitle.fromDocument(it) },
                      // Title
                      doc.list("authors") apply { it.map { Author.fromDocument(it) } },
                      // Abstract
                      doc.at("abstract") apply { BookAbstract.fromDocument(it) },
                      // Introduction
                      doc.at("introduction") apply { BookIntroduction.fromDocument(it) },
                      // Chapters
                      doc.list("chapters") apply {
                          it.mapMut { BookChapter.fromDocument(it) }
                      })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "rulebook_id" to this.rulebookId().toDocument(),
        "title" to this.title().toDocument(),
        "abstract" to this.abstract().toDocument(),
        "introduction" to this.introduction().toDocument(),
        "chapters" to DocList(this.chapters().map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun rulebookId() : BookId = this.bookId


    fun title() : BookTitle = this.title


    fun authors() : List<Author> = this.authors


    fun abstract() : BookAbstract = this.abstract


    fun introduction() : BookIntroduction = this.introduction


    fun chapters() : List<BookChapter> = this.chapters


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun subsection(rulebookReference : BookReference) : BookSubsection?
    {
        val chapter = this.chapterById[rulebookReference.chapterId()]

        // TODO use as maybe refactoring example
        if (chapter != null)
        {
            val sectionId = rulebookReference.sectionId()
            when (sectionId)
            {
                is Just ->
                {
                    val section = chapter.sectionWithId(sectionId.value)
                    if (section != null)
                    {
                        val subsectionId = rulebookReference.subsectionId()
                        when (subsectionId)
                        {
                            is Just ->
                            {
                                val subsection = section.subsectionWithId(subsectionId.value)
                                if (subsection != null)
                                    return subsection
                            }
                        }
                    }
                }
            }
        }

        return null
    }


    fun excerpt(rulebookReference : BookReference) : RulebookExcerpt?
    {
        val chapter = this.chapterById[rulebookReference.chapterId()]

        // TODO use as maybe refactoring example
        if (chapter != null)
        {
            val sectionId = rulebookReference.sectionId()
            when (sectionId)
            {
                is Just ->
                {
                    val section = chapter.sectionWithId(sectionId.value)
                    if (section != null)
                    {
                        val subsectionId = rulebookReference.subsectionId()
                        when (subsectionId)
                        {
                            is Just ->
                            {
                                val subsection = section.subsectionWithId(subsectionId.value)
                                if (subsection != null)
                                    return RulebookExcerpt(subsection.titleString(), subsection.body().value)
                            }
                            is Nothing -> return RulebookExcerpt(section.title().value, section.body().value)
                        }
                    }
                }
            }
        }

        return null
    }


    fun referencePath(rulebookReference : BookReference) : RulebookReferencePath?
    {
        val chapter = this.chapterById[rulebookReference.chapterId()]

        if (chapter != null)
        {
            val sectionId = rulebookReference.sectionId()
            when (sectionId)
            {
                is Just ->
                {
                    val section = chapter.sectionWithId(sectionId.value)
                    if (section != null)
                    {
                        val subsectionId = rulebookReference.subsectionId()
                        when (subsectionId)
                        {
                            is Just ->
                            {
                                val subsection = section.subsectionWithId(subsectionId.value)
                                if (subsection != null)
                                    return RulebookReferencePath(this.title(),
                                            chapter.title(),
                                            Just(section.title()),
                                            Just(subsection.title()))
                            }
                            else ->{
                                return RulebookReferencePath(this.title(),
                                        chapter.title(),
                                        Just(section.title()),
                                        Nothing())
                            }
                        }
                    }
                }
                else -> {
                    return RulebookReferencePath(this.title(), chapter.title(), Nothing(), Nothing())
                }
            }
        }

        return null
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_RulebookValue =
        RowValue5(rulebookTable,
                  PrimValue(this.title),
                  CollValue(this.authors),
                  PrimValue(this.abstract),
                  PrimValue(this.introduction),
                  CollValue(this.chapters))

}


/**
 * Rulebook Id
 */
data class BookId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookId>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookId> = when (doc)
        {
            is DocText -> effValue(BookId(doc.text))
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
 * Book Title
 */
data class BookTitle(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookTitle>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BookTitle> = when (doc)
        {
            is DocText -> effValue(BookTitle(doc.text))
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
 * Abstract
 */
data class BookAbstract(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookAbstract>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BookAbstract> = when (doc)
        {
            is DocText -> effValue(BookAbstract(doc.text))
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
 * Introduction
 */
data class BookIntroduction(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookIntroduction>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookIntroduction> = when (doc)
        {
            is DocText -> effValue(BookIntroduction(doc.text))
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


// ---------------------------------------------------------------------------------------------
// BOOK REFERENCE
// --------------------------------------------------------------------------------------------

/**
 * Book Reference
 */
data class BookReference(override val id : UUID,
                         val rulebookId : BookId,
                         val chapterId : BookChapterId,
                         val sectionId : Maybe<BookSectionId>,
                         val subsectionId : Maybe<BookSubsectionId>)
                              : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(rulebookId : BookId,
                chapterId : BookChapterId,
                sectionId : Maybe<BookSectionId>,
                subsectionId : Maybe<BookSubsectionId>)
        : this(UUID.randomUUID(),
               rulebookId,
               chapterId,
               sectionId,
               subsectionId)


    companion object : Factory<BookReference>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookReference> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookReference,
                      // Rulebook Id
                      doc.at("rulebook_id") apply { BookId.fromDocument(it) },
                      // Chapter Id
                      doc.at("chapter_id") apply { BookChapterId.fromDocument(it) },
                      // Section Id
                      split(doc.maybeAt("section_id"),
                            effValue<ValueError,Maybe<BookSectionId>>(Nothing()),
                            { effApply(::Just, BookSectionId.fromDocument(it)) }),
                      // Subsection Id
                      split(doc.maybeAt("subsection_id"),
                            effValue<ValueError,Maybe<BookSubsectionId>>(Nothing()),
                            { effApply(::Just, BookSubsectionId.fromDocument(it)) })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "rulebook_id" to this.rulebookId().toDocument(),
        "chapter_id" to this.chapterId().toDocument()
    ))
    .maybeMerge(this.sectionId.apply {
        Just(Pair("section_id", it.toDocument() as SchemaDoc)) })
    .maybeMerge(this.subsectionId.apply {
        Just(Pair("subsection_id", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun rulebookId() : BookId = this.rulebookId


    fun chapterId() : BookChapterId = this.chapterId


    fun sectionId() : Maybe<BookSectionId> = this.sectionId


    fun subsectionId() : Maybe<BookSubsectionId> = this.subsectionId


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_RulebookReferenceValue =
        RowValue4(rulebookReferenceTable,
                  PrimValue(this.rulebookId),
                  PrimValue(this.chapterId),
                  MaybePrimValue(this.sectionId),
                  MaybePrimValue(this.subsectionId))


}


/**
 * Rulebook Reference Path
 */
data class RulebookReferencePath(val bookTitle : BookTitle,
                                 val chapterTitle : BookChapterTitle,
                                 val sectionTitle : Maybe<BookSectionTitle>,
                                 val subsectionTitle : Maybe<BookSubsectionTitle>)
                                  : Serializable
{

    override fun toString() : String
    {
        var s = ""

        s += bookTitle.value
        s += " \u203A "
        s += chapterTitle.value

        when (this.sectionTitle) {
            is Just -> {
                s += " \u203A "
                s += this.sectionTitle.value.value
            }
        }

        when (this.subsectionTitle) {
            is Just -> {
                s += " \u203A "
                s += this.subsectionTitle.value.value
            }
        }

        return s
    }

}

data class RulebookExcerpt(val title : String, val body : String) : Serializable

