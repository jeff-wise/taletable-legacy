
package com.kispoko.tome.model.game.engine.reference


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
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



/**
 * Text Reference
 */
sealed class TextReference
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TextReference> =
            when (doc.case)
            {
                "literal"  -> TextReferenceLiteral.fromDocument(doc)
                "value"    -> TextReferenceValue.fromDocument(doc)
                "variable" -> TextReferenceVariable.fromDocument(doc)
                else       -> effError<ValueError,TextReference>(
                                        UnknownCase(doc.case, doc.path))
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
        override fun fromDocument(doc : SpecDoc) : ValueParser<TextReference> = when (doc)
        {
            is DocText -> effValue(TextReferenceLiteral(doc.text))
            else       -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLText({ this.value })

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
        override fun fromDocument(doc : SpecDoc) : ValueParser<TextReference> =
                effApply(::TextReferenceValue, ValueReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.valueReference.asSQLValue()

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
        override fun fromDocument(doc : SpecDoc) : ValueParser<TextReference> =
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

}


fun liftBooleanReference(reference : TextReference) : Func<TextReference>
    = when (reference)
    {
        is TextReferenceLiteral  -> Prim(reference, "literal")
        is TextReferenceVariable -> Prim(reference, "variable")
        is TextReferenceValue    -> Prim(reference, "value")
    }


