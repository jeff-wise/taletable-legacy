
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



fun newSession(loaders : List<EntityLoader>,
               sessionId : SessionId,
               context : Context) = launch(UI)
{
    val loadedSession = async(CommonPool) {
        Session.load(loaders, sessionId, context)
    }.await()

    sessionById.put(sessionId, loadedSession)

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


//fun sessionSheet(sheetId : SheetId) : Maybe<Sheet>
//{
//
//}



/**
 * Session
 */
data class Session(val sessionId : SessionId,
                   val entityIds : MutableSet<EntityId>)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------
    // ADD
    // -----------------------------------------------------------------------------------------


    companion object
    {
        suspend fun load(loaders : List<EntityLoader>,
                         sessionId : SessionId,
                         context : Context) : Session
        {
            val loadedEntityIds : MutableSet<EntityId> = mutableSetOf()

            loaders.forEach {
                val entityId = async(CommonPool) { loadEntity(it, context) }.await()
                when (entityId) {
                    is Just -> loadedEntityIds.add(entityId.value)
                }
            }

            loadedEntityIds.forEach {
                initialize(it)
            }

            return Session(sessionId, loadedEntityIds)
        }
    }



//    suspend fun addOfficialSheet(officialSheetId : OfficialSheetId, context : Context)
//    {
//        val onLoad : (Sheet) -> Unit = {
//            launch(UI) {
//                // Add sheet to session
//                addSheet(it)
//
//                // Broadcast session update
//                Log.d("***SESSION", "send sheet loaded message")
//                Router.send(SessionMessageOfficialSheetLoaded(it.sheetId()))
//            }
//        }
//
//        // Second, make sure campaign is present
//        addOfficialCampaign(officialSheetId.officialCampaignId(), context)
//
//        // Finally, load sheet
//        OfficialManager.loadSheet(officialSheetId, onLoad, context)
//
//    }
//
//
//    suspend fun addOfficialCampaign(officialCampaignId : OfficialCampaignId, context : Context)
//    {
//        // Campaign already exists.
//        when (campaign(officialCampaignId.campaignId)) {
//            is Just -> return
//        }
//
//        val onLoad : (Campaign) -> Unit = {
//            launch(UI) {
//                // Add campaign
//                addCampaign(it)
//
//                // Broadcast session update
//                Router.send(SessionMessageOfficialCampaignLoaded(it.campaignId()))
//            }
//        }
//
//        // First, make sure game is present
//        addOfficialGame(officialCampaignId.officialGameId(), context)
//
//        OfficialManager.loadCampaign(officialCampaignId, onLoad, context)
//    }
//
//
//    suspend fun addOfficialGame(officialGameId : OfficialGameId, context : Context)
//    {
//        // Check if game already exists.
//        when (game(officialGameId.gameId)) {
//            is Just -> return
//        }
//
//        val onLoad : (Game) -> Unit = {
//            launch(UI) {
//                // Add campaign to state
//                addGame(it)
//
//                // Broadcast session update
//                Router.send(SessionMessageOfficialGameLoaded(it.gameId()))
//            }
//        }
//
//        OfficialManager.loadGame(officialGameId, onLoad, context)
//    }


    // -----------------------------------------------------------------------------------------
    // GET
    // -----------------------------------------------------------------------------------------

//    fun sheet(sheetId : SheetId) : Maybe<Sheet> = entitySheet(sheetId)

}


data class SessionEntityLoader(val entityId : EntityId, val entityLoader : EntityLoader)



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

