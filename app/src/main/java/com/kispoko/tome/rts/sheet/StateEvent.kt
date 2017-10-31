
package com.kispoko.tome.rts.sheet


import com.kispoko.tome.app.ApplicationEvent
import com.kispoko.tome.model.game.engine.mechanic.MechanicId
import com.kispoko.tome.model.game.engine.variable.VariableId



/**
 * State Error
 */
sealed class StateEvent : ApplicationEvent


data class VariableAdded(val variableId : VariableId) : StateEvent()
{
    override fun debugMessage() : String = """Variable Added:
            |    Variable Id: $variableId""".trimMargin()

    override fun logMessage(): String = debugMessage()
}


data class VariableRemoved(val variableId : VariableId) : StateEvent()
{
    override fun debugMessage() : String = """Variable Removed:
            |    Variable Id: $variableId""".trimMargin()

    override fun logMessage(): String = debugMessage()
}


data class VariableUpdated(val variableId : VariableId) : StateEvent()
{
    override fun debugMessage() : String = """Variable Updated:
            |    Variable Id: $variableId""".trimMargin()

    override fun logMessage(): String = debugMessage()
}


data class VariableRenamed(val variableId : VariableId, val newVariableId : VariableId) : StateEvent()
{
    override fun debugMessage() : String = """Variable Updated:
            |    Old Variable Id: $variableId
            |    New Variable Id: $newVariableId""".trimMargin()

    override fun logMessage(): String = debugMessage()
}


data class MechanicAdded(val mechanicId : MechanicId,
                         val variableIds : Set<VariableId>) : StateEvent()
{
    override fun debugMessage() : String
    {
        val variablesString = variableIds.map { it.nameString() }.joinToString("\n        ")

        return """Mechanic Added:
               |    Mechanic Id: ${mechanicId.value}
               |    Variables Added:
               |        $variablesString
               """.trimMargin()
    }

    override fun logMessage(): String = debugMessage()
}


data class MechanicRemoved(val mechanicId : MechanicId,
                           val variableIds : Set<VariableId>) : StateEvent()
{
    override fun debugMessage() : String
    {
        val variablesString = variableIds.map { it.nameString() }.joinToString("\n        ")

        return """Mechanic Removed:
               |    Mechanic Id: ${mechanicId.value}
               |    Variables Removed:
               |        $variablesString
               """.trimMargin()
    }

    override fun logMessage(): String = debugMessage()
}

