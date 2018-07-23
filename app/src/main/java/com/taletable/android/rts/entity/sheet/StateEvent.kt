
package com.taletable.android.rts.entity.sheet


import com.taletable.android.app.ApplicationEvent
import com.taletable.android.app.EventTypeState
import com.taletable.android.model.engine.mechanic.MechanicId
import com.taletable.android.model.engine.variable.VariableId
import com.taletable.android.model.engine.variable.VariableRelation
import com.taletable.android.model.engine.variable.VariableTag



/**
 * State Error
 */
sealed class StateEvent : ApplicationEvent


data class VariableAdded(val variableId : VariableId) : StateEvent()
{
    override fun debugMessage() : String = """Variable Added:
            |    Variable Id: $variableId""".trimMargin()

    override fun logMessage(): String = debugMessage()

    override fun eventType() = EventTypeState
}


data class VariableRemoved(val variableId : VariableId) : StateEvent()
{
    override fun debugMessage() : String = """Variable Removed:
            |    Variable Id: $variableId""".trimMargin()

    override fun logMessage(): String = debugMessage()

    override fun eventType() = EventTypeState
}


data class VariableUpdated(val variableId : VariableId) : StateEvent()
{
    override fun debugMessage() : String = """Variable Updated:
            |    Variable Id: $variableId""".trimMargin()

    override fun logMessage(): String = debugMessage()

    override fun eventType() = EventTypeState
}


data class VariableRenamed(val variableId : VariableId, val newVariableId : VariableId) : StateEvent()
{
    override fun debugMessage() : String = """Variable Updated:
            |    Old Variable Id: $variableId
            |    New Variable Id: $newVariableId""".trimMargin()

    override fun logMessage(): String = debugMessage()

    override fun eventType() = EventTypeState
}


data class VariableRelationAdded(val variableId : VariableId,
                                 val relation : VariableRelation) : StateEvent()
{
    override fun debugMessage() : String = """Variable Relation Added:
            |    Variable Id: $variableId
            |    Relation: ${relation.value}""".trimMargin()

    override fun logMessage(): String = debugMessage()

    override fun eventType() = EventTypeState
}


data class VariableTagAdded(val variableId : VariableId,
                            val tag : VariableTag) : StateEvent()
{
    override fun debugMessage() : String = """Variable Tag Added:
            |    Variable Id: $variableId
            |    Tag: ${tag.value}""".trimMargin()

    override fun logMessage(): String = debugMessage()

    override fun eventType() = EventTypeState
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

    override fun eventType() = EventTypeState
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

    override fun eventType() = EventTypeState
}

