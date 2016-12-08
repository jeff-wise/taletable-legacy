
package com.kispoko.tome.engine.programming.summation;


import com.kispoko.tome.engine.programming.summation.error.UndefinedVariableError;
import com.kispoko.tome.engine.programming.summation.error.VariableNotNumberError;


/**
 * Evaluation Excdption
 */
public class SummationException extends Exception {

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object error;
    private ErrorType errorType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    private SummationException(Object error, ErrorType errorType)
    {
        this.error = error;
        this.errorType = errorType;
    }


    public static SummationException undefinedVariable(UndefinedVariableError error)
    {
        return new SummationException(error, ErrorType.UNDEFINED_VARIABLE);
    }


    public static SummationException variableNotNumber(VariableNotNumberError error)
    {
        return new SummationException(error, ErrorType.VARIABLE_NOT_NUMBER);
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

        errorBuilder.append("Summation Error: ");

        switch (this.errorType) {
            case UNDEFINED_VARIABLE:
                errorBuilder.append(((UndefinedVariableError) this.error).errorMessage());
                break;
        }

        return errorBuilder.toString();
    }


    // EXCEPTION TYPES
    // ------------------------------------------------------------------------------------------

    private enum ErrorType
    {
        UNDEFINED_VARIABLE,
        VARIABLE_NOT_NUMBER
    }

}

