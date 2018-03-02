
package com.kispoko.tome.model.book


import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.*
import com.kispoko.tome.lib.orm.schema.CollValue
import com.kispoko.tome.lib.orm.schema.MaybeProdValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.Author
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.style.ElementFormat
import com.kispoko.tome.model.sheet.style.TextFormat
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
                val introduction : Maybe<BookContent>,
                val conclusion : Maybe<BookContent>,
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
                introduction : Maybe<BookContent>,
                conclusion : Maybe<BookContent>,
                chapters : List<BookChapter>)
        : this(UUID.randomUUID(),
               rulebookId,
               title,
               authors,
               abstract,
               introduction,
               conclusion,
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
                      split(doc.maybeAt("introduction"),
                            effValue<ValueError,Maybe<BookContent>>(Nothing()),
                            { apply(::Just, BookContent.fromDocument(it)) }),
                      // Conclusion
                      split(doc.maybeAt("conclusion"),
                            effValue<ValueError,Maybe<BookContent>>(Nothing()),
                            { apply(::Just, BookContent.fromDocument(it)) }),
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
        "chapters" to DocList(this.chapters().map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun rulebookId() : BookId = this.bookId


    fun title() : BookTitle = this.title


    fun authors() : List<Author> = this.authors


    fun abstract() : BookAbstract = this.abstract


    fun introduction() : Maybe<BookContent> = this.introduction


    fun conclusion() : Maybe<BookContent> = this.conclusion


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


    override fun rowValue() : DB_BookValue =
        RowValue6(bookTable,
                  PrimValue(this.title),
                  CollValue(this.authors),
                  PrimValue(this.abstract),
                  MaybeProdValue(this.introduction),
                  MaybeProdValue(this.conclusion),
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


/**
 * Book Content
 */
data class BookContent(override val id : UUID,
                       val groups : List<Group>)
                        : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(groups : List<Group>)
        : this(UUID.randomUUID(),
               groups)


    companion object : Factory<BookContent>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookContent> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookContent,
                      // Groups
                      doc.list("groups") apply {
                          it.mapIndexed { doc, index -> Group.fromDocument(doc, index) } }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "groups" to DocList(this.groups.map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun groups() : List<Group> = this.groups


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_BookContentValue =
        RowValue1(bookContentTable,
                  CollValue(this.groups))

}


/**
 * Book Format
 */
data class BookFormat(override val id : UUID,
                      val elementFormat : ElementFormat,
                      val chapterIndexFormat : ElementFormat,
                      val chapterButtonFormat : ChapterButtonFormat)
                        : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(elementFormat : ElementFormat,
                chapterIndexFormat : ElementFormat,
                chapterButtonFormat : ChapterButtonFormat)
        : this(UUID.randomUUID(),
               elementFormat,
               chapterIndexFormat,
               chapterButtonFormat)


    companion object : Factory<BookFormat>
    {

        private fun defaultElementFormat()       = ElementFormat.default()
        private fun defaultChapterIndexFormat()  = ElementFormat.default()
        private fun defaultChapterButtonFormat() = ChapterButtonFormat.default()

        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookFormat,
                      // Element Format
                      split(doc.maybeAt("element_format"),
                            effValue(defaultElementFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Chapter Index Format
                      split(doc.maybeAt("chapter_index_format"),
                            effValue(defaultChapterIndexFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Chapter Button Format
                      split(doc.maybeAt("chapter_button_format"),
                            effValue(defaultChapterButtonFormat()),
                            { ChapterButtonFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "element_format" to this.elementFormat.toDocument(),
        "chapter_index_format" to this.chapterIndexFormat.toDocument(),
        "chapter_button_format" to this.chapterButtonFormat.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun elementFormat() : ElementFormat = this.elementFormat


    fun chapterIndexFormat() : ElementFormat = this.chapterIndexFormat


    fun chapterButtonFormat() : ChapterButtonFormat = this.chapterButtonFormat


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_BookFormatValue =
        RowValue3(bookFormatTable,
                  ProdValue(this.elementFormat),
                  ProdValue(this.chapterIndexFormat),
                  ProdValue(this.chapterButtonFormat))

}


/**
 * Chapter Button Format
 */
data class ChapterButtonFormat(override val id : UUID,
                               val elementFormat : ElementFormat,
                               val indexFormat : TextFormat,
                               val titleFormat : TextFormat,
                               val summaryFormat : TextFormat)
                                : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(elementFormat : ElementFormat,
                indexFormat : TextFormat,
                titleFormat : TextFormat,
                summaryFormat : TextFormat)
        : this(UUID.randomUUID(),
               elementFormat,
               indexFormat,
               titleFormat,
               summaryFormat)


    companion object : Factory<ChapterButtonFormat>
    {

        private fun defaultElementFormat()  = ElementFormat.default()
        private fun defaultIndexFormat()    = TextFormat.default()
        private fun defaultTitleFormat()    = TextFormat.default()
        private fun defaultSummaryFormat()  = TextFormat.default()

        override fun fromDocument(doc : SchemaDoc) : ValueParser<ChapterButtonFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::ChapterButtonFormat,
                      // Element Format
                      split(doc.maybeAt("element_format"),
                            effValue(defaultElementFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Index Format
                      split(doc.maybeAt("index_format"),
                            effValue(defaultIndexFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Title Format
                      split(doc.maybeAt("title_format"),
                            effValue(defaultTitleFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Summary Format
                      split(doc.maybeAt("summary_format"),
                            effValue(defaultSummaryFormat()),
                            { TextFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ChapterButtonFormat(defaultElementFormat(),
                                            defaultIndexFormat(),
                                            defaultTitleFormat(),
                                            defaultSummaryFormat())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "element_format" to this.elementFormat.toDocument(),
        "index_format" to this.indexFormat.toDocument(),
        "title_format" to this.titleFormat.toDocument(),
        "summary_format" to this.summaryFormat.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun elementFormat() : ElementFormat = this.elementFormat


    fun indexFormat() : TextFormat = this.indexFormat


    fun titleFormat() : TextFormat = this.titleFormat


    fun summaryFormat() : TextFormat = this.summaryFormat


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_ChapterButtonFormatValue =
        RowValue4(chapterButtonFormatTable,
                  ProdValue(this.elementFormat),
                  ProdValue(this.indexFormat),
                  ProdValue(this.titleFormat),
                  ProdValue(this.summaryFormat))

}

