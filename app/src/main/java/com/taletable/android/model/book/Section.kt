
package com.taletable.android.model.book


import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.model.engine.variable.VariableReference
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.style.ElementFormat
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.textListVariable
import com.taletable.android.rts.entity.textVariable
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import maybe.filterJust
import java.io.Serializable



/**
 * Section
 */
data class BookSection(val sectionId : BookSectionId,
                       val title : BookSectionTitle,
                       val introduction : List<BookContentId>,
                       val introductionLabel : Maybe<VariableReference>,
                       val header : List<BookContentId>,
                       val group: Maybe<BookSectionGroup>,
                       val position : Maybe<BookSectionEntryPosition>,
                       val format : BookSectionFormat,
                       val subsections : MutableList<BookSubsection>,
                       val entries : List<BookSectionEntry>)
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
                      // Introduction Label
                      split(doc.maybeAt("introduction_label"),
                            effValue<ValueError,Maybe<VariableReference>>(Nothing()),
                            { apply(::Just, VariableReference.fromDocument(it)) }),
                      // Header
                      split(doc.maybeList("header"),
                            effValue(listOf()),
                            { it.map { BookContentId.fromDocument(it) } }),
                      // Group
                      split(doc.maybeAt("group"),
                            effValue<ValueError,Maybe<BookSectionGroup>>(Nothing()),
                            { apply(::Just, BookSectionGroup.fromDocument(it)) }),
                      // Position
                      split(doc.maybeAt("entry_position"),
                            effValue<ValueError,Maybe<BookSectionEntryPosition>>(Nothing()),
                            { apply(::Just, BookSectionEntryPosition.fromDocument(it)) }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(BookSectionFormat.default()),
                            { BookSectionFormat.fromDocument(it) }),
                      // Subsections
                      split(doc.maybeList("subsections"),
                            effValue(mutableListOf()),
                            { it.mapMut { BookSubsection.fromDocument(it) } }),
                      // Entries
                      split(doc.maybeList("entries"),
                              effValue(listOf()),
                              { it.map { BookSectionEntry.fromDocument(it) } })
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


    fun group() : Maybe<BookSectionGroup> = this.group


    fun introduction() : List<BookContentId> = this.introduction


    fun conclusion() : List<BookContentId> = this.header


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

    fun headerContent(book : Book) : List<BookContent> =
            this.header.map { book.content(it) }.filterJust()


    fun introductionLabelValue(entityId : EntityId) : Maybe<String>
    {
        val maybeVarRef = this.introductionLabel
        return when (maybeVarRef) {
            is Just -> {
                val effValue = textVariable(maybeVarRef.value, entityId).apply { it.value(entityId) }
                when (effValue) {
                    is Val -> effValue.value
                    is Eff -> Nothing()
                }
            }
            is Nothing -> Nothing()
        }
    }

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


/**
 * Section Group
 */
data class BookSectionGroup(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookSectionGroup>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookSectionGroup> = when (doc)
        {
            is DocText -> effValue(BookSectionGroup(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}


/**
 * Section Entry Position
 */
data class BookSectionEntryPosition(val value : Int) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookSectionEntryPosition>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookSectionEntryPosition> = when (doc)
        {
            is DocNumber -> effValue(BookSectionEntryPosition(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())

}


sealed class BookSectionEntry()
{
    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookSectionEntry>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<BookSectionEntry> =
            when (doc.case())
            {
                "book_section_entry_simple"             -> BookSectionEntrySimple.fromDocument(doc) as ValueParser<BookSectionEntry>
                "book_section_entry_inline_expandable"  -> BookSectionEntryInlineExpandable.fromDocument(doc) as ValueParser<BookSectionEntry>
                "book_section_entry_card_group"         -> BookSectionEntryCardGroup.fromDocument(doc) as ValueParser<BookSectionEntry>
                "book_section_entry_card"               -> BookSectionEntryCard.fromDocument(doc) as ValueParser<BookSectionEntry>
                "book_section_entry_group"              -> BookSectionEntryGroup.fromDocument(doc) as ValueParser<BookSectionEntry>
                else                 -> {
                    effError(UnknownCase(doc.case(), doc.path))
                }
            }
    }

}


data class BookSectionEntrySimple(val subsectionId : BookSubsectionId) : BookSectionEntry()
{

    companion object : Factory<BookSectionEntrySimple>
    {

        override fun fromDocument(doc: SchemaDoc)
                : ValueParser<BookSectionEntrySimple> = when (doc)
        {
            is DocDict -> {
                apply(::BookSectionEntrySimple,
                      // Subsection Id
                      doc.at("subsection_id").apply { BookSubsectionId.fromDocument(it) }
                )
            }
            else -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }

    }

}

data class BookSectionEntryInlineExpandable(
        val contentId : BookContentId) : BookSectionEntry()
{

    companion object : Factory<BookSectionEntryInlineExpandable>
    {

        override fun fromDocument(doc: SchemaDoc)
                : ValueParser<BookSectionEntryInlineExpandable> = when (doc)
        {
            is DocDict -> {
                apply(::BookSectionEntryInlineExpandable,
                      // Content Id
                      doc.at("content_id").apply { BookContentId.fromDocument(it) }
                )
            }
            else -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }

    }

}

data class BookSectionEntryCard(
        val entryContent : BookContentId) : BookSectionEntry()
{

    companion object : Factory<BookSectionEntryCard>
    {

        override fun fromDocument(doc: SchemaDoc)
                : ValueParser<BookSectionEntryCard> = when (doc)
        {
            is DocDict -> {
                apply(::BookSectionEntryCard,
                      // Entry Content
                      doc.at("entry_content").apply { BookContentId.fromDocument(it) }
                )
            }
            else -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }

    }

}


data class BookSectionEntryCardGroup(
        val title : String,
        val cardEntries : List<BookContentId>) : BookSectionEntry()
{

    companion object : Factory<BookSectionEntryCardGroup>
    {

        override fun fromDocument(doc: SchemaDoc)
                : ValueParser<BookSectionEntryCardGroup> = when (doc)
        {
            is DocDict -> {
                apply(::BookSectionEntryCardGroup,
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


data class BookSectionEntryGroup(
        val title : String,
        val entries : List<BookSectionEntry>,
        val isExpandable : Boolean = false
) : BookSectionEntry()
{

    companion object : Factory<BookSectionEntryGroup>
    {

        override fun fromDocument(doc: SchemaDoc)
                : ValueParser<BookSectionEntryGroup> = when (doc)
        {
            is DocDict -> {
                apply(::BookSectionEntryGroup,
                      // Group Title
                      doc.text("title"),
                      // Entries
                      split(doc.maybeList("entries"),
                            effValue(listOf()),
                            { it.map { BookSectionEntry.fromDocument(it) } }),
                      split(doc.maybeBoolean("is_expandable"),
                            effValue(false),
                            { effValue(it) })
                )
            }
            else -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }

    }

}
