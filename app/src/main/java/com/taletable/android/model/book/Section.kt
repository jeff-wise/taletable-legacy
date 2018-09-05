
package com.taletable.android.model.book


import com.taletable.android.db.*
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.RowValue1
import com.taletable.android.lib.orm.RowValue3
import com.taletable.android.lib.orm.schema.CollValue
import com.taletable.android.lib.orm.schema.PrimValue
import com.taletable.android.lib.orm.schema.ProdValue
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.model.sheet.style.ElementFormat
import com.taletable.android.model.sheet.style.TextFormat
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
 * Section
 */
data class BookSection(val sectionId : BookSectionId,
                       val title : BookSectionTitle,
                       val introduction : List<BookContentId>,
                       val conclusion : List<BookContentId>,
                       val format : BookSectionFormat,
                       val subsections : MutableList<BookSubsection>)
                        : ToDocument, Serializable
{

    // | Indexes
    // -----------------------------------------------------------------------------------------

    private val subsectionById : MutableMap<BookSubsectionId, BookSubsection> =
                                    subsections().associateBy { it.subsectionId() }
                                            as MutableMap<BookSubsectionId, BookSubsection>

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookSection>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookSection> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookSection,
                      // Section Id
                      doc.at("id") apply { BookSectionId.fromDocument(it) },
                      // Title
                      doc.at("title") apply { BookSectionTitle.fromDocument(it) },
                      // Introduction
                      split(doc.maybeList("introduction"),
                            effValue(listOf()),
                            { it.map { BookContentId.fromDocument(it) } }),
                      // Conclusion
                      split(doc.maybeList("conclusion"),
                            effValue(listOf()),
                            { it.map { BookContentId.fromDocument(it) } }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(BookSectionFormat.default()),
                            { BookSectionFormat.fromDocument(it) }),
                      // Subsections
                      split(doc.maybeList("subsections"),
                            effValue(mutableListOf()),
                            { it.mapMut { BookSubsection.fromDocument(it) } })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.sectionId().toDocument(),
        "title" to this.title().toDocument(),
        "subsections" to DocList(this.subsections().map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun sectionId() : BookSectionId = this.sectionId


    fun title() : BookSectionTitle = this.title


    fun introduction() : List<BookContentId> = this.introduction


    fun conclusion() : List<BookContentId> = this.conclusion


    fun subsections() : List<BookSubsection> = this.subsections


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun subsectionWithId(subsectionId : BookSubsectionId) : BookSubsection? =
        this.subsectionById[subsectionId]


    // -----------------------------------------------------------------------------------------
    // CONTENT
    // -----------------------------------------------------------------------------------------

    fun introductionContent(book : Book) : List<BookContent> =
            this.introduction.map { book.content(it) }.filterJust()

}


/**
 * Section Id
 */
data class BookSectionId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookSectionId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BookSectionId> = when (doc)
        {
            is DocText -> effValue(BookSectionId(doc.text))
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
 * Section Title
 */
data class BookSectionTitle(val value : String) : ToDocument, SQLSerializable, java.io.Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookSectionTitle>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BookSectionTitle> = when (doc)
        {
            is DocText -> effValue(BookSectionTitle(doc.text))
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
 * Book Section Format
 */
data class BookSectionFormat(val pageHeaderFormat : BookSectionPageHeaderFormat)
                             : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookSectionFormat>
    {

        fun defaultPageHeaderFormat()     = BookSectionPageHeaderFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookSectionFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookSectionFormat,
                      // Page Header Format
                      split(doc.maybeList("page_header_format"),
                            effValue(defaultPageHeaderFormat()),
                            { BookSectionPageHeaderFormat.fromDocument(it) } )
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }

        fun default() = BookSectionFormat(defaultPageHeaderFormat())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "page_header_format" to this.pageHeaderFormat.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun pageHeaderFormat() : BookSectionPageHeaderFormat = this.pageHeaderFormat


}


/**
 * Section
 */
data class BookSectionPageHeaderFormat(val elementFormat : ElementFormat,
                                       val chapterNameFormat : TextFormat,
                                       val sectionNameFormat: TextFormat)
                                        : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookSectionPageHeaderFormat>
    {


        fun defaultElementFormat()     = ElementFormat.default()
        fun defaultChapterNameFormat() = TextFormat.default()
        fun defaultSectionNameFormat() = TextFormat.default()

        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookSectionPageHeaderFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookSectionPageHeaderFormat,
                      // Element Format
                      split(doc.maybeList("element_format"),
                            effValue(defaultElementFormat()),
                            { ElementFormat.fromDocument(it) } ),
                      // Chapter Name Format
                      split(doc.maybeList("chapter_name_format"),
                            effValue(defaultChapterNameFormat()),
                            { TextFormat.fromDocument(it) } ),
                      // Section Name Format
                      split(doc.maybeList("section_name_format"),
                            effValue(defaultSectionNameFormat()),
                            { TextFormat.fromDocument(it) } )
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }

        fun default() = BookSectionPageHeaderFormat(defaultElementFormat(),
                                                    defaultChapterNameFormat(),
                                                    defaultSectionNameFormat())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "element_format" to this.elementFormat.toDocument(),
        "chapter_name_format" to this.chapterNameFormat.toDocument(),
        "section_name_format" to this.sectionNameFormat.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun elementFormat() : ElementFormat = this.elementFormat


    fun chapterNameFormat() : TextFormat = this.chapterNameFormat


    fun sectionNameFormat() : TextFormat = this.sectionNameFormat


}

