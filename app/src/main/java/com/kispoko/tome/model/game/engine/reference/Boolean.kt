
package com.kispoko.tome.model.game.engine.reference


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.SumType
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.model.game.engine.variable.*
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Boolean Reference
 */
sealed class BooleanReference : ToDocument, SumType, Serializable
{

    companion object : Factory<BooleanReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BooleanReference> =
            when (doc.case())
            {
                "boolean_literal"    -> BooleanReferenceLiteral.fromDocument(doc.nextCase())
                "variable_reference" -> BooleanReferenceVariable.fromDocument(doc.nextCase())
                else                 -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    open fun dependencies(): Set<VariableReference> = setOf()

}


/**
 * Literal Boolean Reference
 */
data class BooleanReferenceLiteral(val value : Boolean) : BooleanReference(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BooleanReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BooleanReference> = when (doc)
        {
            is DocBoolean -> effValue(BooleanReferenceLiteral(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocBoolean(this.value).withCase("literal")


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "literal"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLInt({ if (this.value) 1 else 0 })

}


/**
 * Variable Boolean Reference
 */
data class BooleanReferenceVariable(val variableReference : VariableReference)
            : BooleanReference(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BooleanReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BooleanReference> =
                effApply(::BooleanReferenceVariable, VariableReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.variableReference.toDocument()
                                    .withCase("partVariable")


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
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

}
