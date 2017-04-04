
package com.kispoko.tome.engine.program.statement;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Statement Parameter
 */
public class Parameter extends Model
                       implements ToYaml, Serializable
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    // > Model
    // --------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // --------------------------------------------------------------------------------------

    private PrimitiveFunctor<Integer>       programParameter;
    private PrimitiveFunctor<String>        variableName;
    private PrimitiveFunctor<String>        literalString;

    private PrimitiveFunctor<ParameterType> type;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public Parameter()
    {
        this.id               = null;

        this.type             = new PrimitiveFunctor<>(null, ParameterType.class);

        this.programParameter = new PrimitiveFunctor<>(null, Integer.class);
        this.variableName     = new PrimitiveFunctor<>(null, String.class);
        this.literalString    = new PrimitiveFunctor<>(null, String.class);
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

        this.type             = new PrimitiveFunctor<>(parameterType, ParameterType.class);

        this.programParameter = new PrimitiveFunctor<>(null, Integer.class);
        this.variableName     = new PrimitiveFunctor<>(null, String.class);
        this.literalString    = new PrimitiveFunctor<>(null, String.class);

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
     * @throws YamlParseException
     */
    public static Parameter fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID          id   = UUID.randomUUID();

        ParameterType type = ParameterType.fromYaml(yaml.atKey("type"));

        switch (type)
        {
            case PARAMETER:
                Integer parameterIndex = yaml.atKey("value").getInteger();
                return Parameter.asParameter(id, parameterIndex);
            case VARIABLE:
                String variableName = yaml.atKey("value").getString();
                return Parameter.asVariable(id, variableName);
            case LITERAL_STRING:
                String stringValue = yaml.atKey("value").getString();
                return Parameter.asStringLiteral(id, stringValue);
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(ParameterType.class.getName())));
        }

        return null;
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


    // > To Yaml
    // --------------------------------------------------------------------------------------

    /**
     * The parameter's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        YamlBuilder valueYaml = null;

        switch (this.type())
        {
            case PARAMETER:
                valueYaml = YamlBuilder.integer(this.parameter());
                break;
            case VARIABLE:
                valueYaml = YamlBuilder.string(this.variable());
                break;
            case LITERAL_STRING:
                valueYaml = YamlBuilder.string(this.stringLiteral());
                break;
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(ParameterType.class.getName())));
        }

        return YamlBuilder.map()
                .putYaml("type", this.type())
                .putYaml("value", valueYaml);
    }


    // > State
    // ------------------------------------------------------------------------------------------


    /**
     * Get the parameter type.
     * @return The ParameterType.
     */
    public ParameterType type()
    {
        return this.type.getValue();
    }


    /**
     * Get the program parameter index.
     * @return The program parameter index.
     */
    public Integer parameter()
    {
        if (this.type() != ParameterType.PARAMETER) {
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
    public String variable()
    {
        if (this.type() != ParameterType.VARIABLE) {
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
    public String stringLiteral()
    {
        if (this.type() != ParameterType.LITERAL_STRING) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("literal_string", this.type.toString())));
        }
        return this.literalString.getValue();
    }


    // > String Representations
    // --------------------------------------------------------------------------------------

    /**
     * The parameter value as a string.
     * @return The value string.
     */
    public String valueString()
    {
        switch (this.type())
        {
            case PARAMETER:
                return this.parameter().toString();
            case VARIABLE:
                return this.variable();
            case LITERAL_STRING:
                return this.stringLiteral();
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(ParameterType.class.getName())));
        }

        return "";
    }


    /**
     * The parameter's type as a "pretty" string.
     * @return The type string.
     */
    public String typeString()
    {
        return this.type().toString();
    }

}
