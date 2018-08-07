
package com.taletable.android.model.engine.task


import com.taletable.android.lib.Factory
import com.taletable.android.model.engine.constraint.Trigger
import com.taletable.android.model.engine.variable.VariableId
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Task
 */
data class Task(val id : UUID,
                val title : TaskTitle,
                val description : TaskDescription,
                val actionName : TaskActionName,
                val actionDescription : TaskActionDescription,
                val trigger : Trigger,
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
                apply(::Task,
                      // Id
                      effValue(UUID.randomUUID()),
                      // Title
                      doc.at("title") ap { TaskTitle.fromDocument(it) },
                      // Description
                      doc.at("description") ap { TaskDescription.fromDocument(it) },
                      // Action Name
                      split(doc.maybeAt("action_name"),
                            effValue(TaskActionName("")),
                            { TaskActionName.fromDocument(it) }),
                      // Action Description
                      split(doc.maybeAt("action_description"),
                            effValue(TaskActionDescription("")),
                            { TaskActionDescription.fromDocument(it) }),
                      // Trigger
                      doc.at("trigger") ap { Trigger.fromDocument(it) },
//                      effValue(TaskTriggerOr(listOf())),
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

    fun id() : UUID = this.id


    fun title() : TaskTitle = this.title


    fun description() : TaskDescription = this.description


    fun actionName() : TaskActionName = this.actionName


    fun actionDescription() : TaskActionDescription = this.actionDescription


    fun trigger() : Trigger = this.trigger


    fun action() : TaskAction = this.action


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "title" to this.title().toDocument(),
        "description" to this.description().toDocument()
    ))


    override fun equals(other : Any?) : Boolean
    {
        if (this === other) return true

        if (other == null || javaClass != other.javaClass) return false

        val that = other as Task
        return this.id == that.id()
    }


    override fun hashCode() : Int {
        return Objects.hash(id)
    }

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


/**
 * Task Action Name
 */
data class TaskActionName(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TaskActionName>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TaskActionName> = when (doc)
        {
            is DocText -> effValue(TaskActionName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}



/**
 * Task Action Description
 */
data class TaskActionDescription(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TaskActionDescription>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TaskActionDescription> = when (doc)
        {
            is DocText -> effValue(TaskActionDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}


sealed class TaskAction : Serializable
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
                                     : TaskAction(), ToDocument
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



