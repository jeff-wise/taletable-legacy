
package com.kispoko.tome.model.feed


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.model.AppAction
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.group.GroupReference
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.groups
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
import java.io.Serializable



/**
 * Card
 */
data class Card(private val title : CardTitle,
                private val isPinned : CardIsPinned,
                private val appAction : Maybe<AppAction>,
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
                effect.apply(::Card,
                      // Title
                      doc.at("title").apply { CardTitle.fromDocument(it) },
                      // Is Pinned?
                      doc.at("is_pinned").apply { CardIsPinned.fromDocument(it) },
                      // App Action
                      split(doc.maybeAt("app_action"),
                            effValue<ValueError,Maybe<AppAction>>(Nothing()),
                            { effect.apply(::Just, AppAction.fromDocument(it)) }  ),
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

    fun title() : CardTitle = this.title


    fun isPinned() : CardIsPinned = this.isPinned


    fun groupReferences() : List<GroupReference> = this.groupReferences


    fun appAction() : Maybe<AppAction> = this.appAction


    // -----------------------------------------------------------------------------------------
    // GROUPS
    // -----------------------------------------------------------------------------------------

    fun groups(entityId : EntityId) : List<Group> = groups(this.groupReferences, entityId)

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
data class CardIsPinned(val value : Boolean) : ToDocument, java.io.Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<CardIsPinned>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<CardIsPinned> = when (doc)
        {
            is DocBoolean -> effValue(CardIsPinned(doc.boolean))
            else          -> effError(lulo.value.UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocBoolean(this.value)

}


