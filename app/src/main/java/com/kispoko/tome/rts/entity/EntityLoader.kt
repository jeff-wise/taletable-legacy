
package com.kispoko.tome.rts.entity


import android.content.Context
import com.kispoko.culebra.*
import com.kispoko.tome.R.string.group
import com.kispoko.tome.R.string.label
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.app.assetInputStream
import com.kispoko.tome.load.*
import com.kispoko.tome.model.book.BookId
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.Game
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.rts.entity.OfficialSheetLoader.Companion.fromYaml
import effect.Err
import effect.Val
import effect.apply
import effect.effError
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
    else -> {
        Nothing()
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

            loadOfficialGameData(officialGameLoader, game, context)

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


private fun loadOfficialGameData(officialGameLoader : OfficialGameLoader,
                                 game : Game,
                                 context : Context)
{
    val groupIndexLoader = assetInputStream(context, officialGameLoader.weaponGroupsFilePath())
            .apply { TomeDoc.loadGroupIndex(it, officialGameLoader.gameId.value, context) }

    when (groupIndexLoader)
    {
        is Val -> {
            game.addGroups(groupIndexLoader.value.groups)
        }
        is Err -> {
            ApplicationLog.error(groupIndexLoader.error)
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

sealed class EntityLoader() : Serializable
{
    companion object
    {
        fun fromYaml(yamlValue: YamlValue) : YamlParser<EntityLoader> = when (yamlValue)
        {
            is YamlDict ->
            {
                yamlValue.text("type") apply {
                    when (it) {
                        "official" -> yamlValue.at("official").apply(EntityLoaderOfficial.Companion::fromYaml) as YamlParser<EntityLoader>
                        else       -> effError<YamlParseError,EntityLoader>(
                                        UnexpectedStringValue(it, yamlValue.path))
                    }
                }
            }
            else        -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
        }
    }

}


// ENTITY LOADER > UNKNOWN
// ---------------------------------------------------------------------------------------------

class EntityLoaderUnknown() : EntityLoader()


// ENTITY LOADER > SAVED
// ---------------------------------------------------------------------------------------------

data class EntityLoaderSaved(val rowId : Long) : EntityLoader()


// ENTITY LOADER > OFFICIAL
// ---------------------------------------------------------------------------------------------

sealed class EntityLoaderOfficial : EntityLoader()
{
    abstract fun filePath() : String

    companion object
    {
        fun fromYaml(yamlValue: YamlValue) : YamlParser<EntityLoaderOfficial> = when (yamlValue)
        {
            is YamlDict ->
            {
                yamlValue.text("type") apply {
                    when (it) {
                        "sheet"    -> yamlValue.at("sheet").apply(OfficialSheetLoader.Companion::fromYaml)
                        "campaign" -> yamlValue.at("campaign").apply(OfficialCampaignLoader.Companion::fromYaml)
                        "game"     -> yamlValue.at("game").apply(OfficialGameLoader.Companion::fromYaml)
                        "book"     -> yamlValue.at("book").apply(OfficialBookLoader.Companion::fromYaml)
                        else       -> effError<YamlParseError,EntityLoaderOfficial>(
                                          UnexpectedStringValue(it, yamlValue.path))
                    }
                }
            }
            else        -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
        }

    }
}


data class OfficialSheetLoader(val sheetId : SheetId,
                               val gameId : GameId) : EntityLoaderOfficial()
{

    override fun filePath() : String =
            "official/" + gameId.value +
            "/sheets/" + sheetId.value + ".yaml"


    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityLoaderOfficial> = when (yamlValue)
        {
            is YamlDict ->
            {
                apply(::OfficialSheetLoader,
                      // Sheet Id
                      yamlValue.at("sheet_id") ap { SheetId.fromYaml(it) },
                      // Game Id
                      yamlValue.at("game_id") ap { GameId.fromYaml(it) })
            }
            else        -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
        }

    }

}


data class OfficialCampaignLoader(val campaignId : CampaignId,
                                  val gameId : GameId) : EntityLoaderOfficial()
{

    override fun filePath() : String =
            "official/" + gameId.value +
            "/campaigns/" + campaignId.value + ".yaml"


    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityLoaderOfficial> = when (yamlValue)
        {
            is YamlDict ->
            {
                apply(::OfficialCampaignLoader,
                      // Campaign Id
                      yamlValue.at("campaign_id") ap { CampaignId.fromYaml(it) },
                      // Game Id
                      yamlValue.at("game_id") ap { GameId.fromYaml(it) })
            }
            else        -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
        }

    }


}


data class OfficialGameLoader(val gameId : GameId) : EntityLoaderOfficial()
{

    override fun filePath() : String =
        "official/" + gameId.value +
        "/" + gameId.value +  ".yaml"


    fun weaponGroupsFilePath() : String =
            "official/" + gameId.value + "/indexes/group/weapons.yaml"


    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityLoaderOfficial> = when (yamlValue)
        {
            is YamlDict ->
            {
                apply(::OfficialGameLoader,
                      // Game Id
                      yamlValue.at("game_id") ap { GameId.fromYaml(it) })
            }
            else        -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
        }

    }

}


data class OfficialBookLoader(val bookId : BookId,
                              val gameId : GameId)
                               : EntityLoaderOfficial()
{

    override fun filePath() : String =
        "official/" + gameId.value +
        "/books/" + bookId.value +  ".yaml"


    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityLoaderOfficial> = when (yamlValue)
        {
            is YamlDict ->
            {
                apply(::OfficialBookLoader,
                      // Book Id
                      yamlValue.at("book_id") ap { BookId.fromYaml(it) },
                      // Game Id
                      yamlValue.at("game_id") ap { GameId.fromYaml(it) })
            }
            else        -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
        }

    }
}
