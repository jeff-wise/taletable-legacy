
package com.kispoko.tome.rules.programming.program;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.UUID;


/**
 * Program Invocation Parameter
 */
public class ProgramInvocationParameter implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                                           id;

    private PrimitiveValue<String>                         referenceValue;

    private PrimitiveValue<ProgramInvocationParameterType> type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ProgramInvocationParameter()
    {
        this.id             = null;

        this.referenceValue = new PrimitiveValue<>(null, String.class);
        this.type           = new PrimitiveValue<>(null, ProgramInvocationParameterType.class);
    }


    public ProgramInvocationParameter(UUID id, Object value, ProgramInvocationParameterType type)
    {
        this.id             = id;

        this.referenceValue = new PrimitiveValue<>(null, String.class);

        this.type           = new PrimitiveValue<>(type, ProgramInvocationParameterType.class);

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
    public static ProgramInvocationParameter asReference(UUID id, String reference)
    {
        return new ProgramInvocationParameter(id, reference,
                                              ProgramInvocationParameterType.REFERENCE);
    }


    /**
     * Create a new ProgramInvocationParameter from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return A new ProgramInvocationParameter.
     */
    public static ProgramInvocationParameter fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID id = UUID.randomUUID();

        ProgramInvocationParameterType type =
                                    ProgramInvocationParameterType.fromYaml(yaml.atKey("type"));

        switch (type)
        {
            case REFERENCE:
                String referenceValue = yaml.atKey("value").getString();
                return ProgramInvocationParameter.asReference(id, referenceValue);
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

    // ** Parameter Type
    // ------------------------------------------------------------------------------------------

    /**
     * Get the type of program invocation parameter.
     * @return The Program Invocation Parameter Type.
     */
    public ProgramInvocationParameterType getType()
    {
        return this.type.getValue();
    }


    // ** Reference
    // ------------------------------------------------------------------------------------------

    /**
     * When the parameter is the REFERENCE case, get the reference parameter (a variable name).
     * @return The reference value.
     */
    public String getReference()
    {
        return this.referenceValue.getValue();
    }

}
