
package com.kispoko.tome.engine.program.invocation;


import com.kispoko.tome.engine.variable.VariableReference;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Program Invocation
 */
public class Invocation implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private PrimitiveFunctor<String> programName;
    private CollectionFunctor<InvocationParameterUnion> parameters;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Invocation()
    {
        this.id = null;

        this.programName = new PrimitiveFunctor<>(null, String.class);

        List<Class<? extends InvocationParameterUnion>> parameterClasses
                                                = new ArrayList<>();
        parameterClasses.add(InvocationParameterUnion.class);
        this.parameters  = CollectionFunctor.empty(parameterClasses);
    }


    public Invocation(UUID id,
                      String programName,
                      List<InvocationParameterUnion> parameters)
    {
        this.id = id;

        this.programName = new PrimitiveFunctor<>(programName, String.class);

        List<Class<? extends InvocationParameterUnion>> parameterClasses
                                                = new ArrayList<>();
        parameterClasses.add(InvocationParameterUnion.class);
        this.parameters  = CollectionFunctor.full(parameters, parameterClasses);
    }


    public static Invocation fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID   id          = UUID.randomUUID();
        String programName = yaml.atKey("program").getString();

        List<InvocationParameterUnion> parameters =
                yaml.atKey("parameters").forEach(new YamlParser.ForEach<InvocationParameterUnion>() {
            @Override
            public InvocationParameterUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return InvocationParameterUnion.fromYaml(yaml);
            }
        });

        return new Invocation(id, programName, parameters);
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
     * This method is called when the Program Invocation is completely loaded for the first time.
     */
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Program Name
    // ------------------------------------------------------------------------------------------

    /**
     * Get the name of the program to be invoked.
     * @return The program name.
     */
    public String programName()
    {
        return this.programName.getValue();
    }


    // ** Parameters
    // ------------------------------------------------------------------------------------------

    /**
     * Get the program parameters.
     * @return A List of the program's parameters.
     */
    public List<InvocationParameterUnion> parameters()
    {
        return this.parameters.getValue();
    }


    // ** Dependencies
    // ------------------------------------------------------------------------------------------

    /**
     * Get the list of variables that this program invocation depends on.
     * @return A list of variable names.
     */
    public List<VariableReference> variableDependencies()
    {
        List<VariableReference> variableReferences = new ArrayList<>();

        for (InvocationParameterUnion parameter : this.parameters())
        {
            switch (parameter.type())
            {
                case REFERENCE:
                    variableReferences.add(VariableReference.asByName(parameter.reference()));
                    break;
            }
        }

        return variableReferences;
    }

}