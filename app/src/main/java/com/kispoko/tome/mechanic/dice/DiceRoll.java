
package com.kispoko.tome.mechanic.dice;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.Random;
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


    // > Internal
    // ------------------------------------------------------------------------------------------

    Random randomGen;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public DiceRoll()
    {
        this.id = null;

        this.diceType = new PrimitiveValue<>(null, DiceType.class);
        this.quantity = new PrimitiveValue<>(null, Integer.class);
        this.modifier = new PrimitiveValue<>(null, Integer.class);

        randomGen = new Random();
    }


    public DiceRoll(UUID id, DiceType diceType, Integer quantity, Integer modifier)
    {
        this.id = id;

        this.diceType = new PrimitiveValue<>(diceType, DiceType.class);
        this.quantity = new PrimitiveValue<>(null, Integer.class);
        this.modifier = new PrimitiveValue<>(null, Integer.class);

        this.setQuantity(quantity);
        this.setModifier(modifier);

        randomGen = new Random();
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
     * Get the type of dice used in the roll.
     * @return The dice type.
     */
    public DiceType diceType()
    {
        return this.diceType.getValue();
    }


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
     * Get the number of times the dice is to be rolled.
     * @return The roll quantity.
     */
    public Integer quantity()
    {
        return this.quantity.getValue();
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


    /**
     * Get the modifier that is added to the dice roll.
     * @return The roll modifier.
     */
    public Integer modifier()
    {
        return this.modifier.getValue();
    }


    // > Roll
    // ------------------------------------------------------------------------------------------

    /**
     * Roll the dice.
     * @return The result of rolling the dice.
     */
    public Integer roll()
    {
        int total = 0;

        // [1] Roll the dice <quantity> times
        for (int i = 0; i < this.quantity(); i++) {
            total += dieRoll(this.diceType());
        }

        // [2] Add the modifier
        total += this.modifier();

        return total;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Roll one die.
     * @return
     */
    private int dieRoll(DiceType diceType)
    {
        switch (diceType)
        {
            case D3:
                return this.randRange(1, 3);
            case D4:
                return this.randRange(1, 4);
            case D6:
                return this.randRange(1, 6);
            case D8:
                return this.randRange(1, 8);
            case D10:
                return this.randRange(1, 10);
            case D12:
                return this.randRange(1, 12);
            case D20:
                return this.randRange(1, 20);
            case D100:
                return this.randRange(1, 100);
            default:
                return 0;
        }
    }


    private int randRange(int min, int max)
    {
        return randomGen.nextInt((max - min) + 1) + min;
    }

}
