
package com.taletable.android.rts.entity


import android.content.Context
import android.util.Log
import com.kispoko.culebra.*
import com.taletable.android.app.ApplicationLog
import com.taletable.android.db.readEntity
import com.taletable.android.load.*
import com.taletable.android.model.engine.variable.Variable
import com.taletable.android.model.entity.PersistedEntity
import com.taletable.android.model.entity.entityManifest
import com.taletable.android.model.sheet.group.GroupIndex
import effect.Err
import effect.Val
import effect.apply
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



fun loadEntity(entityId : EntityId, context : Context) : Maybe<EntityLoadResult>
{
    var loadResult : Maybe<EntityLoadResult>

    loadResult = loadEntityFromDatabase(entityId, context)

    when (loadResult) {
        is Nothing -> {
            loadResult = loadEntityFromOfficial(entityId, context)
        }
    }

    return loadResult
}


fun loadEntityFromDatabase(entityId : EntityId, context : Context) : Maybe<EntityLoadResult> =
    readEntity(entityId, context).apply {
        addEntity(it)
        Log.d("***ENTITY LOADER", "loaded entity from database: ${it.name()}")
        Just(EntityLoadResult(entityId, false))
    }



fun loadEntityFromOfficial(entityId : EntityId, context : Context) : Maybe<EntityLoadResult> =
    entityManifest(context)
        .apply { it.persistedEntity(entityId)  }
        .apply {
            when (it.entityType)
            {
                is EntityTypeSheet    -> loadPersistedSheet(entityId, it.path, context)
                is EntityTypeCampaign -> loadPersistedCampaign(entityId, it.path, context)
                is EntityTypeGame     -> loadPersistedGame(it, context)
                is EntityTypeBook     -> loadPersistedBook(it, context)
                else                  -> Nothing()
            }
        }

// LOAD > Official
// ---------------------------------------------------------------------------------------------

// LOAD > Official > Sheet
// ---------------------------------------------------------------------------------------------
//

fun loadPersistedSheet(sheetId : EntityId, filepath : String, context : Context)
                       : Maybe<EntityLoadResult>
{
    when (sheet(sheetId)) {
        is Just -> return Just(EntityLoadResult(sheetId, true))
    }

    val sheetLoader = TomeDoc.loadSheet(filepath, context)
    return when (sheetLoader)
    {
        is Val ->
        {
            val sheet = sheetLoader.value

            addSheet(sheet)

            // Log event
//            ApplicationLog.event(OfficialSheetLoaded(sheet.entityId())

            Just(EntityLoadResult(sheet.entityId(), false))
        }
        is Err -> {
            ApplicationLog.error(sheetLoader.error)
            Nothing()
        }
    }
}


//fun loadPersistedSheet(persistedEntity : PersistedEntity, context : Context)
//                      : Maybe<EntityLoadResult>
//{
//    val sheetId = persistedEntity.entityId
//    val filepath = persistedEntity.path
//
//    when (sheet(sheetId)) {
//        is Just -> return Just(EntityLoadResult(sheetId, true))
//    }
//
//    val sheetLoader = TomeDoc.loadSheet(filepath, context)
//    return when (sheetLoader)
//    {
//        is Val ->
//        {
//            val sheet = sheetLoader.value
//
//            persistedEntity.indexes.forEach {
//                when (it.indexType) {
//                    is EntityIndexTypeGroup -> {
//                        val groups = loadGroupIndex(it.path, context)
//                        sheet.addGroups(groups)
//                    }
//                }
//            }
//
//            addGame(game)
//
//            // Log event
////            ApplicationLog.event(OfficialGameLoaded(game.gameId().value))
//
//            Just(EntityLoadResult(game.entityId(), false))
//        }
//        is Err -> {
//            ApplicationLog.error(gameLoader.error)
//            Nothing()
//        }
//    }
//}



// LOAD > Official > Campaign
// ---------------------------------------------------------------------------------------------

fun loadPersistedCampaign(campaignId : EntityId, filepath : String, context : Context)
                          : Maybe<EntityLoadResult>
{
    when (campaign(campaignId)) {
        is Just -> return Just(EntityLoadResult(campaignId, true))
    }

    val campaignLoader = TomeDoc.loadCampaign(filepath, context)
    return when (campaignLoader)
    {
        is Val ->
        {
            val campaign = campaignLoader.value

            addCampaign(campaign)

            // Log event
//            ApplicationLog.event(OfficialCampaignLoaded(campaign.campaignId().value))

            Just(EntityLoadResult(campaign.entityId(), false))
        }
        is Err -> {
            ApplicationLog.error(campaignLoader.error)
            Nothing()
        }
    }
}


// LOAD > Official > Game
// --------------------------------------------------------------------------------------------

fun loadPersistedGame(persistedEntity : PersistedEntity, context : Context)
                      : Maybe<EntityLoadResult>
{
    val gameId = persistedEntity.entityId
    val filepath = persistedEntity.path

    when (game(gameId)) {
        is Just -> return Just(EntityLoadResult(gameId, true))
    }

    val gameLoader = TomeDoc.loadGame(filepath, context)
    return when (gameLoader)
    {
        is Val ->
        {
            val game = gameLoader.value

            persistedEntity.indexes.forEach {
                when (it.indexType) {
                    is EntityIndexTypeGroup -> {
                        Log.d("***ENTITY LOADER", "loading group index")
                        val groupIndex = loadGroupIndex(it.path, context)
                        game.groupIndex.merge(groupIndex)
                    }
                    is EntityIndexTypeVariable -> {
                        Log.d("***ENTITY LOADER", "loading variable index")
                        val variables = loadVariableIndex(it.path, context)
                        game.addVariables(variables)
                    }
                }
            }

            addGame(game)

            // Log event
//            ApplicationLog.event(OfficialGameLoaded(game.gameId().value))

            Just(EntityLoadResult(game.entityId(), false))
        }
        is Err -> {
            ApplicationLog.error(gameLoader.error)
            Nothing()
        }
    }
}


private fun loadGroupIndex(filepath : String, context : Context) : GroupIndex
{
    val groupIndexLoader = TomeDoc.loadGroupIndex(filepath, context)
    return when (groupIndexLoader) {
        is Val -> groupIndexLoader.value
        is Err -> {
            ApplicationLog.error(groupIndexLoader.error)
            GroupIndex.empty()
        }
    }
}


private fun loadVariableIndex(filepath : String, context : Context) : List<Variable>
{
    val variableIndexLoader = TomeDoc.loadVariableIndex(filepath, context)
    return when (variableIndexLoader) {
        is Val -> variableIndexLoader.value.variables
        is Err -> {
            ApplicationLog.error(variableIndexLoader.error)
            listOf()
        }
    }
}

// LOAD > Official > Book
// --------------------------------------------------------------------------------------------


fun loadPersistedBook(persistedEntity : PersistedEntity, context : Context)
                      : Maybe<EntityLoadResult>
{
    val bookId = persistedEntity.entityId
    val filepath = persistedEntity.path

    when (book(bookId)) {
        is Just -> return Just(EntityLoadResult(bookId, true))
    }

    val bookLoader = TomeDoc.loadBook(filepath, context)
    return when (bookLoader)
    {
        is Val ->
        {
            val book = bookLoader.value

            persistedEntity.indexes.forEach {
                when (it.indexType) {
                    is EntityIndexTypeGroup -> {
                        val groupIndex = loadGroupIndex(it.path, context)
                        Log.d("***ENTITY LOADER", "adding group index to book")
                        book.groupIndex.merge(groupIndex)
                    }
                    is EntityIndexTypeVariable -> {
                        val variables = loadVariableIndex(it.path, context)
                        book.addVariables(variables)
                    }
                }
            }

            addBook(book)

            Just(EntityLoadResult(book.entityId(), false))
        }
        is Err -> {
            ApplicationLog.error(bookLoader.error)
            Nothing()
        }
    }
}


//
//fun loadPersistedBook(bookId : EntityId, filepath : String, context : Context)
//                      : Maybe<EntityLoadResult>
//{
//    when (book(bookId)) {
//        is Just -> return Just(EntityLoadResult(bookId, true))
//    }
//
//    val bookLoader = TomeDoc.loadBook(filepath, context)
//    return when (bookLoader)
//    {
//        is Val ->
//        {
//            val book = bookLoader.value
//
//            addBook(book)
//
//            // Log event
////            ApplicationLog.event(OfficialBookLoaded(book.bookId().value))
//
//            Just(EntityLoadResult(book.entityId(), false))
//        }
//        is Err -> {
//            ApplicationLog.error(bookLoader.error)
//            Nothing()
//        }
//    }
//}


// ---------------------------------------------------------------------------------------------
// DEFINITIONS
// ---------------------------------------------------------------------------------------------

data class EntityLoadResult(val entityId : EntityId, val fromCache : Boolean)


