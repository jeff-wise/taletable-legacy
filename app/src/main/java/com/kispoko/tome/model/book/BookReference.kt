
package com.kispoko.tome.model.book


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.rts.entity.EntityId
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueParser
import java.io.Serializable



sealed class BookReference(open val bookId : EntityId) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BookReference> =
            when (doc.case())
            {
                "book_reference_book"       -> BookReferenceBook.fromDocument(doc) as ValueParser<BookReference>
                "book_reference_chapter"    -> BookReferenceChapter.fromDocument(doc) as ValueParser<BookReference>
                "book_reference_section"    -> BookReferenceSection.fromDocument(doc) as ValueParser<BookReference>
                "book_reference_subsection" -> BookReferenceSubsection.fromDocument(doc) as ValueParser<BookReference>
                "book_reference_content"    -> BookReferenceContent.fromDocument(doc) as ValueParser<BookReference>
                else                 -> effError(UnknownCase(doc.case(), doc.path))
            }
    }

}


/**
 * Book Reference Content
 */
data class BookReferenceContent(override val bookId : EntityId,
                                val contentId : BookContentId) : BookReference(bookId)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookReferenceContent>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookReferenceContent> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookReferenceContent,
                      // Book Id
                      doc.at("book_id") apply { EntityId.fromDocument(it) },
                      // Book Content 1Id
                      doc.at("content_id") apply { BookContentId.fromDocument(it) }
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "book_id" to this.bookId.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun bookId() : EntityId = this.bookId


    fun contentId() : BookContentId = this.contentId

}


/**
 * Book Reference Main
 */
data class BookReferenceBook(override val bookId : EntityId) : BookReference(bookId)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookReferenceBook>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookReferenceBook> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookReferenceBook,
                      // Book Id
                      doc.at("book_id") apply { EntityId.fromDocument(it) }
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "book_id" to this.bookId.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun bookId() : EntityId = this.bookId

}



/**
 * Book Reference Chapter
 */
data class BookReferenceChapter(override val bookId : EntityId,
                                val chapterId : BookChapterId) : BookReference(bookId)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookReferenceChapter>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookReferenceChapter> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookReferenceChapter,
                      // Book Id
                      doc.at("book_id") apply { EntityId.fromDocument(it) },
                      // Book Chapter Id
                      doc.at("chapter_id") apply { BookChapterId.fromDocument(it) }
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "book_id" to this.bookId.toDocument(),
        "book_id" to this.chapterId.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun bookId() : EntityId = this.bookId


    fun chapterId() : BookChapterId = this.chapterId

}


/**
 * Book Reference Section
 */
data class BookReferenceSection(override val bookId : EntityId,
                                val chapterId : BookChapterId,
                                val sectionId : BookSectionId) : BookReference(bookId)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookReferenceSection>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookReferenceSection> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookReferenceSection,
                      // Book Id
                      doc.at("book_id") apply { EntityId.fromDocument(it) },
                      // Chapter Id
                      doc.at("chapter_id") apply { BookChapterId.fromDocument(it) },
                      // Section Id
                      doc.at("section_id") apply { BookSectionId.fromDocument(it) }
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "book_id" to this.bookId.toDocument(),
        "chapter_id" to this.chapterId.toDocument(),
        "section_id" to this.sectionId.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun bookId() : EntityId = this.bookId


    fun chapterId() : BookChapterId = this.chapterId


    fun sectionId() : BookSectionId = this.sectionId

}


/**
 * Book Reference Subsection
 */
data class BookReferenceSubsection(override val bookId : EntityId,
                                   val chapterId : BookChapterId,
                                   val sectionId : BookSectionId,
                                   val subsectionId : BookSubsectionId) : BookReference(bookId)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookReferenceSubsection>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookReferenceSubsection> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookReferenceSubsection,
                      // Book Id
                      doc.at("book_id") apply { EntityId.fromDocument(it) },
                      // Chapter Id
                      doc.at("chapter_id") apply { BookChapterId.fromDocument(it) },
                      // Section Id
                      doc.at("section_id") apply { BookSectionId.fromDocument(it) },
                      // Subsection Id
                      doc.at("subsection_id") apply { BookSubsectionId.fromDocument(it) }
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "book_id" to this.bookId.toDocument(),
        "chapter_id" to this.chapterId.toDocument(),
        "section_id" to this.sectionId.toDocument(),
        "subsection_id" to this.sectionId.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun bookId() : EntityId = this.bookId


    fun chapterId() : BookChapterId = this.chapterId


    fun sectionId() : BookSectionId = this.sectionId


    fun subsectionId() : BookSubsectionId = this.subsectionId

}




//
///**
// * Rulebook Reference Path
// */
//data class RulebookReferencePath(val bookTitle : BookTitle,
//                                 val chapterTitle : BookChapterTitle,
//                                 val sectionTitle : Maybe<BookSectionTitle>,
//                                 val subsectionTitle : Maybe<BookSubsectionTitle>)
//                                  : Serializable
//{
//
//    override fun toString() : String
//    {
//        var s = ""
//
//        s += bookTitle.value
//        s += " \u203A "
//        s += chapterTitle.value
//
//        when (this.sectionTitle) {
//            is Just -> {
//                s += " \u203A "
//                s += this.sectionTitle.value.value
//            }
//        }
//
//        when (this.subsectionTitle) {
//            is Just -> {
//                s += " \u203A "
//                s += this.subsectionTitle.value.value
//            }
//        }
//
//        return s
//    }
//
//}
//
//data class RulebookExcerpt(val title : String, val body : String) : Serializable
//

//
///**
// * Book Reference Cover
// */
//data class BookReferenceCover(val bookId : BookId,
//                              val chapterId : BookChapterId,
//                              val sectionId : Maybe<BookSectionId>,
//                              val subsectionId : Maybe<BookSubsectionId>)
//                                  : BookReference(), ToDocument, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    constructor(bookId : BookId,
//                chapterId : BookChapterId,
//                sectionId : Maybe<BookSectionId>,
//                subsectionId : Maybe<BookSubsectionId>)
//        : this(UUID.randomUUID(),
//               bookId,
//               chapterId,
//               sectionId,
//               subsectionId)
//
//
//    companion object : Factory<BookReference>
//    {
//        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookReference> = when (doc)
//        {
//            is DocDict ->
//            {
//                apply(::BookReference,
//                      // Rulebook Id
//                      doc.at("book_id") apply { BookId.fromDocument(it) },
//                      // Chapter Id
//                      doc.at("chapter_id") apply { BookChapterId.fromDocument(it) },
//                      // Section Id
//                      split(doc.maybeAt("section_id"),
//                            effValue<ValueError,Maybe<BookSectionId>>(Nothing()),
//                            { effApply(::Just, BookSectionId.fromDocument(it)) }),
//                      // Subsection Id
//                      split(doc.maybeAt("subsection_id"),
//                            effValue<ValueError,Maybe<BookSubsectionId>>(Nothing()),
//                            { effApply(::Just, BookSubsectionId.fromDocument(it)) })
//                     )
//            }
//            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
//        }
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // TO DOCUMENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun toDocument() = DocDict(mapOf(
//        "rulebook_id" to this.rulebookId().toDocument(),
//        "chapter_id" to this.chapterId().toDocument()
//    ))
//    .maybeMerge(this.sectionId.apply {
//        Just(Pair("section_id", it.toDocument() as SchemaDoc)) })
//    .maybeMerge(this.subsectionId.apply {
//        Just(Pair("subsection_id", it.toDocument() as SchemaDoc)) })
//
//
//    // -----------------------------------------------------------------------------------------
//    // GETTERS
//    // -----------------------------------------------------------------------------------------
//
//    fun rulebookId() : BookId = this.bookId
//
//
//    fun chapterId() : BookChapterId = this.chapterId
//
//
//    fun sectionId() : Maybe<BookSectionId> = this.sectionId
//
//
//    fun subsectionId() : Maybe<BookSubsectionId> = this.subsectionId
//
//
//    // -----------------------------------------------------------------------------------------
//    // MODEL
//    // -----------------------------------------------------------------------------------------
//
//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_BookReferenceValue =
//        RowValue4(bookReferenceTable,
//                  PrimValue(this.bookId),
//                  PrimValue(this.chapterId),
//                  MaybePrimValue(this.sectionId),
//                  MaybePrimValue(this.subsectionId))
//
//}
//

