
package com.taletable.android.model.feed


import com.taletable.android.lib.Factory
import com.taletable.android.model.AppAction
import com.taletable.android.model.sheet.group.Group
import com.taletable.android.model.sheet.group.GroupReference
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.groups
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
                private val reason : CardReason,
                private val appAction : Maybe<AppAction>,
                private val actionLabel : Maybe<CardActionLabel>,
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
                      // Reason
                      doc.at("reason").apply { CardReason.fromDocument(it) },
                      // App Action
                      split(doc.maybeAt("app_action"),
                            effValue<ValueError,Maybe<AppAction>>(Nothing()),
                            { effect.apply(::Just, AppAction.fromDocument(it)) }  ),
                      // Action Label
                      split(doc.maybeAt("action_label"),
                            effValue<ValueError,Maybe<CardActionLabel>>(Nothing()),
                            { effect.apply(::Just, CardActionLabel.fromDocument(it)) }  ),
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


    fun reason() : CardReason = this.reason


    fun groupReferences() : List<GroupReference> = this.groupReferences


    fun appAction() : Maybe<AppAction> = this.appAction


    fun actionLabel() : Maybe<CardActionLabel> = this.actionLabel


    // -----------------------------------------------------------------------------------------
    // GROUPS
    // -----------------------------------------------------------------------------------------

    fun groups(entityId : EntityId) : List<Group> = groups(this.groupReferences, entityId).map { it.group }

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
 * Card Reason
 */
data class CardReason(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<CardReason>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<CardReason> = when (doc)
        {
            is DocText -> effValue(CardReason(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}


/**
 * Card Button Label
 */
data class CardActionLabel(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<CardActionLabel>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<CardActionLabel> = when (doc)
        {
            is DocText -> effValue(CardActionLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}
