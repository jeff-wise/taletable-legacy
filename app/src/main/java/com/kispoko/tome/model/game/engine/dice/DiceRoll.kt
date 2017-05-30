
package com.kispoko.tome.model.game.engine.dice


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Coll
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import effect.Err
import effect.effApply
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Dice Roll
 */
data class DiceRoll(override val id : UUID,
                    val quantities : Coll<DiceQuantity>,
                    val modifiers : Coll<RollModifier>) : Model
{

    companion object : Factory<DiceRoll>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<DiceRoll> = when (doc)
        {
            is DocDict -> effApply(::DiceRoll,
                                   // Model Id
                                   valueResult(UUID.randomUUID()),
                                   // Quantity
                                   doc.list("quantity") ap { docList ->
                                       effApply(::Coll,
                                               docList.map { DiceQuantity.fromDocument(it) })
                                   },
                                   // Modifier
                                   doc.list("modifier") ap { docList ->
                                       effApply(::Coll,
                                               docList.map { RollModifier.fromDocument(it) })
                                   })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Dice Quantity
 */
data class DiceQuantity(override val id : UUID,
                        val sides : Func<DiceSides>,
                        val quantity : Func<Int>) : Model
{

    companion object : Factory<DiceQuantity>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<DiceQuantity> = when (doc)
        {
            is DocDict -> effApply(::DiceQuantity,
                                   // Model Id
                                   valueResult(UUID.randomUUID()),
                                   // Sides
                                   doc.at("sides") ap {
                                       effApply(::Prim, DiceSides.fromDocument(it))
                                   },
                                   // Quantity
                                   effApply(::Prim, doc.int("quantity")))
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Dice Sides
 */
data class DiceSides(val value : Int)
{

    companion object : Factory<DiceSides>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<DiceSides> = when (doc)
        {
            is DocInteger -> valueResult(DiceSides(doc.integer.toInt()))
            else          -> Err(UnexpectedType(DocType.INTEGER, docType(doc)), doc.path)
        }
    }
}


/**
 * Dice Modifier
 */
data class RollModifier(override val id : UUID,
                        val value : Func<Int>,
                        val name : Func<String>) : Model
{

    companion object : Factory<RollModifier>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<RollModifier> = when (doc)
        {
            is DocDict -> effApply(::RollModifier,
                                   // Model Id
                                   valueResult(UUID.randomUUID()),
                                   // Value
                                   effApply(::Prim, doc.int("value")),
                                   // Name
                                   effApply(::Prim, doc.text("name")))
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}



//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the sides of the dice in the quantity.
//     * @return The dice type.
//     */
//    public int diceSides()
//    {
//        return this.diceSides.getValue();
//    }
//
//
//    /**
//     * Set the quantity of dice to be rolled. If quantity is null, then the default quantity of
//     * one is used.
//     *
//     * @param quantity The dice quantity.
//     */
//    public void setQuantity(Integer quantity)
//    {
//        if (quantity != null)
//            this.quantity.setValue(quantity);
//        else
//            this.quantity.setValue(1);
//    }
//
//
//    /**
//     * Get the number of times the dice is to be rolled.
//     * @return The roll quantity.
//     */
//    public int quantity()
//    {
//        return this.quantity.getValue();
//    }
//
//
//    // > Description
//    // ------------------------------------------------------------------------------------------
//
//    public String description()
//    {
//        String description = "";
//
//        description += "Roll a 1d";
//        description += Integer.toString(this.diceSides());
//        description += " ";
//        description += Integer.toString(this.quantity());
//        description += " times";
//
//        return description;
//    }
//
//    // > To String
//    // ------------------------------------------------------------------------------------------
//
//    @Override
//    public String toString()
//    {
//        StringBuilder quantity = new StringBuilder();
//
//        quantity.append(Integer.toString(this.quantity()));
//        quantity.append("d");
//        quantity.append(Integer.toString(this.diceSides()));
//
//        return quantity.toString();
//    }
//
//
//    // > Roll
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Roll the dice and return the total value.
//     * @return The sum of the random dice values.
//     */
//    public Integer roll()
//    {
//        return this.rollAsSummary().rollValue();
//    }
//
//
//    /**
//     * Roll the dice and return a summary of the values of each die in the roll.
//     * @return The randomly generated roll summary.
//     */
//    public RollSummary rollAsSummary()
//    {
//        List<DieRollResult> results = new ArrayList<>();
//
//        for (int i = 0; i < this.quantity(); i++) {
//            results.add(DieRollResult.generate(this.diceSides()));
//        }
//
//        return new RollSummary(results);
//    }
//
//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Initialize
//    // -----------------------------------------------------------------------------------------
//
//    private void initializeFunctors()
//    {
//        // Dice Sides
//        this.diceSides.setName("dice_sides");
//        this.diceSides.setLabelId(R.string.dice_quantity_field_dice_sides_label);
//        this.diceSides.setDescriptionId(R.string.dice_quantity_field_dice_sides_description);
//
//        // Quantity
//        this.quantity.setName("quantity");
//        this.quantity.setLabelId(R.string.dice_quantity_field_quantity_label);
//        this.quantity.setDescriptionId(R.string.dice_quantity_field_quantity_description);
//    }
//
//}



//
//    // > Value Plus String
//    // ------------------------------------------------------------------------------------------
//
//    public String valuePlusString()
//    {
//        return "+" + Integer.toString(this.value());
//    }
//
//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Initialize
//    // -----------------------------------------------------------------------------------------
//
//    private void initializeFunctors()
//    {
//        // Value
//        this.value.setName("value");
//        this.value.setLabelId(R.string.roll_modifier_field_value_label);
//        this.value.setDescriptionId(R.string.roll_modifier_field_value_description);
//
//        // Name
//        this.name.setName("name");
//        this.name.setLabelId(R.string.roll_modifier_field_name_label);
//        this.name.setDescriptionId(R.string.roll_modifier_field_name_description);
   //  }


//
//    // > To String
//    // ------------------------------------------------------------------------------------------
//
//    @Override
//    public String toString()
//    {
//        return this.toString(true);
//    }
//
//
//    public String toString(boolean withModifier)
//    {
//        StringBuilder diceRoll = new StringBuilder();
//
//        String sep = "";
//        for (DiceQuantity diceQuantity : this.quantities()) {
//            diceRoll.append(sep);
//            diceRoll.append(diceQuantity.toString());
//            sep = " + ";
//        }
//
//
//        int totalModifier = 0;
//
//        for (RollModifier modifier : this.modifiers()) {
//            totalModifier += modifier.value();
//        }
//
//        Log.d("***DICEROLL", "modifier " + Integer.toString(totalModifier));
//        if (totalModifier > 0 && withModifier)
//        {
//            diceRoll.append(" + ");
//            diceRoll.append(Integer.toString(totalModifier));
//        }
//
//        return diceRoll.toString();
//    }
//
//
//    /**
//     * Roll the dice.
//     * @return The result of rolling the dice.
//     */
//    public Integer roll()
//    {
//        return this.rollAsSummary().rollValue();
//    }
//
//
//    public RollSummary rollAsSummary()
//    {
//        RollSummary rollSummary = new RollSummary(new ArrayList<DieRollResult>());
//
//        for (DiceQuantity quantity : this.quantities()) {
//            rollSummary = rollSummary.addSummary(quantity.rollAsSummary());
//        }
//
//        return new RollSummary(rollSummary.rollResults(), this.modifiers());
//    }
//
//
//    // > Add Roll
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * Add another dice roll to this dice roll.
//     * @param diceRoll The dice to roll to add to this roll.
//     */
//    public void addDiceRoll(DiceRoll diceRoll)
//    {
//        if (diceRoll != null)
//        {
//            this.quantitiesMutable().addAll(diceRoll.quantities());
//            this.modifiersMutable().addAll(diceRoll.modifiers());
//        }
//    }
//
//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Initialize
//    // -----------------------------------------------------------------------------------------
//
//    private void initializeFunctors()
//    {
//        // Quantities
//        this.quantities.setName("quantities");
//        this.quantities.setLabelId(R.string.dice_roll_field_quantities_label);
//        this.quantities.setDescriptionId(R.string.dice_roll_field_quantities_description);
//
//        // Modifiers
//        this.modifiers.setName("modifiers");
//        this.modifiers.setLabelId(R.string.dice_roll_field_modifiers_label);
//        this.modifiers.setDescriptionId(R.string.dice_roll_field_modifiers_description);
//    }

