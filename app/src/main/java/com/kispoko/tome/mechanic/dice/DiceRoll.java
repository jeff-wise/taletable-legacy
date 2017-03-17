
package com.kispoko.tome.mechanic.dice;


import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Random;
import java.util.UUID;



/**
 * Dice Roll
 */
public class DiceRoll implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<DiceType>  diceType;
    private PrimitiveFunctor<Integer>   quantity;
    private PrimitiveFunctor<Integer>   modifier;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Random                      randomGen;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public DiceRoll()
    {
        this.id = null;

        this.diceType = new PrimitiveFunctor<>(null, DiceType.class);
        this.quantity = new PrimitiveFunctor<>(null, Integer.class);
        this.modifier = new PrimitiveFunctor<>(null, Integer.class);

        randomGen = new Random();
    }


    public DiceRoll(UUID id, DiceType diceType, Integer quantity, Integer modifier)
    {
        this.id = id;

        this.diceType = new PrimitiveFunctor<>(diceType, DiceType.class);
        this.quantity = new PrimitiveFunctor<>(null, Integer.class);
        this.modifier = new PrimitiveFunctor<>(null, Integer.class);

        this.setQuantity(quantity);
        this.setModifier(modifier);

        randomGen = new Random();
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


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Dice Roll's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("type", this.diceType())
                .putInteger("quantity", this.quantity())
                .putInteger("modifier", this.modifier());
    }


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

        diceRoll.append(this.quantity().toString());

        diceRoll.append(this.diceType().name().toLowerCase());


        if (this.modifier() > 0 && withModifier)
        {
            diceRoll.append(" +");

            diceRoll.append(this.modifier().toString());
        }

        return diceRoll.toString();
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


    // COMPARATOR
    // ------------------------------------------------------------------------------------------

    public static class DiceRollComparator implements Comparator<DiceRoll>
    {
        @Override
        public int compare(DiceRoll diceRoll1, DiceRoll diceRoll2)
        {
            Integer diceRoll1Value = sides(diceRoll1.diceType());
            Integer diceRoll2Value = sides(diceRoll2.diceType());

            return diceRoll1Value.compareTo(diceRoll2Value);
        }
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


    private static int sides(DiceType diceType)
    {
        switch (diceType)
        {
            case D3:
                return 3;
            case D4:
                return 4;
            case D6:
                return 6;
            case D8:
                return 8;
            case D10:
                return 10;
            case D12:
                return 12;
            case D20:
                return 20;
            case D100:
                return 100;
            default:
                return 0;
        }
    }


}
