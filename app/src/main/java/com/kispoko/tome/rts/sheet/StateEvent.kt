
package com.kispoko.tome.rts.sheet


import com.kispoko.tome.app.ApplicationEvent
import com.kispoko.tome.model.game.engine.variable.VariableId



/**
 * State Error
 */
sealed class StateEvent : ApplicationEvent


data class VariableAdded(val variableId : VariableId) : StateEvent()
{
    override fun debugMessage(): String = "Variable Added: ${variableId.name.value.value}"

    override fun logMessage(): String = debugMessage()
}

