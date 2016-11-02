
package com.kispoko.tome.rules;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Statement
 */
public class Statement
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    String variableName;
    String functionName;
    List<Parameter> parameters;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Statement(String variableName, String functionName, List<Parameter> parameters)
    {
        this.variableName = variableName;
        this.functionName = functionName;
        this.parameters = parameters;
    }


    @SuppressWarnings("unchecked")
    public static Statement fromYaml(Map<String,Object> statementYaml)
    {
        // VALUES TO PARSE
        // --------------------------------------------------------------------------------------
        String variableName = null;
        String functionName = null;
        List<Parameter> parameters = new ArrayList<>();

        // PARSE VALUES
        // --------------------------------------------------------------------------------------

        // ** Variable Name
        if (statementYaml.containsKey("let"))
            variableName = (String) statementYaml.get("let");

        // ** Function Name
        if (statementYaml.containsKey("function"))
            functionName = (String) statementYaml.get("function");

        // ** Parameters
        if (statementYaml.containsKey("parameters")) {
            List<Map<String,Object>> parametersYaml =
                    (List<Map<String,Object>>) statementYaml.get("parameters");
            for (Map<String,Object> parameterYaml : parametersYaml) {
                parameters.add(Parameter.fromYaml(parameterYaml));
            }
        }

        return new Statement(variableName, functionName, parameters);
    }


    // API
    // ------------------------------------------------------------------------------------------

    public String getVariableName() {
        return this.variableName;
    }


    public String getFunctionName() {
        return this.functionName;
    }


    public List<Parameter> getParameters() {
        return this.parameters;
    }


    // NESTED CLASSES
    // ------------------------------------------------------------------------------------------

    public enum ParameterType
    {
        PARAMETER,
        VARIABLE,
        LITERAL_STRING;

        public static ParameterType fromString(String parameterType)
        {
            if (parameterType != null)
                return ParameterType.valueOf(parameterType.toUpperCase());
            return null;
        }
    }


    public static class Parameter
    {

        // PROPERTIES
        // --------------------------------------------------------------------------------------

        Object value;
        ParameterType _type;


        // CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public Parameter(Object value, ParameterType _type)
        {
            this.value = value;
            this._type = _type;
        }


        public static Parameter fromYaml(Map<String,Object> parameterYaml)
        {
            // VALUES TO PARSE
            // --------------------------------------------------------------------------------------
            Object value = null;
            ParameterType _type = null;

            // PARSE VALUES
            // --------------------------------------------------------------------------------------

            // ** Name
            if (parameterYaml.containsKey("value"))
                value = parameterYaml.get("value");

            if (parameterYaml.containsKey("kind"))
                _type = ParameterType.fromString((String) parameterYaml.get("value"));

            return new Parameter(value, _type);
        }


        // API
        // --------------------------------------------------------------------------------------

        public Object getValue() {
            return this.value;
        }


        public ParameterType getType() {
            return this._type;
        }

    }

}

