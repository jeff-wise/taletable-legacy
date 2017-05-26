
package com.kispoko.tome.model.engine.variable;


import com.kispoko.tome.model.engine.variable.error.UndefinedVariableError;
import com.kispoko.tome.model.engine.variable.error.UnexpectedVariableTypeError;



/**
 * Variable Exception
 */
public class VariableException extends Exception
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object    error;
    private ErrorType errorType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    private VariableException(Object error, ErrorType errorType)
    {
        this.error     = error;
        this.errorType = errorType;
    }


    public static VariableException undefinedVariable(UndefinedVariableError error)
    {
        return new VariableException(error, ErrorType.UNDEFINED_VARIABLE);
    }


    public static VariableException unexpectedVariableType(UnexpectedVariableTypeError error)
    {
        return new VariableException(error, ErrorType.UNEXPECTED_VARIABLE_TYPE);
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

        errorBuilder.append("Variable Error: ");

        switch (this.errorType)
        {
            case UNDEFINED_VARIABLE:
                errorBuilder.append(((UndefinedVariableError) this.error).errorMessage());
                break;
            case UNEXPECTED_VARIABLE_TYPE:
                errorBuilder.append(((UnexpectedVariableTypeError) this.error).errorMessage());
                break;
        }

        return errorBuilder.toString();
    }


    // EXCEPTION TYPES
    // ------------------------------------------------------------------------------------------

    private enum ErrorType
    {
        UNDEFINED_VARIABLE,
        UNEXPECTED_VARIABLE_TYPE
    }

}
