
package com.kispoko.tome.rules.evaluation;


import com.kispoko.tome.rules.BuiltInFunction;
import com.kispoko.tome.rules.function.FunctionIndex;
import com.kispoko.tome.rules.function.FunctionValue;
import com.kispoko.tome.rules.function.FunctionValueType;
import com.kispoko.tome.rules.program.Program;
import com.kispoko.tome.rules.program.Statement;
import com.kispoko.tome.rules.evaluation.error.ParameterWrongType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Evaluator - Runs programs.
 */
public class Evaluator
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    //private Map<String,Component> componentIndex;

    private BuiltInFunction builtInFunction;
    private FunctionIndex functionIndex;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Evaluator(BuiltInFunction builtInFunction, FunctionIndex functionIndex)
    {
        this.builtInFunction = builtInFunction;
        this.functionIndex = functionIndex;
    }


    // API
    // ------------------------------------------------------------------------------------------

    public FunctionValue evaluate(Program program, List<FunctionValue> parameters)
                         throws EvaluationException
    {
        Map<String,FunctionValue> variables = new HashMap<>();

        // Evaluate Statements
        for (Statement statement : program.getStatements())
        {
            String variableName = statement.getVariableName();
            FunctionValue statementValue = evaluateStatement(statement, parameters, variables);
            variables.put(variableName, statementValue);
        }

        // Return result variable value
        String resultVariableName = program.getResultVariableName();
        return variables.get(resultVariableName);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private FunctionValue evaluateStatement(Statement statement,
                                            List<FunctionValue> programParameters,
                                            Map<String,FunctionValue> variables)
                          throws EvaluationException
    {
        String functionName = statement.getFunctionName();

        List<FunctionValue> parameters = new ArrayList<>();
        int i = 0;
        for (Statement.Parameter parameter : statement.getParameters())
        {
            parameters.add(evaluateParameter(parameter, programParameters, variables, i));
            i++;
        }

        return evaluateFunction(functionName, parameters, variables);
    }


    private FunctionValue evaluateParameter(Statement.Parameter parameter,
                                            List<FunctionValue> programParameters,
                                            Map<String,FunctionValue> variables, Integer index)
                          throws EvaluationException
    {
        switch (parameter.getType())
        {
            case PARAMETER:
                try {
                    int parameterIndex = (Integer) parameter.getValue();
                    return programParameters.get(parameterIndex);
                } catch (ClassCastException e) {
                    throw new EvaluationException(
                                new ParameterWrongType(index, parameter.getType()),
                                EvaluationError.Type.PROGRAM_PARAMETER_WRONG_TYPE);
                }
            case VARIABLE:
                try {
                    String variableName = (String) parameter.getValue();
                    if (variables.containsKey(variableName))
                        return variables.get(variableName);
                    else
                        throw new EvaluationException();
                } catch (ClassCastException e) {
                    throw new EvaluationException(
                            new ParameterWrongType(index, parameter.getType()),
                            EvaluationError.Type.PROGRAM_PARAMETER_WRONG_TYPE);
                }
            case LITERAL_STRING:
                try {
                    String s = (String) parameter.getValue();
                    return new FunctionValue(s, FunctionValueType.STRING);
                } catch (ClassCastException e) {
                    throw new EvaluationException(
                            new ParameterWrongType(index, parameter.getType()),
                            EvaluationError.Type.PROGRAM_PARAMETER_WRONG_TYPE);
                }
        }

        return null;
    }


    private FunctionValue evaluateFunction(String functionName, List<FunctionValue> parameters,
                                           Map<String,FunctionValue> context)
                          throws EvaluationException
    {
        // Check built-ins first
        if (BuiltInFunction.exists(functionName)) {
            return BuiltInFunction.execute(functionName, parameters, context);
        }
        // Then custom functions
        else if (this.functionIndex.hasFunction(functionName)) {
            return this.functionIndex.getFunction(functionName).execute(parameters);
        }

        return null;
    }


    // NESTED CLASSES
    // ------------------------------------------------------------------------------------------


}
