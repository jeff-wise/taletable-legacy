
package com.kispoko.tome.model.game.engine.reference


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.model.game.engine.variable.*
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser



/**
 * Boolean Reference
 */
sealed class BooleanReference
{

    companion object : Factory<BooleanReference>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<BooleanReference> =
            when (doc.case)
            {
                "literal"  -> BooleanReferenceLiteral.fromDocument(doc)
                "variable" -> BooleanReferenceVariable.fromDocument(doc)
                else                 -> effError<ValueError,BooleanReference>(
                                            UnknownCase(doc.case, doc.path))
            }
    }


    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    open fun dependencies(): Set<VariableReference> = setOf()

}


/**
 * Literal Boolean Reference
 */
data class BooleanReferenceLiteral(val value : Boolean) : BooleanReference()
{

    companion object : Factory<BooleanReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<BooleanReference> = when (doc)
        {
            is DocBoolean -> effValue(BooleanReferenceLiteral(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


}


/**
 * Variable Boolean Reference
 */
data class BooleanReferenceVariable(val variableReference : VariableReference) : BooleanReference()
{

    companion object : Factory<BooleanReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<BooleanReference> =
                effApply(::BooleanReferenceVariable, VariableReference.fromDocument(doc))
    }


    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(): Set<VariableReference> = setOf(variableReference)

}



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

