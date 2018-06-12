
package com.kispoko.tome.model.feed


import com.kispoko.tome.model.engine.variable.Variable
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.group.GroupId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.*
import effect.apply
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
import java.util.*



/**
 * Feed
 */
data class Feed(override val id : UUID,
                private val settings : FeedSettings,
                private val cards : List<Card>,
                private val variables : List<Variable>,
                private val groups : List<Group>)
                 : Entity, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INDEXES
    // -----------------------------------------------------------------------------------------

    private val groupById : MutableMap<GroupId,Group> =
                               groups.associateBy { it.id }
                                    as MutableMap<GroupId,Group>


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
                      // Id
                      effValue(UUID.randomUUID()),
                      // Settings
                      split(doc.maybeAt("settings"),
                            effValue(FeedSettings.default()),
                            { FeedSettings.fromDocument(it) }),
                      // Cards
                      doc.list("cards") ap { docList ->
                          docList.map { Card.fromDocument(it) }
                      },
                      // Variables
                      split(doc.maybeList("variables"),
                            effValue(listOf()),
                            { it.map { Variable.fromDocument(it) } }),
                      // Groups
                      split(doc.maybeList("groups"),
                            effValue(mutableListOf()),
                            { it.mapIndexed { doc, index -> Group.fromDocument(doc, index) } })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun empty() : Feed = Feed(UUID.randomUUID(),
                                  FeedSettings(ThemeId.Light),
                                  listOf(),
                                  listOf(),
                                  listOf())

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

    fun settings() : FeedSettings = this.settings


    fun cards() : List<Card> = this.cards


    fun variables() : List<Variable> = this.variables


    fun groups() : List<Group> = this.groups


    // -----------------------------------------------------------------------------------------
    // ENTITY
    // -----------------------------------------------------------------------------------------

    override fun name() = ""


    override fun summary() = ""


    override fun entityLoader() = EntityLoaderUnknown()


    override fun entityId() = EntityFeedId(this.id)


    // -----------------------------------------------------------------------------------------
    // GROUPS
    // -----------------------------------------------------------------------------------------

    fun groupWithId(groupId : GroupId) : Maybe<Group>
    {
        val _group = this.groupById[groupId]
        return if (_group != null)
            Just(_group)
        else
            Nothing()
    }

}


/**
 * Feed Settings
 */
data class FeedSettings(private val themeId : ThemeId)
                         : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------


    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<FeedSettings> = when (doc)
        {
            is DocDict ->
            {
                apply(::FeedSettings,
                      // Theme Id
                      split(doc.maybeAt("theme_id"),
                            effValue<ValueError,ThemeId>(ThemeId.Light),
                            { ThemeId.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = FeedSettings(ThemeId.Light)
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "theme_id" to this.themeId.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GET
    // -----------------------------------------------------------------------------------------

    fun themeId() : ThemeId = this.themeId

}


