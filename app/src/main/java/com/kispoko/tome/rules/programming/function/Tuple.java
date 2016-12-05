
package com.kispoko.tome.rules.programming.function;


import com.kispoko.tome.rules.programming.program.ProgramValue;
import com.kispoko.tome.rules.programming.program.ProgramValueType;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Tuple
 */
public class Tuple implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                          id;

    private CollectionValue<ProgramValue> parameters;
    private ModelValue<ProgramValue>      result;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Tuple()
    {
        this.id         = null;

        List<Class<? extends ProgramValue>> programValueClasses = new ArrayList<>();
        programValueClasses.add(ProgramValue.class);
        this.parameters = CollectionValue.empty(programValueClasses);

        this.result     = ModelValue.empty(ProgramValue.class);
    }


    public Tuple(UUID id, List<ProgramValue> parameters, ProgramValue result)
    {
        this.id         = id;

        List<Class<? extends ProgramValue>> programValueClasses = new ArrayList<>();
        programValueClasses.add(ProgramValue.class);
        this.parameters = CollectionValue.full(parameters, programValueClasses);

        this.result     = ModelValue.full(result, ProgramValue.class);

    }


    public static Tuple fromYaml(Yaml yaml,
                                 final List<ProgramValueType> parameterTypes,
                                 ProgramValueType resultType)
                  throws YamlException
    {
        UUID id = UUID.randomUUID();

        // ** Parameters
        List<ProgramValue> parameters = yaml.atKey("parameters")
                                            .forEach(new Yaml.ForEach<ProgramValue>() {
            @Override
            public ProgramValue forEach(Yaml yaml, int index) throws YamlException {
                return ProgramValue.fromYaml(yaml, parameterTypes.get(index));
            }
        });

        // ** Result
        ProgramValue result = ProgramValue.fromYaml(yaml.atKey("result"), resultType);

        return new Tuple(id, parameters, result);
    }


    // > API
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
     * This method is called when the Tuple is completely loaded for the first time.
     */
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the tuple parameters. These represent the input to one case of a function.
     * @return List of ordered tuple parameters.
     */
    public List<ProgramValue> getParameters()
    {
        return this.parameters.getValue();
    }


    /**
     * Get the tuple result. Represents the result of one case of the function, determined
     * by the inputs.
     * @return ProgramValue result of the tuple.
     */
    public ProgramValue getResult()
    {
        return this.result.getValue();
    }

}

