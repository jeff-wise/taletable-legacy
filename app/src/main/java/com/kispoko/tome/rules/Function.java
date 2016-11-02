
package com.kispoko.tome.rules;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * Function Definition
 */
public class Function
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String name;
    private List<FunctionValueType> parameterTypes;
    private FunctionValueType resultType;
    private List<Tuple> tuples;

    private Map<List<FunctionValue>,FunctionValue> lookupMap;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Function(String name, List<FunctionValueType> parameterTypes,
                    FunctionValueType resultType, List<Tuple> tuples)
    {
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.resultType = resultType;
        this.tuples = tuples;
    }


    @SuppressWarnings("unchecked")
    public static Function fromYaml(Map<String,Object> functionDefinitionYaml)
    {
        // VALUES TO PARSE
        // --------------------------------------------------------------------------------------
        String name = null;
        List<FunctionValueType> parameterTypes = new ArrayList<>();
        FunctionValueType resultType = null;
        List<Tuple> tuples = new ArrayList<>();

        // PARSE VALUES
        // --------------------------------------------------------------------------------------

        // > Top Level
        // --------------------------------------------------------------------------------------

        // ** Name
        if (functionDefinitionYaml.containsKey("name"))
            name = (String) functionDefinitionYaml.get("name");

        // > Definition
        // --------------------------------------------------------------------------------------

        // ** Parameter Types
        if (functionDefinitionYaml.containsKey("parameter_types")) {
            List<String> parameterTypeList =
                    ((List<String>) functionDefinitionYaml.get("parameter_types"));
            for (String parameterType : parameterTypeList) {
                parameterTypes.add(FunctionValueType.fromString(parameterType));
            }
        }

        // ** Result Type
        if (functionDefinitionYaml.containsKey("result_type")) {
            resultType = FunctionValueType.fromString(
                            (String) functionDefinitionYaml.get("result_type"));
        }

        // ** Tuples
        if (functionDefinitionYaml.containsKey("tuples")) {
            List<Map<String,Object>> tuplesYaml =
                    (List<Map<String,Object>>) functionDefinitionYaml.get("tuples");
            for (Map<String,Object> tupleYaml : tuplesYaml) {
                tuples.add(Tuple.fromYaml(parameterTypes, resultType, tupleYaml));
            }
        }

        return new Function(name, parameterTypes, resultType, tuples);

    }


    // API
    // ------------------------------------------------------------------------------------------

    public String getName() {
        return this.name;
    }


    public List<Tuple> getTuples() {
        return this.tuples;
    }


    public FunctionValue execute(List<FunctionValue> parameters)
    {
        return lookupMap.get(parameters);
    }


    // NESTED CLASSES
    // ------------------------------------------------------------------------------------------


    public static class Tuple
    {

        // > PROPERTIES
        // ------------------------------------------------------------------------------------------

        private List<FunctionValue> parameters;
        private FunctionValue result;


        // > CONSTRUCTORS
        // ------------------------------------------------------------------------------------------

        public Tuple(List<FunctionValue> parameters, FunctionValue result)
        {
            this.parameters = parameters;
            this.result = result;
        }


        @SuppressWarnings("unchecked")
        public static Tuple fromYaml(List<FunctionValueType> parameterTypes,
                                     FunctionValueType resultType,
                                     Map<String,Object> tupleYaml)
        {
            List<FunctionValue> parameters = new ArrayList<>();
            FunctionValue result = null;

            if (tupleYaml.containsKey("parameters")) {
                List<Object> parametersYaml = ((List<Object>) tupleYaml.get("parameters"));
                int i = 0;
                for (Object parameterYaml : parametersYaml) {
                    FunctionValueType parameterType = parameterTypes.get(i);
                    parameters.add(new FunctionValue(parameterYaml, parameterType));
                    i++;
                }
            }

            if (tupleYaml.containsKey("result"))
                result = new FunctionValue(tupleYaml.get("result"), resultType);

            return new Tuple(parameters, result);
        }


        // > API
        // ------------------------------------------------------------------------------------------

        public List<FunctionValue> getParameters() {
            return this.parameters;
        }


        public FunctionValue getResult() {
            return this.result;
        }

    }

}
