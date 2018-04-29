
package com.kispoko.tome.model.game


import com.kispoko.culebra.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.sql.SQLBlob
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.book.*
import com.kispoko.tome.model.game.engine.Engine
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.group.GroupId
import com.kispoko.tome.rts.entity.*
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*



/**
 * Game
 */
data class Game(override val id : UUID,
                val gameId : GameId,
                val gameInfo : GameInfo,
                val engine : Engine,
                val variables : MutableList<Variable>,
                val groups : MutableList<Group>,
                var entityLoader : EntityLoader)
                 : ToDocument, Entity, Serializable
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

    companion object : Factory<Game>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Game> = when (doc)
        {
            is DocDict ->
            {
                apply(::Game,
                      effValue(UUID.randomUUID()),
                      // Game Id
                      doc.at("id") apply { GameId.fromDocument(it) },
                      // Game Info
                      doc.at("game_info") apply { GameInfo.fromDocument(it) },
                      // Engine
                      doc.at("engine") apply { Engine.fromDocument(it) },
                      // Variables
                      split(doc.maybeList("variables"),
                            effValue(mutableListOf()),
                            { it.mapMut { Variable.fromDocument(it) } }),
                      // Groups
                      split(doc.maybeList("groups"),
                            effValue(mutableListOf()),
                            { it.mapIndexed { d, i -> Group.fromDocument(d,i) }} ),
                      // Entity Loader
                      effValue(EntityLoaderUnknown())
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.gameId.toDocument(),
        "game_info" to this.gameInfo.toDocument(),
        "engine" to this.engine.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun gameId() : GameId = this.gameId


    fun gameInfo() : GameInfo = this.gameInfo


    fun groups() : List<Group> = this.groups


    fun engine() : Engine = this.engine


    fun variables() : List<Variable> = this.variables


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


    fun addGroups(groups : List<Group>)
    {
        this.groups.addAll(groups)

        groups.forEach {
            this.groupById.put(it.id, it)
        }
    }


    // -----------------------------------------------------------------------------------------
    // ENTITY
    // -----------------------------------------------------------------------------------------

    override fun name() = this.gameInfo.name().value


    override fun summary() = this.gameInfo.summary().value


    override fun entityLoader() = this.entityLoader


    override fun entityId() = EntityGameId(this.gameId)


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

//    fun rulebookWithId(rulebookId : BookId) : Book? = this.rulebookById[rulebookId]

}


/**
 * Game Id
 */
data class GameId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<GameId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<GameId> = when (doc)
        {
            is DocText -> effValue(GameId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }

        fun fromYaml(yamlValue : YamlValue) : YamlParser<GameId> =
            when (yamlValue)
            {
                is YamlText -> effValue(GameId(yamlValue.text))
                else        -> error(UnexpectedTypeFound(YamlType.TEXT,
                                                         yamlType(yamlValue),
                                                         yamlValue.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Game Book Ids
 */
data class GameBookIds(val bookIds : List<BookId>) : SQLSerializable, Serializable
{
    // TODO find way to not need this auxilary data type

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})

}


/**
 * Game Info
 */
data class GameInfo(val name : GameName,
                    val summary : GameSummary,
                    val authors : MutableList<Author>,
                    val bookIds : List<BookId>)
                     : ToDocument, Serializable
{

    companion object : Factory<GameInfo>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<GameInfo> = when (doc)
        {
            is DocDict ->
            {
                apply(::GameInfo,
                      // Name
                      doc.at("name") apply { GameName.fromDocument(it) },
                      // Summary
                      doc.at("summary") apply { GameSummary.fromDocument(it) },
                      // Authors
                      doc.list("authors") apply { it.mapMut { Author.fromDocument(it) } },
                      // Book Ids
                      split(doc.maybeList("book_ids"),
                            effValue(mutableListOf()),
                            { it.map { BookId.fromDocument(it) } })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "name" to this.name.toDocument(),
        "summary" to this.summary.toDocument(),
        "authors" to DocList(this.authors.map { it.toDocument() }),
        "book_ids" to DocList(this.bookIds.map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun name() : GameName = this.name


    fun summary() : GameSummary = this.summary


    fun authors() : List<Author> = this.authors


    fun bookIds() : List<BookId> = this.bookIds

}



//
///**
// * Game Description
// */
//data class GameDescription(override val id : UUID,
//                            : ToDocument, ProdType, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    constructor(gameName : GameName,
//                summary : GameSummary,
//                authors : List<Author>)
//        : this(UUID.randomUUID(),
//               gameName,
//               summary,
//               authors.toMutableList())
//
//
//    companion object : Factory<GameDescription>
//    {
//        override fun fromDocument(doc : SchemaDoc) : ValueParser<GameDescription> = when (doc)
//        {
//            is DocDict ->
//            {
//                apply(::GameDescription,
//                      // Game Name
//                      doc.at("game_name") ap { GameName.fromDocument(it) },
//                      // Summary
//                      doc.at("summary") ap { GameSummary.fromDocument(it) },
//                      // Authors
//                      doc.list("authors") ap { it.mapMut { Author.fromDocument(it) } })
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
//        "game_name" to this.gameName().toDocument(),
//        "summary" to this.summary().toDocument(),
//        "authors" to DocList(this.authors().map { it.toDocument() })
//    ))
//
//
//    // -----------------------------------------------------------------------------------------
//    // GETTERS
//    // -----------------------------------------------------------------------------------------
//
//    fun gameName() : GameName = this.gameName
//
//
//    fun gameNameString() : String = this.gameName.value
//
//
//    fun summary() : GameSummary = this.summary
//
//
//    fun summaryString() : String = this.summary.value
//
//
//    fun authors() : List<Author> = this.authors
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
//    override fun row() : DB_GameDescription =
//            dbGameDescription(this.gameName, this.summary, this.authors)
//
//}


/**
 * Game Name
 */
data class GameName(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<GameName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<GameName> = when (doc)
        {
            is DocText -> effValue(GameName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText( {this.value} )

}


/**
 * Game Summary
 */
data class GameSummary(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<GameSummary>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<GameSummary> = when (doc)
        {
            is DocText -> effValue(GameSummary(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}
