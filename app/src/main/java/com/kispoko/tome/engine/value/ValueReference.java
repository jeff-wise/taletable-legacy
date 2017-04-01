
package com.kispoko.tome.engine.value;


import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Value Reference
 */
public class ValueReference implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                     id;

    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String> valueSetName;
    private PrimitiveFunctor<String> valueName;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ValueReference()
    {
        this.id             = null;

        this.valueSetName   = new PrimitiveFunctor<>(null, String.class);
        this.valueName      = new PrimitiveFunctor<>(null, String.class);
    }


    public ValueReference(UUID id, String valueSetName, String valueName)
    {
        this.id             = id;

        this.valueSetName   = new PrimitiveFunctor<>(valueSetName, String.class);
        this.valueName      = new PrimitiveFunctor<>(valueName, String.class);
    }


    /**
     * Non-persistent constructor. Sets an ID just in case.
     * @param valueSetName The value set name.
     * @param valueName The value name.
     */
    public ValueReference(String valueSetName, String valueName)
    {
        this.id             = UUID.randomUUID();

        this.valueSetName   = new PrimitiveFunctor<>(valueSetName, String.class);
        this.valueName      = new PrimitiveFunctor<>(valueName, String.class);
    }


    /**
     * Create a Value Reference from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Value Reference.
     * @throws YamlParseException
     */
    public static ValueReference fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID   id           = UUID.randomUUID();

        String valueSetName = yaml.atKey("set_name").getString();
        String valueName    = yaml.atKey("value_name").getString();

        return new ValueReference(id, valueSetName, valueName);
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

    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The value set name of the referenced value.
     * @return The value set name.
     */
    public String valueSetName()
    {
        return this.valueSetName.getValue();
    }


    /**
     * The name of the referenced value.
     * @return The value name.
     */
    public String valueName()
    {
        return this.valueName.getValue();
    }

}
