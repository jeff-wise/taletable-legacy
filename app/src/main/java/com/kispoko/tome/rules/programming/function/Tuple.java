
package com.kispoko.tome.rules.programming.function;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * Tuple
 */
public class Tuple
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

