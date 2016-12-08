
package com.kispoko.tome.engine.programming.program.statement;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Statement Parameter
 */
public class Parameter implements Model, Serializable
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private UUID                          id;

    private PrimitiveValue<Integer>       programParameter;
    private PrimitiveValue<String>        variableName;
    private PrimitiveValue<String>        literalString;

    private PrimitiveValue<ParameterType> type;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public Parameter()
    {
        this.id               = null;

        this.type             = new PrimitiveValue<>(null, ParameterType.class);

        this.programParameter = new PrimitiveValue<>(null, Integer.class);
        this.variableName     = new PrimitiveValue<>(null, String.class);
        this.literalString    = new PrimitiveValue<>(null, String.class);
    }


    /**
     * Create a Parameter as a sum type. This constructor is private to force use of the safe
     * constructors "as__" which help ensure that the type matches the object.
     * @param parameterValue
     * @param parameterType
     */
    private Parameter(UUID id, Object parameterValue, ParameterType parameterType)
    {
        this.id               = id;

        this.type             = new PrimitiveValue<>(parameterType, ParameterType.class);

        this.programParameter = new PrimitiveValue<>(null, Integer.class);
        this.variableName     = new PrimitiveValue<>(null, String.class);
        this.literalString    = new PrimitiveValue<>(null, String.class);

        switch (parameterType)
        {
            case PARAMETER:
                this.programParameter.setValue((Integer) parameterValue);
                break;
            case VARIABLE:
                this.variableName.setValue((String) parameterValue);
                break;
            case LITERAL_STRING:
                this.literalString.setValue((String) parameterValue);
                break;
        }
    }


    /**
     * Create a "program parameter" statement parameter.
     * @param programParameter The integer index of the program parameter to reference.
     * @return A new "program parameter" Parameter.
     */
    public static Parameter asParameter(UUID id, Integer programParameter)
    {
        return new Parameter(id, programParameter, ParameterType.PARAMETER);
    }


    /**
     * Create a "variable" statement parameter.
     * @param variableName The name of the program variable to reference.
     * @return A new "variable" Parameter.
     */
    public static Parameter asVariable(UUID id, String variableName)
    {
        return new Parameter(id, variableName, ParameterType.VARIABLE);
    }


    /**
     * Create a "string literal" statement parameter.
     * @param stringLiteral The string literal value.
     * @return A new "string literal" Parameter.
     */
    public static Parameter asStringLiteral(UUID id, String stringLiteral)
    {
        return new Parameter(id, stringLiteral, ParameterType.LITERAL_STRING);
    }


    /**
     * Create a Parameter from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return A new Parameter.
     * @throws YamlException
     */
    public static Parameter fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID          id   = UUID.randomUUID();
        ParameterType type = ParameterType.fromYaml(yaml.atKey("type"));

        Object value = null;
        switch (type)
        {
            case PARAMETER:
                value = yaml.atKey("value").getInteger();
                break;
            case VARIABLE:
                value = yaml.atKey("value").getString();
                break;
            case LITERAL_STRING:
                value = yaml.atKey("value").getString();
                break;
        }

        return new Parameter(id, value, type);
    }


    // API
    // --------------------------------------------------------------------------------------

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
     * This method is called when the Parameter is completely loaded for the first time.
     */
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------


    /**
     * Get the parameter type.
     * @return The ParameterType.
     */
    public ParameterType getType()
    {
        return this.type.getValue();
    }


    /**
     * Get the program parameter index.
     * @return The program parameter index.
     */
    public Integer getParameter()
    {
        if (this.getType() != ParameterType.PARAMETER) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("parameter", this.type.toString())));
        }
        return this.programParameter.getValue();
    }


    /**
     * If this parameter is a variable, get the variable name.
     * @return The variable name String.
     */
    public String getVariable()
    {
        if (this.getType() != ParameterType.VARIABLE) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("variable", this.type.toString())));
        }
        return this.variableName.getValue();
    }


    /**
     * If this parameter is a string literal, get the string literal value.
     * @return The string literal value.
     */
    public String getStringLiteral()
    {
        if (this.getType() != ParameterType.LITERAL_STRING) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("literal_string", this.type.toString())));
        }
        return this.literalString.getValue();
    }



}
