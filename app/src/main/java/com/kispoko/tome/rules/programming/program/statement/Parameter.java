
package com.kispoko.tome.rules.programming.program.statement;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.UUID;



/**
 * Statement Parameter
 */
public class Parameter implements Model
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

    public Parameter() { }


    /**
     * Create a Parameter as a sum type. This constructor is private to force use of the safe
     * constructors "as__" which help ensure that the type matches the object.
     * @param parameterValue
     * @param parameterType
     */
    private Parameter(Object parameterValue, ParameterType parameterType)
    {
        this.type             = new PrimitiveValue<>(parameterType, this, ParameterType.class);

        this.programParameter = new PrimitiveValue<>(null, this, Integer.class);
        this.variableName     = new PrimitiveValue<>(null, this, String.class);
        this.literalString    = new PrimitiveValue<>(null, this, String.class);

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
    public static Parameter asParameter(Integer programParameter)
    {
        return new Parameter(programParameter, ParameterType.PARAMETER);
    }


    /**
     * Create a "variable" statement parameter.
     * @param variableName The name of the program variable to reference.
     * @return A new "variable" Parameter.
     */
    public static Parameter asVariable(String variableName)
    {
        return new Parameter(variableName, ParameterType.VARIABLE);
    }


    /**
     * Create a "string literal" statement parameter.
     * @param stringLiteral The string literal value.
     * @return A new "string literal" Parameter.
     */
    public static Parameter asStringLiteral(String stringLiteral)
    {
        return new Parameter(stringLiteral, ParameterType.LITERAL_STRING);
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

        return new Parameter(value, type);
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


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onModelUpdate(String valueName) { }


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
        return this.programParameter.getValue();
    }


    /**
     * If this parameter is a variable, get the variable name.
     * @return The variable name String.
     */
    public String getVariable()
    {
        return this.variableName.getValue();
    }


    /**
     * If this parameter is a string literal, get the string literal value.
     * @return The string literal value.
     */
    public String getStringLiteral()
    {
        return this.literalString.getValue();
    }



}
