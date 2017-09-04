
package com.kispoko.tome.model.game


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Coll
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.Engine
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Game
 */
data class Game(override val id : UUID,
                val gameId : Prim<GameId>,
                val description : Comp<GameDescription>,
                val engine : Comp<Engine>,
                val rulebook : Comp<Rulebook>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.gameId.name        = "game_id"
        this.description.name   = "description"
        this.engine.name        = "engine"
        this.rulebook.name      = "rulebook"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(gameId : GameId,
                description : GameDescription,
                engine : Engine,
                rulebook : Rulebook)
        : this(UUID.randomUUID(),
               Prim(gameId),
               Comp(description),
               Comp(engine),
               Comp(rulebook))


    companion object : Factory<Game>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Game> = when (doc)
        {
            is DocDict ->
            {
                val gameIdParser = doc.at("id") ap { GameId.fromDocument(it) }
                gameIdParser ap { gameId ->
                    effApply(::Game,
                             // Game Id
                             effValue(gameId),
                             // Description
                             doc.at("description") apply { GameDescription.fromDocument(it) },
                             // Engine
                             doc.at("engine") apply { Engine.fromDocument(it, gameId) },
                             // Rulebook
                             doc.at("rulebook") apply { Rulebook.fromDocument(it) }
                             )
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun gameId() : GameId = this.gameId.value

    fun description() : GameDescription = this.description.value

    fun engine() : Engine = this.engine.value

    fun rulebook() : Rulebook = this.rulebook.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "game"

    override val modelObject = this

}


/**
 * Game Id
 */
data class GameId(val value : String) : SQLSerializable, Serializable
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
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Game Description
 */
data class GameDescription(override val id : UUID,
                           val gameName : Prim<GameName>,
                           val summary : Prim<GameSummary>,
                           val authors : Coll<Author>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.gameName.name      = "game_name"
        this.summary.name       = "summary"
        this.authors.name       = "authors"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(gameName : GameName,
                summary : GameSummary,
                authors : MutableList<Author>)
        : this(UUID.randomUUID(),
               Prim(gameName),
               Prim(summary),
               Coll(authors))


    companion object : Factory<GameDescription>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<GameDescription> = when (doc)
        {
            is DocDict ->
            {
                effApply(::GameDescription,
                         // Game Name
                         doc.at("game_name") ap { GameName.fromDocument(it) },
                         // Summary
                         doc.at("summary") ap { GameSummary.fromDocument(it) },
                         // Authors
                         doc.list("authors") ap { it.mapMut { Author.fromDocument(it) } })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun gameName() : String = this.gameName.value.value

    fun summary() : String = this.summary.value.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "game_description"

    override val modelObject = this

}


/**
 * Game Name
 */
data class GameName(val value : String) : SQLSerializable, Serializable
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
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText( {this.value} )

}


/**
 * Game Summary
 */
data class GameSummary(val value : String) : SQLSerializable, Serializable
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
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}
