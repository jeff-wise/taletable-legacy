
package com.kispoko.tome.model.game.engine.variable


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.SumType
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.program.Invocation
import com.kispoko.tome.model.game.engine.value.ValueReference
import com.kispoko.tome.model.game.engine.value.ValueSetId
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.textValue
import com.kispoko.tome.rts.entity.value
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



/**
 * Text Variable Value
 */
sealed class TextVariableValue : ToDocument, SumType, Serializable
{

    companion object : Factory<TextVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextVariableValue> =
            when (doc.case())
            {
                "text_literal"       -> TextVariableLiteralValue.fromDocument(doc)
                "value_reference"    -> TextVariableValueValue.fromDocument(doc)
                "program_invocation" -> TextVariableProgramValue.fromDocument(doc)
                "value_set_id"       -> TextVariableValueUnknownValue.fromDocument(doc)
                else                 -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------

    open fun dependencies(entityId : EntityId) : Set<VariableReference> = setOf()


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    abstract fun value(entityId : EntityId) : AppEff<Maybe<String>>


    abstract fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>>

}


/**
 * Literal Value
 */
data class TextVariableLiteralValue(val value : String) : TextVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextVariableValue> = when (doc)
        {
            is DocText -> effValue(TextVariableLiteralValue(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value).withCase("text_literal")


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    override fun value(entityId : EntityId) : AppEff<Maybe<String>> =
            effValue(Just(this.value))


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
        effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "literal"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Unknown Literal Value
 */
class TextVariableUnknownLiteralValue() : TextVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextVariableValue> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "unknown_literal_value" -> effValue<ValueError,TextVariableValue>(
                                                TextVariableUnknownLiteralValue())
                else                    -> effError<ValueError,TextVariableValue>(
                                                UnexpectedValue("TextVariableUnknownLiteralValue",
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
    // Value
    // -----------------------------------------------------------------------------------------

    override fun value(entityId : EntityId) : AppEff<Maybe<String>> = effValue(Nothing())


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


data class TextVariableValueValue(val valueReference : ValueReference)
            : TextVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextVariableValue> =
                effApply(::TextVariableValueValue, ValueReference.fromDocument(doc))
    }



    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.valueReference.toDocument().withCase("value_reference")


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId) : Set<VariableReference> =
            this.valueReference.dependencies(entityId)


    override fun value(entityId : EntityId) : AppEff<Maybe<String>> =
        textValue(this.valueReference, entityId)
          .apply { effValue<AppError,Maybe<String>>(Just(it.value())) }


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


data class TextVariableValueUnknownValue(val valueSetId : ValueSetId)
            : TextVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextVariableValue> =
                effApply(::TextVariableValueUnknownValue, ValueSetId.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.valueSetId.toDocument().withCase("value_set_id")


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    override fun value(entityId : EntityId) : AppEff<Maybe<String>> = effValue(Nothing())


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
        effValue(setOf())



    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "unknown_value"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = this.valueSetId.asSQLValue()

}


data class TextVariableProgramValue(val invocation : Invocation) : TextVariableValue()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextVariableValue> =
                effApply(::TextVariableProgramValue, Invocation.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.invocation.toDocument().withCase("program_invocation")


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId) : Set<VariableReference> =
            this.invocation.dependencies(entityId)


    override fun value(entityId : EntityId) = TODO("Not Implemented")


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = ProdValue(this.invocation)


    override fun case() = "program"


    override val sumModelObject = this

}

