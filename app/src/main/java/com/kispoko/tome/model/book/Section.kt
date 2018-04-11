
package com.kispoko.tome.model.book


import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue1
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.RowValue4
import com.kispoko.tome.lib.orm.schema.CollValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.sheet.style.ElementFormat
import com.kispoko.tome.model.sheet.style.TextFormat
import effect.apply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Section
 */
data class BookSection(override val id : UUID,
                       val sectionId : BookSectionId,
                       val title : BookSectionTitle,
                       val body : BookContent,
                       val format : BookSectionFormat,
                       val subsections : MutableList<BookSubsection>)
                            : ToDocument, ProdType, java.io.Serializable
{

    // -----------------------------------------------------------------------------------------
    // INDEXES
    // -----------------------------------------------------------------------------------------

    private val subsectionById : MutableMap<BookSubsectionId, BookSubsection> =
                                    subsections().associateBy { it.subsectionId() }
                                            as MutableMap<BookSubsectionId, BookSubsection>


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(sectionId : BookSectionId,
                title : BookSectionTitle,
                body : BookContent,
                format : BookSectionFormat,
                subsections : List<BookSubsection>)
        : this(UUID.randomUUID(),
               sectionId,
               title,
               body,
               format,
               subsections.toMutableList())


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
                      // Body
                      doc.at("body") apply { BookContent.fromDocument(it) },
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(BookSectionFormat.default()),
                            { BookSectionFormat.fromDocument(it) }),
                      // Subsections
                      split(doc.maybeList("subsections"),
                            effValue(listOf()),
                            { it.map { BookSubsection.fromDocument(it) } })
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
        "body" to this.body().toDocument(),
        "subsections" to DocList(this.subsections().map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun sectionId() : BookSectionId = this.sectionId


    fun title() : BookSectionTitle = this.title


    fun body() : BookContent = this.body


    fun subsections() : List<BookSubsection> = this.subsections


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun subsectionWithId(subsectionId : BookSubsectionId) : BookSubsection? =
        this.subsectionById[subsectionId]


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_BookSectionValue =
        RowValue3(bookSectionTable, PrimValue(this.sectionId),
                                    PrimValue(this.title),
                                    CollValue(this.subsections))

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
data class BookSectionFormat(override val id : UUID,
                             val pageHeaderFormat : BookSectionPageHeaderFormat)
                             : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(pageHeaderFormat : BookSectionPageHeaderFormat)
        : this(UUID.randomUUID(),
               pageHeaderFormat)


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


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_BookSectionFormatValue =
        RowValue1(bookSectionFormatTable,
                  ProdValue(this.pageHeaderFormat))

}


/**
 * Section
 */
data class BookSectionPageHeaderFormat(override val id : UUID,
                                       val elementFormat : ElementFormat,
                                       val chapterNameFormat : TextFormat,
                                       val sectionNameFormat: TextFormat)
                                        : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(elementFormat : ElementFormat,
                chapterNameFormat : TextFormat,
                sectionNameFormat : TextFormat)
        : this(UUID.randomUUID(),
               elementFormat,
               chapterNameFormat,
               sectionNameFormat)


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


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_BookSectionPageHeaderFormatValue =
        RowValue3(bookSectionPageHeaderFormatTable,
                  ProdValue(this.elementFormat),
                  ProdValue(this.chapterNameFormat),
                  ProdValue(this.sectionNameFormat))

}

