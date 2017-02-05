
package com.kispoko.tome.engine.interpreter;



import android.util.Log;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.function.builtin.BuiltInFunction;
import com.kispoko.tome.engine.function.builtin.BuiltInFunctionException;
import com.kispoko.tome.engine.interpreter.error.FunctionNotFoundError;
import com.kispoko.tome.engine.interpreter.error.NullResultError;
import com.kispoko.tome.engine.interpreter.error.NullVariableError;
import com.kispoko.tome.engine.interpreter.error.UndefinedProgramError;
import com.kispoko.tome.engine.interpreter.error.UndefinedProgramVariableError;
import com.kispoko.tome.engine.interpreter.error.UndefinedVariableError;
import com.kispoko.tome.engine.function.FunctionIndex;
import com.kispoko.tome.engine.program.invocation.InvocationParameterUnion;
import com.kispoko.tome.engine.program.Program;
import com.kispoko.tome.engine.program.ProgramIndex;
import com.kispoko.tome.engine.program.invocation.Invocation;
import com.kispoko.tome.engine.program.ProgramValueUnion;
import com.kispoko.tome.engine.program.statement.Parameter;
import com.kispoko.tome.engine.program.statement.Statement;
import com.kispoko.tome.engine.variable.VariableException;
import com.kispoko.tome.engine.variable.VariableUnion;
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


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Interpreter(ProgramIndex programIndex,
                       FunctionIndex functionIndex)
    {
        this.programIndex    = programIndex;
        this.functionIndex   = functionIndex;
    }


    // API
    // ------------------------------------------------------------------------------------------

    public ProgramValueUnion evaluate(Invocation invocation)
           throws InterpreterException
    {
        Tuple2<Program, List<ProgramValueUnion>> evaluationParameters =
                                                evaluateProgramInvocation(invocation);

        Program            program    = evaluationParameters.getItem1();
        List<ProgramValueUnion> parameters = evaluationParameters.getItem2();

        return evaluateProgram(program, parameters);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private Tuple2<Program, List<ProgramValueUnion>>
                    evaluateProgramInvocation(Invocation invocation)
            throws InterpreterException
    {
        // > Lookup Program
        // ----------------------------------------------------------------------------------
        String programName = invocation.programName();
        Program program = this.programIndex.programWithName(programName);

        // If the program doesn't exist, throw Evaluation Exception
        if (program == null) {
            throw InterpreterException.undefinedProgram(new UndefinedProgramError(programName));
        }

        // > Evaluate Parameters
        // ----------------------------------------------------------------------------------
        List<ProgramValueUnion> parameters = new ArrayList<>();

        for (InvocationParameterUnion invocationParameter : invocation.parameters())
        {
            switch (invocationParameter.type())
            {
                // The parameter is a reference to a variable value.
                case REFERENCE:

                    // Lookup variable
                    String variableName = invocationParameter.reference();

                    VariableUnion variableUnion = State.variableWithName(variableName);
                    if (variableUnion == null) {
                        throw InterpreterException.undefinedVariable(
                                new UndefinedVariableError(variableName));
                    }

                    if (variableUnion.isNull()) {
                        throw InterpreterException.nullVariable(
                                new NullVariableError(variableName));
                    }

                    // Assign parameter from variable value
                    ProgramValueUnion programValueUnion = null;
                    switch (variableUnion.type())
                    {
                        case TEXT:
                            programValueUnion = ProgramValueUnion.asString(
                                                    variableUnion.textVariable().value());
                            break;
                        case NUMBER:
                            try {
                                programValueUnion = ProgramValueUnion.asInteger(
                                                    variableUnion.numberVariable().value());
                            }
                            catch (VariableException exception) {
                                ApplicationFailure.variable(exception);
                            }
                            break;
                        case BOOLEAN:
                            programValueUnion = ProgramValueUnion.asBoolean(
                                                    variableUnion.booleanVariable().value());
                            break;
                    }

                    parameters.add(programValueUnion);
                    break;
            }
        }

        return new Tuple2<>(program, parameters);
    }


    private ProgramValueUnion evaluateProgram(Program program, List<ProgramValueUnion> parameters)
            throws InterpreterException
    {
        Map<String,ProgramValueUnion> context = new HashMap<>();

        // Evaluate Statements
        for (Statement statement : program.statements())
        {
            String variableName = statement.variableName();
            ProgramValueUnion statementValue = evaluateStatement(statement, parameters, context);

            context.put(variableName, statementValue);
        }

        ProgramValueUnion resultValue = evaluateStatement(program.resultStatement(),
                                                     parameters,
                                                     context);
        return resultValue;
    }


    private ProgramValueUnion evaluateStatement(Statement statement,
                                                List<ProgramValueUnion> programParameters,
                                                Map<String,ProgramValueUnion> context)
                          throws InterpreterException
    {
        String functionName = statement.functionName();

        List<ProgramValueUnion> parameters = new ArrayList<>();
        for (Parameter parameter : statement.parameters())
        {
            parameters.add(evaluateParameter(parameter, programParameters, context));
        }

        return evaluateFunction(functionName, parameters, context);
    }


    private ProgramValueUnion evaluateParameter(Parameter parameter,
                                                List<ProgramValueUnion> programParameters,
                                                Map<String,ProgramValueUnion> context)
                          throws InterpreterException
    {
        switch (parameter.type())
        {
            case PARAMETER:
                int parameterIndex = parameter.parameter();
                return programParameters.get(parameterIndex - 1);
            case VARIABLE:
                String variableName = parameter.variable();
                if (context.containsKey(variableName)) {
                    return context.get(variableName);
                }
                else {
                    throw InterpreterException.undefinedProgramVariable(
                            new UndefinedProgramVariableError(variableName));
                }
            case LITERAL_STRING:
                String stringLiteral = parameter.stringLiteral();
                return ProgramValueUnion.asString(stringLiteral);
        }

        return null;
    }


    private ProgramValueUnion evaluateFunction(String functionName,
                                               List<ProgramValueUnion> parameters,
                                               Map<String,ProgramValueUnion> context)
                          throws InterpreterException
    {
        ProgramValueUnion result;

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

}
