
package com.kispoko.tome.engine.programming.variable;


import com.kispoko.tome.mechanic.dice.DiceRoll;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Dice Variable
 */
public class DiceVariable extends Variable implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Model
    // ------------------------------------------------------------------------------------------

    private PrimitiveValue<String> name;
    private ModelValue<DiceRoll>   diceRoll;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public DiceVariable()
    {
        this.id       = null;

        this.name     = new PrimitiveValue<>(null, String.class);
        this.diceRoll = ModelValue.empty(DiceRoll.class);
    }


    public DiceVariable(UUID id, String name, DiceRoll diceRoll)
    {
        this.id       = id;

        this.name     = new PrimitiveValue<>(name, String.class);
        this.diceRoll = ModelValue.full(diceRoll, DiceRoll.class);
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
    public String getName()
    {
        return this.name.getValue();
    }

}

