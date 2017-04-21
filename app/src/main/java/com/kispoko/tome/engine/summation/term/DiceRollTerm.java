
package com.kispoko.tome.engine.summation.term;


import com.kispoko.tome.engine.summation.SummationException;
import com.kispoko.tome.engine.summation.error.NullTermError;
import com.kispoko.tome.engine.summation.error.SummationVariableError;
import com.kispoko.tome.engine.variable.VariableException;
import com.kispoko.tome.engine.variable.VariableReference;
import com.kispoko.tome.mechanic.dice.DiceRoll;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

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

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<DiceRollTermValue> termValue;

    /**
     * The name/description of the term.
     */
    private PrimitiveFunctor<String>        name;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public DiceRollTerm()
    {
        this.id = null;

        this.termValue = ModelFunctor.empty(DiceRollTermValue.class);
        this.name = new PrimitiveFunctor<>(null, String.class);
    }


    public DiceRollTerm(UUID id, DiceRollTermValue diceRollTermValue, String name)
    {
        this.id = id;

        this.termValue = ModelFunctor.full(diceRollTermValue, DiceRollTermValue.class);
        this.name = new PrimitiveFunctor<>(name, String.class);
    }


    /**
     * Create a Dice Roll Term from its Yaml representation.
     *
     * @param yaml The yaml parser.
     * @return The parsed Dice Roll Term.
     * @throws YamlParseException
     */
    public static DiceRollTerm fromYaml(YamlParser yaml)
            throws YamlParseException
    {
        UUID id = UUID.randomUUID();

        DiceRollTermValue termValue = DiceRollTermValue.fromYaml(yaml.atKey("value"));
        String name = yaml.atMaybeKey("name").getString();

        return new DiceRollTerm(id, termValue, name);
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
     * This method is called when the Column Union is completely loaded for the first time.
     */
    public void onLoad() { }


    // > Term
    // ------------------------------------------------------------------------------------------

    /**
     * Get the term value. The returned value is just the value of the referenced variable.
     *
     * @return The term value. Throws SummationException if the variable is invalid.
     */
    public Integer value()
            throws SummationException
    {
        try
        {
            Integer value = this.termValue().value();

            if (value == null) {
                throw SummationException.nullTerm(
                        new NullTermError(this.name()));
            }

            return value;
        }
        catch (VariableException exception)
        {
            throw SummationException.variable(
                    new SummationVariableError(exception));
        }
    }


    /**
     * Get the variables that this term depends upon to calculate its value.
     *
     * @return A list of variable names.
     */
    public List<VariableReference> variableDependencies()
    {
        List<VariableReference> variableReferences = new ArrayList<>();

        if (this.termValue().type() == DiceRollTermValue.Type.VARIABLE) {
            VariableReference variableReference = this.termValue().variable();
            if (variableReference != null)
                variableReferences.add(variableReference);
        }

        return variableReferences;
    }


    public TermSummary summary()
           throws VariableException
    {
        return new TermSummary(this.name(), this.termValue().components());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The Dice Roll Term Value.
     * @return The Dice Roll Term Value.
     */
    public DiceRollTermValue termValue()
    {
        return this.termValue.getValue();
    }


    public DiceRollTermValue.Type termValueType()
    {
        if (this.termValue() != null)
            return this.termValue().type();
        return null;
    }


    /**
     * Get the Dice Roll value.
     * @return The Dice Roll.
     */
    public DiceRoll diceRoll()
           throws SummationException
    {
        try
        {
            return this.termValue().diceRoll();
        }
        catch (VariableException exception)
        {
            throw SummationException.variable(new SummationVariableError(exception));
        }
    }


    /**
     * The name of the term.
     * @return The term name.
     */
    public String name()
    {
        return this.name.getValue();
    }

}
