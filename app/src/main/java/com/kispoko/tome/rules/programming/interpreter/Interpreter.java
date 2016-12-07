
package com.kispoko.tome.rules.programming.interpreter;



import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.rules.programming.builtin.BuiltInFunction;
import com.kispoko.tome.rules.programming.builtin.BuiltInFunctionException;
import com.kispoko.tome.rules.programming.interpreter.error.FunctionNotFoundError;
import com.kispoko.tome.rules.programming.interpreter.error.NullResultError;
import com.kispoko.tome.rules.programming.interpreter.error.NullVariableError;
import com.kispoko.tome.rules.programming.interpreter.error.UndefinedProgramError;
import com.kispoko.tome.rules.programming.interpreter.error.UndefinedProgramVariableError;
import com.kispoko.tome.rules.programming.interpreter.error.UndefinedVariableError;
import com.kispoko.tome.rules.programming.function.FunctionIndex;
import com.kispoko.tome.rules.programming.program.Program;
import com.kispoko.tome.rules.programming.program.ProgramIndex;
import com.kispoko.tome.rules.programming.program.ProgramInvocation;
import com.kispoko.tome.rules.programming.program.ProgramInvocationParameter;
import com.kispoko.tome.rules.programming.program.ProgramValue;
import com.kispoko.tome.rules.programming.program.statement.Parameter;
import com.kispoko.tome.rules.programming.program.statement.Statement;
import com.kispoko.tome.rules.programming.summation.SummationException;
import com.kispoko.tome.rules.programming.variable.VariableIndex;
import com.kispoko.tome.rules.programming.variable.VariableUnion;
import com.kispoko.tome.util.tuple.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Interpreter - Runs programs.
 */
public class Interpreter implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ProgramIndex    programIndex;
    private FunctionIndex   functionIndex;
    private VariableIndex   variableIndex;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Interpreter(ProgramIndex programIndex,
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
           throws InterpreterException
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
            throws InterpreterException
    {
        // > Lookup Program
        // ----------------------------------------------------------------------------------
        String programName = programInvocation.getProgramName();
        Program program = this.programIndex.programWithName(programName);

        // If the program doesn't exist, throw Evaluation Exception
        if (program == null) {
            throw InterpreterException.undefinedProgram(new UndefinedProgramError(programName));
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
                        throw InterpreterException.undefinedVariable(
                                new UndefinedVariableError(variableName));
                    }

                    if (variableUnion.isNull()) {
                        throw InterpreterException.nullVariable(
                                new NullVariableError(variableName));
                    }

                    // Assign parameter from variable value
                    ProgramValue programValue = null;
                    switch (variableUnion.getType())
                    {
                        case TEXT:
                            programValue = ProgramValue.asString(
                                                    variableUnion.getText().value());
                            break;
                        case NUMBER:
                            try {
                                programValue = ProgramValue.asInteger(
                                                    variableUnion.getNumber().value());
                            } catch (SummationException exception) {
                                ApplicationFailure.summation(exception);
                            }
                            break;
                        case BOOLEAN:
                            programValue = ProgramValue.asBoolean(
                                                    variableUnion.getBoolean().value());
                            break;
                    }

                    parameters.add(programValue);
                    break;
            }
        }

        return new Tuple2<>(program, parameters);
    }


    private ProgramValue evaluateProgram(Program program, List<ProgramValue> parameters)
            throws InterpreterException
    {
        Map<String,ProgramValue> context = new HashMap<>();

        // Evaluate Statements
        for (Statement statement : program.getStatements())
        {
            String variableName = statement.getVariableName();
            ProgramValue statementValue = evaluateStatement(statement, parameters, context);

            context.put(variableName, statementValue);
        }

        ProgramValue resultValue = evaluateStatement(program.getResultStatement(),
                                                     parameters,
                                                     context);
        return resultValue;
    }


    private ProgramValue evaluateStatement(Statement statement,
                                           List<ProgramValue> programParameters,
                                           Map<String,ProgramValue> context)
                          throws InterpreterException
    {
        String functionName = statement.getFunctionName();

        List<ProgramValue> parameters = new ArrayList<>();
        for (Parameter parameter : statement.getParameters())
        {
            parameters.add(evaluateParameter(parameter, programParameters, context));
        }

        return evaluateFunction(functionName, parameters, context);
    }


    private ProgramValue evaluateParameter(Parameter parameter,
                                           List<ProgramValue> programParameters,
                                           Map<String,ProgramValue> context)
                          throws InterpreterException
    {
        switch (parameter.getType())
        {
            case PARAMETER:
                int parameterIndex = parameter.getParameter();
                return programParameters.get(parameterIndex - 1);
            case VARIABLE:
                String variableName = parameter.getVariable();
                if (context.containsKey(variableName)) {
                    return context.get(variableName);
                }
                else {
                    throw InterpreterException.undefinedProgramVariable(
                            new UndefinedProgramVariableError(variableName));
                }
            case LITERAL_STRING:
                String stringLiteral = parameter.getStringLiteral();
                return ProgramValue.asString(stringLiteral);
        }

        return null;
    }


    private ProgramValue evaluateFunction(String functionName,
                                          List<ProgramValue> parameters,
                                          Map<String,ProgramValue> context)
                          throws InterpreterException
    {
        ProgramValue result;

        // [1] Check built-in function first
        // --------------------------------------------------------------------------------------

        if (BuiltInFunction.exists(functionName))
        {
            try {
                result = BuiltInFunction.execute(functionName, parameters, context);
            }
            catch (BuiltInFunctionException exception) {
                throw InterpreterException.builtInFunction(exception);
            }
        }

        // [2] Lookup function in custom functions
        // --------------------------------------------------------------------------------------

        else if (this.functionIndex.hasFunction(functionName))
        {
            result = this.functionIndex.functionWithName(functionName).execute(parameters);
        }

        // [3] Throw function not found exception
        // --------------------------------------------------------------------------------------

        else
        {
            throw InterpreterException.functionNotFound(new FunctionNotFoundError(functionName));
        }


        // [4] If result is NULL, throw exception because succeeding computations will fail.
        // --------------------------------------------------------------------------------------

        if (result == null)
            throw InterpreterException.nullResult(new NullResultError(functionName));


        // [5] Return function result
        // --------------------------------------------------------------------------------------

        return result;
    }


    // NESTED CLASSES
    // ------------------------------------------------------------------------------------------


}
