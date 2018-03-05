
package com.kispoko.tome.model.game.engine.reference


import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.SumType
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.SQLReal
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.model.game.engine.summation.term.TermComponent
import com.kispoko.tome.model.game.engine.value.ValueReference
import com.kispoko.tome.model.game.engine.variable.VariableReference
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.numberVariables
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
