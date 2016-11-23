
package com.kispoko.tome.rules.programming.evaluation;


import com.kispoko.tome.rules.programming.BuiltInFunction;
import com.kispoko.tome.rules.programming.function.FunctionIndex;
import com.kispoko.tome.rules.programming.program.Program;
import com.kispoko.tome.rules.programming.program.ProgramValue;
import com.kispoko.tome.rules.programming.program.statement.Parameter;
import com.kispoko.tome.rules.programming.program.statement.Statement;
import com.kispoko.tome.rules.programming.evaluation.error.ParameterWrongType;

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

    //private Map<String,WidgetData> componentIndex;

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

    public ProgramValue evaluate(Program program, List<ProgramValue> parameters)
                         throws EvaluationException
    {
        Map<String,ProgramValue> variables = new HashMap<>();

        // Evaluate Statements
        for (Statement statement : program.getStatements())
        {
            String variableName = statement.getVariableName();
            ProgramValue statementValue = evaluateStatement(statement, parameters, variables);
            variables.put(variableName, statementValue);
        }

        ProgramValue resultValue = evaluateStatement(program.getResultStatement(),
                                                     parameters,
                                                     variables);
        return resultValue;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private ProgramValue evaluateStatement(Statement statement,
                                           List<ProgramValue> programParameters,
                                           Map<String,ProgramValue> variables)
                          throws EvaluationException
    {
        String functionName = statement.getFunctionName();

        List<ProgramValue> parameters = new ArrayList<>();
        int i = 0;
        for (Parameter parameter : statement.getParameters())
        {
            parameters.add(evaluateParameter(parameter, programParameters, variables, i));
            i++;
        }

        return evaluateFunction(functionName, parameters, variables);
    }


    private ProgramValue evaluateParameter(Parameter parameter,
                                           List<ProgramValue> programParameters,
                                           Map<String,ProgramValue> variables, Integer index)
                          throws EvaluationException
    {
        switch (parameter.getType())
        {
            case PARAMETER:
                try {
                    int parameterIndex = parameter.getParameter();
                    return programParameters.get(parameterIndex);
                } catch (ClassCastException e) {
                    throw new EvaluationException(
                                new ParameterWrongType(index, parameter.getType()),
                                EvaluationError.Type.PROGRAM_PARAMETER_WRONG_TYPE);
                }
            case VARIABLE:
                try {
                    String variableName = parameter.getVariable();
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
                    String stringLiteral = parameter.getStringLiteral();
                    return ProgramValue.asString(stringLiteral);
                } catch (ClassCastException e) {
                    throw new EvaluationException(
                            new ParameterWrongType(index, parameter.getType()),
                            EvaluationError.Type.PROGRAM_PARAMETER_WRONG_TYPE);
                }
        }

        return null;
    }


    private ProgramValue evaluateFunction(String functionName, List<ProgramValue> parameters,
                                          Map<String,ProgramValue> context)
                          throws EvaluationException
    {
        // Check built-ins first
        if (BuiltInFunction.exists(functionName)) {
            return BuiltInFunction.execute(functionName, parameters, context);
        }
        // Then custom functions
        else if (this.functionIndex.hasFunction(functionName)) {
            return this.functionIndex.functionWithName(functionName).execute(parameters);
        }

        return null;
    }


    // NESTED CLASSES
    // ------------------------------------------------------------------------------------------


}
