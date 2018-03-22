
package com.kispoko.tome.rts.entity


import android.content.Context
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.app.assetInputStream
import com.kispoko.tome.load.*
import com.kispoko.tome.model.book.BookId
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.sheet.SheetId
import effect.Err
import effect.Val
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



// ---------------------------------------------------------------------------------------------
// LOAD
// ---------------------------------------------------------------------------------------------

suspend fun loadEntity(entityLoader : EntityLoader,
                       context : Context) : Maybe<EntityLoadResult> = when (entityLoader)
{
    is EntityLoaderOfficial ->
    {
        when (entityLoader)
        {
            is OfficialSheetLoader    -> loadOfficialSheet(entityLoader, context)
            is OfficialCampaignLoader -> loadOfficialCampaign(entityLoader, context)
            is OfficialGameLoader     -> loadOfficialGame(entityLoader, context)
            is OfficialBookLoader     -> loadOfficialBook(entityLoader, context)
        }
    }
}


// LOAD > Official
// ---------------------------------------------------------------------------------------------

// LOAD > Official > Sheet
// ---------------------------------------------------------------------------------------------

fun loadOfficialSheet(officialSheetLoader : OfficialSheetLoader,
                      context : Context)
                       : Maybe<EntityLoadResult>
{
    when (sheet(officialSheetLoader.sheetId)) {
        is Just -> return Just(EntityLoadResult(EntitySheetId(officialSheetLoader.sheetId), true))
    }

    val sheetLoader = assetInputStream(context, officialSheetLoader.filePath())
                            .apply { TomeDoc.loadSheet(it, officialSheetLoader.sheetId.value, context) }
    return when (sheetLoader)
    {
        is Val ->
        {
            val sheet = sheetLoader.value

            addSheet(sheet)

            // Log event
            ApplicationLog.event(OfficialSheetLoaded(sheet.sheetId().value))

            Just(EntityLoadResult(EntitySheetId(sheet.sheetId()), false))
        }
        is Err -> {
            ApplicationLog.error(sheetLoader.error)
            Nothing()
        }
    }
}


// LOAD > Official > Campaign
// ---------------------------------------------------------------------------------------------

fun loadOfficialCampaign(officialCampaignLoader : OfficialCampaignLoader,
                         context : Context)
                          : Maybe<EntityLoadResult>
{
    when (campaign(officialCampaignLoader.campaignId)) {
        is Just -> return Just(EntityLoadResult(EntityCampaignId(officialCampaignLoader.campaignId), true))
    }

    val campaignLoader = assetInputStream(context, officialCampaignLoader.filePath())
                            .apply { TomeDoc.loadCampaign(it, officialCampaignLoader.campaignId.value, context) }
    return when (campaignLoader)
    {
        is Val ->
        {
            val campaign = campaignLoader.value

            addCampaign(campaign)

            // Log event
            ApplicationLog.event(OfficialCampaignLoaded(campaign.campaignId().value))

            Just(EntityLoadResult(EntityCampaignId(campaign.campaignId), false))
        }
        is Err -> {
            ApplicationLog.error(campaignLoader.error)
            Nothing()
        }
    }
}


// LOAD > Official > Game
// --------------------------------------------------------------------------------------------

fun loadOfficialGame(officialGameLoader : OfficialGameLoader,
                     context : Context)
                      : Maybe<EntityLoadResult>
{
    when (game(officialGameLoader.gameId)) {
        is Just -> return Just(EntityLoadResult(EntityGameId(officialGameLoader.gameId), true))
    }

    val gameLoader = assetInputStream(context, officialGameLoader.filePath())
                       .apply { TomeDoc.loadGame(it, officialGameLoader.gameId.value, context) }
    return when (gameLoader)
    {
        is Val ->
        {
            val game = gameLoader.value

            addGame(game)

            // Log event
            ApplicationLog.event(OfficialGameLoaded(game.gameId().value))

            Just(EntityLoadResult(EntityGameId(game.gameId), false))
        }
        is Err -> {
            ApplicationLog.error(gameLoader.error)
            Nothing()
        }
    }
}


// LOAD > Official > Book
// --------------------------------------------------------------------------------------------

fun loadOfficialBook(officialBookLoader : OfficialBookLoader,
                     context : Context)
                      : Maybe<EntityLoadResult>
{
    when (book(officialBookLoader.bookId)) {
        is Just -> return Just(EntityLoadResult(EntityBookId(officialBookLoader.bookId), true))
    }

    val bookLoader = assetInputStream(context, officialBookLoader.filePath())
                       .apply { TomeDoc.loadBook(it, officialBookLoader.bookId.value, context) }
    return when (bookLoader)
    {
        is Val ->
        {
            val book = bookLoader.value

            addBook(book)

            // Log event
            ApplicationLog.event(OfficialBookLoaded(book.bookId().value))

            Just(EntityLoadResult(EntityBookId(book.bookId()), false))
        }
        is Err -> {
            ApplicationLog.error(bookLoader.error)
            Nothing()
        }
    }
}


// ---------------------------------------------------------------------------------------------
// DEFINITIONS
// ---------------------------------------------------------------------------------------------

data class EntityLoadResult(val entityId : EntityId, val fromCache : Boolean)


// ---------------------------------------------------------------------------------------------
// ENTITY LOADER
// ---------------------------------------------------------------------------------------------

sealed class EntityLoader(open val label : String) : Serializable


// ENTITY LOADER > OFFICIAL
// ---------------------------------------------------------------------------------------------

sealed class EntityLoaderOfficial(override val label : String) : EntityLoader(label)
{
    abstract fun filePath() : String
}


data class OfficialSheetLoader(override val label : String,
                               val sheetId : SheetId,
                               val campaignId : CampaignId,
                               val gameId : GameId) : EntityLoaderOfficial(label)
{

    override fun filePath() : String =
            "official/" + gameId.value +
            "/sheets/" + sheetId.value + ".yaml"

}


data class OfficialCampaignLoader(override val label : String,
                                  val campaignId : CampaignId,
                                  val gameId : GameId) : EntityLoaderOfficial(label)
{

    override fun filePath() : String =
            "official/" + gameId.value +
            "/campaigns/" + campaignId.value + ".yaml"

}


data class OfficialGameLoader(override val label : String,
                              val gameId : GameId) : EntityLoaderOfficial(label)
{

    override fun filePath() : String =
        "official/" + gameId.value +
        "/" + gameId.value +  ".yaml"

}


data class OfficialBookLoader(override val label : String,
                              val bookId : BookId,
                              val gameId : GameId)
                               : EntityLoaderOfficial(label)
{

    override fun filePath() : String =
        "official/" + gameId.value +
        "/books/" + bookId.value +  ".yaml"

}
