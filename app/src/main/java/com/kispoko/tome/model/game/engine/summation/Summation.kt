
package com.kispoko.tome.model.game.engine.summation


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Conj
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.summation.term.SummationTerm
import com.kispoko.tome.model.game.engine.summation.term.TermSummary
import com.kispoko.tome.model.game.engine.variable.VariableReference
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetUIContext
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
import java.io.Serializable
import java.util.*



/**
 * Summation
 */
data class Summation(override val id : UUID,
                     val terms : Conj<SummationTerm>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.terms.name = "terms"
    }


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

    override val name = "summation"

    override val modelObject = this


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


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun summary(sheetUIContext: SheetUIContext) : List<TermSummary> =
            this.terms().mapNotNull { it.summary(SheetContext(sheetUIContext)) }

}



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

