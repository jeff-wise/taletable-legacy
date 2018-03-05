
package com.kispoko.tome.model.book


import com.kispoko.tome.db.DB_BookReferenceValue
import com.kispoko.tome.db.bookReferenceTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue4
import com.kispoko.tome.lib.orm.schema.MaybePrimValue
import com.kispoko.tome.lib.orm.schema.PrimValue
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
 * Book Reference
 */
data class BookReference(override val id : UUID,
                         val bookId : BookId,
                         val chapterId : BookChapterId,
                         val sectionId : Maybe<BookSectionId>,
                         val subsectionId : Maybe<BookSubsectionId>)
                              : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(bookId : BookId,
                chapterId : BookChapterId,
                sectionId : Maybe<BookSectionId>,
                subsectionId : Maybe<BookSubsectionId>)
        : this(UUID.randomUUID(),
               bookId,
               chapterId,
               sectionId,
               subsectionId)


    companion object : Factory<BookReference>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookReference> = when (doc)
        {
            is DocDict ->
            {
                apply(::BookReference,
                      // Rulebook Id
                      doc.at("rulebook_id") apply { BookId.fromDocument(it) },
                      // Chapter Id
                      doc.at("chapter_id") apply { BookChapterId.fromDocument(it) },
                      // Section Id
                      split(doc.maybeAt("section_id"),
                            effValue<ValueError,Maybe<BookSectionId>>(Nothing()),
                            { effApply(::Just, BookSectionId.fromDocument(it)) }),
                      // Subsection Id
                      split(doc.maybeAt("subsection_id"),
                            effValue<ValueError,Maybe<BookSubsectionId>>(Nothing()),
                            { effApply(::Just, BookSubsectionId.fromDocument(it)) })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "rulebook_id" to this.rulebookId().toDocument(),
        "chapter_id" to this.chapterId().toDocument()
    ))
    .maybeMerge(this.sectionId.apply {
        Just(Pair("section_id", it.toDocument() as SchemaDoc)) })
    .maybeMerge(this.subsectionId.apply {
        Just(Pair("subsection_id", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun rulebookId() : BookId = this.bookId


    fun chapterId() : BookChapterId = this.chapterId


    fun sectionId() : Maybe<BookSectionId> = this.sectionId


    fun subsectionId() : Maybe<BookSubsectionId> = this.subsectionId


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_BookReferenceValue =
        RowValue4(bookReferenceTable,
                  PrimValue(this.bookId),
                  PrimValue(this.chapterId),
                  MaybePrimValue(this.sectionId),
                  MaybePrimValue(this.subsectionId))

}


/**
 * Rulebook Reference Path
 */
data class RulebookReferencePath(val bookTitle : BookTitle,
                                 val chapterTitle : BookChapterTitle,
                                 val sectionTitle : Maybe<BookSectionTitle>,
                                 val subsectionTitle : Maybe<BookSubsectionTitle>)
                                  : Serializable
{

    override fun toString() : String
    {
        var s = ""

        s += bookTitle.value
        s += " \u203A "
        s += chapterTitle.value

        when (this.sectionTitle) {
            is Just -> {
                s += " \u203A "
                s += this.sectionTitle.value.value
            }
        }

        when (this.subsectionTitle) {
            is Just -> {
                s += " \u203A "
                s += this.subsectionTitle.value.value
            }
        }

        return s
    }

}

data class RulebookExcerpt(val title : String, val body : String) : Serializable
