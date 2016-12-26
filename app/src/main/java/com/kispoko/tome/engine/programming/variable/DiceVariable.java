
package com.kispoko.tome.engine.programming.variable;


import com.kispoko.tome.mechanic.dice.DiceRoll;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Dice Variable
 */
public class DiceVariable extends Variable implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                     id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String> name;
    private ModelFunctor<DiceRoll>   diceRoll;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public DiceVariable()
    {
        this.id       = null;

        this.name     = new PrimitiveFunctor<>(null, String.class);
        this.diceRoll = ModelFunctor.empty(DiceRoll.class);
    }


    public DiceVariable(UUID id, String name, DiceRoll diceRoll)
    {
        this.id       = id;

        this.name     = new PrimitiveFunctor<>(name, String.class);
        this.diceRoll = ModelFunctor.full(diceRoll, DiceRoll.class);
    }


    /**
     * Create a Dice Variable from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Dice Variable.
     * @throws YamlException
     */
    public static DiceVariable fromYaml(Yaml yaml)
                  throws YamlException
    {
        if (yaml.isNull())
            return null;

        UUID     id       = UUID.randomUUID();
        String   name     = yaml.atMaybeKey("name").getString();
        DiceRoll diceRoll = DiceRoll.fromYaml(yaml.atKey("dice"));

        return new DiceVariable(id, name, diceRoll);
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


    // > Variable
    // ------------------------------------------------------------------------------------------

    /**
     * Get the variable name which is a unique identifier.
     * @return The variable name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    public List<String> dependencies()
    {
        return new ArrayList<>();
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Dice Roll
    // ------------------------------------------------------------------------------------------

    /**
     * Get the dice roll.
     * @return The dice roll.
     */
    public DiceRoll diceRoll()
    {
        return this.diceRoll.getValue();
    }


    public Integer rollValue()
    {
        return this.diceRoll().roll();

    }

}

