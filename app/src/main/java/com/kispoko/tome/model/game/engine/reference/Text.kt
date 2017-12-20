
package com.kispoko.tome.model.game.engine.reference


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.SumType
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.model.game.engine.value.ValueReference
import com.kispoko.tome.model.game.engine.variable.VariableReference
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Text Reference
 */
sealed class TextReference : SumType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextReference> =
            when (doc.case())
            {
                "literal"  -> TextReferenceLiteral.fromDocument(doc)
                "value"    -> TextReferenceValue.fromDocument(doc)
                "variable" -> TextReferenceVariable.fromDocument(doc)
                else       -> effError<ValueError,TextReference>(
                                        UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    open fun dependencies(): Set<VariableReference> = setOf()
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

    override fun dependencies(): Set<VariableReference> = setOf(variableReference)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.variableReference.asSQLValue()


    // -----------------------------------------------------------------------------------------
    // SUM TYPE
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "variable"


    override val sumModelObject = this

}


