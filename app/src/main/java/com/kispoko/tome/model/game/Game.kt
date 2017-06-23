
package com.kispoko.tome.model.game


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Coll
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
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
                val engine : Comp<Engine>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.gameId.name        = "game_id"
        this.description.name   = "description"
        this.engine.name        = "engine"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(gameId : GameId,
                description : GameDescription,
                engine : Engine)
        : this(UUID.randomUUID(),
               Prim(gameId),
               Comp(description),
               Comp(engine))


    companion object : Factory<Game>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Game> = when (doc)
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
                             doc.at("engine") apply { Engine.fromDocument(it, gameId) })
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


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "game"

    override val modelObject = this

}


/**
 * Game Name
 */
data class GameId(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<GameId>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<GameId> = when (doc)
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
                           val summary : Prim<GameSummary>,
                           val authors : Coll<Author>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.summary.name       = "summary"
        this.authors.name       = "authors"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<GameDescription>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<GameDescription> = when (doc)
        {
            is DocDict -> effApply(::GameDescription,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Summary
                                   doc.at("summary") apply {
                                       effApply(::Prim, GameSummary.fromDocument(it))
                                   },
                                   // Authors
                                   doc.list("authors") apply {
                                       effApply(::Coll, it.mapMut { Author.fromDocument(it) })
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "game_description"

    override val modelObject = this

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
        override fun fromDocument(doc: SpecDoc): ValueParser<GameSummary> = when (doc)
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
