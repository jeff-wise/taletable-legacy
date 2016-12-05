
package com.kispoko.tome.rules.programming.interpreter;


import com.kispoko.tome.rules.programming.builtin.BuiltInFunctionException;
import com.kispoko.tome.rules.programming.interpreter.error.FunctionNotFoundError;
import com.kispoko.tome.rules.programming.interpreter.error.NullResultError;
import com.kispoko.tome.rules.programming.interpreter.error.NullVariableError;
import com.kispoko.tome.rules.programming.interpreter.error.UndefinedProgramError;
import com.kispoko.tome.rules.programming.interpreter.error.UndefinedProgramVariableError;
import com.kispoko.tome.rules.programming.interpreter.error.UndefinedVariableError;
import com.kispoko.tome.rules.programming.interpreter.error.UnexpectedProgramVariableTypeError;



/**
 * Evaluation Excdption
 */
public class InterpreterException extends Exception
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object error;
    private ErrorType errorType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    private InterpreterException(Object error, ErrorType errorType)
    {
        this.error     = error;
        this.errorType = errorType;
    }


    public static InterpreterException undefinedProgram(UndefinedProgramError error)
    {
        return new InterpreterException(error, ErrorType.UNDEFINED_PROGRAM);
    }


    public static InterpreterException undefinedVariable(UndefinedVariableError error)
    {
        return new InterpreterException(error, ErrorType.UNDEFINED_VARIABLE);
    }


    public static InterpreterException nullVariable(NullVariableError error)
    {
        return new InterpreterException(error, ErrorType.NULL_VARIABLE);
    }


    public static InterpreterException nullResult(NullResultError error)
    {
        return new InterpreterException(error, ErrorType.NULL_RESULT);
    }


    public static InterpreterException undefinedProgramVariable(UndefinedProgramVariableError error)
    {
        return new InterpreterException(error, ErrorType.UNDEFINED_PROGRAM_VARIABLE);
    }


    public static InterpreterException functionNotFound(FunctionNotFoundError error)
    {
        return new InterpreterException(error, ErrorType.FUNCTION_NOT_FOUND);
    }


    public static InterpreterException
                    unexpectedProgramVariableType(UnexpectedProgramVariableTypeError error)
    {
        return new InterpreterException(error, ErrorType.UNEXPECTED_PROGRAM_VARIABLE_TYPE);
    }


    public static InterpreterException builtInFunction(BuiltInFunctionException error)
    {
        return new InterpreterException(error, ErrorType.BUILT_IN_FUNCTION);
    }


    // API
    // ------------------------------------------------------------------------------------------

    public ErrorType getErrorType()
    {
        return this.errorType;
    }


    public String errorMessage()
    {
        StringBuilder errorBuilder = new StringBuilder();

        errorBuilder.append("Interpreter Error: ");

        switch (this.errorType)
        {
            case UNDEFINED_PROGRAM:
                errorBuilder.append(((UndefinedProgramError) this.error).errorMessage());
                break;
            case UNDEFINED_VARIABLE:
                errorBuilder.append(((UndefinedVariableError) this.error).errorMessage());
                break;
            case NULL_VARIABLE:
                errorBuilder.append(((NullVariableError) this.error).errorMessage());
                break;
            case UNDEFINED_PROGRAM_VARIABLE:
                errorBuilder.append(((UndefinedProgramVariableError) this.error).errorMessage());
                break;
            case UNEXPECTED_PROGRAM_VARIABLE_TYPE:
                errorBuilder.append(
                        ((UnexpectedProgramVariableTypeError) this.error).errorMessage());
                break;
            case FUNCTION_NOT_FOUND:
                errorBuilder.append(
                        ((FunctionNotFoundError) this.error).errorMessage());
                break;
            case BUILT_IN_FUNCTION:
                errorBuilder.append(((BuiltInFunctionException) this.error).errorMessage());
                break;
            case NULL_RESULT:
                errorBuilder.append(((NullResultError) this.error).errorMessage());
                break;
        }

        return errorBuilder.toString();
    }


    // EXCEPTION TYPES
    // ------------------------------------------------------------------------------------------

    public enum ErrorType
    {
        UNDEFINED_PROGRAM,
        UNDEFINED_VARIABLE,
        NULL_VARIABLE,
        NULL_RESULT,
        UNDEFINED_PROGRAM_VARIABLE,
        UNEXPECTED_PROGRAM_VARIABLE_TYPE,
        FUNCTION_NOT_FOUND,
        BUILT_IN_FUNCTION
    }

}

