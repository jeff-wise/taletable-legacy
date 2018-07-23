
package com.taletable.android.db


import com.taletable.android.app.ApplicationEvent
import com.taletable.android.app.EventTypeDatabase
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.session.SessionId
import com.taletable.android.util.Util



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
