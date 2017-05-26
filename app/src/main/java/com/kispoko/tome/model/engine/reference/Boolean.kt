
package com.kispoko.tome.model.engine.reference


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.engine.variable.VariableReference
import effect.Err
import effect.effApply
import effect.effApply2
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Boolean Reference
 */
sealed class BooleanReference : Model
{

    companion object : Factory<BooleanReference>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<BooleanReference> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "literal"  -> BooleanReferenceLiteral.fromDocument(doc)
                    "variable" -> BooleanReferenceVariable.fromDocument(doc)
                    else       -> Err<ValueError, DocPath,BooleanReference>(
                                            UnknownCase(doc.case()), doc.path)
                }
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Literal Boolean Reference
 */
data class BooleanReferenceLiteral(override val id : UUID,
                                   val value : Func<Boolean>) : BooleanReference()
{

    companion object : Factory<BooleanReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<BooleanReference> = when (doc)
        {
            is DocDict -> effApply2(::BooleanReferenceLiteral,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Value
                                    effApply(::Prim, doc.boolean("value")))
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Variable Boolean Reference
 */
data class BooleanReferenceVariable(
                            override val id : UUID,
                            val variableReference : Func<VariableReference>) : BooleanReference()
{

    companion object : Factory<BooleanReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<BooleanReference> = when (doc)
        {
            is DocDict -> effApply2(::BooleanReferenceVariable,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Value
                                    doc.at("reference") ap {
                                        effApply(::Comp, VariableReference.fromDocument(it ))
                                    })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

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

