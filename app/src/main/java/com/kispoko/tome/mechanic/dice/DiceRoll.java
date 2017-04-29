
package com.kispoko.tome.mechanic.dice;


import android.util.Log;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;



/**
 * Dice Roll
 */
public class DiceRoll extends Model
                      implements ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private CollectionFunctor<DiceQuantity> quantities;
    private CollectionFunctor<RollModifier> modifiers;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public DiceRoll()
    {
        this.id         = null;

        this.quantities = CollectionFunctor.empty(DiceQuantity.class);
        this.modifiers  = CollectionFunctor.empty(RollModifier.class);

        this.initializeFunctors();
    }


    public DiceRoll(UUID id,
                    List<DiceQuantity> quantities,
                    List<RollModifier> modifiers)
    {
        this.id         = id;

        this.quantities = CollectionFunctor.full(quantities, DiceQuantity.class);
        this.modifiers  = CollectionFunctor.full(modifiers, RollModifier.class);

        this.initializeFunctors();
    }


    /**
     * Create an empty dice roll with no dice and no modifiers.
     * @return The empty Dice Roll.
     */
    public static DiceRoll empty()
    {
        return new DiceRoll(UUID.randomUUID(),
                            new ArrayList<DiceQuantity>(),
                            new ArrayList<RollModifier>());
    }


    public DiceRoll(List<DiceQuantity> quantities,
                    List<RollModifier> modifiers)
    {
        this.id         = UUID.randomUUID();

        this.quantities = CollectionFunctor.full(quantities, DiceQuantity.class);
        this.modifiers  = CollectionFunctor.full(modifiers, RollModifier.class);
    }


    /**
     * Create a Dice Roll from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Dice Roll.
     * @throws YamlParseException
     */
    public static DiceRoll fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID               id        = UUID.randomUUID();

        List<DiceQuantity> quantities = yaml.atKey("quantities").forEach(
                                                new YamlParser.ForEach<DiceQuantity>() {
            @Override
            public DiceQuantity forEach(YamlParser yaml, int index) throws YamlParseException {
                return DiceQuantity.fromYaml(yaml);
            }
        }, true);

        List<RollModifier> modifiers = yaml.atMaybeKey("modifiers").forEach(
                                                new YamlParser.ForEach<RollModifier>() {
            @Override
            public RollModifier forEach(YamlParser yaml, int index) throws YamlParseException {
                return RollModifier.fromYaml(yaml);
            }
        }, true);

        return new DiceRoll(id, quantities, modifiers);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     * @return The model UUID.
     */
    public UUID getId()
    {
        return this.id;
    }


    /**
     * Set the model identifier.
     * @param id The new model UUID.
     */
    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the RulesEngine is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Dice Roll's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putList("quantities", this.quantitiesMutable())
                .putList("modifiers", this.modifiersMutable());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Quantities
    // ------------------------------------------------------------------------------------------

    /**
     * The dice quantities in the dice roll. Returns an unmodifiable list.
     */
    public List<DiceQuantity> quantities()
    {
        return Collections.unmodifiableList(this.quantities.getValue());
    }


    /**
     * The dice quantities in the dice roll.
     * @return
     */
    private List<DiceQuantity> quantitiesMutable()
    {
        return this.quantities.getValue();
    }


    // ** Modifiers
    // ------------------------------------------------------------------------------------------

    /**
     * Get the modifiers applied to this roll. Returns an unmodifiable list.
     * @return The roll modifiers.
     */
    public List<RollModifier> modifiers()
    {
        return Collections.unmodifiableList(this.modifiers.getValue());
    }


    /**
     * Get the modifiers applied to this roll. Returns an unmodifiable list.
     * @return The roll modifiers.
     */
    private List<RollModifier> modifiersMutable()
    {
        return this.modifiers.getValue();
    }


    /**
     * Add a modifier to the dice roll.
     * @param modifier The modifier to add.
     */
    public void addModifier(RollModifier modifier)
    {
        this.modifiersMutable().add(modifier);
    }


    // > To String
    // ------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return this.toString(true);
    }


    public String toString(boolean withModifier)
    {
        StringBuilder diceRoll = new StringBuilder();

        String sep = "";
        for (DiceQuantity diceQuantity : this.quantities()) {
            diceRoll.append(sep);
            diceRoll.append(diceQuantity.toString());
            sep = " + ";
        }


        int totalModifier = 0;

        for (RollModifier modifier : this.modifiers()) {
            totalModifier += modifier.value();
        }

        Log.d("***DICEROLL", "modifier " + Integer.toString(totalModifier));
        if (totalModifier > 0 && withModifier)
        {
            diceRoll.append(" + ");
            diceRoll.append(Integer.toString(totalModifier));
        }

        return diceRoll.toString();
    }


    /**
     * Roll the dice.
     * @return The result of rolling the dice.
     */
    public Integer roll()
    {
        return this.rollAsSummary().rollValue();
    }


    public RollSummary rollAsSummary()
    {
        RollSummary rollSummary = new RollSummary(new ArrayList<DieRollResult>());

        for (DiceQuantity quantity : this.quantities()) {
            rollSummary = rollSummary.addSummary(quantity.rollAsSummary());
        }

        return new RollSummary(rollSummary.rollResults(), this.modifiers());
    }


    // > Add Roll
    // -----------------------------------------------------------------------------------------

    /**
     * Add another dice roll to this dice roll.
     * @param diceRoll The dice to roll to add to this roll.
     */
    public void addDiceRoll(DiceRoll diceRoll)
    {
        if (diceRoll != null)
        {
            this.quantitiesMutable().addAll(diceRoll.quantities());
            this.modifiersMutable().addAll(diceRoll.modifiers());
        }
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Initialize
    // -----------------------------------------------------------------------------------------

    private void initializeFunctors()
    {
        // Quantities
        this.quantities.setName("quantities");
        this.quantities.setLabelId(R.string.dice_roll_field_quantities_label);
        this.quantities.setDescriptionId(R.string.dice_roll_field_quantities_description);

        // Modifiers
        this.modifiers.setName("modifiers");
        this.modifiers.setLabelId(R.string.dice_roll_field_modifiers_label);
        this.modifiers.setDescriptionId(R.string.dice_roll_field_modifiers_description);
    }

}
