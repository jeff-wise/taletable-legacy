
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
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.*
import maybe.Nothing
import java.io.Serializable


/**
 * Subsection
 */
data class BookSubsection(val subsectionId : BookSubsectionId,
                          val title : BookSubsectionTitle,
                          val subtitle : Maybe<BookSubsectionSubtitle>,
                          val body : List<BookContentId>,
                          val entries : List<BookSubsectionEntry>)
                           : ToDocument, Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

//    constructor(subsectionId : BookSubsectionId,
//                title : BookSubsectionTitle,
//                subtitle : Maybe<BookSubsectionSubtitle>,
//                body : List<BookContentId>)
//        : this(UUID.randomUUID(),
//               subsectionId,
//               title,
//               subtitle,
//               body)


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
                      // Subtitle
                      split(doc.maybeAt("subtitle"),
                            effValue<ValueError,Maybe<BookSubsectionSubtitle>>(Nothing()),
                            { apply(::Just, BookSubsectionSubtitle.fromDocument(it)) }),
                      // Body
                      split(doc.maybeList("body"),
                            effValue(listOf()),
                            { it.map { BookContentId.fromDocument(it) } }),
                      // Entries
                      split(doc.maybeList("entries"),
                            effValue(listOf()),
                            { it.map { BookSubsectionEntry.fromDocument(it) } })
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


    fun subtitle() : Maybe<BookSubsectionSubtitle> = this.subtitle


    fun body() : List<BookContentId> = this.body


    // | CONTENT
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



sealed class BookSubsectionEntry()
{
    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookSubsectionEntry>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<BookSubsectionEntry> =
            when (doc.case())
            {
                "book_subsection_entry_card_group"  -> BookSubsectionEntryCardGroup.fromDocument(doc) as ValueParser<BookSubsectionEntry>
                "book_subsection_entry_card"        -> BookSubsectionEntryCard.fromDocument(doc) as ValueParser<BookSubsectionEntry>
                "book_subsection_entry_card_inline_expandable" -> BookSubsectionEntryCardInlineExpandable.fromDocument(doc) as ValueParser<BookSubsectionEntry>
                else                 -> {
                    effError(UnknownCase(doc.case(), doc.path))
                }
            }
    }

}


data class BookSubsectionEntryCard(
        val entryContent : BookContentId) : BookSubsectionEntry()
{

    companion object : Factory<BookSubsectionEntryCard>
    {

        override fun fromDocument(doc: SchemaDoc)
                : ValueParser<BookSubsectionEntryCard> = when (doc)
        {
            is DocDict -> {
                apply(::BookSubsectionEntryCard,
                      // Entry Content
                      doc.at("entry_content").apply { BookContentId.fromDocument(it) }
                )
            }
            else -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }

    }

}


data class BookSubsectionEntryCardInlineExpandable(
        val cardId : BookCardId) : BookSubsectionEntry()
{

    companion object : Factory<BookSubsectionEntryCardInlineExpandable>
    {

        override fun fromDocument(doc: SchemaDoc)
                : ValueParser<BookSubsectionEntryCardInlineExpandable> = when (doc)
        {
            is DocDict -> {
                apply(::BookSubsectionEntryCardInlineExpandable,
                      // Card Id
                      doc.at("card_id").apply { BookCardId.fromDocument(it) }
                )
            }
            else -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }

    }

}

data class BookSubsectionEntryCardGroup(
        val title : String,
        val cardEntries : List<BookContentId>) : BookSubsectionEntry()
{

    companion object : Factory<BookSubsectionEntryCardGroup>
    {

        override fun fromDocument(doc: SchemaDoc)
                : ValueParser<BookSubsectionEntryCardGroup> = when (doc)
        {
            is DocDict -> {
                apply(::BookSubsectionEntryCardGroup,
                      // Group Title
                      doc.text("title"),
                      // Subsections
                      split(doc.maybeList("card_entries"),
                            effValue(listOf()),
                            { it.map { BookContentId.fromDocument(it) } })
                )
            }
            else -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }

    }

}


