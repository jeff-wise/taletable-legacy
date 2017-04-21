
package com.kispoko.tome.engine.summation.term;


import com.kispoko.tome.engine.summation.SummationException;
import com.kispoko.tome.engine.summation.error.SummationVariableError;
import com.kispoko.tome.engine.variable.VariableException;
import com.kispoko.tome.engine.variable.VariableReference;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.util.tuple.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Summation Term: Literal
 */
public class IntegerTerm extends Term implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<IntegerTermValue>  termValue;
    private PrimitiveFunctor<String>        name;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public IntegerTerm()
    {
        this.id         = null;

        this.termValue  = ModelFunctor.empty(IntegerTermValue.class);
        this.name       = new PrimitiveFunctor<>(null, String.class);
    }


    /**
     * Create an Integer Term value
     * @param id The model id.
     * @param termValue The Integer Term Value.
     * @param name The term name. Used mostly when the term value consists of multiple variables.
     */
    public IntegerTerm(UUID id, IntegerTermValue termValue, String name)
    {
        this.id         = id;

        this.termValue  = ModelFunctor.full(termValue, IntegerTermValue.class);
        this.name       = new PrimitiveFunctor<>(name, String.class);
    }


    /**
     * Create an Integer Term from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The Integer Term.
     * @throws YamlParseException
     */
    public static IntegerTerm fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID             id        = UUID.randomUUID();

        IntegerTermValue termValue = IntegerTermValue.fromYaml(yaml.atKey("value"));
        String           name      = yaml.atMaybeKey("name").getString();

        return new IntegerTerm(id, termValue, name);
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


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The Integer Term's name.
     * @return The name.
     */
    public String name()
    {
        //return this.name.getValue();
        return this.termValue().name();
    }


    /**
     * The integer term's value.
     * @return The integer term value.
     */
    private IntegerTermValue termValue()
    {
        return this.termValue.getValue();
    }


    public IntegerTermValue.Type termValueType()
    {
        return this.termValue().type();
    }


    // > Term
    // ------------------------------------------------------------------------------------------

    /**
     * Get the term value. The returned value is just the value of the referenced variable.
     * @return The term value. Throws SummationException if the variable is invalid.
     */
    public Integer value()
           throws SummationException
    {
        try {
            return termValue().value();
        }
        catch (VariableException exception) {
            throw SummationException.variable(new SummationVariableError(exception));
        }
    }


    /**
     * Get the variables that this term depends upon to calculate its value.
     * @return A list of variable names.
     */
    public List<VariableReference> variableDependencies()
    {
        List<VariableReference> variableReferences = new ArrayList<>();

        VariableReference variableReference = this.termValue().variableDependency();

        if (variableReference != null)
            variableReferences.add(variableReference);

        return variableReferences;
    }


    // > Summary
    // ------------------------------------------------------------------------------------------

    /**
     * A summary of the terms variables.
     * @return The list of 2-tuples (value, description) of each of the term's variables.
     */
    public TermSummary summary()
    {
        // > Convert the component integers to strings
        List<Tuple2<String,Integer>> components = this.termValue().components();
        List<Tuple2<String,String>> componentsWithStringValue = new ArrayList<>();

        for (Tuple2<String,Integer> component : components)
        {
            Tuple2<String,String> comp = new Tuple2<>(component.getItem1(),
                                                      component.getItem2().toString());
            componentsWithStringValue.add(comp);
        }

        return new TermSummary(this.name(), componentsWithStringValue);
    }

}
