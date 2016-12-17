
package com.kispoko.tome.engine.programming.summation;


import com.kispoko.tome.engine.programming.summation.term.ConditionalTerm;
import com.kispoko.tome.engine.programming.summation.term.LiteralTerm;
import com.kispoko.tome.engine.programming.summation.term.Term;
import com.kispoko.tome.engine.programming.summation.term.TermType;
import com.kispoko.tome.engine.programming.summation.term.TermUnion;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Summation
 */
public class Summation implements Model, Serializable {

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private CollectionValue<TermUnion> terms;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer sum;

    private Boolean hasDiceRoll;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Summation()
    {
        this.id = null;

        List<Class<? extends TermUnion>> termClasses = new ArrayList<>();
        termClasses.add(TermUnion.class);
        this.terms = CollectionValue.empty(termClasses);
    }


    public Summation(UUID id, List<TermUnion> terms)
    {
        this.id = id;

        List<Class<? extends TermUnion>> termClasses = new ArrayList<>();
        termClasses.add(TermUnion.class);
        this.terms = CollectionValue.full(terms, termClasses);

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
            throws SummationException
    {
        return sum();
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
    public List<String> variableDependencies() {
        List<String> variableNames = new ArrayList<>();

        for (TermUnion termUnion : this.terms.getValue()) {
            variableNames.addAll(termUnion.term().variableDependencies());
        }

        return variableNames;
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
     * Evaluate the sum of this summation.
     */
    private Integer sum()
            throws SummationException
    {
        Integer sum = 0;

        for (TermUnion termUnion : this.terms.getValue()) {
            sum += termUnion.term().value();
        }

        this.sum = sum;

        return this.sum;
    }


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

}
