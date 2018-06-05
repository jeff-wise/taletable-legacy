
package com.kispoko.tome.model.feed


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.group.GroupName
import com.kispoko.tome.model.sheet.group.GroupReference
import effect.apply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Feed
 */
data class Feed(private val cards : List<Card>) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------


    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<Feed> = when (doc)
        {
            is DocDict ->
            {
                apply(::Feed,
                      // Cards
                      doc.list("cards") ap { docList ->
                          docList.map { Card.fromDocument(it) }
                      }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "cards" to DocList(this.cards.map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GET
    // -----------------------------------------------------------------------------------------

    fun cards() : List<Card> = this.cards

}


/**
 * Card
 */
data class Card(private val title : CardTitle,
                private val isPinned : CardIsPinned,
                private val groupReferences : List<GroupReference>)
                 : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------


    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<Card> = when (doc)
        {
            is DocDict ->
            {
                apply(::Card,
                      // Title
                      doc.at("title").apply { CardTitle.fromDocument(it) },
                      // Is Pinned?
                      doc.at("is_pinned").apply { CardIsPinned.fromDocument(it) },
                      // Group References
                      doc.list("group_references") ap { docList ->
                          docList.map { GroupReference.fromDocument(it) }
                      }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "title" to this.title.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GET
    // -----------------------------------------------------------------------------------------

    fun groupReferences() : List<GroupReference> = this.groupReferences

}


/**
 * Card Title
 */
data class CardTitle(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<CardTitle>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<CardTitle> = when (doc)
        {
            is DocText -> effValue(CardTitle(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}


/**
 * Card Is Pinned
 */
data class CardIsPinned(val value : Boolean) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<CardIsPinned>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<CardIsPinned> = when (doc)
        {
            is DocBoolean -> effValue(CardIsPinned(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocBoolean(this.value)

}



