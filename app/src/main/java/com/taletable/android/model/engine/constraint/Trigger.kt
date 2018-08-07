
package com.taletable.android.model.engine.constraint


import com.taletable.android.lib.Factory
import com.taletable.android.model.engine.variable.VariableReference
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.variable
import effect.Err
import effect.Val
import effect.effError
import lulo.document.*
import lulo.value.UnknownCase
import lulo.value.ValueParser
import java.io.Serializable



sealed class Trigger : Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Trigger>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Trigger> =
            when (doc.case()) {
                "trigger_state" -> TaskTriggerState.fromDocument(doc.nextCase()) as ValueParser<Trigger>
                "trigger_and"   -> TaskTriggerAnd.fromDocument(doc.nextCase()) as ValueParser<Trigger>
                "trigger_or"    -> TaskTriggerOr.fromDocument(doc.nextCase()) as ValueParser<Trigger>
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
                             : Trigger(), ToDocument
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
data class TaskTriggerAnd(val triggers : List<Trigger>)
                        : Trigger(), ToDocument
{

//    private val serialVersionUID = 6529685098267757690L

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
                      doc.list("triggers") ap { it.map { Trigger.fromDocument(it) } }
                      )
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun triggers() : List<Trigger> = this.triggers


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
data class TaskTriggerOr(val triggers : List<Trigger>)
                        : Trigger(), ToDocument
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TaskTriggerOr>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TaskTriggerOr> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::TaskTriggerOr,
                      // Triggers
                      doc.list("triggers") ap { it.map { Trigger.fromDocument(it) } }
                      )
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun triggers() : List<Trigger> = this.triggers


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
