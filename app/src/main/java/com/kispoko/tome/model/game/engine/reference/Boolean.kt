
package com.kispoko.tome.model.game.engine.reference


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.SumModel
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.model.game.engine.variable.*
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
 * Boolean Reference
 */
sealed class BooleanReference : ToDocument, SumModel, Serializable
{

    companion object : Factory<BooleanReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BooleanReference> =
            when (doc.case())
            {
                "literal"  -> BooleanReferenceLiteral.fromDocument(doc)
                "variable" -> BooleanReferenceVariable.fromDocument(doc)
                else                 -> effError<ValueError,BooleanReference>(
                                            UnknownCase(doc.case(), doc.path))
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

    override fun functor() : Func<BooleanReference> = Prim(this, "literal")

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
                                    .withCase("variable")


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() : Func<BooleanReference> = Prim(this, "variable")

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

//
//
//fun liftBooleanReference(reference : BooleanReference) : Func<BooleanReference>
//    = when (reference)
//    {
//        is BooleanReferenceLiteral  -> Prim(reference, "literal")
//        is BooleanReferenceVariable -> Prim(reference, "variable")
//    }
//



//
//    /**
//     * Get the value of the boolean term. It may be a static boolean value, or the value of a
//     * boolean variable.
//     * @return The boolean value.
//     * @throws VariableException
//     */
//    public Boolean value()
//           throws VariableException
//    {
//        switch (this.kind.getValue())
//        {
//            case LITERAL:
//                return this.booleanValue.getValue();
//            case VARIABLE:
//                return this.variableValue(this.variableName.getValue());
//        }
//
//        return null;
//    }
//
//
//    /**
//     * Get the name of the boolean variable of the term. If the term is not a variable, then
//     * null is returned.
//     * @return The variable name, or null.
//     */
//    public VariableReference variableDependency()
//    {
//        switch (this.kind.getValue())
//        {
//            case LITERAL:
//                return null;
//            case VARIABLE:
//                return VariableReference.asByName(this.variableName());
//        }
//
//        return null;
//    }
//
//
//    /**
//     * Get the value of the term's boolean variable.
//     * @param variableName The boolean variable name.
//     * @return The boolean variable's value.
//     */
//    private Boolean variableValue(String variableName)
//            throws VariableException
//    {
//        // > If variable does not exist, throw exception
//        if (!State.hasVariable(variableName)) {
//            throw VariableException.undefinedVariable(
//                    new UndefinedVariableError(variableName));
//        }
//
//        // [1] Get the variable
//        VariableUnion variableUnion = State.variableWithName(variableName);
//
//        // > If variable is not a number, throw exception
//        if (!variableUnion.type().equals(VariableType.BOOLEAN)) {
//            throw VariableException.unexpectedVariableType(
//                    new UnexpectedVariableTypeError(variableName,
//                                                    VariableType.BOOLEAN,
//                                                    variableUnion.type()));
//        }
//
//        return variableUnion.booleanVariable().value();
//    }
//

