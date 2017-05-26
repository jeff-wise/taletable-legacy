
package com.kispoko.tome.model.engine.summation.term


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.engine.reference.BooleanReference
import com.kispoko.tome.model.engine.reference.DiceRollReference
import com.kispoko.tome.model.engine.reference.NumberReference
import effect.Err
import effect.effApply
import effect.effApply2
import effect.effApply4
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.util.*



/**
 * Summation Term
 */
sealed class SummationTerm : Model
{

    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<SummationTerm> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "integer"     -> SummationNumberTerm.fromDocument(doc)
                    "dice"        -> SummationDiceRollTerm.fromDocument(doc)
                    "conditional" -> SummationConditionalTerm.fromDocument(doc)
                    else          -> Err<ValueError, DocPath,SummationTerm>(
                                            UnknownCase(doc.case()), doc.path)
                }
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


data class SummationNumberTerm(override val id : UUID,
                               val numberReference : Func<NumberReference>) : SummationTerm()
{

    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<SummationTerm> = when (doc)
        {
            is DocDict -> effApply2(::SummationNumberTerm,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Value
                                    doc.at("reference") ap {
                                        effApply(::Comp, NumberReference.fromDocument(it))
                                    })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


data class SummationDiceRollTerm(override val id : UUID,
                                 val diceRollReference: Func<DiceRollReference>) : SummationTerm()
{

    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<SummationTerm> = when (doc)
        {
            is DocDict -> effApply2(::SummationDiceRollTerm,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Value
                                    doc.at("reference") ap {
                                        effApply(::Comp, DiceRollReference.fromDocument(it))
                                    })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


data class SummationConditionalTerm(
                            override val id : UUID,
                            val conditionalValueReference: Func<BooleanReference>,
                            val trueValueReference : Func<NumberReference>,
                            val falseValueReference: Func<NumberReference>) : SummationTerm()
{

    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<SummationTerm> = when (doc)
        {
            is DocDict -> effApply4(::SummationConditionalTerm,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Conditional
                                    doc.at("conditional") ap {
                                        effApply(::Comp, BooleanReference.fromDocument(it))
                                    },
                                    // When True
                                    doc.at("when_true") ap {
                                        effApply(::Comp, NumberReference.fromDocument(it))
                                    },
                                    // When False
                                    doc.at("when_false") ap {
                                        effApply(::Comp, NumberReference.fromDocument(it))
                                    })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}



//
//public abstract class Term extends Model
//{
//
//    // INTERFACE
//    // ------------------------------------------------------------------------------------------
//
//    public abstract Integer value() throws SummationException;
//
//    public abstract List<VariableReference> variableDependencies();
//
//    public abstract com.kispoko.tome.engine.definition.summation.term.TermSummary summary() throws VariableException;
//}

//
//
//
//    public IntegerTermValue.Type termValueType()
//    {
//        return this.termValue().type();
//    }
//
//
//    // > Term
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the term value. The returned value is just the value of the referenced variable.
//     * @return The term value. Throws SummationException if the variable is invalid.
//     */
//    public Integer value()
//           throws SummationException
//    {
//        try {
//            return termValue().value();
//        }
//        catch (VariableException exception) {
//            throw SummationException.variable(new SummationVariableError(exception));
//        }
//    }
//
//
//    /**
//     * Get the variables that this term depends upon to calculate its value.
//     * @return A list of variable names.
//     */
//    public List<VariableReference> variableDependencies()
//    {
//        List<VariableReference> variableReferences = new ArrayList<>();
//
//        VariableReference variableReference = this.termValue().variableDependency();
//
//        if (variableReference != null)
//            variableReferences.add(variableReference);
//
//        return variableReferences;
//    }
//
//
//    // > Summary
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * A summary of the terms variables.
//     * @return The list of 2-tuples (value, description) of each of the term's variables.
//     */
//    public com.kispoko.tome.engine.definition.summation.term.TermSummary summary()
//    {
//        // > Convert the component integers to strings
//        List<Tuple2<String, Integer>> components = this.termValue().components();
//        List<Tuple2<String,String>> componentsWithStringValue = new ArrayList<>();
//
//        for (Tuple2<String,Integer> component : components)
//        {
//            Tuple2<String,String> comp = new Tuple2<>(component.getItem1(),
//                                                      component.getItem2().toString());
//            componentsWithStringValue.add(comp);
//        }
//
//        return new com.kispoko.tome.engine.definition.summation.term.TermSummary(this.name(), componentsWithStringValue);
//    }
//
//
//
//    // > Term
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the term value. The returned value is just the value of the referenced variable.
//     *
//     * @return The term value. Throws SummationException if the variable is invalid.
//     */
//    public Integer value()
//            throws SummationException
//    {
//        try
//        {
//            Integer value = this.termValue().value();
//
//            if (value == null)
//                throw SummationException.nullTerm(new NullTermError(this.termValue().name()));
//
//            return value;
//        }
//        catch (VariableException exception)
//        {
//            throw SummationException.variable(
//                    new SummationVariableError(exception));
//        }
//    }
//
//
//    /**
//     * Get the variables that this term depends upon to calculate its value.
//     *
//     * @return A list of variable names.
//     */
//    public List<VariableReference> variableDependencies()
//    {
//        List<VariableReference> variableReferences = new ArrayList<>();
//
//        if (this.termValue().type() == DiceRollTermValue.Type.VARIABLE) {
//            VariableReference variableReference = this.termValue().variableReference();
//            if (variableReference != null)
//                variableReferences.add(variableReference);
//        }
//
//        return variableReferences;
//    }
//
//
//    public com.kispoko.tome.engine.definition.summation.term.TermSummary summary()
//           throws VariableException
//    {
//        return new com.kispoko.tome.engine.definition.summation.term.TermSummary(this.termValue().name(), this.termValue().components());
//    }
//
//
//    public String valueName()
//    {
//        if (this.termValue() != null)
//            return this.termValue().name();
//
//        return "";
//    }
//
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The Dice Roll Term Value.
//     * @return The Dice Roll Term Value.
//     */
//    public DiceRollTermValue termValue()
//    {
//        return this.termValue.getValue();
//    }
//
//
//    public DiceRollTermValue.Type termValueType()
//    {
//        if (this.termValue() != null)
//            return this.termValue().type();
//        return null;
//    }
//
//
//    /**
//     * Get the Dice Roll value.
//     * @return The Dice Roll.
//     */
//    public DiceRoll diceRoll()
//           throws SummationException
//    {
//        try
//        {
//            return this.termValue().diceRoll();
//        }
//        catch (VariableException exception)
//        {
//            throw SummationException.variable(new SummationVariableError(exception));
//        }
//    }
//
//
//    // > Term
//    // ------------------------------------------------------------------------------------------
//
//    public com.kispoko.tome.engine.definition.summation.term.TermSummary summary()
//    {
//        List<Tuple2<String,Integer>> components = new ArrayList<>();
//
//        try {
//            if (conditionalTermValue().value())
//                components = whenTrueTermValue().components();
//            else
//                components = whenFalseTermValue().components();
//        }
//        catch (VariableException exception) {
//            ApplicationFailure.variable(exception);
//        }
//
//        // > Convert the component integers to strings
//        List<Tuple2<String,String>> componentsWithStringValue = new ArrayList<>();
//
//        for (Tuple2<String,Integer> component : components)
//        {
//            Tuple2<String,String> comp = new Tuple2<>(component.getItem1(),
//                                                      component.getItem2().toString());
//            componentsWithStringValue.add(comp);
//        }
//
//
//        return new com.kispoko.tome.engine.definition.summation.term.TermSummary(this.name(), componentsWithStringValue);
//    }
//
//
//    // > Value
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the value of the conditional term. If the condition variable is true
//     * @return
//     * @throws com.kispoko.tome.engine.definition.variable.VariableException
//     */
//    public Integer value()
//           throws SummationException
//    {
//        try
//        {
//            Boolean cond = conditionalTermValue().value();
//
//            if (cond) {
//                return whenTrueTermValue().value();
//            }
//            else {
//                return whenFalseTermValue().value();
//            }
//        }
//        catch (VariableException exception)
//        {
//            throw SummationException.variable(new SummationVariableError(exception));
//        }
//
//    }
//
//
//    /**
//     * Get the variables that this term depends upon to calculate its value.
//     * @return A list of variable names.
//     */
//    public List<VariableReference> variableDependencies()
//    {
//        List<VariableReference> variableReferences = new ArrayList<>();
//
//        VariableReference conditionalVariableRef = this.conditionalTermValue().variableDependency();
//        VariableReference whenTrueVariableRef    = this.whenTrueTermValue().variableDependency();
//        VariableReference whenFalseVariableRef   = this.whenFalseTermValue().variableDependency();
//
//        if (conditionalVariableRef != null)
//            variableReferences.add(conditionalVariableRef);
//
//        if (whenFalseVariableRef != null)
//            variableReferences.add(whenFalseVariableRef);
//
//        if (whenTrueVariableRef != null)
//            variableReferences.add(whenTrueVariableRef);
//
//        return variableReferences;
//    }
//
