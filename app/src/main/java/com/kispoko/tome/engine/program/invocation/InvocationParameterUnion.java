
package com.kispoko.tome.engine.program.invocation;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Program Invocation Parameter
 */
public class InvocationParameterUnion implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                                      id;

    private PrimitiveFunctor<String>                  referenceValue;

    private PrimitiveFunctor<InvocationParameterType> type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public InvocationParameterUnion()
    {
        this.id             = null;

        this.referenceValue = new PrimitiveFunctor<>(null, String.class);
        this.type           = new PrimitiveFunctor<>(null, InvocationParameterType.class);
    }


    public InvocationParameterUnion(UUID id, Object value, InvocationParameterType type)
    {
        this.id             = id;

        this.referenceValue = new PrimitiveFunctor<>(null, String.class);

        this.type           = new PrimitiveFunctor<>(type, InvocationParameterType.class);

        // Set the value, depending on the case
        switch (type)
        {
            case REFERENCE:
                this.referenceValue.setValue((String) value);
                break;
        }
    }


    /**
     * Create a "reference" parameter, that references a value in another variable.
     * @param id The Model id.
     * @param reference The reference string.
     * @return A "reference" Program Inovcation parameter.
     */
    public static InvocationParameterUnion asReference(UUID id, String reference)
    {
        return new InvocationParameterUnion(id, reference,
                                              InvocationParameterType.REFERENCE);
    }


    /**
     * Create a new InvocationParameterUnion from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return A new InvocationParameterUnion.
     */
    public static InvocationParameterUnion fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID id = UUID.randomUUID();

        InvocationParameterType type =
                                    InvocationParameterType.fromYaml(yaml.atKey("type"));

        switch (type)
        {
            case REFERENCE:
                String referenceValue = yaml.atKey("value").getString();
                return InvocationParameterUnion.asReference(id, referenceValue);
        }

        return null;
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
     * This method is called when the Program Invocation Parameter is completely loaded
     * for the first time.
     */
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Parameter ErrorType
    // ------------------------------------------------------------------------------------------

    /**
     * Get the type of program invocation parameter.
     * @return The Program Invocation Parameter ErrorType.
     */
    public InvocationParameterType type()
    {
        return this.type.getValue();
    }


    // ** Reference
    // ------------------------------------------------------------------------------------------

    /**
     * Get the reference case.
     * @return The reference value (a variable name).
     */
    public String reference()
    {
        return this.referenceValue.getValue();
    }

}
