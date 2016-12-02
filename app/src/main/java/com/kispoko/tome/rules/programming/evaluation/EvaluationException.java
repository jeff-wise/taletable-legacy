
package com.kispoko.tome.rules.programming.evaluation;


import com.kispoko.tome.rules.programming.builtin.BuiltInFunctionException;
import com.kispoko.tome.rules.programming.evaluation.error.UndefinedProgramError;
import com.kispoko.tome.rules.programming.evaluation.error.UndefinedProgramVariableError;
import com.kispoko.tome.rules.programming.evaluation.error.UndefinedVariableError;
import com.kispoko.tome.rules.programming.evaluation.error.UnexpectedProgramVariableTypeError;


/**
 * Evaluation Excdption
 */
public class EvaluationException extends Exception
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object error;
    private Type   errorType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    private EvaluationException(Object error, Type errorType)
    {
        this.error     = error;
        this.errorType = errorType;
    }


    public static EvaluationException invalidParameterType(InvalidParameterTypeError error)
    {
        return new EvaluationException(error, Type.INVALID_PARAMETER_TYPE);
    }


    public static EvaluationException undefinedProgram(UndefinedProgramError error)
    {
        return new EvaluationException(error, Type.UNDEFINED_PROGRAM);
    }


    public static EvaluationException undefinedVariable(UndefinedVariableError error)
    {
        return new EvaluationException(error, Type.UNDEFINED_VARIABLE);
    }


    public static EvaluationException undefinedProgramVariable(UndefinedProgramVariableError error)
    {
        return new EvaluationException(error, Type.UNDEFINED_PROGRAM_VARIABLE);
    }


    public static EvaluationException
                    unexpectedProgramVariableType(UnexpectedProgramVariableTypeError error)
    {
        return new EvaluationException(error, Type.UNEXPECTED_PROGRAM_VARIABLE_TYPE);
    }


    public static EvaluationException builtInFunction(BuiltInFunctionException error)
    {
        return new EvaluationException(error, Type.BUILT_IN_FUNCTION);
    }


    // API
    // ------------------------------------------------------------------------------------------


    // EXCEPTION TYPES
    // ------------------------------------------------------------------------------------------

    public enum Type
    {
        INVALID_PARAMETER_TYPE,
        UNDEFINED_PROGRAM,
        UNDEFINED_VARIABLE,
        UNDEFINED_PROGRAM_VARIABLE,
        UNEXPECTED_PROGRAM_VARIABLE_TYPE,
        BUILT_IN_FUNCTION
    }

}

