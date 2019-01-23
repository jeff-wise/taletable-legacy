
package com.taletable.android.model.entity


import com.taletable.android.lib.Factory
import com.taletable.android.model.book.*
import effect.effError
import lulo.document.DocDict
import lulo.document.SchemaDoc
import lulo.document.ToDocument
import lulo.value.UnknownCase
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



sealed class ContentReference() : ToDocument, Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ContentReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ContentReference> =
            when (doc.case())
            {
                "book_reference" -> ContentReferenceBook.fromDocument(doc.nextCase()) as ValueParser<ContentReference>
                else             -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // | Variants
    // -----------------------------------------------------------------------------------------

    fun bookReference() : Maybe<BookReference> = when (this)
    {
        is ContentReferenceBook -> Just(this.bookReference)
        else                    -> Nothing()
    }

}




/**
 * | Content Reference :> Book
 */
data class ContentReferenceBook(val bookReference : BookReference) : ContentReference()
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ContentReferenceBook>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ContentReferenceBook> =
                effect.apply(::ContentReferenceBook, BookReference.fromDocument(doc))

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf())

}

