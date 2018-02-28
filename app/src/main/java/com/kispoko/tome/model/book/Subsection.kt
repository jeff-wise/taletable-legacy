
package com.kispoko.tome.model.book


import com.kispoko.tome.db.DB_RulebookSubsectionValue
import com.kispoko.tome.db.rulebookSubsectionTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import effect.apply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Subsection
 */
data class BookSubsection(override val id : UUID,
                          val subsectionId : BookSubsectionId,
                          val title : BookSubsectionTitle,
                          val body : BookSubsectionBody)
                                : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(subsectionId : BookSubsectionId,
                title : BookSubsectionTitle,
                body : BookSubsectionBody)
        : this(UUID.randomUUID(),
               subsectionId,
               title,
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
                      // Body
                      doc.at("body") apply { BookSubsectionBody.fromDocument(it) })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.subsectionId().toDocument(),
        "title" to this.title().toDocument(),
        "body" to this.body().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun subsectionId() : BookSubsectionId = this.subsectionId


    fun title() : BookSubsectionTitle = this.title


    fun titleString() : String = this.title.value


    fun body() : BookSubsectionBody = this.body


    fun bodyString() : String = this.body.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_RulebookSubsectionValue =
        RowValue3(rulebookSubsectionTable, PrimValue(this.subsectionId),
                                           PrimValue(this.title),
                                           PrimValue(this.body))

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
 * Book Subsection Body
 */
data class BookSubsectionBody(val value : String) : ToDocument, SQLSerializable, java.io.Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookSubsectionBody>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BookSubsectionBody> = when (doc)
        {
            is DocText -> effValue(BookSubsectionBody(doc.text))
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
