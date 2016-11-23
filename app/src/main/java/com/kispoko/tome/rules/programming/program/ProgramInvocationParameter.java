
package com.kispoko.tome.rules.programming.program;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.UUID;

import static android.R.attr.value;


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

    public ProgramInvocationParameter() { }


    public ProgramInvocationParameter(UUID id, Object value, ProgramInvocationParameterType type)
    {
        this.id             = id;

        this.referenceValue = new PrimitiveValue<>(null, this, String.class);

        this.type           = new PrimitiveValue<>(type, this,
                                                   ProgramInvocationParameterType.class);

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


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onModelUpdate(String valueName) { }

}
