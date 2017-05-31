
package com.kispoko.tome.model.game.engine.reference


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.value.ValueReference
import com.kispoko.tome.model.game.engine.variable.VariableReference
import effect.Err
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.util.*



/**
 * Number Reference
 */
sealed class NumberReference : Model
{

    companion object : Factory<NumberReference>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<NumberReference> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "literal"  -> NumberReferenceLiteral.fromDocument(doc)
                    "variable" -> NumberReferenceVariable.fromDocument(doc)
                    "value"    -> NumberReferenceValue.fromDocument(doc)
                    else       -> effError<ValueError,NumberReference>(
                                            UnknownCase(doc.case(), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * Literal Number Reference
 */
data class NumberReferenceLiteral(override val id : UUID,
                                  val value : Func<Double>) : NumberReference()
{

    companion object : Factory<NumberReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberReference> = when (doc)
        {
            is DocDict -> effApply(::NumberReferenceLiteral,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Value
                                   effApply(::Prim, doc.double("value")))
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * Variable Number Reference
 */
data class NumberReferenceVariable(
                            override val id : UUID,
                            val variableReference : Func<VariableReference>) : NumberReference()
{

    companion object : Factory<NumberReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberReference> = when (doc)
        {
            is DocDict -> effApply(::NumberReferenceVariable,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Value
                                   doc.at("reference") ap {
                                       effApply(::Comp, VariableReference.fromDocument(it ))
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * Value Number Reference
 */
data class NumberReferenceValue(
                            override val id : UUID,
                            val valueReference : Func<ValueReference>) : NumberReference()
{

    companion object : Factory<NumberReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberReference> = when (doc)
        {
            is DocDict -> effApply(::NumberReferenceValue,
                                   // Model Id
                                    effValue(UUID.randomUUID()),
                                   // Value
                                   doc.at("reference") ap {
                                       effApply(::Comp, ValueReference.fromDocument(it ))
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


//
//
//    // ** Name
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The term value name (if a literal value). Variable's should have their own name.
//     * @return The literal value name.
//     */
//    public String name()
//    {
//        switch (this.type())
//        {
//            case LITERAL:
//                return this.name.getValue();
//            case VARIABLE:
//                return this.variableReference().variable().variable().label();
//        }
//
//        return "";
//    }
//
//
//    // > Value
//    // ------------------------------------------------------------------------------------------
//
//
//    /**
//     * Get the value of the integer term. It is either a literal integer, or the value of an
//     * integer variable.
//     * @return The integer value.
//     * @throws VariableException
//     */
//    public Integer value()
//           throws VariableException
//    {
//        switch (this.type.getValue())
//        {
//            case LITERAL:
//                return this.integerValue.getValue();
//            case VARIABLE:
//                return this.variableValue(this.variableReference());
//        }
//
//        return null;
//    }
//
//
//    /**
//     * Get the name of the integer variable of the term. If the term is not a variable, then
//     * null is returned.
//     * @return The variable name, or null.
//     */
//    public VariableReference variableDependency()
//    {
//        switch (this.type.getValue())
//        {
//            case LITERAL:
//                return null;
//            case VARIABLE:
//                return this.variableReference();
//        }
//
//        return null;
//    }
//
//
//    // > Components
//    // ------------------------------------------------------------------------------------------
//
//    public List<Tuple2<String,Integer>> components()
//    {
//        switch (this.type())
//        {
//            case LITERAL:
//                List<Tuple2<String, Integer>> components = new ArrayList<>();
//                String name = this.name() != null ? this.name() : "";
//                components.add(new Tuple2<>(name, this.literal()));
//                return components;
//            case VARIABLE:
//                return this.variableSummaries();
//        }
//
//        return new ArrayList<>();
//    }
//
//
//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    private Integer variableValue(VariableReference variableReference)
//            throws VariableException
//    {
//        Integer total = 0;
//
//        for (VariableUnion variableUnion : variableReference.variables())
//        {
//            // [1] If variable is not a number, throw exception
//            // ----------------------------------------------------------------------------------
//            if (variableUnion.type() != VariableType.NUMBER) {
//                throw VariableException.unexpectedVariableType(
//                        new UnexpectedVariableTypeError(variableUnion.variable().name(),
//                                                        VariableType.NUMBER,
//                                                        variableUnion.type()));
//            }
//
//            // [2] Add the variable's value to the sum.
//            // ----------------------------------------------------------------------------------
//
//
//            Integer variableValue = 0;
//            try {
//                variableValue = variableUnion.numberVariable().value();
//            }
//            catch (NullVariableException exception) {
//                ApplicationFailure.nullVariable(exception);
//            }
//
//            total += variableValue;
//        }
//
//        return total;
//    }
//
//
//    private List<Tuple2<String,Integer>> variableSummaries()
//    {
//        List<Tuple2<String,Integer>> summaries = new ArrayList<>();
//
//        for (VariableUnion variableUnion : this.variableReference().variables())
//        {
//            // [1] If variable is not a number, throw exception
//            // ----------------------------------------------------------------------------------
//            if (variableUnion.type() != VariableType.NUMBER) {
//                ApplicationFailure.variable(
//                        VariableException.unexpectedVariableType(
//                                new UnexpectedVariableTypeError(variableUnion.variable().name(),
//                                        VariableType.NUMBER,
//                                        variableUnion.type())));
//                continue;
//            }
//
//            NumberVariable variable = variableUnion.numberVariable();
//
//            try {
//                Integer value = variable.value();
//                summaries.add(new Tuple2<>(variable.label(), value));
//            }
//            catch (NullVariableException exception) {
//                ApplicationFailure.nullVariable(exception);
//            }
//        }
//
//        // > Sort the summaries
//        Collections.sort(summaries, new Comparator<Tuple2<String,Integer>>()
//        {
//            @Override
//            public int compare(Tuple2<String,Integer> summary1, Tuple2<String,Integer> summary2)
//            {
//                int summary1Value = summary1.getItem2();
//                int summary2Value = summary2.getItem2();
//
//                if (summary1Value > summary2Value)
//                    return -1;
//                if (summary2Value < summary1Value)
//                    return 1;
//                return 0;
//            }
//        });
//
//
//        return summaries;
//    }

