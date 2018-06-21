
package com.kispoko.tome.rts.session


import android.content.Context
import android.util.Log
import com.kispoko.culebra.*
import com.kispoko.tome.db.saveSession
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.router.Router
import com.kispoko.tome.rts.entity.*
import effect.*
import com.kispoko.tome.rts.entity.sheet as entitySheet
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import maybe.maybe
import java.io.IOException
import java.io.Serializable
import java.util.*



// ---------------------------------------------------------------------------------------------
// SESSIONS
// ---------------------------------------------------------------------------------------------


private val sessionById : MutableMap<SessionId,Session> = mutableMapOf()

private var activeSessionId : Maybe<SessionId> = Nothing()



fun activeSession() : Maybe<Session> = activeSessionId ap {
    val session = sessionById[it]
    if (session != null)
        Just(session)
    else
        Nothing<Session>()
}


fun newSession(loader : SessionLoader,
               context : Context) = launch(UI)
{
    val loadedSession = async(CommonPool) {
        Session.load(loader, context)
    }.await()

    sessionById[loader.sessionId] = loadedSession

    activeSessionId = Just(loader.sessionId)

    saveSession(loadedSession.record(), context)

    Router.send(MessageSessionLoaded(loader.sessionId))
}


fun session(sessionId : SessionId) : Maybe<Session>
{
    val sessionOrNull = sessionById.get(sessionId)
    return if (sessionOrNull != null)
        Just(sessionOrNull)
    else
        Nothing()
}



/**
 * Session
 */
data class Session(val sessionId : SessionId,
                   val sessionName : SessionName,
                   val sessionInfo : SessionInfo,
                   val gameId : GameId,
                   val entityKindId : EntityKindId,
                   val timeLastUsed : Calendar,
                   val entityIds : MutableSet<EntityId>,
                   val mainEntityId : EntityId) : Serializable
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    companion object
    {
        suspend fun load(loader : SessionLoader,
                         context : Context) : Session
        {
            val entityLoadResults : MutableSet<EntityLoadResult> = mutableSetOf()

            val numberOfLoaders = loader.entityLoaders.size
            loader.entityLoaders.forEachIndexed { index, entityLoader ->
                val entityLoadResult = async(CommonPool) { loadEntity(entityLoader, context) }.await()
                when (entityLoadResult) {
                    is Just -> {
                        Log.d("***SESSION", "loaded: $entityLoadResult")
                        Router.send(MessageSessionEntityLoaded(SessionLoadUpdate(index + 1, numberOfLoaders)))
                        entityLoadResults.add(entityLoadResult.value)
                    }
                }
            }

            entityLoadResults.forEach {
                if (!it.fromCache)
                    initialize(it.entityId)
            }

            val entityIds = entityLoadResults.map { it.entityId }.toMutableSet()

            val date = when (loader.timeLastUsed) {
                is Just    -> loader.timeLastUsed.value
                is Nothing -> Calendar.getInstance()
            }

            return Session(loader.sessionId,
                           loader.sessionName,
                           loader.sessionInfo,
                           loader.gameId,
                           loader.entityKindId,
                           date,
                           entityIds,
                           loader.mainEntityId)
        }
    }


    // -----------------------------------------------------------------------------------------
    // ENTITIES
    // -----------------------------------------------------------------------------------------

    fun entityRecords() : List<EntityRecord>
    {
        val records : MutableList<EntityRecord> = mutableListOf()

        this.entityIds.forEach {
            entityRecord(it) apDo {
                records.add(it)
            }
        }

        return records
    }


    fun entityRecordsByType() : Map<String,List<Entity>>
    {
        val recordByType : MutableMap<String,MutableList<Entity>> = mutableMapOf()

        this.entityIds.forEach { entityId ->
            entityRecord(entityId) apDo { record ->
                val entityType = record.entityType.toString()
                if (!recordByType.containsKey(entityType))
                    recordByType.put(entityType, mutableListOf())

                recordByType[entityType]!!.add(record.entity())
            }
        }

        return recordByType
    }


    // -----------------------------------------------------------------------------------------
    // LOADER
    // -----------------------------------------------------------------------------------------

    fun loader() : SessionLoader =
        SessionLoader(this.sessionId,
                      this.sessionName,
                      this.sessionInfo,
                      this.gameId,
                      this.entityKindId,
                      Just(this.timeLastUsed),
                      this.entityRecords().map { it.entity().entityLoader() },
                      this.mainEntityId)


    // -----------------------------------------------------------------------------------------
    // RECORD
    // -----------------------------------------------------------------------------------------

    fun record() = SessionRecord(this.sessionId,
                                 this.sessionName,
                                 Calendar.getInstance(),
                                 this.loader())

}


data class SessionLoadUpdate(val entityLoadNumber : Int,
                             val totalEntities : Int) : Serializable


data class SessionRecord(val sessionId : SessionId,
                         val sessionName : SessionName,
                         val lastUsed : Calendar,
                         val loader : SessionLoader?) : Serializable


/**
 * Session Id
 */
data class SessionId(val value : UUID) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SessionId>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<SessionId> = when (doc)
        {
            is DocText -> {
                try {
                    effValue<ValueError,SessionId>(SessionId(UUID.fromString(doc.text)))
                }
                catch (e : IllegalArgumentException) {
                    effError<ValueError,SessionId>(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }

        fun fromYaml(yamlValue : YamlValue) : YamlParser<SessionId> =
            when (yamlValue)
            {
                is YamlText -> effValue(SessionId(UUID.fromString(yamlValue.text)))
                else        -> error(UnexpectedTypeFound(YamlType.TEXT,
                                                         yamlType(yamlValue),
                                                         yamlValue.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value.toString())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value.toString()})

}


/**
 * Session Name
 */
data class SessionName(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SessionName>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<SessionName> = when (doc)
        {
            is DocText -> effValue(SessionName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }

        fun fromYaml(yamlValue : YamlValue) : YamlParser<SessionName> =
            when (yamlValue)
            {
                is YamlText -> effValue(SessionName(yamlValue.text))
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
 * Session Summary
 */
data class SessionSummary(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SessionSummary>
    {

        override fun fromDocument(doc : SchemaDoc) : ValueParser<SessionSummary> = when (doc)
        {
            is DocText -> effValue(SessionSummary(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }

        fun fromYaml(yamlValue : YamlValue) : YamlParser<SessionSummary> =
            when (yamlValue)
            {
                is YamlText -> effValue(SessionSummary(yamlValue.text))
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
 * Session Description
 */
data class SessionDescription(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SessionDescription>
    {

        override fun fromDocument(doc : SchemaDoc) : ValueParser<SessionDescription> = when (doc)
        {
            is DocText -> effValue(SessionDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }

        fun fromYaml(yamlValue : YamlValue) : YamlParser<SessionDescription> =
            when (yamlValue)
            {
                is YamlText -> effValue(SessionDescription(yamlValue.text))
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
 * Session Tag
 */
data class SessionTag(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SessionTag>
    {

        override fun fromDocument(doc : SchemaDoc) : ValueParser<SessionTag> = when (doc)
        {
            is DocText -> effValue(SessionTag(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }

        fun fromYaml(yamlValue : YamlValue) : YamlParser<SessionTag> =
            when (yamlValue)
            {
                is YamlText -> effValue(SessionTag(yamlValue.text))
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


data class SessionInfo(val sessionSummary : SessionSummary,
                       val sessionDescription : SessionDescription,
                       val tagline : String,
                       val primaryTag : SessionTag,
                       val secondaryTags : List<SessionTag>) : Serializable
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<SessionInfo> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::SessionInfo,
                          // Summary
                          yamlValue.at("summary") ap { SessionSummary.fromYaml(it) },
                          // Description
                          yamlValue.at("description") ap { SessionDescription.fromYaml(it) },
                          // Tagline
                          yamlValue.text("tagline"),
                          // Primary Tag
                          yamlValue.at("primary_tag") ap { SessionTag.fromYaml(it) },
                          // Secondary Attributes
                          yamlValue.array("secondary_tags") ap {
                              it.mapApply { SessionTag.fromYaml(it) }}
                          )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }

}



data class SessionLoader(val sessionId : SessionId,
                         val sessionName : SessionName,
                         val sessionInfo : SessionInfo,
                         val gameId : GameId,
                         val entityKindId : EntityKindId,
                         val timeLastUsed : Maybe<Calendar>,
                         val entityLoaders : List<EntityLoader>,
                         val mainEntityId : EntityId) : Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<SessionLoader> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::SessionLoader,
                          // Session Id
                          yamlValue.at("session_id") ap { SessionId.fromYaml(it) },
                          // Session Name
                          yamlValue.at("session_name") ap { SessionName.fromYaml(it) },
                          // Session Info
                          yamlValue.at("info") ap { SessionInfo.fromYaml(it) },
                          // Game Id
                          yamlValue.at("game_id") ap { GameId.fromYaml(it) },
                          // Main Entity Kind Id
                          yamlValue.at("main_entity_kind_id") ap { EntityKindId.fromYaml(it) },
                          // Time Last Used
                          effValue(Nothing()),
                          // Entity Loaders
                          yamlValue.array("loaders") ap {
                              it.mapApply { EntityLoader.fromYaml(it) }},
                          // Main Entity Id
                          yamlValue.at("main_entity_id") ap { EntityId.fromYaml(it) }
                          )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    fun entityLoadersByType() : Map<EntityType,List<EntityLoader>>
    {
        val loaderByType : MutableMap<EntityType,MutableList<EntityLoader>> = mutableMapOf()

        loaderByType[EntityTypeSheet] = mutableListOf()
        loaderByType[EntityTypeCampaign] = mutableListOf()
        loaderByType[EntityTypeGame] = mutableListOf()
        loaderByType[EntityTypeBook] = mutableListOf()

        this.entityLoaders.forEach {
            when (it) {
                is EntityLoaderOfficial -> {
                    when (it) {
                        is OfficialSheetLoader -> loaderByType[EntityTypeSheet]?.add(it)
                        is OfficialCampaignLoader -> loaderByType[EntityTypeCampaign]?.add(it)
                        is OfficialGameLoader -> loaderByType[EntityTypeGame]?.add(it)
                        is OfficialBookLoader -> loaderByType[EntityTypeBook]?.add(it)
                    }
                }
            }
        }

        return loaderByType
    }

}


data class SessionManifest(val summaries : List<SessionLoader>)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    private val loadersBySessionId : MutableMap<SessionId,SessionLoader> =
            summaries.associateBy { it.sessionId }
                    as MutableMap<SessionId,SessionLoader>

    private val loadersByEntityId : MutableMap<EntityKindId,MutableList<SessionLoader>> = mutableMapOf()



    init {

        summaries.forEach { loader ->
            val kindId = loader.entityKindId
            if (!loadersByEntityId.containsKey(kindId))
                loadersByEntityId[kindId] = mutableListOf()
            loadersByEntityId[kindId]!!.add(loader)
        }
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<SessionManifest> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::SessionManifest,
                          // Summaries
                          yamlValue.array("summaries") ap {
                              it.mapApply { SessionLoader.fromYaml(it) }}
                    )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    fun sessionLoader(sessionId : SessionId) : Maybe<SessionLoader> =
            maybe(this.loadersBySessionId[sessionId])


    fun sessionLoaders(entityKindId : EntityKindId) : List<SessionLoader> =
            this.loadersByEntityId[entityKindId] ?: listOf()


}


// ---------------------------------------------------------------------------------------------
// OFFICIAL SESSION
// ---------------------------------------------------------------------------------------------


val sessionManifestCache : MutableMap<GameId,SessionManifest> = mutableMapOf()


/**
 * Get an official session.
 */
fun officialSession(gameId : GameId, sessionId : SessionId, context : Context) : Maybe<SessionLoader> =
    sessionManifest(gameId, context).apply { manifest ->
        manifest.sessionLoader(sessionId)
    }


fun sessionManifest(gameId : GameId, context : Context) : Maybe<SessionManifest>
{
    val cachedManifest = sessionManifestCache[gameId]

    if (cachedManifest != null)
    {
        return Just(cachedManifest)
    }
    else
    {
        val filePath = "official/${gameId.value}/session_manifest.yaml"


        val fileInputStream = try {
            context.assets?.open(filePath)
        }
        catch (e : IOException) {
            null
        }

        return maybe(fileInputStream).apply {
            val manifestParser : YamlParser<SessionManifest> = parseYaml(it, SessionManifest.Companion::fromYaml)
            when (manifestParser) {
                is Val -> {
                    sessionManifestCache[gameId] = manifestParser.value
                    Just(manifestParser.value)
                }
                is Err -> {
                    Nothing<SessionManifest>()
                }
            }
        }
    }

}

