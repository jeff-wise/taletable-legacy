
package com.kispoko.tome.model.engine.reference


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.SumType
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.model.engine.program.Invocation
import com.kispoko.tome.model.engine.value.ValueReference
import com.kispoko.tome.model.engine.variable.VariableReference
import com.kispoko.tome.rts.entity.EntityId
import effect.apply
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Text Reference
 */
sealed class TextReference : ToDocument, SumType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextReference> =
            when (doc.case())
            {
                "text_literal"       -> TextReferenceLiteral.fromDocument(doc.nextCase())
                "value_reference"    -> TextReferenceValue.fromDocument(doc.nextCase())
                "variable_reference" -> TextReferenceVariable.fromDocument(doc.nextCase())
                "program_invocation" -> TextReferenceProgram.fromDocument(doc.nextCase())
                else                 -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    open fun dependencies(entityId : EntityId): Set<VariableReference> = setOf()
}


/**
 * Literal Text Reference
 */
data class TextReferenceLiteral(val value : String) : TextReference(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextReference> = when (doc)
        {
            is DocText -> effValue(TextReferenceLiteral(doc.text))
            else       -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value).withCase("text_literal")


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLText({ this.value })


    // -----------------------------------------------------------------------------------------
    // SUM TYPE
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "literal"


    override val sumModelObject = this

}



/**
 * Value Text Reference
 */
data class TextReferenceValue(val valueReference : ValueReference)
            : TextReference(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextReference> =
                effApply(::TextReferenceValue, ValueReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.valueReference.toDocument()
                                    .withCase("value_reference")


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.valueReference.asSQLValue()


    // -----------------------------------------------------------------------------------------
    // SUM TYPE
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "value"


    override val sumModelObject = this

}


/**
 * Variable Text Reference
 */
data class TextReferenceVariable(val variableReference : VariableReference)
            : TextReference(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextReference> =
                effApply(::TextReferenceVariable, VariableReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId) = setOf(variableReference)


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.variableReference.toDocument()
                                    .withCase("variable_reference")


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.variableReference.asSQLValue()


    // -----------------------------------------------------------------------------------------
    // SUM TYPE
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "partVariable"


    override val sumModelObject = this

}


/**
 * Program Text Reference
 */
data class TextReferenceProgram(val invocation : Invocation) : TextReference()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextReference> =
                apply(::TextReferenceProgram, Invocation.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId) : Set<VariableReference> =
            this.invocation.dependencies(entityId)


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.invocation.toDocument().withCase("invocation")


    // -----------------------------------------------------------------------------------------
    // SUM TYPE
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = ProdValue(this.invocation)


    override fun case() = "invocation"


    override val sumModelObject = this


}
