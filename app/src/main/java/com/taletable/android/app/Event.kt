
package com.taletable.android.app


import com.taletable.android.db.DatabaseEvent
import com.taletable.android.rts.entity.sheet.SheetUpdateEvent
import com.taletable.android.rts.entity.sheet.StateEvent



/**
 * Application Error
 */
interface ApplicationEvent
{
    fun debugMessage() : String
    fun logMessage() : String

    fun eventType() : EventType
}


sealed class EventType


object EventTypeLoad : EventType()
object EventTypeDatabase : EventType()
object EventTypeSheetUpdate : EventType()
object EventTypeState : EventType()
object EventTypeOther : EventType()


sealed class AppEvent : ApplicationEvent




data class AppDBEvent(val dbEvent : DatabaseEvent) : AppEvent()
{
    override fun debugMessage() : String = "Database Event: " + dbEvent.debugMessage()

    override fun logMessage() : String = "Database Event: " + dbEvent.logMessage()

    override fun eventType() = EventTypeDatabase
}


data class AppStateEvent(val stateEvent : StateEvent) : AppEvent()
{
    override fun debugMessage() : String = "State Event: " + stateEvent.debugMessage()

    override fun logMessage() : String = "State Event: " + stateEvent.logMessage()

    override fun eventType() = EventTypeOther
}


data class AppSheetUpdateEvent(val sheetUpdateEvent : SheetUpdateEvent) : AppEvent()
{
    override fun debugMessage() : String = "Sheet Update Event: " + sheetUpdateEvent.debugMessage()

    override fun logMessage() : String = "Sheet Update Event: " + sheetUpdateEvent.logMessage()

    override fun eventType() = EventTypeOther
}
