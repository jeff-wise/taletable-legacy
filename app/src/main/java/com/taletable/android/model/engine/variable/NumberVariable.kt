
package com.taletable.android.model.engine.variable


import com.taletable.android.app.AppEff
import com.taletable.android.app.AppError
import com.taletable.android.app.ApplicationLog
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.SumType
import com.taletable.android.lib.orm.schema.PrimValue
import com.taletable.android.lib.orm.schema.ProdValue
import com.taletable.android.lib.orm.sql.SQLReal
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.model.engine.program.Invocation
import com.taletable.android.model.engine.summation.SummationId
import com.taletable.android.model.engine.value.ValueReference
import com.taletable.android.rts.entity.*
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable
import java.util.*



/**
 * Number Variable
 */
sealed class NumberVariableValue : ToDocument, SumType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberVariableValue> =
            when (doc.case())
            {
                "number_literal"     -> NumberVariableLiteralValue.fromDocument(doc)
                "variable_id"        -> NumberVariableVariableValue.fromDocument(doc)
                "program_invocation" -> NumberVariableProgramValue.fromDocument(doc)
                "value_reference"    -> NumberVariableValueValue.fromDocument(doc)
                "summation_id"       -> NumberVariableSummationValue.fromDocument(doc)
                else                 -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    open fun dependencies(entityId : EntityId) : Set<VariableReference> = setOf()


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    abstract fun value(entityId : EntityId) : AppEff<Maybe<Double>>

    abstract fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>>

}


/**
 * Literal Value
 */
data class NumberVariableLiteralValue(val value : Double)
            : NumberVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberVariableValue> = when (doc)
        {
            is DocNumber -> effValue(NumberVariableLiteralValue(doc.number))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value).withCase("number_literal")


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value(entityId : EntityId) : AppEff<Maybe<Double>> =
            effValue(Just(this.value))


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM TYPE
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "literal"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({ this.value })

}


/**
 * Unknown Literal Value
 */
class NumberVariableUnknownLiteralValue() : NumberVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberVariableValue> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "unknown_literal_value" -> effValue<ValueError,NumberVariableValue>(
                                                NumberVariableUnknownLiteralValue())
                else                    -> effError<ValueError,NumberVariableValue>(
                                                UnexpectedValue("NumberVariableUnknownLiteralValue",
                                                                doc.text,
                                                                doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText("unknown")


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value(entityId : EntityId) : AppEff<Maybe<Double>> = effValue(Nothing())


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "unknown_literal"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ "unknown_literal_value" })

}


/**
 * Variable Value
 */
data class NumberVariableVariableValue(val variableId : VariableId)
            : NumberVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberVariableValue> =
                effApply(::NumberVariableVariableValue, VariableId.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.variableId.toDocument().withCase("variable_id")


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId) = setOf(variableId)


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value(entityId : EntityId) : AppEff<Maybe<Double>> =
        numberVariable(variableId, entityId)
            .apply { it.variableValue().value(entityId) }



    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "partVariable"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = this.variableId.asSQLValue()


}


/**
 * Program Value
 */
data class NumberVariableProgramValue(val invocation : Invocation) : NumberVariableValue()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberVariableValue> =
            effApply(::NumberVariableProgramValue, Invocation.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId) = invocation.dependencies(entityId)


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.invocation.toDocument().withCase("program_invocation")


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value(entityId : EntityId) : AppEff<Maybe<Double>> =
        effApply(::Just, this.invocation.numberValue(entityId))


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = ProdValue(this.invocation)


    override fun case() = "program"


    override val sumModelObject = this

}


/**
 * Program Value
 */
data class NumberVariableValueValue(val valueReference : ValueReference)
            : NumberVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberVariableValue> =
                effApply(::NumberVariableValueValue, ValueReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.valueReference.toDocument().withCase("value_reference")


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value(entityId : EntityId) : AppEff<Maybe<Double>> =
        numberValue(valueReference, entityId)
          .apply { effValue<AppError,Maybe<Double>>(Just(it.value())) }


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
        value(this.valueReference, entityId)
          .apply { effValue<AppError,Set<Variable>>(it.variables().toSet()) }


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "value"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = this.valueReference.asSQLValue()

}


/**
 * Summation Value
 */
data class NumberVariableSummationValue(val summationId : SummationId)
            : NumberVariableValue(), SQLSerializable
{

    companion object : Factory<NumberVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberVariableValue> =
                effApply(::NumberVariableSummationValue, SummationId.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.summationId.toDocument().withCase("summation_id")


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId) : Set<VariableReference>
    {
        val deps = summation(summationId, entityId)
                       .apply { effValue<AppError,Set<VariableReference>>(it.dependencies(entityId)) }

        when (deps) {
            is effect.Val -> return deps.value
            is Err -> ApplicationLog.error(deps.error)
        }

        return setOf()
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value(entityId : EntityId) : AppEff<Maybe<Double>> =
        summation(summationId, entityId)
          .apply { effValue<AppError,Double>(it.value(entityId)) }
          .apply { effValue<AppError,Maybe<Double>>(Just(it)) }


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "summationWithId"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = this.summationId.asSQLValue()

}


// ---------------------------------------------------------------------------------------------
// HISTORY
// ---------------------------------------------------------------------------------------------

/**
 * Number Variable History
 */
data class NumberVariableHistory(val id : UUID,
                                 val entries : MutableList<NumberVariableHistoryEntry>)
                                  : Serializable
{


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor() : this(mutableListOf())


    constructor(entries : MutableList<NumberVariableHistoryEntry>)
        : this(UUID.randomUUID(),
               entries)


    companion object : Factory<NumberVariableHistory>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberVariableHistory> = when (doc)
        {
            is DocDict ->
            {
                effApply(::NumberVariableHistory,
                         // Variable Id
                         doc.list("entries") ap {
                             it.mapMut { NumberVariableHistoryEntry.fromDocument(it) }
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun entries() : List<NumberVariableHistoryEntry> = this.entries


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun append(value : NumberVariableValue)
    {
        this.entries.add(NumberVariableHistoryEntry(value, Nothing()))
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------
//
//    override fun onLoad() {}
//
//    override val name = "variable_number_history"
//
//    override val prodTypeObject: ProdType = this
//
//    override fun persistentFunctors() : List<Val<*>> = listOf(entries)

}



/**
 * Number Variable History Entry
 */
data class NumberVariableHistoryEntry(
                            val id : UUID,
                            val value : NumberVariableValue,
                            val description : Maybe<NumberVariableHistoryEntryDescription>)
                             : Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(value : NumberVariableValue,
                description : Maybe<NumberVariableHistoryEntryDescription>)
        : this(UUID.randomUUID(),
               value,
               description)


    companion object : Factory<NumberVariableHistoryEntry>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberVariableHistoryEntry> = when (doc)
        {
            is DocDict ->
            {
                effApply(::NumberVariableHistoryEntry,
                         // Value
                         doc.at("value") ap { NumberVariableValue.fromDocument(it) },
                         // Description
                         split(doc.maybeAt("description"),
                               effValue<ValueError,Maybe<NumberVariableHistoryEntryDescription>>(Nothing()),
                               { effApply(::Just, NumberVariableHistoryEntryDescription.fromDocument(it))  })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun value() : NumberVariableValue = this.value

    fun description() : Maybe<NumberVariableHistoryEntryDescription> = this.description


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------
//
//    override fun onLoad() {}
//
//
//    override val name = "variable_number_history_entry"
//
//
//    override val prodTypeObject: ProdType = this


}


/**
 * Number Variable History Entry Description
 */
data class NumberVariableHistoryEntryDescription(val value : String)
                : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberVariableHistoryEntryDescription>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberVariableHistoryEntryDescription> = when (doc)
        {
            is DocText -> effValue(NumberVariableHistoryEntryDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}

