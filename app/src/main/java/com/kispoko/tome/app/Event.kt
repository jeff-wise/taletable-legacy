
package com.kispoko.tome.app


import com.kispoko.tome.official.OfficialTheme
import com.kispoko.tome.rts.sheet.StateEvent


/**
 * Application Error
 */
interface ApplicationEvent
{
    fun debugMessage() : String
    fun logMessage() : String
}


sealed class AppEvent : ApplicationEvent


data class OfficialThemeAdded(val officialTheme : OfficialTheme) : AppEvent()
{
    override fun debugMessage() = "Official Theme Added: $officialTheme"

    override fun logMessage() : String = debugMessage()
}


data class AppStateEvent(val stateEvent : StateEvent) : AppEvent()
{
    override fun debugMessage() : String = "State Event: " + stateEvent.debugMessage()

    override fun logMessage() : String = "State Event: " + stateEvent.logMessage()
}
