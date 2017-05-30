
package com.kispoko.tome.model.game.engine.reference


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.game.engine.variable.*
import effect.Err
import effect.effApply
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
sealed class DiceRollReference : Model
{

    companion object : Factory<DiceRollReference>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<DiceRollReference> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "literal"  -> DiceRollReferenceLiteral.fromDocument(doc)
                    "variable" -> DiceRollReferenceVariable.fromDocument(doc)
                    else       -> Err<ValueError, DocPath,DiceRollReference>(
                                            UnknownCase(doc.case()), doc.path)
                }
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Literal Dice Roll Reference
 */
data class DiceRollReferenceLiteral(override val id : UUID,
                                    val value : Func<DiceRoll>) : DiceRollReference()
{

    companion object : Factory<DiceRollReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<DiceRollReference> = when (doc)
        {
            is DocDict -> effApply(::DiceRollReferenceLiteral,
                                   // Model Id
                                   valueResult(UUID.randomUUID()),
                                   // Value
                                   doc.at("dice_roll") ap {
                                       effApply(::Comp, DiceRoll.fromDocument(it))
                                   })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Variable Dice Roll Reference
 */
data class DiceRollReferenceVariable(
                            override val id : UUID,
                            val variableReference : Func<VariableReference>) : DiceRollReference()
{

    companion object : Factory<DiceRollReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<DiceRollReference> = when (doc)
        {
            is DocDict -> effApply(::DiceRollReferenceVariable,
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
//
//
//
//
//    // > Name
//    // ------------------------------------------------------------------------------------------
//
//    public String name()
//    {
//        if (this.valueId() != null)
//            return this.valueId();
//
//        if (this.type() == Type.VARIABLE)
//        {
//            VariableUnion variableUnion = this.variableReference().variable();
//            if (variableUnion != null)
//                return variableUnion.variable().label();
//        }
//
//        return "";
//    }
//
//
//    // > Components
//    // ------------------------------------------------------------------------------------------
//
//    public List<Tuple2<String,String>> components()
//           throws VariableException
//    {
//        switch (this.type())
//        {
//            case LITERAL:
//                List<Tuple2<String, String>> components = new ArrayList<>();
//                String name = this.name() != null ? this.name() : "";
//                components.add(new Tuple2<>(name, this.literal().toString()));
//                return components;
//            case VARIABLE:
//                return this.variableSummaries();
//        }
//
//        return new ArrayList<>();
//    }
//
//
//    // > Value
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the value of the dice roll term. It is either a literal dice roll, or the value of an
//     * dice roll variable.
//     * @return The Dice Roll value.
//     * @throws VariableException
//     */
//    public Integer value()
//           throws VariableException
//    {
//        switch (this.type())
//        {
//            case LITERAL:
//                return this.diceRoll().roll();
//            case VARIABLE:
//                DiceRoll roll = this.variableValue();
//                if (roll == null)
//                    return null;
//                return roll.roll();
//            default:
//                ApplicationFailure.union(
//                        UnionException.unknownVariant(
//                                new UnknownVariantError(Type.class.getName())));
//        }
//
//        return null;
//    }
//
//
//    /**
//     * Get the dice roll.
//     * @return The Dice Roll.
//     */
//    public DiceRoll diceRoll()
//           throws VariableException
//    {
//        switch (this.type())
//        {
//            case LITERAL:
//                return this.literal();
//            case VARIABLE:
//                return this.variableValue();
//            default:
//                ApplicationFailure.union(
//                        UnionException.unknownVariant(
//                                new UnknownVariantError(Type.class.getName())));
//        }
//
//        return null;
//    }
//
//
//
//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    // > Initialize
//    // ------------------------------------------------------------------------------------------
//
//    private void initializeFunctors()
//    {
//        // Value Name
//        this.valueId.setName("value_name");
//        this.valueId.setLabelId(R.string.activity_dice_roll_term_value_field_value_name_label);
//        this.valueId
//            .setDescriptionId(R.string.activity_dice_roll_term_value_field_value_name_description);
//
//        // Type
//        this.type.setName("type");
//        this.type.setLabelId(R.string.activity_dice_roll_term_value_field_type_label);
//        this.type.setDescriptionId(R.string.activity_dice_roll_term_value_field_type_description);
//
//        // Literal Value
//        this.diceRoll.setName("type_literal");
//        this.diceRoll.setLabelId(R.string.activity_dice_roll_term_value_field_literal_value_label);
//        this.diceRoll
//          .setDescriptionId(R.string.activity_dice_roll_term_value_field_literal_value_description);
//        this.diceRoll.caseOf("type", "literal");
//
//        // Variable Value
//        this.variableReference.setName("type_variable");
//        this.variableReference
//                .setLabelId(R.string.activity_dice_roll_term_value_field_variable_reference_label);
//        int descId = R.string.activity_dice_roll_term_value_field_variable_reference_description;
//        this.variableReference.setDescriptionId(descId);
//        this.variableReference.caseOf("type", "variable");
//    }
//
//
//    // > Variable
//    // ------------------------------------------------------------------------------------------
//
//    private DiceRoll variableValue()
//            throws VariableException
//    {
//        VariableUnion variableUnion = this.variableReference().variable();
//
//        if (variableUnion == null)
//            return null;
//
//        // > If variable is not a dice roll, throw exception
//        if (!variableUnion.type().equals(VariableType.DICE)) {
//            throw VariableException.unexpectedVariableType(
//                    new UnexpectedVariableTypeError(this.variableReference().name(),
//                                                    VariableType.DICE,
//                                                    variableUnion.type()));
//        }
//
//        DiceRoll variableValue = variableUnion.diceVariable().diceRoll();
//
//        return variableValue;
//    }
//
//
//
//    private List<Tuple2<String,String>> variableSummaries()
//    {
//        List<Tuple2<String,String>> summaries = new ArrayList<>();
//
//        for (VariableUnion variableUnion : this.variableReference().variables())
//        {
//            // [1] If variable is not a number, throw exception
//            // ----------------------------------------------------------------------------------
//            if (variableUnion.type() != VariableType.DICE) {
//                ApplicationFailure.variable(
//                        VariableException.unexpectedVariableType(
//                                new UnexpectedVariableTypeError(variableUnion.variable().name(),
//                                        VariableType.DICE,
//                                        variableUnion.type())));
//                continue;
//            }
//
//            DiceRollVariable variable = variableUnion.diceVariable();

//            summaries.add(new Tuple2<>(variable.label(), variable.diceRoll().toString()));
//        }
//
//        return summaries;
//    }

