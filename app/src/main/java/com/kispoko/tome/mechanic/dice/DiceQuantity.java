
package com.kispoko.tome.mechanic.dice;


import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;



/**
 * Dice Quantity
 */
public class DiceQuantity extends Model
                          implements ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<Integer>   diceSides;
    private PrimitiveFunctor<Integer>   quantity;




    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public DiceQuantity()
    {
        this.id         = null;

        this.diceSides  = new PrimitiveFunctor<>(null, Integer.class);
        this.quantity   = new PrimitiveFunctor<>(null, Integer.class);
    }


    public DiceQuantity(UUID id, int diceSides, int quantity)
    {
        this.id         = id;

        this.diceSides  = new PrimitiveFunctor<>(diceSides, Integer.class);
        this.quantity   = new PrimitiveFunctor<>(quantity, Integer.class);

        this.setQuantity(quantity);
    }


    /**
     * Create a Dice Roll from its Yaml representation.
     *
     * @param yaml The yaml parser.
     * @return The parsed Dice Roll.
     * @throws YamlParseException
     */
    public static DiceQuantity fromYaml(YamlParser yaml)
            throws YamlParseException
    {
        UUID     id         = UUID.randomUUID();

        Integer  diceSides  = yaml.atKey("sides").getInteger();
        Integer  quantity   = yaml.atMaybeKey("quantity").getInteger();

        return new DiceQuantity(id, diceSides, quantity);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     *
     * @return The model UUID.
     */
    public UUID getId()
    {
        return this.id;
    }


    /**
     * Set the model identifier.
     *
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
     *
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putInteger("sides", this.diceSides())
                .putInteger("quantity", this.quantity());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the sides of the dice in the quantity.
     * @return The dice type.
     */
    public int diceSides()
    {
        return this.diceSides.getValue();
    }


    /**
     * Set the quantity of dice to be rolled. If quantity is null, then the default quantity of
     * one is used.
     *
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
    public int quantity()
    {
        return this.quantity.getValue();
    }


    // > To String
    // ------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        StringBuilder quantity = new StringBuilder();

        quantity.append(Integer.toString(this.quantity()));
        quantity.append("d");
        quantity.append(Integer.toString(this.diceSides()));

        return quantity.toString();
    }


    // > Roll
    // ------------------------------------------------------------------------------------------

    /**
     * Roll the dice and return the total value.
     * @return The sum of the random dice values.
     */
    public Integer roll()
    {
        return this.rollAsSummary().rollValue();
    }


    /**
     * Roll the dice and return a summary of the values of each die in the roll.
     * @return The randomly generated roll summary.
     */
    public RollSummary rollAsSummary()
    {
        List<DieRollResult> results = new ArrayList<>();

        for (int i = 0; i < this.quantity(); i++) {
            results.add(DieRollResult.generate(this.diceSides()));
        }

        return new RollSummary(results);
    }

}
