
package com.kispoko.tome.engine.programming.summation.term;


import com.kispoko.tome.engine.variable.VariableException;
import com.kispoko.tome.engine.variable.VariableReference;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Summation Term: Conditional
 */
public class ConditionalTerm extends Term implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<BooleanTermValue> conditionalTermValue;
    private ModelFunctor<IntegerTermValue> whenTrueTermValue;
    private ModelFunctor<IntegerTermValue> whenFalseTermValue;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ConditionalTerm()
    {
        this.id                   = null;

        this.conditionalTermValue = ModelFunctor.empty(BooleanTermValue.class);
        this.whenTrueTermValue    = ModelFunctor.empty(IntegerTermValue.class);
        this.whenFalseTermValue   = ModelFunctor.empty(IntegerTermValue.class);
    }


    public ConditionalTerm(UUID id,
                           BooleanTermValue conditionalTermValue,
                           IntegerTermValue whenTrueTermValue,
                           IntegerTermValue whenFalseTermValue)
    {
        this.id                   = id;

        this.conditionalTermValue = ModelFunctor.full(conditionalTermValue, BooleanTermValue.class);
        this.whenTrueTermValue    = ModelFunctor.full(whenTrueTermValue, IntegerTermValue.class);
        this.whenFalseTermValue   = ModelFunctor.full(whenFalseTermValue, IntegerTermValue.class);
    }


    /**
     * Create a Conditional Term from its Yaml representation.
     * @param yaml The yaml parser.
     * @return A new Conditional Term.
     * @throws YamlParseException
     */
    public static ConditionalTerm fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID             id                   = UUID.randomUUID();

        BooleanTermValue conditionalTermValue = BooleanTermValue.fromYaml(
                                                                    yaml.atKey("conditional"));

        IntegerTermValue whenTrueTermValue    = IntegerTermValue.fromYaml(yaml.atKey("when_true"));
        IntegerTermValue whenFalseTermValue   = IntegerTermValue.fromYaml(yaml.atKey("when_false"));

        return new ConditionalTerm(id, conditionalTermValue, whenTrueTermValue, whenFalseTermValue);
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
     * The conditional term value. The term value that is true or false, and dictates the
     * value (integer) of the conditional term expression
     * @return The Boolean Term Value.
     */
    private BooleanTermValue conditionalTermValue()
    {
        return this.conditionalTermValue.getValue();
    }


    /**
     * The integer term value for when the conditional term value is true.
     * @return The integer term value.
     */
    private IntegerTermValue whenTrueTermValue()
    {
        return this.whenTrueTermValue.getValue();
    }


    /**
     * The integer term value for when the conditional term value is false.
     * @return The integer term value.
     */
    private IntegerTermValue whenFalseTermValue()
    {
        return this.whenFalseTermValue.getValue();
    }


    // > Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the value of the conditional term. If the condition variable is true
     * @return
     * @throws com.kispoko.tome.engine.variable.VariableException
     */
    public Integer value()
           throws VariableException
    {
        Boolean cond = conditionalTermValue().value();

        if (cond) {
            return whenTrueTermValue.getValue().value();
        }
        else {
            return whenFalseTermValue.getValue().value();
        }
    }


    /**
     * Get the variables that this term depends upon to calculate its value.
     * @return A list of variable names.
     */
    public List<VariableReference> variableDependencies()
    {
        List<VariableReference> variableReferences = new ArrayList<>();

        VariableReference conditionalVariableRef = this.conditionalTermValue().variableDependency();
        VariableReference whenTrueVariableRef    = this.whenTrueTermValue().variableDependency();
        VariableReference whenFalseVariableRef   = this.whenFalseTermValue().variableDependency();

        if (conditionalVariableRef != null)
            variableReferences.add(conditionalVariableRef);

        if (whenFalseVariableRef != null)
            variableReferences.add(whenFalseVariableRef);

        if (whenTrueVariableRef != null)
            variableReferences.add(whenTrueVariableRef);

        return variableReferences;
    }


}
