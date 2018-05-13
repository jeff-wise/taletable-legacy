
package com.kispoko.tome.model.engine.task


import android.util.Log
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.model.engine.EngineValue
import com.kispoko.tome.model.engine.variable.VariableId
import com.kispoko.tome.model.engine.constraint.Constraint
import com.kispoko.tome.model.engine.variable.VariableReference
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.variable
import effect.Err
import effect.Val
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Task
 */
data class Task(val title : TaskTitle,
                val description : TaskDescription,
                val trigger : TaskTrigger,
                val action : TaskAction)
                 : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Task>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Task> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::Task,
                      // Title
                      doc.at("title") ap { TaskTitle.fromDocument(it) },
                      // Description
                      doc.at("description") ap { TaskDescription.fromDocument(it) },
                      // Trigger
                      doc.at("trigger") ap { TaskTrigger.fromDocument(it) },
                      // Action
                      doc.at("action") ap { TaskAction.fromDocument(it) }
                      )
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun title() : TaskTitle = this.title


    fun description() : TaskDescription = this.description


    fun trigger() : TaskTrigger = this.trigger


    fun action() : TaskAction = this.action


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "title" to this.title().toDocument(),
        "description" to this.description().toDocument()
    ))


}


/**
 * Task Title
 */
data class TaskTitle(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TaskTitle>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TaskTitle> = when (doc)
        {
            is DocText -> effValue(TaskTitle(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}


/**
 * Task Description
 */
data class TaskDescription(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TaskDescription>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TaskDescription> = when (doc)
        {
            is DocText -> effValue(TaskDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}



sealed class TaskAction
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TaskAction>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TaskAction> =
            when (doc.case()) {
                "task_action_toggle_variables" -> TaskActionToggleVariables.fromDocument(doc) as ValueParser<TaskAction>
                else                           -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


}


/**
 * Task Action: Toggle Booleans
 */
data class TaskActionToggleVariables(val variableIds : List<VariableId>)
                                     : TaskAction(), ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TaskActionToggleVariables>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TaskActionToggleVariables> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::TaskActionToggleVariables,
                      // Title
                      doc.list("variable_ids") ap {
                          it.map { VariableId.fromDocument(it) } }
                      )
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun variableIds() : List<VariableId> = this.variableIds


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
    ))


}



sealed class TaskTrigger
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TaskTrigger>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TaskTrigger> =
            when (doc.case()) {
                "task_trigger_state" -> TaskTriggerState.fromDocument(doc.nextCase()) as ValueParser<TaskTrigger>
                "task_trigger_and"   -> TaskTriggerAnd.fromDocument(doc.nextCase()) as ValueParser<TaskTrigger>
                "task_trigger_or"    -> TaskTriggerOr.fromDocument(doc.nextCase()) as ValueParser<TaskTrigger>
                else                 -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // ABSTRACT METHODS
    // -----------------------------------------------------------------------------------------

    open fun variableReferences() : List<VariableReference> = listOf()


    abstract fun isActive(entityId : EntityId) : Boolean

}


/**
 * Task Trigger
 */
data class TaskTriggerState(val variableReference : VariableReference,
                            val constraint : Constraint)
                             : TaskTrigger(), ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TaskTriggerState>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TaskTriggerState> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::TaskTriggerState,
                      // Variable Reference
                      doc.at("variable_reference") ap { VariableReference.fromDocument(it) },
                      // Constraint
                      doc.at("constraint") ap { Constraint.fromDocument(it) }
                      )
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun variableReference() : VariableReference = this.variableReference


    fun constraint() : Constraint = this.constraint


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
    ))


    // -----------------------------------------------------------------------------------------
    // TASK
    // -----------------------------------------------------------------------------------------

    override fun variableReferences() = listOf(this.variableReference)


    override fun isActive(entityId : EntityId) : Boolean
    {
        val engineValue = variable(this.variableReference, entityId)
                            .apply { it.engineValue(entityId) }

        return when (engineValue) {
            is Val -> this.constraint.matchesValue(engineValue.value, entityId)
            is Err -> false
        }
    }

}


/**
 * Task Trigger: And
 */
data class TaskTriggerAnd(val triggers : List<TaskTrigger>)
                        : TaskTrigger(), ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TaskTriggerAnd>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TaskTriggerAnd> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::TaskTriggerAnd,
                      // Triggers
                      doc.list("triggers") ap { it.map { TaskTrigger.fromDocument(it) } }
                      )
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun triggers() : List<TaskTrigger> = this.triggers


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
    ))


    // -----------------------------------------------------------------------------------------
    // TASK
    // -----------------------------------------------------------------------------------------

    override fun variableReferences() =
            this.triggers.fold(listOf<VariableReference>(),
                              { refs, trigger ->  refs.plus(trigger.variableReferences()) })


    override fun isActive(entityId : EntityId) : Boolean
    {
//        this.triggers.forEach {
//            Log.d("****TASK", "is active: ${it.isActive(entityId)}")
//        }

        return this.triggers.map { it.isActive(entityId) }.all { it }
    }

}


/**
 * Task Trigger: Or
 */
data class TaskTriggerOr(val triggers : List<TaskTrigger>)
                        : TaskTrigger(), ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TaskTriggerAnd>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TaskTriggerAnd> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::TaskTriggerAnd,
                      // Triggers
                      doc.list("triggers") ap { it.map { TaskTrigger.fromDocument(it) } }
                      )
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun triggers() : List<TaskTrigger> = this.triggers


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
    ))


    // -----------------------------------------------------------------------------------------
    // TASK
    // -----------------------------------------------------------------------------------------

    override fun variableReferences() =
            this.triggers.fold(listOf<VariableReference>(),
                              { refs, trigger ->  refs.plus(trigger.variableReferences()) })


    override fun isActive(entityId : EntityId) : Boolean =
            this.triggers.map { it.isActive(entityId) }.any()

}

