
package com.kispoko.tome.model.game.engine.summation


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Coll
import com.kispoko.tome.lib.functor.Conj
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.summation.term.SummationTerm
import com.kispoko.tome.model.game.engine.variable.VariableReference
import com.kispoko.tome.rts.sheet.SheetContext
import effect.effApply
import effect.effError
import effect.effValue
import effect.sequenceI
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.lang.Double.sum
import java.util.*



/**
 * Summation
 */
data class Summation(override val id : UUID,
                     val terms : Conj<SummationTerm>) : Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(terms : MutableSet<SummationTerm>)
        : this(UUID.randomUUID(), Conj(terms))


    companion object : Factory<Summation>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<Summation> = when (doc)
        {
            is DocDict -> effApply(::Summation,
                                   // Terms
                                   doc.list("terms") ap { docList ->
                                       docList.mapSetMut { SummationTerm.fromDocument(it) }
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun terms() : Set<SummationTerm> = this.terms.set


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    fun dependencies() : Set<VariableReference> =
        this.terms.set.fold(setOf(), {
            accSet, term -> accSet.plus(term.dependencies())
        })


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value(sheetContext : SheetContext) : AppEff<Double> =
            this.terms().map({it.value(sheetContext)}).sequenceI()
                .apply { effValue<AppError,Double>(it.sum()) }

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

