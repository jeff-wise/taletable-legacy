
package com.kispoko.tome.model.game


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Coll
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.Engine
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.util.*



/**
 * Game
 */
data class Game(override val id : UUID,
                val gameId : Prim<GameId>,
                val description : Comp<GameDescription>,
                val engine : Comp<Engine>) : Model
{

    companion object : Factory<Game>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Game> = when (doc)
        {
            is DocDict -> effApply(::Game,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Summary
                                   doc.at("id") apply {
                                       effApply(::Prim, GameId.fromDocument(it))
                                   },
                                   // Description
                                   doc.at("description") apply {
                                       effApply(::Comp, GameDescription.fromDocument(it))
                                   },
                                   // Engine
                                   doc.at("engine") apply {
                                       effApply(::Comp, Engine.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * Game Name
 */
data class GameId(val value : String)
{

    companion object : Factory<GameId>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<GameId> = when (doc)
        {
            is DocText -> effValue(GameId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Game Description
 */
data class GameDescription(override val id : UUID,
                           val summary : Func<GameSummary>,
                           val authors : Coll<Author>) : Model
{

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
                                       effApply(::Coll, it.map { Author.fromDocument(it) })
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * Game Summary
 */
data class GameSummary(val value : String)
{

    companion object : Factory<GameSummary>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<GameSummary> = when (doc)
        {
            is DocText -> effValue(GameSummary(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}
