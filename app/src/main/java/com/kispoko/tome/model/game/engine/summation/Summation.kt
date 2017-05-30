
package com.kispoko.tome.model.game.engine.summation


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Coll
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.summation.term.SummationTerm
import effect.Err
import effect.effApply
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Summation
 */
data class Summation(override val id : UUID,
                     val terms : Coll<SummationTerm>) : Model
{

    companion object : Factory<Summation>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<Summation> = when (doc)
        {
            is DocDict -> effApply(::Summation,
                                   // Model Id
                                   valueResult(UUID.randomUUID()),
                                   // Terms
                                   doc.list("terms") ap { docList ->
                                       effApply(::Coll,
                                           docList.map { SummationTerm.fromDocument(it) })
                                   })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}

//
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//    // ** Terms
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the terms in the summation.
//     * @return The List of Terms.
//     */
//    public List<TermUnion> terms()
//    {
//        return this.terms.getValue();
//    }
//
//
//    public List<TermUnion> termsSorted()
//    {
//        List<TermUnion> termsList = new ArrayList<>(this.terms());
//
//        Collections.sort(termsList, new Comparator<TermUnion>()
//        {
//            @Override
//            public int compare(TermUnion term1, TermUnion term2)
//            {
//                int term1Value;
//                int term2Value;
//
//                try {
//                    term1Value = term1.term().value();
//                    term2Value = term2.term().value();
//                }
//                catch (SummationException exception) {
//                    return 0;
//                }
//
//                if (term1Value > term2Value)
//                    return -1;
//                if (term1Value < term2Value)
//                    return 1;
//                return 0;
//            }
//        });
//
//        return termsList;
//    }
//
//
//    // ** Dependencies
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the names of all of the variables that the summation depends on to calculate its value.
//     *
//     * @return A list of variable names.
//     */
//    public List<VariableReference> variableDependencies()
//    {
//        List<VariableReference> variableReferences = new ArrayList<>();
//
//        for (TermUnion termUnion : this.terms.getValue()) {
//            variableReferences.addAll(termUnion.term().variableDependencies());
//        }
//
//        return variableReferences;
//    }
//
//
//    // ** To String
//    // ------------------------------------------------------------------------------------------
//
//    @Override
//    public String toString()
//    {
//        return "";
//    }
//
//
//    // > Summary
//    // ------------------------------------------------------------------------------------------
//
//    public List<TermSummary> summary()
//    {
//        List<TermSummary> summaries = new ArrayList<>();
//
//        for (TermUnion termUnion : this.terms())
//        {
//            try {
//                summaries.add(termUnion.term().summary());
//            }
//            catch (VariableException exception) {
//                continue;
//            }
//        }
//
//        return summaries;
//    }
//
//
//    // > Dice Roll
//    // ------------------------------------------------------------------------------------------
//
//    public DiceRoll diceRoll()
//    {
//        if (this.diceRoll == null)
//            this.initializeDiceRoll();
//
//        return this.diceRoll;
//    }
//
//
//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    private void initializeDiceRoll()
//    {
//        this.diceRoll = null;
//
//        // [1] Check if there is a dice roll in the summation
//        // -------------------------------------------------------------------------------------
//        boolean hasDiceRoll = false;
//        for (TermUnion termUnion : this.terms())
//        {
//            if (termUnion.type() == TermType.DICE_ROLL) {
//                hasDiceRoll = true;
//                break;
//            }
//        }
//
//        if (!hasDiceRoll)
//            return;
//
//
//        // [2] Calculate Dice Roll
//        // -------------------------------------------------------------------------------------
//
//        DiceRoll diceRoll = DiceRoll.empty();
//
//        for (TermUnion termUnion : this.terms())
//        {
//            if (termUnion.type() == TermType.DICE_ROLL)
//            {
//                try {
//                    diceRoll.addDiceRoll(termUnion.diceRollTerm().diceRoll());
//                }
//                catch (SummationException exception) {
//                    ApplicationFailure.summation(exception);
//                }
//            }
//            else if (termUnion.type() == TermType.INTEGER)
//            {
//                IntegerTerm integerTerm = termUnion.integerTerm();
//                try
//                {
//                    RollModifier rollModifier = new RollModifier(integerTerm.value(),
//                                                                 integerTerm.name());
//                    diceRoll.addModifier(rollModifier);
//                }
//                catch (SummationException exception)
//                {
//                    ApplicationFailure.summation(exception);
//                }
//            }
//        }
//
//        this.diceRoll = diceRoll;
//    }
//
//
//    /**
//     * Evaluate the sum of this summation.
//     */
//    private Integer sum()
//    {
//        Integer sum = 0;
//
//        for (TermUnion termUnion : this.terms.getValue())
//        {
//            try {
//                sum += termUnion.term().value();
//            }
//            catch (SummationException exception) {
//                ApplicationFailure.summation(exception);
//            }
//        }
//
//        this.sum = sum;
//
//        return this.sum;
//    }

