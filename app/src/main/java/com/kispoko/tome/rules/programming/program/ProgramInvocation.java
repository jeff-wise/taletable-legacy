
package com.kispoko.tome.rules.programming.program;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Program Invocation
 */
public class ProgramInvocation implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private PrimitiveValue<String> programName;
    private CollectionValue<ProgramInvocationParameter> parameters;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ProgramInvocation()
    {
        this.id = null;

        this.programName = new PrimitiveValue<>(null, String.class);

        List<Class<? extends ProgramInvocationParameter>> parameterClasses
                                                = new ArrayList<>();
        parameterClasses.add(ProgramInvocationParameter.class);
        this.parameters  = new CollectionValue<>(null, parameterClasses);
    }


    public ProgramInvocation(UUID id,
                             String programName,
                             List<ProgramInvocationParameter> parameters)
    {
        this.id = id;

        this.programName = new PrimitiveValue<>(programName, String.class);

        List<Class<? extends ProgramInvocationParameter>> parameterClasses
                                                = new ArrayList<>();
        parameterClasses.add(ProgramInvocationParameter.class);
        this.parameters  = new CollectionValue<>(parameters, parameterClasses);
    }


    public static ProgramInvocation fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID   id          = UUID.randomUUID();
        String programName = yaml.atKey("program").getString();

        List<ProgramInvocationParameter> parameters =
                yaml.atKey("parameters").forEach(new Yaml.ForEach<ProgramInvocationParameter>() {
            @Override
            public ProgramInvocationParameter forEach(Yaml yaml, int index) throws YamlException {
                return ProgramInvocationParameter.fromYaml(yaml);
            }
        });

        return new ProgramInvocation(id, programName, parameters);
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

    public void onValueUpdate(String valueName) { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Program Name
    // ------------------------------------------------------------------------------------------

    /**
     * Get the name of the program to be invoked.
     * @return The program name.
     */
    public String getProgramName()
    {
        return this.programName.getValue();
    }


    // ** Parameters
    // ------------------------------------------------------------------------------------------

    /**
     * Get the program parameters.
     * @return A List of the program's parameters.
     */
    public List<ProgramInvocationParameter> getParameters()
    {
        return this.parameters.getValue();
    }

}
