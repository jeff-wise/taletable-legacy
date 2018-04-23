
package com.kispoko.tome.db


import com.kispoko.tome.app.ApplicationEvent
import com.kispoko.tome.app.EventTypeDatabase
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.session.SessionId
import com.kispoko.tome.util.Util



/**
 * Database Event
 */
sealed class DatabaseEvent : ApplicationEvent


data class DatabaseEntitySaved(val entityId : EntityId,
                               val insertTimeMS : Long) : DatabaseEvent()
{
    override fun debugMessage() : String = """Entity Saved:
            |    Entity Id: $entityId
            |    Insert Time (ms): ${Util.timeDifferenceString(insertTimeMS)}""".trimMargin()

    override fun logMessage(): String = debugMessage()

    override fun eventType() = EventTypeDatabase
}


data class DatabaseEntityUpdated(val entityId : EntityId,
                                 val insertTimeMS : Long) : DatabaseEvent()
{
    override fun debugMessage() : String = """Entity Updated:
            |    Entity Id: $entityId
            |    Insert Time (ms): ${Util.timeDifferenceString(insertTimeMS)}""".trimMargin()

    override fun logMessage(): String = debugMessage()

    override fun eventType() = EventTypeDatabase
}



data class DatabaseSessionSaved(val sessionId : SessionId,
                                val insertTimeMS : Long) : DatabaseEvent()
{
    override fun debugMessage() : String = """Session Saved:
            |    Session Id: $sessionId
            |    Insert Time (ms): ${Util.timeDifferenceString(insertTimeMS)}""".trimMargin()

    override fun logMessage(): String = debugMessage()

    override fun eventType() = EventTypeDatabase
}
