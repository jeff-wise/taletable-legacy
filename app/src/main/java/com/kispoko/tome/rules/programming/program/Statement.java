
package com.kispoko.tome.rules.programming.program;


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


        public static String asString(ParameterType parameterType)
        {
            if (parameterType != null)
                return parameterType.toString().toLowerCase();
            // TODO should not happen
            return null;
        }

    }


    public static class Parameter
    {

        // PROPERTIES
        // --------------------------------------------------------------------------------------

        private Object value;
        private ParameterType _type;


        // CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public Parameter(Object value, ParameterType _type)
        {
            this.value = value;
            this._type = _type;
            // TODO verify that value is compatible with type
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


        /**
         * Create a parameter from a string value. This method is usually used to deserialize a
         * parameter, where the value was stored as a string, and we need to convert the value to
         * the appropriate type.
         * @param value Value represented as a string.
         * @param _type The type of parameter.
         * @return A parameter.
         */
        public static Parameter fromString(String value, ParameterType _type)
        {
            switch (_type)
            {
                case PARAMETER:
                    Integer intValue = Integer.parseInt(value);
                    return new Parameter(intValue, _type);
                case VARIABLE:
                    return new Parameter(value, _type);
                case LITERAL_STRING:
                    return new Parameter(value, _type);
            }

            // TODO should not happen
            return null;
        }


        // API
        // --------------------------------------------------------------------------------------

        public Object getValue() {
            return this.value;
        }


        public ParameterType getType() {
            return this._type;
        }


        public String valueAsString()
        {
            switch (this._type)
            {
                case PARAMETER:
                    return Integer.toString((Integer) this.value);
                case VARIABLE:
                    return (String) this.value;
                case LITERAL_STRING:
                    return (String) this.value;
            }

            // TODO should not happen
            return null;
        }

    }

}

