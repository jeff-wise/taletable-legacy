
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
                is EntityTypeBook     -> loadPersistedBook(entityId, it.path, context)
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

fun loadPersistedBook(bookId : EntityId, filepath : String, context : Context)
                      : Maybe<EntityLoadResult>
{
    when (book(bookId)) {
        is Just -> return Just(EntityLoadResult(bookId, true))
    }

    val bookLoader = TomeDoc.loadBook(filepath, context)
    return when (bookLoader)
    {
        is Val ->
        {
            val book = bookLoader.value

            addBook(book)

            // Log event
//            ApplicationLog.event(OfficialBookLoaded(book.bookId().value))

            Just(EntityLoadResult(book.entityId(), false))
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


data class EntityLoader(val entityId : EntityId,
                        val name : String,
                        val category : String) : Serializable
{
    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityLoader> = when (yamlValue)
        {
            is YamlDict ->
            {
                apply(::EntityLoader,
                      // Entity Id
                      yamlValue.at("entity_id") ap { EntityId.fromYaml(it) },
                      // Name
                      yamlValue.text("name"),
                      // Category
                      yamlValue.text("category"))
            }
            else        -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
        }

    }

}


//sealed class EntityLoader(open val name : String, open val category : String) : Serializable
//{
//    companion object
//    {
//        fun fromYaml(yamlValue: YamlValue) : YamlParser<EntityLoader> = when (yamlValue)
//        {
//            is YamlDict ->
//            {
//                yamlValue.text("type") apply {
//                    when (it) {
//                        "official" -> yamlValue.at("official").apply(EntityLoaderOfficial.Companion::fromYaml) as YamlParser<EntityLoader>
//                        else       -> effError<YamlParseError,EntityLoader>(
//                                        UnexpectedStringValue(it, yamlValue.path))
//                    }
//                }
//            }
//            else        -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
//        }
//    }
//
//}


// ENTITY LOADER > UNKNOWN
// ---------------------------------------------------------------------------------------------

//class EntityLoaderUnknown() : EntityLoader("", "")


// ENTITY LOADER > SAVED
// ---------------------------------------------------------------------------------------------

//data class EntityLoaderSaved(val rowId : Long) : EntityLoader("", "")


// ENTITY LOADER > OFFICIAL
// ---------------------------------------------------------------------------------------------

//sealed class EntityLoaderOfficial(override val name : String,
//                                  override val category : String) : EntityLoader(name, category)
//{
//    abstract fun filePath() : String
//
//    companion object
//    {
//        fun fromYaml(yamlValue: YamlValue) : YamlParser<EntityLoaderOfficial> = when (yamlValue)
//        {
//            is YamlDict ->
//            {
//                yamlValue.text("type") apply {
//                    when (it) {
//                        "sheet"    -> yamlValue.at("sheet").apply(OfficialSheetLoader.Companion::fromYaml)
//                        "campaign" -> yamlValue.at("campaign").apply(OfficialCampaignLoader.Companion::fromYaml)
//                        "game"     -> yamlValue.at("game").apply(OfficialGameLoader.Companion::fromYaml)
//                        "book"     -> yamlValue.at("book").apply(OfficialBookLoader.Companion::fromYaml)
//                        else       -> effError<YamlParseError,EntityLoaderOfficial>(
//                                          UnexpectedStringValue(it, yamlValue.path))
//                    }
//                }
//            }
//            else        -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
//        }
//
//    }
//}
//
//
//data class OfficialSheetLoader(override val name : String,
//                               override val category : String,
//                               val sheetId : SheetId,
//                               val gameId : GameId) : EntityLoaderOfficial(name, category)
//{
//
//    override fun filePath() : String =
//            "official/" + gameId.value +
//            "/sheets/" + sheetId.value + ".yaml"
//
//
//    companion object
//    {
//        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityLoaderOfficial> = when (yamlValue)
//        {
//            is YamlDict ->
//            {
//                apply(::OfficialSheetLoader,
//                      // Name
//                      yamlValue.text("name"),
//                      // Category
//                      yamlValue.text("category"),
//                      // Sheet Id
//                      yamlValue.at("sheet_id") ap { SheetId.fromYaml(it) },
//                      // Game Id
//                      yamlValue.at("game_id") ap { GameId.fromYaml(it) })
//            }
//            else        -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
//        }
//
//    }
//
//}
//
//
//data class OfficialCampaignLoader(override val name : String,
//                                  override val category : String,
//                                  val campaignId : CampaignId,
//                                  val gameId : GameId) : EntityLoaderOfficial(name, category)
//{
//
//    override fun filePath() : String =
//            "official/" + gameId.value +
//            "/campaigns/" + campaignId.value + ".yaml"
//
//
//    companion object
//    {
//        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityLoaderOfficial> = when (yamlValue)
//        {
//            is YamlDict ->
//            {
//                apply(::OfficialCampaignLoader,
//                      // Name
//                      yamlValue.text("name"),
//                      // Category
//                      yamlValue.text("category"),
//                      // Campaign Id
//                      yamlValue.at("campaign_id") ap { CampaignId.fromYaml(it) },
//                      // Game Id
//                      yamlValue.at("game_id") ap { GameId.fromYaml(it) })
//            }
//            else        -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
//        }
//
//    }
//
//
//}
//
//
//data class OfficialGameLoader(override val name: String,
//                              override val category : String,
//                              val gameId : GameId) : EntityLoaderOfficial(name, category)
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    override fun filePath() : String =
//        "official/" + gameId.value +
//        "/" + gameId.value +  ".yaml"
//
//
//    fun weaponGroupsFilePath() : String =
//            "official/" + gameId.value + "/indexes/group/weapons.yaml"
//
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object
//    {
//        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityLoaderOfficial> = when (yamlValue)
//        {
//            is YamlDict ->
//            {
//                apply(::OfficialGameLoader,
//                      // Name
//                      yamlValue.text("name"),
//                      // Category
//                      yamlValue.text("category"),
//                      // Game Id
//                      yamlValue.at("game_id") ap { GameId.fromYaml(it) })
//            }
//            else        -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
//        }
//
//    }
//
//}
//
//
//data class OfficialBookLoader(override val name : String,
//                              override val category : String,
//                              val bookId : BookId,
//                              val gameId : GameId)
//                               : EntityLoaderOfficial(name, category)
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    override fun filePath() : String =
//        "official/" + gameId.value +
//        "/books/" + bookId.value +  ".yaml"
//
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object
//    {
//        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityLoaderOfficial> = when (yamlValue)
//        {
//            is YamlDict ->
//            {
//                apply(::OfficialBookLoader,
//                      // Name
//                      yamlValue.text("name"),
//                      // Category
//                      yamlValue.text("category"),
//                      // Book Id
//                      yamlValue.at("book_id") ap { BookId.fromYaml(it) },
//                      // Game Id
//                      yamlValue.at("game_id") ap { GameId.fromYaml(it) })
//            }
//            else        -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
//        }
//
//    }
//}
