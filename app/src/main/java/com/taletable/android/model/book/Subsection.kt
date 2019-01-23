
package com.taletable.android.model.book


import com.taletable.android.db.DB_BookSubsectionValue
import com.taletable.android.db.bookSubsectionTable
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.RowValue2
import com.taletable.android.lib.orm.schema.PrimValue
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
import java.util.*



/**
 * Subsection
 */
data class BookSubsection(override val id : UUID,
                          val subsectionId : BookSubsectionId,
                          val title : BookSubsectionTitle,
                          val group : Maybe<BookSubsectionGroup>,
                          val subtitle : Maybe<BookSubsectionSubtitle>,
                          val body : List<BookContentId>)
                           : ToDocument, ProdType, Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    constructor(subsectionId : BookSubsectionId,
                title : BookSubsectionTitle,
                group : Maybe<BookSubsectionGroup>,
                subtitle : Maybe<BookSubsectionSubtitle>,
                body : List<BookContentId>)
        : this(UUID.randomUUID(),
               subsectionId,
               title,
               group,
               subtitle,
               body)


    companion object : Factory<BookSubsection>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookSubsection> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookSubsection,
                      // Id
                      doc.at("id") apply { BookSubsectionId.fromDocument(it) },
                      // Title
                      doc.at("title") apply { BookSubsectionTitle.fromDocument(it) },
                      // Group
                      split(doc.maybeAt("group"),
                            effValue<ValueError,Maybe<BookSubsectionGroup>>(Nothing()),
                            { apply(::Just, BookSubsectionGroup.fromDocument(it)) }),
                      // Subtitle
                      split(doc.maybeAt("subtitle"),
                            effValue<ValueError,Maybe<BookSubsectionSubtitle>>(Nothing()),
                            { apply(::Just, BookSubsectionSubtitle.fromDocument(it)) }),
                      // Body
                      split(doc.maybeList("body"),
                            effValue(listOf()),
                            { it.map { BookContentId.fromDocument(it) } })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.subsectionId().toDocument(),
        "title" to this.title().toDocument()
//        "content" to this.body().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun subsectionId() : BookSubsectionId = this.subsectionId


    fun title() : BookSubsectionTitle = this.title


    fun group() : Maybe<BookSubsectionGroup> = this.group


    fun subtitle() : Maybe<BookSubsectionSubtitle> = this.subtitle


    fun body() : List<BookContentId> = this.body


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_BookSubsectionValue =
        RowValue2(bookSubsectionTable, PrimValue(this.subsectionId),
                                       PrimValue(this.title))


    // -----------------------------------------------------------------------------------------
    // CONTENT
    // -----------------------------------------------------------------------------------------

    fun bodyContent(book : Book) : List<BookContent> =
            this.body.map { book.content(it) }.filterJust()

}


/**
 * Book Subsection Id
 */
data class BookSubsectionId(val value : String) : ToDocument, SQLSerializable, java.io.Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookSubsectionId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BookSubsectionId> = when (doc)
        {
            is DocText -> effValue(BookSubsectionId(doc.text))
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
 * Subsection Title
 */
data class BookSubsectionTitle(val value : String) : ToDocument, SQLSerializable, java.io.Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookSubsectionTitle>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BookSubsectionTitle> = when (doc)
        {
            is DocText -> effValue(BookSubsectionTitle(doc.text))
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
 * Subsection Title
 */
data class BookSubsectionSubtitle(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookSubsectionSubtitle>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookSubsectionSubtitle> = when (doc)
        {
            is DocText -> effValue(BookSubsectionSubtitle(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}


/**
 * Subsection Group
 */
data class BookSubsectionGroup(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookSubsectionGroup>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookSubsectionGroup> = when (doc)
        {
            is DocText -> effValue(BookSubsectionGroup(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}

