
package com.kispoko.tome.model.game


import com.kispoko.tome.db.DB_GameValue
import com.kispoko.tome.db.gameTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue6
import com.kispoko.tome.lib.orm.schema.CollValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.book.Book
import com.kispoko.tome.model.book.BookId
import com.kispoko.tome.model.game.engine.Engine
import effect.effApply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Game
 */
data class Game(override val id : UUID,
                val gameId : GameId,
                val gameName : GameName,
                val gameSummary : GameSummary,
                val authors : MutableList<Author>,
                val engine : Engine,
                val rulebooks : List<Book>)
                 : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private val rulebookById : MutableMap<BookId, Book> =
                        rulebooks.associateBy { it.rulebookId() }
                                as MutableMap<BookId, Book>


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(gameId : GameId,
                gameName : GameName,
                gameSummary : GameSummary,
                authors : List<Author>,
                engine : Engine,
                rulebooks : List<Book>)
        : this(UUID.randomUUID(),
               gameId,
               gameName,
               gameSummary,
               authors.toMutableList(),
               engine,
               rulebooks)


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
                             // Name
                             doc.at("game_name") apply { GameName.fromDocument(it) },
                             // Summary
                             doc.at("summary") apply { GameSummary.fromDocument(it) },
                             // Authors
                             doc.list("authors") ap { it.map { Author.fromDocument(it) } },
                             // Engine
                             doc.at("engine") apply { Engine.fromDocument(it) },
                             // Rulebook
                             split(doc.maybeList("rulebooks"),
                                   effValue(listOf()),
                                   { it.map { Book.fromDocument(it) } } )
                             )
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.gameId().toDocument(),
        "game_name" to this.gameName.toDocument(),
        "game_summary" to this.gameSummary.toDocument(),
        "authors" to DocList(this.authors.map { it.toDocument() }),
        "engine" to this.engine().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun gameId() : GameId = this.gameId


    fun gameName() : GameName = this.gameName


    fun gameSummary() : GameSummary = this.gameSummary


    fun authors() : List<Author> = this.authors


    fun engine() : Engine = this.engine


    fun rulebooks() : List<Book> = this.rulebooks


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_GameValue =
        RowValue6(gameTable, PrimValue(this.gameId),
                             PrimValue(this.gameName),
                             PrimValue(this.gameSummary),
                             CollValue(this.authors),
                             ProdValue(this.engine),
                             CollValue(this.rulebooks))


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun rulebookWithId(rulebookId : BookId) : Book? = this.rulebookById[rulebookId]

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
