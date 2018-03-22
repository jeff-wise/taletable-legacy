
package com.kispoko.tome.rts.session


import android.content.Context
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.router.Router
import com.kispoko.tome.rts.entity.*
import com.kispoko.tome.rts.entity.sheet as entitySheet
import effect.effError
import effect.effValue
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



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


fun newSession(loaders : List<EntityLoader>,
               sessionId : SessionId,
               context : Context) = launch(UI)
{
    val loadedSession = async(CommonPool) {
        Session.load(loaders, sessionId, context)
    }.await()

    sessionById.put(sessionId, loadedSession)

    activeSessionId = Just(sessionId)

    Router.send(MessageSessionLoaded(sessionId))
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
                   val entityIds : MutableSet<EntityId>)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    companion object
    {
        suspend fun load(loaders : List<EntityLoader>,
                         sessionId : SessionId,
                         context : Context) : Session
        {
            val entityLoadResults : MutableSet<EntityLoadResult> = mutableSetOf()

            loaders.forEach {
                val entityLoadResult = async(CommonPool) { loadEntity(it, context) }.await()
                when (entityLoadResult) {
                    is Just -> entityLoadResults.add(entityLoadResult.value)
                }
            }

            entityLoadResults.forEach {
                if (!it.fromCache)
                    initialize(it.entityId)
            }

            return Session(sessionId, entityLoadResults.map { it.entityId }.toMutableSet() )
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

}


/**
 * Session Id
 */
data class SessionId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SessionId>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<SessionId> = when (doc)
        {
            is DocText -> effValue(SessionId(doc.text))
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

