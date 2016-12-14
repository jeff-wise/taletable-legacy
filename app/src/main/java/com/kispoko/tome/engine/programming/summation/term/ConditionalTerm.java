
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

    private ModelValue<BooleanTermValue> conditionalTermValue;
    private ModelValue<IntegerTermValue> whenTrueTermValue;
    private ModelValue<IntegerTermValue> whenFalseTermValue;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ConditionalTerm()
    {
        this.id                   = null;

        this.conditionalTermValue = ModelValue.empty(BooleanTermValue.class);
        this.whenTrueTermValue    = ModelValue.empty(IntegerTermValue.class);
        this.whenFalseTermValue   = ModelValue.empty(IntegerTermValue.class);
    }


    public ConditionalTerm(UUID id,
                           BooleanTermValue conditionalTermValue,
                           IntegerTermValue whenTrueTermValue,
                           IntegerTermValue whenFalseTermValue)
    {
        this.id                   = id;

        this.conditionalTermValue = ModelValue.full(conditionalTermValue, BooleanTermValue.class);
        this.whenTrueTermValue    = ModelValue.full(whenTrueTermValue, IntegerTermValue.class);
        this.whenFalseTermValue   = ModelValue.full(whenFalseTermValue, IntegerTermValue.class);
    }


    /**
     * Create a Conditional Term from its Yaml representation.
     * @param yaml The yaml parser.
     * @return A new Conditional Term.
     * @throws YamlException
     */
    public static ConditionalTerm fromYaml(Yaml yaml)
                  throws YamlException
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


    // > Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the value of the conditional term. If the condition variable is true
     * @return
     * @throws SummationException
     */
    public Integer value()
           throws SummationException
    {
        Boolean cond = conditionalTermValue.getValue().value();

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
    public List<String> variableDependencies()
    {
        List<String> variableNames = new ArrayList<>();

        String conditionalVariableName = this.conditionalTermValue.getValue().variableName();
        String whenTrueVariableName    = this.whenTrueTermValue.getValue().variableName();
        String whenFalseVariableName   = this.whenFalseTermValue.getValue().variableName();

        if (conditionalVariableName != null)
            variableNames.add(conditionalVariableName);

        if (whenTrueVariableName != null)
            variableNames.add(whenTrueVariableName);

        if (whenFalseVariableName != null)
            variableNames.add(whenFalseVariableName);

        return variableNames;
    }


}
