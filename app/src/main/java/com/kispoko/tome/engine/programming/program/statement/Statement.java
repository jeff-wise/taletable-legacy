
package com.kispoko.tome.engine.programming.program.statement;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Statement
 */
public class Statement implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                       id;

    private PrimitiveFunctor<String> variableName;
    private PrimitiveFunctor<String> functionName;
    private CollectionFunctor<Parameter> parameters;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Statement()
    {
        this.id           = null;

        this.variableName = new PrimitiveFunctor<>(null, String.class);
        this.functionName = new PrimitiveFunctor<>(null, String.class);

        List<Class<? extends Parameter>> parameterClasses = new ArrayList<>();
        parameterClasses.add(Parameter.class);
        this.parameters   = CollectionFunctor.empty(parameterClasses);
    }


    public Statement(UUID id, String variableName, String functionName, List<Parameter> parameters)
    {
        this.id           = id;

        this.variableName = new PrimitiveFunctor<>(variableName, String.class);
        this.functionName = new PrimitiveFunctor<>(functionName, String.class);

        List<Class<? extends Parameter>> parameterClasses = new ArrayList<>();
        parameterClasses.add(Parameter.class);
        this.parameters   = CollectionFunctor.full(parameters, parameterClasses);
    }


    /**
     * Create a new Statement from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return A new Statement.
     * @throws YamlException
     */
    public static Statement fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID   id           = UUID.randomUUID();

        String variableName = yaml.atKey("let").getString();
        String functionName = yaml.atKey("function").getString();

        List<Parameter> parameters = yaml.atKey("parameters")
                                         .forEach(new Yaml.ForEach<Parameter>() {
            @Override
            public Parameter forEach(Yaml yaml, int index) throws YamlException {
                return Parameter.fromYaml(yaml);
            }
        });

        return new Statement(id, variableName, functionName, parameters);
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
     * This method is called when the Statement is completely loaded for the first time.
     */
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the name of the variable that this statements assigns a value to.
     * @return The statement's variable name.
     */
    public String getVariableName()
    {
        return this.variableName.getValue();
    }


    /**
     * Get the name of the function called by the statement. The result of this function is
     * assigned to the statement's variable.
     * @return The function name String.
     */
    public String getFunctionName()
    {
        return this.functionName.getValue();
    }


    /**
     * Get the parameters of this statement. These parameters are used to evaluate the function
     * and calcuate the result of the variable assigned by the statement.
     * @return The statement Parameter List.
     */
    public List<Parameter> getParameters()
    {
        return this.parameters.getValue();
    }


}

