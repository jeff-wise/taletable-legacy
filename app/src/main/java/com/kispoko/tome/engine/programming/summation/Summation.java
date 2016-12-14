
package com.kispoko.tome.engine.programming.summation;


import com.kispoko.tome.engine.programming.summation.term.ConditionalTerm;
import com.kispoko.tome.engine.programming.summation.term.LiteralTerm;
import com.kispoko.tome.engine.programming.summation.term.Term;
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
public class Summation implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                  id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private CollectionValue<Term> terms;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                sum;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Summation()
    {
        this.id = null;

        List<Class<? extends Term>> termClasses = new ArrayList<>();
        termClasses.add(LiteralTerm.class);
        termClasses.add(ConditionalTerm.class);
        this.terms = CollectionValue.empty(termClasses);
    }


    public Summation(UUID id, List<Term> terms)
    {
        this.id    = id;

        List<Class<? extends Term>> termClasses = new ArrayList<>();
        termClasses.add(LiteralTerm.class);
        termClasses.add(ConditionalTerm.class);
        this.terms = CollectionValue.full(terms, termClasses);
    }


    public static Summation fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID       id    = UUID.randomUUID();

        List<Term> terms = yaml.atKey("terms").forEach(new Yaml.ForEach<Term>() {
            @Override
            public Term forEach(Yaml yaml, int index) throws YamlException {
                return Term.fromYaml(yaml);
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


    // > Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the summation value.
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

    // ** Dependencies
    // ------------------------------------------------------------------------------------------

    /**
     * Get the names of all of the variables that the summation depends on to calculate its value.
     * @return A list of variable names.
     */
    public List<String> variableDependencies()
    {
        List<String> variableNames = new ArrayList<>();

        for (Term term : this.terms.getValue())
        {
            variableNames.addAll(term.variableDependencies());
        }

        return variableNames;
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

        for (Term term : this.terms.getValue())
        {
            sum += term.value();
        }

        this.sum = sum;

        return this.sum;
    }

}
