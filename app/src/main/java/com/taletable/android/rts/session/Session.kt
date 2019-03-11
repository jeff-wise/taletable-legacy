
package com.taletable.android.rts.session


import android.content.Context
import com.kispoko.culebra.*
import com.taletable.android.activity.session.SessionListHeader
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.model.entity.PersistedEntity
import com.taletable.android.model.entity.entityManifest
import com.taletable.android.router.Router
import com.taletable.android.rts.entity.*
import effect.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.taletable.android.rts.entity.sheet as entitySheet
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
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


suspend fun openSession(session : Session, context : Context)
{
    activateSession(session, context)

    sessionById[session.sessionId] = session

    activeSessionId = Just(session.sessionId)

//    writeSession(session, context)

    Router.send(MessageSessionLoaded(session.sessionId))
}


fun session(sessionId : SessionId) : Maybe<Session>
{
    val sessionOrNull = sessionById.get(sessionId)
    return if (sessionOrNull != null)
        Just(sessionOrNull)
    else
        Nothing()
}


suspend fun activateSession(loader : Session, context : Context)
{
    val entityLoadResults : MutableSet<EntityLoadResult> = mutableSetOf()

    val numberOfLoaders = loader.entityIds.size
    loader.entityIds.forEachIndexed { index, entityId ->

        val entityLoadResult = withContext(Dispatchers.IO) {
            loadEntity(entityId, context)
        }

        entityLoadResult.doMaybe {
//            Log.d("***SESSION", "loaded: $entityLoadResult")
            Router.send(MessageSessionEntityLoaded(SessionLoadUpdate(index + 1, numberOfLoaders)))
            entityLoadResults.add(it)
        }
    }

    entityLoadResults.forEach {
        if (!it.fromCache)
            initialize(it.entityId)
    }

}



/**
 * Session
 */
data class Session(val sessionId : SessionId,
                   val sessionName : SessionName,
                   val sessionInfo : SessionInfo,
                   val gameId : EntityId,
                   val timeLastUsed : Maybe<Calendar>,
                   val entityIds : List<EntityId>,
                   val mainEntityId : EntityId) : Serializable
{

    // | PROPERTIES
    // -----------------------------------------------------------------------------------------

    var selectedEntityId : EntityId? = null



    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<Session> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::Session,
                          // Session Id
                          yamlValue.at("session_id") ap { SessionId.fromYaml(it) },
                          // Session Name
                          yamlValue.at("session_name") ap { SessionName.fromYaml(it) },
                          // Session Info
                          yamlValue.at("info") ap { SessionInfo.fromYaml(it) },
                          // Game Id
                          yamlValue.at("game_id") ap { EntityId.fromYaml(it) },
                          // Time Last Used
                          effValue<YamlParseError,Maybe<Calendar>>(Just(Calendar.getInstance())),
                          // Entity Ids
                          yamlValue.array("entity_ids") ap {
                              it.mapApply { EntityId.fromYaml(it) }},
                          // Main Entity Id
                          yamlValue.at("main_entity_id") ap { EntityId.fromYaml(it) }
                          )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }



    // | Entities
    // -----------------------------------------------------------------------------------------

    fun persistedEntities(context : Context) : List<PersistedEntity>
    {
        val entities : MutableList<PersistedEntity> = mutableListOf()

        val mManifest = entityManifest(context)
        when (mManifest) {
            is Just -> {
                val manifest = mManifest.value
                this.entityIds.forEach { entityId ->
                    manifest.persistedEntity(entityId).doMaybe {
                        entities.add(it)
                    }
                }
            }
        }

        return entities
    }


    fun entityViewList(context : Context) : List<Any>
    {
        val viewList : MutableList<Any> = mutableListOf()

//        var selectedEntity : PersistedEntity? = null
        var otherEntities : MutableList<PersistedEntity> = mutableListOf()

        entityManifest(context).doMaybe { manifest ->

            this.entityIds.forEach { entityId ->
//                if (selectedEntityId == entityId) {
//                    selectedEntity = manifest.persistedEntity(entityId).toNullable()
//                } else {
                manifest.persistedEntity(entityId).doMaybe { otherEntities.add(it) }
                //}
            }
        }


//        viewList.add(SessionCurrentEntityHeader())
//        selectedEntity?.let { viewList.add(SessionCurrentEntity(it)) }
//        viewList.add(SessionOtherEntitiesHeader())
//        viewList.addAll(otherEntities)

        viewList.add(SessionListHeader())
        viewList.addAll(otherEntities)

        return viewList
    }


    fun entities() : List<Entity>
    {
        val entityList : MutableList<Entity> = mutableListOf()

        this.entityIds.forEach { entityId ->
            entityRecord(entityId) apDo { record ->
                entityList.add(record.entity())
            }
        }

        return entityList
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


}


data class SessionLoadUpdate(val entityLoadNumber : Int,
                             val totalEntities : Int) : Serializable


//data class SessionRecord(val sessionId : SessionId,
//                         val sessionName : SessionName,
//                         val sessionTagline : String,
//                         val sessionDescription : SessionDescription,
//                         val lastUsed : Calendar,
//                         val loader : SessionLoader?) : Serializable
//

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


        fun fromString(uuidString : String) : SessionId?
        {
            return try {
                SessionId(UUID.fromString(uuidString))
            }
            catch (e : IllegalArgumentException) {
                null
            }
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


//
//data class SessionLoader(val sessionId : SessionId,
//                         val sessionName : SessionName,
//                         val sessionInfo : SessionInfo,
//                         val gameId : EntityId,
//                         val entityKindId : EntityKindId,
//                         val timeLastUsed : Maybe<Calendar>,
//                         val entityIds : List<EntityId>,
//                         val mainEntityId : EntityId) : Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object
//    {
//        fun fromYaml(yamlValue : YamlValue) : YamlParser<SessionLoader> =
//            when (yamlValue)
//            {
//                is YamlDict ->
//                {
//                    apply(::SessionLoader,
//                          // Session Id
//                          yamlValue.at("session_id") ap { SessionId.fromYaml(it) },
//                          // Session Name
//                          yamlValue.at("session_name") ap { SessionName.fromYaml(it) },
//                          // Session Info
//                          yamlValue.at("info") ap { SessionInfo.fromYaml(it) },
//                          // Game Id
//                          yamlValue.at("game_id") ap { EntityId.fromYaml(it) },
//                          // Main Entity Kind Id
//                          yamlValue.at("main_entity_kind_id") ap { EntityKindId.fromYaml(it) },
//                          // Time Last Used
//                          effValue(Nothing()),
//                          // Entity Loaders
//                          yamlValue.array("entity_ids") ap {
//                              it.mapApply { EntityId.fromYaml(it) }},
//                          // Main Entity Id
//                          yamlValue.at("main_entity_id") ap { EntityId.fromYaml(it) }
//                          )
//                }
//                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
//            }
//    }


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

//    fun entityLoadersByType() : Map<EntityType,List<EntityLoader>>
//    {
//        val loaderByType : MutableMap<EntityType,MutableList<EntityLoader>> = mutableMapOf()
//
//        loaderByType[EntityTypeSheet] = mutableListOf()
//        loaderByType[EntityTypeCampaign] = mutableListOf()
//        loaderByType[EntityTypeGame] = mutableListOf()
//        loaderByType[EntityTypeBook] = mutableListOf()
//
//        this.entityLoaders.forEach {
//            when (it) {
//                is EntityLoaderOfficial -> {
//                    when (it) {
//                        is OfficialSheetLoader -> loaderByType[EntityTypeSheet]?.add(it)
//                        is OfficialCampaignLoader -> loaderByType[EntityTypeCampaign]?.add(it)
//                        is OfficialGameLoader -> loaderByType[EntityTypeGame]?.add(it)
//                        is OfficialBookLoader -> loaderByType[EntityTypeBook]?.add(it)
//                    }
//                }
//            }
//        }
//
//        return loaderByType
//    }

// }


