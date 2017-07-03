
package com.kispoko.tome.model.game.engine.summation.term


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.functor.Sum
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.reference.*
import com.kispoko.tome.model.game.engine.variable.VariableReference
import com.kispoko.tome.model.sheet.Sheet
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetData
import com.kispoko.tome.rts.sheet.SheetManager
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.util.*



/**
 * Summation Term
 */
sealed class SummationTerm
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

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
                    else          -> effError<ValueError,SummationTerm>(
                                            UnknownCase(doc.case(), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TERM
    // -----------------------------------------------------------------------------------------

    abstract fun dependencies(): Set<VariableReference>

    abstract fun value(sheetContext : SheetContext) : AppEff<Double>

}


data class SummationNumberTerm(val numberReference : NumberReference) : SummationTerm()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<SummationTerm> = when (doc)
        {
            is DocDict -> effApply(::SummationNumberTerm,
                                   // Value
                                   doc.at("reference") ap { NumberReference.fromDocument(it) })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TERM
    // -----------------------------------------------------------------------------------------

    override fun dependencies(): Set<VariableReference> = this.numberReference.dependencies()


    override fun value(sheetContext : SheetContext) : AppEff<Double> =
        SheetData.number(sheetContext, numberReference)

}


data class SummationDiceRollTerm(val diceRollReference: DiceRollReference) : SummationTerm()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<SummationTerm> = when (doc)
        {
            is DocDict -> effApply(::SummationDiceRollTerm,
                                   // Value
                                   doc.at("reference") ap { DiceRollReference.fromDocument(it) })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TERM
    // -----------------------------------------------------------------------------------------

    override fun dependencies(): Set<VariableReference> =
            this.diceRollReference.dependencies()


    override fun value(sheetContext : SheetContext) : AppEff<Double> =
            SheetData.diceRoll(sheetContext, diceRollReference)
                    .apply { effValue<AppError,Double>(it.roll().toDouble()) }

}


data class SummationConditionalTerm(
                            override val id : UUID,
                            val conditionalValueReference : Sum<BooleanReference>,
                            val trueValueReference : Sum<NumberReference>,
                            val falseValueReference: Sum<NumberReference>)
                             : SummationTerm(), Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(conditionalValueReference: BooleanReference,
                trueValueReference: NumberReference,
                falseValueReference: NumberReference)
        : this(UUID.randomUUID(),
               Sum(conditionalValueReference),
               Sum(trueValueReference),
               Sum(falseValueReference))


    companion object : Factory<SummationTerm>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<SummationTerm> = when (doc)
        {
            is DocDict -> effApply(::SummationConditionalTerm,
                                   // Conditional
                                   doc.at("conditional") ap { BooleanReference.fromDocument(it) },
                                   // When True
                                   doc.at("when_true") ap { NumberReference.fromDocument(it) },
                                   // When False
                                   doc.at("when_false") ap { NumberReference.fromDocument(it) })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun conditionalValueReference() : BooleanReference = this.conditionalValueReference.value

    fun trueValueReference() : NumberReference = this.trueValueReference.value

    fun falseValueReference() : NumberReference = this.falseValueReference.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "summation_conditional_term"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // TERM
    // -----------------------------------------------------------------------------------------

    override fun dependencies(): Set<VariableReference> =
        conditionalValueReference.value.dependencies()
            .plus(trueValueReference.value.dependencies())
            .plus(falseValueReference.value.dependencies())


    override fun value(sheetContext : SheetContext): AppEff<Double> =
        SheetData.boolean(sheetContext, conditionalValueReference())
            .apply { condition ->
                if (condition)
                    SheetData.number(sheetContext, trueValueReference())
                else
                    SheetData.number(sheetContext, falseValueReference())
            }


    //
//
//    // > Value
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the value of the conditional term. If the condition variable is true
//     * @return
//     * @throws com.kispoko.tome.rts.game.engine.definition.variable.VariableException
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

}


//    // > Summary
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * A summary of the terms variables.
//     * @return The list of 2-tuples (value, description) of each of the term's variables.
//     */
//    public com.kispoko.tome.rts.game.engine.definition.summation.term.TermSummary summary()
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
//        return new com.kispoko.tome.rts.game.engine.definition.summation.term.TermSummary(this.name(), componentsWithStringValue);
//    }
//

//    public com.kispoko.tome.rts.game.engine.definition.summation.term.TermSummary summary()
//           throws VariableException
//    {
//        return new com.kispoko.tome.rts.game.engine.definition.summation.term.TermSummary(this.termValue().name(), this.termValue().components());
//    }
//
//
//    public String valueId()
//    {
//        if (this.termValue() != null)
//            return this.termValue().name();
//
//        return "";
//    }
//
//

//    // > Term
//    // ------------------------------------------------------------------------------------------
//
//    public com.kispoko.tome.rts.game.engine.definition.summation.term.TermSummary summary()
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
//        return new com.kispoko.tome.rts.game.engine.definition.summation.term.TermSummary(this.name(), componentsWithStringValue);
//    }

