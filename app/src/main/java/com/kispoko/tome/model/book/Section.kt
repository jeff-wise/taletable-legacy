
package com.kispoko.tome.model.book


import com.kispoko.tome.db.DB_BookSectionValue
import com.kispoko.tome.db.bookSectionTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue4
import com.kispoko.tome.lib.orm.schema.CollValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
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
import java.io.Serializable
import java.util.*



/**
 * Section
 */
data class BookSection(override val id : UUID,
                       val sectionId : BookSectionId,
                       val title : BookSectionTitle,
                       val body : BookContent,
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
                subsections : List<BookSubsection>)
        : this(UUID.randomUUID(),
               sectionId,
               title,
               body,
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
        RowValue4(bookSectionTable, PrimValue(this.sectionId),
                                    PrimValue(this.title),
                                    ProdValue(this.body),
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
 * Section Body
 */
data class BookSectionBody(val value : String) : ToDocument, SQLSerializable, java.io.Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookSectionBody>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BookSectionBody> = when (doc)
        {
            is DocText -> effValue(BookSectionBody(doc.text))
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
