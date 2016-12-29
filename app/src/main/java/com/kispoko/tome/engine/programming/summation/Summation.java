
package com.kispoko.tome.engine.programming.summation;


import com.kispoko.tome.engine.programming.summation.term.TermType;
import com.kispoko.tome.engine.programming.summation.term.TermUnion;
import com.kispoko.tome.engine.programming.variable.VariableException;
import com.kispoko.tome.engine.programming.variable.VariableReference;
import com.kispoko.tome.mechanic.dice.DiceRoll;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;



/**
 * Summation
 */
public class Summation implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                         id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private CollectionFunctor<TermUnion> terms;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                      sum;

    private Boolean                      hasDiceRoll;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Summation()
    {
        this.id = null;

        List<Class<? extends TermUnion>> termClasses = new ArrayList<>();
        termClasses.add(TermUnion.class);
        this.terms = CollectionFunctor.empty(termClasses);
    }


    public Summation(UUID id, List<TermUnion> terms)
    {
        this.id = id;

        List<Class<? extends TermUnion>> termClasses = new ArrayList<>();
        termClasses.add(TermUnion.class);
        this.terms = CollectionFunctor.full(terms, termClasses);

        this.initialize();
    }


    public static Summation fromYaml(Yaml yaml)
            throws YamlException
    {
        UUID            id    = UUID.randomUUID();

        List<TermUnion> terms = yaml.atKey("terms").forEach(new Yaml.ForEach<TermUnion>() {
            @Override
            public TermUnion forEach(Yaml yaml, int index) throws YamlException {
                return TermUnion.fromYaml(yaml);
            }
        }, true);

        return new Summation(id, terms);
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
    public void onLoad()
    {
        this.initialize();
    }


    // > Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the summation value.
     *
     * @return The sum.
     * @throws SummationException
     */
    public Integer value()
           throws VariableException
    {
        return sum();
    }


    /**
     * Get the summation value as a string (formula).
     * @return The value string.
     */
    public String valueString()
           throws VariableException
    {
        if (this.hasDiceRoll())
        {
            List<DiceRoll> diceRolls = new ArrayList<>();
            Integer        modifier  = 0;

            for (TermUnion termUnion : this.terms())
            {
                if (termUnion.type() == TermType.DICE_ROLL) {
                    diceRolls.add(termUnion.diceRollTerm().diceRoll());

                } else {
                    modifier += termUnion.term().value();
                }
            }

            return formulaString(diceRolls, modifier);
        }
        // Otherwise, just one number
        else
        {
            return "+" + Integer.toString(this.value());
        }
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Terms
    // ------------------------------------------------------------------------------------------

    /**
     * Get the terms in the summation.
     * @return The List of Terms.
     */
    private List<TermUnion> terms()
    {
        return this.terms.getValue();
    }


    // ** Dependencies
    // ------------------------------------------------------------------------------------------

    /**
     * Get the names of all of the variables that the summation depends on to calculate its value.
     *
     * @return A list of variable names.
     */
    public List<VariableReference> variableDependencies() {
        List<VariableReference> variableReferences = new ArrayList<>();

        for (TermUnion termUnion : this.terms.getValue()) {
            variableReferences.addAll(termUnion.term().variableDependencies());
        }

        return variableReferences;
    }


    // ** Has Roll
    // ------------------------------------------------------------------------------------------

    /**
     * Returns true if a dice roll is a part of the summation value.
     * @return True if the summation contains a dice roll, False otherwise.
     */
    public boolean hasDiceRoll()
    {
        return this.hasDiceRoll;
    }


    // ** To String
    // ------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "";
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the summation.
     */
    private void initialize()
    {
        // Check for a dice roll term
        // --------------------------------------------------------------------------------------

        this.hasDiceRoll = false;

        for (TermUnion termUnion : this.terms())
        {
            if (termUnion.type() == TermType.DICE_ROLL) {
                this.hasDiceRoll = true;
                break;
            }
        }
    }


    /**
     * Evaluate the sum of this summation.
     */
    private Integer sum()
            throws VariableException
    {
        Integer sum = 0;

        for (TermUnion termUnion : this.terms.getValue()) {
            sum += termUnion.term().value();
        }

        this.sum = sum;

        return this.sum;
    }


    /**
     * Create a formula string for a summation that includes dice rolls.
     * @return
     */
    private String formulaString(List<DiceRoll> diceRolls, Integer modifier)
    {
        StringBuilder formula = new StringBuilder();

        // Sort the dice rolls
        Collections.sort(diceRolls, new DiceRoll.DiceRollComparator());

        for (DiceRoll diceRoll : diceRolls)
        {
            modifier += diceRoll.modifier();

            formula.append(diceRoll.toString(false));
            formula.append(" ");
        }

        formula.append(modifier.toString());

        return formula.toString();
    }


}
