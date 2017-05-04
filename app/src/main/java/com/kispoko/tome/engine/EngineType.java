
package com.kispoko.tome.engine;


import com.kispoko.tome.lib.functor.OptionFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Engine Type
 */
public class EngineType extends Model
                        implements ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private OptionFunctor<EngineDataType>   dataType;
    private PrimitiveFunctor<String>        shortName;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public EngineType()
    {
        this.id         = null;

        this.dataType   = new OptionFunctor<>(null, EngineDataType.class);
        this.shortName  = new PrimitiveFunctor<>(null, String.class);
    }


    public EngineType(UUID id, EngineDataType dataType, String shortName)
    {
        this.id         = id;

        this.dataType   = new OptionFunctor<>(dataType, EngineDataType.class);
        this.shortName  = new PrimitiveFunctor<>(shortName, String.class);
    }


    /**
     * Create an Engine Type from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Engine Type.
     * @throws YamlParseException
     */
    public static EngineType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID           id       = UUID.randomUUID();

        EngineDataType dataType = EngineDataType.fromYaml(yaml.atKey("type"));
        String         name     = yaml.atMaybeKey("short_name").getString();

        return new EngineType(id, dataType, name);
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
     * This method is called when the Function is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The function's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("type", this.dataType())
                .putString("name", this.shortName());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The data type.
     * @return The data type.
     */
    public EngineDataType dataType()
    {
        return this.dataType.getValue();
    }


    /**
     * The type name.
     * @return The type name.
     */
    public String shortName()
    {
        return this.shortName.getValue();
    }
}
