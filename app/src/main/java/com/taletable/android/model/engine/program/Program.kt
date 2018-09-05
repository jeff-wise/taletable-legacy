
package com.taletable.android.model.engine.program


import android.content.Context
import android.text.SpannableStringBuilder
import com.taletable.android.app.AppEff
import com.taletable.android.app.AppEvalError
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.sql.*
import com.taletable.android.model.engine.EngineValue
import com.taletable.android.model.engine.EngineValueType
import com.taletable.android.model.engine.reference.DataReference
import com.taletable.android.model.engine.message.Message
import com.taletable.android.model.engine.variable.VariableReference
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.engine.interpreter.ResultBindingDoesNotExist
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Nothing
import maybe.Maybe
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*



/**
 * Program
 */
data class Program(val programId : ProgramId,
                   val label : ProgramLabel,
                   val description : ProgramDescription,
                   val typeSignature : ProgramTypeSignature,
                   val statements : MutableList<Statement>,
                   val resultBindingName : StatementBindingName,
                   val resultMessage : Maybe<Message>)
                    : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------


    companion object : Factory<Program>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Program> = when (doc)
        {
            is DocDict ->
            {
                apply(::Program,
                      // Program Id
                      doc.at("program_id") ap { ProgramId.fromDocument(it) },
                      // Label
                      doc.at("label") ap { ProgramLabel.fromDocument(it) },
                      // Description
                      doc.at("description") ap { ProgramDescription.fromDocument(it) },
                      // Type Signature
                      doc.at("type_signature") ap { ProgramTypeSignature.fromDocument(it) },
                      // Statements
                      split(doc.maybeList("statements"),
                            effValue(mutableListOf()),
                            { it.mapMut { Statement.fromDocument(it) } }),
                      // Result Binding Name
                      doc.at("result_binding_name") ap { StatementBindingName.fromDocument(it) },
                      // Result Message
                      split(doc.maybeAt("result_message"),
                            effValue<ValueError,Maybe<Message>>(Nothing()),
                            { apply(::Just, Message.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "program_id" to this.programId().toDocument(),
        "label" to this.label().toDocument(),
        "description" to this.description().toDocument(),
        "statements" to DocList(this.statements().map { it.toDocument() }),
        "result_binding_name" to this.resultBindingName().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun programId() : ProgramId = this.programId


    fun label() : ProgramLabel = this.label


    fun labelString() : String = this.label.value


    fun description() : ProgramDescription = this.description


    fun descriptionString() : String = this.description.value


    fun typeSignature() : ProgramTypeSignature = this.typeSignature


    fun statements() : List<Statement> = this.statements


    fun resultBindingName() : StatementBindingName = this.resultBindingName


    fun resultBindingNameString() : String = this.resultBindingName.value


    fun resultMessage() : Maybe<Message> = this.resultMessage

    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    fun dependencies(entityId : EntityId) : Set<VariableReference>
    {
        val deps = mutableSetOf<VariableReference>()

        this.statements().forEach {
            deps.addAll(it.dependencies(entityId))
        }

        return deps
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value(parameters : ProgramParameterValues,
              entityId : EntityId) : AppEff<EngineValue>
    {
        val bindings : MutableMap<String,EngineValue> = mutableMapOf()

        for (statement in this.statements())
        {
            val value = statement.value(parameters,
                                        bindings,
                                        this.programId(),
                                        entityId)
            when (value)
            {
                is Val ->
                {
                    val binding = value.value
                    bindings.put(statement.bindingNameString(), binding)
                }
                is Err -> {
                    return value
                }
            }
        }

        return note(bindings[this.resultBindingNameString()],
                    AppEvalError(ResultBindingDoesNotExist(this.resultBindingNameString(),
                                                           this.programId())))
    }


    fun result(parameters : ProgramParameterValues,
               entityId : EntityId,
               context : Context) : AppEff<ProgramResultSpannable>
    {
        val bindings : MutableMap<String,EngineValue> = mutableMapOf()

        for (statement in this.statements())
        {
            val value = statement.value(parameters,
                                        bindings,
                                        this.programId(),
                                        entityId)
            when (value)
            {
                is Val ->
                {
                    val binding = value.value
                    bindings.put(statement.bindingNameString(), binding)
                }
                is Err -> {
                    return value as AppEff<ProgramResultSpannable>
                }
            }
        }

        var messageSpannable = SpannableStringBuilder("")


        // Build messsage
        when (this.resultMessage)
        {
            is Just ->
            {
                val message = this.resultMessage.value
                val valueStrings = message.parts().mapNotNull { bindings[it.key()]?.toString() }
                messageSpannable = message.templateSpannable(valueStrings, entityId, context)
            }
        }

        val resultValue = bindings[this.resultBindingNameString()]
        return if (resultValue != null)
        {
            effValue(ProgramResultSpannable(resultValue, messageSpannable))
        }
        else
        {
            effError(AppEvalError(ResultBindingDoesNotExist(this.resultBindingNameString(),
                                                            this.programId())))
        }
    }


}


/**
 * Program Id
 */
data class ProgramId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ProgramId> = when (doc)

        {
            is DocText -> effValue(ProgramId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Program Label
 */
data class ProgramLabel(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramLabel>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ProgramLabel> = when (doc)
        {
            is DocText -> effValue(ProgramLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Program Description
 */
data class ProgramDescription(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramDescription>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProgramDescription> = when (doc)
        {
            is DocText -> effValue(ProgramDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}



/**
 * Program Parameters
 */
data class ProgramParameters(val parameters : List<DataReference>)
                : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------
//
//    fun atIndex(index : Int) : Maybe<EngineValue> =
//        if (index < parameters.size)
//            Just(parameters[index])
//        else
//            Nothing()
//

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this) })

}


/**
 * Program Parameters
 */
data class ProgramTypeSignature(val parameters : List<ProgramParameter>,
                                val result : EngineValueType)
                                 : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramTypeSignature>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProgramTypeSignature> = when (doc)
        {
            is DocDict ->
            {
                apply(::ProgramTypeSignature,
                      // Parameters
                      split(doc.maybeList("parameters"),
                            effValue(listOf()),
                            { it.map { ProgramParameter.fromDocument(it) } }),
                      // Result
                      doc.at("result") ap { EngineValueType.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "parameters" to DocList(this.parameters().map { it.toDocument() }),
        "result" to this.result().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun parameters() : List<ProgramParameter> = this.parameters


    fun result() : EngineValueType = this.result

}


/**
 * Program Parameter Values
 */
data class ProgramParameterValues(val parameterMap : Map<String,EngineValue>)
                : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun value(name : String) : Maybe<EngineValue> =
        if (this.parameterMap.containsKey(name))
            Just(this.parameterMap[name]!!)
        else
            Nothing()


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramParameterValues>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProgramParameterValues> = when (doc)
        {
            is DocList ->
            {
                doc.map { ProgramParameterValue.fromDocument(it) } ap {
                    effValue<ValueError,ProgramParameterValues>(ProgramParameterValues(it.map { it.toPair() }.toMap()))
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})

}


/**
 * Program Parameter Value
 */
data class ProgramParameterValue(val name : String, val value : EngineValue) : Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramParameterValue>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProgramParameterValue> = when (doc)
        {
            is DocDict ->
            {
                apply(::ProgramParameterValue,
                      // Name
                      doc.text("name"),
                      // Value
                      doc.at("value") ap { EngineValue.fromDocument(it) })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    fun toPair() : Pair<String,EngineValue> = Pair(this.name, this.value)

}


data class ProgramResult(val value : EngineValue, val message : String)


data class ProgramResultSpannable(val value : EngineValue, val message : SpannableStringBuilder)

