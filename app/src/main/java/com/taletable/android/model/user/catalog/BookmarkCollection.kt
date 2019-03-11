
package com.taletable.android.model.user.catalog


import com.taletable.android.lib.Factory
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable



data class BookmarkCollection(val name : BookmarkCollectionName,
                              val bookmarks : List<Bookmark>)
{


}


/**
 * Bookmark Collection Name
 */
data class BookmarkCollectionName(val value : String) : ToDocument, Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BookmarkCollectionName>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BookmarkCollectionName> = when (doc)
        {
            is DocText -> effValue(BookmarkCollectionName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // | To Document
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}

