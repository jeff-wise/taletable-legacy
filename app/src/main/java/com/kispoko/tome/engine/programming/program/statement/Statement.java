
package com.kispoko.tome.engine.programming.program.statement;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Statement
 */
public class Statement implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                            id;

    private PrimitiveFunctor<String>        variableName;
    private PrimitiveFunctor<String>        functionName;
    private CollectionFunctor<Parameter>    parameters;

    public static final int MAX_PARAMETERS = 3;


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
     * @throws YamlParseException
     */
    public static Statement fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID   id           = UUID.randomUUID();

        String variableName = yaml.atKey("let").getString();
        String functionName = yaml.atKey("function").getString();

        List<Parameter> parameters = yaml.atKey("parameters")
                                         .forEach(new YamlParser.ForEach<Parameter>() {
            @Override
            public Parameter forEach(YamlParser yaml, int index) throws YamlParseException {
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


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The statement's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("let", this.variableName())
                .putString("function", this.functionName())
                .putList("parameters", this.parameters());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the name of the variable that this statements assigns a value to.
     * @return The statement's variable name.
     */
    public String variableName()
    {
        return this.variableName.getValue();
    }


    /**
     * Get the name of the function called by the statement. The result of this function is
     * assigned to the statement's variable.
     * @return The function name String.
     */
    public String functionName()
    {
        return this.functionName.getValue();
    }


    /**
     * Get the parameters of this statement. These parameters are used to evaluate the function
     * and calcuate the result of the variable assigned by the statement.
     * @return The statement Parameter List.
     */
    public List<Parameter> parameters()
    {
        return this.parameters.getValue();
    }


    // > Arity
    // ------------------------------------------------------------------------------------------

    /**
     * The number of parameters the statement takes.
     * @return The arity.
     */
    public int arity()
    {
        return this.parameters().size();
    }

}

