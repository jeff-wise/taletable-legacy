
package com.taletable.android.model.engine.reference


import com.taletable.android.app.ApplicationLog
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.SumType
import com.taletable.android.lib.orm.schema.PrimValue
import com.taletable.android.lib.orm.sql.SQLReal
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.model.engine.summation.term.TermComponent
import com.taletable.android.model.engine.value.ValueReference
import com.taletable.android.model.engine.variable.VariableReference
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.numberVariables
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.io.Serializable



/**
 * Number Reference
 */
sealed class NumberReference : ToDocument, SumType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberReference> =
            when (doc.case())
            {
                "number_literal"     -> NumberReferenceLiteral.fromDocument(doc.nextCase())
                "value_reference"    -> NumberReferenceValue.fromDocument(doc.nextCase())
                "variable_reference" -> NumberReferenceVariable.fromDocument(doc.nextCase())
                else                 -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    open fun dependencies(): Set<VariableReference> = setOf()


    // -----------------------------------------------------------------------------------------
    // COMPONENTS
    // -----------------------------------------------------------------------------------------

    abstract fun components(entityId : EntityId) : List<TermComponent>

}


/**
 * Literal Number Reference
 */
data class NumberReferenceLiteral(val value : Double) : NumberReference(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberReference> = when (doc)
        {
            is DocNumber -> effValue(NumberReferenceLiteral(doc.number))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value).withCase("number_literal")


    // -----------------------------------------------------------------------------------------
    // SUM TYPE
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "literal"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLReal({ this.value })


    // -----------------------------------------------------------------------------------------
    // COMPONENTS
    // -----------------------------------------------------------------------------------------

    override fun components(entityId : EntityId) : List<TermComponent> = listOf()

}



/**
 * Value Number Reference
 */
data class NumberReferenceValue(val valueReference : ValueReference)
            : NumberReference(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberReference> =
                effApply(::NumberReferenceValue, ValueReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.valueReference.toDocument()
                                    .withCase("value_reference")


    // -----------------------------------------------------------------------------------------
    // SUM TYPE
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "value"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.valueReference.asSQLValue()


    // -----------------------------------------------------------------------------------------
    // COMPONENTS
    // -----------------------------------------------------------------------------------------

    override fun components(entityId : EntityId) : List<TermComponent> = listOf()

}


/**
 * Variable Number Reference
 */
data class NumberReferenceVariable(val variableReference : VariableReference)
            : NumberReference(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberReference> =
                effApply(::NumberReferenceVariable, VariableReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.variableReference.toDocument()
                                    .withCase("variable_reference")


    // -----------------------------------------------------------------------------------------
    // SUM TYPE
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "partVariable"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(): Set<VariableReference> = setOf(variableReference)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.variableReference.asSQLValue()


    // -----------------------------------------------------------------------------------------
    // COMPONENTS
    // -----------------------------------------------------------------------------------------

    override fun components(entityId : EntityId) : List<TermComponent>
    {
        val variables = numberVariables(this.variableReference, entityId)

        when (variables)
        {
            is Val ->
            {
                return variables.value.mapNotNull {
                    val valueString = it.valueString(entityId)
                    when (valueString)
                    {
                        is Val -> TermComponent(it.label().value, valueString.value)
                        is Err -> {
                            ApplicationLog.error(valueString.error)
                            null
                        }
                    }
                }
            }
            is Err -> ApplicationLog.error(variables.error)
        }

        return listOf()
    }

}
