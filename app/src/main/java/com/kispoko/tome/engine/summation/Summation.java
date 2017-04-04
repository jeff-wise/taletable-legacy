
package com.kispoko.tome.engine.summation;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.engine.summation.term.TermSummary;
import com.kispoko.tome.engine.summation.term.TermType;
import com.kispoko.tome.engine.summation.term.TermUnion;
import com.kispoko.tome.engine.variable.VariableException;
import com.kispoko.tome.engine.variable.VariableReference;
import com.kispoko.tome.mechanic.dice.DiceRoll;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;



/**
 * Summation
 */
public class Summation extends Model
                       implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
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

        this.terms = CollectionFunctor.empty(TermUnion.class);
    }


    public Summation(UUID id, List<TermUnion> terms)
    {
        this.id = id;

        this.terms = CollectionFunctor.full(terms, TermUnion.class);

        this.initialize();
    }


    public static Summation fromYaml(YamlParser yaml)
            throws YamlParseException
    {
        UUID            id    = UUID.randomUUID();

        List<TermUnion> terms = yaml.atKey("terms").forEach(new YamlParser.ForEach<TermUnion>() {
            @Override
            public TermUnion forEach(YamlParser yaml, int index) throws YamlParseException {
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
     */
    public Integer value()
    {
        return sum();
    }


    /**
     * Get the summation value as a string (formula).
     * @return The value string.
     */
    public String valueString()
    {
        if (this.hasDiceRoll())
        {
            List<DiceRoll> diceRolls = new ArrayList<>();
            Integer        modifier  = 0;

            for (TermUnion termUnion : this.terms())
            {
                try
                {
                    if (termUnion.type() == TermType.DICE_ROLL) {
                        diceRolls.add(termUnion.diceRollTerm().diceRoll());
                    } else {
                        modifier += termUnion.term().value();
                    }
                } catch (SummationException exception) {
                    ApplicationFailure.summation(exception);
                }
            }

            return formulaString(diceRolls, modifier);
        }
        // Otherwise, just one number
        else
        {
            return Integer.toString(this.value());
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
    public List<TermUnion> terms()
    {
        return this.terms.getValue();
    }


    public List<TermUnion> termsSorted()
    {
        List<TermUnion> termsList = new ArrayList<>(this.terms());

        Collections.sort(termsList, new Comparator<TermUnion>()
        {
            @Override
            public int compare(TermUnion term1, TermUnion term2)
            {
                int term1Value;
                int term2Value;

                try {
                    term1Value = term1.term().value();
                    term2Value = term2.term().value();
                }
                catch (SummationException exception) {
                    return 0;
                }

                if (term1Value > term2Value)
                    return -1;
                if (term1Value < term2Value)
                    return 1;
                return 0;
            }
        });

        return termsList;
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


    // > Summary
    // ------------------------------------------------------------------------------------------

    public List<TermSummary> summary()
    {
        List<TermSummary> summaries = new ArrayList<>();

        for (TermUnion termUnion : this.terms())
        {
            try {
                summaries.add(termUnion.term().summary());
            }
            catch (VariableException exception) {
                continue;
            }
        }

        return summaries;
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
    {
        Integer sum = 0;

        for (TermUnion termUnion : this.terms.getValue())
        {
            try {
                sum += termUnion.term().value();
            }
            catch (SummationException exception) {
                ApplicationFailure.summation(exception);
            }
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
