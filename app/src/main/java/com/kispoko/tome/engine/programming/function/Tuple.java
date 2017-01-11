
package com.kispoko.tome.engine.programming.function;


import com.kispoko.tome.engine.programming.program.ProgramValueUnion;
import com.kispoko.tome.engine.programming.program.ProgramValueType;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Tuple
 */
public class Tuple implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                    id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private CollectionFunctor<ProgramValueUnion>    parameters;
    private ModelFunctor<ProgramValueUnion>         result;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Tuple()
    {
        this.id         = null;

        List<Class<? extends ProgramValueUnion>> programValueClasses = new ArrayList<>();
        programValueClasses.add(ProgramValueUnion.class);
        this.parameters = CollectionFunctor.empty(programValueClasses);

        this.result     = ModelFunctor.empty(ProgramValueUnion.class);
    }


    public Tuple(UUID id, List<ProgramValueUnion> parameters, ProgramValueUnion result)
    {
        this.id         = id;

        List<Class<? extends ProgramValueUnion>> programValueClasses = new ArrayList<>();
        programValueClasses.add(ProgramValueUnion.class);
        this.parameters = CollectionFunctor.full(parameters, programValueClasses);

        this.result     = ModelFunctor.full(result, ProgramValueUnion.class);

    }


    public static Tuple fromYaml(YamlParser yaml,
                                 final List<ProgramValueType> parameterTypes,
                                 ProgramValueType resultType)
                  throws YamlParseException
    {
        UUID id = UUID.randomUUID();

        // ** Parameters
        List<ProgramValueUnion> parameters = yaml.atKey("parameters")
                                            .forEach(new YamlParser.ForEach<ProgramValueUnion>() {
            @Override
            public ProgramValueUnion forEach(YamlParser yaml, int index) throws YamlParseException {
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


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The tuple's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putList("parameters", this.parameters())
                .putYaml("result", this.result());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the tuple parameters. These represent the input to one case of a function.
     * @return List of ordered tuple parameters.
     */
    public List<ProgramValueUnion> parameters()
    {
        return this.parameters.getValue();
    }


    /**
     * Get the tuple result. Represents the result of one case of the function, determined
     * by the inputs.
     * @return ProgramValueUnion result of the tuple.
     */
    public ProgramValueUnion result()
    {
        return this.result.getValue();
    }

}

