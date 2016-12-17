
package com.kispoko.tome.engine.programming.summation.term;


import com.kispoko.tome.engine.programming.summation.SummationException;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Dice Roll Term
 */
public class DiceRollTerm extends Term implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                          id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelValue<DiceRollTermValue> termValue;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public DiceRollTerm()
    {
        this.id           = null;

        this.termValue    = ModelValue.empty(DiceRollTermValue.class);
    }


    public DiceRollTerm(UUID id, DiceRollTermValue diceRollTermValue)
    {
        this.id           = null;

        this.termValue    = ModelValue.full(diceRollTermValue, DiceRollTermValue.class);
    }


    /**
     * Create a Dice Roll Term from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Dice Roll Term.
     * @throws YamlException
     */
    public static DiceRollTerm fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID              id        = UUID.randomUUID();

        DiceRollTermValue termValue = DiceRollTermValue.fromYaml(yaml.atKey("value"));

        return new DiceRollTerm(id, termValue);
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
     * This method is called when the Column Union is completely loaded for the first time.
     */
    public void onLoad() { }


    // > Term
    // ------------------------------------------------------------------------------------------

    /**
     * Get the term value. The returned value is just the value of the referenced variable.
     * @return The term value. Throws SummationException if the variable is invalid.
     */
    public Integer value()
           throws SummationException
    {
        return termValue.getValue().value();
    }


    /**
     * Get the variables that this term depends upon to calculate its value.
     * @return A list of variable names.
     */
    public List<String> variableDependencies()
    {
        List<String> variableNames = new ArrayList<>();

        String variableName = this.termValue.getValue().variableName();

        if (variableName != null)
            variableNames.add(variableName);

        return variableNames;
    }



}
