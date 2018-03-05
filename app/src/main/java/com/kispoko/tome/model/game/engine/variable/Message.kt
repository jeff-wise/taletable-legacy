
package com.kispoko.tome.model.game.engine.variable


import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.DB_MessageValue
import com.kispoko.tome.db.messageTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue2
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.SQLBlob
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.variable
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*



/**
 * Message
 */
data class Message(override val id : UUID,
                   val template : MessageTemplate,
                   val variables : MessageVariables)
                    : ProdType, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(templateString : MessageTemplate,
                variableIds : List<VariableId>)
        : this(UUID.randomUUID(),
               templateString,
               MessageVariables(variableIds))


    companion object : Factory<Message>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Message> = when (doc)
        {
            is DocDict ->
            {
                apply(::Message,
                      // Template String
                      doc.at("template") ap { MessageTemplate.fromDocument(it) },
                      // Variable Ids
                      split(doc.maybeList("variable_ids"),
                            effValue(listOf()),
                            { it.map { VariableId.fromDocument(it) } })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun template() : MessageTemplate = this.template


    fun variables() : MessageVariables = this.variables


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "template" to DocText(this.template.value),
        "variable_ids" to DocList(this.variables().variables.map  { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_MessageValue =
        RowValue2(messageTable,
                  PrimValue(this.template),
                  PrimValue(this.variables))


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    fun toString(entityId : EntityId) : String
    {
        if (this.variables().variables.isEmpty())
            return this.template().value

        val variableStrings = this.variables().variables.mapM { varId ->
            variable(varId, entityId).apply { it.valueString(entityId) }
        }

        return when (variableStrings)
        {
            is Val -> {
                templateString(variableStrings.value)

            }
            is Err -> {
                ApplicationLog.error(variableStrings.error)
                template().value
            }
        }
    }


    fun templateString(variableStrings : List<String>) : String
    {
        val baseString = this.template().value
        val parts = baseString.split("$$$")

        if ((parts.size - 1) != variableStrings.size)
            return baseString

        return when (parts.size)
        {
            0 -> baseString
            1 -> parts[0]
            else ->
            {
                var stringBuilder = parts[0]

                val partsRest = parts.takeLast(parts.size - 1)

                partsRest.forEachIndexed { index, s ->
                    stringBuilder += variableStrings[index]
                    stringBuilder += s
                }

                return stringBuilder
            }
        }
    }


}



/**
 * Message Template
 */
data class MessageTemplate(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MessageTemplate>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<MessageTemplate> = when (doc)
        {
            is DocText -> effValue(MessageTemplate(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLText({this.value})

}


/**
 * Message Variables
 */
data class MessageVariables(val variables : List<VariableId>) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MessageVariables>
    {

        override fun fromDocument(doc : SchemaDoc) : ValueParser<MessageVariables> = when (doc)
        {
            is DocList -> effect.apply(::MessageVariables, doc.map { VariableId.fromDocument(it) })
            else       -> effError(UnexpectedType(DocType.LIST, docType(doc), doc.path))
        }

    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})

}

