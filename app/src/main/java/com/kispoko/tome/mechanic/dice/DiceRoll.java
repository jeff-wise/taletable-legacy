
package com.kispoko.tome.mechanic.dice;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Dice Roll
 */
public class DiceRoll implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveValue<DiceType> diceType;
    private PrimitiveValue<Integer>  quantity;
    private PrimitiveValue<Integer>  modifier;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public DiceRoll()
    {
        this.id = null;

        this.diceType = new PrimitiveValue<>(null, DiceType.class);
        this.quantity = new PrimitiveValue<>(null, Integer.class);
        this.modifier = new PrimitiveValue<>(null, Integer.class);
    }


    public DiceRoll(UUID id, DiceType diceType, Integer quantity, Integer modifier)
    {
        this.id = id;

        this.diceType = new PrimitiveValue<>(diceType, DiceType.class);
        this.quantity = new PrimitiveValue<>(null, Integer.class);
        this.modifier = new PrimitiveValue<>(null, Integer.class);

        this.setQuantity(quantity);
        this.setModifier(modifier);
    }


    /**
     * Create a Dice Roll from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Dice Roll.
     * @throws YamlException
     */
    public static DiceRoll fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID     id       = UUID.randomUUID();

        DiceType diceType = DiceType.fromYaml(yaml.atKey("type"));
        Integer  quantity = yaml.atMaybeKey("quantity").getInteger();
        Integer  modifier = yaml.atMaybeKey("modifier").getInteger();

        return new DiceRoll(id, diceType, quantity, modifier);
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


    // > State
    // ------------------------------------------------------------------------------------------


    /**
     * Set the quantity of dice to be rolled. If quantity is null, then the default quantity of
     * one is used.
     * @param quantity The dice quantity.
     */
    public void setQuantity(Integer quantity)
    {
        if (quantity != null)
            this.quantity.setValue(quantity);
        else
            this.quantity.setValue(1);
    }


    /**
     * Set the modifier of the dice roll. If the modifier is null, then the default modifier of
     * zero is used.
     * @param modifier The modifier.
     */
    public void setModifier(Integer modifier)
    {
        if (modifier != null)
            this.modifier.setValue(modifier);
        else
            this.modifier.setValue(0);
    }

}
