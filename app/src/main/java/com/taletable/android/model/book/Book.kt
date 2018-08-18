
package com.taletable.android.model.book


import com.taletable.android.db.*
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.*
import com.taletable.android.lib.orm.schema.CollValue
import com.taletable.android.lib.orm.schema.PrimValue
import com.taletable.android.lib.orm.schema.ProdValue
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.model.game.Author
import com.taletable.android.model.engine.Engine
import com.taletable.android.model.engine.variable.Variable
import com.taletable.android.model.sheet.group.GroupIndex
import com.taletable.android.model.sheet.group.GroupReference
import com.taletable.android.model.sheet.style.ElementFormat
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.*
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import maybe.filterJust
import java.io.Serializable
import java.util.*



/**
 * Book
 */
data class Book(val bookId : EntityId,
                val bookInfo : BookInfo,
                val settings : BookSettings,
                val engine : Engine,
                val variables : MutableList<Variable>,
                val groupIndex : GroupIndex,
                val content : List<BookContent>,
                val introduction : List<BookContentId>,
                val conclusion : List<BookContentId>,
                val chapters : MutableList<BookChapter>)
                 : ToDocument, Entity, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INDEXES
    // -----------------------------------------------------------------------------------------

    private val chapterById : MutableMap<BookChapterId, BookChapter> =
                                        chapters().associateBy { it.chapterId() }
                                                as MutableMap<BookChapterId, BookChapter>


    private val contentById : MutableMap<BookContentId,BookContent> =
                                        content.associateBy { it.id() }
                                                as MutableMap<BookContentId,BookContent>

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------


    companion object : Factory<Book>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Book> = when (doc)
        {
            is DocDict ->
            {
                apply(::Book,
                      // Book Id
                      doc.at("book_id") apply { EntityId.fromDocument(it) },
                      // Book Info
                      doc.at("book_info") apply { BookInfo.fromDocument(it) },
                      // Book Settings
                      split(doc.maybeAt("settings"),
                            effValue(BookSettings.default()),
                            { BookSettings.fromDocument(it) }),
                      // Engine
                      doc.at("engine") apply { Engine.fromDocument(it) },
                      // Variables
                      split(doc.maybeList("variables"),
                            effValue(mutableListOf()),
                            { it.mapMut { Variable.fromDocument(it) } }),
                      // Group Index
                      split(doc.maybeAt("group_index"),
                            effValue(GroupIndex(mutableListOf(), mutableListOf())),
                            { GroupIndex.fromDocument(it) }),
                      // Content
                      split(doc.maybeList("content"),
                            effValue(listOf()),
                            { it.map { BookContent.fromDocument(it) } }),
                      // Introduction
                      split(doc.maybeList("introduction"),
                            effValue(listOf()),
                            { it.map { BookContentId.fromDocument(it) } }),
                      // Conclusion
                      split(doc.maybeList("conclusion"),
                            effValue(listOf()),
                            { it.map { BookContentId.fromDocument(it) } }),
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
        "book_id" to this.bookId.toDocument(),
        "book_info" to this.bookInfo.toDocument(),
        "book_settings" to this.settings.toDocument(),
        "engine" to this.engine.toDocument(),
        "chapters" to DocList(this.chapters().map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun bookInfo() : BookInfo = this.bookInfo


    fun settings() : BookSettings = this.settings


    fun engine() : Engine = this.engine


    fun introduction() : List<BookContentId> = this.introduction


    fun conclusion() : List<BookContentId> = this.conclusion


    fun chapters() : List<BookChapter> = this.chapters


    // -----------------------------------------------------------------------------------------
    // ENTITY
    // -----------------------------------------------------------------------------------------

    override fun name() = this.bookInfo.title.value


    override fun summary() = this.bookInfo.summary.value


    override fun entityId() = this.bookId


    override fun category() = "Book"


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun chapter(chapterId : BookChapterId) : Maybe<BookChapter>
    {
        val chapter = this.chapterById[chapterId]
        return if (chapter != null)
            Just(chapter)
        else
            Nothing()
    }


    fun section(chapterId : BookChapterId, sectionId : BookSectionId) : Maybe<BookSection>
    {
        val chapter = this.chapterById[chapterId]

        if (chapter != null) {
            val section = chapter.sectionWithId(sectionId)
            if (section != null) {
                return Just(section)
            }
        }

        return Nothing()
    }


    fun subsection(chapterId : BookChapterId,
                   sectionId : BookSectionId,
                   subsectionId : BookSubsectionId) : Maybe<BookSubsection>
    {
        val chapter = this.chapterById[chapterId]

        if (chapter != null) {
            val section = chapter.sectionWithId(sectionId)
            if (section != null) {
                val subsection = section.subsectionWithId(subsectionId)
                if (subsection != null) {
                    return Just(subsection)
                }
            }
        }

        return Nothing()
    }


    // -----------------------------------------------------------------------------------------
    // CONTENT
    // -----------------------------------------------------------------------------------------

    fun content(id : BookContentId) : Maybe<BookContent>
    {
        val contentOrNull = this.contentById[id]
        return if (contentOrNull != null)
            Just(contentOrNull)
        else
            Nothing()
    }


    fun introductionContent() : List<BookContent> =
        this.introduction.map { this.content(it) }.filterJust()


    fun conclusionContent() : List<BookContent> =
            this.conclusion.map { this.content(it) }.filterJust()

    // -----------------------------------------------------------------------------------------
    // | Group Index
    // -----------------------------------------------------------------------------------------

    fun groupIndex() : GroupIndex = this.groupIndex


    // -----------------------------------------------------------------------------------------
    // | Variables
    // -----------------------------------------------------------------------------------------

    fun variables() : List<Variable> = this.variables


    fun addVariables(variables : List<Variable>)
    {
        this.variables.addAll(variables)
    }

}


/**
 * Rulebook Id
 */
//data class BookId(val value : String) : ToDocument, SQLSerializable, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object : Factory<BookId>
//    {
//        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookId> = when (doc)
//        {
//            is DocText -> effValue(BookId(doc.text))
//            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
//        }
//
//        fun fromYaml(yamlValue : YamlValue) : YamlParser<BookId> =
//            when (yamlValue)
//            {
//                is YamlText -> effValue(BookId(yamlValue.text))
//                else        -> error(UnexpectedTypeFound(YamlType.TEXT,
//                                                         yamlType(yamlValue),
//                                                         yamlValue.path))
//            }
//
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // TO DOCUMENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun toDocument() = DocText(this.value)
//
//
//    // -----------------------------------------------------------------------------------------
//    // SQL SERIALIZABLE
//    // -----------------------------------------------------------------------------------------
//
//    override fun asSQLValue() : SQLValue = SQLText({ this.value })
//
//}


/**
 * Book Info
 */
data class BookInfo(override val id : UUID,
                    val title : BookTitle,
                    val summary : BookSummary,
                    val authors : List<Author>,
                    val abstract : BookAbstract)
                     : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(title : BookTitle,
                summary : BookSummary,
                authors : List<Author>,
                abstract : BookAbstract)
        : this(UUID.randomUUID(),
               title,
               summary,
               authors,
               abstract)


    companion object : Factory<BookInfo>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookInfo> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookInfo,
                      // Title
                      doc.at("title") apply { BookTitle.fromDocument(it) },
                      // Summary
                      doc.at("summary") apply { BookSummary.fromDocument(it) },
                      // Authors
                      doc.list("authors") apply { it.map { Author.fromDocument(it) } },
                      // Abstract
                      doc.at("abstract") apply { BookAbstract.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "title" to this.title().toDocument(),
        "summary" to this.summary().toDocument(),
        "abstract" to this.abstract().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun title() : BookTitle = this.title


    fun summary() : BookSummary = this.summary


    fun authors() : List<Author> = this.authors


    fun abstract() : BookAbstract = this.abstract


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_BookInfoValue =
        RowValue4(bookInfoTable,
                  PrimValue(this.title),
                  PrimValue(this.summary),
                  CollValue(this.authors),
                  PrimValue(this.abstract))


    // -----------------------------------------------------------------------------------------
    // AUTHORS
    // -----------------------------------------------------------------------------------------

    fun authorListString() : String =
        if (this.authors.isEmpty())
        {
            ""
        }
        else if (this.authors.size == 1)
        {
            this.authors.firstOrNull()?.authorName?.value ?: ""
        }
        else if (this.authors.size == 2)
        {
            val firstAuthorName = this.authors[0].authorName.value
            val secondAuthorName = this.authors[1].authorName.value
            "$firstAuthorName and $secondAuthorName"
        }
        else
        {
            var s = ""
            this.authors.forEachIndexed { index, author ->
                if (index > 0)
                    s += ", "

                if (index == (authors.size - 1))
                    s += "and "

                s += author.authorName.value
            }
            s
        }


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
 * Book Summary
 */
data class BookSummary(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookSummary>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookSummary> = when (doc)
        {
            is DocText -> effValue(BookSummary(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
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
 * Book Settings
 */
data class BookSettings(override val id : UUID,
                        val themeId : ThemeId)
                         : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(themeId : ThemeId)
        : this(UUID.randomUUID(),
               themeId)


    companion object : Factory<BookSettings>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookSettings> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookSettings,
                      // Theme Id
                      split(doc.maybeAt("theme_id"),
                            effValue<ValueError,ThemeId>(ThemeId.Light),
                            { ThemeId.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = BookSettings(ThemeId.Light)

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "theme_id" to this.themeId.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun themeId() : ThemeId = this.themeId


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_BookSettingsValue =
        RowValue1(bookSettingsTable,
                  PrimValue(this.themeId))

}


/**
 * Book Content
 */
data class BookContent(private val id : BookContentId,
                       private val title : BookContentTitle,
                       private val groupReferences : List<GroupReference>)
                        : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookContent>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookContent> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookContent,
                      // Id
                      doc.at("id") apply { BookContentId.fromDocument(it) },
                      // Title
                      doc.at("title") apply { BookContentTitle.fromDocument(it) },
                      // Group References
                      doc.list("group_references") apply {
                          it.map { doc -> GroupReference.fromDocument(doc) } }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "group_references" to DocList(this.groupReferences.map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun id() : BookContentId = this.id


    fun title() : BookContentTitle = this.title


    fun groupReferences() : List<GroupReference> = this.groupReferences

}


/**
 * Book Content Id
 */
data class BookContentId(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookContentId>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookContentId> = when (doc)
        {
            is DocText -> effValue(BookContentId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value.toString())

}


/**
 * Book Content Title
 */
data class BookContentTitle(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookContentTitle>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookContentTitle> = when (doc)
        {
            is DocText -> effValue(BookContentTitle(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

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

