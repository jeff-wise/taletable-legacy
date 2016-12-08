
package com.kispoko.tome.engine.programming.function;


import com.kispoko.tome.engine.programming.program.ProgramValueUnion;
import com.kispoko.tome.engine.programming.program.ProgramValueType;
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

    private CollectionValue<ProgramValueUnion> parameters;
    private ModelValue<ProgramValueUnion>      result;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Tuple()
    {
        this.id         = null;

        List<Class<? extends ProgramValueUnion>> programValueClasses = new ArrayList<>();
        programValueClasses.add(ProgramValueUnion.class);
        this.parameters = CollectionValue.empty(programValueClasses);

        this.result     = ModelValue.empty(ProgramValueUnion.class);
    }


    public Tuple(UUID id, List<ProgramValueUnion> parameters, ProgramValueUnion result)
    {
        this.id         = id;

        List<Class<? extends ProgramValueUnion>> programValueClasses = new ArrayList<>();
        programValueClasses.add(ProgramValueUnion.class);
        this.parameters = CollectionValue.full(parameters, programValueClasses);

        this.result     = ModelValue.full(result, ProgramValueUnion.class);

    }


    public static Tuple fromYaml(Yaml yaml,
                                 final List<ProgramValueType> parameterTypes,
                                 ProgramValueType resultType)
                  throws YamlException
    {
        UUID id = UUID.randomUUID();

        // ** Parameters
        List<ProgramValueUnion> parameters = yaml.atKey("parameters")
                                            .forEach(new Yaml.ForEach<ProgramValueUnion>() {
            @Override
            public ProgramValueUnion forEach(Yaml yaml, int index) throws YamlException {
                return ProgramValueUnion.fromYaml(yaml, parameterTypes.get(index));
            }
        });

        // ** Result
        ProgramValueUnion result = ProgramValueUnion.fromYaml(yaml.atKey("result"), resultType);

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
    public List<ProgramValueUnion> getParameters()
    {
        return this.parameters.getValue();
    }


    /**
     * Get the tuple result. Represents the result of one case of the function, determined
     * by the inputs.
     * @return ProgramValueUnion result of the tuple.
     */
    public ProgramValueUnion getResult()
    {
        return this.result.getValue();
    }

}

