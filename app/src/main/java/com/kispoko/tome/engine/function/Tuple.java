
package com.kispoko.tome.engine.function;


import com.kispoko.tome.engine.EngineType;
import com.kispoko.tome.engine.EngineValueUnion;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;



/**
 * Tuple
 */
public class Tuple extends Model
                   implements ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                    id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private CollectionFunctor<EngineValueUnion>    parameters;
    private ModelFunctor<EngineValueUnion>         result;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Tuple()
    {
        this.id         = null;

        this.parameters = CollectionFunctor.empty(EngineValueUnion.class);

        this.result     = ModelFunctor.empty(EngineValueUnion.class);
    }


    public Tuple(UUID id, List<EngineValueUnion> parameters, EngineValueUnion result)
    {
        this.id         = id;

        this.parameters = CollectionFunctor.full(parameters, EngineValueUnion.class);

        this.result     = ModelFunctor.full(result, EngineValueUnion.class);

    }


    public static Tuple fromYaml(YamlParser yaml,
                                 final List<EngineType> parameterTypes,
                                 EngineType resultType)
                  throws YamlParseException
    {
        UUID id = UUID.randomUUID();

        // ** Parameters
        List<EngineValueUnion> parameters = yaml.atKey("parameters")
                                            .forEach(new YamlParser.ForEach<EngineValueUnion>() {
            @Override
            public EngineValueUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return EngineValueUnion.fromYaml(yaml, parameterTypes.get(index).dataType());
            }
        });

        // ** Result
        EngineValueUnion result =
                            EngineValueUnion.fromYaml(yaml.atKey("result"), resultType.dataType());

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
    public List<EngineValueUnion> parameters()
    {
        return this.parameters.getValue();
    }


    /**
     * Get the tuple result. Represents the result of one case of the function, determined
     * by the inputs.
     * @return EngineValueUnion result of the tuple.
     */
    public EngineValueUnion result()
    {
        return this.result.getValue();
    }


}

