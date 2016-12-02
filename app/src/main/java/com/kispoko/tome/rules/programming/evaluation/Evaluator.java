
package com.kispoko.tome.rules.programming.evaluation;


import com.kispoko.tome.rules.programming.builtin.BuiltInFunction;
import com.kispoko.tome.rules.programming.builtin.BuiltInFunctionException;
import com.kispoko.tome.rules.programming.evaluation.error.UndefinedProgramError;
import com.kispoko.tome.rules.programming.evaluation.error.UndefinedProgramVariableError;
import com.kispoko.tome.rules.programming.evaluation.error.UndefinedVariableError;
import com.kispoko.tome.rules.programming.function.FunctionIndex;
import com.kispoko.tome.rules.programming.program.Program;
import com.kispoko.tome.rules.programming.program.ProgramIndex;
import com.kispoko.tome.rules.programming.program.ProgramInvocation;
import com.kispoko.tome.rules.programming.program.ProgramInvocationParameter;
import com.kispoko.tome.rules.programming.program.ProgramValue;
import com.kispoko.tome.rules.programming.program.statement.Parameter;
import com.kispoko.tome.rules.programming.program.statement.Statement;
import com.kispoko.tome.rules.programming.variable.VariableIndex;
import com.kispoko.tome.rules.programming.variable.VariableUnion;
import com.kispoko.tome.util.tuple.Tuple2;

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

    private ProgramIndex    programIndex;
    private FunctionIndex   functionIndex;
    private VariableIndex   variableIndex;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Evaluator(ProgramIndex programIndex,
                     FunctionIndex functionIndex,
                     VariableIndex variableIndex)
    {
        this.programIndex    = programIndex;
        this.functionIndex   = functionIndex;
        this.variableIndex   = variableIndex;
    }


    // API
    // ------------------------------------------------------------------------------------------

    public ProgramValue evaluate(ProgramInvocation programInvocation)
           throws EvaluationException
    {
        Tuple2<Program, List<ProgramValue>> evaluationParameters =
                                                evaluateProgramInvocation(programInvocation);

        Program            program    = evaluationParameters.getItem1();
        List<ProgramValue> parameters = evaluationParameters.getItem2();

        return evaluateProgram(program, parameters);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private Tuple2<Program, List<ProgramValue>>
                    evaluateProgramInvocation(ProgramInvocation programInvocation)
            throws EvaluationException
    {
        // > Lookup Program
        // ----------------------------------------------------------------------------------
        String programName = programInvocation.getProgramName();
        Program program = this.programIndex.programWithName(programName);

        // If the program doesn't exist, throw Evaluation Exception
        if (program == null) {
            throw EvaluationException.undefinedProgram(new UndefinedProgramError(programName));
        }

        // > Evaluate Parameters
        // ----------------------------------------------------------------------------------
        List<ProgramValue> parameters = new ArrayList<>();

        for (ProgramInvocationParameter invocationParameter : programInvocation.getParameters())
        {
            switch (invocationParameter.getType())
            {
                // The parameter is a reference to a variable value.
                case REFERENCE:

                    // Lookup variable
                    String variableName = invocationParameter.getReference();
                    VariableUnion variableUnion = this.variableIndex.variableWithName(variableName);
                    if (variableUnion == null) {
                        throw EvaluationException.undefinedVariable(
                                new UndefinedVariableError(variableName));
                    }

                    // Assign parameter from variable value
                    ProgramValue programValue = null;
                    switch (variableUnion.getType())
                    {
                        case TEXT:
                            programValue = ProgramValue.asStringTemp(
                                                    variableUnion.getText().getString());
                            break;
                        case NUMBER:
                            programValue = ProgramValue.asIntegerTemp(
                                                    variableUnion.getNumber().getInteger());
                            break;
                        case BOOLEAN:
                            programValue = ProgramValue.asBooleanTemp(
                                                    variableUnion.getBoolean().getBoolean());
                            break;
                    }

                    parameters.add(programValue);
                    break;
            }
        }


        return new Tuple2<>(program, parameters);

    }

    private ProgramValue evaluateProgram(Program program, List<ProgramValue> parameters)
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
                int parameterIndex = parameter.getParameter();
                return programParameters.get(parameterIndex);
            case VARIABLE:
                String variableName = parameter.getVariable();
                if (variables.containsKey(variableName)) {
                    return variables.get(variableName);
                }
                else {
                    throw EvaluationException.undefinedProgramVariable(
                            new UndefinedProgramVariableError(variableName));
                }
            case LITERAL_STRING:
                String stringLiteral = parameter.getStringLiteral();
                return ProgramValue.asStringTemp(stringLiteral);
        }

        return null;
    }


    private ProgramValue evaluateFunction(String functionName, List<ProgramValue> parameters,
                                          Map<String,ProgramValue> context)
                          throws EvaluationException
    {
        // Check built-ins first
        if (BuiltInFunction.exists(functionName)) {
            try {
                return BuiltInFunction.execute(functionName, parameters, context);
            }
            catch (BuiltInFunctionException exception) {
                throw EvaluationException.builtInFunction(exception);
            }
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
