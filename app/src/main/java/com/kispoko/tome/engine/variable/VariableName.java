
package com.kispoko.tome.engine.variable;


import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Variable Name
 */
public class VariableName implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String> namespace;
    private PrimitiveFunctor<String> name;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public VariableName()
    {
        this.id         = null;

        this.namespace  = new PrimitiveFunctor<>(null, String.class);
        this.name       = new PrimitiveFunctor<>(null, String.class);
    }


    public VariableName(UUID id, String namespace, String name)
    {
        this.id         = null;

        this.namespace  = new PrimitiveFunctor<>(namespace, String.class);
        this.name       = new PrimitiveFunctor<>(name, String.class);
    }


    public static VariableName fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID   id        = UUID.randomUUID();

        String namespace = yaml.atMaybeKey("namespace").getString();
        String name      = yaml.atKey("name").getString();

        return new VariableName(id, namespace, name);
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
     * This method is called when the Table Widget is completely loaded for the first time.
     */
    public void onLoad() { }

}
